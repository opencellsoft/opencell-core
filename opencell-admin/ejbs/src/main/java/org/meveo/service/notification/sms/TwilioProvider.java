package org.meveo.service.notification.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.meveo.api.exception.MeveoApiException;

public class TwilioProvider implements SMSProvider {

    private String accountSid;
    private String from;
    private String token;

    public TwilioProvider(String accountSid, String token, String from) {
        this.accountSid = accountSid;
        this.token = token;
        this.from = from;
    }

    @Override
    public Message send(SMSProviderInfo sms) {
        try {
            Twilio.init(accountSid, token);
            Message message = Message
                    .creator(new PhoneNumber(sms.getTo()), new PhoneNumber(from), sms.getMessage())
                    .create();
            return message;
        } catch (Exception e) {
            throw new MeveoApiException(e.getMessage());
        }
    }
}