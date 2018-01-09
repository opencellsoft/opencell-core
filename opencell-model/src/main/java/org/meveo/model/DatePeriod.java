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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.shared.DateUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Ignas
 * 
 */
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatePeriod implements Comparable<DatePeriod>, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date from;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date to;

    public DatePeriod() {
    }

    public DatePeriod(Date from, Date to) {
        super();
        this.from = from;
        this.to = to;
    }

    public DatePeriod(String from, String to, String datePattern) {

        if (from != null) {
            this.from = DateUtils.parseDateWithPattern(from, datePattern);
        }
        if (to != null) {
            this.to = DateUtils.parseDateWithPattern(to, datePattern);
        }
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
        return (from == null || (date != null &&  date.compareTo(from) >= 0)) && (to == null || (date != null && date.before(to)));
    }

    /**
     * Check if dates match period start and end dates (strict match) or overlap period start and end dates (non-strict match)
     * 
     * @param period Date period to check
     * @param strictMatch True If dates match period start and end dates (strict match) or False when overlap period start and end dates (non-strict match)
     * @return True if current period object corresponds to give dates and strict matching type
     */
    public boolean isCorrespondsToPeriod(DatePeriod period, boolean strictMatch) {
        if (period == null) {
            return isCorrespondsToPeriod(null, null, strictMatch);
        } else {
            return isCorrespondsToPeriod(period.getFrom(), period.getTo(), strictMatch);
        }
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
        return from + " - " + to;
    }

    public String toString(String datePattern) {
        if (isEmpty()) {
            return "";
        }

        String txt = " - ";
        if (from != null) {
            txt = DateUtils.formatDateWithPattern(from, datePattern) + txt;
        }
        if (to != null) {
            txt = txt + DateUtils.formatDateWithPattern(to, datePattern);
        }

        return txt;
    }

    @Override
    public int compareTo(DatePeriod other) {

        if (this.from == null && other.getFrom() == null) {
            return 0;
        } else if (this.from != null && other.getFrom() == null) {
            return 1;
        } else if (this.from == null && other.getFrom() != null) {
            return -1;
        } else {
            return this.from.compareTo(other.getFrom());
        }
    }

    /**
     * Is period empty - are both From and To values are not specified
     * 
     * @return True if both From and To values are not specified
     */
    public boolean isEmpty() {
        return from == null && to == null;
    }

    @Override
    public boolean equals(Object other) {
        if (isEmpty() && other == null) {
            return true;
        } else if (!isEmpty() && other == null) {
            return false;
        } else if (!(other instanceof DatePeriod)) {
            return false;
        }

        return isCorrespondsToPeriod((DatePeriod) other, true);
    }
}