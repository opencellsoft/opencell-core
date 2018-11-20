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
        Query query = getEntityManager().createQuery("from " + getEntityClass().getSimpleName() + " b where :date>= b.startDate and :date<= b.endDate").setParameter("date", date);
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
}