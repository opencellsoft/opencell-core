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

package org.meveo.api.dto.response.module;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class MeveoModuleDtosResponse.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Edward Legaspi(edward.legaspi@manaty.net)
 **/
@XmlRootElement(name = "MeveoModuleDtosResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoModuleDtosResponse extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The modules. */
    @XmlElementWrapper(name = "modules")
    @XmlElements({ 
                  @XmlElement(name = "businessServiceModel", type = BusinessServiceModelDto.class), 
                  @XmlElement(name = "businessOfferModel", type = BusinessOfferModelDto.class),
                  @XmlElement(name = "businessAccountModel", type = BusinessAccountModelDto.class), 
                  @XmlElement(name = "module", type = MeveoModuleDto.class) })
    private List<MeveoModuleDto> modules = new ArrayList<>();

    /**
     * Instantiates a new meveo module dtos response.
     */
    public MeveoModuleDtosResponse() {
        super();
    }

    /**
     * Gets the modules.
     *
     * @return the modules
     */
    public List<MeveoModuleDto> getModules() {
        if(modules == null) {
            return new ArrayList<>();
        }
        return modules;
    }

    /**
     * Sets the modules.
     *
     * @param modules the new modules
     */
    public void setModules(List<MeveoModuleDto> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "MeveoModuleDtosResponse [modules=" + modules + "]";
    }

}