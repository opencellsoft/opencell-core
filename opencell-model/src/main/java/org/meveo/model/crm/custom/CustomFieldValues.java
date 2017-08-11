package org.meveo.model.crm.custom;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meveo.model.DatePeriod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

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
    
    public void setValuesByCode(Map<String, List<CustomFieldValue>> valuesByCode) {
        this.valuesByCode = valuesByCode;
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
        return getValueByPeriod(cfCode, new DatePeriod(dateFrom, dateTo), true, false);
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
            cfValue.deserializeValue();
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
            cfValue.deserializeValue();
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

        CustomFieldValue valueByPeriod = getValueByPeriod(cfCode, period, true, true);
        if (priority == null) {
            valueByPeriod.setPriority(0);
        } else if (priority.intValue() >= 0) {
            valueByPeriod.setPriority(priority);
        }
        valueByPeriod.setValue(value);
        return valueByPeriod;
    }

    private CustomFieldValue getValueByPeriod(String cfCode, DatePeriod period, boolean strictMatch, Boolean createIfNotFound) {
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
     * @return JSON formated string
     */
    public String asJson() {

        if (valuesByCode == null) {
            return "";
        }

        String result = "";
        String sep = "";
        for (Entry<String, List<CustomFieldValue>> cfValueInfo : valuesByCode.entrySet()) {
            for (CustomFieldValue cfValue : cfValueInfo.getValue()) {
                result = result + sep + cfValueInfo.getKey() + ":" + cfValue.toJson(sdf) + ",description:" + "";

                if (cfValue.getPeriod() != null && cfValue.getPeriod().getFrom() != null) {
                    result = result + "," + "periodStartDate:\"" + sdf.format(cfValue.getPeriod().getFrom()) + "\"";
                }
                if (cfValue.getPeriod() != null && cfValue.getPeriod().getTo() != null) {
                    result = result + "," + "periodEndDate:\"" + sdf.format(cfValue.getPeriod().getTo()) + "\"";
                }

                sep = ";";
            }
        }

        return result;
    }

    /**
     * Append custom field values to XML document, each as "customField" element
     * 
     * @param doc Document to append custom field values
     */
    public void asDomElement(Document doc) {

        for (Entry<String, List<CustomFieldValue>> cfValueInfo : valuesByCode.entrySet()) {
            for (CustomFieldValue cfValue : cfValueInfo.getValue()) {

                Element customFieldTag = doc.createElement("customField");
                customFieldTag.setAttribute("code", cfValueInfo.getKey());
                customFieldTag.setAttribute("description", "");
                if (cfValue.getPeriod() != null && cfValue.getPeriod().getFrom() != null) {
                    customFieldTag.setAttribute("periodStartDate", xmlsdf.format(cfValue.getPeriod().getFrom()));
                }
                if (cfValue.getPeriod() != null && cfValue.getPeriod().getTo() != null) {
                    customFieldTag.setAttribute("periodEndDate", xmlsdf.format(cfValue.getPeriod().getTo()));
                }

                Text customFieldText = doc.createTextNode(cfValue.toXmlText(xmlsdf));
                customFieldTag.appendChild(customFieldText);
            }
        }
    }
}