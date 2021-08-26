package org.meveo.service.accounting.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.service.base.PersistenceService;

@Stateless
public class AccountingPeriodService extends PersistenceService<AccountingPeriod> {

    public AccountingPeriod findByAccountingPeriodYear(Integer year) {
        TypedQuery<AccountingPeriod> query = getEntityManager().createQuery("select ap from " + entityClass.getSimpleName() + " ap where accountingPeriodYear=:year", entityClass)
            .setParameter("year", year);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of AccountingPeriodYear {} found", getEntityClass().getSimpleName(), year);
            return null;
        }
    }
}
