package org.meveo.model.crm.custom;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.meveo.model.DatePeriod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.persistence.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Represents custom field values held by an ICustomFieldEntity entity
 * 
 * @author Andrius Karpavicius
 *
 */
public class CustomFieldValues implements Serializable {

    private static final long serialVersionUID = -1733710622601844949L;

    /**
     * Date format for custom field value period conversion to DOM XML string
     */
    private static SimpleDateFormat xmlsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Custom field values (CF value entity) grouped by a custom field code.
     */
    private Map<String, List<CustomFieldValue>> valuesByCode = new HashMap<>();

    /**
     * Tracks Custom fields (code) that were added, modified, or removed. Note, as value is transient - not stored in json, it will be lost after persistence or merge.
     */
    private Set<String> dirtyCfValues = new HashSet<>();

    /**
     * Tracks Custom fields (code) periods that were added or removed. Same as dirtyCfValues minus the custom fields, which had change in value only. Note, as value is transient -
     * not stored in json, it will be lost after persistence or merge.
     */
    private Set<String> dirtyCfPeriods = new HashSet<>();

    /**
     * Constructor
     */
    public CustomFieldValues() {
    }

    /**
     * Instantiate custom field value holder with a given set of custom field values that are parsed from JSON
     * 
     * @param json JSON string containing custom field values (CF value entity) grouped by a custom field code.
     */
    public CustomFieldValues(String json) {
        this.valuesByCode = JacksonUtil.fromString(json, new TypeReference<Map<String, List<CustomFieldValue>>>() {
        });
    }

    /**
     * @return Custom field values (CF value entity) grouped by a custom field code.
     */
    public Map<String, List<CustomFieldValue>> getValuesByCode() {
        return valuesByCode;
    }

    /**
     * Set custom field values as is. Not responsible for validity of what is being set. Only a check is made to mark new versionable custom field value periods as new.
     * 
     * @param newValuesByCode values by code
     */
    public void setValuesByCode(Map<String, List<CustomFieldValue>> newValuesByCode) {
        if (newValuesByCode == null || newValuesByCode.isEmpty()) {
            clearValues();
            return;
        }

        // Mark dirty fields - the old ones that no longer exists in new ones
        if (valuesByCode != null && !valuesByCode.isEmpty()) {
            Set<String> cfs = new HashSet<>(newValuesByCode.keySet());
            cfs.removeAll(valuesByCode.keySet());
            dirtyCfValues.addAll(cfs);
            dirtyCfPeriods.addAll(cfs);
        }

        for (Entry<String, List<CustomFieldValue>> valueInfo : newValuesByCode.entrySet()) {
            String cfCode = valueInfo.getKey();
            for (CustomFieldValue cfValue : valueInfo.getValue()) {
                CustomFieldValue cfPeriodExisting = getCfValueByPeriod(valueInfo.getKey(), cfValue.getPeriod(), true, false);
                cfValue.isNewPeriod = cfPeriodExisting == null;

                // Mark dirty fields - new period, or just a value change
                if (cfPeriodExisting == null) {
                    dirtyCfValues.add(cfCode);
                    dirtyCfPeriods.add(cfCode);
                } else if ((cfPeriodExisting.getValue() == null && cfValue.getValue() == null)
                        || (cfPeriodExisting.getValue() != null && !cfPeriodExisting.getValue().equals(cfValue.getValue()))) {
                    dirtyCfValues.add(cfCode);
                }
            }

        }

        this.valuesByCode = newValuesByCode;
    }

    /**
     * clear values.
     */
    public void clearValues() {

        if (valuesByCode != null) {

            // Mark dirty fields - all existing fields
            dirtyCfPeriods.addAll(valuesByCode.keySet());
            dirtyCfValues.addAll(valuesByCode.keySet());

            valuesByCode = null;
        }
    }

    /**
     * Check if entity has a non-empty value for a given custom field.
     * 
     * @param cfCode Custom field code
     * @return True if entity has a non-empty value for a given custom field
     */
    public boolean hasCfValueNotEmpty(String cfCode) {
        if (valuesByCode == null) {
            return false;
        }

        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues == null || cfValues.isEmpty()) {
            return true;
        }

        for (CustomFieldValue cfValue : cfValues) {
            if (!cfValue.isValueEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if entity has a value (NULL value is also a valid value) for a given custom field.
     * 
     * @param cfCode Custom field code
     * @return True if entity has a value for a given custom field
     */
    public boolean hasCfValue(String cfCode) {
        if (valuesByCode == null) {
            return false;
        }

        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        return cfValues != null && !cfValues.isEmpty();
    }

    /**
     * Check if entity has a value for a given custom field on a given date. Will always return true on non-versioned fields
     * 
     * @param cfCode Custom field code
     * @return True if entity has a value for a given custom field
     */
    public boolean hasCfValue(String cfCode, Date date) {

        if (valuesByCode == null) {
            return false;
        }
        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues != null && !cfValues.isEmpty()) {
            for (CustomFieldValue cfValue : cfValues) {
                if (cfValue.getPeriod() == null || (cfValue.getPeriod() != null && cfValue.getPeriod().isCorrespondsToPeriod(date))) {
                    return true;
                }
            }
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
    public boolean hasCfValue(String cfCode, Date dateFrom, Date dateTo) {
        if (valuesByCode == null) {
            return false;
        }
        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues != null && !cfValues.isEmpty()) {
            for (CustomFieldValue value : cfValues) {
                if (value.getPeriod() == null || (value.getPeriod() != null && value.getPeriod().isCorrespondsToPeriod(dateFrom, dateTo, true))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get a single RAW custom field value entity for a given custom field. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a today
     * will be returned
     * 
     * @param cfCode Custom field code
     * @return CF value entity
     */
    private CustomFieldValue getCfValue(String cfCode) {
        if (valuesByCode == null) {
            return null;
        }
        List<CustomFieldValue> values = valuesByCode.get(cfCode);
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                return values.get(0);
            } else {
                return getCfValue(cfCode, new Date());
            }
        }
        return null;
    }

    /**
     * Get a RAW single custom field value entity for a given custom field for a given date.
     * 
     * @param cfCode Custom field code
     * @param date Date
     * @return CF value entity
     */
    private CustomFieldValue getCfValue(String cfCode, Date date) {
        if (valuesByCode == null) {
            return null;
        }
        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues != null && !cfValues.isEmpty()) {
            CustomFieldValue valueFound = null;
            for (CustomFieldValue cfValue : cfValues) {
                if (cfValue.getPeriod() == null && (valueFound == null || valueFound.getPriority() < cfValue.getPriority())) {
                    valueFound = cfValue;

                } else if (cfValue.getPeriod() != null && cfValue.getPeriod().isCorrespondsToPeriod(date)) {
                    if (valueFound == null || valueFound.getPriority() < cfValue.getPriority()) {
                        valueFound = cfValue;
                    }
                }
            }
            return valueFound;
        }

        return null;
    }

    /**
     * Get a single RAW custom field value entity for a given custom field for a given date period, strictly matching the CF value's period start/end dates
     * 
     * @param cfCode Custom field code
     * @param dateFrom Period start date
     * @param dateTo Period end date
     * @return CF value entity
     */
    @SuppressWarnings("unused")
    private CustomFieldValue getCfValue(String cfCode, Date dateFrom, Date dateTo) {
        if (valuesByCode == null) {
            return null;
        }
        return getCfValueByPeriod(cfCode, new DatePeriod(dateFrom, dateTo), true, false);
    }

    /**
     * Get a list of RAW custom field value entities for a given custom field.
     * 
     * @param cfCode Custom field code
     * @return A list of CF value entities
     */
    private List<CustomFieldValue> getCfValues(String cfCode) {
        return valuesByCode.get(cfCode);
    }

    /**
     * Get a value (not CF value entity) for a given custom field. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a today will be
     * returned
     * 
     * @param cfCode Custom field code
     * @return Value
     */
    public Object getValue(String cfCode) {
        CustomFieldValue cfValue = getCfValue(cfCode);
        if (cfValue != null) {
            return cfValue.getValue();
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
    public Object getValue(String cfCode, Date date) {
        CustomFieldValue cfValue = getCfValue(cfCode, date);
        if (cfValue != null) {
            return cfValue.getValue();
        }
        return null;
    }

    /**
     * Get custom field values (not CF value entity). In case of versioned values (more than one entry in CF value list) a CF value corresponding to today will be returned
     * 
     * @return A map of values with key being custom field code.
     */
    public Map<String, Object> getValues() {

        Map<String, Object> values = new HashMap<>();

        for (Entry<String, List<CustomFieldValue>> valueInfo : valuesByCode.entrySet()) {
            String cfCode = valueInfo.getKey();
            List<CustomFieldValue> cfValues = valueInfo.getValue();
            if (cfValues != null && !cfValues.isEmpty()) {
                CustomFieldValue valueFound = null;
                if (cfValues.size() == 1) {
                    valueFound = cfValues.get(0);

                } else {
                    Date date = new Date();

                    for (CustomFieldValue cfValue : cfValues) {
                        if (cfValue.getPeriod() == null && (valueFound == null || valueFound.getPriority() < cfValue.getPriority())) {
                            valueFound = cfValue;

                        } else if (cfValue.getPeriod() != null && cfValue.getPeriod().isCorrespondsToPeriod(date)) {
                            if (valueFound == null || valueFound.getPriority() < cfValue.getPriority()) {
                                valueFound = cfValue;
                            }
                        }
                    }
                }
                if (valueFound != null) {
                    values.put(cfCode, valueFound.getValue());
                }
            }
        }
        return values;
    }

    /**
     * Remove custom field values
     * 
     * @param cfCode Custom field code
     */
    public void removeValue(String cfCode) {
        if (valuesByCode == null) {
            return;
        }
        if (valuesByCode.containsKey(cfCode)) {
            // Mark dirty fields - both period and value change
            dirtyCfPeriods.add(cfCode);
            dirtyCfValues.add(cfCode);

            valuesByCode.remove(cfCode);
        }

    }

    /**
     * Remove custom field values for a given date
     * 
     * @param cfCode Custom field code
     * @param date Date
     */
    public void removeValue(String cfCode, Date date) {

        if (valuesByCode == null) {
            return;
        }
        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues != null && !cfValues.isEmpty()) {
            for (int i = cfValues.size() - 1; i >= 0; i--) {
                CustomFieldValue value = cfValues.get(i);
                if (value.getPeriod() == null || (value.getPeriod() != null && value.getPeriod().isCorrespondsToPeriod(date))) {
                    cfValues.remove(i);

                    // Mark dirty fields - both period and value change
                    dirtyCfPeriods.add(cfCode);
                    dirtyCfValues.add(cfCode);
                }
            }

            if (cfValues.isEmpty()) {
                valuesByCode.remove(cfCode);
            }
        }
    }

    /**
     * Remove custom field values for a given date period, strictly matching custom field value's period start and end dates
     * 
     * @param cfCode Custom field code
     * @param dateFrom Period start date
     * @param dateTo Period end date
     */
    public void removeValue(String cfCode, Date dateFrom, Date dateTo) {

        if (valuesByCode == null) {
            return;
        }
        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues != null && !cfValues.isEmpty()) {
            for (int i = cfValues.size() - 1; i >= 0; i--) {
                CustomFieldValue value = cfValues.get(i);
                if (value.getPeriod() == null || (value.getPeriod() != null && value.getPeriod().isCorrespondsToPeriod(dateFrom, dateTo, true))) {
                    cfValues.remove(i);

                    // Mark dirty fields - both period and value change
                    dirtyCfPeriods.add(cfCode);
                    dirtyCfValues.add(cfCode);
                }
            }

            if (cfValues.isEmpty()) {
                valuesByCode.remove(cfCode);
            }
        }
    }

    /**
     * Set custom field value
     * 
     * @param cfCode Custom field code
     * @param value Value to set
     */
    public void setValue(String cfCode, Object value) {
        if (valuesByCode == null) {
            valuesByCode = new HashMap<>();
        }

        // Mark dirty fields - value change
        dirtyCfValues.add(cfCode);

        List<CustomFieldValue> cfValues = valuesByCode.get(cfCode);
        if (cfValues == null || cfValues.isEmpty() || cfValues.size() > 0) {
            valuesByCode.put(cfCode, new ArrayList<>());
            CustomFieldValue cfValue = new CustomFieldValue(value);
            valuesByCode.get(cfCode).add(cfValue);

            // Mark dirty fields - new period
            dirtyCfPeriods.add(cfCode);

        } else {
            cfValues.get(0).setValue(value);
        }
    }

    /**
     * Set custom field value for a given period
     * 
     * @param cfCode Custom field code
     * @param period Period
     * @param priority Priority. Will default to 0 if passed null, will default to next value if passed as -1, will be set otherwise.
     * @param value Value to set
     */
    public void setValue(String cfCode, DatePeriod period, Integer priority, Object value) {
        if (valuesByCode == null) {
            valuesByCode = new HashMap<>();
        }

        CustomFieldValue valueByPeriod = getCfValueByPeriod(cfCode, period, true, true);

        if (priority == null && valueByPeriod.isNewPeriod) {
            valueByPeriod.setPriority(0);
        } else if (priority != null && priority.intValue() >= 0) {
            valueByPeriod.setPriority(priority);
        }
        valueByPeriod.setValue(value);

        dirtyCfValues.add(cfCode);
        if (valueByPeriod.isNewPeriod) {
            dirtyCfPeriods.add(cfCode);
        }
    }

    private CustomFieldValue getCfValueByPeriod(String cfCode, DatePeriod period, boolean strictMatch, Boolean createIfNotFound) {
        CustomFieldValue valueFound = null;
        if (valuesByCode.containsKey(cfCode)) {
            for (CustomFieldValue value : valuesByCode.get(cfCode)) {
                if (value.getPeriod() == null && (valueFound == null || valueFound.getPriority() < value.getPriority())) {
                    valueFound = value;

                } else if (value.getPeriod() != null && value.getPeriod().isCorrespondsToPeriod(period, strictMatch)) {
                    if (valueFound == null || valueFound.getPriority() < value.getPriority()) {
                        valueFound = value;
                    }
                }
            }
        }
        // Create a value for period if match not found
        if (valueFound == null && createIfNotFound) {
            if (!valuesByCode.containsKey(cfCode)) {
                valuesByCode.put(cfCode, new ArrayList<>());
            }
            valueFound = new CustomFieldValue(period, getNextPriority(cfCode), null);
            valuesByCode.get(cfCode).add(valueFound);
        }
        return valueFound;
    }

    /**
     * Calculate the next priority (max+1) for a given CF code
     * 
     * @param cfCode CF code
     * @return The next priority (max+1) value
     */
    private int getNextPriority(String cfCode) {
        int maxPriority = 0;
        for (CustomFieldValue value : valuesByCode.get(cfCode)) {
            maxPriority = (value.getPriority() > maxPriority ? value.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    /**
     * Return custom field values as JSON. Will return NUll if not values are present.
     * 
     * @return JSON formated string
     */
    public String asJson() {
        if (valuesByCode == null || valuesByCode.isEmpty()) {
            return null;
        }
        return JacksonUtil.toString(valuesByCode);
    }

    /**
     * Return custom field values as JSON. Same as asJson(), but adds CFT descriptions to the custom field values
     * 
     * @param cfts Custom field template definitions for description lookup
     * @return JSON formated string
     */
    public String asJson(Map<String, CustomFieldTemplate> cfts) {

        String json = asJson();
        if (json != null) {
            ObjectMapper om = new ObjectMapper();
            try {
                JsonNode jsonTree = om.readTree(json);
                Iterator<Map.Entry<String, JsonNode>> cfFields = jsonTree.fields();
                while (cfFields.hasNext()) {
                    Map.Entry<String, JsonNode> cfField = cfFields.next();
                    CustomFieldTemplate cft = cfts.get(cfField.getKey());
                    if (cft != null && cft.getDescription() != null) {

                        Iterator<JsonNode> cfValues = cfField.getValue().elements();
                        while (cfValues.hasNext()) {
                            ObjectNode cfValue = (ObjectNode) cfValues.next();
                            cfValue.set("description", new TextNode(cft.getDescription()));
                        }
                    }
                }
                json = om.writeValueAsString(jsonTree);

            } catch (IOException e) {
                Logger log = LoggerFactory.getLogger(getClass());
                log.error("Failed to parse json {}", json, e);
            }
            json = json.replaceAll("\"", "'");
        }
        return json;
    }

    /**
     * Append custom field values to XML document, each as "customField" element
     * 
     * @param doc Document to append custom field values
     * @param parentElement Parent elemnt to append custom field values to
     * @param cfts Custom field template definitions for description lookup
     */
    public void asDomElement(Document doc, Element parentElement, Map<String, CustomFieldTemplate> cfts) {

        for (Entry<String, List<CustomFieldValue>> cfValueInfo : valuesByCode.entrySet()) {
            CustomFieldTemplate cft = cfts.get(cfValueInfo.getKey());

            for (CustomFieldValue cfValue : cfValueInfo.getValue()) {

                Element customFieldTag = doc.createElement("customField");
                customFieldTag.setAttribute("code", cfValueInfo.getKey());
                customFieldTag.setAttribute("description", cft != null ? cft.getDescription() : "");
                if (cfValue.getPeriod() != null && cfValue.getPeriod().getFrom() != null) {
                    customFieldTag.setAttribute("periodStartDate", xmlsdf.format(cfValue.getPeriod().getFrom()));
                }
                if (cfValue.getPeriod() != null && cfValue.getPeriod().getTo() != null) {
                    customFieldTag.setAttribute("periodEndDate", xmlsdf.format(cfValue.getPeriod().getTo()));
                }

                Text customFieldText = doc.createTextNode(cfValue.toXmlText(xmlsdf));
                customFieldTag.appendChild(customFieldText);
                parentElement.appendChild(customFieldTag);
            }
        }
    }

    /**
     * Get new versioned custom field value periods
     * 
     * @return A map of new custom field value periods with custom field code as a key and list of date periods as values
     */
    public Map<String, List<DatePeriod>> getNewVersionedCFValuePeriods() {

        if (valuesByCode == null) {
            return null;
        }

        Map<String, List<DatePeriod>> newPeriods = new HashMap<>();

        for (Entry<String, List<CustomFieldValue>> valueInfo : valuesByCode.entrySet()) {
            for (CustomFieldValue cfValue : valueInfo.getValue()) {
                if (cfValue.isNewPeriod && cfValue.getPeriod() != null && cfValue.getPeriod().getTo() != null) {
                    if (!newPeriods.containsKey(valueInfo.getKey())) {
                        newPeriods.put(valueInfo.getKey(), new ArrayList<>());
                    }
                    newPeriods.get(valueInfo.getKey()).add(cfValue.getPeriod());
                }
            }
        }

        return newPeriods;
    }

    /**
     * Override (matching the period) existing RAW custom field value entities or append missing ones for a given custom field
     * 
     * @param cfCode Custom field code
     * @param cfValues Custom field value holder with values to override with. If value is null or is empty, a record for a given custom field will be removed altogether as in
     *        removeValue()
     */
    public void overrideOrAppendCfValues(String cfCode, CustomFieldValues cfValues) {

        if (cfValues == null) {
            if (valuesByCode.containsKey(cfCode)) {

                valuesByCode.remove(cfCode);

                // Mark fields dirty - both period and value change
                dirtyCfPeriods.add(cfCode);
                dirtyCfValues.add(cfCode);
            }
        }

        List<CustomFieldValue> cfValueList = cfValues.getCfValues(cfCode);

        if (cfValueList == null || cfValueList.isEmpty()) {

            if (valuesByCode.containsKey(cfCode)) {

                valuesByCode.remove(cfCode);

                // Mark fields dirty - both period and value change
                dirtyCfPeriods.add(cfCode);
                dirtyCfValues.add(cfCode);
            }

        } else {
            for (CustomFieldValue customFieldValueToOverride : cfValueList) {
                CustomFieldValue customFieldValueFound = getCfValueByPeriod(cfCode, customFieldValueToOverride.getPeriod(), true, true);
                customFieldValueFound.setValue(customFieldValueToOverride.getValue());

                dirtyCfValues.add(cfCode);

                if (customFieldValueFound.isNewPeriod) {
                    // Mark fields dirty - new period
                    dirtyCfPeriods.add(cfCode);
                }
            }
        }
    }

    /**
     * Append missing (or missing periods) RAW custom field value entities to existing ones for a given custom field
     * 
     * @param cfCode Custom field code
     * @param cfValues Custom field value holder with values to append
     * @return True if new values were appended
     */
    public boolean appendCfValues(String cfCode, CustomFieldValues cfValues) {
        if (cfValues == null) {
            return false;
        }
        List<CustomFieldValue> cfValueList = cfValues.getCfValues(cfCode);
        if (cfValueList == null || cfValueList.isEmpty()) {
            return false;
        }
        boolean hasChanged = false;

        for (CustomFieldValue customFieldValueToOverride : cfValueList) {
            CustomFieldValue customFieldValueFound = getCfValueByPeriod(cfCode, customFieldValueToOverride.getPeriod(), true, false);
            if (customFieldValueFound == null) {
                if (!valuesByCode.containsKey(cfCode)) {
                    valuesByCode.put(cfCode, new ArrayList<>());
                }
                valuesByCode.get(cfCode).add(customFieldValueToOverride);// TODO need to increment priority in case of versioned fields
                hasChanged = true;

                // Mark fields dirty - both period and value change
                dirtyCfPeriods.add(cfCode);
                dirtyCfValues.add(cfCode);
            }
        }
        return hasChanged;
    }

    /**
     * Copy Custom field values from another custom field value holder for a given custom field
     * 
     * @param cfCode Custom field code
     * @param cfValues Custom field value holder with values to copy from. If value is null or is empty, a record for a given custom field will be removed altogether as in
     *        removeValue()
     */
    public void copyCfValues(String cfCode, CustomFieldValues cfValues) {

        if (cfValues == null) {
            removeValue(cfCode);
            return;
        }

        List<CustomFieldValue> cfValueList = cfValues.getCfValues(cfCode);
        if (cfValueList == null || cfValueList.isEmpty()) {
            removeValue(cfCode);
        } else {
            valuesByCode.put(cfCode, cfValueList);

            // Mark fields dirty - both period and value change
            dirtyCfPeriods.add(cfCode);
            dirtyCfValues.add(cfCode);
        }
    }
}