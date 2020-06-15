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

package org.meveo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.DateUtils.DatePeriodSplit;

public class DateUtilsTest {

    @Test()
    public void isPeriodsOverlap() {

        Date startDate = DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0);
        Date endDate = DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 25, 0, 0, 0);

        // Check with both period dates
        Integer[] days = new Integer[] { 10, 15, 0, 10, 16, 1, 16, 20, 1, 20, 26, 1, 25, 27, 0, 10, 27, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(startDate, endDate, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0), DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period start date
        days = new Integer[] { 10, 15, 1, 10, 16, 1, 16, 20, 1, 20, 26, 1, 25, 27, 0, 10, 27, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(null, endDate, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0), DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period end date
        days = new Integer[] { 10, 15, 0, 10, 16, 1, 16, 20, 1, 20, 26, 1, 25, 27, 1, 10, 27, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(startDate, null, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0), DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no check start date
        days = new Integer[] { null, 10, 0, null, 15, 0, null, 16, 1, null, 25, 1, null, 27, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(startDate, endDate, null, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no check end date
        days = new Integer[] { 10, null, 1, 15, null, 1, 25, null, 0, 25, null, 0 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(startDate, endDate, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0), null);

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period start date, no check start date
        days = new Integer[] { null, 10, 1, null, 25, 1, null, 27, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(null, endDate, null, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period start date, no check end date
        days = new Integer[] { 10, null, 1, 25, null, 0, 27, null, 0 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(null, endDate, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0), null);

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period end date, no check start date
        days = new Integer[] { null, 10, 0, null, 15, 0, null, 27, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(startDate, null, null, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period end date, no check end date
        days = new Integer[] { 10, null, 1, 15, null, 1, 27, null, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(startDate, null, DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0), null);

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

        // Check with no period start and end dates
        days = new Integer[] { 10, 15, 1, 15, null, 1, null, 15, 1, null, null, 1 };

        for (int i = 0; i < days.length; i = i + 3) {
            Integer from = days[i];
            Integer to = days[i + 1];
            boolean shouldMatch = days[i + 2] == 1;
            boolean matched = DateUtils.isPeriodsOverlap(null, null, from != null ? DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0) : null,
                to != null ? DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0) : null);

            Assert.assertTrue("Days " + from + "-" + to + " are incorrect", matched == shouldMatch);
        }

    }

    @SuppressWarnings("deprecation")
    @Test()
    public void getPeriodOverlap() {

        Date startDate = DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 15, 0, 0, 0);
        Date endDate = DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, 25, 0, 0, 0);

        List<Integer[]> days = new ArrayList<>();

        // Check with both period dates
        days.add(new Integer[] { 10, 11, -1, -1, 10, 15, -1, -1, 10, 17, 15, 17, 15, 25, 15, 25, 17, 20, 17, 20, 20, 27, 20, 25, 25, 28, -1, -1, 26, 28, -1, -1 });

        // Check with no end date
        days.add(new Integer[] { 10, null, 15, 25, 15, null, 15, 25, 17, null, 17, 25, 25, null, -1, -1, 26, null, -1, -1 });

        // Check with no start date
        days.add(new Integer[] { null, 11, -1, -1, null, 15, -1, -1, null, 17, 15, 17, null, 25, 15, 25, null, 27, 15, 25 });

        // Check with no dates
        days.add(new Integer[] { null, null, 15, 25 });

        for (Integer[] day : days) {

            for (int i = 0; i < day.length; i = i + 4) {
                Integer from = day[i];
                Integer to = day[i + 1];
                Integer matchStart = day[i + 2];
                Integer matchEnd = day[i + 3];

                DatePeriod matched = DateUtils.getPeriodOverlap(startDate, endDate, from == null ? null : DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, from, 0, 0, 0),
                    to == null ? null : DateUtils.newDate(2015, java.util.Calendar.FEBRUARY, to, 0, 0, 0));

                if (matchStart == -1) {

                    Assert.assertNull("Days " + from + "-" + to + " should not match", matched);
                } else {
                    Assert.assertEquals("Days " + from + "-" + to + " should match start ", matchStart, (Integer) matched.getFrom().getDate());
                    Assert.assertEquals("Days " + from + "-" + to + " should match end ", matchEnd, (Integer) matched.getTo().getDate());
                }
            }

        }
    }

    @Test()
    public void normalizeOverlapingDatePeriods1() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-04-10", "2020-05-31", DateUtils.DATE_PATTERN), "4", "b");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-03", "2020-05-17", DateUtils.DATE_PATTERN), "2", "c");
        DatePeriodSplit period4 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "2", "d");
        DatePeriodSplit period5 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-10", "2020-05-15", DateUtils.DATE_PATTERN), "3", "d");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3, period4, period5);

        Assert.assertEquals(6, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-04-10", "2020-05-01", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", "2020-05-17", DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-17", "2020-05-20", DateUtils.DATE_PATTERN), normalized.get(3).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), normalized.get(4).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-30", "2020-05-31", DateUtils.DATE_PATTERN), normalized.get(5).getPeriod());
        Assert.assertEquals("b", normalized.get(0).getValue());
        Assert.assertEquals("a", normalized.get(1).getValue());
        Assert.assertEquals("c", normalized.get(2).getValue());
        Assert.assertEquals("b", normalized.get(3).getValue());
        Assert.assertEquals("d", normalized.get(4).getValue());
        Assert.assertEquals("b", normalized.get(5).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods2() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-04-10", "2020-05-31", DateUtils.DATE_PATTERN), "4", "b");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-03", "2020-05-17", DateUtils.DATE_PATTERN), "3", "c");
        DatePeriodSplit period4 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "3", "d");
        DatePeriodSplit period5 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-10", "2020-05-15", DateUtils.DATE_PATTERN), "2", "d");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3, period4, period5);

        Assert.assertEquals(8, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-04-10", "2020-05-01", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", "2020-05-10", DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-10", "2020-05-15", DateUtils.DATE_PATTERN), normalized.get(3).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-15", "2020-05-17", DateUtils.DATE_PATTERN), normalized.get(4).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-17", "2020-05-20", DateUtils.DATE_PATTERN), normalized.get(5).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), normalized.get(6).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-30", "2020-05-31", DateUtils.DATE_PATTERN), normalized.get(7).getPeriod());
        Assert.assertEquals("b", normalized.get(0).getValue());
        Assert.assertEquals("a", normalized.get(1).getValue());
        Assert.assertEquals("c", normalized.get(2).getValue());
        Assert.assertEquals("d", normalized.get(3).getValue());
        Assert.assertEquals("c", normalized.get(4).getValue());
        Assert.assertEquals("b", normalized.get(5).getValue());
        Assert.assertEquals("d", normalized.get(6).getValue());
        Assert.assertEquals("b", normalized.get(7).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods3() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-30", DateUtils.DATE_PATTERN), "4", "b");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-03", "2020-05-17", DateUtils.DATE_PATTERN), "2", "c");
        DatePeriodSplit period4 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "2", "d");
        DatePeriodSplit period5 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-10", "2020-05-15", DateUtils.DATE_PATTERN), "3", "d");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3, period4, period5);

        Assert.assertEquals(4, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", "2020-05-17", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-17", "2020-05-20", DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), normalized.get(3).getPeriod());
        Assert.assertEquals("a", normalized.get(0).getValue());
        Assert.assertEquals("c", normalized.get(1).getValue());
        Assert.assertEquals("b", normalized.get(2).getValue());
        Assert.assertEquals("d", normalized.get(3).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods4() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-05", "2020-05-17", DateUtils.DATE_PATTERN), "2", "c");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "2", "d");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3);

        Assert.assertEquals(3, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", "2020-05-17", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals("a", normalized.get(0).getValue());
        Assert.assertEquals("c", normalized.get(1).getValue());
        Assert.assertEquals("d", normalized.get(2).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods5() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", null, DateUtils.DATE_PATTERN), "2", "c");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "2", "d");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3);

        Assert.assertEquals(2, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", null, DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals("a", normalized.get(0).getValue());
        Assert.assertEquals("c", normalized.get(1).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods6() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "2", "d");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-04-10", null, DateUtils.DATE_PATTERN), "2", "c");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3);

        Assert.assertEquals(3, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-04-10", "2020-05-01", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", null, DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals("c", normalized.get(0).getValue());
        Assert.assertEquals("a", normalized.get(1).getValue());
        Assert.assertEquals("c", normalized.get(2).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods7() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), "1", "a");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), "2", "d");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-04-10", null, DateUtils.DATE_PATTERN), "3", "c");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3);

        Assert.assertEquals(5, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-04-10", "2020-05-01", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", "2020-05-20", DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), normalized.get(3).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-30", null, DateUtils.DATE_PATTERN), normalized.get(4).getPeriod());
        Assert.assertEquals("c", normalized.get(0).getValue());
        Assert.assertEquals("a", normalized.get(1).getValue());
        Assert.assertEquals("c", normalized.get(2).getValue());
        Assert.assertEquals("d", normalized.get(3).getValue());
        Assert.assertEquals("c", normalized.get(4).getValue());
    }

    @Test()
    public void normalizeOverlapingDatePeriods8_priorityInt() {

        DatePeriodSplit period1 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), 1, "a");
        DatePeriodSplit period3 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), 2, "d");
        DatePeriodSplit period2 = new DateUtils.DatePeriodSplit(new DatePeriod("2020-04-10", null, DateUtils.DATE_PATTERN), 3, "c");

        List<DatePeriodSplit> normalized = DateUtils.normalizeOverlapingDatePeriods(period1, period2, period3);

        Assert.assertEquals(5, normalized.size());
        Assert.assertEquals(new DatePeriod("2020-04-10", "2020-05-01", DateUtils.DATE_PATTERN), normalized.get(0).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-01", "2020-05-05", DateUtils.DATE_PATTERN), normalized.get(1).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-05", "2020-05-20", DateUtils.DATE_PATTERN), normalized.get(2).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-20", "2020-05-30", DateUtils.DATE_PATTERN), normalized.get(3).getPeriod());
        Assert.assertEquals(new DatePeriod("2020-05-30", null, DateUtils.DATE_PATTERN), normalized.get(4).getPeriod());
        Assert.assertEquals("c", normalized.get(0).getValue());
        Assert.assertEquals("a", normalized.get(1).getValue());
        Assert.assertEquals("c", normalized.get(2).getValue());
        Assert.assertEquals("d", normalized.get(3).getValue());
        Assert.assertEquals("c", normalized.get(4).getValue());
    }
}