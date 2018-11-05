package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The Class CRMAccountHierarchyDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CRMAccountHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CRMAccountHierarchyDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8382992060653977244L;

    /** The Constant ACCOUNT_TYPE_BASIC. */
    public static final String ACCOUNT_TYPE_BASIC = "BASIC";
    
    /** The Constant ACCOUNT_TYPE_CORP. */
    public static final String ACCOUNT_TYPE_CORP = "CORP";
    
    /** The Constant ACCOUNT_TYPE_BRANCH. */
    public static final String ACCOUNT_TYPE_BRANCH = "BRANCH";

    /** The crm account type. */
    private String crmAccountType;

    /** The crm parent code. */
    // shared
    private String crmParentCode;
    
    /** The code. */
    private String code;
    
    /** The description. */
    private String description;
    
    /** The external ref 1. */
    private String externalRef1;
    
    /** The external ref 2. */
    private String externalRef2;
    
    /** The name. */
    private NameDto name;
    
    /** The address. */
    private AddressDto address;
    
    /** The contact information. */
    private ContactInformationDto contactInformation;
    
    /** The job title. */
    private String jobTitle;

    /** The language. */
    private String language;

    /** The termination reason. */
    private String terminationReason;
    
    /** The subscription date. */
    private Date subscriptionDate;
    
    /** The termination date. */
    private Date terminationDate;

    /** The customer category. */
    // customer
    private String customerCategory;
    
    /** The customer brand. */
    private String customerBrand;
    
    /** The registration no. */
    private String registrationNo;
    
    /** The vat no. */
    private String vatNo;
    
    /** The seller. */
    @Deprecated
    /**
     * We now use crmParentCode.
     */
    private String seller;
    
    /** The mandate identification. */
    private String mandateIdentification;
    
    /** The mandate date. */
    private Date mandateDate;

    /** The currency. */
    // customer account
    private String currency;
    
    /** The ca status. */
    private CustomerAccountStatusEnum caStatus;
    
    /** The credit category. */
    private String creditCategory;
    
    /** The date status. */
    private Date dateStatus;
    
    /** The date dunning level. */
    private Date dateDunningLevel;
    
    /** The dunning level. */
    private DunningLevelEnum dunningLevel;

    /** The payment methods. */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
    private List<PaymentMethodDto> paymentMethods;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead
     */
    @Deprecated
    private PaymentMethodEnum paymentMethod;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead
     */
    @Deprecated
    private BankCoordinatesDto bankCoordinates;

    /**
     * Field was deprecated in 4.6 version. Use custom fields instead
     */
    @Deprecated
    private String paymentTerms;

    /** The billing cycle. */
    // billing account
    private String billingCycle;
    
    /** The country. */
    private String country;
    
    /** The next invoice date. */
    private Date nextInvoiceDate;
    
    /** The electronic billing. */
    private Boolean electronicBilling;
    
    /** The ba status. */
    private AccountStatusEnum baStatus;
    
    /** The email. */
    private String email;
    
    /** The invoicing threshold. */
    private BigDecimal invoicingThreshold;
    
    /** The discount plan. */
    private String discountPlan;
    
    /** The list of discount plans. */
    private List<String> discountPlans;

    /** The ua status. */
    // user account
    private AccountStatusEnum uaStatus;

    /** The cf to add. */
    private CustomFieldDto cfToAdd = new CustomFieldDto();
    
    /** The cf map to add. */
    private CustomFieldDto cfMapToAdd = new CustomFieldDto();

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Gets the crm account type.
     *
     * @return the crm account type
     */
    public String getCrmAccountType() {
        return crmAccountType;
    }

    /**
     * Sets the crm account type.
     *
     * @param crmAccountType the new crm account type
     */
    public void setCrmAccountType(String crmAccountType) {
        this.crmAccountType = crmAccountType;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the external ref 1.
     *
     * @return the external ref 1
     */
    public String getExternalRef1() {
        return externalRef1;
    }

    /**
     * Sets the external ref 1.
     *
     * @param externalRef1 the new external ref 1
     */
    public void setExternalRef1(String externalRef1) {
        this.externalRef1 = externalRef1;
    }

    /**
     * Gets the external ref 2.
     *
     * @return the external ref 2
     */
    public String getExternalRef2() {
        return externalRef2;
    }

    /**
     * Sets the external ref 2.
     *
     * @param externalRef2 the new external ref 2
     */
    public void setExternalRef2(String externalRef2) {
        this.externalRef2 = externalRef2;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public NameDto getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(NameDto name) {
        this.name = name;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public AddressDto getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the new address
     */
    public void setAddress(AddressDto address) {
        this.address = address;
    }

    /**
     * Gets the contact information.
     *
     * @return the contact information
     */
    public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    /**
     * Sets the contact information.
     *
     * @param contactInformation the new contact information
     */
    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
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
     * Gets the customer category.
     *
     * @return the customer category
     */
    public String getCustomerCategory() {
        return customerCategory;
    }

    /**
     * Sets the customer category.
     *
     * @param customerCategory the new customer category
     */
    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    /**
     * Gets the customer brand.
     *
     * @return the customer brand
     */
    public String getCustomerBrand() {
        return customerBrand;
    }

    /**
     * Sets the customer brand.
     *
     * @param customerBrand the new customer brand
     */
    public void setCustomerBrand(String customerBrand) {
        this.customerBrand = customerBrand;
    }

    /**
     * Gets the seller.
     *
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller the new seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Gets the mandate identification.
     *
     * @return the mandate identification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * Sets the mandate identification.
     *
     * @param mandateIdentification the new mandate identification
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * Gets the mandate date.
     *
     * @return the mandate date
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * Sets the mandate date.
     *
     * @param mandateDate the new mandate date
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the ca status.
     *
     * @return the ca status
     */
    public CustomerAccountStatusEnum getCaStatus() {
        return caStatus;
    }

    /**
     * Sets the ca status.
     *
     * @param caStatus the new ca status
     */
    public void setCaStatus(CustomerAccountStatusEnum caStatus) {
        this.caStatus = caStatus;
    }

    /**
     * Gets the credit category.
     *
     * @return the credit category
     */
    public String getCreditCategory() {
        return creditCategory;
    }

    /**
     * Sets the credit category.
     *
     * @param creditCategory the new credit category
     */
    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    /**
     * Gets the date status.
     *
     * @return the date status
     */
    public Date getDateStatus() {
        return dateStatus;
    }

    /**
     * Sets the date status.
     *
     * @param dateStatus the new date status
     */
    public void setDateStatus(Date dateStatus) {
        this.dateStatus = dateStatus;
    }

    /**
     * Gets the date dunning level.
     *
     * @return the date dunning level
     */
    public Date getDateDunningLevel() {
        return dateDunningLevel;
    }

    /**
     * Sets the date dunning level.
     *
     * @param dateDunningLevel the new date dunning level
     */
    public void setDateDunningLevel(Date dateDunningLevel) {
        this.dateDunningLevel = dateDunningLevel;
    }

    /**
     * Gets the dunning level.
     *
     * @return the dunning level
     */
    public DunningLevelEnum getDunningLevel() {
        return dunningLevel;
    }

    /**
     * Sets the dunning level.
     *
     * @param dunningLevel the new dunning level
     */
    public void setDunningLevel(DunningLevelEnum dunningLevel) {
        this.dunningLevel = dunningLevel;
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
     * Gets the ba status.
     *
     * @return the ba status
     */
    public AccountStatusEnum getBaStatus() {
        return baStatus;
    }

    /**
     * Sets the ba status.
     *
     * @param baStatus the new ba status
     */
    public void setBaStatus(AccountStatusEnum baStatus) {
        this.baStatus = baStatus;
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
     * Gets the ua status.
     *
     * @return the ua status
     */
    public AccountStatusEnum getUaStatus() {
        return uaStatus;
    }

    /**
     * Sets the ua status.
     *
     * @param uaStatus the new ua status
     */
    public void setUaStatus(AccountStatusEnum uaStatus) {
        this.uaStatus = uaStatus;
    }

    /**
     * Gets the crm parent code.
     *
     * @return the crm parent code
     */
    public String getCrmParentCode() {
        return crmParentCode;
    }

    /**
     * Sets the crm parent code.
     *
     * @param crmParentCode the new crm parent code
     */
    public void setCrmParentCode(String crmParentCode) {
        this.crmParentCode = crmParentCode;
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
     * Gets the cf to add.
     *
     * @return the cfToAdd
     */
    public CustomFieldDto getCfToAdd() {
        return cfToAdd;
    }

    /**
     * Sets the cf to add.
     *
     * @param cfToAdd the cfToAdd to set
     */
    public void setCfToAdd(CustomFieldDto cfToAdd) {
        customFields.getCustomField().add(cfToAdd);
    }

    /**
     * Gets the cf map to add.
     *
     * @return the cfMapToAdd
     */
    public CustomFieldDto getCfMapToAdd() {
        return cfMapToAdd;
    }

    /**
     * Sets the cf map to add.
     *
     * @param cfMapToAdd the cfMapToAdd to set
     */
    public void setCfMapToAdd(CustomFieldDto cfMapToAdd) {
        CustomFieldDto customFieldDto = customFields.getCF(cfMapToAdd.getCode());
        if (customFieldDto == null) {
            customFieldDto = new CustomFieldDto();
            customFieldDto.setCode(cfMapToAdd.getCode());
            customFields.getCustomField().add(customFieldDto);
        }
        if (customFieldDto.getMapValue() == null) {
            customFieldDto.setMapValue(new LinkedHashMap<String, CustomFieldValueDto>());
        }
        if (cfMapToAdd.getDoubleValue() != null) {
            CustomFieldValueDto cfValue = new CustomFieldValueDto(cfMapToAdd.getDoubleValue());
            customFieldDto.getMapValue().put(cfMapToAdd.getCode(), cfValue);
        }
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
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
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
    
    @Override
    public String toString() {
        return "CRMAccountHierarchyDto [crmAccountType=" + crmAccountType + ", crmParentCode=" + crmParentCode + ", code=" + code + ", description=" + description
                + ", externalRef1=" + externalRef1 + ", externalRef2=" + externalRef2 + ", name=" + name + ", address=" + address + ", contactInformation=" + contactInformation
                + ", language=" + language + ", terminationReason=" + terminationReason + ", subscriptionDate=" + subscriptionDate + ", terminationDate=" + terminationDate
                + ", customerCategory=" + customerCategory + ", customerBrand=" + customerBrand + ", seller=" + seller + ", mandateIdentification=" + mandateIdentification
                + ", mandateDate=" + mandateDate + ", currency=" + currency + ", caStatus=" + caStatus + ", creditCategory=" + creditCategory + ", dateStatus=" + dateStatus
                + ", dateDunningLevel=" + dateDunningLevel + ", dunningLevel=" + dunningLevel + ", billingCycle=" + billingCycle + ", country=" + country + ", nextInvoiceDate="
                + nextInvoiceDate + ", electronicBilling=" + electronicBilling + ", baStatus=" + baStatus + ", email=" + email + ", uaStatus=" + uaStatus + ",customFields="
                + customFields + ", invoicingThreshold=" + invoicingThreshold + ", invoicingThreshold=" + invoicingThreshold + "]";
    }

	public List<String> getDiscountPlans() {
		return discountPlans;
	}

	public void setDiscountPlans(List<String> discountPlans) {
		this.discountPlans = discountPlans;
	}    
}