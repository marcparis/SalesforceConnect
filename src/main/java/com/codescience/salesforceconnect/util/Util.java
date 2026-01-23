package com.codescience.salesforceconnect.util;

import com.codescience.salesforceconnect.service.Messages;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    /**
     * Private Constructor to ensure static only
     */
    private Util() {

    }

    /**
     * Method returns the EdmEntity set for the given navigation property
     * Example:
     * For the following navigation: Odata.svc/ Policies(1) /Claims
     * we need the EdmEntitySet for the navigation property "Claims"
     * This is defined as follows in the metadata:
     * <code>
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

        LOG.info("In getNavigationTargetEntitySet");
        EdmEntitySet navigationTargetEntitySet;

        String navPropName = edmNavigationProperty.getName();
        EdmBindingTarget edmBindingTarget = startEdmEntitySet.getRelatedBindingTarget(navPropName);
        if (edmBindingTarget == null) {
            LOG.error(Messages.NOT_SUPPORTED);
            throw new ODataApplicationException(Messages.NOT_SUPPORTED,
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.getDefault());
        }

        if (edmBindingTarget instanceof EdmEntitySet) {
            navigationTargetEntitySet = (EdmEntitySet) edmBindingTarget;
        } else {
            LOG.error(Messages.NOT_SUPPORTED);
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
        LOG.info("In getPrimaryKeyFromParam");
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
        LOG.info("In parseId");
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
     * This method will create an entity using the Entity type. The ID property name and the navigation name
     * For example for a Product this might return Product(1)
     * @param entity Olingo entity
     * @param idPropertyName the name of the IdProperty field
     * @param navigationName the navigation name that occurs after the url ex (Product(1)/navigationName)
     * @param entitySetName Name of the EntitySet used to create the ID
     * @return URI that represents the Entity (ex Product(1)
     */
    public static URI createId(Entity entity, String idPropertyName, String navigationName, String entitySetName) {
        LOG.info("In createId");
        try {
            StringBuilder sb = new StringBuilder(entitySetName).append("(");
            final Property property = entity.getProperty(idPropertyName);
            sb.append("'").append(property.asPrimitive()).append("'").append(")");
            if(navigationName != null) {
                sb.append("/").append(navigationName);
            }
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException(Messages.ATOM_ID_CREATION_ERROR + "{}" + e.getMessage());
        }
    }
}
