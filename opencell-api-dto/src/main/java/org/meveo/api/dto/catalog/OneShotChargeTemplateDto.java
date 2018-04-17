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
 * The Class OneShotChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "OneShotChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateDto extends ChargeTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4465303539660526917L;

    /** The one shot charge template type. */
    @XmlElement(required = true)
    private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;

    /** The immediate invoicing. */
    private Boolean immediateInvoicing = true;

    /** The filter expression. */
    @Size(max = 2000)
    private String filterExpression = null;

    /**
     * Instantiates a new one shot charge template dto.
     */
    public OneShotChargeTemplateDto() {

    }

    /**
     * Instantiates a new one shot charge template dto.
     *
     * @param oneShotChargeTemplate the OneShotChargeTemplate entity
     * @param customFieldInstances the custom field instances
     */
    public OneShotChargeTemplateDto(OneShotChargeTemplate oneShotChargeTemplate, CustomFieldsDto customFieldInstances) {
        super(oneShotChargeTemplate, customFieldInstances);
        oneShotChargeTemplateType = oneShotChargeTemplate.getOneShotChargeTemplateType();
        immediateInvoicing = oneShotChargeTemplate.getImmediateInvoicing();
        setFilterExpression(oneShotChargeTemplate.getFilterExpression());
    }

    /**
     * Gets the immediate invoicing.
     *
     * @return the immediate invoicing
     */
    public Boolean getImmediateInvoicing() {
        return immediateInvoicing;
    }

    /**
     * Sets the immediate invoicing.
     *
     * @param immediateInvoicing the new immediate invoicing
     */
    public void setImmediateInvoicing(Boolean immediateInvoicing) {
        this.immediateInvoicing = immediateInvoicing;
    }


    /**
     * Gets the one shot charge template type.
     *
     * @return the one shot charge template type
     */
    public OneShotChargeTemplateTypeEnum getOneShotChargeTemplateType() {
        return oneShotChargeTemplateType;
    }

    /**
     * Sets the one shot charge template type.
     *
     * @param oneShotChargeTemplateType the new one shot charge template type
     */
    public void setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum oneShotChargeTemplateType) {
        this.oneShotChargeTemplateType = oneShotChargeTemplateType;
    }

    /**
     * Gets the filter expression.
     *
     * @return the filter expression
     */
    public String getFilterExpression() {
        return filterExpression;
    }

    /**
     * Sets the filter expression.
     *
     * @param filterExpression the new filter expression
     */
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }
    
    @Override
    public String toString() {
        return "OneShotChargeTemplateDto [oneShotChargeTemplateType=" + oneShotChargeTemplateType + ", immediateInvoicing=" + immediateInvoicing + ", getCode()=" + getCode()
                + ", getDescription()=" + getDescription() + ", getLanguageDescriptions()=" + getLanguageDescriptions() + ", toString()=" + super.toString()
                + ", getAmountEditable()=" + getAmountEditable() + ", getInvoiceSubCategory()=" + getInvoiceSubCategory() + ", isDisabled()=" + isDisabled() + ", getClass()="
                + getClass() + ", hashCode()=" + hashCode() + "]";
    }
}