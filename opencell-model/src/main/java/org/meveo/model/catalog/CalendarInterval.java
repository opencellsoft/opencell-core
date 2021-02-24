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
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meveo.model.shared.DateUtils;

/**
 * Represents a time inverval(s) based calendar. Time interval specifies a begin and end times, which can be expressed in the following units: weekdays, month/day and hour/minute.
 * 
 * Example: given a month/day interval of 01/15 - 01/30 a previous calendar date for 2015/01/20 will be 2015/01/15 and next calendar date will be 2015/01/30&lt;br/&gt; given a
 * hour/minute interval of 15:30 - 16:45 a previous calendar date for 2015/01/20 16:00 will be 2015/01/20 15:30 and next calendar date will be 2015/01/20 16:45 &lt;br/&gt; given
 * weekday interval of 1-5 (monday - friday) a previous calendar date for 2015/01/20 will be 2015/01/19 and next calendar date will be 2015/01/23&lt;br/&gt;
 * 
 * @author Andrius Karpavicius
 * 
 */
@Entity
@DiscriminatorValue("INTERVAL")
public class CalendarInterval extends Calendar {

    private static final long serialVersionUID = 1L;

    @Column(name = "interval_type")
    @Enumerated(EnumType.STRING)
    private CalendarIntervalTypeEnum intervalType = CalendarIntervalTypeEnum.DAY;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("intervalBegin")
    private List<CalendarDateInterval> intervals;

    public List<CalendarDateInterval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<CalendarDateInterval> intervals) {
        this.intervals = intervals;
    }

    public CalendarIntervalTypeEnum getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(CalendarIntervalTypeEnum intervalType) {
        this.intervalType = intervalType;
    }

    /**
     * Determines a next calendar date matching any of calendar's time intervals. Example: given a month/day period of 01/15-01/30 a next calendar date for 2015/01/20 will be
     * 2015/01/30
     * 
     * 
     * @param date Date to check
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        boolean found = false;

        // Get the first interval that contains a given date's weekday. To handle a special case that spans to another week (e.g. thursday to monday), the interval end value is
        // adjusted by 7 days.
        if (intervalType == CalendarIntervalTypeEnum.WDAY) {
            int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            if (weekday == 0) {
                weekday = 7;
            }

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date's weekday if interval crosses to another week - and weekday corresponds to another week. E.g checking monday for friday-tuesday period
                int weekdayAdjusted = weekday;
                if (interval.isCrossBoundry() && weekday < interval.getIntervalBegin()) {
                    weekdayAdjusted = weekday + 7;
                }
                if (interval.getIntervalBegin() <= weekdayAdjusted && weekdayAdjusted < interval.getIntervalEndAdjusted()) {

                    calendar.add(java.util.Calendar.DAY_OF_MONTH, interval.getIntervalEndAdjusted() - weekdayAdjusted);
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    calendar.set(java.util.Calendar.MINUTE, 0);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    found = true;
                    break;
                }
            }

            // Get the first interval that contains a given date's date (month and day). To handle a special case that spans to another year (e.g. 12/15 to 01/25), the interval end
            // value is adjusted by 12 month.
        } else if (intervalType == CalendarIntervalTypeEnum.DAY) {

            int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + "" + (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
                    + calendar.get(java.util.Calendar.DAY_OF_MONTH));

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date if interval crosses to another year - and date corresponds to another year. E.g checking 01/03 for 12/15 - 01/30 period
                int monthDayAdjusted = monthDay;
                if (interval.isCrossBoundry() && monthDay < interval.getIntervalBegin()) {
                    monthDayAdjusted = monthDay + 1200;
                }
                if (interval.getIntervalBegin() <= monthDayAdjusted && monthDayAdjusted < interval.getIntervalEndAdjusted()) {
                    int endValue = interval.getIntervalEnd();
                    // Advance to another year if interval end value was adjusted by 12 month
                    if (interval.isCrossBoundry() && monthDay >= interval.getIntervalEnd()) {
                        calendar.add(java.util.Calendar.YEAR, 1);
                    }
                    calendar.set(java.util.Calendar.MONTH, endValue / 100 - 1);
                    calendar.set(java.util.Calendar.DAY_OF_MONTH, endValue % 100);
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    calendar.set(java.util.Calendar.MINUTE, 0);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    found = true;
                    break;
                }
            }

            // Get the first interval that contains a given date's time. To handle a special case that spans to another day (e.g. 23:15 to 00:45), the interval end value is
            // adjusted by 24 hours.
        } else if (intervalType == CalendarIntervalTypeEnum.HOUR) {

            int hourMin = Integer
                .parseInt(calendar.get(java.util.Calendar.HOUR_OF_DAY) + "" + (calendar.get(java.util.Calendar.MINUTE) < 10 ? "0" : "") + calendar.get(java.util.Calendar.MINUTE));

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval crosses to another day - and date time corresponds to another day. E.g checking 23:11 for 23:00 - 01:30 period
                int hourMinAdjusted = hourMin;
                if (interval.isCrossBoundry() && hourMin < interval.getIntervalBegin()) {
                    hourMinAdjusted = hourMin + 2400;
                }

                if (interval.getIntervalBegin() <= hourMinAdjusted && hourMinAdjusted < interval.getIntervalEndAdjusted()) {
                    int endValue = interval.getIntervalEnd();
                    // Advance to another day if interval end value was adjusted by 24 hours
                    if (interval.isCrossBoundry() && hourMin >= interval.getIntervalEnd()) {
                        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    }
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, endValue / 100);
                    calendar.set(java.util.Calendar.MINUTE, endValue % 100);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    found = true;
                    break;
                }
            }

        }

        if (found) {
            return calendar.getTime();
        } else {
            return null;
        }
    }

    /**
     * Determines a previous calendar date matching any of calendar's time intervals. Example: given a month/day period of 01/15-01/30 a previous calendar date for 2015/01/20 will
     * be 2015/01/15
     * 
     * 
     * @param date Date to check
     * @return Previous calendar date.
     */
    public Date previousCalendarDate(Date date) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        boolean found = false;

        // Get the first interval that contains a given date's weekday. To handle a special case that spans to another week (e.g. thursday to monday), the interval end value is
        // adjusted by 7 days.
        if (intervalType == CalendarIntervalTypeEnum.WDAY) {
            int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            if (weekday == 0) {
                weekday = 7;
            }

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date's weekday if interval crosses to another week - and weekday corresponds to another week. E.g checking monday for friday-tuesday period
                int weekdayAdjusted = weekday;
                if (interval.isCrossBoundry() && weekday < interval.getIntervalBegin()) {
                    weekdayAdjusted = weekday + 7;
                }
                if (interval.getIntervalBegin() <= weekdayAdjusted && weekdayAdjusted < interval.getIntervalEndAdjusted()) {

                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1 * (weekdayAdjusted - interval.getIntervalBegin()));
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    calendar.set(java.util.Calendar.MINUTE, 0);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    found = true;
                    break;
                }
            }

            // Get the first interval that contains a given date's date (month and day). To handle a special case that spans to another year (e.g. 12/15 to 01/25), the interval end
            // value is adjusted by 12 month.
        } else if (intervalType == CalendarIntervalTypeEnum.DAY) {

            int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + "" + (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
                    + calendar.get(java.util.Calendar.DAY_OF_MONTH));

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date if interval crosses to another year - and date corresponds to another year. E.g checking 01/03 for 12/15 - 01/30 period
                int monthDayAdjusted = monthDay;
                if (interval.isCrossBoundry() && monthDay < interval.getIntervalBegin()) {
                    monthDayAdjusted = monthDay + 1200;
                }
                if (interval.getIntervalBegin() <= monthDayAdjusted && monthDayAdjusted < interval.getIntervalEndAdjusted()) {
                    // Advance to a previous year if interval end value was adjusted by 12 month
                    int beginValue = interval.getIntervalBegin();
                    if (interval.isCrossBoundry() && monthDay < interval.getIntervalBegin()) {
                        calendar.add(java.util.Calendar.YEAR, -1);
                    }
                    calendar.set(java.util.Calendar.MONTH, beginValue / 100 - 1);
                    calendar.set(java.util.Calendar.DAY_OF_MONTH, beginValue % 100);
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    calendar.set(java.util.Calendar.MINUTE, 0);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    found = true;
                    break;
                }
            }

            // Get the first interval that contains a given date's time. To handle a special case that spans to another day (e.g. 23:15 to 00:45), the interval end value is
            // adjusted by 24 hours.
        } else if (intervalType == CalendarIntervalTypeEnum.HOUR) {

            int hourMin = Integer
                .parseInt(calendar.get(java.util.Calendar.HOUR_OF_DAY) + "" + (calendar.get(java.util.Calendar.MINUTE) < 10 ? "0" : "") + calendar.get(java.util.Calendar.MINUTE));

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval crosses to another day - and date time corresponds to another day. E.g checking 01:11 for 23:00 - 01:30 period
                int hourMinAdjusted = hourMin;
                if (interval.isCrossBoundry() && hourMin < interval.getIntervalBegin()) {
                    hourMinAdjusted = hourMin + 2400;
                }

                if (interval.getIntervalBegin() <= hourMinAdjusted && hourMinAdjusted < interval.getIntervalEndAdjusted()) {
                    // Advance to a previous day if interval end value was adjusted by 24 hours
                    int beginValue = interval.getIntervalBegin();
                    if (interval.isCrossBoundry() && hourMin < interval.getIntervalBegin()) {
                        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
                    }
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, beginValue / 100);
                    calendar.set(java.util.Calendar.MINUTE, beginValue % 100);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    found = true;
                    break;
                }
            }

        }

        if (found) {
            return calendar.getTime();
        } else {
            return null;
        }
    }

    @Override
    public Date previousPeriodEndDate(Date date) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        boolean found = false;

        // Calculate the previous earliest interval end time given current date's time. To handle a special case that calculation crosses to another week (e.g. checking monday for
        // tuesday to wednesday period), the interval end value is adjusted by -7 days.
        if (intervalType == CalendarIntervalTypeEnum.WDAY) {

            int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            if (weekday == 0) {
                weekday = 7;
            }

            int earliestWeekday = -10000000;
            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval is on another week - and date time corresponds to previous week. E.g checking monday for tuesday to wednesday period
                int intervalEndAdjusted = interval.getIntervalEnd();
                if (weekday < interval.getIntervalEnd()) {
                    intervalEndAdjusted = intervalEndAdjusted - 7;

                }
                // Remember the earliest value
                if (intervalEndAdjusted <= weekday && earliestWeekday < intervalEndAdjusted) {
                    earliestWeekday = intervalEndAdjusted;
                }

            }
            if (earliestWeekday != 10000000) {

                calendar.add(java.util.Calendar.DAY_OF_MONTH, -1 * Math.abs(earliestWeekday - weekday));
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                calendar.set(java.util.Calendar.MINUTE, 0);
                calendar.set(java.util.Calendar.SECOND, 0);
                calendar.set(java.util.Calendar.MILLISECOND, 0);
                found = true;
            }

            // Calculate the previous earliest interval end time given current date's time. To handle a special case that calculation crosses to another year (e.g. checking 01/10
            // for 01/15 to 03/25 period), the interval begin value is adjusted by -12 month.
        } else if (intervalType == CalendarIntervalTypeEnum.DAY) {

            int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + "" + (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
                    + calendar.get(java.util.Calendar.DAY_OF_MONTH));

            int earliestMonthDay = -10000000;
            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval is on another year - and date time corresponds to previous year. E.g checking 01/10 for 01/15 to 03/25 period
                int intervalEndAdjusted = interval.getIntervalEnd();
                if (monthDay < interval.getIntervalEnd()) {
                    intervalEndAdjusted = intervalEndAdjusted - 1200;

                }
                // Remember the earliest value
                if (intervalEndAdjusted <= monthDay && earliestMonthDay < intervalEndAdjusted) {
                    earliestMonthDay = intervalEndAdjusted;
                }

            }
            if (earliestMonthDay != 10000000) {

                // Advance to a previous year if interval end value was adjusted by -12 month
                int beginValue = earliestMonthDay;
                if (earliestMonthDay < 100) {
                    calendar.add(java.util.Calendar.YEAR, -1);
                    beginValue = earliestMonthDay + 1200;
                }
                calendar.set(java.util.Calendar.MONTH, beginValue / 100 - 1);
                calendar.set(java.util.Calendar.DAY_OF_MONTH, beginValue % 100);
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                calendar.set(java.util.Calendar.MINUTE, 0);
                calendar.set(java.util.Calendar.SECOND, 0);
                calendar.set(java.util.Calendar.MILLISECOND, 0);
                found = true;
            }

            // Calculate the previous earliest interval end time given current date's time. To handle a special case that calculation crosses to another day (e.g. 01:15 to 10:45
            // when checking for 01:00), the interval end value is adjusted by -24 hours.
        } else if (intervalType == CalendarIntervalTypeEnum.HOUR) {

            int hourMin = Integer
                .parseInt(calendar.get(java.util.Calendar.HOUR_OF_DAY) + "" + (calendar.get(java.util.Calendar.MINUTE) < 10 ? "0" : "") + calendar.get(java.util.Calendar.MINUTE));

            int earliestHourMin = -10000000;
            for (CalendarDateInterval interval : intervals) {

                // Adjust interval end time if interval end time is on another day - and date time corresponds to next day. E.g checking 01:00 for 01:15 to 10:45 period
                int intervalEndAdjusted = interval.getIntervalEnd();
                if (hourMin < interval.getIntervalEnd()) {
                    intervalEndAdjusted = intervalEndAdjusted - 2400;

                }

                // Remember the earliest value
                if (intervalEndAdjusted <= hourMin && earliestHourMin < intervalEndAdjusted) {
                    earliestHourMin = intervalEndAdjusted;
                }

            }
            if (earliestHourMin != -10000000) {
                // Advance to a previous day if interval end time was adjusted by -24 hours
                int beginValue = earliestHourMin;
                if (earliestHourMin < 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
                    beginValue = earliestHourMin + 2400;
                }
                calendar.set(java.util.Calendar.HOUR_OF_DAY, beginValue / 100);
                calendar.set(java.util.Calendar.MINUTE, beginValue % 100);
                calendar.set(java.util.Calendar.SECOND, 0);
                calendar.set(java.util.Calendar.MILLISECOND, 0);
                found = true;
            }
        }

        if (found) {
            return calendar.getTime();
        } else {
            return null;
        }
    }

    @Override
    public Date nextPeriodStartDate(Date date) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        boolean found = false;

        // Get the next earliest interval begin time given current date's time. To handle a special case that calculation crosses to another week (e.g. checking friday for monday
        // to wednesday period), the interval begin value is adjusted by 7 days.
        if (intervalType == CalendarIntervalTypeEnum.WDAY) {

            int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            if (weekday == 0) {
                weekday = 7;
            }

            int earliestWeekday = 10000000;
            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval is on another week - and date time corresponds to previous week. E.g checking friday for monday to wednesday period
                int intervalBeginAdjusted = interval.getIntervalBegin();
                if (weekday >= interval.getIntervalBegin()) {
                    intervalBeginAdjusted = intervalBeginAdjusted + 7;

                }
                // Remember the earliest value
                if (intervalBeginAdjusted >= weekday && earliestWeekday >= intervalBeginAdjusted) {
                    earliestWeekday = intervalBeginAdjusted;
                }

            }
            if (earliestWeekday != 10000000) {

                calendar.add(java.util.Calendar.DAY_OF_MONTH, 1 * (earliestWeekday - weekday));
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                calendar.set(java.util.Calendar.MINUTE, 0);
                calendar.set(java.util.Calendar.SECOND, 0);
                calendar.set(java.util.Calendar.MILLISECOND, 0);
                found = true;
            }

            // Get the next earliest interval begin time given current date's time. To handle a special case that calculation crosses to another year (e.g. checking 05/20 for 01/15
            // to 03/25 period), the interval begin value is adjusted by 12 month.
        } else if (intervalType == CalendarIntervalTypeEnum.DAY) {

            int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + "" + (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
                    + calendar.get(java.util.Calendar.DAY_OF_MONTH));

            int earliestMonthDay = 10000000;
            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval is on another year - and date time corresponds to previous year. E.g checking 05/20 for 01/15 to 03/25 period
                int intervalBeginAdjusted = interval.getIntervalBegin();
                if (monthDay >= interval.getIntervalBegin()) {
                    intervalBeginAdjusted = intervalBeginAdjusted + 1200;

                }
                // Remember the earliest value
                if (intervalBeginAdjusted >= monthDay && earliestMonthDay >= intervalBeginAdjusted) {
                    earliestMonthDay = intervalBeginAdjusted;
                }

            }
            if (earliestMonthDay != 10000000) {

                // Advance to a next year if interval begin value was adjusted by 12 month
                int beginValue = earliestMonthDay;
                if (earliestMonthDay > 1300) {
                    calendar.add(java.util.Calendar.YEAR, +1);
                    beginValue = earliestMonthDay - 1200;
                }
                calendar.set(java.util.Calendar.MONTH, beginValue / 100 - 1);
                calendar.set(java.util.Calendar.DAY_OF_MONTH, beginValue % 100);
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                calendar.set(java.util.Calendar.MINUTE, 0);
                calendar.set(java.util.Calendar.SECOND, 0);
                calendar.set(java.util.Calendar.MILLISECOND, 0);
                found = true;
            }

            // Get the next earliest interval begin time given current date's time. To handle a special case that calculation crosses to another day (e.g. 01:15 to 10:45 when
            // checking for 23:00), the interval begin value is adjusted by 24 hours.
        } else if (intervalType == CalendarIntervalTypeEnum.HOUR) {

            int hourMin = Integer
                .parseInt(calendar.get(java.util.Calendar.HOUR_OF_DAY) + "" + (calendar.get(java.util.Calendar.MINUTE) < 10 ? "0" : "") + calendar.get(java.util.Calendar.MINUTE));

            int earliestHourMin = 10000000;
            for (CalendarDateInterval interval : intervals) {

                // Adjust interval begin time if interval begin time is on another day - and date time corresponds to previous day. E.g checking 23:00 for 02:00 - 11:30 period
                int intervalBeginAdjusted = interval.getIntervalBegin();
                if (hourMin >= interval.getIntervalBegin()) {
                    intervalBeginAdjusted = intervalBeginAdjusted + 2400;

                }

                // Remember the earliest value
                if (intervalBeginAdjusted >= hourMin && earliestHourMin >= intervalBeginAdjusted) {
                    earliestHourMin = intervalBeginAdjusted;
                }

            }
            if (earliestHourMin != 10000000) {
                // Advance to a next day if interval begin value was adjusted by 24 hours
                int beginValue = earliestHourMin;
                if (earliestHourMin >= 2400) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    beginValue = earliestHourMin - 2400;
                }
                calendar.set(java.util.Calendar.HOUR_OF_DAY, beginValue / 100);
                calendar.set(java.util.Calendar.MINUTE, beginValue % 100);
                calendar.set(java.util.Calendar.SECOND, 0);
                calendar.set(java.util.Calendar.MILLISECOND, 0);
                found = true;
            }
        }

        if (found) {
            return calendar.getTime();
        } else {
            return null;
        }
    }

    @Override
    public Date truncateDateTime(Date dateToTruncate) {
        if (intervalType != CalendarIntervalTypeEnum.HOUR) {
            return DateUtils.setTimeToZero(dateToTruncate);
        } else {
            return dateToTruncate;
        }
    }
}