package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.data.ClaimDAO;
import com.codescience.salesforceconnect.entities.Claim;
import com.codescience.salesforceconnect.service.OdataEdmProvider;

import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of the ClaimDAO interface for Simple in Memory Pojos
 */
public class ClaimDAOPojo implements ClaimDAO {

    // Reference to the object store
    private ObjectFactory factory = new ObjectFactory();

    /**
     * Method returns a map of Claims for a given policy id
     *
     * @param policyId Policy Id
     * @return Map of Claim objects
     */
    @Override
    public Map<String, Claim> findAllByPolicyId(String policyId) {
        Map<String, Claim> map = new TreeMap<String, Claim>();

        for (Claim claim : findAll().values()) {
            if ((policyId == null) && (claim.getPolicy() == null)) {
                map.put(claim.getRecordId(), claim);
            }
            else if ((policyId != null) && policyId.equalsIgnoreCase(claim.getPolicy().getRecordId())) {
                map.put(claim.getRecordId(), claim);
            }
        }
        return map;
    }

    /**
     * Method returns all objects of keyed by the record Id
     *
     * @return Map of entities keyed by Id
     */
    @Override
    public Map<String, Claim> findAll() {
        return factory.getClaims();
    }

    /**
     * Method returns the record based on the recordId passed in
     *
     * @param recordId String representing the record Id
     * @return Entity found by the recordId
     */
    @Override
    public Claim findByRecordId(String recordId) {
        return factory.getClaims().get(recordId);
    }

    /**
     * Method inserts the record based on the Entity passed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    @Override
    public Claim insert(Claim entity) {
        if (entity.getRecordId() == null) {
            entity.setRecordId(factory.getNewId(OdataEdmProvider.ET_CLAIM_FQN));
            factory.getClaims().put(entity.getRecordId(), entity);
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
    public Claim update(Claim entity) {
        factory.getClaims().put(entity.getRecordId(), entity);
        return entity;
    }

    /**
     * Method deletes the record passed in
     *
     * @param recordId String representing the record Id
     * @return Entity deleted
     */
    @Override
    public Claim delete(String recordId) {
        return factory.getClaims().remove(recordId);
    }
}
