package com.codescience.salesforceconnect.service;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of EntityProcessor to process Odata Entities
 */
public class OdataEntityProcessor implements EntityProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(OdataEntityProcessor.class);

    private OData odata;
    private ServiceMetadata serviceMetadata;
    private Storage storage;

    /**
     * Method returns the Storage implementation
     * @return Implementation of the Storage interface
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Method sets the Storage implementation
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
        LOG.info("In init method");
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    /**
     * Method called to read an OData Entity. Part of the Olingo lifecycle
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @param responseFormat Content type of the result
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     */
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException {
        LOG.info("In readEntity method");
        try {
            UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySet(uriInfo);
            List<UriResource> resourceParts = uriInfo.getUriResourceParts();
            int segmentCount = resourceParts.size();

            // Read the top level entity
            EdmEntitySet responseEdmEntitySet = uriResourceEntitySet.getEntitySet();
            EdmEntityType responseEdmEntityType = responseEdmEntitySet.getEntityType();
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            Entity responseEntity = getStorage().readEntityData(responseEdmEntitySet, keyPredicates);
            int segmentIndex = 1;

            // If there are more object navigation then keep reading each object until you reach the end of the url
            // Ex Object(1)/Object2/Object3
            while (segmentIndex < segmentCount) {
                UriResource nextSegment = resourceParts.get(segmentIndex);
                if (nextSegment instanceof UriResourceNavigation) {
                    UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) nextSegment;
                    EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
                    responseEdmEntitySet = Util.getNavigationTargetEntitySet(responseEdmEntitySet, edmNavigationProperty);
                    responseEdmEntityType = responseEdmEntitySet.getEntityType();

                    List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();

                    // If not nav key predicatest it's going from the many side to the one side. If there are it's the opposite
                    if (navKeyPredicates.isEmpty()) {
                        responseEntity = getStorage().getRelatedEntity(responseEntity, responseEdmEntityType);
                    } else {
                        responseEntity = getStorage().getRelatedEntity(responseEntity, responseEdmEntityType, navKeyPredicates);
                    }
                    segmentIndex++;
                }
            }
            ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).suffix(ContextURL.Suffix.ENTITY).build();
            EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextUrl).build();

            ODataSerializer serializer = this.odata.createSerializer(responseFormat);

            SerializerResult serializerResult = serializer.entity(this.serviceMetadata, responseEdmEntityType, responseEntity, opts);

            response.setContent(serializerResult.getContent());
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        } catch (ODataException e) {
            LOG.error(Messages.ERROR_READING_ENTITY,e);
            throw new ODataApplicationException(Messages.ERROR_READING_ENTITY, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        }
    }

    /**
     * Method called to create a new entity. Currently not supported
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @param requestFormat ContentType of the input data
     * @param responseFormat Content type of the result
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     */
    public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
                             ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException {
        LOG.info("In createEntity method");
        try {
            UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySet(uriInfo);
            Entity entity = getEntityFromRequest(request, uriInfo, requestFormat, uriResourceEntitySet.getKeyPredicates());
            entity = getStorage().createEntity(entity);
            // Read the top level entity

            EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
            EdmEntityType edmEntityType = edmEntitySet.getEntityType();
            // 3. serialize the response (we have to return the created entity)
            ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
            // expand and select currently not supported
            EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

            ODataSerializer serializer = this.odata.createSerializer(responseFormat);
            SerializerResult serializedResponse = serializer.entity(serviceMetadata, edmEntityType, entity, options);

            //4. configure the response object
            response.setContent(serializedResponse.getContent());
            response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        } catch (ODataException e) {
            LOG.error(Messages.ERROR_CREATING_ENTITY,e);
            throw new ODataApplicationException(Messages.ERROR_CREATING_ENTITY, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        }
    }

    /**
     * Method called to update an entity. Currently not supported
     * @param request ODataRequest object
     * @param response ODataResponse object with the result
     * @param uriInfo Information about the url path
     * @param requestFormat ContentType of the input data
     * @param responseFormat Content type of the result
     * @throws ODataApplicationException If an exception or unsupported method is called throw this error
     */
    public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
                             ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException {
        LOG.info("In updateEntity method");
        try {
            // Read the entity from the parameters passed in
            UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySet(uriInfo);
            Entity entity = getEntityFromRequest(request, uriInfo, requestFormat,uriResourceEntitySet.getKeyPredicates());

            // If a PUT method - it will set to null any nullable setter non-primitive that is not passed int
            HttpMethod httpMethod = request.getMethod();
            boolean forceNulls = false;
            if (HttpMethod.PUT.equals(httpMethod)) {
                forceNulls = true;
            }

            // Update the entity and return
            getStorage().updateEntity(entity, forceNulls);
            response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
        } catch (ODataException e) {
            LOG.error(Messages.ERROR_UPDATING_ENTITY,e);
            throw new ODataApplicationException(Messages.ERROR_UPDATING_ENTITY, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        }
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
        LOG.info("In deleteEntity method");
        try {
            // 1. Retrieve the entity set which belongs to the requested entity
            List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
            // Note: only in our example we can assume that the first segment is the EntitySet
            UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
            EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

            // 2. delete the data in backend
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            storage.deleteEntity(edmEntitySet, keyPredicates);

            response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
        } catch (ODataException e) {
            LOG.error(Messages.ERROR_UPDATING_ENTITY,e);
            throw new ODataApplicationException(Messages.ERROR_UPDATING_ENTITY, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        }
    }

    /**
     * Method used to build the Entity object from the Request input body (ex during a create/update)
     * It ensures they keys are set
     * @param request ODataRequest containing the HttpRequest
     * @param uriInfo Info about the uri used to extract type and keys
     * @param requestFormat Content type of the request
     * @param keyPredicates List of parameters used as keys
     * @return Entity object populated
     * @throws DeserializerException Exception throw if unable to convert to a valid entity
     */
    private Entity getEntityFromRequest(ODataRequest request, UriInfo uriInfo, ContentType requestFormat, List<UriParameter> keyPredicates) throws DeserializerException {
        LOG.info("In getEntityFromRequest method");
        // Retrieve the entity set which belongs to the requested entity
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        // Note: We are assuming that the first segment is the EntitySet
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        // update the data in backend
        // retrieve the payload from the request for the entity
        InputStream requestInputStream = request.getBody();
        ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
        DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);

        // Extract the key from the parameters in the url - get the Ids and populate them
        // We are assuming that these are simple single value keys
        String id = Util.getPrimaryKeyFromParam(edmEntityType.getKeyPredicateNames(), keyPredicates);
        Entity entity = result.getEntity();
        entity.addProperty(new Property(null, Constants.ID, ValueType.PRIMITIVE, id));
        // Created the URI id
        entity.setId(Util.createId(entity, Constants.ID, null, edmEntitySet.getName()));
        return entity;
    }

    /**
     * Method return the URIResourceEntitySet from the uriInfo passed int
     * @param uriInfo URI Info containing the data
     * @return UriResourceEntitySet extracted and validated. Must be a UriResourceEntitySet
     * @throws ODataApplicationException if an error occurred
     */
    private UriResourceEntitySet getUriResourceEntitySet(UriInfo uriInfo) throws ODataApplicationException {
        LOG.info("In getUriResourceEntitySet method");
        List<UriResource> resourceParts = uriInfo.getUriResourceParts();

        UriResource uriResource = resourceParts.get(0);
        if(!(uriResource instanceof UriResourceEntitySet)) {
            LOG.error(Messages.ERROR_ONLY_ENTITY_SET);
            throw new ODataApplicationException(Messages.ERROR_ONLY_ENTITY_SET, HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
        }

        return (UriResourceEntitySet) uriResource;
    }
}