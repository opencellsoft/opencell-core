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
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

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
    protected String groupedAttributeCode;
    
    /**
     * Corresponding to minimum one shot charge template code.
     */
    protected AttributeTypeEnum attributeType;
    
    /**
     * Corresponding to predefined allowed values
     */
    protected Set<String> allowedValues;
	  /**
     * Display
     */
    protected boolean display;
    /**
     * attribute order in the GUI
     */
    protected Integer sequence;
    
    /**
     * The lower number, the higher the priority is
     */
    protected Integer priority ;
    /**
     * Mandatory
     */
    @NotNull
    protected boolean mandatory=Boolean.FALSE;
    
    
    protected List<String> commercialRuleCodes=new ArrayList<String>();
    
   protected boolean selectable=Boolean.TRUE;
    
    protected boolean ruled=Boolean.FALSE;
    
    protected Object value;
    
    @XmlElementWrapper(name = "chargeTemplateCodes")
    @XmlElement(name = "chargeTemplateCodes") 
    private List<String> chargeTemplateCodes = new ArrayList<String>();
 
    
   
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
        groupedAttributeCode=attribute.getGroupedAttributes()!=null?attribute.getGroupedAttributes().getCode():null;
        display=attribute.isDisplay();
    }

 

	public String getGroupedAttributeCode() {
		return groupedAttributeCode;
	}

	public void setGroupedAttributeCode(String groupedAttributeCode) {
		this.groupedAttributeCode = groupedAttributeCode;
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
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}



	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
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


 



	 

	
	


    
}