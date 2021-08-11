package org.meveo.service.accounting.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.service.base.PersistenceService;

@Stateless
public class SubAccountingPeriodService extends PersistenceService<SubAccountingPeriod> {

    public SubAccountingPeriod findByAccountingPeriod(AccountingPeriod accountingPeriod, Date accountingDate) {
        TypedQuery<SubAccountingPeriod> query = getEntityManager()
            .createQuery("select s from " + entityClass.getSimpleName() + " s where accountingPeriod=:accountingPeriod and :accountingDate between startDate and endDate",
                entityClass)
            .setParameter("accountingPeriod", accountingPeriod).setParameter("accountingDate", accountingDate);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
