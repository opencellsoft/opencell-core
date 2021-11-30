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

package org.meveo.model;

import java.math.BigDecimal;

/**
 * Represents a change made to counter value
 * 
 * @author Andrius Karpavicius
 */
public class CounterValueChangeInfo {

    /**
     * Counter period identifier
     */
    private Long counterPeriodId;

    /**
     * Is this an accumulator counter
     */
    private boolean accumulator;

    /**
     * Previous counter value
     */
    private BigDecimal previousValue;

    /**
     * The actual change amount
     */
    private BigDecimal deltaValue;

    /**
     * New counter value
     */
    private BigDecimal newValue;

    /**
     * Constructor
     * 
     * @param counterPeriodId Counter period identifier
     * @param isAccumulator Is this an accumulator counter
     * @param previousValue Previous counter value
     * @param deltaValue The actual change amount
     * @param newValue New counter value
     */
    public CounterValueChangeInfo(Long counterPeriodId, boolean isAccumulator, BigDecimal previousValue, BigDecimal deltaValue, BigDecimal newValue) {
        super();
        this.previousValue = previousValue;
        this.deltaValue = deltaValue;
        this.newValue = newValue;
        this.counterPeriodId = counterPeriodId;
        this.accumulator = isAccumulator;
    }

    /**
     * @return Previous counter value
     */
    public BigDecimal getPreviousValue() {
        return previousValue;
    }

    /**
     * @return The actual change amount
     */
    public BigDecimal getDeltaValue() {
        return deltaValue;
    }

    /**
     * @return New counter value
     */
    public BigDecimal getNewValue() {
        return newValue;
    }

    /**
     * @return Counter period identifier
     */
    public Long getCounterPeriodId() {
        return counterPeriodId;
    }

    /**
     * @return Is this an accumulator counter
     */
    public boolean isAccumulator() {
        return accumulator;
    }

    @Override
    public String toString() {
        return "from " + previousValue + " by " + deltaValue + " to " + newValue;
    }

    /**
     * Was there any counter value change
     * 
     * @return True if delta value is not zero
     */
    public boolean isChange() {
        return deltaValue != null && deltaValue.compareTo(BigDecimal.ZERO) != 0;
    }
}
