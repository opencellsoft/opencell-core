package org.meveo.model.crm.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.customEntities.CustomEntityInstance;

/**
 * Represents a custom field value type - Child entity by CET code and consisting of a list of field values.
 * 
 * @author Andrius Karpavicius
 */
public class ChildEntityValueWrapper extends CustomEntityInstance {

    private static final long serialVersionUID = -8341601508824882094L;

    /**
     * Field values grouped by field code
     */
    private Map<String, List<CustomFieldInstance>> fieldValues = new HashMap<String, List<CustomFieldInstance>>();

    public ChildEntityValueWrapper() {
        super();
    }

    public ChildEntityValueWrapper(CustomFieldValueHolder customFieldValueHolder) {
        setUuid(customFieldValueHolder.getEntityUuid());
        fieldValues = customFieldValueHolder.getValues();
    }

    public Map<String, List<CustomFieldInstance>> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, List<CustomFieldInstance>> fieldValues) {
        this.fieldValues = fieldValues;
    }

    /**
     * Check if all fields are empty
     * 
     * @return True if all the fields are empty
     */
    public boolean isEmpty() {
        for (List<CustomFieldInstance> cfis : fieldValues.values()) {
            for (CustomFieldInstance cfi : cfis) {

                if (!cfi.isValueEmpty()) {
                    return false;
                }
            }

        }
        return true;
    }
}
