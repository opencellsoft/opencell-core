package org.meveo.service.communication.impl;

import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import java.util.Map;

@Stateless
public class InternationalSettingsService extends BusinessService<EmailTemplate> {


    public String resolveSubject(EmailTemplate emailTemplate, String languageCode) {

        return emailTemplate.getTranslatedSubject().entrySet().stream().filter(
                        translation -> translation.getKey().equals(languageCode))
                .map(Map.Entry::getValue).findFirst().orElse(emailTemplate.getSubject());

    }

    public String resolveEmailContent(EmailTemplate emailTemplate, String languageCode) {
        return emailTemplate.getTranslatedTextContent().entrySet().stream().filter(
                        translation -> translation.getKey().equals(languageCode))
                .map(Map.Entry::getValue).findFirst().orElse(emailTemplate.getTextContent());
    }

    public String resolveHtmlContent(EmailTemplate emailTemplate, String languageCode) {
        return emailTemplate.getTranslatedHtmlContent().entrySet().stream().filter(
                        translation -> translation.getKey().equals(languageCode))
                .map(Map.Entry::getValue).findFirst().orElse(emailTemplate.getHtmlContent());
    }
}
