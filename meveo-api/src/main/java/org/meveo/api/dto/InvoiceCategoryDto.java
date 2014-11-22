package org.meveo.api.dto;

import java.io.Serializable;
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
public class InvoiceCategoryDto implements Serializable {

	private static final long serialVersionUID = 5166093858617578774L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private List<LanguageDescriptionDto> languageDescriptions;

	public InvoiceCategoryDto() {

	}

	public InvoiceCategoryDto(InvoiceCategory invoiceCategory) {
		code = invoiceCategory.getCode();
		description = invoiceCategory.getDescription();
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

	public void setLanguageDescriptions(
			List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
	}

	@Override
	public String toString() {
		return "InvoiceCategoryDto [code=" + code + ", description="
				+ description + ", languageDescriptions="
				+ languageDescriptions + "]";
	}

}
