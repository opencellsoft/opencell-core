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

import javax.persistence.EntityManager;

import org.junit.Assert;

import org.junit.Test;
import org.meveo.model.BeforeDBTest;
import org.meveo.model.shared.DateUtils;

public class CalendarTest {

    // @Test(groups = { "db" })
    public void testNextCalendarDate() {
        EntityManager em = BeforeDBTest.factory.createEntityManager();
        Calendar cal = em.find(Calendar.class, 1L);
        Date nextDate = cal.nextCalendarDate(DateUtils.newDate(2010, java.util.Calendar.JANUARY, 5, 0, 0, 0));
        // Assert.assertEquals(nextDate, DateUtils.newDate(2010,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        // Date nextDate2 = cal.nextCalendarDate(DateUtils.newDate(2010,
        // java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        // Assert.assertEquals(nextDate2, DateUtils.newDate(2011,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        // Date nextDate3 = cal.nextCalendarDate(DateUtils.newDate(2010,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        // Assert.assertEquals(nextDate3, DateUtils.newDate(2011,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        em.close();
    }

    // @Test(groups = { "db" })
    public void testPreviousCalendarDate() {
        EntityManager em = BeforeDBTest.factory.createEntityManager();
        Calendar cal = em.find(Calendar.class, 1L);
        Date nextDate = cal.previousCalendarDate(DateUtils.newDate(2010, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        // Assert.assertEquals(nextDate, DateUtils.newDate(2010,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        // Date nextDate2 = cal.previousCalendarDate(DateUtils.newDate(2010,
        // java.util.Calendar.JANUARY, 5, 0, 0, 0));
        // Assert.assertEquals(nextDate2, DateUtils.newDate(2009,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        // Date nextDate3 = cal.previousCalendarDate(DateUtils.newDate(2010,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        // Assert.assertEquals(nextDate3, DateUtils.newDate(2009,
        // java.util.Calendar.JANUARY, 31, 0, 0, 0));
        em.close();
    }

    @Test()
    public void testOnePeriodCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(1);
        calendar.setPeriodLength(20);
        calendar.setStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0));

        try {
            calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 21, 0, 0, 0), nextDate);

    }

    @Test()
    public void testMultiPeriodCalendar() {

        CalendarPeriod calendar = new CalendarPeriod();
        calendar.setNbPeriods(5);
        calendar.setPeriodLength(7);
        calendar.setStartDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0));

        try {
            calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 1, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 9, 0, 0, 0));
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        Date prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        Date nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 8, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 9, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 12, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 16, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 19, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 22, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 23, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 26, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 1, 0, 0, 0), nextDate);

        prevDate = calendar.previousCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 2, 0, 0, 0), prevDate);

        nextDate = calendar.nextCalendarDate(DateUtils.newDate(2015, java.util.Calendar.MARCH, 5, 0, 0, 0));
        Assert.assertEquals(DateUtils.newDate(2015, java.util.Calendar.MARCH, 8, 0, 0, 0), nextDate);

    }
}
