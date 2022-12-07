package org.meveo.apiv2.communication.service;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.communication.impl.EmailTemplateMapper;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.communication.impl.InternationalSettingsService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InternationalSettingsApiService implements ApiService<EmailTemplate> {

    @Inject
    InternationalSettingsService internationalSettingsService;

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    EmailTemplateMapper emailTemplateMapper;

    @Override
    public List<EmailTemplate> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return Collections.emptyList();
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<EmailTemplate> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public EmailTemplate create(EmailTemplate baseEntity) {
        return null;
    }

    @Override
    public Optional<EmailTemplate> update(Long id, EmailTemplate emailTemplate) {
        internationalSettingsService.update(emailTemplate);
        return Optional.of(emailTemplate);
    }

    public Optional<EmailTemplate> checkAndUpdate(String emailTemplateCode, EmailTemplateDto emailTemplateDto) {

        EmailTemplate emailTemplate = emailTemplateService.findByCode(emailTemplateCode);

        if (emailTemplate == null) {
            throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
        }
        return update(emailTemplate.getId(), emailTemplateMapper.toEntity(emailTemplateDto, emailTemplate));

    }

    @Override
    public Optional<EmailTemplate> patch(Long id, EmailTemplate baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<EmailTemplate> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<EmailTemplate> findByCode(String code) {
        return Optional.empty();
    }

    public Optional<EmailTemplate> checkAndUpdate(String emailTemplateCode, EmailTemplatePatchDto emailTemplatePatchDto) {

        EmailTemplate emailTemplate = emailTemplateService.findByCode(emailTemplateCode);

        if (emailTemplate == null) {
            throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
        }
        return update(emailTemplate.getId(), emailTemplateMapper.fromPatchtoDto(emailTemplatePatchDto, emailTemplate));
    }
}
