package com.codescience.salesforceconnect.entities;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;

import java.util.Comparator;

public class EntityComparator implements Comparator<Entity> {
    String sortPropertyName;
    boolean isDescending;

    /**
     * Constructor used to populate the Entity property to be compared as well as the sort order
     * @param sortPropertyName Property name to be compared
     * @param isDescending true if descending order
     */
    public EntityComparator (String sortPropertyName, boolean isDescending) {
        this.sortPropertyName = sortPropertyName;
        this.isDescending = isDescending;
    }

    /**
     * Method Compares two objects. It used the sortPropertyName and the isDescending value passed into the constructor
     * @param obj1 the first object to be compared.
     * @param obj2 the second object to be compared.
     * @return 0 if equal -1 if obj1 < obj2 otherwise 1
     */
    public int compare(Entity obj1, Entity obj2) {
        Property property1 = obj1.getProperty(sortPropertyName);
        Property property2 = obj2.getProperty(sortPropertyName);
        int direction = isDescending ? -1 : 1;

        Object value1 = property1.getValue();
        Object value2 = property2.getValue();
        if (value1 instanceof Comparable) {
            Comparable v1c = (Comparable) value1;
            Comparable v2c = (Comparable) value2;
            return v1c.compareTo(v2c) * direction;
        } else if (value1 instanceof Comparator) {
            Comparator v1c = (Comparator) value1;
            return v1c.compare(value1, value2) * direction;
        }

        return 0;
    }
}
