package org.meveo.admin.job.utils;

import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

/**
 * Custom field template related utilities
 */
public class CustomFieldTemplateUtils {

    /**
     * Build a custom field definition
     * 
     * @param code Custom field code
     * @param description Description
     * @param fieldType Field type
     * @param guiPosition GUI position
     * @param defaultValue Default value
     * @param valueRequire Is value required
     * @param storageType Storage type
     * @param entityClazz Entity class
     * @param appliedTo Applies to value
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String defaultValue, boolean valueRequire, CustomFieldStorageTypeEnum storageType,
            String entityClazz, String appliedTo) {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode(code);
        cft.setAppliesTo("JobInstance_RatedTransactionsJob");
        cft.setActive(true);
        cft.setDescription(description);
        cft.setFieldType(fieldType);
        cft.setValueRequired(valueRequire);
        cft.setGuiPosition(guiPosition);
        if (defaultValue != null) {
            cft.setDefaultValue(defaultValue);
        }
        if (storageType != null) {
            cft.setStorageType(storageType);
        }
        if (entityClazz != null) {
            cft.setEntityClazz(entityClazz);
        }
        return cft;
    }
}