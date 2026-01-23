package com.codescience.salesforceconnect.data;

import com.codescience.salesforceconnect.entities.*;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.Messages;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import com.codescience.salesforceconnect.translators.ODataTypeTranslator;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementation of the Storage interface that stores Simple Java objects in a Map
 */
public class StoragePojo implements Storage {
    private static final Logger LOG = LoggerFactory.getLogger(StoragePojo.class);
    private Map<String, ODataTypeTranslator> typeTranslators = new HashMap<>();
    private final Map<String, Map<String, BaseEntity>> objects = new HashMap <> ();

    public StoragePojo() {
        initializeData();
    }

    /**
     * Method used to initialize default Pojo records as the entity model
     */
    private void initializeData() {
        LOG.info("In initializeData method");
        Map <String, BaseEntity> mapOfProducts = new TreeMap<>();
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

        Map <String, BaseEntity> mapOfPolicies = new TreeMap<>();
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

        Map <String, BaseEntity> mapOfClaims = new TreeMap<>();
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

        Map <String, BaseEntity> mapOfBeneficiaries = new TreeMap<>();
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
    @Override
    public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, UriInfo uriInfo) {
        LOG.info("In readEntitySetData method");
        EntityCollection entitySet = new EntityCollection();

        try {
            filterEntities(uriInfo.getFilterOption(), entitySet, edmEntitySet);
            processOrder(entitySet, uriInfo);
        } catch (ODataApplicationException e) {
            throw new RuntimeException(e);
        } catch (ExpressionVisitException e) {
            throw new RuntimeException(e);
        }

        return entitySet;
    }

    /**
     * Method reads an individual object from the Pojo Storage.
     * @param edmEntitySet Type of object to read
     * @param keyParams  Identity keys to read from the data storage
     * @return Entity object containing data or null if nothing found
     */
    @Override
    public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) {
        LOG.info("In readEntityData method");
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        FullQualifiedName fqn = edmEntityType.getFullQualifiedName();
        String objectType = fqn.getFullQualifiedNameAsString();
        String keyValue = Util.getPrimaryKeyFromParam(edmEntityType.getKeyPredicateNames(), keyParams);

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
     * Method takes a source entity object and will return the related collection of target entity type objects specified
     * @param sourceEntity Source entity that is related to the returned target entity collection
     * @param targetEntityType Target entity type that should be returned
     * @return EntityCollection populated with 0 or more TargetEntityType objects
     */
    @Override
    public EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) {
        LOG.info("In getRelatedEntityCollection method");
        Map<String, BaseEntity> entities = objects.get(sourceEntity.getType());
        BaseEntity ent = entities.get(Util.parseId(sourceEntity.getId()));
        ODataTypeTranslator odtt = getTypeTranslators().get(targetEntityType.getFullQualifiedName().getFullQualifiedNameAsString());
        EntityCollection ec = new EntityCollection();

        // For brevity and clarity purposes the code will check the types and load the relationship data from the underlying objects. Normally implement in a more generic fashion by autowiring or configuring
        // metadata lookups between the Odata entity model and the underlying persistence model

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
    @Override
    public Entity getRelatedEntity(Entity sourceEntity, EdmEntityType targetEntityType, List<UriParameter> keyPredicates) {
        LOG.info("In getRelatedEntity method");
        Map<String, BaseEntity> entities = objects.get(sourceEntity.getType());
        BaseEntity ent = entities.get(Util.parseId(sourceEntity.getId()));
        ODataTypeTranslator odtt = getTypeTranslators().get(targetEntityType.getFullQualifiedName().getFullQualifiedNameAsString());

        Entity returnEntity = null;
        String targetTypeName = targetEntityType.getFullQualifiedName().getFullQualifiedNameAsString();
        String sourceTypeName = sourceEntity.getType();

        // For brevity and clarity purposes the code will check the types and load the relationship data from the underlying objects. Normally implement in a more generic fashion by autowiring or configuring
        // metadata lookups between the Odata entity model and the underlying persistence model

        // If the object is a claim object look up either the related policy, or the related beneficiary (selected among the list)
        if (OdataEdmProvider.ET_CLAIM_FQN.getFullQualifiedNameAsString().equals(sourceTypeName)) {
            Claim claim = (Claim) ent;
            if (OdataEdmProvider.ET_POLICY_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                returnEntity = odtt.translate(claim.getPolicy());
            }
            else if (OdataEdmProvider.ET_BENEFICIARY_FQN.getFullQualifiedNameAsString().equals(targetTypeName)) {
                String targetEntityId = Util.getPrimaryKeyFromParam(targetEntityType.getKeyPredicateNames(), keyPredicates);

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
                String targetEntityId = Util.getPrimaryKeyFromParam(targetEntityType.getKeyPredicateNames(), keyPredicates);

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
    @Override
    public Entity getRelatedEntity(Entity sourceEntity, EdmEntityType targetEntityType) {
        return getRelatedEntity(sourceEntity, targetEntityType, null);
    }

    /**
     * Method Creates the entity for the source entity passed in and persists it
     * @param entity Source entity
     * @return Entity that was newly created
     * @throws ODataException if error occurred creating the entity
     */
    @Override
    public Entity createEntity(Entity entity) throws ODataException {
        LOG.info("In createEntity method");
        if (entity == null) {
            return null;
        }
        BaseEntity be = getBaseEntityByKey(entity);
        if (be == null) {
            ODataTypeTranslator odtt = typeTranslators.get(entity.getType());
            Map<String,BaseEntity> baseEntities = objects.get(entity.getType());
            be = odtt.translate(entity, true);
            if (be.getId() == null) {
                setNextKey(be, baseEntities);
            }
            baseEntities.put(be.getId(), be);
            return odtt.translate(be);
        } else {
            LOG.error(Messages.ERROR_ENTITY_ALREADY_EXISTS);
            throw new ODataException(Messages.ERROR_ENTITY_ALREADY_EXISTS);
        }
    }

    /**
     * Method Updates the entity for the source entity passed in and persists it
     * @param entity Source entity
     * @param forceNulls If true a null passed in will replace a value, if false it won't
     * @return Entity that was newly updated
     * @throws ODataException if error occurred updating the entity
     */
    @Override
    public Entity updateEntity(Entity entity, boolean forceNulls) throws ODataException {
        LOG.info("In updateEntity method");
        if (entity == null) {
            return null;
        }
        BaseEntity baseEntity = getBaseEntityByKey(entity);
        if (baseEntity == null) {
            throw new ODataException(Messages.ERROR_ENTITY_NOT_FOUND_FOR_UPDATE);
        }
        Map<String, BaseEntity> typeObjects = getBaseEntitiesByType(entity);
        ODataTypeTranslator ott = typeTranslators.get(entity.getType());
        baseEntity = ott.merge(entity, baseEntity, forceNulls);
        typeObjects.put(baseEntity.getId(), baseEntity);
        return ott.translate(baseEntity);
    }

    /**
     * Method Deletes the entity for the source entity passed in and persists it
     * @param edmEntitySet Source entitySet type
     * @param keyPredicates Primary key to find record to delete
     * @return Entity that was deleted from persistent storage
     */
    @Override
    public Entity deleteEntity(EdmEntitySet edmEntitySet, List<UriParameter> keyPredicates) {
        LOG.info("In deleteEntity method");
        // Null check
        if ((edmEntitySet == null) || (keyPredicates == null)) {
            return null;
        }
        ODataTypeTranslator odtt = typeTranslators.get(edmEntitySet.getEntityType().getName());
        Map<String, BaseEntity> typeObjects = objects.get(edmEntitySet.getEntityType().getName());
        String id = Util.getPrimaryKeyFromParam(edmEntitySet.getEntityType().getKeyPredicateNames(),keyPredicates);
        return odtt.translate(typeObjects.remove(id));
    }

    /**
     * Method Upserts the entity for the source entity passed in and persists it.
     * It will update it if it matches an existing record by key, otherwise it will create new
     * @param entity Source entity
     * @param forceNulls If true a null passed in will replace a value, if false it won't
     * @return Entity that was upserted in storage
     * @throws ODataException if error occurred creating or updating the entity
     */
    @Override
    public Entity upsertEntity(Entity entity, boolean forceNulls) throws ODataException {
        LOG.info("In upsertEntity method");
        if (entity == null) {
            return null;
        }
        BaseEntity be = getBaseEntityByKey(entity);
        if (be == null) {
            return createEntity(entity);
        } else {
            return updateEntity(entity, forceNulls);
        }
    }

    /**
     * Method will return a Maps of Base entity types based on the entity passed in.
     * @param entity Entity type to use as base for lookup
     * @return Map of BaseEntities for the type by id
     * @throws ODataException If an error occurred looking up the entity
     */
    private Map<String, BaseEntity> getBaseEntitiesByType(Entity entity) throws ODataException {
        LOG.info("In getBaseEntitiesByType method");
        // Get the type
        String entityType = entity.getType();
        if (entityType == null) {
            throw new ODataException(Messages.ERROR_ENTITY_TYPE_NOT_FOUND);
        }
        return objects.get(entityType);
    }

    /**
     * Method will return a Base entity type based on the entity passed in.
     * @param entity Entity type to use as base for lookup
     * @return BaseEntity subclass if found or null
     * @throws ODataException If an error occurred looking up the entity
     */
    private BaseEntity getBaseEntityByKey(Entity entity) throws ODataException {
        LOG.info("In getBaseEntityByKey method");
        Map<String, BaseEntity> typeObjects = getBaseEntitiesByType(entity);
        Property property = entity.getProperty(Constants.ID);
        if ((property == null) || (property.getValue() == null)) {
            return null;
        }

        // Retrieve by ID
        String id = (String) property.getValue();
        return typeObjects.get(id);
    }

    /**
     * Method finds the last key and sets the BaseEntity's subclass to max value + 1
     * @param baseEntity Base Entity value whose key to set
     * @param entityMap Map of all the entities for the type
     */
    private void setNextKey(BaseEntity baseEntity, Map<String, BaseEntity> entityMap) {
        LOG.info("In setNextKey method");
        int newKey = 0;
        for (String key : entityMap.keySet()) {
           int keyValue = Integer.parseInt(key);
           newKey = Math.max(newKey, keyValue);
        }
        baseEntity.setId(Integer.toString(newKey+1));
    }

    /**
     * Method processes the orderBy parameter and orders the list in ascending or descending order
     * @param entitySet The Collection containing the entities to sort
     * @param uriInfo URI Info object containing the sort parameters
     */
    private void processOrder(EntityCollection entitySet, UriInfo uriInfo) {
        LOG.info("In processOrder method");
        OrderByOption orderByOption = uriInfo.getOrderByOption();
        if (orderByOption == null) {
            return;
        }

        for (OrderByItem orderByItem : orderByOption.getOrders()) {
            Expression expression = orderByItem.getExpression();
            if (expression instanceof Member) {
                for (UriResource uriResource : ((Member) expression).getResourcePath().getUriResourceParts()) {
                    if (uriResource instanceof UriResourcePrimitiveProperty) {
                        String sortPropertyName = ((UriResourcePrimitiveProperty)uriResource).getProperty().getName();
                        EntityComparator entityComparator = new EntityComparator(sortPropertyName,orderByItem.isDescending());
                        Collections.sort (entitySet.getEntities(), entityComparator);
                    }
                }
            }
        }
    }

    private void filterEntities(FilterOption filterOption, EntityCollection entitySet, EdmEntitySet edmEntitySet) throws ExpressionVisitException, ODataApplicationException {
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        String objectType = edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString();
        ODataTypeTranslator odtt = getTypeTranslators().get(objectType);

        for (BaseEntity baseEntity : objects.get(objectType).values()) {
            entitySet.getEntities().add(odtt.translate(baseEntity));
        }

        if (filterOption == null) {
            return;
        }
        Expression filterExpression = filterOption.getExpression();

        Iterator<Entity> entityIterator = entitySet.getEntities().iterator();

        while (entityIterator.hasNext()) {
            Entity currentEntity = entityIterator.next();
            PojoFilterExpressionVisitor pojoFilterExpressionVisitor = new PojoFilterExpressionVisitor(currentEntity);
            Object visitorResult = filterExpression.accept(pojoFilterExpressionVisitor);
            if (visitorResult instanceof Boolean) {
                if (!Boolean.TRUE.equals(visitorResult)) {
                    entityIterator.remove();
                }
            } else {
                throw new ODataApplicationException("A filter expression must evaluate to type Edm.Boolean", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
            }
        }
    }
}
