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
package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.model.billing.WalletOperation;

/**
 * Tracks rating progress
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 7.3
 */
public class RatingResult {

    /**
     * A list of EDRs that were triggered as part of this processing
     */
    private List<EDR> triggeredEDRs;

    /**
     * Was consumption/EDR fully rated
     */
    private boolean fullyRated;

    /**
     * Wallet operation as result of rating an EDR
     */
    private WalletOperation walletOperation;

    private Map<Long, BigDecimal> counterChanges;
    /**
     * @return A list of EDRs that were triggered as part of this EDR processing
     */
    public List<EDR> getTriggeredEDRs() {
        return triggeredEDRs;
    }

    /**
     * @param triggeredEDRs A list of EDRs that were triggered as part of this EDR processing
     */
    public void setTriggeredEDRs(List<EDR> triggeredEDRs) {
        this.triggeredEDRs = triggeredEDRs;
    }

    /**
     * @return Was consumption/EDR fully rated
     */
    public boolean isFullyRated() {
        return fullyRated;
    }

    /**
     * @param fullyRated Was consumption/EDR fully rated
     */
    public void setFullyRated(boolean fullyRated) {
        this.fullyRated = fullyRated;
    }

    /**
     * @return Wallet operation as result of rating
     */
    public WalletOperation getWalletOperation() {
        return walletOperation;
    }

    /**
     * @param walletOperation Wallet operation as result of rating
     */
    public void setWalletOperation(WalletOperation walletOperation) {
        this.walletOperation = walletOperation;
    }

    /**
     * Add a counter period change
     * 
     * @param counterPeriodId Counter period identifier
     * @param delta Delta value
     */
    public void addCounterChange(Long counterPeriodId, BigDecimal delta) {

        if (counterChanges == null) {
            counterChanges = new HashMap<Long, BigDecimal>();
        }
        BigDecimal counterDelta = counterChanges.get(counterPeriodId);
        if (counterDelta == null) {
            counterChanges.put(counterPeriodId, delta);
        } else {
            counterChanges.put(counterPeriodId, counterDelta.add(delta));
        }
    }

    public Map<Long, BigDecimal> getCounterChanges() {
        return counterChanges;
    }

    public void setCounterChanges(Map<Long, BigDecimal> counterChanges) {
        this.counterChanges = counterChanges;
    }
}