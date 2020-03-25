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
