package org.global;

import org.meveo.commons.utils.EjbUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ClusterInformation {
    private String clusterNodeName;
    @PostConstruct
    public void init(){
        this.clusterNodeName = EjbUtils.isRunningInClusterMode() ? EjbUtils.getCurrentClusterNode() : "";
    }
    public String getClusterNodeName(){
        return clusterNodeName;
    }
}
