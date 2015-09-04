package org.meveo.service.crm.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jaxb.customer.CustomField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImportService {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Populate custom field values from DTO
     * 
     * @param cfType An entity that custom field template applies to
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param cfiFieldName Custom field name in an entity
     * @param currentUser User that authenticated for API
     * @throws MissingParameterException
     */
    protected void populateCustomFields(AccountLevelEnum cfType, List<CustomField> customFieldDtos, ICustomFieldEntity entity, String cfiFieldName, User currentUser) {
        // throws MissingParameterException {

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
     * @throws MissingParameterException
     */
    private void populateCustomFields(List<CustomFieldTemplate> customFieldTemplates, List<CustomField> customFieldDtos, ICustomFieldEntity entity, String cfiFieldName,
            User currentUser) {
        // throws MissingParameterException {

        List<String> missingParameters = new ArrayList<String>();

        // check if any templates are applicable
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            log.warn("No custom field templates defined. Custom field values will be ignored");
            return;
        }
        if (customFieldDtos != null) {
            for (CustomField cfDto : customFieldDtos) {
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
                            // if ((cfDto.getValueDate() == null && cft.getCalendar() != null)) {
                            // throw new MissingParameterException("Custom field is versionable by calendar. Missing valueDate parameter.");
                            //
                            // } else if (cft.getCalendar() == null && (cfDto.getValuePeriodStartDate() == null || cfDto.getValuePeriodEndDate() == null)) {
                            // throw new MissingParameterException("Custom field is versionable by periods. Missing valuePeriodStartDate and/or valuePeriodEndDate parameters.");
                            // }
                        }

                        CustomFieldInstance cfi = entity.getCustomFields().get(cfDto.getCode());
                        // Create an instance if does not exist yet
                        if (cfi == null) {
                            cfi = CustomFieldInstance.fromTemplate(cft);
                            try {
                                FieldUtils.getField(CustomFieldInstance.class, cfiFieldName, true).set(cfi, entity);
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            entity.getCustomFields().put(cfi.getCode(), cfi);
                        }

                        // Update TODO
                        cfi.setActive(true);
                        cfi.updateAudit(currentUser);

                        if (cfi.isVersionable()) {

                            // if (cfi.getCalendar() != null) {
                            // cfi.setValue(cfDto.getValueConverted(), cfDto.getValueDate());
                            //
                            // } else {
                            // cfi.setValue(cfDto.getValueConverted(), cfDto.getValuePeriodStartDate(), cfDto.getValuePeriodEndDate());
                            // }

                        } else {
                            cfi.setValue(cfDto.getValueConverted());
                        }

                        break;
                    }
                }
                if (!found) {
                    log.warn("No custom field template found with code={} for entity {}. Value will be ignored.", cfDto.getCode(), entity.getClass());
                }
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
            // throw new MissingParameterException(getMissingParametersExceptionMessage());
        }
    }
}