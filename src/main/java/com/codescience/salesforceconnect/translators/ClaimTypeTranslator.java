package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Claim;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.BigDecimal;

/**
 * Subclass of ODataTypeTranslator to handle claims Translation from Pojo Claim object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class ClaimTypeTranslator extends ODataTypeTranslator {

    /**
     * Implementation method to translate the BaseEntity inmplementation (Claim) to an Odata Entity object
     * @param object BaseEntity implementation
     * @return Olingo entity for the Claim
     */
    public Entity translate(BaseEntity object) {
        Claim claim = (Claim) object;
        Entity entity = new Entity();

        entity.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, claim.getId()));
        entity.addProperty(new Property(null, "ClaimDate", ValueType.PRIMITIVE, claim.getClaimDate()));
        entity.addProperty(new Property(null, "ClaimReason", ValueType.PRIMITIVE, claim.getClaimReason()));
        entity.addProperty(new Property(null, "Approved", ValueType.PRIMITIVE, claim.isApproved()));
        if (claim.getClaimAmount() != null) {
            entity.addProperty(new Property(null, "ClaimAmount", ValueType.PRIMITIVE, claim.getClaimAmount().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.setType(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, "Id"));
        return entity;
    }

    /**
     * Method returns the Claim Set Name
     * @return String with the Claim Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_CLAIMS_NAME;
    }
}
