package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.dto.invoice.InvoiceConfigurationDto;
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
	private Boolean multiCurrency;
	private Boolean multiCountry;
	private Boolean multiLanguage;
	private String userAccount;
	
	private Boolean enterprise;
	private Boolean levelDuplication;	
	private Integer rounding;
	private Long prepaidReservationExpirationDelayinMillisec;
	private String discountAccountingCode;
	private String email;
	private BankCoordinatesDto bankCoordinates = new BankCoordinatesDto();
	private Boolean recognizeRevenue;
	private InvoiceConfigurationDto invoiceConfiguration = new InvoiceConfigurationDto();

	@XmlElement(required = false)
	private CustomFieldsDto customFields;

	public ProviderDto() {
	}
	
	public ProviderDto(Provider e, CustomFieldsDto customFieldInstances) {
		this(e, customFieldInstances, true);
	}

	public ProviderDto(Provider provider, CustomFieldsDto customFieldInstances, boolean loadProviderData) {
		code = provider.getCode();
		
		if (loadProviderData) {			
			description = provider.getDescription();			
			if (provider.getCurrency() != null) {
				currency = provider.getCurrency().getCurrencyCode();
			}
			if (provider.getCountry() != null) {
				country = provider.getCountry().getCountryCode();
			}
			if (provider.getLanguage() != null) {
				language = provider.getLanguage().getLanguageCode();
			}
			multiCurrency = provider.getMulticurrencyFlag();
			multiCountry = provider.getMulticountryFlag();
			multiLanguage = provider.getMultilanguageFlag();
			rounding = provider.getRounding();
			prepaidReservationExpirationDelayinMillisec = provider.getPrepaidReservationExpirationDelayinMillisec();
			discountAccountingCode = provider.getDiscountAccountingCode();
			email = provider.getEmail();			

			this.setEnterprise(provider.isEntreprise());
			this.setLevelDuplication(provider.isLevelDuplication());
			
			this.setRecognizeRevenue(provider.isRecognizeRevenue());

			if (provider.getBankCoordinates() != null) {
				this.setBankCoordinates(new BankCoordinatesDto(provider.getBankCoordinates()));
			}

			if (provider.getInvoiceConfiguration() != null) {
				this.setInvoiceConfiguration(new InvoiceConfigurationDto(provider.getInvoiceConfiguration()));
			}else{
				this.setInvoiceConfiguration(new InvoiceConfigurationDto());
			}
			this.getInvoiceConfiguration().setDisplayFreeTransacInInvoice(provider.isDisplayFreeTransacInInvoice());
		}
		
		customFields = customFieldInstances;
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

	public Boolean isMultiCurrency() {
		return multiCurrency;
	}

	public void setMultiCurrency(Boolean multiCurrency) {
		this.multiCurrency = multiCurrency;
	}

	public Boolean isMultiCountry() {
		return multiCountry;
	}

	public void setMultiCountry(Boolean multiCountry) {
		this.multiCountry = multiCountry;
	}

	public Boolean isMultiLanguage() {
		return multiLanguage;
	}

	public void setMultiLanguage(Boolean multiLanguage) {
		this.multiLanguage = multiLanguage;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}


	public Boolean isEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Boolean enterprise) {
		this.enterprise = enterprise;
	}

	public Boolean isLevelDuplication() {
		return levelDuplication;
	}

	public void setLevelDuplication(Boolean levelDuplication) {
		this.levelDuplication = levelDuplication;
	}

	public BankCoordinatesDto getBankCoordinates() {
		return bankCoordinates;
	}

	public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
		this.bankCoordinates = bankCoordinates;
	}

	public InvoiceConfigurationDto getInvoiceConfiguration() {
		return invoiceConfiguration;
	}

	public void setInvoiceConfiguration(InvoiceConfigurationDto invoiceConfiguration) {
		this.invoiceConfiguration = invoiceConfiguration;
	}

	public Integer getRounding() {
		return rounding;
	}

	public void setRounding(Integer rounding) {
		this.rounding = rounding;
	}

	public Long getPrepaidReservationExpirationDelayinMillisec() {
		return prepaidReservationExpirationDelayinMillisec;
	}

	public void setPrepaidReservationExpirationDelayinMillisec(Long prepaidReservationExpirationDelayinMillisec) {
		this.prepaidReservationExpirationDelayinMillisec = prepaidReservationExpirationDelayinMillisec;
	}

	public String getDiscountAccountingCode() {
		return discountAccountingCode;
	}

	public void setDiscountAccountingCode(String discountAccountingCode) {
		this.discountAccountingCode = discountAccountingCode;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean isRecognizeRevenue() {
		return recognizeRevenue;
	}

	public void setRecognizeRevenue(Boolean recognizeRevenue) {
		this.recognizeRevenue = recognizeRevenue;
	}

	@Override
	public String toString() {
		return "ProviderDto [code=" + code + ", description=" + description + ", currency=" + currency + ", country=" + country + ", language=" + language + ", multiCurrency=" + multiCurrency + ", multiCountry=" + multiCountry + ", multiLanguage=" + multiLanguage + ", userAccount=" + userAccount + ", enterprise=" + enterprise + ", levelDuplication=" + levelDuplication +  ", rounding=" + rounding
				+ ", prepaidReservationExpirationDelayinMillisec=" + prepaidReservationExpirationDelayinMillisec + ", discountAccountingCode=" + discountAccountingCode + ", email=" + email + ", bankCoordinates=" + bankCoordinates + ", recognizeRevenue=" + recognizeRevenue + ", invoiceConfiguration=" + invoiceConfiguration + ", customFields=" + customFields + "]";
	}


	

}
