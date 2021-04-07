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

import org.meveo.model.catalog.CalendarDateInterval;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class CalendarDateIntervalDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CalendarDateInterval")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarDateIntervalDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;

    /** The interval begin. */
    @XmlAttribute(required = true)
    @Schema(description = "The interval begin")
    private Integer intervalBegin;

    /** The interval end. */
    @XmlAttribute(required = true)
    @Schema(description = "The interval end")
    private Integer intervalEnd;

    /**
     * Instantiates a new calendar date interval dto.
     */
    public CalendarDateIntervalDto() {

    }

    /**
     * Instantiates a new calendar date interval dto.
     *
     * @param d the d
     */
    public CalendarDateIntervalDto(CalendarDateInterval d) {
        intervalBegin = d.getIntervalBegin();
        intervalEnd = d.getIntervalEnd();
    }

    /**
     * Gets the interval begin.
     *
     * @return the interval begin
     */
    public Integer getIntervalBegin() {
        return intervalBegin;
    }

    /**
     * Sets the interval begin.
     *
     * @param intervalBegin the new interval begin
     */
    public void setIntervalBegin(Integer intervalBegin) {
        this.intervalBegin = intervalBegin;
    }

    /**
     * Gets the interval end.
     *
     * @return the interval end
     */
    public Integer getIntervalEnd() {
        return intervalEnd;
    }

    /**
     * Sets the interval end.
     *
     * @param intervalEnd the new interval end
     */
    public void setIntervalEnd(Integer intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CalendarDateIntervalDto [intervalBegin=" + intervalBegin + ", intervalEnd=" + intervalEnd + "]";
    }
}