package org.meveo.service.billing.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;

import org.meveo.model.billing.CounterPeriod;

/**
 * Tracks virtual counter period values for the time of request
 * 
 * @author Andrius Karpavicius
 *
 */
@RequestScoped
public class VirtualCounterInstances {

    /**
     * Counter periods grouped by a counter instance with key= <Counter instance id>-<Counter code> and value a list of counter periods
     */
    private Map<String, List<CounterPeriod>> virtualCounters;

    /**
     * Get a counter period for a given counter instance and date
     * 
     * @param counterInstanceId Counter instance identifier. Optional.
     * @param counterCode Counter code
     * @param date Date
     * @return Counter period matched
     */
    public CounterPeriod getCounterPeriod(Long counterInstanceId, String counterCode, Date date) {
        if (virtualCounters == null) {
            return null;
        }

        String key = counterInstanceId + "-" + counterCode;
        List<CounterPeriod> counterPeriods = virtualCounters.get(key);
        if (counterPeriods != null) {
            for (CounterPeriod counterPeriod : counterPeriods) {
                if (counterPeriod.isCorrespondsToPeriod(date)) {
                    return counterPeriod;
                }
            }
        }
        return null;
    }

    /**
     * Store a counter period for a given counter instance
     * 
     * @param counterPeriod Counter period to add
     */
    public void addCounterPeriod(CounterPeriod counterPeriod) {

        if (virtualCounters == null) {
            virtualCounters = new HashMap<String, List<CounterPeriod>>();
        }

        String key = counterPeriod.getCounterInstance().getId() + "-" + counterPeriod.getCode();
        List<CounterPeriod> counterPeriods = virtualCounters.get(key);

        if (counterPeriods == null) {
            counterPeriods = new ArrayList<CounterPeriod>();
            virtualCounters.put(key, counterPeriods);
        }
        counterPeriods.add(counterPeriod);
    }

    /**
     * @param virtualCounters Counter periods grouped by a counter instance with key= <Counter instance id>-<Counter code> and value a list of counter periods
     */
    public void setVirtualCounters(Map<String, List<CounterPeriod>> virtualCounters) {
        this.virtualCounters = virtualCounters;
    }
}