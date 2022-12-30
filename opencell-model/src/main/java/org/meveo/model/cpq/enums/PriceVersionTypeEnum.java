package org.meveo.model.cpq.enums;

/**
 * 
 * @author Khalid HORRI.
 * @version 14.0
 */
public enum PriceVersionTypeEnum {
	/**
	 * Fixed Price.
	 */
	FIXED("FIXED"),
	/**
	 * A discount percentage.
	 */
	PERCENTAGE("PERCENTAGE");


	private PriceVersionTypeEnum(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return this.value;
	}

}
