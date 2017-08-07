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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 
 * A reference to an entity, child entity, list and map values should not be modified behind the scenes - an appropriate SET method has to be called to serialise the value. - This
 * limitations comes from MERGE loosing transient values and thus JPA callback @postUpdate can not be used (see CustomFieldInstance class).
 * 
 * Serialised value format is described in serializeValue() method for each data type.
 * 
 * entityReferenceValueForGUI, mapValuesForGUI, matrixValuesForGUI fields are used in data entry from GUI ONLY.
 * 
 * @author Andrius Karpavicius
 * 
 */
@Embeddable
public class CustomFieldValue implements Serializable {

    private static final long serialVersionUID = -9038541899269528670L;

    public static String MAP_KEY = "key";
    public static String MAP_VALUE = "value";

    public static String MATRIX_COLUMN_NAME_SEPARATOR = "/";
    public static String MATRIX_KEY_SEPARATOR = "|";
    public static String RON_VALUE_SEPARATOR = "<";

    private static String SERIALIZATION_SEPARATOR = "|";

    @Transient
    private DatePeriod period;

    @Transient
    private int priority;

    @Column(name = "string_value", columnDefinition = "TEXT")
    private String stringValue;

    @Column(name = "date_value")
    private Date dateValue;

    @Column(name = "long_value")
    private Long longValue;

    @Column(name = "double_value")
    private Double doubleValue;

    @Column(name = "serialized_value", columnDefinition = "LONGTEXT")
    private String serializedValue;

    /**
     * Entity reference type value deserialized from serializedValue field
     */
    @Transient
    private EntityReferenceWrapper entityReferenceValue;

    /**
     * List type value deserialized from serializedValue field
     */
    @Transient
    private List<Object> listValue = null; // new ArrayList<Object>();

    /**
     * Map type value deserialized from serializedValue field
     */
    @Transient
    private Map<String, Object> mapValue = null; // new HashMap<String, Object>();

    /**
     * Contains mapValue adapted for GUI data entry in the following way:
     * 
     * List item corresponds to an entry in a mapValue with the following list's map values: MAP_KEY=mapValue.entry.key and MAP_VALUE=mapValue.entry.value
     */
    @Transient
    private List<Map<String, Object>> mapValuesForGUI = new ArrayList<Map<String, Object>>();

    /**
     * Contains mapValue adapted for GUI data entry in the following way:
     * 
     * List item corresponds to an entry in a mapValue with the following list's map values: MAP_VALUE=mapValue.entry.value, mapValue.entry.key is parsed into separate key/value
     * pairs and inserted into map
     */
    @Transient
    private List<Map<String, Object>> matrixValuesForGUI = new ArrayList<Map<String, Object>>();

    /**
     * Contains entity reference converted into a CustomFieldValueHolder object in the following way:
     * 
     * CustomFieldValueHolder.entity = entity reference CEI object, CustomFieldValueHolder.values = childEntityValue.fieldValues
     */
    @Transient
    private List<CustomFieldValueHolder> childEntityValuesForGUI = new ArrayList<>();

    /**
     * Contains entityReferenceValue converted into a BusinessEntity object in the following way:
     * 
     * A class of entityReferenceValue.className type is instantiated with code field set to entityReferenceValue.code value and in case it is Custom Entity Instance class, a
     * cetCode field is set to entityReferenceValue.classnameCode value
     */
    @Transient
    private BusinessEntity entityReferenceValueForGUI;

    public CustomFieldValue() {
    }

    public CustomFieldValue(Object value) {
        setValue(value);
    }

    public CustomFieldValue(DatePeriod period, int priority, Object value) {
        this.period = period;
        this.priority = priority;
        setValue(value);
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setMapValuesForGUI(List<Map<String, Object>> mapValuesForGUI) {
        this.mapValuesForGUI = mapValuesForGUI;
    }

    public List<Map<String, Object>> getMapValuesForGUI() {
        return mapValuesForGUI;
    }

    public List<Map<String, Object>> getMatrixValuesForGUI() {
        return matrixValuesForGUI;
    }

    public void setMatrixValuesForGUI(List<Map<String, Object>> matrixValuesForGUI) {
        this.matrixValuesForGUI = matrixValuesForGUI;
    }

    public EntityReferenceWrapper getEntityReferenceValue() {
        return entityReferenceValue;
    }

    /**
     * Set a reference to an entity value. Value is serialised immediately.
     * 
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted, or explicitly call serializeValue() afterwards.
     * 
     * @param entityReferenceValue Reference to an entity value
     */
    public void setEntityReferenceValue(EntityReferenceWrapper entityReferenceValue) {
        this.entityReferenceValue = entityReferenceValue;
        serializeValue(entityReferenceValue);
    }

    // public String getSerializedValue() {
    // return serializedValue;
    // }

    // public void setSerializedValue(String serializedValue) {
    // this.serializedValue = serializedValue;
    // }

    public List<Object> getListValue() {
        return listValue;
    }

    /**
     * Set a list of values. Value is serialised immediately.
     * 
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted, or explicitly call serializeValue() afterwards.
     * 
     * @param listValue
     */
    public void setListValue(List<Object> listValue) {
        this.listValue = listValue;
        serializeValue(listValue);
    }

    public Map<String, Object> getMapValue() {
        return mapValue;
    }

    /**
     * Set a map of values. Value is serialised immediately.
     * 
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted, or explicitly call serializeValue() afterwards.
     * 
     * @param mapValue A map of values
     */
    public void setMapValue(Map<String, Object> mapValue) {
        this.mapValue = mapValue;
        serializeValue(mapValue);
    }

    public void setEntityReferenceValueForGUI(BusinessEntity businessEntity) {
        this.entityReferenceValueForGUI = businessEntity;
    }

    public BusinessEntity getEntityReferenceValueForGUI() {
        return entityReferenceValueForGUI;
    }

    public List<CustomFieldValueHolder> getChildEntityValuesForGUI() {
        return childEntityValuesForGUI;
    }

    public void setChildEntityValuesForGUI(List<CustomFieldValueHolder> childEntityValuesForGUI) {
        this.childEntityValuesForGUI = childEntityValuesForGUI;
    }

    public DatePeriod getPeriod() {
        return period;
    }

    public void setPeriod(DatePeriod period) {
        this.period = period;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Set value of a given type
     * 
     * @param value
     * @param fieldType
     */
    public void setSingleValue(Object value, CustomFieldTypeEnum fieldType) {

        switch (fieldType) {
        case DATE:
            dateValue = (Date) value;
            break;

        case DOUBLE:

            if (value instanceof BigDecimal) {
                doubleValue = ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP).doubleValue();
            } else {
                doubleValue = (Double) value;
            }
            break;

        case LONG:
            if (value instanceof BigDecimal) {
                longValue = ((BigDecimal) value).longValue();
            } else {
                longValue = (Long) value;
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
        }
    }

    public String toJson(SimpleDateFormat sdf) {
        String result = "";

        if (stringValue != null) {
            result += "'" + stringValue + "'";
        } else if (dateValue != null) {
            result += "'" + sdf.format(dateValue) + "'";
        } else if (longValue != null) {
            result += longValue;
        } else if (doubleValue != null) {
            result += doubleValue;
        } else if (serializedValue != null) {
            result += serializedValue.replaceAll("\"", "'");
        } else {
            result = "";
        }

        return result;
    }

    public String toXmlText(SimpleDateFormat sdf) {
        String result = "";

        if (stringValue != null) {
            result += stringValue;
        } else if (dateValue != null) {
            result += sdf.format(dateValue);
        } else if (longValue != null) {
            result += longValue;
        } else if (doubleValue != null) {
            result += doubleValue;
        } else if (serializedValue != null) {
            result += serializedValue;
        } else {
            result = "";
        }

        return result;
    }

    public String getValueAsString(SimpleDateFormat sdf) {
        String result = "";

        if (stringValue != null) {
            result += stringValue;
        } else if (dateValue != null) {
            result += sdf.format(dateValue);
        } else if (longValue != null) {
            result += longValue;
        } else if (doubleValue != null) {
            result += doubleValue;
        } else {
            result = "";
        }

        return result;
    }

    /**
     * Get a short representation of a value to be used as display in GUI in inherited fields
     * 
     * @param cft Custom field template
     * @param dateFormat Date format
     * @return Return formated value when storage type is Single and concatenated values when storage type is multiple
     */
    @SuppressWarnings("unchecked")
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

                Object value = mapInfo.get(MAP_VALUE);
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

                StringBuilder valBuilder = new StringBuilder();

                for (Entry<String, Object> valueInfo : mapInfo.entrySet()) {
                    if (valueInfo.getKey().equals(MAP_VALUE)) {
                        continue;
                    }

                    valBuilder.append(valBuilder.length() == 0 ? "" : "|");
                    valBuilder.append(valueInfo.getKey()).append("/").append(valueInfo.getValue());
                }
                builder.append(builder.length() == 0 ? "" : ", ");
                builder.append(String.format("%s: [%s]", valBuilder.toString(), value));
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
            case STRING:
            case LIST:
            case TEXT_AREA:
                return stringValue;
            case CHILD_ENTITY:
                throw new RuntimeException("Child entity type of field supports only list of entities");
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
        boolean isEmpty = ((stringValue == null || stringValue.isEmpty()) && dateValue == null && longValue == null && doubleValue == null && entityReferenceValueForGUI == null
                && (mapValuesForGUI == null || mapValuesForGUI.isEmpty()) && (matrixValuesForGUI == null || matrixValuesForGUI.isEmpty())
                && (childEntityValuesForGUI == null || childEntityValuesForGUI.isEmpty()));

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
     * Check if values is empty when used in non-GUI data manipulation (use serializedValue instead of XXXForGUI fields)
     * 
     * @return True is value is empty
     */
    public boolean isValueEmpty() {
        return ((stringValue == null || stringValue.isEmpty()) && dateValue == null && longValue == null && doubleValue == null
                && (serializedValue == null || serializedValue.isEmpty()));
    }

    /**
     * Serialise a reference to an entity, list or map of values to a Json string, stored in serializedValue field in the following format:
     * <ul>
     * <li>"entity"|<json representation of EntityReferenceWrapper></li>
     * <li>"list_"<value classname eg. String>|<json representation of List></li>
     * <li>"map_"<value classname eg. String>|<json representation of Map></li>
     * <li>"matrix_"<value classname eg. String>|<key names>|<json representation of Map></li>
     * </ul>
     */
    protected void serializeValue(Object valueToSerialize) {

        String sValue = CustomFieldValue.serializeValueToString(valueToSerialize);

        // Logger log = LoggerFactory.getLogger(getClass());
        // if (log.isTraceEnabled()) {
        // log.trace("Serialized to value {}", sValue != null ? sValue.substring(100) : null);
        // }
        serializedValue = sValue;

    }

    /**
     * Serialise a reference to an entity, list or map of values to a Json string, stored in serializedValue field in the following format:
     * <ul>
     * <li>"entity"|<json representation of EntityReferenceWrapper></li>
     * <li>"list_"<value classname eg. String>|<json representation of List></li>
     * <li>"map_"<value classname eg. String>|<json representation of Map></li>
     * <li>"matrix_"<value classname eg. String>|<key names>|<json representation of Map></li>
     * </ul>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String serializeValueToString(Object valueToSerialize) {

        if (valueToSerialize == null) {
            return null;
        }

        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-dd-MM HH:mm:ss zzz");
        Gson gson = builder.create();

        String sValue = null;
        if (valueToSerialize instanceof EntityReferenceWrapper && !((EntityReferenceWrapper) valueToSerialize).isEmpty()) {
            sValue = "entity" + SERIALIZATION_SEPARATOR + gson.toJson(valueToSerialize);

        } else if (valueToSerialize instanceof List && !((List) valueToSerialize).isEmpty()) {

            // Find the first not null value to determine item class.
            Iterator iterator = ((List) valueToSerialize).iterator();
            Class itemClass = findItemClass(iterator);

            if (itemClass != null) {
                sValue = "list_" + itemClass.getSimpleName() + SERIALIZATION_SEPARATOR + gson.toJson(((List) valueToSerialize));
            } else {
                sValue = null;
            }

        } else if (valueToSerialize instanceof Map && !((Map) valueToSerialize).isEmpty()) {

            // Handle map that stores matrix type values
            if (((Map) valueToSerialize).containsKey(MAP_KEY)) {

                Map<String, Object> mapCopy = new LinkedHashMap<String, Object>();
                mapCopy.putAll(((Map) valueToSerialize));
                mapCopy.remove(MAP_KEY);

                Object columnNames = ((Map) valueToSerialize).get(MAP_KEY);
                String columnNamesString = null;
                if (columnNames instanceof String) {
                    columnNamesString = (String) columnNames;

                } else if (columnNames instanceof Collection) {
                    columnNamesString = StringUtils.concatenate(MATRIX_COLUMN_NAME_SEPARATOR, (Collection) columnNames);
                }

                // Find the first not null value to determine item class.
                Iterator iterator = mapCopy.values().iterator();
                Class itemClass = findItemClass(iterator);

                if (itemClass != null) {
                    sValue = "matrix_" + itemClass.getSimpleName() + SERIALIZATION_SEPARATOR + columnNamesString + SERIALIZATION_SEPARATOR + gson.toJson(mapCopy);
                } else {
                    sValue = null;
                }

                // A regular map
            } else {

                // Find the first not null value to determine item class.
                Iterator iterator = ((Map) valueToSerialize).values().iterator();
                Class itemClass = findItemClass(iterator);

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
    @SuppressWarnings("rawtypes")
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
     * Deserialize JSON value serializedValue to a reference to an entity, list or map of values. See method serialize() for serialized value format
     * 
     * @param jsonValue Serialized value - contains only JSON part of serialized value. A prefix information is determined and added before passed to deserialize method
     * @param fieldType Field type
     * @param storageType Storage type
     */
    public static Object deserializeValueFromString(String jsonValue, CustomFieldTypeEnum fieldType, CustomFieldStorageTypeEnum storageType, List<String> matrixColumnNames) {

        if (jsonValue == null) {
            return null;
        }

        String serializedValue = null;

        // Add seralization metadata if it is not available in json string
        if (!jsonValue.contains(SERIALIZATION_SEPARATOR) || jsonValue.indexOf("{") < jsonValue.indexOf(SERIALIZATION_SEPARATOR)) {
            if (storageType == CustomFieldStorageTypeEnum.SINGLE && fieldType == CustomFieldTypeEnum.ENTITY) {
                serializedValue = "entity" + SERIALIZATION_SEPARATOR + jsonValue;

            } else if (storageType == CustomFieldStorageTypeEnum.SINGLE && fieldType == CustomFieldTypeEnum.CHILD_ENTITY) {
                serializedValue = "childEntity" + SERIALIZATION_SEPARATOR + jsonValue;

            } else if (storageType == CustomFieldStorageTypeEnum.LIST) {
                serializedValue = "list_" + fieldType.getDataClass().getSimpleName() + SERIALIZATION_SEPARATOR + jsonValue;

            } else if (storageType == CustomFieldStorageTypeEnum.MAP) {
                serializedValue = "map_" + fieldType.getDataClass().getSimpleName() + SERIALIZATION_SEPARATOR + jsonValue;

            } else if (storageType == CustomFieldStorageTypeEnum.MATRIX) {
                serializedValue = "matrix_" + fieldType.getDataClass().getSimpleName() + SERIALIZATION_SEPARATOR
                        + StringUtils.concatenate(MATRIX_COLUMN_NAME_SEPARATOR, matrixColumnNames) + SERIALIZATION_SEPARATOR + jsonValue;
            }
        } else {
            serializedValue = jsonValue;
        }

        Object deserializedValue = CustomFieldValue.deserializeValue(serializedValue);
        return deserializedValue;
    }

    /**
     * Deserialize a serialized reference to an entity, list or map of values. See method serialize() for serialized value format
     * 
     * @param serializedValue Serialized value
     * @return EntityReferenceWrapper, ChildEntityValueWrapper, List or Map object
     */
    public static Object deserializeValue(String serializedValue) {
        if (serializedValue == null) {
            return null;
        }

        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-dd-MM HH:mm:ss zzz");
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
     * Deserialize serializedValue field to a reference to an entity, list or map of values. See method serialize() for serialized value format
     */
    public void deserializeValue() {
        if (serializedValue == null) {
            return;
        }
        setValue(CustomFieldValue.deserializeValue(serializedValue), false);
    }

    public Object getValue() {
        if (mapValue != null && !mapValue.isEmpty()) {
            return mapValue;
        } else if (listValue != null && !listValue.isEmpty()) {
            return listValue;
        } else if (stringValue != null) {
            return stringValue;
        } else if (dateValue != null) {
            return dateValue;
        } else if (doubleValue != null) {
            return doubleValue;
        } else if (longValue != null) {
            return longValue;
        } else if (entityReferenceValue != null) {
            return entityReferenceValue;
        }
        return null;
    }

    /**
     * Set value of a given type
     * 
     * @param value Value to set
     */
    public void setValue(Object value) {
        setValue(value, true);
    }

    /**
     * Set value of a given type with an option whether complex values should be serialized
     * 
     * @param value Value to set
     * @param toSerialize whether complex values should be serialized
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setValue(Object value, boolean toSerialize) {

        if (value instanceof Date) {
            dateValue = (Date) value;

        } else if (value instanceof BigDecimal) {
            doubleValue = ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP).doubleValue();

        } else if (value instanceof Double) {
            doubleValue = (Double) value;

        } else if (value instanceof Long) {
            longValue = (Long) value;

        } else if (value instanceof String) {
            stringValue = (String) value;

        } else if (value instanceof BusinessEntity) {
            if (toSerialize) {
                setEntityReferenceValue(new EntityReferenceWrapper((BusinessEntity) value));
            } else {
                entityReferenceValue = new EntityReferenceWrapper((BusinessEntity) value);
            }

        } else if (value instanceof EntityReferenceWrapper) {
            if (toSerialize) {
                setEntityReferenceValue((EntityReferenceWrapper) value);
            } else {
                entityReferenceValue = (EntityReferenceWrapper) value;
            }

        } else if (value instanceof Map) {
            if (toSerialize) {
                setMapValue((Map) value);
            } else {
                mapValue = (Map) value;
            }

        } else if (value instanceof List) {
            if (toSerialize) {
                setListValue((List) value);
            } else {
                listValue = (List) value;
            }
        }
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format(
            "CustomFieldValue [stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s, serializedValue=%s, entityReferenceValue=%s, listValue=%s, mapValue=%s, mapValuesForGUI=%s, matrixValuesForGUI=%s, childEntityValuesForGUI=%s, entityReferenceValueForGUI=%s]",
            stringValue, dateValue, longValue, doubleValue, serializedValue, entityReferenceValue, listValue != null ? toString(listValue, maxLen) : null,
            mapValue != null ? toString(mapValue.entrySet(), maxLen) : null, mapValuesForGUI != null ? toString(mapValuesForGUI, maxLen) : null,
            matrixValuesForGUI != null ? toString(matrixValuesForGUI, maxLen) : null, childEntityValuesForGUI != null ? toString(childEntityValuesForGUI, maxLen) : null,
            entityReferenceValueForGUI);
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

    public String getSerializedValue() {
        return serializedValue;
    }

    /**
     * Convert (deserialize) a string value (serialized value in case of list, map, entity, childEntity) into an object according to custom field data type definition
     * 
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
                } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                    return Long.parseLong(valueToConvert);
                } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST
                        || cft.getFieldType() == CustomFieldTypeEnum.TEXT_AREA) {
                    return valueToConvert;
                } else if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                    return DateUtils.parseDateWithPattern(valueToConvert, DateUtils.DATE_TIME_PATTERN);
                }
            } else {

                List<String> matrixColumnNames = cft.getMatrixColumnCodes();
                return CustomFieldValue.deserializeValueFromString(valueToConvert, cft.getFieldType(), cft.getStorageType(), matrixColumnNames);

            }

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValue.class);
            log.error("Failed to parse {} for CFT {}", valueToConvert, cft, e);
            return null;
        }
        return null;
    }

    /**
     * Convert (serialize) object according to custom field data type definition to a string value (serialized value in case of list, map, entity, childEntity)
     * 
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
                return CustomFieldValue.serializeValueToString(valueToConvert);
            }

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(CustomFieldValue.class);
            log.error("Failed to convert {} to String for CFT {}", valueToConvert, cft, e);
            return null;
        }
    }
}