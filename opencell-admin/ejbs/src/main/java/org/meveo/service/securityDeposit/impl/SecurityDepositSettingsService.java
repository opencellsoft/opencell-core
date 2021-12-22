package org.meveo.service.securityDeposit.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.securityDeposit.SecurityDepositSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class SecurityDepositSettingsService extends BusinessService<SecurityDepositSettings> {

    @Override public void create(SecurityDepositSettings entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public SecurityDepositSettings update(SecurityDepositSettings securityDepositSettings) throws BusinessException {

        checkParameters(securityDepositSettings);
        return super.update(securityDepositSettings);
    }

    private void checkParameters(SecurityDepositSettings securityDepositSettings)
    {
        if(securityDepositSettings.getMaxAmountPerSecurityDeposit().longValue() < 1)
            throw new InvalidParameterException("max amount per security Deposit should be greater or equals 1");
        if(securityDepositSettings.getMaxAmountPerCustomer().longValue() < 1)
            throw new InvalidParameterException("max amount per customer should be greater or equals 1");
    }
}
