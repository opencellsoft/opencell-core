package org.meveo.api.dto.cpq;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Rachid-AY.
 * @version 11.0
 */
public enum EntityTypeEnum {
	
	/**
	 * Offer
	 */
	OFFER(0),

	/**
	 * Product
	 */
	PRODUCT(1),
	
	/**
	 * Attribute
	 */
	ATTRIBUTE(2);
	
	
	private EntityTypeEnum(int value) {
		this.value = value;
	}
	
	private int value;
	
	public int getValue() {
		return this.value;
	}
	
	public Optional<EntityTypeEnum> getCurrentRule(int value) {
		return Stream.of(EntityTypeEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
