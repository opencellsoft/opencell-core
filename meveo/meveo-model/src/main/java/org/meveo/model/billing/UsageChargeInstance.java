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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "BILLING_USAGE_CHARGE_INST")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "BILLING_USAGE_CHARGE_INST_SEQ")
public class UsageChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_INSTANCE_ID")
	private ServiceInstance serviceInstance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTER_ID")
	private CounterInstance counter;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE")
	private Date lastUpdate;

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
		if (serviceInstance != null) {
			serviceInstance.getUsageChargeInstances().add(this);
		}
	}

	public CounterInstance getCounter() {
		return counter;
	}

	public void setCounter(CounterInstance counter) {
		this.counter = counter;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

}
