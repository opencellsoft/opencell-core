package org.meveo.model;

import java.util.Date;
import java.util.Map;

import org.meveo.model.crm.CustomFieldInstance;

/**
 * An entity that contains custom fields
 * 
 * @author Andrius Karpavicius
 * 
 */
public interface ICustomFieldEntity {

    public Map<String, CustomFieldInstance> getCustomFields();

    public void setCustomFields(Map<String, CustomFieldInstance> customFields);

    /**
     * Get custom field value (non-versioned fields only) from a current entity
     * 
     * @param cfCode Custom field code
     * @return A value (corresponds to value type supported by CustomFieldValue class)
     */
    public Object getCFValue(String cfCode);

    /**
     * Get custom field value for a given date (versioned fields only. For non-versioned fields, date will be ignored)
     * 
     * @param cfCode Custom field code
     * @param date Period date
     * @return A value (corresponds to value type supported by CustomFieldValue class)
     */
    public Object getCFValue(String cfCode, Date date);

    /**
     * Get a parent custom field entity in case custom field values should be inherited from a parent entity
     * 
     * @return An entity
     */
    public ICustomFieldEntity getParentCFEntity();

    /**
     * Get inherited custom field value (non-versioned fields only). Does NOT check if current entity has a value set
     * 
     * @param cfCode Custom field code
     * @return A value (corresponds to value type supported by CustomFieldValue class)
     */
    public Object getInheritedOnlyCFValue(String cfCode);

    /**
     * Get inherited custom field value for a given date (versioned fields only. For non-versioned fields, date will be ignored). Does NOT check if current entity has a value set
     * 
     * @param cfCode Custom field code
     * @param date Period date
     * @return A value (corresponds to value type supported by CustomFieldValue class)
     */
    public Object getInheritedOnlyCFValue(String cfCode, Date date);

    /**
     * Get custom field value (non-versioned fields only) from a current entity or inherited value from a parent CF entity if one applies
     * 
     * @param cfCode Custom field code
     * @return A value (corresponds to value type supported by CustomFieldValue class)
     */
    public Object getInheritedCFValue(String cfCode);

    /**
     * Get custom field value for a given date (versioned fields only. For non-versioned fields, date will be ignored) from a current entity or inherited value from a parent CF
     * entity if one applies
     * 
     * @param cfCode Custom field code
     * @param date Period date
     * @return A value (corresponds to value type supported by CustomFieldValue class)
     */
    public Object getInheritedCFValue(String cfCode, Date date);
}