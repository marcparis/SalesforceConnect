package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.entities.Beneficiary;

import java.util.Map;

/**
 * Templated interface subclass for Beneficiary object
 */
public interface BeneficiaryDAO extends BaseDAO<Beneficiary> {

    /**
     * Method returns a map of beneficiaries for a given contactIdentifier
     * @param contactIdentifier Contact Identifier
     * @return Map of Beneficiary objects
     */
    Map<String, Beneficiary> findAllByContactIdentifier(String contactIdentifier);

    /**
     * Method returns a map of beneficiaries for a given claimId
     * @param claimId Claim Id
     * @return Map of Beneficiary objects
     */
    Map<String, Beneficiary> findAllByClaimId(String claimId);
}
