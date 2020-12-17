package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay.
 * @version 10.0
 */
public enum ProductStatusEnum {

	/**
	 * status of draft. <br />
	 * DRAFT = 0 
	 */
	DRAFT("DRAFT"),
	/**
	 * status of ACTIVE. <br />
	 * ACTIVE = 1 
	 */
	ACTIVE("ACTIVE"),
	/**
	 * status of CLOSED. <br />
	 * CLOSED = 2 
	 */
	CLOSED("CLOSED");
	
	private ProductStatusEnum(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param value
	 * @return current product status name  by its value
	 */
	public static Optional<ProductStatusEnum> getCurrentStatus(String value) {
		return Stream.of(ProductStatusEnum.values()).filter(v -> v.value.equalsIgnoreCase(value)).findFirst();
	}
}
