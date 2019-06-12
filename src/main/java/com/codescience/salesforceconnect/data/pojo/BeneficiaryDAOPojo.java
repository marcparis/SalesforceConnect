package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.data.BeneficiaryDAO;
import com.codescience.salesforceconnect.entities.Beneficiary;
import com.codescience.salesforceconnect.service.OdataEdmProvider;

import java.util.Map;

public class BeneficiaryDAOPojo implements BeneficiaryDAO {

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
     * Method inserts the record baseed in
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
     * Method updates the record baseed in
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
}
