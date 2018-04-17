package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;

/**
 * The Class ChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "ChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeTemplateDto extends BusinessDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5143285194077662656L;

    /** The invoice sub category. */
    @XmlElement(required = true)
    private String invoiceSubCategory;

    /** The disabled. */
    @XmlElement(required = true)
    private boolean disabled;

    /** The amount editable. */
    private Boolean amountEditable;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The input unit description. */
    private String inputUnitDescription;

    /** The rating unit description. */
    private String ratingUnitDescription;

    /** The unit multiplicator. */
    private BigDecimal unitMultiplicator;

    /** The unit nb decimal. */
    private int unitNbDecimal = BaseEntity.NB_DECIMALS;

    /** The rounding mode dto enum. */
    private RoundingModeEnum roundingModeDtoEnum;

    /** The revenue recognition rule code. */
    private String revenueRecognitionRuleCode;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The triggered edrs. */
    private TriggeredEdrTemplatesDto triggeredEdrs = new TriggeredEdrTemplatesDto();

    /**
     * Instantiates a new charge template dto.
     */
    public ChargeTemplateDto() {

    }

    /**
     * Instantiates a new charge template dto.
     *
     * @param chargeTemplate the charge template
     * @param customFieldInstances the custom field instances
     */
    public ChargeTemplateDto(ChargeTemplate chargeTemplate, CustomFieldsDto customFieldInstances) {
        super(chargeTemplate);
        if (chargeTemplate.getInvoiceSubCategory() != null) {
            invoiceSubCategory = chargeTemplate.getInvoiceSubCategory().getCode();
        }
        disabled = chargeTemplate.isDisabled();
        amountEditable = chargeTemplate.getAmountEditable();
        if (chargeTemplate.getEdrTemplates() != null) {
            triggeredEdrs = new TriggeredEdrTemplatesDto();
            for (TriggeredEDRTemplate edrTemplate : chargeTemplate.getEdrTemplates()) {
                triggeredEdrs.getTriggeredEdr().add(new TriggeredEdrTemplateDto(edrTemplate));
            }
        }
        roundingModeDtoEnum = chargeTemplate.getRoundingMode();
        customFields = customFieldInstances;

        inputUnitDescription = chargeTemplate.getInputUnitDescription();
        ratingUnitDescription = chargeTemplate.getRatingUnitDescription();
        unitNbDecimal = chargeTemplate.getUnitNbDecimal();
        unitMultiplicator = chargeTemplate.getUnitMultiplicator();
        roundingModeDtoEnum = chargeTemplate.getRoundingMode();
        revenueRecognitionRuleCode = chargeTemplate.getRevenueRecognitionRule() == null ? null : chargeTemplate.getRevenueRecognitionRule().getCode();
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(chargeTemplate.getDescriptionI18n()));
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
     * Gets the amount editable.
     *
     * @return the amount editable
     */
    public Boolean getAmountEditable() {
        return amountEditable;
    }

    /**
     * Sets the amount editable.
     *
     * @param amountEditable the new amount editable
     */
    public void setAmountEditable(Boolean amountEditable) {
        this.amountEditable = amountEditable;
    }

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public String getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(String invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * Checks if is disabled.
     *
     * @return true, if is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the new disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Gets the input unit description.
     *
     * @return the input unit description
     */
    public String getInputUnitDescription() {
        return inputUnitDescription;
    }

    /**
     * Sets the input unit description.
     *
     * @param inputUnitDescription the new input unit description
     */
    public void setInputUnitDescription(String inputUnitDescription) {
        this.inputUnitDescription = inputUnitDescription;
    }

    /**
     * Gets the rating unit description.
     *
     * @return the rating unit description
     */
    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    /**
     * Sets the rating unit description.
     *
     * @param ratingUnitDescription the new rating unit description
     */
    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    /**
     * Gets the unit multiplicator.
     *
     * @return the unit multiplicator
     */
    public BigDecimal getUnitMultiplicator() {
        return unitMultiplicator;
    }

    /**
     * Sets the unit multiplicator.
     *
     * @param unitMultiplicator the new unit multiplicator
     */
    public void setUnitMultiplicator(BigDecimal unitMultiplicator) {
        this.unitMultiplicator = unitMultiplicator;
    }

    /**
     * Gets the unit nb decimal.
     *
     * @return the unit nb decimal
     */
    public int getUnitNbDecimal() {
        return unitNbDecimal;
    }

    /**
     * Sets the unit nb decimal.
     *
     * @param unitNbDecimal the new unit nb decimal
     */
    public void setUnitNbDecimal(int unitNbDecimal) {
        this.unitNbDecimal = unitNbDecimal;
    }

    /**
     * Gets the triggered edrs.
     *
     * @return the triggered edrs
     */
    public TriggeredEdrTemplatesDto getTriggeredEdrs() {
        return triggeredEdrs;
    }

    /**
     * Sets the triggered edrs.
     *
     * @param triggeredEdrs the new triggered edrs
     */
    public void setTriggeredEdrs(TriggeredEdrTemplatesDto triggeredEdrs) {
        this.triggeredEdrs = triggeredEdrs;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the rounding mode dto enum.
     *
     * @return the rounding mode dto enum
     */
    public RoundingModeEnum getRoundingModeDtoEnum() {
        return roundingModeDtoEnum;
    }

    /**
     * Sets the rounding mode dto enum.
     *
     * @param roundingModeDtoEnum the new rounding mode dto enum
     */
    public void setRoundingModeDtoEnum(RoundingModeEnum roundingModeDtoEnum) {
        this.roundingModeDtoEnum = roundingModeDtoEnum;
    }

    /**
     * Gets the revenue recognition rule code.
     *
     * @return the revenue recognition rule code
     */
    public String getRevenueRecognitionRuleCode() {
        return revenueRecognitionRuleCode;
    }

    /**
     * Sets the revenue recognition rule code.
     *
     * @param revenueRecognitionRuleCode the new revenue recognition rule code
     */
    public void setRevenueRecognitionRuleCode(String revenueRecognitionRuleCode) {
        this.revenueRecognitionRuleCode = revenueRecognitionRuleCode;
    }
    
    @Override
    public String toString() {
        return "ChargeTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", invoiceSubCategory=" + invoiceSubCategory + ", disabled=" + disabled
                + ", amountEditable=" + amountEditable + ", languageDescriptions=" + languageDescriptions + ", inputUnitDescription=" + inputUnitDescription
                + ", ratingUnitDescription=" + ratingUnitDescription + ", unitMultiplicator=" + unitMultiplicator + ", unitNbDecimal=" + unitNbDecimal + ", customFields="
                + customFields + ", triggeredEdrs=" + triggeredEdrs + ",roundingModeDtoEnum=" + roundingModeDtoEnum + "]";
    }
}