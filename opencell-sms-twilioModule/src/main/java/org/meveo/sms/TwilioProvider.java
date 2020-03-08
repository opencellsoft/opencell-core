package org.meveo.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.meveo.service.notification.sms.MessageResponse;
import org.meveo.service.notification.sms.SMS;
import org.meveo.service.notification.sms.SMSGateWay;

public class TwilioProvider implements SMSGateWay {

    private Configuration config;

    public TwilioProvider(Configuration configuration) {
        this.config = configuration;
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