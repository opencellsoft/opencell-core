package org.meveo.apiv2.settings.globalSettings.service;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.settings.AdvancedSettings;
import org.meveo.service.settings.impl.AdvancedSettingsService;

import liquibase.repackaged.org.apache.commons.lang3.StringUtils;

public class AdvancedSettingsApiService implements ApiService<AdvancedSettings> {

    @Inject
    private AdvancedSettingsService advancedSettingsService;

    @Override
    public List<AdvancedSettings> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<AdvancedSettings> findById(Long id) {
        return Optional.ofNullable(advancedSettingsService.findById(id));
    }

    @Override
    public AdvancedSettings create(AdvancedSettings baseEntity) {
        advancedSettingsService.create(baseEntity);
        return baseEntity;
    }

    @Override
    public Optional<AdvancedSettings> update(Long id, AdvancedSettings baseEntity) {
        AdvancedSettings entityToUpdate = findById(id).orElseThrow(() -> new NotFoundException("The AdvancedsettingSettings does not exist"));

        if(!StringUtils.equals(entityToUpdate.getCode(),baseEntity.getCode())){
        	throw new InvalidParameterException("the property code cannot be modified ");
        }
        mapEntityToUpdate(baseEntity, entityToUpdate);

        return Optional.of(advancedSettingsService.update(entityToUpdate));
    }
    
    public static AdvancedSettings mapEntityToUpdate(AdvancedSettings source, AdvancedSettings target) {

        if (source.getOrigin() != null) {
            target.setOrigin(source.getOrigin());
        }

        if (source.getCategory() != null) {
            target.setCategory(source.getCategory());
        }

        if (source.getGroup() != null) {
            target.setGroup(source.getGroup());
        }

        if (source.getValue() != null) {
            target.setValue(source.getValue());
        }

        if (source.getType() != null) {
            target.setType(source.getType());
        }

        return target;
    }

    @Override
    public Optional<AdvancedSettings> patch(Long id, AdvancedSettings baseEntity) {
        return null;
    }

    @Override
    public Optional<AdvancedSettings> delete(Long id) {
        return null;
    }

    @Override
    public Optional<AdvancedSettings> findByCode(String code) {
        return null;
    }

}