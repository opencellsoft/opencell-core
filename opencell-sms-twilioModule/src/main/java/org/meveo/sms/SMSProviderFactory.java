package org.meveo.sms;

import static org.meveo.commons.utils.ParamBean.getInstance;

import org.meveo.service.notification.sms.SMSGateWay;

public class SMSProviderFactory {

    public static SMSGateWay create() {
        Configuration configuration = loadTwilioConfig();
        return new TwilioProvider(configuration);
    }

    private static Configuration loadTwilioConfig() {
        String accountSid = getInstance().getProperty("twilio.account.sid",
                "AC6f01746d5c14fa05fb60975c46aa09e3");
        String token = getInstance().getProperty("twilio.auth.token", "df00b21b07f46f763c6d0da154d03fb7");
        String from = getInstance().getProperty("twilio.phoneNumberFrom", "+14242342601");
        return new Configuration(accountSid, from, token);
    }
}