package org.meveo.admin.job;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;

/**
 * Singleton bean for managing job context data using maps.
 */
@Singleton
public class JobContextHolder {

    // Store job context data in a map
    private static Map<String, Map<?, ?>> jobContextValues = new ConcurrentHashMap<>();

    /**
     * Clears the job context by resetting the internal map.
     */
    public void destroyJobContext() {
        jobContextValues = new TreeMap<>();
    }

    /**
     * Puts a map into the job context with the specified name.
     *
     * @param <K>  The type of keys in the map.
     * @param <V>  The type of values in the map.
     * @param name The name to associate with the map.
     * @param map  The map to store in the job context.
     */
    public <K, V> void putMap(String name, Map<K, V> map) {
        jobContextValues.put(name, map);
    }
    
    /**
     * remove a map from the job context with the specified name.
     *
     * @param <K>  The type of keys in the map.
     * @param <V>  The type of values in the map.
     * @param name The name to associate with the map.
     */
    public <K, V> void clearMap(String name) {
        jobContextValues.remove(name);
    }

    /**
     * Retrieves a map from the job context with the specified name.
     *
     * @param <K>  The type of keys in the map.
     * @param <V>  The type of values in the map.
     * @param name The name associated with the map.
     * @return The map stored in the job context, or null if not found.
     */
    public <K, V> Map<K, V> getMap(String name) {
        return (Map<K, V>) jobContextValues.get(name);
    }
    
    /**
     * Check if a map exists in the job context with the specified name.
     *
     * @param <K>  The type of keys in the map.
     * @param <V>  The type of values in the map.
     * @param name The name associated with the map.
     * @return The map stored in the job context, or null if not found.
     */
    public boolean isNotEmpty(String name) {
        return jobContextValues.get(name)!=null;
    }

    /**
     * Retrieves a value from the map in the job context with the specified map key and key.
     *
     * @param <K>    The type of keys in the map.
     * @param <V>    The type of values in the map.
     * @param mapKey The name associated with the map.
     * @param key    The key to retrieve the value.
     * @return The value associated with the key in the specified map, or null if not found.
     */
    public <K, V> V getValueFromMap(String mapKey, K key) {
        Map<K, V> map = getMap(mapKey);
        if (map != null) {
            return map.get(key);
        }
        return null;
    }
    
}
