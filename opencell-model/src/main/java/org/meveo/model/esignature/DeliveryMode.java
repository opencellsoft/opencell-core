package org.meveo.model.esignature;

public enum DeliveryMode {
	NONE("none"), EMAIL("email");
	
	private String value;
	
	DeliveryMode(String value){
		this.value = value;
	}
	
	public DeliveryMode getValue(DeliveryMode deliveryMode) {
		return deliveryMode != null ? deliveryMode : NONE;
	}
	
	public String getValue(){
		return this.value;
	}
}
