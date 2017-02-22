package com.codescience.salesforceconnect.service;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of CsdlAbstractEdmProvider to define the metadata
 * for the Odata object model
 */
public class OdataEdmProvider extends CsdlAbstractEdmProvider {

    // Service Namespace
    private static final String NAMESPACE = "OData.InsuranceSystem";

    // EDM Container
    private static final String CONTAINER_NAME = "Container";
    private static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity Types Names
    private static final String ET_PRODUCT_NAME = "Product";
    public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

    private static final String ET_POLICY_NAME = "Policy";
    public static final FullQualifiedName ET_POLICY_FQN = new FullQualifiedName(NAMESPACE, ET_POLICY_NAME);

    private static final String ET_CLAIM_NAME = "Claim";
    public static final FullQualifiedName ET_CLAIM_FQN = new FullQualifiedName(NAMESPACE, ET_CLAIM_NAME);

    private static final String ET_BENEFICIARY_NAME = "Beneficiary";
    public static final FullQualifiedName ET_BENEFICIARY_FQN = new FullQualifiedName(NAMESPACE, ET_BENEFICIARY_NAME);

    // Entity Set Names
    public static final String ES_PRODUCTS_NAME = "Products";
    public static final String ES_POLICIES_NAME = "Policies";
    public static final String ES_CLAIMS_NAME = "Claims";
    public static final String ES_BENEFICIARIES_NAME = "Beneficiaries";


    @Override
    public List<CsdlSchema> getSchemas() {

        // create Schema
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        // add EntityTypes
        List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
        entityTypes.add(getEntityType(ET_PRODUCT_FQN));
        entityTypes.add(getEntityType(ET_POLICY_FQN));
        entityTypes.add(getEntityType(ET_CLAIM_FQN));
        entityTypes.add(getEntityType(ET_BENEFICIARY_FQN));
        schema.setEntityTypes(entityTypes);

        // add EntityContainer
        schema.setEntityContainer(getEntityContainer());

        // finally
        List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
        schemas.add(schema);

        return schemas;
    }


    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

        // this method is called for one of the EntityTypes that are configured in the Schema
        if(entityTypeName.equals(ET_PRODUCT_FQN)) {
            return getProductEntityType();
        }
        if(entityTypeName.equals(ET_POLICY_FQN)) {
            return getPolicyEntityType();
        }
        if(entityTypeName.equals(ET_CLAIM_FQN)) {
            return getClaimEntityType();
        }
        if(entityTypeName.equals(ET_BENEFICIARY_FQN)) {
            return getBeneficiaryEntityType();
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

        if(entityContainer.equals(CONTAINER)){
            if(entitySetName.equals(ES_PRODUCTS_NAME)){
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_PRODUCTS_NAME);
                entitySet.setType(ET_PRODUCT_FQN);

                return entitySet;
            }
            if(entitySetName.equals(ES_POLICIES_NAME)){
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_POLICIES_NAME);
                entitySet.setType(ET_POLICY_FQN);

                // navigation
                List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();

                CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
                navPropBinding.setTarget(CONTAINER.getFullQualifiedNameAsString() + "/" + ES_PRODUCTS_NAME); // the target entity set, where the navigation property points to
                navPropBinding.setPath(ET_PRODUCT_NAME); // the path from entity type to navigation property
                navPropBindingList.add(navPropBinding);
                navPropBinding = new CsdlNavigationPropertyBinding();
                navPropBinding.setTarget(ES_CLAIMS_NAME); // the target entity set, where the navigation property points to
                navPropBinding.setPath(ES_CLAIMS_NAME); // the path from entity type to navigation property
                navPropBindingList.add(navPropBinding);
                entitySet.setNavigationPropertyBindings(navPropBindingList);

                return entitySet;
            }
            if(entitySetName.equals(ES_CLAIMS_NAME)){
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_CLAIMS_NAME);
                entitySet.setType(ET_CLAIM_FQN);

                // navigation
                List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();

                CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
                navPropBinding.setTarget(ES_POLICIES_NAME); // the target entity set, where the navigation property points to
                navPropBinding.setPath(ET_POLICY_NAME); // the path from entity type to navigation property
                navPropBindingList.add(navPropBinding);

                // navigation
                navPropBinding = new CsdlNavigationPropertyBinding();
                navPropBinding.setTarget(ES_BENEFICIARIES_NAME); // the target entity set, where the navigation property points to
                navPropBinding.setPath(ES_BENEFICIARIES_NAME); // the path from entity type to navigation property
                navPropBindingList.add(navPropBinding);
                entitySet.setNavigationPropertyBindings(navPropBindingList);

                return entitySet;
            }
            if(entitySetName.equals(ES_BENEFICIARIES_NAME)){
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_BENEFICIARIES_NAME);
                entitySet.setType(ET_BENEFICIARY_FQN);

                // navigation
                CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
                navPropBinding.setTarget(ES_CLAIMS_NAME); // the target entity set, where the navigation property points to
                navPropBinding.setPath(ET_CLAIM_NAME); // the path from entity type to navigation property
                List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
                navPropBindingList.add(navPropBinding);
                entitySet.setNavigationPropertyBindings(navPropBindingList);

                return entitySet;
            }
        }

        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {

        // create EntitySets
        List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
        entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_POLICIES_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_CLAIMS_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_BENEFICIARIES_NAME));

        // create EntityContainer
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(CONTAINER_NAME);
        entityContainer.setEntitySets(entitySets);

        return entityContainer;
    }

    /**
     * Method will return the CsdlEntityContainerInfo for the given container name passed in
     * @param entityContainerName FullQualified Container name
     * @return CsdlEntityContainerInfo
     */
    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

        // This method is invoked when displaying the service document at e.g. http://localhost:8080/DemoService/DemoService.svc
        if(entityContainerName == null || entityContainerName.equals(CONTAINER)){
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER);
            return entityContainerInfo;
        }

        return null;
    }

    /**
     * Method returns a CsdlEntityType for the Product OData object
     * @return CsdlEntityType
     */
    private CsdlEntityType getProductEntityType() {
        //create EntityType properties
        CsdlProperty productId = new CsdlProperty().setName(Constants.PRODUCT_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty productName = new CsdlProperty().setName(Constants.PRODUCT_NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty productType = new CsdlProperty().setName(Constants.PRODUCT_TYPE).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty costPerUnitAmount = new CsdlProperty().setName(Constants.COST_PER_UNIT_AMOUNT).setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
        CsdlProperty activeProduct = new CsdlProperty().setName(Constants.PRODUCT_ACTIVE).setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());

        // create CsdlPropertyRef for Key element
        CsdlPropertyRef propertyRef = new CsdlPropertyRef();
        propertyRef.setName(Constants.PRODUCT_ID);

        // configure EntityType
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_PRODUCT_NAME);
        entityType.setProperties(Arrays.asList(productId,productName, productType, costPerUnitAmount, activeProduct));
        entityType.setKey(Collections.singletonList(propertyRef));

        return entityType;
    }

    /**
     * Method returns a CsdlEntityType for the Policy OData object
     * @return CsdlEntityType
     */
    private CsdlEntityType getPolicyEntityType() {
        //create EntityType properties
        CsdlProperty policyId = new CsdlProperty().setName(Constants.POLICY_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty policyStartDate = new CsdlProperty().setName(Constants.POLICY_START_DATE).setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());
        CsdlProperty policyEndDate = new CsdlProperty().setName(Constants.POLICY_END_DATE).setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());
        CsdlProperty policyHolderId = new CsdlProperty().setName(Constants.POLICY_HOLDER_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty totalCostAmount = new CsdlProperty().setName(Constants.TOTAL_COST_AMOUNT).setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
        CsdlProperty numberOfUnits = new CsdlProperty().setName(Constants.NUMBER_OF_UNITS).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
        CsdlProperty active = new CsdlProperty().setName(Constants.POLICY_ACTIVE).setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
        CsdlProperty productId = new CsdlProperty().setName(Constants.POLICY_PRODUCT_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlNavigationProperty product = new CsdlNavigationProperty().setName(Constants.PRODUCT).setType(ET_PRODUCT_FQN).setNullable(false);
        CsdlNavigationProperty claims = new CsdlNavigationProperty().setName(Constants.CLAIMS).setType(ET_CLAIM_FQN).setCollection(true).setPartner(Constants.POLICY);

        // create CsdlPropertyRef for Key element
        CsdlPropertyRef propertyRef = new CsdlPropertyRef();
        propertyRef.setName(Constants.POLICY_ID);

        // configure EntityType
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_POLICY_NAME);
        entityType.setProperties(Arrays.asList(policyId, policyStartDate, policyEndDate, policyHolderId, totalCostAmount, numberOfUnits, active, productId));
        entityType.setNavigationProperties(Arrays.asList(product,claims));
        entityType.setKey(Collections.singletonList(propertyRef));

        return entityType;
    }

    /**
     * Method returns a CsdlEntityType for the Claim OData object
     * @return CsdlEntityType
     */
    private CsdlEntityType getClaimEntityType() {
        //create EntityType properties
        CsdlProperty claimId = new CsdlProperty().setName(Constants.CLAIM_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty claimDate = new CsdlProperty().setName(Constants.CLAIM_DATE).setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());
        CsdlProperty claimReason = new CsdlProperty().setName(Constants.CLAIM_REASON).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty approved = new CsdlProperty().setName(Constants.CLAIM_APPROVED).setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
        CsdlProperty claimAmount = new CsdlProperty().setName(Constants.CLAIM_AMOUNT).setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
        CsdlProperty policyId = new CsdlProperty().setName(Constants.CLAIM_POLICY_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlNavigationProperty policy = new CsdlNavigationProperty().setName(Constants.POLICY).setType(ET_POLICY_FQN).setNullable(false).setPartner(Constants.CLAIMS);
        CsdlNavigationProperty beneficiaries = new CsdlNavigationProperty().setName(Constants.BENEFICIARIES).setType(ET_BENEFICIARY_FQN).setCollection(true).setPartner(Constants.CLAIM);

        // create CsdlPropertyRef for Key element
        CsdlPropertyRef propertyRef = new CsdlPropertyRef();
        propertyRef.setName(Constants.CLAIM_ID);

        // configure EntityType
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_CLAIM_NAME);
        entityType.setProperties(Arrays.asList(claimId, claimDate, claimReason, approved, claimAmount, policyId));
        entityType.setNavigationProperties(Arrays.asList(policy, beneficiaries));
        entityType.setKey(Collections.singletonList(propertyRef));

        return entityType;
    }

    private CsdlEntityType getBeneficiaryEntityType() {
        //create EntityType properties
        CsdlProperty beneficiaryId = new CsdlProperty().setName(Constants.BENEFICIARY_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty beneficiaryPercent = new CsdlProperty().setName(Constants.BENEFICIARY_PERCENT).setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
        CsdlProperty beneficiaryAmount = new CsdlProperty().setName(Constants.BENEFICIARY_AMOUNT).setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
        CsdlProperty contactIdentifierId = new CsdlProperty().setName(Constants.CONTACT_IDENTIFIER).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty claimId = new CsdlProperty().setName(Constants.BENEFICIARY_CLAIM_ID).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlNavigationProperty claim = new CsdlNavigationProperty().setName(Constants.CLAIM).setType(ET_CLAIM_FQN).setNullable(false).setPartner(Constants.BENEFICIARIES);

        // create CsdlPropertyRef for Key element
        CsdlPropertyRef propertyRef = new CsdlPropertyRef();
        propertyRef.setName(Constants.BENEFICIARY_ID);

        // configure EntityType
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_BENEFICIARY_NAME);
        entityType.setProperties(Arrays.asList(beneficiaryId, beneficiaryPercent, beneficiaryAmount, contactIdentifierId,claimId));
        entityType.setNavigationProperties(Arrays.asList(claim));
        entityType.setKey(Collections.singletonList(propertyRef));

        return entityType;
    }
}
