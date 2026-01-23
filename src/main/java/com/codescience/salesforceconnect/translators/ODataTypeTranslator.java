package com.codescience.salesforceconnect.translators;

import com.codescience.salesforceconnect.entities.BaseEntity;
import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.Messages;
import com.codescience.salesforceconnect.util.Util;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.ex.ODataException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
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
     * @param forceNulls if true will override existing values with nulls if false it will leave values as is
     * @return Subclass of BaseEntity
     * @throws ODataException if an error occurred
     */
    public BaseEntity translate(Entity entity, boolean forceNulls) throws ODataException {
        try {
            // Ignore warning about class cast check, it will be caught by exception and thrown
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
     * @throws ODataException Exception if making a change occurred (ex method mismatch)
     */
    public BaseEntity merge(Entity entity, BaseEntity baseEntity, boolean forceNulls) throws ODataException {
        try {
            // Ignore warning about class cast check, it will be caught by exception and thrown
            Class<BaseEntity> baseEntityClass = (Class<BaseEntity>) Class.forName(getClassName());
            List<Method> setMethods = new ArrayList<>();
            List<Method> methodsToNull = new ArrayList<>();

            populateMethods(setMethods, methodsToNull, baseEntityClass);
            populateProperties(setMethods, methodsToNull, entity, baseEntity);
            populateNulls(methodsToNull, baseEntity, forceNulls);

            return baseEntity;
        } catch (Exception e) {
            throw new ODataException(Messages.ERROR_MERGING_ENTITIES, e);
        }
    }

    /**
     * Method Populates the setMethods and the methods to null lists with all the setters on the BaseEntityClass implementation
     * @param setMethods List of setterMethods
     * @param methodsToNull List of methods to null, will be reduced for all setters that populate a value
     * @param baseEntityClass Class implementation of the BaseEntityClass to get the methods
     */
    private void populateMethods(List<Method> setMethods, List<Method> methodsToNull, Class<BaseEntity> baseEntityClass) {
        // Track all the setter methods, ignore the rest
        // Track all the relevant setters so they can be set to null if needed
        for (Method method : baseEntityClass.getMethods()) {
            if (method.getName().startsWith(Constants.SETTER_PREFIX)) {
                setMethods.add(method);
                methodsToNull.add(method);
            }
        }
    }

    /**
     * Method will populate the Properties on the BaseEntity with the Values from the Entity object
     * @param setMethods List of Setter Methods
     * @param methodsToNull List of Setter Methods to set params to null
     * @param entity Source Entity with the data
     * @param baseEntity BaseEntity implementation with the back end data
     * @throws IllegalAccessException Thrown if trying to illegally access a method
     * @throws InvocationTargetException Thrown in the case of an invocation error
     */
    private void populateProperties(List<Method> setMethods, List<Method> methodsToNull, Entity entity, BaseEntity baseEntity) throws IllegalAccessException, InvocationTargetException {
        // Loop through each property, if there is a setter that matches the property name set the value
        for (Property property : entity.getProperties()) {
            for (Method method : setMethods) {
                if (method.getName().equals(Constants.SETTER_PREFIX+property.getName())) {
                    Object value = property.getValue();
                    Object[] values = {value};
                    methodsToNull.remove(method);

                    if (!skipNull(method, value) && method.canAccess(baseEntity)) {
                        method.invoke(baseEntity, values);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Method will set nulls to any setter in the BaseEntity that wasn't passed in to the Entity. This occurs during PUTs
     * @param methodsToNull List of methods whose parameter should be set to null
     * @param baseEntity Base Entity implementation that contains the back end data
     * @param forceNulls if true it will force nulls on unset parameters - otherwise it will skip
     * @throws IllegalAccessException Thrown if trying to illegally access a method
     * @throws InvocationTargetException Thrown in the case of an invocation error
     */
    private void populateNulls(List<Method> methodsToNull, BaseEntity baseEntity, boolean forceNulls) throws IllegalAccessException, InvocationTargetException {
        // Null out parameters that weren't set if this was a PUT.
        // Only null out if they are not primitives
        if(forceNulls && !methodsToNull.isEmpty()) {
            for (Method method : methodsToNull) {
                Object[] values = {null};
                if (!isPrimitiveParam(method) && method.canAccess(baseEntity)) {
                    method.invoke(baseEntity, values);
                }
            }
        }
    }

    /**
     * Method will return true if the input is a primitive. Should not attempt to null it
     * @param method Method to inspect
     * @return true if is primitive otherwise false
     */
    private boolean isPrimitiveParam(Method method) {
        for (Class<?> paramClass : method.getParameterTypes()) {
            if (paramClass.isPrimitive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method will return true if the method is such that it should skip being set to null
     * @param method Method whose parameters should be evaluated
     * @param value Value to be set
     * @return true if this method's params should not be null, otherwise false
     */
    private boolean skipNull(Method method, Object value) {
        // Can't null out ID
        // Don't try to set null to a primitive
        boolean returnValue = false;
        if (value == null) {
            if (method.getName().equals(Constants.SETTER_PREFIX + Constants.ID)) {
                returnValue = true;
            }
            if (isPrimitiveParam(method)) {
                returnValue = true;
            }
        }
        return returnValue;
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
     * Each translator will return the appropriate Entity Set name - abstract method to be overridden
     * @return String that is the entity set name
     */
    public abstract String getEntitySetName();

    /**
     * Each translator will return the appropriate class name of the BaseEntity it represents
     * @return Name of the class of the BaseEntity implementation
     */
    protected abstract String getClassName();
}
