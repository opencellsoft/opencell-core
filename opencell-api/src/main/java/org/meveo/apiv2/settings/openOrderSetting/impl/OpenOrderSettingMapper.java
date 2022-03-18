package org.meveo.apiv2.settings.openOrderSetting.impl;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.settings.ImmutableOpenOrderSettingInput;
import org.meveo.apiv2.settings.OpenOrderSettingInput;
import org.meveo.model.settings.OpenOrderSetting;

public class OpenOrderSettingMapper extends ResourceMapper<OpenOrderSettingInput, OpenOrderSetting> {

    @Override protected OpenOrderSettingInput toResource(OpenOrderSetting entity) {
        return ImmutableOpenOrderSettingInput.builder()
                .id(entity.getId())
                .useOpenOrders(entity.getUseOpenOrders())
                .applyMaximumValidity(entity.getApplyMaximumValidity())
                .applyMaximumValidityValue(entity.getApplyMaximumValidityValue())
                .applyMaximumValidityUnit(entity.getApplyMaximumValidityUnit())
                .defineMaximumValidity(entity.getDefineMaximumValidity())
                .defineMaximumValidityValue(entity.getDefineMaximumValidityValue())
                .useManagmentValidationForOOQuotation(entity.getUseManagmentValidationForOOQuotation())
                .build();
    }

    @Override protected OpenOrderSetting toEntity(OpenOrderSettingInput resource) {
        return toEntity(new OpenOrderSetting(), resource);
    }

     protected OpenOrderSetting toEntity(OpenOrderSetting entity, OpenOrderSettingInput resource) {
         entity.setUseOpenOrders(resource.getUseOpenOrders());
         entity.setApplyMaximumValidity(resource.getApplyMaximumValidity());
         entity.setApplyMaximumValidityValue(resource.getApplyMaximumValidityValue());
         entity.setApplyMaximumValidityUnit(resource.getApplyMaximumValidityUnit());
         entity.setDefineMaximumValidity(resource.getDefineMaximumValidity());
         entity.setDefineMaximumValidityValue(resource.getDefineMaximumValidityValue());
         entity.setUseManagmentValidationForOOQuotation(resource.getUseManagmentValidationForOOQuotation());
        return entity;
    }
}
