/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.rating;

import java.util.List;

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
}