package com.codescience.salesforceconnect.entities;

import java.io.Serializable;

/**
 * Base Entity class that should be subclassed by all POJO entity model implementations
 */
public abstract class BaseEntity implements Serializable, Comparable<BaseEntity> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * Method returns the ID (Primary Key) parameter.  All entities assumed to have Integer primary keys
     * @return String representing a primary key
     */
    public String getId() {
        return id;
    }

    /**
     * Method sets the ID (Primary Key) parameter. All entities assumed to have Integer primary keys
     * @param id String representing a primary key
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;

        BaseEntity that = (BaseEntity) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                '}';
    }

    /**
     * Method compares the argument passed in to ID parameter of the base entity. If the value passed in is a different type than Base Entity return 0
     * If the object passed in is null return -1 (nulls go to the end). If the object passed in has a null id and this object has a null id return 0
     * If one object has a null id it's sent to the end.
     * @param be BaseEntity to compare
     * @return 0 if they are the same, -1 if this object is smaller, 1 if this object is bigger
     */
    public int compareTo(BaseEntity be) {
        int returnValue;
        if ((be != this)) {
            if ((be.getId() == null) && (getId() == null)) {
                returnValue = 0;
            } else if (be.getId() == null) {
                returnValue = -1;
            } else if (getId() == null) {
                returnValue = 1;
            } else {
                returnValue = getId().compareTo(be.getId());
            }
        } else {
            returnValue = 0;
        }

        return returnValue;
    }
}