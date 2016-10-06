package org.meveo.api;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;

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

    public void create(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException, BusinessException {

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
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public void update(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException, BusinessException {

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
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public void remove(String cetCode, String code, User currentUser) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code, currentUser.getProvider());
        if (cei != null) {
            customEntityInstanceService.remove(cei, currentUser);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, code);
        }
    }

    public CustomEntityInstanceDto find(String cetCode, String code, User currentUser) throws MeveoApiException {
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
        return CustomEntityInstanceDto.toDTO(cei, entityToDtoConverter.getCustomFieldsDTO(cei));
    }

    public void createOrUpdate(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException, BusinessException {

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider());
        if (cei == null) {
            create(dto, currentUser);
        } else {
            update(dto, currentUser);
        }
    }

    /**
     * Validate CustomEntityInstance DTO without saving it
     * 
     * @param ceiDto CustomEntityInstance DTO to validate
     * @param currentUser Current user
     * @throws MissingParameterException
     * @throws InvalidParameterException
     */
    public void validateEntityInstanceDto(CustomEntityInstanceDto ceiDto, User currentUser) throws InvalidParameterException, MissingParameterException {

        if (StringUtils.isBlank(ceiDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(ceiDto.getCetCode())) {
            missingParameters.add("cetCode");
        }
        handleMissingParameters();

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(ceiDto.getCetCode(), ceiDto.getCode(), currentUser.getProvider());
        boolean isNew = cei == null;
        if (cei == null) {
            cei = new CustomEntityInstance();
            cei.setCetCode(ceiDto.getCetCode());
        }

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(cei, currentUser.getProvider());

        validateAndConvertCustomFields(customFieldTemplates, ceiDto.getCustomFields().getCustomField(), true, isNew, cei, currentUser);
    }
}