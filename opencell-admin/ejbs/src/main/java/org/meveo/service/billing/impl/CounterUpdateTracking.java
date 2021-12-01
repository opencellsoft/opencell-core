package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.billing.CounterPeriod;

/**
 * Tracks counter period updates for the time of request
 * 
 * @author Andrius Karpavicius
 *
 */
@RequestScoped
public class CounterUpdateTracking {

    /**
     * Counter periods grouped by a counter instance with key=<Counter instance id>-<Counter code> and value a list of counter periods
     */
    private Map<String, List<CounterPeriod>> counterUpdates;

    /**
     * Store a counter period as updated
     * 
     * @param counterPeriod Counter period to track
     * @param counterValueChangeInfo Counter value change details
     */
    public void addCounterPeriodChange(CounterPeriod counterPeriod, CounterValueChangeInfo counterValueChangeInfo) {

        if (counterUpdates == null) {
            counterUpdates = new HashMap<String, List<CounterPeriod>>();
        }

        String key = counterPeriod.getCounterInstance().getId() + "-" + counterPeriod.getCode();
        List<CounterPeriod> counterPeriods = counterUpdates.get(key);

        if (counterPeriods == null) {
            counterPeriods = new ArrayList<CounterPeriod>();
            counterUpdates.put(key, counterPeriods);
        }

        CounterPeriod counterPeriodMatched = null;
        for (CounterPeriod counterPeriodTracked : counterPeriods) {
            if (counterPeriodTracked.isCorrespondsToPeriod(counterPeriod.getPeriodStartDate())) {
                counterPeriodMatched = counterPeriodTracked;
                break;
            }
        }

        // Update already tracked counter period value
        if (counterPeriodMatched != null) {
            counterPeriodMatched.setValue(counterPeriod.getValue());
            if (counterPeriod.getAccumulatedValues() != null) {
                counterPeriodMatched.setAccumulatedValues(new HashMap<String, BigDecimal>(counterPeriod.getAccumulatedValues()));
            }
        } else {
            try {
                counterPeriodMatched = counterPeriod.clone();
                counterPeriodMatched.setId(counterPeriod.getId());
                counterPeriods.add(counterPeriodMatched);
            } catch (CloneNotSupportedException e) {
                // There is no reason to get here
            }
        }
    }

    /**
     * Get a list of updated counter periods grouped by a counter instance
     * 
     * @return Counter periods grouped by a counter instance with key=<Counter instance id>-<Counter code> and value a list of counter periods
     */
    public Map<String, List<CounterPeriod>> getCounterUpdates() {
        return counterUpdates;
    }

    /**
     * @param counterUpdates Counter periods grouped by a counter instance with key=<Counter instance id>-<Counter code> and value a list of counter periods
     */
    public void setCounterUpdates(Map<String, List<CounterPeriod>> counterUpdates) {
        this.counterUpdates = counterUpdates;
    }
}