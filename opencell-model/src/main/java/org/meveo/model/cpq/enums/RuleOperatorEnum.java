package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/** 
 *  @author Mbarek-Ay.
 * @version 10.0
 */
public enum RuleOperatorEnum {

	GREATER_THAN(">"), 
	LESS_THAN("<"),
	EQUAL("="),
	GREATER_THAN_OR_EQUAL(">="), 
	LESS_THAN_OR_EQUAL("<="),
	NOT_EQUAL("!=");
	
	
	
	
	private RuleOperatorEnum(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return this.value;
	}

	public Optional<RuleOperatorEnum> getCurrentStatus(String value) {
		return Stream.of(RuleOperatorEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
