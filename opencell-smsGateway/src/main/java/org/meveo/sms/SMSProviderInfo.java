package org.meveo.sms;

public class SMS {

    private String providerName;
    private String to;
    private String message;

<<<<<<< HEAD:opencell-smsGateway/src/main/java/org/meveo/sms/SMSProviderInfo.java
    public SMSProviderInfo(String providerName, String to, String message) {
        this.providerName = providerName;
        this.to = to;
=======
    public SMS(String customerCode, String message) {
        this.to = customerCode;
>>>>>>> RE #3903 : add SMS gateway:opencell-admin/ejbs/src/main/java/org/meveo/service/notification/sms/SMS.java
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