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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.shared.DateUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A period of dates
 * 
 * @author Ignas
 */
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatePeriod implements Comparable<DatePeriod>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * From date
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date from;

    /**
     * To date
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date to;

    /**
     * Was another date period matched strictly (true if dates match exactly or false if dates overlap)
     */
    @Transient
    private Boolean strictMatch;

    /**
     * Another date period matched - from date
     */
    @Transient
    private Date fromMatch;

    /**
     * Another date period matched - to date
     */
    @Transient
    private Date toMatch;

    public DatePeriod() {
    }

    /**
     * Constructor
     * 
     * @param from Date from. Optional
     * @param to Date to. Optional
     */
    public DatePeriod(Date from, Date to) {
        super();
        this.from = from;
        this.to = to;
    }

    /**
     * Constructor
     * 
     * @param from Date from. Optional
     * @param to Date to. Optional
     * @param datePattern Date pattern
     */
    public DatePeriod(String from, String to, String datePattern) {

        if (from != null) {
            this.from = DateUtils.parseDateWithPattern(from, datePattern);
        }
        if (to != null) {
            this.to = DateUtils.parseDateWithPattern(to, datePattern);
        }
    }

    /**
     * @return Date period from date
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from Date period from date
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @return Date period to date
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to Date period to date
     */
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
        return (from == null || (date != null && date.compareTo(from) >= 0)) && (to == null || (date != null && date.before(to)));
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
        } else if (this.from != null) {
            return this.from.compareTo(other.getFrom());
        }

        return 0;
    }

    public int compareFieldTo(DatePeriod other) {

        if (this.to == null && other.to == null) {
            return 0;
        } else if (this.to != null && other.to == null) {
            return 1;
        } else if (this.to == null && other.to != null) {
            return -1;
        } else if (this.to != null) {
            return this.to.compareTo(other.to);
        }

        return 0;
    
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

    /**
     * @param strictMatch Was another date period matched strictly (true if dates match exactly or false if dates overlap)
     */
    public void setStrictMatch(Boolean strictMatch) {
        this.strictMatch = strictMatch;
    }

    /**
     * @return Was another date period matched strictly (true if dates match exactly or false if dates overlap)
     */
    public Boolean getStrictMatch() {
        return strictMatch;
    }

    /**
     * @return Another date period matched - from date
     */
    public Date getFromMatch() {
        return fromMatch;
    }

    /**
     * @param fromMatch Another date period matched - from date
     */
    public void setFromMatch(Date fromMatch) {
        this.fromMatch = fromMatch;
    }

    /**
     * @return Another date period matched - to date
     */
    public Date getToMatch() {
        return toMatch;
    }

    /**
     * @param toMatch Another date period matched - to date
     */
    public void setToMatch(Date toMatch) {
        this.toMatch = toMatch;
    }

    /**
     * Check if Period duration is zero, that is start and end are the same
     * 
     * @return True if from=to
     */
    public boolean hasNoDuration() {
        return from != null && to != null && from.compareTo(to) == 0;
    }
}