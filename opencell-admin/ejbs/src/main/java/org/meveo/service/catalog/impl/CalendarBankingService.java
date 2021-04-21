/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.catalog.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

import org.meveo.model.catalog.CalendarBanking;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

/**
 * Calendar service implementation.
 */
@Stateless
@Named
public class CalendarBankingService extends PersistenceService<CalendarBanking> {

    /**
     * Gets the banking calendar in which the given date is between its startDate and EndDate.
     *
     * @param date the date
     * @return the banking calendar
     */
    @SuppressWarnings("unchecked")
    public CalendarBanking getBankingCalendarByDate(Date date) {
        Query query = getEntityManager().createQuery("from " + getEntityClass().getSimpleName() + " b where :date>= b.startDate and :date<= b.endDate order by b.endDate desc").setParameter("date", date);
        List<CalendarBanking> calandars = query.getResultList();

        return calandars.size() > 0 ? calandars.get(0) : null;
    }
    
    /**
     * Gets the current banking calendar.
     *
     * @return the current banking Calendar
     */
    public CalendarBanking getCurrentBankingCalendar() {
        return getBankingCalendarByDate(new Date());
    }
    
    /**
     * Retrieves the banking calendar that corresponds to the date of entry, 
     * if this date corresponds to a weekend or holiday it returns the next bank Working date.
     *
     * @param date the date
     * @return the next bank working date if the given date is a holiday or weekend, the same date otherwise.
     */
    public Date getNextBankWorkingDate(Date date) {
        CalendarBanking bankingCalendar = getBankingCalendarByDate(date);
        if (bankingCalendar != null) {
            date = bankingCalendar.nextCalendarDate(date);
        }
        return date;
    }
    
    /**
     * Retrieves the banking calendar that corresponds to the date of entry, 
     * if this date corresponds to a weekend or holiday it returns the previous bank Working date.
     *
     * @param date the date
     * @return the previous bank working date if the given date is a holiday or weekend, the same date otherwise
     */
    public Date getPreviousBankWorkingDate(Date date) {
        CalendarBanking bankingCalendar = getBankingCalendarByDate(date);
        if (bankingCalendar != null) {
            date = bankingCalendar.previousCalendarDate(date);
        }
        return date;
    }
    
    /**
     * Checks if it's a bank working date.
     *
     * @param date the date to check
     * @return false if the date is a holiday or weekend, true otherwise
     */
    public Boolean isBankWorkingDate(Date date) {
        CalendarBanking bankingCalendar = getBankingCalendarByDate(date);
        if (bankingCalendar == null) {
            return true;
        }
        return bankingCalendar.isBankWorkingDate(date);
    }
    
	/**
	 * Adds the business days to date.
	 *
	 * @param dateStart the date start
	 * @param nbDays the nb days
	 * @return the date
	 */
	public Date addBusinessDaysToDate(Date dateStart, Integer nbDays) {
		Date dateResult = null;
		if (dateStart == null || nbDays == null) {
			return dateResult;
		}
		dateResult = dateStart;
		int daysAdded = 0;
		while (daysAdded != nbDays) {
			dateResult = DateUtils.addDaysToDate(dateResult, 1);
			if (isBankWorkingDate(dateResult)) {
				daysAdded++;
			}
		}
		return dateResult;
	}

}