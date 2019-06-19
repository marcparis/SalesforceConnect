package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.data.PolicyDAO;
import com.codescience.salesforceconnect.entities.Policy;
import com.codescience.salesforceconnect.service.OdataEdmProvider;

import java.util.Map;

/**
 * Implementation of the PolicyDAO interface for Simple in Memory Pojos
 */
public class PolicyDAOPojo implements PolicyDAO {

    private ObjectFactory factory = new ObjectFactory();

    /**
     * Method returns all objects of keyed by the record Id
     *
     * @return Map of entities keyed by Id
     */
    @Override
    public Map<String, Policy> findAll() {
        return factory.getPolicies();
    }

    /**
     * Method returns the record based on the recordId passed in
     *
     * @param recordId String representing the record Id
     * @return Entity found by the recordId
     */
    @Override
    public Policy findByRecordId(String recordId) {
        return factory.getPolicies().get(recordId);
    }

    /**
     * Method inserts the record baseed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    @Override
    public Policy insert(Policy entity) {
        if (entity.getRecordId() == null) {
            entity.setRecordId(factory.getNewId(OdataEdmProvider.ET_PRODUCT_FQN));
            factory.getPolicies().put(entity.getRecordId(), entity);
        }
        return entity;
    }

    /**
     * Method updates the record baseed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    @Override
    public Policy update(Policy entity) {
        factory.getPolicies().put(entity.getRecordId(), entity);
        return entity;
    }

    /**
     * Method deletes the record passed in
     *
     * @param recordId String representing the record Id
     * @return Entity deleted
     */
    @Override
    public Policy delete(String recordId) {
        return factory.getPolicies().remove(recordId);
    }
}
