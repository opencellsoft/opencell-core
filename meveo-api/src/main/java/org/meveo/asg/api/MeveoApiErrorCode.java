package org.meveo.asg.api;

/**
 * @author Edward P. Legaspi
 * @since Nov 9, 2013
 **/
public class MeveoApiErrorCode {

	// General API Errors 1xxx
	public static int TRADING_COUNTRY_DOES_NOT_EXISTS = 1000;
	public static int TRADING_COUNTRY_ALREADY_EXISTS = 1001;
	public static int TRADING_CURRENCY_DOES_NOT_EXISTS = 1002;
	public static int TRADING_CURRENCY_ALREADY_EXISTS = 1003;
	public static int TRADING_LANGUAGE_DOES_NOT_EXISTS = 1004;
	public static int TRADING_LANGUAGE_ALREADY_EXISTS = 1005;
	public static int CURRENCY_DOES_NOT_EXISTS = 1006;
	public static int CURRENCY_ALREADY_EXISTS = 1007;
	public static int COUNTRY_DOES_NOT_EXISTS = 1008;
	public static int COUNTRY_ALREADY_EXISTS = 1009;
	public static int LANGUAGE_DOES_NOT_EXISTS = 1010;
	public static int LANGUAGE_ALREADY_EXISTS = 1011;

	// Validation Errors 2xxx
	/**
	 * Missing Parameter.
	 */
	public static int MISSING_PARAMETER = 2000;

	/**
	 * Invalid Parameter.
	 */
	public static int INVALID_PARAMETER = 2001;

	// Sellers
	public static int SELLER_ALREADY_EXISTS = 3000;
	public static int PARENT_SELLER_DOES_NOT_EXISTS = 3002;
	public static int SELLER_DOES_NOT_EXISTS = 3003;

	// Offer and Service Templates
	public static int SERVICE_TEMPLATE_ALREADY_EXISTS = 4000;
	public static int SERVICE_TEMPLATE_DOES_NOT_EXISTS = 4001;
	public static int OFFER_TEMPLATE_ALREADY_EXISTS = 4100;
	public static int OFFER_TEMPLATE_DOES_NOT_EXISTS = 4101;

}
