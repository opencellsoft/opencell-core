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
package org.meveo.model.catalog;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meveo.model.shared.DateUtils;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
@DiscriminatorValue("YEARLY")
public class CalendarYearly extends Calendar {

    private static final long serialVersionUID = 1L;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cat_calendar_days", joinColumns = @JoinColumn(name = "calendar_id"), inverseJoinColumns = @JoinColumn(name = "day_id"))
    private List<DayInYear> days;

    public List<DayInYear> getDays() {
        return days;
    }

    public void setDays(List<DayInYear> days) {
        this.days = days;
    }

    /**
     * Checks for next calendar date. If not found in this year checks next years dates. Calendar has list of days (month/day), so if calendar has at least one date it will be
     * found in this or next year. For example today is 2010.12.06. Calendar has only one day - 12.05. So nextCalendarDate will be found for 2011.12.05.
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {
        // Empty Calendar Case
        if (days == null || days.isEmpty()) {
            return null;
        }
        Date future = DateUtils.newDate(3000, 0, 1, 0, 0, 0);
        Date result = future;
        int currentYear = DateUtils.getYearFromDate(date);
        for (DayInYear dayInYear : days) {
            Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
            if (d.after(date) && d.before(result)) {
                result = d;
            }
        }
        if (result == future) { // if result did not change
            currentYear++; // check for date in next year
            for (DayInYear dayInYear : days) {
                Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
                if (d.after(date) && d.before(result)) {
                    result = d;
                }
            }
        }
        if (result == future) {
            throw new IllegalStateException("Next calendar date could not be found!");
        }
        return result;
    }

    /**
     * Checks for previous calendar date. If not found in this year checks previous years dates. Calendar has list of days (month/day), so if calendar has at least one date it will
     * be found in this or next year. For example today is 2010.12.06. Calendar has only one day - 12.07. So previousCalendarDate will be found for 2009.12.07.
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public Date previousCalendarDate(Date date) {
        Date past = DateUtils.newDate(1970, 0, 1, 0, 0, 0);
        Date result = past;
        int currentYear = DateUtils.getYearFromDate(date);
        for (DayInYear dayInYear : days) {
            Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
            if ((d.before(date) || d.equals(date)) && d.after(result)) {
                result = d;
            }
        }
        if (result == past) { // if result did not change
            currentYear--; // check for date in previous year
            for (DayInYear dayInYear : days) {
                Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
                if ((d.before(date) || d.equals(date)) && d.after(result)) {
                    result = d;
                }
            }
        }
        if (result == past) {
            throw new IllegalStateException("Previous calendar date could not be found!");
        }
        return result;
    }

    @Override
    public Date previousPeriodEndDate(Date date) {
        return null;
    }

    @Override
    public Date nextPeriodStartDate(Date date) {
        return null;
    }

    @Override
    public Date truncateDateTime(Date dateToTruncate) {
        return DateUtils.setTimeToZero(dateToTruncate);
    }
}