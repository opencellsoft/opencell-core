package org.meveo.api;

/**
 * @author Edward P. Legaspi
 * @since Nov 9, 2013
 **/
public class MeveoApiErrorCode {
	// General API Errors 1xxx
	public static int TRADING_COUNTRY_DOES_NOT_EXISTS = 1000;

	public static int TRADING_CURRENCY_DOES_NOT_EXISTS = 1001;

	public static int PARENT_SELLER_DOES_NOT_EXISTS = 1002;

	// Validation Errors 2xxx
	/**
	 * Missing Parameter.
	 */
	public static int MISSING_PARAMETER = 2000;

	/**
	 * Invalid Parameter.
	 */
	public static int INVALID_PARAMETER = 2001;

	/**
	 * Organization already exists.
	 */
	public static int ORGANIZATION_ALREADY_EXISTS = 3000;

}
