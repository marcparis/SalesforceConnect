package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Claim;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.RoundingMode;

/**
 * Subclass of ODataTypeTranslator to handle claims Translation from Pojo Claim object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class ClaimTypeTranslator extends ODataTypeTranslator {

    /**
     * Implementation method to translate the BaseEntity implementation (Claim) to an Odata Entity object
     * @param object BaseEntity implementation
     * @return Olingo entity for the Claim
     */
    public Entity translate(BaseEntity object) {
        Claim claim = (Claim) object;
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.ID, ValueType.PRIMITIVE, claim.getId()));
        entity.addProperty(new Property(null, Constants.CLAIM_DATE, ValueType.PRIMITIVE, claim.getClaimDate()));
        entity.addProperty(new Property(null, Constants.CLAIM_REASON, ValueType.PRIMITIVE, claim.getClaimReason()));
        entity.addProperty(new Property(null, Constants.CLAIM_APPROVED, ValueType.PRIMITIVE, claim.isApproved()));
        if (claim.getClaimAmount() != null) {
            entity.addProperty(new Property(null, Constants.CLAIM_AMOUNT, ValueType.PRIMITIVE, claim.getClaimAmount().setScale(0, RoundingMode.HALF_EVEN)));
        }
        entity.addProperty(new Property(null, Constants.CLAIM_POLICY_ID, ValueType.PRIMITIVE, claim.getPolicy().getId()));
        entity.setType(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, Constants.ID));
        return entity;
    }

    /**
     * Method returns the Claim Set Name
     * @return String with the Claim Set Name
     */
    public String getEntitySetName() {
        return Constants.ES_CLAIMS_NAME;
    }

    /**
     * Each translator will return the appropriate class name of the BaseEntity it represents
     * @return Name of the Claim Class
     */
    @Override
    public String getClassName() {
        return Claim.class.getName();
    }
}
