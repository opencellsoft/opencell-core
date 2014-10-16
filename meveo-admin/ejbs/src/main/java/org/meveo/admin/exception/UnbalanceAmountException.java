/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.admin.exception;

import java.math.BigDecimal;

import javax.ejb.ApplicationException;

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
