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
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.catalog.OneShotChargeTemplate;

@Entity
@Table(name = "billing_one_shot_charge_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_one_shot_chrg_inst_seq"), })
public class OneShotChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subs_serv_inst_id")
	private ServiceInstance subscriptionServiceInstance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "term_serv_inst_id")
	private ServiceInstance terminationServiceInstance;

	public OneShotChargeInstance(String code, String description, Date chargeDate,
			BigDecimal amountWithoutTax, BigDecimal amount2, Subscription subscription,
			OneShotChargeTemplate oneShotChargeTemplate) {
		this.code = code;
		this.description = description;
		setChargeDate(chargeDate);
		setAmountWithoutTax(amountWithoutTax);
		setAmountWithTax(amount2);
		this.setSubscription(subscription);
		this.setSeller(subscription.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().getSeller());
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
		//if (subscriptionServiceInstance != null) {
		//	subscriptionServiceInstance.getSubscriptionChargeInstances().add(this);
		//}
	}

	public ServiceInstance getTerminationServiceInstance() {
		return terminationServiceInstance;
	}

	public void setTerminationServiceInstance(ServiceInstance terminationServiceInstance) {
		this.terminationServiceInstance = terminationServiceInstance;
		//if (terminationServiceInstance != null) {
		//	terminationServiceInstance.getTerminationChargeInstances().add(this);
		//}
	}

}
