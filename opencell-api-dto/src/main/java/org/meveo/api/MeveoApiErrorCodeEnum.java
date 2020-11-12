/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

/**
 * Action
 * status error codes. See ActionStatus.message for a detailed error message
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.2
 **/

public enum MeveoApiErrorCodeEnum implements ApiErrorCodeEnum {

    /**
     * Entity on which action should be performed or referenced, was not found.
     */
    ENTITY_DOES_NOT_EXISTS_EXCEPTION,

    /**
     * Entity on which action should be performed or referenced, is not allowed.
     */
    ENTITY_NOT_ALLOWED_EXCEPTION,

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
     * EDR rejection due to data validation, inconsistency or other rating related failure
     */
    RATING_REJECT,

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
    INVALID_IMAGE_DATA,
    
    /**
     * database constraint violation.
     */
    CONSTRAINT_VIOLATION_EXCEPTION,

    /**
     * Transition not executed: condition is false
     */
    CONDITION_FALSE
}
