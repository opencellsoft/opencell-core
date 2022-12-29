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
import org.meveo.model.tunnel.*;
import org.meveo.service.tunnel.CustomStyleService;
import org.meveo.service.tunnel.HypertextSectionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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
            String code = "SC_"+ new Date().getTime();
            postData.setCode(code);
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

    public void delete(String sectionCode) {
        if (sectionCode == null)
            missingParameters.add("code");
        handleMissingParameters();

        HypertextSection section = sectionService.findByCode(sectionCode);
        if (section == null) {
            throw new EntityDoesNotExistsException(HypertextSection.class, sectionCode);
        }

        sectionService.remove(section.getId());
    }

    public void deleteMany(List<String> sectionCodes) {

        List<HypertextSection> sections = sectionService.findByCodes(sectionCodes);
        sectionService.remove(sections.stream().map(HypertextSection::getId).collect(Collectors.toSet()));
    }

    public List<HypertextSection> createOrUpdate(List<HypertextSectionDto> postData) {
        List<HypertextSection>  sections = new ArrayList<>();

        List<String> stylesCodes = postData.stream().map(HypertextSectionDto::getCustomStyleCode)
                .collect(Collectors.toList());

        List<CustomStyle> allStyles = customStyleService.findByCodes(stylesCodes);
        List<String> sectionsCodes = allStyles.stream().flatMap(cs -> cs.getHypertextSections().stream())
                .collect(Collectors.toList())
                .stream().map(HypertextSection::getCode).collect(Collectors.toList());
        List<String> postdataCodes = postData.stream().map(HypertextSectionDto::getCode).collect(Collectors.toList());

        List<String> toDelete = sectionsCodes.stream()
                .filter(element -> !postdataCodes.contains(element))
                .collect(Collectors.toList());

        for (String sc: toDelete) {
            delete(sc);
        }

        for (HypertextSectionDto sectionDto: postData) {
            HypertextSection entity = sectionService.findByCode(sectionDto.getCode());

            if (entity == null) {
                entity = create(sectionDto);
            } else {
                entity = update(sectionDto);
            }
            entity.getCustomStyle().setHypertextSections(null);
            entity.setLinks(new ArrayList<>());
            sections.add(entity);
        }

        return sections;
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
