package org.meveo.service.accounting.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    
	public List<SubAccountingPeriod> createSubAccountingPeriods(AccountingPeriod ap, SubAccountingPeriodTypeEnum type,
			Date start, boolean regularPeriods) {
		List<SubAccountingPeriod> periods = new ArrayList<>();
		LocalDateTime startDateTime = start==null? LocalDate.now().withDayOfMonth(1).atStartOfDay(): start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime endDate = ap.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX);
		final int numberOfPeriodsPerYear = type.getNumberOfPeriodsPerYear();
		final int monthsPerPeriod = 12 / numberOfPeriodsPerYear;
		while (endDate.isAfter(startDateTime)) {
			SubAccountingPeriod subAccountingPeriod = new SubAccountingPeriod();
			subAccountingPeriod.setAccountingPeriod(ap);
			subAccountingPeriod.setStartDate(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			startDateTime = startDateTime.plusMonths(monthsPerPeriod);
			if (!endDate.isAfter(startDateTime) && !regularPeriods) {
				subAccountingPeriod.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
			} else {
				subAccountingPeriod.setEndDate(Date.from(startDateTime.minusNanos(1).atZone(ZoneId.systemDefault()).toInstant()));
			}
			periods.add(subAccountingPeriod);
			create(subAccountingPeriod);
		}
		return periods;
	}
}
