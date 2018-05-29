package org.meveo.model.persistence;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.meveo.model.crm.custom.CustomFieldValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (cfValues == null) {
            return null;
        }

        try {
            String json = cfValues.asJson();
            return json;
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValuesConverter.class);
            log.error("Failed to convert CF Value to json", e);
            return null;
        }
    }

    @Override
    public CustomFieldValues convertToEntityAttribute(String json) {

        if (json == null) {
            return null;// A nice approach would be to return new CustomFieldValues(), but that will cause update to db even though empty CustomFieldValues with no values is
                        // serialized back to null. Hibernate probably assumes that if json was null, deserialized value should also be null.
        }
        try {
            return new CustomFieldValues(json);
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to convert json to CF Value", e);
            return null;
        }
    }
}