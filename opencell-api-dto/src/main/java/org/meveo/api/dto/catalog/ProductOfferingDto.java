package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;

@XmlRootElement(name = "ProductOffering")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductOfferingDto extends BusinessDto {

    private static final long serialVersionUID = 4599063410509766484L;

    @XmlAttribute(required = true)
    protected String code;

    @XmlAttribute()
    protected String description;

    @XmlAttribute()
    protected Date validFrom;

    @XmlAttribute()
    protected Date validTo;

    protected String name;

    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    protected List<OfferTemplateCategoryDto> offerTemplateCategories;

    @XmlElementWrapper(name = "digitalResources")
    @XmlElement(name = "digitalResource")
    protected List<DigitalResourcesDto> attachments;

    protected String modelCode;

    protected LifeCycleStatusEnum lifeCycleStatus;

    protected CustomFieldsDto customFields = new CustomFieldsDto();

    /**
     * This field is populated on find and list. Use to pull the image from a servlet later on.
     */
    protected String imagePath;
    protected String imageBase64;

    protected boolean disabled = false;

    public ProductOfferingDto() {
    }

    /**
     * Constructor
     * 
     * @param productOffering Product offering entity
     * @param customFieldsDto Custom fields DTO
     * @param asLink Convert to DTO with minimal information only - code and validity dates
     */
    public ProductOfferingDto(ProductOffering productOffering, CustomFieldsDto customFieldsDto, boolean asLink) {
        this.setCode(productOffering.getCode());
        if (productOffering.getValidityRaw() != null) {
            this.setValidFrom(productOffering.getValidityRaw().getFrom());
            this.setValidTo(productOffering.getValidityRaw().getTo());
        }
        
        if (asLink) {
            return;
        }
        this.setDescription(productOffering.getDescription());
        this.setName(productOffering.getName());
        this.setLifeCycleStatus(productOffering.getLifeCycleStatus());
        this.imagePath = productOffering.getImagePath();

        List<OfferTemplateCategory> offerTemplateCategories = productOffering.getOfferTemplateCategories();
        if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
            this.setOfferTemplateCategories(new ArrayList<OfferTemplateCategoryDto>());
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                this.getOfferTemplateCategories().add(new OfferTemplateCategoryDto(offerTemplateCategory));
            }
        }
        List<DigitalResource> attachments = productOffering.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            this.setAttachments(new ArrayList<DigitalResourcesDto>());
            for (DigitalResource digitalResource : attachments) {
                this.getAttachments().add(new DigitalResourcesDto(digitalResource));
            }
        }
        this.customFields = customFieldsDto;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OfferTemplateCategoryDto> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    public void setOfferTemplateCategories(List<OfferTemplateCategoryDto> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    public List<DigitalResourcesDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DigitalResourcesDto> attachments) {
        this.attachments = attachments;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public LifeCycleStatusEnum getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(LifeCycleStatusEnum lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}