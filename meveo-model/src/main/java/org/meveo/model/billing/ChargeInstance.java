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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.ChargeTemplate;

@Entity
@Table(name = "BILLING_CHARGE_INSTANCE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_CHARGE_INSTANCE_SEQ")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@Inheritance(strategy = InheritanceType.JOINED)
public class ChargeInstance extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	protected InstanceStatusEnum status = InstanceStatusEnum.ACTIVE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE")
	protected Date statusDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "TERMINATION_DATE")
	protected Date terminationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHARGE_TEMPLATE_ID")
	protected ChargeTemplate chargeTemplate;

	@Temporal(TemporalType.DATE)
	@Column(name = "CHARGE_DATE")
	protected Date chargeDate;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	protected BigDecimal amountWithoutTax;

	@Column(name = "AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	protected BigDecimal amountWithTax;

	@Column(name = "CRITERIA_1")
	protected String criteria1;

	@Column(name = "CRITERIA_2")
	protected String criteria2;

	@Column(name = "CRITERIA_3")
	protected String criteria3;

	@OneToMany(mappedBy = "chargeInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Set<WalletOperation> walletOperations = new HashSet<WalletOperation>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SELLER_ID")
	private Seller seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	protected Subscription subscription;

	@Column(name = "PR_DESCRIPTION", length = 100)
	protected String prDescription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_CURRENCY")
	private TradingCurrency currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_COUNTRY")
	TradingCountry country;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BILLING_CHRGINST_WALLET", joinColumns = @JoinColumn(name = "CHRG_INSTANCE_ID"), inverseJoinColumns = @JoinColumn(name = "WALLET_INSTANCE_ID"))
	@OrderColumn(name = "INDX")
	private List<WalletInstance> walletInstances = new ArrayList<WalletInstance>();
	

	@Column(name = "IS_PREPAID", length = 1)
	protected Boolean prepaid=Boolean.FALSE;

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
		this.code = chargeTemplate.getCode();
		this.description = chargeTemplate.getDescription();
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public Set<WalletOperation> getWalletOperations() {
		return walletOperations;
	}

	public void setWalletOperations(Set<WalletOperation> walletOperations) {
		this.walletOperations = walletOperations;
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

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
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





	

}
