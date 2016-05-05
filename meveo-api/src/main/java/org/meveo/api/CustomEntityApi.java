package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityActionScriptDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.script.EntityActionScriptService;
import org.meveo.util.EntityCustomizationUtils;

/**
 * @author Andrius Karpavicius
 **/
@Stateless
public class CustomEntityApi extends BaseApi {

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private EntityActionScriptService entityActionScriptService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    public void createEntityTemplate(CustomEntityTemplateDto dto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if (customEntityTemplateService.findByCode(dto.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        CustomEntityTemplate cet = CustomEntityTemplateDto.fromDTO(dto, null);
        customEntityTemplateService.create(cet, currentUser);

        if (dto.getFields() != null) {
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {
                customFieldTemplateApi.createOrUpdate(cftDto, cet.getAppliesTo(), currentUser);
            }
        }

        if (dto.getActions() != null) {
            for (EntityActionScriptDto actionDto : dto.getActions()) {
                scriptInstanceApi.createOrUpdate(actionDto, cet.getAppliesTo(), currentUser);
            }
        }
    }

    public void updateEntityTemplate(CustomEntityTemplateDto dto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCode(), currentUser.getProvider());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        cet = CustomEntityTemplateDto.fromDTO(dto, cet);
        cet = customEntityTemplateService.update(cet, currentUser);

        synchronizeCustomFieldsAndActions(cet.getAppliesTo(), dto.getFields(), dto.getActions(), currentUser);

    }

    public void removeEntityTemplate(String code, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code, currentUser.getProvider());
        if (cet != null) {
            // Related custom field templates will be removed along with CET
            customEntityTemplateService.remove(cet);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
    }

    public CustomEntityTemplateDto findEntityTemplate(String code, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code, currentUser.getProvider());

        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo(), currentUser.getProvider());

        Map<String, EntityActionScript> cetActions = entityActionScriptService.findByAppliesTo(cet.getAppliesTo(), currentUser.getProvider());

        return CustomEntityTemplateDto.toDTO(cet, cetFields.values(), cetActions.values());
    }

    public void createOrUpdateEntityTemplate(CustomEntityTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        CustomEntityTemplate cet = customEntityTemplateService.findByCode(postData.getCode(), currentUser.getProvider());
        if (cet == null) {
            createEntityTemplate(postData, currentUser);
        } else {
            updateEntityTemplate(postData, currentUser);
        }
    }

    public void createEntityInstance(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCetCode())) {
            missingParameters.add("cetCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCetCode(), currentUser.getProvider());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCetCode());
        }

        if (customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityInstance.class, dto.getCode());
        }

        CustomEntityInstance cei = CustomEntityInstanceDto.fromDTO(dto, null);

        customEntityInstanceService.create(cei, currentUser);

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), cei, true, currentUser);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }

    }

    public void updateEntityInstance(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCetCode())) {
            missingParameters.add("cetCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCetCode(), currentUser.getProvider());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCetCode());
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider());
        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, dto.getCode());
        }

        cei = CustomEntityInstanceDto.fromDTO(dto, cei);

        cei = customEntityInstanceService.update(cei, currentUser);

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), cei, false, currentUser);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }

    }

    public void removeEntityInstance(String cetCode, String code, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code, currentUser.getProvider());
        if (cei != null) {
            customEntityInstanceService.remove(cei);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, code);
        }
    }

    public CustomEntityInstanceDto findEntityInstance(String cetCode, String code, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code, currentUser.getProvider());

        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        return CustomEntityInstanceDto.toDTO(cei, customFieldInstanceService.getCustomFieldInstances(cei));
    }

    public void createOrUpdateEntityInstance(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException, BusinessException {
        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider());
        if (cei == null) {
            createEntityInstance(dto, currentUser);
        } else {
            updateEntityInstance(dto, currentUser);
        }
    }

    public List<CustomEntityTemplateDto> listCustomEntityTemplates(String code, User currentUser) {

        List<CustomEntityTemplate> cets = null;
        if (StringUtils.isBlank(code)) {
            cets = customEntityTemplateService.list(currentUser.getProvider());
        } else {
            cets = customEntityTemplateService.findByCodeLike(code, currentUser.getProvider());
        }

        List<CustomEntityTemplateDto> cetDtos = new ArrayList<CustomEntityTemplateDto>();

        for (CustomEntityTemplate cet : cets) {

            Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo(), currentUser.getProvider());
            Map<String, EntityActionScript> cetActions = entityActionScriptService.findByAppliesTo(cet.getAppliesTo(), currentUser.getProvider());

            cetDtos.add(CustomEntityTemplateDto.toDTO(cet, cetFields.values(), cetActions.values()));
        }

        return cetDtos;
    }

    @SuppressWarnings("rawtypes")
    public void customizeEntity(EntityCustomizationDto dto, User currentUser) throws MeveoApiException, BusinessException {

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

        synchronizeCustomFieldsAndActions(appliesTo, dto.getFields(), dto.getActions(), currentUser);
    }

    private void synchronizeCustomFieldsAndActions(String appliesTo, List<CustomFieldTemplateDto> fields, List<EntityActionScriptDto> actions, User currentUser)
            throws MeveoApiException, BusinessException {

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(appliesTo, currentUser.getProvider());

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
                customFieldTemplateApi.createOrUpdate(cftDto, appliesTo, currentUser);
            }

        } else {
            cftsToRemove.addAll(cetFields.values());
        }

        for (CustomFieldTemplate cft : cftsToRemove) {
            customFieldTemplateService.remove(cft.getId());
        }

        Map<String, EntityActionScript> cetActions = entityActionScriptService.findByAppliesTo(appliesTo, currentUser.getProvider());

        // Create, update or remove fields as necessary
        List<EntityActionScript> actionsToRemove = new ArrayList<EntityActionScript>();
        if (actions != null && !actions.isEmpty()) {

            for (EntityActionScript action : cetActions.values()) {
                boolean found = false;
                for (EntityActionScriptDto actionDto : actions) {
                    if (actionDto.getCode().equals(action.getLocalCodeForRead())) {
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
            for (EntityActionScriptDto actionDto : actions) {
                scriptInstanceApi.createOrUpdate(actionDto, appliesTo, currentUser);
            }

        } else {
            actionsToRemove.addAll(cetActions.values());
        }

        for (EntityActionScript action : actionsToRemove) {
            entityActionScriptService.remove(action.getId());
        }
    }

    @SuppressWarnings("rawtypes")
    public EntityCustomizationDto findEntityCustomizations(String customizedEntityClass, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
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

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(appliesTo, currentUser.getProvider());

        Map<String, EntityActionScript> cetActions = entityActionScriptService.findByAppliesTo(appliesTo, currentUser.getProvider());

        return EntityCustomizationDto.toDTO(clazz, cetFields.values(), cetActions.values());
    }
}