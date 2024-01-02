package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;

@XmlAccessorType(XmlAccessType.FIELD)
public class BillingAccount {
    @XmlAttribute
    private Long id;
    @XmlAttribute
    private String billingCycleCode;
    @XmlAttribute
    private String code;
    @XmlAttribute
    private String description;
    @XmlAttribute
    private String externalRef1;
    @XmlAttribute
    private String externalRef2;
    @XmlAttribute
    private String jobTitle;
    @XmlAttribute
    private String registrationNo;
    @XmlAttribute
    private String vatNo;
    private Name name;
    private Address address;
    private CustomFieldsDto customFields;
    private PaymentMethod paymentMethod;
    @XmlAttribute
    private String country;
    @XmlAttribute
    private String lang;
    

    public BillingAccount(org.meveo.model.billing.BillingAccount billingAccount) {
        this.billingCycleCode = billingAccount.getBillingCycle().getCode();
        this.id = billingAccount.getId();
        this.code = billingAccount.getCode();
        this.description = billingAccount.getDescription();
        this.externalRef1 = billingAccount.getExternalRef1();
        this.externalRef2 = billingAccount.getExternalRef2();
        this.jobTitle = billingAccount.getJobTitle();
        this.vatNo = billingAccount.getVatNo();
        this.name = new Name(billingAccount.getName());
        this.address = new Address(billingAccount.getAddress());
        this.country = billingAccount.getTradingCountry() != null ? billingAccount.getTradingCountry().getCode() : "";
        this.lang = billingAccount.getTradingLanguage() != null ? billingAccount.getTradingLanguage().getLanguageCode() : "";
    }
    
    public BillingAccount(org.meveo.model.billing.BillingAccount billingAccount, PaymentMethod paymentMethod, CustomFieldsDto customFieldsDto) {
    	this(billingAccount);
    	this.customFields = customFieldsDto;
    	this.paymentMethod=paymentMethod;
        this.country = billingAccount.getTradingCountry() != null ? billingAccount.getTradingCountry().getCode() : "";
        this.lang = billingAccount.getTradingLanguage() != null ? billingAccount.getTradingLanguage().getLanguageCode() : "";
    }

    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

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

    public String getExternalRef1() {
        return externalRef1;
    }

    public void setExternalRef1(String externalRef1) {
        this.externalRef1 = externalRef1;
    }

    public String getExternalRef2() {
        return externalRef2;
    }

    public void setExternalRef2(String externalRef2) {
        this.externalRef2 = externalRef2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }



	/**
	 * @return the paymentMethod
	 */
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	
	

}
