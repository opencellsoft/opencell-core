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
package org.meveo.admin.exception;

import java.math.BigDecimal;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UnbalanceAmountException extends Exception {
    private static final long serialVersionUID = 1L;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    public UnbalanceAmountException() {
        super();
    }

    public UnbalanceAmountException(BigDecimal debitAmount, BigDecimal creditAmount) {
        super();
        this.creditAmount = creditAmount;
        this.debitAmount = debitAmount;
    }

    public UnbalanceAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnbalanceAmountException(String message) {
        super(message);
    }

    public UnbalanceAmountException(Throwable cause) {
        super(cause);
    }

    /**
     * @return the debitAmount
     */
    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    /**
     * @param debitAmount
     *            the debitAmount to set
     */
    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    /**
     * @return the creditAmount
     */
    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    /**
     * @param creditAmount
     *            the creditAmount to set
     */
    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

}
