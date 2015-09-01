package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
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

    /**
     * Populate custom field values from DTO
     * 
     * @param cfType An entity that custom field template applies to
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param cfiFieldName Custom field name in an entity
     * @param currentUser User that authenticated for API
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws MissingParameterException
     */
    protected void populateCustomFields(AccountLevelEnum cfType, List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, String cfiFieldName, User currentUser)
            throws IllegalArgumentException, IllegalAccessException, MissingParameterException {

        List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(cfType, currentUser.getProvider());

        populateCustomFields(customFieldTemplates, customFieldDtos, entity, cfiFieldName, currentUser);
    }

    /**
     * Populate custom field values from DTO
     * 
     * @param customFieldTemplates Custom field templates
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param cfiFieldName Custom field name in an entity
     * @param currentUser User that authenticated for API
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws MissingParameterException
     */
    protected void populateCustomFields(List<CustomFieldTemplate> customFieldTemplates, List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, String cfiFieldName,
            User currentUser) throws IllegalArgumentException, IllegalAccessException, MissingParameterException {

        // check if any templates are applicable
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            log.warn("No custom field templates defined. Custom field values will be ignored");
            return;
        }
        for (CustomFieldDto cfDto : customFieldDtos) {
            boolean found = false;
            for (CustomFieldTemplate cft : customFieldTemplates) {
                if (cfDto.getCode().equals(cft.getCode())) {
                    found = true;

                    // Validate if value is not empty when field is mandatory
                    if (cft.isValueRequired()) {
                        if (cfDto.isEmpty(cft.getFieldType(), cft.getStorageType())) {
                            missingParameters.add(cft.getCode() + "___112");
                            break;
                        }
                    }

                    // Validate parameters
                    if (cft.isVersionable()) {
                        if ((cfDto.getValueDate() == null && cft.getCalendar() != null)) {
                            throw new MissingParameterException("Custom field is versionable by calendar. Missing valueDate parameter.");

                        } else if (cft.getCalendar() == null && (cfDto.getValuePeriodStartDate() == null || cfDto.getValuePeriodEndDate() == null)) {
                            throw new MissingParameterException("Custom field is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
                        }
                    }

                    CustomFieldInstance cfi = entity.getCustomFields().get(cfDto.getCode());
                    // Create an instance if does not exist yet
                    if (cfi == null) {
                        cfi = new CustomFieldInstance();
                        FieldUtils.getField(CustomFieldInstance.class, cfiFieldName, true).set(cfi, entity);
                        cfi.setCode(cfDto.getCode());
                        cfi.setDescription(StringUtils.isBlank(cfi.getDescription()) ? cft.getDescription() : cfi.getDescription());
                        cfi.setProvider(currentUser.getProvider());
                        cfi.setVersionable(cft.isVersionable());
                        if (cft.isVersionable()) {
                            cfi.setCalendar(cft.getCalendar());
                        }
                        entity.getCustomFields().put(cfi.getCode(), cfi);
                    }

                    // Update TODO
                    cfi.setActive(true);
                    cfi.updateAudit(currentUser);

                    if (cfi.isVersionable()) {

                        if (cfi.getCalendar() != null) {
                            if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
                                switch (cft.getFieldType()) {
                                case DATE:
                                    cfi.setDateValue(cfDto.getDateValue(), cfDto.getValueDate());
                                    break;
                                case DOUBLE:
                                    cfi.setDoubleValue(cfDto.getDoubleValue(), cfDto.getValueDate());
                                    break;
                                case LONG:
                                    cfi.setLongValue(cfDto.getLongValue(), cfDto.getValueDate());
                                    break;
                                case LIST:
                                case STRING:
                                case TEXT_AREA:
                                    cfi.setStringValue(cfDto.getStringValue(), cfDto.getValueDate());
                                    break;
                                case ENTITY:
                                    cfi.setEntityReferenceValue(cfDto.getEntityReferenceValue().fromDTO(), cfDto.getValueDate());
                                }

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                                cfi.setListValue(CustomFieldValueDto.fromDTO(cfDto.getListValue()), cfDto.getValueDate());

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                                cfi.setMapValue(CustomFieldValueDto.fromDTO(cfDto.getMapValue()), cfDto.getValueDate());
                            }

                        } else {
                            if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
                                switch (cft.getFieldType()) {
                                case DATE:
                                    cfi.setDateValue(cfDto.getDateValue(), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                                    break;
                                case DOUBLE:
                                    cfi.setDoubleValue(cfDto.getDoubleValue(), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                                    break;
                                case LONG:
                                    cfi.setLongValue(cfDto.getLongValue(), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                                    break;
                                case LIST:
                                case STRING:
                                case TEXT_AREA:
                                    cfi.setStringValue(cfDto.getStringValue(), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                                    break;
                                case ENTITY:
                                    cfi.setEntityReferenceValue(cfDto.getEntityReferenceValue().fromDTO(), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                                }

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                                cfi.setListValue(CustomFieldValueDto.fromDTO(cfDto.getListValue()), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                                cfi.setMapValue(CustomFieldValueDto.fromDTO(cfDto.getMapValue()), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                            }
                        }

                    } else {
                        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
                            switch (cft.getFieldType()) {
                            case DATE:
                                cfi.setDateValue(cfDto.getDateValue());
                                break;
                            case DOUBLE:
                                cfi.setDoubleValue(cfDto.getDoubleValue());
                                break;
                            case LONG:
                                cfi.setLongValue(cfDto.getLongValue());
                                break;
                            case LIST:
                            case STRING:
                            case TEXT_AREA:
                                cfi.setStringValue(cfDto.getStringValue());
                                break;
                            case ENTITY:
                                cfi.setEntityReferenceValue(cfDto.getEntityReferenceValue().fromDTO());
                            }

                        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                            cfi.setListValue(CustomFieldValueDto.fromDTO(cfDto.getListValue()));

                        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                            cfi.setMapValue(CustomFieldValueDto.fromDTO(cfDto.getMapValue()));
                        }
                    }

                    break;
                }
            }
            if (!found) {
                log.warn("No custom field template found with code={} for entity {}. Value will be ignored.", cfDto.getCode(), entity.getClass());
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
}
