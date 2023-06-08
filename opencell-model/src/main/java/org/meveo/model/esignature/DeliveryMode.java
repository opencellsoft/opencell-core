package org.meveo.model.esignature;

public enum DeliveryMode {
	NONE("none"), EMAIL("email");
	
	private String value;
	
	DeliveryMode(String value){
		this.value = value;
	}
	
	public String getValue(DeliveryMode deliveryMode) {
		return deliveryMode != null ? deliveryMode.value : NONE.value;
	}
	
	public String getValue(){
		return this.value;
	}
}
