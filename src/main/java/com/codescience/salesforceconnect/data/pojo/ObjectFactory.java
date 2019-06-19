package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.entities.*;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Factory object used by the PojoDAOs
 */
public class ObjectFactory {

    private static Map<String, Product> products;
    private static Map<String, Policy> policies;
    private static Map<String, Claim> claims;
    private static Map<String, Beneficiary> beneficiaries;

    /**
     * Default constructor will initialize the data
     */
    public ObjectFactory() {
        if (products == null) {
            initializeData();
        }
    }

    /**
     * Method used to initialize default pojos as the entity model
     */
    private static void initializeData() {
        products = new TreeMap<String, Product>();
        policies = new TreeMap<String, Policy>();
        claims = new TreeMap<String, Claim>();
        beneficiaries = new TreeMap<String, Beneficiary>();

        Product prod = new Product();
        prod.setActive(true);
        prod.setCostPerUnit(new BigDecimal(100));
        prod.setRecordId("1000");
        prod.setProductName("Long Term Disability");
        prod.setProductType("Disability");
        products.put(prod.getRecordId(), prod);

        Product prod2 = new Product();
        prod2.setActive(true);
        prod2.setCostPerUnit(new BigDecimal(150));
        prod2.setRecordId("1001");
        prod2.setProductName("Short Term Disability");
        prod2.setProductType("Disability");
        products.put(prod2.getRecordId(), prod2);

        Product prod3 = new Product();
        prod3.setActive(false);
        prod3.setCostPerUnit(new BigDecimal(200));
        prod3.setRecordId("1002");
        prod3.setProductName("Umbrella Policy");
        prod3.setProductType("Liability");
        products.put(prod3.getRecordId(), prod3);

        Product prod4 = new Product();
        prod4.setActive(true);
        prod4.setCostPerUnit(new BigDecimal(250));
        prod4.setRecordId("1003");
        prod4.setProductName("Umbrella Policy");
        prod4.setProductType("Liability");
        products.put(prod4.getRecordId(), prod4);

        Product prod5 = new Product();
        prod5.setActive(true);
        prod5.setCostPerUnit(new BigDecimal(300));
        prod5.setRecordId("1004");
        prod5.setProductName("Life");
        prod5.setProductType("Life");
        products.put(prod5.getRecordId(), prod5);

        Product prod6 = new Product();
        prod6.setActive(true);
        prod6.setCostPerUnit(new BigDecimal(350));
        prod6.setRecordId("1005");
        prod6.setProductName("Accidental Death");
        prod6.setProductType("Life");
        products.put(prod6.getRecordId(), prod6);

        Calendar cal = Calendar.getInstance();
        cal.set(2010,Calendar.APRIL, 10,0,0,0);

        Policy policy = new Policy();
        policy.setRecordId("2000");
        policy.setNumberOfUnits(100);
        policy.setPolicyHolderId("10000");
        policy.setProduct(prod);
        policy.setPolicyStartDate(cal.getTime());
        policies.put(policy.getRecordId(), policy);

        Policy policy2 = new Policy();
        policy2.setRecordId("2001");
        policy2.setNumberOfUnits(200);
        policy2.setPolicyHolderId("10000");
        policy2.setProduct(prod3);
        cal.set(2011, Calendar.JUNE, 20,0,0,0);
        policy2.setPolicyStartDate(cal.getTime());
        policies.put(policy2.getRecordId(), policy2);

        cal.set(2014,Calendar.DECEMBER, 5,0,0,0);
        policy2.setPolicyEndDate(cal.getTime());

        Policy policy3 = new Policy();
        policy3.setRecordId("2002");
        policy3.setNumberOfUnits(300);
        policy3.setPolicyHolderId("10001");
        policy3.setProduct(prod4);
        cal.set(2011, Calendar.JUNE, 20,0,0,0);
        policy3.setPolicyStartDate(cal.getTime());
        policies.put(policy3.getRecordId(), policy3);

        cal.set( 2016, Calendar.JULY, 28,0,0,0);
        Claim claim = new Claim();
        claim.setRecordId("3000");
        claim.setApproved(true);
        claim.setClaimAmount(new BigDecimal(10000));
        claim.setClaimDate(cal.getTime());
        claim.setClaimReason("Injury");
        policy.addClaim(claim);
        claims.put(claim.getRecordId(), claim);

        cal.set( 2009, Calendar.MARCH, 4,0,0,0);
        Claim claim2 = new Claim();
        claim2.setRecordId("3001");
        claim2.setApproved(false);
        claim2.setClaimAmount(new BigDecimal(25000));
        claim2.setClaimDate(cal.getTime());
        claim2.setClaimReason("Accident");
        policy.addClaim(claim2);
        claims.put(claim2.getRecordId(), claim2);

        cal.set( 2012, Calendar.AUGUST, 12,0,0,0);
        Claim claim3 = new Claim();
        claim3.setRecordId("3002");
        claim3.setApproved(false);
        claim3.setClaimAmount(new BigDecimal(25000));
        claim3.setClaimDate(cal.getTime());
        claim3.setClaimReason("Lawsuit");
        policy2.addClaim(claim3);
        claims.put(claim3.getRecordId(), claim3);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setRecordId("4000");
        beneficiary.setBeneficiaryPercent(new BigDecimal(100));
        beneficiary.setContactIdentifierId("20000");
        claim.addBeneficiary(beneficiary);
        beneficiaries.put(beneficiary.getRecordId(), beneficiary);

        Beneficiary beneficiary2 = new Beneficiary();
        beneficiary2.setRecordId("4001");
        beneficiary2.setBeneficiaryPercent(new BigDecimal(75));
        beneficiary2.setContactIdentifierId("20001");
        claim2.addBeneficiary(beneficiary2);
        beneficiaries.put(beneficiary2.getRecordId(), beneficiary2);

        Beneficiary beneficiary3 = new Beneficiary();
        beneficiary3.setRecordId("4002");
        beneficiary3.setBeneficiaryPercent(new BigDecimal(25));
        beneficiary3.setContactIdentifierId("20002");
        claim2.addBeneficiary(beneficiary3);
        beneficiaries.put(beneficiary3.getRecordId(), beneficiary3);
    }

    /**
     * Method returns the map of Products by Key
     * @return Map of Products keyed by record Id
     */
    public Map<String, Product> getProducts() {
        return products;
    }

    /**
     * Method returns the map of Policies by Key
     * @return Map of Policies keyed by record Id
     */
    public Map<String, Policy> getPolicies() {
        return policies;
    }

    /**
     * Method returns the map of Claims by Key
     * @return Map of Claims keyed by record Id
     */
    public Map<String, Claim> getClaims() {
        return claims;
    }

    /**
     * Method returns the map of Beneficiaries by Key
     * @return Map of Beneficiaries keyed by record Id
     */
    public Map<String, Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    /**
     * Method returns a map of entities for the type passed in
     * @param type Type of entity
     * @return Map of BaseEntity implementations
     */
    Map<String, BaseEntity> getEntities(String type) {
        Map<String, BaseEntity> map = new HashMap<String, BaseEntity>();
        if (OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString().equalsIgnoreCase(type)) {
            for (Product prod : products.values()) {
                map.put(prod.getRecordId(), prod);
            }
        }
        if (OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString().equalsIgnoreCase(type)) {
            for (Policy policy : policies.values()) {
                map.put(policy.getRecordId(), policy);
            }
        }
        if (OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString().equalsIgnoreCase(type)) {
            for (Claim claim : claims.values()) {
                map.put(claim.getRecordId(), claim);
            }
        }
        if (OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString().equalsIgnoreCase(type)) {
            for (Beneficiary beneficiary : beneficiaries.values()) {
                map.put(beneficiary.getRecordId(), beneficiary);
            }
        }
        return map;
    }

    /**
     * Method returns a new Id for the FullQualifiedName passed in. It will take the largest value and increment 1
     * @param fqn FullQualifiedName (Object type)
     * @return String that is the Id
     */
    public String getNewId(FullQualifiedName fqn) {
        Map<String, BaseEntity> mapOfEntities = getEntities(fqn.getFullQualifiedNameAsString());
        int maxValue = 0;
        for (BaseEntity entity : mapOfEntities.values()) {
            int id = Integer.parseInt(entity.getRecordId());
            if (id > maxValue) {
                maxValue = id;
            }
        }

        return Integer.toString(++maxValue);
    }
}
