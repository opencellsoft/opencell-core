package org.meveo.service.index;

import java.util.Map;

import org.meveo.commons.utils.ReflectionUtils;

/**
 * Represents changes pending to be submitted to Elastic Search
 * 
 * @author Andrius Karpavicius
 */
public class ElasticSearchChangeset {

    public enum ElasticSearchAction {
        /**
         * Add or replace full document
         */
        ADD_REPLACE,

        /**
         * Partial update of document
         */
        UPDATE,

        /**
         * Partial update or add new document if does not exist
         */
        UPSERT,

        /**
         * Delete document
         */
        DELETE
    }

    private String fullId;

    private ElasticSearchAction action;

    private String index;

    private String type;

    private String id;

    @SuppressWarnings("rawtypes")
    private Class clazz;

    private Map<String, Object> source;

    @SuppressWarnings("rawtypes")
    public ElasticSearchChangeset(ElasticSearchAction action, String index, String type, String id, Class clazz, Map<String, Object> source) {
        this.action = action;
        this.index = index;
        this.type = type;
        this.id = id;
        this.clazz = clazz;
        this.source = source;
        this.fullId = String.format("%s/%s/%s", index, type, id);
    }

    public ElasticSearchAction getAction() {
        return action;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("rawtypes")
    public Class getClazz() {
        return clazz;
    }

    public String getClazzName() {
        return ReflectionUtils.getCleanClassName(clazz.getSimpleName());
    }

    public Map<String, Object> getSource() {
        return source;
    }

    /**
     * Apply updates to original request
     * 
     * @param updates Map of fieldnames and values with values to update
     */
    public void mergeUpdates(Map<String, Object> updates) {
        source.putAll(updates);
    }

    public String getFullId() {
        return fullId;
    }

    @Override
    public String toString() {
        return String.format("ElasticSearchChangeset [clazz=%s, action=%s, fullId=%s, source=%s]", getClazzName(), action, fullId, source);
    }
}