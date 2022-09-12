package org.meveo.model.cpq.enums;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
	POURCENTAGE("POURCENTAGE");


	private PriceVersionTypeEnum(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return this.value;
	}

}
