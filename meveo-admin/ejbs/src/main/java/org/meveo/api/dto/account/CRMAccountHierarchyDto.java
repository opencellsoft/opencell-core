package org.meveo.api.dto.account;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;


/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CRMAccountHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CRMAccountHierarchyDto extends BaseDto {

	private static final long serialVersionUID = -8382992060653977244L;

	public static final String ACCOUNT_TYPE_BASIC = "BASIC";
	public static final String ACCOUNT_TYPE_CORP = "CORP";
	public static final String ACCOUNT_TYPE_BRANCH = "BRANCH";

	private String crmAccountType;

	// shared
	private String crmParentCode;
	private String code;
	private String description;
	private String externalRef1;
	private String externalRef2;
	private NameDto name = new NameDto();
	private AddressDto address = new AddressDto();
	private ContactInformationDto contactInformation = new ContactInformationDto();

	private String language;
	private String paymentMethod;

	private String terminationReason;
	private Date subscriptionDate;
	private Date terminationDate;

	// customer
	private String customerCategory;
	private String customerBrand;
	@Deprecated
	/**
	 * We now use crmParentCode.
	 */
	private String seller;
	private String mandateIdentification = "";
	private Date mandateDate;

	// customer account
	private String currency;
	private String caStatus;
	private String creditCategory;
	private Date dateStatus;
	private Date dateDunningLevel;
	private String dunningLevel;

	// billing account
	private String billingCycle;
	private String country;
	private Date nextInvoiceDate;
	private String paymentTerms;
	private Boolean electronicBilling;
	private String baStatus;
	private String email;
	private BankCoordinatesDto bankCoordinates = new BankCoordinatesDto();
	
	// user account
	private String uaStatus;
	
	//Used for SMS_Facile integration
	private CustomFieldDto cfToAdd = new CustomFieldDto();
	private CustomFieldDto cfMapToAdd = new CustomFieldDto();
	
	private String offerCode;
	private String serviceCode;
	private String access;


	private CustomFieldsDto customFields = new CustomFieldsDto();

	public String getCrmAccountType() {
		return crmAccountType;
	}

	public void setCrmAccountType(String crmAccountType) {
		this.crmAccountType = crmAccountType;
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

	public NameDto getName() {
		return name;
	}

	public void setName(NameDto name) {
		this.name = name;
	}

	public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}

	public ContactInformationDto getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(ContactInformationDto contactInformation) {
		this.contactInformation = contactInformation;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getCustomerCategory() {
		return customerCategory;
	}

	public void setCustomerCategory(String customerCategory) {
		this.customerCategory = customerCategory;
	}

	public String getCustomerBrand() {
		return customerBrand;
	}

	public void setCustomerBrand(String customerBrand) {
		this.customerBrand = customerBrand;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getMandateIdentification() {
		return mandateIdentification;
	}

	public void setMandateIdentification(String mandateIdentification) {
		this.mandateIdentification = mandateIdentification;
	}

	public Date getMandateDate() {
		return mandateDate;
	}

	public void setMandateDate(Date mandateDate) {
		this.mandateDate = mandateDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCaStatus() {
		return caStatus;
	}

	public void setCaStatus(String caStatus) {
		this.caStatus = caStatus;
	}

	public String getCreditCategory() {
		return creditCategory;
	}

	public void setCreditCategory(String creditCategory) {
		this.creditCategory = creditCategory;
	}

	public Date getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	public Date getDateDunningLevel() {
		return dateDunningLevel;
	}

	public void setDateDunningLevel(Date dateDunningLevel) {
		this.dateDunningLevel = dateDunningLevel;
	}

	public String getDunningLevel() {
		return dunningLevel;
	}

	public void setDunningLevel(String dunningLevel) {
		this.dunningLevel = dunningLevel;
	}

	public String getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(String billingCycle) {
		this.billingCycle = billingCycle;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getNextInvoiceDate() {
		return nextInvoiceDate;
	}

	public void setNextInvoiceDate(Date nextInvoiceDate) {
		this.nextInvoiceDate = nextInvoiceDate;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public Boolean getElectronicBilling() {
		return electronicBilling;
	}

	public void setElectronicBilling(Boolean electronicBilling) {
		this.electronicBilling = electronicBilling;
	}

	public String getBaStatus() {
		return baStatus;
	}

	public void setBaStatus(String baStatus) {
		this.baStatus = baStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BankCoordinatesDto getBankCoordinates() {
		return bankCoordinates;
	}

	public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
		this.bankCoordinates = bankCoordinates;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CRMAccountHierarchyDto [crmAccountType=" + crmAccountType + ", crmParentCode=" + crmParentCode + ", code=" + code + ", description=" + description + ", externalRef1=" + externalRef1 + ", externalRef2=" + externalRef2 + ", name=" + name + ", address=" + address + ", contactInformation=" + contactInformation + ", language=" + language + ", paymentMethod=" + paymentMethod + ", terminationReason=" + terminationReason + ", subscriptionDate=" + subscriptionDate
				+ ", terminationDate=" + terminationDate + ", customerCategory=" + customerCategory + ", customerBrand=" + customerBrand + ", seller=" + seller + ", mandateIdentification=" + mandateIdentification + ", mandateDate=" + mandateDate + ", currency=" + currency + ", caStatus=" + caStatus + ", creditCategory=" + creditCategory + ", dateStatus=" + dateStatus + ", dateDunningLevel=" + dateDunningLevel + ", dunningLevel=" + dunningLevel + ", billingCycle="
				+ billingCycle + ", country=" + country + ", nextInvoiceDate=" + nextInvoiceDate + ", paymentTerms=" + paymentTerms + ", electronicBilling=" + electronicBilling + ", baStatus=" + baStatus + ", email=" + email + ", bankCoordinates=" + bankCoordinates + ", uaStatus=" + uaStatus + ",customFields=" + customFields + "]";
	}

	public String getUaStatus() {
		return uaStatus;
	}

	public void setUaStatus(String uaStatus) {
		this.uaStatus = uaStatus;
	}

	public String getCrmParentCode() {
		return crmParentCode;
	}

	public void setCrmParentCode(String crmParentCode) {
		this.crmParentCode = crmParentCode;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	/**
	 * @return the cfToAdd
	 */
	public CustomFieldDto getCfToAdd() {
		return cfToAdd;
	}

	/**
	 * @param cfToAdd the cfToAdd to set
	 */
	public void setCfToAdd(CustomFieldDto cfToAdd) {
		customFields.getCustomField().add(cfToAdd);
	}

	/**
	 * @return the offerCode
	 */
	public String getOfferCode() {
		return offerCode;
	}

	/**
	 * @param offerCode the offerCode to set
	 */
	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	/**
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * @param serviceCode the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * @return the access
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * @param access the access to set
	 */
	public void setAccess(String access) {
		this.access = access;
	}

	/**
	 * @return the cfMapToAdd
	 */
	public CustomFieldDto getCfMapToAdd() {
		return cfMapToAdd;
	}

	/**
	 * @param cfMapToAdd the cfMapToAdd to set
	 */
	public void setCfMapToAdd(CustomFieldDto cfMapToAdd) {		
		CustomFieldDto customFieldDto = customFields.getCF(cfMapToAdd.getCode());
		if(customFieldDto == null){
			customFieldDto = new CustomFieldDto();
			customFieldDto.setCode(cfMapToAdd.getCode());	
			customFields.getCustomField().add(customFieldDto);
		}
		if(customFieldDto.getMapValue() == null){
			customFieldDto.setMapValue(new HashMap<String,CustomFieldValueDto>());
		}
		if(cfMapToAdd.getDoubleValue() != null){
			CustomFieldValueDto cfValue = new CustomFieldValueDto(cfMapToAdd.getDoubleValue());
			customFieldDto.getMapValue().put(cfMapToAdd.getDescription(),cfValue);
		}
	}
	
}
