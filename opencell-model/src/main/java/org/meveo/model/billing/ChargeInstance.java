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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.tax.TaxClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instantiated/subscribed charge
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.1.2
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "ChargeInstance", inheritCFValuesFrom = "chargeTemplate")
@Table(name = "billing_charge_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_charge_instance_seq"), })
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "charge_type", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({ @NamedQuery(name = "ChargeInstance.listPrepaid", query = "SELECT c FROM ChargeInstance c where c.prepaid=true and  c.status='ACTIVE'") })
public abstract class ChargeInstance extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Specifies that charge does not apply to any order
     */
    public static final String NO_ORDER_NUMBER = "none";

    /**
     * Charge type (class discriminator value)
     */
    @Column(name = "charge_type", insertable = false, updatable = false)
    @Size(max = 1)
    private String chargeType;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected InstanceStatusEnum status = InstanceStatusEnum.ACTIVE;

    /**
     * Last status change date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    protected Date statusDate;

    /**
     * Termination timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    protected Date terminationDate;

    /**
     * Charge template/definition that charge was instantiated from
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id")
    protected ChargeTemplate chargeTemplate;

    /**
     * Calendar to use when creating Wallet operations. Service subscription start date is taken as calendar's initiation date. Invoicing calendar to calculate if operation should
     * be invoiced on an future date.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicing_calendar_id")
    protected Calendar invoicingCalendar;

    /**
     * Charge instantiation date - one shot and usage charges<br>
     * or the last date charge applied on - recurring charges
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "charge_date")
    protected Date chargeDate;

    /**
     * Overridden amount without tax
     */
    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal amountWithoutTax;

    /**
     * Overridden amount with tax
     */
    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal amountWithTax;

    /**
     * Rating matching criteria 1
     */
    @Column(name = "criteria_1", length = 255)
    @Size(max = 255)
    protected String criteria1;

    /**
     * Rating matching criteria 2
     */
    @Column(name = "criteria_2", length = 255)
    @Size(max = 255)
    protected String criteria2;

    /**
     * Rating matching criteria 3
     */
    @Column(name = "criteria_3", length = 255)
    @Size(max = 255)
    protected String criteria3;

    /**
     * Wallet operations associated with a charge
     */
    @OneToMany(mappedBy = "chargeInstance", fetch = FetchType.LAZY)
    protected List<WalletOperation> walletOperations = new ArrayList<>();

    /**
     * Associated Seller
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = true)
    protected Seller seller;

    /**
     * Associated User account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    protected UserAccount userAccount;

    /**
     * Associated subscription. Might be null, for productCharges for instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    protected Subscription subscription;

    /**
     * Buyer's currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency")
    protected TradingCurrency currency;

    /**
     * Buyer's country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country")
    protected TradingCountry country;

    /**
     * Wallet instances
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_chrginst_wallet", joinColumns = @JoinColumn(name = "chrg_instance_id"), inverseJoinColumns = @JoinColumn(name = "wallet_instance_id"))
    @OrderColumn(name = "INDX")
    protected List<WalletInstance> walletInstances = new ArrayList<>();

    /**
     * Subscribed service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    /**
     * Prepaid wallet instances
     */
    @Transient
    private List<WalletInstance> prepaidWalletInstances;

    /**
     * Wallet operations sorted by date
     */
    @Transient
    protected List<WalletOperation> sortedWalletOperations;

    /**
     * Is this a prepaid charge. True if any of the wallet instances is of prepaid type.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_prepaid")
    protected Boolean prepaid = Boolean.FALSE;

    /**
     * Order number that instantiated the charge
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    protected String orderNumber;

    /**
     * Wallet instances
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_chrg_inst_counter", joinColumns = @JoinColumn(name = "chrg_instance_id"), inverseJoinColumns = @JoinColumn(name = "counter_instance_id"))
    @OrderColumn(name = "INDX")
    protected List<CounterInstance> counterInstances = new ArrayList<>();

    /**
     * Resolved taxClass
     */
    @Transient
    private TaxClass taxClassResolved;

    public ChargeInstance() {
    }

    public ChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, ChargeTemplate chargeTemplate, Subscription subscription, InstanceStatusEnum status) {

        this.code = chargeTemplate.getCode();

        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.userAccount = subscription.getUserAccount();
        this.subscription = subscription;
        this.seller = subscription.getSeller();
        this.country = userAccount.getBillingAccount().getTradingCountry();
        this.currency = userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency();
        this.chargeTemplate = chargeTemplate;
        this.status = status != null ? status : InstanceStatusEnum.ACTIVE;

        if (chargeTemplate.getDescriptionI18n() != null) {
            String languageCode = userAccount.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
            if (!StringUtils.isBlank(chargeTemplate.getDescriptionI18n().get(languageCode))) {
                this.description = chargeTemplate.getDescriptionI18n().get(languageCode);
            }
        }
        
        if (StringUtils.isBlank(this.description)) {
            this.description = chargeTemplate.getDescription();
        }
        
        this.setCfValues(chargeTemplate.getCfValues());
    }

    public ChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, ChargeTemplate chargeTemplate, ServiceInstance serviceInstance, InstanceStatusEnum status) {

        this(amountWithoutTax, amountWithTax, chargeTemplate, serviceInstance.getSubscription(), status);

        this.serviceInstance = serviceInstance;
        this.orderNumber = serviceInstance.getOrderNumber();
        this.invoicingCalendar = serviceInstance.getInvoicingCalendar();

        if (this.status == InstanceStatusEnum.ACTIVE) {
            this.chargeDate = serviceInstance.getSubscriptionDate();
        }
    }

    public String getCriteria1() {
        return criteria1;
    }

    public void setCriteria1(String criteria1) {
        this.criteria1 = criteria1;
    }

    public String getCriteria2() {
        return criteria2;
    }

    public void setCriteria2(String criteria2) {
        this.criteria2 = criteria2;
    }

    public String getCriteria3() {
        return criteria3;
    }

    public void setCriteria3(String criteria3) {
        this.criteria3 = criteria3;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
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

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public ChargeTemplate getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(ChargeTemplate chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
        if (chargeTemplate == null) {
            this.code = null;
            this.description = null;
        } else {
            this.code = chargeTemplate.getCode();
            this.description = chargeTemplate.getDescription();
        }
    }

    /**
     * @return Charge instantiation date - one shot and usage charges<br>
     *         or the last date charge applied on - recurring charges
     */
    public Date getChargeDate() {
        return chargeDate;
    }

    /**
     * @param chargeDate Charge instantiation date - one shot and usage charges<br>
     *        or the last date charge applied on - recurring charges
     */
    public void setChargeDate(Date chargeDate) {
        this.chargeDate = chargeDate;
    }

    public Calendar getInvoicingCalendar() {
        return invoicingCalendar;
    }

    public void setInvoicingCalendar(Calendar invoicingCalendar) {
        this.invoicingCalendar = invoicingCalendar;
    }

    public List<WalletOperation> getWalletOperations() {
        return walletOperations;
    }

    public void setWalletOperations(List<WalletOperation> walletOperations) {
        this.walletOperations = walletOperations;
    }

    public List<WalletOperation> getWalletOperationsSorted() {
        if (sortedWalletOperations == null) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.debug("getSortedWalletOperations");
            sortedWalletOperations = new ArrayList<WalletOperation>(getWalletOperations());

            Collections.sort(sortedWalletOperations, new Comparator<WalletOperation>() {
                @Override
                public int compare(WalletOperation c0, WalletOperation c1) {
                    return c1.getOperationDate().compareTo(c0.getOperationDate());
                }
            });
        }

        return sortedWalletOperations;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
        if (subscription.getUserAccount() != null) {
            this.setUserAccount(subscription.getUserAccount());
        }
    }

    public TradingCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(TradingCurrency currency) {
        this.currency = currency;
    }

    public TradingCountry getCountry() {
        return country;
    }

    public void setCountry(TradingCountry country) {
        this.country = country;
    }

    public List<WalletInstance> getWalletInstances() {
        return walletInstances;
    }

    public void setWalletInstances(List<WalletInstance> walletInstances) {
        this.walletInstances = walletInstances;
    }

    public Boolean getPrepaid() {
        return prepaid;
    }

    public void setPrepaid(Boolean prepaid) {
        this.prepaid = prepaid;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Get a list of prepaid wallet instances
     * 
     * @return A list of prepaid wallet instances associated to a charge
     */
    public List<WalletInstance> getPrepaidWalletInstances() {

        if (prepaidWalletInstances == null) {
            prepaidWalletInstances = new ArrayList<>();

            for (WalletInstance wallet : getWalletInstances()) {
                if (wallet.getWalletTemplate() != null && wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
                    prepaidWalletInstances.add(wallet);
                }
            }
        }
        return prepaidWalletInstances;
    }

    /**
     * @return Service instance that charge is associated to
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * @param serviceInstance Service instance that charge is associated to
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * @return Resolved tax class
     */
    public TaxClass getTaxClassResolved() {
        return taxClassResolved;
    }

    /**
     * @param taxClass Resolved tax class
     */
    public void setTaxClassResolved(TaxClass taxClass) {
        this.taxClassResolved = taxClass;
    }

    /**
     * Gets a counter instance.
     *
     * @return CounterInstance
     */
    public CounterInstance getCounter() {
        return null;
    }

    /**
     * Gets counter instances.
     *
     * @return counter instances
     */
    public List<CounterInstance> getCounterInstances() {
        return counterInstances;
    }

    /**
     * Sets counter instances.
     *
     * @param counterInstances counter instances
     */
    public void setCounterInstances(List<CounterInstance> counterInstances) {
        this.counterInstances = counterInstances;
    }

    /**
     * Add a counter instance.
     *
     * @param counterInstance the counter instance
     */
    public void addCounterInstance(CounterInstance counterInstance) {
        if (this.counterInstances == null) {
            this.counterInstances = new ArrayList<>();
        }
        this.counterInstances.add(counterInstance);
    }

    /**
     * Get a charge main type
     * 
     * @return Charge main type
     */
    public abstract ChargeMainTypeEnum getChargeMainType();

    /**
     * @return Charge type (class discriminator value)
     */
    public String getChargeType() {
        return chargeType;
    }

    /**
     * @param chargeType Charge type (class discriminator value)
     */
    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }
}