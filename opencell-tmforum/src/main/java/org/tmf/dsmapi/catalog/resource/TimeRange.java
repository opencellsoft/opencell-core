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