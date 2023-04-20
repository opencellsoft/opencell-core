package org.meveo.service.communication.impl;

import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.sms.SMSTemplate;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Map;

@Stateless
public class InternationalSettingsService {

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    SMSTemplateService smsTemplateService;


    public String resolveSubject(EmailTemplate emailTemplate, String languageCode) {

        return emailTemplate.getTranslatedSubject() != null ? emailTemplate.getTranslatedSubject().entrySet().stream().filter(
                        translation -> translation.getKey().equals(languageCode))
                .map(Map.Entry::getValue).findFirst().orElse(emailTemplate.getSubject()) : emailTemplate.getSubject();

    }

    public String resolveEmailContent(EmailTemplate emailTemplate, String languageCode) {
        return emailTemplate.getTranslatedTextContent() != null ? emailTemplate.getTranslatedTextContent().entrySet().stream().filter(
                        translation -> translation.getKey().equals(languageCode))
                .map(Map.Entry::getValue).findFirst().orElse(emailTemplate.getTextContent()) : emailTemplate.getTextContent();
    }

    public String resolveHtmlContent(EmailTemplate emailTemplate, String languageCode) {
        return emailTemplate.getTranslatedHtmlContent() != null ? emailTemplate.getTranslatedHtmlContent().entrySet().stream().filter(
                        translation -> translation.getKey().equals(languageCode))
                .map(Map.Entry::getValue).findFirst().orElse(emailTemplate.getHtmlContent()) : emailTemplate.getHtmlContent();
    }

    public EmailTemplate update(EmailTemplate emailTemplate) {

        return emailTemplateService.update(emailTemplate);
    }

    public SMSTemplate createSMSTemplate(SMSTemplate smsTemplate) {

        return smsTemplateService.createSMSTemplate(smsTemplate);
    }

    public SMSTemplate updateSMSTemplate(SMSTemplate smsTemplate) {

        return smsTemplateService.update(smsTemplate);
    }
}
