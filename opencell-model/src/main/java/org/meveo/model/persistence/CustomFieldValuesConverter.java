package org.meveo.model.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Converts CustomFieldValues entity to/from JSON format string for storage in DB
 * 
 * @author Andrius Karpavicius
 *
 */
@Converter
public class CustomFieldValuesConverter implements AttributeConverter<CustomFieldValues, String> {

    @Override
    public String convertToDatabaseColumn(CustomFieldValues cfValues) {
        return toJson(cfValues);
    }

    @Override
    public CustomFieldValues convertToEntityAttribute(String json) {

        if (json == null) {
            return null;// A nice approach would be to return new CustomFieldValues(), but that will cause update to db even though empty CustomFieldValues with no values is
                        // serialized back to null. Hibernate probably assumes that if json was null, deserialized value should also be null.
        }
        try {
            return new CustomFieldValues(JacksonUtil.fromString(json, new TypeReference<Map<String, List<CustomFieldValue>>>() {
            }));
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to convert json to CF Value", e);
            return null;
        }
    }

    public static String toJson(CustomFieldValues cfValues) {
        if (cfValues == null || cfValues.getValuesByCode() == null || cfValues.getValuesByCode().isEmpty()) {
            return null;
        }

        try {
            String json = JacksonUtil.toString(cfValues.getValuesByCode());
            return json;
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValuesConverter.class);
            log.error("Failed to convert CF Value to json", e);
            return null;
        }
    }
}