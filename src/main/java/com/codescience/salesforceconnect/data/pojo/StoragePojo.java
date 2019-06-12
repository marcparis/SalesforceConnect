package com.codescience.salesforceconnect.data.pojo;

import com.codescience.salesforceconnect.data.BaseDAO;
import com.codescience.salesforceconnect.data.BeneficiaryDAO;
import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.data.pojo.ObjectFactory;
import com.codescience.salesforceconnect.entities.*;
import com.codescience.salesforceconnect.service.OdataEdmProvider;
import com.codescience.salesforceconnect.translators.ODataTypeTranslator;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the Storage interface that stores Simple Java objects in a Map
 */
public class StoragePojo implements Storage {
    private Map<String, ODataTypeTranslator> typeTranslators = new HashMap<String, ODataTypeTranslator>();
    private Map<String, BaseDAO> dataAccessObjects = new HashMap<String, BaseDAO>();

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

        ObjectFactory objectFactory = new ObjectFactory();
        // Find the object with the key
        for (BaseEntity baseEntity : objectFactory.getEntities(objectType).values()) {
            if (id == null) {
                entitySet.getEntities().add(odtt.translate(baseEntity));
            }
            else if (primaryKey && baseEntity.getRecordId().equalsIgnoreCase(id)) {
                entitySet.getEntities().add(odtt.translate(baseEntity));
            }
            else if (claimId) {
                Beneficiary ben = (Beneficiary) baseEntity;
                if (ben.getClaim().getRecordId().equalsIgnoreCase(id)) {
                    entitySet.getEntities().add(odtt.translate(baseEntity));
                }
            }
            else if (policyId) {
                Claim claim = (Claim) baseEntity;
                if (claim.getPolicy().getRecordId().equalsIgnoreCase(id)) {
                    entitySet.getEntities().add(odtt.translate(baseEntity));
                }
            }
            else if (productId) {
                Policy pol = (Policy) baseEntity;
                if (pol.getProduct().getRecordId().equalsIgnoreCase(id)) {
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
        return odtt.translate(dataAccessObjects.get(objectType).findByRecordId(keyValue));
    }

    /**
     * Method creates a single Entity for the EntitySet.
     *
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param entity Entity to Create
     * @return Entity created
     */
    @Override
    public Entity createEntityData(EdmEntitySet edmEntitySet, Entity entity) {
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        return createEntity(edmEntityType, entity);
    }

    /**
     * Method retuns a single Entity for the EntitySet. It filters based on the keyParams
     *
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param keyParams    List of key parameters to find the record
     * @param entity  Entity record being update
     * @param method Http Method being called
     * @return Entity for the key params passed in updated
     */
    @Override
    public Entity updateEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams, Entity entity, HttpMethod method) {
        return updateEntity( edmEntitySet.getEntityType(), keyParams, entity, method);
    }

    /**
     * Method deletes a single Entity for the EntitySet. It filters based on the keyParams
     *
     * @param edmEntitySet EDMEntitySet for the type to return
     * @param keyParams    List of key parameters to find the record
     * @return Deleted Entity for the key params passed in
     */
    @Override
    public Entity deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) {
        return null;
    }

    /**
     * Method returns the TypeTranslators Map.
     * @return TypeTranslator map that contains type translators for each object
     */
    @Override
    public Map<String, ODataTypeTranslator> getTypeTranslators() {
        return typeTranslators;
    }

    /**
     * Method sets the TypeTranslators Map
     * @param typeTranslators TypeTranslator Map that contains type translators for each object
     */
    @Override
    public void setTypeTranslators(Map<String, ODataTypeTranslator> typeTranslators) {
        this.typeTranslators = typeTranslators;
    }

    /**
     * Method returns a Map of BaseDAO implementations
     * @return DAO map that contains the DAO implementations for each object
     */
    @Override
    public Map<String, BaseDAO> getDataAccessObjects() { return dataAccessObjects; }

    /**
     * Method sets the Map of BaseDAO implementations
     * @param dataAccessObjects DAO map that contains the DAO implementations for each object
     */
    @Override
    public void setDataAccessObjects(Map<String, BaseDAO> dataAccessObjects) { this .dataAccessObjects = dataAccessObjects;}

    /**
     * Method takes a source entity object and will return the related collection of targetentity type objects specified
     * @param sourceEntity Source entity that is related to the returned target entity collection
     * @param targetEntityType Target entity type that should be returned
     * @return EntityCollection populated with 0 or more TargetEntityType objects
     */
    public EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) {
        Map<String, BaseEntity> entities = dataAccessObjects.get(sourceEntity.getType()).findAll();
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
        Map<String, BaseEntity> entities = dataAccessObjects.get(sourceEntity.getType()).findAll();
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
                    if ((targetEntityId == null) || (targetEntityId.equals(b.getRecordId()))) {
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
                    if ((targetEntityId == null) || (targetEntityId.equals(c.getRecordId()))) {
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

    private Entity createEntity(EdmEntityType edmEntityType, Entity entity) {
        FullQualifiedName fqn = edmEntityType.getFullQualifiedName();
        ODataTypeTranslator odtt = this.getTypeTranslators().get(fqn);
        BaseEntity pojo = odtt.translate(entity, this, false);
        pojo = dataAccessObjects.get(fqn.getFullQualifiedNameAsString()).insert(pojo);

        Property idProperty = entity.getProperty("ID");
        if (idProperty != null) {
            idProperty.setValue(ValueType.PRIMITIVE, pojo.getRecordId());
        } else {
            entity.getProperties().add(new Property(null, "ID", ValueType.PRIMITIVE, pojo.getRecordId()));
        }
        entity.setId(createId(OdataEdmProvider.ES_POLICIES_NAME, pojo.getRecordId()));
        return entity;
    }

    private Entity updateEntity(EdmEntityType edmEntityType, List<UriParameter> keyParams, Entity entity, HttpMethod httpMethod) {
        FullQualifiedName fqn = edmEntityType.getFullQualifiedName();
        ODataTypeTranslator odtt = this.getTypeTranslators().get(fqn);
        BaseEntity pojo = odtt.translate(entity, this, true);
        pojo = dataAccessObjects.get(fqn.getFullQualifiedNameAsString()).update(pojo);
        return odtt.translate(pojo);
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

    /**
     * Method used to create an ID as a URI
     * @param entitySetName Entity Set for the ID
     * @param id Unique Identifier for the record
     * @return URI for the ID
     */
    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
}
