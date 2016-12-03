package org.meveo.event;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.IEntity;
import org.meveo.model.billing.CounterPeriod;

public class CounterPeriodEvent implements Serializable, IEvent {

    private static final long serialVersionUID = -1937181899391134383L;

    private CounterPeriod counterPeriod;
    private BigDecimal counterValue;

    public CounterPeriodEvent() {

    }

    public CounterPeriodEvent(CounterPeriod counterPeriod, BigDecimal counterValue) {
        this.counterPeriod = counterPeriod;
        this.counterValue = counterValue;
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

    @Override
    public IEntity getEntity() {
        return counterPeriod;
    }

    @Override
    public String toString() {
        return String.format("CounterPeriodEvent [counterValue=%s, CounterPeriod=%s]", counterValue, counterPeriod);
    }
}