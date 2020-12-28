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

package org.meveo.api.dto.response.catalog;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.model.catalog.OfferTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class GetOfferTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 * @author Mbarek-Ay
 */
@XmlRootElement(name = "GetOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"tagCodes"})
public class GetOfferTemplateResponseDto extends OfferTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8776189890084137788L;

    /** The offer template. */
    private OfferTemplateDto offerTemplate;
    
	@XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private List<TagDto> tags;

    /**
     * Gets the offer template.
     *
     * @return the offer template
     */
    public OfferTemplateDto getOfferTemplate() {
        return offerTemplate;
    }

    /**
     * Sets the offer template.
     *
     * @param offerTemplate the new offer template
     */
    public void setOfferTemplate(OfferTemplateDto offerTemplate) {
        this.offerTemplate = offerTemplate;
    }
    
    public GetOfferTemplateResponseDto(OfferTemplate offerTemplate, CustomFieldsDto customFieldsDto, boolean asLink, boolean loadTags) {
    	super(offerTemplate, customFieldsDto, asLink);
    	if(loadTags) { 
    		if(offerTemplate.getTags() != null && !offerTemplate.getTags().isEmpty()) { 
    			tags = offerTemplate.getTags().stream().map(t -> {
    				final TagDto dto = new TagDto(t);
    				return dto;
    			}).collect(Collectors.toList());
    		}  
    		
    		
    	} 
    }

 

	public GetOfferTemplateResponseDto() { 
	}

	
	 /**
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();

    /**
 

    /**
     * Instantiates a new base response.
     *
     * @param status the status
     * @param errorCode the error code
     * @param message the message
     */
    public GetOfferTemplateResponseDto(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        actionStatus = new ActionStatus(status, errorCode, message);
    }
 
    

	/**
	 * @return the actionStatus
	 */
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
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

	@Override
    public String toString() {
        return "GetOfferTemplateResponse [offerTemplate=" + offerTemplate + ", toString()=" + super.toString() + "]";
    }
}