package org.meveo.api;

/**
 * Action status error codes. See ActionStatus.message for a detailed error message
 * 
 * @author Andrius Karpavicius
 **/

public enum MeveoApiErrorCodeEnum {

    /**
     * Entity on which action should be performed or referenced, was not found.
     */
    ENTITY_DOES_NOT_EXISTS_EXCEPTION,

    /**
     * Entity with an identical code, or some other unique identifiers was found and should be updated instead.
     */
    ENTITY_ALREADY_EXISTS_EXCEPTION,

    /**
     * Unable to delete an entity as it is referenced from other entities.
     */
    DELETE_REFERENCED_ENTITY_EXCEPTION,

    /**
     * Missing a required parameter or field value.
     */
    MISSING_PARAMETER,

    /**
     * Invalid parameter or field value passed
     */
    INVALID_PARAMETER,

    /**
     * Parameter or field value does not correspond to a valid Enum value option
     */
    INVALID_ENUM_VALUE,

    /**
     * Access with such code and subscription already exists
     */
    DUPLICATE_ACCESS,

    /**
     * Insufficient balance to perform operation
     */
    INSUFFICIENT_BALANCE,

    /**
     * A general exception encountered
     */
    GENERIC_API_EXCEPTION,

    /**
     * A business exception encountered
     */
    BUSINESS_API_EXCEPTION,

    /**
     * Were not able to authenticate with given user credentials, or user does not have a required permission
     */
    AUTHENTICATION_AUTHORIZATION_EXCEPTION,

    /**
     * Action was not allowed to be performed
     */
    ACTION_FORBIDDEN,
    
    /**
     * Could be a wrong content type or invalid image byte[].
     */
    INVALID_IMAGE_DATA;
}
