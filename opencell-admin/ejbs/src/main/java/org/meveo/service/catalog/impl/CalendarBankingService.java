package org.meveo.service.catalog.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

import org.meveo.model.catalog.CalendarBanking;
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
}