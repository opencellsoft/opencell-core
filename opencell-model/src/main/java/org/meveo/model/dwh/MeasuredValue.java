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

package org.meveo.model.dwh;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Measured value for a given date and dimention
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "dwh_measured_value")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_measured_value_seq"), })
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class MeasuredValue extends BaseEntity {

    private static final long serialVersionUID = -3343485468990186936L;

    /**
     * Measurable quantity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurable_quantity", nullable = true, unique = false, updatable = true)
    private MeasurableQuantity measurableQuantity;

    /**
     * Code
     */
    @Column(name = "code", nullable = false, length = 60)
    @Size(max = 60, min = 1)
    @NotNull
    protected String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_period")
    private MeasurementPeriodEnum measurementPeriod;

    /**
     * Date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @XmlTransient
    private Date date;

    /**
     * Measure dimension
     */
    @Column(name = "dimension_1", length = 255)
    @Size(max = 255)
    private String dimension1;

    /**
     * Measure dimension
     */
    @Column(name = "dimension_2", length = 255)
    @Size(max = 255)
    private String dimension2;

    /**
     * Measure dimension
     */
    @Column(name = "dimension_3", length = 255)
    @Size(max = 255)
    private String dimension3;

    /**
     * Measure dimension
     */
    @Column(name = "dimension_4", length = 255)
    @Size(max = 255)
    private String dimension4;

    /**
     * Value
     */
    @Column(name = "value", precision = NB_PRECISION, scale = NB_DECIMALS)
    @XmlTransient
    private BigDecimal value;

    public MeasurableQuantity getMeasurableQuantity() {
        return measurableQuantity;
    }

    public void setMeasurableQuantity(MeasurableQuantity measurableQuantity) {
        this.measurableQuantity = measurableQuantity;
        this.code = this.measurableQuantity.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MeasurementPeriodEnum getMeasurementPeriod() {
        return measurementPeriod;
    }

    public void setMeasurementPeriod(MeasurementPeriodEnum measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDimension1() {
        return dimension1;
    }

    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    public String getDimension2() {
        return dimension2;
    }

    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    public String getDimension3() {
        return dimension3;
    }

    public void setDimension3(String dimension3) {
        this.dimension3 = dimension3;
    }

    public String getDimension4() {
        return dimension4;
    }

    public void setDimension4(String dimension4) {
        this.dimension4 = dimension4;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof MeasuredValue)) {
            return false;
        }

        MeasuredValue other = (MeasuredValue) obj;

        return getId().equals(other.getId());
    }

}
