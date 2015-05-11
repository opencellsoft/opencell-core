package org.meveo.api;

/**
 * @author Edward P. Legaspi
 * @since Nov 9, 2013
 **/
public class MeveoApiErrorCode {

	// General API Errors 1xxx
	public static String ENTITY_DOES_NOT_EXISTS_EXCEPTION = "ENTITY_DOES_NOT_EXISTS_EXCEPTION";
	public static String ENTITY_ALREADY_EXISTS_EXCEPTION = "ENTITY_ALREADY_EXISTS_EXCEPTION";

	// Validation Errors 2xxx
	/**
	 * Missing Parameter.
	 */
	public static String MISSING_PARAMETER = "MISSING_PARAMETER";

	/**
	 * Invalid Parameter.
	 */
	public static String INVALID_PARAMETER = "INVALID_PARAMETER";
	public static String INVALID_ENUM_VALUE = "INVALID_ENUM_VALUE";
	public static String DUPLICATE_ACCESS = "DUPLICATE_ACCESS";

	// Balance error
	public static String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";

	public static String GENERIC_API_EXCEPTION = "GENERIC_API_EXCEPTION";
	public static String BUSINESS_API_EXCEPTION = "BUSINESS_API_EXCEPTION";

}
