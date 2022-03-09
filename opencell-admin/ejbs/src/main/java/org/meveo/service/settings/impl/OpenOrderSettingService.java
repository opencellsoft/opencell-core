package org.meveo.service.settings.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;

@Stateless
public class OpenOrderSettingService extends BusinessService<OpenOrderSetting> {

    @Override
    public void create(OpenOrderSetting entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }


    public void checkParameters(OpenOrderSetting entity) {
        if (entity.getApplyMaximumValidityValue() != null && entity.getApplyMaximumValidityValue() < 1)
            throw new InvalidParameterException("Apply Maximum Validity Value must be greater than 0");
        if (entity.getDefineMaximumValidityValue() != null && entity.getDefineMaximumValidityValue().longValue() < 1)
            throw new InvalidParameterException("Define Maximum Validity Value must be greater than 0");
    }


}
