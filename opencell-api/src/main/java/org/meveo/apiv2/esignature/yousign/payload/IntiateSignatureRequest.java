package org.meveo.apiv2.esignature.yousign.payload;

public class IntiateSignatureRequest {
	
	private String name;
	private String delivery_mode;
	
	public IntiateSignatureRequest() {
	}
	public IntiateSignatureRequest(String name, String delivery_mode) {
		this.name = name;
		this.delivery_mode = delivery_mode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDelivery_mode() {
		return delivery_mode;
	}
	
	public void setDelivery_mode(String delivery_mode) {
		this.delivery_mode = delivery_mode;
	}
}
