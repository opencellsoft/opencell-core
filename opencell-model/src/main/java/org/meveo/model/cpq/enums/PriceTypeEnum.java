package org.meveo.model.cpq.enums;

public enum PriceTypeEnum {

	RECURRENT(0),
	ONE_SHOT(1),
	USAGE(3);
	
	private int value;
	
	private PriceTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
