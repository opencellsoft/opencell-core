package org.meveo.model;

import java.math.BigDecimal;

/**
 * Represents a change made to counter value
 * 
 * @author Andrius Karpavicius
 */
public class CounterValueChangeInfo {
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

    public CounterValueChangeInfo(BigDecimal previousValue, BigDecimal deltaValue, BigDecimal newValue) {
        super();
        this.previousValue = previousValue;
        this.deltaValue = deltaValue;
        this.newValue = newValue;
    }

    public BigDecimal getPreviousValue() {
        return previousValue;
    }

    public BigDecimal getDeltaValue() {
        return deltaValue;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return "from " + previousValue + " by " + deltaValue + " to " + newValue;
    }
}
