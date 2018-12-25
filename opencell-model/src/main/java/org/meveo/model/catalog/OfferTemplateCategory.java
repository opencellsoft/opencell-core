package org.meveo.model.catalog;

import java.util.List;

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
 **/
@Entity
@ObservableEntity
@ModuleItem
@Cacheable
@CustomFieldEntity(cftCodePrefix = "OFFER_CATEGORY")
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