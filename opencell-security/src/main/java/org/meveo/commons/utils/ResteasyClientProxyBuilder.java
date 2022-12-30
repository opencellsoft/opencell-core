/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.commons.utils;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResteasyClientProxyBuilder is an extension of ResteasyClientBuilder , witch set a default  proxyHost and proxyPort  , from a JVM variables values 
 */
public class ResteasyClientProxyBuilder extends ResteasyClientBuilderImpl {

//    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PROXY_HOSTNAME_VAR_KEY = "http.proxyHost";
    private static final String PROXY_PORT_VAR_KEY = "http.proxyPort";

    public ResteasyClientProxyBuilder() {
        String proxyHostName = System.getProperty(PROXY_HOSTNAME_VAR_KEY);
//        log.debug(" proxyHostName : {} ", proxyHostName);
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
//        log.debug(" proxyPort : {} ", proxyPort);
        try {
            return Integer.valueOf(proxyPort);
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error(" Error getting proxy port : ",e);
            return null;
        }
    }
}
