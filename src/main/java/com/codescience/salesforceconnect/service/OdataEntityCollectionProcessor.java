package com.codescience.salesforceconnect.service;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.*;
import org.apache.olingo.server.api.uri.queryoption.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class implements the EntityCollectionProcessor interface
 * It will fetch an entity collecrtion
 * Created by marcparis on 12/23/16.
 */
public class OdataEntityCollectionProcessor implements EntityCollectionProcessor {

    private OData odata;
    private ServiceMetadata srvMetadata;
    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.srvMetadata = serviceMetadata;
    }

    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException {

        try {
            List<UriResource> resourceParts = uriInfo.getUriResourceParts();
            int segmentCount = resourceParts.size();

            UriResource uriResource = resourceParts.get(0);

            if (!(uriResource instanceof UriResourceEntitySet)) {
                throw new ODataApplicationException("Only EntitySet is supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
            }
            // Read the top level entities
            UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
            EdmEntitySet responseEdmEntitySet = uriResourceEntitySet.getEntitySet();
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            EntityCollection responseEntityCollection = getStorage().readEntitySetData(responseEdmEntitySet, uriInfo);
            EdmEntityType responseEdmEntityType = responseEdmEntitySet.getEntityType();
            OrderByOption orderByOption = uriInfo.getOrderByOption();

            int segmentIndex = 1;
            while (segmentIndex < segmentCount) {
                UriResource lastSegment = resourceParts.get(segmentIndex);
                if (lastSegment instanceof UriResourceNavigation) {
                    UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) lastSegment;
                    EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
                    responseEdmEntityType = edmNavigationProperty.getType();
                    responseEdmEntitySet = Util.getNavigationTargetEntitySet(responseEdmEntitySet, edmNavigationProperty);

                    Entity sourceEntity = getStorage().readEntityData(responseEdmEntitySet, keyPredicates);
                    if (sourceEntity == null) {
                        throw new ODataApplicationException("Entity Not Found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.getDefault());
                    }

                    responseEntityCollection = getStorage().getRelatedEntityCollection(sourceEntity, responseEdmEntityType);
                    keyPredicates = uriResourceNavigation.getKeyPredicates();
                }
                segmentIndex++;
            }
            EdmEntityType entityType = uriResourceEntitySet.getEntityType();
            List<Entity> entityList = responseEntityCollection.getEntities();
            boolean countSet = processCount(responseEntityCollection, uriInfo);
            entityList = processSkip(entityList, uriInfo);
            entityList = processTop(entityList, uriInfo);
            String selectList = processSelect(entityType, uriInfo);

            // If the collection size was changed through use of the $top or $skip, update the returned list
            if (entityList.size() != responseEntityCollection.getEntities().size()) {
                entityList = new ArrayList<Entity>(entityList);// Avoid concurrent modification exception
                responseEntityCollection.getEntities().clear();
                for (Entity ent : entityList) {
                    responseEntityCollection.getEntities().add(ent);
                }
            }

            if (responseEdmEntitySet == null) {
                throw new ODataApplicationException("responseEDMEntitySet not found", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.getDefault());
            }

            EntityCollectionSerializerOptions.Builder builder = null;
            final String id = request.getRawBaseUri();

            // Build the builder. it changes if the select option has been defined
            if (selectList != null) {
                ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).selectList(selectList).build();
                builder = EntityCollectionSerializerOptions.with().contextURL(contextUrl).select(uriInfo.getSelectOption()).id(id);
            } else {
                ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).build();
                builder = EntityCollectionSerializerOptions.with().contextURL(contextUrl).id(id);
            }

            // Add the count if it was set
            if (countSet) {
                builder.count(uriInfo.getCountOption());
            }

            EntityCollectionSerializerOptions opts = builder.build();

            EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();

            ODataSerializer serializer = odata.createSerializer(responseFormat);
            SerializerResult serializerResult = serializer.entityCollection(this.srvMetadata, edmEntityType, responseEntityCollection, opts);

            response.setContent(serializerResult.getContent());
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        } catch (ODataException e) {
            throw new ODataApplicationException("sdfs", 0, null, e);
        }
    }

    private boolean processCount(EntityCollection entityCollection, UriInfo info) {
        if ((entityCollection == null) || (info == null)) {
            return false;
        }

        CountOption countOption = info.getCountOption();
        if (countOption != null) {
            boolean isCount = countOption.getValue();
            if (isCount) {
                entityCollection.setCount(entityCollection.getEntities().size());
            }
            return true;
        }
        return false;
    }

    private List<Entity> processSkip(List<Entity> entityList, UriInfo info) throws ODataApplicationException {
        if ((entityList == null) || (info == null)) {
            return entityList;
        }

        SkipOption skipOption = info.getSkipOption();
        if (skipOption != null) {
            int skip = skipOption.getValue();
            if (skip >= 0) {
                if (skip < entityList.size()) {
                    entityList = entityList.subList(skip, entityList.size());
                } else {
                    entityList.clear();
                }
            } else {
                throw new ODataApplicationException("Invalid value for $skip", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
            }
        }
        return entityList;
    }

    private List<Entity>  processTop(List<Entity> entityList, UriInfo info) throws ODataApplicationException {
        if ((entityList == null) || (info == null)) {
            return entityList;
        }

        TopOption topOption = info.getTopOption();
        if (topOption != null) {
            int top = topOption.getValue();
            if (top >= 0) {
                if (top < entityList.size()) {
                    entityList = entityList.subList(0, top);
                }
            } else {
                throw new ODataApplicationException("Invalid value for $top", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
            }
        }
        return entityList;
    }

    private String processSelect(EdmEntityType edmEntityType, UriInfo info) throws SerializerException {
        if (info == null) {
            return null;
        }
        SelectOption selectOption = info.getSelectOption();
        if (selectOption != null) {
            return odata.createUriHelper().buildContextURLSelectList(edmEntityType,null, selectOption);
        }
        return null;
    }
}
