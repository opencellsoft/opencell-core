/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.IVersionedDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;

/**
 * The Class ProductOfferingDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "ProductOffering")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductOfferingDto extends EnableBusinessDto implements IVersionedDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4599063410509766484L;

    /** The valid from. */
    @XmlAttribute()
    protected Date validFrom;

    /** The valid to. */
    @XmlAttribute()
    protected Date validTo;
    
    /** The new valid from. */
    @XmlAttribute()
    protected Date newValidFrom;

    /** The new valid to. */
    @XmlAttribute()
    protected Date newValidTo;


    /** The name. */
    @XmlElement(required = true)
    protected String name;

    /** The offer template categories. */
    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    protected List<OfferTemplateCategoryDto> offerTemplateCategories;

    /** The channels. */
    @XmlElementWrapper(name = "channels")
    @XmlElement(name = "channel")
    private List<ChannelDto> channels;

    /** The attachments. */
    @XmlElementWrapper(name = "digitalResources")
    @XmlElement(name = "digitalResource")
    protected List<DigitalResourceDto> attachments;

    /** The model code. */
    protected String modelCode;

    /** The life cycle status. */
    @XmlElement(required = true)
    protected LifeCycleStatusEnum lifeCycleStatus = LifeCycleStatusEnum.IN_DESIGN;

    /** The custom fields. */
    protected CustomFieldsDto customFields;

    /**
     * This field is populated on find and list. Use to pull the image from a servlet later on.
     */
    protected String imagePath;

    /** The image base 64. */
    protected String imageBase64;

    /** The language descriptions. */
    protected List<LanguageDescriptionDto> languageDescriptions;

    /** The long description. */
    protected String longDescription;

    /** The long descriptions translated. */
    protected List<LanguageDescriptionDto> longDescriptionsTranslated;

    /** The global rating script instance. */
    private String globalRatingScriptInstance;

    /** The sellers. */
    @XmlElementWrapper(name = "sellers")
    @XmlElement(name = "seller")
    private List<String> sellers;

    @XmlElementWrapper(name = "customerCategories")
    @XmlElement(name = "customerCategory")
    private List<CustomerCategoryDto> customerCategories;

    /**
     * Instantiates a new product offering dto.
     */
    public ProductOfferingDto() {
    }

    /**
     * Constructor.
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
            this.setDisabled(null);
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
                this.getOfferTemplateCategories().add(new OfferTemplateCategoryDto(offerTemplateCategory, null));
            }
        }
        List<DigitalResource> digitalResources = productOffering.getAttachments();
        if (digitalResources != null && !digitalResources.isEmpty()) {
            this.setAttachments(new ArrayList<DigitalResourceDto>());
            for (DigitalResource digitalResource : digitalResources) {
                this.getAttachments().add(new DigitalResourceDto(digitalResource));
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

        if (productOffering.getGlobalRatingScriptInstance() != null) {
            setGlobalRatingScriptInstance(productOffering.getGlobalRatingScriptInstance().getCode());
        }

        if (productOffering.getSellers() != null && !productOffering.getSellers().isEmpty()) {
            this.sellers = new ArrayList<>();
            for (Seller seller : productOffering.getSellers()) {
                this.sellers.add(seller.getCode());
            }
            Collections.sort(this.sellers);
        }

        if (productOffering.getChannels() != null && !productOffering.getChannels().isEmpty()) {
            setChannels(productOffering.getChannels().stream().map(p -> {
                return new ChannelDto(p);
            }).collect(Collectors.toList()));
        }

        if (productOffering.getCustomerCategories() != null && !productOffering.getCustomerCategories().isEmpty()) {
            setCustomerCategories(productOffering.getCustomerCategories().stream().map(p -> {
                return new CustomerCategoryDto(p);
            }).collect(Collectors.toList()));
        }

        this.customFields = customFieldsDto;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the offer template categories.
     *
     * @return the offer template categories
     */
    public List<OfferTemplateCategoryDto> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    /**
     * Sets the offer template categories.
     *
     * @param offerTemplateCategories the new offer template categories
     */
    public void setOfferTemplateCategories(List<OfferTemplateCategoryDto> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    /**
     * Gets the attachments.
     *
     * @return the attachments
     */
    public List<DigitalResourceDto> getAttachments() {
        return attachments;
    }

    /**
     * Sets the attachments.
     *
     * @param attachments the new attachments
     */
    public void setAttachments(List<DigitalResourceDto> attachments) {
        this.attachments = attachments;
    }

    /**
     * Gets the model code.
     *
     * @return the model code
     */
    public String getModelCode() {
        return modelCode;
    }

    /**
     * Sets the model code.
     *
     * @param modelCode the new model code
     */
    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    @Override
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public Date getValidTo() {
        return validTo;
    }

    @Override
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Gets the life cycle status.
     *
     * @return the life cycle status
     */
    public LifeCycleStatusEnum getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    /**
     * Sets the life cycle status.
     *
     * @param lifeCycleStatus the new life cycle status
     */
    public void setLifeCycleStatus(LifeCycleStatusEnum lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
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
     * Gets the image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path.
     *
     * @param imagePath the new image path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the image base 64.
     *
     * @return the image base 64
     */
    public String getImageBase64() {
        return imageBase64;
    }

    /**
     * Sets the image base 64.
     *
     * @param imageBase64 the new image base 64
     */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    /**
     * Gets the channels.
     *
     * @return the channels
     */
    public List<ChannelDto> getChannels() {
        return channels;
    }

    /**
     * Sets the channels.
     *
     * @param channels the new channels
     */
    public void setChannels(List<ChannelDto> channels) {
        this.channels = channels;
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
     * Gets the long description.
     *
     * @return the long description
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the long description.
     *
     * @param longDescription the new long description
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Gets the long descriptions translated.
     *
     * @return the long descriptions translated
     */
    public List<LanguageDescriptionDto> getLongDescriptionsTranslated() {
        return longDescriptionsTranslated;
    }

    /**
     * Sets the long descriptions translated.
     *
     * @param longDescriptionsTranslated the new long descriptions translated
     */
    public void setLongDescriptionsTranslated(List<LanguageDescriptionDto> longDescriptionsTranslated) {
        this.longDescriptionsTranslated = longDescriptionsTranslated;
    }

    /**
     * Gets the global rating script instance.
     *
     * @return the globalRatingScriptInstance
     */
    public String getGlobalRatingScriptInstance() {
        return globalRatingScriptInstance;
    }

    /**
     * Sets the global rating script instance.
     *
     * @param globalRatingScriptInstance the globalRatingScriptInstance to set
     */
    public void setGlobalRatingScriptInstance(String globalRatingScriptInstance) {
        this.globalRatingScriptInstance = globalRatingScriptInstance;
    }

    /**
     * Gets the sellers.
     *
     * @return the sellers
     */
    public List<String> getSellers() {
        return sellers;
    }

    /**
     * Sets the sellers.
     *
     * @param sellers the new sellers
     */
    public void setSellers(List<String> sellers) {
        this.sellers = sellers;
    }

    public List<CustomerCategoryDto> getCustomerCategories() {
        return customerCategories;
    }

    public void setCustomerCategories(List<CustomerCategoryDto> customerCategories) {
        this.customerCategories = customerCategories;
    }

    /**
     * @return the newValidFrom
     */
    public Date getNewValidFrom() {
        return newValidFrom;
    }

    /**
     * @param newValidFrom the newValidFrom to set
     */
    public void setNewValidFrom(Date newValidFrom) {
        this.newValidFrom = newValidFrom;
    }

    /**
     * @return the newValidTo
     */
    public Date getNewValidTo() {
        return newValidTo;
    }

    /**
     * @param newValidTo the newValidTo to set
     */
    public void setNewValidTo(Date newValidTo) {
        this.newValidTo = newValidTo;
    }
}