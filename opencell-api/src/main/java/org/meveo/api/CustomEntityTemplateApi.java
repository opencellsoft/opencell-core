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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.util.EntityCustomizationUtils;

/**
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class CustomEntityTemplateApi extends BaseCrudApi<CustomEntityTemplate, CustomEntityTemplateDto> {

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private EntityCustomActionService entityActionScriptService;

    @Inject
    private EntityCustomActionApi entityCustomActionApi;

    @Inject
    private EntityCustomActionService entityCustomActionService;

    @Override
    public CustomEntityTemplate create(CustomEntityTemplateDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if (dto.getStoreAsTable() == null) {
            dto.setStoreAsTable(Boolean.FALSE);
        }

        if (dto.getStoreInES() == null) {
            dto.setStoreInES(Boolean.TRUE);
        }

        if (customEntityTemplateService.findByCode(dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        // Validate field types
        if (dto.getFields() != null) {
            int pos = 0;
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {

                // Default to 'Index but not analyze storage', 'single' storage type and sequential field position for custom tables
                if (dto.getStoreAsTable() && cftDto.getIndexType() == null) {
                    cftDto.setIndexType(CustomFieldIndexTypeEnum.INDEX_NOT_ANALYZE);
                }
                if (cftDto.getStorageType() == null) {
                    cftDto.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
                }
                if (cftDto.getGuiPosition() == null) {
                    cftDto.setGuiPosition("tab:" + dto.getName() + ":0;field:" + pos);
                    pos++;
                }

                if (dto.getStoreAsTable() && (cftDto.getStorageType() != CustomFieldStorageTypeEnum.SINGLE || (cftDto.getFieldType() != CustomFieldTypeEnum.DATE && cftDto.getFieldType() != CustomFieldTypeEnum.DOUBLE
                        && cftDto.getFieldType() != CustomFieldTypeEnum.LIST && cftDto.getFieldType() != CustomFieldTypeEnum.LONG && cftDto.getFieldType() != CustomFieldTypeEnum.STRING
                        && cftDto.getFieldType() != CustomFieldTypeEnum.BOOLEAN && cftDto.getFieldType() != CustomFieldTypeEnum.ENTITY && cftDto.getFieldType() != CustomFieldTypeEnum.TEXT_AREA)
                        || (cftDto.isVersionable() != null && cftDto.isVersionable()))) {
                    throw new InvalidParameterException("Custom table supports only unversioned and simple Date, Double, Long, Boolean, String, Reference to entity, text area and Select from list type fields");
                }
            }
        }

        CustomEntityTemplate cet = convertCustomEntityTemplateFromDTO(dto, null);
        customEntityTemplateService.create(cet);

        if (dto.getFields() != null) {
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {
                cftDto.setDisabled(dto.isDisabled());
                customFieldTemplateApi.createWithoutUniqueConstraint(cftDto, cet.getAppliesTo());
            }
            String columnNames = dto.getFields().stream().filter(x -> x.getUniqueConstraint() != null && x.getUniqueConstraint()).map(x -> CustomFieldTemplate.getDbFieldname(x.getCode())).distinct().sorted()
                .collect(Collectors.joining(","));
            customFieldTemplateService.addConstraintByColumnsName(cet, columnNames);
        }

        if (dto.getActions() != null) {
            for (EntityCustomActionDto actionDto : dto.getActions()) {
                actionDto.setDisabled(dto.isDisabled());
                entityCustomActionApi.createOrUpdate(actionDto, cet.getAppliesTo());
            }
        }

        return cet;
    }

    @Override
    public CustomEntityTemplate update(CustomEntityTemplateDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeNoCache(dto.getCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        // Validate field types
        if (dto.getFields() != null) {
            int pos = 0;
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {

                // Default to 'Index but not analyze storage' and 'single' storeage type for custom tables
                if (cet.isStoreAsTable() && cftDto.getIndexType() == null) {
                    cftDto.setIndexType(CustomFieldIndexTypeEnum.INDEX_NOT_ANALYZE);
                }
                //
                if (cftDto.getStorageType() == null) {
                    cftDto.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
                }
                if (cftDto.getGuiPosition() == null) {
                    cftDto.setGuiPosition("tab:" + dto.getName() + ":0;field:" + pos);
                    pos++;
                }

                if (cet.isStoreAsTable() && (cftDto.getStorageType() != CustomFieldStorageTypeEnum.SINGLE || (cftDto.getFieldType() != CustomFieldTypeEnum.DATE && cftDto.getFieldType() != CustomFieldTypeEnum.DOUBLE
                        && cftDto.getFieldType() != CustomFieldTypeEnum.LIST && cftDto.getFieldType() != CustomFieldTypeEnum.LONG && cftDto.getFieldType() != CustomFieldTypeEnum.STRING
                        && cftDto.getFieldType() != CustomFieldTypeEnum.BOOLEAN && cftDto.getFieldType() != CustomFieldTypeEnum.ENTITY && cftDto.getFieldType() != CustomFieldTypeEnum.TEXT_AREA)
                        || (cftDto.isVersionable() != null && cftDto.isVersionable()))) {
                    throw new InvalidParameterException("Custom table supports only unversioned and simple Date, Double, Long, Boolean, String, Reference to entity, text area and Select from list type fields");
                }
            }
        }

        cet = convertCustomEntityTemplateFromDTO(dto, cet);
        cet = customEntityTemplateService.update(cet);

        synchronizeCustomFieldsAndActions(cet, cet.getAppliesTo(), dto.getFields(), dto.getActions());

        return cet;
    }

    @Override
    public CustomEntityTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code);

        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        return convertCustomEntityTemplateToDTO(cet);
    }

    @Override
    public CustomEntityTemplate createOrUpdate(CustomEntityTemplateDto postData) throws MeveoApiException, BusinessException {
        CustomEntityTemplate cet = customEntityTemplateService.findByCode(postData.getCode());
        if (cet == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }

    public List<CustomEntityTemplateDto> listCustomEntityTemplates(String code) {

        List<CustomEntityTemplate> cets = null;
        if (StringUtils.isBlank(code)) {
            cets = customEntityTemplateService.list();
        } else {
            cets = customEntityTemplateService.findByCodeLike(code);
        }

        List<CustomEntityTemplateDto> cetDtos = new ArrayList<CustomEntityTemplateDto>();

        for (CustomEntityTemplate cet : cets) {
            cetDtos.add(convertCustomEntityTemplateToDTO(cet));
        }

        return cetDtos;
    }

    @SuppressWarnings("rawtypes")
    public void customizeEntity(EntityCustomizationDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getClassname())) {
            missingParameters.add("className");
        }

        handleMissingParameters();

        Class clazz;
        try {
            clazz = Class.forName(dto.getClassname());
        } catch (ClassNotFoundException e) {
            throw new EntityDoesNotExistsException("Customizable entity of class " + dto.getClassname() + " not found");
        }

        String appliesTo = EntityCustomizationUtils.getAppliesTo(clazz, null);
        CustomEntityTemplate cet = customEntityTemplateService.findByCodeNoCache(dto.getClassname());
        synchronizeCustomFieldsAndActions(cet, appliesTo, dto.getFields(), dto.getActions());
    }

    private void synchronizeCustomFieldsAndActions(CustomEntityTemplate cet, String appliesTo, List<CustomFieldTemplateDto> fields, List<EntityCustomActionDto> actions) throws MeveoApiException, BusinessException {

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesToNoCache(appliesTo);
        String oldConstraintColumns = cetFields.values().stream().filter(x -> x.isUniqueConstraint()).map(x -> x.getDbFieldname()).distinct().sorted().collect(Collectors.joining(","));

        // Create, update or remove fields as necessary
        List<CustomFieldTemplate> cftsToRemove = new ArrayList<CustomFieldTemplate>();
        if (fields != null && !fields.isEmpty()) {

            for (CustomFieldTemplate cft : cetFields.values()) {
                boolean found = false;
                for (CustomFieldTemplateDto cftDto : fields) {
                    if (cftDto.getCode().equals(cft.getCode())) {
                        found = true;
                        break;
                    }
                }

                // Old field is no longer needed. Remove by id, as CFT might come detached from cache
                if (!found) {
                    cftsToRemove.add(cft);
                }
            }
            // Update or create custom field templates
            for (CustomFieldTemplateDto cftDto : fields) {
                customFieldTemplateApi.createOrUpdateWithoutUniqueConstraint(cftDto, appliesTo);
            }

        } else {
            cftsToRemove.addAll(cetFields.values());
        }

        for (CustomFieldTemplate cft : cftsToRemove) {
            customFieldTemplateService.remove(cft.getId());
        }

        Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(appliesTo);

        // Create, update or remove fields as necessary
        List<EntityCustomAction> actionsToRemove = new ArrayList<EntityCustomAction>();
        if (actions != null && !actions.isEmpty()) {

            for (EntityCustomAction action : cetActions.values()) {
                boolean found = false;
                for (EntityCustomActionDto actionDto : actions) {
                    if (actionDto.getCode().equals(action.getCode())) {
                        found = true;
                        break;
                    }
                }

                // Old action is no longer needed. Remove by id, as Action might come detached from cache
                if (!found) {
                    actionsToRemove.add(action);
                }
            }
            // Update or create custom field templates
            for (EntityCustomActionDto actionDto : actions) {
                entityCustomActionApi.createOrUpdate(actionDto, appliesTo);
            }

        } else {
            actionsToRemove.addAll(cetActions.values());
        }

        for (EntityCustomAction action : actionsToRemove) {
            entityActionScriptService.remove(action.getId());
        }
        if (cet != null) {
            boolean keyColumnRemoved = cftsToRemove.stream().anyMatch(x -> x.isUniqueConstraint());
            String newConstraintColumns = CollectionUtils.isEmpty(fields) ? ""
                    : fields.stream().filter(x -> x.getUniqueConstraint() != null && x.getUniqueConstraint()).map(x -> CustomFieldTemplate.getDbFieldname(x.getCode())).distinct().sorted()
                        .collect(Collectors.joining(","));
            customFieldTemplateService.updateConstraintByColumnsName(cet, oldConstraintColumns, newConstraintColumns, keyColumnRemoved);
        }
    }

    @SuppressWarnings("rawtypes")
    public EntityCustomizationDto findEntityCustomizations(String customizedEntityClass) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(customizedEntityClass)) {
            missingParameters.add("customizedEntityClass");
        }

        handleMissingParameters();

        Class clazz;
        try {
            clazz = Class.forName(customizedEntityClass);
        } catch (ClassNotFoundException e) {
            throw new EntityDoesNotExistsException("Customizable entity of class " + customizedEntityClass + " not found");
        }

        String appliesTo = EntityCustomizationUtils.getAppliesTo(clazz, null);

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(appliesTo);

        Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(appliesTo);

        return EntityCustomizationDto.toDTO(clazz, cetFields.values(), cetActions.values());
    }

    public List<BusinessEntityDto> listBusinessEntityForCFVByCode(String code, String wildcode) throws MeveoApiException, ClassNotFoundException {
        List<BusinessEntityDto> result = new ArrayList<>();

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(wildcode)) {
            wildcode = "";
        }

        handleMissingParameters();

        CustomFieldTemplate cft = customFieldTemplateService.findByCode(code);
        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }

        String entityClazz = cft.getEntityClazz();
        if (!StringUtils.isBlank(entityClazz)) {
            List<BusinessEntity> businessEntities = customFieldInstanceService.findBusinessEntityForCFVByCode(entityClazz, wildcode, null);
            if (businessEntities != null) {
                for (BusinessEntity be : businessEntities) {
                    result.add(new BusinessEntityDto(be));
                }
            }
        }

        return result;
    }

    /**
     * Finds an entity that match the given criterion. Evaluates applicableEL on custom fields and actions of the entity, if false it will not be included in the resulting object.
     * 
     * @param appliesTo type of entity
     * @param entityCode code of the entity
     * @return an object with a list of custom fields and actions
     * @throws MissingParameterException when there is a missing parameter
     * @throws BusinessException business logic is violated
     */
    public EntityCustomizationDto listELFiltered(String appliesTo, String entityCode, Long entityId) throws MissingParameterException, BusinessException {
        EntityCustomizationDto result = new EntityCustomizationDto();
        log.debug("IPIEL: listELFiltered");

        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }
        if (StringUtils.isBlank(entityCode) && entityId == null) {
            missingParameters.add("entityCode");
        }

        handleMissingParameters();

        @SuppressWarnings("rawtypes")
        Class entityClass = null;
        // get all the class annotated with customFieldEntity
        Set<Class<?>> cfClasses = ReflectionUtils.getClassesAnnotatedWith(CustomFieldEntity.class);
        for (Class<?> clazz : cfClasses) {
            // check if appliesTo match, eg OfferTemplate
            if (appliesTo.equals(clazz.getAnnotation(CustomFieldEntity.class).cftCodePrefix())) {
                entityClass = clazz;
                break;
            }
        }

        // search for custom field entity filtered by type and code
        String key = entityId != null ? "id" : "code";
        Object value = entityId != null ? entityId : entityCode;
        // search for custom field entity filtered by type and code
        ICustomFieldEntity entityInstance = customEntityTemplateService.findByClassAndKeyValue(entityClass, key, value);

        // custom fields that applies to an entity type, eg. OfferTemplate
        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(appliesTo);
        Map<String, EntityCustomAction> caFields = entityCustomActionService.findByAppliesTo(appliesTo);
        result = EntityCustomizationDto.toDTO(entityClass, cetFields.values(), caFields.values());

        // evaluate the CFT against the entity
        List<CustomFieldTemplateDto> evaluatedCFTDto = new ArrayList<>();
        for (CustomFieldTemplateDto cft : result.getFields()) {
            if (ValueExpressionWrapper.evaluateToBooleanOneVariable(cft.getApplicableOnEl(), "entity", entityInstance)) {
                evaluatedCFTDto.add(cft);
            }
        }
        result.setFields(evaluatedCFTDto);

        // evaluate the CA againsts the entity
        List<EntityCustomActionDto> evaluatedCA = new ArrayList<>();
        for (EntityCustomActionDto eca : result.getActions()) {
            if (ValueExpressionWrapper.evaluateToBooleanOneVariable(eca.getApplicableOnEl(), "entity", entityInstance)) {
                evaluatedCA.add(eca);
            }
        }
        result.setActions(evaluatedCA);

        return result;
    }

    /**
     * Convert CustomEntityTemplate instance to CustomEntityTemplateDto object including the fields and actions
     * 
     * @param cet CustomEntityTemplate object to convert
     * @param cetFields Fields (CustomFieldTemplate) that are part of CustomEntityTemplate
     * @param cetActions Actions (EntityActionScript) available on CustomEntityTemplate
     * @return A CustomEntityTemplateDto object with fields set
     */
    private CustomEntityTemplateDto convertCustomEntityTemplateToDTO(CustomEntityTemplate cet) {

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());

        Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(cet.getAppliesTo());

        CustomEntityTemplateDto dto = new CustomEntityTemplateDto(cet, cetFields.values(), cetActions.values());

        return dto;
    }

    /**
     * Convert CustomEntityTemplateDto to a CustomEntityTemplate instance. Note: does not convert custom fields that are part of DTO
     * 
     * @param dto CustomEntityTemplateDto object to convert
     * @param cetToUpdate CustomEntityTemplate to update with values from dto, or if null create a new one
     * @return A new or updated CustomEntityTemplate instance
     */
    private CustomEntityTemplate convertCustomEntityTemplateFromDTO(CustomEntityTemplateDto dto, CustomEntityTemplate cetToUpdate) {
        CustomEntityTemplate cet = cetToUpdate;
        if (cetToUpdate == null) {
            cet = new CustomEntityTemplate();
            cet.setCode(dto.getCode());
            cet.setStoreAsTable(dto.getStoreAsTable());
            if (dto.isDisabled() != null) {
                cet.setDisabled(dto.isDisabled());
            }
        }
        cet.setName(dto.getName());
        cet.setDescription(dto.getDescription());

        if (dto.getStoreInES() != null) {
            cet.setStoreInES(dto.getStoreInES());
        }

        return cet;
    }

    @Override
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeNoCache(code);
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        if (enable) {
            customEntityTemplateService.enable(cet);
        } else {
            customEntityTemplateService.disable(cet);
        }
    }

    @Override
    public void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeNoCache(code);

        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }

        customEntityTemplateService.remove(cet);
    }
}