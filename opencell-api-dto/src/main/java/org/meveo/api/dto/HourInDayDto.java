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

package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.HourInDay;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class HourInDayDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "HourInDay")
@XmlAccessorType(XmlAccessType.FIELD)
public class HourInDayDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;

    /** The hour. */
    @XmlAttribute(required = true)
    @Schema(description = "hour")
    private Integer hour;

    /** The min. */
    @XmlAttribute(required = true)
    @Schema(description = "minute of the hours")
    private Integer min;

    /**
     * Instantiates a new hour in day dto.
     */
    public HourInDayDto() {

    }

    /**
     * Instantiates a new hour in day dto.
     *
     * @param d the d
     */
    public HourInDayDto(HourInDay d) {
        hour = d.getHour();
        min = d.getMinute();
    }

    /**
     * Gets the hour.
     *
     * @return the hour
     */
    public Integer getHour() {
        return hour;
    }

    /**
     * Sets the hour.
     *
     * @param hour the new hour
     */
    public void setHour(Integer hour) {
        this.hour = hour;
    }

    /**
     * Gets the min.
     *
     * @return the min
     */
    public Integer getMin() {
        return min;
    }

    /**
     * Sets the min.
     *
     * @param min the new min
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HourInDayDto [hour=" + hour + ", min=" + min + "]";
    }
}