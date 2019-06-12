package com.codescience.salesforceconnect.entities;

import java.io.Serializable;

/**
 * Base Entity class that should be subclassed by all POJO entity model implementations
 */
public abstract class BaseEntity implements Serializable, Comparable {

    static final long serialVersionUID = 1L;

    private String recordId;

    /**
     * Method returns the Record Id (Primary Key) parameter.
     * @return String representing a primary key
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * Method sets the Record Id (Primary Key) parameter.
     * @param recordId String representing a primary key
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;

        BaseEntity that = (BaseEntity) o;

        return getRecordId() != null ? getRecordId().equals(that.getRecordId()) : that.getRecordId() == null;
    }

    @Override
    public int hashCode() {
        return getRecordId() != null ? getRecordId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + recordId +
                '}';
    }

    /**
     * Method compares the argument passed in to Id parameter of the base entity. If the value passed in is a different type than Base Entity return 0
     * If the object passed in is null return -1 (nulls go to the end). If the object passed in has a null id and this object has a null id return 0
     * If one object has a null id it's sent to the end.
     * @param o Object to compare
     * @return 0 if they are the same, -1 if this object is smaller, 1 if this object is bigger
     */
    public int compareTo(Object o) {
        int returnValue = 0;

        if (o == null) {
            returnValue = -1;
        }
        else if ((o != this) && (o instanceof BaseEntity)) {
            BaseEntity be = (BaseEntity) o;
            if ((be.getRecordId() == null) && (getRecordId() == null)) {
                returnValue = 0;
            } else if (be.getRecordId() == null) {
                returnValue = -1;
            } else if (getRecordId() == null) {
                returnValue = 1;
            } else {
                returnValue = getRecordId().compareTo(be.getRecordId());
            }
        }

        return returnValue;
    }

    /**
     * Abstract method will merge the entity passed in and
     * @param baseEntity Source Entity to merge
     * @param ignoreNulls if true don't overwrite the target if the source is null
     */
    public abstract void merge(BaseEntity baseEntity, boolean ignoreNulls);

    /**
     * Method will return true if the value passed in is not null or the ignoreNulls is false
     * @param value Value to set
     * @param ignoreNulls if true then return false if the object passed in is null
     * @return true if value should be set. False otherwise
     */
    protected boolean shouldSetValue(Object value, boolean ignoreNulls) {
        // If there is a value then ignoreNulls is irrelevant
        if (value != null) {
            return true;
        }
        // value is null so set it only if ignoreNulls is false
        return !ignoreNulls;
    }
}