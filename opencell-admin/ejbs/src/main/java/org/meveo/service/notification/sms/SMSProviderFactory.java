package org.meveo.service.notification.sms;

public class SMSProviderFactory {

    public static SMSProvider create(String accountSid, String token, String from) {
        return new TwilioProvider(accountSid, token, from);
    }
}