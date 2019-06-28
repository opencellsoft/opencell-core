package org.meveo.model.crm.custom;

public enum CustomFieldIndexTypeEnum {

    /**
     * Store in Elastic Search but do not index it
     */
    STORE_ONLY,

    /**
     * Store, index and analyze in Elastic Search
     */
    INDEX,

    /**
     * Store and index without analyzing in Elastic Search
     */
    INDEX_NOT_ANALYZE;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}