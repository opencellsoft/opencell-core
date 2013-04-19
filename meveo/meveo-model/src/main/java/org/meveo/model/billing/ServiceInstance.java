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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_SERVICE_INSTANCE")
@AttributeOverrides( { @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_SERVICE_INSTANCE_SEQ")
public class ServiceInstance extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_TEMPLATE_ID")
    private ServiceTemplate serviceTemplate;

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
     //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<RecurringChargeInstance> recurringChargeInstances = new ArrayList<RecurringChargeInstance>();

    @OneToMany(mappedBy = "subscriptionServiceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<OneShotChargeInstance> subscriptionChargeInstances = new ArrayList<OneShotChargeInstance>();

    @OneToMany(mappedBy = "terminationServiceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<OneShotChargeInstance> terminationChargeInstances = new ArrayList<OneShotChargeInstance>();


    @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<UsageChargeInstance> usageChargeInstances = new ArrayList<UsageChargeInstance>();
   
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_TERMIN_REASON_ID", nullable = true)
    private SubscriptionTerminationReason subscriptionTerminationReason;

    @Column(name = "QUANTITY")
    protected Integer quantity = 1;

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

	public void setUsageChargeInstances(
			List<UsageChargeInstance> usageChargeInstances) {
		this.usageChargeInstances = usageChargeInstances;
	}

	public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public SubscriptionTerminationReason getSubscriptionTerminationReason() {
        return subscriptionTerminationReason;
    }

    public void setSubscriptionTerminationReason(SubscriptionTerminationReason subscriptionTerminationReason) {
        this.subscriptionTerminationReason = subscriptionTerminationReason;
    }

}
