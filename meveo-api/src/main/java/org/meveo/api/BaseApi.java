package org.meveo.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
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
	protected CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private Validator validator;

	protected List<String> missingParameters = new ArrayList<String>();

	protected String getMissingParametersExceptionMessage() {

		if (missingParameters == null) {
			missingParameters = new ArrayList<String>();
		}

		StringBuilder sb = new StringBuilder("The following parameters are required : ");
		List<String> missingFields = new ArrayList<String>();

		if (missingParameters != null) {
			for (String param : missingParameters) {
				missingFields.add(param);
			}
		}

		if (!missingFields.isEmpty()) {
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");
		}

		missingParameters = new ArrayList<String>();

		return sb.toString();
	}

	/**
	 * Populate custom field values from DTO
	 * 
	 * @param customFieldsDto
	 *            Custom field values
	 * @param entity
	 *            Entity
	 * @param isNewEntity
	 *            Is entity a newly saved entity
	 * @param currentUser
	 *            User that authenticated for API
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws MissingParameterException
	 * @throws BusinessApiException
	 */
	protected void populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity,
			boolean isNewEntity, User currentUser) throws IllegalArgumentException, IllegalAccessException,
			MissingParameterException, BusinessApiException {
		populateCustomFields(customFieldsDto, entity, isNewEntity, currentUser, true);
	}

	/**
	 * Populate custom field values from DTO
	 * 
	 * @param customFieldsDto
	 *            Custom field values
	 * @param entity
	 *            Entity
	 * @param isNewEntity
	 *            Is entity a newly saved entity
	 * @param currentUser
	 *            User that authenticated for API
	 * @param checkCustomField
	 *            Should a check be made if CF field is required
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws MissingParameterException
	 * @throws BusinessApiException
	 */
	protected void populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity,
			boolean isNewEntity, User currentUser, boolean checkCustomField) throws IllegalArgumentException,
			IllegalAccessException, MissingParameterException, BusinessApiException {

		Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity,
				currentUser.getProvider());

		List<CustomFieldDto> customFieldDtos = null;
		if (customFieldsDto != null) {
			customFieldDtos = customFieldsDto.getCustomField();
		} else {
			customFieldDtos = new ArrayList<CustomFieldDto>();
		}

		populateCustomFields(customFieldTemplates, customFieldDtos, entity, isNewEntity, currentUser, checkCustomField);
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
	 * @param isNewEntity
	 *            Is entity a newly saved entity
	 * @param currentUser
	 *            User that authenticated for API
	 * @param checkCustomFields
	 *            Should a check be made if CF field is required
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws MissingParameterException
	 * @throws BusinessApiException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates,
			List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, boolean isNewEntity, User currentUser,
			boolean checkCustomFields) throws IllegalArgumentException, IllegalAccessException,
			MissingParameterException, BusinessApiException {

		// check if any templates are applicable
		if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
			if (customFieldDtos != null && !customFieldDtos.isEmpty()) {
				log.error("No custom field templates defined while Custom field values were passed");
				// in createCRMAccountHierarchy cft in dto can be used in any
				// account level
				// for instance if the current CFT not for a customer then dont
				// throw exception, because the current CFT can be used on
				// billingAccount...
				// TODO need to re think about this for a global fix
				// throw new
				// MissingParameterException("No Custom field templates were found to match provided custom field values");
			} else {
				return;
			}
		}

		if (customFieldDtos != null && !customFieldDtos.isEmpty()) {
			for (CustomFieldDto cfDto : customFieldDtos) {
				CustomFieldTemplate cft = customFieldTemplates.get(cfDto.getCode());

				if (checkCustomFields && cft == null) {
					log.error("No custom field template found with code={} for entity {}. Value will be ignored.",
							cfDto.getCode(), entity.getClass());
					throw new MissingParameterException("Custom field template with code " + cfDto.getCode()
							+ " and provider " + currentUser.getProvider() + " not found.");
				}

                // Ignore the value when creating entity and CFT.hideOnNew=true or editing entity and CFT.allowEdit=false or when CFT.applicableOnEL expression evaluates to false
                if ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit())
                        || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                    log.debug("Custom field value not applicable for this state of entity lifecycle: code={} for entity {} transient{}. Value will be ignored.", cfDto.getCode(),
                        entity.getClass(), isNewEntity);
                    continue;
                }

				// Validate that value is not empty when field is mandatory
				boolean isEmpty = cfDto.isEmpty(cft.getFieldType(), cft.getStorageType());
				if (cft.isValueRequired() && isEmpty) {
					missingParameters.add(cft.getCode());
					continue;

					// Validate that value is valid (min/max, regexp). When
					// value is a list or a map, check separately each value
				} else if (!isEmpty
						&& (cft.getFieldType() == CustomFieldTypeEnum.STRING
								|| cft.getFieldType() == CustomFieldTypeEnum.DOUBLE || cft.getFieldType() == CustomFieldTypeEnum.LONG)) {

					List valuesToCheck = new ArrayList<>();

					Object valueConverted = cfDto.getValueConverted();
					if (valueConverted instanceof Map) {
						valuesToCheck.addAll(((Map) valueConverted).values());

					} else if (valueConverted instanceof List) {
						valuesToCheck.addAll((List) valueConverted);

					} else {
						valuesToCheck.add(valueConverted);
					}

					for (Object valueToCheck : valuesToCheck) {

						if (cft.getFieldType() == CustomFieldTypeEnum.STRING) {
							String stringValue = (String) valueToCheck;

							if (cft.getMaxValue() == null) {
								cft.setMaxValue(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING);
							}
							// Validate String length
							if (stringValue.length() > cft.getMaxValue()) {
								throw new MissingParameterException("Custom field " + cft.getCode() + " value "
										+ stringValue + " length is longer then " + cft.getMaxValue() + " symbols");

								// Validate String regExp
							} else if (cft.getRegExp() != null) {
								try {
									Pattern pattern = Pattern.compile(cft.getRegExp());
									Matcher matcher = pattern.matcher(stringValue);
									if (!matcher.matches()) {
										throw new MissingParameterException("Custom field " + cft.getCode() + " value "
												+ stringValue + " does not match regular expression " + cft.getRegExp());
									}
								} catch (PatternSyntaxException pse) {
									throw new MissingParameterException("Custom field " + cft.getCode()
											+ " definition specifies an invalid regular expression " + cft.getRegExp());
								}
							}

						} else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
							Long longValue = (Long) valueToCheck;

							if (cft.getMaxValue() != null && longValue.compareTo(cft.getMaxValue()) > 0) {
								throw new MissingParameterException("Custom field " + cft.getCode() + " value "
										+ longValue + " is bigger then " + cft.getMaxValue()
										+ ". Allowed value range is from "
										+ (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
										+ (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");

							} else if (cft.getMinValue() != null && longValue.compareTo(cft.getMinValue()) < 0) {
								throw new MissingParameterException("Custom field " + cft.getCode() + " value "
										+ longValue + " is smaller then " + cft.getMinValue()
										+ ". Allowed value range is from "
										+ (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
										+ (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");
							}
						} else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
							Double doubleValue = (Double) valueToCheck;

							if (cft.getMaxValue() != null && doubleValue.compareTo(cft.getMaxValue().doubleValue()) > 0) {
								throw new MissingParameterException("Custom field " + cft.getCode() + " value "
										+ doubleValue + " is bigger then " + cft.getMaxValue()
										+ ". Allowed value range is from "
										+ (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
										+ (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");

							} else if (cft.getMinValue() != null
									&& doubleValue.compareTo(cft.getMinValue().doubleValue()) < 0) {
								throw new MissingParameterException("Custom field " + cft.getCode() + " value "
										+ doubleValue + " is smaller then " + cft.getMinValue()
										+ ". Allowed value range is from "
										+ (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
										+ (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");
							}
						}
					}
				}
				// Validate parameters
				if (cft.isVersionable()) {
					if ((cfDto.getValueDate() == null && cft.getCalendar() != null)) {
						throw new MissingParameterException("Custom field " + cft.getCode()
								+ " is versionable by calendar. Missing valueDate parameter.");

					} else if (cft.getCalendar() == null
							&& (cfDto.getValuePeriodStartDate() == null || cfDto.getValuePeriodEndDate() == null)) {
						throw new MissingParameterException(
								"Custom field "
										+ cft.getCode()
										+ " is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
					}
				}

				// Set the value
				try {
					if (cft.isVersionable()) {
						if (cft.getCalendar() != null) {
							customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(),
									cfDto.getValueDate(), currentUser);

						} else {
							customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(),
									cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate(),
									cfDto.getValuePeriodPriority(), currentUser);
						}

					} else {
						customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(),
								currentUser);
					}

				} catch (Exception e) {
					log.error("Failed to set value {} on custom field {} for entity {}", cfDto.getValueConverted(),
							cfDto.getCode(), entity, e);
					throw new BusinessApiException("Failed to set value " + cfDto.getValueConverted()
							+ " on custom field " + cfDto.getCode() + " for entity " + entity);

				}
			}
		}

		// Validate that CustomField value is not empty when field is mandatory
		Map<String, List<CustomFieldInstance>> cfisAsMap = customFieldInstanceService.getCustomFieldInstances(entity);

        for (CustomFieldTemplate cft : customFieldTemplates.values()) {
            if (cft.isDisabled() || !cft.isValueRequired() || (isNewEntity && cft.isHideOnNew())
                    || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                continue;
            }
			if (!cfisAsMap.containsKey(cft.getCode()) || cfisAsMap.get(cft.getCode()).isEmpty()) {
				missingParameters.add(cft.getCode());
			} else {
				for (CustomFieldInstance cfi : cfisAsMap.get(cft.getCode())) {
					if (cfi == null || cfi.isValueEmpty()) {
						missingParameters.add(cft.getCode());
						break;
					}
				}
			}
		}

		if (missingParameters.size() > 0) {
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	/**
	 * Validates the DTO based on its constraint annotations
	 * 
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
