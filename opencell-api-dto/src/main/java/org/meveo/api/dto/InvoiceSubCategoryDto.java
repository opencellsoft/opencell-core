package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSubCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InvoiceSubCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryDto extends BusinessDto {

	private static final long serialVersionUID = 1832246068609179546L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	@XmlElement(required = true)
	private String invoiceCategory;
	
	@XmlElement(required = true)
	private String accountingCode;

	private List<LanguageDescriptionDto> languageDescriptions;
	
	private CustomFieldsDto customFields = new CustomFieldsDto();

	public InvoiceSubCategoryDto() {

	}

	public InvoiceSubCategoryDto(InvoiceSubCategory invoiceSubCategory, CustomFieldsDto customFieldInstances) {
		code = invoiceSubCategory.getCode();
		description = invoiceSubCategory.getDescription();
		invoiceCategory = invoiceSubCategory.getInvoiceCategory().getCode();
		accountingCode=invoiceSubCategory.getAccountingCode();
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

	public String getInvoiceCategory() {
		return invoiceCategory;
	}

	public void setInvoiceCategory(String invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	public List<LanguageDescriptionDto> getLanguageDescriptions() {
		return languageDescriptions;
	}

	public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
	}
	
	public String getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(String accountingCode) {
		this.accountingCode = accountingCode;
	}

	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	
	@Override
	public String toString() {
		return "InvoiceSubCategoryDto [code=" + code + ", description=" + description + ", invoiceCategory=" + invoiceCategory + ", accountingCode=" + accountingCode + ", languageDescriptions=" + languageDescriptions + ", customFields=" + customFields + "]";
	}

	
}
