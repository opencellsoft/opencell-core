package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.CustomFieldDto;
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
     */
    protected void populateCustomFields(AccountLevelEnum cfType, List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, String cfiFieldName, User currentUser)
            throws IllegalArgumentException, IllegalAccessException {

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
     */
    protected void populateCustomFields(List<CustomFieldTemplate> customFieldTemplates, List<CustomFieldDto> customFieldDtos, ICustomFieldEntity entity, String cfiFieldName,
            User currentUser) throws IllegalArgumentException, IllegalAccessException {

        // check if any templates are applicable
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            log.warn("No custom field templates defined.");
            return;
        }
        for (CustomFieldDto cf : customFieldDtos) {
            boolean found = false;
            for (CustomFieldTemplate cft : customFieldTemplates) {
                if (cf.getCode().equals(cft.getCode())) {
                    found = true;
                    CustomFieldInstance cfi = entity.getCustomFields().get(cf.getCode());
                    if (cfi == null) {
                        cfi = new CustomFieldInstance();
                        FieldUtils.getField(CustomFieldInstance.class, cfiFieldName, true).set(cfi, entity);
                        cfi.setCode(cf.getCode());
                        cfi.setDescription(StringUtils.isBlank(cfi.getDescription()) ? cft.getDescription() : cfi.getDescription());
                        cfi.setProvider(currentUser.getProvider());
                        entity.getCustomFields().put(cfi.getCode(), cfi);
                    }
                    // update
                    cfi.setActive(true);
                    cfi.setDateValue(cf.getDateValue());
                    cfi.setDoubleValue(cf.getDoubleValue());
                    cfi.setLongValue(cf.getLongValue());
                    cfi.setStringValue(cf.getStringValue());
                    cfi.updateAudit(currentUser);

                    break;
                }
            }
            if (!found) {
                log.warn("No custom field template with code={} for entity {}", cf.getCode(), entity.getClass());
            }
        }
    }
}
