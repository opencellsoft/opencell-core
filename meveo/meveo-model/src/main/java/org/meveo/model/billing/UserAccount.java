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
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AccountEntity;

/**
 * @author Ignas Lelys
 * @created Dec 3, 2010
 * 
 */
@Entity
@Table(name = "BILLING_USER_ACCOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_USER_ACCOUNT_SEQ")
public class UserAccount extends AccountEntity {

    private static final long serialVersionUID = 1L;

    public static final String ACCOUNT_TYPE = "userAccount.type";

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 10)
    private AccountStatusEnum status = AccountStatusEnum.ACTIVE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STATUS_DATE")
    private Date statusDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SUBSCRIPTION_DATE")
    private Date subscriptionDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TERMINATION_DATE")
    private Date terminationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_ACCOUNT_ID")
    private BillingAccount billingAccount;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Subscription> subscriptions = new ArrayList<Subscription>();

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<InvoiceAgregate> invoiceAgregates = new ArrayList<InvoiceAgregate>();

    @OneToOne( cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name = "WALLET_ID")
    private Wallet wallet;

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public AccountStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AccountStatusEnum status) {
        this.status = status;
        this.statusDate = new Date();
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String getAccountType() {
        return ACCOUNT_TYPE;
    }

    public List<InvoiceAgregate> getInvoiceAgregates() {
        return invoiceAgregates;
    }

    public void setInvoiceAgregates(List<InvoiceAgregate> invoiceAgregates) {
        this.invoiceAgregates = invoiceAgregates;
    }

}
