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
package org.meveo.model.billing;

/**
 * Recurring charge application mode
 * 
 * @author Andrius Karpavicius
 */
public enum ChargeApplicationModeEnum {

    /**
     * A regular periodic charge application
     */
    SUBSCRIPTION,

    /**
     * Apply charges to reach Service's agreement end date
     */
    AGREEMENT,

    /**
     * Reimburse charges from termination date to the last Wallet operation of that charge
     */
    REIMBURSMENT,

    /**
     * Rerating of existing charges
     */
    RERATING,

    /**
     * Rerating of reimbursement charges
     */
    RERATING_REIMBURSEMENT;

    public String toString() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    /**
     * Is this a reimbursement related mode
     * 
     * @return True if its reimbursement or rerating reimbursement
     */
    public boolean isReimbursement() {
        return this == REIMBURSMENT || this == RERATING_REIMBURSEMENT;
    }
}