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
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.meveo.model.shared.DateUtils;

@Entity
@DiscriminatorValue("YEARLY")
public class CalendarYearly extends Calendar {

	private static final long serialVersionUID = 1L;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_CALENDAR_DAYS", joinColumns = @JoinColumn(name = "CALENDAR_ID"), inverseJoinColumns = @JoinColumn(name = "DAY_ID"))
	private List<DayInYear> days;

	
	public List<DayInYear> getDays() {
		return days;
	}

	public void setDays(List<DayInYear> days) {
		this.days = days;
	}

	/**
	 * Checks for next calendar date. If not found in this year checks next
	 * years dates. Calendar has list of days (month/day), so if calendar has at
	 * least one date it will be found in this or next year. For example today
	 * is 2010.12.06. Calendar has only one day - 12.05. So nextCalendarDate
	 * will be found for 2011.12.05.
	 * 
	 * @param date
	 *            Current date.
	 * @return Next calendar date.
	 */
	public Date nextCalendarDate(Date date) {
		Date future = DateUtils.newDate(3000, 0, 1, 0, 0, 0);
		Date result = future;
		int currentYear = DateUtils.getYearFromDate(date);
		for (DayInYear dayInYear : days) {
			Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1,
					dayInYear.getDay(), 0, 0, 0);
			if (d.after(date) && d.before(result)) {
				result = d;
			}
		}
		if (result == future) { // if result did not change
			currentYear++; // check for date in next year
			for (DayInYear dayInYear : days) {
				Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1,
						dayInYear.getDay(), 0, 0, 0);
				if (d.after(date) && d.before(result)) {
					result = d;
				}
			}
		}
		if (result == future) {
			throw new IllegalStateException("Next calendar date could not be found!");
		}
		return result;
	}

	/**
	 * Checks for previous calendar date. If not found in this year checks
	 * previous years dates. Calendar has list of days (month/day), so if
	 * calendar has at least one date it will be found in this or next year. For
	 * example today is 2010.12.06. Calendar has only one day - 12.07. So
	 * previousCalendarDate will be found for 2009.12.07.
	 * 
	 * @param date
	 *            Current date.
	 * @return Next calendar date.
	 */
	public Date previousCalendarDate(Date date) {
		Date past = DateUtils.newDate(1970, 0, 1, 0, 0, 0);
		Date result = past;
		int currentYear = DateUtils.getYearFromDate(date);
		for (DayInYear dayInYear : days) {
			Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1,
					dayInYear.getDay(), 0, 0, 0);
			if ((d.before(date) || d.equals(date)) && d.after(result)) {
				result = d;
			}
		}
		if (result == past) { // if result did not change
			currentYear--; // check for date in previous year
			for (DayInYear dayInYear : days) {
				Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1,
						dayInYear.getDay(), 0, 0, 0);
				if ((d.before(date) || d.equals(date)) && d.after(result)) {
					result = d;
				}
			}
		}
		if (result == past) {
			throw new IllegalStateException("Previous calendar date could not be found!");
		}
		return result;
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