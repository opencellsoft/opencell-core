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
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;

@Entity
@ObservableEntity
@Table(name = "BILLING_SERVICE_INSTANCE")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_SERVICE_INSTANCE_SEQ")
public class ServiceInstance extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_TEMPLATE_ID")
	private ServiceTemplate serviceTemplate;

	@ManyToOne
	@JoinColumn(name = "INVOICING_CALENDAR_ID")
	private Calendar invoicingCalendar;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private InstanceStatusEnum status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE")
	private Date statusDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_DATE")
	private Date subscriptionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMINATION_DATE")
	private Date terminationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_AGREMENT_DATE")
	private Date endAgrementDate;

	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<RecurringChargeInstance> recurringChargeInstances = new ArrayList<RecurringChargeInstance>();

	@OneToMany(mappedBy = "subscriptionServiceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<OneShotChargeInstance> subscriptionChargeInstances = new ArrayList<OneShotChargeInstance>();

	@OneToMany(mappedBy = "terminationServiceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<OneShotChargeInstance> terminationChargeInstances = new ArrayList<OneShotChargeInstance>();

	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<UsageChargeInstance> usageChargeInstances = new ArrayList<UsageChargeInstance>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_TERMIN_REASON_ID", nullable = true)
	private SubscriptionTerminationReason subscriptionTerminationReason;

	@Column(name = "QUANTITY", precision = NB_PRECISION, scale = NB_DECIMALS)
	protected BigDecimal quantity = BigDecimal.ONE;

	public Date getEndAgrementDate() {
		return endAgrementDate;
	}

	public void setEndAgrementDate(Date endAgrementDate) {
		this.endAgrementDate = endAgrementDate;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
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

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public Calendar getInvoicingCalendar() {
		return invoicingCalendar;
	}

	public void setInvoicingCalendar(Calendar invoicingCalendar) {
		this.invoicingCalendar = invoicingCalendar;
	}

	public List<RecurringChargeInstance> getRecurringChargeInstances() {
		return recurringChargeInstances;
	}

	public void setRecurringChargeInstances(List<RecurringChargeInstance> recurringChargeInstances) {
		this.recurringChargeInstances = recurringChargeInstances;
	}

	public List<OneShotChargeInstance> getSubscriptionChargeInstances() {
		return subscriptionChargeInstances;
	}

	public void setSubscriptionChargeInstances(List<OneShotChargeInstance> subscriptionChargeInstances) {
		this.subscriptionChargeInstances = subscriptionChargeInstances;
	}

	public List<OneShotChargeInstance> getTerminationChargeInstances() {
		return terminationChargeInstances;
	}

	public void setTerminationChargeInstances(List<OneShotChargeInstance> terminationChargeInstances) {
		this.terminationChargeInstances = terminationChargeInstances;
	}

	public List<UsageChargeInstance> getUsageChargeInstances() {
		return usageChargeInstances;
	}

	public void setUsageChargeInstances(List<UsageChargeInstance> usageChargeInstances) {
		this.usageChargeInstances = usageChargeInstances;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public SubscriptionTerminationReason getSubscriptionTerminationReason() {
		return subscriptionTerminationReason;
	}

	public void setSubscriptionTerminationReason(SubscriptionTerminationReason subscriptionTerminationReason) {
		this.subscriptionTerminationReason = subscriptionTerminationReason;
	}

	public String getDescriptionAndStatus() {
		if (!StringUtils.isBlank(description))
			return description + ", " + status;
		else
			return status.name();
	}

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof ServiceInstance)) {
            return false;
        }

        ServiceInstance other = (ServiceInstance) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }
        return false;
    }
}
