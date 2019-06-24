package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.entities.Policy;

import java.util.Map;

/**
 * Templated interface subclass for Policy object
 */
public interface PolicyDAO extends BaseDAO<Policy> {

    /**
     * Method returns a map of policies for a given policyHolder
     * @param policyHolderId Policy Holder Id
     * @return Map of Policy objects
     */
    Map<String, Policy> findAllByPolicyHolderId(String policyHolderId);

    /**
     * Method returns a map of policies for a given productId
     * @param productId Product Id
     * @return Map of Policy objects
     */
    Map<String, Policy> findAllByProductId(String productId);
}
