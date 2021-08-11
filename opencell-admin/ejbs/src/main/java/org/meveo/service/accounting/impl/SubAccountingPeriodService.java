package org.meveo.service.accounting.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.service.base.PersistenceService;

@Stateless
public class SubAccountingPeriodService extends PersistenceService<SubAccountingPeriod> {

    public List<SubAccountingPeriod> findByAccountingPeriod(AccountingPeriod accountingPeriod, Date date) {
        TypedQuery<SubAccountingPeriod> query = getEntityManager()
            .createQuery("select s from " + entityClass.getSimpleName() + " s where accountingPeriod=:accountingPeriod and :date between startDate and endDate", entityClass)
            .setParameter("accountingPeriod", accountingPeriod).setParameter("date", date);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.debug("No {} of AccountingPeriod {} found", getEntityClass().getSimpleName(), accountingPeriod);
            return null;
        }
    }
}
