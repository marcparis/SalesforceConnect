package com.codescience.salesforceconnect.service;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.*;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of EntityProcessor to process Odata Entities
 */
public class OdataEntityProcessor implements EntityProcessor {

    private OData odata;
    private ServiceMetadata srvMetadata;
    private Storage storage;

    /**
     * Method returns the Storage implementation
     * @return Implementation of the Storage interface
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Method sets the Storage implmentation
     * @param storage Implementation of the Storage interface
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Method called through the Olingo lifecycle to set the odata environments variables
     * @param odata Olingo OData implementation
     * @param serviceMetadata Olingo Service Metadata
     */
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.srvMetadata = serviceMetadata;
    }

    /**
     * Method called to read an OData Entity. Part of the Olingo lifecycle
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @param responseFormat Content type of the result
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     * @throws SerializerException Error thrown if unable to serialize the result to the content type
     */
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
        EdmEntityType responseEdmEntityType = null;
        Entity responseEntity = null;
        EdmEntitySet responseEdmEntitySet = null;

        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        int segmentCount = resourceParts.size();

        UriResource uriResource = resourceParts.get(0);
        if(!(uriResource instanceof UriResourceEntitySet)) {
            throw new ODataApplicationException("Only EntitySet is supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }

        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
        EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();

        if (segmentCount == 1) {
            responseEdmEntityType = startEdmEntitySet.getEntityType();
            responseEdmEntitySet = startEdmEntitySet;
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            responseEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);
        }
        else {
            int segmentIndex = 1;
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            while (segmentIndex < segmentCount) {
                UriResource nextSegment = resourceParts.get(segmentIndex);
                if (nextSegment instanceof UriResourceNavigation) {
                    UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) nextSegment;
                    EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
                    EdmEntityType targetEntityType = edmNavigationProperty.getType();
                    responseEdmEntitySet = Util.getNavigationTargetEntitySet(startEdmEntitySet, edmNavigationProperty);
                    responseEdmEntityType = responseEdmEntitySet.getEntityType();

                    Entity sourceEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);

                    List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();

                    if (navKeyPredicates.isEmpty()) {
                        responseEntity = storage.getRelatedEntity(sourceEntity, responseEdmEntityType);
                    } else {
                        responseEntity = storage.getRelatedEntity(sourceEntity, responseEdmEntityType, navKeyPredicates);
                    }
                    startEdmEntitySet = responseEdmEntitySet;
                    keyPredicates = uriResourceNavigation.getKeyPredicates();
                }
                segmentIndex++;
            }
        }

        if (responseEdmEntitySet == null) {
            throw new ODataApplicationException("responseEDMEntitySet not found", HttpStatusCode.BAD_REQUEST.getStatusCode(),Locale.getDefault());
        }
        ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).suffix(ContextURL.Suffix.ENTITY).build();
        EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextUrl).build();

        ODataSerializer serializer = this.odata.createSerializer(responseFormat);

        SerializerResult serializerResult = serializer.entity(this.srvMetadata, responseEdmEntityType, responseEntity, opts);

        response.setContent(serializerResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    /**
     * Method called to create a new entity. Currently not supported
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @param requestFormat ContentType of the input data
     * @param responseFormat Content type of the result
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     * @throws DeserializerException Error thrown if unable to deserialize the input
     * @throws SerializerException Error thrown if unable to serialize the result to the content type
     */
    public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
                             ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, DeserializerException, SerializerException {
        throw new ODataApplicationException("Not supported.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
    }

    /**
     * Method called to update an entity. Currently not supported
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @param requestFormat ContentType of the input data
     * @param responseFormat Content type of the result
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     * @throws DeserializerException Error thrown if unable to deserialize the input
     * @throws SerializerException Error thrown if unable to serialize the result to the content type
     */
    public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
                             ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, DeserializerException, SerializerException {
        throw new ODataApplicationException("Not supported.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
    }

    /**
     * Method called to delete an entity. Currently not supported
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     */
    public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
            throws ODataApplicationException {
        throw new ODataApplicationException("Not supported.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
    }
}
