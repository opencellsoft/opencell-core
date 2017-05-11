/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.shared.DateUtils;

/**
 * @author Ignas
 * @created 2009.10.20
 */
@Embeddable
public class DatePeriod {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date from;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    private Date to;

    public DatePeriod() {
    }

    public DatePeriod(Date from, Date to) {
        super();
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * Check if date falls within period start and end dates
     * 
     * @param date Date to check
     * @return True/false
     */
    public boolean isCorrespondsToPeriod(Date date) {
        return (from == null || date.compareTo(from) >= 0) && (to == null || date.before(to));
    }

    /**
     * Check if dates match period start and end dates (strict match) or overlap period start and end dates (non-strict match)
     * 
     * @param period Date period to check
     * @param strictMatch True If dates match period start and end dates (strict match) or False when overlap period start and end dates (non-strict match)
     * @return True if current period object corresponds to give dates and strict matching type
     */
    public boolean isCorrespondsToPeriod(DatePeriod period, boolean strictMatch) {
        return isCorrespondsToPeriod(period.getFrom(), period.getTo(), strictMatch);
    }

    /**
     * Check if dates match period start and end dates (strict match) or overlap period start and end dates (non-strict match)
     * 
     * @param checkFrom Period start date to check
     * @param checkTo Period end date to check
     * @param strictMatch True If dates match period start and end dates (strict match) or False when overlap period start and end dates (non-strict match)
     * @return True if current period object corresponds to give dates and strict matching type
     */
    public boolean isCorrespondsToPeriod(Date checkFrom, Date checkTo, boolean strictMatch) {

        if (strictMatch) {
            boolean match = (checkFrom == null && this.from == null) || (checkFrom != null && this.from != null && checkFrom.equals(this.from));
            match = match && ((checkTo == null && this.to == null) || (checkTo != null && this.to != null && checkTo.equals(this.to)));
            return match;
        }
        // Check non-strict match case when dates overlap
        return DateUtils.isPeriodsOverlap(this.from, this.to, checkFrom, checkTo);
    }

    /**
     * Check that start date is before end date or any of them is empty
     * 
     * @return True if start date is before end date or any of them is empty
     */
    public boolean isValid() {
        return from == null || to == null || from.before(to);
    }

    @Override
    public String toString() {
        return from + ">" + to;
    }
}