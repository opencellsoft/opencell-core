package org.meveo.model.cpq.enums;

import java.util.Optional;

import java.util.stream.Stream;


/**
 * 
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay.
 * @version 10.0
 */

public enum OperatorLogicEnum {

	GREAT_THAN(0),
	LESS_THAN(1),
	EQUAL(2),
	DIFFERENT_FROM(3),
	EXIST(4);
	
	private OperatorLogicEnum(int value) {
		this.value = value;
	}
	
	private int value;
	
	public int getValue() {
		return this.value;
	}

	public Optional<OperatorLogicEnum> getCurrentStatus(int value) {
		return Stream.of(OperatorLogicEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
