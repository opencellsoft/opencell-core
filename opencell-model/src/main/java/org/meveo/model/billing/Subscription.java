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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.shared.DateUtils;

/**
 * Subscription
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "SUB", inheritCFValuesFrom = { "offer", "userAccount" })
@ExportIdentifier({ "code" })
@Table(name = "billing_subscription", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_subscription_seq"), })
@NamedQueries({
        @NamedQuery(name = "Subscription.getExpired", query = "select s.id from Subscription s where s.subscribedTillDate is not null and s.subscribedTillDate<=:date and s.status in (:statuses)"),
        @NamedQuery(name = "Subscription.getToNotifyExpiration", query = "select s.id from Subscription s where s.subscribedTillDate is not null and s.renewalNotifiedDate is null and s.notifyOfRenewalDate is not null and s.notifyOfRenewalDate<=:date and :date < s.subscribedTillDate and s.status in (:statuses)"),
        @NamedQuery(name = "Subscription.getIdsByUsageChargeTemplate", query = "select ci.serviceInstance.subscription.id from UsageChargeInstance ci where ci.chargeTemplate=:chargeTemplate") })

public class Subscription extends BusinessCFEntity implements IBillableEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatusEnum status = SubscriptionStatusEnum.CREATED;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate = new Date();;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscribed_till_date")
    private Date subscribedTillDate;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("id")
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("id")
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ProductInstance> productInstances = new ArrayList<ProductInstance>();

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("id")
    private List<Access> accessPoints = new ArrayList<Access>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    @NotNull
    private UserAccount userAccount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_agrement_date")
    private Date endAgreementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason subscriptionTerminationReason;

    @Type(type = "numeric_boolean")
    @Column(name = "default_level")
    private Boolean defaultLevel = true;

    @Embedded
    private SubscriptionRenewal subscriptionRenewal = new SubscriptionRenewal();

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

    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_cycle")
    private BillingCycle billingCycle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run")
    private BillingRun billingRun;

    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
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

    public List<ProductInstance> getProductInstances() {
        return productInstances;
    }

    public void setProductInstances(List<ProductInstance> productInstances) {
        this.productInstances = productInstances;
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
        if (this.status != status) {
            this.statusDate = new Date();
        }
        this.status = status;
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

    public SubscriptionTerminationReason getSubscriptionTerminationReason() {
        return subscriptionTerminationReason;
    }

    public void setSubscriptionTerminationReason(SubscriptionTerminationReason subscriptionTerminationReason) {
        this.subscriptionTerminationReason = subscriptionTerminationReason;
    }

    public List<Access> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(List<Access> accessPoints) {
        this.accessPoints = accessPoints;
    }

    public Boolean getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Boolean defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { offer, userAccount };
    }

    /**
     * Check if subscription has any active instantiated services
     * 
     * @return True if any of instantiated services are active
     */
    public boolean isAnyServiceActive() {
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    /**
     * get orderNumber linked to subscription
     * 
     * @return orderNumber
     */
    public String getOrderNumber() {
        String orderNumber = null;
        if (serviceInstances != null && !serviceInstances.isEmpty()) {
            orderNumber = serviceInstances.get(0).getOrderNumber();
        }
        if (orderNumber == null) {
            if (productInstances != null && !productInstances.isEmpty()) {
                orderNumber = productInstances.get(0).getOrderNumber();
            }
        }
        return orderNumber;
    }

    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    public SubscriptionRenewal getSubscriptionRenewal() {
        return subscriptionRenewal;
    }

    public void setSubscriptionRenewal(SubscriptionRenewal subscriptionRenewal) {
        this.subscriptionRenewal = subscriptionRenewal;
    }

    public Date getRenewalNotifiedDate() {
        return renewalNotifiedDate;
    }

    public void setRenewalNotifiedDate(Date renewalNotifiedDate) {
        this.renewalNotifiedDate = renewalNotifiedDate;
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

    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
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
     * Check if subscription has expired for a current date
     * 
     * @return True if subscription has expired for a current date
     */
    public boolean isSubscriptionExpired() {

        return subscribedTillDate != null && DateUtils.setTimeToZero(subscribedTillDate).compareTo(DateUtils.setTimeToZero(new Date())) <= 0;
    }

    /**
     * Update subscribedTillDate field in subscription while it was not renewed yet. Also calculate Notify of renewal date
     */
    public void updateSubscribedTillAndRenewalNotifyDates() {
        if (isRenewed()) {
            return;
        }
		if (getSubscriptionRenewal().getInitialTermType().equals(SubscriptionRenewal.InitialTermTypeEnum.RECURRING)) {
        if (getSubscriptionDate() != null && getSubscriptionRenewal() != null && getSubscriptionRenewal().getInitialyActiveFor() != null) {
            if (getSubscriptionRenewal().getInitialyActiveForUnit() == null) {
                getSubscriptionRenewal().setInitialyActiveForUnit(RenewalPeriodUnitEnum.MONTH);
            }
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(getSubscriptionDate());
            calendar.add(getSubscriptionRenewal().getInitialyActiveForUnit().getCalendarField(), getSubscriptionRenewal().getInitialyActiveFor());
            setSubscribedTillDate(calendar.getTime());

        } else {
            setSubscribedTillDate(null);
        }
		}

        if (getSubscribedTillDate() != null && getSubscriptionRenewal().isAutoRenew() && getSubscriptionRenewal().getDaysNotifyRenewal() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(getSubscribedTillDate());
            calendar.add(Calendar.DAY_OF_MONTH, getSubscriptionRenewal().getDaysNotifyRenewal() * (-1));
            setNotifyOfRenewalDate(calendar.getTime());
        } else {
            setNotifyOfRenewalDate(null);
        }
	} 

    public void updateRenewalRule(SubscriptionRenewal newRenewalRule) {
        if (getSubscribedTillDate() != null && isRenewed()) {

        }

    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    /**
     * Is subscription active
     * 
     * @return True if Status is ACTIVE
     */
    public boolean isActive() {
        return SubscriptionStatusEnum.ACTIVE == status;
    }
}