package org.meveo.model;

import org.meveo.model.billing.CounterPeriod;

import java.math.BigDecimal;

/**
 * Grouping the counter period and the deducedQuantity calculated for the EDR and the counter period.
 *
 * @author Khalid HORRI
 */
public class DeducedCounter {
    /**
     * Counter Period.
     */
    private CounterPeriod counterPeriod = null;

    /**
     * A deduced quantity of an EDR related to a counter.
     */
    private BigDecimal deducedQuantity = BigDecimal.ZERO;

    /**
     * Default constructor.
     */
    public DeducedCounter() {
    }

    /**
     * Constructor.
     *
     * @param counterPeriod   the counter period
     * @param deducedQuantity the deduced quantity
     */
    public DeducedCounter(final CounterPeriod counterPeriod, final BigDecimal deducedQuantity) {
        this.counterPeriod = counterPeriod;
        this.deducedQuantity = deducedQuantity;
    }

    /**
     * Gets Counter Period.
     *
     * @return a counter period
     */
    public CounterPeriod getCounterPeriod() {
        return counterPeriod;
    }

    /**
     * Sets counter period.
     *
     * @param counterPeriod a counter period
     */
    public void setCounterPeriod(CounterPeriod counterPeriod) {
        this.counterPeriod = counterPeriod;
    }

    /**
     * Gets deduced quantity.
     *
     * @return deduced quantity.
     */
    public BigDecimal getDeducedQuantity() {
        return deducedQuantity;
    }

    /**
     * Sets deduced quantity.
     *
     * @param deducedQuantity deduced quantity.
     */
    public void setDeducedQuantity(BigDecimal deducedQuantity) {
        this.deducedQuantity = deducedQuantity;
    }
}
