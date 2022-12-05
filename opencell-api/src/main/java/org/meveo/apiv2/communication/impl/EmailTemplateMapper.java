package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.api.dto.communication.TranslatedHtmlContentDto;
import org.meveo.api.dto.communication.TranslatedSubjectDto;
import org.meveo.api.dto.communication.TranslatedTextContentDto;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailTemplateMapper {

    public static final String IS_NOT_SUPPORTED_BY_THE_PROVIDER = " is not supported by the provider.";
    public static final String LANGUAGE = "Language ";
    @Inject
    TradingLanguageService tradingLanguageService;

    public static EmailTemplateDto toEmailTemplateDto(EmailTemplate emailTemplate) {
        EmailTemplateDto emailTemplateDto = new EmailTemplateDto();

        emailTemplateDto.setId(emailTemplate.getId());
        emailTemplateDto.setSubject(emailTemplate.getSubject());
        emailTemplateDto.setCode(emailTemplate.getCode());
        emailTemplateDto.setHtmlContent(emailTemplate.getHtmlContent());
        emailTemplateDto.setSubject(emailTemplate.getSubject());
        emailTemplateDto.setTextContent(emailTemplate.getTextContent());
        emailTemplateDto.setTranslatedHtmlContent(convertToTranslatedHtmlDto(emailTemplate.getTranslatedHtmlContent()));
        emailTemplateDto.setTranslatedSubject(convertToTranslatedSubjectDto(emailTemplate.getTranslatedSubject()));
        emailTemplateDto.setTranslatedTextContent(convertToTranslatedTextDto(emailTemplate.getTranslatedTextContent()));

        return emailTemplateDto;
    }

    private static List<TranslatedTextContentDto> convertToTranslatedTextDto(Map<String, String> textTranslationsMap) {

        List<TranslatedTextContentDto> textTranslations = new ArrayList<>();
        for (Map.Entry<String, String> languageTranslation : textTranslationsMap.entrySet()) {
            TranslatedTextContentDto translatedTextContentDto = new TranslatedTextContentDto(languageTranslation.getKey(), languageTranslation.getValue());
            textTranslations.add(translatedTextContentDto);
        }

        return textTranslations;
    }

    private static List<TranslatedSubjectDto> convertToTranslatedSubjectDto(Map<String, String> subjectTranslationsMap) {

        List<TranslatedSubjectDto> subjectTranslations = new ArrayList<>();

        for (Map.Entry<String, String> subjecTranslation : subjectTranslationsMap.entrySet()) {
            TranslatedSubjectDto translatedSubjectDto = new TranslatedSubjectDto(subjecTranslation.getKey(), subjecTranslation.getValue());
            subjectTranslations.add(translatedSubjectDto);
        }

        return subjectTranslations;
    }

    private static List<TranslatedHtmlContentDto> convertToTranslatedHtmlDto(Map<String, String> htmlTranslationsMap) {

        List<TranslatedHtmlContentDto> htmlTranslations = new ArrayList<>();

        for (Map.Entry<String, String> htmlTranslation : htmlTranslationsMap.entrySet()) {
            TranslatedHtmlContentDto translatedHtmlContentDto = new TranslatedHtmlContentDto(htmlTranslation.getKey(), htmlTranslation.getValue());
            htmlTranslations.add(translatedHtmlContentDto);
        }

        return htmlTranslations;
    }


    public EmailTemplate toEntity(EmailTemplateDto emailTemplateDto, EmailTemplate emailTemplate) {

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        if (emailTemplateDto.getSubject() != null) {
            emailTemplate.setSubject(emailTemplateDto.getSubject());
        }
        if (emailTemplateDto.getTextContent() != null) {
            emailTemplate.setTextContent(emailTemplateDto.getTextContent());
        }
        if (emailTemplateDto.getHtmlContent() != null) {
            emailTemplate.setHtmlContent(emailTemplateDto.getHtmlContent());
        }
        if (emailTemplateDto.getTranslatedSubject() != null) {
            emailTemplate.setTranslatedSubject(convertSubjectsToMap(emailTemplateDto.getTranslatedSubject(),supportedLanguages));
        }
        if (emailTemplateDto.getTranslatedHtmlContent() != null) {
            emailTemplate.setTranslatedHtmlContent(convertHtmlTranslationsToMap(emailTemplateDto.getTranslatedHtmlContent(),supportedLanguages));
        }
        if (emailTemplateDto.getTranslatedTextContent() != null) {
            emailTemplate.setTranslatedTextContent(convertTextTranslationsToMap(emailTemplateDto.getTranslatedTextContent(),supportedLanguages));
        }

        return emailTemplate;
    }

    private Map<String, String> convertSubjectsToMap(List<TranslatedSubjectDto> translatedSubjects, List<String> supportedLanguages) {

        if (translatedSubjects == null || translatedSubjects.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> values = new HashMap<>();

        for (TranslatedSubjectDto translatedSubjectDto : translatedSubjects) {
            if (!supportedLanguages.contains(translatedSubjectDto.getLanguageCode())) {
                throw new InvalidParameterException(LANGUAGE + translatedSubjectDto.getLanguageCode() + IS_NOT_SUPPORTED_BY_THE_PROVIDER);
            }
            if (StringUtils.isBlank(translatedSubjectDto.getSubject())) {
                values.remove(translatedSubjectDto.getLanguageCode());
            } else {
                values.put(translatedSubjectDto.getLanguageCode(), translatedSubjectDto.getSubject());
            }
        }

        return values;
    }

    private Map<String, String> convertHtmlTranslationsToMap(List<TranslatedHtmlContentDto> htmlTranslations, List<String> supportedLanguages) {

        if (htmlTranslations == null || htmlTranslations.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> values = new HashMap<>();

        for (TranslatedHtmlContentDto translatedHtmlContentDto : htmlTranslations) {
            if (!supportedLanguages.contains(translatedHtmlContentDto.getLanguageCode())) {
                throw new InvalidParameterException(LANGUAGE + translatedHtmlContentDto.getLanguageCode() + IS_NOT_SUPPORTED_BY_THE_PROVIDER);
            }
            if (StringUtils.isBlank(translatedHtmlContentDto.getHtmlContent())) {
                values.remove(translatedHtmlContentDto.getLanguageCode());
            } else {
                values.put(translatedHtmlContentDto.getLanguageCode(), translatedHtmlContentDto.getHtmlContent());
            }
        }

        return values;
    }

    private Map<String, String> convertTextTranslationsToMap(List<TranslatedTextContentDto> textTranslations, List<String> supportedLanguages) {

        if (textTranslations == null || textTranslations.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> values = new HashMap<>();

        for (TranslatedTextContentDto translatedTextContentDto : textTranslations) {
            if (!supportedLanguages.contains(translatedTextContentDto.getLanguageCode())) {
                throw new InvalidParameterException(LANGUAGE + translatedTextContentDto.getLanguageCode() + IS_NOT_SUPPORTED_BY_THE_PROVIDER);
            }
            if (StringUtils.isBlank(translatedTextContentDto.getTextContent())) {
                values.remove(translatedTextContentDto.getLanguageCode());
            } else {
                values.put(translatedTextContentDto.getLanguageCode(), translatedTextContentDto.getTextContent());
            }
        }

        return values;
    }

    public EmailTemplate fromPatchtoDto(EmailTemplatePatchDto emailTemplatePatchDto, EmailTemplate emailTemplate) {

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        if (emailTemplatePatchDto.getSubject() != null) {
            emailTemplate.setSubject(emailTemplatePatchDto.getSubject());
        }
        if (emailTemplatePatchDto.getTextContent() != null) {
            emailTemplate.setTextContent(emailTemplatePatchDto.getTextContent());
        }
        if (emailTemplatePatchDto.getHtmlContent() != null) {
            emailTemplate.setHtmlContent(emailTemplatePatchDto.getHtmlContent());
        }
        if (emailTemplatePatchDto.getTranslatedSubject() != null) {
            emailTemplate.setTranslatedSubject(convertSubjectsToMap(emailTemplatePatchDto.getTranslatedSubject(), supportedLanguages));
        }
        if (emailTemplatePatchDto.getTranslatedHtmlContent() != null) {
            emailTemplate.setTranslatedHtmlContent(convertHtmlTranslationsToMap(emailTemplatePatchDto.getTranslatedHtmlContent(), supportedLanguages));
        }
        if (emailTemplatePatchDto.getTranslatedTextContent() != null) {
            emailTemplate.setTranslatedTextContent(convertTextTranslationsToMap(emailTemplatePatchDto.getTranslatedTextContent(), supportedLanguages));
        }

        return emailTemplate;
    }
}
