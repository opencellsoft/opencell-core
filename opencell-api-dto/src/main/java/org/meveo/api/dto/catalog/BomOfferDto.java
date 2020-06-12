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
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.model.catalog.LifeCycleStatusEnum;

/**
 * The Class BomOfferDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BomOffer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomOfferDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4557706201829891403L;

    /** The bom code. */
    @NotNull
    @XmlAttribute(required = true)
    private java.lang.String bomCode;

    /** The code. */
    @NotNull
    @XmlAttribute(required = true)
    private java.lang.String code;

    /** The name. */
    @NotNull
    @XmlAttribute(required = true)
    private java.lang.String name;

    /** The description. */
    @XmlAttribute
    private java.lang.String description;

    /** The custom fields. */
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;

    /** The prefix. */
    @Deprecated
    private java.lang.String prefix;

    /** The services to activate. */
    @XmlElementWrapper(name = "servicesToActivate")
    @XmlElement(name = "serviceToActivate")
    private List<ServiceConfigurationDto> servicesToActivate = new ArrayList<>();

    /** The products to activate. */
    @XmlElementWrapper(name = "productsToActivate")
    @XmlElement(name = "productToActivate")
    private List<ServiceConfigurationDto> productsToActivate = new ArrayList<>();

    /** The business service models. */
    @XmlElementWrapper(name = "businessServiceModels")
    @XmlElement(name = "businessServiceModel")
    private List<BSMConfigurationDto> businessServiceModels = new ArrayList<>();

    /** The life cycle status enum. */
    private LifeCycleStatusEnum lifeCycleStatusEnum;

    /** The offer template categories. */
    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    private List<OfferTemplateCategoryDto> offerTemplateCategories;

    /** The image base64 encoding string. */
    private java.lang.String imageBase64;
    
    /** The image path. */
    private java.lang.String imagePath;

    /** The valid from. */
    @XmlAttribute()
    protected Date validFrom;

    /** The valid to. */
    @XmlAttribute()
    protected Date validTo;

    /** The renewal rule. */
    private SubscriptionRenewalDto renewalRule;

    /** The long description. */
    protected java.lang.String longDescription;

    /** The long descriptions translated. */
    protected List<LanguageDescriptionDto> longDescriptionsTranslated;

    /** The channels. */
    @XmlElementWrapper(name = "channels")
    private List<String> channels;

    /** The sellers. */
    @XmlElementWrapper(name = "sellers")
    private List<String> sellers;

    @XmlElementWrapper(name = "customerCategories")
    private List<String> customerCategories;

    /**
     * Gets the bom code.
     *
     * @return the bom code
     */
    public java.lang.String getBomCode() {
        return bomCode;
    }

    /**
     * Sets the bom code.
     *
     * @param bomCode the new bom code
     */
    public void setBomCode(java.lang.String bomCode) {
        this.bomCode = bomCode;
    }

    /**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public java.lang.String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix the new prefix
     */
    public void setPrefix(java.lang.String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public java.lang.String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    /**
     * Gets the services to activate.
     *
     * @return the services to activate
     */
    public List<ServiceConfigurationDto> getServicesToActivate() {
        return servicesToActivate;
    }

    /**
     * Sets the services to activate.
     *
     * @param servicesToActivate the new services to activate
     */
    public void setServicesToActivate(List<ServiceConfigurationDto> servicesToActivate) {
        this.servicesToActivate = servicesToActivate;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public java.lang.String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the products to activate.
     *
     * @return the products to activate
     */
    public List<ServiceConfigurationDto> getProductsToActivate() {
        return productsToActivate;
    }

    /**
     * Sets the products to activate.
     *
     * @param productsToActivate the new products to activate
     */
    public void setProductsToActivate(List<ServiceConfigurationDto> productsToActivate) {
        this.productsToActivate = productsToActivate;
    }

    /**
     * Gets the business service models.
     *
     * @return the business service models
     */
    public List<BSMConfigurationDto> getBusinessServiceModels() {
        return businessServiceModels;
    }

    /**
     * Sets the business service models.
     *
     * @param businessServiceModels the new business service models
     */
    public void setBusinessServiceModels(List<BSMConfigurationDto> businessServiceModels) {
        this.businessServiceModels = businessServiceModels;
    }

    /**
     * Gets the life cycle status enum.
     *
     * @return the lifeCycleStatusEnum
     */
    public LifeCycleStatusEnum getLifeCycleStatusEnum() {
        return lifeCycleStatusEnum;
    }

    /**
     * Sets the life cycle status enum.
     *
     * @param lifeCycleStatusEnum the lifeCycleStatusEnum to set
     */
    public void setLifeCycleStatusEnum(LifeCycleStatusEnum lifeCycleStatusEnum) {
        this.lifeCycleStatusEnum = lifeCycleStatusEnum;
    }

    /**
     * Gets the offer template categories.
     *
     * @return the offerTemplateCategories
     */
    public List<OfferTemplateCategoryDto> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    /**
     * Sets the offer template categories.
     *
     * @param offerTemplateCategories the offerTemplateCategories to set
     */
    public void setOfferTemplateCategories(List<OfferTemplateCategoryDto> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    
    /**
     * Gets the image Base64 encoding string.
     *
     * @return the image Base64 encoding string
     */
    public java.lang.String getImageBase64() {
        return imageBase64;
    }
    
    /**
     * Sets the image Base64 encoding string.
     *
     * @param imageBase64 the image Base64 encoding string
     */
    public void setImageBase64(java.lang.String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    
    /**
     * Gets the image path.
     *
     * @return the image path
     */
    public java.lang.String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path.
     *
     * @param imagePath the new image path
     */
    public void setImagePath(java.lang.String imagePath) {
        this.imagePath = imagePath;
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

    /**
     * Gets the channels.
     *
     * @return the channels
     */
    public List<java.lang.String> getChannels() {
        return channels;
    }

    /**
     * Sets the channels.
     *
     * @param channels the new channels
     */
    public void setChannels(List<java.lang.String> channels) {
        this.channels = channels;
    }


    /**
     * Gets the long description.
     *
     * @return the long description
     */
    public java.lang.String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the long description.
     *
     * @param longDescription the new long description
     */
    public void setLongDescription(java.lang.String longDescription) {
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

    /**
     * Gets the sellers.
     *
     * @return the sellers
     */
    public List<java.lang.String> getSellers() {
        return sellers;
    }

    /**
     * Sets the sellers.
     *
     * @param sellers the new sellers
     */
    public void setSellers(List<java.lang.String> sellers) {
        this.sellers = sellers;
    }

    public List<String> getCustomerCategories() {
        return customerCategories;
    }

    public void setCustomerCategories(List<String> customerCategories) {
        this.customerCategories = customerCategories;
    }

    @Override
    public java.lang.String toString() {
        return "BomOfferDto [bomCode=" + bomCode + ", code=" + code + ", name=" + name + ", description=" + description + ", customFields=" + customFields + ", prefix=" + prefix
                + ", servicesToActivate=" + servicesToActivate + ", productsToActivate=" + productsToActivate + ", businessServiceModels=" + businessServiceModels
                + ", lifeCycleStatusEnum=" + lifeCycleStatusEnum + ", offerTemplateCategories=" + offerTemplateCategories + "]";
    }

}