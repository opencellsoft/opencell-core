package org.meveo.model.rating;

public enum EDRRejectReasonEnum {

    SUBSCRIPTION_IS_NULL("SUBSCRIPTION_IS_NULL"), NO_MATCHING_CHARGE("NO_MATCHING_CHARGE"), SUBSCRIPTION_HAS_NO_CHARGE("SUBSCRIPTION_HAS_NO_CHARGE"), QUANTITY_IS_NULL(
            "QUANTITY_IS_NULL"), NO_PRICEPLAN("NO_PRICEPLAN"), NO_TAX("NO_TAX"), RATING_SCRIPT_EXECUTION_ERROR(
                    "RATING_SCRIPT_EXECUTION_ERROR"), PRICE_EL_ERROR("PRICE_EL_ERROR"), INSUFFICIENT_BALANCE(
                            "INSUFFICIENT_BALANCE"), CHARGING_EDR_ON_REMOTE_INSTANCE_ERROR("CHARGING_EDR_ON_REMOTE_INSTANCE_ERROR"), WALLET_NOT_FOUND("WALLET_NOT_FOUND");

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
