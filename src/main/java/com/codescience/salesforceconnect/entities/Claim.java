package com.codescience.salesforceconnect.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of the BaseEnity for each individual Claim
 */
public class Claim extends BaseEntity {

    static final long serialVersionUID = 1L;

    private Policy policy;

    private Date claimDate;

    private String claimReason;

    private boolean approved;

    private BigDecimal claimAmount;

    private Set<Beneficiary> beneficiaries;

    /**
     * Method returns the Policy for the Claim
     * @return Policy object for the Claim
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * Method sets the Policy for the Claim
     * @param policy Policy object for the claim
     */
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    /**
     * Method gets the Date for the Claim
     * @return Date of the Claim
     */
    public Date getClaimDate() {
        return claimDate;
    }

    /**
     * Method sets the Date of the Claim
     * @param claimDate Date of the Claim
     */
    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    /**
     * Method gets the Reason for the Claim
     * @return Reason for the Claim
     */
    public String getClaimReason() { return claimReason; }

    /**
     * Method sets the Reason for the Claim
     * @param claimReason Reason for the Claim
     */
    public void setClaimReason(String claimReason) { this.claimReason = claimReason; }

    /**
     * Method returns true if the Claim is Approved
     * @return true if the Claim is Approved
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * Method sets whether the Claim is Approved
     * @param approved true if the Claim is Approved
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * Method returns the Claim Amount
     * @return BigDecimal of the Claim Amount
     */
    public BigDecimal getClaimAmount() { return claimAmount; }

    /**
     * Method sets the Claim Amount
     * @param claimAmount BigDecimal represents the Claim Amount
     */
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }

    /**
     * Method gets the Beneficiaries
     * @return Set of Beneficiaries
     */
    public Set<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    /**
     * Method sets the Beneficiaries. It will clear out the claim of any existing beneficiaries
     * and sets the Claim for the new beneficiaries
     * @param beneficiaries Set of Beneficiaries
     */
    public void setBeneficiaries(Set<Beneficiary> beneficiaries) {
        if (beneficiaries == null) {
            return;
        }

        if (this.beneficiaries != null) {
            for (Beneficiary ben : this.beneficiaries) {
                ben.setClaim(null);
            }
        }
        for (Beneficiary ben : beneficiaries) {
            ben.setClaim(this);
        }
        this.beneficiaries = beneficiaries;
    }

    /**
     * Method adds a Beneficiary to the Set of Beneficiaries and it sets the claim to this claim
     * @param beneficiary Beneficiary to add
     */
    public void addBeneficiary(Beneficiary beneficiary) {
        if (beneficiary == null) {
            return;
        }

        this.beneficiaries = this.beneficiaries == null ? new TreeSet<Beneficiary>() : this.beneficiaries;
        beneficiary.setClaim(this);
        this.beneficiaries.add(beneficiary);
    }

    /**
     * Method removes a Beneficiary from the Set of Beneficiaries and it clears the claim
     * @param beneficiary Beneficiary to remove
     */
    public void removeBeneficiary(Beneficiary beneficiary) {
        if (beneficiary == null) {
            return;
        }

        this.beneficiaries = this.beneficiaries == null ? new TreeSet<Beneficiary>() : this.beneficiaries;
        if (this.beneficiaries.remove(beneficiary)) {
            beneficiary.setClaim(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Claim claim = (Claim) o;

        if (isApproved() != claim.isApproved()) return false;
        if (getPolicy() != null ? !getPolicy().equals(claim.getPolicy()) : claim.getPolicy() != null) return false;
        if (getClaimDate() != null ? !getClaimDate().equals(claim.getClaimDate()) : claim.getClaimDate() != null)
            return false;
        if (getClaimReason() != null ? !getClaimReason().equals(claim.getClaimReason()) : claim.getClaimReason() != null)
            return false;
        return getClaimAmount() != null ? getClaimAmount().equals(claim.getClaimAmount()) : claim.getClaimAmount() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getPolicy() != null ? getPolicy().hashCode() : 0);
        result = 31 * result + (getClaimDate() != null ? getClaimDate().hashCode() : 0);
        result = 31 * result + (getClaimReason() != null ? getClaimReason().hashCode() : 0);
        result = 31 * result + (isApproved() ? 1 : 0);
        result = 31 * result + (getClaimAmount() != null ? getClaimAmount().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "policy=" + policy +
                ", claimDate=" + claimDate +
                ", claimReason='" + claimReason + '\'' +
                ", approved=" + approved +
                ", claimAmount=" + claimAmount +
                "} " + super.toString();
    }
}
