package org.meveo.sms;

public class SMSProviderInfo {

    private String providerName;
    private String to;
    private String message;

    public SMSProviderInfo(String providerName, String to, String message) {
        this.providerName = providerName;
        this.to = to;
        this.message = message;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}