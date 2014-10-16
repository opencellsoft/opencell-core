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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OneShotChargeTemplate;

@Entity
@Table(name = "BILLING_ONE_SHOT_CHARGE_INST")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_ONE_SHOT_CHRG_INST_SEQ")
public class OneShotChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBS_SERV_INST_ID")
	private ServiceInstance subscriptionServiceInstance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TERM_SERV_INST_ID")
	private ServiceInstance terminationServiceInstance;

	public OneShotChargeInstance(String code, String description, Date chargeDate,
			BigDecimal amountWithoutTax, BigDecimal amount2, Subscription subscription,
			OneShotChargeTemplate oneShotChargeTemplate, Seller seller) {
		this.code = code;
		this.description = description;
		setChargeDate(chargeDate);
		setAmountWithoutTax(amountWithoutTax);
		setAmountWithTax(amount2);
		this.subscription = subscription;
		this.setSeller(seller);
		this.setCountry(subscription.getUserAccount().getBillingAccount().getTradingCountry());
		this.setCurrency(subscription.getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency());
		this.chargeTemplate = oneShotChargeTemplate;
		this.status = InstanceStatusEnum.ACTIVE;
	}

	public OneShotChargeInstance() {

	}

	public ServiceInstance getSubscriptionServiceInstance() {
		return subscriptionServiceInstance;
	}

	public void setSubscriptionServiceInstance(ServiceInstance subscriptionServiceInstance) {
		this.subscriptionServiceInstance = subscriptionServiceInstance;
		if (subscriptionServiceInstance != null) {
			subscriptionServiceInstance.getSubscriptionChargeInstances().add(this);
		}
	}

	public ServiceInstance getTerminationServiceInstance() {
		return terminationServiceInstance;
	}

	public void setTerminationServiceInstance(ServiceInstance terminationServiceInstance) {
		this.terminationServiceInstance = terminationServiceInstance;
		if (terminationServiceInstance != null) {
			terminationServiceInstance.getTerminationChargeInstances().add(this);
		}
	}

}
