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

	ET(0), 
	OU(1);
	
	private OperatorEnum(int value) {
		this.value = value;
	}
	
	private int value;
	
	public int getValue() {
		return this.value;
	}

	public Optional<OperatorEnum> getCurrentStatus(int value) {
		return Stream.of(OperatorEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
