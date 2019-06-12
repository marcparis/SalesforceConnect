package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Beneficiary;
import com.codescience.salesforceconnect.entities.Claim;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import java.math.BigDecimal;

/**
 * Subclass of ODataTypeTranslator to handle Beneficiary Translation from Pojo Beneficiary object to Olingo Entity Object.
 * This does not do a &quot;Deep Clone&quot;
 */
public class BeneficiaryTypeTranslator extends ODataTypeTranslator<Beneficiary> {

    /**
     * Implementation method to translate the BaseEntity implementation (Beneficiary) to an Olingo Entity object
     * @param beneficiary BaseEntity implementation (Beneficiary)
     * @return Olingo entity for the Beneficiary
     */
    public Entity translate(Beneficiary beneficiary) {
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.BENEFICIARY_ID, ValueType.PRIMITIVE, beneficiary.getRecordId()));
        entity.addProperty(new Property(null, Constants.BENEFICIARY_PERCENT, ValueType.PRIMITIVE, beneficiary.getBeneficiaryPercent()));
        if (beneficiary.getBeneficiaryAmount() != null) {
            entity.addProperty(new Property(null, Constants.BENEFICIARY_AMOUNT, ValueType.PRIMITIVE, beneficiary.getBeneficiaryAmount().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, Constants.CONTACT_IDENTIFIER, ValueType.PRIMITIVE, beneficiary.getContactIdentifierId()));
        entity.addProperty(new Property(null, Constants.BENEFICIARY_CLAIM_ID, ValueType.PRIMITIVE, beneficiary.getClaim().getRecordId()));
        entity.setType(OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, Constants.BENEFICIARY_ID));
        return entity;
    }

    /**
     * Implementation method to translate the Olingo Entity object to the BaseEntity implementation (Beneficiary)
     * @param entity Olingo entity
     * @param storage Storage implementation
     * @param merge boolean if true then nulls won't overwrite non nulls
     * @return BaseEntity implementation (Beneficiary)
     */
    public Beneficiary translate(Entity entity, Storage storage, boolean merge) {
        Beneficiary beneficiary = new Beneficiary();
        Property prop = entity.getProperty(Constants.BENEFICIARY_ID);
        beneficiary.setRecordId((String) prop.getValue());
        prop = entity.getProperty(Constants.BENEFICIARY_PERCENT);
        beneficiary.setBeneficiaryPercent((BigDecimal) prop.getValue());
        prop = entity.getProperty(Constants.CONTACT_IDENTIFIER);
        beneficiary.setContactIdentifierId((String) prop.getValue());
        prop = entity.getProperty(Constants.BENEFICIARY_CLAIM_ID);

        String benId = prop == null ? null : (String) prop.getValue();
        if (benId != null) {
            beneficiary.setClaim((Claim) storage.getDataAccessObjects().get(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString()).findByRecordId(benId));
        }
        return beneficiary;
    }

    /**
     * Method returns the Beneficiary Set Name
     * @return String with the Beneficiary Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_BENEFICIARIES_NAME;
    }
}
