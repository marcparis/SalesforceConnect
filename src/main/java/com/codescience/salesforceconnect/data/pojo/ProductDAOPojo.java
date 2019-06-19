package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.data.ProductDAO;
import com.codescience.salesforceconnect.entities.Product;
import com.codescience.salesforceconnect.service.OdataEdmProvider;

import java.util.Map;

/**
 * Implementation of the ProductDAO interface for Simple in Memory Pojos
 */
public class ProductDAOPojo implements ProductDAO {

    private ObjectFactory factory = new ObjectFactory();

    /**
     * Method returns all objects of keyed by the record Id
     *
     * @return Map of entities keyed by Id
     */
    @Override
    public Map<String, Product> findAll() {
        return factory.getProducts();
    }

    /**
     * Method returns the record based on the recordId passed in
     *
     * @param recordId String representing the record Id
     * @return Entity found by the recordId
     */
    @Override
    public Product findByRecordId(String recordId) {
        return factory.getProducts().get(recordId);
    }

    /**
     * Method inserts the record baseed in
     *
     * @param entity Entity to persist
     * @return Entity persisted
     */
    @Override
    public Product insert(Product entity) {
        if (entity.getRecordId() == null) {
            entity.setRecordId(factory.getNewId(OdataEdmProvider.ET_PRODUCT_FQN));
            factory.getProducts().put(entity.getRecordId(), entity);
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
    public Product update(Product entity) {
        factory.getProducts().put(entity.getRecordId(), entity);
        return entity;
    }

    /**
     * Method deletes the record passed in
     *
     * @param recordId String representing the record Id
     * @return Entity deleted
     */
    @Override
    public Product delete(String recordId) {
        return factory.getProducts().remove(recordId);
    }
}
