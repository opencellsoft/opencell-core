package org.meveo.api.dto.catalog;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.OfferTemplateCategory;

/**
 * The Class OfferTemplateCategoryDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "OfferCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateCategoryDto extends BusinessDto {

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

    /** The active. */
    private boolean active;

    /** The parent id. */
    private Long parentId;

    /** The image path. */
    private String imagePath;

    /** The image base 64. */
    private String imageBase64;

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
     */
    public OfferTemplateCategoryDto(OfferTemplateCategory offerTemplateCategory) {
        super(offerTemplateCategory);

        if (offerTemplateCategory != null) {
            this.setId(offerTemplateCategory.getId());
            this.setName(offerTemplateCategory.getName());
            this.setVersion(offerTemplateCategory.getVersion());
            this.setLastModified(offerTemplateCategory.getAuditable().getLastModified());
            this.setActive(offerTemplateCategory.isActive());
            this.imagePath = offerTemplateCategory.getImagePath();

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
     * @param baseUri the base uri
     */
    public OfferTemplateCategoryDto(OfferTemplateCategory offerTemplateCategory, String baseUri) {
        this(offerTemplateCategory);
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
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active the new active
     */
    public void setActive(boolean active) {
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
    
    @Override
    public String toString() {
        return "OfferTemplateCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", name=" + name + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode
                + ", id=" + id + ", href=" + href + ", version=" + version + ", lastModified=" + lastModified + ", active=" + active + ", parentId=" + parentId + ", imagePath="
                + imagePath + "]";
    }
}