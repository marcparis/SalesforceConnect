package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.service.Messages;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * Method translates an OData entity to a Backend object type
     * @param entity OData Entity
     * @param forceNulls if true will override exisiting values with nulls if false it will leave values as is
     * @return Subclass of BaseEntity
     * @throws ODataException if an error occured
     */
    public BaseEntity translate(Entity entity, boolean forceNulls) throws ODataException {
        try {
            Class<BaseEntity> baseEntityClass = (Class<BaseEntity>) Class.forName(getClassName());
            BaseEntity baseEntity = baseEntityClass.getDeclaredConstructor().newInstance();
            return merge(entity, baseEntity, forceNulls);
        } catch (Exception e) {
            throw new ODataException(Messages.ERROR_MERGING_ENTITIES, e);
        }
    }

    /**
     * Method will merge the changes from the entity into the Base Entity object passed in. It will only override
     * parameters passed in. If force nulls is set a null passed in will null out the value otherwise it will ignore the null
     * @param entity OData Entity with source data
     * @param baseEntity Back end entity with existing data
     * @param forceNulls if true a null passed in will override the content
     * @return BaseEntity update with new data
     * @throws ODataException Exception if making a change ocurred (ex method mismatch)
     */
    public BaseEntity merge(Entity entity, BaseEntity baseEntity, boolean forceNulls) throws ODataException {
        try {
            Class<BaseEntity> baseEntityClass = (Class<BaseEntity>) Class.forName(getClassName());
            List<Method> setMethods = new ArrayList<Method>();
            List<Method> methodsToNull = new ArrayList<Method>();

            // Track all the setter methods, ignore the rest
            // Track all the relevant setters so they can be nulled out if needed
            for (Method method : baseEntityClass.getMethods()) {
                if (method.getName().startsWith("set")) {
                    setMethods.add(method);
                    methodsToNull.add(method);
                }
            }

            // Loop through each property, if there is a setter that matches the property name set the value
            for (Property property : entity.getProperties()) {
                for (Method method : setMethods) {
                    if (method.getName().equals("set"+property.getName())) {
                        Object value = property.getValue();
                        Object[] values = {value};
                        if (method.getName().equals("setId")) {
                            // Can't null out Id
                            if (value != null) {
                                method.invoke(baseEntity, values);
                            }
                        } else {
                            method.invoke(baseEntity, values);
                        }
                        methodsToNull.remove(method);
                    }
                }
            }

            // Null out parameters that weren't set if this was a PUT.
            // Only null out if they are not primitives
            if(forceNulls && !methodsToNull.isEmpty()) {
                for (Method method : methodsToNull) {
                    boolean primitive = false;
                    for (Class paramClass : method.getParameterTypes()) {
                        if (paramClass.isPrimitive()) {
                            primitive = true;
                        }
                    }
                    if (primitive) {
                        continue;
                    }
                    Object[] values = {null};
                    method.invoke(baseEntity, values);
                }
            }
            return baseEntity;
        } catch (Exception e) {
            throw new ODataException(Messages.ERROR_MERGING_ENTITIES, e);
        }
    }

    /**
     * Overloaded method calls createId with a null navigationName
     * @param entity Olingo entity
     * @param idPropertyName the name of the IdProperty field
     * @return URI that represents the Entity (ex Product(1)
     */
    protected URI createId(Entity entity, String idPropertyName) {
        return Util.createId(entity, idPropertyName, null, getEntitySetName());
    }

    /**
     * Each translator will return the appropriate Entity Set name - abstract method to be overriden
     * @return String that is the entity set name
     */
    public abstract String getEntitySetName();

    /**
     * Each translator will return the appropriate class name of the BaseEntity it represents
     * @return Name of the class of the BaseEntity implementation
     */
    protected abstract String getClassName();
}
