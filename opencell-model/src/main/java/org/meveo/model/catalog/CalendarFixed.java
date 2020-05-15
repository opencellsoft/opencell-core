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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
@DiscriminatorValue("FIXED")
public class CalendarFixed extends Calendar {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "calendarFixed", cascade = { CascadeType.ALL }, orphanRemoval=true)
	@OrderBy("fixedDate")
	private List<FixedDate> fixedDates;

	public CalendarFixed() {
		fixedDates = new ArrayList<>();
	}

	public List<FixedDate> getFixedDates() {
		return fixedDates;
	}

	public void setFixedDates(List<FixedDate> fixedDates) {
		this.fixedDates = fixedDates;
	}

	public void addFixedDate(Date date) {
		FixedDate fixedDate = new FixedDate(date);
		fixedDate.setCalendarFixed(this);
		fixedDates.add(fixedDate);
	}

	public void removeFixedDate(Date date) {
		FixedDate fixedDate = new FixedDate(date);
		fixedDates.remove(fixedDate);
	}

	/**
	 * Checks for next calendar date.
	 * 
	 * @param date Current date.
	 * @return Next calendar date.
	 */
	public Date nextCalendarDate(Date date) {
		return fixedDates.stream().map(FixedDate::getFixedDate)
				   .filter(fixedDate -> date.before(fixedDate))
				   .min(Date::compareTo).orElse(null);
	}

	/**
	 * Checks for previous calendar date. 
	 * 
	 * @param date Current date.
	 * @return Next calendar date.
	 */
	public Date previousCalendarDate(Date date) {
		 return fixedDates.stream().map(FixedDate::getFixedDate)
						   .filter(fixedDate -> date.after(fixedDate))
						   .max(Date::compareTo).orElse(null);
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