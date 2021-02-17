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
package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.dwh.GdprConfiguration;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.shared.InterBankTitle;

/**
 * Application tenant configuration
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "Provider")
@ExportIdentifier("code")
@Table(name = "crm_provider", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "crm_provider_seq"), })
public class Provider extends AuditableEntity implements ICustomFieldEntity, ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * A hardcoded ID of a current provider/tenant. Each provider/tenant has its's own schema and all should have same ID for fast retrieval instead of ordering and taking a first
     * record
     */
    public static final long CURRENT_PROVIDER_ID = 1L;

    /**
     * Code
     */
    @Column(name = "code", nullable = false, length = 60)
    @Size(max = 60, min = 1)
    @NotNull
    protected String code;

    /**
     * Description
     */
    @Column(name = "description", length = 255)
    @Size(max = 255)
    protected String description;

    /**
     * Is it enabled. Deprecated in 5.3 for not use
     */
    @Deprecated
    @Type(type = "numeric_boolean")
    @Column(name = "disabled", nullable = false)
    @NotNull
    private boolean disabled;

    /**
     * Default currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    /**
     * Default country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    /**
     * Default language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    /**
     * Does application support multiple countries
     */
    @Type(type = "numeric_boolean")
    @Column(name = "multicountry_flag")
    private boolean multicountryFlag;

    /**
     * Does application support multiple currencies
     */
    @Type(type = "numeric_boolean")
    @Column(name = "multicurrency_flag")
    private boolean multicurrencyFlag;

    /**
     * Does application support multiple languages
     */
    @Type(type = "numeric_boolean")
    @Column(name = "multilanguage_flag")
    private boolean multilanguageFlag;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Payment methods allowed
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ElementCollection(targetClass = PaymentMethodEnum.class)
    @CollectionTable(name = "crm_provider_pay_methods", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private List<PaymentMethodEnum> paymentMethods = new ArrayList<PaymentMethodEnum>();

    /**
     * The Rating amount rounding
     */
    @Column(name = "rating_rounding", columnDefinition = "int DEFAULT 2", nullable = false)
    @NotNull
    private int rounding = 2;

    /**
     * The Rating amount rounding mode
     */
    @Column(name = "rounding_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoundingModeEnum roundingMode = RoundingModeEnum.NEAREST;

    /**
     * The invoice amount rounding
     */
    @Column(name = "invoice_rounding", columnDefinition = "int DEFAULT 2", nullable = false)
    @NotNull
    private int invoiceRounding = 2;

    /**
     * The invoice amount rounding mode
     */
    @Column(name = "invoice_rounding_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoundingModeEnum invoiceRoundingMode = RoundingModeEnum.NEAREST;

    /**
     * Bank coordinates
     */
    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();

    /**
     * Is application running in B2B or B2C mode. In B2B (enterprise=true) mode amounts without tax are used for rating and invoicing. In B2C mode amounts with tax are used.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "entreprise")
    private boolean entreprise = false;

    /**
     * In automatic invoicing invoice preInvoicing status is skipped and invoice is advanced to postInvoiced status.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "automatic_invoicing")
    private boolean automaticInvoicing = false;

    /**
     * Inter bank title
     */
    @Embedded
    private InterBankTitle interBankTitle = new InterBankTitle();

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Type(type = "numeric_boolean")
    @Column(name = "amount_validation")
    private boolean amountValidation = false;

    /**
     * With account level duplication, accounts will default to the name and other properties of the parent account.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "level_duplication")
    private boolean levelDuplication = false;

    /**
     * Contact email
     */
    @Column(name = "email", length = 100)
    @Pattern(regexp = ".+@.+\\..{2,4}")
    @Size(max = 100)
    protected String email;

    /**
     * Shall Rated transactions with Zero amount be displayed in an XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_free_tx_in_invoice")
    private boolean displayFreeTransacInInvoice = false;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * @deprecated As of version 5.0, replaced by {@link AccountingCode}
     */
    @Deprecated
    @Column(name = "discount_accounting_code", length = 255)
    @Size(max = 255)
    private String discountAccountingCode;

    /**
     * Default prepaid reservation delay in miliseconds
     */
    @Column(name = "prepaid_resrv_delay_ms")
    private Long prepaidReservationExpirationDelayinMillisec = 60000L;

    /**
     * Invoice configuration
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_config_id")
    private InvoiceConfiguration invoiceConfiguration;

    /**
     * Should revenue be recognized
     */
    @Type(type = "numeric_boolean")
    @Column(name = "recognize_revenue")
    private boolean recognizeRevenue;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Expired data delete configuration
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "gdpr_config_id")
    private GdprConfiguration gdprConfiguration;

    /**
     * RUM number sequence
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "prefix", column = @Column(name = "rum_prefix")), @AttributeOverride(name = "sequenceSize", column = @Column(name = "rum_sequence_size")),
            @AttributeOverride(name = "currentSequenceNb", column = @Column(name = "rum_current_sequence_nb")) })
    private GenericSequence rumSequence = new GenericSequence();

    /**
     * Customer number sequence
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "prefix", column = @Column(name = "cust_no_prefix")), @AttributeOverride(name = "sequenceSize", column = @Column(name = "cust_no_sequence_size")),
            @AttributeOverride(name = "currentSequenceNb", column = @Column(name = "cust_no_current_sequence_nb")) })
    private GenericSequence customerNoSequence = new GenericSequence();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isActive() {
        return !disabled;
    }

    public void setActive(boolean active) {
        setDisabled(!active);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public boolean getMulticountryFlag() {
        return multicountryFlag;
    }

    public void setMulticountryFlag(boolean multicountryFlag) {
        this.multicountryFlag = multicountryFlag;
    }

    public boolean getMulticurrencyFlag() {
        return multicurrencyFlag;
    }

    public void setMulticurrencyFlag(boolean multicurrencyFlag) {
        this.multicurrencyFlag = multicurrencyFlag;
    }

    public boolean getMultilanguageFlag() {
        return multilanguageFlag;
    }

    public void setMultilanguageFlag(boolean multilanguageFlag) {
        this.multilanguageFlag = multilanguageFlag;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setBankCoordinates(BankCoordinates bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    }

    public boolean isEntreprise() {
        return entreprise;
    }

    public void setEntreprise(boolean entreprise) {
        this.entreprise = entreprise;
    }

    public InterBankTitle getInterBankTitle() {
        return interBankTitle;
    }

    public void setInterBankTitle(InterBankTitle interBankTitle) {
        this.interBankTitle = interBankTitle;
    }

    /**
     * @return Rating amount rounding precision
     */
    public Integer getRounding() {
        return rounding;
    }

    /**
     * @param rounding Rating amount rounding precision
     */
    public void setRounding(Integer rounding) {
        this.rounding = rounding;
    }

    public boolean isAutomaticInvoicing() {
        return automaticInvoicing;
    }

    public void setAutomaticInvoicing(boolean automaticInvoicing) {
        this.automaticInvoicing = automaticInvoicing;
    }

    public boolean isAmountValidation() {
        return amountValidation;
    }

    public void setAmountValidation(boolean amountValidation) {
        this.amountValidation = amountValidation;
    }

    public boolean isLevelDuplication() {
        return levelDuplication;
    }

    public void setLevelDuplication(boolean levelDuplication) {
        this.levelDuplication = levelDuplication;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDisplayFreeTransacInInvoice() {
        return displayFreeTransacInInvoice;
    }

    public void setDisplayFreeTransacInInvoice(boolean displayFreeTransacInInvoice) {
        this.displayFreeTransacInInvoice = displayFreeTransacInInvoice;
    }

    public Long getPrepaidReservationExpirationDelayinMillisec() {
        return prepaidReservationExpirationDelayinMillisec;
    }

    public void setPrepaidReservationExpirationDelayinMillisec(Long prepaidReservationExpirationDelayinMillisec) {
        this.prepaidReservationExpirationDelayinMillisec = prepaidReservationExpirationDelayinMillisec;
    }

    public String getDiscountAccountingCode() {
        return discountAccountingCode;
    }

    public void setDiscountAccountingCode(String discountAccountingCode) {
        this.discountAccountingCode = discountAccountingCode;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Provider)) {
            return false;
        }

        Provider other = (Provider) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Provider [code=%s]", code);
    }

    public InvoiceConfiguration getInvoiceConfiguration() {
        return invoiceConfiguration;
    }

    /**
     * @return Invoice configuration or a default invoice configuration if one was not setup already
     */
    public InvoiceConfiguration getInvoiceConfigurationOrDefault() {
        return invoiceConfiguration != null ? invoiceConfiguration : new InvoiceConfiguration();
    }

    public void setInvoiceConfiguration(InvoiceConfiguration invoiceConfiguration) {
        this.invoiceConfiguration = invoiceConfiguration;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    public boolean isRecognizeRevenue() {
        return recognizeRevenue;
    }

    public void setRecognizeRevenue(boolean recognizeRevenue) {
        this.recognizeRevenue = recognizeRevenue;
    }

    /**
     * @return the paymentMethods
     */
    public List<PaymentMethodEnum> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * @param paymentMethods the paymentMethods to set
     */
    public void setPaymentMethods(List<PaymentMethodEnum> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @Override
    public String getUuid() {
        setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    /**
     * @param uuid Unique identifier
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    /**
     * @return Rating amount rounding mode
     */
    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    /**
     * @param roundingMode Rating amount rounding mode
     */
    public void setRoundingMode(RoundingModeEnum roundingMode) {
        this.roundingMode = roundingMode;
    }

    /**
     * @return Invoice and invoice aggregate amount rounding precision
     */
    public int getInvoiceRounding() {
        return invoiceRounding;
    }

    /**
     * @param invoiceRounding Invoice and invoice aggregate amount rounding precision
     */
    public void setInvoiceRounding(int invoiceRounding) {
        this.invoiceRounding = invoiceRounding;
    }

    /**
     * @return Invoice and invoice aggregate amount rounding mode
     */
    public RoundingModeEnum getInvoiceRoundingMode() {
        return invoiceRoundingMode;
    }

    /**
     * @param invoiceRoundingMode Invoice and invoice aggregate amount rounding mode
     */
    public void setInvoiceRoundingMode(RoundingModeEnum invoiceRoundingMode) {
        this.invoiceRoundingMode = invoiceRoundingMode;
    }

    public GdprConfiguration getGdprConfiguration() {
        return gdprConfiguration;
    }

    public void setGdprConfiguration(GdprConfiguration gdprConfiguration) {
        this.gdprConfiguration = gdprConfiguration;
    }

    public GdprConfiguration getGdprConfigurationNullSafe() {
        if (gdprConfiguration == null) {
            gdprConfiguration = new GdprConfiguration();
        }

        return gdprConfiguration;
    }

    public GenericSequence getRumSequence() {
        return rumSequence;
    }

    public void setRumSequence(GenericSequence rumSequence) {
        this.rumSequence = rumSequence;
    }

    public GenericSequence getCustomerNoSequence() {
        return customerNoSequence;
    }

    public void setCustomerNoSequence(GenericSequence customerNoSequence) {
        this.customerNoSequence = customerNoSequence;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    /**
     * Check if this is a main provider A hardcoded ID = 1 of a current provider/tenant. Each provider/tenant has its's own schema and all should have same ID for fast retrieval
     * instead of ordering and taking a first record
     * 
     * @return True if its a provider with ID=1
     */
    public boolean isCurrentProvider() {
        return id != null && id == CURRENT_PROVIDER_ID;
    }
}