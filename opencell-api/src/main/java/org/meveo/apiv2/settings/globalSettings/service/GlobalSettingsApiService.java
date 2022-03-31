package org.meveo.apiv2.settings.globalSettings.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.service.settings.impl.GlobalSettingsService;

public class GlobalSettingsApiService implements ApiService<GlobalSettings> {

    @Inject
    private GlobalSettingsService globalSettingsService;

    @Override
    public List<GlobalSettings> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<GlobalSettings> findById(Long id) {
        return null;
    }

    @Override
    public GlobalSettings create(GlobalSettings baseEntity) {
        globalSettingsService.create(baseEntity);
        return baseEntity;
    }

    @Override
    public Optional<GlobalSettings> update(Long id, GlobalSettings baseEntity) {
        return null;
    }

    @Override
    public Optional<GlobalSettings> patch(Long id, GlobalSettings baseEntity) {
        return null;
    }

    @Override
    public Optional<GlobalSettings> delete(Long id) {
        return null;
    }

    @Override
    public Optional<GlobalSettings> findByCode(String code) {
        return null;
    }

}