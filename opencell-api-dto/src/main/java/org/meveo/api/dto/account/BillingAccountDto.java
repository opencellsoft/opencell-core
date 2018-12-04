package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.DiscountPlanInstanceDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.ContactInformation;

/**
 * The Class BillingAccountDto.
 * 
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 **/
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
// @FilterResults(propertyToFilter = "userAccounts.userAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = UserAccount.class) })
public class BillingAccountDto extends AccountDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8701417481481359155L;

    /** The customer account. */
    @XmlElement(required = true)
    private String customerAccount;

    /** The billing cycle. */
    @XmlElement(required = true)
    private String billingCycle;

    /** The country. */
    @XmlElement(required = true)
    private String country;

    /** The language. */
    @XmlElement(required = true)
    private String language;

    /** The next invoice date. */
    private Date nextInvoiceDate;

    /** The subscription date. */
    private Date subscriptionDate;

    /** The termination date. */
    private Date terminationDate;

    /** The electronic billing. */
    private Boolean electronicBilling;

    /** The status. */
    private AccountStatusEnum status;

    /** The status date. */
    private Date statusDate;

    /** The termination reason. */
    private String terminationReason;

    /** The email. */
    private String email;

    /** The invoices. */
    private List<InvoiceDto> invoices = new ArrayList<>();

    /** The invoicing threshold. */
    private BigDecimal invoicingThreshold;

    /** The phone. */
    protected String phone;

    /**
     * Expression to determine minimum amount value
     */
    private String minimumAmountEl;

    /**
     * Expression to determine minimum amount value - for Spark
     */
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    private String minimumLabelEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    private String minimumLabelElSpark;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field on CustomerAccount entity instead.
     */
    @Deprecated
    private PaymentMethodEnum paymentMethod;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field on CustomerAccount entity instead.
     */
    @Deprecated
    private BankCoordinatesDto bankCoordinates;

    /**
     * Field was deprecated in 4.6 version. Use custom fields instead.
     */
    @Deprecated
    private String paymentTerms;

    /**
     * Use for GET / LIST only.
     */
    private UserAccountsDto userAccounts = new UserAccountsDto();
    
    /** List of discount plans. Use in instantiating {@link DiscountPlanInstance}. */
	@XmlElementWrapper(name = "discountPlansForInstantiation")
	@XmlElement(name = "discountPlanForInstantiation")
    private List<DiscountPlanDto> discountPlansForInstantiation;
    
    /** List of discount plans to be disassociated in a BillingAccount */
	@XmlElementWrapper(name = "discountPlansForTermination")
	@XmlElement(name = "discountPlanForTermination")
    private List<String> discountPlansForTermination;
    
    /**
     * Use to return the active discount plans for this entity.
     */
	@XmlElementWrapper(name = "discountPlanInstances")
	@XmlElement(name = "discountPlanInstance")
    private List<DiscountPlanInstanceDto> discountPlanInstances;
    
    /**
     * Instantiates a new billing account dto.
     */
    public BillingAccountDto() {
        super();
    }

    /**
     * Instantiates a new billing account dto.
     * 
     * @param e BillingAccount entity
     */
    public BillingAccountDto(BillingAccount e) {
        super(e);

        if (e.getCustomerAccount() != null) {
            setCustomerAccount(e.getCustomerAccount().getCode());
        }
        BillingCycle bc = e.getBillingCycle();
        if (bc != null) {
            setBillingCycle(bc.getCode());
            setInvoicingThreshold(bc.getInvoicingThreshold());
        }
        if (e.getTradingCountry() != null) {
            setCountry(e.getTradingCountry().getCountryCode());
        }
        if (e.getTradingLanguage() != null) {
            setLanguage(e.getTradingLanguage().getLanguageCode());
        }
        setNextInvoiceDate(e.getNextInvoiceDate());
        setSubscriptionDate(e.getSubscriptionDate());
        setTerminationDate(e.getTerminationDate());
        setElectronicBilling(e.getElectronicBilling());
        setStatus(e.getStatus());
        setStatusDate(e.getStatusDate());
        setMinimumAmountEl(e.getMinimumAmountEl());
        setMinimumAmountElSpark(e.getMinimumAmountElSpark());
        setMinimumLabelEl(e.getMinimumLabelEl());
        setMinimumLabelElSpark(e.getMinimumLabelElSpark());
        if (e.getTerminationReason() != null) {
            setTerminationReason(e.getTerminationReason().getCode());
        }
        ContactInformation contactInfos = e.getContactInformation();
        if (contactInfos != null) {
            setPhone(contactInfos.getPhone());
            setEmail(contactInfos.getEmail());
        }

        // Start compatibility with pre-4.6 versions

        PaymentMethod paymentMethod = e.getCustomerAccount().getPreferredPaymentMethod();
        if (paymentMethod != null) {
            setPaymentMethod(paymentMethod.getPaymentType());
            if (paymentMethod instanceof DDPaymentMethod) {
                setBankCoordinates(new BankCoordinatesDto(((DDPaymentMethod) paymentMethod).getBankCoordinates()));
            }
        }

        // End compatibility with pre-4.6 versions
        
//        if(e.getDiscountPlans() != null) {
//			discountPlans = new ArrayList<>();
//			discountPlans = e.getDiscountPlans().stream().map(p -> p.getCode()).collect(Collectors.toList());
//		}
        
		if (e.getDiscountPlanInstances() != null && !e.getDiscountPlanInstances().isEmpty()) {
			discountPlanInstances = new ArrayList<>();
			discountPlanInstances = e.getDiscountPlanInstances().stream().map(p -> new DiscountPlanInstanceDto(p))
					.collect(Collectors.toList());
		}
    }
	
	public void addDiscountPlan(DiscountPlanDto dp) {
		if (discountPlansForInstantiation == null) {
			discountPlansForInstantiation = new ArrayList<>();
		}

		discountPlansForInstantiation.add(dp);
	}

    /**
     * Gets the customer account.
     *
     * @return the customer account
     */
    public String getCustomerAccount() {
        return customerAccount;
    }

    /**
     * Sets the customer account.
     *
     * @param customerAccount the new customer account
     */
    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    /**
     * Gets the billing cycle.
     *
     * @return the billing cycle
     */
    public String getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the new billing cycle
     */
    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the next invoice date.
     *
     * @return the next invoice date
     */
    public Date getNextInvoiceDate() {
        return nextInvoiceDate;
    }

    /**
     * Sets the next invoice date.
     *
     * @param nextInvoiceDate the new next invoice date
     */
    public void setNextInvoiceDate(Date nextInvoiceDate) {
        this.nextInvoiceDate = nextInvoiceDate;
    }

    /**
     * Gets the electronic billing.
     *
     * @return the electronic billing
     */
    public Boolean getElectronicBilling() {
        return electronicBilling;
    }

    /**
     * Sets the electronic billing.
     *
     * @param electronicBilling the new electronic billing
     */
    public void setElectronicBilling(Boolean electronicBilling) {
        this.electronicBilling = electronicBilling;
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
     * Gets the status.
     *
     * @return the status
     */
    public AccountStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(AccountStatusEnum status) {
        this.status = status;
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
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    /**
     * Gets the user accounts.
     *
     * @return the user accounts
     */
    public UserAccountsDto getUserAccounts() {
        return userAccounts;
    }

    /**
     * Sets the user accounts.
     *
     * @param userAccounts the new user accounts
     */
    public void setUserAccounts(UserAccountsDto userAccounts) {
        this.userAccounts = userAccounts;
    }

    /**
     * Gets the email. Get's priority over ContactInformation.email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the invoices.
     *
     * @return the invoices
     */
    public List<InvoiceDto> getInvoices() {
        return invoices;
    }

    /**
     * Sets the invoices.
     *
     * @param invoices the new invoices
     */
    public void setInvoices(List<InvoiceDto> invoices) {
        this.invoices = invoices;
    }

    /**
     * Gets the invoicing threshold.
     *
     * @return the invoicingThreshold
     */
    public BigDecimal getInvoicingThreshold() {
        return invoicingThreshold;
    }

    /**
     * Sets the invoicing threshold.
     *
     * @param invoicingThreshold the invoicingThreshold to set
     */
    public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the bank coordinates.
     *
     * @return the bank coordinates
     */
    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    /**
     * Sets the bank coordinates.
     *
     * @param bankCoordinates the new bank coordinates
     */
    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    /**
     * Gets the payment terms.
     *
     * @return the payment terms
     */
    public String getPaymentTerms() {
        return paymentTerms;
    }

    /**
     * Sets the payment terms.
     *
     * @param paymentTerms the new payment terms
     */
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    /**
     * Gets the phone. Gets priority over ContactInformation.phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone the new phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "BillingAccountDto [code=" + code + ", description=" + description + "]";
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
    
    /**
     * Gets the code of discount plans.
     * @return codes of discount plan
     */
    public List<DiscountPlanDto> getDiscountPlansForInstantiation() {
		return discountPlansForInstantiation;
	}

    /**
     * Sets the code of the discount plans.
     * @param discountPlansForInstantiation codes of the discount plans
     */
	public void setDiscountPlansForInstantiation(List<DiscountPlanDto> discountPlansForInstantiation) {
		this.discountPlansForInstantiation = discountPlansForInstantiation;
	}

	/**
	 * Gets the list of active discount plan instance.
	 * @return list of active discount plan instance
	 */
	public List<DiscountPlanInstanceDto> getDiscountPlanInstances() {
		return discountPlanInstances;
	}

	/**
	 * Sets the list of active discount plan instance.
	 * @param discountPlanInstances list of active discount plan instance
	 */
	public void setDiscountPlanInstances(List<DiscountPlanInstanceDto> discountPlanInstances) {
		this.discountPlanInstances = discountPlanInstances;
	}

	/**
	 * Gets the list of discount plan codes for termination.
	 * @return discount plan codes
	 */
	public List<String> getDiscountPlansForTermination() {
		return discountPlansForTermination;
	}

	/**
	 * Sets the list of discount plan codes for termination.
	 * @param discountPlansForTermination discount plan codes
	 */
	public void setDiscountPlansForTermination(List<String> discountPlansForTermination) {
		this.discountPlansForTermination = discountPlansForTermination;
	}
}