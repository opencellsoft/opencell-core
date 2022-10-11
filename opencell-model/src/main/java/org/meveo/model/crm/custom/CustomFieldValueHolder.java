/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.crm.custom;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to facilitate custom field value data entry in GUI. Represents custom field values of a single entity
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.0.1
 */
public class CustomFieldValueHolder implements Serializable {

    private static final long serialVersionUID = 2516863650382630587L;

    /** Logger. */
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Map<String, Object> newValues = new HashMap<String, Object>();

    /**
     * Values of a single entity
     */
    private Map<String, List<CustomFieldValue>> valuesByCode = new HashMap<String, List<CustomFieldValue>>();

    private ICustomFieldEntity entity;

    /**
     * Field used to show detail values of a single value period.
     */
    private CustomFieldTemplate selectedFieldTemplate;

    /**
     * Field used to show detail values of a single value period.
     */
    private CustomFieldValue selectedValuePeriod;

    /**
     * GUI Field id used to show detail values of a single value period.
     */
    private String selectedValuePeriodId; // TODO no longer available

    /**
     * Is single value period editable.
     */
    private boolean selectedValuePeriodEdit = true;

    /**
     * Was value period found with identical/overlapping dates.
     */
    private Boolean valuePeriodMatched;

    /**
     * Field used to show detail values of a single child entity.
     */
    private CustomFieldValueHolder selectedChildEntity;

    /**
     * Were values updated - used only in child entity type field to not update not changed values.
     */
    private boolean updated;

    /**
     * Constructor.
     * 
     * @param customFieldTemplates Custom field templates applicable for the entity, mapped by a CFT code
     * @param cfValuesAsMap Custom field values mapped by a CFT code
     * @param entity Entity containing custom field values
     */
    public CustomFieldValueHolder(Map<String, CustomFieldTemplate> customFieldTemplates, Map<String, List<CustomFieldValue>> cfValuesAsMap, ICustomFieldEntity entity) {

        this.entity = entity;
        if (customFieldTemplates == null || customFieldTemplates.isEmpty()) {
            return;
        }

        valuesByCode.putAll(cfValuesAsMap);

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
     * Clear value from GUI "new value field" - used when displaying a popup with period values.
     * 
     * @param cft Custom field template
     */
    public void clearNewValueDefaults(CustomFieldTemplate cft) {
        newValues.remove(cft.getCode() + "_value");
    }

    /**
     * Get a custom field value corresponding to a given date. Calendar is used to determine period start/end dates if requested to create one if not found
     * 
     * @param cft Custom field template
     * @param date Date
     * @param createIfNotFound Should custom field value be created if not found
     * @return Custom field value corresponding to a given date
     */
    public CustomFieldValue getValuePeriod(CustomFieldTemplate cft, Date date, Boolean createIfNotFound) {
        CustomFieldValue cfValueFound = null;
        for (CustomFieldValue cfValue : valuesByCode.get(cft.getCode())) {
            if (cfValue.getPeriod() != null && cfValue.getPeriod().isCorrespondsToPeriod(date)) {
                // If calendar is used for versioning, then no periods can overlap
                if (cft.getCalendar() != null) {
                    cfValueFound = cfValue;
                    break;
                    // Otherwise match the period with highest priority
                } else if (cfValueFound == null || cfValueFound.getPriority() < cfValue.getPriority()) {
                    cfValueFound = cfValue;
                }
            }
        }

        // Create a custom field value if match not found
        if (cfValueFound == null && createIfNotFound && cft.getCalendar() != null) {
            cfValueFound = new CustomFieldValue(cft.getDatePeriod(date), 0, null);
            valuesByCode.get(cft.getCode()).add(cfValueFound);

        }
        return cfValueFound;
    }

    /**
     * Get a custom field value corresponding to a given start and end date.
     * 
     * @param cft Custom field template
     * @param startDate Date
     * @param endDate ending date.
     * @param createIfNotFound Should custom field value be created if not found
     * @param strictMatch Should a match occur only if start and end dates match. Non-strict match would match when dates overlap
     * @return Custom field value corresponding to a given start and end date
     */
    public CustomFieldValue getValuePeriod(CustomFieldTemplate cft, Date startDate, Date endDate, boolean strictMatch, Boolean createIfNotFound) {
        CustomFieldValue cfValueFound = null;
        for (CustomFieldValue cfValue : valuesByCode.get(cft.getCode())) {
            if (cfValue.getPeriod() != null && cfValue.getPeriod().isCorrespondsToPeriod(startDate, endDate, strictMatch)) {
                if (cfValueFound == null || cfValueFound.getPriority() < cfValue.getPriority()) {
                    cfValueFound = cfValue;
                }
            }
        }

        // Create a custom field value if match not found
        if (cfValueFound == null && createIfNotFound) {
            cfValueFound = new CustomFieldValue(new DatePeriod(startDate, endDate), getNextPriority(cft), null);
            valuesByCode.get(cft.getCode()).add(cfValueFound);
        }
        return cfValueFound;
    }

    /**
     * Calculate the next priority value.
     * 
     * @param cft Custom field template
     * @return Integer
     */
    private int getNextPriority(CustomFieldTemplate cft) {
        int maxPriority = 0;
        for (CustomFieldValue cfValue : valuesByCode.get(cft.getCode())) {
            maxPriority = (cfValue.getPriority() > maxPriority ? cfValue.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    /**
     * Add a new custom field value, corresponding to a given date.
     * 
     * @param cft Custom field template
     * @param date Value date
     * @return Instantiated custom field instance corresponding to a value date period
     */
    public CustomFieldValue addValuePeriod(CustomFieldTemplate cft, Date date) {
        CustomFieldValue cfValue = getValuePeriod(cft, date, true);
        return cfValue;
    }

    /**
     * Add a new custom field value, corresponding to a given date range.
     * 
     * @param cft Custom field template
     * @param startDate Custom field value start date
     * @param endDate Custom field value end date
     * @return Instantiated custom field value corresponding to a value date period
     */
    public CustomFieldValue addValuePeriod(CustomFieldTemplate cft, Date startDate, Date endDate) {
        CustomFieldValue period = getValuePeriod(cft, startDate, endDate, true, true);
        return period;
    }

    /**
     * @return list of customer field value.
     */
    public Map<String, List<CustomFieldValue>> getValuesByCode() {
        return valuesByCode;
    }

    public List<CustomFieldValue> getValues(CustomFieldTemplate cft) {
        return valuesByCode.get(cft.getCode());
    }

    public CustomFieldValue getFirstValue(String cftCode) {
        List<CustomFieldValue> cfValues = valuesByCode.get(cftCode);
        if (cfValues != null && !cfValues.isEmpty()) {
            return cfValues.get(0);
        }
        log.error("No custom field value found for {} when it should", cftCode);
        return null;
    }

    /**
     * Check if any field value should be considered as empty for GUI.
     * 
     * @param cft custom field template.
     * @return true if any field is emty.
     */
    public boolean isAnyFieldEmptyForGui(CustomFieldTemplate cft) {

        for (CustomFieldValue cfValue : valuesByCode.get(cft.getCode())) {
            if (!cfValue.isValueEmptyForGui()) {
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
     * Check if all fields are empty.
     * 
     * @return True if all the fields are empty
     */
    public boolean isEmpty() {

        for (List<CustomFieldValue> cfValues : valuesByCode.values()) {
            for (CustomFieldValue cfValue : cfValues) {
                if (!cfValue.isValueEmptyForGui()) {
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

    public CustomFieldValue getSelectedValuePeriod() {
        return selectedValuePeriod;
    }

    public void setSelectedValuePeriod(CustomFieldValue selectedValuePeriod) {
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

    public boolean isSelectedValuePeriodEdit() {
        return selectedValuePeriodEdit;
    }

    public void setSelectedValuePeriodEdit(boolean selectedValuePeriodEdit) {
        this.selectedValuePeriodEdit = selectedValuePeriodEdit;
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

    /**
     * Remove custom field value (period)
     * 
     * @param cft Custom field template
     * @param valuePeriodToRemove Custom field value (period) to remove
     * @return True if value was removed
     */
    public boolean removeValuePeriod(CustomFieldTemplate cft, CustomFieldValue valuePeriodToRemove) {
        List<CustomFieldValue> periodValues = valuesByCode.get(cft.getCode());

        if (periodValues == null) {
            return false;
        }

        boolean removed = false;

        // Remove period
        for (int i = periodValues.size() - 1; i >= 0; i--) {
            CustomFieldValue periodValue = periodValues.get(i);

            if ((periodValue.getPeriod() != null && periodValue.getPeriod().equals(valuePeriodToRemove.getPeriod())
                    || (periodValue.getPeriod() == null && valuePeriodToRemove.getPeriod() == null)) && periodValue.getPriority() == valuePeriodToRemove.getPriority()) {
                periodValues.remove(i);
                removed = true;
                break;
            }
        }

        // Reassign priority numbers if not versioned by a calendar
        if (removed && cft.getCalendar() == null && !periodValues.isEmpty()) {
            Collections.sort(periodValues, Comparator.comparing(CustomFieldValue::getPriority));

            for (int i = 0; i < periodValues.size(); i++) {
                periodValues.get(i).setPriority(i + 1);
            }
        }

        return removed;
    }

    /**
     * Increase or decrease priority of custom field value (period). A greater number means higher priority. Applies when period dates are not managed by a calendar.
     * 
     * @param cft Custom field template
     * @param valuePeriodToChange Custom field value (period) to change
     * @param increasePriority True if priority should be increased
     * @return True if priority was changed
     */
    public boolean changePriority(CustomFieldTemplate cft, CustomFieldValue valuePeriodToChange, boolean increasePriority) {
        List<CustomFieldValue> periodValues = valuesByCode.get(cft.getCode());

        if (periodValues == null || cft.getCalendar() != null) {
            return false;
        }

        int currentIndex = -1;

        // Find position of the period to change
        Collections.sort(periodValues, Comparator.comparing(CustomFieldValue::getPriority));

        for (int i = periodValues.size() - 1; i >= 0; i--) {
            CustomFieldValue periodValue = periodValues.get(i);

            if ((periodValue.getPeriod() != null && periodValue.getPeriod().equals(valuePeriodToChange.getPeriod())
                    || (periodValue.getPeriod() == null && valuePeriodToChange.getPeriod() == null)) && periodValue.getPriority() == valuePeriodToChange.getPriority()) {
                currentIndex = i;
                break;
            }
        }

        // Exchange priority numbers and reorder
        if (currentIndex > -1) {

            // Nowhere to move - either the first ir last item already
            if ((currentIndex == 0 && !increasePriority) || currentIndex == periodValues.size() - 1 && increasePriority) {
                return false;
            }

            int otherIndex = increasePriority ? currentIndex + 1 : currentIndex - 1;
            int currentPriority = periodValues.get(currentIndex).getPriority();
            int otherPriority = periodValues.get(otherIndex).getPriority();
            periodValues.get(currentIndex).setPriority(otherPriority);
            periodValues.get(otherIndex).setPriority(currentPriority);

            Collections.sort(periodValues, Comparator.comparing(CustomFieldValue::getPriority));

            for (int i = 0; i < periodValues.size(); i++) {
                periodValues.get(i).setPriority(i + 1);
            }
        }
        return currentIndex > -1;
    }
}