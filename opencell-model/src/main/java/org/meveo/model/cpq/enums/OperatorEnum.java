package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay.
 * @version 10.0
 */
public enum OperatorEnum {

	AND("AND"), 
	OR("OR");
	
	private OperatorEnum(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return this.value;
	}

	public Optional<OperatorEnum> getCurrentStatus(String value) {
		return Stream.of(OperatorEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
