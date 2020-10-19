package org.meveo.model.cpq.enums;

import java.util.Optional;

import java.util.stream.Stream;

/**
 * 
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay.
 * @version 10.0
 */

public enum MediaTypeEnum {

	IMAGE(0),
	VIDEO(1);
	
	private int value;
	
	private MediaTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public Optional<MediaTypeEnum> getCurrentStatus(int value) {
		return Stream.of(MediaTypeEnum.values()).filter(v -> v.value == value).findFirst();
	}

	
}
