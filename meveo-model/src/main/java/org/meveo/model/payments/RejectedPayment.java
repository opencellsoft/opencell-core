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
package org.meveo.model.payments;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@DiscriminatorValue(value = "R")
public class RejectedPayment extends AccountOperation {

    private static final long serialVersionUID = 1L;

    @Column(name = "REJECTED_TYPE")
    @Enumerated(EnumType.STRING)
    private RejectedType rejectedType;

    @Column(name = "BANK_LOT")
    private String bankLot;

    @Column(name = "BANK_REFERENCE")
    private String bankReference;

    @Column(name = "REJECTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rejectedDate;

    @Column(name = "REJECTED_DESCRIPTION")
    private String rejectedDescription;

    @Column(name = "REJECTED_CODE")
    private String rejectedCode;

    public String getBankLot() {
        return bankLot;
    }

    public void setBankLot(String bankLot) {
        this.bankLot = bankLot;
    }

    public String getBankReference() {
        return bankReference;
    }

    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    public Date getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getRejectedDescription() {
        return rejectedDescription;
    }

    public void setRejectedDescription(String rejectedDescription) {
        this.rejectedDescription = rejectedDescription;
    }

    public String getRejectedCode() {
        return rejectedCode;
    }

    public void setRejectedCode(String rejectedCode) {
        this.rejectedCode = rejectedCode;
    }

    public RejectedType getRejectedType() {
        return rejectedType;
    }

    public void setRejectedType(RejectedType rejectedType) {
        this.rejectedType = rejectedType;
    }

}
