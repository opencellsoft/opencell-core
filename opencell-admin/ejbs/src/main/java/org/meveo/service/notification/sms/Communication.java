package org.meveo.service.notification.sms;

public class Communication {

    private String code;
    private String to;
    private String message;
    private String targetType;

    public Communication(String code, String to, String message, String targetType) {
        this.code = code;
        this.to = to;
        this.message = message;
        this.targetType = targetType;
    }

    public String getCode() {
        return code;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String getTargetType() {
        return targetType;
    }
}