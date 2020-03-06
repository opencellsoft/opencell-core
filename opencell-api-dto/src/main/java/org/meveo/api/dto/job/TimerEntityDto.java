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

package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.jobs.TimerEntity;

/**
 * The Class TimerEntityDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "TimerEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5166093858617578774L;

    /** The hour. */
    @XmlAttribute(required = true)
    private String hour = "*";

    /** The minute. */
    @XmlAttribute(required = true)
    private String minute = "0";

    /** The second. */
    @XmlAttribute(required = true)
    private String second = "0";

    /** The year. */
    @XmlAttribute(required = true)
    private String year = "*";

    /** The month. */
    @XmlAttribute(required = true)
    private String month = "*";

    /** The day of month. */
    @XmlAttribute(required = true)
    private String dayOfMonth = "*";

    /** The day of week. */
    @XmlAttribute(required = true)
    private String dayOfWeek = "*";

    /**
     * Instantiates a new timer entity dto.
     */
    public TimerEntityDto() {
    }

    /**
     * Convert a timer entity to DTO
     *
     * @param timerEntity Entity to convert
     */
    public TimerEntityDto(TimerEntity timerEntity) {
        super(timerEntity);

        this.year = timerEntity.getYear();
        this.month = timerEntity.getMonth();
        this.dayOfMonth = timerEntity.getDayOfMonth();
        this.dayOfWeek = timerEntity.getDayOfWeek();
        this.hour = timerEntity.getHour();
        this.minute = timerEntity.getMinute();
        this.second = timerEntity.getSecond();
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the new year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the month.
     *
     * @param month the new month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the day of month.
     *
     * @return the day of month
     */
    public String getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Sets the day of month.
     *
     * @param dayOfMonth the new day of month
     */
    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * Gets the day of week.
     *
     * @return the day of week
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the day of week.
     *
     * @param dayOfWeek the new day of week
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Gets the hour.
     *
     * @return the hour
     */
    public String getHour() {
        return hour;
    }

    /**
     * Sets the hour.
     *
     * @param hour the new hour
     */
    public void setHour(String hour) {
        this.hour = hour;
    }

    /**
     * Gets the minute.
     *
     * @return the minute
     */
    public String getMinute() {
        return minute;
    }

    /**
     * Sets the minute.
     *
     * @param minute the new minute
     */
    public void setMinute(String minute) {
        this.minute = minute;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public String getSecond() {
        return second;
    }

    /**
     * Sets the second.
     *
     * @param second the new second
     */
    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        
        if (obj == null || !(obj instanceof TimerEntityDto)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        TimerEntityDto other = (TimerEntityDto) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TimerEntityDto [code=" + getCode() + ", description=" + getDescription() + ", hour=" + hour + ", minute=" + minute + ", second=" + second + ", year=" + year
                + ", month=" + month + ", dayOfMonth=" + dayOfMonth + ", dayOfWeek=" + dayOfWeek + "]";
    }
}
