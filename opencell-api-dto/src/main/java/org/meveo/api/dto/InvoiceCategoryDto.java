package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceCategory;

/**
 * The Class InvoiceCategoryDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InvoiceCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoryDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5166093858617578774L;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new invoice category dto.
     */
    public InvoiceCategoryDto() {

    }

    /**
     * Instantiates a new invoice category dto.
     *
     * @param invoiceCategory the invoice category
     * @param customFieldInstances the custom field instances
     */
    public InvoiceCategoryDto(InvoiceCategory invoiceCategory, CustomFieldsDto customFieldInstances) {
        super(invoiceCategory);
        customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(invoiceCategory.getDescriptionI18n()));
    }

    /**
     * Gets the language descriptions.
     *
     * @return the language descriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the new language descriptions
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", languageDescriptions=" + languageDescriptions + ", customFields=" + customFields
                + "]";
    }

}
