package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.meveo.commons.utils.EjbUtils;
import org.slf4j.Logger;

/**
 * Provides cache related services (tracking running jobs) for job running related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class JobCacheContainerProvider implements CacheContainerProvider, Serializable {

    private static final long serialVersionUID = -4730906690144309131L;

    public enum JobRunningStatusEnum {
        NOT_RUNNING, RUNNING_THIS, RUNNING_OTHER
    };

    @Inject
    protected Logger log;

    /**
     * Contains association between job instance and cluster nodes it runs in. Key format: JobInstance.id, values: List of cluster node names
     */
    @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-running-jobs")
    private Cache<Long, List<String>> runningJobsCache;

    @PostConstruct
    private void init() {
        try {
            log.debug("JobCacheContainerProvider initializing...");

            runningJobsCache.clear();

            log.info("JobCacheContainerProvider initialized");
        } catch (Exception e) {
            log.error("JobCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Get a summary of cached information
     * 
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Map<String, BasicCache> getCaches() {
        Map<String, BasicCache> summaryOfCaches = new HashMap<String, BasicCache>();
        summaryOfCaches.put(runningJobsCache.getName(), runningJobsCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(runningJobsCache.getName())) {
            runningJobsCache.clear();
        }
    }

    /**
     * Determine if job, identified by a given job instance id, is currently running and if on this or another clusternode
     * 
     * @param jobInstanceId Job instance identifier
     * @return Job by a given job instance id is currently running and if on this or another node
     */
    public JobRunningStatusEnum isJobRunning(Long jobInstanceId) {

        if (!runningJobsCache.containsKey(jobInstanceId)) {
            return JobRunningStatusEnum.NOT_RUNNING;

        } else if (!EjbUtils.isRunningInClusterMode()) {
            return JobRunningStatusEnum.RUNNING_THIS;

        } else {

            String nodeToCheck = EjbUtils.getCurrentClusterNode();

            if (runningJobsCache.get(jobInstanceId).contains(nodeToCheck)) {
                return JobRunningStatusEnum.RUNNING_THIS;

            } else {
                return JobRunningStatusEnum.RUNNING_OTHER;
            }
        }
    }

    /**
     * Mark job, identified by a given job instance id, as currently running on current cluster node
     * 
     * @param jobInstanceId Job instance identifier
     */
    @Lock(LockType.WRITE)
    public void markJobAsRunning(Long jobInstanceId) {

        List<String> nodes = runningJobsCache.get(jobInstanceId);
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        if (EjbUtils.isRunningInClusterMode()) {
            nodes.add(EjbUtils.getCurrentClusterNode());
        } else {
            nodes.add("Current");
        }

        runningJobsCache.putAsync(jobInstanceId, nodes);
    }

    /**
     * Mark job, identified by a given job instance id, as currently NOT running on current cluster node
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void markJobAsNotRunning(Long jobInstanceId) {

        if (EjbUtils.isRunningInClusterMode()) {
            List<String> nodes = runningJobsCache.get(jobInstanceId);
            nodes.remove(EjbUtils.getCurrentClusterNode());
            if (nodes.isEmpty()) {
                runningJobsCache.removeAsync(jobInstanceId);
            } else {
                runningJobsCache.putAsync(jobInstanceId, nodes);
            }
        } else {
            runningJobsCache.removeAsync(jobInstanceId);
        }
    }
}