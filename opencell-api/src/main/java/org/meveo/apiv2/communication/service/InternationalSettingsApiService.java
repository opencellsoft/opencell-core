package org.meveo.apiv2.communication.service;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.communication.impl.InternationalSettingsService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InternationalSettingsApiService implements ApiService<EmailTemplate> {

    @Inject
    InternationalSettingsService internationalSettingsService;

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
        if(emailTemplate == null){
           throw new EntityDoesNotExistsException(EmailTemplate.class, id);
        }
        internationalSettingsService.update(emailTemplate);
        return Optional.of(emailTemplate);
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
}
