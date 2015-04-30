package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OneShotChargeTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OneShotChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateDto extends ChargeTemplateDto {

	private static final long serialVersionUID = 4465303539660526917L;

	@XmlElement(required = true)
	private Integer oneShotChargeTemplateType;

	private Boolean immediateInvoicing = true;

	public OneShotChargeTemplateDto() {

	}

	public OneShotChargeTemplateDto(OneShotChargeTemplate e) {
		super(e);
		oneShotChargeTemplateType = e.getOneShotChargeTemplateType().getId();
		immediateInvoicing = e.getImmediateInvoicing();
	}

	public Boolean getImmediateInvoicing() {
		return immediateInvoicing;
	}

	public void setImmediateInvoicing(Boolean immediateInvoicing) {
		this.immediateInvoicing = immediateInvoicing;
	}

	@Override
	public String toString() {
		return "OneShotChargeTemplateDto [oneShotChargeTemplateType=" + oneShotChargeTemplateType
				+ ", immediateInvoicing=" + immediateInvoicing + ", getCode()=" + getCode() + ", getDescription()="
				+ getDescription() + ", getLanguageDescriptions()=" + getLanguageDescriptions() + ", toString()="
				+ super.toString() + ", getAmountEditable()=" + getAmountEditable() + ", getInvoiceSubCategory()="
				+ getInvoiceSubCategory() + ", isDisabled()=" + isDisabled() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}

	public Integer getOneShotChargeTemplateType() {
		return oneShotChargeTemplateType;
	}

	public void setOneShotChargeTemplateType(Integer oneShotChargeTemplateType) {
		this.oneShotChargeTemplateType = oneShotChargeTemplateType;
	}

}
