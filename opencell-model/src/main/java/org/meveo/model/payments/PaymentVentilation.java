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
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

// TODO: Auto-generated Javadoc
/**
 * Ventilation of payment to customerAccount.
 *
 * @author hznibar
 */
@Entity
@Table(name = "ar_payment_ventilation")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_ventilation_seq"), })
public class PaymentVentilation extends AuditableEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The original OTG. */
    @ManyToOne
    @JoinColumn(name = "original_ot_id")
    private OtherTransaction originalOT;

    /** Account operation. */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_operation_id")
    private AccountOperation accountOperation;

    /** The new OTG. */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "new_ot_id")
    private OtherTransaction newOT;

    /** The customer account. */
    @ManyToOne
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    /** The ventilation amount. */
    @Column(name = "ventilation_amount", precision = 23, scale = 12)
    private BigDecimal ventilationAmount;

    /** The ventilation date. */
    @Column(name = "ventilation_date")
    @Temporal(TemporalType.DATE)
    private Date ventilationDate;

    /**
     * Instantiates a new payment ventilation.
     */
    public PaymentVentilation() {
    }
    

    public OtherTransaction getOriginalOT() {
        return originalOT;
    }



    public void setOriginalOT(OtherTransaction originalOT) {
        this.originalOT = originalOT;
    }



    public OtherTransaction getNewOT() {
        return newOT;
    }



    public void setNewOT(OtherTransaction newOT) {
        this.newOT = newOT;
    }



    public AccountOperation getAccountOperation() {
        return accountOperation;
    }

    public void setAccountOperation(AccountOperation accountOperation) {
        this.accountOperation = accountOperation;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public BigDecimal getVentilationAmount() {
        return ventilationAmount;
    }

    public void setVentilationAmount(BigDecimal ventilationAmount) {
        this.ventilationAmount = ventilationAmount;
    }

    public Date getVentilationDate() {
        return ventilationDate;
    }

    public void setVentilationDate(Date ventilationDate) {
        this.ventilationDate = ventilationDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.model.BaseEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof PaymentVentilation)) {
            return false;
        }

        PaymentVentilation other = (PaymentVentilation) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (newOT != null && accountOperation != null) {
            if (newOT.equals(other.getNewOT()) && accountOperation.equals(other.getAccountOperation())) {
                return true;
            }
        }
        return false;
    }

}
