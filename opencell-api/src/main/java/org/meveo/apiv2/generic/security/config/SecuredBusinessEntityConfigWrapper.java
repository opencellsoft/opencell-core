package org.meveo.apiv2.generic.security.config;

import org.meveo.api.security.config.SecuredBusinessEntityConfig;

/**
 * Class wrapper for the class {@link SecuredBusinessEntityConfig} that
 * adds a new property for the requested Generic API method
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
public class SecuredBusinessEntityConfigWrapper {


    private String method;
    private SecuredBusinessEntityConfig securedBusinessEntityConfig;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public SecuredBusinessEntityConfig getSecuredBusinessEntityConfig() {
        return securedBusinessEntityConfig;
    }

    public void setSecuredBusinessEntityConfig(SecuredBusinessEntityConfig securedBusinessEntityConfig) {
        this.securedBusinessEntityConfig = securedBusinessEntityConfig;
    }
}
