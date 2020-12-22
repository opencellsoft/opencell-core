package org.meveo.model.cpq.enums;

public enum PriceTypeEnum {

	 RECURRING("recurring"), ONE_SHOT("oneShot"), USAGE("usage");
	
	private String value;
	
	private PriceTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
