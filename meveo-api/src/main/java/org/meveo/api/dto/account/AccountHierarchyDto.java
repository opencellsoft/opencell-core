package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.crm.Customer;

@XmlRootElement(name = "AccountHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountHierarchyDto extends BaseDto {

	private static final long serialVersionUID = -8469973066490541924L;

	private String customerId;
	private String sellerCode;
	private String customerBrandCode;
	private String customerCategoryCode;
	private String currencyCode;
	private String countryCode;
	private String languageCode;

	private String titleCode;
	private String firstName;
	private String lastName;
	private String email;
	private Date birthDate;
	private String phoneNumber;

	private String billingCycleCode;

	private String address1;
	private String address2;
	private String zipCode;
	private String city;	

	@XmlTransient
	private int limit;
	
	@XmlTransient
	private String sortField;
	
	@XmlTransient
	private int index;
	
	public AccountHierarchyDto() {
		
	}

	public AccountHierarchyDto(Customer customer) {
		this.setCustomerId(customer.getCode());
		this.setEmail(customer.getContactInformation().getEmail());
		this.setPhoneNumber(customer.getContactInformation().getPhone());

		if (customer.getAddress() != null) {
			this.setAddress1(customer.getAddress().getAddress1());
			this.setAddress2(customer.getAddress().getAddress2());
			this.setZipCode(customer.getAddress().getZipCode());
			this.setCountryCode(customer.getAddress().getCountry());
			this.setCity(customer.getAddress().getCity());
		}

		if (customer.getName() != null) {
			if (customer.getName().getTitle() != null) {
				this.setTitleCode(customer.getName().getTitle().getCode());
			}
			this.setLastName(customer.getName().getLastName());
			this.setFirstName(customer.getName().getFirstName());
		}
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
		return "AccountHierarchyDto [customerId=" + customerId
				+ ", sellerCode=" + sellerCode + ", customerBrandCode="
				+ customerBrandCode + ", customerCategoryCode="
				+ customerCategoryCode + ", currencyCode=" + currencyCode
				+ ", countryCode=" + countryCode + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", languageCode=" + languageCode
				+ ", billingCycleCode=" + billingCycleCode + ", email=" + email
				+ ", zipCode=" + zipCode + ", address1=" + address1
				+ ", address2=" + address2 + ", birthDate=" + birthDate
				+ ", phoneNumber=" + phoneNumber + ", city=" + city
				+ ", titleCode=" + titleCode + "]";
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

}
