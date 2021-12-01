package org.meveo.service.payments.impl;

public enum PolicyConditionTargetEnum {

    creditCategory("inv.billingAccount.customerAccount.creditCategory.code"),
    customerCategory("inv.billingAccount.customerAccount.customer.customerCategory.code"),
    isCompany("inv.billingAccount.isCompany"),
    paymentMethod("inv.paymentMethod.paymentType");

    private String filed;

    PolicyConditionTargetEnum(String filed) {
        this.filed = filed;
    }

    public String getField() {
        return filed;
    }
}