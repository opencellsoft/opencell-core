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

@XmlRootElement(name = "ChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeTemplateDto extends BusinessDto implements Serializable {

    private static final long serialVersionUID = -5143285194077662656L;

    @XmlElement(required = true)
    private String invoiceSubCategory;

    @XmlElement(required = true)
    private boolean disabled;

    private Boolean amountEditable;
    private List<LanguageDescriptionDto> languageDescriptions;

    private String inputUnitDescription;
    private String ratingUnitDescription;
    private BigDecimal unitMultiplicator;
    private int unitNbDecimal = BaseEntity.NB_DECIMALS;
    private RoundingModeEnum roundingModeDtoEnum;
    private String revenueRecognitionRuleCode;
    private CustomFieldsDto customFields;    

    private TriggeredEdrTemplatesDto triggeredEdrs = new TriggeredEdrTemplatesDto();

    public ChargeTemplateDto() {

    }

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

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    @Override
    public String toString() {
        return "ChargeTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", invoiceSubCategory=" + invoiceSubCategory + ", disabled=" + disabled
                + ", amountEditable=" + amountEditable + ", languageDescriptions=" + languageDescriptions + ", inputUnitDescription=" + inputUnitDescription
                + ", ratingUnitDescription=" + ratingUnitDescription + ", unitMultiplicator=" + unitMultiplicator + ", unitNbDecimal=" + unitNbDecimal + ", customFields="
                + customFields + ", triggeredEdrs=" + triggeredEdrs + ",roundingModeDtoEnum=" + roundingModeDtoEnum + "]";
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

    public RoundingModeEnum getRoundingModeDtoEnum() {
        return roundingModeDtoEnum;
    }

    public void setRoundingModeDtoEnum(RoundingModeEnum roundingModeDtoEnum) {
        this.roundingModeDtoEnum = roundingModeDtoEnum;
    }

    public String getRevenueRecognitionRuleCode() {
        return revenueRecognitionRuleCode;
    }

    public void setRevenueRecognitionRuleCode(String revenueRecognitionRuleCode) {
        this.revenueRecognitionRuleCode = revenueRecognitionRuleCode;
    }

}