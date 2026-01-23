package com.codescience.salesforceconnect.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TypeWrapper {

    public Object leftSourceObject;
    public Object rightSourceObject;
    public BigDecimal leftObjectConvertedToNumber;
    public BigDecimal rightObjectConvertedToNumber;
    public Boolean leftObjectConvertedToBoolean;
    public Boolean rightObjectConvertedToBoolean;
    public String leftObjectConvertedToString;
    public String rightObjectConvertedToString;
    public LocalDate leftObjectConvertedToDate;
    public LocalDate rightObjectConvertedToDate;

    public TypeWrapper(Object leftObject, Object rightObject) {
        this.leftSourceObject = leftObject;
        this.rightSourceObject = rightObject;
        this.leftObjectConvertedToString = leftObject != null ? leftObject.toString() : null;
        this.rightObjectConvertedToString = rightObject != null ? rightObject.toString() : null;
        this.leftObjectConvertedToNumber = convertToBigDecimal(leftObject);
        this.rightObjectConvertedToNumber = convertToBigDecimal(rightObject);
        this.leftObjectConvertedToBoolean = convertToBoolean(leftObject);
        this.rightObjectConvertedToBoolean = convertToBoolean(rightObject);
        this.leftObjectConvertedToDate = convertToDate(leftObject);
        this.rightObjectConvertedToDate = convertToDate(rightObject);
    }

    public boolean isBothNumeric() {
        return (this.leftObjectConvertedToNumber != null) && (this.rightObjectConvertedToNumber != null);
    }

    public boolean isBothBoolean () {
        return (this.leftObjectConvertedToBoolean != null) && (this.rightObjectConvertedToBoolean != null);
    }

    public boolean isBothString () {
        return (this.leftObjectConvertedToString != null) && (this.rightObjectConvertedToString != null);
    }

    public boolean isBothDate () {
        return (this.leftObjectConvertedToDate != null) && (this.rightObjectConvertedToDate != null);
    }

    /**
     * Method converts the object passed in to a boolean if possible - otherwise it returns null
     * @param object Object to convert to Boolean
     * @return Object converted to Boolean or null
     */
    private Boolean convertToBoolean(Object object) {
        Boolean returnValue = null;

        if (object instanceof Boolean) {
            returnValue = (Boolean) object;
        } else if ( object instanceof Number) {
            returnValue = convertToBoolean((Number) object);
        } else if (object != null) {
            returnValue = convertToBoolean(object.toString());
        }
        return returnValue;
    }

    /**
     * Method converts the numeric value to a BigDecimal
     * @param object If instance of number - it's converted to a big decimal
     * @return BigDecimal for the numeric value or null
     */
    private BigDecimal convertToBigDecimal(Object object) {
        if (object instanceof Number) {
            Number number = (Number) object;
            return new BigDecimal(number.doubleValue());
        }
        return null;
    }

    /**
     * If it's a number that is 0 it's false otherwise it's true
     * @param number Number to convert to boolean
     * @return Boolean true if non 0
     */
    private Boolean convertToBoolean(Number number) {
        if (number.doubleValue() != 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Method will evaluate if the lowercased trimmed string is equal to true or false. If so it will return the Boolean equivalent. If not it will return null
     * @param string string to compare
     * @return Boolean representing the string passed in or null
     */
    private Boolean convertToBoolean(String string) {
        if (string == null) {
            return null;
        }
        String trimmedString = string.trim().toLowerCase();
        if (trimmedString.equals("true")) {
            return Boolean.TRUE;
        } else if (trimmedString.equals("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    private LocalDate convertToDate(Object object) {
        if (object instanceof Date) {
            return LocalDate.ofInstant(((Date) object).toInstant(), ZoneId.systemDefault());
        } if (object instanceof LocalDate) {
            return (LocalDate) object;
        }
        return null;
    }
}
