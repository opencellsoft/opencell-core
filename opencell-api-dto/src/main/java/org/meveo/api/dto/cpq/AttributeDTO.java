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

package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class ServiceDto.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.00
 */
@XmlRootElement(name = "AttributeDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeDTO extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    protected static final long serialVersionUID = -6794700715161690227L;

    /**
     * Corresponding to minimum one shot charge template code.
     */
    @NotNull
    @Schema(description = "Corresponding to minimum one shot charge template code",
    		example = "possible value are : INFO, LIST_TEXT, LIST_MULTIPLE_TEXT, LIST_NUMERIC, "
    				+ "LIST_MULTIPLE_NUMERIC, TEXT, NUMERIC, INTEGER, DATE, CALENDAR, EMAIL, PHONE, TOTAL, COUNT, EXPRESSION_LANGUAGE")
    protected AttributeTypeEnum attributeType;
    
    /**
     * Corresponding to predefined allowed values
     */
    @Schema(description = "Corresponding to predefined allowed values")
    protected Set<String> allowedValues;
	  /**
     * Display
     */
    @Schema(description = "diplay the attribute")
    protected boolean display;
    /**
     * attribute order in the GUI
     */
    @Schema(description = "attribute order in the GUI")
    protected Integer sequence;
    
    /**
     * The lower number, the higher the priority is
     */
    @Schema(description = "The lower number, the higher the priority is")
    protected Integer priority ;
    /**
     * Mandatory
     */
    @NotNull
    @Schema(description = "indicate if the attribute is mandatory")
    protected boolean mandatory=Boolean.FALSE;

    @Schema(description = "indicate if the attribute is selectable")
   protected boolean selectable=Boolean.TRUE;

    @Schema(description = "indicate if the attribute is ruled")
    protected boolean ruled=Boolean.FALSE;
    
    @XmlElementWrapper(name = "chargeTemplateCodes")
    @XmlElement(name = "chargeTemplateCodes") 
    @Schema(description = "list of charge template code", example = "chargeTemplateCodes : [CODE_1, CODE_2,..]")
    private List<String> chargeTemplateCodes = new ArrayList<String>();
 
    @XmlElementWrapper(name = "commercialRuleCodes")
    @XmlElement(name = "commercialRuleCodes") 
    @Schema(description = "list of commercial rule code", example = "commercialRuleCodes : [CODE_1, CODE_2,..]")
    protected List<String> commercialRuleCodes=new ArrayList<String>();
     
    /** The media codes. */
    @XmlElementWrapper(name = "mediaCodes")
    @XmlElement(name = "mediaCodes")
    @Schema(description = "list of media code", example = "mediaCodes : [CODE_1, CODE_2,..]")
    protected Set<String> mediaCodes = new HashSet<String>();
    
    
    /** The tags */
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    @Schema(description = "list of tag code", example = "tags : [CODE_1, CODE_2,..]")
    protected List<String> tagCodes=new ArrayList<String>();
    
    @XmlElementWrapper(name = "assignedAttributeCodes")
    @XmlElement(name = "assignedAttributeCodes")
    @Schema(description = "list of assigned attribute code", example = "assignedAttributeCodes : [CODE_1, CODE_2,..]")
    private List<String> assignedAttributeCodes=new ArrayList<String>();

    @Schema(description = "number of decimal for attribute if the type of attribute is a NUMBER")
    private Integer unitNbDecimal = BaseEntity.NB_DECIMALS;

    @Schema(description = "indicate if the attribute is read only")
    protected boolean readOnly = Boolean.FALSE;
    

    @Schema(description = "list of custom field associated to attribute")
    protected CustomFieldsDto customFields;

    @Schema(description = "default value for attribute")
    protected String defaultValue;
    
    public AttributeDTO() {
    }

 

    /**
     * Instantiates a new service template dto.
     *
     * @param serviceTemplate the service template
     */
    public AttributeDTO(Attribute attribute) {
        super(attribute);
        mandatory=attribute.isMandatory();
        sequence=attribute.getSequence();
        priority=attribute.getPriority();
        allowedValues=attribute.getAllowedValues();
        attributeType=attribute.getAttributeType();
        display=attribute.isDisplay();
        code=attribute.getCode();
        description=attribute.getDescription();
        id=attribute.getId();
        mandatory=attribute.isMandatory();
        unitNbDecimal = attribute.getUnitNbDecimal(); 
        readOnly = attribute.getReadOnly();
        defaultValue = attribute.getDefaultValue();
        if (attribute.getAssignedAttributes()!=null) {
        	for (Attribute attr:attribute.getAssignedAttributes()) {
        		assignedAttributeCodes.add(attr.getCode());
        	}
        }
        
        
    }

	public AttributeTypeEnum getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeTypeEnum serviceType) {
		this.attributeType = serviceType;
	}



	/**
	 * @return the allowedValues
	 */
	public Set<String> getAllowedValues() {
		return allowedValues;
	}



	/**
	 * @param allowedValues the allowedValues to set
	 */
	public void setAllowedValues(Set<String> allowedValues) {
		this.allowedValues = allowedValues;
	}



	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}



	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}



	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}



	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}



	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}



	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}



	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}



	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}


	/**
	 * @return the commercialRuleCodes
	 */
	public List<String> getCommercialRuleCodes() {
		return commercialRuleCodes;
	}



	/**
	 * @param commercialRuleCodes the commercialRuleCodes to set
	 */
	public void setCommercialRuleCodes(List<String> commercialRuleCodes) {
		this.commercialRuleCodes = commercialRuleCodes;
	}



	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}



	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}



	/**
	 * @return the ruled
	 */
	public boolean isRuled() {
		return ruled;
	}



	/**
	 * @param ruled the ruled to set
	 */
	public void setRuled(boolean ruled) {
		this.ruled = ruled;
	}






	/**
	 * @return the chargeTemplateCodes
	 */
	public List<String> getChargeTemplateCodes() {
		return chargeTemplateCodes;
	}



	/**
	 * @param chargeTemplateCodes the chargeTemplateCodes to set
	 */
	public void setChargeTemplateCodes(List<String> chargeTemplateCodes) {
		this.chargeTemplateCodes = chargeTemplateCodes;
	}

	


	/**
	 * @return the mediaCodes
	 */
	public Set<String> getMediaCodes() {
		return mediaCodes;
	}



	/**
	 * @param mediaCodes the mediaCodes to set
	 */
	public void setMediaCodes(Set<String> mediaCodes) {
		this.mediaCodes = mediaCodes;
	}



	/**
	 * @return the tagCodes
	 */
	public List<String> getTagCodes() {
		return tagCodes;
	}



	/**
	 * @param tagCodes the tagCodes to set
	 */
	public void setTagCodes(List<String> tagCodes) {
		this.tagCodes = tagCodes;
	}






	/**
	 * @return the assignedAttributeCodes
	 */
	public List<String> getAssignedAttributeCodes() {
		return assignedAttributeCodes;
	}



	/**
	 * @param assignedAttributeCodes the assignedAttributeCodes to set
	 */
	public void setAssignedAttributeCodes(List<String> assignedAttributeCodes) {
		this.assignedAttributeCodes = assignedAttributeCodes;
	}



	/**
	 * @return the unitNbDecimal
	 */
	public Integer getUnitNbDecimal() {
		return unitNbDecimal;
	}



	/**
	 * @param unitNbDecimal the unitNbDecimal to set
	 */
	public void setUnitNbDecimal(Integer unitNbDecimal) {
		this.unitNbDecimal = unitNbDecimal;
	}



	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}



	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}



	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


 
	


	
 



	 

	
	


    
}