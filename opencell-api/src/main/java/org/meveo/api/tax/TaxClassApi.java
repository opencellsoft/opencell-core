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

package org.meveo.api.tax;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.tax.TaxClassListResponseDto;
import org.meveo.api.dto.tax.TaxClassDto;
import org.meveo.api.exception.*;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.tax.TaxClassService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.function.BiFunction;

/**
 * CRUD API for {@link TaxClass} - Tax class
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class TaxClassApi extends BaseCrudApi<TaxClass, TaxClassDto> {

    @Inject
    private TaxClassService entityService;

    /**
     * Creates a new TaxClass entity
     * 
     * @param dto Posted Tax class data to API
     * 
     * @throws MeveoApiException Api exception
     * @throws BusinessException General business exception.
     */
    @Override
    public TaxClass create(TaxClassDto dto) throws MeveoApiException, BusinessException {

        String code = dto.getCode();

        if (StringUtils.isBlank(code)) {
            addGenericCodeIfAssociated(TaxClass.class.getName(), dto);
        }

        handleMissingParametersAndValidate(dto);

        TaxClass entity = entityService.findByCode(code);

        if (entity != null) {
            throw new EntityAlreadyExistsException(TaxClass.class, code);
        }

        entity = new TaxClass();

        dtoToEntity(entity, dto);
        entityService.create(entity);

        return entity;
    }

    public TaxClassListResponseDto list(PagingAndFiltering pagingAndFiltering) {
        TaxClassListResponseDto result = new TaxClassListResponseDto();
        result.setPaging( pagingAndFiltering );

        List<TaxClass> taxClasses = entityService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (taxClasses != null) {
            for (TaxClass taxClass : taxClasses) {
                result.getDtos().add(new TaxClassDto(taxClass,
                        entityToDtoConverter.getCustomFieldsDTO(taxClass, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        return result;
    }

    /**
     * Updates a Tax class based on code)
     * 
     * @param dto Posted Tax class data to API
     * 
     * @throws MeveoApiException API exception
     * @throws BusinessException business exception.
     */
    @Override
    public TaxClass update(TaxClassDto dto) throws MeveoApiException, BusinessException {

        String code = dto.getCode();
        Long id = dto.getId();

        if (StringUtils.isBlank(code) && id == null) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(dto);

        TaxClass entity = null;
        if (!StringUtils.isBlank(code)) {
            entity = entityService.findByCode(code);
        } else {
            entity = entityService.findById(id);
        }
        if (entity == null) {
            throw new EntityDoesNotExistsException(TaxClass.class, !StringUtils.isBlank(code) ? code : id.toString());
        }

        if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            if (entityService.findByCode(dto.getUpdatedCode()) != null) {
                throw new EntityAlreadyExistsException(TaxClass.class, dto.getUpdatedCode());
            }
        }

        dtoToEntity(entity, dto);

        entity = entityService.update(entity);
        return entity;
    }

    @Override
    protected BiFunction<TaxClass, CustomFieldsDto, TaxClassDto> getEntityToDtoFunction() {
        return TaxClassDto::new;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param entity Entity to populate
     * @param dto DTO entity object to populate from
     **/
    private void dtoToEntity(TaxClass entity, TaxClassDto dto) {

        boolean isNew = entity.getId() == null;
        if (isNew) {
            entity.setCode(dto.getCode());
        } else if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            entity.setCode(dto.getUpdatedCode());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(StringUtils.isEmpty(dto.getDescription()) ? null : dto.getDescription());
        }
        if (dto.getDescriptionI18n() != null) {
            entity.setDescriptionI18n(convertMultiLanguageToMapOfValues(dto.getDescriptionI18n(), entity.getDescriptionI18n()));
        }

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), entity, isNew, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }
}