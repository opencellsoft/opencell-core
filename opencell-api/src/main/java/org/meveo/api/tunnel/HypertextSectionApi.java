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
import org.meveo.api.dto.tunnel.HypertextSectionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.subscriptionTunnel.CustomStyle;
import org.meveo.model.subscriptionTunnel.HypertextLink;
import org.meveo.model.subscriptionTunnel.HypertextSection;
import org.meveo.service.tunnel.CustomStyleService;
import org.meveo.service.tunnel.HypertextLinkService;
import org.meveo.service.tunnel.HypertextSectionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
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

    @Inject
    private HypertextLinkService linkService;

    @Inject
    private HypertextLinkApi linkApi;


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

        List<HypertextLink> links = new ArrayList<>();
        for (HypertextLinkDto link: postData.getLinks()) {
            HypertextLink l = null;
            if (linkService.findByCode(link.getCode()) != null) {
                l = linkApi.update(link);
            } else {
                l = linkApi.create(link);
            }
            links.add(l);
        }

        entity.setLinks(links);
        sectionService.create(entity);

        return entity;
    }

    @Override
    public HypertextSection update(HypertextSectionDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }


    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto    DTO entity object to populate from
     * @param entity Entity to populate
     **/
    public void dtoToEntity(HypertextSectionDto dto, HypertextSection entity) {
        entity.setCode(dto.getCode());
        if (dto.getLabel() != null) {
            entity.setLabel(convertMultiLanguageToMapOfValues(dto.getLabel(), null));
        }
        if (dto.getCustomStyleCode() != null) {
            CustomStyle customStyle = customStyleService.findByCode(dto.getCustomStyleCode());
            entity.setCustomStyle(customStyle);
        }
    }
}
