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
import org.meveo.api.dto.tunnel.HypertextLinkDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.subscriptionTunnel.HypertextLink;
import org.meveo.model.subscriptionTunnel.HypertextSection;
import org.meveo.service.tunnel.HypertextLinkService;
import org.meveo.service.tunnel.HypertextSectionService;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class HypertextLinkApi extends BaseCrudApi<HypertextLink, HypertextLinkDto> {

    @Inject
    private HypertextSectionService sectionService;

    @Inject
    private HypertextLinkService linkService;

    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto    DTO entity object to populate from
     * @param entity Entity to populate
     **/
    public void dtoToEntity(HypertextLinkDto dto, HypertextLink entity) {
        entity.setCode(dto.getCode());
        if (dto.getLabel() != null) {
            entity.setLabel(convertMultiLanguageToMapOfValues(dto.getLabel(), null));
        }
        if (dto.getUrl() != null) {
            entity.setUrl(dto.getUrl());
        }
        if (dto.getIcon() != null) {
            entity.setIcon(dto.getIcon());
        }
        if (dto.getDisplayIcon() != null) {
            entity.setDisplayIcon(dto.getDisplayIcon());
        }
        if (dto.getHypertextSectionCode() != null) {
            HypertextSection section = sectionService.findByCode(dto.getHypertextSectionCode());
            entity.setHypertextSection(section);
        }
    }


    @Override
    public HypertextLink create(HypertextLinkDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(HypertextSection.class.getName(), postData);
        }

        if (linkService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(HypertextSection.class, postData.getCode());
        }

        HypertextLink entity = new HypertextLink();

        dtoToEntity(postData, entity);

        linkService.create(entity);

        return entity;
    }

    @Override
    public HypertextLink update(HypertextLinkDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }
}
