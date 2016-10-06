package org.meveo.model.catalog;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "OFFER_CATEGORY")
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_OFFER_TEMPLATE_CATEGORY", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_TEMPLATE_CATEGORY_SEQ")
public class OfferTemplateCategory extends BusinessCFEntity implements Comparable<OfferTemplateCategory> {

    private static final long serialVersionUID = -5088201294684394309L;

    @Column(name = "NAME", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String name;

    @Column(name = "IMAGE")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private Blob image;

    @Column(name = "IMAGE_CONTENT_TYPE", length = 50)
    @Size(max = 50)
    private String imageContentType;

    @ManyToOne
    @JoinColumn(name = "OFFER_TEMPLATE_CATEGORY_ID")
    private OfferTemplateCategory offerTemplateCategory;

    @OneToMany(mappedBy = "offerTemplateCategory", cascade = CascadeType.REMOVE)
    private List<OfferTemplateCategory> children;

    @ManyToMany(mappedBy = "offerTemplateCategories")
    private List<ProductOffering> productOffering;

    @Column(name = "LEVEL")
    private int orderLevel = 1;

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

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
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

    public byte[] getImageAsByteArr() {
        if (image != null) {
            int blobLength;
            try {
                blobLength = (int) image.length();
                byte[] blobAsBytes = image.getBytes(1, blobLength);

                return blobAsBytes;
            } catch (SQLException e) {
                return null;
            }
        }

        return null;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
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

        for (OfferTemplateCategory childCategory : getChildren()) {
            if (childCategory.isAssignedToProductOffering()) {
                return true;
            }
        }
        return false;
    }
}