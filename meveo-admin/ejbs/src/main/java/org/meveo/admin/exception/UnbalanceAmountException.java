/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
