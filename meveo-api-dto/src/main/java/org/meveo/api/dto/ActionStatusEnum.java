package org.meveo.api.dto;

/**
 * Tells whether the request is successful or not.
 * 
 * @author Edward P. Legaspi
 **/
public enum ActionStatusEnum {
    /**
     * Request is ok. No error found.
     */
    SUCCESS,

    /**
     * Request failed. See ActionStatus.errorCode for an error code.
     */
    FAIL
}
