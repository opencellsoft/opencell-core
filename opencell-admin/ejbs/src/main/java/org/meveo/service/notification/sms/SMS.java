package org.meveo.service.notification.sms;

public class SMS {

    private String to;
    private String message;

    public SMS(String customerCode, String message) {
        this.to = customerCode;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}