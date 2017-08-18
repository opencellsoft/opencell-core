package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldMatrixColumnDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.EntityCustomizationUtils;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CustomFieldTemplateApi extends BaseApi {

    @Inject
    private CalendarService calendarService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomizedEntityService customizedEntityService;

    public void create(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (appliesTo == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }
        if (postData.getFieldType() == null) {
            missingParameters.add("fieldType");
        }
        if (postData.getStorageType() == null) {
            missingParameters.add("storageType");
        }
        if (postData.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && (postData.getMatrixColumns() == null || postData.getMatrixColumns().isEmpty())) {
            missingParameters.add("matrixColumns");

        } else if (postData.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            for (CustomFieldMatrixColumnDto columnDto : postData.getMatrixColumns()) {
                if (StringUtils.isBlank(columnDto.getCode())) {
                    missingParameters.add("matrixColumns/code");
                }
                if (StringUtils.isBlank(columnDto.getLabel())) {
                    missingParameters.add("matrixColumns/label");
                }
                if (columnDto.getKeyType() == null) {
                    missingParameters.add("matrixColumns/keyType");
                }
            }
        }

        if (postData.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && (postData.getStorageType() != CustomFieldStorageTypeEnum.LIST || postData.isVersionable())) {
            throw new InvalidParameterException("Custom field of type CHILD_ENTITY only supports unversioned values and storage type of LIST");
        }
        if (postData.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY
                && (postData.getChildEntityFieldsForSummary() == null || postData.getChildEntityFieldsForSummary().isEmpty())) {
            missingParameters.add("childEntityFieldsForSummary");
        }
        handleMissingParameters();

        if (appliesTo != null) {
            postData.setAppliesTo(appliesTo);

        } else {
            // Support for old API
            if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
                appliesTo = postData.getAccountLevel();
            } else {
                appliesTo = postData.getAppliesTo();
            }
        }

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        if (customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo) != null) {
            throw new EntityAlreadyExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        CustomFieldTemplate cft = fromDTO(postData, appliesTo, null);
        customFieldTemplateService.create(cft);

    }

    public void update(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (appliesTo == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }
        if (postData.getFieldType() == null) {
            missingParameters.add("fieldType");
        }
        if (postData.getStorageType() == null) {
            missingParameters.add("storageType");
        }
        if (postData.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && (postData.getMatrixColumns() == null || postData.getMatrixColumns().isEmpty())) {
            missingParameters.add("matrixColumns");
        } else if (postData.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            for (CustomFieldMatrixColumnDto columnDto : postData.getMatrixColumns()) {
                if (StringUtils.isBlank(columnDto.getCode())) {
                    missingParameters.add("matrixColumns/code");
                }
                if (StringUtils.isBlank(columnDto.getLabel())) {
                    missingParameters.add("matrixColumns/label");
                }
                if (columnDto.getKeyType() == null) {
                    missingParameters.add("matrixColumns/keyType");
                }
            }
        }
        if (postData.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && (postData.getStorageType() != CustomFieldStorageTypeEnum.LIST || postData.isVersionable())) {
            throw new InvalidParameterException("Custom field of type CHILD_ENTITY only supports unversioned values and storage type of LIST");
        }
        if (postData.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY
                && (postData.getChildEntityFieldsForSummary() == null || postData.getChildEntityFieldsForSummary().isEmpty())) {
            missingParameters.add("childEntityFieldsForSummary");
        }

        handleMissingParameters();

        if (appliesTo != null) {
            postData.setAppliesTo(appliesTo);

        } else {
            // Support for old API
            if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
                appliesTo = postData.getAccountLevel();
            } else {
                appliesTo = postData.getAppliesTo();
            }
        }

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo);
        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        cft = fromDTO(postData, appliesTo, cft);

        customFieldTemplateService.update(cft);

    }

    public void remove(String code, String appliesTo) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo);
        if (cft != null) {
            customFieldTemplateService.remove(cft.getId());
        } else {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
    }

    /**
     * Find Custom Field Template by its code and appliesTo attributes
     * 
     * @param actionCode Custom Field Template code
     * @param appliesTo Applies to
     * @return DTO
     * @throws EntityDoesNotExistsException Custom Field Template was not found
     * @throws InvalidParameterException AppliesTo value is incorrect
     * @throws MissingParameterException A parameter, necessary to find an Custom Field Template, was not provided
     */
    public CustomFieldTemplateDto find(String code, String appliesTo) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesToNoCache(code, appliesTo);

        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code + "/" + appliesTo);
        }
        return new CustomFieldTemplateDto(cft);
    }

    /**
     * Same as find method, only ignore EntityDoesNotExistException exception and return Null instead
     * 
     * @param actionCode Custom Field Template code
     * @param appliesTo Applies to
     * @return DTO or Null if not found
     * @throws InvalidParameterException AppliesTo value is incorrect
     * @throws MissingParameterException A parameter, necessary to find an Custom Field Template, was not provided
     */
    public CustomFieldTemplateDto findIgnoreNotFound(String code, String appliesTo) throws MissingParameterException, InvalidParameterException {
        try {
            return find(code, appliesTo);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    public void createOrUpdate(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (appliesTo == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        if (appliesTo != null) {
            postData.setAppliesTo(appliesTo);

        } else {
            // Support for old API
            if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
                appliesTo = postData.getAccountLevel();
            } else {
                appliesTo = postData.getAppliesTo();
            }
        }

        CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo);

        if (customFieldTemplate == null) {
            create(postData, appliesTo);
        } else {
            update(postData, appliesTo);
        }
    }

    protected CustomFieldTemplate fromDTO(CustomFieldTemplateDto dto, String appliesTo, CustomFieldTemplate cftToUpdate) {

        // Set default values
        if (dto.getFieldType() == CustomFieldTypeEnum.STRING && dto.getMaxValue() == null) {
            dto.setMaxValue(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING);
        }

        CustomFieldTemplate cft = new CustomFieldTemplate();
        if (cftToUpdate != null) {
            cft = cftToUpdate;
        }
        cft.setCode(dto.getCode());
        cft.setDescription(dto.getDescription());
        if (appliesTo == null) {

            // Support for old API
            if (dto.getAccountLevel() != null) {
                appliesTo = dto.getAccountLevel();
            } else {
                appliesTo = dto.getAppliesTo();
            }
        }
        cft.setAppliesTo(appliesTo);
        cft.setFieldType(dto.getFieldType());
        cft.setDefaultValue(dto.getDefaultValue());
        cft.setUseInheritedAsDefaultValue(dto.isUseInheritedAsDefaultValue());
        cft.setStorageType(dto.getStorageType());
        cft.setValueRequired(dto.isValueRequired());
        cft.setVersionable(dto.isVersionable());
        cft.setTriggerEndPeriodEvent(dto.isTriggerEndPeriodEvent());
        cft.setEntityClazz(org.apache.commons.lang3.StringUtils.trimToNull(dto.getEntityClazz()));
        cft.setAllowEdit(dto.isAllowEdit());
        cft.setHideOnNew(dto.isHideOnNew());
        cft.setMinValue(dto.getMinValue());
        cft.setMaxValue(dto.getMaxValue());
        // cft.setCacheValue(dto.isCacheValue());
        cft.setRegExp(dto.getRegExp());
        // cft.setCacheValueTimeperiod(dto.getCacheValueTimeperiod());
        cft.setGuiPosition(dto.getGuiPosition());
        cft.setApplicableOnEl(dto.getApplicableOnEl());

        if (cft.getFieldType() == CustomFieldTypeEnum.LIST) {
            cft.setListValues(dto.getListValues());
        }

        cft.setMapKeyType(dto.getMapKeyType());
        cft.setIndexType(dto.getIndexType());
        cft.setTags(dto.getTags());

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && cft.getMapKeyType() == null) {
            cft.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        }

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            if (cft.getMatrixColumns() == null) {
                cft.setMatrixColumns(new ArrayList<CustomFieldMatrixColumn>());
            } else {
                cft.getMatrixColumns().clear();
            }

            for (CustomFieldMatrixColumnDto columnDto : dto.getMatrixColumns()) {
                cft.getMatrixColumns().add(CustomFieldMatrixColumnDto.fromDto(columnDto));
            }
        }

        if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
            cft.setStorageType(CustomFieldStorageTypeEnum.LIST);
            cft.setVersionable(false);
            cft.setChildEntityFieldsAsList(dto.getChildEntityFieldsForSummary());
        }

        if (!StringUtils.isBlank(dto.getCalendar())) {
            Calendar calendar = calendarService.findByCode(dto.getCalendar());
            if (calendar != null) {
                cft.setCalendar(calendar);
            } else {
                cft.setCalendar(null);
            }
        } else {
            cft.setCalendar(null);
        }
        return cft;
    }

    private List<String> getCustomizedEntitiesAppliesTo() {
        List<String> cftAppliesto = new ArrayList<String>();
        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(null, false, true, null, null);
        for (CustomizedEntity customizedEntity : entities) {
            cftAppliesto.add(EntityCustomizationUtils.getAppliesTo(customizedEntity.getEntityClass(), customizedEntity.getEntityCode()));
        }
        return cftAppliesto;
    }
}
