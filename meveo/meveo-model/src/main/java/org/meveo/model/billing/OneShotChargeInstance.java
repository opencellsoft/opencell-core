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
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

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
		this.chargeTemplate = oneShotChargeTemplate;
		this.status = InstanceStatusEnum.ACTIVE;
	}

	public OneShotChargeInstance() {

	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
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
