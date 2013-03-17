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
import javax.validation.constraints.NotNull;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;

/**
 * Subscription
 * 
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_SUBSCRIPTION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_SUBSCRIPTION_SEQ")
public class Subscription extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFER_ID")
    private OfferTemplate offer;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private SubscriptionStatusEnum status = SubscriptionStatusEnum.CREATED;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STATUS_DATE")
    private Date statusDate = new Date();;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SUBSCRIPTION_DATE")
    private Date subscriptionDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TERMINATION_DATE")
    private Date terminationDate;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
     //TODO : Add orphanRemoval annotation. @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<OneShotChargeInstance> oneShotChargeInstances = new ArrayList<OneShotChargeInstance>();

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<RecurringChargeInstance> recurringChargeInstances = new ArrayList<RecurringChargeInstance>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID", nullable = false)
    @NotNull
    private UserAccount userAccount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_AGREMENT_DATE")
    private Date endAgrementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_TERMIN_REASON_ID", nullable = true)
    private SubscriptionTerminationReason subscriptionTerminationReason;
    
    @Column(name = "DEFAULT_LEVEL")
    private Boolean defaultLevel = true;


    public Date getEndAgrementDate() {
        return endAgrementDate;
    }

    public void setEndAgrementDate(Date endAgrementDate) {
        this.endAgrementDate = endAgrementDate;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    public OfferTemplate getOffer() {
        return offer;
    }

    public void setOffer(OfferTemplate offer) {
        this.offer = offer;
    }

    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatusEnum status) {
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

    public List<OneShotChargeInstance> getOneShotChargeInstances() {
        return oneShotChargeInstances;
    }

    public void setOneShotChargeInstances(List<OneShotChargeInstance> oneShotChargeInstances) {
        this.oneShotChargeInstances = oneShotChargeInstances;
    }

    public SubscriptionTerminationReason getSubscriptionTerminationReason() {
        return subscriptionTerminationReason;
    }

    public void setSubscriptionTerminationReason(SubscriptionTerminationReason subscriptionTerminationReason) {
        this.subscriptionTerminationReason = subscriptionTerminationReason;
    }

    public List<RecurringChargeInstance> getRecurringChargeInstances() {
        return recurringChargeInstances;
    }

    public void setRecurringChargeInstances(List<RecurringChargeInstance> recurringChargeInstances) {
        this.recurringChargeInstances = recurringChargeInstances;
    }

	public Boolean getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Boolean defaultLevel) {
		this.defaultLevel = defaultLevel;
	}
    
    

}
