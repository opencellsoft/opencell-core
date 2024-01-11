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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.IWFEntity;
import org.meveo.model.RegistrationNumber;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.IInvoicingMinimumApplicable;

/**
 * User account
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@WorkflowedEntity
@CustomFieldEntity(cftCodePrefix = "UserAccount", inheritCFValuesFrom = "billingAccount")
@ExportIdentifier({ "code" })
@DiscriminatorValue(value = "ACCT_UA")
@Table(name = "billing_user_account")
@NamedQueries({ @NamedQuery(name = "UserAccount.findByCode", query = "select u from  UserAccount u where u.code = :code"),
        @NamedQuery(name = "UserAccount.getUserAccountsWithMinAmountELNotNullByBA", query = "select u from UserAccount u where u.minimumAmountEl is not null AND u.status = org.meveo.model.billing.AccountStatusEnum.ACTIVE AND u.billingAccount=:billingAccount"),
        @NamedQuery(name = "UserAccount.getUserAccountsWithMinAmountELNotNullByUA", query = "select u from UserAccount u where u.minimumAmountEl is not null AND u.status = org.meveo.model.billing.AccountStatusEnum.ACTIVE AND u=:userAccount"),
        @NamedQuery(name = "UserAccount.getMinimumAmountUsed", query = "select u.minimumAmountEl from UserAccount u where u.minimumAmountEl is not null"),
        @NamedQuery(name = "UserAccount.getCountByParent", query = "select count(*) from UserAccount ua where ua.billingAccount=:parent") })
public class UserAccount extends AccountEntity implements IInvoicingMinimumApplicable, IWFEntity, ICounterEntity, ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * Account status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private AccountStatusEnum status = AccountStatusEnum.ACTIVE;

    /**
     * Last account status change timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate = new Date();

    /**
     * Billing account creation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate = new Date();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_useraccount_id",referencedColumnName = "id")
    private UserAccount parentUserAccount;
    
    @OneToMany(mappedBy = "parentUserAccount", fetch = FetchType.LAZY)
    private List<UserAccount> userAccounts = new ArrayList<>();

    /**
     * Account termination date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    /**
     * Parent Billing account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    /**
     * Account's subscriptions
     */
    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    /**
     * Primary waller
     */
    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    /**
     * Prepaid wallets
     */
    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @MapKey(name = "code")
    private Map<String, WalletInstance> prepaidWallets = new HashMap<>();

    /**
     * Counters instantiated on the User account with Counter template code as a key
     */
    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    private Map<String, CounterInstance> counters = new HashMap<>();

    /**
     * Account termination reason
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termin_reason_id")
    private SubscriptionTerminationReason terminationReason;
    
    /**
     * is the user account a consumer
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_consumer")
    private Boolean isConsumer=Boolean.TRUE;
	
	@OneToMany(mappedBy = "userAccount")
	private List<RegistrationNumber> registrationNumbers = new ArrayList<>();
	
	public List<RegistrationNumber> getRegistrationNumbers() {
		return registrationNumbers;
	}
	
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
        if (this.status != status) {
            this.statusDate = new Date();
        }
        this.status = status;
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

	public UserAccount getParentUserAccount() {
		return parentUserAccount;
	}

	public void setParentUserAccount(UserAccount parentUserAccount) {
		this.parentUserAccount = parentUserAccount;
	}

	public List<UserAccount> getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(List<UserAccount> userAccounts) {
		this.userAccounts = userAccounts;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    public Map<String, WalletInstance> getPrepaidWallets() {
        return prepaidWallets;
    }

    public void setPrepaidWallets(Map<String, WalletInstance> prepaidWallets) {
        this.prepaidWallets = prepaidWallets;
    }

    public Map<String, CounterInstance> getCounters() {
        return counters;
    }

    public void setCounters(Map<String, CounterInstance> counters) {
        this.counters = counters;
    }

    public SubscriptionTerminationReason getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
        this.terminationReason = terminationReason;
    }

    public WalletInstance getWalletInstance(String walletCode) {
        WalletInstance result = wallet;
        if (!"PRINCIPAL".equals(walletCode) && prepaidWallets.containsKey(walletCode)) {
            result = prepaidWallets.get(walletCode);
        }
        return result;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (billingAccount != null) {
            return new ICustomFieldEntity[] { billingAccount };
        }
        return null;
    }

    @Override
    public BusinessEntity getParentEntity() {
        return billingAccount;
    }

    @Override
    public Class<? extends BusinessEntity> getParentEntityType() {
        return BillingAccount.class;
    }

	public Boolean getIsConsumer() {
		return isConsumer;
	}

	public void setIsConsumer(Boolean isConsumer) {
		this.isConsumer = isConsumer;
	}
	
	  public Seller getSeller() {
	    	if(billingAccount==null) {
	    		return null;
	    	}
	    	return billingAccount.getSeller();
	    }
    
}