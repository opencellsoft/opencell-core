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
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.OfferProductsDto;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplate;

/**
 * The Class OfferTemplateDto.
 *
 * @author Rachid.AITYAAZZA
 * @lastModifiedVersion 11.0
 */
@XmlRootElement(name = "CpqOfferDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CpqOfferDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9156372453581362595L;

    /** The bom code. */
    private String bomCode;

    /** The offer service templates. */
    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    private List<OfferServiceTemplateDto> offerServiceTemplates;
    
    /** The offer component. */
    @XmlElementWrapper(name = "offerComponents")
    @XmlElement(name = "offerComponent")
    private List<OfferProductsDto> offerProducts;

    /** The offer product templates. */
    @XmlElementWrapper(name = "allowedDiscountPlans")
    @XmlElement(name = "allowedDiscountPlans")
    private List<DiscountPlanDto> allowedDiscountPlans;
    
    /** The tags. */
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private List<TagDto> tags;
    
    /** The tags. */
    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attributes")
    private List<AttributeDTO> attributes;
    
    
    /** The valid from. */
    @XmlAttribute()
    protected Date validFrom;

    /** The valid to. */
    @XmlAttribute()
    protected Date validTo;

    /** The name. */
    @XmlElement(required = true)
    protected String name;

    /** The attachments. */
    @XmlElementWrapper(name = "digitalResources")
    @XmlElement(name = "digitalResource")
    protected List<DigitalResourceDto> attachments;


    /** The life cycle status. */
    @XmlElement(required = true)
    protected LifeCycleStatusEnum lifeCycleStatus = LifeCycleStatusEnum.IN_DESIGN;

    /** The custom fields. */
    protected CustomFieldsDto customFields;
    
    
    

    public CpqOfferDto(OfferTemplate entity) {
		super(entity);
		init(entity);
	}
    
     private void init(OfferTemplate entity) {
    	this.validFrom=entity.getValidity().getFrom();
		this.validTo=entity.getValidity().getTo();
		this.name=entity.getName();
		this.bomCode=entity.getBusinessOfferModel()!=null?entity.getBusinessOfferModel().getCode():null;
    }
    
    

	public CpqOfferDto(GetOfferTemplateResponseDto offerTemplatedto) {
		super();
		this.bomCode = offerTemplatedto.getBomCode();
		this.offerServiceTemplates = offerTemplatedto.getOfferServiceTemplates();
		this.offerProducts = offerTemplatedto.getOfferProducts();
		this.allowedDiscountPlans = offerTemplatedto.getAllowedDiscountPlans();
		this.tags = offerTemplatedto.getTags(); 
		this.validFrom = offerTemplatedto.getValidFrom();
		this.validTo = offerTemplatedto.getValidTo();
		this.name = offerTemplatedto.getName();
		this.attachments = offerTemplatedto.getAttachments();
		this.lifeCycleStatus = offerTemplatedto.getLifeCycleStatus();
		this.customFields = offerTemplatedto.getCustomFields();
		this.attributes=offerTemplatedto.getAttributes();
	}



	/**
     * Instantiates a new offer template dto.
     */
    public CpqOfferDto() {

    }

	/**
	 * @return the bomCode
	 */
	public String getBomCode() {
		return bomCode;
	}

	/**
	 * @param bomCode the bomCode to set
	 */
	public void setBomCode(String bomCode) {
		this.bomCode = bomCode;
	}

	/**
	 * @return the offerServiceTemplates
	 */
	public List<OfferServiceTemplateDto> getOfferServiceTemplates() {
		return offerServiceTemplates;
	}

	/**
	 * @param offerServiceTemplates the offerServiceTemplates to set
	 */
	public void setOfferServiceTemplates(List<OfferServiceTemplateDto> offerServiceTemplates) {
		this.offerServiceTemplates = offerServiceTemplates;
	}

	/**
	 * @return the offerProducts
	 */
	public List<OfferProductsDto> getOfferProducts() {
		return offerProducts;
	}

	/**
	 * @param offerProducts the offerProducts to set
	 */
	public void setOfferProducts(List<OfferProductsDto> offerProducts) {
		this.offerProducts = offerProducts;
	}

	/**
	 * @return the allowedDiscountPlans
	 */
	public List<DiscountPlanDto> getAllowedDiscountPlans() {
		return allowedDiscountPlans;
	}

	/**
	 * @param allowedDiscountPlans the allowedDiscountPlans to set
	 */
	public void setAllowedDiscountPlans(List<DiscountPlanDto> allowedDiscountPlans) {
		this.allowedDiscountPlans = allowedDiscountPlans;
	}

	/**
	 * @return the tags
	 */
	public List<TagDto> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<TagDto> tags) {
		this.tags = tags;
	}

	/**
	 * @return the validFrom
	 */
	public Date getValidFrom() {
		return validFrom;
	}

	/**
	 * @param validFrom the validFrom to set
	 */
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	/**
	 * @return the validTo
	 */
	public Date getValidTo() {
		return validTo;
	}

	/**
	 * @param validTo the validTo to set
	 */
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the attachments
	 */
	public List<DigitalResourceDto> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(List<DigitalResourceDto> attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the lifeCycleStatus
	 */
	public LifeCycleStatusEnum getLifeCycleStatus() {
		return lifeCycleStatus;
	}

	/**
	 * @param lifeCycleStatus the lifeCycleStatus to set
	 */
	public void setLifeCycleStatus(LifeCycleStatusEnum lifeCycleStatus) {
		this.lifeCycleStatus = lifeCycleStatus;
	}

	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	/**
	 * @return the attributes
	 */
	public List<AttributeDTO> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<AttributeDTO> attributes) {
		this.attributes = attributes;
	}

  
	
	
	
    
}