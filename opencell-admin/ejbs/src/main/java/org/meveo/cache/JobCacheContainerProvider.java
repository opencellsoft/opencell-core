package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.JobInstanceService;
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
public class JobCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = -4730906690144309131L;

    @Inject
    protected Logger log;

    @EJB
    private JobInstanceService jobInstanceService;

    /**
     * Contains association between job instance and cluster nodes it runs in. Key format: <JobInstance.id>, value: List of <cluster node name>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-running-jobs")
    private Cache<Long, List<String>> runningJobsCache;

    @PostConstruct
    private void init() {
        try {
            log.debug("JobCacheContainerProvider initializing...");

            populateJobCache();

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
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(runningJobsCache.getName(), runningJobsCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(runningJobsCache.getName())) {
            populateJobCache();
        }
    }

    /**
     * Determine if job, identified by a given job instance id, is currently running and if on this or another clusternode
     * 
     * @param jobInstanceId Job instance identifier
     * @return Is Job currently running and if on this or another node
     */
    public JobRunningStatusEnum isJobRunning(Long jobInstanceId) {

        List<String> runningInNodes = runningJobsCache.get(jobInstanceId);
        if (runningInNodes == null || runningInNodes.isEmpty()) {
            return JobRunningStatusEnum.NOT_RUNNING;

        } else if (!EjbUtils.isRunningInClusterMode()) {
            return JobRunningStatusEnum.RUNNING_THIS;

        } else {

            String nodeToCheck = EjbUtils.getCurrentClusterNode();

            if (runningInNodes.contains(nodeToCheck)) {
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
     * @param limitToSingleNode
     * @return Was Job running before and if on this or another node
     */
    @Lock(LockType.WRITE)
    public JobRunningStatusEnum markJobAsRunning(Long jobInstanceId, boolean limitToSingleNode) {

        JobRunningStatusEnum[] isRunning = new JobRunningStatusEnum[1];

        String currentNode = EjbUtils.getCurrentClusterNode();

        BiFunction<? super Long, ? super List<String>, ? extends List<String>> remappingFunction = (jobInstId, nodesOld) -> {

            if (nodesOld == null || nodesOld.isEmpty()) {
                isRunning[0] = JobRunningStatusEnum.NOT_RUNNING;

                // If already running, don't modify nodes
            } else if (nodesOld.contains(currentNode)) {
                isRunning[0] = JobRunningStatusEnum.RUNNING_THIS;
                return nodesOld;

            } else {
                isRunning[0] = JobRunningStatusEnum.RUNNING_OTHER;

                // If limited to run on a single node, don't modify nodes
                if (limitToSingleNode) {
                    return nodesOld;
                }
            }

            List<String> nodes = new ArrayList<>();
            if (nodesOld != null) {
                nodes.addAll(nodesOld);
            }
            nodes.add(currentNode);

            return nodes;
        };

        List<String> nodes = runningJobsCache.compute(jobInstanceId, remappingFunction);

        log.trace("Job {} marked as running in job cache. Job is currently running on {} nodes. Previous job running status is {}", jobInstanceId, nodes, isRunning[0]);

        return isRunning[0];
    }

    /**
     * Mark job, identified by a given job instance id, as currently NOT running on CURRENT cluster node
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void markJobAsNotRunning(Long jobInstanceId) {

        String currentNode = EjbUtils.getCurrentClusterNode();
        boolean isClusterMode = EjbUtils.isRunningInClusterMode();

        BiFunction<? super Long, ? super List<String>, ? extends List<String>> remappingFunction = (jobInstId, nodesOld) -> {

            if (nodesOld == null || nodesOld.isEmpty()) {
                return nodesOld;

            } else if (!isClusterMode) {
                return new ArrayList<>();

            } else {
                List<String> nodes = new ArrayList<>(nodesOld);
                nodes.remove(currentNode);
                return nodes;
            }
        };

        List<String> nodes = runningJobsCache.compute(jobInstanceId, remappingFunction);

        log.trace("Job {} marked as NOT running in job cache. Job is currently running on {} nodes.", jobInstanceId, nodes);
    }

    /**
     * Reset job running status - mark job, identified by a given job instance id, as currently NOT running on ALL cluster nodes
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void resetJobRunningStatus(Long jobInstanceId) {
        // Use flags to not return previous value
        runningJobsCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(jobInstanceId, new ArrayList<>());
        log.trace("Job {} marked as not running in job cache", jobInstanceId);
    }

    /**
     * Get a list of nodes that job is currently running on
     * 
     * @param jobInstanceId Job instance identifier
     * @return A list of cluster node names that job is currently running on
     */
    public List<String> getNodesJobIsRuningOn(Long jobInstanceId) {
        return runningJobsCache.get(jobInstanceId);
    }

    /**
     * Initialize cache record for a given job instance. According to Infinispan documentation in clustered mode one node is treated as primary node to manage a particular key
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void addUpdateJobInstance(Long jobInstanceId) {

        BiFunction<? super Long, ? super List<String>, ? extends List<String>> remappingFunction = (jobInstId, nodesOld) -> {

            if (nodesOld != null) {
                return nodesOld;
            } else {
                return new ArrayList<>();
            }

        };
        runningJobsCache.compute(jobInstanceId, remappingFunction);
    }

    /**
     * Remove job instance running status tracking from cace
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void removeJobInstance(Long jobInstanceId) {
        runningJobsCache.remove(jobInstanceId);
    }

    /**
     * Initialize cache for all job instances
     */
    private void populateJobCache() {

        runningJobsCache.clear();

        List<JobInstance> jobInsances = jobInstanceService.list();
        for (JobInstance jobInstance : jobInsances) {
            addUpdateJobInstance(jobInstance.getId());
        }
    }
}