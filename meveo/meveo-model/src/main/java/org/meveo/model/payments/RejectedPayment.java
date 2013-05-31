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
