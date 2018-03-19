package org.meveo.api.dto.catalog;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "OneShotChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateDto extends ChargeTemplateDto {

	private static final long serialVersionUID = 4465303539660526917L;

	@XmlElement(required = true)
	private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;

	private Boolean immediateInvoicing = true;
	
	@Size(max = 2000)
    private String filterExpression = null;

	public OneShotChargeTemplateDto() {

	}
	
	public OneShotChargeTemplateDto(OneShotChargeTemplate e, CustomFieldsDto customFieldInstances) {
		super(e, customFieldInstances);
		oneShotChargeTemplateType = e.getOneShotChargeTemplateType();
		immediateInvoicing = e.getImmediateInvoicing();
		setFilterExpression(e.getFilterExpression());
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

	public OneShotChargeTemplateTypeEnum getOneShotChargeTemplateType() {
		return oneShotChargeTemplateType;
	}

	public void setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum oneShotChargeTemplateType) {
		this.oneShotChargeTemplateType = oneShotChargeTemplateType;
	}
	
    public String getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

}