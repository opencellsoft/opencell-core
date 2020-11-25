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

package org.meveo.service.index;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.http.HttpHost;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;

/**
 * Establish a connection to Elastic Search cluster
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Deprecated
@Startup
@Singleton
@Lock(LockType.READ)
public class ElasticClientConnection {

    @Inject
    private Logger log;

    @Inject
    private ElasticSearchConfiguration esConfiguration;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    /**
     * The actual ES client
     */
    private RestHighLevelClient client = null;

    /**
     * Is Elastic Search enabled/connected
     */
    private boolean esEnabled = false;

    /**
     * Initialize Elastic Search client connection
     */
    @PostConstruct
    private void initES() {

        String[] hosts = null;

        try {

            String restUri = paramBean.getProperty("elasticsearch.restUri", "");
            hosts = restUri.split(";");

            if (StringUtils.isBlank(restUri) || hosts.length == 0) {
                log.warn("Elastic search is not enabled. Current settings: hosts={}", Arrays.toString(hosts));

            } else {
                HttpHost[] httpHosts = new HttpHost[hosts.length];

                for (int i = 0; i < hosts.length; i++) {
                    httpHosts[i] = HttpHost.create(hosts[i]);
                }

                client = new RestHighLevelClient(RestClient.builder(httpHosts));

                @SuppressWarnings("unused")
                MainResponse response = client.info(RequestOptions.DEFAULT);
            }

        } catch (Exception e) {
            log.error("Error while initializing elastic search. Current settings:  hosts={}", hosts, e);
            shutdownES();
            throw new RuntimeException(
                "Failed to connect to or initialize elastic search client. Application will be stopped. You can disable Elastic Search integration by clearing 'elasticsearch.restUri' property in opencell-admin.properties file.");
        }

        try {
            if (client != null) {
                esConfiguration.loadConfiguration();
            }
        } catch (Exception e) {
            log.error("Error while loading elastic search mapping configuration", e);
            shutdownES();
            throw new RuntimeException(
                "Error while loading elastic search mapping configuration. Application will be stopped. You can disable Elastic Search integration by clearing 'elasticsearch.cluster.name' property in opencell-admin.properties file.");
        }

        esEnabled = client != null;

    }

    /**
     * Shutdown Elastic Search client
     */
    @PreDestroy
    private void shutdownES() {
        if (client != null) {
            try {
                client.close();
                client = null;
            } catch (Exception e) {
                log.error("Failed to close ES client", e);
            }
        }
    }

    /**
     * Reinitialize ES connection
     */
    public void reinitES() {
        shutdownES();
        initES();
    }

    /**
     * Is Elastic Search integration turned on.
     * 
     * @return true if enabled
     */
    public boolean isEnabled() {
        return esEnabled;
    }

    /**
     * Get Elastic Search client instance
     * 
     * @return Elastic Search client instance
     */
    public RestHighLevelClient getClient() {
        return client;
    }
}