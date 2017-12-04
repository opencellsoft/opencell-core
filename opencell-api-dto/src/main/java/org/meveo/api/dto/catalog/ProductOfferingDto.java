package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.Collections;
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
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;

@XmlRootElement(name = "ProductOffering")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductOfferingDto extends BusinessDto {

    private static final long serialVersionUID = 4599063410509766484L;

    @XmlAttribute()
    protected Date validFrom;

    @XmlAttribute()
    protected Date validTo;

    @XmlElement(required = true)
    protected String name;

    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    protected List<OfferTemplateCategoryDto> offerTemplateCategories;

    @XmlElement
    private List<ChannelDto> channels;

    @XmlElementWrapper(name = "digitalResources")
    @XmlElement(name = "digitalResource")
    protected List<DigitalResourcesDto> attachments;

    protected String modelCode;

    protected LifeCycleStatusEnum lifeCycleStatus;

    protected CustomFieldsDto customFields;

    /**
     * This field is populated on find and list. Use to pull the image from a servlet later on.
     */
    protected String imagePath;
    protected String imageBase64;

    protected boolean disabled = false;

    protected List<LanguageDescriptionDto> languageDescriptions;

    protected String longDescription;

    protected List<LanguageDescriptionDto> longDescriptionsTranslated;
    
    private String globalRatingScriptInstance;

    @XmlElementWrapper(name = "sellers")
    @XmlElement(name = "seller")
    private List<String> sellers;

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
        super(productOffering);

        if (productOffering.getValidity() != null) {
            this.setValidFrom(productOffering.getValidity().getFrom());
            this.setValidTo(productOffering.getValidity().getTo());
        }

        if (asLink) {
            this.setDescription(null);
            return;
        }
        this.setDescription(productOffering.getDescription());
        this.setName(productOffering.getName());
        this.setLifeCycleStatus(productOffering.getLifeCycleStatus());
        this.imagePath = productOffering.getImagePath();
        this.disabled = productOffering.isDisabled();

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

        if (productOffering.getChannels() != null && !productOffering.getChannels().isEmpty()) {
            this.channels = new ArrayList<>();
            for (Channel channel : productOffering.getChannels()) {
                this.channels.add(new ChannelDto(channel));
            }
        }
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(productOffering.getDescriptionI18n()));
        setLongDescription(productOffering.getLongDescription());
        setLongDescriptionsTranslated(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(productOffering.getLongDescriptionI18n()));

        if(productOffering.getGlobalRatingScriptInstance() != null) {
            setGlobalRatingScriptInstance(productOffering.getGlobalRatingScriptInstance().getCode());
        }        

        if (productOffering.getSellers() != null && !productOffering.getSellers().isEmpty()) {
            this.sellers = new ArrayList<>();
            for (Seller seller : productOffering.getSellers()) {
                this.sellers.add(seller.getCode());
            }
            Collections.sort(this.sellers);
        }

        this.customFields = customFieldsDto;
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

    public List<ChannelDto> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelDto> channels) {
        this.channels = channels;
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<LanguageDescriptionDto> getLongDescriptionsTranslated() {
        return longDescriptionsTranslated;
    }

    public void setLongDescriptionsTranslated(List<LanguageDescriptionDto> longDescriptionsTranslated) {
        this.longDescriptionsTranslated = longDescriptionsTranslated;
    }

    /**
     * @return the globalRatingScriptInstance
     */
    public String getGlobalRatingScriptInstance() {
        return globalRatingScriptInstance;
    }

    /**
     * @param globalRatingScriptInstance the globalRatingScriptInstance to set
     */
    public void setGlobalRatingScriptInstance(String globalRatingScriptInstance) {
        this.globalRatingScriptInstance = globalRatingScriptInstance;
    }
    
    public List<String> getSellers() {
        return sellers;
    }

    public void setSellers(List<String> sellers) {
        this.sellers = sellers;
    }
}