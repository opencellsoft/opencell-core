package org.meveo.apiv2.communication.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.apiv2.communication.InternationalSettingsResource;
import org.meveo.apiv2.communication.service.InternationalSettingsApiService;
import org.meveo.model.communication.email.EmailTemplate;

import javax.inject.Inject;

import java.util.Optional;


public class InternationalSettingsResourceImpl implements InternationalSettingsResource {

    @Inject
    InternationalSettingsApiService internationalSettingsApiService;

    @Override
    public EmailTemplateDto update(String emailTemplateCode, EmailTemplateDto emailTemplateDto) {

        Optional<EmailTemplate> updatedEmailTemplate = internationalSettingsApiService.checkAndUpdate(emailTemplateCode,emailTemplateDto);
        return updatedEmailTemplate.map(EmailTemplateMapper::toEmailTemplateDto).orElse(null);
    }

    private static ActionStatus buildSucessResponse(Optional<EmailTemplate> emailTemplate) {
        ActionStatus responseStatus = new ActionStatus();
        responseStatus.setStatus(ActionStatusEnum.SUCCESS);
        return responseStatus;
    }

}
