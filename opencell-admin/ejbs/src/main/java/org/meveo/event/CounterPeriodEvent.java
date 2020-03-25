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

package org.meveo.event;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.IEntity;
import org.meveo.model.billing.CounterPeriod;

/**
 * Represents a counter deduction event that reached a threshold that user specified
 * 
 * @author Andrius Karpavicius
 * 
 */
public class CounterPeriodEvent implements Serializable, IEvent {

    private static final long serialVersionUID = -1937181899391134383L;

    /*
     * Associated counter period
     */
    private CounterPeriod counterPeriod;

    /**
     * Counter value reached
     */
    private BigDecimal counterValue;

    /**
     * Counter value corresponding to a originally entered value to a user. It will be different from counterValue if user entered threshold as %. E.g. counterValueLabel=50% and
     * with initial value of 84, counterValue will contain a value of 42.
     */
    private String counterValueLabel;

    public CounterPeriodEvent() {

    }

    public CounterPeriodEvent(CounterPeriod counterPeriod, BigDecimal counterValue, String counterValueLabel) {
        this.counterPeriod = counterPeriod;
        this.counterValue = counterValue;
        this.counterValueLabel = counterValueLabel;
    }

    public CounterPeriod getCounterPeriod() {
        return counterPeriod;
    }

    public void setCounterPeriod(CounterPeriod counterPeriod) {
        this.counterPeriod = counterPeriod;
    }

    public BigDecimal getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(BigDecimal counterValue) {
        this.counterValue = counterValue;
    }

    public String getCounterValueLabel() {
        return counterValueLabel;
    }

    public void setCounterValueLabel(String counterValueLabel) {
        this.counterValueLabel = counterValueLabel;
    }

    @Override
    public IEntity getEntity() {
        return counterPeriod;
    }

    @Override
    public String toString() {
        return String.format("CounterPeriodEvent [counterPeriod=%s, counterValueLabel=%s, counterValue=%s]", counterPeriod.getId(), counterValueLabel, counterValue);
    }
}