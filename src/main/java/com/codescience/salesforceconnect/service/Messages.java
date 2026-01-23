package com.codescience.salesforceconnect.service;

/**
 * Class contains messages that can be localized
 */
public class Messages {

    public static final String SERVER_ERROR = "Server Error occurred in ODataServlet";
    public static final String NOT_SUPPORTED = "Not supported";
    public static final String ATOM_ID_CREATION_ERROR = "Unable to create (Atom) id for entity:";
    public static final String ERROR_CREATING_ENTITY = "Exception occurred creating Entity:";
    public static final String ERROR_UPDATING_ENTITY = "Exception occurred creating Entity:";
    public static final String ERROR_READING_ENTITY = "Exception occurred reading Entity:";
    public static final String ERROR_MERGING_ENTITIES = "Exception occurred merging Entities:";
    public static final String ERROR_RESPONSE_EDM_NOT_SET = "ResponseEDMEntitySet not found";
    public static final String ERROR_ONLY_ENTITY_SET = "Only EntitySet is supported";
    public static final String ERROR_ENTITY_ALREADY_EXISTS = "This entity already exists";
    public static final String ERROR_ENTITY_NOT_FOUND_FOR_UPDATE = "This entity was not found for update";
    public static final String ERROR_ENTITY_NOT_FOUND = "Entity not found";
    public static final String ERROR_OCCURRED_READ_ENTITY_COLLECTION = "Error Occurred in readEntityCollection: ";
    public static final String ERROR_INVALID_VALUE_FOR_SKIP = "Invalid value for $skip";
    public static final String ERROR_INVALID_VALUE_FOR_TOP = "Invalid value for $top";
    public static final String ERROR_ENTITY_TYPE_NOT_FOUND = "Entity type for Entity passed in not found";
    public static final String INVALID_BENEFICIARY_PERCENT = "The value passed in for the Beneficiary Percent must be between 0 and 100";

    /**
     * Default private constructor prevents instantiation
     */
    private Messages() {}
}
