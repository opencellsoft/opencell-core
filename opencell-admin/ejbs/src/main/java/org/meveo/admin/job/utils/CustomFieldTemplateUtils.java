package org.meveo.admin.job.utils;

import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

public class CustomFieldTemplateUtils {

    private CustomFieldTemplateUtils(){}

    public static CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum type,
                                        String guiPosition, String defaultValue, String appliedToName) {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode(code);
        cft.setAppliesTo(appliedToName);
        cft.setActive(true);
        cft.setDescription(description);
        cft.setFieldType(type);
        cft.setValueRequired(false);
        cft.setGuiPosition(guiPosition);
        cft.setDefaultValue(defaultValue);
        return cft;
    }
}
