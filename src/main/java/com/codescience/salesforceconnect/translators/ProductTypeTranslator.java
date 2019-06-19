package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.entities.Product;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.BigDecimal;

/**
 * Subclass of ODataTypeTranslator to handle Product Translation from Pojo Produc t object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class ProductTypeTranslator extends ODataTypeTranslator<Product> {

    /**
     * Implementation method to translate the BaseEntity implementation (Product) to an Olingo Entity object
     * @param product BaseEntity implementation (Product)
     * @return Olingo entity for the Product
     */
    public Entity translate(Product product) {
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.PRODUCT_ID, ValueType.PRIMITIVE, product.getRecordId()));
        entity.addProperty(new Property(null, Constants.PRODUCT_NAME, ValueType.PRIMITIVE, product.getProductName()));
        entity.addProperty(new Property(null, Constants.PRODUCT_TYPE, ValueType.PRIMITIVE, product.getProductType()));
        if (product.getCostPerUnit() != null) {
            entity.addProperty(new Property(null, Constants.COST_PER_UNIT_AMOUNT, ValueType.PRIMITIVE, product.getCostPerUnit().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, Constants.PRODUCT_ACTIVE, ValueType.PRIMITIVE, product.isActive()));
        entity.setType(OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, Constants.PRODUCT_ID));
        return entity;
    }

    /**
     * Implementation method to translate the Olingo Entity object to the BaseEntity implementation (Product)
     * @param entity Olingo entity
     * @param storage Storage implementation
     * @param merge boolean if true then nulls won't overwrite non nulls
     * @return BaseEntity implementation (Product)
     */
    public Product translate(Entity entity, Storage storage, boolean merge) {
        Product product = new Product();
        Property prop = entity.getProperty(Constants.PRODUCT_ID);
        Product existingProduct = getExistingEntity(storage.getDataAccessObjects().get(OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString()),Product.class, prop);

        product.setRecordId(existingProduct.getRecordId());

        prop = entity.getProperty(Constants.PRODUCT_NAME);
        product.setProductName((String) extractValue(existingProduct.getProductName(), prop, merge));

        prop = entity.getProperty(Constants.PRODUCT_TYPE);
        product.setProductType((String) extractValue(existingProduct.getProductType(), prop, merge));

        prop = entity.getProperty(Constants.COST_PER_UNIT_AMOUNT);
        product.setCostPerUnit((BigDecimal) extractValue(existingProduct.getCostPerUnit(), prop, merge));

        prop = entity.getProperty(Constants.PRODUCT_ACTIVE);
        product.setActive((Boolean) extractValue(existingProduct.isActive(), prop, merge));
        return product;
    }

    /**
     * Method returns the Product Set Name
     * @return String with the Product Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_PRODUCTS_NAME;
    }
}
