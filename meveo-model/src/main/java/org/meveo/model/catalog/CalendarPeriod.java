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

@Entity
@DiscriminatorValue("PERIOD")
public class CalendarPeriod extends Calendar {

    private static final long serialVersionUID = 1L;

    @Column(name = "PERIOD_LENGTH")
    private Integer periodLength = 30;

    @Column(name = "PERIOD_UNIT")
    private Integer periodUnit = java.util.Calendar.DAY_OF_MONTH;

    @Column(name = "NB_PERIODS")
    private Integer nbPeriods = 0;

    public Integer getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    public Integer getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(Integer periodUnit) {
        this.periodUnit = periodUnit;
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

        if (periodLength == null || periodUnit == null || getInitDate() == null || date.before(getInitDate())) {
            return null;
        }
        if (nbPeriods == null) {
            nbPeriods = 0;
        }

        // Truncate date to day or a corresponding period unit if it is more detail
        // Date cleanDate = DateUtils.truncate(getInitDate(), periodUnit < java.util.Calendar.DAY_OF_MONTH ? java.util.Calendar.DAY_OF_MONTH : periodUnit);
        // GregorianCalendar calendar = new GregorianCalendar();
        // calendar.setTime(cleanDate);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getInitDate());

        int i = 1;
        while (date.compareTo(calendar.getTime()) >= 0) {
            Date oldDate = calendar.getTime();
            calendar.add(periodUnit, periodLength);
            if (date.compareTo(oldDate) >= 0 && date.compareTo(calendar.getTime()) < 0) {
                return calendar.getTime();
            }

            i++;
            if (nbPeriods > 0 && i > nbPeriods) {
                break;
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

        if (periodLength == null || periodUnit == null || getInitDate() == null || date.before(getInitDate())) {
            return null;
        }
        if (nbPeriods == null) {
            nbPeriods = 0;
        }

        // Date cleanDate = DateUtils.truncate(getInitDate(), java.util.Calendar.DAY_OF_MONTH);
        // GregorianCalendar calendar = new GregorianCalendar();
        // calendar.setTime(cleanDate);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getInitDate());

        int i = 1;
        while (date.compareTo(calendar.getTime()) >= 0) {
            Date oldDate = calendar.getTime();
            calendar.add(periodUnit, periodLength);
            if (date.compareTo(oldDate) >= 0 && date.compareTo(calendar.getTime()) < 0) {
                return oldDate;
            }

            i++;
            if (nbPeriods > 0 && i > nbPeriods) {
                break;
            }
        }

        return null;
    }    

    @Override
    public Date previousPeriodEndDate(Date date) {
        return null;
    }

    @Override
    public Date nextPeriodStartDate(Date date) {
        return null;
    }
}