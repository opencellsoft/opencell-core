package org.meveo.service.crm.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jaxb.customer.CustomField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImportService {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());    

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    /**
     * Populate custom field values from DTO
     * 
     * @param cfType An entity that custom field template applies to
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param cfiFieldName Custom field name in an entity
     * @param currentUser User that authenticated for API
     * @throws BusinessException 
     * @throws MissingParameterException
     */
    protected void populateCustomFields(List<CustomField> customFieldDtos, ICustomFieldEntity entity, User currentUser) throws BusinessException {
        // throws MissingParameterException {

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity, currentUser.getProvider());

        populateCustomFields(customFieldTemplates, customFieldDtos, entity, currentUser);
    }

    /**
     * Populate custom field values from DTO
     * 
     * @param customFieldTemplates Custom field templates mapped by a template key
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @param cfiFieldName Custom field name in an entity
     * @param currentUser User that authenticated for API
     * @throws BusinessException 
     * @throws MissingParameterException
     */
    private void populateCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates, List<CustomField> customFieldDtos, ICustomFieldEntity entity, User currentUser) throws BusinessException {
        
        // check if any templates are applicable
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            log.warn("No custom field templates defined. Custom field values will be ignored");
            return;
        }

        if (customFieldDtos != null && !customFieldDtos.isEmpty()) {
            for (CustomField cfDto : customFieldDtos) {
                CustomFieldTemplate cft = customFieldTemplates.get(cfDto.getCode());

                if (cft == null) {
                    log.warn("No custom field template found with code={} for entity {}. Value will be ignored.", cfDto.getCode(), entity.getClass());
                    continue;
                }

                // Validate if value is not empty when field is mandatory
                if (cft.isValueRequired()) {
                    if (cfDto.isEmpty(cft.getFieldType(), cft.getStorageType())) {
                         continue;
                    }
                }

             
                // Set the value
                try {
                    if (cft.isVersionable()) {
                        if (cft.getCalendar() != null) {
                            customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(), cfDto.getValueDate(), currentUser);

                        } else {
                            customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(), cfDto.getValuePeriodStartDate(),
                                cfDto.getValuePeriodEndDate(), cfDto.getValuePeriodPriority(), currentUser);
                        }

                    } else {
                        customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(), currentUser);
                    }

                } catch (Exception e) {
                    log.error("Failed to set value {} on custom field {} for entity {}", cfDto.getValueConverted(), cfDto.getCode(), entity, e);
                    throw new BusinessException("Failed to set value " + cfDto.getValueConverted() + " on custom field " + cfDto.getCode() + " for entity " + entity);

                }
            }
        }
    }
}