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
	DRAFT(0),
	/**
	 * status of publied. <br />
	 * publied = 1 
	 */
	PUBLISHED(1),
	/**
	 * status of CLOSED. <br />
	 * CLOSED = 2 
	 */
	CLOSED(2);
	
	private VersionStatusEnum(int value) {
		this.value = value;
	}
	
	private int value;
	
	public int getValue() {
		return this.value;
	}
	
	public Optional<VersionStatusEnum> getCurrentStatus(int value) {
		return Stream.of(VersionStatusEnum.values()).filter(v -> v.value == value).findFirst();
	}
}
