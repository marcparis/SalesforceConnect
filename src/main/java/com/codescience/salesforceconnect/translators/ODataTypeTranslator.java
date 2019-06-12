package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.data.Storage;
import com.codescience.salesforceconnect.entities.BaseEntity;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Base Class for the Various Type Translators. Each Translator implementation will convert a Java POJO to an Olingo Entity
 * The base class contains common methods used by all subclasses
 */
public abstract class ODataTypeTranslator<T extends BaseEntity> {

    /**
     * Base abstract method to translate a BaseEntity object to an Olingo Entity. This must be implemented by the subclasses
     * @param object Subclass of BaseEntity
     * @return Olingo entity
     */
    public abstract Entity translate(T object);

    /**
     * Base abstract method to translate an Olingo Entity object to a BaseEntity Object. This must be implemented by the subclasses
     * @param entity Olingo entity
     * @param storage Storage implementation
     * @param merge boolean if true then nulls won't overwrite non nulls
     * @return Subclass of BaseEntity
     */
    public abstract T translate(Entity entity, Storage storage, boolean merge);

    /**
     * Each translator will return the appropriate Entity Set name - abstract method to be overriden
     * @return String that is the entity set name
     */
    public abstract String getEntitySetName();

    /**
     * Overloaded method calls createId with a null navigationName
     * @param entity Olingo entity
     * @param idPropertyName the name of the IdProperty field
     * @return URI that represents the Entity (ex Product(1)
     */
    protected URI createId(Entity entity, String idPropertyName) {
        return createId(entity, idPropertyName, null);
    }

    /**
     * This method will create an entity using the Entity type. The Id property name and the navigation name
     * For example for a Product this might return Product(1)
     * @param entity Olingo entity
     * @param idPropertyName the name of the IdProperty field
     * @param navigationName the navigation name that occurs after the url ex (Product(1)/navigationName)
     * @return URI that represents the Entity (ex Product(1)
     */
    protected URI createId(Entity entity, String idPropertyName, String navigationName) {
        try {
            StringBuilder sb = new StringBuilder(getEntitySetName()).append("(");
            final Property property = entity.getProperty(idPropertyName);
            sb.append("'").append(property.asPrimitive()).append("'").append(")");
            if(navigationName != null) {
                sb.append("/").append(navigationName);
            }
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create (Atom) id for entity: " + entity, e);
        }
    }

    /**
     * Method used to extract a value from a entity property. It will check for null values
     * @param existingValue Existing value that may be overwittent
     * @param prop source property
     * @param merge If true then don't replace a non null value with a null. Otherwise overwrite
     * @return Object representing the parameter's value
     */
    protected Object extractValue(Object existingValue, Property prop, boolean merge) {
        if (!merge) {
            return prop == null ? null : prop.getValue();
        } else {
            if (prop == null) {
                return existingValue;
            } else {
                Object value = prop.getValue();
                if (value == null) {
                    return existingValue;
                } else {
                    return value;
                }
            }
        }
    }
}
