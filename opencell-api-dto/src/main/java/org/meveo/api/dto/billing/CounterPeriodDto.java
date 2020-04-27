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

package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Map;

import org.meveo.model.catalog.AccumulatorCounterTypeEnum;
import org.meveo.model.catalog.CounterTypeEnum;

public class CounterPeriodDto {

    private CounterTypeEnum counterType;
    private BigDecimal level;
    private String periodStartDate;
    private String periodEndDate;
    private BigDecimal value;
    /**
     * Check if is it an accumulator account.
     */
    private Boolean accumulator;
    /**
     * Accumulated values.
     */
    private Map<String, BigDecimal> accumulatedValues;

    /**
     * The type field can be "Multi-value" if the accumulator is true.
     */
    private AccumulatorCounterTypeEnum accumulatorType;

    /**
     * An EL expression that returns a boolean that tells us if we accumulate or not.
     */

    private String filterEl;
    /**
     * An EL expression that Returns a string that is an identifier for what we count.
     */

    private String keyEl;

    /**
     * An EL expression that returns a number (BigDecimal) that contains the quantity we count.
     */

    private String valueEl;

    public CounterTypeEnum getCounterType() {
        return counterType;
    }

    public void setCounterType(CounterTypeEnum counterType) {
        this.counterType = counterType;
    }

    public BigDecimal getLevel() {
        return level;
    }

    public void setLevel(BigDecimal level) {
        this.level = level;
    }

    public String getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(String periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public String getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(String periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Boolean getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(Boolean accumulator) {
        this.accumulator = accumulator;
    }

    public Map<String, BigDecimal> getAccumulatedValues() {
        return accumulatedValues;
    }

    public void setAccumulatedValues(Map<String, BigDecimal> accumulatedValues) {
        this.accumulatedValues = accumulatedValues;
    }

    public AccumulatorCounterTypeEnum getAccumulatorType() {
        return accumulatorType;
    }

    public void setAccumulatorType(AccumulatorCounterTypeEnum accumulatorType) {
        this.accumulatorType = accumulatorType;
    }

    public String getFilterEl() {
        return filterEl;
    }

    public void setFilterEl(String filterEl) {
        this.filterEl = filterEl;
    }

    public String getKeyEl() {
        return keyEl;
    }

    public void setKeyEl(String keyEl) {
        this.keyEl = keyEl;
    }

    public String getValueEl() {
        return valueEl;
    }

    public void setValueEl(String valueEl) {
        this.valueEl = valueEl;
    }
}
