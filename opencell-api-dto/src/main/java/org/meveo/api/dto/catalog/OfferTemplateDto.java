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
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0.1
 **/
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto extends ProductOfferingDto {

    private static final long serialVersionUID = 9156372453581362595L;

    private String bomCode;

    @Deprecated
    private String offerTemplateCategoryCode;

    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    private List<OfferServiceTemplateDto> offerServiceTemplates;

    @XmlElementWrapper(name = "offerProductTemplates")
    @XmlElement(name = "offerProductTemplate")
    private List<OfferProductTemplateDto> offerProductTemplates;

    private SubscriptionRenewalDto renewalRule;
    
    private String minimumAmountEl;
    
    private String minimumLabelEl;

    public OfferTemplateDto() {

    }

    /**
     * Constructor
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "OfferTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", longDescription=" + longDescription + ", disabled=" + disabled + ", bomCode="
                + bomCode + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + ", offerServiceTemplates=" + offerServiceTemplates + ", customFields=" + customFields
                + ", validFrom=" + validFrom + ", validTo=" + validTo + "]";
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getBomCode() {
        return bomCode;
    }

    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

    public List<OfferServiceTemplateDto> getOfferServiceTemplates() {
        return offerServiceTemplates;
    }

    public void setOfferServiceTemplates(List<OfferServiceTemplateDto> offerServiceTemplates) {
        this.offerServiceTemplates = offerServiceTemplates;
    }

    public List<OfferProductTemplateDto> getOfferProductTemplates() {
        return offerProductTemplates;
    }

    public void setOfferProductTemplates(List<OfferProductTemplateDto> offerProductTemplates) {
        this.offerProductTemplates = offerProductTemplates;
    }

    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && StringUtils.isBlank(bomCode) && StringUtils.isBlank(offerTemplateCategoryCode)
                && (offerServiceTemplates == null || offerServiceTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }

    public SubscriptionRenewalDto getRenewalRule() {
        return renewalRule;
    }

    public void setRenewalRule(SubscriptionRenewalDto renewalRule) {
        this.renewalRule = renewalRule;
    }

    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

}