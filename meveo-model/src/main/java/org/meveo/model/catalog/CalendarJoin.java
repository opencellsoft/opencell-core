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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents a calendar that operates on two calendars joining them by union or intersection. Union will return the greatest matched period while intersect will return the
 * smallest matched period.
 * 
 * Example: given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return
 * monday and friday as previous and next calendar days and intersection calendar will return 8 and 15 as previous and next calendar days
 * 
 * @author Andrius Karpavicius
 * 
 */
@Entity
@DiscriminatorValue("JOIN")
public class CalendarJoin extends Calendar {

    public enum CalendarJoinTypeEnum {
        UNION, INTERSECT;

        public String getLabel() {
            return "CalendarJoinTypeEnum." + this.name();
        }
    }

    private static final long serialVersionUID = 1L;

    @Column(name = "JOIN_TYPE")
    @Enumerated(EnumType.STRING)
    private CalendarJoinTypeEnum joinType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOIN_CAL_1_ID")
    private Calendar joinCalendar1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOIN_CAL_2_ID")
    private Calendar joinCalendar2;

    public CalendarJoinTypeEnum getJoinType() {
        return joinType;
    }

    public void setJoinType(CalendarJoinTypeEnum joinType) {
        this.joinType = joinType;
    }

    public Calendar getJoinCalendar1() {
        return joinCalendar1;
    }

    public void setJoinCalendar1(Calendar joinCalendar1) {
        this.joinCalendar1 = joinCalendar1;
    }

    public Calendar getJoinCalendar2() {
        return joinCalendar2;
    }

    public void setJoinCalendar2(Calendar joinCalendar2) {
        this.joinCalendar2 = joinCalendar2;
    }

    /**
     * Determines a next calendar date joining next calendar date result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return friday as
     * next calendar days and intersection calendar will return 15 as next calendar days
     * 
     * @param date Date to check
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {

        Date date1 = joinCalendar1.nextCalendarDate(date);
        Date date2 = joinCalendar2.nextCalendarDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;

            } else if (date1 != null && date2 == null) {
                return date1;

            } else if (date1.after(date2)) {
                return date1;

            } else {

                return date2;
            }

            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;

            } else if (date1.before(date2)) {
                return date1;

            } else {
                return date2;
            }
        }

        return null;
    }

    /**
     * Determines a previous calendar date joining previous calendar date result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return monday as
     * previous calendar days and intersection calendar will return 8 as previous calendar days
     * 
     * @param date Date to check
     * @return Previous calendar date.
     */
    public Date previousCalendarDate(Date date) {

        Date date1 = joinCalendar1.previousCalendarDate(date);
        Date date2 = joinCalendar2.previousCalendarDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;

            } else if (date1 != null && date2 == null) {
                return date1;

            } else if (date1.before(date2)) {
                return date1;

            } else {
                return date2;
            }

            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;

            } else if (date1.after(date2)) {
                return date1;

            } else {
                return date2;
            }
        }

        return null;
    }

    /**
     * Determines a previous period end date by joining previousPeriodEndDate result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return friday as
     * previous period end date and intersection calendar will return 15 as period end date
     * 
     * @param date Date to check
     * @return Previous period end date calendar date.
     */
    @Override
    public Date previousPeriodEndDate(Date date) {

        Date date1 = joinCalendar1.previousPeriodEndDate(date);
        Date date2 = joinCalendar2.previousPeriodEndDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;

            } else if (date1 != null && date2 == null) {
                return date1;

            } else if (date1.after(date2)) {
                return date1;

            } else {

                return date2;
            }

            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;

            } else if (date1.before(date2)) {
                return date1;

            } else {
                return date2;
            }
        }

        return null;
    }

    /**
     * Determines a next period start date joining nextPeriodStartDate result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return monday as
     * next period start date and intersection calendar will return 8 as next period start date
     * 
     * @param date Date to check
     * @return Next period start date.
     */
    @Override
    public Date nextPeriodStartDate(Date date) {

        Date date1 = joinCalendar1.nextPeriodStartDate(date);
        Date date2 = joinCalendar2.nextPeriodStartDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;

            } else if (date1 != null && date2 == null) {
                return date1;

            } else if (date1.before(date2)) {
                return date1;

            } else {
                return date2;
            }

            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;

            } else if (date1.after(date2)) {
                return date1;

            } else {
                return date2;
            }
        }

        return null;
    }

    @Override
    public boolean truncDateTime() {
        return joinCalendar1.truncDateTime() && joinCalendar2.truncDateTime();
    }
}