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

import org.meveo.model.AuditableEntity;

/**
 * DiscountplanInstanciation entity.
 */
@Entity
@Table(name = "BILLING_DISCPLAN_INST")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_DISC_INST_SEQ")
public class DiscountplanInstanciation extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_ACCOUNT_ID")
	private BillingAccount billingAccount;

	@Column(name = "USAGE_TYPE")
	private EventTypeEnum usageType;

	@Column(name = "CHARGE_CODE")
	private String chargeCode;

	@Column(name = "START_SUBSCRIPTION_DATE")
	private Date startSubscriptionDate;

	@Column(name = "END_SUBSCRIPTION_DATE")
	private Date endSubscriptionDate;

	@Column(name = "NB_PERIOD")
	private Integer nbPeriod;

	@Column(name = "POURCENT")
	private BigDecimal pourcent;

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccountId(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public EventTypeEnum getUsageType() {
		return usageType;
	}

	public void setUsageType(EventTypeEnum usageType) {
		this.usageType = usageType;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
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

	public Integer getNbPeriod() {
		return nbPeriod;
	}

	public void setNbPeriod(Integer nbPeriod) {
		this.nbPeriod = nbPeriod;
	}

	public BigDecimal getPourcent() {
		return pourcent;
	}

	public void setPourcent(BigDecimal pourcent) {
		this.pourcent = pourcent;
	}

}
