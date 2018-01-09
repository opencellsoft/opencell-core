package org.meveo.export;

import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * Summary of entities imported/exported per entity class
     */
    @SuppressWarnings("rawtypes")
    private Map<Class, Integer> summary = new HashMap<Class, Integer>();

    /**
     * Summary of entities deleted per entity class
     */
    @SuppressWarnings("rawtypes")
    private Map<Class, Integer> deleteSummary = new HashMap<Class, Integer>();

    /**
     * Intermediate storage of entities (ids) that should be removed after export is completed
     */
    @SuppressWarnings("rawtypes")
    private Map<Class, List<Long>> entitiesToRemove = new HashMap<Class, List<Long>>();

    /**
     * Stores a list of field names that were not imported because of differences between original and current model - field does not exist in current model
     */
    private Map<String, Collection<String>> fieldsNotImported = new HashMap<String, Collection<String>>();

    /**
     * Occurred exception
     */
    private Throwable exception;

    /**
     * Occurred error message
     */
    private String errorMessageKey;

    /**
     * Execution id of remote meveo instance import call
     */
    private String remoteImportExecutionId;

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
     * Merge statistics from two.
     * 
     * @param stats statistics
     */
    @SuppressWarnings("rawtypes")
    public void mergeStatistics(ExportImportStatistics stats) {
        if (stats == null) {
            return;
        }

        for (Entry<Class, Integer> statInfo : stats.getSummary().entrySet()) {
            updateSummary(statInfo.getKey(), statInfo.getValue());
        }

        for (Entry<String, Collection<String>> fieldInfo : stats.getFieldsNotImported().entrySet()) {
            addFieldsNotImported(fieldInfo.getKey(), fieldInfo.getValue());
        }
    }

    /**
     * Add to delete statistics.
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
    public void trackEntitiesToDelete(List<? extends IEntity> entities) {

        for (IEntity iEntity : entities) {

            if (!entitiesToRemove.containsKey(iEntity.getClass())) {
                entitiesToRemove.put(iEntity.getClass(), new ArrayList<Long>());
            }
            entitiesToRemove.get(iEntity.getClass()).add((Long) iEntity.getId());
        }
    }

    /**
     * Store a collection of fields that were not imported because of difference in model - field no longer exists in current model.
     * 
     * @param exportTemplateName Export template used
     * @param fields A collection of field names
     */
    public void addFieldsNotImported(String exportTemplateName, Collection<String> fields) {

        if (fieldsNotImported.containsKey(exportTemplateName)) {
            fieldsNotImported.get(exportTemplateName).addAll(fields);

        } else {
            fieldsNotImported.put(exportTemplateName, fields);
        }
    }

    public Map<String, Collection<String>> getFieldsNotImported() {
        return fieldsNotImported;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getRemoteImportExecutionId() {
        return remoteImportExecutionId;
    }

    public void setRemoteImportExecutionId(String remoteImportExecutionId) {
        this.remoteImportExecutionId = remoteImportExecutionId;
    }

    public void setErrorMessageKey(String errorMessageKey) {
        this.errorMessageKey = errorMessageKey;
    }

    public String getErrorMessageKey() {
        return errorMessageKey;
    }

    public boolean isFailed() {
        return exception != null || errorMessageKey != null;
    }
}