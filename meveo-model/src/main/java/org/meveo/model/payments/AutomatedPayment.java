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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@DiscriminatorValue(value = "AP")
public class AutomatedPayment extends Payment {
    private static final long serialVersionUID = 1L;

    @Column(name = "BANK_LOT")
    private String bankLot;

    @Column(name = "BANK_REFERENCE")
    private String bankReference;

    @Column(name = "DEPOSIT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date depositDate;

    @Column(name = "BANK_COLLECTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankCollectionDate;

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

    public Date getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    public Date getBankCollectionDate() {
        return bankCollectionDate;
    }

    public void setBankCollectionDate(Date bankCollectionDate) {
        this.bankCollectionDate = bankCollectionDate;
    }

}
