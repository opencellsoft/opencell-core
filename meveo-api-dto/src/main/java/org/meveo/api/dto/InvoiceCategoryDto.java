package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InvoiceCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoryDto extends BusinessDto {

	private static final long serialVersionUID = 5166093858617578774L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	private List<LanguageDescriptionDto> languageDescriptions;
	
	private CustomFieldsDto customFields = new CustomFieldsDto();

	public InvoiceCategoryDto() {

	}

	public InvoiceCategoryDto(InvoiceCategory invoiceCategory, CustomFieldsDto customFieldInstances) {
		code = invoiceCategory.getCode();
		description = invoiceCategory.getDescription();
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

	public List<LanguageDescriptionDto> getLanguageDescriptions() {
		return languageDescriptions;
	}

	public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
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
		return "InvoiceCategoryDto [code=" + code + ", description=" + description + ", languageDescriptions=" + languageDescriptions + ", customFields=" + customFields + "]";
	}



}
