package org.meveo.model.jobs;

/**
 * How job should process data in a cluster environment
 * 
 * @author Andrius Karpavicius
 *
 */
public enum JobClusterBehaviorEnum {

    /**
     * Run job on a single node only
     */
    LIMIT_TO_SINGLE_NODE,

    /**
     * Run job on multiple cluster nodes, each processing it's own data set
     */
    RUN_IN_PARALLEL,

    /**
     * Process a single data set over several cluster nodes
     */
    SPREAD_OVER_CLUSTER_NODES;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}