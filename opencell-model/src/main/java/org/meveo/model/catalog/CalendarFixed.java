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

import org.meveo.model.DatePeriod;

@Entity
@DiscriminatorValue("FIXED")
public class CalendarFixed extends Calendar {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "calendarFixed", cascade = { CascadeType.ALL }, orphanRemoval = true)
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

    public void addFixedDate(DatePeriod datePeriod) {
        FixedDate fixedDate = new FixedDate(datePeriod);
        fixedDate.setCalendarFixed(this);
        fixedDates.add(fixedDate);
    }

    public Boolean isValidFixedDate(DatePeriod datePeriod) {
        for (FixedDate fixedDate : fixedDates) {
            if (fixedDate.getDatePeriod().isCorrespondsToPeriod(datePeriod, false)) {
                return false;
            }
        }
        return true;
    }

    public void removeFixedDate(DatePeriod datePeriod) {
        FixedDate fixedDate = new FixedDate(datePeriod);
        fixedDates.remove(fixedDate);
    }

    /**
     * Checks for next calendar date.
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {
        return fixedDates.stream().map(FixedDate::getDatePeriod)
            .filter(datePeriod -> datePeriod.isCorrespondsToPeriod(date))
            .map(DatePeriod::getTo)
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks for previous calendar date.
     * 
     * @param date Current date.
     * @return previous calendar date.
     */
    public Date previousCalendarDate(Date date) {
        return fixedDates.stream().map(FixedDate::getDatePeriod)
                .filter(datePeriod -> datePeriod.isCorrespondsToPeriod(date))
                .map(DatePeriod::getFrom)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks for previous period end date.
     * 
     * @param date Current date.
     * @return previous period end date.
     */
    @Override
    public Date previousPeriodEndDate(Date date) {
        return fixedDates.stream().map(fixedDate -> fixedDate.getDatePeriod().getTo())
                .filter(toDate -> toDate.before(date))
                .max(Date::compareTo)
                .orElse(null);    
        }

    /**
     * Checks for next period start date.
     * 
     * @param date Current date.
     * @return next period start date.
     */
    @Override
    public Date nextPeriodStartDate(Date date) {
        return fixedDates.stream().map(fixedDate -> fixedDate.getDatePeriod().getFrom())
                .filter(fromDate -> fromDate.after(date))
                .min(Date::compareTo)
                .orElse(null);    
    }
}