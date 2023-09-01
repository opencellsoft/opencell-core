package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.api.dto.communication.sms.SMSTemplateDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.communication.InternationalSettingsResource;
import org.meveo.apiv2.communication.service.InternationalSettingsApiService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;


@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class InternationalSettingsResourceImpl implements InternationalSettingsResource {

    @Inject
    InternationalSettingsApiService internationalSettingsApiService;

    @Override
    public EmailTemplateDto create(EmailTemplateDto emailTemplateDto) {
        return internationalSettingsApiService.checkAndCreateEmailTemplate(emailTemplateDto);
    }

    @Override
    public EmailTemplateDto getEmailTemplate(String emailTemplateCode) {
        return internationalSettingsApiService.checkAndGetEmailTemplate(emailTemplateCode);
    }

    @Override
    public ActionStatus deleteEmailTemplate(String emailTemplateCode) {
        internationalSettingsApiService.checkAndDeleteEmailTemplate(emailTemplateCode);
        return new ActionStatus(ActionStatusEnum.SUCCESS, "Email Template with code " + emailTemplateCode + " was deleted successfully");
    }

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

    @Override
    public ActionStatus delete(String smsTemplateCode) {
        internationalSettingsApiService
                .checkAndDeleteSMSTemplate(smsTemplateCode);

        return new ActionStatus(ActionStatusEnum.SUCCESS, "SMS Template with code " + smsTemplateCode + " was deleted successfully");
    }

    @Override
    public SMSTemplateDto get(String smsTemplateCode) {
        return internationalSettingsApiService
                .checkAndGetSMSTemplate(smsTemplateCode);
    }


}
