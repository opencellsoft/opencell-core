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
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.OrderHistory;
import org.meveo.model.order.OrderItemActionEnum;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "SERVICE_INSTANCE")
@Table(name = "billing_service_instance")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_service_instance_seq"), })
public class ServiceInstance extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    private ServiceTemplate serviceTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicing_calendar_id")
    private Calendar invoicingCalendar;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstanceStatusEnum status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_agrement_date")
    private Date endAgreementDate;

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
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason subscriptionTerminationReason;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity = BigDecimal.ONE;

    /**
     * Used to track if "quantity" field value has changed. Value is populated on postLoad, postPersist and postUpdate JPA events
     */
    @Transient
    private BigDecimal previousQuantity;

    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rate_until_date")
    private Date rateUntilDate;

    @OneToMany(mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<OrderHistory> orderHistories;

    /**
     * PK of OrderItem.id.
     */
    @Transient
    private Long orderItemId;

    @Transient
    private OrderItemActionEnum orderItemAction;

    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getRateUntilDate() {
        return rateUntilDate;
    }

    public void setRateUntilDate(Date rateUntilDate) {
        this.rateUntilDate = rateUntilDate;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
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

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { serviceTemplate };
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void trackPreviousValues() {
        previousQuantity = quantity;
    }

    /**
     * Check if current and previous "quantity" field values match. Note: previous value is set to current value at postLoad, postPersist, postUpdate JPA events
     * 
     * @return True if current and previous "quantity" field values DO NOT match
     */
    public boolean isQuantityChanged() {
        return quantity == null ? previousQuantity != null : previousQuantity == null ? true : quantity.compareTo(previousQuantity) != 0;
    }

    public BigDecimal getPreviousQuantity() {
        return previousQuantity;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public OrderItemActionEnum getOrderItemAction() {
        return orderItemAction;
    }

    public void setOrderItemAction(OrderItemActionEnum orderItemAction) {
        this.orderItemAction = orderItemAction;
    }

    public List<OrderHistory> getOrderHistories() {
        return orderHistories;
    }

    public void setOrderHistories(List<OrderHistory> orderHistories) {
        this.orderHistories = orderHistories;
    }
}