package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.AccountLevelEnum;
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
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getFieldType()) && !StringUtils.isBlank(postData.getAccountLevel())
				&& !StringUtils.isBlank(postData.getStorageType())) {

			try {
				AccountLevelEnum accountLevel = AccountLevelEnum.valueOf(postData.getAccountLevel());
				if (customFieldTemplateService.findByCodeAndAccountLevel(postData.getCode(), accountLevel,
						currentUser.getProvider()) != null) {
					throw new EntityAlreadyExistsException(CustomFieldTemplate.class, postData.getCode());
				}
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(AccountLevelEnum.class.getName(), postData.getAccountLevel());
			}

			CustomFieldTemplate cf = new CustomFieldTemplate();
			cf.setCode(postData.getCode());
			cf.setDescription(postData.getDescription());
			try {
				cf.setFieldType(CustomFieldTypeEnum.valueOf(postData.getFieldType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(CustomFieldTypeEnum.class.getName(), postData.getFieldType());
			}
			try {
				cf.setAccountLevel(AccountLevelEnum.valueOf(postData.getAccountLevel()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(AccountLevelEnum.class.getName(), postData.getAccountLevel());
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
			cf.setEntityClazz(postData.getEntityClazz());
			
			if(cf.getFieldType() == CustomFieldTypeEnum.LIST ){
				cf.setListValues(postData.getListValues());
			}
			
			if (!StringUtils.isBlank(postData.getCalendar())) {
				Calendar calendar = calendarService.findByCode(postData.getCalendar(), currentUser.getProvider());
				if (calendar != null) {
					cf.setCalendar(calendar);
				}
			}

			customFieldTemplateService.create(cf, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getAccountLevel())) {
				missingParameters.add("accountLevel");
			}
			if (StringUtils.isBlank(postData.getFieldType())) {
				missingParameters.add("fieldType");
			}
			if (StringUtils.isBlank(postData.getStorageType())) {
				missingParameters.add("storageType");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(CustomFieldTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getFieldType()) && !StringUtils.isBlank(postData.getAccountLevel())
				&& !StringUtils.isBlank(postData.getStorageType())) {

			AccountLevelEnum accountLevel = null;
			try {
				accountLevel = AccountLevelEnum.valueOf(postData.getAccountLevel());
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(AccountLevelEnum.class.getName(), postData.getAccountLevel());
			}

			CustomFieldTemplate cf = customFieldTemplateService.findByCodeAndAccountLevel(postData.getCode(),
					accountLevel, currentUser.getProvider());
			if (cf == null) {
				throw new EntityDoesNotExistsException(CustomFieldTemplate.class, postData.getCode());
			}

			cf.setDescription(postData.getDescription());
			try {
				cf.setFieldType(CustomFieldTypeEnum.valueOf(postData.getFieldType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(CustomFieldTypeEnum.class.getName(), postData.getFieldType());
			}
			try {
				cf.setAccountLevel(AccountLevelEnum.valueOf(postData.getAccountLevel()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(AccountLevelEnum.class.getName(), postData.getAccountLevel());
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
			
			if(cf.getFieldType() == CustomFieldTypeEnum.LIST ){
				cf.setListValues(postData.getListValues());
			}

			customFieldTemplateService.update(cf, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getAccountLevel())) {
				missingParameters.add("accountLevel");
			}
			if (StringUtils.isBlank(postData.getFieldType())) {
				missingParameters.add("fieldType");
			}
			if (StringUtils.isBlank(postData.getStorageType())) {
				missingParameters.add("storageType");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String code, String al, Provider provider)  throws InvalidEnumValue, EntityDoesNotExistsException, MissingParameterException   {
		if(StringUtils.isBlank(code)){
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		AccountLevelEnum accountLevel = null;
		CustomFieldTemplate cft =  null;
		if(!StringUtils.isBlank(al)){
			try {
				accountLevel = AccountLevelEnum.valueOf(al);
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(AccountLevelEnum.class.getName(), al);
			}
			 cft = customFieldTemplateService.findByCodeAndAccountLevel(code, accountLevel, provider);
		}else{
			 cft = customFieldTemplateService.findByCodeAndAccountLevel(code, provider);
		}
		if (cft != null) {
			customFieldTemplateService.remove(cft);
		}
	}

	public CustomFieldTemplateDto find(String code, String al, Provider provider) throws InvalidEnumValue, EntityDoesNotExistsException, MissingParameterException   {
		if(StringUtils.isBlank(code)){
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		AccountLevelEnum accountLevel = null;
		CustomFieldTemplate cft =  null;
		if(!StringUtils.isBlank(al)){
			try {
				accountLevel = AccountLevelEnum.valueOf(al);
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(AccountLevelEnum.class.getName(), al);
			}
			 cft = customFieldTemplateService.findByCodeAndAccountLevel(code, accountLevel, provider);
		}else{
			 cft = customFieldTemplateService.findByCodeAndAccountLevel(code, provider);
		}

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