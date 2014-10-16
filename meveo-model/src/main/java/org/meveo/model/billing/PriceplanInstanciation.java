/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

/**
 * PriceplanInstanciation entity.
 */
@Entity
@Table(name = "BILLING_PRICEPLAN_INST")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_PRICEPLAN_INST_SEQ")
public class PriceplanInstanciation extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_ACCOUNT_ID")
	private BillingAccount billingAccount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_SUBSCRIPTION_DATE")
	private Date startSubscriptionDate;

	@Column(name = "CHARGE_CODE")
	private String chargeCode;

	@Column(name = "USAGE_TYPE")
	private EventTypeEnum usageType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_SUBSCRIPTION_DATE")
	private Date endSubscriptionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_AGE_MIN_IN_MONTH")
	private Date subscriptionAgeMinInMonth;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_AGE_MAX_IN_MONTH")
	private Date subscriptionAgeMaxInMonth;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_EVENT_DATE")
	private Date startEventDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_EVENT_DATE")
	private Date endEventDate;

	@Column(name = "StringCriteria1")
	private String stringcriteria1;

	@Column(name = "StringCriteria2")
	private String stringcriteria2;

	@Column(name = "StringCriteria3")
	private String stringcriteria3;

	@Column(name = "AMOUNT_WITHOUT_TAX")
	private BigDecimal amountWithoutTax;

	@Column(name = "PR_AMOUNT_WITHOUT_TAX")
	private BigDecimal prAmountWithoutTax;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_CURRENCY_ID")
	private TradingCurrency tradingCurrency;

	public EventTypeEnum getUsageType() {
		return usageType;
	}

	public void setUsageType(EventTypeEnum usageType) {
		this.usageType = usageType;
	}

	public BigDecimal getPrAmountWithoutTax() {
		return prAmountWithoutTax;
	}

	public void setPrAmountWithoutTax(BigDecimal prAmountWithoutTax) {
		this.prAmountWithoutTax = prAmountWithoutTax;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public Date getSubscriptionAgeMinInMonth() {
		return subscriptionAgeMinInMonth;
	}

	public Date getStartSubscriptionDate() {
		return startSubscriptionDate;
	}

	public void setStartSubscriptionDate(Date startSubscriptionDate) {
		this.startSubscriptionDate = startSubscriptionDate;
	}

	public Date getEndSubscriptionDate() {
		return endSubscriptionDate;
	}

	public void setEndSubscriptionDate(Date endSubscriptionDate) {
		this.endSubscriptionDate = endSubscriptionDate;
	}

	public void setSubscriptionAgeMinInMonth(Date subscriptionAgeMinInMonth) {
		this.subscriptionAgeMinInMonth = subscriptionAgeMinInMonth;
	}

	public Date getSubscriptionAgeMaxInMonth() {
		return subscriptionAgeMaxInMonth;
	}

	public void setSubscriptionAgeMaxInMonth(Date subscriptionAgeMaxInMonth) {
		this.subscriptionAgeMaxInMonth = subscriptionAgeMaxInMonth;
	}

	public Date getStartEventDate() {
		return startEventDate;
	}

	public void setStartEventDate(Date startEventDate) {
		this.startEventDate = startEventDate;
	}

	public Date getEndEventDate() {
		return endEventDate;
	}

	public void setEndEventDate(Date endEventDate) {
		this.endEventDate = endEventDate;
	}

	public String getStringcriteria1() {
		return stringcriteria1;
	}

	public void setStringcriteria1(String stringcriteria1) {
		this.stringcriteria1 = stringcriteria1;
	}

	public String getStringcriteria2() {
		return stringcriteria2;
	}

	public void setStringcriteria2(String stringcriteria2) {
		this.stringcriteria2 = stringcriteria2;
	}

	public String getStringcriteria3() {
		return stringcriteria3;
	}

	public void setStringcriteria3(String stringcriteria3) {
		this.stringcriteria3 = stringcriteria3;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

}
