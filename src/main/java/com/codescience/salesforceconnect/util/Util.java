package com.codescience.salesforceconnect.util;

import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.Messages;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utility class with methods used to that is used to find an entity and traverse
 * relationships
 */
public class Util {

    /**
     * Method used to find a given entitty of type EDMEntityType from an entity collection given search key parameters
     * @param edmEntityType Type of entity to search
     * @param entitySet Collection of the entity
     * @param keyParams Parameters to filter
     * @return Entity found
     */
    public static Entity findEntity(EdmEntityType edmEntityType, EntityCollection entitySet,
                                    List<UriParameter> keyParams) {

        List<Entity> entityList = entitySet.getEntities();

        // loop over all entities in order to find that one that matches
        // all keys in request e.g. contacts(ContactID=1, CompanyID=1)
        for (Entity entity : entityList) {
            boolean foundEntity = entityMatchesAllKeys(edmEntityType, entity, keyParams);
            if (foundEntity) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Method used to determine whether all they keys passed in match
     * @param edmEntityType The type of entity to check
     * @param rt_entity The actual entity object
     * @param keyParams The list of key params to compare
     * @return true if they all match false otherwise
     */
    private static boolean entityMatchesAllKeys(EdmEntityType edmEntityType, Entity rt_entity,
                                               List<UriParameter> keyParams) {

        // loop over all keys
        for (final UriParameter key : keyParams) {
            // key
            String keyName = key.getName();
            String keyText = key.getText();

            // note: below line doesn't consider: keyProp can be part of a complexType in V4
            // in such case, it would be required to access it via getKeyPropertyRef()
            // but since this isn't the case in our model, we ignore it in our implementation
            EdmProperty edmKeyProperty = (EdmProperty) edmEntityType.getProperty(keyName);
            // Edm: we need this info for the comparison below
            Boolean isNullable = edmKeyProperty.isNullable();
            Integer maxLength = edmKeyProperty.getMaxLength();
            Integer precision = edmKeyProperty.getPrecision();
            Boolean isUnicode = edmKeyProperty.isUnicode();
            Integer scale = edmKeyProperty.getScale();
            // get the EdmType in order to compare
            EdmType edmType = edmKeyProperty.getType();
            // if(EdmType instanceof EdmPrimitiveType) // do we need this?
            EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmType;

            // Runtime data: the value of the current entity
            // don't need to check for null, this is done in FWK
            Object valueObject = rt_entity.getProperty(keyName).getValue();
            // TODO if the property is a complex type

            // now need to compare the valueObject with the keyText String
            // this is done using the type.valueToString
            String valueAsString;
            try {
                valueAsString = edmPrimitiveType.valueToString(valueObject, isNullable,
                        maxLength, precision, scale, isUnicode);
            } catch (EdmPrimitiveTypeException e) {
                return false; // TODO proper Exception handling
            }

            if (valueAsString == null) {
                return false;
            }

            boolean matches = valueAsString.equals(keyText);
            if (!matches) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method returns the EdmEntity set for the given navigation property
     * Example:
     * For the following navigation: Odata.svc/Policies(1)/Claims
     * we need the EdmEntitySet for the navigation property "Claims"
     *
     * This is defined as follows in the metadata:
     * <code>
     *
     * <EntitySet Name="Policies" EntityType="OData.Demo.Policy">
     * <NavigationPropertyBinding Path="Claims" Target="Claims"/>
     * </EntitySet>
     * </code>
     * The "Target" attribute specifies the target EntitySet
     * Therefore we need the startEntitySet "Policies" in order to retrieve the target EntitySet "Claims"
     * @param startEdmEntitySet Entity set of the starting point on the navigation
     * @param edmNavigationProperty The navigation property used to follow to get the target entity set
     */
    public static EdmEntitySet getNavigationTargetEntitySet(EdmEntitySet startEdmEntitySet,
                                                            EdmNavigationProperty edmNavigationProperty)
            throws ODataApplicationException {

        EdmEntitySet navigationTargetEntitySet;

        String navPropName = edmNavigationProperty.getName();
        EdmBindingTarget edmBindingTarget = startEdmEntitySet.getRelatedBindingTarget(navPropName);
        if (edmBindingTarget == null) {
            throw new ODataApplicationException(Messages.NOT_SUPPORTED,
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }

        if (edmBindingTarget instanceof EdmEntitySet) {
            navigationTargetEntitySet = (EdmEntitySet) edmBindingTarget;
        } else {
            throw new ODataApplicationException(Messages.NOT_SUPPORTED,
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }

        return navigationTargetEntitySet;
    }


    /**
     * Method extracts the Primary key parameter from the list of keyParams passed in
     * @param paramNames List Name of the primary key parameters
     * @param keyParams List of key params to inspect
     * @return String that represents the primary key
     */
    public static String getPrimaryKeyFromParam(List<String> paramNames, List<UriParameter> keyParams) {
        // Grab the first - simple - could get more complicated
        String paramName = paramNames.get(0);

        for (UriParameter uriParameter : keyParams) {
            if (uriParameter.getName().equalsIgnoreCase(paramName)) {
                return uriParameter.getText().replace("'", "");
            }
        }

        return null;
    }

    /**
     * Method used to extract the Primary key from the URI passed in (ex Policy('1') will return 1)
     * @param uri Uri for the object ex Product('1')
     * @return String which is the primary key. 1 in the above example
     */
    public static String parseId(URI uri) {
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
     * This method will create an entity using the Entity type. The Id property name and the navigation name
     * For example for a Product this might return Product(1)
     * @param entity Olingo entity
     * @param idPropertyName the name of the IdProperty field
     * @param navigationName the navigation name that occurs after the url ex (Product(1)/navigationName)
     * @param entitySetName Name of the Entityset used to create the ID
     * @return URI that represents the Entity (ex Product(1)
     */
    public static URI createId(Entity entity, String idPropertyName, String navigationName, String entitySetName) {
        try {
            StringBuilder sb = new StringBuilder(entitySetName).append("(");
            final Property property = entity.getProperty(idPropertyName);
            sb.append("'").append(property.asPrimitive()).append("'").append(")");
            if(navigationName != null) {
                sb.append("/").append(navigationName);
            }
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException(Messages.ATOM_ID_CREATION_ERROR + entity, e);
        }
    }
}
