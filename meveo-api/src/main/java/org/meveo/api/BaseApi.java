package org.meveo.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
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
    private CustomEntityInstanceApi customEntityInstanceApi;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private Validator validator;

    protected List<String> missingParameters = new ArrayList<String>();

    /**
     * Check if any parameters are missing and throw and exception
     * 
     * @throws MissingParameterException
     */
    protected void handleMissingParameters() throws MissingParameterException {
        if (!missingParameters.isEmpty()) {
            MissingParameterException mpe = new MissingParameterException(missingParameters);
            missingParameters.clear();
            throw mpe;
        }
    }

    protected void handleMissingParameters(BaseDto dto, String... fields) throws MissingParameterException {

        for (String fieldName : fields) {

            try {
                Object value;
                value = FieldUtils.readField(dto, fieldName, true);
                if (value == null) {
                    missingParameters.add(fieldName);
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to read field value {}.{}", dto.getClass().getName(), fieldName, e.getMessage());
                missingParameters.add(fieldName);
            }

        }
        handleMissingParameters();
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
                            customEntityInstanceApi.createOrUpdate(ceiDto, currentUser);
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
        if (entity.getParentCFEntities() != null) {
            for (ICustomFieldEntity entityParent : entity.getParentCFEntities()) {
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
                        customEntityInstanceApi.validateEntityInstanceDto((CustomEntityInstanceDto) valueToCheck, currentUser);
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

    /**
     * Convert DTO object to an entity. In addition process child DTO object by creating or updating related entities via calls to API.createOrUpdate(). Note: Does not persist the
     * entity passed to the method.Takes about 1ms longer as compared to a regular hardcoded jpa.value=dto.value assignment
     * 
     * @param entityToPopulate JPA Entity to populate with data from DTO object
     * @param dto DTO object
     * @param partialUpdate Is this a partial update - fields with null values will be ignored
     * @param currentUser Current user
     * @throws MeveoApiException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void convertDtoToEntityWithChildProcessing(Object entityToPopulate, Object dto, boolean partialUpdate, User currentUser) throws MeveoApiException {

        String dtoClassName = dto.getClass().getName();
        for (Field dtoField : FieldUtils.getAllFieldsList(dto.getClass())) {
            if (Modifier.isStatic(dtoField.getModifiers())) {
                continue;
            }

            // log.trace("AKK Populate field {}.{}", dtoClassName, dtoField.getName());
            Object dtoValue = null;
            try {
                dtoValue = dtoField.get(dto);
                if (partialUpdate && dtoValue == null) {
                    continue;
                }

                // Process custom fields as special case
                if (dtoField.getType().isAssignableFrom(CustomFieldsDto.class)) {
                    populateCustomFields((CustomFieldsDto) dtoValue, (ICustomFieldEntity) entityToPopulate, true, currentUser);
                    continue;

                } else if (dtoField.getName().equals("active")) {
                    if (dtoValue != null) {
                        FieldUtils.writeField(entityToPopulate, "disabled", !(boolean) dtoValue, true);
                    }
                    continue;
                }

                Field entityField = FieldUtils.getField(entityToPopulate.getClass(), dtoField.getName(), true);
                if (entityField == null) {
                    log.warn("No match found for field {}.{} in entity {}", dtoClassName, dtoField.getName(), entityToPopulate.getClass().getName());
                    continue;
                }

                // Null value - clear current field value
                if (dtoValue == null) {
                    // TODO Need to handle list, Map and Set type field by clearing them instead of setting them null

                    FieldUtils.writeField(entityToPopulate, dtoField.getName(), dtoValue, true);

                    // Both DTO object and Entity fields are DTO or JPA type fields and require a conversion
                } else if (ReflectionUtils.isDtoOrEntityType(dtoField.getType()) && ReflectionUtils.isDtoOrEntityType(entityField.getType())) {

                    // String entityClassName = dtoValue.getClass().getSimpleName().substring(0, dtoValue.getClass().getSimpleName().lastIndexOf("Dto"));
                    // Class entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClassName, Entity.class);
                    // if (entityClass == null) {
                    // entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClassName, Embeddable.class);
                    // }
                    //
                    // if (entityClass == null) {
                    // log.debug("Don't know how to process a child DTO entity {}. No JPA entity class matched. Will skip the field {}.{}", dtoValue, dtoClassName,
                    // dtoField.getName());
                    // continue;
                    // }
                    Class entityClass = entityField.getType();

                    // Process DTOs that have exposed their own API (extends BaseDto class)
                    if (dtoValue instanceof BaseDto) {

                        // For BusinessEntity DTO, a full DTO entity or only a reference (e.g. Code) is passed
                        if (BusinessEntity.class.isAssignableFrom(entityClass)) {

                            BusinessEntity valueAsEntity = null;
                            String codeValue = (String) FieldUtils.readField(dtoValue, "code", true);

                            // Find an entity referenced
                            if (isEntityReferenceOnly(dtoValue)) {
                                // log.trace("A lookup for {} with code {} will be done as reference was passed", entityClass, codeValue);
                                PersistenceService persistenceService = getPersistenceService(entityClass, true);
                                valueAsEntity = ((BusinessService) persistenceService).findByCode(codeValue, currentUser.getProvider());
                                if (valueAsEntity == null) {
                                    throw new EntityDoesNotExistsException(entityClass, codeValue);
                                }

                                // Create or update a full entity DTO passed
                            } else {

                                ApiService apiService = getApiService((BaseDto) dtoValue, true);
                                valueAsEntity = (BusinessEntity) apiService.createOrUpdate((BaseDto) dtoValue, currentUser);
                            }

                            // Update field with a new entity
                            FieldUtils.writeField(entityToPopulate, dtoField.getName(), valueAsEntity, true);

                            // For non-business entity just Create or update a full entity DTO passed
                        } else {

                            ApiService apiService = getApiService((BaseDto) dtoValue, true);
                            IEntity valueAsEntity = (BusinessEntity) apiService.createOrUpdate((BaseDto) dtoValue, currentUser);

                            // Update field with a new entity
                            FieldUtils.writeField(entityToPopulate, dtoField.getName(), valueAsEntity, true);
                        }

                        // Process other embedded DTO entities
                    } else {

                        // Use existing or create a new entity
                        Object embededEntity = FieldUtils.readField(entityToPopulate, dtoField.getName(), true);
                        if (embededEntity == null) {
                            embededEntity = entityClass.newInstance();
                        }
                        convertDtoToEntityWithChildProcessing(embededEntity, dtoValue, partialUpdate, currentUser);

                        FieldUtils.writeField(entityToPopulate, dtoField.getName(), embededEntity, true);
                    }

                    // DTO field is a simple field (String) representing entity identifier (code) and entity field is a JPA type field
                } else if (!ReflectionUtils.isDtoOrEntityType(dtoField.getType()) && ReflectionUtils.isDtoOrEntityType(entityField.getType())) {

                    Class entityClass = entityField.getType();

                    // Find an entity referenced

                    PersistenceService persistenceService = getPersistenceService(entityClass, true);
                    IEntity valueAsEntity = ((BusinessService) persistenceService).findByCode((String) dtoValue, currentUser.getProvider());
                    if (valueAsEntity == null) {
                        throw new EntityDoesNotExistsException(entityClass, (String) dtoValue);
                    }

                    // Update field with a new entity
                    FieldUtils.writeField(entityToPopulate, dtoField.getName(), valueAsEntity, true);

                    // Regular type like String, Integer, etc..
                } else {
                    FieldUtils.writeField(entityToPopulate, dtoField.getName(), dtoValue, true);
                }

            } catch (MeveoApiException e) {
                log.error("Failed to read/convert/populate field value {}.{}. Value {}. Processing will stop.", dtoClassName, dtoField.getName(), dtoValue, e);
                throw e;

            } catch (Exception e) {

                log.error("Failed to read/convert/populate field value {}.{}. Value {}", dtoClassName, dtoField.getName(), dtoValue, e);
                continue;
            }
        }
    }

    /**
     * Check if DTO object represents only a reference. In case of reference only code and provider fields contain values.
     * 
     * @param objectToEvaluate Dto to evaluate
     * @return True if only code and provider fields contain values
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private boolean isEntityReferenceOnly(Object objectToEvaluate) throws IllegalArgumentException, IllegalAccessException {

        for (Field field : FieldUtils.getAllFieldsList(objectToEvaluate.getClass())) {
            if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive() || field.getName().equals("code") || field.getName().equals("provider")) {
                continue;
            }

            Object fieldValue = field.get(objectToEvaluate);

            if (fieldValue != null) {

                if (ReflectionUtils.isDtoOrEntityType(field.getType())) {
                    if (!isEntityReferenceOnly(fieldValue)) {
                        return false;
                    }
                    continue;
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Get a corresponding API service for a given DTO object. Find API service class first trying with item's classname and then with its super class (a simplified version instead
     * of trying various classsuper classes)
     * 
     * @param dto DTO object
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     * @throws MeveoApiException
     */
    @SuppressWarnings("rawtypes")
    protected ApiService getApiService(BaseDto dto, boolean throwException) throws MeveoApiException {
        String entityClassName = dto.getClass().getSimpleName().substring(0, dto.getClass().getSimpleName().lastIndexOf("Dto"));

        ApiService apiService = (ApiService) EjbUtils.getServiceInterface(entityClassName + "Api");
        if (apiService == null) {
            String entitySuperClassName = dto.getClass().getSuperclass().getSimpleName().substring(0, dto.getClass().getSuperclass().getSimpleName().lastIndexOf("Dto"));
            apiService = (ApiService) EjbUtils.getServiceInterface(entitySuperClassName + "Api");
        }
        if (apiService == null && throwException) {
            throw new MeveoApiException("Failed to find implementation of API service for class " + dto.getClass());
        }

        return apiService;
    }

    /**
     * Find API service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses)
     * 
     * @param classname JPA entity classname
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    protected ApiService getApiService(String classname, boolean throwException) throws ClassNotFoundException {

        Class clazz = Class.forName(classname);
        return getApiService(clazz, throwException);
    }

    /**
     * Find API service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses)
     * 
     * @param entityClass JPA entity class
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    protected ApiService getApiService(Class entityClass, boolean throwException) {

        ApiService apiService = (ApiService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Api");
        if (apiService == null) {
            apiService = (ApiService) EjbUtils.getServiceInterface(entityClass.getSuperclass().getSimpleName() + "Api");
        }
        if (apiService == null && throwException) {
            throw new RuntimeException("Failed to find implementation of API service for class " + entityClass.getName());
        }

        return apiService;
    }

    /**
     * Find Persistence service class a given DTO object. Find API service class first trying with item's classname and then with its super class (a simplified version instead of
     * trying various class superclasses)
     * 
     * @param dto DTO object
     * @param throwException Should exception be thrown if API service is not found
     * @return Persistence service
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    protected PersistenceService getPersistenceService(BaseDto dto, boolean throwException) throws MeveoApiException {
        String entityClassName = dto.getClass().getSimpleName().substring(0, dto.getClass().getSimpleName().lastIndexOf("Dto"));

        PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClassName + "Service");
        if (persistenceService == null) {
            String entitySuperClassName = dto.getClass().getSuperclass().getSimpleName().substring(0, dto.getClass().getSuperclass().getSimpleName().lastIndexOf("Dto"));
            persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entitySuperClassName + "Service");
        }
        if (persistenceService == null && throwException) {
            throw new MeveoApiException("Failed to find implementation of persistence service for class " + dto.getClass());
        }

        return persistenceService;
    }

    /**
     * Find Persistence service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses)
     * 
     * @param entityClass JPA Entity class
     * @param throwException Should exception be thrown if API service is not found
     * @return Persistence service
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    protected PersistenceService getPersistenceService(Class entityClass, boolean throwException) throws MeveoApiException {

        PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
        if (persistenceService == null) {
            persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSuperclass().getSimpleName() + "Service");
        }
        if (persistenceService == null && throwException) {
            throw new MeveoApiException("Failed to find implementation of persistence service for class " + entityClass.getName());
        }

        return persistenceService;
    }
}