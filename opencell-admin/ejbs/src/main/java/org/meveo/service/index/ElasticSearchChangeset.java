package org.meveo.service.index;

import java.util.Map;

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

    /**
     * Full identifier including an index, type and record identifier
     */
    private String fullId;

    /**
     * Action to perform
     */
    private ElasticSearchAction action;

    /**
     * Elastic search index
     */
    private String index;

    /**
     * Elastic search type
     */
    private String type;

    /**
     * Record identifier in Opencell
     */
    private Object id;

    /**
     * Record identifier for Elastic Search. Includes type and record identifier.
     */
    private String idForES;

    /**
     * Associated data
     */
    private Map<String, Object> source;

    /**
     * Constructor
     * 
     * @param action Action to perform
     * @param index Elastic search index name
     * @param type Elastic search type. Optional. If provided, will be used as part of record identifier in ES.
     * @param id Record identifier in Opencell
     */
    public ElasticSearchChangeset(ElasticSearchAction action, String index, String type, Object id) {
        this(action, index, type, id, null);
    }

    /**
     * Constructor
     * 
     * @param action Action to perform
     * @param index Elastic search index name
     * @param type Elastic search type. Optional. If provided, will be used as part of record identifier in ES. In case of action Create or Add_replace, value will be supplemented
     *        with 'type' value.
     * @param id Record identifier in Opencell
     * @param source Associated data (null for Delete action)
     */
    public ElasticSearchChangeset(ElasticSearchAction action, String index, String type, Object id, Map<String, Object> source) {
        this.action = action;
        this.index = index;
        this.type = type;
        this.id = id;
        this.idForES = type != null ? type + "_" + id : id.toString();
        this.source = source;
        this.fullId = String.format("%s/%s", index, this.idForES);
        if (type != null && (action == ElasticSearchAction.ADD_REPLACE || action == ElasticSearchAction.UPSERT)) {
            this.source.put(ElasticSearchConfiguration.MAPPING_FIELD_TYPE, type);
        }
    }

    /**
     * @return Action to perform
     */
    public ElasticSearchAction getAction() {
        return action;
    }

    /**
     * @return Elastic search index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @return Elastic search type
     */
    public String getType() {
        return type;
    }

    /**
     * @return Record identifier in Opencell
     */
    public Object getId() {
        return id;
    }

    /**
     * @return Record identifier for Elastic Search. Includes type and record identifier.
     */
    public String getIdForES() {
        return idForES;
    }

    /**
     * @return Associated data
     */
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

    /**
     * Get full identifier including index
     * 
     * @return Index+type+id value
     */
    protected String getFullId() {
        return fullId;
    }

    @Override
    public String toString() {
        return "ElasticSearchChangeset [fullId=" + fullId + ", action=" + action + ", source=" + source + "]";
    }
}