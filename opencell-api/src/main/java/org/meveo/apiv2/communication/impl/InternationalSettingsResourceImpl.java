package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.api.dto.communication.sms.SMSTemplateDto;
import org.meveo.apiv2.communication.InternationalSettingsResource;
import org.meveo.apiv2.communication.service.InternationalSettingsApiService;

import javax.ejb.Stateless;
import javax.inject.Inject;


@Stateless
public class InternationalSettingsResourceImpl implements InternationalSettingsResource {

    @Inject
    InternationalSettingsApiService internationalSettingsApiService;

    @Override
    public EmailTemplateDto update(String emailTemplateCode, EmailTemplateDto emailTemplateDto) {

        return internationalSettingsApiService.checkAndUpdateEmailTemplate(emailTemplateCode,emailTemplateDto);
    }

    @Override
    public EmailTemplateDto partialUpdate(String emailTemplateCode, EmailTemplatePatchDto emailTemplatePatchDto) {

        return internationalSettingsApiService
                .checkAndUpdateEmailTemplate(emailTemplateCode, emailTemplatePatchDto);

    }

    public SMSTemplateDto create(SMSTemplateDto smsTemplateDto) {

        return internationalSettingsApiService
                .checkAndCreateSMSTemplate(smsTemplateDto);
    }

    @Override
    public SMSTemplateDto update(String smsTemplateCode, SMSTemplateDto smsTemplateDto) {
        return internationalSettingsApiService
                .checkAndUpdateSMSTemplate(smsTemplateCode, smsTemplateDto);
    }


}
