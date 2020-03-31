package org.meveo.sms;

import static org.meveo.commons.utils.ParamBean.getInstance;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.meveo.service.notification.sms.MessageResponse;
import org.meveo.service.notification.sms.SMS;
import org.meveo.service.notification.sms.SMSGateWay;

public class TwilioProvider implements SMSGateWay {

    private Configuration config;

    public TwilioProvider() {
        String accountSid = getInstance().getProperty("twilio.account.sid",
                "AC6f01746d5c14fa05fb60975c46aa09e3");
        String token = getInstance().getProperty("twilio.auth.token",
                "df00b21b07f46f763c6d0da154d03fb7");
        String from = getInstance().getProperty("twilio.phoneNumberFrom",
                "+14242342601");
        this.config = new Configuration(accountSid, from, token);
    }

    @Override
    public MessageResponse send(SMS sms) {
        try {
            Twilio.init(config.getAccountSid(), config.getToken());
            Message message = Message
                    .creator(new PhoneNumber(sms.getTo()), new PhoneNumber(config.getFrom()), sms.getMessage())
                    .create();
            return to(message);
        } catch (Exception e) {
            throw new TwilioException(e.getMessage());
        }
    }

    private MessageResponse to(Message message) {
        return new MessageResponse.Builder()
                .withSid(message.getSid())
                .withSentTo(message.getSid())
                .withSentTo(message.getTo())
                .withBody(message.getBody())
                .withStatus(message.getStatus().toString())
                .withPrice(message.getPrice())
                .withErrorCode(message.getErrorCode())
                .withErrorMessage(message.getErrorMessage())
                .build();
    }
}