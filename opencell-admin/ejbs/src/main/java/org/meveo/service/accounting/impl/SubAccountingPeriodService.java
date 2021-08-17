package org.meveo.service.accounting.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriodTypeEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class SubAccountingPeriodService extends PersistenceService<SubAccountingPeriod> {

    public SubAccountingPeriod findByAccountingPeriod(AccountingPeriod accountingPeriod, Date accountingDate) {
        TypedQuery<SubAccountingPeriod> query = getEntityManager()
            .createQuery("select s from " + entityClass.getSimpleName() + " s where accountingPeriod=:accountingPeriod and (:accountingDate >= startDate and :accountingDate <= endDate)",
                entityClass)
            .setParameter("accountingPeriod", accountingPeriod).setParameter("accountingDate", accountingDate);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
	public List<SubAccountingPeriod> createSubAccountingPeriods(AccountingPeriod ap, SubAccountingPeriodTypeEnum type) {
		List<SubAccountingPeriod> periods = new ArrayList<>();
		LocalDate startDate = LocalDate.ofYearDay(ap.getAccountingPeriodYear(), 1);
		final int numberOfPeriodsPerYear = type.getNumberOfPeriodsPerYear();
		for (int i = 1; i <= numberOfPeriodsPerYear; i++) {
			SubAccountingPeriod subAccountingPeriod = new SubAccountingPeriod();
			subAccountingPeriod.setAccountingPeriod(ap);
			subAccountingPeriod.setStartDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			startDate = startDate.plusMonths(12 / numberOfPeriodsPerYear);
			subAccountingPeriod.setEndDate(Date
					.from(startDate.atTime(LocalTime.MIN).minusNanos(1).atZone(ZoneId.systemDefault()).toInstant()));
			periods.add(subAccountingPeriod);
			create(subAccountingPeriod);
		}
		return periods;
	}
}
