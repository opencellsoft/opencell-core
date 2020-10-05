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
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Encapsulates a custom field value. Supports the following data types:
 * <ul>
 * <li>string, date, double and long that are stored as separate fields</li>
 * <li>reference to an entity, that is serialized as Json to serializedValue field</li>
 * <li>a child entity containing the actual data, that is serialized as Json to serializedValue field</li>
 * <li>a list or a map of above mentioned data types, serialized as Json to to serializedValue field</li>
 * </ul>
 * <p>
 * A reference to an entity, child entity, list and map values should not be modified behind the scenes - an appropriate SET method has to be called to serialize the value. - This
 * limitations comes from MERGE loosing transient values and thus JPA callback @postUpdate can not be used (see CustomFieldInstance class).
 * <p>
 * Serialized value format is described in serializeValue() method for each data type.
 * <p>
 * entityReferenceValueForGUI, mapValuesForGUI, matrixValuesForGUI fields are used in data entry from GUI ONLY.
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomFieldValue implements Serializable, Cloneable {

    private static final long serialVersionUID = -9038541899269528670L;

    /**
     * In a deserialized matrix, represents a map key name to hold the names of matrix keys In Map/matrix value management for GUI (a list of maps), represents a map key name to
     * hold key value
     */
    public static final String MAP_KEY = "key";

    /**
     * In Map/matrix value management for GUI (a list of maps), represents a map key name to hold a value
     */
    public static final String MAP_VALUE = "value";

    /**
     * A separator for matrix column names
     */
    public static final String MATRIX_COLUMN_NAME_SEPARATOR = "/";

    /**
     * A separator for matrix keys
     */
    public static final String MATRIX_KEY_SEPARATOR = "|";

    /**
     * A separator for values in a range of numbers
     */
    public static final String RON_VALUE_SEPARATOR = "<";

    private static String SERIALIZATION_SEPARATOR = "|";

    public static final String WILDCARD_MATCH_ALL = "*";

    /**
     * Period to which value applies to
     */
    @JsonUnwrapped()
    private DatePeriod period;

    /**
     * Value priority if periods overlap. The higher the number, the higher the priority is.
     */
    private Integer priority;

    /**
     * String type value
     */
    @JsonProperty("string")
    private String stringValue;

    /**
     * Date type value
     */
    @JsonProperty("date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date dateValue;

    /**
     * Long type value
     */
    @JsonProperty("long")
    private Long longValue;

    /**
     * Double type value
     */
    @JsonProperty("double")
    private Double doubleValue;

    /**
     * Double type value
     */
    @JsonProperty("boolean")
    private Boolean booleanValue;

    /**
     * Entity reference type value
     */
    @JsonProperty("entity")
    private EntityReferenceWrapper entityReferenceValue;

    /**
     * List of Strings type value
     */
    @JsonProperty("listString")
    private List<String> listStringValue = null;

    /**
     * List of Date type value
     */
    @JsonProperty("listDate")
    @JsonSerialize(contentUsing = CustomDateSerializer.class)
    private List<Date> listDateValue = null;

    /**
     * List of Long type value
     */
    @JsonProperty("listLong")
    private List<Long> listLongValue = null;

    /**
     * List of Double type value
     */
    @JsonProperty("listDouble")
    private List<Double> listDoubleValue = null;

    /**
     * List of Boolean type value
     */
    @JsonProperty("listBoolean")
    private List<Boolean> listBooleanValue = null;

    /**
     * List of Entity references type value
     */
    @JsonProperty("listEntity")
    private List<EntityReferenceWrapper> listEntityValue = null;

    /**
     * Map of String type value
     */
    @JsonProperty("mapString")
    private Map<String, String> mapStringValue = null;

    /**
     * Map of Date type value
     */
    @JsonProperty("mapDate")
    @JsonSerialize(contentUsing = CustomDateSerializer.class)
    private Map<String, Date> mapDateValue = null;

    /**
     * Map of Long type value
     */
    @JsonProperty("mapLong")
    private Map<String, Long> mapLongValue = null;

    /**
     * Map of Double type value
     */
    @JsonProperty("mapDouble")
    private Map<String, Double> mapDoubleValue = null;

    /**
     * Map of Boolean type value
     */
    @JsonProperty("mapBoolean")
    private Map<String, Boolean> mapBooleanValue = null;

    /**
     * Map of Entity reference type value
     */
    @JsonProperty("mapEntity")
    private Map<String, EntityReferenceWrapper> mapEntityValue = null;

    /**
     * Source entity/accumulation path that value came from
     */
    @JsonProperty("source")
    private String source = null;

    /**
     * Contains mapValue adapted for GUI data entry in the following way:
     * <p>
     * List item corresponds to an entry in a mapValue with the following list's map values: MAP_KEY=mapValue.entry.key and MAP_VALUE=mapValue.entry.value
     */
    @JsonIgnore
    private List<Map<String, Object>> mapValuesForGUI = new ArrayList<Map<String, Object>>();

    /**
     * Contains mapValue adapted for GUI data entry in the following way:
     * <p>
     * List item corresponds to an entry in a mapValue with the following list's map values: MAP_VALUE=mapValue.entry.value, mapValue.entry.key is parsed into separate key/value
     * pairs and inserted into map
     */
    @JsonIgnore
    private List<Map<String, Object>> matrixValuesForGUI = new ArrayList<Map<String, Object>>();

    /**
     * Contains entity reference converted into a CustomFieldValueHolder object in the following way:
     * <p>
     * CustomFieldValueHolder.entity = entity reference CEI object, CustomFieldValueHolder.values = childEntityValue.fieldValues
     */
    @JsonIgnore
    private List<CustomFieldValueHolder> childEntityValuesForGUI = new ArrayList<>();

    /**
     * Contains entityReferenceValue converted into a BusinessEntity object in the following way:
     * <p>
     * A class of entityReferenceValue.className type is instantiated with code field set to entityReferenceValue.code value and in case it is Custom Entity Instance class, a
     * cetCode field is set to entityReferenceValue.classnameCode value
     */
    @JsonIgnore
    private IReferenceEntity entityReferenceValueForGUI;

    /**
     * Is it a newly entered period that was not saved to DB yet
     */
    @JsonIgnore
    private boolean isNewPeriod = false;

    /**
     * A lazy dataset of type LazyDataModel for list, map and matrix type value display for data entry in GUI to be used in p:dataTable component
     */
    @JsonIgnore
    private Object datasetForGUI;

    /**
     * Custom Table Code.
     */
    @JsonProperty("customTableCode")
    private String customTableCode;
    /**
     * Filters for custom table wrapper.
     */
    @JsonProperty("dataFilter")
    private String dataFilter;

    /**
     * Fields for custom table wrapper.
     */
    @JsonProperty("fields")
    private String fields;

    /**
     * Custom field value instance
     */
    public CustomFieldValue() {
    }

    /**
     * Instantiate Custom field value with a given value
     *
     * @param value Value to assign
     */
    public CustomFieldValue(Object value) {
        setValue(value);
    }

    /**
     * Instantiate Custom field value with a given value and a period
     * 
     * @param period Value validity period
     * @param priority Priority to assign
     * @param value Value to assign
     */
    public CustomFieldValue(DatePeriod period, Integer priority, Object value) {
        this.period = period;
        this.priority = priority;
        setValue(value);
        this.isNewPeriod = true;
    }

    /**
     * @return boolean value of isNewPeriod
     */
    public boolean isNewPeriod() {
        return isNewPeriod;
    }

    /**
     * set a new value for variable isNewPeriod
     */
    public void setNewPeriod(boolean value) {
        this.isNewPeriod = value;
    }

    /**
     * @return String type value
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * @param stringValue String type value
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * @return Date type value
     */
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * @param dateValue Date type value
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * @return Long type value
     */
    public Long getLongValue() {
        return longValue;
    }

    /**
     * @param longValue Long type value
     */
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    /**
     * @return Double type value
     */
    public Double getDoubleValue() {
        return doubleValue;
    }

    /**
     * @param doubleValue Double type value
     */
    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    /**
     * @return Boolean type value
     */
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    /**
     * @param booleanValue Boolean type value
     */
    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * @return List of Boolean type value
     */
    public List<Boolean> getListBooleanValue() {
        return listBooleanValue;
    }

    /**
     * @param listBooleanValue List of Boolean type value
     */
    public void setListBooleanValue(List<Boolean> listBooleanValue) {
        this.listBooleanValue = listBooleanValue;
    }

    /**
     * @return Map of Boolean type value
     */
    public Map<String, Boolean> getMapBooleanValue() {
        return mapBooleanValue;
    }

    /**
     * @param mapBooleanValue Map of Boolean type value
     */
    public void setMapBooleanValue(Map<String, Boolean> mapBooleanValue) {
        this.mapBooleanValue = mapBooleanValue;
    }

    /**
     * @param mapValuesForGUI Map type values when entered from GUI
     */
    public void setMapValuesForGUI(List<Map<String, Object>> mapValuesForGUI) {
        this.mapValuesForGUI = mapValuesForGUI;
    }

    /**
     * @return Map type values when displayed in GUI
     */
    public List<Map<String, Object>> getMapValuesForGUI() {
        return mapValuesForGUI;
    }

    /**
     * @return Matrix/Map type values when displayed in GUI
     */
    public List<Map<String, Object>> getMatrixValuesForGUI() {
        return matrixValuesForGUI;
    }

    /**
     * @param matrixValuesForGUI Matrix/map type values when entered from GUI
     */
    public void setMatrixValuesForGUI(List<Map<String, Object>> matrixValuesForGUI) {
        this.matrixValuesForGUI = matrixValuesForGUI;
    }

    /**
     * @return Entity reference value
     */
    public EntityReferenceWrapper getEntityReferenceValue() {
        return entityReferenceValue;
    }

    /**
     * Set a reference to an entity value. Value is serialized immediately.
     * <p>
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted, or explicitly call serializeValue() afterwards.
     * 
     * @param entityReferenceValue Reference to an entity value
     */
    public void setEntityReferenceValue(EntityReferenceWrapper entityReferenceValue) {
        this.entityReferenceValue = entityReferenceValue;
    }

    /**
     * @return List type value
     */
    public List getListValue() {
        if (listStringValue != null) {
            return listStringValue;
        } else if (listDateValue != null) {
            return listDateValue;
        } else if (listLongValue != null) {
            return listLongValue;
        } else if (listDoubleValue != null) {
            return listDoubleValue;
        } else if (listBooleanValue != null) {
            return listBooleanValue;
        } else if (listEntityValue != null) {
            return listEntityValue;
        }

        return null;
    }

    /**
     * Set a list of values.
     * 
     * @param listValue list of values to set.
     */
    public void setListValue(List listValue) {

        Iterator iterator = ((List) listValue).iterator();
        Class itemClass = findItemClass(iterator);

        if (itemClass == null) {
            // #4804 : clear list when un select all form the checkbox menu
            if (listStringValue != null) {
                listStringValue.clear();
            }
        } else if (itemClass == String.class) {
            listStringValue = new ArrayList<>();
            for (Object listItem : listValue) {
                // prevent duplicated values
                if (!listStringValue.contains(listItem)) {
                    listStringValue.add(listItem.toString());
                }
            }

        } else if (itemClass == Date.class) {
            listDateValue = new ArrayList<>();
            for (Object listItem : listValue) {
                listDateValue.add((Date) listItem);
            }

        } else if (itemClass == Integer.class || itemClass == Long.class) {
            listLongValue = new ArrayList<>();
            for (Object listItem : listValue) {
                if (listItem instanceof Integer) {
                    listLongValue.add(((Integer) listItem).longValue());
                } else if (listItem instanceof Long) {
                    listLongValue.add(((Long) listItem));
                }
            }

        } else if (itemClass == Double.class || itemClass == BigDecimal.class) {
            listDoubleValue = new ArrayList<>();
            for (Object listItem : listValue) {
                if (listItem instanceof BigDecimal) {
                    listDoubleValue.add(((BigDecimal) listItem).doubleValue());
                } else if (listItem instanceof Double) {
                    listDoubleValue.add((Double) listItem);
                }
            }
        } else if (itemClass == Boolean.class) {
            listBooleanValue = new ArrayList<>();
            for (Object listItem : listValue) {
                if (listItem instanceof Boolean) {
                    listBooleanValue.add(((Boolean) listItem).booleanValue());
                }
            }

        } else if (itemClass == EntityReferenceWrapper.class) {
            listEntityValue = new ArrayList<>();
            for (Object listItem : listValue) {
                listEntityValue.add((EntityReferenceWrapper) listItem);
            }
        }
    }

    /**
     * @return Map type value
     */
    public Map getMapValue() {
        if (mapStringValue != null && !mapStringValue.isEmpty()) {
            return mapStringValue;
        } else if (mapDateValue != null && !mapDateValue.isEmpty()) {
            return mapDateValue;
        } else if (mapLongValue != null && !mapLongValue.isEmpty()) {
            return mapLongValue;
        } else if (mapDoubleValue != null && !mapDoubleValue.isEmpty()) {
            return mapDoubleValue;
        } else if (mapBooleanValue != null && !mapBooleanValue.isEmpty()) {
            return mapBooleanValue;
        } else if (mapEntityValue != null && !mapEntityValue.isEmpty()) {
            return mapEntityValue;
        }
        return null;
    }

    /**
     * Set a map of values. Value is serialized immediately.
     * <p>
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted, or explicitly call serializeValue() afterwards.
     * 
     * @param mapValue A map of values
     */
    public void setMapValue(Map<String, Object> mapValue) {

        Map<String, Object> mapCopy = null;
        // Handle map that stores matrix type values - not interested in storing matrix column names
        if (mapValue.containsKey(MAP_KEY)) {

            mapCopy = new LinkedHashMap<String, Object>();
            mapCopy.putAll(mapValue);
            mapCopy.remove(MAP_KEY);

            // Object columnNames = mapValue.get(MAP_KEY);
            // String columnNamesString = null;
            // if (columnNames instanceof String) {
            // columnNamesString = (String) columnNames;
            //
            // } else if (columnNames instanceof Collection) {
            // columnNamesString = StringUtils.concatenate(MATRIX_COLUMN_NAME_SEPARATOR, (Collection) columnNames);
            // }

            // A regular map
        } else {
            mapCopy = mapValue;
        }

        // Find the first not null value to determine item class.
        Iterator iterator = mapCopy.values().iterator();
        Class itemClass = findItemClass(iterator);

        if (itemClass == String.class) {
            mapStringValue = new LinkedHashMap<>();
            for (Entry<String, Object> mapItem : mapCopy.entrySet()) {
                mapStringValue.put(mapItem.getKey(), mapItem.getValue() == null ? "" : mapItem.getValue().toString());
            }

        } else if (itemClass == Date.class) {
            mapDateValue = new LinkedHashMap<>();
            for (Entry<String, Object> mapItem : mapCopy.entrySet()) {
                mapDateValue.put(mapItem.getKey(), mapItem.getValue() == null ? null : (Date) mapItem.getValue());
            }

        } else if (itemClass == Integer.class || itemClass == Long.class) {
            mapLongValue = new LinkedHashMap<>();
            for (Entry<String, Object> mapItem : mapCopy.entrySet()) {
                if (mapItem.getValue() instanceof Integer) {
                    mapLongValue.put(mapItem.getKey(), mapItem.getValue() == null ? 0 : ((Integer) mapItem.getValue()).longValue());
                } else if (mapItem.getValue() instanceof Long) {
                    mapLongValue.put(mapItem.getKey(), mapItem.getValue() == null ? 0L : (Long) mapItem.getValue());
                }
            }

        } else if (itemClass == Double.class || itemClass == BigDecimal.class) {
            mapDoubleValue = new LinkedHashMap<>();
            for (Entry<String, Object> mapItem : mapCopy.entrySet()) {
                if (mapItem.getValue() instanceof BigDecimal) {
                    mapDoubleValue.put(mapItem.getKey(), mapItem.getValue() == null ? 0 : ((BigDecimal) mapItem.getValue()).doubleValue());
                } else if (mapItem.getValue() instanceof Double) {
                    mapDoubleValue.put(mapItem.getKey(), (Double) mapItem.getValue());
                }
            }
        } else if (itemClass == Boolean.class) {
            mapBooleanValue = new LinkedHashMap<>();
            for (Entry<String, Object> mapItem : mapCopy.entrySet()) {
                if (mapItem.getValue() instanceof Boolean) {
                	mapBooleanValue.put(mapItem.getKey(), mapItem.getValue() == null ? null : ((Boolean) mapItem.getValue()).booleanValue());
                }
            }

        } else if (itemClass == EntityReferenceWrapper.class) {
            mapEntityValue = new LinkedHashMap<>();
            for (Entry<String, Object> mapItem : mapCopy.entrySet()) {
                mapEntityValue.put(mapItem.getKey(), mapItem.getValue() == null ? null :(EntityReferenceWrapper) mapItem.getValue());
            }
        }
    }

    /**
     * @return Source entity/accumulation path that value came from
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source Source entity/accumulation path that value came from
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Append source entity/accumulation path that value came from
     * 
     * @param sourceToAdd Source entity/accumulation path that value came from
     */
    public void addSource(String sourceToAdd) {
        if (sourceToAdd == null) {
            return;
        }
        if (this.source == null) {
            this.source = sourceToAdd;
        } else if (!this.source.contains(sourceToAdd)) { // TODO this could be potentially a problem when path are similar e.g. customer.ca and customer.ca.ba and search is by
                                                         // "customer.ca", but right now - unlikely.
            this.source = this.source + "," + sourceToAdd;
        }
    }

    /**
     * @param businessEntity Business entity value when set from GUI
     */
    public void setEntityReferenceValueForGUI(IReferenceEntity businessEntity) {
        this.entityReferenceValueForGUI = businessEntity;
    }

    /**
     * @return Business entity value when used in GUI
     */
    public IReferenceEntity getEntityReferenceValueForGUI() {
        return entityReferenceValueForGUI;
    }

    /**
     * @return Child entity values when used in GUI
     */
    public List<CustomFieldValueHolder> getChildEntityValuesForGUI() {
        return childEntityValuesForGUI;
    }

    /**
     * @param childEntityValuesForGUI Child entity values when set in GUI
     */
    public void setChildEntityValuesForGUI(List<CustomFieldValueHolder> childEntityValuesForGUI) {
        this.childEntityValuesForGUI = childEntityValuesForGUI;
    }

    /**
     * @return Period to which value applies to
     */
    public DatePeriod getPeriod() {
        return period;
    }

    /**
     * @param period Period to which value applies to
     */
    public void setPeriod(DatePeriod period) {
        this.period = period;
    }

    /**
     * @return Value priority if periods overlap. The higher the number, the higher the priority is.
     */
    public int getPriority() {
        if (priority == null) {
            return 0;
        } else {
            return priority;
        }
    }

    /**
     * @param priority Value priority if periods overlap. The higher the number, the higher the priority is.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return A lazy dataset of type LazyDataModel for list, map and matrix type value display for data entry in GUI to be used in p:dataTable component
     */
    public Object getDatasetForGUI() {
        return datasetForGUI;
    }

    /**
     * @param datasetForGUI A lazy dataset of type LazyDataModel for list, map and matrix type value display for data entry in GUI to be used in p:dataTable component
     */
    public void setDatasetForGUI(Object datasetForGUI) {
        this.datasetForGUI = datasetForGUI;
    }

    /**
     * Gets custom table code
     *
     * @return the custom table code
     */
    public String getCustomTableCode() {
        return customTableCode;
    }

    /**
     * Sets custom table code
     *
     * @param customTableCode the custom table code
     */
    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    /**
     * Gets filters for custom table wrapper
     *
     * @return customTableWrapper's filters
     */
    public String getDataFilter() {
        return dataFilter;
    }

    /**
     * Gets filters for custom table wrapper
     *
     * @param dataFilter
     */
    public void setDataFilter(String dataFilter) {
        this.dataFilter = dataFilter;
    }

    /**
     * Gets CustomTableWrapper's fields
     *
     * @return CustomTableWrapper's fields
     */
    public String getFields() {
        return fields;
    }

    /**
     * Sets CustomTableWrapper's fields
     *
     * @param fields CustomTableWrapper's fields
     */
    public void setFields(String fields) {
        this.fields = fields;
    }

    /**
     * Set value of a given type
     *
     * @param value value object
     * @param fieldType field type.
     */
    public void setSingleValue(Object value, CustomFieldTypeEnum fieldType) {

        switch (fieldType) {
        case DATE:
            dateValue = (Date) value;
            break;

        case DOUBLE:
            if (value instanceof BigDecimal) {
                doubleValue = ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP).doubleValue();
            } else if (value instanceof Long) {
                doubleValue = ((Long) value).doubleValue();
            } else if (value instanceof Integer) {
                doubleValue = ((Integer) value).doubleValue();
            } else {
                doubleValue = (Double) value;
            }
            break;

        case LONG:
            if (value instanceof BigDecimal) {
                longValue = ((BigDecimal) value).longValue();
            } else if (value instanceof Double) {
                longValue = ((Double) value).longValue();
            } else if (value instanceof Integer) {
                longValue = ((Integer) value).longValue();
            } else {
                longValue = (Long) value;
            }
            break;

        case BOOLEAN:
            if (value instanceof Boolean) {
                booleanValue = ((Boolean) value);
            }
            break;

        case STRING:
        case LIST:
        case TEXT_AREA:
            stringValue = (String) value;

            break;

        case ENTITY:
            setEntityReferenceValue(new EntityReferenceWrapper((BusinessEntity) value));
            break;

        case CHILD_ENTITY:
            throw new RuntimeException("Child entity type of field supports only list of entities");

        case MULTI_VALUE:
            throw new RuntimeException("Multi-value type of field supports only matrix");
        }
    }

    /**
     * Convert value to a string. List and map values are converted to string with usual JAVA list.toSting() and map.toString() means.
     * 
     * @param sdf Date format if applicable
     * @return Value as string
     */
    public String toXmlText(SimpleDateFormat sdf) {

        if (stringValue != null) {
            return stringValue;
        } else if (dateValue != null) {
            return sdf.format(dateValue);
        } else if (longValue != null) {
            return longValue.toString();
        } else if (doubleValue != null) {
            return doubleValue.toString();
        } else if (booleanValue != null) {
            return booleanValue.toString();
        } else {
            List listValue = getListValue();
            if (listValue != null) {
                return listValue.toString();
            }
            Map mapValue = getMapValue();
            if (mapValue != null) {
                return mapValue.toString();
            }
        }
        return "";
    }

    /**
     * Convert simple value (string, date, long, double) to a string
     * 
     * @param sdf Date format if applicable
     * @return Value as string
     */
    public String getValueAsString(SimpleDateFormat sdf) {

        if (stringValue != null) {
            return stringValue;
        } else if (dateValue != null) {
            return sdf.format(dateValue);
        } else if (longValue != null) {
            return longValue.toString();
        } else if (doubleValue != null) {
            return doubleValue.toString();
        } else if (booleanValue != null) {
            return booleanValue.toString();
        }

        return "";
    }

    /**
     * Get a short representation of a value to be used as display in GUI in inherited fields.
     * 
     * @param value object value.
     * @param dateFormat Date format
     * @return Return formated value when storage type is Single and concatenated values when storage type is multiple
     */
    public static String getShortRepresentationOfValueObj(Object value, String dateFormat) {
        if (value == null) {
            return null;

        } else if (value instanceof Map) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (Map.Entry<String, Object> valueInfo : ((Map<String, Object>) value).entrySet()) {
                builder.append(builder.length() == 0 ? "" : ", ");
                Object val = valueInfo.getValue();
                if (val instanceof Date) {
                    val = sdf.format(val);
                } else if (val instanceof EntityReferenceWrapper) {
                    val = ((EntityReferenceWrapper) val).getCode();
                }

                builder.append(String.format("%s: [%s]", valueInfo.getKey(), val));
                i++;
                if (i >= 10) {
                    break;
                }
            }

            return builder.toString();

        } else if (value instanceof List) {
            StringBuilder builder = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            int i = 0;
            for (Object val : ((List<Object>) value)) {
                builder.append(builder.length() == 0 ? "" : ", ");
                if (val instanceof Date) {
                    val = sdf.format(val);
                } else if (val instanceof EntityReferenceWrapper) {
                    val = ((EntityReferenceWrapper) val).getCode();
                }

                builder.append(val);
                i++;
                if (i >= 10) {
                    break;
                }
            }

            return builder.toString();

        } else if (value instanceof EntityReferenceWrapper) {
            return ((EntityReferenceWrapper) value).getCode();

        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format((Date) value);

        } else {
            return value.toString();
        }

    }

    /**
     * Get a short representation of a value to be used as display in GUI in versioned field summary (periods table)
     * 
     * @param cft Custom field template
     * @param dateFormat Date format
     * @return Return formated value when storage type is Single and concatenated values when storage type is multiple
     */
    public String getShortRepresentationOfValue(CustomFieldTemplate cft, String dateFormat) {

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST || cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            StringBuilder builder = new StringBuilder();

            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            int i = 0;
            for (Map<String, Object> valueInfo : mapValuesForGUI) {
                builder.append(builder.length() == 0 ? "" : ", ");

                Object value = valueInfo.get(MAP_VALUE);
                if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {

                    value = sdf.format(value);
                } else if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY && value != null) {
                    value = ((BusinessEntity) value).getCode();

                } else if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && value != null) {
                    value = ((CustomFieldValueHolder) value).getShortRepresentationOfValues();
                }

                if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                    builder.append(value);
                } else {
                    builder.append(String.format("%s: [%s]", valueInfo.get(MAP_KEY), value));
                }
                i++;
                if (i >= 10) {
                    break;
                }
            }

            if (mapValuesForGUI.size() > 10) {
                builder.append(", ...");
            }

            return builder.toString();

        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            StringBuilder builder = new StringBuilder();

            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            int i = 0;
            for (Map<String, Object> mapInfo : matrixValuesForGUI) {

                StringBuilder keyBuilder = new StringBuilder();

                for (CustomFieldMatrixColumn column : cft.getMatrixKeyColumns()) {
                    String columnValue = (String) mapInfo.get(column.getCode());
                    if (columnValue != null) {
                        keyBuilder.append(keyBuilder.length() == 0 ? "" : "|");
                        keyBuilder.append(column.getCode()).append("/").append(columnValue);
                    }
                }

                Object value = null;

                if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {

                    StringBuilder valueBuilder = new StringBuilder();

                    for (CustomFieldMatrixColumn column : cft.getMatrixValueColumns()) {
                        Object columnValue = mapInfo.get(column.getCode());
                        if (columnValue != null) {
                            valueBuilder.append(valueBuilder.length() == 0 ? "" : "|");
                            // No need to check for column data type as they are all work fine with toString(). If in future date is added, would need to format it
                            valueBuilder.append(column.getCode()).append("/").append(columnValue);
                        }
                    }

                    value = valueBuilder.toString();

                } else {

                    value = mapInfo.get(MAP_VALUE);
                    if (value == null) {
                        continue;
                    }

                    if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                        value = sdf.format(value);

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY && value != null) {
                        value = ((BusinessEntity) value).getCode();

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && value != null) {
                        value = ((CustomFieldValueHolder) value).getShortRepresentationOfValues();
                    }
                }

                builder.append(builder.length() == 0 ? "" : ", ");
                builder.append(String.format("%s: [%s]", keyBuilder.toString(), value));
                i++;
                if (i >= 10) {
                    break;
                }
            }

            if (matrixValuesForGUI.size() > 10) {
                builder.append(", ...");
            }

            return builder.toString();

        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            switch (cft.getFieldType()) {
            case DATE:
                if (dateValue != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    return sdf.format(dateValue);
                }
                break;
            case DOUBLE:
                if (doubleValue != null) {
                    return doubleValue.toString();
                }
                break;
            case ENTITY:
                if (entityReferenceValue != null) {
                    return entityReferenceValue.getCode();
                }
                break;
            case LONG:
                if (longValue != null) {
                    return longValue.toString();
                }
                break;
            case BOOLEAN:
                if (booleanValue != null) {
                    return booleanValue.toString();
                }
                break;
            case STRING:
            case LIST:
            case TEXT_AREA:
                return stringValue;
            case CHILD_ENTITY:
                throw new RuntimeException("Child entity type of field supports only list of entities");
            case MULTI_VALUE:
                throw new RuntimeException("Multi-value type of field supports only matrix");
            }
        }
        return null;
    }

    /**
     * Check if values is empty when used in data entry/display for GUI (use XXXForGUI fields instead of serializedValue field )
     * 
     * @return True is value is empty
     */
    public boolean isValueEmptyForGui() {
        boolean isEmpty = ((stringValue == null || stringValue.isEmpty()) && dateValue == null && longValue == null && doubleValue == null && booleanValue == null && entityReferenceValueForGUI == null
                && (mapValuesForGUI == null || mapValuesForGUI.isEmpty()) && (matrixValuesForGUI == null || matrixValuesForGUI.isEmpty()) && (childEntityValuesForGUI == null || childEntityValuesForGUI.isEmpty()));

        if (isEmpty) {
            return true;

        } else if (matrixValuesForGUI != null && !matrixValuesForGUI.isEmpty()) {
            for (Map<String, Object> mapValue : matrixValuesForGUI) {
                for (Object value : mapValue.values()) {
                    boolean empty = StringUtils.isBlank(value);
                    if (!empty) {
                        // Logger log = LoggerFactory.getLogger(getClass());
                        // log.error("AKK cfv matrix is NOT empty {}", matrixValuesForGUI);
                        return false;
                    }
                }
            }
            return true;

        } else if (childEntityValuesForGUI != null && !childEntityValuesForGUI.isEmpty()) {
            for (CustomFieldValueHolder childEntity : childEntityValuesForGUI) {
                for (List<CustomFieldValue> cfValueList : childEntity.getValuesByCode().values()) {
                    for (CustomFieldValue cfValue : cfValueList) {
                        if (!cfValue.isValueEmptyForGui()) {

                            // Logger log = LoggerFactory.getLogger(getClass());
                            // log.error("AKK cfv che is NOT empty {}", cfi);
                            return false;
                        }
                    }

                }
            }
            return true;
        }

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK cfv is NOT empty {}", toString());
        return false;
    }

    /**
     * Check if values is empty when used in non-GUI data manipulation (do not use XXXForGUI fields)
     * 
     * @return True is value is empty
     */
    public boolean isValueEmpty() {
        return ((stringValue == null || stringValue.isEmpty()) && dateValue == null && longValue == null && doubleValue == null && booleanValue == null && (listStringValue == null || listStringValue.isEmpty())
                && (listDateValue == null || listDateValue.isEmpty()) && (listLongValue == null || listLongValue.isEmpty()) && (listDoubleValue == null || listDoubleValue.isEmpty())
                && (listBooleanValue == null || listBooleanValue.isEmpty()) && (listEntityValue == null || listEntityValue.isEmpty()) && (mapStringValue == null || mapStringValue.isEmpty())
                && (mapDateValue == null || mapDateValue.isEmpty()) && (mapLongValue == null || mapLongValue.isEmpty()) && (mapDoubleValue == null || mapDoubleValue.isEmpty())
                && (mapBooleanValue == null || mapBooleanValue.isEmpty()) && (mapEntityValue == null || mapEntityValue.isEmpty()) && (entityReferenceValue == null || entityReferenceValue.isEmpty()));
    }

    /**
     * Serialize a reference to an entity, list or map of values to a Json string in the following format:
     * <ul>
     * <li>"entity"|<json representation of EntityReferenceWrapper></li>
     * <li>"list_"<value classname eg. String>|<json representation of List></li>
     * <li>"map_"<value classname eg. String>|<json representation of Map></li>
     * <li>"matrix_"<value classname eg. String>|<key names>|<json representation of Map></li>
     * </ul>
     * 
     * @param cft Custom field template
     * @param valueToSerialize Value to serialize
     */
    private static String serializeValueToString(CustomFieldTemplate cft, Object valueToSerialize) {

        if (valueToSerialize == null) {
            return null;
        }

        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Gson gson = builder.create();

        String sValue = null;
        if (valueToSerialize instanceof EntityReferenceWrapper && !((EntityReferenceWrapper) valueToSerialize).isEmpty()) {
            sValue = "entity" + SERIALIZATION_SEPARATOR + gson.toJson(valueToSerialize);

        } else if (valueToSerialize instanceof List && !((List) valueToSerialize).isEmpty()) {

            Class itemClass = null;
            if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                itemClass = Date.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                itemClass = Double.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY || cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
                itemClass = EntityReferenceWrapper.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST || cft.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST
                    || cft.getFieldType() == CustomFieldTypeEnum.TEXT_AREA) {
                itemClass = String.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                itemClass = Long.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN) {
                itemClass = Boolean.class;
            }

            if (itemClass != null) {
                sValue = "list_" + itemClass.getSimpleName() + SERIALIZATION_SEPARATOR + gson.toJson(((List) valueToSerialize));
            } else {
                sValue = null;
            }

        } else if (valueToSerialize instanceof Map && !((Map) valueToSerialize).isEmpty()) {

            Class itemClass = null;
            if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                itemClass = Date.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                itemClass = Double.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY || cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
                itemClass = EntityReferenceWrapper.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST || cft.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST
                    || cft.getFieldType() == CustomFieldTypeEnum.TEXT_AREA) {
                itemClass = String.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                itemClass = Long.class;
            } else if (cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN) {
                itemClass = Boolean.class;
            }

            // Handle map that stores matrix type values
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

                Map<String, Object> mapCopy = new LinkedHashMap<String, Object>();
                mapCopy.putAll(((Map) valueToSerialize));
                mapCopy.remove(MAP_KEY);

                String columnNamesString = StringUtils.concatenate(MATRIX_COLUMN_NAME_SEPARATOR, cft.getMatrixColumnCodes());

                if (itemClass != null) {
                    sValue = "matrix_" + itemClass.getSimpleName() + SERIALIZATION_SEPARATOR + columnNamesString + SERIALIZATION_SEPARATOR + gson.toJson(mapCopy);
                } else {
                    sValue = null;
                }

                // A regular map
            } else {

                if (itemClass != null) {
                    sValue = "map_" + itemClass.getSimpleName() + SERIALIZATION_SEPARATOR + gson.toJson(((Map) valueToSerialize));
                } else {
                    sValue = null;
                }
            }
        }
        return sValue;
    }

    /**
     * Get the data type of the first item. If the type is Integer check for further item in the list to see if a Double item exists.
     */
    private static Class findItemClass(Iterator iterator) {
        if (!iterator.hasNext()) {
            return null;
        }
        Object item = iterator.next();
        while (item == null && iterator.hasNext()) {
            item = iterator.next();
        }

        Class itemClass = null;
        if (item != null) {
            itemClass = item.getClass();
        } else {
            return null;
        }

        if (itemClass != null && (itemClass.equals(Long.class) || itemClass.equals(Integer.class))) {
            // check for further type
            while (iterator.hasNext()) {
                item = iterator.next();
                if (item != null) {
                    if (Double.class.equals(item.getClass())) {
                        itemClass = Double.class;
                        break;
                    }
                }
            }
        }

        return itemClass;
    }

    /**
     * Deserialize a serialized reference to an entity, list or map of values. See method serialize() for serialized value format
     * 
     * @param serializedValue Serialized value
     * @return EntityReferenceWrapper, ChildEntityValueWrapper, List or Map object
     */
    private static Object deserializeValueFromString(String serializedValue) {
        if (serializedValue == null) {
            return null;
        }

        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Gson gson = builder.create();

        int firstSeparatorIndex = serializedValue.indexOf(SERIALIZATION_SEPARATOR);

        String type = serializedValue.substring(0, firstSeparatorIndex);
        String subType = null;

        Object deserializedValue = null;
        if (type.indexOf('_') > 0) {
            subType = type.substring(serializedValue.indexOf("_") + 1);
            type = type.substring(0, serializedValue.indexOf("_"));
        }

        if ("entity".equals(type)) {
            String sValue = serializedValue.substring(firstSeparatorIndex + 1);
            EntityReferenceWrapper entityReferenceValue = gson.fromJson(sValue, EntityReferenceWrapper.class);
            deserializedValue = entityReferenceValue;

        } else if ("list".equals(type)) {

            // Type defaults to String
            Type itemType = new TypeToken<List<String>>() {
            }.getType();

            // Determine an appropriate type
            if (Date.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Date>>() {
                }.getType();
            } else if (Double.class.getSimpleName().equals(subType) || BigDecimal.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Double>>() {
                }.getType();
            } else if (Boolean.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Boolean>>() {
                }.getType();
            } else if (Long.class.getSimpleName().equals(subType) || Integer.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Long>>() {
                }.getType();
            } else if (EntityReferenceWrapper.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<EntityReferenceWrapper>>() {
                }.getType();
            }

            String sValue = serializedValue.substring(firstSeparatorIndex + 1);
            List<Object> listValue = gson.fromJson(sValue, itemType);
            deserializedValue = listValue;

        } else if ("map".equals(type)) {

            // Type defaults to String
            Type itemType = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();

            // Determine an appropriate type
            if (Date.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Date>>() {
                }.getType();
            } else if (Double.class.getSimpleName().equals(subType) || BigDecimal.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Double>>() {
                }.getType();
            } else if (Boolean.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Boolean>>() {
                }.getType();
            } else if (Long.class.getSimpleName().equals(subType) || Integer.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Long>>() {
                }.getType();
            } else if (EntityReferenceWrapper.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, EntityReferenceWrapper>>() {
                }.getType();
            }

            String sValue = serializedValue.substring(firstSeparatorIndex + 1);
            Map<String, Object> mapValue = gson.fromJson(sValue, itemType);
            deserializedValue = mapValue;

        } else if ("matrix".equals(type)) {

            // Type defaults to String
            Type itemType = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();

            // Determine an appropriate type
            if (Date.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Date>>() {
                }.getType();
            } else if (Double.class.getSimpleName().equals(subType) || BigDecimal.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Double>>() {
                }.getType();
            } else if (Boolean.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Boolean>>() {
                }.getType();
            } else if (Long.class.getSimpleName().equals(subType) || Integer.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, Long>>() {
                }.getType();
            } else if (EntityReferenceWrapper.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<LinkedHashMap<String, EntityReferenceWrapper>>() {
                }.getType();
            }

            int secondSeparatorIndex = serializedValue.indexOf(SERIALIZATION_SEPARATOR, firstSeparatorIndex + 1);
            String keys = serializedValue.substring(firstSeparatorIndex + 1, secondSeparatorIndex);
            String sValue = serializedValue.substring(secondSeparatorIndex + 1);

            Map<String, Object> mapValue = gson.fromJson(sValue, itemType);
            mapValue.put(MAP_KEY, keys);
            deserializedValue = mapValue;
        }

        // Logger log = LoggerFactory.getLogger(CustomFieldValue.class);
        // log.trace("Value {} deserialized to {}", serializedValue, deserializedValue);
        return deserializedValue;
    }

    /**
     * @return Custom field value
     */
    public Object getValue() {
        if (mapStringValue != null && !mapStringValue.isEmpty()) {
            return mapStringValue;
        } else if (mapDateValue != null && !mapDateValue.isEmpty()) {
            return mapDateValue;
        } else if (mapLongValue != null && !mapLongValue.isEmpty()) {
            return mapLongValue;
        } else if (mapDoubleValue != null && !mapDoubleValue.isEmpty()) {
            return mapDoubleValue;
        } else if (mapBooleanValue != null && !mapBooleanValue.isEmpty()) {
            return mapBooleanValue;
        } else if (mapEntityValue != null && !mapEntityValue.isEmpty()) {
            return mapEntityValue;
        } else if (listStringValue != null && !listStringValue.isEmpty()) {
            return listStringValue;
        } else if (listDateValue != null && !listDateValue.isEmpty()) {
            return listDateValue;
        } else if (listLongValue != null && !listLongValue.isEmpty()) {
            return listLongValue;
        } else if (listDoubleValue != null && !listDoubleValue.isEmpty()) {
            return listDoubleValue;
        } else if (listBooleanValue != null && !listBooleanValue.isEmpty()) {
            return listBooleanValue;
        } else if (listEntityValue != null && !listEntityValue.isEmpty()) {
            return listEntityValue;
        } else if (stringValue != null) {
            return stringValue;
        } else if (dateValue != null) {
            return dateValue;
        } else if (doubleValue != null) {
            return doubleValue;
        } else if (booleanValue != null) {
            return booleanValue;
        } else if (longValue != null) {
            return longValue;
        } else if (entityReferenceValue != null) {
            return entityReferenceValue;
        }
        return null;
    }

    public Map<String, Object> getkeyValueMap() {
        Map<String, Object> result = new TreeMap<String, Object>();
        if (mapStringValue != null && !mapStringValue.isEmpty()) {
            result.put("mapString", mapStringValue);
            return result;
        } else if (mapDateValue != null && !mapDateValue.isEmpty()) {
            result.put("mapDate", mapDateValue);
            return result;
        } else if (mapLongValue != null && !mapLongValue.isEmpty()) {
            result.put("mapLong", mapLongValue);
            return result;
        } else if (mapDoubleValue != null && !mapDoubleValue.isEmpty()) {
            result.put("mapDouble", mapDoubleValue);
            return result;
        } else if (mapBooleanValue != null && !mapBooleanValue.isEmpty()) {
            result.put("mapBoolean", mapBooleanValue);
            return result;
        } else if (mapEntityValue != null && !mapEntityValue.isEmpty()) {
            result.put("mapEntity", mapEntityValue);
            return result;
        } else if (listStringValue != null && !listStringValue.isEmpty()) {
            result.put("listString", listStringValue);
            return result;
        } else if (listDateValue != null && !listDateValue.isEmpty()) {
            result.put("listDate", listDateValue);
            return result;
        } else if (listLongValue != null && !listLongValue.isEmpty()) {
            result.put("listLong", listLongValue);
            return result;
        } else if (listDoubleValue != null && !listDoubleValue.isEmpty()) {
            result.put("listDouble", listDoubleValue);
            return result;
        } else if (listBooleanValue != null && !listBooleanValue.isEmpty()) {
            result.put("listBoolean", listBooleanValue);
            return result;
        } else if (listEntityValue != null && !listEntityValue.isEmpty()) {
            result.put("listEntity", listEntityValue);
            return result;
        } else if (stringValue != null) {
            result.put("string", stringValue);
            return result;
        } else if (dateValue != null) {
            result.put("date", dateValue);
            return result;
        } else if (doubleValue != null) {
            result.put("double", doubleValue);
            return result;
        } else if (booleanValue != null) {
            result.put("boolean", booleanValue);
            return result;
        } else if (longValue != null) {
            result.put("long", longValue);
            return result;
        } else if (entityReferenceValue != null) {
            result.put("entity", entityReferenceValue);
            return result;
        }
        return null;
    }

    /**
     * Set value of a given type
     * 
     * @param value Value to set
     */
    public void setValue(Object value) {

        dateValue = null;
        doubleValue = null;
        booleanValue = null;
        longValue = null;
        stringValue = null;
        entityReferenceValue = null;
        mapStringValue = null;
        mapDateValue = null;
        mapLongValue = null;
        mapDoubleValue = null;
        mapBooleanValue = null;
        mapEntityValue = null;
        listStringValue = null;
        listDateValue = null;
        listLongValue = null;
        listDoubleValue = null;
        listBooleanValue = null;
        listEntityValue = null;

        if (value instanceof Date) {
            dateValue = (Date) value;

        } else if (value instanceof BigDecimal) {
            doubleValue = ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP).doubleValue();

        } else if (value instanceof Double) {
            doubleValue = (Double) value;

        } else if (value instanceof Boolean) {
            booleanValue = (Boolean) value;

        } else if (value instanceof Long) {
            longValue = (Long) value;

        } else if (value instanceof Integer) {
            longValue = ((Integer) value).longValue();

        } else if (value instanceof String) {
            stringValue = (String) value;

        } else if (value instanceof BusinessEntity) {
            setEntityReferenceValue(new EntityReferenceWrapper((BusinessEntity) value));

        } else if (value instanceof EntityReferenceWrapper) {
            setEntityReferenceValue((EntityReferenceWrapper) value);

        } else if (value instanceof Map) {
            setMapValue((Map) value);

        } else if (value instanceof List) {
            setListValue((List) value);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String toString() {
        final int maxLen = 10;

        StringBuffer sb = new StringBuffer();
        if (source != null) {
            sb.append(", source=").append(source);
        }
        if (stringValue != null) {
            sb.append(", stringValue=").append(stringValue);
        }
        if (dateValue != null) {
            sb.append(", dateValue=").append(DateUtils.formatAsTime(dateValue));
        }
        if (longValue != null) {
            sb.append(", longValue=").append(longValue);
        }
        if (doubleValue != null) {
            sb.append(", doubleValue=").append(doubleValue);
        }
        if (booleanValue != null) {
            sb.append(", booleanValue=").append(booleanValue);
        }
        if (entityReferenceValue != null) {
            sb.append(", entityReferenceValue=").append(entityReferenceValue);
        }
        List lValue = getListValue();
        if (lValue != null && !lValue.isEmpty()) {
            sb.append(", listValue=").append(toString(lValue, maxLen));
        }
        Map mValue = getMapValue();
        if (mValue != null && !mValue.isEmpty()) {
            sb.append(", mapValue=").append(toString(mValue.entrySet(), maxLen));
        }
        if (mapValuesForGUI != null && !mapValuesForGUI.isEmpty()) {
            sb.append(", mapValuesForGUI=").append(toString(mapValuesForGUI, maxLen));
        }
        if (matrixValuesForGUI != null && !matrixValuesForGUI.isEmpty()) {
            sb.append(", matrixValuesForGUI=").append(toString(matrixValuesForGUI, maxLen));
        }
        if (childEntityValuesForGUI != null && !childEntityValuesForGUI.isEmpty()) {
            sb.append(", childEntityValuesForGUI=").append(toString(childEntityValuesForGUI, maxLen));
        }
        if (entityReferenceValueForGUI != null) {
            sb.append(", entityReferenceValueForGUI=").append(entityReferenceValueForGUI);
        }

        if (period != null) {
            sb.append(", ").append(period.toString(DateUtils.DATE_TIME_PATTERN));
        }
        if (isNewPeriod) {
            sb.append(", isNewPeriod=true");
        }

        if (sb.length() > 0) {
            return "CustomFieldValue [" + sb.substring(2);
        } else {
            return "";
        }
    }

    /**
     * @param collection collection
     * @param maxLen max length
     * @return collection as string.
     */
    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Convert (deserialize) a string value (serialized value in case of list, map, entity, childEntity) into an object according to custom field data type definition.
     * 
     * @param cft custom field template.s
     * @param valueToConvert Value to convert
     * @return A value corresponding to custom field data type definition
     */
    public static Object parseValueFromString(CustomFieldTemplate cft, String valueToConvert) {

        if (valueToConvert == null) {
            return null;
        }

        try {

            if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && !cft.getFieldType().isStoredSerialized()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                    return Double.parseDouble(valueToConvert);
                } else if (cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN) {
                    return Boolean.parseBoolean(valueToConvert);
                } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                    return Long.parseLong(valueToConvert);
                } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST || cft.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST
                        || cft.getFieldType() == CustomFieldTypeEnum.TEXT_AREA) {
                    return valueToConvert;
                } else if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                    return DateUtils.parseDateWithPattern(valueToConvert, DateUtils.DATE_TIME_PATTERN);
                }
            } else {

                List<String> matrixColumnNames = cft.getMatrixColumnCodes();

                String serializedValueWithMetaInfo = null;

                // Add serialization metadata if it is not available in json string - no | character present, or | is after the serialized json start character {
                int jsonStartIndex = valueToConvert.indexOf("{");
                int serialSeparatorIndex = valueToConvert.indexOf(SERIALIZATION_SEPARATOR);
                if (!(serialSeparatorIndex > -1) || (jsonStartIndex > -1 && jsonStartIndex < serialSeparatorIndex)) {
                    if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        serializedValueWithMetaInfo = "entity" + SERIALIZATION_SEPARATOR + valueToConvert;

                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
                        serializedValueWithMetaInfo = "childEntity" + SERIALIZATION_SEPARATOR + valueToConvert;

                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
                        serializedValueWithMetaInfo = "list_" + cft.getFieldType().getDataClass().getSimpleName() + SERIALIZATION_SEPARATOR + valueToConvert;

                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                        serializedValueWithMetaInfo = "map_" + cft.getFieldType().getDataClass().getSimpleName() + SERIALIZATION_SEPARATOR + valueToConvert;

                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
                        serializedValueWithMetaInfo = "matrix_" + cft.getFieldType().getDataClass().getSimpleName() + SERIALIZATION_SEPARATOR + StringUtils.concatenate(MATRIX_COLUMN_NAME_SEPARATOR, matrixColumnNames)
                                + SERIALIZATION_SEPARATOR + valueToConvert;
                    }
                } else {
                    serializedValueWithMetaInfo = valueToConvert;
                }

                Object deserializedValue = CustomFieldValue.deserializeValueFromString(serializedValueWithMetaInfo);
                return deserializedValue;

            }

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValue.class);
            log.error("Failed to parse {} for CFT {}", valueToConvert, cft, e);
            return null;
        }
        return null;
    }

    /**
     * Convert (serialize) object according to custom field data type definition to a string value (serialized value in case of list, map, entity, childEntity).
     * 
     * @param cft customer field template.
     * @param valueToConvert Value to convert
     * @return A value corresponding to custom field data type definition
     */
    public static String convertValueToString(CustomFieldTemplate cft, Object valueToConvert) {

        if (valueToConvert == null) {
            return null;
        }

        try {

            if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && !cft.getFieldType().isStoredSerialized()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                    return DateUtils.formatDateWithPattern((Date) valueToConvert, DateUtils.DATE_TIME_PATTERN);
                } else {
                    return valueToConvert.toString();
                }
            } else {
                return serializeValueToString(cft, valueToConvert);
            }

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValue.class);
            log.error("Failed to convert {} to String for CFT {}", valueToConvert, cft, e);
            return null;
        }
    }

    @Override
    public CustomFieldValue clone() {
        try {
            CustomFieldValue cloned = (CustomFieldValue) super.clone();
            if (mapStringValue != null) {
                cloned.mapStringValue = new LinkedHashMap<>(mapStringValue);
            } else if (mapDateValue != null) {
                cloned.mapDateValue = new LinkedHashMap<>(mapDateValue);
            } else if (mapDoubleValue != null) {
                cloned.mapDoubleValue = new LinkedHashMap<>(mapDoubleValue);
            } else if (mapBooleanValue != null) {
                cloned.mapBooleanValue = new LinkedHashMap<>(mapBooleanValue);
            } else if (mapLongValue != null) {
                cloned.mapLongValue = new LinkedHashMap<>(mapLongValue);
            } else if (mapEntityValue != null) {
                cloned.mapEntityValue = new LinkedHashMap<>(mapEntityValue);
            }

            return cloned;

        } catch (CloneNotSupportedException e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValue.class);
            log.error("Failed to clone", e);
            return this;
        }
    }

    /**
     * Check if List/Map/Matrix type field is excessive in size
     * 
     * @return True if List or Map value exceeds 20K rows
     */
    public boolean isExcessiveInSize() {
        List listValue = getListValue();

        if (listValue != null && listValue.size() > 20000) {
            return true;
        }

        Map mapValue = getMapValue();
        if (mapValue != null && mapValue.size() > 20000) {
            return true;
        }

        return false;
    }

    public List<EntityReferenceWrapper> getAllEntities() {
        List<EntityReferenceWrapper> entities = Optional.ofNullable(listEntityValue).orElse(new ArrayList<>());
        Collection<EntityReferenceWrapper> mapEntities = Optional.ofNullable(mapEntityValue).map(Map::values).orElse(new ArrayList<>());
        entities.addAll(mapEntities);

        if (entityReferenceValue != null) {
            entities.add(entityReferenceValue);
        }

        return entities;
    }
}