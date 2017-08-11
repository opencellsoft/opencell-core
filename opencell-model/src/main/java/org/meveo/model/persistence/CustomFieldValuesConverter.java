package org.meveo.model.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.AttributeConverter;

import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Converts CustomFieldValues entity to/from JSON format string for storage in DB
 * 
 * @author Andrius Karpavicius
 *
 */
public class CustomFieldValuesConverter implements AttributeConverter<CustomFieldValues, String> {

    @Override
    public String convertToDatabaseColumn(CustomFieldValues cfValues) {

        if (cfValues == null || cfValues.getValuesByCode() == null || cfValues.getValuesByCode().isEmpty()) {
            return null;
        }

        return JacksonUtil.toString(cfValues.getValuesByCode());
    }

    @Override
    public CustomFieldValues convertToEntityAttribute(String json) {

        if (json == null) {
            return null;// A nice approach would be to return new CustomFieldValues(), but that will cause update to db even though empty CustomFieldValues with no values is
                        // serialized back to null. Hibernate probably assumes that if json was null, deserialized value should also be null.
        }
        return new CustomFieldValues(JacksonUtil.fromString(json, new TypeReference<Map<String, List<CustomFieldValue>>>() {
        }));
    }
}