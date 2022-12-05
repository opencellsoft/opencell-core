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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.crm.Customer;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class AccountHierarchyDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */

@XmlRootElement(name = "AccountHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountHierarchyDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8469973066490541924L;

    /** The email. */
    @XmlElement(required = true)
	@Schema(description = "email")
    private String email;

    /**
     * Replaced by customerCode. If customerId parameter is present then its value is use.
     */
    @Deprecated
    private String customerId;

    /** Customer Code. */
	@Schema(description = "code of the customer")
    private String customerCode;

    /** Seller Code. */
	@Schema(description = "code of the seller")
    private String sellerCode;

    /** SelCustomer Brand Code. */
	@Schema(description = "code of the customer brand")
    private String customerBrandCode;

    /** Custmork Code. */
	@Schema(description = "code of the customer category")
    private String customerCategoryCode;

    /** Currency Code. */
	@Schema(description = "code of the currency")
    private String currencyCode;

    /** SeCountry Cideller Code. */
	@Schema(description = "code of the customer country")
    private String countryCode;

    /** Language Code. */
	@Schema(description = "code of the language")
    private String languageCode;

    /** Title Code. */
	@Schema(description = "code of the title")
    private String titleCode;

    /** First Code. */
	@Schema(description = "first name of the account user")
    private String firstName;

    /** Last Name. */
	@Schema(description = "last name of the account user")
    private String lastName;

    /** Birth Date. */
	@Schema(description = "date of the birthday")
    private Date birthDate;

    /** Phone Number. */
	@Schema(description = "phone number")
    private String phoneNumber;

    /** Billing Cycle Code. */
	@Schema(description = "code of the billing cycle")
    private String billingCycleCode;

    /** Address 1. */
	@Schema(description = "the first adresse, to be used for account")
    private String address1;

    /** Address 2. */
	@Schema(description = "the second adresse, to be used for account")
    private String address2;

    /** Address 3. */
	@Schema(description = "the third adresse, to be used for account")
    private String address3;

    /** Zip Code. */
	@Schema(description = "zip code")
    private String zipCode;

    /** State. */
    private String state;

    /** City. */
	@Schema(description = "city of the user account")
    private String city;

    /** True if use prefix. */
    private Boolean usePrefix;

    /** Invoicing Threshold. */
    private BigDecimal invoicingThreshold;

    /**
     * Account tax category code - overrides the value from a customer category
     **/
	@Schema(description = "Account tax category code - overrides the value from a customer category")
    private String taxCategoryCode;

    /** List of discount plans. Use in instantiating {@link DiscountPlanInstance}. */
    @XmlElementWrapper(name = "discountPlansForInstantiation")
    @XmlElement(name = "discountPlanForInstantiation")
	@Schema(description = "List of discount plans")
    private List<DiscountPlanDto> discountPlansForInstantiation;

    /** List of discount plans to be disassociated in a BillingAccount */
    @XmlElementWrapper(name = "discountPlansForTermination")
    @XmlElement(name = "discountPlanForTermination")
	@Schema(description = "List of discount plans to be disassociated in a billing account")
    private List<String> discountPlansForTermination;

    /** Custom Fiends. */
	@Schema(description = "list of the custom field associated to account")
    private CustomFieldsDto customFields;

    /** The limit. */
    @XmlTransient
    private Integer limit;

    /** The sort field. */
    @XmlTransient
    private String sortField;

    /** The index. */
    @XmlTransient
    private Integer index;

    /** The payment methods. */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
	@Schema(description = "The payment methods")
    private List<PaymentMethodDto> paymentMethods;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead
     */
    @Deprecated
    private Integer paymentMethod;

    /**
     * Job title. Account Entity
     */
	@Schema(description = "Job title. Account Entity")
    private String jobTitle;

    /**
     * Registration number. CUST.
     */
	@Schema(description = "Registration number")
    private String registrationNo;

    /**
     * The option on how to check the threshold.
     */
    @XmlElement
	@Schema(description = "The option on how to check the threshold", example = "possible value are : BEFORE_DISCOUNT, AFTER_DISCOUNT, POSITIVE_RT, POSITIVE_IL")
    private ThresholdOptionsEnum checkThreshold;

    /**
     * The option on how to check the threshold for customer Account.
     */
    @XmlElement
	@Schema(description = "The option on how to check the threshold for customer Account", example = "possible value are : BEFORE_DISCOUNT, AFTER_DISCOUNT, POSITIVE_RT, POSITIVE_IL")
    private ThresholdOptionsEnum customerAccountCheckThreshold;

    /**
     * The option on how to check the threshold for customer.
     */
    @XmlElement
	@Schema(description = "The option on how to check the threshold for customer", example = "possible value are : BEFORE_DISCOUNT, AFTER_DISCOUNT, POSITIVE_RT, POSITIVE_IL")
    private ThresholdOptionsEnum customerCheckThreshold;

    /**
     * VAT. CUST.
     */
    private String vatNo;
    /**
     * The mailing Type.
     */
    @Schema(description = "The mailing Type")
    private String mailingType;
    /**
     * Email template.
     */
    @Schema(description = "Email template")
    private String emailTemplate;
    /**
     * CC Emails.
     */
    @Schema(description = "cc Emails")
    private String ccedEmails;

    /**
     * An object to store minimumAmount data for each account.
     */
    @Schema(description = "store minimumAmount data for each account")
    private MinimumAmountElDto minimumAmountEl;

    /** The invoicing threshold for the customer . */
    @Schema(description = "The invoicing threshold for the customer")
    private BigDecimal customerInvoicingThreshold;

    /** The invoicing threshold for the customer account. */
    @Schema(description = "The invoicing threshold for the customer account")
    private BigDecimal customerAccountInvoicingThreshold;
    
    /**
     * 
     * check the threshold per entity/invoice for BA.
     */
    @XmlElement
    @Schema(description = "check the threshold per entity/invoice for BA")
    private Boolean thresholdPerEntity;

    public Boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(Boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

    /**
     * 
     * check the threshold per entity/invoice for customerAccount.
     */
    @XmlElement
    private Boolean customerAccountThresholdPerEntity;

    public Boolean isCustomerAccountThresholdPerEntity() {
		return customerAccountThresholdPerEntity;
	}

	public void setCustomerAccountThresholdPerEntity(Boolean customerAccountThresholdPerEntity) {
		this.customerAccountThresholdPerEntity = customerAccountThresholdPerEntity;
	}

    /**
     * 
     * check the threshold per entity/invoice for customer.
     */
    @XmlElement
    private Boolean customerThresholdPerEntity;

    /** General client account code **/
    @Schema(description = "General client account code")
    private String generalClientAccountCode;

    public Boolean isCustomerThresholdPerEntity() {
		return customerThresholdPerEntity;
	}

	public void setCustomerThresholdPerEntity(Boolean customerThresholdPerEntity) {
		this.customerThresholdPerEntity = thresholdPerEntity;
	}

    /**
     * Instantiates a new account hierarchy dto.
     */
    public AccountHierarchyDto() {

    }

    /**
     * Instantiates a new account hierarchy dto.
     *
     * @param customer the customer
     * @param customFieldInstances the custom field instances
     */
    public AccountHierarchyDto(Customer customer, CustomFieldsDto customFieldInstances) {
        this.setCustomerId(customer.getCode());
        if (customer.getContactInformation() != null) {
            this.setEmail(customer.getContactInformation().getEmail());
            this.setPhoneNumber(customer.getContactInformation().getPhone());
        }
        if (customer.getSeller() != null) {
            this.sellerCode = customer.getSeller().getCode();
        }

        if (customer.getAddress() != null) {
            this.setAddress1(customer.getAddress().getAddress1());
            this.setAddress2(customer.getAddress().getAddress2());
            this.setAddress3(customer.getAddress().getAddress3());
            this.setState(customer.getAddress().getState());
            this.setZipCode(customer.getAddress().getZipCode());
            this.setCountryCode(customer.getAddress().getCountry() == null ? null : customer.getAddress().getCountry().getCountryCode());
            this.setCity(customer.getAddress().getCity());
        }

        if (customer.getName() != null) {
            if (customer.getName().getTitle() != null) {
                this.setTitleCode(customer.getName().getTitle().getCode());
            }
            this.setLastName(customer.getName().getLastName());
            this.setFirstName(customer.getName().getFirstName());
        }

        customFields = customFieldInstances;
    }

    /**
     * Gets the customer id.
     *
     * @return the customer id
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer id.
     *
     * @param customerId the new customer id
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the seller code.
     *
     * @return the seller code
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * Sets the seller code.
     *
     * @param sellerCode the new seller code
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * Gets the customer brand code.
     *
     * @return the customer brand code
     */
    public String getCustomerBrandCode() {
        return customerBrandCode;
    }

    /**
     * Sets the customer brand code.
     *
     * @param customerBrandCode the new customer brand code
     */
    public void setCustomerBrandCode(String customerBrandCode) {
        this.customerBrandCode = customerBrandCode;
    }

    /**
     * Gets the customer category code.
     *
     * @return the customer category code
     */
    public String getCustomerCategoryCode() {
        return customerCategoryCode;
    }

    /**
     * Sets the customer category code.
     *
     * @param customerCategoryCode the new customer category code
     */
    public void setCustomerCategoryCode(String customerCategoryCode) {
        this.customerCategoryCode = customerCategoryCode;
    }

    /**
     * Gets the country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the new country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the currency code.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode the new currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the language code.
     *
     * @return the language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the new language code
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Gets the billing cycle code.
     *
     * @return the billing cycle code
     */
    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    /**
     * Sets the billing cycle code.
     *
     * @param billingCycleCode the new billing cycle code
     */
    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

    /**
     * Gets the email.
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
     * Gets the zip code.
     *
     * @return the zip code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the zip code.
     *
     * @param zipCode the new zip code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the address 1.
     *
     * @return the address 1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Sets the address 1.
     *
     * @param address1 the new address 1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Gets the address 2.
     *
     * @return the address 2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the address 2.
     *
     * @param address2 the new address 2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the birth date.
     *
     * @return the birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date.
     *
     * @param birthDate the new birth date
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the new city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the title code.
     *
     * @return the title code
     */
    public String getTitleCode() {
        return titleCode;
    }

    /**
     * Sets the title code.
     *
     * @param titleCode the new title code
     */
    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Gets the sort field.
     *
     * @return the sort field
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * Sets the sort field.
     *
     * @param sortField the new sort field
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * Gets the index.
     *
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param index the new index
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the address 3.
     *
     * @return the address 3
     */
    public String getAddress3() {
        return address3;
    }

    /**
     * Sets the address 3.
     *
     * @param address3 the new address 3
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the customer code.
     *
     * @return the customer code
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * Sets the customer code.
     *
     * @param customerCode the new customer code
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * Gets the use prefix.
     *
     * @return the usePrefix
     */
    public Boolean getUsePrefix() {
        return usePrefix;
    }

    /**
     * Sets the use prefix.
     *
     * @param usePrefix the usePrefix to set
     */
    public void setUsePrefix(Boolean usePrefix) {
        this.usePrefix = usePrefix;
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
     * Gets the payment methods.
     *
     * @return the payment methods
     */
    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * Sets the payment methods.
     *
     * @param paymentMethods the new payment methods
     */
    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the job title.
     *
     * @return the job title
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the job title.
     *
     * @param jobTitle the new job title
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Gets the registration no.
     *
     * @return the registration no
     */
    public String getRegistrationNo() {
        return registrationNo;
    }

    /**
     * Sets the registration no.
     *
     * @param registrationNo the new registration no
     */
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    /**
     * Gets the vat no.
     *
     * @return the vat no
     */
    public String getVatNo() {
        return vatNo;
    }

    /**
     * Sets the vat no.
     *
     * @param vatNo the new vat no
     */
    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    @Override
    public String toString() {
        return "AccountHierarchyDto [email=" + email + ", customerId=" + customerId + ", customerCode=" + customerCode + ", sellerCode=" + sellerCode + ", customerBrandCode=" + customerBrandCode
                + ", customerCategoryCode=" + customerCategoryCode + ", currencyCode=" + currencyCode + ", countryCode=" + countryCode + ", languageCode=" + languageCode + ", titleCode=" + titleCode + ", firstName="
                + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate + ", phoneNumber=" + phoneNumber + ", billingCycleCode=" + billingCycleCode + ", address1=" + address1 + ", address2=" + address2
                + ", address3=" + address3 + ", zipCode=" + zipCode + ", state=" + state + ", city=" + city + ", usePrefix=" + usePrefix + ", invoicingThreshold=" + invoicingThreshold + ", discountPlansForInstantiation="
                + discountPlansForInstantiation + ", discountPlansForTermination=" + discountPlansForTermination + ", customFields=" + customFields + ", limit=" + limit + ", sortField=" + sortField + ", index=" + index
                + ", paymentMethods=" + paymentMethods + ", paymentMethod=" + paymentMethod + ", jobTitle=" + jobTitle + ", registrationNo=" + registrationNo + ", vatNo=" + vatNo + "]";
    }

    /**
     * Gets a list of discount plans for termination.
     * 
     * @return List of discount plan code.
     */
    public List<String> getDiscountPlansForTermination() {
        return discountPlansForTermination;
    }

    /**
     * Sets a list of discount plan code for termination.
     * 
     * @param discountPlansForTermination list of discount plan code
     */
    public void setDiscountPlansForTermination(List<String> discountPlansForTermination) {
        this.discountPlansForTermination = discountPlansForTermination;
    }

    /**
     * Gets a list of discount plan dto for instantiation.
     * 
     * @return list of discount plan dto
     */
    public List<DiscountPlanDto> getDiscountPlansForInstantiation() {
        return discountPlansForInstantiation;
    }

    /**
     * Sets a list of discount plan dto for instantiation.
     * 
     * @param discountPlansForInstantiation list of discount plan dto
     */
    public void setDiscountPlansForInstantiation(List<DiscountPlanDto> discountPlansForInstantiation) {
        this.discountPlansForInstantiation = discountPlansForInstantiation;
    }

    /**
     * @return mailingType
     */
    public String getMailingType() {
        return mailingType;
    }

    /**
     * @return emailTemplate
     */
    public String getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * @return ccedEmails
     */
    public String getCcedEmails() {
        return ccedEmails;
    }

    /**
     * @param mailingType
     */
    public void setMailingType(String mailingType) {
        this.mailingType = mailingType;
    }

    /**
     * @param emailTemplate
     */
    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * @param ccedEmails
     */
    public void setCcedEmails(String ccedEmails) {
        this.ccedEmails = ccedEmails;
    }

    /**
     * Gets the minimum amounts EL expression.
     *
     * @return the minimum amounts EL expression
     */
    public MinimumAmountElDto getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * Sets the minimum amounts EL expression.
     *
     * @param minimumAmountEl the minimum amounts EL expression.
     */
    public void setMinimumAmountEl(MinimumAmountElDto minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return the customer's invoicingThreshold
     */
    public BigDecimal getCustomerInvoicingThreshold() {
        return customerInvoicingThreshold;
    }

    /**
     * @param customerInvoicingThreshold the customer's invoicingThreshold to set
     */
    public void setCustomerInvoicingThreshold(BigDecimal customerInvoicingThreshold) {
        this.customerInvoicingThreshold = customerInvoicingThreshold;
    }

    /**
     * @return the customer account's invoicingThreshold
     */
    public BigDecimal getCustomerAccountInvoicingThreshold() {
        return customerAccountInvoicingThreshold;
    }

    /**
     * @param customerAccountInvoicingThreshold the customer account's invoicingThreshold to set
     */
    public void setCustomerAccountInvoicingThreshold(BigDecimal customerAccountInvoicingThreshold) {
        this.customerAccountInvoicingThreshold = customerAccountInvoicingThreshold;
    }

    /**
     * Gets the threshold option.
     * 
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     *
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

    /**
     * Gets the threshold option.
     *
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCustomerAccountCheckThreshold() {
        return customerAccountCheckThreshold;
    }

    /**
     * Sets the threshold option.
     *
     * @param customerAccountCheckThreshold the threshold option
     */
    public void setCustomerAccountCheckThreshold(ThresholdOptionsEnum customerAccountCheckThreshold) {
        this.customerAccountCheckThreshold = customerAccountCheckThreshold;
    }

    /**
     * Gets the threshold option.
     *
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCustomerCheckThreshold() {
        return customerCheckThreshold;
    }

    /**
     * Sets the threshold option.
     *
     * @param customerCheckThreshold the threshold option
     */
    public void setCustomerCheckThreshold(ThresholdOptionsEnum customerCheckThreshold) {
        this.customerCheckThreshold = customerCheckThreshold;
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

    public String getGeneralClientAccountCode() {
        return generalClientAccountCode;
    }

    public void setGeneralClientAccountCode(String generalClientAccountCode) {
        this.generalClientAccountCode = generalClientAccountCode;
    }
}