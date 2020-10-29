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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * The CalendarPeriod class.
 *
 * @author Andrius Karpavicius
 * @author Mounir Bahije
 * @lastModifiedVersion 5.0
 */
@Entity
@DiscriminatorValue("PERIOD")
public class CalendarPeriod extends Calendar {

    private static final long serialVersionUID = 1L;

    @Column(name = "period_length")
    private Integer periodLength = 30;

    /**
     * java.util.Calendar.MONTH = 2 java.util.Calendar.DAY_OF_MONTH = 5 java.util.Calendar.HOUR_OF_DAY = 11 java.util.Calendar.MINUTE = 12 java.util.Calendar.SECOND = 13
     */
    @Column(name = "period_unit")
    private Integer periodUnit = java.util.Calendar.DAY_OF_MONTH;

    public static List<Integer> VALID_PERIOD_UNITS = Arrays.asList(java.util.Calendar.MONTH, java.util.Calendar.DAY_OF_MONTH, java.util.Calendar.HOUR_OF_DAY, java.util.Calendar.MINUTE, java.util.Calendar.SECOND);

    @Column(name = "nb_periods")
    private Integer nbPeriods = 0;

    /**
     * The last date/time unit to preserve when truncating the date. E.g. setting to DAY_OF_MONTH will set time to 0. Setting it to SECOND will not truncate date at all.
     */
    @Transient
    private Integer lastUnitInDateTruncate = java.util.Calendar.SECOND;

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

    public Integer getLastUnitInDateTruncate() {
        return lastUnitInDateTruncate;
    }

    public void setLastUnitInDateTruncate(Integer lastUnitInDateTruncate) {
        this.lastUnitInDateTruncate = lastUnitInDateTruncate;
    }

    /**
     * Checks for next calendar date by adding number of days in a period to a starting date. Date being checked must fall within a period timeframe or null is returned
     *
     * @param date Date being checked
     * @return Next calendar date.
     */
    @Override
    public Date nextCalendarDate(Date date) {
        return nextCalendarDate(date, getInitDate());
    }

    @Override
    protected Date nextCalendarDate(Date date, Date initDate) {
        if (periodLength == null || periodUnit == null || initDate == null || date == null|| date.before(initDate)) {
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
        calendar.setTime(initDate);

        int i = 1;
        while (date.compareTo(calendar.getTime()) >= 0) {

            calendar.setTime(initDate);

            GregorianCalendar calendarOld = new GregorianCalendar();
            calendarOld.setTime(initDate);
            calendarOld.add(periodUnit, (i - 1) * periodLength);
            Date oldDate = calendarOld.getTime();

            calendar.add(periodUnit, i * periodLength);
            if (date.compareTo(oldDate) >= 0 && date.compareTo(calendar.getTime()) < 0) {
                truncateDateTime(calendar);
                return calendar.getTime();
            }

            i++;
            if (nbPeriods > 0 && i > nbPeriods) {
                break;
            }
        }

        return null;

    }

    public Date getLimitOfNextDate() {
        if (getNbPeriods() <= 0) {
            return null;
        }
        Date current = getInitDate();
        int i = 0;
        while (i++ < getNbPeriods()) {
            current = nextCalendarDate(current);
        }
        return current;
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

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getInitDate());

        int i = 1;

        while (date.compareTo(calendar.getTime()) >= 0) {

            calendar.setTime(getInitDate());

            GregorianCalendar calendarOld = new GregorianCalendar();
            calendarOld.setTime(getInitDate());
            calendarOld.add(periodUnit, (i - 1) * periodLength);
            Date oldDate = calendarOld.getTime();

            calendar.add(periodUnit, i * periodLength);
            if (date.compareTo(oldDate) >= 0 && date.compareTo(calendar.getTime()) < 0) {
                truncateDateTime(calendarOld);
                return calendarOld.getTime();
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

    public static boolean isValidPeriodUnit(Integer unit) {
        return VALID_PERIOD_UNITS.contains(unit);
    }

    @Override
    public Date truncateDateTime(Date dateToTruncate) {

        // No truncation is needed
        if (lastUnitInDateTruncate.intValue() == java.util.Calendar.SECOND) {
            return dateToTruncate;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dateToTruncate);
        truncateDateTime(calendar);

        return calendar.getTime();
    }

    /**
     * Truncates day and time portion of a date based on truncation unit set
     *
     * @param calendar Date as calendar to truncate
     */
    private void truncateDateTime(GregorianCalendar calendar) {

        // No truncation is needed
        if (lastUnitInDateTruncate.intValue() == java.util.Calendar.SECOND) {
            return;
        }

        calendar.set(java.util.Calendar.MILLISECOND, 0);

        if (lastUnitInDateTruncate.intValue() == java.util.Calendar.DAY_OF_MONTH) {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        }
        if (lastUnitInDateTruncate.intValue() == java.util.Calendar.DAY_OF_MONTH || lastUnitInDateTruncate.intValue() == java.util.Calendar.HOUR_OF_DAY) {

            calendar.set(java.util.Calendar.MINUTE, 0);
        }
        if (lastUnitInDateTruncate.intValue() == java.util.Calendar.DAY_OF_MONTH || lastUnitInDateTruncate.intValue() == java.util.Calendar.HOUR_OF_DAY || lastUnitInDateTruncate.intValue() == java.util.Calendar.MINUTE) {
            calendar.set(java.util.Calendar.SECOND, 0);
        }

    }

    @Override
    public void setInitDate(Date startDate) {
        initializeTruncateDateToUnit();
        super.setInitDate(truncateDateTime(startDate));
    }

    /**
     * Initialize truncate date to unit value based on granularity of period
     */
    private void initializeTruncateDateToUnit() {
        if (lastUnitInDateTruncate == null) {
            if (periodUnit.intValue() == java.util.Calendar.MONTH || periodUnit.intValue() == java.util.Calendar.DAY_OF_MONTH) {
                lastUnitInDateTruncate = java.util.Calendar.DAY_OF_MONTH;

            } else if (periodUnit.intValue() == java.util.Calendar.HOUR_OF_DAY) {
                lastUnitInDateTruncate = java.util.Calendar.HOUR_OF_DAY;

            } else if (periodUnit.intValue() == java.util.Calendar.MINUTE) {
                lastUnitInDateTruncate = java.util.Calendar.MINUTE;

            } else if (periodUnit.intValue() == java.util.Calendar.SECOND) {
                lastUnitInDateTruncate = java.util.Calendar.SECOND;
            }
        }
    }

    @Override
    public boolean isInitializationRequired() {
        return true;
    }
}