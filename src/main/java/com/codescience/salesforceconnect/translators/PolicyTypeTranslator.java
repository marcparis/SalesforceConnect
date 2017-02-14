package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Policy;
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

        entity.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, policy.getId()));
        entity.addProperty(new Property(null, "NumberOfUnits", ValueType.PRIMITIVE, policy.getNumberOfUnits()));
        entity.addProperty(new Property(null, "PolicyEndDate", ValueType.PRIMITIVE, policy.getPolicyEndDate()));
        entity.addProperty(new Property(null, "PolicyHolderId", ValueType.PRIMITIVE, policy.getPolicyHolderId()));
        entity.addProperty(new Property(null, "PolicyStartDate", ValueType.PRIMITIVE, policy.getPolicyStartDate()));
        if (policy.getTotalCost() != null) {
            entity.addProperty(new Property(null, "TotalCostAmount", ValueType.PRIMITIVE, policy.getTotalCost().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
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
