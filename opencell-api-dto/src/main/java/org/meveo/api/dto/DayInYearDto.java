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

import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.MonthEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class DayInYearDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "DayInYear")
@XmlAccessorType(XmlAccessType.FIELD)
public class DayInYearDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7771829024178269036L;

    /** The day. */
    @XmlAttribute(required = true)
    @Schema(description = "day in the year")
    private Integer day;

    /** The month. */
    @XmlAttribute(required = true)
    @Schema(description = "month of the year", example = "possible value are : JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER")
    private MonthEnum month;

    /**
     * Instantiates a new day in year dto.
     */
    public DayInYearDto() {

    }

    /**
     * Instantiates a new day in year dto.
     *
     * @param d the d
     */
    public DayInYearDto(DayInYear d) {
        day = d.getDay();

        if (d.getMonth() != null) {
            month = d.getMonth();
        }
    }

    /**
     * Gets the day.
     *
     * @return the day
     */
    public Integer getDay() {
        return day;
    }

    /**
     * Sets the day.
     *
     * @param day the new day
     */
    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public MonthEnum getMonth() {
        return month;
    }

    /**
     * Sets the month.
     *
     * @param month the new month
     */
    public void setMonth(MonthEnum month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "DayInYearDto [day=" + day + ", month=" + month + "]";
    }

}
