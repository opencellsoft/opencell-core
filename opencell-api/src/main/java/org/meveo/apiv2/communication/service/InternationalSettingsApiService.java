package org.meveo.apiv2.communication.service;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.api.dto.communication.sms.SMSTemplateDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.communication.impl.EmailTemplateMapper;
import org.meveo.apiv2.communication.impl.SMSTemplateMapper;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.sms.SMSTemplate;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.communication.impl.InternationalSettingsService;
import org.meveo.service.communication.impl.SMSTemplateService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class InternationalSettingsApiService  {

    @Inject
    InternationalSettingsService internationalSettingsService;

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    EmailTemplateMapper emailTemplateMapper;

    @Inject
    SMSTemplateMapper smsTemplateMapper;

    @Inject
    SMSTemplateService smsTemplateService;


    public EmailTemplate update(EmailTemplate emailTemplate) {
        return internationalSettingsService.update(emailTemplate);
    }

    public EmailTemplateDto checkAndUpdate(String emailTemplateCode, EmailTemplateDto emailTemplateDto) {

        EmailTemplate emailTemplate = checkEmailTemplate(emailTemplateCode);
        return EmailTemplateMapper.toEmailTemplateDto(update(emailTemplateMapper.toEntity(emailTemplateDto, emailTemplate)));

    }

    public EmailTemplateDto checkAndUpdate(String emailTemplateCode, EmailTemplatePatchDto emailTemplatePatchDto) {

        EmailTemplate emailTemplate = checkEmailTemplate(emailTemplateCode);
        return EmailTemplateMapper.toEmailTemplateDto(update(emailTemplateMapper.fromPatchDtoToEntity(emailTemplatePatchDto, emailTemplate)));

    }

    private EmailTemplate checkEmailTemplate(String emailTemplateCode) {

        EmailTemplate emailTemplate = emailTemplateService.findByCode(emailTemplateCode);

        if (emailTemplate == null) {
            throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
        }
        return emailTemplate;
    }

    public SMSTemplateDto checkAndCreateSMSTemplate(SMSTemplateDto smsTemplateDto) {

        checkSMSTemplateInput(smsTemplateDto);

        return smsTemplateMapper.fromEntityToDto(smsTemplateService.createAndReturnEntity(smsTemplateMapper.fromDtoToEntity(smsTemplateDto)));

    }

    private void checkSMSTemplateInput(SMSTemplateDto smsTemplateDto) {
        if(smsTemplateDto.getCode() == null || smsTemplateDto.getCode().isEmpty()){
            throw new BusinessException("the code is invalid");
        }

        SMSTemplate smsTemplate = smsTemplateService.findByCode(smsTemplateDto.getCode());
        if (smsTemplate != null) {
            throw new BusinessException("The SMS template already exists");
        }
    }
}
