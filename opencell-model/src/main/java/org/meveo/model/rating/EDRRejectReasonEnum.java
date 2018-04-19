package org.meveo.model.rating;

public enum EDRRejectReasonEnum {
	
    SUBSCRIPTION_IS_NULL ("SUBSCRIPTION_IS_NULL"), NO_MATCHING_CHARGE ("NO_MATCHING_CHARGE"), SUBSCRIPTION_HAS_NO_CHARGE ("SUBSCRIPTION_HAS_NO_CHARGE"), 
    GENERAL_ERROR ("GENERAL_ERROR"), NULL_QUANTITY("NULL_QUANTITY"), NO_PRICEPLAN ("NO_PRICEPLAN") ;
	
	private String code;

	private EDRRejectReasonEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}


	public String toString() {
		return name();
	}

}
