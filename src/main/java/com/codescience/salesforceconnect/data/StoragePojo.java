package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.entities.*;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import com.codescience.salesforceconnect.translators.ODataTypeTranslator;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the Storage interface that stores Simple Java objects in a Map
 */
public class StoragePojo implements Storage {
    private Map<String, ODataTypeTranslator> typeTranslators = new HashMap<String, ODataTypeTranslator>();
    private Map<String, Map<String, BaseEntity>> objects = new HashMap <String, Map<String, BaseEntity>> ();

    public StoragePojo() {
        initializeData();
    }

    /**
     * Method used to initialize default pojos as the entity model
     */
    private void initializeData() {
        Map <String, BaseEntity> mapOfProducts = new TreeMap<String, BaseEntity>();
        objects.put(OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString(), mapOfProducts);

        Product prod = new Product();
        prod.setActive(true);
        prod.setCostPerUnit(new BigDecimal(100));
        prod.setId("1000");
        prod.setProductName("Long Term Disability");
        prod.setProductType("Disability");
        mapOfProducts.put(prod.getId(), prod);

        Product prod2 = new Product();
        prod2.setActive(true);
        prod2.setCostPerUnit(new BigDecimal(150));
        prod2.setId("1001");
        prod2.setProductName("Short Term Disability");
        prod2.setProductType("Disability");
        mapOfProducts.put(prod2.getId(), prod2);

        Product prod3 = new Product();
        prod3.setActive(false);
        prod3.setCostPerUnit(new BigDecimal(200));
        prod3.setId("1002");
        prod3.setProductName("Umbrella Policy");
        prod3.setProductType("Liability");
        mapOfProducts.put(prod3.getId(), prod3);

        Product prod4 = new Product();
        prod4.setActive(true);
        prod4.setCostPerUnit(new BigDecimal(250));
        prod4.setId("1003");
        prod4.setProductName("Umbrella Policy");
        prod4.setProductType("Liability");
        mapOfProducts.put(prod4.getId(), prod4);

        Product prod5 = new Product();
        prod5.setActive(true);
        prod5.setCostPerUnit(new BigDecimal(300));
        prod5.setId("1004");
        prod5.setProductName("Life");
        prod5.setProductType("Life");
        mapOfProducts.put(prod5.getId(), prod5);

        Product prod6 = new Product();
        prod6.setActive(true);
        prod6.setCostPerUnit(new BigDecimal(350));
        prod6.setId("1005");
        prod6.setProductName("Accidental Death");
        prod6.setProductType("Life");
        mapOfProducts.put(prod6.getId(), prod6);

        Map <String, BaseEntity> mapOfPolicies = new TreeMap<String, BaseEntity>();
        objects.put(OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString(), mapOfPolicies);


        Calendar cal = Calendar.getInstance();
        cal.set(2010,Calendar.APRIL, 10);

        Policy policy = new Policy();
        policy.setId("2000");
        policy.setNumberOfUnits(100);
        policy.setPolicyHolderId("10000");
        policy.setProduct(prod);
        policy.setPolicyStartDate(cal.getTime());
        mapOfPolicies.put(policy.getId(), policy);

        Policy policy2 = new Policy();
        policy2.setId("2001");
        policy2.setNumberOfUnits(200);
        policy2.setPolicyHolderId("10000");
        policy2.setProduct(prod3);
        cal.set(2011, Calendar.JUNE, 20);
        policy2.setPolicyStartDate(cal.getTime());
        mapOfPolicies.put(policy2.getId(), policy2);

        cal.set(2014,Calendar.DECEMBER, 5);
        policy2.setPolicyEndDate(cal.getTime());

        Policy policy3 = new Policy();
        policy3.setId("2002");
        policy3.setNumberOfUnits(300);
        policy3.setPolicyHolderId("10001");
        policy3.setProduct(prod4);
        cal.set(2011, Calendar.JUNE, 20);
        policy3.setPolicyStartDate(cal.getTime());
        mapOfPolicies.put(policy3.getId(), policy3);

        Map <String, BaseEntity> mapOfClaims = new TreeMap<String, BaseEntity>();
        objects.put(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString(), mapOfClaims);

        cal.set( 2016, Calendar.JULY, 28);
        Claim claim = new Claim();
        claim.setId("3000");
        claim.setApproved(true);
        claim.setClaimAmount(new BigDecimal(10000));
        claim.setClaimDate(cal.getTime());
        claim.setClaimReason("Injury");
        policy.addClaim(claim);
        mapOfClaims.put(claim.getId(), claim);

        cal.set( 2009, Calendar.MARCH, 4);
        Claim claim2 = new Claim();
        claim2.setId("3001");
        claim2.setApproved(false);
        claim2.setClaimAmount(new BigDecimal(25000));
        claim2.setClaimDate(cal.getTime());
        claim2.setClaimReason("Accident");
        policy.addClaim(claim2);
        mapOfClaims.put(claim2.getId(), claim2);

        cal.set( 2012, Calendar.AUGUST, 12);
        Claim claim3 = new Claim();
        claim3.setId("3002");
        claim3.setApproved(false);
        claim3.setClaimAmount(new BigDecimal(25000));
        claim3.setClaimDate(cal.getTime());
        claim3.setClaimReason("Lawsuit");
        policy2.addClaim(claim3);
        mapOfClaims.put(claim3.getId(), claim3);

        Map <String, BaseEntity> mapOfBeneficiaries = new TreeMap<String, BaseEntity>();
        objects.put(OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString(), mapOfBeneficiaries);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setId("4000");
        beneficiary.setBeneficiaryPercent(new BigDecimal(100));
        beneficiary.setContactIdentifierId("20000");
        claim.addBeneficiary(beneficiary);
        mapOfBeneficiaries.put(beneficiary.getId(), beneficiary);

        Beneficiary beneficiary2 = new Beneficiary();
        beneficiary2.setId("4001");
        beneficiary2.setBeneficiaryPercent(new BigDecimal(75));
        beneficiary2.setContactIdentifierId("20001");
        claim2.addBeneficiary(beneficiary2);
        mapOfBeneficiaries.put(beneficiary2.getId(), beneficiary2);

        Beneficiary beneficiary3 = new Beneficiary();
        beneficiary3.setId("4002");
        beneficiary3.setBeneficiaryPercent(new BigDecimal(25));
        beneficiary3.setContactIdentifierId("20002");
        claim2.addBeneficiary(beneficiary3);
        mapOfBeneficiaries.put(beneficiary3.getId(), beneficiary3);
    }

    /**
     * Method reads the collection of objects based on the type passed in
     * @param edmEntitySet EntitySet (collection) type to be read
     * @return EntityCollection containing Entities read from Pojo storage
     */
    public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, UriInfo uriInfo) {
        EntityCollection entitySet = new EntityCollection();

        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        String objectType = edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString();
        ODataTypeTranslator odtt = getTypeTranslators().get(objectType);

        FilterOption filterOption = uriInfo.getFilterOption();

        // TODO Enhance using more general expressions and visitors (future session)
        // TODO Currently the code can only handle id eq value querries on primary keys.
        // TODO Visitors provide a much more extensive and capable process and will be added next session
        String id = null;
        boolean primaryKey = false;
        boolean claimId = false;
        boolean policyId = false;
        boolean productId = false;

        if (filterOption != null) {
            String value = filterOption.getText().toLowerCase();
            if (value.indexOf("claimid eq") > -1) {
                claimId = true;
            }
            else if (value.indexOf("policyid eq") > -1) {
                policyId = true;

            }
            else if (value.indexOf("productid eq") > -1) {
                productId = true;
            }
            else if (value.indexOf("id eq") > -1) {
                primaryKey = true;
            }
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(value);
            while (m.find()) {
                id = m.group();
            }
        }
        // Find the object with the key
        for (BaseEntity baseEntity : objects.get(objectType).values()) {
            if (id == null) {
                entitySet.getEntities().add(odtt.translate(baseEntity));
            }
            else if (primaryKey && baseEntity.getId().equalsIgnoreCase(id)) {
                entitySet.getEntities().add(odtt.translate(baseEntity));
            }
            else if (claimId) {
                Beneficiary ben = (Beneficiary) baseEntity;
                if (ben.getClaim().getId().equalsIgnoreCase(id)) {
                    entitySet.getEntities().add(odtt.translate(baseEntity));
                }
            }
            else if (policyId) {
                Claim claim = (Claim) baseEntity;
                if (claim.getPolicy().getId().equalsIgnoreCase(id)) {
                    entitySet.getEntities().add(odtt.translate(baseEntity));
                }
            }
            else if (productId) {
                Policy pol = (Policy) baseEntity;
                if (pol.getProduct().getId().equalsIgnoreCase(id)) {
                    entitySet.getEntities().add(odtt.translate(baseEntity));
                }
            }
        }

        return entitySet;
    }

    /**
     * Method reads an individual object from the Pojo Storage.
     * @param edmEntitySet Type of object to read
     * @param keyParams  Identity keys to read from the data storage
     * @return Entity object containing data or null if nothing found
     */
    public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) {
        Entity entity = null;

        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        FullQualifiedName fqn = edmEntityType.getFullQualifiedName();
        String objectType = fqn.getFullQualifiedNameAsString();
        String keyName = edmEntityType.getKeyPredicateNames().get(0);
        String keyValue = getPrimaryKeyFromParam(keyName, keyParams);

        // No primary key sent nothing to return
        if (keyValue == null) {
            return null;
        }

        ODataTypeTranslator odtt = getTypeTranslators().get(objectType);

        // Find the object with the key
        return odtt.translate(objects.get(objectType).get(keyValue));
    }

    /**
     * Method returns the TypeTranslators Map.
     * @return TypeTranslator map that contains type translators for each object
     */
    public Map<String, ODataTypeTranslator> getTypeTranslators() {
        return typeTranslators;
    }

    /**
     * Method sets the TypeTranslators Map
     * @param typeTranslators TypeTranslator Map that contains type translators for each object
     */
    public void setTypeTranslators(Map<String, ODataTypeTranslator> typeTranslators) {
        this.typeTranslators = typeTranslators;
    }

    /**
     * Method takes a source entity object and will return the related collection of targetentity type objects specified
     * @param sourceEntity Source entity that is related to the returned target entity collection
     * @param targetEntityType Target entity type that should be returned
     * @return EntityCollection populated with 0 or more TargetEntityType objects
     */
    public EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) {
        Map<String, BaseEntity> entities = objects.get(sourceEntity.getType());
        BaseEntity ent = entities.get(parseId(sourceEntity.getId()));
        ODataTypeTranslator odtt = getTypeTranslators().get(targetEntityType.getFullQualifiedName().getFullQualifiedNameAsString());
        EntityCollection ec = new EntityCollection();

        // For brevity and clarity purposes the code will check the types and load the relationship data from the underlying objects. Normally implement in a more generic fashion by autowiring or configuring
        // metadata lookups between the Odata entity model and the underlying persistance model

        // If this object is a policy, and the target is a claim return the underlying claims from the policy object and translate
        if ((targetEntityType.getFullQualifiedName().equals(OdataEdmProvider.ET_CLAIM_FQN)) && (sourceEntity.getType().equals(OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString()))) {
            Policy pol = (Policy) ent;
            if (pol.getClaims() != null) {
                for (Claim c : pol.getClaims()) {
                    ec.getEntities().add(odtt.translate(c));
                }
            }
        }

        // If this object is a claim, and the target is a beneficiary return the underlying beneficiary from the claim object and translate
        if ((targetEntityType.getFullQualifiedName().equals(OdataEdmProvider.ET_BENEFICIARY_FQN)) && (sourceEntity.getType().equals(OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString()))) {
            Claim claim = (Claim) ent;
            if (claim.getBeneficiaries() != null) {
                for (Beneficiary b : claim.getBeneficiaries()) {
                    ec.getEntities().add(odtt.translate(b));
                }
            }
        }

        return ec;
    }

    /**
     * Method returns the entity that is related to the source entity. The returned entity is of targetEntity type
     * @param sourceEntity Source entity that contains a reference to the target
     * @param targetEntityType type of target object that will be returned
     * @param keyPredicates  Filter parameters that can be sent it
     * @return Entity of target type that matches the entity.
     */
    public Entity getRelatedEntity(Entity sourceEntity, EdmEntityType targetEntityType, List<UriParameter> keyPredicates) {
        Map<String, BaseEntity> entities = objects.get(sourceEntity.getType());
        BaseEntity ent = entities.get(parseId(sourceEntity.getId()));
        ODataTypeTranslator odtt = getTypeTranslators().get(targetEntityType.getFullQualifiedName().getFullQualifiedNameAsString());

        Entity returnEntity = null;
        String targetTypeName = targetEntityType.getFullQualifiedName().getFullQualifiedNameAsString();
        String sourceTypeName = sourceEntity.getType();

        // For brevity and clarity purposes the code will check the types and load the relationship data from the underlying objects. Normally implement in a more generic fashion by autowiring or configuring
        // metadata lookups between the Odata entity model and the underlying persistance model

        // If the object is a claim object look up either the related policy, or the related beneficiary (selected among the list)
        if (OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString().equals(sourceTypeName)) {
            Claim claim = (Claim) ent;
            if (OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                returnEntity = odtt.translate(claim.getPolicy());
            }
            else if (OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                String targetEntityId = getPrimaryKeyFromParam("Id", keyPredicates);

                for (Beneficiary b : claim.getBeneficiaries()) {
                    if ((targetEntityId == null) || (targetEntityId.equals(b.getId()))) {
                        returnEntity = odtt.translate(b);
                        break;
                    }
                }
            }
        }

        // If the object is a policy object look up either the related product, or the related claim (selected among the list)
        else if (OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString().equals(sourceTypeName)) {
            Policy pol = (Policy) ent;
            if (OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                String targetEntityId = getPrimaryKeyFromParam("Id", keyPredicates);

                for (Claim c : pol.getClaims()) {
                    if ((targetEntityId == null) || (targetEntityId.equals(c.getId()))) {
                        returnEntity = odtt.translate(c);
                        break;
                    }
                }
            }
            else if (OdataEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                returnEntity = odtt.translate(pol.getProduct());
            }
        }

        // If the object is a beneficiary look up the related claim.
        else if (OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString().equals(sourceTypeName)) {
            Beneficiary ben = (Beneficiary) ent;
            if (OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                returnEntity = odtt.translate(ben.getClaim());
            }
        }
        return returnEntity;
    }

    /**
     * Method returns the entity that is related to the source entity. The returned entity is of targetEntity type
     * @param sourceEntity Source entity that contains a reference to the target
     * @param targetEntityType type of target object that will be returned
     * @return EntityCollection populated with 0 or more TargetEntityType objects
     */
    public Entity getRelatedEntity(Entity sourceEntity, EdmEntityType targetEntityType) {
        return getRelatedEntity(sourceEntity, targetEntityType, null);
    }

    /**
     * Method extracts the Primary key parameter from the list of keyParams passed in
     * @param paramName Name of the primary key parameter
     * @param keyParams List of key params to inspect
     * @return String that represents the primary key
         */
    private String getPrimaryKeyFromParam(String paramName, List<UriParameter> keyParams) {
        String param = null;

        for (UriParameter uriParameter : keyParams) {
            if (uriParameter.getName().equalsIgnoreCase(paramName)) {
                param = uriParameter.getText().replace("\'", "");
                break;
            }
        }

        return param;
    }

    /**
     * Method used to extract the Primary key from the URI passed in (ex Policy('1') will return 1)
     * @param uri Uri for the object ex Product('1')
     * @return String which is the primary key. 1 in the above example
     */
    private String parseId(URI uri) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(uri.getRawPath());

        if(m.find()) {
            return m.group();
        }
        else {
            return null;
        }
    }
}
