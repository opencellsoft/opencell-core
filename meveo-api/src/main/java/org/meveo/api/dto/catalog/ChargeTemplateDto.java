package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.api.dto.LanguageDescriptionDto;

/**
 * @author Edward P. Legaspi
 **/
public abstract class ChargeTemplateDto implements Serializable {

	private static final long serialVersionUID = -5143285194077662656L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	@XmlAttribute(required = true)
	private String invoiceSubCategory;

	private Boolean amountEditable;
	private List<LanguageDescriptionDto> languageDescriptions;

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

	public void setLanguageDescriptions(
			List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
	}

	@Override
	public String toString() {
		return "ChargeTemplateDto [code=" + code + ", description="
				+ description + ", amountEditable=" + amountEditable
				+ ", invoiceSubCategory=" + invoiceSubCategory
				+ ", languageDescriptions=" + languageDescriptions + "]";
	}

	public Boolean getAmountEditable() {
		return amountEditable;
	}

	public void setAmountEditable(Boolean amountEditable) {
		this.amountEditable = amountEditable;
	}

	public String getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(String invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

}
