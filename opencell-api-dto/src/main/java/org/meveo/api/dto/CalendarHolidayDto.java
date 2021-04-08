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

import org.meveo.model.catalog.CalendarHoliday;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class CalendarHolidayDto.
 *
 * @author hznibar
 */
@XmlRootElement(name = "CalendarHoliday")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarHolidayDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;

    /** The holiday begin. */
    @XmlAttribute(required = true)
    @Schema(description = "The holiday begin")
    private Integer holidayBegin;

    /** The holiday end. */
    @XmlAttribute(required = true)
    @Schema(description = "The holiday end")
    private Integer holidayEnd;

    /**
     * Instantiates a new calendar date holiday dto.
     */
    public CalendarHolidayDto() {

    }

    /**
     * Instantiates a new calendar holiday dto.
     *
     * @param holiday the holiday
     */
    public CalendarHolidayDto(CalendarHoliday holiday) {
        holidayBegin = holiday.getHolidayBegin();
        holidayEnd = holiday.getHolidayEnd();
    }

    /**
     * Gets the holiday begin.
     *
     * @return the holiday begin
     */
    public Integer getHolidayBegin() {
        return holidayBegin;
    }

    /**
     * Sets the holiday begin.
     *
     * @param holidayBegin the new holiday begin
     */
    public void setHolidayBegin(Integer holidayBegin) {
        this.holidayBegin = holidayBegin;
    }

    /**
     * Gets the holiday end.
     *
     * @return the holiday end
     */
    public Integer getHolidayEnd() {
        return holidayEnd;
    }

    /**
     * Sets the holiday end.
     *
     * @param holidayEnd the new holiday end
     */
    public void setHolidayEnd(Integer holidayEnd) {
        this.holidayEnd = holidayEnd;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CalendarHolidayDto [holidayBegin=" + holidayBegin + ", holidayEnd=" + holidayEnd + "]";
    }
}