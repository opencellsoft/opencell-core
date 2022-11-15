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
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.subscriptionTunnel.CustomStyle;
import org.meveo.model.subscriptionTunnel.Theme;
import org.meveo.model.subscriptionTunnel.TunnelCustomization;
import org.meveo.service.tunnel.ThemeService;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class ThemeApi extends BaseCrudApi<Theme, ThemeDto> {

    @Inject
    private ThemeService themeService;

    @Inject
    CustomStyleApi customStyleApi;

    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto    DTO entity object to populate from
     * @param entity Entity to populate
     **/
    private void dtoToEntity(ThemeDto dto, Theme entity) {

        entity.setCode(dto.getCode());
        if (dto.getBody() != null) {
            entity.setBody(customStyleApi.create(dto.getBody()));
        }
        if (dto.getHeader() != null) {
            entity.setHeader(customStyleApi.create(dto.getHeader()));
        }
        if (dto.getFooter() != null) {
            entity.setFooter(customStyleApi.create(dto.getFooter()));
        }
        if (dto.getCreatedOn() != null) {
            entity.setCreatedOn(dto.getCreatedOn());
        }
    }

    @Override
    public Theme create(ThemeDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(Theme.class.getName(), postData);
        }

        if (themeService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Theme.class, postData.getCode());
        }

        Theme entity = new Theme();

        dtoToEntity(postData, entity);
        themeService.create(entity);

        return entity;
    }

    @Override
    public Theme update(ThemeDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Theme theme = themeService.findByCode(postData.getCode());
        if (theme == null) {
            throw new EntityDoesNotExistsException(Theme.class, postData.getCode());
        }

        dtoToEntity(postData, theme);

        themeService.update(theme);

        return theme;
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
