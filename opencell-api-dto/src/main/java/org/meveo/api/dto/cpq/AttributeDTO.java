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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.ServiceTypeEnum;

/**
 * The Class ServiceDto.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.00
 */
@XmlRootElement(name = "ServiceDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeDTO extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6794700715161690227L;


  
    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String groupedServiceCode;
    
    /**
     * Corresponding to minimum one shot charge template code.
     */
    private ServiceTypeEnum serviceType;
    
    /**
     * Corresponding to predefined allowed values
     */
    private List<Object> values;
    
    
    /**
     * Corresponding to the value validator (regex expression)
     */
    private String valueValidator;

    
    
    /**
     * Instantiates a new service template dto.
     */
    public AttributeDTO() {
    }

 

    /**
     * Instantiates a new service template dto.
     *
     * @param serviceTemplate the service template
     */
    public AttributeDTO(Attribute attribute) {
        super(attribute);
    }

 

	public String getGroupedServiceCode() {
		return groupedServiceCode;
	}

	public void setGroupedServiceCode(String groupedServiceCode) {
		this.groupedServiceCode = groupedServiceCode;
	}

	public ServiceTypeEnum getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceTypeEnum serviceType) {
		this.serviceType = serviceType;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	public String getValueValidator() {
		return valueValidator;
	}

	public void setValueValidator(String valueValidator) {
		this.valueValidator = valueValidator;
	}
    
}