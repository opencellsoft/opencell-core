package org.meveo.model.cpq.enums;

public enum OneShotTypeEnum {

	DIVERS(0),
	SOUSCRIPTION(1),
	RESILIASATION(2);
	
	private int value;
	
	private OneShotTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
