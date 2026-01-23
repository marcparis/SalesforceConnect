package com.codescience.salesforceconnect.data;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;

import java.util.List;

/**
 * Storage Interface defined methods to access data. Underlying implementation can handle
 * different storage mechanism such as JPA, SQL, Pojo
 */
public interface Storage {

    /**
     * Method returns an EntityCollection for the given Entity Set passed in
     * @param edmEntitySet EDM Entity Set for the type to return
     * @return EntityCollection containing object objects for the entitySet
     */
    EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, UriInfo uriInfo) throws ODataException;

    /**
     * Method returns a single Entity for the EntitySet. It filters based on the keyParams
     * @param edmEntitySet EDM Entity Set for the type to return
     * @param keyParams List of key parameters to find the record
     * @return Entity for the key params passed in
     */
    Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) throws ODataException;

    /**
     * Method returns the related entity collection for the source entity passed in. This allows
     * the returning of Claims for a given policy or Beneficiaries for a given claim
     * @param sourceEntity Source entity object that contains the relationship
     * @param targetEntityType Target entity collection in a one-to-many relationship
     * @return EntityCollection containing 0 or more entities of the target entity type
     */
    EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) throws ODataException;

    /**
     * Method returns the related entity for the source entity passed in. Can be filtered by URIParameters
     * @param entity Source entity
     * @param relatedEntityType Type of the related entity
     * @param keyPredicates key filter params
     * @return Entity that is related to the source entity
     */
    Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType, List<UriParameter> keyPredicates) throws ODataException;

    /**
     * Method returns the related entity for the source entity passed in. No filters are applied
     * This is usually used on the many side of a one-to-many relationship
     * @param entity Source entity
     * @param relatedEntityType Type of related entity
     * @return Entity that is related to the source entity
     */
    Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType) throws ODataException;

    /**
     * Method Creates the entity for the source entity passed in and persists it
     * @param entity Source entity
     * @return Entity that was newly created
     * @throws ODataException if error occurred creating the entity
     */
    Entity createEntity(Entity entity) throws ODataException;

    /**
     * Method Updates the entity for the source entity passed in and persists it
     * @param entity Source entity
     * @param forceNulls If true a null passed in will replace a value, if false it won't
     * @return Entity that was newly updated
     * @throws ODataException if error occurred updating the entity
     */
    Entity updateEntity(Entity entity, boolean forceNulls) throws ODataException;

    /**
     * Method Deletes the entity for the source entity passed in and persists it
     * @param edmEntitySet Source entitySet type
     * @param keyPredicates Primary key to find record to delete
     * @return Entity that was deleted from persistent storage
     */
    Entity deleteEntity(EdmEntitySet edmEntitySet, List<UriParameter> keyPredicates);

    /**
     * Method Upserts the entity for the source entity passed in and persists it.
     * It will update it if it matches an existing record by key, otherwise it will create new
     * @param entity Source entity
     * @param forceNulls If true a null passed in will replace a value, if false it won't
     * @return Entity that was upserted in storage
     * @throws ODataException if error occurred creating or updating the entity
     */
    Entity upsertEntity(Entity entity, boolean forceNulls) throws ODataException;
}