package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meveo.model.IEntity;
import org.meveo.service.base.local.IPersistenceService;

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
}
