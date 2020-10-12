package org.meveo.apiv2.generic.security.config;

import org.meveo.api.security.config.SecuredBusinessEntityConfig;

/**
 * POJO to define Json based secured entities configuration instance
 * used into the Generic API
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
public class SecuredBusinessEntityJsonConfig {

    private String entityName;

    private SecuredBusinessEntityConfigWrapper[] configs;

    /**
     * The Generic API requested entity name
     */
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * A wrapper for the class {@link SecuredBusinessEntityConfig} that
     * adds a new property for the requested Generic API method
     */
    public SecuredBusinessEntityConfigWrapper[] getConfigs() {
        return configs;
    }

    public void setConfigs(SecuredBusinessEntityConfigWrapper[] configs) {
        this.configs = configs;
    }
}
