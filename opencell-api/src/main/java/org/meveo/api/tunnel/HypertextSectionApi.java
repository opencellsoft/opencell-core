package org.meveo.api.tunnel;/*
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


import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.tunnel.HypertextSectionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.tunnel.CustomStyle;
import org.meveo.model.tunnel.HypertextSection;
import org.meveo.model.tunnel.TunnelCustomization;
import org.meveo.service.tunnel.CustomStyleService;
import org.meveo.service.tunnel.HypertextSectionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class HypertextSectionApi extends BaseCrudApi<HypertextSection, HypertextSectionDto> {

    @Inject
    private CustomStyleService customStyleService;

    @Inject
    private HypertextSectionService sectionService;


    @Override
    public HypertextSection create(HypertextSectionDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(HypertextSection.class.getName(), postData);
        }

        if (sectionService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(HypertextSection.class, postData.getCode());
        }

        HypertextSection entity = new HypertextSection();

        dtoToEntity(postData, entity);

        sectionService.create(entity);

        return entity;
    }

    @Override
    public HypertextSection update(HypertextSectionDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        HypertextSection entity = sectionService.findByCode(postData.getCode());
        if (entity == null) {
            throw new EntityDoesNotExistsException(TunnelCustomization.class, postData.getCode());
        }

        dtoToEntity(postData, entity);

        sectionService.update(entity);

        return entity;
    }

    public void createOrUpdate(List<HypertextSectionDto> postData) {
        for (HypertextSectionDto sectionDto: postData) {
            HypertextSection entity = sectionService.findByCode(sectionDto.getCode());
            if (entity == null) {
                create(sectionDto);
            } else {
                update(sectionDto);
            }
        }
    }


    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto    DTO entity object to populate from
     * @param entity Entity to populate
     **/
    private void dtoToEntity(HypertextSectionDto dto, HypertextSection entity) {
        entity.setCode(dto.getCode());
        if (dto.getLabel() != null) {
            entity.setLabel(convertMultiLanguageToMapOfValues(dto.getLabel(), null));
        }
        if (dto.getCustomStyleCode() != null) {
            CustomStyle customStyle = customStyleService.findByCode(dto.getCustomStyleCode());
            if (customStyle == null) {
                throw new EntityDoesNotExistsException(CustomStyle.class, dto.getCustomStyleCode());
            }
            entity.setCustomStyle(customStyle);
        }
    }
}
