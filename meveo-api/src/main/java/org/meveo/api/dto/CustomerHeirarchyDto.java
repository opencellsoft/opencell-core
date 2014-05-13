package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customerHeirarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerHeirarchyDto extends BaseDto {

	private static final long serialVersionUID = -8469973066490541924L;

	String customerId;
	String sellerCode;
	String customerBrandCode;
	String customerCategoryCode;
	String currencyCode;
	String countryCode;
	String lastName;
	String languageCode;
	String billingCycleCode;

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

	@Override
	public String toString() {
		return "CustomerHeirarchyDto [customerId = " + customerId
				+ ",sellerCode = " + sellerCode + ", customerBrandCode = "
				+ customerBrandCode + ", customerCategoryCode = "
				+ customerCategoryCode + ",currencyCode = " + currencyCode
				+ ", countryCode = " + countryCode + ", lastName = " + lastName
				+ ", languageCode = " + languageCode + ",billingCycleCode = "
				+ billingCycleCode + "]";
	}
}
