package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.communication.impl.TranslationsUtils;

import javax.inject.Inject;

public class EmailTemplateMapper {

    @Inject
    private TranslationsUtils translationUtils;

    public static EmailTemplateDto toEmailTemplateDto(EmailTemplate emailTemplate) {

        EmailTemplateDto emailTemplateDto = new EmailTemplateDto();

        emailTemplateDto.setId(emailTemplate.getId());
        emailTemplateDto.setSubject(emailTemplate.getSubject());
        emailTemplateDto.setCode(emailTemplate.getCode());
        emailTemplateDto.setHtmlContent(emailTemplate.getHtmlContent());
        emailTemplateDto.setSubject(emailTemplate.getSubject());
        emailTemplateDto.setTextContent(emailTemplate.getTextContent());

        if (emailTemplate.getTranslatedHtmlContent() != null) {
            emailTemplateDto.setTranslatedHtmlContent(TranslationsUtils.convertToTranslatedHtmlDto(emailTemplate.getTranslatedHtmlContent()));
        }
        if (emailTemplate.getTranslatedSubject() != null) {
            emailTemplateDto.setTranslatedSubject(TranslationsUtils.convertToTranslatedSubjectDto(emailTemplate.getTranslatedSubject()));
        }
        if (emailTemplate.getTranslatedTextContent() != null) {
            emailTemplateDto.setTranslatedTextContent(TranslationsUtils.convertToTranslatedTextDto(emailTemplate.getTranslatedTextContent()));
        }

        return emailTemplateDto;
    }

    public EmailTemplate toEntity(EmailTemplateDto emailTemplateDto, EmailTemplate emailTemplate) {

        if (emailTemplate == null) {
            emailTemplate = new EmailTemplate();
        }
        if (emailTemplateDto.getCode() != null) {
            emailTemplate.setCode(emailTemplateDto.getCode());
        }
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
            emailTemplate.setTranslatedSubject(translationUtils.convertSubjectsToMap(emailTemplateDto.getTranslatedSubject()));
        }
        if (emailTemplateDto.getTranslatedHtmlContent() != null) {
            emailTemplate.setTranslatedHtmlContent(translationUtils.convertHtmlTranslationsToMap(emailTemplateDto.getTranslatedHtmlContent()));
        }
        if (emailTemplateDto.getTranslatedTextContent() != null) {
            emailTemplate.setTranslatedTextContent(translationUtils.convertTextTranslationsToMap(emailTemplateDto.getTranslatedTextContent()));
        }

        return emailTemplate;
    }

    public EmailTemplate fromPatchDtoToEntity(EmailTemplatePatchDto emailTemplatePatchDto, EmailTemplate emailTemplate) {

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
            emailTemplate.setTranslatedSubject(translationUtils.convertSubjectsToMap(emailTemplatePatchDto.getTranslatedSubject()));
        }
        if (emailTemplatePatchDto.getTranslatedHtmlContent() != null) {
            emailTemplate.setTranslatedHtmlContent(translationUtils.convertHtmlTranslationsToMap(emailTemplatePatchDto.getTranslatedHtmlContent()));
        }
        if (emailTemplatePatchDto.getTranslatedTextContent() != null) {
            emailTemplate.setTranslatedTextContent(translationUtils.convertTextTranslationsToMap(emailTemplatePatchDto.getTranslatedTextContent()));
        }

        return emailTemplate;
    }
}
