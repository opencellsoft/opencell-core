package org.meveo.apiv2.settings.openOrderSetting.impl;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.settings.ImmutableOpenOrderSettingInput;
import org.meveo.apiv2.settings.OpenOrderSettingInput;
import org.meveo.model.settings.OpenOrderSetting;

public class OpenOrderSettingMapper extends ResourceMapper<OpenOrderSettingInput, OpenOrderSetting> {

    @Override protected OpenOrderSettingInput toResource(OpenOrderSetting entity) {
        return ImmutableOpenOrderSettingInput.builder()
                .id(entity.getId())
                .useOpenOrders(entity.isUseOpenOrders())
                .applyMaximumValidity(entity.isApplyMaximumValidity())
                .applyMaximumValidityValue(entity.getApplyMaximumValidityValue())
                .applyMaximumValidityUnit(entity.getApplyMaximumValidityUnit())
                .defineMaximumValidity(entity.isDefineMaximumValidity())
                .defineMaximumValidityValue(entity.getDefineMaximumValidityValue())
                .useManagmentValidationForOOQuotation(entity.isUseManagmentValidationForOOQuotation())
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
