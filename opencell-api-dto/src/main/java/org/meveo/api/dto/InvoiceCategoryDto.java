package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InvoiceCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoryDto extends BusinessDto {

	private static final long serialVersionUID = 5166093858617578774L;

	private List<LanguageDescriptionDto> languageDescriptions;
	
	private CustomFieldsDto customFields;

	public InvoiceCategoryDto() {

	}

	public InvoiceCategoryDto(InvoiceCategory invoiceCategory, CustomFieldsDto customFieldInstances) {
		super(invoiceCategory);
        customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(invoiceCategory.getDescriptionI18n()));
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
		return "InvoiceCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", languageDescriptions=" + languageDescriptions + ", customFields=" + customFields + "]";
	}



}
