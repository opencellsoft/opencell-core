package org.meveo.model.crm.custom;

public enum CustomFieldIndexTypeEnum {

    /**
     * Store in Elastic Search but do not index it
     */
    STORE_ONLY(true),

    /**
     * Store and index in Elastic Search
     */
    INDEX(false);

    private boolean storeOnly;

    private CustomFieldIndexTypeEnum(boolean storeOnly) {
        this.storeOnly = storeOnly;
    }

    public boolean isStoreOnly() {
        return storeOnly;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}