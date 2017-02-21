package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
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
public class ProductTypeTranslator extends ODataTypeTranslator {

    /**
     * Implementation method to translate the BaseEntity inmplementation (Product) to an Odata Entity object
     * @param object BaseEntity implementation (Product)
     * @return Olingo entity for the Product
     */
    public Entity translate(BaseEntity object) {
        Product product = (Product) object;
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.PRODUCT_ID, ValueType.PRIMITIVE, product.getId()));
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
     * Method returns the Product Set Name
     * @return String with the Product Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_PRODUCTS_NAME;
    }
}
