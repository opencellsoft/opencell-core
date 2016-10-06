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
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
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

    public void create(CustomFieldTemplateDto postData, String appliesTo, User currentUser) throws MeveoApiException, BusinessException {

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

        if (!getCustomizedEntitiesAppliesTo(currentUser.getProvider()).contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        if (customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo, currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        CustomFieldTemplate cft = fromDTO(postData, currentUser, appliesTo, null);
        customFieldTemplateService.create(cft, currentUser);

    }

    public void update(CustomFieldTemplateDto postData, String appliesTo, User currentUser) throws MeveoApiException, BusinessException {

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

        if (!getCustomizedEntitiesAppliesTo(currentUser.getProvider()).contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo, currentUser.getProvider());
        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        cft = fromDTO(postData, currentUser, appliesTo, cft);

        customFieldTemplateService.update(cft, currentUser);

    }

    public void remove(String code, String appliesTo, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        if (!getCustomizedEntitiesAppliesTo(currentUser.getProvider()).contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo, currentUser.getProvider());
        if (cft != null) {
            customFieldTemplateService.remove(cft.getId(), currentUser);
        } else {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
    }

    public CustomFieldTemplateDto find(String code, String appliesTo, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();
        
        if (!getCustomizedEntitiesAppliesTo(provider).contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesToNoCache(code, appliesTo, provider);

        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code + "/" + appliesTo);
        }
        return new CustomFieldTemplateDto(cft);
    }

    public void createOrUpdate(CustomFieldTemplateDto postData, String appliesTo, User currentUser) throws MeveoApiException, BusinessException {
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

        CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo, currentUser.getProvider());

        if (customFieldTemplate == null) {
            create(postData, appliesTo, currentUser);
        } else {
            update(postData, appliesTo, currentUser);
        }
    }

    protected CustomFieldTemplate fromDTO(CustomFieldTemplateDto dto, User currentUser, String appliesTo, CustomFieldTemplate cftToUpdate) {

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
        cft.setStorageType(dto.getStorageType());
        cft.setValueRequired(dto.isValueRequired());
        cft.setVersionable(dto.isVersionable());
        cft.setTriggerEndPeriodEvent(dto.isTriggerEndPeriodEvent());
        cft.setEntityClazz(org.apache.commons.lang3.StringUtils.trimToNull(dto.getEntityClazz()));
        cft.setAllowEdit(dto.isAllowEdit());
        cft.setHideOnNew(dto.isHideOnNew());
        cft.setMinValue(dto.getMinValue());
        cft.setMaxValue(dto.getMaxValue());
        cft.setCacheValue(dto.isCacheValue());
        cft.setRegExp(dto.getRegExp());
        cft.setCacheValueTimeperiod(dto.getCacheValueTimeperiod());
        cft.setGuiPosition(dto.getGuiPosition());
        cft.setApplicableOnEl(dto.getApplicableOnEl());

        if (cft.getFieldType() == CustomFieldTypeEnum.LIST) {
            cft.setListValues(dto.getListValues());
        }

        cft.setMapKeyType(dto.getMapKeyType());
        cft.setIndexType(dto.getIndexType());
        
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
            Calendar calendar = calendarService.findByCode(dto.getCalendar(), currentUser.getProvider());
            if (calendar != null) {
                cft.setCalendar(calendar);
            }
        }
        return cft;
    }

    private List<String> getCustomizedEntitiesAppliesTo(Provider provider) {
        List<String> cftAppliesto = new ArrayList<String>();
        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(null, false, true, null, null, provider);
        for (CustomizedEntity customizedEntity : entities) {
            cftAppliesto.add(EntityCustomizationUtils.getAppliesTo(customizedEntity.getEntityClass(), customizedEntity.getEntityCode()));
        }
        return cftAppliesto;
    }
}
