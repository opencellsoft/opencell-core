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

package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EntityCustomizationDto;

/**
 * The Class EntityCustomizationResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "EntityCustomizationResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomizationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1871967200014440842L;

    /** The entity customization. */
    private EntityCustomizationDto entityCustomization;

    /**
     * Gets the entity customization.
     *
     * @return the entity customization
     */
    public EntityCustomizationDto getEntityCustomization() {
        return entityCustomization;
    }

    /**
     * Sets the entity customization.
     *
     * @param entityCustomization the new entity customization
     */
    public void setEntityCustomization(EntityCustomizationDto entityCustomization) {
        this.entityCustomization = entityCustomization;
    }
}