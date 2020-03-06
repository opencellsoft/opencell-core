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

package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.module.MeveoModule;

/**
 * The Class BusinessAccountModelDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BusinessAccountModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessAccountModelDto extends MeveoModuleDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2264963153183287690L;

    /** The hierarchy type. */
    private AccountHierarchyTypeEnum hierarchyType;

    /**
     * Instantiates a new business account model dto.
     */
    public BusinessAccountModelDto() {

    }

    /**
     * Instantiates a new business account model dto.
     *
     * @param meveoModule the MeveoModule entity
     */
    public BusinessAccountModelDto(MeveoModule meveoModule) {
        super(meveoModule);
        if (meveoModule instanceof BusinessAccountModel) {
            BusinessAccountModel businessAccountModel = (BusinessAccountModel) meveoModule;
            this.hierarchyType = businessAccountModel.getHierarchyType();
        }
    }

    /**
     * Gets the hierarchy type.
     *
     * @return the hierarchy type
     */
    public AccountHierarchyTypeEnum getHierarchyType() {
        return hierarchyType;
    }

    /**
     * Sets the hierarchy type.
     *
     * @param hierarchyType the new hierarchy type
     */
    public void setHierarchyType(AccountHierarchyTypeEnum hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

}
