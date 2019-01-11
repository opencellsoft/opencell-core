package org.meveo.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * The Class ResteasyClientProxyBuilder is an extension of ResteasyClientBuilder , witch set a default proxy url  , from a JVM variable value 
 */
public class ResteasyClientProxyBuilder extends ResteasyClientBuilder {
    private static final String PROXY_VAR_KEY = "opencell.keycloak.proxy-url";
    public ResteasyClientProxyBuilder() {
        String proxyUrl = System.getProperty(PROXY_VAR_KEY);
        if (StringUtils.isNotBlank(proxyUrl)) {
            this.defaultProxy(proxyUrl);
        }
    }
}
