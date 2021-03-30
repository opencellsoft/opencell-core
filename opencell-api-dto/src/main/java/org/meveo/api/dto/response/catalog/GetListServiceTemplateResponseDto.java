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

import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GetListServiceTemplateResponseDto.
 * 
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */
@XmlRootElement(name = "GetListServiceTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListServiceTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6452175083213220603L;

    /** The list product template. */
    private List<ServiceTemplateDto> listServiceTemplate = new ArrayList<>();

    /**
     * Gets the service template list.
     *
     * @return the list service template
     */
    public List<ServiceTemplateDto> getListServiceTemplate() {
        return listServiceTemplate;
    }

    /**
     * Sets the list product template.
     *
     * @param listServiceTemplate the new list service template
     */
    public void setListServiceTemplate(List<ServiceTemplateDto> listServiceTemplate) {
        this.listServiceTemplate = listServiceTemplate;
    }

    public void addServiceTemplate(ServiceTemplateDto serviceTemplateDto) {
        if (listServiceTemplate == null) {
            listServiceTemplate = new ArrayList<>();
        }
        listServiceTemplate.add(serviceTemplateDto);
    }

}