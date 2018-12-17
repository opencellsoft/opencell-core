package org.meveo.model;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * An entity that contains custom fields
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public interface ICustomFieldEntity {

    /**
     * Get unique identifier.
     * 
     * @return uuid
     */
    public String getUuid();

    /**
     * Set a new UUID value.
     * 
     * @return Old UUID value
     */
    public String clearUuid();

    /**
     * Get an array of parent custom field entity in case custom field values should be inherited from a parent entity.
     * 
     * @return An entity
     */
    public ICustomFieldEntity[] getParentCFEntities();

    /**
     * @return Custom field values holder
     */
    public CustomFieldValues getCfValues();

    /**
     * @param cfValues Custom field values holder
     */
    public void setCfValues(CustomFieldValues cfValues);

    /**
     * Instantiate custom field values holder if it is null (the case when entity with no CF values is retrieved from DB)
     * 
     * @return Custom field values holder
     */
    public default CustomFieldValues getCfValuesNullSafe() {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues == null) {
            setCfValues(new CustomFieldValues());
            return getCfValues();
        }
        return cfValues;
    }

    /**
     * Clear custom field values
     */
    public default void clearCfValues() {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues == null) {
            return;
        }
        cfValues.clearValues();
    }

    /**
     * Check if entity has a non-empty value for a given custom field.
     * 
     * @param cfCode Custom field code
     * @return True if entity has a non-empty value for a given custom field
     */
    public default boolean hasCFValueNotEmpty(String cfCode) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            return cfValues.hasCfValueNotEmpty(cfCode);
        }
        return false;
    }

    /**
     * Check if entity has a value for a given custom field.
     * 
     * @param cfCode Custom field code
     * @return True if entity has a value for a given custom field
     */
    public default boolean hasCfValue(String cfCode) {

        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            return cfValues.hasCfValue(cfCode);
        }
        return false;
    }

    /**
     * Check if entity has a value for a given custom field on a given date
     * 
     * @param cfCode Custom field code
     * @param date Date to check for
     * @return True if entity has a value for a given custom field and on a given date
     */
    public default boolean hasCfValue(String cfCode, Date date) {

        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            return cfValues.hasCfValue(cfCode, date);
        }
        return false;
    }

    /**
     * Check if entity has a value for a given custom field on a given date period, strictly matching the CF value's period start/end dates
     * 
     * @param cfCode Custom field code
     * @param dateFrom Period start date
     * @param dateTo Period end date
     * @return True if entity has a value for a given custom field
     */
    public default boolean hasCfValue(String cfCode, Date dateFrom, Date dateTo) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            return cfValues.hasCfValue(cfCode, dateFrom, dateTo);
        }
        return false;
    }

    /**
     * Get custom field values (not CF value entity). In case of versioned values (more than one entry in CF value list) a CF value corresponding to today will be returned
     * 
     * @return A map of values with key being custom field code.
     */
    public default Map<String, Object> getCfValuesAsValues() {
        CustomFieldValues cfValues = getCfValues();
		if (cfValues != null && cfValues.getValuesByCode() != null) {
            return cfValues.getValues();
        }
        return null;
    }

    /**
     * Get a value (not CF value entity) for a given custom field. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a today will be
     * returned
     * 
     * @param cfCode Custom field code
     * @return Value
     */
    public default Object getCfValue(String cfCode) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            return cfValues.getValue(cfCode);
        }
        return null;
    }

    /**
     * Get a value (not CF value entity) for a given custom field for a given date
     * 
     * @param cfCode Custom field code
     * @param date Date
     * @return Value
     */
    public default Object getCfValue(String cfCode, Date date) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            return cfValues.getValue(cfCode, date);
        }
        return null;
    }

    /**
     * Match custom field's map's key as close as possible to the key provided and return a map value (not CF value entity). Match is performed by matching a full string and then
     * reducing one by one symbol until a match is found. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a today will be returned
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param cfCode Custom field code
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public default Object getCFValueByClosestMatch(String cfCode, String keyToMatch) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            Object valueMatched = cfValues.getValueByClosestMatch(cfCode, keyToMatch);
            return valueMatched;
        }
        return null;
    }

    /**
     * Match for a given date (versionable values) custom field's map's key as close as possible to the key provided and return a map value (not CF value entity). Match is
     * performed by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param cfCode Custom field code
     * @param date Date to check for
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public default Object getCFValueByClosestMatch(String cfCode, Date date, String keyToMatch) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            Object valueMatched = cfValues.getValueByClosestMatch(cfCode, date, keyToMatch);
            return valueMatched;
        }
        return null;
    }

    /**
     * Remove custom field values
     * 
     * @param cfCode Custom field code
     */
    public default void removeCfValue(String cfCode) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            cfValues.removeValue(cfCode);
        }
    }

    /**
     * Remove custom field values for a given date
     * 
     * @param cfCode Custom field code
     * @param date Date
     */
    public default void removeCfValue(String cfCode, Date date) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            cfValues.removeValue(cfCode, date);
        }
    }

    /**
     * Remove custom field values for a given date period, strictly matching custom field value's period start and end dates
     * 
     * @param cfCode Custom field code
     * @param dateFrom Period start date
     * @param dateTo Period end date
     */
    public default void removeCfValue(String cfCode, Date dateFrom, Date dateTo) {
        CustomFieldValues cfValues = getCfValues();
        if (cfValues != null) {
            cfValues.removeValue(cfCode, dateFrom, dateTo);
        }
    }

    /**
     * Set custom field value. A raw implementation. Consider using CustomFieldInstanceService.setCFValue() for more controlled logic.
     * 
     * @param cfCode Custom field code
     * @param value Value to set. If value is null, it will store a NULL value - consider using removeCfValue() instead if you want to remove CF value if it is null.
     */
    public default void setCfValue(String cfCode, Object value) {
        getCfValuesNullSafe().setValue(cfCode, value);
    }

    /**
     * Set custom field value for a given period. A raw implementation. Consider using CustomFieldInstanceService.setCFValue() for more controlled logic.
     * 
     * @param cfCode Custom field code
     * @param period Period
     * @param priority Priority. Will default to 0 if passed null, will default to next value if passed as -1, will be set otherwise.
     * @param value Value to set. If value is null, it will store a NULL value - consider using removeCfValue() instead if you want to remove CF value if it is null.
     */
    public default void setCfValue(String cfCode, DatePeriod period, Integer priority, Object value) {
        getCfValuesNullSafe().setValue(cfCode, period, priority, value);
    }

    /**
     * @return Accumulated Custom field values holder
     */
    public CustomFieldValues getCfAccumulatedValues();

    /**
     * Instantiate Accumulated custom field values holder if it is null (the case when entity with no CF values is retrieved from DB)
     * 
     * @return Custom field values holder
     */
    public default CustomFieldValues getCfAccumulatedValuesNullSafe() {
        CustomFieldValues cfValues = getCfAccumulatedValues();
        if (cfValues == null) {
            setCfAccumulatedValues(new CustomFieldValues());
            return getCfAccumulatedValues();
        }
        return cfValues;
    }

    /**
     * @param cfValues Accumulated Custom field values holder
     */
    public void setCfAccumulatedValues(CustomFieldValues cfValues);

    /**
     * Get an accumulated value (not CF value entity) for a given custom field. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a
     * today will be returned
     * 
     * @param cfCode Custom field code
     * @return Accumulated field value
     */
    public default Object getCfAccumulatedValue(String cfCode) {
        CustomFieldValues cfValues = getCfAccumulatedValues();
        if (cfValues != null) {
            return cfValues.getValue(cfCode);
        }
        return null;
    }

    /**
     * Get an accumulated value (not CF value entity) for a given custom field for a given date
     * 
     * @param cfCode Custom field code
     * @param date Date
     * @return Accumulated field value
     */
    public default Object getCfAccumulatedValue(String cfCode, Date date) {
        CustomFieldValues cfValues = getCfAccumulatedValues();
        if (cfValues != null) {
            return cfValues.getValue(cfCode, date);
        }
        return null;
    }

    /**
     * Match custom field's map's key as close as possible to the key provided and return a map value (not CF value entity). Match is performed by matching a full string and then
     * reducing one by one symbol until a match is found. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a today will be returned
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param cfCode Custom field code
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public default Object getCFAccumulatedValueByClosestMatch(String cfCode, String keyToMatch) {
        CustomFieldValues cfValues = getCfAccumulatedValues();
        if (cfValues != null) {
            Object valueMatched = cfValues.getValueByClosestMatch(cfCode, keyToMatch);
            return valueMatched;
        }
        return null;
    }

    /**
     * Match for a given date (versionable values) custom field's map's key as close as possible to the key provided and return a map value (not CF value entity). Match is
     * performed by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param cfCode Custom field code
     * @param date Date to check for
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public default Object getCFAccumulatedValueByClosestMatch(String cfCode, Date date, String keyToMatch) {
        CustomFieldValues cfValues = getCfAccumulatedValues();
        if (cfValues != null) {
            Object valueMatched = cfValues.getValueByClosestMatch(cfCode, date, keyToMatch);
            return valueMatched;
        }
        return null;
    }

    /**
     * Match as close as possible map's key to the key provided and return a map value. Match is performed by matching a full string and then reducing one by one symbol untill a
     * match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     *
     * @param value Value to inspect
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    @SuppressWarnings("unchecked")
    public static Object matchClosestValue(Object value, String keyToMatch) {
        if (value == null || !(value instanceof Map) || StringUtils.isEmpty(keyToMatch)) {
            return null;
        }
        // Logger log = LoggerFactory.getLogger(ICustomFieldEntity.class);
        Object valueFound = null;
        Map<String, Object> mapValue = (Map<String, Object>) value;
        // log.trace("matchClosestValue keyToMatch: {} in {}", keyToMatch, mapValue);
        for (int i = keyToMatch.length(); i > 0; i--) {
            valueFound = mapValue.get(keyToMatch.substring(0, i));
            if (valueFound != null) {
                // log.trace("matchClosestValue found value: {} for key: {}", valueFound, keyToMatch.substring(0, i));
                return valueFound;
            }
        }

        return null;
    }
}