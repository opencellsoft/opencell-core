package org.meveo.api.dto.catalog;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.OfferTemplateCategory;

@XmlRootElement(name = "OfferCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateCategoryDto extends BusinessDto {

    private static final long serialVersionUID = 1L;

    private String name;

    private String offerTemplateCategoryCode;

    private String href;

    private int version;

    private Date lastModified;

    private boolean active;

    private Long parentId;

    private String imagePath;
    private String imageBase64;

    public OfferTemplateCategoryDto() {

    }

    public OfferTemplateCategoryDto(String code) {
        this.code = code;
    }

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

    public OfferTemplateCategoryDto(OfferTemplateCategory offerTemplateCategory, String baseUri) {
        this(offerTemplateCategory);
        this.setHref(String.format("%scatalogManagement/category/%s", baseUri, this.getId()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

    @Override
    public String toString() {
        return "OfferTemplateCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", name=" + name + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode
                + ", id=" + id + ", href=" + href + ", version=" + version + ", lastModified=" + lastModified + ", active=" + active + ", parentId=" + parentId + ", imagePath="
                + imagePath + "]";
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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
}