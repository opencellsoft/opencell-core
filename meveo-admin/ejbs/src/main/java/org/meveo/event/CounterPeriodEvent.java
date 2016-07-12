package org.meveo.event;

import java.io.Serializable;

import org.meveo.model.billing.CounterPeriod;


public class CounterPeriodEvent implements Serializable {

    private static final long serialVersionUID = -1937181899391134383L;

    private CounterPeriod CounterPeriod;

   

    public CounterPeriod getCounterPeriod() {
		return CounterPeriod;
	}

	public void setCounterPeriod(CounterPeriod counterPeriod) {
		CounterPeriod = counterPeriod;
	}

	@Override
    public String toString() {
        return "CounterPeriodEvent [CounterPeriod=" + CounterPeriod + "]";
    }
}