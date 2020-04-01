package org.meveo.sms;

public class TwilioException extends RuntimeException {

    public TwilioException(String message) {
        super(message);
    }
}