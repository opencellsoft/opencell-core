package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.communication.InternationalSettingsResource;
import org.meveo.apiv2.communication.service.InternationalSettingsApiService;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.communication.impl.EmailTemplateService;

import javax.inject.Inject;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class InternationalSettingsResourceImpl implements InternationalSettingsResource {

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    InternationalSettingsApiService internationalSettingsApiService;

    @Inject
    EmailTemplateMapper emailTemplateMapper;


    @Override
    public EmailTemplateDto update(String emailTemplateCode, EmailTemplateDto emailTemplateDto) {

        EmailTemplate emailTemplate = ofNullable(emailTemplateService.findByCode(emailTemplateCode))
                .orElseThrow(() -> new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode));

        Optional<EmailTemplate> updatedEmailTemplate = internationalSettingsApiService
                .update(emailTemplate.getId(), emailTemplateMapper.toEntity(emailTemplateDto, emailTemplate));

        return updatedEmailTemplate.map(EmailTemplateMapper::toEmailTemplateDto).orElse(null);
    }

    private static ActionStatus buildSucessResponse(Optional<EmailTemplate> emailTemplate) {
        ActionStatus responseStatus = new ActionStatus();
        responseStatus.setStatus(ActionStatusEnum.SUCCESS);
        return responseStatus;
    }

}
