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
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/

@XmlRootElement(name = "AccountHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountHierarchyDto implements Serializable {

    private static final long serialVersionUID = -8469973066490541924L;

    @XmlElement(required = true)
    private String email;

    /**
     * Replaced by customerCode. If customerId parameter is present then its value is use.
     */
    @Deprecated
    private String customerId;

    /**
     * Customer Code
     */
    private String customerCode;

    /**
     * Seller Code
     */
    private String sellerCode;

    /**
     * SelCustomer Brand Code
     */
    private String customerBrandCode;

    /**
     * Custmork Code
     */
    private String customerCategoryCode;

    /**
     * Currency Code
     */
    private String currencyCode;

    /**
     * SeCountry Cideller Code
     */
    private String countryCode;

    /**
     * Language Code
     */
    private String languageCode;

    /**
     * Title Code
     */
    private String titleCode;

    /**
     * First Code
     */
    private String firstName;

    /**
     * Last Name
     */
    private String lastName;

    /**
     * Birth Date
     */
    private Date birthDate;

    /**
     * Phone Number
     */
    private String phoneNumber;

    /**
     * Billing Cycle Code
     */
    private String billingCycleCode;

    /**
     * Address 1
     */
    private String address1;

    /**
     * Address 2
     */
    private String address2;

    /**
     * Address 3
     */
    private String address3;

    /**
     * Zip Code
     */
    private String zipCode;

    /**
     * State
     */
    private String state;

    /**
     * City
     */
    private String city;

    /**
     * True if use prefix
     */
    private Boolean usePrefix;

    /**
     * Invoicing Threshold
     */
    private BigDecimal invoicingThreshold;

    /**
     * Discount Plan
     */
    private String discountPlan;

    /**
     * Custom Fiends
     */
    private CustomFieldsDto customFields;

    @XmlTransient
    private int limit;

    @XmlTransient
    private String sortField;

    @XmlTransient
    private int index;

    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name="methodOfPayment")
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

    public AccountHierarchyDto() {

    }

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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getCustomerBrandCode() {
        return customerBrandCode;
    }

    public void setCustomerBrandCode(String customerBrandCode) {
        this.customerBrandCode = customerBrandCode;
    }

    public String getCustomerCategoryCode() {
        return customerCategoryCode;
    }

    public void setCustomerCategoryCode(String customerCategoryCode) {
        this.customerCategoryCode = customerCategoryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * @return the usePrefix
     */
    public Boolean getUsePrefix() {
        return usePrefix;
    }

    /**
     * @param usePrefix the usePrefix to set
     */
    public void setUsePrefix(Boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    /**
     * @return the invoicingThreshold
     */
    public BigDecimal getInvoicingThreshold() {
        return invoicingThreshold;
    }

    /**
     * @param invoicingThreshold the invoicingThreshold to set
     */
    public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    public String getDiscountPlan() {
        return discountPlan;
    }

    public void setDiscountPlan(String discountPlan) {
        this.discountPlan = discountPlan;
    }

    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }
}