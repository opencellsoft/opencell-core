package org.meveo.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Oct 15, 2013
 **/
public abstract class BaseApi {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;
	
	@Inject
    private Validator validator;
	
	protected List<String> missingParameters = new ArrayList<String>();

	protected String getMissingParametersExceptionMessage() {

		if (missingParameters == null) {
			missingParameters = new ArrayList<String>();
		}

		StringBuilder sb = new StringBuilder("The following parameters are required ");
		List<String> missingFields = new ArrayList<String>();

		if (missingParameters != null) {
			for (String param : missingParameters) {
				missingFields.add(param);
			}
		}

		if (missingFields.size() > 1) {
			sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
		} else {
			sb.append(missingFields.get(0));
		}
		sb.append(".");

		missingParameters = new ArrayList<String>();

		return sb.toString();
	}

	protected void populateCustomFields(AccountLevelEnum cfType, List<CustomFieldDto> customFieldDtos,
			ICustomFieldEntity entity, User currentUser) throws IllegalArgumentException, IllegalAccessException,
			MissingParameterException {
		populateCustomFields(cfType, customFieldDtos, entity, currentUser, true);
	}

	/**
	 * Populate custom field values from DTO
	 * 
	 * @param cfType
	 *            An entity type that custom field template applies to
	 * @param customFieldDtos
	 *            Custom field values
	 * @param entity
	 *            Entity
	 * @param cfiFieldName
	 *            Custom field name in an entity
	 * @param currentUser
	 *            User that authenticated for API
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws MissingParameterException
	 */
	protected void populateCustomFields(AccountLevelEnum cfType, List<CustomFieldDto> customFieldDtos,
			ICustomFieldEntity entity, User currentUser, boolean checkCustomField) throws IllegalArgumentException,
			IllegalAccessException, MissingParameterException {

		List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(cfType,
				currentUser.getProvider());

		populateCustomFields(customFieldTemplates, customFieldDtos, entity, cfType, currentUser, checkCustomField);
	}

	protected void populateCustomFields(List<CustomFieldTemplate> customFieldTemplates,
			List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, AccountLevelEnum cfType, User currentUser)
			throws IllegalArgumentException, IllegalAccessException, MissingParameterException {
		populateCustomFields(customFieldTemplates, customFieldDtos, entity, cfType, currentUser, true);
	}

	/**
	 * Populate custom field values from DTO
	 * 
	 * @param customFieldTemplates
	 *            Custom field templates
	 * @param customFieldDtos
	 *            Custom field values
	 * @param entity
	 *            Entity
	 * @param cfType
	 *            An entity type that custom field template applies to
	 * @param currentUser
	 *            User that authenticated for API
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws MissingParameterException
	 */
	protected void populateCustomFields(List<CustomFieldTemplate> customFieldTemplates,
			List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, AccountLevelEnum cfType, User currentUser,
			boolean checkCustomFields) throws IllegalArgumentException, IllegalAccessException,
			MissingParameterException {

		// check if any templates are applicable
		if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
			if (customFieldDtos != null && !customFieldDtos.isEmpty()) {
				log.error("No custom field templates defined while Custom field values were passed");
				throw new MissingParameterException(
						"No Custom field templates were found to match provided custom field values");
			} else {
				return;
			}
		}

		for (CustomFieldDto cfDto : customFieldDtos) {
			boolean found = false;
			for (CustomFieldTemplate cft : customFieldTemplates) {
				if (cfDto.getCode().equals(cft.getCode())) {
					found = true;

					// Validate if value is not empty when field is mandatory
					if (cft.isValueRequired()) {
						if (cfDto.isEmpty(cft.getFieldType(), cft.getStorageType())) {
							missingParameters.add(cft.getCode());
							break;
						}
					}

					// Validate parameters
					if (cft.isVersionable()) {
						if ((cfDto.getValueDate() == null && cft.getCalendar() != null)) {
							throw new MissingParameterException(
									"Custom field is versionable by calendar. Missing valueDate parameter.");

						} else if (cft.getCalendar() == null
								&& (cfDto.getValuePeriodStartDate() == null || cfDto.getValuePeriodEndDate() == null)) {
							throw new MissingParameterException(
									"Custom field is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
						}
					}

					CustomFieldInstance cfi = entity.getCustomFields().get(cfDto.getCode());
					// Create an instance if does not exist yet
					if (cfi == null) {
						cfi = CustomFieldInstance.fromTemplate(cft);
						FieldUtils.getField(CustomFieldInstance.class, cfType.getRelationFieldname(), true).set(cfi,
								entity);
						entity.getCustomFields().put(cfi.getCode(), cfi);
					}

					// Update TODO
					cfi.setActive(true);
					cfi.updateAudit(currentUser);

					if (cfi.isVersionable()) {

						if (cfi.getCalendar() != null) {
							cfi.setValue(cfDto.getValueConverted(), cfDto.getValueDate());

						} else {
							cfi.setValue(cfDto.getValueConverted(), cfDto.getValuePeriodStartDate(),
									cfDto.getValuePeriodEndDate());
						}

					} else {
						cfi.setValue(cfDto.getValueConverted());
					}

					break;
				}
			}
			if (checkCustomFields && !found) {
				log.error("No custom field template found with code={} for entity {}. Value will be ignored.",
						cfDto.getCode(), entity.getClass());
				throw new MissingParameterException("Custom field template with code " + cfDto.getCode()
						+ " and provider " + currentUser.getProvider() + " not found.");
			}
		}

		// Validate that CustomField value is not empty when field is mandatory
		for (CustomFieldTemplate cft : customFieldTemplates) {
			if (cft.isDisabled() || !cft.isValueRequired()) {
				continue;
			}
			CustomFieldInstance cfi = entity.getCustomFields().get(cft.getCode());
			if (cfi == null || cfi.isValueEmpty()) {
				missingParameters.add(cft.getCode());
			}
		}

		if (missingParameters.size() > 0) {
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	/**
	 * Validates the DTO based on its constraint annotations 
	 * @param dto
	 * @throws ConstraintViolationException
	 * @throws ValidationException
	 */
	protected void validate(BaseDto dto) throws ConstraintViolationException, ValidationException {
    	
    	Set<ConstraintViolation<BaseDto>> violations = validator.validate(dto);
    	
    	if (!violations.isEmpty()) {
    		throw new ConstraintViolationException(new HashSet<ConstraintViolation<BaseDto>>(violations));
    	}
    	
    }

}
