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

package org.meveo.model.catalog;

import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.annotation.ImageType;

/**
 * Product offer category
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@ModuleItem
@Cacheable
@CustomFieldEntity(cftCodePrefix = "OfferTemplateCategory")
@ExportIdentifier({ "code" })
@Table(name = "cat_offer_template_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_offer_template_category_seq"), })
public class OfferTemplateCategory extends EnableBusinessCFEntity implements Comparable<OfferTemplateCategory>, IImageUpload {

    private static final long serialVersionUID = -5088201294684394309L;

    /**
     * Category name
     */
    @Column(name = "name", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String name;

    /**
     * Parent category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_template_category_id")
    private OfferTemplateCategory offerTemplateCategory;

    /**
     * Child categories
     */
    @OneToMany(mappedBy = "offerTemplateCategory", cascade = CascadeType.REMOVE)
    private List<OfferTemplateCategory> children;

    /**
     * Product offerings
     */
    @ManyToMany(mappedBy = "offerTemplateCategories")
    private List<ProductOffering> productOffering;

    /**
     * Ordering index
     */
    @Column(name = "level")
    private int orderLevel = 1;

    /**
     * Category logo/image path
     */
    @ImageType
    @Column(name = "image_path", length = 100)
    @Size(max = 100)
    private String imagePath;

    /**
     * Parent category code
     */
    @Transient
    private String parentCategoryCode;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescriptionOrCode() {
        if (!StringUtils.isBlank(description)) {
            return description;
        } else if (!(StringUtils.isBlank(name))) {
            return name;
        } else {
            return code;
        }
    }

    public OfferTemplateCategory getOfferTemplateCategory() {
        return offerTemplateCategory;
    }

    public void setOfferTemplateCategory(OfferTemplateCategory offerTemplateCategory) {
        this.offerTemplateCategory = offerTemplateCategory;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int level) {
        this.orderLevel = level;
    }

    public List<OfferTemplateCategory> getChildren() {
        return children;
    }

    public void setChildren(List<OfferTemplateCategory> children) {
        this.children = children;
    }

    public List<ProductOffering> getProductOffering() {
        return productOffering;
    }

    public void setProductOffering(List<ProductOffering> productOffering) {
        this.productOffering = productOffering;
    }

    @Override
    public int compareTo(OfferTemplateCategory o) {
        return o.orderLevel - this.orderLevel;
    }

    /**
     * Check if offer category or any of its subcategories are assigned to any product offering
     * 
     * @return True if offer category or any of its subcategories are assigned to any product offering
     */
    public boolean isAssignedToProductOffering() {
        if (getProductOffering() != null && !getProductOffering().isEmpty()) {
            return true;
        }

        if (getChildren() != null) {
            for (OfferTemplateCategory childCategory : getChildren()) {
                if (childCategory.isAssignedToProductOffering()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getParentCategoryCode() {
        return parentCategoryCode;
    }

    public void setParentCategoryCode(String parentCategoryCode) {
        this.parentCategoryCode = parentCategoryCode;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.description);
        } else {
            return this.description;
        }
    }

    @Override
    public String toString() {
        return "OfferTemplateCategory [name=" + name + ", offerTemplateCategory=" + offerTemplateCategory + ", parentCategoryCode=" + parentCategoryCode + ", code=" + code
                + ", description=" + description + "]";
    }

    public void updateFromImport(OfferTemplateCategory iCat) {
        if (!StringUtils.isBlank(iCat.getName())) {
            this.setName(iCat.getName());
        }
        if (!StringUtils.isBlank(iCat.getDescription())) {
            this.setDescription(iCat.getDescription());
        }
    }

    @PostLoad
    public void initParentCategoryCode() {
        if (getOfferTemplateCategory() != null) {
            setParentCategoryCode(getOfferTemplateCategory().getCode());
        }
    }

}