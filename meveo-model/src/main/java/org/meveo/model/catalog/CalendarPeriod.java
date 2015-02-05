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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

@Entity
@DiscriminatorValue("PERIOD")
public class CalendarPeriod extends Calendar {

    private static final long serialVersionUID = 1L;

    @Column(name = "PERIOD_LENGTH")
    private Integer periodLength = 30;

    @Transient
    private int periodUnit = java.util.Calendar.DAY_OF_MONTH;

    @Column(name = "NB_PERIODS")
    private Integer nbPeriods = 1;


    public Integer getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    public Integer getNbPeriods() {
        return nbPeriods;
    }

    public void setNbPeriods(Integer nbPeriods) {
        this.nbPeriods = nbPeriods;
    }


    /**
     * Checks for next calendar date by adding number of days in a period to a starting date. Date being checked must fall within a period timeframe or null is returned
     * 
     * @param date Date being checked
     * @return Next calendar date.
     */
    @Override
    public Date nextCalendarDate(Date date) {

        if (nbPeriods == null || periodLength == null || getStartDate() == null) {
            return null;
        }

        Date cleanDate = DateUtils.truncate(getStartDate(), java.util.Calendar.DAY_OF_MONTH);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(cleanDate);

        for (int i = 1; i <= nbPeriods; i++) {
            Date oldDate = calendar.getTime();
            calendar.add(periodUnit, periodLength);
            if (date.compareTo(oldDate) >= 0 && date.compareTo(calendar.getTime()) < 0) {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
                return calendar.getTime();
            }
        }

        return null;
    }

    /**
     * Checks for previous calendar date by adding number of days in a period to a starting date. Date being checked must fall within a period timeframe or null is returned
     * 
     * @param date Current date.
     * @return Previous calendar date.
     */
    @Override
    public Date previousCalendarDate(Date date) {

        if (nbPeriods == null || periodLength == null || getStartDate() == null) {
            return null;
        }

        Date cleanDate = DateUtils.truncate(getStartDate(), java.util.Calendar.DAY_OF_MONTH);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(cleanDate);

        for (int i = 1; i <= nbPeriods; i++) {
            Date oldDate = calendar.getTime();
            calendar.add(periodUnit, periodLength);
            if (date.compareTo(oldDate) >= 0 && date.compareTo(calendar.getTime()) < 0) {
                return oldDate;
            }
        }

        return null;
    }
}