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

package org.meveo.api.dto.dwh;

import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasurementPeriodEnum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The Class MeasurableQuantityDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "MeasurableQuantity")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurableQuantityDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2678416518718451635L;

    /** The theme. */
    private String theme;
    
    /** The dimension 1. */
    private String dimension1;
    
    /** The dimension 2. */
    private String dimension2;
    
    /** The dimension 3. */
    private String dimension3;
    
    /** The dimension 4. */
    private String dimension4;
    
    /** The editable. */
    private boolean editable;
    
    /** The additive. */
    private boolean additive;
    
    /** The sql query. */
    private String sqlQuery;
    
    /** The measurement period. */
    private MeasurementPeriodEnum measurementPeriod;
    
    /** The last measure date. */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date lastMeasureDate;

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && StringUtils.isBlank(theme) && StringUtils.isBlank(dimension1) && StringUtils.isBlank(dimension2)
                && StringUtils.isBlank(dimension3) && StringUtils.isBlank(dimension4) && StringUtils.isBlank(sqlQuery) && measurementPeriod == null && lastMeasureDate == null;
    }

    /**
     * Instantiates a new measurable quantity dto.
     */
    public MeasurableQuantityDto() {
        super();
    }

    /**
     * Instantiates a new measurable quantity dto.
     *
     * @param mq the MeasurableQuantity entity
     */
    public MeasurableQuantityDto(MeasurableQuantity mq) {
        super(mq);

        setTheme(mq.getTheme());
        setDimension1(mq.getDimension1());
        setDimension2(mq.getDimension2());
        setDimension3(mq.getDimension3());
        setDimension4(mq.getDimension4());
        setEditable(mq.isEditable());
        setAdditive(mq.isAdditive());
        setSqlQuery(mq.getSqlQuery());
        setMeasurementPeriod(mq.getMeasurementPeriod());
        setLastMeasureDate(mq.getLastMeasureDate());
    }

    /**
     * Gets the theme.
     *
     * @return the theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Sets the theme.
     *
     * @param theme the new theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * Gets the dimension 1.
     *
     * @return the dimension 1
     */
    public String getDimension1() {
        return dimension1;
    }

    /**
     * Sets the dimension 1.
     *
     * @param dimension1 the new dimension 1
     */
    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    /**
     * Gets the dimension 2.
     *
     * @return the dimension 2
     */
    public String getDimension2() {
        return dimension2;
    }

    /**
     * Sets the dimension 2.
     *
     * @param dimension2 the new dimension 2
     */
    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    /**
     * Gets the dimension 3.
     *
     * @return the dimension 3
     */
    public String getDimension3() {
        return dimension3;
    }

    /**
     * Sets the dimension 3.
     *
     * @param dimension3 the new dimension 3
     */
    public void setDimension3(String dimension3) {
        this.dimension3 = dimension3;
    }

    /**
     * Gets the dimension 4.
     *
     * @return the dimension 4
     */
    public String getDimension4() {
        return dimension4;
    }

    /**
     * Sets the dimension 4.
     *
     * @param dimension4 the new dimension 4
     */
    public void setDimension4(String dimension4) {
        this.dimension4 = dimension4;
    }

    /**
     * Checks if is editable.
     *
     * @return true, if is editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the editable.
     *
     * @param editable the new editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Checks if is additive.
     *
     * @return true, if is additive
     */
    public boolean isAdditive() {
        return additive;
    }

    /**
     * Sets the additive.
     *
     * @param additive the new additive
     */
    public void setAdditive(boolean additive) {
        this.additive = additive;
    }

    /**
     * Gets the sql query.
     *
     * @return the sql query
     */
    public String getSqlQuery() {
        return sqlQuery;
    }

    /**
     * Sets the sql query.
     *
     * @param sqlQuery the new sql query
     */
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    /**
     * Gets the measurement period.
     *
     * @return the measurement period
     */
    public MeasurementPeriodEnum getMeasurementPeriod() {
        return measurementPeriod;
    }

    /**
     * Sets the measurement period.
     *
     * @param measurementPeriod the new measurement period
     */
    public void setMeasurementPeriod(MeasurementPeriodEnum measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    /**
     * Gets the last measure date.
     *
     * @return the last measure date
     */
    public Date getLastMeasureDate() {
        return lastMeasureDate;
    }

    /**
     * Sets the last measure date.
     *
     * @param lastMeasureDate the new last measure date
     */
    public void setLastMeasureDate(Date lastMeasureDate) {
        this.lastMeasureDate = lastMeasureDate;
    }

    @Override
    public String toString() {
        return String.format(
            "MeasurableQuantityDto [code=%s, description=%s, theme=%s, dimension1=%s, dimension2=%s, dimension3=%s, dimension4=%s, editable=%s, additive=%s, sqlQuery=%s, measurementPeriod=%s, lastMeasureDate=%s]",
            getCode(), getDescription(), theme, dimension1, dimension2, dimension3, dimension4, editable, additive, sqlQuery, measurementPeriod, lastMeasureDate);
    }
}