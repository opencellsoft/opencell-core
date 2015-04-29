package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.account.CustomersDto;
import org.meveo.model.admin.Seller;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Seller")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerDto extends BaseDto {

	private static final long serialVersionUID = 4763606402719751014L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private String invoicePrefix;
	private String currencyCode;
	private String countryCode;
	private String languageCode;
	private String parentSeller;
	private String provider;

	private CustomersDto customers;

	public SellerDto() {
	}

	public SellerDto(Seller seller) {
		code = seller.getCode();
		description = seller.getDescription();
		invoicePrefix = seller.getInvoicePrefix();

		if (seller.getTradingCountry() != null) {
			countryCode = seller.getTradingCountry().getCountryCode();
		}

		if (seller.getTradingCurrency() != null) {
			currencyCode = seller.getTradingCurrency().getCurrencyCode();
		}

		if (seller.getTradingLanguage() != null) {
			languageCode = seller.getTradingLanguage().getLanguageCode();
		}

		if (seller.getSeller() != null) {
			parentSeller = seller.getSeller().getCode();
		}

		if (seller.getProvider() != null) {
			provider = seller.getProvider().getCode();
		}
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

	public String getInvoicePrefix() {
		return invoicePrefix;
	}

	public void setInvoicePrefix(String invoicePrefix) {
		this.invoicePrefix = invoicePrefix;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getParentSeller() {
		return parentSeller;
	}

	public void setParentSeller(String parentSeller) {
		this.parentSeller = parentSeller;
	}

	@Override
	public String toString() {
		return "SellerDto [code=" + code + ", description=" + description + ", invoicePrefix=" + invoicePrefix
				+ ", currencyCode=" + currencyCode + ", countryCode=" + countryCode + ", languageCode=" + languageCode
				+ ", parentSeller=" + parentSeller + ", provider=" + provider + ", customers=" + customers + "]";
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public CustomersDto getCustomers() {
		return customers;
	}

	public void setCustomers(CustomersDto customers) {
		this.customers = customers;
	}

}
