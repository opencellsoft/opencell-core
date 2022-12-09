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


    public EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate) {
        return internationalSettingsService.update(emailTemplate);
    }

    public EmailTemplateDto checkAndUpdateEmailTemplate(String emailTemplateCode, EmailTemplateDto emailTemplateDto) {

        EmailTemplate emailTemplate = checkEmailTemplate(emailTemplateCode);
        return EmailTemplateMapper.toEmailTemplateDto(updateEmailTemplate(emailTemplateMapper.toEntity(emailTemplateDto, emailTemplate)));

    }

    public EmailTemplateDto checkAndUpdateEmailTemplate(String emailTemplateCode, EmailTemplatePatchDto emailTemplatePatchDto) {

        EmailTemplate emailTemplate = checkEmailTemplate(emailTemplateCode);
        return EmailTemplateMapper.toEmailTemplateDto(updateEmailTemplate(emailTemplateMapper.fromPatchDtoToEntity(emailTemplatePatchDto, emailTemplate)));

    }

    private EmailTemplate checkEmailTemplate(String emailTemplateCode) {

        EmailTemplate emailTemplate = emailTemplateService.findByCode(emailTemplateCode);

        if (emailTemplate == null) {
            throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
        }
        return emailTemplate;
    }

    private void checkSMSTemplateInput(SMSTemplateDto smsTemplateDto) {
        if (smsTemplateDto.getCode() == null || smsTemplateDto.getCode().isEmpty()) {
            throw new BusinessException("Unrecognized field CODE");
        }

        SMSTemplate smsTemplate = smsTemplateService.findByCode(smsTemplateDto.getCode());
        if (smsTemplate != null) {
            throw new BusinessException("SMSTemplate with code " + smsTemplate.getCode() + " already exists");
        }
    }

    public SMSTemplateDto checkAndCreateSMSTemplate(SMSTemplateDto smsTemplateDto) {

        checkSMSTemplateInput(smsTemplateDto);

        return createSMSTemplate(smsTemplateMapper.fromDtoToEntity(smsTemplateDto, null));

    }

    private SMSTemplateDto createSMSTemplate(SMSTemplate smsTemplate) {

        return smsTemplateMapper.fromEntityToDto(internationalSettingsService.createSMSTemplate(smsTemplate));
    }


    public SMSTemplateDto checkAndUpdateSMSTemplate(String smsTemplateCode, SMSTemplateDto smsTemplateDto) {

        SMSTemplate smsTemplate = checkSMSTemplateToUpdateOrDelete(smsTemplateCode);

        return updateSMSTemplate(smsTemplateMapper.fromDtoToEntity(smsTemplateDto, smsTemplate));

    }

    private SMSTemplateDto updateSMSTemplate(SMSTemplate smsTemplate) {

        return smsTemplateMapper.fromEntityToDto(internationalSettingsService.updateSMSTemplate(smsTemplate));
    }

    private SMSTemplate checkSMSTemplateToUpdateOrDelete(String smsTemplateCode) {
        if (smsTemplateCode == null || smsTemplateCode.isEmpty()) {
            throw new BusinessException("SMS Template Code is invalid");
        }

        SMSTemplate smsTemplate = smsTemplateService.findByCode(smsTemplateCode);
        if (smsTemplate == null) {
            throw new BusinessException("The SMS Template with code " + smsTemplateCode + " does not exist ");
        }
        return smsTemplate;
    }


    public void checkAndDeleteSMSTemplate(String smsTemplateCode) {

        SMSTemplate smsTemplate = checkSMSTemplateToUpdateOrDelete(smsTemplateCode);
        smsTemplateService.remove(smsTemplate);

    }
}
