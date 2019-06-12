package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Policy;
import com.codescience.salesforceconnect.entities.Product;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Subclass of ODataTypeTranslator to handle Policy Translation from Pojo Policy object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class PolicyTypeTranslator extends ODataTypeTranslator<Policy> {

    /**
     * Implementation method to translate the BaseEntity implementation (Policy) to an Olingo Entity object
     * @param policy BaseEntity implementation (Policy)
     * @return Olingo entity for the Policy
     */
    public Entity translate(Policy policy) {
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.POLICY_ID, ValueType.PRIMITIVE, policy.getRecordId()));
        entity.addProperty(new Property(null, Constants.NUMBER_OF_UNITS, ValueType.PRIMITIVE, policy.getNumberOfUnits()));
        entity.addProperty(new Property(null, Constants.POLICY_END_DATE, ValueType.PRIMITIVE, policy.getPolicyEndDate()));
        entity.addProperty(new Property(null, Constants.POLICY_HOLDER_ID, ValueType.PRIMITIVE, policy.getPolicyHolderId()));
        entity.addProperty(new Property(null, Constants.POLICY_START_DATE, ValueType.PRIMITIVE, policy.getPolicyStartDate()));
        if (policy.getTotalCost() != null) {
            entity.addProperty(new Property(null, Constants.TOTAL_COST_AMOUNT, ValueType.PRIMITIVE, policy.getTotalCost().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, Constants.POLICY_ACTIVE, ValueType.PRIMITIVE, policy.isActive()));
        entity.addProperty(new Property(null, Constants.POLICY_PRODUCT_ID, ValueType.PRIMITIVE, policy.getProduct().getRecordId()));
        entity.setType(OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, Constants.POLICY_ID));
        return entity;
    }

    /**
     * Implementation method to translate the Olingo Entity object to the BaseEntity implementation (Policy)
     * @param entity Olingo entity
     * @param storage Storage implementation
     * @param merge boolean if true then nulls won't overwrite non nulls
     * @return BaseEntity implementation (Policy)
     */
    public Policy translate(Entity entity, Storage storage, boolean merge) {
        Policy policy = new Policy();
        Property prop = entity.getProperty(Constants.POLICY_ID);
        Policy existingPolicy  = (Policy) storage.getDataAccessObjects().get(OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString()).findByRecordId((String) prop.getValue());

        prop = entity.getProperty(Constants.NUMBER_OF_UNITS);
        policy.setNumberOfUnits((Integer) extractValue(existingPolicy.getNumberOfUnits(), prop, merge));
        prop = entity.getProperty(Constants.POLICY_END_DATE);
        policy.setPolicyEndDate((Date) extractValue(existingPolicy.getPolicyEndDate(), prop, merge));

        prop = entity.getProperty(Constants.POLICY_HOLDER_ID);
        policy.setPolicyHolderId((String) extractValue(existingPolicy.getPolicyHolderId(), prop, merge));

        prop = entity.getProperty(Constants.POLICY_START_DATE);
        policy.setPolicyStartDate((Date) extractValue(existingPolicy.getPolicyStartDate(), prop, merge));

        prop = entity.getProperty(Constants.POLICY_PRODUCT_ID);
        String existingProductId = existingPolicy.getProduct() == null ? null : existingPolicy.getProduct().getRecordId();
        String productId = (String) extractValue(existingProductId, prop, merge);

        if (productId != null) {
            policy.setProduct((Product) storage.getDataAccessObjects().get(OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString()).findByRecordId((String) prop.getValue()));
        }
        return policy;
    }

    /**
     * Method returns the Policy Set Name
     * @return String with the Policy Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_POLICIES_NAME;
    }
}
