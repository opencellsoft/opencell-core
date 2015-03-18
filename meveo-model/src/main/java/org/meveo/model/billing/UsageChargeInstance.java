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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "BILLING_USAGE_CHARGE_INST")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_USAGE_CHARGE_INST_SEQ")
@NamedQueries({
        @NamedQuery(name = "UsageChargeInstance.listPrepaidActive", query = "SELECT c FROM UsageChargeInstance c where c.prepaid=true and "
                + "c.status=org.meveo.model.billing.InstanceStatusEnum.ACTIVE"),
        @NamedQuery(name = "UsageChargeInstance.listActive", query = "SELECT c FROM UsageChargeInstance c where c.status=org.meveo.model.billing.InstanceStatusEnum.ACTIVE") })
public class UsageChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_INSTANCE_ID")
	private ServiceInstance serviceInstance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTER_ID")
	private CounterInstance counter;

	@Column(name = "UNITY_DESCRIPTION", length = 20)
	private String unityDescription;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE")
	private Date lastUpdate;

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
		//if (serviceInstance != null) {
		//	serviceInstance.getUsageChargeInstances().add(this);
		//}
	}

	public CounterInstance getCounter() {
		return counter;
	}

	public void setCounter(CounterInstance counter) {
		this.counter = counter;
	}

	public String getUnityDescription() {
		return unityDescription;
	}

	public void setUnityDescription(String unityDescription) {
		this.unityDescription = unityDescription;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
