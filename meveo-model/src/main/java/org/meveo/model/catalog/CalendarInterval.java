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
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * Represents a time inverval(s) based calendar. Time interval specifies a begin and end times, which can be expressed in the following units: weekdays, month/day and hour/minute.
 * 
 * Example: given a month/day interval of 01/15 - 01/30 a previous calendar date for 2015/01/20 will be 2015/01/15 and next calendar date will be 2015/01/30<br/>
 * given a hour/minute interval of 15:30 - 16:45 a previous calendar date for 2015/01/20 16:00 will be 2015/01/20 15:30 and next calendar date will be 2015/01/20 16:45 <br/>
 * given weekday interval of 1-5 (monday - friday) a previous calendar date for 2015/01/20 will be 2015/01/19 and next calendar date will be 2015/01/23<br/>
 * 
 * @author Andrius Karpavicius
 * 
 */
@Entity
@DiscriminatorValue("INTERVAL")
public class CalendarInterval extends Calendar {

    public enum CalendarIntervalTypeEnum {
        DAY, HOUR, WDAY;

        public String getLabel() {
            return "CalendarIntervalTypeEnum." + this.name();
        }
    }

    private static final long serialVersionUID = 1L;

    @Column(name = "INTERVAL_TYPE")
    @Enumerated(EnumType.STRING)
    private CalendarIntervalTypeEnum intervalType = CalendarIntervalTypeEnum.DAY;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
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

            int hourMin = Integer.parseInt(calendar.get(java.util.Calendar.HOUR_OF_DAY) + "" + (calendar.get(java.util.Calendar.MINUTE) < 10 ? "0" : "")
                    + calendar.get(java.util.Calendar.MINUTE));

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

            int hourMin = Integer.parseInt(calendar.get(java.util.Calendar.HOUR_OF_DAY) + "" + (calendar.get(java.util.Calendar.MINUTE) < 10 ? "0" : "")
                    + calendar.get(java.util.Calendar.MINUTE));

            for (CalendarDateInterval interval : intervals) {

                // Adjust given date time if interval crosses to another day - and date time corresponds to another day. E.g checking 23:11 for 23:00 - 01:30 period
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

    /**
     * Indicate to truncate time values for day and weekday calendars
     */
    @Override
    public boolean truncDateTime() {
        return intervalType != CalendarIntervalTypeEnum.HOUR;
    }
}