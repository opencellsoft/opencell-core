package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto extends BaseDto {

    private static final long serialVersionUID = 9156372453581362595L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    private String name;

    private String longDescription;

    private boolean disabled = false;
    private String bomCode;

    @Deprecated
    private String offerTemplateCategoryCode;

    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    private List<String> offerTemplateCategories;

    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    private List<OfferServiceTemplateDto> offerServiceTemplates;

    @XmlElementWrapper(name = "offerProductTemplates")
    @XmlElement(name = "offerProductTemplate")
    private List<OfferProductTemplateDto> offerProductTemplates;

    private CustomFieldsDto customFields = new CustomFieldsDto();

    public OfferTemplateDto() {

    }

    public OfferTemplateDto(OfferTemplate offerTemplate, CustomFieldsDto customFieldInstances) {
        code = offerTemplate.getCode();
        description = offerTemplate.getDescription();
        name = offerTemplate.getName();
        longDescription = offerTemplate.getLongDescription();
        disabled = offerTemplate.isDisabled();
        if (offerTemplate.getBusinessOfferModel() != null) {
            bomCode = offerTemplate.getBusinessOfferModel().getCode();
        }

        if (offerTemplate.getOfferTemplateCategories() != null) {
            for (OfferTemplateCategory oc : offerTemplate.getOfferTemplateCategories()) {
                offerTemplateCategories = new ArrayList<>();
                offerTemplateCategories.add(oc.getCode());
            }
        }

        if (offerTemplate.getOfferServiceTemplates() != null && offerTemplate.getOfferServiceTemplates().size() > 0) {
            offerServiceTemplates = new ArrayList<>();
            for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
                offerServiceTemplates.add(new OfferServiceTemplateDto(st));
            }
        }
        customFields = customFieldInstances;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "OfferTemplateDto [code=" + code + ", description=" + description + ", longDescription=" + longDescription + ", disabled=" + disabled + ", bomCode=" + bomCode
                + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + ", offerServiceTemplates=" + offerServiceTemplates + ", customFields=" + customFields + "]";
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
        return StringUtils.isBlank(description) && StringUtils.isBlank(bomCode) && StringUtils.isBlank(offerTemplateCategoryCode)
                && (offerServiceTemplates == null || offerServiceTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    public void setOfferTemplateCategories(List<String> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}