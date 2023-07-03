package org.meveo.model.esignature;

public enum DeliveryMode {
	none, email;
	
	
	public String getValue(DeliveryMode deliveryMode) {
		return deliveryMode != null ? deliveryMode.toString() : none.toString();
	}
	
}
