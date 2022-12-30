/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;

/**
 * Instantiated counter
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "billing_counter")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_counter_instance_seq"), })
@NamedQueries({ 
	@NamedQuery(name = "CounterInstance.findByCounterAndCustomer", query = "SELECT ci FROM CounterInstance ci left join ci.customer ca where ci.counterTemplate.code=:counterTemplateCode"),
	@NamedQuery(name = "CounterInstance.findByCounterAndCustomerAccount", query = "SELECT ci.id FROM CounterInstance ci left join ci.customerAccount cust where cust.status='ACTIVE' and ci.counterTemplate.code=:counterTemplateCode"),
	@NamedQuery(name = "CounterInstance.findByCounterAndBillingAccount", query = "SELECT ci.id FROM CounterInstance ci left join ci.billingAccount ba where ba.status='ACTIVE' and ci.counterTemplate.code=:counterTemplateCode"),
	@NamedQuery(name = "CounterInstance.findByCounterAndUserAccount", query = "SELECT ci.id FROM CounterInstance ci left join ci.userAccount ua where ua.status='ACTIVE' and ci.counterTemplate.code=:counterTemplateCode"),
	@NamedQuery(name = "CounterInstance.findByCounterAndSubscription", query = "SELECT ci.id FROM CounterInstance ci left join ci.subscription su where su.status='ACTIVE' and ci.counterTemplate.code=:counterTemplateCode"),
	@NamedQuery(name = "CounterInstance.findByCounterAndService", query = "SELECT ci.id FROM CounterInstance ci left join ci.serviceInstance si where si.status='ACTIVE' and ci.counterTemplate.code=:counterTemplateCode")
	
})
public class CounterInstance extends BusinessEntity {
    private static final long serialVersionUID = -4924601467998738157L;

    /**
     * Counter template that counter was instantiated from
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_template_id")
    private CounterTemplate counterTemplate;

    /**
     * Customer  that counter is tracked on.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Customer account that counter is tracked on.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    /**
     * User account that counter is tracked on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Billing account that counter is tracked on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    /**
     * Subscription that counter is tracked on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Service instance that counter is tracked on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    /**
     * Counter periods
     */
    @OneToMany(mappedBy = "counterInstance", fetch = FetchType.LAZY)
    private List<CounterPeriod> counterPeriods = new ArrayList<>();
    
    /**
     * usage charges instances
     */
    @OneToMany(mappedBy = "counter", fetch = FetchType.LAZY)
    private List<UsageChargeInstance> usageChargeInstances = new ArrayList<>();
    
    /**
     * charge instances related as accumulator counters
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_chrg_inst_counter", joinColumns = @JoinColumn(name = "counter_instance_id"), inverseJoinColumns = @JoinColumn(name = "chrg_instance_id"))
    @OrderColumn(name = "INDX")
    private List<ChargeInstance> chargeInstances = new ArrayList<>();

    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
        if (counterTemplate != null) {
            this.code = counterTemplate.getCode();
            this.description = counterTemplate.getDescription();
        } else {
            this.code = null;
            this.description = null;
        }
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<CounterPeriod> getCounterPeriods() {
        return counterPeriods;
    }

    public void setCounterPeriods(List<CounterPeriod> counterPeriods) {
        this.counterPeriods = counterPeriods;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public CounterPeriod getCounterPeriod(Date date) {
        for (CounterPeriod counterPeriod : counterPeriods) {
            if (DateUtils.isDateTimeWithinPeriod(date, counterPeriod.getPeriodStartDate(), counterPeriod.getPeriodEndDate())) {
                return counterPeriod;
            }
        }
        return null;
    }

    /**
     * Gets the subscription
     * @return a subscription
     */
    public Subscription getSubscription() {
        return subscription;
    }

    /**
     * Sets subscription
     * @param subscription a subscription
     */
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * Gets a service instance
     * @return a service instance
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * Sets a service instance.
     *
     * @param serviceInstance a service instance
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * Gets Customer.
     *
     * @return a customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets Customer.
     *
     * @param customer a customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets a customerAccount.
     *
     * @return a customerAccount
     */
    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    /**
     * Sets a customerAccount.
     *
     * @param customerAccount a customerAccount
     */
    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

	public List<UsageChargeInstance> getUsageChargeInstances() {
		return usageChargeInstances;
	}

	public void setUsageChargeInstances(List<UsageChargeInstance> usageChargeInstances) {
		this.usageChargeInstances = usageChargeInstances;
	}

	public List<ChargeInstance> getChargeInstances() {
		return chargeInstances;
	}

	public void setChargeInstances(List<ChargeInstance> chargeInstances) {
		this.chargeInstances = chargeInstances;
	}
    
    
}
