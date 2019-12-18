package org.global;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.meveo.commons.utils.EjbUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ClusterInformation {
    private String clusterNodeName;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @PostConstruct
    public void init() {
        this.clusterNodeName = EjbUtils.isRunningInClusterMode() ? EjbUtils.getCurrentClusterNode() : "";
        Gauge<Long> gauge = () -> 1L;
        Metadata metadata = new MetadataBuilder()
                .withName("node_uname_info")
                .withType(MetricType.GAUGE)
                .withDescription("Displays the cluster node name")
                .build();
        Tag tgNode = new Tag("nodename", clusterNodeName);
        registry.register(metadata, gauge, tgNode);
    }

    public String getClusterNodeName() {
        return clusterNodeName;
    }
}
