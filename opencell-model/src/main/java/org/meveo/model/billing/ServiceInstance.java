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
import org.meveo.model.shared.DateUtils;

/**
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

    @Type(type = "numeric_boolean")
    @Column(name = "auto_end_of_engagement")
    private Boolean autoEndOfEngagement = Boolean.FALSE;

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

    /**
     * Expression to determine minimum amount value
     */
    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;

    /**
     * Expression to determine minimum amount value - for Spark
     */
    @Column(name = "minimum_amount_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    @Column(name = "minimum_label_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumLabelElSpark;
    @OneToMany(mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<OrderHistory> orderHistories;

    @Embedded
    private SubscriptionRenewal serviceRenewal = new SubscriptionRenewal();

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscribed_till_date")
    private Date subscribedTillDate;

    /**
     * Was subscription renewed
     */
    @Type(type = "numeric_boolean")
    @Column(name = "renewed")
    private boolean renewed;

    /**
     * A date on which "endOfTerm" notification should be fired for soon to expire subscriptions. It is calculated as subscribedTillDate-subscriptionRenewal.daysNotifyRenewal
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "notify_of_renewal_date")
    private Date notifyOfRenewalDate;

    /**
     * Was/when "endOfTerm" notification fired for soon to expire subscription
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "renewal_notified_date")
    private Date renewalNotifiedDate;

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
        if (serviceTemplate != null) {
           this.autoEndOfEngagement =  serviceTemplate.getAutoEndOfEngagement();
    }
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

    /**
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    /**
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
    }

    public boolean isRenewed() {
        return renewed;
    }

    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    public Date getNotifyOfRenewalDate() {
        return notifyOfRenewalDate;
    }

    public void setNotifyOfRenewalDate(Date notifyOfRenewalDate) {
        this.notifyOfRenewalDate = notifyOfRenewalDate;
    }

    public Date getRenewalNotifiedDate() {
        return renewalNotifiedDate;
    }

    public void setRenewalNotifiedDate(Date renewalNotifiedDate) {
        this.renewalNotifiedDate = renewalNotifiedDate;
    }

    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    /**
     * Check if service has expired for a current date
     * 
     * @return True if service has expired for a current date
     */
    public boolean isSubscriptionExpired() {
        return subscribedTillDate != null && DateUtils.setTimeToZero(subscribedTillDate).compareTo(DateUtils.setTimeToZero(new Date())) <= 0;
    }

    /**
     * Check if renewal notice should be fired for a current date
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

    public SubscriptionRenewal getServiceRenewal() {
        return serviceRenewal;
    }

    public void setServiceRenewal(SubscriptionRenewal serviceRenewal) {
        this.serviceRenewal = serviceRenewal;
    }

    /**
     * @return the autoEndOfEngagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }
}