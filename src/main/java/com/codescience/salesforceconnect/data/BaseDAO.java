package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.entities.BaseEntity;

import java.util.Map;

/**
 * Base Interface to handle Data access operations
 * @param <T> Type for the Class to Implement
 */
public interface BaseDAO<T extends BaseEntity> {

    /**
     * Method returns all objects of keyed by the record Id
     * @return Map of entities keyed by Id
     */
    public Map<String, T> findAll();

    /**
     * Method returns the record based on the recordId passed in
     * @param recordId String representing the record Id
     * @return Entity found by the recordId
     */
    public T findByRecordId(String recordId);

    /**
     * Method inserts the record based on the Entity passed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    public T insert(T entity);

    /**
     * Method updates the record baseed in
     * @param entity Entity to persist
     * @return Entity persisted
     */
    public T update(T entity);


    /**
     * Method deletes the record passed in
     * @param recordId String representing the record Id
     * @return Entity deleted
     */
    public T delete(String recordId);

}
