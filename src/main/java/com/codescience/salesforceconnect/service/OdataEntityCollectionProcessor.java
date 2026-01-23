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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class implements the EntityCollectionProcessor interface
 * It will fetch an entity collection
 * Created by marcparis on 12/23/16.
 */
public class OdataEntityCollectionProcessor implements EntityCollectionProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(OdataEntityCollectionProcessor.class);

    private OData odata;
    private ServiceMetadata srvMetadata;
    private Storage storage;

    /**
     * Method returns the back end storage implementation (ex SQL, POJO Objects, Hibernate etc.)
     * @return Storage Implementation
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Method sets the back end storage implementation (ex SQL, POJO Objects, Hibernate etc.)
     * @param storage Storage implementation
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Method initializes the object required to process requests
     * @param odata The OData data
     * @param serviceMetadata The Service Metadata
     */
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        LOG.info("In init method");
        this.odata = odata;
        this.srvMetadata = serviceMetadata;
    }

    /**
     * Method used to Read a collection of entities. It will handle filters, sorts, skips, tops, and counts
     * @param request ODataRequest containing the input data
     * @param response OdataResponse with serialized data
     * @param uriInfo UriInfo containing many of the parameters
     * @param responseFormat ContentType of the response
     * @throws ODataApplicationException Exception thrown if an error occurred
     */
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException {
        LOG.info("In readEntityCollection method");

        try {
            List<UriResource> resourceParts = uriInfo.getUriResourceParts();
            int segmentCount = resourceParts.size();

            UriResource uriResource = resourceParts.get(0);

            if (!(uriResource instanceof UriResourceEntitySet)) {
                LOG.error(Messages.ERROR_ONLY_ENTITY_SET);
                throw new ODataApplicationException(Messages.ERROR_ONLY_ENTITY_SET, HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
            }
            // Read the top level entities
            UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
            EdmEntitySet responseEdmEntitySet = uriResourceEntitySet.getEntitySet();
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            EntityCollection responseEntityCollection = getStorage().readEntitySetData(responseEdmEntitySet, uriInfo);

            int segmentIndex = 1;
            while (segmentIndex < segmentCount) {
                UriResource lastSegment = resourceParts.get(segmentIndex);
                if (lastSegment instanceof UriResourceNavigation) {
                    UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) lastSegment;
                    EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
                    EdmEntityType responseEdmEntityType = edmNavigationProperty.getType();
                    responseEdmEntitySet = Util.getNavigationTargetEntitySet(responseEdmEntitySet, edmNavigationProperty);

                    Entity sourceEntity = getStorage().readEntityData(responseEdmEntitySet, keyPredicates);
                    if (sourceEntity == null) {
                        LOG.error(Messages.ERROR_ENTITY_NOT_FOUND);
                        throw new ODataApplicationException(Messages.ERROR_ENTITY_NOT_FOUND, HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
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
                entityList = new ArrayList<>(entityList);// Avoid concurrent modification exception
                responseEntityCollection.getEntities().clear();
                for (Entity ent : entityList) {
                    responseEntityCollection.getEntities().add(ent);
                }
            }

            if (responseEdmEntitySet == null) {
                LOG.error(Messages.ERROR_RESPONSE_EDM_NOT_SET);
                throw new ODataApplicationException(Messages.ERROR_RESPONSE_EDM_NOT_SET, HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
            }

            EntityCollectionSerializerOptions.Builder builder;
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
            throw new ODataApplicationException(Messages.ERROR_OCCURRED_READ_ENTITY_COLLECTION + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        }
    }

    /**
     * Method return the count of records in the collection if the $count=true parameter is passed in
     * @param entityCollection Collection of entities to count
     * @param info URIInfo containing the count parameter
     * @return true if count was requested, false otherwise
     */
    private boolean processCount(EntityCollection entityCollection, UriInfo info) {
        LOG.info("In processCount method");
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

    /**
     * Method will skip the number of records passed in if the skip parameter is set. It will return an empty list if skip > count of records
     * @param entityList List of entities that should be processed
     * @param info URIInfo containing the skip parameter
     * @return List of entities reduced by skip if passed in
     * @throws ODataApplicationException Throw error if an invalid skip value passed in
     */
    private List<Entity> processSkip(List<Entity> entityList, UriInfo info) throws ODataApplicationException {
        LOG.info("In processSkip method");
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
                LOG.error(Messages.ERROR_INVALID_VALUE_FOR_SKIP + "{}", skip);
                throw new ODataApplicationException(Messages.ERROR_INVALID_VALUE_FOR_SKIP+skip, HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
            }
        }
        return entityList;
    }

    /**
     * Method will reduce to top count the number of returned items in the list of entities.
     * @param entityList List of entities that should be processed
     * @param info URIInfo containing the top parameter
     * @return List of entities reduced to top if passed in
     * @throws ODataApplicationException Throw error if an invalid top value passed in
     */
    private List<Entity>  processTop(List<Entity> entityList, UriInfo info) throws ODataApplicationException {
        LOG.info("In processTop method");
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
                LOG.error(Messages.ERROR_INVALID_VALUE_FOR_TOP + "{}", top);
                throw new ODataApplicationException(Messages.ERROR_INVALID_VALUE_FOR_TOP+top, HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
            }
        }
        return entityList;
    }

    /**
     * Method processes the Select Option if passed in. It will reduce the number of fields to only those included in the Select
     * @param edmEntityType EntityType being process
     * @param info URIInfo containing the select parameter
     * @return String containing the select fields - null if none passed in
     * @throws SerializerException exception thrown if unable to process select fields
     */
    private String processSelect(EdmEntityType edmEntityType, UriInfo info) throws SerializerException {
        LOG.info("In processSelect method");
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
