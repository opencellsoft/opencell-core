package org.meveo.service.settings.impl;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.service.base.PersistenceService;

@Stateless
public class GlobalSettingsService extends PersistenceService<GlobalSettings> {

    @Override
    public void create(GlobalSettings entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public GlobalSettings update(GlobalSettings entity) throws BusinessException {
        checkParameters(entity);
        return super.update(entity);
    }

    public void checkParameters(GlobalSettings entity) {
        if (entity.getQuoteDefaultValidityDelay() <= 0)
            throw new InvalidParameterException("QuoteDefaultValidityDelay must be greater than 0");
    }
}
