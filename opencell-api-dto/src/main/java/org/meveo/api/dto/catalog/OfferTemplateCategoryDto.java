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

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.catalog.OfferTemplateCategory;

/**
 * The Class OfferTemplateCategoryDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "OfferCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateCategoryDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    private String name;

    /** The offer template category code. */
    private String offerTemplateCategoryCode;

    /** The href. */
    private String href;

    /** The version. */
    private int version;

    /** The last modified. */
    private Date lastModified;

    /**
     * Is category active. A negative of Disabled. Deprecated in 5.0.1. Use Disabled field instead.
     * 
     */
    @Deprecated
    private Boolean active;

    /** The parent id. */
    private Long parentId;

    /** The image path. */
    private String imagePath;

    /** The image base 64. */
    private String imageBase64;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    private List<LanguageDescriptionDto> languageDescriptions;

    private List<LanguageDescriptionDto> languageLabels;


    /**
     * Instantiates a new offer template category dto.
     */
    public OfferTemplateCategoryDto() {

    }

    /**
     * Instantiates a new offer template category dto.
     *
     * @param code the code
     */
    public OfferTemplateCategoryDto(String code) {
        this.code = code;
    }

    /**
     * Instantiates a new offer template category dto.
     *
     * @param offerTemplateCategory the offer template category
     * @param customFieldInstances the custom field instances
     */
    public OfferTemplateCategoryDto(OfferTemplateCategory offerTemplateCategory, CustomFieldsDto customFieldInstances) {
        super(offerTemplateCategory);

        if (offerTemplateCategory != null) {
            this.setId(offerTemplateCategory.getId());
            this.setName(offerTemplateCategory.getName());
            this.setVersion(offerTemplateCategory.getVersion());
            this.setLastModified(offerTemplateCategory.getAuditable().getLastModified());
            this.setActive(offerTemplateCategory.isActive());
            this.imagePath = offerTemplateCategory.getImagePath();
            this.languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(offerTemplateCategory.getDescriptionI18n());
            this.languageLabels = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(offerTemplateCategory.getDescriptionI18n());

            customFields = customFieldInstances;

            OfferTemplateCategory parent = offerTemplateCategory.getOfferTemplateCategory();

            if (parent != null) {
                this.setOfferTemplateCategoryCode(parent.getCode());
                this.setParentId(parent.getId());
            }
        }
    }

    /**
     * Instantiates a new offer template category dto.
     *
     * @param offerTemplateCategory the offer template category
     * @param customFieldInstances the custom field instances
     * @param baseUri the base uri
     */
    public OfferTemplateCategoryDto(OfferTemplateCategory offerTemplateCategory, CustomFieldsDto customFieldInstances, String baseUri) {
        this(offerTemplateCategory, customFieldInstances);
        this.setHref(String.format("%scatalogManagement/category/%s", baseUri, this.getId()));
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
     * Gets the href.
     *
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the href.
     *
     * @param href the new href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Gets the last modified.
     *
     * @return the last modified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Sets the last modified.
     *
     * @param lastModified the new last modified
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public Boolean isActive() {

        return active;
    }

    /**
     * Sets the active.
     *
     * @param active the new active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets the parent id.
     *
     * @return the parent id
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * Sets the parent id.
     *
     * @param parentId the new parent id
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    public List<LanguageDescriptionDto> getLanguageLabels() {
        return languageLabels;
    }

    public void setLanguageLabels(List<LanguageDescriptionDto> languageLabels) {
        this.languageLabels = languageLabels;
    }

    @Override
    public String toString() {
        return "OfferTemplateCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", name=" + name + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + ", id=" + id + ", href=" + href
                + ", version=" + version + ", lastModified=" + lastModified + ", active=" + active + ", parentId=" + parentId + ", imagePath=" + imagePath + "]";
    }
}