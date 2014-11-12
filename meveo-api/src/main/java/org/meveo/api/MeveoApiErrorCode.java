package org.meveo.api;

/**
 * @author Edward P. Legaspi
 * @since Nov 9, 2013
 **/
public class MeveoApiErrorCode {

	// General API Errors 1xxx
	public static int ENTITY_DOES_NOT_EXISTS_EXCEPTION = 1000;
	public static int ENTITY_ALREADY_EXISTS_EXCEPTION = 1001;

	// Validation Errors 2xxx
	/**
	 * Missing Parameter.
	 */
	public static int MISSING_PARAMETER = 2000;

	/**
	 * Invalid Parameter.
	 */
	public static int INVALID_PARAMETER = 2001;
	
	public static int GENERIC_API_EXCEPTION = 9000;

}
