package org.meveo.service.notification.sms;

public class SMSProviderInfo {

    private String to;
    private String message;

    public SMSProviderInfo(String to, String message) {
        this.to = to;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}