package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Tarik F.
 * @author Mbarek-Ay.
 * @version 10.0
 */
public enum VersionStatusEnum {

	/**
	 * status of draft. <br />
	 * DRAFT = 0 
	 */
	DRAFT("DRAFT"),
	/**
	 * status of publied. <br />
	 * publied = 1 
	 */
	PUBLISHED("PUBLISHED"),
	/**
	 * status of CLOSED. <br />
	 * CLOSED = 2 
	 */
	CLOSED("CLOSED");
	
	private VersionStatusEnum(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return this.value;
	}
	
	public Optional<VersionStatusEnum> getCurrentStatus(String value) {
		return Stream.of(VersionStatusEnum.values()).filter(v -> v.value.equalsIgnoreCase(value)).findFirst();
	}
}
