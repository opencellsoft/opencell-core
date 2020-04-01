package org.meveo.service.notification.sms;

public class Communication {

    private String customerCode;
    private String message;

    public Communication(String customerCode, String message) {
        this.customerCode = customerCode;
        this.message = message;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getMessage() {
        return message;
    }
}