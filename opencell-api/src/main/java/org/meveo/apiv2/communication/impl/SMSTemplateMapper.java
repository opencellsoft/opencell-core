package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.communication.sms.SMSTemplateDto;
import org.meveo.model.communication.sms.SMSTemplate;
import org.meveo.service.communication.impl.TranslationsUtils;

import javax.inject.Inject;

public class SMSTemplateMapper {

    @Inject
    private TranslationsUtils translationsUtils;

    public SMSTemplate fromDtoToEntity(SMSTemplateDto smsTemplateDto, SMSTemplate smsTemplate) {

        if (smsTemplate == null) {
            smsTemplate = new SMSTemplate();
        }

        if (smsTemplateDto.getCode() != null) {
            smsTemplate.setCode(smsTemplateDto.getCode());
        }
        if (smsTemplateDto.getTextContent() != null) {
            smsTemplate.setTextContent(smsTemplateDto.getTextContent());
        }
        if (smsTemplateDto.getTranslatedTextContent() != null) {
            smsTemplate.setTranslatedTextContent(translationsUtils.convertTextTranslationsToMap(smsTemplateDto.getTranslatedTextContent()));
        }
        if (smsTemplateDto.getMedia() != null) {
            smsTemplateDto.setMedia(smsTemplate.getMedia());
        }

        return smsTemplate;
    }

    public SMSTemplateDto fromEntityToDto(SMSTemplate smsTemplate) {

        SMSTemplateDto smsTemplateDto = new SMSTemplateDto();
        smsTemplateDto.setId(smsTemplate.getId());
        smsTemplateDto.setCode(smsTemplate.getCode());
        smsTemplateDto.setTextContent(smsTemplate.getTextContent());
        smsTemplateDto.setMedia(smsTemplate.getMedia());
        smsTemplateDto.setTranslatedTextContent(TranslationsUtils.convertToTranslatedTextDto(smsTemplate.getTranslatedTextContent()));

        return smsTemplateDto;
    }
}
