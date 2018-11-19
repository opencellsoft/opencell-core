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
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.OrderHistory;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.shared.DateUtils;

/**
 * Service subscribed to.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0.1
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "SERVICE_INSTANCE", inheritCFValuesFrom = "serviceTemplate")
@Table(name = "billing_service_instance")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_service_instance_seq"), })
@NamedQueries({
        @NamedQuery(name = "ServiceInstance.getExpired", query = "select s.id from ServiceInstance s where s.subscription.status in (:subscriptionStatuses) AND s.subscribedTillDate is not null and s.subscribedTillDate<=:date and s.status in (:statuses)"),
        @NamedQuery(name = "ServiceInstance.getToNotifyExpiration", query = "select s.id from ServiceInstance s where s.subscription.status in (:subscriptionStatuses) AND s.subscribedTillDate is not null and s.renewalNotifiedDate is null and s.notifyOfRenewalDate is not null and s.notifyOfRenewalDate<=:date and :date < s.subscribedTillDate and s.status in (:statuses)") })
public class ServiceInstance extends BusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Subscription that service is subscribed under. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /** Service template/definition. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    private ServiceTemplate serviceTemplate;

    /**
     * Calendar to use when creating Wallet operations. Service subscription start date is taken as calendar's initiation date. Invoicing calendar to calculate if operation should
     * be invoiced on an future date.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicing_calendar_id")
    private Calendar invoicingCalendar;

    /** Status. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstanceStatusEnum status;

    /** Last status change timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

    /** Subscription timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate;

    /** Termination timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    /** End agreement timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_agrement_date")
    private Date endAgreementDate;

    /** If true, end of agreement date will be extended automatically till subscribedTillDate field. */
    @Type(type = "numeric_boolean")
    @Column(name = "auto_end_of_engagement")
    private Boolean autoEndOfEngagement = Boolean.FALSE;

    /** Associated recurring charges. */
    @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecurringChargeInstance> recurringChargeInstances = new ArrayList<>();

    /** Associated subscription charges. */
    @OneToMany(mappedBy = "subscriptionServiceInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OneShotChargeInstance> subscriptionChargeInstances = new ArrayList<>();

    /** Associated termination charges. */
    @OneToMany(mappedBy = "terminationServiceInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OneShotChargeInstance> terminationChargeInstances = new ArrayList<>();

    /** Associated usage charges. */
    @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UsageChargeInstance> usageChargeInstances = new ArrayList<>();

    /** Termination reason. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason subscriptionTerminationReason;

    /** Quantity subscribed. */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity = BigDecimal.ONE;

    /**
     * Used to track if "quantity" field value has changed. Value is populated on postLoad, postPersist and postUpdate JPA events
     */
    @Transient
    private BigDecimal previousQuantity;

    /** Order number if service was subscribed as part of an order. */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Create Wallet operations and Rated transactions upon service subscription only to this date. Later RT jobs needs to be run to create the remaining Wallet operations and
     * Rated transactions.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rate_until_date")
    private Date rateUntilDate;

    /** Expression to determine minimum amount value. */
    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    /** Expression to determine rated transaction description to reach minimum amount value. */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;

    /** Expression to determine minimum amount value - for Spark. */
    @Column(name = "minimum_amount_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumAmountElSpark;

    /** Expression to determine rated transaction description to reach minimum amount value - for Spark. */
    @Column(name = "minimum_label_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumLabelElSpark;
    
    /** The order histories. */
    @OneToMany(mappedBy = "serviceInstance", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHistory> orderHistories;

    /** Service renewal configuration. */
    @Embedded
    private SubscriptionRenewal serviceRenewal = new SubscriptionRenewal();

    /**
     * Amount from Payment schedule overridden for this service instance. If null, the amount PS will be taken from the Payment schedule template.
     */
    @Column(name = "amount_ps")
    private BigDecimal amountPS;

    /**
     * Calendar from Payment schedule overridden for this service instance. If null, the calendar PS will be taken from the Payment schedule template.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_ps_id")
    private Calendar calendarPS;

    /** The list of Payment schedule instances linked to this Service instance. */
    @OneToMany(mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<PaymentScheduleInstance> psInstances;

    /**
     * Payment day in month from Payment schedule overridden for this service instance. If null, the Payment day in month PS will be taken from the Payment schedule template.
     */
    @Column(name = "payment_day_in_month_ps")
    private Integer paymentDayInMonthPS;

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscribed_till_date")
    private Date subscribedTillDate;

    /** Was subscription renewed. */
    @Type(type = "numeric_boolean")
    @Column(name = "renewed")
    private boolean renewed;

    /**
     * A date on which "endOfTerm" notification should be fired for soon to expire subscriptions. It is calculated as subscribedTillDate-subscriptionRenewal.daysNotifyRenewal
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "notify_of_renewal_date")
    private Date notifyOfRenewalDate;

    /** Was/when "endOfTerm" notification fired for soon to expire subscription. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "renewal_notified_date")
    private Date renewalNotifiedDate;

    /**
     * PK of OrderItem.id.
     */
    @Transient
    private Long orderItemId;

    /** Order item action. */
    @Transient
    private OrderItemActionEnum orderItemAction;

    /**
     * Gets the end agreement date.
     *
     * @return the end agreement date
     */
    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    /**
     * Sets the end agreement date.
     *
     * @param endAgreementDate the new end agreement date
     */
    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
    }

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public Subscription getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public InstanceStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(InstanceStatusEnum status) {
        this.status = status;
        this.statusDate = new Date();
    }

    /**
     * Gets the status date.
     *
     * @return the status date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the new status date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
        if (serviceTemplate != null) {
            this.autoEndOfEngagement = serviceTemplate.getAutoEndOfEngagement();
        }
    }

    /**
     * Gets the invoicing calendar.
     *
     * @return the invoicing calendar
     */
    public Calendar getInvoicingCalendar() {
        return invoicingCalendar;
    }

    /**
     * Sets the invoicing calendar.
     *
     * @param invoicingCalendar the new invoicing calendar
     */
    public void setInvoicingCalendar(Calendar invoicingCalendar) {
        this.invoicingCalendar = invoicingCalendar;
    }

    /**
     * Gets the recurring charge instances.
     *
     * @return the recurring charge instances
     */
    public List<RecurringChargeInstance> getRecurringChargeInstances() {
        return recurringChargeInstances;
    }

    /**
     * Sets the recurring charge instances.
     *
     * @param recurringChargeInstances the new recurring charge instances
     */
    public void setRecurringChargeInstances(List<RecurringChargeInstance> recurringChargeInstances) {
        this.recurringChargeInstances = recurringChargeInstances;
    }

    /**
     * Gets the subscription charge instances.
     *
     * @return the subscription charge instances
     */
    public List<OneShotChargeInstance> getSubscriptionChargeInstances() {
        return subscriptionChargeInstances;
    }

    /**
     * Sets the subscription charge instances.
     *
     * @param subscriptionChargeInstances the new subscription charge instances
     */
    public void setSubscriptionChargeInstances(List<OneShotChargeInstance> subscriptionChargeInstances) {
        this.subscriptionChargeInstances = subscriptionChargeInstances;
    }

    /**
     * Gets the termination charge instances.
     *
     * @return the termination charge instances
     */
    public List<OneShotChargeInstance> getTerminationChargeInstances() {
        return terminationChargeInstances;
    }

    /**
     * Sets the termination charge instances.
     *
     * @param terminationChargeInstances the new termination charge instances
     */
    public void setTerminationChargeInstances(List<OneShotChargeInstance> terminationChargeInstances) {
        this.terminationChargeInstances = terminationChargeInstances;
    }

    /**
     * Gets the usage charge instances.
     *
     * @return the usage charge instances
     */
    public List<UsageChargeInstance> getUsageChargeInstances() {
        return usageChargeInstances;
    }

    /**
     * Sets the usage charge instances.
     *
     * @param usageChargeInstances the new usage charge instances
     */
    public void setUsageChargeInstances(List<UsageChargeInstance> usageChargeInstances) {
        this.usageChargeInstances = usageChargeInstances;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the subscription termination reason.
     *
     * @return the subscription termination reason
     */
    public SubscriptionTerminationReason getSubscriptionTerminationReason() {
        return subscriptionTerminationReason;
    }

    /**
     * Sets the subscription termination reason.
     *
     * @param subscriptionTerminationReason the new subscription termination reason
     */
    public void setSubscriptionTerminationReason(SubscriptionTerminationReason subscriptionTerminationReason) {
        this.subscriptionTerminationReason = subscriptionTerminationReason;
    }

    /**
     * Gets the description and status.
     *
     * @return the description and status
     */
    public String getDescriptionAndStatus() {
        if (!StringUtils.isBlank(description))
            return description + ", " + status;
        else
            return status.name();
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the rate until date.
     *
     * @return the rate until date
     */
    public Date getRateUntilDate() {
        return rateUntilDate;
    }

    /**
     * Sets the rate until date.
     *
     * @param rateUntilDate the new rate until date
     */
    public void setRateUntilDate(Date rateUntilDate) {
        this.rateUntilDate = rateUntilDate;
    }

    /* (non-Javadoc)
     * @see org.meveo.model.BusinessEntity#equals(java.lang.Object)
     */
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

    /* (non-Javadoc)
     * @see org.meveo.model.BusinessCFEntity#getParentCFEntities()
     */
    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { serviceTemplate };
    }

    /**
     * Track previous values.
     */
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

    /**
     * Gets the previous quantity.
     *
     * @return the previous quantity
     */
    public BigDecimal getPreviousQuantity() {
        return previousQuantity;
    }

    /**
     * Gets the order item id.
     *
     * @return the order item id
     */
    public Long getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the order item id.
     *
     * @param orderItemId the new order item id
     */
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    /**
     * Gets the order item action.
     *
     * @return the order item action
     */
    public OrderItemActionEnum getOrderItemAction() {
        return orderItemAction;
    }

    /**
     * Sets the order item action.
     *
     * @param orderItemAction the new order item action
     */
    public void setOrderItemAction(OrderItemActionEnum orderItemAction) {
        this.orderItemAction = orderItemAction;
    }

    /**
     * Gets the order histories.
     *
     * @return the order histories
     */
    public List<OrderHistory> getOrderHistories() {
        return orderHistories;
    }

    /**
     * Sets the order histories.
     *
     * @param orderHistories the new order histories
     */
    public void setOrderHistories(List<OrderHistory> orderHistories) {
        this.orderHistories = orderHistories;
    }

    /**
     * Gets the minimum amount el.
     *
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * Sets the minimum amount el.
     *
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * Gets the minimum label el.
     *
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * Sets the minimum label el.
     *
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * Gets the minimum amount el spark.
     *
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    /**
     * Sets the minimum amount el spark.
     *
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    /**
     * Gets the minimum label el spark.
     *
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * Sets the minimum label el spark.
     *
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
    }

    /**
     * Checks if is renewed.
     *
     * @return true, if is renewed
     */
    public boolean isRenewed() {
        return renewed;
    }

    /**
     * Sets the renewed.
     *
     * @param renewed the new renewed
     */
    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    /**
     * Gets the notify of renewal date.
     *
     * @return the notify of renewal date
     */
    public Date getNotifyOfRenewalDate() {
        return notifyOfRenewalDate;
    }

    /**
     * Sets the notify of renewal date.
     *
     * @param notifyOfRenewalDate the new notify of renewal date
     */
    public void setNotifyOfRenewalDate(Date notifyOfRenewalDate) {
        this.notifyOfRenewalDate = notifyOfRenewalDate;
    }

    /**
     * Gets the renewal notified date.
     *
     * @return the renewal notified date
     */
    public Date getRenewalNotifiedDate() {
        return renewalNotifiedDate;
    }

    /**
     * Sets the renewal notified date.
     *
     * @param renewalNotifiedDate the new renewal notified date
     */
    public void setRenewalNotifiedDate(Date renewalNotifiedDate) {
        this.renewalNotifiedDate = renewalNotifiedDate;
    }

    /**
     * Gets the subscribed till date.
     *
     * @return the subscribed till date
     */
    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    /**
     * Sets the subscribed till date.
     *
     * @param subscribedTillDate the new subscribed till date
     */
    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    /**
     * Check if service has expired for a current date.
     *
     * @return True if service has expired for a current date
     */
    public boolean isSubscriptionExpired() {
        return subscribedTillDate != null && DateUtils.setTimeToZero(subscribedTillDate).compareTo(DateUtils.setTimeToZero(new Date())) <= 0;
    }

    /**
     * Check if renewal notice should be fired for a current date.
     *
     * @return True if today is within "subscription expiration date - days before to notify renewal" and subscription expiration date
     */
    public boolean isFireRenewalNotice() {
        if (notifyOfRenewalDate != null && renewalNotifiedDate == null) {
            return DateUtils.isTodayWithinPeriod(notifyOfRenewalDate, subscribedTillDate);
        }

        return false;
    }

    /**
     * Auto update end of engagement date.
     */
    public void autoUpdateEndOfEngagementDate() {
        if (BooleanUtils.isTrue(this.autoEndOfEngagement)) {
            this.setEndAgreementDate(this.subscribedTillDate);
        }
    }

    /**
     * Update subscribedTillDate field in service while it was not renewed yet. Also calculate Notify of renewal date
     */
    public void updateSubscribedTillAndRenewalNotifyDates() {
        if (isRenewed()) {
            return;
        }

        if (getServiceRenewal().getInitialTermType().equals(SubscriptionRenewal.InitialTermTypeEnum.RECURRING)) {
            if (getSubscriptionDate() != null && getServiceRenewal() != null && getServiceRenewal().getInitialyActiveFor() != null) {
                if (getServiceRenewal().getInitialyActiveForUnit() == null) {
                    getServiceRenewal().setInitialyActiveForUnit(RenewalPeriodUnitEnum.MONTH);
                }
                java.util.Calendar calendar = new GregorianCalendar();
                calendar.setTime(getSubscriptionDate());
                calendar.add(getServiceRenewal().getInitialyActiveForUnit().getCalendarField(), getServiceRenewal().getInitialyActiveFor());
                setSubscribedTillDate(calendar.getTime());

            } else {
                setSubscribedTillDate(null);
            }
        }

        if (getSubscribedTillDate() != null && getServiceRenewal().isAutoRenew() && getServiceRenewal().getDaysNotifyRenewal() != null) {
            java.util.Calendar calendar = new GregorianCalendar();
            calendar.setTime(getSubscribedTillDate());
            calendar.add(java.util.Calendar.DAY_OF_MONTH, getServiceRenewal().getDaysNotifyRenewal() * (-1));
            setNotifyOfRenewalDate(calendar.getTime());
        } else {
            setNotifyOfRenewalDate(null);
        }
        this.autoUpdateEndOfEngagementDate();
    }

    /**
     * Gets the service renewal.
     *
     * @return the service renewal
     */
    public SubscriptionRenewal getServiceRenewal() {
        return serviceRenewal;
    }

    /**
     * Sets the service renewal.
     *
     * @param serviceRenewal the new service renewal
     */
    public void setServiceRenewal(SubscriptionRenewal serviceRenewal) {
        this.serviceRenewal = serviceRenewal;
    }

    /**
     * Gets the auto end of engagement.
     *
     * @return the autoEndOfEngagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * Sets the auto end of engagement.
     *
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }

    /**
     * Gets the amount PS.
     *
     * @return the amountPS
     */
    public BigDecimal getAmountPS() {
        return amountPS;
    }

    /**
     * Sets the amount PS.
     *
     * @param amountPS the amountPS to set
     */
    public void setAmountPS(BigDecimal amountPS) {
        this.amountPS = amountPS;
    }

    /**
     * Gets the calendar PS.
     *
     * @return the calendarPS
     */
    public Calendar getCalendarPS() {
        return calendarPS;
    }

    /**
     * Sets the calendar PS.
     *
     * @param calendarPS the calendarPS to set
     */
    public void setCalendarPS(Calendar calendarPS) {
        this.calendarPS = calendarPS;
    }

    /**
     * Gets the ps instances.
     *
     * @return the psInstances
     */
    public List<PaymentScheduleInstance> getPsInstances() {
        return psInstances;
    }

    /**
     * Sets the ps instances.
     *
     * @param psInstances the psInstances to set
     */
    public void setPsInstances(List<PaymentScheduleInstance> psInstances) {
        this.psInstances = psInstances;
    }


    /**
     * Gets the payment day in month PS.
     *
     * @return the payment day in month PS
     */
    public Integer getPaymentDayInMonthPS() {
        return paymentDayInMonthPS;
    }


    /**
     * Sets the payment day in month PS.
     *
     * @param paymentDayInMonthPS the new payment day in month PS
     */
    public void setPaymentDayInMonthPS(Integer paymentDayInMonthPS) {
        this.paymentDayInMonthPS = paymentDayInMonthPS;
    }

}