package org.meveo.model.cpq.commercial;

public enum InvoiceLineMinAmountTypeEnum {

    IL_MIN_AMOUNT_CUST("IL_MIN_AMOUNT_CUST"),
    IL_MIN_AMOUNT_CA("IL_MIN_AMOUNT_CA"),
    IL_MIN_AMOUNT_BA("IL_MIN_AMOUNT_BA"),
    IL_MIN_AMOUNT_UA("IL_MIN_AMOUNT_UA"),
    IL_MIN_AMOUNT_SU("OL_MIN_AMOUNT_SU"),
    IL_MIN_AMOUNT_SE("IL_MIN_AMOUNT_SE");

    private String code;

    InvoiceLineMinAmountTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}