package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Tarik F.
 * @author Mbarek-Ay.
 * @version 10.0
 */
public enum RuleTypeEnum {

	/**
	 * Prérequis
	 */
	PRE_REQUISITE(0),
	
	/**
	 * incompatibilité
	 */
	INCOMPATIBILITY(1);
	
	private RuleTypeEnum(int value) {
		this.value = value;
	}
	
	private int value;
	
	public int getValue() {
		return this.value;
	}
	
	public Optional<RuleTypeEnum> getCurrentRule(int value) {
		return Stream.of(RuleTypeEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
