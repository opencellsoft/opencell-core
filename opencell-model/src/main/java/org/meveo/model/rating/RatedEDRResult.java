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
 * Tracks EDR rating progress
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 7.3
 */
public class RatedEDRResult {

    /**
     * A list of EDRs that were triggered as part of this EDR processing
     */
    private List<EDR> triggeredEDRs;

    /**
     * Was EDR fully rated
     */
    private boolean EDRfullyRated;

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
     * @return Was EDR fully rated
     */
    public boolean isEDRfullyRated() {
        return EDRfullyRated;
    }

    /**
     * @param EDRfullyRated Was EDR fully rated
     */
    public void setEDRfullyRated(boolean EDRfullyRated) {
        this.EDRfullyRated = EDRfullyRated;
    }

    /**
     * @return Wallet operation as result of rating an EDR
     */
    public WalletOperation getWalletOperation() {
        return walletOperation;
    }

    /**
     * @param walletOperation Wallet operation as result of rating an EDR
     */
    public void setWalletOperation(WalletOperation walletOperation) {
        this.walletOperation = walletOperation;
    }
}