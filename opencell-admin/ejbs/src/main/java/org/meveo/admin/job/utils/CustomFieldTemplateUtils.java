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
     * @param appliesTo Applies to value
     * @param maxValue max length of field
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String defaultValue, boolean valueRequire, CustomFieldStorageTypeEnum storageType,
            String entityClazz, String appliesTo, long maxValue) {
    	CustomFieldTemplate cf = buildCF(appliesTo, appliesTo, fieldType, appliesTo, appliesTo, valueRequire, storageType, appliesTo, appliesTo);
    	cf.setMaxValue(maxValue);
    	return cf;
    }

    /**
     * Build a custom field definition
     *
     * @param code         Custom field code
     * @param description  Description
     * @param fieldType    Field type
     * @param guiPosition  GUI position
     * @param defaultValue Default value
     * @param valueRequire Is value required
     * @param storageType  Storage type
     * @param entityClazz  Entity class
     * @param appliesTo    Applies to value
     * @param maxValue     Max value
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String defaultValue,
                                              boolean valueRequire, CustomFieldStorageTypeEnum storageType, String entityClazz, String appliesTo, Long maxValue) {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode(code);
        cft.setMaxValue(null);
        cft.setAppliesTo(appliesTo);
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
        if (maxValue != null) {
            cft.setMaxValue(maxValue);
        }
        return cft;
    }

    /**
     * Build a custom field definition
     *
     * @param code         Custom field code
     * @param description  Description
     * @param fieldType    Field type
     * @param guiPosition  GUI position
     * @param defaultValue Default value
     * @param valueRequire Is value required
     * @param appliesTo    Applies to value
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String defaultValue,
                                              boolean valueRequire, String appliesTo) {
        return buildCF(code, description, fieldType, guiPosition, defaultValue, valueRequire, null, null, appliesTo, null);
    }

    /**
     * Build a custom field definition
     *
     * @param code         Custom field code
     * @param description  Description
     * @param fieldType    Field type
     * @param guiPosition  GUI position
     * @param defaultValue Default value
     * @param appliesTo    Applies to value
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String defaultValue, String appliesTo) {
        return buildCF(code, description, fieldType, guiPosition, defaultValue, false, null, null, appliesTo, null);
    }

    /**
     * Build a custom field definition
     *
     * @param code        Custom field code
     * @param description Description
     * @param fieldType   Field type
     * @param guiPosition GUI position
     * @param appliesTo   Applies to value
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String appliesTo) {
        return buildCF(code, description, fieldType, guiPosition, null, false, null, null, appliesTo, null);
    }

    /**
     * Build a custom field definition
     *
     * @param code        Custom field code
     * @param description Description
     * @param fieldType   Field type
     * @param guiPosition GUI position
     * @param appliesTo   Applies to value
     * @param maxValue    Max value
     * @return A custom field template
     */
    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum fieldType, String guiPosition, String appliesTo, Long maxValue) {
        return buildCF(code, description, fieldType, guiPosition, null, false, null, null, appliesTo, maxValue);
    }
}