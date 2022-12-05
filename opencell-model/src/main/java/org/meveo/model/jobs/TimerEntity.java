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
package org.meveo.model.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Job execution schedule. Similar to Unix schedule format.
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ModuleItem
@ExportIdentifier({ "code" })
@Table(name = "meveo_timer", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_timer_seq"), })
public class TimerEntity extends EnableBusinessEntity {

    private static final long serialVersionUID = -3764934334462355788L;

    /**
     * Year
     */
    @Column(name = "sc_year", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String year = "*";

    /**
     * Month
     */
    @Column(name = "sc_month", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String month = "*";

    /**
     * Day of month
     */
    @Column(name = "sc_d_o_month", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String dayOfMonth = "*";

    /**
     * Day of the week
     */
    @Column(name = "sc_d_o_week", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String dayOfWeek = "*";

    /**
     * Hour
     */
    @Column(name = "sc_hour", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String hour = "*";

    /**
     * Minute
     */
    @Column(name = "sc_min", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String minute = "0";

    /**
     * Second
     */
    @Column(name = "sc_sec", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String second = "0";

    /**
     * Start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sc_start", nullable = true)
    private Date start;

    /**
     * End date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sc_end", nullable = true)
    private Date end;

    /**
     * Job instances using this schedule
     */
    @OneToMany(mappedBy = "timerEntity", fetch = FetchType.LAZY)
    private List<JobInstance> jobInstances = new ArrayList<JobInstance>();

    public TimerEntity() {

    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * @return the jobInstances
     */
    public List<JobInstance> getJobInstances() {
        return jobInstances;
    }

    /**
     * @param jobInstances the jobInstances to set
     */
    public void setJobInstances(List<JobInstance> jobInstances) {
        this.jobInstances = jobInstances;
    }

    public String getTimerSchedule() {
        return String.format("Hour %s Minute %s Second %s Year %s Month %s Day of month %s Day of week %s", hour, minute, second, year, month, dayOfMonth, dayOfWeek);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TimerEntity)) {
            return false;
        }

        TimerEntity other = (TimerEntity) obj;

        if (id != null && id.equals(other.getId()) ) {
            return true;
        }

        if (this.getCode() == other.getCode()) {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TimerEntity [year=" + year + ", month=" + month + ", dayOfMonth=" + dayOfMonth + ", dayOfWeek=" + dayOfWeek + ", hour=" + hour + ", minute=" + minute + ", second="
                + second + ", start=" + start + ", end=" + end + "]";
    }

}