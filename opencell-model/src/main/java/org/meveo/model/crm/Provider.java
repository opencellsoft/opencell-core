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
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
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
import org.meveo.model.persistence.CustomFieldValuesConverter;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.shared.InterBankTitle;

/**
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "PROVIDER")
@ExportIdentifier("code")
@Table(name = "crm_provider", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_provider_seq"), })
@NamedQueries({ @NamedQuery(name = "Provider.first", query = "select p from Provider p order by id", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class Provider extends AuditableEntity implements ICustomFieldEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "code", nullable = false, length = 60)
    @Size(max = 60, min = 1)
    @NotNull
    protected String code;

    @Column(name = "description", length = 255)
    @Size(max = 255)
    protected String description;

    @Type(type = "numeric_boolean")
    @Column(name = "disabled", nullable = false)
    @NotNull
    private boolean disabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Type(type = "numeric_boolean")
    @Column(name = "multicountry_flag")
    private boolean multicountryFlag;

    @Type(type = "numeric_boolean")
    @Column(name = "multicurrency_flag")
    private boolean multicurrencyFlag;

    @Type(type = "numeric_boolean")
    @Column(name = "multilanguage_flag")
    private boolean multilanguageFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /** Payment methods allowed. */
    @ElementCollection(targetClass = PaymentMethodEnum.class)
    @CollectionTable(name = "crm_provider_pay_methods", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private List<PaymentMethodEnum> paymentMethods = new ArrayList<PaymentMethodEnum>();

    /** The Rating rounding. */
    @Column(name = "rating_rounding", columnDefinition = "int DEFAULT 2")
    private Integer rounding = 2;
    
    /** The Rating rounding mode*/
    @Column (name = "rounding_mode")
    @Enumerated(EnumType.STRING)
    private RoundingModeEnum roundingMode; 

    /** The invoice rounding. */
    @Column(name = "invoice_rounding", columnDefinition = "int DEFAULT 2")
    private Integer invoiceRounding = 2;
    
    /** The invoice rounding mode. */
    @Column (name = "invoice_rounding_mode")
    @Enumerated(EnumType.STRING)
    private RoundingModeEnum invoiceRoundingMode; 

    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();

    @Type(type = "numeric_boolean")
    @Column(name = "entreprise")
    private boolean entreprise = false;

    @Type(type = "numeric_boolean")
    @Column(name = "automatic_invoicing")
    private boolean automaticInvoicing = false;

    @Embedded
    private InterBankTitle interBankTitle = new InterBankTitle();

    @Type(type = "numeric_boolean")
    @Column(name = "amount_validation")
    private boolean amountValidation = false;

    @Type(type = "numeric_boolean")
    @Column(name = "level_duplication")
    private boolean levelDuplication = false;

    @Column(name = "email", length = 100)
    @Pattern(regexp = ".+@.+\\..{2,4}")
    @Size(max = 100)
    protected String email;

    @Type(type = "numeric_boolean")
    @Column(name = "display_free_tx_in_invoice")
    private boolean displayFreeTransacInInvoice = false;

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

    @Column(name = "prepaid_resrv_delay_ms")
    private Long prepaidReservationExpirationDelayinMillisec = Long.valueOf(60000);

    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, targetEntity = org.meveo.model.billing.InvoiceConfiguration.class, orphanRemoval = true)
    private InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration();

    @Type(type = "numeric_boolean")
    @Column(name = "recognize_revenue")
    private boolean recognizeRevenue;

    // @Type(type = "json")
    @Convert(converter = CustomFieldValuesConverter.class)
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;
    
    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private GdprConfiguration gdprConfiguration;
    
    @Embedded
	@AttributeOverrides({ //
			@AttributeOverride(name = "prefix", column = @Column(name = "rum_prefix")), //
			@AttributeOverride(name = "sequenceSize", column = @Column(name = "rum_sequence_size")), //
			@AttributeOverride(name = "currentSequenceNb", column = @Column(name = "rum_current_sequence_nb"))
	})
	private GenericSequence rumSequence = new GenericSequence();
    
    @Embedded
	@AttributeOverrides({ //
			@AttributeOverride(name = "prefix", column = @Column(name = "cust_no_prefix")), //
			@AttributeOverride(name = "sequenceSize", column = @Column(name = "cust_no_sequence_size")), //
			@AttributeOverride(name = "currentSequenceNb", column = @Column(name = "cust_no_current_sequence_nb"))
	})
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

    public Integer getRounding() {
        return rounding;
    }

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
     * @return the roundingMode
     */
    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    /**
     * @param roundingMode the roundingMode to set
     */
    public void setRoundingMode(RoundingModeEnum roundingMode) {
        this.roundingMode = roundingMode;
    }

    /**
     * @return the invoiceRounding
     */
    public Integer getInvoiceRounding() {
        if (this.invoiceRounding == null) {
            this.invoiceRounding = this.rounding;
        }
        return invoiceRounding;
    }

    /**
     * @return the invoiceRoundingMode
     */
    public RoundingModeEnum getInvoiceRoundingMode() {
        if (this.invoiceRoundingMode == null) {
            this.invoiceRoundingMode = this.roundingMode;
        }
        return invoiceRoundingMode;
    }

    /**
     * @param invoiceRounding the invoiceRounding to set
     */
    public void setInvoiceRounding(Integer invoiceRounding) {
        this.invoiceRounding = invoiceRounding;
    }

    /**
     * @param invoiceRoundingMode the invoiceRoundingMode to set
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
}