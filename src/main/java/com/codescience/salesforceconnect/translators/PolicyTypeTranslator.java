package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Policy;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.BigDecimal;

/**
 * Subclass of ODataTypeTranslator to handle Policy Translation from Pojo Policy object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class PolicyTypeTranslator extends ODataTypeTranslator {

    /**
     * Implementation method to translate the BaseEntity inmplementation (Policy) to an Odata Entity object
     * @param object BaseEntity implementation (Policy)
     * @return Olingo entity for the Policy
     */
    public Entity translate(BaseEntity object) {
        Policy policy = (Policy) object;
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.POLICY_ID, ValueType.PRIMITIVE, policy.getId()));
        entity.addProperty(new Property(null, Constants.NUMBER_OF_UNITS, ValueType.PRIMITIVE, policy.getNumberOfUnits()));
        entity.addProperty(new Property(null, Constants.POLICY_END_DATE, ValueType.PRIMITIVE, policy.getPolicyEndDate()));
        entity.addProperty(new Property(null, Constants.POLICY_HOLDER_ID, ValueType.PRIMITIVE, policy.getPolicyHolderId()));
        entity.addProperty(new Property(null, Constants.POLICY_START_DATE, ValueType.PRIMITIVE, policy.getPolicyStartDate()));
        if (policy.getTotalCost() != null) {
            entity.addProperty(new Property(null, Constants.TOTAL_COST_AMOUNT, ValueType.PRIMITIVE, policy.getTotalCost().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, "Active", ValueType.PRIMITIVE, policy.isActive()));
        entity.setType(OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, "Id"));
        return entity;
    }

    /**
     * Method returns the Policy Set Name
     * @return String with the Policy Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_POLICIES_NAME;
    }
}
