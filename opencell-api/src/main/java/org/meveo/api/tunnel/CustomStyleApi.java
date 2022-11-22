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
import org.meveo.api.dto.tunnel.HypertextSectionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.subscriptionTunnel.CustomStyle;
import org.meveo.model.subscriptionTunnel.HypertextSection;
import org.meveo.model.subscriptionTunnel.TunnelCustomization;
import org.meveo.service.tunnel.CustomStyleService;
import org.meveo.service.tunnel.HypertextSectionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mohamed CHAOUKI
 */
@Stateless
public class CustomStyleApi extends BaseCrudApi<CustomStyle, CustomStyleDto> {

    @Inject
    private CustomStyleService customStyleService;

    @Inject
    private HypertextSectionService sectionService;

    @Inject
    private HypertextSectionApi sectionApi;

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
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
        /*if (dto.getHypertextSections() != null) {
            HypertextSection section = new HypertextSection();
            for (HypertextSectionDto sectionDto : dto.getHypertextSections()) {
                sectionApi.dtoToEntity(sectionDto, section);
                entity.getHypertextSections().add(section);
            }

            for (HypertextSectionDto sectionDto: dto.getHypertextSections()) {
                entity.getHypertextSections().add(sectionService.findByCode(sectionDto.getCode()));
            }
        }*/

    }

    @Override
    public CustomStyle create(CustomStyleDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(CustomStyle.class.getName(), postData);
        }

        if (customStyleService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(TunnelCustomization.class, postData.getCode());
        }

        CustomStyle entity = new CustomStyle();

        dtoToEntity(postData, entity);

        List<HypertextSection> sections = new ArrayList<>();
        if (postData.getHypertextSections() != null) {
            for (HypertextSectionDto section: postData.getHypertextSections()) {
                HypertextSection s = null;
                if (sectionService.findByCode(section.getCode()) != null) {
                    s = sectionApi.update(section);
                } else {
                    s = sectionApi.create(section);
                }
                sections.add(s);
            }
        }


        entity.setHypertextSections(sections);
        customStyleService.create(entity);

        return entity;
    }

    @Override
    public CustomStyle update(CustomStyleDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }
}
