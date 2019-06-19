package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.entities.Claim;
import com.codescience.salesforceconnect.entities.Policy;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Subclass of ODataTypeTranslator to handle claims Translation from Pojo Claim object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class ClaimTypeTranslator extends ODataTypeTranslator<Claim> {

    /**
     * Implementation method to translate the BaseEntity implementation (Claim) to an Olingo Entity object
     * @param claim BaseEntity implementation (Claim)
     * @return Olingo entity for the Claim
     */
    public Entity translate(Claim claim) {
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.CLAIM_ID, ValueType.PRIMITIVE, claim.getRecordId()));
        entity.addProperty(new Property(null, Constants.CLAIM_DATE, ValueType.PRIMITIVE, claim.getClaimDate()));
        entity.addProperty(new Property(null, Constants.CLAIM_REASON, ValueType.PRIMITIVE, claim.getClaimReason()));
        entity.addProperty(new Property(null, Constants.CLAIM_APPROVED, ValueType.PRIMITIVE, claim.isApproved()));
        if (claim.getClaimAmount() != null) {
            entity.addProperty(new Property(null, Constants.CLAIM_AMOUNT, ValueType.PRIMITIVE, claim.getClaimAmount().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, Constants.CLAIM_POLICY_ID, ValueType.PRIMITIVE, claim.getPolicy().getRecordId()));
        entity.setType(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, Constants.CLAIM_ID));
        return entity;
    }

    /**
     * Implementation method to translate the Olingo Entity object to the BaseEntity implementation (Claim)
     * @param entity Olingo entity
     * @param storage Storage implementation
     * @param merge boolean if true then nulls won't overwrite non nulls
     * @return BaseEntity implementation (Claim)
     */
    public Claim translate(Entity entity, Storage storage, boolean merge) {
        Claim claim = new Claim();
        Property prop = entity.getProperty(Constants.CLAIM_ID);
        Claim existingClaim = getExistingEntity(storage.getDataAccessObjects().get(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString()),Claim.class, prop);

        claim.setRecordId(existingClaim.getRecordId());

        prop = entity.getProperty(Constants.CLAIM_AMOUNT);
        claim.setClaimAmount((BigDecimal) extractValue(existingClaim.getClaimAmount(), prop, merge));

        prop = entity.getProperty(Constants.CLAIM_APPROVED);
        claim.setApproved((Boolean) extractValue(existingClaim.isApproved(), prop, merge));

        prop = entity.getProperty(Constants.CLAIM_DATE);
        claim.setClaimDate((Date) extractValue(existingClaim.getClaimDate(), prop, merge));

        prop = entity.getProperty(Constants.CLAIM_REASON);
        claim.setClaimReason((String) extractValue(existingClaim.getClaimReason(), prop, merge));

        prop = entity.getProperty(Constants.CLAIM_POLICY_ID);
        String policyId = prop == null ? null : (String) prop.getValue();

        if (policyId != null) {
            claim.setPolicy((Policy) storage.getDataAccessObjects().get(OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString()).findByRecordId(policyId));
        }
        return claim;
    }

    /**
     * Method returns the Claim Set Name
     * @return String with the Claim Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_CLAIMS_NAME;
    }
}
