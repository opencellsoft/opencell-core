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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@ObservableEntity
@Table(name = "billing_charge_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_charge_instance_seq"), })
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@Inheritance(strategy = InheritanceType.JOINED)
public class ChargeInstance extends BusinessEntity {

	private static final long serialVersionUID = 1L;
    
	/**
	 * Specifies that charge does not apply to any order
	 */
	public static String NO_ORDER_NUMBER ="none";
    
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	protected InstanceStatusEnum status = InstanceStatusEnum.ACTIVE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date")
	protected Date statusDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "termination_date")
	protected Date terminationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "charge_template_id")
	protected ChargeTemplate chargeTemplate;

	@ManyToOne
	@JoinColumn(name = "invoicing_calendar_id")
	private Calendar invoicingCalendar;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "charge_date")
	protected Date chargeDate;

	@Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	protected BigDecimal amountWithoutTax;

	@Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	protected BigDecimal amountWithTax;

	@Column(name = "criteria_1", length = 255)
	@Size(max = 255)
	protected String criteria1;

	@Column(name = "criteria_2", length = 255)
    @Size(max = 255)
	protected String criteria2;

	@Column(name = "criteria_3", length = 255)
    @Size(max = 255)
	protected String criteria3;

	@OneToMany(mappedBy = "chargeInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Set<WalletOperation> walletOperations = new HashSet<WalletOperation>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id")
	protected UserAccount userAccount;
	
	///Might be null, for productCharges for instance
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_id")
	protected Subscription subscription;

	@Column(name = "pr_description", length = 255)
	@Size(max = 255)
	protected String prDescription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency")
	private TradingCurrency currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_country")
	private TradingCountry country;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "billing_chrginst_wallet", joinColumns = @JoinColumn(name = "chrg_instance_id"), inverseJoinColumns = @JoinColumn(name = "wallet_instance_id"))
	@OrderColumn(name = "INDX")
	private List<WalletInstance> walletInstances = new ArrayList<WalletInstance>();
	
	@Transient 
	private List<WalletOperation> sortedWalletOperations;

	@Type(type="numeric_boolean")
    @Column(name = "is_prepaid")
	protected Boolean prepaid=Boolean.FALSE;

    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;
	
	public String getCriteria1() {
		return criteria1;
	}

	public void setCriteria1(String criteria1) {
		this.criteria1 = criteria1;
	}

	public String getCriteria2() {
		return criteria2;
	}

	public void setCriteria2(String criteria2) {
		this.criteria2 = criteria2;
	}

	public String getCriteria3() {
		return criteria3;
	}

	public void setCriteria3(String criteria3) {
		this.criteria3 = criteria3;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public InstanceStatusEnum getStatus() {
		return status;
	}

	public void setStatus(InstanceStatusEnum status) {
		this.status = status;
		this.statusDate = new Date();
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public ChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}

    public void setChargeTemplate(ChargeTemplate chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
        if (chargeTemplate == null) {
            this.code = null;
            this.description = null;
        } else {
            this.code = chargeTemplate.getCode();
            this.description = chargeTemplate.getDescription();
        }
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public Calendar getInvoicingCalendar() {
		return invoicingCalendar;
	}

	public void setInvoicingCalendar(Calendar invoicingCalendar) {
		this.invoicingCalendar = invoicingCalendar;
	}

	public Set<WalletOperation> getWalletOperations() {
		return walletOperations;
	}

	public void setWalletOperations(Set<WalletOperation> walletOperations) {
		this.walletOperations = walletOperations;
	}

    public List<WalletOperation> getWalletOperationsSorted() {
        if (sortedWalletOperations == null) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.debug("getSortedWalletOperations");
            sortedWalletOperations = new ArrayList<WalletOperation>(getWalletOperations());

            Collections.sort(sortedWalletOperations, new Comparator<WalletOperation>() {
                public int compare(WalletOperation c0, WalletOperation c1) {
                    return c1.getOperationDate().compareTo(c0.getOperationDate());
                }
            });
        }

        return sortedWalletOperations;
    }
	
	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
		if(subscription.getUserAccount()!=null){
			this.setUserAccount(subscription.getUserAccount());
		}
	}

	public TradingCurrency getCurrency() {
		return currency;
	}

	public void setCurrency(TradingCurrency currency) {
		this.currency = currency;
	}

	public TradingCountry getCountry() {
		return country;
	}

	public void setCountry(TradingCountry country) {
		this.country = country;
	}

	public List<WalletInstance> getWalletInstances() {
		return walletInstances;
	}

	public void setWalletInstances(List<WalletInstance> walletInstances) {
		this.walletInstances = walletInstances;
	}

	public Boolean getPrepaid() {
		return prepaid;
	}

	public void setPrepaid(Boolean prepaid) {
		this.prepaid = prepaid;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

}
