package org.meveo.model.crm;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.meveo.model.BusinessEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Encapsulates a custom field value. Supports the following data types:
 * <ul>
 * <li>string, date, double and long that are stored as separate fields</li>
 * <li>reference to an entity, that is serialized as Json to serializedValue field</li>
 * <li>a list or a map of above mentioned data types, serialized as Json to to serializedValue field</li>
 * </ul>
 * 
 * A reference to an entity, list and map values should not be modified behind the scenes - an appropriate SET method has to be called to serialise the value. - This limitations
 * comes from MERGE loosing transient values and thus JPA callback @postUpdate can not be used (see CustomFieldInstance class).
 * 
 * Serialised value format is the following: <entity/list/map>_<list/map data type>|<value in JSON format>. E.g.
 * 
 * entityReferenceValueForGUI, mapValuesForGUI fields are used in data entry from GUI ONLY.
 * 
 * @author Andrius Karpavicius
 * 
 */
@Embeddable
public class CustomFieldValue implements Serializable {

    private static final long serialVersionUID = -9038541899269528670L;

    public static String MAP_KEY = "key";
    public static String MAP_VALUE = "value";

    @Column(name = "STRING_VALUE")
    private String stringValue;

    @Column(name = "DATE_VALUE")
    private Date dateValue;

    @Column(name = "LONG_VALUE")
    private Long longValue;

    @Column(name = "DOUBLE_VALUE")
    private Double doubleValue;

    @Column(name = "SERIALIZED_VALUE", nullable = true)
    private String serializedValue;

    @Transient
    private EntityReferenceWrapper entityReferenceValue;

    @Transient
    private List<Object> listValue = new ArrayList<Object>();

    @Transient
    private Map<String, Object> mapValue = new HashMap<String, Object>();

    @Transient
    private List<Map<String, Object>> mapValuesForGUI = new ArrayList<Map<String, Object>>();

    @Transient
    private BusinessEntity entityReferenceValueForGUI;

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

    public EntityReferenceWrapper getEntityReferenceValue() {
        return entityReferenceValue;
    }

    /**
     * Set a reference to an entity value. Value is serialised immediately.
     * 
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted.
     * 
     * @param entityReferenceValue Reference to an entity value
     */
    public void setEntityReferenceValue(EntityReferenceWrapper entityReferenceValue) {
        this.entityReferenceValue = entityReferenceValue;
        serializeValue();
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
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted.
     * 
     * @param listValue
     */
    public void setListValue(List<Object> listValue) {
        this.listValue = listValue;
        serializeValue();
    }

    public Map<String, Object> getMapValue() {
        return mapValue;
    }

    /**
     * Set a map of values. Value is serialised immediately.
     * 
     * NOTE: Always set a new value. DO NOT edit the value, as it will not be persisted.
     * 
     * @param mapValue A map of values
     */
    public void setMapValue(Map<String, Object> mapValue) {
        this.mapValue = mapValue;
        serializeValue();
    }

    public void setEntityReferenceValueForGUI(BusinessEntity businessEntity) {
        this.entityReferenceValueForGUI = businessEntity;
    }

    public BusinessEntity getEntityReferenceValueForGUI() {
        return entityReferenceValueForGUI;
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
            longValue = (Long) value;
            break;

        case STRING:
        case LIST:
        case TEXT_AREA:
            stringValue = (String) value;

            break;
        case ENTITY:
            entityReferenceValue = new EntityReferenceWrapper((BusinessEntity) value);
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
     * Get a short representation of a value to be used as display in GUI.
     * 
     * @param cft Custom field template
     * @param dateFormat Date format
     * @return Return formated value when storage type is Single and concatenated values when storage type is multiple
     */
    public String getShortRepresentationOfValue(CustomFieldTemplate cft, String dateFormat) {

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST || cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            StringBuilder builder = new StringBuilder();

            int i = 0;
            for (Map<String, Object> valueInfo : mapValuesForGUI) {
                builder.append(builder.length() == 0 ? "" : ", ");
                Object value = valueInfo.get(MAP_VALUE);
                if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    value = sdf.format(value);
                } else if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY && value != null) {
                    value = ((BusinessEntity) value).getCode();
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
            }
        }
        return null;
    }

    public boolean isValueEmpty() {
        return (stringValue == null && dateValue == null && longValue == null && doubleValue == null && entityReferenceValueForGUI == null && (mapValuesForGUI == null || mapValuesForGUI
            .isEmpty()));
    }

    /**
     * Serialise a reference to an entity, list or map of values to a Json string, stored in serializedValue field
     */
    @SuppressWarnings("rawtypes")
    protected void serializeValue() {

        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-dd-MM HH:mm:ss zzz");
        Gson gson = builder.create();

        String sValue = null;
        if (entityReferenceValue != null && !entityReferenceValue.isEmpty()) {
            sValue = "entity|" + gson.toJson(entityReferenceValue);

        } else if (listValue != null && !listValue.isEmpty()) {
            Class itemClass = listValue.get(0).getClass();
            sValue = "list_" + itemClass.getSimpleName() + "|" + gson.toJson(listValue);

        } else if (mapValue != null && !mapValue.isEmpty()) {
            Class itemClass = mapValue.values().iterator().next().getClass();
            sValue = "map_" + itemClass.getSimpleName() + "|" + gson.toJson(mapValue);
        }
        serializedValue = sValue;

    }

    /**
     * Deserialize serializedValue field to a reference to an entity, list or map of values
     */
    protected void deserializeValue() {
        if (serializedValue == null) {
            return;
        }

        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-dd-MM HH:mm:ss zzz");
        Gson gson = builder.create();

        String type = serializedValue.substring(0, serializedValue.indexOf("|"));
        String subType = null;
        if (type.indexOf('_') > 0) {
            subType = type.substring(serializedValue.indexOf("_") + 1);
            type = type.substring(0, serializedValue.indexOf("_"));
        }

        String sValue = serializedValue.substring(serializedValue.indexOf("|") + 1);

        if ("entity".equals(type)) {
            entityReferenceValue = gson.fromJson(sValue, EntityReferenceWrapper.class);

        } else if ("list".equals(type)) {
            // Type defaults to String
            Type itemType = new TypeToken<List<String>>() {
            }.getType();

            // Determine an appropriate type
            if (Date.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Date>>() {
                }.getType();
            } else if (Double.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Double>>() {
                }.getType();
            } else if (Long.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<Long>>() {
                }.getType();
            } else if (EntityReferenceWrapper.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<List<EntityReferenceWrapper>>() {
                }.getType();
            }

            listValue = gson.fromJson(sValue, itemType);

        } else if ("map".equals(type)) {

            // Type defaults to String
            Type itemType = new TypeToken<Map<String, String>>() {
            }.getType();

            // Determine an appropriate type
            if (Date.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<Map<String, Date>>() {
                }.getType();
            } else if (Double.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<Map<String, Double>>() {
                }.getType();
            } else if (Long.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<Map<String, Long>>() {
                }.getType();
            } else if (EntityReferenceWrapper.class.getSimpleName().equals(subType)) {
                itemType = new TypeToken<Map<String, EntityReferenceWrapper>>() {
                }.getType();
            }

            mapValue = gson.fromJson(sValue, itemType);
        }
    }

    public Object getSingleValue() {
        if (stringValue != null) {
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

    @Override
    public String toString() {
        final int maxLen = 10;
        return String
            .format(
                "CustomFieldValue [stringValue=%s, dateValue=%s, longValue=%s, doubleValue=%s, serializedValue=%s, entityReferenceValue=%s, listValue=%s, mapValue=%s, mapValuesForGUI=%s, entityReferenceValueForGUI=%s]",
                stringValue, dateValue, longValue, doubleValue, serializedValue, entityReferenceValue, listValue != null ? toString(listValue, maxLen) : null,
                mapValue != null ? toString(mapValue.entrySet(), maxLen) : null, mapValuesForGUI != null ? toString(mapValuesForGUI, maxLen) : null, entityReferenceValueForGUI);
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
}