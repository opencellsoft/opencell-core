package org.meveo.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meveo.model.IEntity;

/**
 * Tracks a number of entities exported/imported by a class type. Also tracks ids of entities to remove
 * 
 * @author Andrius Karpavicius
 */
public class ExportImportStatistics {

    @SuppressWarnings("rawtypes")
    private Map<Class, Integer> summary = new HashMap<Class, Integer>();

    @SuppressWarnings("rawtypes")
    private Map<Class, Integer> deleteSummary = new HashMap<Class, Integer>();

    @SuppressWarnings("rawtypes")
    private Map<Class, List<Long>> entitiesToRemove = new HashMap<Class, List<Long>>();

    @SuppressWarnings("rawtypes")
    public Map<Class, Integer> getSummary() {
        return summary;
    }

    @SuppressWarnings("rawtypes")
    public Map<Class, Integer> getDeleteSummary() {
        return deleteSummary;
    }

    @SuppressWarnings("rawtypes")
    public Map<Class, List<Long>> getEntitiesToRemove() {
        return entitiesToRemove;
    }

    /**
     * Add to statistics
     * 
     * @param clazz Class
     * @param entityCount Extra count value
     */
    @SuppressWarnings("rawtypes")
    public void updateSummary(Class clazz, int entityCount) {

        if (!summary.containsKey(clazz)) {
            summary.put(clazz, entityCount);
        } else {
            summary.put(clazz, summary.get(clazz).intValue() + entityCount);
        }
    }

    /**
     * Add to statistics
     * 
     * @param stats
     */
    @SuppressWarnings("rawtypes")
    public void updateSummary(ExportImportStatistics stats) {
        if (stats == null) {
            return;
        }

        for (Entry<Class, Integer> statInfo : stats.getSummary().entrySet()) {
            updateSummary(statInfo.getKey(), statInfo.getValue());
        }
    }

    /**
     * Add to delete statistics
     * 
     * @param clazz Class
     * @param entityCount Extra count value
     */
    @SuppressWarnings("rawtypes")
    public void updateDeleteSummary(Class clazz, int entityCount) {

        if (!deleteSummary.containsKey(clazz)) {
            deleteSummary.put(clazz, entityCount);
        } else {
            deleteSummary.put(clazz, deleteSummary.get(clazz).intValue() + entityCount);
        }
    }

    /**
     * Get number of items deleted for a given class
     * 
     * @param clazz Class to analyse
     * @return Number of items deleted
     */
    @SuppressWarnings("rawtypes")
    public int getDeleteCount(Class clazz) {
        Integer count = deleteSummary.get(clazz);
        if (count == null) {
            return 0;
        } else {
            return count;
        }
    }

    /**
     * Store a list of identifiers of entities that should be removed after the export process is over
     * 
     * @param entities A list of entities to remove
     */
    public void trackEntitiesToDelete(List<IEntity> entities) {

        for (IEntity iEntity : entities) {

            if (!entitiesToRemove.containsKey(iEntity.getClass())) {
                entitiesToRemove.put(iEntity.getClass(), new ArrayList<Long>());
            }
            entitiesToRemove.get(iEntity.getClass()).add((Long) iEntity.getId());
        }
    }
}