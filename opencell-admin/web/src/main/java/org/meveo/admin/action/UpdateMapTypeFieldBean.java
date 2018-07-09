package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meveo.model.IEntity;
import org.meveo.model.crm.OrderedValue;
import org.meveo.service.base.local.IPersistenceService;

/**
 *
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 */
public abstract class UpdateMapTypeFieldBean<T extends IEntity> extends BaseBean<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** Helper field to enter values for HashMap&lt;String,String&gt; type fields */
    protected Map<String, List<HashMap<String, String>>> mapTypeFieldValues = new HashMap<String, List<HashMap<String, String>>>();


    public UpdateMapTypeFieldBean(){

    }

    public UpdateMapTypeFieldBean(Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected abstract IPersistenceService<T> getPersistenceService();

    public Map<String, List<HashMap<String, String>>> getMapTypeFieldValues() {
        return mapTypeFieldValues;
    }

    public void setMapTypeFieldValues(Map<String, List<HashMap<String, String>>> mapTypeFieldValues) {
        this.mapTypeFieldValues = mapTypeFieldValues;
    }

    /**
     * Remove a value from a map type field attribute used to gather field values in GUI
     *
     * @param fieldName Field name
     * @param valueInfo Value to remove
     */
    public void removeMapTypeFieldValue(String fieldName, Map<String, String> valueInfo) {
        mapTypeFieldValues.get(fieldName).remove(valueInfo);
    }

    /**
     * Add a value to a map type field attribute used to gather field values in GUI
     *
     * @param fieldName Field name
     */
    public void addMapTypeFieldValue(String fieldName) {
        if (!mapTypeFieldValues.containsKey(fieldName)) {
            mapTypeFieldValues.put(fieldName, new ArrayList<HashMap<String, String>>());
        }
        mapTypeFieldValues.get(fieldName).add(new HashMap<String, String>());
    }

    /**
     * Move down an element of a list type field attribute
     *
     * @param fieldName
     * @param valueInfo
     */
    public void moveDown(String fieldName, Map<String, String> valueInfo) {
        int actualIndex =  mapTypeFieldValues.get(fieldName).indexOf(valueInfo);
        int size =  mapTypeFieldValues.get(fieldName).size();


        if ((size - actualIndex - 1) > 0) {
            mapTypeFieldValues.get(fieldName).remove(valueInfo);
            mapTypeFieldValues.get(fieldName).add(actualIndex + 1, (HashMap<String, String>) valueInfo);
        }
    }

    /**
     * Move up an element of a list type field attribute
     *
     * @param fieldName
     * @param valueInfo
     */
    public void moveUp(String fieldName, Map<String, String> valueInfo) {
        int actualIndex =  mapTypeFieldValues.get(fieldName).indexOf(valueInfo);
        int size =  mapTypeFieldValues.get(fieldName).size();


        if (actualIndex > 0) {
            mapTypeFieldValues.get(fieldName).remove(valueInfo);
            mapTypeFieldValues.get(fieldName).add(actualIndex - 1, (HashMap<String, String>) valueInfo);
        }
    }

    /**
     * test to render or not Move down button
     *
     * @param fieldName
     * @param valueInfo
     * @return
     */
    public boolean renderMoveDown(String fieldName, Map<String, String> valueInfo) {
        int actualIndex =  mapTypeFieldValues.get(fieldName).indexOf(valueInfo);
        int size =  mapTypeFieldValues.get(fieldName).size();


        if ((size - actualIndex - 1) == 0) {
            return false;
        }
        return true;
    }

    /**
     * test to render or not Move up button
     *
     * @param fieldName
     * @param valueInfo
     * @return
     */
    public boolean renderMoveUp(String fieldName, Map<String, String> valueInfo) {
        int actualIndex =  mapTypeFieldValues.get(fieldName).indexOf(valueInfo);
        int size =  mapTypeFieldValues.get(fieldName).size();


        if (actualIndex == 0) {
            return false;
        }
        return true;
    }


    /**
     * Extract values from a Map type field in an entity to mapTypeFieldValues attribute used to gather field values in GUI
     *
     * @param entityField Entity field
     * @param fieldName Field name
     */
    public void extractMapTypeFieldFromEntity(Map<String, String> entityField, String fieldName) {

        mapTypeFieldValues.remove(fieldName);

        if (entityField != null) {
            List<HashMap<String, String>> fieldValues = new ArrayList<HashMap<String, String>>();
            mapTypeFieldValues.put(fieldName, fieldValues);
            for (Entry<String, String> setInfo : entityField.entrySet()) {
                HashMap<String, String> value = new HashMap<String, String>();
                value.put("key", setInfo.getKey());
                value.put("value", setInfo.getValue());
                fieldValues.add(value);
            }
        }
    }

    /**
     * Update Map type field in an entity from mapTypeFieldValues attribute used to gather field values in GUI
     *
     * @param entityField Entity field
     * @param fieldName Field name
     */
    public void updateMapTypeFieldInEntity(Map<String, String> entityField, String fieldName) {
        entityField.clear();

        if (mapTypeFieldValues.get(fieldName) != null) {
            for (HashMap<String, String> valueInfo : mapTypeFieldValues.get(fieldName)) {
                if (valueInfo.get("key") != null && !valueInfo.get("key").isEmpty()) {
                    entityField.put(valueInfo.get("key"), valueInfo.get("value") == null ? "" : valueInfo.get("value"));
                }
            }
        }
    }

    /**
     * Update List type field in an entity from mapTypeFieldValues attribute used to gather field values in GUI
     *
     * @param entityField
     * @param fieldName
     *
     * */
    public void updateListTypeFieldInEntity(List<OrderedValue> entityField, String fieldName) {
        entityField.clear();

        if (mapTypeFieldValues.get(fieldName) != null) {
            int index = 0;
            for (HashMap<String, String> valueInfo : mapTypeFieldValues.get(fieldName)) {
                if (valueInfo.get("key") != null && !valueInfo.get("key").isEmpty()) {
                    OrderedValue orderedValue = new OrderedValue();
                    orderedValue.setKey(valueInfo.get("key"));
                    orderedValue.setLabel(valueInfo.get("value") == null ? "" : valueInfo.get("value"));
                    orderedValue.setGui_position(String.valueOf(index));
                    entityField.add(index, orderedValue);
                    index++;
                }
            }
        }
    }
}
