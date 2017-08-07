package org.meveo.model.persistence;

import javax.persistence.AttributeConverter;

import org.meveo.commons.utils.JsonUtils;
import org.meveo.model.crm.custom.CustomFieldValues;

public class CustomFieldValuesConverter implements AttributeConverter<CustomFieldValues, String> {

    @Override
    public String convertToDatabaseColumn(CustomFieldValues cfValues) {
        return JsonUtils.toJson(cfValues, false);
    }

    @Override
    public CustomFieldValues convertToEntityAttribute(String json) {
        return JsonUtils.toObject(json, CustomFieldValues.class);
    }
}