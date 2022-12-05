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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetServiceTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetServiceTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetServiceTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2517820224350375764L;

    /** The service template. */
    private ServiceTemplateDto serviceTemplate;

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public ServiceTemplateDto getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(ServiceTemplateDto serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    @Override
    public String toString() {
        return "GetServiceTemplateResponse [serviceTemplate=" + serviceTemplate + ", toString()=" + super.toString() + "]";
    }
}