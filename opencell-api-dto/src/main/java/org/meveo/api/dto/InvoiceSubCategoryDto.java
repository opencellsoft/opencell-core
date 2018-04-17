package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSubCategory;

/**
 * The Class InvoiceSubCategoryDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "InvoiceSubCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1832246068609179546L;

    /** The invoice category. */
    @XmlElement(required = true)
    private String invoiceCategory;

    /** The accounting code. */
    @XmlElement(required = true)
    private String accountingCode;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new invoice sub category dto.
     */
    public InvoiceSubCategoryDto() {

    }

    /**
     * Instantiates a new invoice sub category dto.
     *
     * @param invoiceSubCategory the invoice sub category
     * @param customFieldInstances the custom field instances
     */
    public InvoiceSubCategoryDto(InvoiceSubCategory invoiceSubCategory, CustomFieldsDto customFieldInstances) {
        super(invoiceSubCategory);
        invoiceCategory = invoiceSubCategory.getInvoiceCategory().getCode();
        if (invoiceSubCategory.getAccountingCode() != null) {
            accountingCode = invoiceSubCategory.getAccountingCode().getCode();
        }
        customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(invoiceSubCategory.getDescriptionI18n()));
    }

    /**
     * Gets the invoice category.
     *
     * @return the invoice category
     */
    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    /**
     * Sets the invoice category.
     *
     * @param invoiceCategory the new invoice category
     */
    public void setInvoiceCategory(String invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
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
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
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
        return "InvoiceSubCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", invoiceCategory=" + invoiceCategory + ", accountingCode=" + accountingCode
                + ", languageDescriptions=" + languageDescriptions + ", customFields=" + customFields + "]";
    }

}
