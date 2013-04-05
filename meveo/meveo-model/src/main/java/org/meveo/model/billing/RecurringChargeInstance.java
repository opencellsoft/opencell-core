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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.catalog.RecurringChargeTemplate;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_RECURRING_CHARGE_INST")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_RECURRING_CHRG_INST_SEQ")
public class RecurringChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECURRING_CHRG_TMPL_ID")
    private RecurringChargeTemplate recurringChargeTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_INSTANCE_ID")
    protected ServiceInstance serviceInstance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SUBSCRIPTION_DATE")
    protected Date subscriptionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NEXT_CHARGE_DATE")
    protected Date nextChargeDate;

    public RecurringChargeInstance(String code, String description, Date subscriptionDate, BigDecimal amountWithoutTax,
            BigDecimal amount2, Subscription subscription, RecurringChargeTemplate recurringChargeTemplate,
            ServiceInstance serviceInstance) {
        this.code = code;
        this.description = description;
        this.subscriptionDate = subscriptionDate;
        this.chargeDate = subscriptionDate;
        this.amountWithoutTax = amountWithoutTax;
        this.amount2 = amount2;
        this.chargeTemplate = recurringChargeTemplate;
        this.serviceInstance = serviceInstance;
        this.subscription = subscription;
    }

    public RecurringChargeInstance() {

    }

    public RecurringChargeTemplate getRecurringChargeTemplate() {
        return recurringChargeTemplate;
    }

    public void setRecurringChargeTemplate(RecurringChargeTemplate recurringChargeTemplate) {
        this.recurringChargeTemplate = recurringChargeTemplate;
        this.code = recurringChargeTemplate.getCode();
        this.description = recurringChargeTemplate.getDescription();
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
        if (serviceInstance != null) {
            serviceInstance.getRecurringChargeInstances().add(this);
        }
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getNextChargeDate() {
        return nextChargeDate;
    }

    public void setNextChargeDate(Date nextChargeDate) {
        this.nextChargeDate = nextChargeDate;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

}
