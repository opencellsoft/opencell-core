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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.rating.EDR;

/**
 * Tracks rating progress
 *
 * @author Khalid HORRI
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
     * Wallet operations as result of rating an EDR
     */
    private List<WalletOperation> walletOperations = new ArrayList<WalletOperation>();

    /**
     * Counter period changes with key=<Counter period id> and delta value as a value
     */
    private Map<Long, BigDecimal> counterChanges;

    /**
     * Exception raised during rating
     */
    private Exception ratingException;
    

    private Set<DiscountPlanItem> eligibleFixedDiscountItems = new HashSet<DiscountPlanItem>();

    /**
     * Constructor
     */
    public RatingResult() {
    }

    /**
     * Constructor
     * 
     * @param e Exception raised
     */
    public RatingResult(Exception e) {
        this.ratingException = e;
    }

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
     * @return Wallet operations as result of rating
     */
    public List<WalletOperation> getWalletOperations() {
        return walletOperations;
    }

    /**
     * @param walletOperation Wallet operations as result of rating
     */
    public void setWalletOperations(List<WalletOperation> walletOperations) {
        this.walletOperations = walletOperations;
    }

    /**
     * Add wallet operation
     * 
     * @param walletOperation Wallet operation
     */
    public void addWalletOperation(WalletOperation walletOperation) {
        this.walletOperations.add(walletOperation);
    }

    /**
     * Add wallet operations
     * 
     * @param walletOperations Wallet operations
     */
    public void addWalletOperations(List<WalletOperation> walletOperations) {
        this.walletOperations.addAll(walletOperations);
    }

    /**
     * Get a list of changed counter periods
     * 
     * @return Counter period changes with key=<Counter period id> and delta value as a value
     */
    public Map<Long, BigDecimal> getCounterChanges() {
        return counterChanges;
    }

    /**
     * @param counterUpdates Counter period changes with key=<Counter period id> and delta value as a value
     */
    public void setCounterChanges(Map<Long, BigDecimal> counterChanges) {
        this.counterChanges = counterChanges;
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

    /**
     * Add rating result
     * 
     * @param ratingResult Rating result
     */
    public void add(RatingResult ratingResult) {
        addWalletOperations(ratingResult.getWalletOperations());

        if (ratingResult.getTriggeredEDRs() != null) {
            if (triggeredEDRs == null) {
                this.triggeredEDRs = ratingResult.getTriggeredEDRs();
            } else {
                this.triggeredEDRs.addAll(ratingResult.getTriggeredEDRs());
            }
        }

        if (ratingResult.getCounterChanges() != null) {
            if (counterChanges == null) {
                this.counterChanges = ratingResult.getCounterChanges();
            } else {
                for (Entry<Long, BigDecimal> counterInfo : ratingResult.getCounterChanges().entrySet()) {
                    BigDecimal counterDelta = counterChanges.get(counterInfo.getKey());
                    if (counterDelta == null) {
                        counterChanges.put(counterInfo.getKey(), counterInfo.getValue());
                    } else {
                        counterChanges.put(counterInfo.getKey(), counterDelta.add(counterInfo.getValue()));
                    }
                }
            }
        }
    }

    /**
     * Add counter change information
     * 
     * @param counterChangeInfo Counter change information
     */
    public void addCounterChange(List<CounterValueChangeInfo> counterChangeInfo) {
        for (CounterValueChangeInfo counterValueChangeInfo : counterChangeInfo) {

            addCounterChange(counterValueChangeInfo.getCounterPeriodId(), counterValueChangeInfo.getDeltaValue());
        }
    }

    /**
     * Add triggered EDRs
     * 
     * @param triggeredEdrs Triggered EDRs
     */
    public void addTriggeredEDRs(List<EDR> triggeredEdrs) {
        if (triggeredEdrs == null || triggeredEdrs.isEmpty()) {
            return;
        }
        if (triggeredEDRs == null) {
            this.triggeredEDRs = triggeredEdrs;
        } else {
            this.triggeredEDRs.addAll(triggeredEdrs);
        }
    }

    /**
     * @return Exception raised during rating
     */
    public Exception getRatingException() {
        return ratingException;
    }

    /**
     * @param ratingException Exception raised during rating
     */
    public void setRatingException(Exception ratingException) {
        this.ratingException = ratingException;
    }

	public Set<DiscountPlanItem> getEligibleFixedDiscountItems() {
		return eligibleFixedDiscountItems;
	}

	public void setEligibleFixedDiscountItems(Set<DiscountPlanItem> eligibleFixedDiscountItems) {
		this.eligibleFixedDiscountItems = eligibleFixedDiscountItems;
	}
}