package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceConfiguration;
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
	private Integer invoiceSequenceSize;
	
	private boolean enterprise;
	private String invoicePrefix;
	private Long currentInvoiceNb;
	private Boolean displaySubscriptions;
	private Boolean displayServices;
	private Boolean displayOffers;
	private Boolean displayEdrs;
	private Boolean displayProvider;
	private boolean displayFreeTransacInInvoice;
	
	private String invoiceAdjustmentPrefix;
	private Long currentInvoiceAdjustmentNb;
	private Integer invoiceAdjustmentSequenceSize;
	
	
	@XmlElement(required = false)
    private CustomFieldsDto customFields;

	public ProviderDto() {

	}

	public ProviderDto(Provider e) {
		code = e.getCode();
		description = e.getDescription();
		invoiceSequenceSize=e.getInvoiceSequenceSize();
		if (e.getCurrency() != null) {
			currency = e.getCurrency().getCurrencyCode();
		}
		if (e.getCountry() != null) {
			country = e.getCountry().getCountryCode();
		}
		if (e.getLanguage() != null) {
			language = e.getLanguage().getLanguageCode();
		}
		multiCurrency = e.getMulticurrencyFlag();
		multiCountry = e.getMulticountryFlag();
		multiLanguage = e.getMultilanguageFlag();
		
		customFields = CustomFieldsDto.toDTO(e.getCfFields());
		
		this.setEnterprise(e.isEntreprise());
		this.setInvoicePrefix(e.getInvoicePrefix());
		this.setCurrentInvoiceNb(e.getCurrentInvoiceNb());
		this.setDisplayFreeTransacInInvoice(e.isDisplayFreeTransacInInvoice());
		InvoiceConfiguration invoiceConfiguration = e.getInvoiceConfiguration();
		if (invoiceConfiguration != null) {
			this.setDisplaySubscriptions(invoiceConfiguration.getDisplaySubscriptions());
			this.setDisplayServices(invoiceConfiguration.getDisplayServices());
			this.setDisplayOffers(invoiceConfiguration.getDisplayOffers());
			this.setDisplayEdrs(invoiceConfiguration.getDisplayEdrs());
			this.setDisplayProvider(invoiceConfiguration.getDisplayProvider());
		}
		
		if (e.getInvoiceAdjustmentPrefix() != null) {
			this.setInvoiceAdjustmentPrefix(e.getInvoiceAdjustmentPrefix());
		}
		
		if (e.getCurrentInvoiceAdjustmentNb() != null) {
			this.setCurrentInvoiceAdjustmentNb(e.getCurrentInvoiceAdjustmentNb());
		}
		
		if (e.getInvoiceAdjustmentSequenceSize() != null) {
			this.setInvoiceAdjustmentSequenceSize(e.getInvoiceAdjustmentSequenceSize());
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

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public Integer getInvoiceSequenceSize() {
		return invoiceSequenceSize;
	}

	public void setInvoiceSequenceSize(Integer invoiceSequenceSize) {
		this.invoiceSequenceSize = invoiceSequenceSize;
	}

	public boolean isEnterprise() {
		return enterprise;
	}

	public void setEnterprise(boolean enterprise) {
		this.enterprise = enterprise;
	}

	public String getInvoicePrefix() {
		return invoicePrefix;
	}

	public void setInvoicePrefix(String invoicePrefix) {
		this.invoicePrefix = invoicePrefix;
	}

	public Long getCurrentInvoiceNb() {
		return currentInvoiceNb;
	}

	public void setCurrentInvoiceNb(Long currentInvoiceNb) {
		this.currentInvoiceNb = currentInvoiceNb;
	}

	public Boolean getDisplaySubscriptions() {
		return displaySubscriptions;
	}

	public void setDisplaySubscriptions(Boolean displaySubscriptions) {
		this.displaySubscriptions = displaySubscriptions;
	}

	public Boolean getDisplayServices() {
		return displayServices;
	}

	public void setDisplayServices(Boolean displayServices) {
		this.displayServices = displayServices;
	}

	public Boolean getDisplayOffers() {
		return displayOffers;
	}

	public void setDisplayOffers(Boolean displayOffers) {
		this.displayOffers = displayOffers;
	}

	public Boolean getDisplayEdrs() {
		return displayEdrs;
	}

	public void setDisplayEdrs(Boolean displayEdrs) {
		this.displayEdrs = displayEdrs;
	}

	public Boolean getDisplayProvider() {
		return displayProvider;
	}

	public void setDisplayProvider(Boolean displayProvider) {
		this.displayProvider = displayProvider;
	}

	public boolean isDisplayFreeTransacInInvoice() {
		return displayFreeTransacInInvoice;
	}

	public void setDisplayFreeTransacInInvoice(boolean displayFreeTransacInInvoice) {
		this.displayFreeTransacInInvoice = displayFreeTransacInInvoice;
	}

	public String getInvoiceAdjustmentPrefix() {
		return invoiceAdjustmentPrefix;
	}

	public void setInvoiceAdjustmentPrefix(String invoiceAdjustmentPrefix) {
		this.invoiceAdjustmentPrefix = invoiceAdjustmentPrefix;
	}

	public Long getCurrentInvoiceAdjustmentNb() {
		return currentInvoiceAdjustmentNb;
	}

	public void setCurrentInvoiceAdjustmentNb(Long currentInvoiceAdjustmentNb) {
		this.currentInvoiceAdjustmentNb = currentInvoiceAdjustmentNb;
	}

	public Integer getInvoiceAdjustmentSequenceSize() {
		return invoiceAdjustmentSequenceSize;
	}

	public void setInvoiceAdjustmentSequenceSize(
			Integer invoiceAdjustmentSequenceSize) {
		this.invoiceAdjustmentSequenceSize = invoiceAdjustmentSequenceSize;
	}

	@Override
	public String toString() {
		return "ProviderDto [code=" + code + ", description=" + description
				+ ", currency=" + currency + ", country=" + country
				+ ", language=" + language + ", multiCurrency=" + multiCurrency
				+ ", multiCountry=" + multiCountry + ", multiLanguage="
				+ multiLanguage + ", userAccount=" + userAccount
				+ ", invoiceSequenceSize=" + invoiceSequenceSize
				+ ", enterprise=" + enterprise + ", invoicePrefix="
				+ invoicePrefix + ", currentInvoiceNb=" + currentInvoiceNb
				+ ", displaySubscriptions=" + displaySubscriptions
				+ ", displayServices=" + displayServices + ", displayOffers="
				+ displayOffers + ", displayEdrs=" + displayEdrs
				+ ", displayProvider=" + displayProvider
				+ ", displayFreeTransacInInvoice="
				+ displayFreeTransacInInvoice + ", invoiceAdjustmentPrefix="
				+ invoiceAdjustmentPrefix + ", currentInvoiceAdjustmentNb="
				+ currentInvoiceAdjustmentNb
				+ ", invoiceAdjustmentSequenceSize="
				+ invoiceAdjustmentSequenceSize + ", customFields="
				+ customFields + "]";
	}

	
}
