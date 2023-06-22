package org.meveo.apiv2.esignature.yousign.payload;

import com.google.gson.annotations.SerializedName;

public class IntiateSignatureRequest {
	
	private String name;
	@SerializedName("delivery_mode")
	private String deliveryMode;
	
	public IntiateSignatureRequest() {
	}
	public IntiateSignatureRequest(String name, String deliveryMode) {
		this.name = name;
		this.deliveryMode = deliveryMode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDeliveryMode() {
		return deliveryMode;
	}
	
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
}
