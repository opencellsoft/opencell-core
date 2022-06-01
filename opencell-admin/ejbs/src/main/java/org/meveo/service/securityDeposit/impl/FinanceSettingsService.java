package org.meveo.service.securityDeposit.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.base.BusinessService;

@Stateless
public class FinanceSettingsService extends BusinessService<FinanceSettings> {

    @Override
    public void create(FinanceSettings entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public FinanceSettings update(FinanceSettings financeSettings) throws BusinessException {

        checkParameters(financeSettings);
        return super.update(financeSettings);
    }

    public void checkParameters(FinanceSettings financeSettings) {
        if (financeSettings.getMaxAmountPerSecurityDeposit() != null && financeSettings.getMaxAmountPerSecurityDeposit().longValue() < 1)
            throw new InvalidParameterException("max amount per security Deposit should be greater or equals 1");
        if (financeSettings.getMaxAmountPerCustomer() != null && financeSettings.getMaxAmountPerCustomer().longValue() < 1)
            throw new InvalidParameterException("max amount per customer should be greater or equals 1");
    }

    public FinanceSettings findLastOne() {
        try {
            TypedQuery<FinanceSettings> query = getEntityManager().createQuery("from FinanceSettings f order by f.id desc", entityClass).setMaxResults(1);
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} found", getEntityClass().getSimpleName());
            return null;
        }
    }
}
