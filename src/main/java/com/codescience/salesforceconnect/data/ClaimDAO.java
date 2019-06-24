package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.entities.Claim;

import java.util.Map;

/**
 * Templated interface subclass for Claim object
 */
public interface ClaimDAO extends BaseDAO<Claim> {

    /**
     * Method returns a map of Claims for a given policy id
     * @param policyId Policy Id
     * @return Map of Claim objects
     */
    Map<String, Claim> findAllByPolicyId(String policyId);

}
