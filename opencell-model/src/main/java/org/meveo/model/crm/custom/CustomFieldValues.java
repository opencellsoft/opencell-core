package org.meveo.model.crm.custom;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meveo.model.DatePeriod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.persistence.CustomFieldValuesConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

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

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat xmlsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Custom field values (CF value entity) grouped by a custom field code
     */
    private Map<String, List<CustomFieldValue>> valuesByCode = new HashMap<>();

    public CustomFieldValues() {
    }

    public CustomFieldValues(Map<String, List<CustomFieldValue>> valuesByCode) {
        this.valuesByCode = valuesByCode;
    }

    public Map<String, List<CustomFieldValue>> getValuesByCode() {
        return valuesByCode;
    }

    /**
     * Set custom field values as is. Not responsible for validity of what is being set. Only a check is made to mark new versionable custom field value periods as new.
     * 
     * @param valuesByCode
     */
    public void setValuesByCode(Map<String, List<CustomFieldValue>> newValuesByCode) {

        if (newValuesByCode != null) {
            for (Entry<String, List<CustomFieldValue>> valueInfo : newValuesByCode.entrySet()) {
                for (CustomFieldValue cfValue : valueInfo.getValue()) {
                    cfValue.isNewPeriod = false;
                    if (cfValue.getPeriod() != null && cfValue.getPeriod().getTo() != null) {
                        boolean cfValueExists = getCfValueByPeriod(valueInfo.getKey(), cfValue.getPeriod(), true, false) != null;
                        cfValue.isNewPeriod = !cfValueExists;
                    }
                }
            }
        }

        this.valuesByCode = newValuesByCode;
    }

    public void clearValues() {
        valuesByCode = null;
    }

    /**
     * Check if entity has a value for a given custom field
     * 
     * @param cfCode Custom field code
     * @return True if entity has a value for a given custom field
     */
    public boolean hasCfValue(String cfCode) {
        return valuesByCode != null && valuesByCode.containsKey(cfCode);
    }

    /**
     * Get a single custom field value for a given custom field. In case of versioned values (more than one entry in CF value list) a CF value corresponding to a today will be
     * returned
     * 
     * @param cfCode Custom field code
     * @return CF value entity
     */
    public CustomFieldValue getCfValue(String cfCode) {
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
     * Get a single custom field value for a given custom field for a given date
     * 
     * @param cfCode Custom field code
     * @param date Date
     * @return CF value entity
     */
    public CustomFieldValue getCfValue(String cfCode, Date date) {
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
     * Get a single custom field value for a given custom field for a given date period, strictly matching the CF value's period start/end dates
     * 
     * @param cfCode Custom field code
     * @param dateFrom Period start date
     * @param dateTo Period end date
     * @return CF value entity
     */
    public CustomFieldValue getCfValue(String cfCode, Date dateFrom, Date dateTo) {
        if (valuesByCode == null) {
            return null;
        }
        return getCfValueByPeriod(cfCode, new DatePeriod(dateFrom, dateTo), true, false);
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
     * Remove custom field values
     * 
     * @param cfCode Custom field code
     */
    public void removeValue(String cfCode) {
        if (valuesByCode == null) {
            return;
        }
        valuesByCode.remove(cfCode);
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
     * @return CF value entity
     */
    public CustomFieldValue setValue(String cfCode, Object value) {
        if (valuesByCode == null) {
            valuesByCode = new HashMap<>();
        }
        valuesByCode.put(cfCode, new ArrayList<>());
        CustomFieldValue cfValue = new CustomFieldValue(value);
        valuesByCode.get(cfCode).add(cfValue);
        return cfValue;
    }

    /**
     * Set custom field value for a given period
     * 
     * @param cfCode Custom field code
     * @param period Period
     * @param priority Priority. Will default to 0 if passed null, will default to next value if passed as -1, will be set otherwise.
     * @param value Value to set
     * @return CF value entity
     */
    public CustomFieldValue setValue(String cfCode, DatePeriod period, Integer priority, Object value) {
        if (valuesByCode == null) {
            valuesByCode = new HashMap<>();
        }

        CustomFieldValue valueByPeriod = getCfValueByPeriod(cfCode, period, true, true);
        if (priority == null) {
            valueByPeriod.setPriority(0);
        } else if (priority.intValue() >= 0) {
            valueByPeriod.setPriority(priority);
        }
        valueByPeriod.setValue(value);
        return valueByPeriod;
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
     * Return custom field values as JSON
     * 
     * @param cfts Custom field template definitions for description lookup
     * @return JSON formated string
     */
    public String asJson(Map<String, CustomFieldTemplate> cfts) {

        String json = CustomFieldValuesConverter.toJson(this);
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
}