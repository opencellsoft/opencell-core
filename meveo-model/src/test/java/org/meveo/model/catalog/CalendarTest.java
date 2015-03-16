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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.catalog.CalendarInterval.CalendarIntervalTypeEnum;
import org.meveo.model.catalog.CalendarJoin.CalendarJoinTypeEnum;
import org.meveo.model.shared.DateUtils;

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

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 2, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 2, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 31, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 31, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 1, 0, 0, 0), nextDate);

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

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 11, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 11, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 9, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 9, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 20, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 10, 0, 0, 0), nextDate);

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

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 0, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 15, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 15, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 2, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 23, 15, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 23, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 23, 15, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 11, 0, 0, 0), nextDate);
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

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 16, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 1, 16, 1));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 2, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 14, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 9, 23, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 14, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 23, 25, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 23, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 10, 23, 25, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 11, 0, 15, 0), nextDate);
    }

    @Test()
    public void testOnePeriodInDaysCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(20);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 15, 12, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(1);
        calendar.setPeriodUnit(java.util.Calendar.MONTH);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 1, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 1, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInHourCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(3);
        calendar.setPeriodUnit(java.util.Calendar.HOUR_OF_DAY);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 58));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 58));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 18, 12, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInMinuteCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(3);
        calendar.setPeriodUnit(java.util.Calendar.MINUTE);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 58));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 58));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 58));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 14, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 14, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 15, 59), nextDate);

    }

    @Test()
    public void testOnePeriodInSecondCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(3);
        calendar.setPeriodUnit(java.util.Calendar.SECOND);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 49));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 49));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 52));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 52));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 51));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 50), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 51));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 12, 53), nextDate);

    }

    @Test()
    public void testMultiPeriodInMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(5);
        calendar.setPeriodLength(7);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59), nextDate);

    }

    @Test()
    public void testMultiPeriodInMinuteCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(6);
        calendar.setPeriodLength(10);
        calendar.setPeriodUnit(java.util.Calendar.MINUTE);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 58));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 58));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 59));
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 59));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 58));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 0, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 58));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 10, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 10, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 20, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 34, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 30, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 34, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 40, 59), nextDate);

    }

    @Test()
    public void testZeroPeriodInMonthCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(0);
        calendar.setPeriodLength(7);
        calendar.setInitDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 16, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 15, 12, 59), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 15, 12, 59), nextDate);

    }

    @Test()
    public void testSimpleWeekdayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1, 5)); // monday through friday

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0)); // saturday
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0)); // saturday
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

    }

    @Test()
    public void testCrossWeekdayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 5, 2)); // friday through tuesday

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0)); // friday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 0, 0, 0), prevDate);
    }

    @Test()
    public void testfullWeekdayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1, 1)); // monday through monday

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 0, 0, 0)); // tuesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);
    }

    @Test()
    public void testfullWeekdayIntervalCalendar2() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 3, 3)); // wednesday through wednesday

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0)); // wednesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0)); // tuesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 10, 0, 0, 0)); // tuesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 0, 0, 0), prevDate);
    }

    @Test()
    public void testSimpleDayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1015, 1231)); // 10/15 to 12/31

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 30, 0, 0, 0)); // 12/30
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 30, 0, 0, 0)); // 12/30
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

    }

    @Test()
    public void testCrossDayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1015, 131)); // 10/15 to 01/31

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 31, 0, 0, 0)); // 01/31
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 31, 0, 0, 0)); // 01/31
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 30, 0, 0, 0)); // 01/30
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 30, 0, 0, 0)); // 01/30
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);
    }

    @Test()
    public void testFullDayIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 101, 101)); // 01/01 to 01/01

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.NOVEMBER, 5, 0, 0, 0)); // 11/05
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 2, 0, 0, 0)); // 01/30
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 2, 0, 0, 0)); // 01/30
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), prevDate);
    }

    @Test()
    public void testFullDayIntervalCalendar2() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1015, 1015)); // 10/15 to 10/15

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 09/07
        Assert.assertEquals(DateUtils.newDate(2014, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2014, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 16, 0, 0, 0)); // 10/16
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 16, 0, 0, 0)); // 10/16
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0)); // 10/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 14, 0, 0, 0)); // 10/14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 14, 0, 0, 0)); // 10/14
        Assert.assertEquals(DateUtils.newDate(2014, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.OCTOBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 15, 0, 0, 0), prevDate);
    }

    @Test()
    public void testSimpleHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 515, 1731)); // 05:15 to 17:31

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 0, 0)); // 04:00
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 0, 0)); // 04:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 11, 15, 0)); // 11:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 11, 15, 0)); // 11:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 30, 59)); // 17:30
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 30, 59)); // 17:30
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), prevDate);

    }

    @Test()
    public void testCrossHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1731, 515)); // 17:31 to 05:15

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 16, 0, 0)); // 16:00
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 16, 0, 0)); // 16:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 5, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0)); // 17:31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 17, 31, 0), prevDate);

    }

    @Test()
    public void testFullHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 0, 0)); // 00:00 to 00:00

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 59)); // 23:59:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 59)); // 23:59:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0)); // 05:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

    }

    @Test()
    public void testFullHourIntervalCalendar2() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1000, 1000)); // 10:00 to 10:00

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 9, 59, 59)); // 09:59:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 9, 59, 59)); // 23:59:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 1)); // 10:00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 1)); // 10:00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 18, 15, 0)); // 18:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 1)); // 00:00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 10, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 14, 59)); // 05:14
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 10, 0, 0), prevDate);

    }

    @Test()
    public void testZeroHourIntervalCalendar() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 0, 15)); // 00:00 to 00:15

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0)); // 01:00
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0)); // 01:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 00:15
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 00:15
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 14, 59)); // 00:14:49
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 14, 59)); // 00:14:49
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 3, 59)); // 00:03
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 3, 59)); // 00:03
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), prevDate);

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
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 7, 0, 0)); // saturday 07:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 14:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 4:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 13, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 20, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 13, 0, 0), prevDate);

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
        Assert.assertNull(nextDate);

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 9, 0, 0)); // saturday 09:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        Assert.assertNull(nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        Assert.assertNull(prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 8, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), prevDate);

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
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 35, 0)); // 00:35
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 5, 15, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 7, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 18, 0)); // 23:18
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 18, 0)); // 23:18
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 17, 31, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 23, 59, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 1, 0)); // 00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 1, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 1, 0)); // 00:01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 23, 59, 0), prevDate);
    }

    @Test()
    public void testCrossHourIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 2359, 30)); // 23:59 to 00:30

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 4, 8, 0)); // 04:08
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 0, 0)); // 00:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 0, 30, 0), prevDate);

        calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.HOUR);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 2315, 30)); // 23:15 to 00:30

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 8, 23, 15, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 59, 0)); // 23:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 30, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 0:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 23, 15, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 7, 0, 15, 0)); // 0:15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.SEPTEMBER, 6, 0, 30, 0), prevDate);
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

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2014, java.util.Calendar.DECEMBER, 31, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // 02/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // 02/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0)); // 12/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0)); // 12/15
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.MAY, 21, 0, 0, 0)); // 05/21
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JUNE, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.MAY, 21, 0, 0, 0)); // 05/21
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.APRIL, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.JANUARY, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), prevDate);
    }

    @Test()
    public void testCrossDayIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1231, 115)); // 12/31 to 01/15

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 1, 0, 0, 0)); // 01/01
        Assert.assertEquals(DateUtils.newDate(2014, java.util.Calendar.JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.OCTOBER, 31, 0, 0, 0)); // 10/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 15, 0, 0, 0), prevDate);

        calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.DAY);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 1215, 115)); // 12/15 to 01/15

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2016, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 31, 0, 0, 0)); // 12/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 15, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.JUNE, 31, 0, 0, 0)); // 6/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.DECEMBER, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.JUNE, 31, 0, 0, 0)); // 6/31
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 15, 0, 0, 0), prevDate);
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
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 13, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 14, 0, 0, 0), prevDate);
    }

    @Test()
    public void testCrossWeekdayIntervalCalendarNextPreviousPeriod() {

        CalendarInterval calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 7, 3)); // sunday to wednesday

        Date nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0)); // wednesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0)); // wednesday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0)); // thursday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 18, 0, 0, 0), prevDate);

        calendar = new CalendarInterval();
        calendar.setIntervalType(CalendarIntervalTypeEnum.WDAY);
        intervals = new ArrayList<CalendarDateInterval>();
        calendar.setIntervals(intervals);
        intervals.add(new CalendarDateInterval(calendar, 5, 3)); // friday to wednesday

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 20, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0)); // sunday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 20, 0, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0)); // monday
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 11, 0, 0, 0), prevDate);
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
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 8, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 7, 0, 0)); // saturday 07:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0)); // friday 20:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 21, 59, 59)); // thursday 21:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 21, 59, 59)); // thursday 21:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 20, 0, 0), prevDate);

        // Tests bellow should not be tested as time tested falls within a valid period. They work when when period do not overlapp. When period overlapp it does not take
        // continuance into calculation.

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 8, 0, 0)); // monday 08:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 13, 0, 0), nextDate); // Should really be friday 6 8:00 as periods overlapp

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 13, 0, 0), nextDate); // Should really be friday 6 8:00 as periods overlapp

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 12, 59, 59)); // thursday 12:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 14:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 14, 0, 0)); // monday 4:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 20, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0)); // monday 15:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 15, 0, 0), prevDate);// Should really be sunday 1 20:00 as periods overlap

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 16, 59, 59)); // thursday 16:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), prevDate); // Should really be wednesday 4 20:00 as periods overlap

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
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        Date prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 7, 9, 0, 0)); // saturday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 6, 9, 0, 0)); // friday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0)); // thursday 15:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 15, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 7, 0, 0)); // monday 07:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.JANUARY, 29, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 9, 0, 0)); // thursday 09:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 14, 59, 59)); // thursday 14:59
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 4, 15, 0, 0), prevDate);

        nextDate = calendar.nextPeriodStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 3, 8, 0, 0), nextDate);

        prevDate = calendar.previousPeriodEndDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 10, 0, 0)); // monday 10:00
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 29, 15, 0, 0), prevDate);

    }
}