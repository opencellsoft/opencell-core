package org.meveo.service.communication.impl;

import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.Map;

@Stateless
public class InternationalSettingsService extends BusinessService<EmailTemplate> {

    private static final String SUBJECT = "subject";
    private static final String TEXT_CONTENT = "textContent";
    private static final String HTML_TEXT_CONTENT = "htmlContent";
    public String getEmailFieldInTargetLanguage(String targetField, String targetLanguage, EmailTemplate emailTemplate) {
        String translatedField = "";
        Map<String,String> translations = getTranslations(targetField, emailTemplate);


        return translatedField;

    }

    private static Map<String, String> getTranslations(String targetField, EmailTemplate emailTemplate) {
        Map<String, String> translations = Collections.emptyMap();

        switch (targetField) {
            case SUBJECT:
                translations = emailTemplate.getTranslatedSubject();
                break;
            case TEXT_CONTENT:
                translations = emailTemplate.getTranslatedTextContent();
                break;
            case HTML_TEXT_CONTENT:
                translations = emailTemplate.getTranslatedHtmlContent();
                break;
        }
        return translations;
    }
}
