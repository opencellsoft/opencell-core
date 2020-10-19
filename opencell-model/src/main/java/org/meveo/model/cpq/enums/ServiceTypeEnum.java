package org.meveo.model.cpq.enums;

/**
 * 
 * @author Khairi
 * @version 10.0
 */
public enum ServiceTypeEnum {

	/** No value to enter, a message entered during the configuration of the service (for example a secondary description) 
	 * is available to be used in the CPQ or in the estimate **/
	INFO,
	
	/**  List of text values: Choice of a value from a predefined list **/
	TEXT_LIST,
	
	/** List of numerical values: choice of a value among a list of numbers **/
	NUMERIC_LIST,
	
	/** Text value: Entering a text **/
	TEXT,
	
	/** Numeric value: Entry of a number **/
	NUMERIC,
	
	/** Email format **/
	EMAIL,
	
	/** phone number format **/
	PHONE;
}
