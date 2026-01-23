package com.codescience.salesforceconnect.service;

/**
 * Constants defined for the application
 * Created by Marc Paris on 2/20/17.
 */
public class Constants {

    public static final String EDM_PROVIDER = "edmProvider";

    public static final String ENTITY_PROCESSOR = "entityProcessor";

    public static final String ENTITY_COLLECTION_PROCESSOR = "entityCollectionProcessor";

    public static final String APPLICATION_CONTEXT_NAME = "org.springframework.web.context.WebApplicationContext.ROOT";

    // Service Namespace
    public static final String NAMESPACE = "OData.InsuranceSystem";

    // EDM Container
    public static final String CONTAINER_NAME = "Container";

    // Entity Types Names
    public static final String ET_PRODUCT_NAME = "Product";
    public static final String ET_POLICY_NAME = "Policy";
    public static final String ET_CLAIM_NAME = "Claim";
    public static final String ET_BENEFICIARY_NAME = "Beneficiary";

    // Entity Set Names
    public static final String ES_PRODUCTS_NAME = "Products";
    public static final String ES_POLICIES_NAME = "Policies";
    public static final String ES_CLAIMS_NAME = "Claims";
    public static final String ES_BENEFICIARIES_NAME = "Beneficiaries";


    public static final String SETTER_PREFIX = "set";

    public static final String ID= "Id";

    // PRODUCT PARAMETER CONSTANTS
    public static final String PRODUCT_NAME = "ProductName";

    public static final String PRODUCT_TYPE = "ProductType";

    public static final String COST_PER_UNIT = "CostPerUnit";

    public static final String PRODUCT_ACTIVE = "Active";

    // POLICY PARAMETER CONSTANTS
    public static final String POLICY_START_DATE = "PolicyStartDate";

    public static final String POLICY_END_DATE = "PolicyEndDate";

    public static final String POLICY_HOLDER_ID = "PolicyHolderId";

    public static final String TOTAL_COST_AMOUNT = "TotalCostAmount";

    public static final String NUMBER_OF_UNITS = "NumberOfUnits";

    public static final String POLICY_ACTIVE = "Active";

    public static final String PRODUCT = "Product";

    public static final String POLICY_PRODUCT_ID = "ProductId";

    // CLAIM PARAMETER CONSTANTS
    public static final String CLAIM_DATE = "ClaimDate";

    public static final String CLAIM_REASON = "ClaimReason";

    public static final String CLAIM_APPROVED = "Approved";

    public static final String CLAIM_AMOUNT = "ClaimAmount";

    public static final String POLICY = "Policy";

    public static final String CLAIMS = "Claims";

    public static final String CLAIM_POLICY_ID = "PolicyId";

    // BENEFICIARY PARAMETER CONSTANTS
    public static final String BENEFICIARY_PERCENT = "BeneficiaryPercent";

    public static final String BENEFICIARY_AMOUNT = "BeneficiaryAmount";

    public static final String CONTACT_IDENTIFIER = "ContactIdentifierId";

    public static final String CLAIM = "Claim";

    public static final String BENEFICIARIES = "Beneficiaries";

    public static final String BENEFICIARY_CLAIM_ID = "ClaimId";

    /**
     * Private Constructor prevents instantiation
     */
    private Constants() {}
}
