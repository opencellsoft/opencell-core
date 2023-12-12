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

import static javax.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Email;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IDiscountable;
import org.meveo.model.IWFEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditTarget;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.crm.IInvoicingMinimumApplicable;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;

/**
 * Subscription
 * 
 * @author Said Ramli
 * @author Mounir BAHIJE
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@WorkflowedEntity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "Subscription", inheritCFValuesFrom = { "offer", "userAccount" })
@ExportIdentifier({ "code" })
@Table(name = "billing_subscription", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "valid_from", "valid_to" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_subscription_seq"), })
@NamedQueries({
        @NamedQuery(name = "Subscription.getExpired", query = "select s.id from Subscription s where s.subscribedTillDate is not null and s.subscribedTillDate<=:date and s.status in (:statuses)"),
        @NamedQuery(name = "Subscription.getToNotifyExpiration", query = "select s.id from Subscription s where s.subscribedTillDate is not null and s.renewalNotifiedDate is null and s.notifyOfRenewalDate is not null and s.notifyOfRenewalDate<=:date and :date < s.subscribedTillDate and s.status in (:statuses)"),
        @NamedQuery(name = "Subscription.findByValidity", query = "select s from Subscription s where lower(s.code)=:code and (s.validity is null or ((s.validity.from is null or s.validity.from <= :validityDate) and  (s.validity.to is null or :validityDate < s.validity.to)))") ,
        @NamedQuery(name = "Subscription.getIdsByUsageChargeTemplate", query = "select ci.serviceInstance.subscription.id from UsageChargeInstance ci where ci.chargeTemplate=:chargeTemplate"),
        @NamedQuery(name = "Subscription.listByBillingRun", query = "select s from Subscription s where s.billingRun.id=:billingRunId order by s.id"),
        @NamedQuery(name = "Subscription.getMinimumAmountUsed", query = "select s.minimumAmountEl from Subscription s where s.minimumAmountEl is not null"),
        @NamedQuery(name = "Subscription.getSubscriptionsWithMinAmountBySubscription", query = "select s from Subscription s where s.minimumAmountEl is not null  AND s.status = org.meveo.model.billing.SubscriptionStatusEnum.ACTIVE AND s=:subscription"),
        @NamedQuery(name = "Subscription.getSubscriptionsWithMinAmountByBA", query = "select s from Subscription s where s.minimumAmountEl is not null AND s.status = org.meveo.model.billing.SubscriptionStatusEnum.ACTIVE AND s.userAccount.billingAccount=:billingAccount"),
        @NamedQuery(name = "Subscription.getSellersByBA", query = "select distinct s.seller from Subscription s where s.userAccount.billingAccount=:billingAccount"),
        @NamedQuery(name = "Subscription.unlinkPaymentMehtodByBA", query = "update Subscription s set s.paymentMethod = null where s.userAccount in (select u from UserAccount u where u.billingAccount=:billingAccount)"),
        @NamedQuery(name = "Subscription.listByCustomer", query = "select s from Subscription s inner join s.userAccount ua inner join ua.billingAccount ba inner join ba.customerAccount ca inner join ca.customer c where c=:customer order by s.code asc"),
        @NamedQuery(name = "Subscription.getCountByParent", query = "select count(*) from Subscription s where s.userAccount=:parent"),
        @NamedQuery(name = "Subscription.getSubscriptionIdsUsingProduct", query = "select si.subscription.id from ServiceInstance si where si.subscription.status not in ('CANCELED','RESILIATED','CLOSED') and si.productVersion.product in (select pc.product from ProductChargeTemplateMapping pc where pc.chargeTemplate.id in (:chargeIds))")})
public class Subscription extends BusinessCFEntity implements IInvoicingMinimumApplicable,IBillableEntity, IWFEntity, IDiscountable, ICounterEntity {

    private static final long serialVersionUID = 1L;

    /**
     * subscription version number
     */
    @Column(name = "version_number")
    protected Integer versionNumber;

    /**
     * reference to next version Subscription
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_version")
    protected Subscription nextVersion;

    /**
     * reference to previous version Subscription
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version")
    protected Subscription previousVersion;

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
    @AuditTarget(type = AuditChangeTypeEnum.STATUS, history = true, notif = true)
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
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("id")
    private List<ServiceInstance> serviceInstances = new ArrayList<>();

    /**
     * Products subscribed to
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("id")
    private List<ProductInstance> productInstances = new ArrayList<>();

    /**
     * Child access points
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("id")
    private List<Access> accessPoints = new ArrayList<>();

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<EDR> edrs = new ArrayList<>();

    /**
     * Parent User account
     */
    @AuditTarget(type = AuditChangeTypeEnum.OTHER, history = true, notif = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    @NotNull
    private UserAccount userAccount;

    /**
     * Account operations associated with a Subscription
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
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
    @AuditTarget(type = AuditChangeTypeEnum.RENEWAL, history = true, notif = true)
    private SubscriptionRenewal subscriptionRenewal = new SubscriptionRenewal();

    /**
     * Was subscription renewed
     */
    @AuditTarget(type = AuditChangeTypeEnum.RENEWAL, history = true, notif = true)
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
    
    /** Corresponding to minimum invoice subcategory */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_invoice_sub_category_id")
    private InvoiceSubCategory minimumInvoiceSubCategory;


    /** Corresponding to minimum one shot charge template */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_charge_template_id")
    private OneShotChargeTemplate minimumChargeTemplate;

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

    /**
     * Instance of discount plans.
     */
    @OneToMany(mappedBy = "subscription", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<DiscountPlanInstance> discountPlanInstances = new ArrayList<DiscountPlanInstance>();

    /**
     * Applicable discount plan. Replaced by discountPlanInstances. Now used only in GUI.
     */
    @Transient
    private DiscountPlan discountPlan;

    /**
     * List of dunning docs accociated with this subcription
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<DunningDocument> dunningDocuments;

    @ManyToOne(fetch = FetchType.LAZY)
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
     * The sales person name
     */
    @Column(name = "sales_person_name", length = 52)
    @Size(max = 52)
    private String salesPersonName;

    /**
     * Counter instances
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    Map<String, CounterInstance> counters = new HashMap<String, CounterInstance>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;
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

    /**
     * Initial subscription renewal configuration
     */
    @Type(type = "longText")
    @Column(name = "initial_renewal")
    private String initialSubscriptionRenewal;

    /**
     * Allowed payment methods
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commercial_order_id")
    private CommercialOrder order;

    @Column(name = "prestation")
    private String prestation;

    /**
     * Subscription validity
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity;

    /**
     * Corresponding to minimum invoice AccountingArticle
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "minimum_article_id")
    private AccountingArticle minimumArticle;

    @Transient
    private List<InvoiceLine> minInvoiceLines;
    
    /**
     * Commercial offer attached to the subscription
     */ 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_offer_id",referencedColumnName = "id")
    private OrderOffer orderOffer;
    
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = LAZY)
    private List<AttributeInstance> attributeInstances = new ArrayList<>();

    

    /**
     * Usage charge instances related to subscription
     */
    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @OrderBy("priority")
    @Where(clause = "charge_type='U'")
    private List<UsageChargeInstance> usageChargeInstances;

    /**
     * Default PriceList (Optional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;
    
    /**
     * This method is called implicitly by hibernate, used to enable
	 * encryption for custom fields of this entity
     */
    @PrePersist
	@PreUpdate
	public void preUpdate() {
		if (cfValues != null) {
			cfValues.setEncrypted(true);
		}
		if (cfAccumulatedValues != null) {
			cfAccumulatedValues.setEncrypted(true);
		}
	}

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Subscription getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Subscription nextVersion) {
        this.nextVersion = nextVersion;
    }

    public Subscription getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(Subscription previousVersion) {
        this.previousVersion = previousVersion;
    }

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
        if(this.order != null){
            orderNumber = this.order.getOrderNumber();
        }
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

    @Override
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
        if (this.status != SubscriptionStatusEnum.RESILIATED && !this.isToBeTerminatedWithFutureDate() && BooleanUtils.isTrue(this.autoEndOfEngagement)) {
            this.setEndAgreementDate(this.subscribedTillDate);
        }
    }

    private boolean isToBeTerminatedWithFutureDate() {
        return this.subscriptionRenewal.getTerminationReason() != null && !this.subscriptionRenewal.isAutoRenew() && this.subscribedTillDate != null
                && subscriptionRenewal.getEndOfTermAction() == EndOfTermActionEnum.TERMINATE;
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

    @Override
    public BillingRun getBillingRun() {
        return billingRun;
    }

    @Override
    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    @Override
    public void setMinRatedTransactions(List<RatedTransaction> ratedTransactions) {
        minRatedTransactions = ratedTransactions;
    }

    @Override
    public List<RatedTransaction> getMinRatedTransactions() {
        return minRatedTransactions;
    }

    @Override
    public BigDecimal getTotalInvoicingAmountWithoutTax() {
        return totalInvoicingAmountWithoutTax;
    }

    @Override
    public void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax) {
        this.totalInvoicingAmountWithoutTax = totalInvoicingAmountWithoutTax;
    }

    @Override
    public BigDecimal getTotalInvoicingAmountWithTax() {
        return totalInvoicingAmountWithTax;
    }

    @Override
    public void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax) {
        this.totalInvoicingAmountWithTax = totalInvoicingAmountWithTax;
    }

    @Override
    public BigDecimal getTotalInvoicingAmountTax() {
        return totalInvoicingAmountTax;
    }

    @Override
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
     *
     * @return Email Template.
     */
    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * Sets Email template.
     *
     * @param emailTemplate the Email template.
     */
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * Gets Mailing Type.
     *
     * @return Mailing Type.
     */
    public MailingTypeEnum getMailingType() {
        return mailingType;
    }

    /**
     * Sets Mailing Type.
     *
     * @param mailingType mailing type
     */
    public void setMailingType(MailingTypeEnum mailingType) {
        this.mailingType = mailingType;
    }

    /**
     * Gets cc Emails.
     *
     * @return cc Emails
     */
    public String getCcedEmails() {
        return ccedEmails;
    }

    /**
     * Sets cc Emails.
     *
     * @param ccedEmails Cc Emails
     */
    public void setCcedEmails(String ccedEmails) {
        this.ccedEmails = ccedEmails;
    }

    /**
     * Gets Email address.
     *
     * @return The Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets Email.
     *
     * @param email the Email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Check id electronic billing is enabled.
     *
     * @return True if enabled, false else
     */
    public boolean getElectronicBilling() {
        return electronicBilling;
    }

    /**
     * Sets the electronic billing.
     *
     * @param electronicBilling True or False
     */
    public void setElectronicBilling(boolean electronicBilling) {
        this.electronicBilling = electronicBilling;
    }
    
    /**
     * @return the salesPersonName
     */
    public String getSalesPersonName() {
    	return salesPersonName;
    }
    
    /**
     * @param salesPersonName the salesPersonName to set
     */
    public void setSalesPersonName(String salesPersonName) {
    	this.salesPersonName = salesPersonName;
    }

    public List<DiscountPlanInstance> getDiscountPlanInstances() {
        return discountPlanInstances;
    }

    @Override
    public List<DiscountPlanInstance> getAllDiscountPlanInstances() {
        return this.getDiscountPlanInstances();
    }

    @Override
    public void addDiscountPlanInstances(DiscountPlanInstance discountPlanInstance) {
        if (this.getDiscountPlanInstances() == null) {
            this.setDiscountPlanInstances(new ArrayList<>());
        }
        this.getDiscountPlanInstances().add(discountPlanInstance);
    }

    public void setDiscountPlanInstances(List<DiscountPlanInstance> discountPlanInstances) {
        this.discountPlanInstances = discountPlanInstances;
    }

    public DiscountPlan getDiscountPlan() {
        return discountPlan;
    }

    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
    }

    /**
     * Gets the initial subscription renewal
     *
     * @return the initial subscription renewal
     */
    public String getInitialSubscriptionRenewal() {
        return initialSubscriptionRenewal;
    }

    /**
     * Sets the initial subscription renewal.
     *
     * @param initialSubscriptionRenewal the new initial subscription renewal
     */
    public void setInitialSubscriptionRenewal(String initialSubscriptionRenewal) {
        this.initialSubscriptionRenewal = initialSubscriptionRenewal;
    }

    /**
     * Gets counters
     * 
     * @return a map of counters
     */
    @Override
    public Map<String, CounterInstance> getCounters() {
        return counters;
    }

    /**
     * Sets counters
     * 
     * @param counters a map of counters
     */
    public void setCounters(Map<String, CounterInstance> counters) {
        this.counters = counters;
    }

    /**
     * @return the minimumInvoiceSubCategory
     */
    public InvoiceSubCategory getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * @param minimumInvoiceSubCategory the minimumInvoiceSubCategory to set
     */
    public void setMinimumInvoiceSubCategory(InvoiceSubCategory minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }

    /**
     * Gets the charge template used in minimum amount.
     * @return a one Shot Charge template
     */
    public OneShotChargeTemplate getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    /**
     * Sets the minimum amount charge template.
     * @param minimumChargeTemplate a one Shot Charge template
     */
    public void setMinimumChargeTemplate(OneShotChargeTemplate minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }

    /**
     * Gets the subscription payment method
     * @return payment method a reference to an active PaymentMethod defined on the CustomerAccount
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the subscription payment method.
     * @param paymentMethod payment method a reference to an active PaymentMethod defined on the CustomerAccount
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public DatePeriod getValidity() {
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    public void setToValidity(Date validToDate) {
        if(getValidity() == null){
            DatePeriod datePeriod = new DatePeriod();
            datePeriod.setTo(validToDate);
            setValidity(datePeriod);
        }else{
            if(getValidity().getFrom() != null && validToDate != null && validToDate.before(getValidity().getFrom()))
                getValidity().setTo(getValidity().getFrom());
            else
                getValidity().setTo(validToDate);
        }
    }

    public void setFromValidity(Date validFromDate) {
        if(getValidity() == null){
            DatePeriod datePeriod = new DatePeriod();
            datePeriod.setFrom(validFromDate);
            setValidity(datePeriod);
        }else{
            getValidity().setFrom(validFromDate);
        }
    }

    public void addServiceInstance(ServiceInstance serviceInstance) {
		serviceInstances=serviceInstances!=null?serviceInstances:new ArrayList<ServiceInstance>();
		if(serviceInstance!=null) {
			serviceInstances.add(serviceInstance);
		}

	}

    public CommercialOrder getOrder() {
        return order;
    }

    public void setOrder(CommercialOrder order) {
        this.order = order;
    }

    public String getPrestation() {
        return prestation;
    }

    public AccountingArticle getMinimumArticle() {
        return minimumArticle;
    }

    public void setMinimumArticle(AccountingArticle minimumArticle) {
        this.minimumArticle = minimumArticle;
    }

    @Override
    public List<InvoiceLine> getMinInvoiceLines() {
        return minInvoiceLines;
    }

    @Override
    public void setMinInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.minInvoiceLines = invoiceLines;
    }

	/**
	 * @param prestation the prestation to set
	 */
	public void setPrestation(String prestation) {
		this.prestation = prestation;
	}

	public OrderOffer getOrderOffer() {
		return orderOffer;
	}

	public void setOrderOffer(OrderOffer orderOffer) {
		this.orderOffer = orderOffer;
	}
	
	
	
	public List<AttributeInstance> getAttributeInstances() {
		return attributeInstances;
	}

	public void setAttributeInstances(List<AttributeInstance> attributeInstances) {
		this.attributeInstances = attributeInstances;
	}

	public void addAttributeInstance(AttributeInstance attributeInstance) {
		attributeInstances=attributeInstances!=null?attributeInstances:new ArrayList<AttributeInstance>();
		if(attributeInstance!=null) {
			attributeInstances.add(attributeInstance);
		}

	}
	public Date getRenewalDate() {
		if(getSubscriptionDate()==null) {
			return null;
		}
		Calendar calendar = new GregorianCalendar();
        calendar.setTime(getSubscriptionDate());
		if(getSubscriptionRenewal()!=null) {
            calendar.add(getSubscriptionRenewal().getInitialyActiveForUnit().getCalendarField(), getSubscriptionRenewal().getInitialyActiveFor());
		}
		return calendar.getTime();
	}
	
	public int getSubscriptionDaysAge() {
		return getSubscriptionDaysAge(null);
	}
	
	public int getSubscriptionDaysAge(Date operationDate) {
		if(getSubscriptionDate()==null) {
			return 0;
		}
		if(operationDate==null) {
			operationDate=new Date();
		}
		return (int) DateUtils.daysBetween(getSubscriptionDate(),operationDate);
	}
	
	public int getSubscriptionMonthsAge() {
	    return calculateAge(ChronoUnit.MONTHS,null);
	}
	
	public int getSubscriptionMonthsAge(Date operationDate) {
	    return calculateAge(ChronoUnit.MONTHS,operationDate);
	}
	public int calculateAge(final ChronoUnit unit,Date operationDate) {
		if(getSubscriptionDate()==null) {
			return 0;
		}
		if(operationDate==null) {
			operationDate=new Date();
		}
	    YearMonth m1 = YearMonth.from(getSubscriptionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	    YearMonth m2 = YearMonth.from(operationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	    return Math.toIntExact(m1.until(m2, unit))+1; //+1 is added to include the last month
	}

	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * @param contract the contract to set
	 */
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	/**
	 * @return Usage charge instances related to subscription
	 */
	public List<UsageChargeInstance> getUsageChargeInstances() {
        return usageChargeInstances;
    }
	/**
	 * @param usageChargeInstances Usage charge instances related to subscription
	 */
	public void setUsageChargeInstances(List<UsageChargeInstance> usageChargeInstances) {
        this.usageChargeInstances = usageChargeInstances;
    }

    /**
     * PriceList Getter
     * @return the priceList
     */
    public PriceList getPriceList() {
        return priceList;
    }

    /**
     * PriceList Setter
     * @param priceList value to set
     */
    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }
}