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

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

/**
 * DiscountplanInstanciation entity.
 */
@Entity
@Table(name = "BILLING_DISCOUNTPLAN_INSTANCIATION")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "BILLING_DISCOUNTPLAN_INSTANCIATION_SEQ")
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
