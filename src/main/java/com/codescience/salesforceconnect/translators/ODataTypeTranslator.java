package com.codescience.salesforceconnect.translators;

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
public abstract class ODataTypeTranslator {

    /**
     * Base abstract method to translate a BaseEntity object to an Olingo Entity. This must be implemented by the subclasses
     * @param object Subclass of BaseEntity
     * @return Olingo entity
     */
    public abstract Entity translate(BaseEntity object);

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
            sb.append(property.asPrimitive()).append(")");
            if(navigationName != null) {
                sb.append("/").append(navigationName);
            }
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create (Atom) id for entity: " + entity, e);
        }
    }

    /**
     * Each translator will return the appropriate Entity Set name - abstract method to be overriden
     * @return String that is the entity set name
     */
    public abstract String getEntitySetName();
}
