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
	 * Request failed. See error codes here https://www.assembla.com/spaces/meveo/wiki/Error_Codes.
	 */
	FAIL
}
