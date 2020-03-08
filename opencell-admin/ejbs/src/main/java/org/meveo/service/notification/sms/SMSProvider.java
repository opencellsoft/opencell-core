package org.meveo.service.notification.sms;

import com.twilio.rest.api.v2010.account.Message;

public interface SMSProvider {

    Message send(SMSProviderInfo sms);
}