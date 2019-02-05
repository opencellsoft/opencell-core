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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;

/**
 * Subscription
 * 
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 * @author Khalid HORRI
 * @lastModifiedVersion 7.0
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

    /**
     * Offer subscribed to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offer;

    /**
     * Subscription status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatusEnum status = SubscriptionStatusEnum.CREATED;

    /**
     * Last status change timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate = new Date();

    /**
     * Timestamp when subscribed
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate = new Date();

    /**
     * Subscription termination date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscribed_till_date")
    private Date subscribedTillDate;

    /**
     * Services subscribed to
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ServiceInstance> serviceInstances = new ArrayList<>();

    /**
     * Products subscribed to
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ProductInstance> productInstances = new ArrayList<>();

    /**
     * Child access points
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Access> accessPoints = new ArrayList<>();

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EDR> edrs = new ArrayList<>();

    /**
     * Parent User account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    @NotNull
    private UserAccount userAccount;

    /**
     * Account operations associated with a Subscription
     */
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations = new ArrayList<>();

    /**
     * End agreement date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_agrement_date")
    private Date endAgreementDate;

    /**
     * Subscription termination reason
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason subscriptionTerminationReason;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Type(type = "numeric_boolean")
    @Column(name = "default_level")
    private Boolean defaultLevel = true;

    /**
     * If true, end of agreement date will be extended automatically till subscribedTillDate field
     */
    @Type(type = "numeric_boolean")
    @Column(name = "auto_end_of_engagement")
    private Boolean autoEndOfEngagement = Boolean.FALSE;

    /**
     * Subscription renewal configuration
     */
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

    /**
     * Optional billing cycle for invoicing by subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_cycle")
    private BillingCycle billingCycle;

    /**
     * Last billing run that invoiced this subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run")
    private BillingRun billingRun;

    /**
     * Seller that offered products/services to subscribe to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    /**
     * String value matched in the usageRatingJob to group the EDRs for rating.
     */
    @Column(name = "rating_group", length = 50)
    private String ratingGroup;


    @ManyToOne()
    @JoinColumn(name = "email_template_id")
    private EmailTemplate emailTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "mailing_type")
    private MailingTypeEnum mailingType;

    @Column(name = "cced_emails", length = 2000)
    @Size(max = 2000)
    private String ccedEmails;

    @Column(name = "email", length = 255)
    @Email
    @Size(max = 255)
    private String email;

    @Type(type = "numeric_boolean")
    @Column(name = "electronic_billing")
    private boolean electronicBilling;


    /**
     * Extra Rated transactions to reach minimum invoice amount per subscription
     */
    @Transient
    private List<RatedTransaction> minRatedTransactions;

    /**
     * Total invoicing amount without tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithoutTax;

    /**
     * Total invoicing amount with tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithTax;

    /**
     * Total invoicing tax amount
     */
    @Transient
    private BigDecimal totalInvoicingAmountTax;

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

        ICustomFieldEntity[] parents = new ICustomFieldEntity[3];
        int count = 0;
        if (offer != null) {
            parents[count] = offer;
            count++;
        }
        if (seller != null) {
            parents[count] = seller;
            count++;
        }
        if (userAccount != null) {
            parents[count] = userAccount;
            count++;
        }
        if (count == 0) {
            return null;
        } else if (count == 3) {
            return parents;
        } else {
            return Arrays.copyOfRange(parents, 0, count);
        }
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
     * Auto update end of engagement date.
     */
    public void autoUpdateEndOfEngagementDate() {
        if (BooleanUtils.isTrue(this.autoEndOfEngagement)) {
            this.setEndAgreementDate(this.subscribedTillDate);
        }
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
        this.autoUpdateEndOfEngagementDate();
    }

    public void updateRenewalRule(SubscriptionRenewal newRenewalRule) {
        if (getSubscribedTillDate() != null && isRenewed()) {

        }

    }

    /**
     * create AutoRenewDate
     */
    public void createAutoRenewDate() {
        SubscriptionRenewal subscriptionRenewal = this.getSubscriptionRenewal();
        if (subscriptionRenewal != null) {
            subscriptionRenewal.setAutoRenewDate(new Date());
        }
    }

    /**
     * update AutoRenewDate when AutoRenew change
     *
     * @param subscriptionOld
     */
    public void updateAutoRenewDate(Subscription subscriptionOld) {
        SubscriptionRenewal subscriptionRenewalOld = subscriptionOld.getSubscriptionRenewal();
        SubscriptionRenewal subscriptionRenewalNew = this.getSubscriptionRenewal();
        boolean autoRenewOld = subscriptionRenewalOld.isAutoRenew();
        boolean autoRenewNew = subscriptionRenewalNew.isAutoRenew();
        if (autoRenewOld != autoRenewNew) {
            if (subscriptionRenewalNew != null) {
                subscriptionRenewalNew.setAutoRenewDate(new Date());
            }
        }
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public void setMinRatedTransactions(List<RatedTransaction> ratedTransactions) {
        minRatedTransactions = ratedTransactions;
    }

    public List<RatedTransaction> getMinRatedTransactions() {
        return minRatedTransactions;
    }

    public BigDecimal getTotalInvoicingAmountWithoutTax() {
        return totalInvoicingAmountWithoutTax;
    }

    public void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax) {
        this.totalInvoicingAmountWithoutTax = totalInvoicingAmountWithoutTax;
    }

    public BigDecimal getTotalInvoicingAmountWithTax() {
        return totalInvoicingAmountWithTax;
    }

    public void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax) {
        this.totalInvoicingAmountWithTax = totalInvoicingAmountWithTax;
    }

    public BigDecimal getTotalInvoicingAmountTax() {
        return totalInvoicingAmountTax;
    }

    public void setTotalInvoicingAmountTax(BigDecimal totalInvoicingAmountTax) {
        this.totalInvoicingAmountTax = totalInvoicingAmountTax;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * Is subscription active
     * 
     * @return True if Status is ACTIVE
     */
    public boolean isActive() {
        return SubscriptionStatusEnum.ACTIVE == status;
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

    public String getRatingGroup() {
        return ratingGroup;
    }

    public void setRatingGroup(String ratingGroup) {
        this.ratingGroup = ratingGroup;
    }

    public List<EDR> getEdrs() {
        return edrs;
    }

    public void setEdrs(List<EDR> edrs) {
        this.edrs = edrs;
    }

    public List<AccountOperation> getAccountOperations() {
        return accountOperations;
    }

    public void setAccountOperations(List<AccountOperation> accountOperations) {
        this.accountOperations = accountOperations;
    }
    /**
     * Gets Email Template.
     * @return Email Template.
     */
    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * Sets Email template.
     * @param emailTemplate the Email template.
     */
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * Gets Mailing Type.
     * @return Mailing Type.
     */
    public MailingTypeEnum getMailingType() {
        return mailingType;
    }

    /**
     * Sets Mailing Type.
     * @param mailingType mailing type
     */
    public void setMailingType(MailingTypeEnum mailingType) {
        this.mailingType = mailingType;
    }

    /**
     * Gets cc Emails.
     * @return cc Emails
     */
    public String getCcedEmails() {
        return ccedEmails;
    }

    /**
     * Sets cc Emails.
     * @param ccedEmails Cc Emails
     */
    public void setCcedEmails(String ccedEmails) {
        this.ccedEmails = ccedEmails;
    }

    /**
     * Gets Email address.
     * @return The Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets Email.
     * @param email the Email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Check id electronic billing is enabled.
     * @return True if enabled, false else
     */
    public boolean getElectronicBilling() {
        return electronicBilling;
    }

    /**
     * Sets the electronic billing.
     * @param electronicBilling True or False
     */
    public void setElectronicBilling(boolean electronicBilling) {
        this.electronicBilling = electronicBilling;
    }
}