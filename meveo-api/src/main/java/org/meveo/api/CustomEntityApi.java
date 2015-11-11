package org.meveo.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;

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

    public void createEntityTemplate(CustomEntityTemplateDto dto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        if (customEntityTemplateService.findByCode(dto.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        CustomEntityTemplate cet = CustomEntityTemplateDto.fromDTO(dto, null);
        customEntityTemplateService.create(cet, currentUser, currentUser.getProvider());

        if (dto.getFields() != null) {
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {
                customFieldTemplateApi.create(cftDto, currentUser, cet);
            }
        }
    }

    public void updateEntityTemplate(CustomEntityTemplateDto dto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCode(), currentUser.getProvider());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        cet = CustomEntityTemplateDto.fromDTO(dto, cet);
        cet = customEntityTemplateService.update(cet, currentUser);

        List<CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getCFTPrefix(), currentUser.getProvider());

        // Create, update or remove fields as necessary
        if (dto.getFields() != null && !dto.getFields().isEmpty()) {
            boolean found = false;
            for (CustomFieldTemplate cft : cetFields) {
                for (CustomFieldTemplateDto cftDto : dto.getFields()) {
                    if (cftDto.getCode().equals(cft.getCode())) {
                        found = true;
                        break;
                    }
                }

                // Old field is no longer needed. Remove by id, as CFT might come detached from cache
                if (!found) {
                    customFieldTemplateService.remove(cft.getId());
                }
            }
            // Update or create custom field templates
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {
                customFieldTemplateApi.createOrUpdate(cftDto, currentUser, cet);

            }

        } else {
            for (CustomFieldTemplate cft : cetFields) {
                customFieldTemplateService.remove(cft.getId());
            }
        }

    }

    public void removeEntityTemplate(String code, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code, provider);
        if (cet != null) {
            // Related custom field templates will be removed along with CET
            customEntityTemplateService.remove(cet);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
    }

    public CustomEntityTemplateDto findEntityTemplate(String code, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code, provider);

        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        List<CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getCFTPrefix(), provider);

        return CustomEntityTemplateDto.toDTO(cet, cetFields);
    }

    public void createOrUpdateEntityTemplate(CustomEntityTemplateDto postData, User currentUser) throws MeveoApiException {
        CustomEntityTemplate cet = customEntityTemplateService.findByCode(postData.getCode(), currentUser.getProvider());
        if (cet == null) {
            createEntityTemplate(postData, currentUser);
        } else {
            updateEntityTemplate(postData, currentUser);
        }
    }

    public void createEntityInstance(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCetCode())) {
            missingParameters.add("cetCode");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCetCode(), currentUser.getProvider());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCetCode());
        }

        if (customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityInstance.class, dto.getCode());
        }

        CustomEntityInstance cei = CustomEntityInstanceDto.fromDTO(dto, null);

        // populate customFields
        if (dto.getCustomFields() != null) {
            try {
                populateCustomFields(dto.getCustomFields().getCustomField(), cei, currentUser);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new MeveoApiException("Failed to associate custom field instance to an entity");
            }
        }

        customEntityInstanceService.create(cei, currentUser, currentUser.getProvider());

    }

    public void updateEntityInstance(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getCetCode())) {
            missingParameters.add("cetCode");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCetCode(), currentUser.getProvider());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCetCode());
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider());
        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, dto.getCode());
        }

        cei = CustomEntityInstanceDto.fromDTO(dto, cei);

        // populate customFields
        if (dto.getCustomFields() != null) {
            try {
                populateCustomFields(dto.getCustomFields().getCustomField(), cei, currentUser);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new MeveoApiException("Failed to associate custom field instance to an entity");
            }
        }

        cei = customEntityInstanceService.update(cei, currentUser);
    }

    public void removeEntityInstance(String cetCode, String code, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("cetCode");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code, provider);
        if (cei != null) {
            customEntityInstanceService.remove(cei);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityInstance.class, code);
        }
    }

    public CustomEntityInstanceDto findEntityInstance(String cetCode, String code, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(cetCode)) {
            missingParameters.add("cetCode");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(cetCode, code, provider);

        if (cei == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        return CustomEntityInstanceDto.toDTO(cei);
    }

    public void createOrUpdateEntityInstance(CustomEntityInstanceDto dto, User currentUser) throws MeveoApiException {
        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(dto.getCetCode(), dto.getCode(), currentUser.getProvider());
        if (cei == null) {
            createEntityInstance(dto, currentUser);
        } else {
            updateEntityInstance(dto, currentUser);
        }
    }
}