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

package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.GDPRInfoDto;
import org.meveo.api.dto.billing.DiscountPlanInstanceDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.ThresholdOptionsEnum;
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
    @XmlElement
    private Date nextInvoiceDate;

    /** The subscription date. */
    @XmlElement
    private Date subscriptionDate;

    /** The termination date. */
    @XmlElement
    private Date terminationDate;

    /** The electronic billing. */
    private Boolean electronicBilling;

    /** The status. */
    private AccountStatusEnum status;

    /** The status date. */
    @XmlElement
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
     * Expression to determine minimum amount value - for Spark
     */
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    private String minimumLabelElSpark;
    
    /**
     * Minimum Invoice SubCategory
     */
    private String minimumInvoiceSubCategory;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field on CustomerAccount entity instead.
     */
    @Deprecated
    private PaymentMethodEnum paymentMethodType;

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
     * Mailing type
     */
	private String mailingType;

    /**
     * Email Template code
     */
	private String emailTemplate;

    /**
     * a list of emails separated by comma
     */
	private String ccedEmails;

    /**
     * Account tax category code - overrides the value from a customer category
     **/
    private String taxCategoryCode;

    /**
     * The option on how to check the threshold.
     */
    private ThresholdOptionsEnum checkThreshold;
    
    /**
     * list of GDPR related to billing account
     */
    private List<GDPRInfoDto> infoGdpr;

    /**
     * Use to return the paymentMethod.
     */
    @XmlElement(name = "paymentMethod")
    private PaymentMethodDto paymentMethod;
    
    /**
     * 
     * check the threshold per entity/invoice for BA.
     */
    @XmlElement
    private Boolean thresholdPerEntity;

    public Boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(Boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}
	
	  /** The tags. */ 
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private Set<TagDto> tags = new HashSet<>();
    
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
        if (e.getInvoicingThreshold() != null) {
            setInvoicingThreshold(e.getInvoicingThreshold());
        }
        BillingCycle bc = e.getBillingCycle();
        if (bc != null) {
            setBillingCycle(bc.getCode());
            
            if (bc.getInvoicingThreshold() != null) {
                setInvoicingThreshold(bc.getInvoicingThreshold());
            }
        }
        if (e.getCheckThreshold() != null) {
            setCheckThreshold(e.getCheckThreshold());
            setThresholdPerEntity(e.isThresholdPerEntity());
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
        
        setMailingType(e.getMailingType() != null ? e.getMailingType().getLabel() : null);
        setEmailTemplate(e.getEmailTemplate() != null ? e.getEmailTemplate().getCode() : null);
        setCcedEmails(e.getCcedEmails());
        setRegistrationNo(e.getRegistrationNo());
        setVatNo(e.getVatNo());

        if (e.getTaxCategory() != null) {
            taxCategoryCode = e.getTaxCategory().getCode();
        }

        // Start compatibility with pre-4.6 versions
        PaymentMethod paymentMethod = e.getCustomerAccount().getPreferredPaymentMethod();
        if(Objects.nonNull(e.getPaymentMethod())){
            paymentMethod = e.getPaymentMethod();
        }
        if (paymentMethod != null) {
            setPaymentMethodType(paymentMethod.getPaymentType());
            if (paymentMethod instanceof DDPaymentMethod) {
                setBankCoordinates(new BankCoordinatesDto(((DDPaymentMethod) paymentMethod).getBankCoordinates()));
            }
        }
        
        if(e.getTags() != null && !e.getTags().isEmpty()) {
			tags = e.getTags().stream().map(t -> {
				final TagDto dto = new TagDto(t);
				return dto;
			}).collect(Collectors.toSet());
		}

        // End compatibility with pre-4.6 versions
    }
    
    public BillingAccountDto(BillingAccount e, List<GDPRInfoDto> billingAccountGDPR) {
    	this(e);
    	if(billingAccountGDPR != null && !billingAccountGDPR.isEmpty()) {
    		setInfoGdpr(billingAccountGDPR);
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
     * @return the payment method TYPE
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethodType the new payment method TYPE
     */

    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
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

    /**
     * Gets the mailing type
     * @return mailing type
     */
	public String getMailingType() {
        return mailingType;
    }

    /**
     * Sets the mailing type.
     * @param mailingType mailing type
     */
    public void setMailingType(String mailingType) {
        this.mailingType = mailingType;
    }

    /**
     * Gets the Email template code.
     * @return Email template code
     */
    public String getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * Sets Email template code.
     * @param emailTemplate
     */
    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * Gets cc emails.
     * @return cc emails
     */
    public String getCcedEmails() {
        return ccedEmails;
    }

    /**
     * Sets cc Emails.
     * @param ccedEmails
     */
    public void setCcedEmails(String ccedEmails) {
        this.ccedEmails = ccedEmails;
    }

    /**
     * @return the minimumInvoiceSubCategory
     */
    public String getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * @param minimumInvoiceSubCategory the minimumInvoiceSubCategory to set
     */
    public void setMinimumInvoiceSubCategory(String minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }


    /**
     * @return Account tax category code - overrides the value from a customer category
     */
    public String getTaxCategoryCode() {
        return taxCategoryCode;
    }

    /**
     * @param taxCategory Account tax category code - overrides the value from a customer category
     */
    public void setTaxCategoryCode(String taxCategoryCode) {
        this.taxCategoryCode = taxCategoryCode;
    }

    /**
     * Gets the threshold option.
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

    /**
     * Gets the payment Method.
     * @return the paymentMethod
     */
    public PaymentMethodDto getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the paymentMethod.
     * @param paymentMethod the payment Method
     */
    public void setPaymentMethod(PaymentMethodDto paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
	/**
	 * @return the infoGdpr
	 */
	public List<GDPRInfoDto> getInfoGdpr() {
		return infoGdpr;
	}

	/**
	 * @param infoGdpr the infoGdpr to set
	 */
	public void setInfoGdpr(List<GDPRInfoDto> infoGdpr) {
		this.infoGdpr = infoGdpr;
	}

	/**
	 * @return the tags
	 */
	public Set<TagDto> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<TagDto> tags) {
		this.tags = tags;
	}
 
	
	

}