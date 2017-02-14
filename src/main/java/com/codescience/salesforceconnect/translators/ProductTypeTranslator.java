package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Product;
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

        entity.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, product.getId()));
        entity.addProperty(new Property(null, "ProductName", ValueType.PRIMITIVE, product.getProductName()));
        entity.addProperty(new Property(null, "ProductType", ValueType.PRIMITIVE, product.getProductType()));
        if (product.getCostPerUnit() != null) {
            entity.addProperty(new Property(null, "CostPerUnitAmount", ValueType.PRIMITIVE, product.getCostPerUnit().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, "ActiveProduct", ValueType.PRIMITIVE, product.isActiveProduct()));
        entity.setType(OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, "Id"));
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
