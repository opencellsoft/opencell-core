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

import static java.util.Calendar.APRIL;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.MARCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.meveo.model.catalog.CalendarJoin.CalendarJoinTypeEnum.APPEND;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.CalendarJoin.CalendarJoinTypeEnum;
import org.meveo.model.shared.DateUtils;

import com.google.common.collect.Lists;

public class CalendarTest {

    @Test
    public void testYearCalendar() {

        CalendarYearly calendar = new CalendarYearly();

        List<DayInYear> days = new ArrayList<DayInYear>();

        for (int i = 1; i <= 12; i++) {
            DayInYear day = new DayInYear();
            day.setMonth(MonthEnum.getValue(i));
            day.setDay(1);
            days.add(day);
        }
        calendar.setDays(days);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 2, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 2, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 1, 0, 0, 0), nextDate);

    }

    @Test
    public void testYearCalendar2() {

        CalendarYearly calendar = new CalendarYearly();

        List<DayInYear> days = new ArrayList<DayInYear>();

        for (int i = 1; i <= 12; i++) {
            DayInYear day = new DayInYear();
            day.setMonth(MonthEnum.getValue(i));
            day.setDay(10);
            days.add(day);
        }
        calendar.setDays(days);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 11, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 11, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 9, 0, 0, 0));
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 9, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, JANUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 10, 0, 0, 0), nextDate);

    }

    @Test
    public void testHourCalendar() {

        CalendarDaily calendar = new CalendarDaily();

        List<HourInDay> hours = new ArrayList<HourInDay>();

        for (int i = 0; i <= 23; i++) {
            HourInDay hour = new HourInDay();
            hour.setHour(i);
            hour.setMinute(0);
            hours.add(hour);
        }
        calendar.setHours(hours);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 1, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 0, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 1, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 1, 15, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 1, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 1, 15, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 2, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 23, 15, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 23, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 23, 15, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 11, 0, 0, 0), nextDate);
    }

    @Test
    public void testHourCalendar2() {

        CalendarDaily calendar = new CalendarDaily();

        List<HourInDay> hours = new ArrayList<HourInDay>();

        for (int i = 0; i <= 23; i++) {
            HourInDay hour = new HourInDay();
            hour.setHour(i);
            hour.setMinute(15);
            hours.add(hour);
        }
        calendar.setHours(hours);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 1, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 1, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 1, 16, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 1, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 1, 16, 1));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 2, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 14, 59));
        assertEquals(DateUtils.newDate(2015, JANUARY, 9, 23, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 0, 14, 59));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 23, 25, 59));
        assertEquals(DateUtils.newDate(2015, JANUARY, 10, 23, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 10, 23, 25, 59));
        assertEquals(DateUtils.newDate(2015, JANUARY, 11, 0, 15, 0), nextDate);
    }

    @Test()
    public void testOnePeriodInDaysCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(20);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(1);
        calendar.setPeriodUnit(java.util.Calendar.MONTH);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

    }

    @Test()
    public void testEndOfMonthDiff_InMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setPeriodLength(1);
        calendar.setPeriodUnit(java.util.Calendar.MONTH);
        calendar.setInitDate(DateUtils.newDate(2020, JANUARY, 31, 0, 0, 0));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2020, JANUARY, 30, 0, 0, 0));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 29, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.MARCH, 31, 0, 0, 0));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 29, 0, 0, 0), nextDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.JULY, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.JULY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.JULY, 1, 0, 0, 0));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.JUNE, 30, 0, 0, 0), prevDate);
    }

    @Test()
    public void testOnePeriodInHourCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(3);
        calendar.setPeriodUnit(java.util.Calendar.HOUR_OF_DAY);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 58));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 58));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInMinuteCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(3);
        calendar.setPeriodUnit(java.util.Calendar.MINUTE);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 58));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 58));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 14, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 14, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInSecondCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(3);
        calendar.setPeriodUnit(java.util.Calendar.SECOND);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 49));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 49));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 52));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 52));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 51));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 51));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53), nextDate);

    }

    @Test()
    public void testMultiPeriodInMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(5);
        calendar.setPeriodLength(7);
        calendar.setPeriodUnit(java.util.Calendar.DAY_OF_MONTH);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59), nextDate);

    }

    @Test()
    public void testMonthPeriodCalendar_withShortMonth() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(0);
        calendar.setPeriodLength(1);
        calendar.setPeriodUnit(java.util.Calendar.MONTH);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.MARCH, 9, 15, 12, 59));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.FEBRUARY, 29, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.MARCH, 9, 15, 12, 59));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.MARCH, 31, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, JANUARY, 31, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.FEBRUARY, 29, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.APRIL, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.MARCH, 31, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.APRIL, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.APRIL, 30, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.AUGUST, 8, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.JULY, 31, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.AUGUST, 8, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.AUGUST, 31, 15, 12, 59), nextDate);
    }

    @Test()
    public void testMultiPeriodInMinuteCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(6);
        calendar.setPeriodLength(10);
        calendar.setPeriodUnit(java.util.Calendar.MINUTE);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 58));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 58));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 59));
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 59));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 58));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 0, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 58));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 20, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 34, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 30, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 34, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 40, 59), nextDate);

    }

    @Test()
    public void testZeroPeriodInMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(0);
        calendar.setPeriodLength(7);
        calendar.setLastUnitInDateTruncate(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 16, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59), nextDate);

    }

    @Test()
    public void testSimpleWeekdayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1, 5)); // monday through friday

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0)); // saturday
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0)); // saturday
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0)); // saturday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0)); // saturday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 30, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 30, 0, 0, 0), resolvedDate);

    }

    @Test()
    public void testCrossWeekdayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 5, 2)); // friday through tuesday

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 8, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 8, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 13, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 13, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0), resolvedDate);
    }

    @Test()
    public void testFullWeekdayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1, 1)); // monday through monday

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), resolvedDate);
    }

    @Test()
    public void testFullWeekdayIntervalCalendar2() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 3, 3)); // wednesday through wednesday

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0)); // tuesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), resolvedDate);

    }

    @Test()
    public void testSimpleDayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1015, 1231)); // 10/15 to 12/31

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 30, 0, 0, 0)); // 12/30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 30, 0, 0, 0)); // 12/30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 30, 0, 0, 0)); // 12/30
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 30, 0, 0, 0)); // 12/30
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 31, 0, 0, 0), resolvedDate);

    }

    @Test()
    public void testCrossDayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1015, 131)); // 10/15 to 01/31

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 31, 0, 0, 0)); // 01/31
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 31, 0, 0, 0)); // 01/31
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2016, JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2016, JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, JANUARY, 30, 0, 0, 0)); // 01/30
        assertEquals(DateUtils.newDate(2016, JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, JANUARY, 30, 0, 0, 0)); // 01/30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);
    }

    @Test()
    public void testFullDayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 101, 101)); // 01/01 to 01/01

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 2, 0, 0, 0)); // 01/30
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 2, 0, 0, 0)); // 01/30
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);
    }

    @Test()
    public void testFullDayIntervalCalendar2() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1015, 1015)); // 10/15 to 10/15

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 16, 0, 0, 0)); // 10/16
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 16, 0, 0, 0)); // 10/16
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 14, 0, 0, 0)); // 10/14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 14, 0, 0, 0)); // 10/14
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), resolvedDate);
    }

    @Test()
    public void testSimpleHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 515, 1731)); // 05:15 to 17:31

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 0, 0)); // 04:00
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 0, 0)); // 04:00
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 0, 0)); // 04:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 0, 0)); // 04:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        assertNull(resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        assertNull(resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 11, 15, 0)); // 11:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 11, 15, 0)); // 11:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 11, 15, 0)); // 11:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 11, 15, 0)); // 11:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 30, 59)); // 17:30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), resolvedDate);

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 30, 59)); // 17:30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 30, 59)); // 17:30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), resolvedDate);

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 30, 59)); // 17:30
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 17, 31, 0), resolvedDate);
    }

    @Test()
    public void testCrossHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1731, 515)); // 17:31 to 05:15

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 16, 0, 0)); // 16:00
        assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 16, 0, 0)); // 16:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 17, 31, 0), prevDate);

    }

    @Test()
    public void testFullHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 0, 0)); // 00:00 to 00:00

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 59)); // 23:59:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 59)); // 23:59:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

    }

    @Test()
    public void testFullHourIntervalCalendar2() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1000, 1000)); // 10:00 to 10:00

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 9, 59, 59)); // 09:59:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 9, 59, 59)); // 23:59:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 1)); // 10:00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 1)); // 10:00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

    }

    @Test()
    public void testZeroHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 0, 15)); // 00:00 to 00:15

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0)); // 01:00
        assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0)); // 01:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 00:15
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 00:15
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 14, 59)); // 00:14:49
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 14, 59)); // 00:14:49
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 3, 59)); // 00:03
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 3, 59)); // 00:03
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

    }

    @Test()
    public void testUnionCalendar() {

        CalendarInterval calendar1 = new CalendarInterval();
        calendar1.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar1.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar1, 1300, 2000)); // 13:00-20:00

        CalendarInterval calendar2 = new CalendarInterval();
        calendar2.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar2.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar2, 800, 1500)); // 8:00-15:00

        CalendarJoin calendar = new CalendarJoin();
        calendar.setJoinType(CalendarJoinTypeEnum.UNION);
        calendar.setJoinCalendar1(calendar1);
        calendar.setJoinCalendar2(calendar2);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 7, 0, 0)); // saturday 07:00
        assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 7, 0, 0)); // saturday 07:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 14:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 4:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 13, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 13, 0, 0), prevDate);

    }

    @Test()
    public void testIntersectCalendar() {

        CalendarInterval calendar1 = new CalendarInterval();
        calendar1.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar1.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar1, 1, 5)); // monday through friday

        CalendarInterval calendar2 = new CalendarInterval();
        calendar2.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar2.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar2, 800, 1500)); // 8:00-15:00

        CalendarJoin calendar = new CalendarJoin();
        calendar.setJoinType(CalendarJoinTypeEnum.INTERSECT);
        calendar.setJoinCalendar1(calendar1);
        calendar.setJoinCalendar2(calendar2);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 9, 0, 0)); // saturday 09:00
        assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 9, 0, 0)); // saturday 09:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), prevDate);

    }

    @Test()
    public void testAppendCalendar() {

        CalendarPeriod calendar1 = new CalendarPeriod();
        calendar1.setNbPeriods(6);
        calendar1.setPeriodLength(3);
        calendar1.setPeriodUnit(java.util.Calendar.MONTH);

        CalendarPeriod calendar2 = new CalendarPeriod();
        calendar2.setNbPeriods(4);
        calendar2.setPeriodLength(3);
        calendar2.setPeriodUnit(java.util.Calendar.MONTH);

        CalendarJoin calendar = new CalendarJoin();
        calendar.setJoinType(APPEND);
        calendar.setJoinCalendar1(calendar1);
        calendar.setJoinCalendar2(calendar2);
        calendar.setInitDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0));

        // test basic dates matching principal calendar
        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.APRIL, 1, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0));
        assertEquals(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0), prevDate);

        // test dates in the intersection of both calendars (principal calendar must be used)
        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.MARCH, 7, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.APRIL, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.MARCH, 7, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), prevDate);

        // test dates in out of first calendar range but matched on the append calendar (append calendar must be used)
        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.JULY, 7, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.JULY, 7, 0, 0, 0));
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.JULY, 1, 0, 0, 0), prevDate);

        // test dates in out of both calendars (null return)
        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2018, java.util.Calendar.MARCH, 1, 0, 0, 0));
        assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2018, java.util.Calendar.MARCH, 1, 0, 0, 0));
        assertNull(prevDate);

    }

    @Test()
    public void testSimpleHourIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 0, 30)); // 00:00 to 00:30
        intervals.add(new CalendarDateInterval(calendar, 100, 407)); // 01:00 to 04:31
        intervals.add(new CalendarDateInterval(calendar, 515, 1731)); // 05:15 to 17:31
        intervals.add(new CalendarDateInterval(calendar, 2315, 2359)); // 23:15 to 23:59

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 35, 0)); // 00:35
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 35, 0)); // 00:35
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 7, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 18, 0)); // 23:18
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 18, 0)); // 23:18
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 23, 59, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 1, 0)); // 00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 1, 0)); // 00:01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 23, 59, 0), prevDate);
    }

    @Test()
    public void testCrossHourIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 2359, 30)); // 23:59 to 00:30

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 23, 59, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 0, 30, 0), prevDate);

        calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 2315, 30)); // 23:15 to 00:30

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 23, 15, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 0:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 15, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 0:15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 0, 30, 0), prevDate);
    }

    @Test()
    public void testSimpleDayIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 101, 115)); // 01/01 to 01/15
        intervals.add(new CalendarDateInterval(calendar, 305, 407)); // 03/05 to 04/07
        intervals.add(new CalendarDateInterval(calendar, 601, 1031)); // 06/01 to 10/31
        intervals.add(new CalendarDateInterval(calendar, 1215, 1231)); // 12/15 to 12/31

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2015, MARCH, 5, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 31, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // 02/15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // 02/15
        assertEquals(DateUtils.newDate(2015, JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0)); // 12/15
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0)); // 12/15
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.MAY, 21, 0, 0, 0)); // 05/21
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.JUNE, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.MAY, 21, 0, 0, 0)); // 05/21
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.APRIL, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), prevDate);
    }

    @Test()
    public void testCrossDayIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1231, 115)); // 12/31 to 01/15

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, JANUARY, 1, 0, 0, 0)); // 01/01
        assertEquals(DateUtils.newDate(2014, JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        assertEquals(DateUtils.newDate(2015, JANUARY, 15, 0, 0, 0), prevDate);

        calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1215, 115)); // 12/15 to 01/15

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2016, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        assertEquals(DateUtils.newDate(2015, JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.JUNE, 31, 0, 0, 0)); // 6/31
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.JUNE, 31, 0, 0, 0)); // 6/31
        assertEquals(DateUtils.newDate(2015, JANUARY, 15, 0, 0, 0), prevDate);
    }

    @Test()
    public void testSimpleWeekdayIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1, 3)); // monday to wednesday
        intervals.add(new CalendarDateInterval(calendar, 5, 6)); // friday to saturday

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 13, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 13, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 14, 0, 0, 0), prevDate);
    }

    @Test()
    public void testCrossWeekdayIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 7, 3)); // sunday to wednesday

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0)); // wednesday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0), prevDate);

        calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 5, 3)); // friday to wednesday

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 20, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 20, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);
    }

    @Test()
    public void testUnionNextPreviousCalendar() {

        CalendarInterval calendar1 = new CalendarInterval();
        calendar1.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar1.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar1, 1300, 2000)); // 13:00-20:00

        CalendarInterval calendar2 = new CalendarInterval();
        calendar2.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar2.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar2, 800, 1500)); // 8:00-15:00

        CalendarJoin calendar = new CalendarJoin();
        calendar.setJoinType(CalendarJoinTypeEnum.UNION);
        calendar.setJoinCalendar1(calendar1);
        calendar.setJoinCalendar2(calendar2);

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 7, 0, 0)); // saturday 07:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 8, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 7, 0, 0)); // saturday 07:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 21, 59, 59)); // thursday 21:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 21, 59, 59)); // thursday 21:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 20, 0, 0), prevDate);

        // Tests bellow should not be tested as time tested falls within a valid period. They work when when period do not overlapp. When period overlapp it does not take
        // continuance into calculation.

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 13, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 13, 0, 0), nextDate); // Should really be friday 6 8:00 as periods overlapp

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 13, 0, 0), nextDate); // Should really be friday 6 8:00 as periods overlapp

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 14:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 4:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0), prevDate);// Should really be sunday 1 20:00 as periods overlap

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), prevDate); // Should really be wednesday 4 20:00 as periods overlap

    }

    // @Test()
    public void testIntersectNextPreviousPeriodCalendar() {

        CalendarInterval calendar1 = new CalendarInterval();
        calendar1.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar1.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar1, 1, 5)); // monday through friday

        CalendarInterval calendar2 = new CalendarInterval();
        calendar2.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar2.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar2, 800, 1500)); // 8:00-15:00

        CalendarJoin calendar = new CalendarJoin();
        calendar.setJoinType(CalendarJoinTypeEnum.INTERSECT);
        calendar.setJoinCalendar1(calendar1);
        calendar.setJoinCalendar2(calendar2);

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 9, 0, 0)); // saturday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 9, 0, 0)); // saturday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        assertEquals(DateUtils.newDate(2015, JANUARY, 29, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 29, 15, 0, 0), prevDate);

    }

    @Test()
    public void testSimpleWeekendBankingCalendar() {

        CalendarBanking calendar = new CalendarBanking();
        calendar.setWeekendBegin(6);// Saturday
        calendar.setWeekendEnd(7);// Sunday

        Date resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0)); // saturday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // TODO must be null

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0)); // saturday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), resolvedDate); // TODO must be null

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0)); // saturday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0)); // saturday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 3, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 11, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // TODO must be null

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 11, 0, 0, 0)); // sunday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), resolvedDate); // TODO must be null

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 11, 0, 0, 0)); // sunday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 11, 0, 0, 0)); // sunday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 3, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // must be 2018-11-17

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0)); // monday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // must be 2019-11-12

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0)); // monday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 19, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0)); // monday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0), resolvedDate); // must be 2018-11-10

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0)); // thursday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0), resolvedDate); // must be 2019-11-05

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0)); // thursday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0)); // thursday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 3, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.nextCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), resolvedDate); // must be 2018-11-10

        resolvedDate = calendar.previousCalendarDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0)); // friday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), resolvedDate); // must be 2019-11-05

        resolvedDate = calendar.nextPeriodStartDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0)); // friday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 12, 0, 0, 0), resolvedDate); // TODO should not be null

        resolvedDate = calendar.previousPeriodEndDate(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0)); // friday
        // assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 3, 0, 0, 0), resolvedDate); // TODO should not be null

    }

    @Test()
    public void testCrossWeekendBankingCalendar() {

        CalendarBanking calendar = new CalendarBanking();
        calendar.setWeekendBegin(6);// Saturday
        calendar.setWeekendEnd(1);// Monday

        Date dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0); // 2018/11/10
        Date nextDate = calendar.nextCalendarDate(dateToTest);
        Date prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 13, 0, 0, 0), nextDate); // 2018/11/13
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), prevDate); // 2018/11/09

        dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 11, 0, 0, 0); // 2018/11/11
        nextDate = calendar.nextCalendarDate(dateToTest);
        prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 13, 0, 0, 0), nextDate); // 2018/11/13
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), prevDate); // 2018/11/09

        dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0); // 2018/11/08
        nextDate = calendar.nextCalendarDate(dateToTest);
        prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0), nextDate); // 2018/11/08
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0), prevDate); // 2018/11/08
    }

    @Test()
    public void testMiddleEastWeekendBankingCalendar() {

        CalendarBanking calendar = new CalendarBanking();
        calendar.setWeekendBegin(4);// Thursday
        calendar.setWeekendEnd(5);// Friday

        Date dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0); // 2018/11/9 : Friday
        Date nextDate = calendar.nextCalendarDate(dateToTest);
        Date prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0), nextDate); // 2018/11/10 :Saturday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 7, 0, 0, 0), prevDate); // 2018/11/07 : Wednesday

        dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 8, 0, 0, 0); // 2018/11/8 : Thursday
        nextDate = calendar.nextCalendarDate(dateToTest);
        prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0), nextDate); // 2018/11/10
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 7, 0, 0, 0), prevDate); // 2018/11/07

        dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 7, 0, 0, 0); // 2018/11/07 : Wednesday
        nextDate = calendar.nextCalendarDate(dateToTest);
        prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 7, 0, 0, 0), nextDate); // 2018/11/07 : Wednesday
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 7, 0, 0, 0), prevDate); // 2018/11/07 Wednesday
    }

    @Test()
    public void testWeekendAndHolidaysBankingCalendar() {

        CalendarBanking calendar = new CalendarBanking();
        calendar.setWeekendBegin(6);// Saturday
        calendar.setWeekendEnd(7);// Sunday

        CalendarHoliday h = new CalendarHoliday();
        h.setHolidayBegin(1112); // 11/12
        h.setHolidayEnd(1130); // 1130
        CalendarHoliday h2 = new CalendarHoliday();
        h2.setHolidayBegin(101);// 01/01
        h2.setHolidayEnd(105);// 01/05
        calendar.setHolidays(Lists.newArrayList(h, h2));

        Date dateToTest = DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 10, 0, 0, 0); // 2018/11/10 : Saturday
        Date nextDate = calendar.nextCalendarDate(dateToTest);
        Date prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.DECEMBER, 03, 0, 0, 0), nextDate); // 2018/12/03
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.NOVEMBER, 9, 0, 0, 0), prevDate); // 2018/11/09

        dateToTest = DateUtils.newDate(2019, JANUARY, 1, 0, 0, 0); // 2019/01/01
        nextDate = calendar.nextCalendarDate(dateToTest);
        prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2019, JANUARY, 7, 0, 0, 0), nextDate); // 2019/01/07
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.DECEMBER, 31, 0, 0, 0), prevDate); // 2018/12/31
    }

    @Test()
    public void testWeekendAndCrossHolidaysBankingCalendar() {

        CalendarBanking calendar = new CalendarBanking();
        calendar.setWeekendBegin(6);// Saturday
        calendar.setWeekendEnd(7);// Sunday

        CalendarHoliday h = new CalendarHoliday();
        h.setHolidayBegin(1225); // 12/25
        h.setHolidayEnd(105); // 0105
        calendar.setHolidays(Lists.newArrayList(h));

        Date dateToTest = DateUtils.newDate(2018, java.util.Calendar.DECEMBER, 26, 0, 0, 0); // 2018/12/26
        Date nextDate = calendar.nextCalendarDate(dateToTest);
        Date prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2019, JANUARY, 7, 0, 0, 0), nextDate); // 2019/01/07
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.DECEMBER, 24, 0, 0, 0), prevDate); // 2018/12/24

        dateToTest = DateUtils.newDate(2019, JANUARY, 1, 0, 0, 0); // 2019/01/01
        nextDate = calendar.nextCalendarDate(dateToTest);
        prevDate = calendar.previousCalendarDate(dateToTest);
        assertEquals(DateUtils.newDate(2019, JANUARY, 7, 0, 0, 0), nextDate); // 2019/01/07
        assertEquals(DateUtils.newDate(2018, java.util.Calendar.DECEMBER, 24, 0, 0, 0), prevDate); // 2018/12/24
    }

    @Test
    public void testFixedDateCalendar_PreviousCalendarDate() {
        DatePeriod datePeriod1 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 01, 0, 0, 0));
        DatePeriod datePeriod2 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));

        CalendarFixed calendar = new CalendarFixed();
        calendar.addFixedDate(datePeriod1);
        calendar.addFixedDate(datePeriod2);

        assertNull(calendar.previousCalendarDate(DateUtils.newDate(2019, java.util.Calendar.DECEMBER, 31, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 01, 0, 0, 0), calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0)));
        assertNull(calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0)));
        assertNull(calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)));
        assertNull(calendar.previousCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)));
    }

    @Test
    public void testFixedDateCalendar_NextCalendarDate() {
        DatePeriod datePeriod1 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 01, 0, 0, 0));
        DatePeriod datePeriod2 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));

        CalendarFixed calendar = new CalendarFixed();
        calendar.addFixedDate(datePeriod1);
        calendar.addFixedDate(datePeriod2);

        assertNull(calendar.nextCalendarDate(DateUtils.newDate(2019, java.util.Calendar.DECEMBER, 31, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 01, 0, 0, 0), calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0)));
        assertNull(calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0)));
        assertNull(calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 8, 0, 0, 0), calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)));
        assertNull(calendar.nextCalendarDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)));
    }

    @Test
    public void testFixedDateCalendar_PreviousPeriodEndDate() {
        DatePeriod datePeriod1 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 01, 0, 0, 0));
        DatePeriod datePeriod2 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));

        CalendarFixed calendar = new CalendarFixed();
        calendar.addFixedDate(datePeriod1);
        calendar.addFixedDate(datePeriod2);

        assertNull(calendar.previousPeriodEndDate(DateUtils.newDate(2019, java.util.Calendar.DECEMBER, 31, 0, 0, 0)));
        assertNull(calendar.previousPeriodEndDate(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0)));
        assertNull(calendar.previousPeriodEndDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), calendar.previousPeriodEndDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), calendar.previousPeriodEndDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 8, 0, 0, 0), calendar.previousPeriodEndDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)));
    }

    @Test
    public void testFixedDateCalendar_NextPeriodStartDate() {
        DatePeriod datePeriod1 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 01, 0, 0, 0));
        DatePeriod datePeriod2 = new DatePeriod(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));

        CalendarFixed calendar = new CalendarFixed();
        calendar.addFixedDate(datePeriod1);
        calendar.addFixedDate(datePeriod2);

        assertEquals(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0), calendar.nextPeriodStartDate(DateUtils.newDate(2019, java.util.Calendar.DECEMBER, 31, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), calendar.nextPeriodStartDate(DateUtils.newDate(2020, java.util.Calendar.JANUARY, 1, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), calendar.nextPeriodStartDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 1, 0, 0, 0)));
        assertEquals(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 5, 0, 0, 0), calendar.nextPeriodStartDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)));
        assertNull(calendar.nextPeriodStartDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)));
        assertNull(calendar.nextPeriodStartDate(DateUtils.newDate(2020, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)));
    }

    @Test
    public void dailyCalendarTest() {
        Date startDate = DateUtils.newDate(2020, JANUARY, 1, 0, 0, 0);

        CalendarPeriod daily = new CalendarPeriod();
        daily.setPeriodLength(5);
        daily.setNbPeriods(1);
        daily.setPeriodUnit(5);
        daily.setInitDate(startDate);

        Date first = daily.nextCalendarDate(startDate);
        assertEquals(first, DateUtils.newDate(2020, JANUARY, 6, 0, 0, 0));

        daily.setInitDate(first);
        Date second = daily.nextCalendarDate(first);
        assertEquals(second, DateUtils.newDate(2020, JANUARY, 11, 0, 0, 0));

    }

    @Test
    public void joinOfPeriodCalendar() {
        Date startDate = DateUtils.newDate(2020, JANUARY, 1, 0, 0, 0);

        CalendarPeriod dailyFive = new CalendarPeriod();
        dailyFive.setPeriodLength(5);
        dailyFive.setNbPeriods(1);
        dailyFive.setPeriodUnit(5);

        CalendarPeriod dailyTwo = new CalendarPeriod();
        dailyTwo.setPeriodLength(2);
        dailyTwo.setNbPeriods(1);
        dailyTwo.setPeriodUnit(5);

        CalendarJoin calendarJoin = new CalendarJoin();
        calendarJoin.setJoinCalendar1(dailyFive);
        calendarJoin.setJoinCalendar2(dailyTwo);
        calendarJoin.setJoinType(APPEND);
        calendarJoin.setInitDate(startDate);

        Date firstDate = calendarJoin.nextCalendarDate(startDate);
        assertEquals(firstDate, DateUtils.newDate(2020, JANUARY, 6, 0, 0, 0));

        Date secondDate = calendarJoin.nextCalendarDate(firstDate);
        assertEquals(secondDate, DateUtils.newDate(2020, JANUARY, 8, 0, 0, 0));

        Date thirdDate = calendarJoin.nextCalendarDate(secondDate);
        assertNull(thirdDate);

    }

    @Test
    public void joinOfJoinCalendar() {

        Date startDate = DateUtils.newDate(2020, JANUARY, 1, 0, 0, 0);

        CalendarPeriod monthly = new CalendarPeriod();
        monthly.setPeriodLength(3);
        monthly.setNbPeriods(1);
        monthly.setPeriodUnit(2);

        CalendarPeriod daily = new CalendarPeriod();
        daily.setPeriodLength(5);
        daily.setNbPeriods(1);
        daily.setPeriodUnit(5);

        CalendarJoin calendarJoin = new CalendarJoin();
        calendarJoin.setJoinType(APPEND);
        calendarJoin.setJoinCalendar1(monthly);
        calendarJoin.setJoinCalendar2(daily);

        CalendarJoin joinOfJoin = new CalendarJoin();
        joinOfJoin.setJoinType(APPEND);
        joinOfJoin.setJoinCalendar1(calendarJoin);
        joinOfJoin.setJoinCalendar2(daily);
        joinOfJoin.setInitDate(startDate);

        Date firstMonthly = joinOfJoin.nextCalendarDate(startDate);
        assertEquals(firstMonthly, DateUtils.newDate(2020, APRIL, 1, 0, 0, 0));

        Date secondDaily = joinOfJoin.nextCalendarDate(firstMonthly);
        assertEquals(secondDaily, DateUtils.newDate(2020, APRIL, 6, 0, 0, 0));

        Date thirdDaily = joinOfJoin.nextCalendarDate(secondDaily);
        assertEquals(thirdDaily, DateUtils.newDate(2020, APRIL, 11, 0, 0, 0));

        Date forthhDaily = joinOfJoin.nextCalendarDate(thirdDaily);
        assertNull(forthhDaily);

        Date fifthDaily = joinOfJoin.nextCalendarDate(forthhDaily);
        assertNull(fifthDaily);
    }

    @Test
    public void joinOfTwoJoinCalendar() {

        Date startDate = DateUtils.newDate(2020, JANUARY, 1, 0, 0, 0);

        CalendarPeriod monthly = new CalendarPeriod();
        monthly.setPeriodLength(3);
        monthly.setNbPeriods(1);
        monthly.setPeriodUnit(2);

        CalendarPeriod daily = new CalendarPeriod();
        daily.setPeriodLength(5);
        daily.setNbPeriods(1);
        daily.setPeriodUnit(5);

        CalendarJoin calendarJoin1 = new CalendarJoin();
        calendarJoin1.setJoinType(APPEND);
        calendarJoin1.setJoinCalendar1(monthly);
        calendarJoin1.setJoinCalendar2(daily);

        CalendarJoin calendarJoin2 = new CalendarJoin();
        calendarJoin2.setJoinType(APPEND);
        calendarJoin2.setJoinCalendar1(daily);
        calendarJoin2.setJoinCalendar2(monthly);

        CalendarJoin joinOfJoin = new CalendarJoin();
        joinOfJoin.setJoinType(APPEND);
        joinOfJoin.setJoinCalendar1(calendarJoin1);
        joinOfJoin.setJoinCalendar2(calendarJoin2);
        joinOfJoin.setInitDate(startDate);

        Date firstMonthly = joinOfJoin.nextCalendarDate(startDate);
        assertEquals(firstMonthly, DateUtils.newDate(2020, APRIL, 1, 0, 0, 0));

        Date secondDaily = joinOfJoin.nextCalendarDate(firstMonthly);
        assertEquals(secondDaily, DateUtils.newDate(2020, APRIL, 6, 0, 0, 0));

        Date thirdDaily = joinOfJoin.nextCalendarDate(secondDaily);
        assertEquals(thirdDaily, DateUtils.newDate(2020, APRIL, 11, 0, 0, 0));

        Date forthhDaily = joinOfJoin.nextCalendarDate(thirdDaily);
        assertEquals(forthhDaily, DateUtils.newDate(2020, JULY, 11, 0, 0, 0));

        Date fifthDaily = joinOfJoin.nextCalendarDate(forthhDaily);
        assertNull(fifthDaily);
    }
}