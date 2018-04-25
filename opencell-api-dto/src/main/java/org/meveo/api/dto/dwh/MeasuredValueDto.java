package org.meveo.api.dto.dwh;

import java.math.BigDecimal;

import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.model.shared.DateUtils;

/**
 * The Class MeasuredValueDto.
 *
 * @author Edward P. Legaspi
 */
public class MeasuredValueDto {

    /** The measurable quantity code. */
    private String measurableQuantityCode;
    
    /** The code. */
    protected String code;
    
    /** The measurement period. */
    private MeasurementPeriodEnum measurementPeriod;
    
    /** The date. */
    private String date;
    
    /** The dimension 1. */
    private String dimension1;
    
    /** The dimension 2. */
    private String dimension2;
    
    /** The dimension 3. */
    private String dimension3;
    
    /** The dimension 4. */
    private String dimension4;
    
    /** The value. */
    private BigDecimal value;

    /**
     * Instantiates a new measured value dto.
     */
    public MeasuredValueDto() {

    }

    /**
     * Instantiates a new measured value dto.
     *
     * @param measuredValue the MeasuredValue entity
     */
    public MeasuredValueDto(MeasuredValue measuredValue) {
        measurableQuantityCode = measuredValue.getMeasurableQuantity().getCode();
        code = measuredValue.getCode();
        measurementPeriod = measuredValue.getMeasurementPeriod();
        date = measuredValue.getDate() != null ? DateUtils.formatDateWithPattern(measuredValue.getDate(), "yyyy-MM-dd'T'HH:mm:ss") : null;
        dimension1 = measuredValue.getDimension1();
        dimension2 = measuredValue.getDimension2();
        dimension3 = measuredValue.getDimension3();
        dimension4 = measuredValue.getDimension4();
        value = measuredValue.getValue();
    }

    /**
     * Gets the measurable quantity code.
     *
     * @return the measurable quantity code
     */
    public String getMeasurableQuantityCode() {
        return measurableQuantityCode;
    }

    /**
     * Sets the measurable quantity code.
     *
     * @param measurableQuantityCode the new measurable quantity code
     */
    public void setMeasurableQuantityCode(String measurableQuantityCode) {
        this.measurableQuantityCode = measurableQuantityCode;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
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
     * Gets the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(String date) {
        this.date = date;
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
     * Gets the value.
     *
     * @return the value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the value to set
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }
}