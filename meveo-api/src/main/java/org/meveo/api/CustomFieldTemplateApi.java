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

    public void create(CustomFieldTemplateDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
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

        String appliesTo = postData.getAppliesTo();
        // Support for old API
        if (postData.getAccountLevel() != null) {
            appliesTo = postData.getAccountLevel();
        }
        if (customFieldTemplateService.findByCodeAndAppliesTo(postData.getCode(), appliesTo, currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        CustomFieldTemplate cf = new CustomFieldTemplate();
        cf.setCode(postData.getCode());
        cf.setDescription(postData.getDescription());
        cf.setAppliesTo(appliesTo);
        try {
            cf.setFieldType(CustomFieldTypeEnum.valueOf(postData.getFieldType()));
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValue(CustomFieldTypeEnum.class.getName(), postData.getFieldType());
        }

        cf.setDefaultValue(postData.getDefaultValue());
        try {
            cf.setStorageType(CustomFieldStorageTypeEnum.valueOf(postData.getStorageType()));
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValue(CustomFieldStorageTypeEnum.class.getName(), postData.getStorageType());
        }
        cf.setValueRequired(postData.isValueRequired());
        cf.setVersionable(postData.isVersionable());
        cf.setTriggerEndPeriodEvent(postData.isTriggerEndPeriodEvent());
        cf.setEntityClazz(org.apache.commons.lang3.StringUtils.trimToNull(postData.getEntityClazz()));

        if (cf.getFieldType() == CustomFieldTypeEnum.LIST) {
            cf.setListValues(postData.getListValues());
        }

        if (!StringUtils.isBlank(postData.getCalendar())) {
            Calendar calendar = calendarService.findByCode(postData.getCalendar(), currentUser.getProvider());
            if (calendar != null) {
                cf.setCalendar(calendar);
            }
        }

        customFieldTemplateService.create(cf, currentUser, currentUser.getProvider());

    }

    public void update(CustomFieldTemplateDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
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

        String appliesTo = postData.getAppliesTo();
        // Support for old API
        if (postData.getAccountLevel() != null) {
            appliesTo = postData.getAccountLevel();
        }

        CustomFieldTemplate cf = customFieldTemplateService.findByCodeAndAppliesTo(postData.getCode(), appliesTo, currentUser.getProvider());
        if (cf == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        cf.setDescription(postData.getDescription());
        cf.setAppliesTo(appliesTo);
        try {
            cf.setFieldType(CustomFieldTypeEnum.valueOf(postData.getFieldType()));
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValue(CustomFieldTypeEnum.class.getName(), postData.getFieldType());
        }
        try {
            cf.setStorageType(CustomFieldStorageTypeEnum.valueOf(postData.getStorageType()));
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValue(CustomFieldStorageTypeEnum.class.getName(), postData.getStorageType());
        }

        cf.setDefaultValue(postData.getDefaultValue());
        cf.setValueRequired(postData.isValueRequired());
        cf.setVersionable(postData.isVersionable());
        cf.setTriggerEndPeriodEvent(postData.isTriggerEndPeriodEvent());
        if (!StringUtils.isBlank(postData.getEntityClazz())) {
            cf.setEntityClazz(postData.getEntityClazz());
        }

        if (!StringUtils.isBlank(postData.getCalendar())) {
            Calendar calendar = calendarService.findByCode(postData.getCalendar(), currentUser.getProvider());
            if (calendar != null) {
                cf.setCalendar(calendar);
            }
        }

        if (cf.getFieldType() == CustomFieldTypeEnum.LIST) {
            cf.setListValues(postData.getListValues());
        }

        customFieldTemplateService.update(cf, currentUser);

    }

    public void remove(String code, String appliesTo, Provider provider) throws InvalidEnumValue, EntityDoesNotExistsException, MissingParameterException {
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
        }
    }

    public CustomFieldTemplateDto find(String code, String appliesTo, Provider provider) throws InvalidEnumValue, EntityDoesNotExistsException, MissingParameterException {
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

    public void createOrUpdate(CustomFieldTemplateDto postData, User currentUser) throws MeveoApiException {
        CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCode(postData.getCode(), currentUser.getProvider());
        if (customFieldTemplate == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}
