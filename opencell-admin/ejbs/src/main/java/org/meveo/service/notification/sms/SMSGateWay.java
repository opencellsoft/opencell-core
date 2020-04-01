package org.meveo.service.notification.sms;

public interface SMSGateWay {

    MessageResponse send(SMS sms);
}