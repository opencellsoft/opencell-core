package org.meveo.api.security.config;

public class SecuredBusinessEntityConfig {

    private SecuredMethodConfig securedMethodConfig;
    private FilterResultsConfig filterResultsConfig;

    public void setSecuredMethodConfig(SecuredMethodConfig securedMethodConfig) {
        this.securedMethodConfig = securedMethodConfig;
    }

    public void setFilterResultsConfig(FilterResultsConfig filterResultsConfig) {
        this.filterResultsConfig = filterResultsConfig;
    }

    public SecuredMethodConfig getSecuredMethodConfig() {
        return this.securedMethodConfig;
    }

    public FilterResultsConfig getFilterResultsConfig() {
        return this.filterResultsConfig;
    }
}
