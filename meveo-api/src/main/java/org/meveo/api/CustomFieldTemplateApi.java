package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CustomFieldTemplateApi extends BaseApi {

    @Inject
    private CalendarService calendarService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    public void create(CustomFieldTemplateDto postData, User currentUser, CustomEntityTemplate cet) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (cet == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }
        if (StringUtils.isBlank(postData.getFieldType())) {
            missingParameters.add("fieldType");
        }
        if (StringUtils.isBlank(postData.getStorageType())) {
            missingParameters.add("storageType");
        }

        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        if (cet != null) {
            postData.setAppliesTo(cet.getCFTPrefix());
        }
        String appliesTo = postData.getAppliesTo();
        // Support for old API
        if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
            appliesTo = postData.getAccountLevel();
        }
        if (customFieldTemplateService.findByCodeAndAppliesTo(postData.getCode(), appliesTo, currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        CustomFieldTemplate cft = fromDTO(postData, currentUser, cet, null);
        customFieldTemplateService.create(cft, currentUser, currentUser.getProvider());

    }

    public void update(CustomFieldTemplateDto postData, User currentUser, CustomEntityTemplate cet) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (cet == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }
        if (StringUtils.isBlank(postData.getFieldType())) {
            missingParameters.add("fieldType");
        }
        if (StringUtils.isBlank(postData.getStorageType())) {
            missingParameters.add("storageType");
        }

        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        if (cet != null) {
            postData.setAppliesTo(cet.getCFTPrefix());
        }
        String appliesTo = postData.getAppliesTo();
        // Support for old API
        if (postData.getAccountLevel() != null) {
            appliesTo = postData.getAccountLevel();
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(postData.getCode(), appliesTo, currentUser.getProvider());
        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        cft = fromDTO(postData, currentUser, cet, cft);
        customFieldTemplateService.update(cft, currentUser);

    }

    public void remove(String code, String appliesTo, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo, provider);
        if (cft != null) {
            customFieldTemplateService.remove(cft);
        } else {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
    }

    public CustomFieldTemplateDto find(String code, String appliesTo, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo, provider);

        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
        return new CustomFieldTemplateDto(cft);
    }

    public void createOrUpdate(CustomFieldTemplateDto postData, User currentUser, CustomEntityTemplate cet) throws MeveoApiException {
        CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCode(postData.getCode(), currentUser.getProvider());
        if (customFieldTemplate == null) {
            create(postData, currentUser, cet);
        } else {
            update(postData, currentUser, cet);
        }
    }

    protected CustomFieldTemplate fromDTO(CustomFieldTemplateDto dto, User currentUser, CustomEntityTemplate cet, CustomFieldTemplate cftToUpdate) throws InvalidEnumValue {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        if (cftToUpdate != null) {
            cft = cftToUpdate;
        }
        cft.setCode(dto.getCode());
        cft.setDescription(dto.getDescription());
        String appliesTo = dto.getAppliesTo();
        // Support for old API
        if (dto.getAccountLevel() != null) {
            appliesTo = dto.getAccountLevel();
        }
        if (cet != null) {
            cft.setAppliesTo(cet.getCFTPrefix());
        } else {
            cft.setAppliesTo(appliesTo);
        }
        try {
            cft.setFieldType(CustomFieldTypeEnum.valueOf(dto.getFieldType()));
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValue(CustomFieldTypeEnum.class.getName(), dto.getFieldType());
        }

        cft.setDefaultValue(dto.getDefaultValue());
        try {
            cft.setStorageType(CustomFieldStorageTypeEnum.valueOf(dto.getStorageType()));
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValue(CustomFieldStorageTypeEnum.class.getName(), dto.getStorageType());
        }
        cft.setValueRequired(dto.isValueRequired());
        cft.setVersionable(dto.isVersionable());
        cft.setTriggerEndPeriodEvent(dto.isTriggerEndPeriodEvent());
        cft.setEntityClazz(org.apache.commons.lang3.StringUtils.trimToNull(dto.getEntityClazz()));

        if (cft.getFieldType() == CustomFieldTypeEnum.LIST) {
            cft.setListValues(dto.getListValues());
        }

        if (!StringUtils.isBlank(dto.getCalendar())) {
            Calendar calendar = calendarService.findByCode(dto.getCalendar(), currentUser.getProvider());
            if (calendar != null) {
                cft.setCalendar(calendar);
            }
        }
        return cft;
    }
}
