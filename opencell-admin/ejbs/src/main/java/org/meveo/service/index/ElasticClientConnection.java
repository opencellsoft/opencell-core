package org.meveo.service.index;

import java.net.InetAddress;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;

/**
 * Establish a connection to Elastic Search cluster
 * 
 * @author Andrius Karpavicius
 *
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class ElasticClientConnection {

    @Inject
    private Logger log;

    @Inject
    private ElasticSearchConfiguration esConfiguration;

    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * The actual ES client
     */
    private TransportClient client = null;

    /**
     * Is Elastic Search enabled/connected
     */
    private boolean esEnabled = false;

    /**
     * Initialize Elastic Search client connection
     */
    @PostConstruct
    private void initES() {

        String clusterName = null;
        String[] hosts = null;
        String portStr = null;

        try {
            clusterName = paramBean.getProperty("elasticsearch.cluster.name", "");
            hosts = paramBean.getProperty("elasticsearch.hosts", "localhost").split(";");
            portStr = paramBean.getProperty("elasticsearch.port", "9300");
            String sniffingStr = paramBean.getProperty("elasticsearch.client.transport.sniff", "false").toLowerCase();
            if (!StringUtils.isBlank(portStr) && StringUtils.isNumeric(portStr) && (sniffingStr.equals("true") || sniffingStr.equals("false")) && !StringUtils.isBlank(clusterName)
                    && hosts.length > 0) {
                log.debug("Connecting to elasticSearch cluster {} and hosts {}, port {}", clusterName, StringUtils.join(hosts, ";"), portStr);
                boolean sniffing = Boolean.parseBoolean(sniffingStr);
                Settings settings = Settings.settingsBuilder().put("client.transport.sniff", sniffing).put("cluster.name", clusterName).build();
                client = TransportClient.builder().settings(settings).build();
                int port = Integer.parseInt(portStr);
                for (String host : hosts) {
                    client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
                }
                List<DiscoveryNode> nodes = client.connectedNodes();
                if (nodes.isEmpty()) {
                    log.error("No nodes available. Verify ES is running!. Current settings: clusterName={}, hosts={}, port={}", clusterName, hosts, portStr);
                    throw new RuntimeException("No nodes available. Verify ES is running!");
                } else {
                    log.debug("connected elasticsearch to {} nodes. Current settings: clusterName={}, hosts={}, port={}", nodes.size(), clusterName, hosts, portStr);
                }
            } else {
                log.warn("Elastic search is not enabled. Current settings: clusterName={}, hosts={}, port={}", clusterName, hosts, portStr);
            }

        } catch (Exception e) {
            log.error("Error while initializing elastic search. Current settings: clusterName={}, hosts={}, port={}", clusterName, hosts, portStr, e);
            shutdownES();
            throw new RuntimeException(
                "Failed to connect to or initialize elastic search client. Application will be stopped. You can disable Elastic Search integration by clearing 'elasticsearch.cluster.name' property in meveo-admin.properties file.");
        }

        try {
            if (client != null) {
                esConfiguration.loadConfiguration();
            }
        } catch (Exception e) {
            log.error("Error while loading elastic search mapping configuration", e);
            shutdownES();
            throw new RuntimeException(
                "Error while loading elastic search mapping configuration. Application will be stopped. You can disable Elastic Search integration by clearing 'elasticsearch.cluster.name' property in meveo-admin.properties file.");
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
    public TransportClient getClient() {
        return client;
    }
}