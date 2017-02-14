package com.codescience.salesforceconnect.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of the BaseEnity for each individual Insurance Policy
 */
public class Policy extends BaseEntity {

    static final long serialVersionUID = 1L;

    private Product product;

    private Date policyStartDate;

    private Date policyEndDate;

    private Long policyHolderId;

    private int numberOfUnits;

    private Set<Claim> claims;

    /**
     * Method returns the Insurance product related to the Insurance policy
     * @return Product entity implementation
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Method sets the Insurance product related to the Insurance policy
     * @param product Product entity implementation
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Method returns the Start date of the policy
     * @return Start Date of the Policy
     */
    public Date getPolicyStartDate() {
        return policyStartDate;
    }

    /**
     * Method sets the Start date of the policy
     * @param policyStartDate start Date of the policy
     */
    public void setPolicyStartDate(Date policyStartDate) {
        this.policyStartDate = policyStartDate;
    }

    /**
     * Method returns the End date of the policy. Null for policies that are active (i.e don't have an end date yet)
     * @return End Date of the Policy. Can be null
     */
    public Date getPolicyEndDate() {
        return policyEndDate;
    }

    /**
     * Method sets the End date of the policy. Null for policies that are active (i.e don't have an enddate determined yet)
     * @param policyEndDate End Date of the Policy. Can be null
     */
    public void setPolicyEndDate(Date policyEndDate) {
        this.policyEndDate = policyEndDate;
    }

    /**
     * Method contains a unique id to identify the Policy Holder
     * @return Unique identifer to represent the Policy Holder
     */
    public Long getPolicyHolderId() {
        return policyHolderId;
    }

    /**
     * Method sets the Policy Holder Id. A unique id to identify the Policy Holder
     * @param policyHolderId Unique Identifier to represent the Policy holder
     */
    public void setPolicyHolderId(Long policyHolderId) {
        this.policyHolderId = policyHolderId;
    }

    /**
     * Method returns the number of units. The Insurance policy is meansure in units with each unit corresponding to a block of coverage
     * @return Number of Units
     */
    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    /**
     * Method sets the number of units. The Insurance policy is measured in units with each unit corresponding to a block of coverage
     * @param numberOfUnits Number of Units
     */
    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    /**
     * Method returns true if the Policy is Active. If the start date is not specified, it is not active
     * If the start date is specified but is after the current date, it is not active. If the end date
     * is specified but is before the current date the policy is not active. Otherwise it is active
     * @return true if the Policy is Active
     */
    public boolean isActive() {
        Date today = new Date();

        if ((getPolicyStartDate() == null) || (getPolicyStartDate().after(today))) {
            return false;
        }

        if ((getPolicyEndDate() != null) && (getPolicyEndDate().before(today))) {
            return false;
        }

        return true;
    }

    /**
     * Method returns the total Cost of the Policy. It multiplies the CostPerUnit on the product with the number of Units.
     * It will return null if any value is unspecified.
     * @return Total Cost for the policy
     */
    public BigDecimal getTotalCost() {
        if ((product == null) || (product.getCostPerUnit() == null) || (getNumberOfUnits() == null)) {
            return null;
        }
        return new BigDecimal(product.getCostPerUnit().doubleValue() * getNumberOfUnits());
    }

    /**
     * Method returns the Claims for the Policy. May be null if no claims available
     * @return Set of Claims for the Policy
     */
    public Set<Claim> getClaims() {
        return claims;
    }

    /**
     * Method sets the Claims for the Policy. May be null if no claims set. It will clear out the policy
     * for any existing claims and will set the Policy for any new claims passed in.
     * @param claims Set of claims for the Policy
     */
    public void setClaims(Set<Claim> claims) {
        if (claims == null) {
            return;
        }

        if (this.claims != null) {
            for (Claim claim : this.claims) {
                claim.setPolicy(null);
            }
        }

        for (Claim claim : claims) {
            claim.setPolicy(this);
        }
        this.claims = claims;
    }

    /**
     * Method adds a claim to collection of claims on the policy. It will set the policy on the claim as well
     * @param claim Claim instancce to add
     */
    public void addClaim(Claim claim) {
        if (claim == null) {
            return;
        }

        this.claims = this.claims == null ? new TreeSet<Claim>() : this.claims;
        claim.setPolicy(this);
        this.claims.add(claim);
    }

    /**
     * Method removes a Claim from the collection of Claims for the policy. It also removes the
     * reference to this policy on the claim
     * @param claim Claim object implementation
     */
    public void removeClaim(Claim claim) {
        if (claim == null) {
            return;
        }

        this.claims = this.claims == null ? new TreeSet<Claim>() : this.claims;
        if (this.claims.remove(claim)) {
            claim.setPolicy(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Policy policy = (Policy) o;

        if (getNumberOfUnits() != null ? !getNumberOfUnits().equals(policy.getNumberOfUnits()) : policy.getNumberOfUnits() != null)
            return false;
        if (getProduct() != null ? !getProduct().equals(policy.getProduct()) : policy.getProduct() != null)
            return false;
        if (getPolicyStartDate() != null ? !getPolicyStartDate().equals(policy.getPolicyStartDate()) : policy.getPolicyStartDate() != null)
            return false;
        if (getPolicyEndDate() != null ? !getPolicyEndDate().equals(policy.getPolicyEndDate()) : policy.getPolicyEndDate() != null)
            return false;
        return getPolicyHolderId() != null ? getPolicyHolderId().equals(policy.getPolicyHolderId()) : policy.getPolicyHolderId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getProduct() != null ? getProduct().hashCode() : 0);
        result = 31 * result + (getPolicyStartDate() != null ? getPolicyStartDate().hashCode() : 0);
        result = 31 * result + (getPolicyEndDate() != null ? getPolicyEndDate().hashCode() : 0);
        result = 31 * result + (getPolicyHolderId() != null ? getPolicyHolderId().hashCode() : 0);
        result = 31 * result + getNumberOfUnits();
        return result;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "product=" + product +
                ", policyStartDate=" + policyStartDate +
                ", policyEndDate=" + policyEndDate +
                ", policyHolderId=" + policyHolderId +
                ", numberOfUnits=" + numberOfUnits +
                "} " + super.toString();
    }
}
