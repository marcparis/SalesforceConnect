package com.codescience.salesforceconnect.util;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

import java.util.List;
import java.util.Locale;

/**
 * Simple utility class with methods used to that is used to find an entity and traverse
 * relationships
 */
public class Util {

    /**
     * Method returns the EdmEntitySet given the uriInfo passed in
     * @param uriInfo
     * @return
     * @throws ODataApplicationException
     */
    public static EdmEntitySet getEdmEntitySet(UriInfoResource uriInfo) throws ODataApplicationException {

        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        // To get the entity set we have to interpret all URI segments
        if (!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
            // Here we should interpret the whole URI but in this example we do not support navigation so we throw an
            // exception
            throw new ODataApplicationException("Invalid resource type for first segment.", HttpStatusCode.NOT_IMPLEMENTED
                    .getStatusCode(), Locale.ENGLISH);
        }

        UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);

        return uriResource.getEntitySet();
    }

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
            throw new ODataApplicationException("Not supported.",
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }

        if (edmBindingTarget instanceof EdmEntitySet) {
            navigationTargetEntitySet = (EdmEntitySet) edmBindingTarget;
        } else {
            throw new ODataApplicationException("Not supported.",
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }

        return navigationTargetEntitySet;
    }
}
