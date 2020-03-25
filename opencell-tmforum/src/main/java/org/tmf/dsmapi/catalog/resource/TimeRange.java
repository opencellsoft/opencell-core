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

package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;
import java.util.Date;

import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.DatePeriod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author pierregauthier
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class TimeRange implements Serializable {
    private final static long serialVersionUID = 1L;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date startDateTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date endDateTime;

    public TimeRange() {
    }

    public TimeRange(DatePeriod datePeriod) {

        if (datePeriod != null) {
            startDateTime = datePeriod.getFrom();
            endDateTime = datePeriod.getTo();
        }
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return "TimeRange{" + "startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + '}';
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (startDateTime == null && endDateTime == null) ? true : false;
    }

    @JsonIgnore
    public boolean isValid() {
        if (startDateTime == null || endDateTime == null) {
            return true;
        }

        if (endDateTime.after(startDateTime) == true) {
            return true;
        }

        return false;
    }

    public static TimeRange createProto() {
        TimeRange timeRange = new TimeRange();

        timeRange.setStartDateTime(new Date(0));
        timeRange.setEndDateTime(new Date());

        return timeRange;
    }

    public DatePeriod toDatePeriod() {
        return new DatePeriod(startDateTime, endDateTime);
    }
}