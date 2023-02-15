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
import org.meveo.api.dto.tunnel.CustomStyleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.tunnel.CustomStyle;
import org.meveo.model.tunnel.TunnelCustomization;
import org.meveo.service.tunnel.CustomStyleService;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author mohamed CHAOUKI
 */
@Stateless
public class CustomStyleApi extends BaseCrudApi<CustomStyle, CustomStyleDto> {

    @Inject
    private CustomStyleService customStyleService;

    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto    DTO entity object to populate from
     * @param entity Entity to populate
     **/
    private void dtoToEntity(CustomStyleDto dto, CustomStyle entity) {

        entity.setCode(dto.getCode());
        if (dto.getLogo() != null) {
            entity.setLogo(dto.getLogo());
        }
        if (dto.getFavIcon() != null) {
            entity.setFavIcon(dto.getFavIcon());
        }
        if (dto.getFont() != null) {
            entity.setFont(dto.getFont());
        }
        if (dto.getBackgroundColor() != null) {
            entity.setBackgroundColor(dto.getBackgroundColor());
        }
        if (dto.getTextColor() != null) {
            entity.setTextColor(dto.getTextColor());
        }
        if (dto.getBackgroundImage() != null) {
            entity.setBackgroundImage(dto.getBackgroundImage());
        }
        if (dto.getPrimaryColor() != null) {
            entity.setPrimaryColor(dto.getPrimaryColor());
        }
        if (dto.getSecondaryColor() != null) {
            entity.setSecondaryColor(dto.getSecondaryColor());
        }
        if (dto.getCss() != null) {
            entity.setCss(dto.getCss());
        }
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
    }

    @Override
    public CustomStyle create(CustomStyleDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(CustomStyle.class.getName(), postData);
            postData.setCode(postData.getType().toString()+"_"+postData.getCode());
        }

        if (customStyleService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(TunnelCustomization.class, postData.getCode());
        }

        CustomStyle entity = new CustomStyle();

        dtoToEntity(postData, entity);

        customStyleService.create(entity);

        return entity;
    }

    @Override
    public CustomStyle update(CustomStyleDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        CustomStyle entity = customStyleService.findByCode(postData.getCode());
        if (entity == null) {
            throw new EntityDoesNotExistsException(TunnelCustomization.class, postData.getCode());
        }

        dtoToEntity(postData, entity);

        customStyleService.update(entity);

        return entity;
    }
}
