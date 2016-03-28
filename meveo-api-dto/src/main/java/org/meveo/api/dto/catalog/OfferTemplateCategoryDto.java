package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OfferTemplateCategory;

@XmlRootElement(name = "OfferCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateCategoryDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required = true)
	private String code;
	
	@XmlAttribute()
	private String description;
	
	private String name;
	
	private String imageByteValue;
	
	private String offerTemplateCategoryCode;
	
	private Long id;
	
	private String href;
	
	private int version;
	
	private Date lastModified; 
	
	private boolean active;
	
	private Long parentId;
	
	public OfferTemplateCategoryDto() {
		
	}
	
	public OfferTemplateCategoryDto (OfferTemplateCategory offerTemplateCategory) {
		
		if (offerTemplateCategory != null) {
			this.setCode(offerTemplateCategory.getCode());
			this.setDescription(offerTemplateCategory.getDescription());
			this.setId(offerTemplateCategory.getId());
			this.setName(offerTemplateCategory.getName());
			this.setVersion(offerTemplateCategory.getVersion());
			this.setLastModified(offerTemplateCategory.getAuditable().getLastModified());
			this.setActive(offerTemplateCategory.isActive());
			
			if (offerTemplateCategory.getImage() != null) {
				this.setImageByteValue(new String(offerTemplateCategory.getImageAsByteArr()));
			}
			
			OfferTemplateCategory parent = offerTemplateCategory.getOfferTemplateCategory();
			
			if (parent != null) {
				this.setOfferTemplateCategoryCode(parent.getCode());
				this.setParentId(parent.getId());
			}
		}
		
	}
	
	public OfferTemplateCategoryDto (OfferTemplateCategory offerTemplateCategory, String baseUri) {
		this(offerTemplateCategory);
		this.setHref(String.format("%scatalogManagement/category/%s", baseUri, this.getId()));
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

	public String getOfferTemplateCategoryCode() {
		return offerTemplateCategoryCode;
	}

	public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
		this.offerTemplateCategoryCode = offerTemplateCategoryCode;
	}

	public String getImageByteValue() {
		return imageByteValue;
	}

	public void setImageByteValue(String imageByteValue) {
		this.imageByteValue = imageByteValue;
	}

	@Override
	public String toString() {
		return "OfferTemplateCategoryDto [code=" + code + ", description="
				+ description + ", name=" + name + ", imageByteValue="
				+ imageByteValue + ", offerTemplateCategoryCode="
				+ offerTemplateCategoryCode + ", id=" + id + ", href=" + href
				+ ", version=" + version + ", lastModified=" + lastModified
				+ "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}