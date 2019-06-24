package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.data.BeneficiaryDAO;
import com.codescience.salesforceconnect.entities.Beneficiary;
import com.codescience.salesforceconnect.service.OdataEdmProvider;

import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of the BeneficiaryDAO interface for Simple in Memory Pojos
 */
public class BeneficiaryDAOPojo implements BeneficiaryDAO {

    // Reference to the object store
    private ObjectFactory factory = new ObjectFactory();

    /**
     * Method returns all objects of keyed by the record Id
     *
     * @return Map of entities keyed by Id
     */
    @Override
    public Map<String, Beneficiary> findAll() {
        return factory.getBeneficiaries();
    }

    /**
     * Method returns the record based on the recordId passed in
     *
     * @param recordId String representing the record Id
     * @return Entity found by the recordId
     */
    @Override
    public Beneficiary findByRecordId(String recordId) {
        return factory.getBeneficiaries().get(recordId);
    }

    /**
     * Method inserts the record based on the Entity passed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    @Override
    public Beneficiary insert(Beneficiary entity) {
        if (entity.getRecordId() == null) {
            entity.setRecordId(factory.getNewId(OdataEdmProvider.ET_CLAIM_FQN));
            factory.getBeneficiaries().put(entity.getRecordId(), entity);
        }
        return entity;
    }

    /**
     * Method updates the record based on the Entity passed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    @Override
    public Beneficiary update(Beneficiary entity) {
        factory.getBeneficiaries().put(entity.getRecordId(), entity);
        return entity;
    }

    /**
     * Method deletes the record passed in
     *
     * @param recordId String representing the record Id
     * @return Entity deleted
     */
    @Override
    public Beneficiary delete(String recordId) {
        return factory.getBeneficiaries().remove(recordId);
    }

    /**
     * Method returns a map of beneficiaries for a given contactIdentifier
     *
     * @param contactIdentifier Contact Identifier
     * @return Map of Beneficiary objects
     */
    @Override
    public Map<String, Beneficiary> findAllByContactIdentifier(String contactIdentifier) {
        Map<String, Beneficiary> map = new TreeMap<String, Beneficiary>();

        for (Beneficiary beneficiary : findAll().values()) {
            if ((contactIdentifier == null) && (beneficiary.getContactIdentifierId() == null)) {
                map.put(beneficiary.getRecordId(), beneficiary);
            }
            else if ((contactIdentifier != null) && contactIdentifier.equalsIgnoreCase(beneficiary.getContactIdentifierId())) {
                map.put(beneficiary.getRecordId(), beneficiary);
            }
        }
        return map;
    }

    /**
     * Method returns a map of beneficiaries for a given claimId
     *
     * @param claimId Claim Id
     * @return Map of Beneficiary objects
     */
    @Override
    public Map<String, Beneficiary> findAllByClaimId(String claimId) {
        Map<String, Beneficiary> map = new TreeMap<String, Beneficiary>();

        for (Beneficiary beneficiary : findAll().values()) {
            if ((claimId == null) && (beneficiary.getClaim() == null)) {
                map.put(beneficiary.getRecordId(), beneficiary);
            }
            else if ((claimId != null) && claimId.equalsIgnoreCase(beneficiary.getClaim().getRecordId())) {
                map.put(beneficiary.getRecordId(), beneficiary);
            }
        }
        return map;
    }
}
