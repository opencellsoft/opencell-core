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

package org.meveo.service.crm.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
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
     * Populate custom field values from DTO.
     * 
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @throws BusinessException  business exception.
     * 
     */
    protected void populateCustomFields(List<CustomField> customFieldDtos, ICustomFieldEntity entity) throws BusinessException {
        // throws MissingParameterException {

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity);

        populateCustomFields(customFieldTemplates, customFieldDtos, entity);
    }

    /**
     * Populate custom field values from DTO.
     * 
     * @param customFieldTemplates Custom field templates mapped by a template key
     * @param customFieldDtos Custom field values
     * @param entity Entity
     * @throws BusinessException business exception
     * @throws MissingParameterException missing parameter exception.
     */
    private void populateCustomFields(Map<String, CustomFieldTemplate> customFieldTemplates, List<CustomField> customFieldDtos, ICustomFieldEntity entity)
            throws BusinessException {

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
                            customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(), cfDto.getValueDate());

                        } else {
                            customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted(), cfDto.getValuePeriodStartDate(),
                                cfDto.getValuePeriodEndDate(), cfDto.getValuePeriodPriority());
                        }

                    } else {
                        customFieldInstanceService.setCFValue(entity, cfDto.getCode(), cfDto.getValueConverted());
                    }

                } catch (Exception e) {
                    log.error("Failed to set value {} on custom field {} for entity {}", cfDto.getValueConverted(), cfDto.getCode(), entity, e);
                    throw new BusinessException("Failed to set value " + cfDto.getValueConverted() + " on custom field " + cfDto.getCode() + " for entity " + entity);

                }
            }
        }
    }
}