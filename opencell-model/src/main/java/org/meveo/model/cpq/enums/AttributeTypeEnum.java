package org.meveo.model.cpq.enums;

/**
 * 
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
public enum AttributeTypeEnum {

	/** No value to enter, a message entered during the configuration of the service (for example a secondary description) 
	 * is available to be used in the CPQ or in the estimate **/
	INFO,
	
	/**  List of text values: Choice of a value from a predefined list **/
	LIST_TEXT,
	/** List of multiple text value: choice of multiple values from a predefined list**/
	LIST_MULTIPLE_TEXT,
	
	/** List of numerical values: choice of a value among a list of numbers **/
	LIST_NUMERIC,
	
	/** List of multiple numerical value: choice of a multiple values among a list of numbers **/
	LIST_MULTIPLE_NUMERIC,
	
	/** Text value: Entering a text **/
	TEXT,
	
	/** Numeric value: Entry of a number **/
	NUMERIC,
	
	/** numeric with predefined decimale **/
	INTEGER,
	
	/** Date type**/
	DATE,
	
	/** choice of calendar of opencell's calendar**/
	CALENDAR,
	
	/** Email format **/
	EMAIL,
	
	/** phone number format **/
	PHONE,
	
	/** display some of list of numerics **/
	TOTAL,
	
	COMPTAGE


}
