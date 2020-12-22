/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.audit.AuditableFieldDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.ConstraintViolationApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.InvalidReferenceException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AuditableField;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.security.Role;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.util.ApplicationProvider;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @author Said Ramli
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 * 
 */
public abstract class BaseApi {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int limitDefaultValue = 100;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @EJB
    private CustomEntityInstanceApi customEntityInstanceApi;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private Validator validator;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    protected BusinessEntityService businessEntityService;

    @Inject
    private RoleService roleService;

    protected List<String> missingParameters = new ArrayList<>();

    private static final String SUPER_ADMIN_MANAGEMENT = "superAdminManagement";

    @Inject
    private AuditableFieldService auditableFieldService;

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    protected void handleMissingParameters() throws MissingParameterException {
        if (!missingParameters.isEmpty()) {
            MissingParameterException mpe = new MissingParameterException(missingParameters);
            missingParameters.clear();
            throw mpe;
        }
    }

    protected void handleMissingParameters(BaseEntityDto dto) throws MeveoApiException {
        if (dto instanceof BusinessEntityDto) {
            BusinessEntityDto bdto = (BusinessEntityDto) dto;
            boolean allowEntityCodeUpdate = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("service.allowEntityCodeUpdate", "true"));
            if (!allowEntityCodeUpdate && !StringUtils.isBlank(bdto.getUpdatedCode()) && !currentUser.hasRole(SUPER_ADMIN_MANAGEMENT)) {
                throw new org.meveo.api.exception.AccessDeniedException("Super administrator permission is required to update entity code");
            }
            handleMissingCode(bdto);
        }
        handleMissingParameters();
    }

    private void handleMissingCode(BusinessEntityDto dto) throws MeveoApiException {
        if(dto.getCode() == null) {
            String dtoClassName = dto.getClass().getSimpleName();
            String entityClass = dtoClassName.substring(0, dtoClassName.length() - 3);
            CustomGenericEntityCode customGenericEntityCode = customGenericEntityCodeService.findByClass(entityClass);
            if(customGenericEntityCode == null) {
                throw new MeveoApiException("missing mandatory field code");
            }
        }
    }
    /**
     * Check if any parameters are missing and throw and exception.
     * 
     * @param dto base data transfer object.
     * @throws MeveoApiException meveo api exception.
     */
    protected void handleMissingParametersAndValidate(BaseEntityDto dto) throws MeveoApiException {
        validate(dto);
        handleMissingParameters(dto);
    }

    protected void handleMissingParameters(BaseEntityDto dto, String... fields) throws MeveoApiException {

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
     * Populate custom field values from DTO.
     * 
     * @param customFieldsDto Custom field values
     * @param entity Entity
     * @param isNewEntity Is entity a newly saved entity
     * 
     * @throws MeveoApiException meveo api exception.
     */
    protected ICustomFieldEntity populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity) throws MeveoApiException {
        return populateCustomFields(customFieldsDto, entity, isNewEntity, true);
    }

    /**
     * Populate custom field values from DTO.
     * 
     * @param customFieldsDto Custom field values
     * @param entity Entity
     * @param isNewEntity Is entity a newly saved entity
     * 
     * @param checkCustomField Should a check be made if CF field is required
     * @throws MeveoApiException meveo api exception.
     */
    protected ICustomFieldEntity populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity, boolean checkCustomField) throws MeveoApiException {

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity);

        List<CustomFieldDto> customFieldDtos = null;
        if (customFieldsDto != null) {
            customFieldDtos = customFieldsDto.getCustomField();
        } else {
            customFieldDtos = new ArrayList<CustomFieldDto>();
        }

        return populateCustomFields(customFieldTemplates, customFieldDtos, entity, isNewEntity, checkCustomField);
    }

    /**
     * Populate custom field values from DTO.
     * 
     * @param customFieldTemplates Custom field templates
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param isNewEntity Is entity a newly saved entity
     * 
     * @param checkCustomFields Should a check be made if CF field is required
     * @throws IllegalArgumentException illegal argument exception
     * @throws IllegalAccessException illegal access exception
     * @throws MeveoApiException
     */
    @SuppressWarnings("unchecked")
    private ICustomFieldEntity populateCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates, List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, boolean isNewEntity, boolean checkCustomFields)
            throws MeveoApiException {

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
                // throw new MissingParameterException("No Custom field
                // templates were found to match provided custom field values");
            } else {
                return entity;
            }
        }

        if (customFieldDtos != null && !customFieldDtos.isEmpty()) {

            // Validate fields
            validateAndConvertCustomFields(customFieldTemplates, customFieldDtos, checkCustomFields, isNewEntity, entity);

            // Save the values
            for (CustomFieldDto cfDto : customFieldDtos) {
                CustomFieldTemplate cft = null;
                if (customFieldTemplates != null) {
                    cft = customFieldTemplates.get(cfDto.getCode());
                }

                // Ignore the value when creating entity and CFT.hideOnNew=true
                // or editing entity and CFT.allowEdit=false or when
                // CFT.applicableOnEL expression evaluates to false
                if (cft != null && ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit()) || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity))) {
                    // log.debug("Custom field value not applicable for this
                    // state of entity lifecycle: code={} for entity {}
                    // transient{}. Value will be ignored.", cfDto.getCode(),
                    // entity.getClass(), isNewEntity);
                    continue;
                }

                Object valueConverted = cfDto.getValueConverted();

                try {

                    // In case of child entity save CustomEntityInstance objects
                    // first and then set CF value to a list of
                    // EntityReferenceWrapper objects
                    if (cft != null && cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {

                        List<EntityReferenceWrapper> childEntityReferences = new ArrayList<>();

                        for (CustomEntityInstanceDto ceiDto : ((List<CustomEntityInstanceDto>) valueConverted)) {
                            customEntityInstanceApi.createOrUpdate(ceiDto);
                            childEntityReferences.add(new EntityReferenceWrapper(CustomEntityInstance.class.getName(), ceiDto.getCetCode(), ceiDto.getCode()));
                        }

                        customFieldInstanceService.setCFValue(entity, cfDto.getCode(), childEntityReferences);

                    } else if (cft != null) {

                        if (cft.isVersionable()) {
                            if (cft.getCalendar() != null) {
                                customFieldInstanceService.setCFValue(entity, cfDto.getCode(), valueConverted, cfDto.getValueDate());

                            } else {
                                customFieldInstanceService.setCFValue(entity, cfDto.getCode(), valueConverted, cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate(), cfDto.getValuePeriodPriority());
                            }

                        } else {
                            customFieldInstanceService.setCFValue(entity, cfDto.getCode(), valueConverted);
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

        // After saving passed CF values, validate that CustomField value is not
        // empty when field is mandatory. Check inherited values as well.
        // Instantiate CF with default value in case of a new entity
        if (customFieldTemplates != null) {

            for (CustomFieldTemplate cft : customFieldTemplates.values()) {

                boolean cftValueRequired = cft.isValueRequired();

                if (cft.isDisabled() || (!cftValueRequired && cft.getDefaultValue() == null && !cft.isUseInheritedAsDefaultValue())) {
                    continue;
                }

                // Does not apply at this moment
                if ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit()) || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                    continue;
                }

                boolean hasValue = entity.hasCfValue(cft.getCode());

                // When no instance was found
                if (!hasValue) {

                    // Need to instantiate default value either from inherited value or from a default value when cft.isInheritedAsDefaultValue()==true
                    if (isNewEntity && cft.isUseInheritedAsDefaultValue()) {
                        Object value = customFieldInstanceService.instantiateCFWithInheritedOrDefaultValue(entity, cft);
                        hasValue = value != null;
                    }

                    // If no value was created, then check if there is any inherited value, as in case of versioned values, value could be set in some other period, and required
                    // field validation should pass even though current period wont have any value
                    if (!hasValue) {
                        if (cft.isVersionable()) {
                            hasValue = customFieldInstanceService.hasInheritedOnlyCFValue(entity, cft.getCode());
                        } else {
                            Object value = customFieldInstanceService.getInheritedOnlyCFValue(entity, cft.getCode());
                            hasValue = value != null;
                        }

                        if (!hasValue && cft.getDefaultValue() != null && (isNewEntity || cftValueRequired)) { // No need to check for !cft.isInheritedAsDefaultValue() as it was
                                                                                                               // checked above
                            Object value = customFieldInstanceService.instantiateCFWithDefaultValue(entity, cft.getCode());
                            hasValue = value != null;
                        }
                    }

                    if (!hasValue && cftValueRequired) {
                        missingParameters.add(cft.getCode());
                    }

                    // When instance, or multiple instances in case of versioned values, were found
                    // in case of empty value, check that inherited value is available or instantiate it from an inherited value if needed
                } else {

                    boolean emptyValue = entity.hasCFValueNotEmpty(cft.getCode());

                    if (emptyValue) {
                        Object value = customFieldInstanceService.getInheritedOnlyCFValue(entity, cft.getCode());

                        if (isNewEntity && !emptyValue && ((value == null && cft.getDefaultValue() != null) || cft.isUseInheritedAsDefaultValue())) {
                            value = customFieldInstanceService.instantiateCFWithInheritedOrDefaultValue(entity, cft);
                        }
                        if (value == null && cftValueRequired) {
                            missingParameters.add(cft.getCode());
                        }
                    }
                }
            }
        }

        handleMissingParameters();
        return entity;
    }

    protected void validateAndConvertCustomFields(List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity) throws MeveoApiException {
        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity);
        this.validateAndConvertCustomFields(customFieldTemplates, customFieldDtos, true, false, entity);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void validateAndConvertCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates, List<CustomFieldDto> customFieldDtos, boolean checkCustomFields, boolean isNewEntity, ICustomFieldEntity entity)
            throws MeveoApiException {

        if (customFieldDtos == null) {
            return;
        }

        for (CustomFieldDto cfDto : customFieldDtos) {
            CustomFieldTemplate cft = customFieldTemplates.get(cfDto.getCode());

            if (checkCustomFields && cft == null) {
                log.error("No custom field template found with code={} for entity {}. Value will be ignored.", cfDto.getCode(), entity.getClass());
                throw new InvalidParameterException("Custom field template with code " + cfDto.getCode() + " not found.");
            }

            // Ignore the value when creating entity and CFT.hideOnNew=true or
            // editing entity and CFT.allowEdit=false or when CFT.applicableOnEL
            // expression evaluates to false
            if ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit()) || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                log.debug("Custom field value not applicable for this state of entity lifecycle: code={} for entity {} transient{}. Value will be ignored.", cfDto.getCode(), entity.getClass(), isNewEntity);
                continue;
            }

            // Validate that value is not empty when field is mandatory
            boolean isEmpty = cfDto.isEmpty(cft.getFieldType(), cft.getStorageType());
            if (cft.isValueRequired() && isEmpty) {
                missingParameters.add(cft.getCode());
                continue;
            }

            Object valueConverted = getValueConverted(cfDto, cft);

            // Validate that value is valid (min/max, regexp). When
            // value is a list or a map, check separately each value
            if (!isEmpty && (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE || cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN
                    || cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY)) {

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
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + stringValue + " length is longer then " + cft.getMaxValue() + " symbols");

                            // Validate String regExp
                        } else if (cft.getRegExp() != null) {
                            try {
                                Pattern pattern = Pattern.compile(cft.getRegExp());
                                Matcher matcher = pattern.matcher(stringValue);
                                if (!matcher.matches()) {
                                    throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + stringValue + " does not match regular expression " + cft.getRegExp());
                                }
                            } catch (PatternSyntaxException pse) {
                                throw new InvalidParameterException("Custom field " + cft.getCode() + " definition specifies an invalid regular expression " + cft.getRegExp());
                            }
                        }

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                        Long longValue = null;
                        if (valueToCheck instanceof Integer) {
                            longValue = ((Integer) valueToCheck).longValue();
                        } else if (valueToCheck instanceof Double) {
                            longValue = ((Double) valueToCheck).longValue();
                        } else {
                            longValue = (Long) valueToCheck;
                        }

                        if (cft.getMaxValue() != null && longValue.compareTo(cft.getMaxValue()) > 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + longValue + " is bigger then " + cft.getMaxValue() + ". Allowed value range is from "
                                    + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to " + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");

                        } else if (cft.getMinValue() != null && longValue.compareTo(cft.getMinValue()) < 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + longValue + " is smaller then " + cft.getMinValue() + ". Allowed value range is from "
                                    + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to " + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");
                        }
                    } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                        Double doubleValue = null;
                        if (valueToCheck instanceof Integer) {
                            doubleValue = ((Integer) valueToCheck).doubleValue();
                        } else if (valueToCheck instanceof Long) {
                            doubleValue = ((Long) valueToCheck).doubleValue();
                        } else {
                            doubleValue = (Double) valueToCheck;
                        }

                        if (cft.getMaxValue() != null && doubleValue.compareTo(cft.getMaxValue().doubleValue()) > 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + doubleValue + " is bigger then " + cft.getMaxValue() + ". Allowed value range is from "
                                    + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to " + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");

                        } else if (cft.getMinValue() != null && doubleValue.compareTo(cft.getMinValue().doubleValue()) < 0) {
                            throw new InvalidParameterException("Custom field " + cft.getCode() + " value " + doubleValue + " is smaller then " + cft.getMinValue() + ". Allowed value range is from "
                                    + (cft.getMinValue() == null ? "unspecified" : cft.getMinValue()) + " to " + (cft.getMaxValue() == null ? "unspecified" : cft.getMaxValue()) + ".");
                        }
                    } else if (cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN) {
                        Boolean booleanValue = Boolean.valueOf(valueToCheck.toString());

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
                        // Just in case, set CET code to whatever CFT definition
                        // requires.
                        ((CustomEntityInstanceDto) valueToCheck).setCetCode(CustomFieldTemplate.retrieveCetCode(cft.getEntityClazz()));
                        customEntityInstanceApi.validateEntityInstanceDto((CustomEntityInstanceDto) valueToCheck);
                    }
                }
            }

            // Validate parameters
            if (cft.isVersionable()) {
                if ((cfDto.getValueDate() == null && cft.getCalendar() != null)) {
                    throw new MissingParameterException("Custom field " + cft.getCode() + " is versionable by calendar. Missing valueDate parameter.");

                    // } else if (cft.getCalendar() == null && (cfDto.getValuePeriodStartDate() == null || cfDto.getValuePeriodEndDate() == null)) {
                    // throw new MissingParameterException(
                    // "Custom field " + cft.getCode() + " is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
                }
            }

            // Add keys to matrix if not provided in DTO and it is not empty
            // (gets converted to null if map has no values)
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && valueConverted != null) {
                boolean matrixColumnsPresent = false;
                for (Entry<String, Object> mapEntry : ((Map<String, Object>) valueConverted).entrySet()) {
                    if (CustomFieldValue.MAP_KEY.equals(mapEntry.getKey())) {
                        matrixColumnsPresent = true;
                    }else {
                    	int keySize=mapEntry.getKey() == null ? 0 : Stream.of(mapEntry.getKey().split("\\" +CustomFieldValue.MATRIX_KEY_SEPARATOR)).collect(Collectors.toList()).size();
                    	int valueSize=mapEntry.getValue() == null ? 0 : Stream.of(mapEntry.getValue().toString().split("\\" +CustomFieldValue.MATRIX_KEY_SEPARATOR)).collect(Collectors.toList()).size();

                    	int matrixKeySize = cft.getMatrixKeyColumns() != null ? cft.getMatrixKeyColumns().size() : 0;
						if(matrixKeySize>0 && matrixKeySize<keySize) {
                    		throw new BusinessApiException("invalid matrix key format for '"+mapEntry.getKey()+"', number of keys is "+keySize+", greater than matrix key definition ("+matrixKeySize+")") ;
                    	}
                    	int matrixValueSize = cft.getMatrixValueColumns()!=null ? cft.getMatrixValueColumns().size() : 0;
						if(matrixValueSize>0 && matrixValueSize<valueSize) {
                    		throw new BusinessApiException("invalid matrix value format for '"+mapEntry.getValue().toString()+"', number of values is "+valueSize+", greater than matrix value definition ("+matrixValueSize+")") ;
                    	}
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
     * Validates the DTO based on its constraint annotations.
     * 
     * @param dto data transfer object.
     * @throws ConstraintViolationException constraint violation exception.
     * @throws MeveoApiException meveo api exception.
     */
    public void validate(Object dto) throws MeveoApiException {

        if (dto == null) {
            return;
        }

        Set<ConstraintViolation<Object>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<? extends ConstraintViolation<?>> it = violations.iterator();
            while (it.hasNext()) {
                ConstraintViolation<?> i = it.next();
                sb.append(i.getPropertyPath().toString() + " " + i.getMessage() + "|");
            }
            sb.delete(sb.length() - 1, sb.length());

            throw new InvalidParameterException(sb.toString());
        }
    }

    /**
     * From DTO.
     *
     * @param cft the custom field template
     * @param cfDto the custom field dto.
     * 
     * @return the list or the linked hash map
     */
    private Object fromDTO(CustomFieldTemplate cft, CustomFieldDto cfDto) {
        Object values = null;
        if (!StringUtils.isBlank(cfDto.getFileValue())) {
            values = customFieldTemplateService.serializeFromFile(cft, cfDto);
        }
        return values;
    }

    /**
     * Get a value converted from DTO a proper Map, List, EntityWrapper, Date, Long, Double or String value.
     * 
     * @param cfDto cf dto.
     * @param cft the the custom field template
     * @return custom field converted object.
     */
    private Object getValueConverted(CustomFieldDto cfDto, CustomFieldTemplate cft) {

        if (cfDto.getMapValue() != null && !cfDto.getMapValue().isEmpty()) {
            return CustomFieldValueDto.fromDTO(cfDto.getMapValue());
        } else if (cfDto.getListValue() != null && !cfDto.getListValue().isEmpty()) {
            return CustomFieldValueDto.fromDTO(cfDto.getListValue());
        } else if (!StringUtils.isBlank(cfDto.getFileValue())) {
            return fromDTO(cft, cfDto);
        } else if (cfDto.getStringValue() != null) {
            return cfDto.getStringValue();
        } else if (cfDto.getDateValue() != null) {
            return cfDto.getDateValue();
        } else if (cfDto.getDoubleValue() != null) {
            return getDoubleValue(cfDto, cft);
        } else if (cfDto.getBooleanValue() != null) {
            return cfDto.getBooleanValue();
        } else if (cfDto.getLongValue() != null) {
            return cfDto.getLongValue();
        } else if (cfDto.getEntityReferenceValue() != null) {
            return cfDto.getEntityReferenceValue().fromDTO();
            // } else {
            // Other type values that are of some other DTO type (e.g.
            // CustomEntityInstanceDto for child entity type) are not converted
        }
        return null;
    }

    /**
     * Get a value converted from DTO a proper Map, List, EntityWrapper, Date, Long, Double or String value.
     * 
     * @param cfDto cf dto.
     * @return custom field converted object.
     */
    protected Object getValueConverted(CustomFieldDto cfDto) {
        return getValueConverted(cfDto, null);
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
     * 
     * @throws MeveoApiException meveo api exception.
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
    protected void convertDtoToEntityWithChildProcessing(Object entityToPopulate, Object dto, boolean partialUpdate) throws MeveoApiException {

        String dtoClassName = dto.getClass().getName();
        for (Field dtoField : FieldUtils.getAllFieldsList(dto.getClass())) {
            if (Modifier.isStatic(dtoField.getModifiers())) {
                continue;
            }

            // log.trace("AKK Populate field {}.{}", dtoClassName,
            // dtoField.getName());
            Object dtoValue = null;
            try {
                dtoValue = dtoField.get(dto);
                if (partialUpdate && dtoValue == null) {
                    continue;
                }

                // Process custom fields as special case
                if (dtoField.getType().isAssignableFrom(CustomFieldsDto.class)) {
                    populateCustomFields((CustomFieldsDto) dtoValue, (ICustomFieldEntity) entityToPopulate, true);
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
                    // TODO Need to handle list, Map and Set type field by
                    // clearing them instead of setting them null

                    FieldUtils.writeField(entityToPopulate, dtoField.getName(), dtoValue, true);

                    // Both DTO object and Entity fields are DTO or JPA type
                    // fields and require a conversion
                } else if (ReflectionUtils.isDtoOrEntityType(dtoField.getType()) && ReflectionUtils.isDtoOrEntityType(entityField.getType())) {

                    // String entityClassName =
                    // dtoValue.getClass().getSimpleName().substring(0,
                    // dtoValue.getClass().getSimpleName().lastIndexOf("Dto"));
                    // Class entityClass =
                    // ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClassName,
                    // Entity.class);
                    // if (entityClass == null) {
                    // entityClass =
                    // ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClassName,
                    // Embeddable.class);
                    // }
                    //
                    // if (entityClass == null) {
                    // log.debug("Don't know how to process a child DTO entity
                    // {}. No JPA entity class matched. Will skip the field
                    // {}.{}", dtoValue, dtoClassName,
                    // dtoField.getName());
                    // continue;
                    // }
                    Class entityClass = entityField.getType();

                    // Process DTOs that have exposed their own API (extends
                    // BaseEntityDto class)
                    if (dtoValue instanceof BaseEntityDto) {

                        // For BusinessEntity DTO, a full DTO entity or only a
                        // reference (e.g. Code) is passed
                        if (BusinessEntity.class.isAssignableFrom(entityClass)) {

                            BusinessEntity valueAsEntity = null;
                            String codeValue = (String) FieldUtils.readField(dtoValue, "code", true);

                            // Find an entity referenced
                            if (isEntityReferenceOnly(dtoValue)) {
                                // log.trace("A lookup for {} with code {} will
                                // be done as reference was passed",
                                // entityClass, codeValue);
                                PersistenceService persistenceService = getPersistenceService(entityClass, true);
                                valueAsEntity = ((BusinessService) persistenceService).findByCode(codeValue);
                                if (valueAsEntity == null) {
                                    throw new EntityDoesNotExistsException(entityClass, codeValue);
                                }

                                // Create or update a full entity DTO passed
                            } else {

                                ApiService apiService = getApiService((BusinessEntityDto) dtoValue, true);
                                valueAsEntity = (BusinessEntity) apiService.createOrUpdate((BusinessEntityDto) dtoValue);
                            }

                            // Update field with a new entity
                            FieldUtils.writeField(entityToPopulate, dtoField.getName(), valueAsEntity, true);

                            // For non-business entity just Create or update a
                            // full entity DTO passed
                        } else {

                            ApiService apiService = getApiService((BusinessEntityDto) dtoValue, true);
                            IEntity valueAsEntity = (BusinessEntity) apiService.createOrUpdate((BusinessEntityDto) dtoValue);

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
                        convertDtoToEntityWithChildProcessing(embededEntity, dtoValue, partialUpdate);

                        FieldUtils.writeField(entityToPopulate, dtoField.getName(), embededEntity, true);
                    }

                    // DTO field is a simple field (String) representing entity
                    // identifier (code) and entity field is a JPA type field
                } else if (!ReflectionUtils.isDtoOrEntityType(dtoField.getType()) && ReflectionUtils.isDtoOrEntityType(entityField.getType())) {

                    Class entityClass = entityField.getType();

                    // Find an entity referenced

                    PersistenceService persistenceService = getPersistenceService(entityClass, true);
                    IEntity valueAsEntity = ((BusinessService) persistenceService).findByCode((String) dtoValue);
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
     * @throws IllegalAccessException illegal access exception
     * @throws IllegalArgumentException illegal argumen exception.
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
     * @throws MeveoApiException meveo api exception
     * @throws ClassNotFoundException class not found exception.
     */
    @SuppressWarnings("rawtypes")
    protected ApiService getApiService(BaseEntityDto dto, boolean throwException) throws MeveoApiException, ClassNotFoundException {
        String entityClassName = dto.getClass().getSimpleName().substring(0, dto.getClass().getSimpleName().lastIndexOf("Dto"));

        return getApiService(entityClassName, throwException);
    }

    /**
     * Find API service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses).
     * 
     * @param classname JPA entity classname
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     * @throws ClassNotFoundException class not found exception.
     */
    @SuppressWarnings("rawtypes")
    protected ApiService getApiService(String classname, boolean throwException) throws ClassNotFoundException {

        Class clazz = Class.forName(classname);
        return getApiService(clazz, throwException);
    }

    /**
     * Find API service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses).
     * 
     * @param entityClass JPA entity class
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     * 
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
     * Find API versioned service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses).
     * 
     * @param classname JPA entity classname
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     * @throws ClassNotFoundException class not found exception.
     */
    @SuppressWarnings("rawtypes")
    protected ApiVersionedService getApiVersionedService(String classname, boolean throwException) throws ClassNotFoundException {

        Class clazz = Class.forName(classname);
        return getApiVersionedService(clazz, throwException);
    }

    /**
     * Find API versioned service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses).
     * 
     * @param entityClass JPA entity class
     * @param throwException Should exception be thrown if API service is not found
     * @return Api service
     *
     */
    @SuppressWarnings("rawtypes")
    protected ApiVersionedService getApiVersionedService(Class entityClass, boolean throwException) {

        ApiVersionedService apiService = (ApiVersionedService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Api");
        if (apiService == null) {
            apiService = (ApiVersionedService) EjbUtils.getServiceInterface(entityClass.getSuperclass().getSimpleName() + "Api");
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
     * @throws MeveoApiException meveo api exception.
     */
    @SuppressWarnings("rawtypes")
    protected PersistenceService getPersistenceService(BaseEntityDto dto, boolean throwException) throws MeveoApiException {
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
     * Find Persistence service class first trying with JPA entity's classname and then with its super class (a simplified version instead of trying various class superclasses).
     * 
     * @param entityClass JPA Entity class
     * @param throwException Should exception be thrown if API service is not found
     * @return Persistence service
     * @throws BusinessException A general business exception
     */
    @SuppressWarnings("rawtypes")
    protected PersistenceService getPersistenceService(Class entityClass, boolean throwException) throws BusinessException {

        PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
        if (persistenceService == null) {
            persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSuperclass().getSimpleName() + "Service");
        }
        if (persistenceService == null && throwException) {
            throw new BusinessException("Failed to find implementation of persistence service for class " + entityClass.getName());
        }

        return persistenceService;
    }

    protected void saveImage(IEntity entity, String imagePath, String imageData) throws IOException, MeveoApiException {

        // No image to save
        if (StringUtils.isBlank(imageData)) {
            return;
        }

        if (StringUtils.isBlank(imagePath)) {
            missingParameters.add("imagePath");
            handleMissingParametersAndValidate(null);
        }

        try {
            ImageUploadEventHandler<IEntity> imageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
            String filename = imageUploadEventHandler.saveImage(entity, imagePath, Base64.decodeBase64(imageData));
            if (filename != null) {
                ((IImageUpload) entity).setImagePath(filename);
            }
        } catch (AccessDeniedException e1) {
            throw new InvalidImageData("Failed saving image. Access is denied: " + e1.getMessage());
        } catch (IOException e) {
            throw new InvalidImageData("Failed saving image. " + e.getMessage());
        }
    }

    protected void deleteImage(IEntity entity) throws InvalidImageData {
        try {
            ImageUploadEventHandler<IEntity> imageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
            imageUploadEventHandler.deleteImage(entity);
        } catch (AccessDeniedException e1) {
            throw new InvalidImageData("Failed deleting image. Access is denied: " + e1.getMessage());
        } catch (IOException e) {
            throw new InvalidImageData("Failed deleting image. " + e.getMessage());
        }
    }

    /**
     * Convert multi language field DTO values into a map of values with language code as a key.
     * 
     * @param translationInfos Multi langauge field DTO values
     * @param currentValues map of current values.
     * @return Map of values with language code as a key
     * @throws InvalidParameterException invalid parameter exception.
     */
    protected Map<String, String> convertMultiLanguageToMapOfValues(List<LanguageDescriptionDto> translationInfos, Map<String, String> currentValues) throws InvalidParameterException {
        if (translationInfos == null || translationInfos.isEmpty()) {
            return null;
        }

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        Map<String, String> values = null;
        if (currentValues == null) {
            values = new HashMap<>();
        } else {
            values = currentValues;
        }

        for (LanguageDescriptionDto translationInfo : translationInfos) {
            if (!supportedLanguages.contains(translationInfo.getLanguageCode())) {
                throw new InvalidParameterException("Language " + translationInfo.getLanguageCode() + " is not supported by the provider.");
            }
            if (StringUtils.isBlank(translationInfo.getDescription())) {
                values.remove(translationInfo.getLanguageCode());
            } else {
                values.put(translationInfo.getLanguageCode(), translationInfo.getDescription());
            }
        }

        if (values.isEmpty()) {
            return null;
        } else {
            return values;
        }
    }

    /**
     * Convert pagination and filtering DTO to a pagination configuration used in services.
     * 
     * @param defaultSortBy A default value to sortBy
     * @param defaultSortOrder A default sort order
     * @param fetchFields Fields to fetch
     * @param pagingAndFiltering Paging and filtering criteria
     * @param targetClass class which is used for pagination.
     * @return Pagination configuration
     * @throws InvalidParameterException invalid parameter exception.
     */
    @SuppressWarnings("rawtypes")
    protected PaginationConfiguration toPaginationConfiguration(String defaultSortBy, SortOrder defaultSortOrder, List<String> fetchFields, PagingAndFiltering pagingAndFiltering, Class targetClass)
            throws InvalidParameterException {

        if (pagingAndFiltering != null && targetClass != null) {
            Map<String, CustomFieldTemplate> cfts = null;
            if (ICustomFieldEntity.class.isAssignableFrom(targetClass)) {
                cfts = customFieldTemplateService.findByAppliesTo(targetClass.getSimpleName());
            }
            pagingAndFiltering.setFilters(convertFilters(targetClass, pagingAndFiltering.getFilters(), cfts));
        }
        PaginationConfiguration paginationConfig = initPaginationConfiguration(defaultSortBy, defaultSortOrder, fetchFields, pagingAndFiltering);
        return paginationConfig;
    }

    /**
     * Convert pagination and filtering DTO to a pagination configuration used in services.
     * 
     * @param defaultSortBy A default value to sortBy
     * @param defaultSortOrder A default sort order
     * @param fetchFields Fields to fetch
     * @param pagingAndFiltering Paging and filtering criteria
     * @param cetCodeOrDbTableName Custom entity template code or DB table name
     * @return Pagination configuration
     * @throws InvalidParameterException invalid parameter exception.
     */
    protected PaginationConfiguration toPaginationConfiguration(String defaultSortBy, SortOrder defaultSortOrder, List<String> fetchFields, PagingAndFiltering pagingAndFiltering, String cetCodeOrDbTableName)
            throws InvalidParameterException {

        if (pagingAndFiltering != null && cetCodeOrDbTableName != null) {

            CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(cetCodeOrDbTableName);
            if (cet == null) {
                throw new EntityDoesNotExistsException(CustomEntityTemplate.class, cetCodeOrDbTableName);
            }
            Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());

            pagingAndFiltering.setFilters(convertFilters(cet.isStoreAsTable() ? null : CustomEntityInstance.class, pagingAndFiltering.getFilters(), cfts));
        }
        PaginationConfiguration paginationConfig = initPaginationConfiguration(defaultSortBy, defaultSortOrder, fetchFields, pagingAndFiltering);
        return paginationConfig;
    }

    /**
     * Convert pagination and filtering DTO to a pagination configuration used in services.
     * 
     * @param defaultSortBy A default value to sortBy
     * @param defaultSortOrder A default sort order
     * @param fetchFields Fields to fetch
     * @param pagingAndFiltering Paging and filtering criteria
     * @param targetClass class which is used for pagination.
     * @return Pagination configuration
     * @throws InvalidParameterException invalid parameter exception.
     */
    protected PaginationConfiguration toPaginationConfiguration(String defaultSortBy, SortOrder defaultSortOrder, List<String> fetchFields, PagingAndFiltering pagingAndFiltering, Map<String, CustomFieldTemplate> cfts)
            throws InvalidParameterException {

        if (pagingAndFiltering != null && cfts != null) {
            pagingAndFiltering.setFilters(convertFilters(null, pagingAndFiltering.getFilters(), cfts));
        }
        PaginationConfiguration paginationConfig = initPaginationConfiguration(defaultSortBy, defaultSortOrder, fetchFields, pagingAndFiltering);
        return paginationConfig;
    }

    private PaginationConfiguration initPaginationConfiguration(String defaultSortBy, SortOrder defaultSortOrder, List<String> fetchFields, PagingAndFiltering pagingAndFiltering) {
        Integer limit = paramBean.getPropertyAsInteger("api.list.defaultLimit", limitDefaultValue);
        if (pagingAndFiltering != null) {
            if (pagingAndFiltering.getLimit() != null) {
                limit = pagingAndFiltering.getLimit();
            } else {
                pagingAndFiltering.setLimit(limit);
            }
        }

        // Commented out as regular API and customTable API has a different meaning of fields parameter - in customTableApi it will return only those fields, whereas in regularAPI
        // it will consider as fields to join with.
        // fetchFields = fetchFields!=null? fetchFields: pagingAndFiltering!=null && pagingAndFiltering.getFields() != null ?
        // Arrays.asList(pagingAndFiltering.getFields().split(",")) : null;
        return new PaginationConfiguration(pagingAndFiltering != null ? pagingAndFiltering.getOffset() : null, limit, pagingAndFiltering != null ? pagingAndFiltering.getFilters() : null,
            pagingAndFiltering != null ? pagingAndFiltering.getFullTextFilter() : null, fetchFields, pagingAndFiltering != null && pagingAndFiltering.getSortBy() != null ? pagingAndFiltering.getSortBy() : defaultSortBy,
            pagingAndFiltering != null && pagingAndFiltering.getSortOrder() != null ? SortOrder.valueOf(pagingAndFiltering.getSortOrder().name()) : defaultSortOrder);
    }

    /**
     * Convert string type filter criteria to a data type corresponding to a particular field
     * 
     * @param targetClass Principal class that filter criteria is targeting
     * @param filtersToConvert Filtering criteria
     * @param cfts
     * @return A converted filter
     * @throws InvalidParameterException
     */
    @SuppressWarnings({ "rawtypes" })
    private Map<String, Object> convertFilters(Class targetClass, Map<String, Object> filtersToConvert, Map<String, CustomFieldTemplate> cfts) throws InvalidParameterException {

//        log.debug("Converting filters {}", filtersToConvert);

        Map<String, Object> filters = new HashMap<>();
        if (filtersToConvert == null) {
            return filters;

            // Search by filter - nothing to convert
        } else if (filtersToConvert.containsKey(PersistenceService.SEARCH_FILTER)) {
            return filtersToConvert;
        }

        for (Entry<String, Object> filterInfo : filtersToConvert.entrySet()) {

            if (filterInfo.getValue() == null) {
                continue;
            }

            String key = filterInfo.getKey();
            Object value = filterInfo.getValue();

            String[] fieldInfo = key.split(" ");
            String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
            String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1];

            // Nothing to convert
            if (PersistenceService.SEARCH_ATTR_TYPE_CLASS.equals(fieldName) || PersistenceService.SEARCH_SQL.equals(key)
                    || (value instanceof String && (PersistenceService.SEARCH_IS_NOT_NULL.equals((String) value) || PersistenceService.SEARCH_IS_NULL.equals((String) value)))) {
                filters.put(key, value);

                // Filter already contains a special

                // Determine what the target field type is and convert to that data type
            } else {

                Class<?> fieldClassType = extractFieldType(targetClass, fieldName, cfts);
                if (fieldClassType == null) {
                    throw new InvalidParameterException("Field '" + fieldName + "' is not a valid field name");
                }
                Object valueConverted = castFilterValue(value, fieldClassType,
                    (condition != null && condition.contains("inList")) || "overlapOptionalRange".equals(condition) || "overlapOptionalRangeInclusive".equals(condition), cfts, false);
                if (valueConverted != null) {
                    filters.put(key, valueConverted);

                    // To support wildcard search in enum value
                } else if (fieldClassType.isEnum()) {
                    filters.put(key, value);

                } else {
                    throw new InvalidParameterException("Filter " + key + " value " + value + " does not match " + fieldClassType.getSimpleName());
                }
            }
        }

        return filters;
    }

    private Class<?> extractFieldType(Class<?> targetClass, String fieldName, Map<String, CustomFieldTemplate> cfts) {
        Class<?> fieldClassType = null;
        if (targetClass != null) {
            Field field;
            try {
                field = ReflectionUtils.getFieldThrowException(targetClass, fieldName);
            } catch (NoSuchFieldException e) {
                throw new InvalidParameterException(e.getMessage());
            }
            fieldClassType = field.getType();
            if (fieldClassType == List.class || fieldClassType == Set.class) {
                fieldClassType = ReflectionUtils.getFieldGenericsType(field);
            }
        } else if (cfts != null) {
            Optional<CustomFieldTemplate> field = cfts.values().stream().filter(x -> x.getCode().equalsIgnoreCase(fieldName) || x.getDbFieldname().equalsIgnoreCase(fieldName)).findFirst();
            if (field.isPresent()) {
                CustomFieldTemplate cft = field.get();
                fieldClassType = cft.getFieldType().getDataClass();
            } else if (NativePersistenceService.FIELD_ID.equals(fieldName)) {
                fieldClassType = Long.class;
            }
        }
        return fieldClassType;
    }

    /**
     * Convert value of unknown data type to a target data type. A value of type list is considered as already converted value, as would come only from WS.
     * 
     * @param value Value to convert
     * @param targetClass Target data type class to convert to
     * @param expectedList Is return value expected to be a list. If value is not a list and is a string a value will be parsed as comma separated string and each value will be
     *        converted accordingly. If a single value is passed, it will be added to a list.
     * @param cfts Custom field templates for target class
     * @param castEntityReferenceAsObject Shall value be converted to EntityWrapper or a primitive (long/string) when target class is entity reference field
     * @return A converted data type
     * @throws InvalidParameterException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object castFilterValue(Object value, Class targetClass, boolean expectedList, Map<String, CustomFieldTemplate> cfts, boolean castEntityReferenceAsObject) throws InvalidParameterException {

        log.trace("Casting {} of class {} target class {} expected list {} is array {}", value, value != null ? value.getClass() : null, targetClass, expectedList, value != null ? value.getClass().isArray() : null);
        // Nothing to cast - same data type
        if (targetClass.isAssignableFrom(value.getClass()) && !expectedList) {
            return value;

            // A list is expected as value. If value is not a list, parse value as comma separated string and convert each value separately
        } else if (expectedList) {
            if (value instanceof Collection || value.getClass().isArray()) {
                Object firstValue = null;
                if (value instanceof List) {
                    firstValue = ((List) value).get(0);
                } else if (value instanceof Set) {
                    firstValue = ((Set) value).iterator().next();
                } else if (value.getClass().isArray()) {
                    firstValue = ((Object[]) value)[0];
                }

                // Its a list, but of a different data type
                if (!targetClass.isAssignableFrom(firstValue.getClass())) {
                    List valuesConverted = new ArrayList<>();
                    if (value.getClass().isArray()) {
                        value = Arrays.asList(value);
                    }
                    Iterator valueIterator = ((Collection) value).iterator();
                    boolean invalidReference = false;
                    while (valueIterator.hasNext()) {
                        Object valueItem = valueIterator.next();
                        try {
                            Object valueConverted = castFilterValue(valueItem, targetClass, false, cfts, castEntityReferenceAsObject);
                            if (valueConverted != null) {
                                valuesConverted.add(valueConverted);
                            } else {
                                throw new InvalidParameterException("Filter value " + value + " does not match " + targetClass.getSimpleName());
                            }
                        } catch (InvalidReferenceException e) {
                            invalidReference = true;
                            continue;
                        }
                    }
                    if (invalidReference && valuesConverted.isEmpty()) {
                        throw new InvalidReferenceException(targetClass.getSimpleName(), value.toString());
                    }
                    return valuesConverted;

                } else {
                    return value;
                }
                // Parse comma separated string
            } else if (value instanceof String) {
                List valuesConverted = new ArrayList<>();
                String[] valueItems = ((String) value).split(",");
                boolean invalidReference = false;
                for (String valueItem : valueItems) {
                    try {
                        Object valueConverted = castFilterValue(valueItem, targetClass, false, cfts, castEntityReferenceAsObject);
                        if (valueConverted != null) {
                            valuesConverted.add(valueConverted);
                        } else {
                            throw new InvalidParameterException("Filter value " + value + " does not match " + targetClass.getSimpleName());
                        }
                    } catch (InvalidReferenceException e) {
                        invalidReference = true;
                        continue;
                    }
                }
                if (invalidReference && valuesConverted.isEmpty()) {
                    throw new InvalidReferenceException(targetClass.getSimpleName(), valueItems);
                }
                return valuesConverted;

                // A single value list
            } else {
                Object valueConverted = castFilterValue(value, targetClass, false, cfts, castEntityReferenceAsObject);
                if (valueConverted != null) {
                    return Arrays.asList(valueConverted);
                } else {
                    throw new InvalidParameterException("Filter value " + value + " does not match " + targetClass.getSimpleName());
                }
            }
        }

        Number numberVal = null;
        BigDecimal bdVal = null;
        String stringVal = null;
        Boolean booleanVal = null;
        Date dateVal = null;
        List listVal = null;
        Map<String, Object> mapVal = null;

        if (value instanceof Number) {
            numberVal = (Number) value;
        } else if (value instanceof BigDecimal) {
            bdVal = (BigDecimal) value;
        } else if (value instanceof Boolean) {
            booleanVal = (Boolean) value;
        } else if (value instanceof Date) {
            dateVal = (Date) value;
        } else if (value instanceof String) {
            stringVal = (String) value;
        } else if (value instanceof List) {
            listVal = (List) value;
        } else if (value instanceof Map) {
            mapVal = (Map) value;
        } else {
            throw new InvalidParameterException("Unrecognized data type for filter criteria value " + value);
        }

        try {
            if (targetClass == String.class) {
                if (stringVal != null || listVal != null) {
                    return value;
                } else {
                    return value.toString();
                }

            } else if (targetClass == Boolean.class || (targetClass.isPrimitive() && targetClass.getName().equals("boolean"))) {
                if (booleanVal != null) {
                    return value;
                } else {
                    return Boolean.parseBoolean(value.toString());
                }

            } else if (targetClass == Date.class) {
                if (dateVal != null || listVal != null) {
                    return value;
                } else if (numberVal != null) {
                    return new Date(numberVal.longValue());
                } else if (stringVal != null) {
                    // first try with date and time and then only with date format
                    Date date = DateUtils.parseDateWithPattern(stringVal, DateUtils.DATE_TIME_PATTERN);
                    if (date == null) {
                        date = DateUtils.parseDateWithPattern(stringVal, DateUtils.DATE_PATTERN);
                    }
                    return date;
                }

            } else if (targetClass.isEnum()) {
                if (listVal != null || targetClass.isAssignableFrom(value.getClass())) {
                    return value;
                } else if (stringVal != null) {
                    Enum enumVal = ReflectionUtils.getEnumFromString((Class<? extends Enum>) targetClass, stringVal);
                    if (enumVal != null) {
                        return enumVal;
                    }
                }

            } else if (targetClass == Integer.class || (targetClass.isPrimitive() && targetClass.getName().equals("int"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Integer.parseInt(stringVal);
                }

            } else if (targetClass == Long.class || (targetClass.isPrimitive() && targetClass.getName().equals("long"))) {
                if (numberVal != null) {
                    return numberVal.longValue();
                } else if (bdVal != null) {
                    return bdVal.longValue();
                } else if (listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Long.parseLong(stringVal);
                }

            } else if (targetClass == Byte.class || (targetClass.isPrimitive() && targetClass.getName().equals("byte"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Byte.parseByte(stringVal);
                }

            } else if (targetClass == Short.class || (targetClass.isPrimitive() && targetClass.getName().equals("short"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Short.parseShort(stringVal);
                }

            } else if (targetClass == Double.class || (targetClass.isPrimitive() && targetClass.getName().equals("double"))) {
                if (bdVal != null || listVal != null) {
                    return value;
                } else if (numberVal != null) {
                    return numberVal.doubleValue();
                } else if (stringVal != null) {
                    return Double.parseDouble(stringVal);
                }

            } else if (targetClass == Float.class || (targetClass.isPrimitive() && targetClass.getName().equals("float"))) {
                if (numberVal != null || bdVal != null || listVal != null) {
                    return value;
                } else if (stringVal != null) {
                    return Float.parseFloat(stringVal);
                }

            } else if (targetClass == BigDecimal.class) {
                if (bdVal != null || listVal != null) {
                    return value;
                } else if (numberVal != null) {
                    return BigDecimal.valueOf(numberVal.doubleValue());
                } else if (stringVal != null) {
                    return new BigDecimal(stringVal);
                }

            } else if (targetClass == EntityReferenceWrapper.class) {

                if (castEntityReferenceAsObject && numberVal != null) {
                    EntityReferenceWrapper wrapper = new EntityReferenceWrapper();
                    wrapper.setCode(numberVal.toString());
                    wrapper.setId(numberVal.longValue());
                    return wrapper;

                } else if (castEntityReferenceAsObject && stringVal != null) {
                    EntityReferenceWrapper wrapper = new EntityReferenceWrapper();
                    wrapper.setCode(stringVal);
                    return wrapper;

                } else if (numberVal != null) {
                    return numberVal.longValue();

                } else if (stringVal != null) {
                    return Long.valueOf(stringVal);
                }

            } else if (BusinessEntity.class.isAssignableFrom(targetClass)) {

                if (stringVal != null && (stringVal.equals(PersistenceService.SEARCH_IS_NULL) || stringVal.equals(PersistenceService.SEARCH_IS_NOT_NULL))) {
                    return stringVal;
                }

                businessEntityService.setEntityClass(targetClass);

                if (stringVal != null) {
                    BusinessEntity businessEntity = businessEntityService.findByCode(stringVal);
                    if (businessEntity == null) {
                        // Did not find a way how to pass nonexistant entity to search sql
                        throw new InvalidReferenceException(targetClass.getSimpleName(), stringVal);
                    }
                    return businessEntity;
                }

            } else if (Role.class.isAssignableFrom(targetClass)) {
                // special case
                if (stringVal != null && (stringVal.equals(PersistenceService.SEARCH_IS_NULL) || stringVal.equals(PersistenceService.SEARCH_IS_NOT_NULL))) {
                    return stringVal;
                }

                if (stringVal != null) {
                    Role role = roleService.findByName(stringVal);
                    if (role == null) {
                        // Did not find a way how to pass nonexistant entity to search sql
                        throw new InvalidParameterException("Entity of type " + targetClass.getSimpleName() + " with code " + stringVal + " not found");
                    }
                    return role;
                }
            } else if (CustomFieldValues.class.isAssignableFrom(targetClass)) {
                if (mapVal != null) {
                    Map<String, List<CustomFieldValue>> cfvMap = new TreeMap<String, List<CustomFieldValue>>();
                    for (String key : mapVal.keySet()) {
                        Object cfValue = mapVal.get(key);
                        String[] fieldInfo = key.split(" ");
                        String[] fields = fieldInfo.length == 1 ? fieldInfo : Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length);
                        Class dataClass = null;
                        CustomFieldStorageTypeEnum storageType = null;
                        for (String f : fields) {
                            CustomFieldTemplate customFieldTemplate = cfts.get(f);
                            if (customFieldTemplate == null) {
                                throw new BusinessException("No custom field found with name :" + f);
                            }
                            storageType = customFieldTemplate.getStorageType();
                            Class tempDataClass = customFieldTemplate.getFieldType().getDataClass();
                            if (dataClass == null) {
                                dataClass = tempDataClass;
                            } else {
                                if (!dataClass.equals(tempDataClass)) {
                                    throw new BusinessException("Different data type used in the same filter : " + key);
                                }
                            }
                        }
                        Object valueConverted = castFilterValue(cfValue, dataClass, expectedList, cfts, true);
                        if (valueConverted == null) {
                            if (!CustomFieldStorageTypeEnum.SINGLE.equals(storageType)) {
                                throw new BusinessException("Only CustomFields with SINGLE storageType are accepted on filters. Cannot use filter '" + key + "'");
                            } else {
                                throw new BusinessException("Not able to cast filter value '" + cfValue + "' of custom field '" + key + "' to " + dataClass);
                            }
                        }
                        cfvMap.put(key, Arrays.asList(new CustomFieldValue(valueConverted)));
                    }
                    CustomFieldValues customFieldsValues = new CustomFieldValues(cfvMap);
                    return customFieldsValues;
                }
            }
        } catch (NumberFormatException e) {
            // Swallow - validation will take care of it later
        }
        return null;
    }

    /**
     * Rounding the double values.
     * 
     * @param cfDto the customFieldDto
     * @param cft The customFieldTemplate
     * @return A rounded bigDecimal number.
     */
    public Object getDoubleValue(CustomFieldDto cfDto, CustomFieldTemplate cft) {
        BigDecimal value = new BigDecimal(cfDto.getDoubleValue());
        RoundingModeEnum roundingMode = cft.getRoundingMode() != null ? cft.getRoundingMode() : RoundingModeEnum.NEAREST;
        Integer nbDecimal = (cft.getNbDecimal() != null && cft.getNbDecimal() != 0) ? cft.getNbDecimal() : NumberUtils.DEFAULT_NUMBER_DIGITS_DECIMAL;
        value = NumberUtils.round(value, nbDecimal, roundingMode.getRoundingMode());
        return value.doubleValue();
    }

    /**
     * Convert auditableField entity to dto
     *
     * @param entity instance of AuditableField to be mapped
     * @return instance of AuditableFieldDto
     */
    public AuditableFieldDto auditableFieldToDto(AuditableField entity) {
        AuditableFieldDto dto = new AuditableFieldDto();
        dto.setEntityClass(entity.getEntityClass());
        dto.setFieldName(entity.getName());
        dto.setChangeOrigin(String.valueOf(entity.getChangeOrigin()));
        dto.setOriginName(entity.getOriginName());
        dto.setPreviousState(entity.getPreviousState());
        dto.setCurrentState(entity.getCurrentState());
        dto.setCreated(DateUtils.formatDateWithPattern(entity.getCreated(), DateUtils.DATE_TIME_PATTERN));
        dto.setActor(entity.getActor());
        return dto;
    }

    /**
     * Convert list of auditableField entity to dto
     *
     * @param entites list of auditableField entity to be mapped
     * @return list of instance of AuditableFieldDto.
     */
    public List<AuditableFieldDto> auditableFieldsToDto(List<AuditableField> entites) {
        List<AuditableFieldDto> dtos = null;
        if (entites != null && !entites.isEmpty()) {
            dtos = new ArrayList<>();
            for (AuditableField entity : entites) {
                dtos.add(auditableFieldToDto(entity));
            }
        }
        return dtos;
    }

    /**
     * Sets the auditable fields in to dto.
     *
     * @param entity entity instance
     * @param dto dto instance
     */
    public void setAuditableFieldsDto(BaseEntity entity, AuditableEntityDto dto) {
        List<AuditableField> auditableFields = auditableFieldService.list(entity);
        List<AuditableFieldDto> auditableFieldsDto = auditableFieldsToDto(auditableFields);
        dto.setAuditableFields(auditableFieldsDto);
    }

    public Throwable getRootCause(Throwable e, Class<?> clazz) {
        while (e != null) {
            if (e.getClass().equals(clazz)) {
                return e;
            }
            e = e.getCause();
        }
        return null;
    }

    public MeveoApiException getMeveoApiException(Throwable e) {
        Throwable rootCause = getRootCause(e, ConstraintViolationException.class);
        if (rootCause != null) {
            return new ConstraintViolationApiException(rootCause.getCause().getMessage());
        }
        return new MeveoApiException(e);
    }

    public String getCustomFieldDataType(Class clazz) {
        if (clazz == Double.class || clazz == Date.class || clazz == Long.class) {
            for (CustomFieldTypeEnum cft : CustomFieldTypeEnum.values()) {
                if (cft.getDataClass().equals(clazz)) {
                    return cft.getDataType();
                }
            }
        }
        return "varchar";
    }

    public ICustomFieldEntity populateCustomFieldsForGenericApi(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity) throws MeveoApiException {
        return populateCustomFields(customFieldsDto, entity, isNewEntity, true);
    }
}