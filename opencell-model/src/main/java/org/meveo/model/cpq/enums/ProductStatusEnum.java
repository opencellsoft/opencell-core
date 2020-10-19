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
	DRAFT(0),
	/**
	 * status of ACTIVE. <br />
	 * ACTIVE = 1 
	 */
	ACTIVE(1),
	/**
	 * status of CLOSED. <br />
	 * CLOSED = 2 
	 */
	CLOSED(2);
	
	private ProductStatusEnum(int value) {
		this.value = value;
	}
	
	private int value;
	
	public int getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param value
	 * @return current product status name  by its value
	 */
	public Optional<ProductStatusEnum> getCurrentStatus(int value) {
		return Stream.of(ProductStatusEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
