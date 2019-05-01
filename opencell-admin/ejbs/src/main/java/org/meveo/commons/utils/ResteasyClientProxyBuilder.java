package org.meveo.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResteasyClientProxyBuilder is an extension of ResteasyClientBuilder , witch set a default  proxyHost and proxyPort  , from a JVM variables values 
 */
public class ResteasyClientProxyBuilder extends ResteasyClientBuilder {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PROXY_HOSTNAME_VAR_KEY = "http.proxyHost";
    private static final String PROXY_PORT_VAR_KEY = "http.proxyPort";

    public ResteasyClientProxyBuilder() {
        String proxyHostName = System.getProperty(PROXY_HOSTNAME_VAR_KEY);
        log.debug(" proxyHostName : {} ", proxyHostName);
        if (StringUtils.isNotBlank(proxyHostName)) {
            Integer proxyPort = getProxyPort();
            if (proxyPort != null) {
                this.defaultProxy(proxyHostName, proxyPort);
            } else {
                this.defaultProxy(proxyHostName);
            }
        }
    }

    private Integer getProxyPort() {
        String proxyPort = System.getProperty(PROXY_PORT_VAR_KEY);
        log.debug(" proxyPort : {} ", proxyPort);
        try {
            return Integer.valueOf(proxyPort);
        } catch (Exception e) {
            log.error(" Error getting proxy port : ",e);
            return null;
        }
    }
}
