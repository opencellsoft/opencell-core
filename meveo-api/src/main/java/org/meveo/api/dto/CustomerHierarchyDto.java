package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomerHeirarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerHierarchyDto extends BaseDto {

	private static final long serialVersionUID = -8469973066490541924L;

	private String customerId;
	private String sellerCode;
	private String customerBrandCode;
	private String customerCategoryCode;
	private String currencyCode;
	private String countryCode;
	private String firstName;
	private String lastName;
	private String languageCode;
	private String billingCycleCode;
	private String email;
	private String zipCode;
	private String address1;
	private String address2;
	private Date birthDate;
	private String phoneNumber;
	private String city;
	private String titleCode;
	

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
		return "CustomerHeirarchyDto [customerId = " + customerId
				+ ",sellerCode = " + sellerCode 
				+ ", customerBrandCode = " + customerBrandCode
				+ ", customerCategoryCode = " + customerCategoryCode 
				+ ", currencyCode = " + currencyCode
				+ ", countryCode = " + countryCode
				+ " ,firstName = " + firstName
				+ ", lastName = " + lastName
				+ ", languageCode = " + languageCode 
				+ ",billingCycleCode = " + billingCycleCode
				+ ", email =" + email
				+ ", zipCode = " + zipCode
				+ ", address1 = " + address1
				+ ", address2 = " + address2
				+ ",birthDate = " + birthDate
				+ ", phoneNumber= " +phoneNumber
				+ ",city = "+ city
				+ ",titleCode = "+ titleCode +"]";
	}
}
