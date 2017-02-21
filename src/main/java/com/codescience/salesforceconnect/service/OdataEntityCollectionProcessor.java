package com.codescience.salesforceconnect.service;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
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
import org.apache.olingo.server.api.uri.queryoption.IdOption;

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

    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {

        EdmEntitySet responseEdmEntitySet = null;
        EntityCollection responseEntityCollection = null;

        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        int segmentCount = resourceParts.size();

        UriResource uriResource = resourceParts.get(0);

        if(!(uriResource instanceof UriResourceEntitySet)) {
            throw new ODataApplicationException("Only EntitySet is supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
        EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();

        if (segmentCount == 1) {
            responseEdmEntitySet = startEdmEntitySet;
            responseEntityCollection = storage.readEntitySetData(startEdmEntitySet, uriInfo);
        }
        else {
            int segmentIndex = 1;
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            while (segmentIndex < segmentCount) {
                UriResource lastSegment = resourceParts.get(segmentIndex);
                if (lastSegment instanceof UriResourceNavigation) {
                    UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) lastSegment;
                    EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
                    EdmEntityType targetEntityType = edmNavigationProperty.getType();

                    responseEdmEntitySet = Util.getNavigationTargetEntitySet(startEdmEntitySet, edmNavigationProperty);

                    Entity sourceEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);
                    if (sourceEntity == null) {
                        throw new ODataApplicationException("Entity Not Found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.getDefault());
                    }

                    responseEntityCollection = storage.getRelatedEntityCollection(sourceEntity, targetEntityType);
                    startEdmEntitySet = responseEdmEntitySet;
                    keyPredicates = uriResourceNavigation.getKeyPredicates();
                }
                segmentIndex++;
            }
        }

        if (responseEdmEntitySet == null) {
            throw new ODataApplicationException("responseEDMEntitySet not found", HttpStatusCode.BAD_REQUEST.getStatusCode(),Locale.getDefault());
        }

        ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).build();
        final String id = request.getRawBaseUri();
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().contextURL(contextUrl).id(id).build();
        EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();

        ODataSerializer serializer = odata.createSerializer(responseFormat);
        SerializerResult serializerResult = serializer.entityCollection(this.srvMetadata, edmEntityType, responseEntityCollection, opts);

        response.setContent(serializerResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
}
