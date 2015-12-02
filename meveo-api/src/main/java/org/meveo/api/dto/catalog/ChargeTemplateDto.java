package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.crm.CustomFieldInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeTemplateDto extends BaseDto implements Serializable {

	private static final long serialVersionUID = -5143285194077662656L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	@XmlElement(required = true)
	private String invoiceSubCategory;

	@XmlElement(required = true)
	private boolean disabled;

	private Boolean amountEditable;
	private List<LanguageDescriptionDto> languageDescriptions;

	private String inputUnitDescription;
	private String ratingUnitDescription;
	private BigDecimal unitMultiplicator;
	private int unitNbDecimal;
	private CustomFieldsDto customFields = new CustomFieldsDto();

	private TriggeredEdrTemplatesDto triggeredEdrs = new TriggeredEdrTemplatesDto();

	public ChargeTemplateDto() {

	}

	public ChargeTemplateDto(ChargeTemplate e) {
		code = e.getCode();
		description = e.getDescription();
		if (e.getInvoiceSubCategory() != null) {
			invoiceSubCategory = e.getInvoiceSubCategory().getCode();
		}
		disabled = e.isDisabled();
		amountEditable = e.getAmountEditable();
		if (e.getEdrTemplates() != null) {
			triggeredEdrs = new TriggeredEdrTemplatesDto();
			for (TriggeredEDRTemplate edrTemplate : e.getEdrTemplates()) {
				triggeredEdrs.getTriggeredEdr().add(new TriggeredEdrTemplateDto(edrTemplate));
			}
		}
		
		if (e.getCustomFields() != null) {
			for (CustomFieldInstance cfi : e.getCustomFields().values()) {
				customFields.getCustomField().addAll(CustomFieldDto.toDTO(cfi));
			}
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

	public List<LanguageDescriptionDto> getLanguageDescriptions() {
		return languageDescriptions;
	}

	public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
	}

	@Override
	public String toString() {
		return "ChargeTemplateDto [code=" + code + ", description=" + description + ", invoiceSubCategory="
				+ invoiceSubCategory + ", disabled=" + disabled + ", amountEditable=" + amountEditable
				+ ", languageDescriptions=" + languageDescriptions + ", inputUnitDescription=" + inputUnitDescription
				+ ", ratingUnitDescription=" + ratingUnitDescription + ", unitMultiplicator=" + unitMultiplicator
				+ ", unitNbDecimal=" + unitNbDecimal + ", customFields=" + customFields + ", triggeredEdrs="
				+ triggeredEdrs + "]";
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getInputUnitDescription() {
		return inputUnitDescription;
	}

	public void setInputUnitDescription(String inputUnitDescription) {
		this.inputUnitDescription = inputUnitDescription;
	}

	public String getRatingUnitDescription() {
		return ratingUnitDescription;
	}

	public void setRatingUnitDescription(String ratingUnitDescription) {
		this.ratingUnitDescription = ratingUnitDescription;
	}

	public BigDecimal getUnitMultiplicator() {
		return unitMultiplicator;
	}

	public void setUnitMultiplicator(BigDecimal unitMultiplicator) {
		this.unitMultiplicator = unitMultiplicator;
	}

	public int getUnitNbDecimal() {
		return unitNbDecimal;
	}

	public void setUnitNbDecimal(int unitNbDecimal) {
		this.unitNbDecimal = unitNbDecimal;
	}

	public TriggeredEdrTemplatesDto getTriggeredEdrs() {
		return triggeredEdrs;
	}

	public void setTriggeredEdrs(TriggeredEdrTemplatesDto triggeredEdrs) {
		this.triggeredEdrs = triggeredEdrs;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

}
