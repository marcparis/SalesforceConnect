package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.entities.Beneficiary;
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
public class BeneficiaryTypeTranslator extends ODataTypeTranslator {

    /**
     * Implementation method to translate the BaseEntity inmplementation (Beneficiary) to an Odata Entity object
     * @param object BaseEntity implementation (Beneficiary)
     * @return Olingo entity for the Beneficiary
     */
    public Entity translate(BaseEntity object) {
        Beneficiary beneficiary = (Beneficiary) object;
        Entity entity = new Entity();

        entity.addProperty(new Property(null, Constants.BENEFICIARY_ID, ValueType.PRIMITIVE, beneficiary.getId()));
        entity.addProperty(new Property(null, Constants.BENEFICIARY_PERCENT, ValueType.PRIMITIVE, beneficiary.getBeneficiaryPercent()));
        if (beneficiary.getBeneficiaryAmount() != null) {
            entity.addProperty(new Property(null, Constants.BENEFICIARY_AMOUNT, ValueType.PRIMITIVE, beneficiary.getBeneficiaryAmount().setScale(0, BigDecimal.ROUND_HALF_EVEN)));
        }
        entity.addProperty(new Property(null, Constants.CONTACT_IDENTIFIER, ValueType.PRIMITIVE, beneficiary.getContactIdentifierId()));
        entity.addProperty(new Property(null, Constants.BENEFICIARY_CLAIM_ID, ValueType.PRIMITIVE, beneficiary.getClaim().getId()));
        entity.setType(OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString());
        entity.setId(createId(entity, Constants.BENEFICIARY_ID));
        return entity;
    }

    /**
     * Method returns the Beneficiary Set Name
     * @return String with the Beneficiary Set Name
     */
    public String getEntitySetName() {
        return OdataEdmProvider.ES_BENEFICIARIES_NAME;
    }
}
