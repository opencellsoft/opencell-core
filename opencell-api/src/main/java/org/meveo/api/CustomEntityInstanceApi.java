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

package org.meveo.api;

import static org.meveo.service.base.NativePersistenceService.FIELD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.primefaces.model.SortOrder;

/**
 * @author Andrius Karpavicius
 **/
@Stateless
public class CustomEntityInstanceApi extends BaseApi {

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    public void create(CustomEntityInstanceDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCetCode())) {
            missingParameters.add("cetCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCetCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCetCode());
        }

        if (!currentUser.hasRole(CustomEntityTemplate.getModifyPermission(dto.getCetCode())) && !currentUser.hasRole("ModifyAllCE")) {
            throw new ActionForbiddenException("User does not have permission '" + CustomEntityTemplate.getModifyPermission(dto.getCetCode()) + "'");
        }

        if (customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityInstance.class, dto.getCode());
        }

        CustomEntityInstance cei = convertFromDTO(dto, null);

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), cei, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        customEntityInstanceService.create(cei);
    }

    public void update(CustomEntityInstanceDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCetCode())) {
            missingParameters.add("cetCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCetCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCetCode());
        }

        if (!currentUser.hasRole(CustomEntityTemplate.getModifyPermission(dto.getCetCode())) && !currentUser.hasRole("ModifyAllCE")) {
            throw new ActionForbiddenException("User does not have permission '" + CustomEntityTemplate.getModifyPermission(dto.getCetCode()) + "'");
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode());
        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, dto.getCode());
        }

        cei = convertFromDTO(dto, cei);

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), cei, false);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        cei = customEntityInstanceService.update(cei);
    }

    public void remove(String cetCode, String code) throws EntityDoesNotExistsException, MissingParameterException, MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        if (!currentUser.hasRole(CustomEntityTemplate.getModifyPermission(cetCode)) && !currentUser.hasRole("ModifyAllCE")) {
            throw new ActionForbiddenException("User does not have permission '" + CustomEntityTemplate.getModifyPermission(cetCode) + "'");
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code);
        if (cei != null) {
            customEntityInstanceService.remove(cei);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, code);
        }
    }

    public CustomEntityInstanceDto find(String cetCode, String code) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        if (!currentUser.hasRole(CustomEntityTemplate.getReadPermission(cetCode)) && !currentUser.hasRole("ReadAllCE")) {
            throw new ActionForbiddenException("User does not have permission '" + CustomEntityTemplate.getReadPermission(cetCode) + "'");
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code);

        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        return new CustomEntityInstanceDto(cei, entityToDtoConverter.getCustomFieldsDTO(cei, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }

    public CustomEntityInstancesResponseDto list(String cetCode, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        if (!currentUser.hasRole(CustomEntityTemplate.getReadPermission(cetCode)) && !currentUser.hasRole("ReadAllCE")) {
            throw new ActionForbiddenException("User does not have permission '" + CustomEntityTemplate.getReadPermission(cetCode) + "'");
        }

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        CustomEntityInstancesResponseDto result = new CustomEntityInstancesResponseDto();
        result.setPaging(pagingAndFiltering);

        PaginationConfiguration paginationConfig = toPaginationConfiguration(FIELD_ID, SortOrder.ASCENDING, null, pagingAndFiltering, cetCode);
        pagingAndFiltering.getFilters().put("cetCode", cetCode);

        Long totalCount = customEntityInstanceService.count(paginationConfig);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        
        if (totalCount > 0) {
            List<CustomEntityInstance> customEntityInstances = customEntityInstanceService.list(paginationConfig);
            List<CustomEntityInstanceDto> customEntityInstanceDtos = new ArrayList<>();

            for (CustomEntityInstance instance : customEntityInstances) {
                customEntityInstanceDtos.add(new CustomEntityInstanceDto(instance, entityToDtoConverter.getCustomFieldsDTO(instance, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
            result.setCustomEntityInstances(customEntityInstanceDtos);
        }
        return result;
    }

    public void createOrUpdate(CustomEntityInstanceDto dto) throws MeveoApiException, BusinessException {

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode());
        if (cei == null) {
            create(dto);
        } else {
            update(dto);
        }
    }

    /**
     * Validate CustomEntityInstance DTO without saving it
     * 
     * @param ceiDto CustomEntityInstance DTO to validate
     * @throws MeveoApiException meveo api exception.
     */
    public void validateEntityInstanceDto(CustomEntityInstanceDto ceiDto) throws MeveoApiException {

        if (StringUtils.isBlank(ceiDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(ceiDto.getCetCode())) {
            missingParameters.add("cetCode");
        }
        handleMissingParameters();

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(ceiDto.getCetCode(), ceiDto.getCode());
        boolean isNew = cei == null;
        if (cei == null) {
            cei = new CustomEntityInstance();
            cei.setCetCode(ceiDto.getCetCode());
        }

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(cei);

        validateAndConvertCustomFields(customFieldTemplates, ceiDto.getCustomFields() != null ? ceiDto.getCustomFields().getCustomField() : new ArrayList<CustomFieldDto>(), true, isNew, cei);
    }

    /**
     * Convert CustomEntityInstanceDto object to CustomEntityInstance object. Note: does not convert custom field values
     * 
     * @param dto CustomEntityInstanceDto to convert
     * @param ceiToUpdate CustomEntityInstance to update with values from dto, or if null create a new one
     * @return A new or updated CustomEntityInstance instance
     */
    public CustomEntityInstance convertFromDTO(CustomEntityInstanceDto dto, CustomEntityInstance ceiToUpdate) {

        CustomEntityInstance cei = ceiToUpdate;
        if (ceiToUpdate == null) {
            cei = new CustomEntityInstance();
        }
        cei.setCode(dto.getCode());
        cei.setCetCode(dto.getCetCode());
        cei.setDescription(dto.getDescription());
        if (dto.isDisabled() != null) {
            cei.setDisabled(dto.isDisabled());
        }

        return cei;
    }

    /**
     * Enable or disable Custom entity instance by its code
     * 
     * @param cetCode Custom entity template code
     * @param code Custom entity instance code
     * @param enable Should Custom entity instance be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     * @throws ActionForbiddenException User does not have sufficient right to perform operation
     */
    public void enableOrDisable(String cetCode, String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException, ActionForbiddenException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("cetCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(cetCode);
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, cetCode);
        }

        if (!currentUser.hasRole(CustomEntityTemplate.getModifyPermission(cetCode))) {
            throw new ActionForbiddenException("User does not have permission '" + CustomEntityTemplate.getModifyPermission(cetCode) + "'");
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code);
        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, cetCode + "/" + code);
        }

        if (enable) {
            customEntityInstanceService.enable(cei);
        } else {
            customEntityInstanceService.disable(cei);
        }
    }
}