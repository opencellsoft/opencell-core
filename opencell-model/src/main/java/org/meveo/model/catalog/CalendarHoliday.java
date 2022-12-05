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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a holiday period to be used by a banking calendar that excludes holidays. The period is expressed in months / days
 * 
 * @author hznibar
 * 
 */
@Entity
@Cacheable
@ExportIdentifier({ "calendar.code", "holidayBegin", "holidayEnd" })
@Table(name = "cat_calendar_holiday")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_calendar_holiday_seq"), })
public class CalendarHoliday extends BaseEntity implements Comparable<CalendarHoliday> {

    private static final long serialVersionUID = -8419267880869260329L;

    /**
     * Specified holiday start.
     */
    @Column(name = "holiday_begin", nullable = false)
    @NotNull
    private int holidayBegin;

    /**
     * Specified holiday end. 
     */
    @Column(name = "holiday_end", nullable = false)
    @NotNull
    private int holidayEnd;

    /**
     * Holiday based type calendar
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private CalendarBanking calendar;

    public CalendarHoliday() {
        super();
    }

    public CalendarHoliday(CalendarBanking calendar, int holidayBegin, int holidayEnd) {
        super();
        this.calendar = calendar;
        this.holidayBegin = holidayBegin;
        this.holidayEnd = holidayEnd;
    }

    public CalendarBanking getCalendar() {
        return calendar;
    }

    public void setCalendar(CalendarBanking calendar) {
        this.calendar = calendar;
    }

    public int getHolidayBegin() {
        return holidayBegin;
    }

    public void setHolidayBegin(Integer holidayBegin) {
        this.holidayBegin = holidayBegin;
    }

    public int getHolidayEnd() {
        return holidayEnd;
    }

    public void setHolidayEnd(int holidayEnd) {
        this.holidayEnd = holidayEnd;
    }

    public boolean isCrossBoundry() {
        return holidayEnd < holidayBegin;
    }
    
	public boolean isHolidayDate(Date d) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + ""
				+ (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
				+ calendar.get(java.util.Calendar.DAY_OF_MONTH));
		int monthDayAdjusted = monthDay;
		if (isCrossBoundry() && monthDay < getHolidayBegin()) {
			monthDayAdjusted = monthDay + 1200;
		}
		return monthDayAdjusted >= getHolidayBegin() && monthDayAdjusted <= getHolidayEndAdjusted();
	}

	/**
	 * To handle special case when holiday spans to another year (e.g. 12/15 to
	 * 01/25), holiday's end date is adjusted accordingly:
	 * 
	 * when holiday spans to another year (e.g. 12/15 to 01/25), the holiday end
	 * value is adjusted by 12 month.
	 * 
	 * @return Adjusted end holiday value
	 */
	public int getHolidayEndAdjusted() {
		if (holidayEnd < holidayBegin) {
			return holidayEnd + 1200;
		}
		return holidayEnd;
	}

	public String getHolidayAsString() {
		String separator = "/";
		return StringUtils.leftPad(Integer.toString(holidayBegin / 100), 2, '0') + separator
				+ StringUtils.leftPad(Integer.toString(holidayBegin % 100), 2, '0') + " - "
				+ StringUtils.leftPad(Integer.toString(holidayEnd / 100), 2, '0') + separator
				+ StringUtils.leftPad(Integer.toString(holidayEnd % 100), 2, '0');
	}

    @Override
    public String toString() {
        return getHolidayAsString();
    }

    @Override
    public int compareTo(CalendarHoliday other) {
        return holidayBegin - other.getHolidayBegin();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CalendarHoliday)) {
            return false;
        }

        CalendarHoliday other = (CalendarHoliday) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        return holidayBegin == other.getHolidayBegin() && holidayEnd == other.getHolidayEnd();
    }

    /**
     * get the next day after holiday period. 
     * @param date
     * @return the same date if it is not a holiday period,  the next date after holiday period if it's a holiday date.
     */
	public Date getDateAfterHoliday(Date date) {
		// if the date is not a holiday, we return the same date
		if (!isHolidayDate(date)) {
			return date;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + ""
				+ (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
				+ calendar.get(java.util.Calendar.DAY_OF_MONTH));

		int endValue = getHolidayEnd();
		// Advance to another year if holiday end value was adjusted by 12 month
		if (isCrossBoundry() && monthDay > getHolidayEnd()) {
			calendar.add(java.util.Calendar.YEAR, 1);
		}
		calendar.set(java.util.Calendar.MONTH, endValue / 100 - 1);
		calendar.set(java.util.Calendar.DAY_OF_MONTH, (endValue % 100));
		calendar.add(Calendar.DATE, 1); // the next date after holiday period
		return calendar.getTime();
	}

	/**
     * get the last day before holiday period. 
     * @param date
     * @return the same date if it is not a holiday period,  the last date before holiday period if it's a holiday date.
     */
	public Date getDateBeforeHoliday(Date date) {
		// if the date is not a holiday, we return the same date
		if (!isHolidayDate(date)) {
			return date;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + ""
				+ (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
				+ calendar.get(java.util.Calendar.DAY_OF_MONTH));

		int beginValue = getHolidayBegin();
		// Advance to another year if holiday end value was adjusted by 12 month
		if (isCrossBoundry() && monthDay < getHolidayBegin()) {
			calendar.add(java.util.Calendar.YEAR, -1);
		}
		calendar.set(java.util.Calendar.MONTH, beginValue / 100 - 1);
		calendar.set(java.util.Calendar.DAY_OF_MONTH, (beginValue % 100));
		calendar.add(Calendar.DATE, -1); // the last date before holiday period
		return calendar.getTime();
	}
}