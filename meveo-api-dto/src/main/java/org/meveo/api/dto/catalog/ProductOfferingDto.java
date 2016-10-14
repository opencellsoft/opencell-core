package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;

@XmlRootElement(name = "ProductOffering")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductOfferingDto extends BaseDto {

	private static final long serialVersionUID = 4599063410509766484L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	private String name;

	@XmlElementWrapper(name = "offerTemplateCategories")
	@XmlElement(name = "offerTemplateCategory")
	private List<OfferTemplateCategoryDto> offerTemplateCategories;

	@XmlElementWrapper(name = "digitalResources")
	@XmlElement(name = "digitalResource")
	private List<DigitalResourcesDto> attachments;

	private String modelCode;

	private Date validFrom;

	private Date validTo;

	private String imageValue;

	private LifeCycleStatusEnum lifeCycleStatus;

	private CustomFieldsDto customFields = new CustomFieldsDto();

	public ProductOfferingDto() {
	}

	public ProductOfferingDto(ProductOffering product, CustomFieldsDto customFieldsDto) {
		this.setCode(product.getCode());
		this.setDescription(product.getDescription());
		this.setName(product.getName());
		this.setValidFrom(product.getValidFrom());
		this.setValidTo(product.getValidTo());
		this.setLifeCycleStatus(product.getLifeCycleStatus());
		if (product.getImage() != null) {
			this.setImageValue(new String(product.getImageAsByteArr()));
		}
		List<OfferTemplateCategory> offerTemplateCategories = product.getOfferTemplateCategories();
		if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
			this.setOfferTemplateCategories(new ArrayList<OfferTemplateCategoryDto>());
			for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
				this.getOfferTemplateCategories().add(new OfferTemplateCategoryDto(offerTemplateCategory));
			}
		}
		List<DigitalResource> attachments = product.getAttachments();
		if (attachments != null && !attachments.isEmpty()) {
			this.setAttachments(new ArrayList<DigitalResourcesDto>());
			for (DigitalResource digitalResource : attachments) {
				this.getAttachments().add(new DigitalResourcesDto(digitalResource));
			}
		}
		this.customFields = customFieldsDto;
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

	public String getImageValue() {
		return imageValue;
	}

	public void setImageValue(String imageValue) {
		this.imageValue = imageValue;
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

}
