package org.meveo.model;

import java.util.Date;

import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * An entity that contains custom fields
 * 
 * @author Andrius Karpavicius
 * 
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
        setCfValues(null);
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
}