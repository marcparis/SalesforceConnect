package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.translators.ODataTypeTranslator;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;

import java.util.List;
import java.util.Map;

/**
 * Storage Interface defined methods to access data. Underlying implementation can handle
 * different storage mechanism such as JPA, SQL, pojos
 */
public interface Storage {

    /**
     * Method returns an EntityCollection for the given Entity Set passed in
     * @param edmEntitySet EDMEntitySet for the type to return
     * @return EntityCollection containing object objects for the entityset
     */
    EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, UriInfo uriInfo);

    /**
     * Method retuns a single Entity for the EntitySet. It fileters based on the keyParams
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param keyParams List of key parameters to find the record
     * @return Entity for the key params passed in
     */
    Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams);

    /**
     * Method retuns a single Entity for the EntitySet. It fileters based on the keyParams
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param entityToCreate Entity to Create
     * @return Entity for the key params passed in
     */
    Entity createEntityData(EdmEntitySet edmEntitySet, Entity entityToCreate);

    /**
     * Method retuns a single Entity for the EntitySet. It fileters based on the keyParams
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param keyParams List of key parameters to find the record
     * @return Entity for the key params passed in
     */
    Entity updateEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams, Entity entity, HttpMethod method);

    /**
     * Method retuns a single Entity for the EntitySet. It fileters based on the keyParams
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param keyParams List of key parameters to find the record
     * @return Entity for the key params passed in
     */
    Entity deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams);

    /**
     * Method returns the related entity collection for the source entity passed in. This allows
     * the returning of Claims for a given policy or Beneficiaries for a given claim
     * @param sourceEntity Source entity object that contains the relationship
     * @param targetEntityType Target entity collection in a one to many relationship
     * @return EntityCollection containing 0 or more entities of the target entity type
     */
    EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType);

    /**
     * Method returns the related entity for the source entity passed in. Can be filtered by URIParameters
     * @param entity Source entity
     * @param relatedEntityType Type of the related entity
     * @param keyPredicates key filter params
     * @return Entity that is related to the source entity
     */
    Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType, List<UriParameter> keyPredicates);

    /**
     * Method returns the related entity for the source entity passed in. No filters are applied
     * This is usually used on the many side of a one to many relationship
     * @param entity Source entity
     * @param relatedEntityType Type of related entity
     * @return Entity that is related to the source entity
     */
    Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType);

    /**
     * Method returns the TypeTranslators Map.
     * @return TypeTranslator map that contains type translators for each object
     */
    public Map<String, ODataTypeTranslator> getTypeTranslators();

    /**
     * Method sets the TypeTranslators Map
     * @param typeTranslators TypeTranslator Map that contains type translators for each object
     */
    public void setTypeTranslators(Map<String, ODataTypeTranslator> typeTranslators);

    /**
     * Method returns a Map of BaseDAO implementations
     * @return DAO map that contains the DAO implementations for each object
     */
    public Map<String, BaseDAO> getDataAccessObjects();

    /**
     * Method sets the Map of BaseDAO implementations
     * @param dataAccessObjects DAO map that contains the DAO implementations for each object
     */
    public void setDataAccessObjects(Map<String, BaseDAO> dataAccessObjects);
}