package org.meveo.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.api.EntityToDtoConverter;
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

    @EJB
    private CustomEntityApi customEntityApi;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private Validator validator;
    
    protected List<String> missingParameters = new ArrayList<String>();

    protected void handleMissingParameters() throws MissingParameterException {
        if (!missingParameters.isEmpty()) {
            MissingParameterException mpe = new MissingParameterException(missingParameters);
            missingParameters.clear();
            throw mpe;
        }
    }

    /**
     * Populate custom field values from DTO
     * 
     * @param customFieldsDto Custom field values
     * @param entity Entity
     * @param isNewEntity Is entity a newly saved entity
     * @param currentUser User that authenticated for API
     * @throws MeveoApiException
     */
    protected void populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity, User currentUser) throws MeveoApiException {
        populateCustomFields(customFieldsDto, entity, isNewEntity, currentUser, true);
    }

    /**
     * Populate custom field values from DTO
     * 
     * @param customFieldsDto Custom field values
     * @param entity Entity
     * @param isNewEntity Is entity a newly saved entity
     * @param currentUser User that authenticated for API
     * @param checkCustomField Should a check be made if CF field is required
     * @throws MeveoApiException
     */
    protected void populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity, User currentUser, boolean checkCustomField)
            throws MeveoApiException {

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity, currentUser.getProvider());

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
     * @param customFieldTemplates Custom field templates
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param isNewEntity Is entity a newly saved entity
     * @param currentUser User that authenticated for API
     * @param checkCustomFields Should a check be made if CF field is required
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws MeveoApiException
     */
    @SuppressWarnings("unchecked")
    private void populateCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates, List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, boolean isNewEntity,
            User currentUser, boolean checkCustomFields) throws MeveoApiException {

        // check if any templates are applicable
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            if (customFieldDtos != null && !customFieldDtos.isEmpty()) {
                log.error("No custom field templates defined while Custom field values were passed");
                // in createCRMAccountHierarchy cft in dto can be used in any account level
                // for instance if the current CFT not for a customer then dont throw exception, because the current CFT can be used on billingAccount...
                // TODO need to re think about this for a global fix
                // throw new MissingParameterException("No Custom field templates were found to match provided custom field values");
            } else {
                return;
            }
        }

        if (customFieldDtos != null && !customFieldDtos.isEmpty()) {

            // Validate fields
            validateAndConvertCustomFields(customFieldTemplates, customFieldDtos, checkCustomFields, isNewEntity, entity, currentUser);

            // Save the values
            for (CustomFieldDto cfDto : customFieldDtos) {
                CustomFieldTemplate cft = customFieldTemplates.get(cfDto.getCode());

                // Ignore the value when creating entity and CFT.hideOnNew=true or editing entity and CFT.allowEdit=false or when CFT.applicableOnEL expression evaluates to false
                if ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit())
                        || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                    // log.debug("Custom field value not applicable for this state of entity lifecycle: code={} for entity {} transient{}. Value will be ignored.", cfDto.getCode(),
                    // entity.getClass(), isNewEntity);
                    continue;
                }

                Object valueConverted = cfDto.getValueConverted();

                try {

                    // In case of child entity save CustomEntityInstance objects first and then set CF value to a list of EntityReferenceWrapper objects
                    if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {

                        List<EntityReferenceWrapper> childEntityReferences = new ArrayList<>();

                        for (CustomEntityInstanceDto ceiDto : ((List<CustomEntityInstanceDto>) valueConverted)) {
                            customEntityApi.createOrUpdateEntityInstance(ceiDto, currentUser);
                            childEntityReferences.add(new EntityReferenceWrapper(CustomEntityInstance.class.getName(), ceiDto.getCetCode(), ceiDto.getCode()));
                        }

                        customFieldInstanceService.setCFValue(entity, cfDto.getCode(), childEntityReferences, currentUser);

                    } else {

                        if (cft.isVersionable()) {
                            if (cft.getCalendar() != null) {
                                customFieldInstanceService.setCFValue(entity, cfDto.getCode(), valueConverted, cfDto.getValueDate(), currentUser);

                            } else {
                                customFieldInstanceService.setCFValue(entity, cfDto.getCode(), valueConverted, cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate(),
                                    cfDto.getValuePeriodPriority(), currentUser);
                            }

                        } else {
                            customFieldInstanceService.setCFValue(entity, cfDto.getCode(), valueConverted, currentUser);
                        }
                    }

                } catch (Exception e) {
                    log.error("Failed to set value {} on custom field {} for entity {}", valueConverted, cfDto.getCode(), entity, e);
                    if (e instanceof MeveoApiException) {
                        throw (MeveoApiException) e;
                    } else {
                        throw new BusinessApiException("Failed to set value " + valueConverted + " on custom field " + cfDto.getCode() + " for entity " + entity);
                    }
                }
            }
        }

        // After saving passed CF values, validate that CustomField value is not empty when field is mandatory
        Map<String, List<CustomFieldInstance>> cfisAsMap = customFieldInstanceService.getCustomFieldInstances(entity);
        if(entity.getParentCFEntities() != null){
	        for(ICustomFieldEntity entityParent :entity.getParentCFEntities()){
	        	 cfisAsMap.putAll(customFieldInstanceService.getCustomFieldInstances(entityParent));
	        }
        }
               
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

        handleMissingParameters();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void validateAndConvertCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates, List<CustomFieldDto> customFieldDtos, boolean checkCustomFields,
            boolean isNewEntity, ICustomFieldEntity entity, User currentUser) throws InvalidParameterException, MissingParameterException {

        for (CustomFieldDto cfDto : customFieldDtos) {
            CustomFieldTemplate cft = customFieldTemplates.get(cfDto.getCode());

            if (checkCustomFields && cft == null) {
                log.error("No custom field template found with code={} for entity {}. Value will be ignored.", cfDto.getCode(), entity.getClass());
                throw new InvalidParameterException("Custom field template with code " + cfDto.getCode() + " and provider " + currentUser.getProvider() + " not found.");
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
            }

            Object valueConverted = getValueConverted(cfDto);

            // Validate that value is valid (min/max, regexp). When
            // value is a list or a map, check separately each value
            if (!isEmpty
                    && (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE || cft.getFieldType() == CustomFieldTypeEnum.LONG || cft
                        .getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY)) {

                List valuesToCheck = new ArrayList<>();

                if (valueConverted instanceof Map) {

                    // Skip Key item if Storage type is Matrix
                    if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

                        for (Entry<String, Object> mapEntry : ((Map<String, Object>) valueConverted).entrySet()) {
                            if (CustomFieldValue.MAP_KEY.equals(mapEntry.getKey())) {
                                continue;
                            }
                            valuesToCheck.add(mapEntry.getValue());
                        }

                    } else {
                        valuesToCheck.addAll(((Map) valueConverted).values());
                    }

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
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + stringValue + " length is longer then " + cft.getMaxValue()
                                    + " symbols");

                            // Validate String regExp
                        } else if (cft.getRegExp() != null) {
                            try {
                                Pattern pattern = Pattern.compile(cft.getRegExp());
                                Matcher matcher = pattern.matcher(stringValue);
                                if (!matcher.matches()) {
                                    throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + stringValue + " does not match regular expression "
                                            + cft.getRegExp());
                                }
                            } catch (PatternSyntaxException pse) {
                                throw new InvalidParameterException("Custom field " + cft.getCode() + " definition specifies an invalid regular expression " + cft.getRegExp());
                            }
                        }

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                        Long longValue = null;
                        if (valueToCheck instanceof Integer) {
                            longValue = ((Integer) valueToCheck).longValue();
                        } else {
                            longValue = (Long) valueToCheck;
                        }

                        if (cft.getMaxValue() != null && longValue.compareTo(cft.getMaxValue()) > 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + longValue + " is bigger then " + cft.getMaxValue()
                                    + ". Allowed value range is from " + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
                                    + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");

                        } else if (cft.getMinValue() != null && longValue.compareTo(cft.getMinValue()) < 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + longValue + " is smaller then " + cft.getMinValue()
                                    + ". Allowed value range is from " + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
                                    + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");
                        }
                    } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                        Double doubleValue = null;
                        if (valueToCheck instanceof Integer) {
                            doubleValue = ((Integer) valueToCheck).doubleValue();
                        } else {
                            doubleValue = (Double) valueToCheck;
                        }

                        if (cft.getMaxValue() != null && doubleValue.compareTo(cft.getMaxValue().doubleValue()) > 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + doubleValue + " is bigger then " + cft.getMaxValue()
                                    + ". Allowed value range is from " + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
                                    + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");

                        } else if (cft.getMinValue() != null && doubleValue.compareTo(cft.getMinValue().doubleValue()) < 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + doubleValue + " is smaller then " + cft.getMinValue()
                                    + ". Allowed value range is from " + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to "
                                    + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");
                        }

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
                        // Just in case, set CET code to whatever CFT definition requires.
                        ((CustomEntityInstanceDto) valueToCheck).setCetCode(CustomFieldTemplate.retrieveCetCode(cft.getEntityClazz()));
                        customEntityApi.validateEntityInstanceDto((CustomEntityInstanceDto) valueToCheck, currentUser);
                    }
                }
            }

            // Validate parameters
            if (cft.isVersionable()) {
                if ((cfDto.getValueDate() == null && cft.getCalendar() != null)) {
                    throw new MissingParameterException("Custom field " + cft.getCode() + " is versionable by calendar. Missing valueDate parameter.");

                } else if (cft.getCalendar() == null && (cfDto.getValuePeriodStartDate() == null || cfDto.getValuePeriodEndDate() == null)) {
                    throw new MissingParameterException("Custom field " + cft.getCode()
                            + " is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
                }
            }

            // Add keys to matrix if not provided in DTO and it is not empty (gets converted to null if map has no values)
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && valueConverted != null) {

                boolean matrixColumnsPresent = false;
                for (Entry<String, Object> mapEntry : ((Map<String, Object>) valueConverted).entrySet()) {
                    if (CustomFieldValue.MAP_KEY.equals(mapEntry.getKey())) {
                        matrixColumnsPresent = true;
                        break;
                    }
                }

                if (!matrixColumnsPresent) {
                    ((Map<String, Object>) valueConverted).put(CustomFieldValue.MAP_KEY, cft.getMatrixColumnCodes());
                }
            }

            cfDto.setValueConverted(valueConverted);
        }

        handleMissingParameters();
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

    /**
     * Get a value converted from DTO a proper Map, List, EntityWrapper, Date, Long, Double or String value
     * 
     * @return
     */
    protected Object getValueConverted(CustomFieldDto cfDto) {

        if (cfDto.getMapValue() != null && !cfDto.getMapValue().isEmpty()) {
            return CustomFieldValueDto.fromDTO(cfDto.getMapValue());
        } else if (cfDto.getListValue() != null && !cfDto.getListValue().isEmpty()) {
            return CustomFieldValueDto.fromDTO(cfDto.getListValue());
        } else if (cfDto.getStringValue() != null) {
            return cfDto.getStringValue();
        } else if (cfDto.getDateValue() != null) {
            return cfDto.getDateValue();
        } else if (cfDto.getDoubleValue() != null) {
            return cfDto.getDoubleValue();
        } else if (cfDto.getLongValue() != null) {
            return cfDto.getLongValue();
        } else if (cfDto.getEntityReferenceValue() != null) {
            return cfDto.getEntityReferenceValue().fromDTO();
            // } else {
            // Other type values that are of some other DTO type (e.g. CustomEntityInstanceDto for child entity type) are not converted
        }
        return null;
    }
    
    protected <T> T keepOldValueIfNull(T newValue, T oldValue) {
		if (newValue == null) {
			return oldValue;
		}
		return newValue;
	}
}