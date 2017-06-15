package org.meveo.model.crm.custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.slf4j.LoggerFactory;

/**
 * Used to facilitate custom field value data entry in GUI. Represents custom field values of a single entity
 * 
 * @author Andrius Karpavicius
 * 
 */
public class CustomFieldValueHolder implements Serializable {

    private static final long serialVersionUID = 2516863650382630587L;

    /** Logger. */
    protected org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<String, Object> newValues = new HashMap<String, Object>();

    /**
     * Values of a single entity
     */
    private Map<String, List<CustomFieldInstance>> values = new HashMap<String, List<CustomFieldInstance>>();

    private ICustomFieldEntity entity;

    /**
     * Field used to show detail values of a single value period
     */
    private CustomFieldTemplate selectedFieldTemplate;

    /**
     * Field used to show detail values of a single value period
     */
    private CustomFieldInstance selectedValuePeriod;

    /**
     * Field used to show detail values of a single value period
     */
    private String selectedValuePeriodId;

    /**
     * Was value period found with identical/overlapping dates
     */
    private Boolean valuePeriodMatched;

    /**
     * Field used to show detail values of a single child entity
     */
    private CustomFieldValueHolder selectedChildEntity;

    /**
     * Were values updated - used only in child entity type field to not update not changed values
     */
    private boolean updated;

    /**
     * Constructor
     * 
     * @param customFieldTemplates Custom field templates applicable for the entity, mapped by a CFT code
     * @param cfisAsMap Custom field instances mapped by a CFT code
     * @param entity Entity containing custom field values
     */
    public CustomFieldValueHolder(Map<String, CustomFieldTemplate> customFieldTemplates, Map<String, List<CustomFieldInstance>> cfisAsMap, ICustomFieldEntity entity) {

        this.entity = entity;
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            return;
        }

        values.putAll(cfisAsMap);

        populateNewValueDefaults(customFieldTemplates.values(), null);

    }

    /**
     * Populate GUI "new value fields" with default values
     * 
     * @param cfts A list of custom field templates to clear and [re]populate default values.
     * @param cftToReset Reset default values for a given template only. Only one - cfts or cftToReset parameter should be passed.
     */
    public void populateNewValueDefaults(Collection<CustomFieldTemplate> cfts, CustomFieldTemplate cftToReset) {

        /**
         * [Re]Populate default values for all custom fields
         */
        if (cfts != null) {
            newValues.clear();

            for (CustomFieldTemplate cft : cfts) {
                if (cft.isVersionable() && cft.getDefaultValue() != null && !newValues.containsKey(cft.getCode() + "_value")) {
                    newValues.put(cft.getCode() + "_value", cft.getDefaultValueConverted());
                }
            }
            // Reset default values for a given template only. Also remove any other with field related values such as period dates
        } else if (cftToReset != null && cftToReset.isVersionable()) {
            List<String> keysToRemove = new ArrayList<>();

            for (String fieldKey : newValues.keySet()) {
                if (fieldKey.startsWith(cftToReset.getCode() + "_")) {
                    keysToRemove.add(fieldKey);
                }
            }

            for (String keyToRemove : keysToRemove) {
                newValues.remove(keyToRemove);
            }
            if (cftToReset.getDefaultValue() != null) {
                newValues.put(cftToReset.getCode() + "_value", cftToReset.getDefaultValueConverted());
            }
        }
    }

    /**
     * Clear value from GUI "new value field" - used when displaying a popup with period values
     * 
     * @param cft Custom field template
     */
    public void clearNewValueDefaults(CustomFieldTemplate cft) {
        newValues.remove(cft.getCode() + "_value");
    }

    /**
     * Get a custom field instance corresponding to a given date. Calendar is used to determine period start/end dates if requested to create one if not found
     * 
     * @param cft Custom field template
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @return Custom field period
     */
    public CustomFieldInstance getValuePeriod(CustomFieldTemplate cft, Date date, Boolean createIfNotFound) {
        CustomFieldInstance periodFound = null;
        for (CustomFieldInstance period : values.get(cft.getCode())) {
            if (period.getPeriodRaw() != null && period.getPeriodRaw().isCorrespondsToPeriod(date)) {
                // If calendar is used for versioning, then no periods can overlap
                if (cft.getCalendar() != null) {
                    periodFound = period;
                    break;
                    // Otherwise match the period with highest priority
                } else if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            }
        }

        if (periodFound == null && createIfNotFound && cft.getCalendar() != null) {
            periodFound = CustomFieldInstance.fromTemplate(cft, entity, date);
            values.get(cft.getCode()).add(periodFound);

        }
        return periodFound;
    }

    /**
     * Get a custom field instance corresponding to a given start and end date
     * 
     * @param cft Custom field template
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @param calendar Calendar to determine period start/end dates when creating a new period
     * @param strictMatch Should a match occur only if start and end dates match. Non-strict match would match when dates overlap
     * @return Custom field period
     */
    public CustomFieldInstance getValuePeriod(CustomFieldTemplate cft, Date startDate, Date endDate, boolean strictMatch, Boolean createIfNotFound) {
        CustomFieldInstance periodFound = null;
        for (CustomFieldInstance period : values.get(cft.getCode())) {
            if (period.getPeriodRaw() != null && period.getPeriodRaw().isCorrespondsToPeriod(startDate, endDate, strictMatch)) {
                if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            }
        }
        // Create a period if match not found
        if (periodFound == null && createIfNotFound) {
            periodFound = CustomFieldInstance.fromTemplate(cft, entity, startDate, endDate, getNextPriority(cft));
            values.get(cft.getCode()).add(periodFound);
        }
        return periodFound;
    }

    /**
     * Calculate the next priority value
     * 
     * @param cft Custom field template
     * @return Integer
     */
    private int getNextPriority(CustomFieldTemplate cft) {
        int maxPriority = 0;
        for (CustomFieldInstance period : values.get(cft.getCode())) {
            maxPriority = (period.getPriority() > maxPriority ? period.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    /**
     * Add a new custom field instance, corresponding to a given date
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param date Value date
     * @return Instantiated custom field instance corresponding to a value date period
     */
    public CustomFieldInstance addValuePeriod(CustomFieldTemplate cft, Date date) {
        CustomFieldInstance period = getValuePeriod(cft, date, true);
        return period;
    }

    /**
     * Add a new custom field instance, corresponding to a given date range
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param startDate Period strt date
     * @param endDate Period end date
     * @return Instantiated custom field instance corresponding to a value date period
     */
    public CustomFieldInstance addValuePeriod(CustomFieldTemplate cft, Date startDate, Date endDate) {
        CustomFieldInstance period = getValuePeriod(cft, startDate, endDate, true, true);
        return period;
    }

    public Map<String, List<CustomFieldInstance>> getValues() {
        return values;
    }

    public List<CustomFieldInstance> getValues(CustomFieldTemplate cft) {
        return values.get(cft.getCode());
    }

    public CustomFieldInstance getFirstValue(String cftCode) {
        List<CustomFieldInstance> cfis = values.get(cftCode);
        if (cfis != null && !cfis.isEmpty()) {
            return cfis.get(0);
        }
        log.error("No custom field instance found for {} when it should", cftCode);
        return null;
    }

    /**
     * Check if any field value should be considered as empty for GUI
     * 
     * @return
     */
    public boolean isAnyFieldEmptyForGui(CustomFieldTemplate cft) {

        for (CustomFieldInstance cfi : values.get(cft.getCode())) {
            if (!cfi.isValueEmptyForGui()) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Object> getNewValues() {
        return newValues;
    }

    public void setNewValues(Map<String, Object> newValues) {
        this.newValues = newValues;
    }

    public Object getNewValue(String valueKey) {
        return newValues.get(valueKey);
    }

    public void clearNewValues() {
        newValues.clear();
    }

    public ICustomFieldEntity getEntity() {
        return entity;
    }

    public String getEntityUuid() {
        return entity.getUuid();
    }

    public String getShortRepresentationOfValues() {
        return "CustomFieldValueHolder short representation"; // TODO should we implement it??
    }

    /**
     * Check if all fields are empty
     * 
     * @return True if all the fields are empty
     */
    public boolean isEmpty() {

        for (List<CustomFieldInstance> cfis : values.values()) {
            for (CustomFieldInstance cfi : cfis) {
                if (!cfi.isValueEmptyForGui()) {
                    return false;
                }
            }
        }
        return true;
    }

    public CustomFieldValueHolder getSelectedChildEntity() {
        return selectedChildEntity;
    }

    public void setSelectedChildEntity(CustomFieldValueHolder selectedChildEntity) {
        this.selectedChildEntity = selectedChildEntity;
    }

    public CustomFieldTemplate getSelectedFieldTemplate() {
        return selectedFieldTemplate;
    }

    public void setSelectedFieldTemplate(CustomFieldTemplate selectedFieldTemplate) {
        this.selectedFieldTemplate = selectedFieldTemplate;
    }

    public CustomFieldInstance getSelectedValuePeriod() {
        return selectedValuePeriod;
    }

    public void setSelectedValuePeriod(CustomFieldInstance selectedValuePeriod) {
        this.selectedValuePeriod = selectedValuePeriod;
    }

    public String getSelectedValuePeriodId() {
        return selectedValuePeriodId;
    }

    public void setSelectedValuePeriodId(String selectedValuePeriodId) {
        this.selectedValuePeriodId = selectedValuePeriodId;
    }

    public Boolean getValuePeriodMatched() {
        return valuePeriodMatched;
    }

    public void setValuePeriodMatched(Boolean valuePeriodMatched) {
        this.valuePeriodMatched = valuePeriodMatched;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomFieldValueHolder)) {
            return false;
        }

        CustomFieldValueHolder other = (CustomFieldValueHolder) obj;

        return getEntityUuid().equals(other.getEntityUuid());
    }
}