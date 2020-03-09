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

package org.meveo.api.dto.response.account;

import org.meveo.api.dto.account.ParentEntitiesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ParentEntitiesResponseDto.
 *
 * @author Tony Alejandro.
 */
public class ParentEntitiesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The parent entities. */
    private ParentEntitiesDto parentEntities;

    /**
     * Gets the parent entities.
     *
     * @return the parent entities
     */
    public ParentEntitiesDto getParentEntities() {
        return parentEntities;
    }

    /**
     * Sets the parent entities.
     *
     * @param parentEntities the new parent entities
     */
    public void setParentEntities(ParentEntitiesDto parentEntities) {
        this.parentEntities = parentEntities;
    }

    @Override
    public String toString() {
        return "ParentEntitiesResponseDto [parentEntities=" + parentEntities + ", toString()=" + super.toString() + "]";
    }
}