package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.ProductChargeTemplate;

/**
 * The Class ProductChargeTemplateDto.
 */
@XmlRootElement(name = "ProductChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductChargeTemplateDto extends ChargeTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8453142818864003969L;

    /**
     * Instantiates a new product charge template dto.
     */
    public ProductChargeTemplateDto() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new product charge template dto.
     *
     * @param productChargeTemplate the product charge template
     * @param customFieldInstances the custom field instances
     */
    public ProductChargeTemplateDto(ProductChargeTemplate productChargeTemplate, CustomFieldsDto customFieldInstances) {
        super(productChargeTemplate, customFieldInstances);
    }

    @Override
    public String toString() {
        return "ProductChargeTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", invoiceSubCategory=" + getInvoiceSubCategory() + ", disabled="
                + isDisabled() + ", amountEditable=" + getAmountEditable() + ", languageDescriptions=" + getLanguageDescriptions() + ", inputUnitDescription="
                + getInputUnitDescription() + ", ratingUnitDescription=" + getRatingUnitDescription() + ", unitMultiplicator=" + getUnitMultiplicator() + ", unitNbDecimal="
                + getUnitNbDecimal() + ", customFields=" + getCustomFields() + ", triggeredEdrs=" + getTriggeredEdrs() + ",roundingModeDtoEnum=" + getRoundingModeDtoEnum() + "]";
    }

}
