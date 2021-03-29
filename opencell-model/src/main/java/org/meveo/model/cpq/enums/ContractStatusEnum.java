package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/** 
 *  @author Mbarek-Ay.
 * @version 11.0
 */
public enum ContractStatusEnum {
  
	
	DRAFT("DRAFT"),
	
	ACTIVE("ACTIVE"),
	
	CLOSED("CLOSED");
	
	private ContractStatusEnum(String value) {
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
	public static Optional<ContractStatusEnum> getCurrentStatus(String value) {
		return Stream.of(ContractStatusEnum.values()).filter(v -> v.value.equalsIgnoreCase(value)).findFirst();
	}
}
