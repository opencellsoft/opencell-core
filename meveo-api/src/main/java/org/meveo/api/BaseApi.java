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
            log.warn("No custom field templates defined.");
            return;
        }
        for (CustomFieldDto cf : customFieldDtos) {
            boolean found = false;
            for (CustomFieldTemplate cft : customFieldTemplates) {
                if (cf.getCode().equals(cft.getCode())) {

                    // Validate parameters
                    if (cft.isVersionable()) {

                        if ((cf.getValueDate() == null && cft.getCalendar() != null)) {
                            throw new MissingParameterException("Custom field is versionable by calendar. Missing valueDate parameter.");

                        } else if (cft.getCalendar() == null && (cf.getValuePeriodStartDate() == null || cf.getValuePeriodEndDate() == null)) {
                            throw new MissingParameterException("Custom field is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
                        }
                    }

                    found = true;
                    CustomFieldInstance cfi = entity.getCustomFields().get(cf.getCode());
                    // Create an instance if does not exist yet
                    if (cfi == null) {
                        cfi = new CustomFieldInstance();
                        FieldUtils.getField(CustomFieldInstance.class, cfiFieldName, true).set(cfi, entity);
                        cfi.setCode(cf.getCode());
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
                                    cfi.setDateValue(cf.getDateValue(), cf.getDateValue());
                                    break;
                                case DOUBLE:
                                    cfi.setDoubleValue(cf.getDoubleValue(), cf.getDateValue());
                                    break;
                                case LONG:
                                    cfi.setLongValue(cf.getLongValue(), cf.getDateValue());
                                    break;
                                case LIST:
                                case STRING:
                                case TEXT_AREA:
                                    cfi.setStringValue(cf.getStringValue(), cf.getDateValue());
                                    break;
                                case ENTITY:
                                    cfi.setEntityReferenceValue(cf.getEntityReferenceValue().fromDTO(), cf.getDateValue());
                                }

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                                cfi.setListValue(CustomFieldValueDto.fromDTO(cf.getListValue()), cf.getDateValue());

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                                cfi.setMapValue(CustomFieldValueDto.fromDTO(cf.getMapValue()), cf.getDateValue());
                            }

                        } else {
                            if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
                                switch (cft.getFieldType()) {
                                case DATE:
                                    cfi.setDateValue(cf.getDateValue(), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());
                                    break;
                                case DOUBLE:
                                    cfi.setDoubleValue(cf.getDoubleValue(), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());
                                    break;
                                case LONG:
                                    cfi.setLongValue(cf.getLongValue(), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());
                                    break;
                                case LIST:
                                case STRING:
                                case TEXT_AREA:
                                    cfi.setStringValue(cf.getStringValue(), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());
                                    break;
                                case ENTITY:
                                    cfi.setEntityReferenceValue(cf.getEntityReferenceValue().fromDTO(), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());
                                }

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                                cfi.setListValue(CustomFieldValueDto.fromDTO(cf.getListValue()), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());

                            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                                cfi.setMapValue(CustomFieldValueDto.fromDTO(cf.getMapValue()), cf.getValuePeriodStartDate(), cf.getValuePeriodEndDate());
                            }
                        }

                    } else {
                        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
                            switch (cft.getFieldType()) {
                            case DATE:
                                cfi.setDateValue(cf.getDateValue());
                                break;
                            case DOUBLE:
                                cfi.setDoubleValue(cf.getDoubleValue());
                                break;
                            case LONG:
                                cfi.setLongValue(cf.getLongValue());
                                break;
                            case LIST:
                            case STRING:
                            case TEXT_AREA:
                                cfi.setStringValue(cf.getStringValue());
                                break;
                            case ENTITY:
                                cfi.setEntityReferenceValue(cf.getEntityReferenceValue().fromDTO());
                            }

                        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                            cfi.setListValue(CustomFieldValueDto.fromDTO(cf.getListValue()));

                        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                            cfi.setMapValue(CustomFieldValueDto.fromDTO(cf.getMapValue()));
                        }
                    }

                    break;
                }
            }
            if (!found) {
                log.warn("No custom field template with code={} for entity {}", cf.getCode(), entity.getClass());
            }
        }
    }
}
