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

package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.ServiceDTO;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListServiceResponseDto.
 * 
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "GetListServiceResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListServiceResponseDto extends SearchResponse {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4317478215685305319L;
	
   

    /** The list product template. */
    private List<ServiceDTO> services;

    /**
     * Gets the service template list.
     *
     * @return the list service template
     */
    public List<ServiceDTO> getServices() {
        return services;
    }

    /**
     * Sets the list product template.
     *
     * @param listServiceTemplate the new list service template
     */
    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }

    public void addServiceTemplate(ServiceDTO service) {
        if (service == null) {
        	services = new ArrayList<>();
        }
        services.add(service);
    }

}