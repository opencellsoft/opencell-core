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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.QuoteAttribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO to create or update a quote
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteAttributeDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteAttributeDTO extends BaseEntityDto{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;
	
	private Long quoteAttributeId;
    
    private String quoteAttributeCode;

    private List<QuoteAttributeDTO> linkedQuoteAttribute = new ArrayList<>();
    
    private String stringValue;

	private Double doubleValue;

	private Date dateValue;

    private CustomFieldsDto customFields;

	/**
	 * @return the quoteAttributeCode
	 */
	public String getQuoteAttributeCode() {
		return quoteAttributeCode;
	}
	
	public QuoteAttributeDTO() {
		super();
	}

	public QuoteAttributeDTO(QuoteAttribute quoteAttribue) {
		super();
		quoteAttributeId=quoteAttribue.getId();
		quoteAttributeCode=quoteAttribue.getAttribute().getCode();
		stringValue =quoteAttribue.getStringValue();
		dateValue =quoteAttribue.getDateValue();
		doubleValue =quoteAttribue.getDoubleValue();
	}

	/**
	 * @param quoteAttributeCode the quoteAttributeCode to set
	 */
	public void setQuoteAttributeCode(String quoteAttributeCode) {
		this.quoteAttributeCode = quoteAttributeCode;
	}

	/**
	 * @return the quoteAttributeId
	 */
	public Long getQuoteAttributeId() {
		return quoteAttributeId;
	}

	/**
	 * @param quoteAttributeId the quoteAttributeId to set
	 */
	public void setQuoteAttributeId(Long quoteAttributeId) {
		this.quoteAttributeId = quoteAttributeId;
	}


	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public List<QuoteAttributeDTO> getLinkedQuoteAttribute() {
		return linkedQuoteAttribute;
	}

	public void setLinkedQuoteAttribute(List<QuoteAttributeDTO> linkedQuoteAttribute) {
		this.linkedQuoteAttribute = linkedQuoteAttribute;
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
}