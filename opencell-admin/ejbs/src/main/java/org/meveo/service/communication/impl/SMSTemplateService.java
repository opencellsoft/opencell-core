package org.meveo.service.communication.impl;

import org.meveo.model.communication.sms.SMSTemplate;
import org.meveo.service.base.BusinessService;

public class SMSTemplateService extends BusinessService<SMSTemplate> {

    public SMSTemplate createSMSTemplate(SMSTemplate smsTemplate) {
        super.create(smsTemplate);
        return smsTemplate;
    }
}


