/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

@Entity
@DiscriminatorValue("DAILY")
public class CalendarDaily extends Calendar {

    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "CAT_CALENDAR_HOURS", joinColumns = @JoinColumn(name = "CALENDAR_ID"), inverseJoinColumns = @JoinColumn(name = "HOUR_ID"))
    @OrderBy("hour, minute")
    private List<HourInDay> hours;

    private static final long MILLISEC_IN_DAY = 24 * 3600 * 1000L;

    GregorianCalendar calendar = new GregorianCalendar();

    public List<HourInDay> getHours() {
        return hours;
    }

    public void setHours(List<HourInDay> hours) {
        this.hours = hours;
    }

    /**
     * Checks for next calendar date. If not found in this day checks next day hours. Calendar has list of hours (hour:min), so if calendar has at least one hour it will be found
     * in this or next day. For example today is 2010.12.06T08:45:02. Calendar has only one hour - 07:15. So nextCalendarDate will return for 2010.12.07T07:15:00.
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {
        calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date result = null;
        long minDist = MILLISEC_IN_DAY;
        for (HourInDay hourInDay : hours) {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hourInDay.getHour());
            calendar.set(java.util.Calendar.MINUTE, hourInDay.getMinute());
            Date d = calendar.getTime();
            long dist = d.getTime() - date.getTime();
            if (dist > 0 & dist < minDist) {
                result = d;
                minDist = dist;
            }
        }
        if (result == null) { // try next day
            calendar.add(java.util.Calendar.DATE, 1);
            for (HourInDay hourInDay : hours) {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, hourInDay.getHour());
                calendar.set(java.util.Calendar.MINUTE, hourInDay.getMinute());
                Date d = calendar.getTime();
                long dist = d.getTime() - date.getTime();
                if (dist > 0 & dist < minDist) {
                    result = d;
                    minDist = dist;
                }
            }
        }
        if (result == null) {
            throw new IllegalStateException("Next calendar date could not be found!");
        }
        return result;
    }

    /**
     * Checks for previous calendar date. If not found in this day checks previous day hours. Calendar has list of hours (hour:min), so if calendar has at least one hour it will be
     * found in this or previous day. For example today is 2010.12.06T08:45:02. Calendar has only one hour - 07:15. So previousCalendarDate will return 2010.12.06T07:15:00.
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public Date previousCalendarDate(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date result = null;
        long minDist = -MILLISEC_IN_DAY;
        for (HourInDay hourInDay : hours) {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hourInDay.getHour());
            calendar.set(java.util.Calendar.MINUTE, hourInDay.getMinute());
            Date d = calendar.getTime();
            long dist = d.getTime() - date.getTime();
            if (dist <= 0 & dist > minDist) {
                result = d;
                minDist = dist;
            }
        }
        if (result == null) { // if result did not change
            calendar.add(java.util.Calendar.DATE, -1);
            for (HourInDay hourInDay : hours) {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, hourInDay.getHour());
                calendar.set(java.util.Calendar.MINUTE, hourInDay.getMinute());
                Date d = calendar.getTime();
                long dist = d.getTime() - date.getTime();
                if (dist <= 0 & dist > minDist) {
                    result = d;
                    minDist = dist;
                }
            }
        }
        if (result == null) {
            throw new IllegalStateException("Next calendar date could not be found!");
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
    public boolean truncDateTime() {
        return false;
    }
}