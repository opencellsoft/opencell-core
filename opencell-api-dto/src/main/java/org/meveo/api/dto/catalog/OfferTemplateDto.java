package org.meveo.api.dto.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;

/**
 * The Class OfferTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto extends ProductOfferingDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9156372453581362595L;

    /** The bom code. */
    private String bomCode;

    /** The offer template category code. */
    @Deprecated
    private String offerTemplateCategoryCode;

    /** The offer service templates. */
    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    private List<OfferServiceTemplateDto> offerServiceTemplates;

    /** The offer product templates. */
    @XmlElementWrapper(name = "offerProductTemplates")
    @XmlElement(name = "offerProductTemplate")
    private List<OfferProductTemplateDto> offerProductTemplates;

    /** The renewal rule. */
    private SubscriptionRenewalDto renewalRule;

    /**
     * Instantiates a new offer template dto.
     */
    public OfferTemplateDto() {

    }

    /**
     * Constructor.
     *
     * @param offerTemplate Offer template entity
     * @param customFieldsDto Custom fields DTO
     * @param asLink Convert to DTO with minimal information only - code and validity dates
     */
    public OfferTemplateDto(OfferTemplate offerTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
        super(offerTemplate, customFieldsDto, asLink);
        id = offerTemplate.getId();

        if (offerTemplate.getBusinessOfferModel() != null) {
            setBomCode(offerTemplate.getBusinessOfferModel().getCode());
        }
        if (!asLink) {
            setRenewalRule(new SubscriptionRenewalDto(offerTemplate.getSubscriptionRenewal()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.dto.catalog.ProductOfferingDto#isDisabled()
     */
    public boolean isDisabled() {
        return disabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.dto.catalog.ProductOfferingDto#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

   
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

   
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the bom code.
     *
     * @return the bom code
     */
    public String getBomCode() {
        return bomCode;
    }

    /**
     * Sets the bom code.
     *
     * @param bomCode the new bom code
     */
    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    /**
     * Gets the offer template category code.
     *
     * @return the offer template category code
     */
    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    /**
     * Sets the offer template category code.
     *
     * @param offerTemplateCategoryCode the new offer template category code
     */
    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

    /**
     * Gets the offer service templates.
     *
     * @return the offer service templates
     */
    public List<OfferServiceTemplateDto> getOfferServiceTemplates() {
        return offerServiceTemplates;
    }

    /**
     * Sets the offer service templates.
     *
     * @param offerServiceTemplates the new offer service templates
     */
    public void setOfferServiceTemplates(List<OfferServiceTemplateDto> offerServiceTemplates) {
        this.offerServiceTemplates = offerServiceTemplates;
    }

    /**
     * Gets the offer product templates.
     *
     * @return the offer product templates
     */
    public List<OfferProductTemplateDto> getOfferProductTemplates() {
        return offerProductTemplates;
    }

    /**
     * Sets the offer product templates.
     *
     * @param offerProductTemplates the new offer product templates
     */
    public void setOfferProductTemplates(List<OfferProductTemplateDto> offerProductTemplates) {
        this.offerProductTemplates = offerProductTemplates;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && StringUtils.isBlank(bomCode) && StringUtils.isBlank(offerTemplateCategoryCode)
                && (offerServiceTemplates == null || offerServiceTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }

    /**
     * Gets the renewal rule.
     *
     * @return the renewal rule
     */
    public SubscriptionRenewalDto getRenewalRule() {
        return renewalRule;
    }

    /**
     * Sets the renewal rule.
     *
     * @param renewalRule the new renewal rule
     */
    public void setRenewalRule(SubscriptionRenewalDto renewalRule) {
        this.renewalRule = renewalRule;
    }
    
    @Override
    public String toString() {
        return "OfferTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", longDescription=" + longDescription + ", disabled=" + disabled + ", bomCode="
                + bomCode + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + ", offerServiceTemplates=" + offerServiceTemplates + ", customFields=" + customFields
                + ", validFrom=" + validFrom + ", validTo=" + validTo + "]";
    }
}