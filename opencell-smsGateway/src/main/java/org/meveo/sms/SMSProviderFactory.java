package org.meveo.sms;

import static java.lang.String.format;
import static org.meveo.commons.utils.ParamBean.getInstance;

public class SMSProviderFactory {

    public static final String TWILIO_PREFIX = "twilio";

    public SMSProvider create(SMSProviderInfo providerInfo) {
        if (providerInfo.getProviderName().equalsIgnoreCase(TWILIO_PREFIX) ||
                providerInfo.getProviderName().equalsIgnoreCase("")) {
            return loadTwilioConfig();
        }
        throw new IllegalArgumentException(format("No configuration for provider name: %s", providerInfo.getProviderName()));
    }

    private SMSProvider loadTwilioConfig() {
        String accountSid = getInstance().getProperty(format("%s.account.sid", TWILIO_PREFIX),
                "AC6f01746d5c14fa05fb60975c46aa09e3");
        String token = getInstance().getProperty(format("%s.auth.token", TWILIO_PREFIX),
                "df00b21b07f46f763c6d0da154d03fb7");
        String from = getInstance().getProperty(format("%s.phoneNumberFrom", TWILIO_PREFIX),
                "+14242342601");
        return new TwilioProvider(accountSid, token, from);
    }
}