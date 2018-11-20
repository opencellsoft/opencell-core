package org.meveo.api.dto.account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.crm.Customer;

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
    private String email;

    /**
     * Replaced by customerCode. If customerId parameter is present then its value is use.
     */
    @Deprecated
    private String customerId;

    /** Customer Code. */
    private String customerCode;

    /** Seller Code. */
    private String sellerCode;

    /** SelCustomer Brand Code. */
    private String customerBrandCode;

    /** Custmork Code. */
    private String customerCategoryCode;

    /** Currency Code. */
    private String currencyCode;

    /** SeCountry Cideller Code. */
    private String countryCode;

    /** Language Code. */
    private String languageCode;

    /** Title Code. */
    private String titleCode;

    /** First Code. */
    private String firstName;

    /** Last Name. */
    private String lastName;

    /** Birth Date. */
    private Date birthDate;

    /** Phone Number. */
    private String phoneNumber;

    /** Billing Cycle Code. */
    private String billingCycleCode;

    /** Address 1. */
    private String address1;

    /** Address 2. */
    private String address2;

    /** Address 3. */
    private String address3;

    /** Zip Code. */
    private String zipCode;

    /** State. */
    private String state;

    /** City. */
    private String city;

    /** True if use prefix. */
    private Boolean usePrefix;

    /** Invoicing Threshold. */
    private BigDecimal invoicingThreshold;

    /** Discount Plan. */
    private String discountPlan;
    
    /** List of Discount Plans. */
    @XmlElementWrapper(name = "discountPlans")
	@XmlElement(name = "discountPlan")
    private List<String> discountPlans;
    
    /** List of discount plans to be disassociated in a BillingAccount */
	@XmlElementWrapper(name = "discountPlansForTermination")
	@XmlElement(name = "discountPlan")
    private List<String> discountPlansForTermination;

    /** Custom Fiends. */
    private CustomFieldsDto customFields;

    /** The limit. */
    @XmlTransient
    private int limit;

    /** The sort field. */
    @XmlTransient
    private String sortField;

    /** The index. */
    @XmlTransient
    private int index;

    /** The payment methods. */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
    private List<PaymentMethodDto> paymentMethods;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead
     */
    @Deprecated
    private Integer paymentMethod;

    /**
     * Job title. Account Entity
     */
    private String jobTitle;

    /**
     * Registration number. CUST.
     */
    private String registrationNo;

    /**
     * VAT. CUST.
     */
    private String vatNo;

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
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    public void setLimit(int limit) {
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
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param index the new index
     */
    public void setIndex(int index) {
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
     * Gets the discount plan.
     *
     * @return the discount plan
     */
    public String getDiscountPlan() {
        return discountPlan;
    }

    /**
     * Sets the discount plan.
     *
     * @param discountPlan the new discount plan
     */
    public void setDiscountPlan(String discountPlan) {
        this.discountPlan = discountPlan;
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
        return "AccountHierarchyDto [email=" + email + ", customerId=" + customerId + ", customerCode=" + customerCode + ", sellerCode=" + sellerCode + ", customerBrandCode="
                + customerBrandCode + ", customerCategoryCode=" + customerCategoryCode + ", currencyCode=" + currencyCode + ", countryCode=" + countryCode + ", languageCode="
                + languageCode + ", titleCode=" + titleCode + ", firstName=" + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate + ", phoneNumber=" + phoneNumber
                + ", billingCycleCode=" + billingCycleCode + ", address1=" + address1 + ", address2=" + address2 + ", address3=" + address3 + ", zipCode=" + zipCode + ", state="
                + state + ", city=" + city + ", customFields=" + customFields + ", limit=" + limit + ", sortField=" + sortField + ", index=" + index + ", invoicingThreshold="
                + invoicingThreshold + ", discountPlan=" + discountPlan + "]";
    }

	public List<String> getDiscountPlans() {
		return discountPlans;
	}

	public void setDiscountPlans(List<String> discountPlans) {
		this.discountPlans = discountPlans;
	}

	public List<String> getDiscountPlansForTermination() {
		return discountPlansForTermination;
	}

	public void setDiscountPlansForTermination(List<String> discountPlansForTermination) {
		this.discountPlansForTermination = discountPlansForTermination;
	}    
}