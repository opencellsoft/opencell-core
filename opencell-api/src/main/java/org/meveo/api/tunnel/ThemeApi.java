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

package org.meveo.api.tunnel;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.tunnel.ThemeDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.subscriptionTunnel.Theme;

import javax.ejb.Stateless;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class ThemeApi extends BaseCrudApi<Theme, ThemeDto> {

    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto DTO entity object to populate from
     * @param entity Entity to populate
     **/
    public void dtoToEntity(ThemeDto dto, Theme entity) {

    }

    @Override
    public Theme create(ThemeDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }

    @Override
    public Theme update(ThemeDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }

    public ThemeDto findById(Long id) {

        if (StringUtils.isBlank(id)) {
            missingParameters.add("id");
            handleMissingParameters();
        }

        ThemeDto themeDto = find(id);
        if (themeDto == null) {
            throw new EntityDoesNotExistsException(ThemeDto.class, id);
        }

        return themeDto;
    }
}
