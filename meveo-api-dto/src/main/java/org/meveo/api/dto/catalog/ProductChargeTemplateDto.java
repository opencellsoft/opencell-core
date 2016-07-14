package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.ProductChargeTemplate;

@XmlRootElement(name = "ProductChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductChargeTemplateDto extends ChargeTemplateDto implements Serializable {

	private static final long serialVersionUID = -8453142818864003969L;
	
	public ProductChargeTemplateDto() {
		// TODO Auto-generated constructor stub
	}
	
	public ProductChargeTemplateDto(ProductChargeTemplate productChargeTemplate) {
		super(productChargeTemplate, null);
	}

	@Override
	public String toString() {
		return "ProductChargeTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", invoiceSubCategory=" + getInvoiceSubCategory() + ", disabled="
				+ isDisabled() + ", amountEditable=" + getAmountEditable() + ", languageDescriptions=" + getLanguageDescriptions() + ", inputUnitDescription="
				+ getInputUnitDescription() + ", ratingUnitDescription=" + getRatingUnitDescription() + ", unitMultiplicator=" + getUnitMultiplicator() + ", unitNbDecimal="
				+ getUnitNbDecimal() + ", customFields=" + getCustomFields() + ", triggeredEdrs=" + getTriggeredEdrs() + ",roundingModeDtoEnum=" + getRoundingModeDtoEnum() + "]";
	}

}
