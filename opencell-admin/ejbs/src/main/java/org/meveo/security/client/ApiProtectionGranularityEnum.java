package org.meveo.security.client;

/**
 * At what granularity generic API should be protected
 * 
 * @author Andrius Karpavicius
 */
public enum ApiProtectionGranularityEnum {

    /**
     * Protect at package/theme level
     */
    PACKAGE,

    /**
     * Protect at entity class level
     */
    ENTITY_CLASS;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}