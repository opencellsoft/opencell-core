package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.Provider;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Provider")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderDto extends BaseDto {

	private static final long serialVersionUID = 5599223889050605880L;

	@XmlAttribute(required = true)
	private String code;

	private String description;
	private String currency;
	private String country;
	private String language;
	private boolean multiCurrency;
	private boolean multiCountry;
	private boolean multiLanguage;
	private String userAccount;

	public ProviderDto() {

	}

	public ProviderDto(Provider provider) {
		code = provider.getCode();
		description = provider.getDescription();
		currency = provider.getCurrency().getCurrencyCode();
		country = provider.getCountry().getCountryCode();
		language = provider.getLanguage().getLanguageCode();
		multiCurrency = provider.getMulticurrencyFlag();
		multiCountry = provider.getMulticountryFlag();
		multiLanguage = provider.getMultilanguageFlag();
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isMultiCurrency() {
		return multiCurrency;
	}

	public void setMultiCurrency(boolean multiCurrency) {
		this.multiCurrency = multiCurrency;
	}

	public boolean isMultiCountry() {
		return multiCountry;
	}

	public void setMultiCountry(boolean multiCountry) {
		this.multiCountry = multiCountry;
	}

	public boolean isMultiLanguage() {
		return multiLanguage;
	}

	public void setMultiLanguage(boolean multiLanguage) {
		this.multiLanguage = multiLanguage;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public String toString() {
		return "ProviderDto [code=" + code + ", description=" + description + ", currency=" + currency + ", country="
				+ country + ", language=" + language + ", multiCurrency=" + multiCurrency + ", multiCountry="
				+ multiCountry + ", multiLanguage=" + multiLanguage + ", userAccount=" + userAccount + "]";
	}

}
