package com.codescience.salesforceconnect.entities;

import com.codescience.salesforceconnect.service.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of the BaseEntity for each individual Beneficiary
 */
public class Beneficiary extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(Beneficiary.class);

    private static final long serialVersionUID = 1L;

    private BigDecimal beneficiaryPercent;

    private Claim claim;

    private String contactIdentifierId;

    /**
     * Method returns the BeneficiaryPercent - A value between 0 and 100 or null if not specified
     * @return BigDecimal representing the Beneficiary Percent
     */
    public BigDecimal getBeneficiaryPercent() {
        return beneficiaryPercent;
    }

    /**
     * Method sets the beneficiary percent - A value between 0 and 100 or null if not specified
     * @param beneficiaryPercent BigDecimal representing the Beneficiary Percent
     */
    public void setBeneficiaryPercent(BigDecimal beneficiaryPercent) {
        if (beneficiaryPercent == null) {
            this.beneficiaryPercent = null;
        }
        else if (beneficiaryPercent.compareTo(BigDecimal.ZERO) < 0 || beneficiaryPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            LOG.error(Messages.INVALID_BENEFICIARY_PERCENT);
            throw new IllegalArgumentException(Messages.INVALID_BENEFICIARY_PERCENT);
        } else {
            this.beneficiaryPercent = beneficiaryPercent;
        }
    }

    /**
     * Method returns the Beneficiary Amount by multiplying the percentage by the claim amount
     * @return Beneficiary amount or null if either the claim, the claim amount or the beneficiary percent are null
     */
    public BigDecimal getBeneficiaryAmount() {
        if ((claim != null) && (getBeneficiaryPercent() != null) && claim.getClaimAmount() != null) {
            return claim.getClaimAmount().multiply(getBeneficiaryPercent().divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN));
        }
        return null;
    }

    /**
     * Method returns the Claim for the Beneficiary
     * @return Claim for the Beneficiary
     */
    public Claim getClaim() {
        return claim;
    }

    /**
     * Method sets the Claim for the Beneficiary
     * @param claim Claim for the Beneficiary
     */
    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    /**
     * Method returns the Contact Identifier. This is a unique identifier that identifies an individual
     * @return Contact Identifier for the Beneficiary
     */
    public String getContactIdentifierId() {
        return contactIdentifierId;
    }

    /**
     * Method sets the Contact Identifier. This is a unique identifier that identifies an individual
     * @param contactIdentifierId Contact Identifier for the Beneficiary
     */
    public void setContactIdentifierId(String contactIdentifierId) {
        this.contactIdentifierId = contactIdentifierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Beneficiary that = (Beneficiary) o;

        if (getBeneficiaryPercent() != null ? !getBeneficiaryPercent().equals(that.getBeneficiaryPercent()) : that.getBeneficiaryPercent() != null)
            return false;
        if (getClaim() != null ? !getClaim().equals(that.getClaim()) : that.getClaim() != null) return false;
        return getContactIdentifierId() != null ? getContactIdentifierId().equals(that.getContactIdentifierId()) : that.getContactIdentifierId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getBeneficiaryPercent() != null ? getBeneficiaryPercent().hashCode() : 0);
        result = 31 * result + (getClaim() != null ? getClaim().hashCode() : 0);
        result = 31 * result + (getContactIdentifierId() != null ? getContactIdentifierId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Beneficiary{" +
                "beneficiaryPercent=" + beneficiaryPercent +
                ", claim=" + claim +
                ", contactIdentifierId=" + contactIdentifierId +
                "} " + super.toString();
    }
}
