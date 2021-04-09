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

package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.context.Flag;
import org.infinispan.util.function.SerializableBiFunction;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ThreadUtils;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobInstanceService;
import org.slf4j.Logger;

/**
 * Provides cache related services (tracking running jobs) for job running related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
// @Singleton
// @Lock(LockType.READ)
@Stateless
public class JobCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = -4730906690144309131L;

    public static final String CACHE_RETRY_DELAY = "runningJobsCache.retry.delay";
    public static final String CACHE_RETRY_TIMES = "runningJobsCache.retry.times";

    @Inject
    protected Logger log;

    @EJB
    private JobInstanceService jobInstanceService;

    /**
     * Contains association between job instance and cluster nodes it runs in. Key format: &lt;JobInstance.id&gt;, value: List of &lt;cluster node name&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-running-jobs")
    private Cache<CacheKeyLong, JobExecutionStatus> runningJobsCache;

    /**
     * Futures executing the jobs. JobInstance id as a key and a list of futures as a value
     */
    @SuppressWarnings("rawtypes")
    private static Map<Long, List<Future>> runningJobFutures = new HashMap<Long, List<Future>>();

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Get a summary of cached information.
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
     * Refresh cache by name. Removes current provider's data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(runningJobsCache.getName())) {
            clear();
            populateJobCache();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(runningJobsCache.getName())) {
            populateJobCache();
        }
    }

    /**
     * Determine if job, identified by a given job instance id, is currently running and if - on this or another clusternode.
     * 
     * @param jobInstanceId Job instance identifier
     * @return Is Job currently running and if on this or another node
     */
    // @Lock(LockType.READ)
    public JobRunningStatusEnum isJobRunning(Long jobInstanceId) {
        if (jobInstanceId == null) {
            return JobRunningStatusEnum.NOT_RUNNING;
        }

        String currentProvider = currentUser.getProviderCode();

        JobExecutionStatus jobExecutionStatus = runningJobsCache.get(new CacheKeyLong(currentProvider, jobInstanceId));

        return getJobRunningStatus(jobExecutionStatus);
    }

    /**
     * Convert job execution status object to a job running status Enum
     * 
     * @param jobExecutionStatus Job execution status object
     * @return Job running status enum
     */
    private JobRunningStatusEnum getJobRunningStatus(JobExecutionStatus jobExecutionStatus) {

        if (jobExecutionStatus == null) {
            return JobRunningStatusEnum.NOT_RUNNING;

        } else if (jobExecutionStatus.isRequestedToStop()) {
            return JobRunningStatusEnum.REQUEST_TO_STOP;

        } else if (jobExecutionStatus.getLockForNode() != null) {
            String nodeToCheck = EjbUtils.getCurrentClusterNode();

            if (jobExecutionStatus.getLockForNode().equals(nodeToCheck)) {
                return JobRunningStatusEnum.LOCKED_THIS;
            } else {
                return JobRunningStatusEnum.LOCKED_OTHER;
            }
            
        } else if (!jobExecutionStatus.isRunning()) {
            return JobRunningStatusEnum.NOT_RUNNING;

        } else if (!EjbUtils.isRunningInClusterMode()) {
            return JobRunningStatusEnum.RUNNING_THIS;

        } else {

            String nodeToCheck = EjbUtils.getCurrentClusterNode();

            if (jobExecutionStatus.isRunning(nodeToCheck)) {
                return JobRunningStatusEnum.RUNNING_THIS;

            } else {
                return JobRunningStatusEnum.RUNNING_OTHER;
            }
        }
    }

    /**
     * Determine if job, identified by a given job instance id, should be running on a current cluster node
     * 
     * @param jobInstanceId Job instance identifier
     * @return Is Job currently running on this cluster node and was not requested to be stopped
     */
    // @Lock(LockType.READ)
    public boolean isShouldJobContinue(Long jobInstanceId) {
        String currentProvider = currentUser.getProviderCode();
        if (jobInstanceId == null) {
            return false;
        }
        JobExecutionStatus jobExecutionStatus = runningJobsCache.get(new CacheKeyLong(currentProvider, jobInstanceId));
        if (jobExecutionStatus == null || jobExecutionStatus.isRequestedToStop()) {
            return false;

        } else {

            String nodeToCheck = EjbUtils.getCurrentClusterNode();

            return jobExecutionStatus.isRunning(nodeToCheck);
        }
    }

    /**
     * Mark job, identified by a given job instance, as LOCKED to be running on current cluster node. Applies to cases when job is limited to run on a single node. For cases when job is not allowed to run in a current
     * node, a previous status will be returned.
     * 
     * @param jobInstance Job instance
     * @param limitToSingleNode true if this job can be run on only one node.
     * @return Previous job execution status - was Job locked or running before and if on this or another node
     */
    // @Lock(LockType.WRITE)
    public JobRunningStatusEnum lockForRunning(JobInstance jobInstance, boolean limitToSingleNode) {

        String currentNode = EjbUtils.getCurrentClusterNode();
        String currentProvider = currentUser.getProviderCode();

        JobRunningStatusEnum previousStatus = isJobRunning(jobInstance.getId());
        if (previousStatus == JobRunningStatusEnum.RUNNING_THIS) {
            log.trace("Job {} of provider {} attempted to be marked as LOCKED in job cache for node {}. Job is already running on {} node.", jobInstance.getId(), currentProvider, currentNode, currentNode);
            return previousStatus;
        }

        if (!jobInstance.isRunnableOnNode(EjbUtils.getCurrentClusterNode())) {
            return previousStatus;
        }

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            JobRunningStatusEnum isRunning = null;

            if (jobExecutionStatusOld == null) {
                isRunning = JobRunningStatusEnum.NOT_RUNNING;

                // No change is status when job was requested to stop
            } else if (jobExecutionStatusOld.isRequestedToStop()) {
                isRunning = JobRunningStatusEnum.REQUEST_TO_STOP;
                return jobExecutionStatusOld;

            } else if (!jobExecutionStatusOld.isRunning()) {

                if (jobExecutionStatusOld.getLockForNode() == null) {
                    isRunning = JobRunningStatusEnum.NOT_RUNNING;
                } else if (jobExecutionStatusOld.getLockForNode().equals(currentNode)) {
                    isRunning = JobRunningStatusEnum.LOCKED_THIS;
                } else {
                    isRunning = JobRunningStatusEnum.LOCKED_OTHER;
                }

                // If already running, don't modify nodes
            } else if (jobExecutionStatusOld.isRunning(currentNode)) {
                isRunning = JobRunningStatusEnum.RUNNING_THIS;

            } else {
                isRunning = JobRunningStatusEnum.RUNNING_OTHER;
            }

            if (limitToSingleNode && isRunning == JobRunningStatusEnum.NOT_RUNNING) {

                JobExecutionStatus jobExecutionStatus = null;
                if (jobExecutionStatusOld == null) {
                    jobExecutionStatus = new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());
                } else {
                    jobExecutionStatus = jobExecutionStatusOld.clone();
                }

                jobExecutionStatus.markAsLockedOn(currentNode);

                return jobExecutionStatus;
            } else {
                return jobExecutionStatusOld;
            }
        };

        CacheKeyLong cacheKey = new CacheKeyLong(currentProvider, jobInstance.getId());

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        JobExecutionStatus jobStatus = this.computeCacheWithRetry(cacheKey, remappingFunction, delay, times);

        JobRunningStatusEnum currentStatus = getJobRunningStatus(jobStatus);

        log.trace("Job {} of provider {} was attempted to be marked as LOCKED in job cache for node {}. Job is current status is {}. Previous job running status is {}. Current cache value is {}", jobInstance.getId(), currentProvider,
            currentNode, currentStatus, previousStatus, jobStatus);

        if (currentStatus == JobRunningStatusEnum.LOCKED_OTHER || currentStatus == JobRunningStatusEnum.RUNNING_OTHER) {
            return currentStatus;

        } else {
            return previousStatus;
        }
    }

    /**
     * Mark job, identified by a given job instance, as currently running on current cluster node.
     * 
     * @param jobInstance Job instance
     * @param limitToSingleNode true if this job can be run on only one node.
     * @param jobExecutionResultId Job execution result/progress identifier
     * @param threads Threads/futures that job is running on (optional)
     * @return Previous job execution status - was Job locked or running before and if on this or another node
     */
    // @Lock(LockType.WRITE)
    @SuppressWarnings("rawtypes")
    public JobRunningStatusEnum markJobAsRunning(JobInstance jobInstance, boolean limitToSingleNode, Long jobExecutionResultId, List<Future> threads) {

        String currentNode = EjbUtils.getCurrentClusterNode();
        String currentProvider = currentUser.getProviderCode();

        final Integer nrOfThreads = threads == null ? null : threads.size();

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            JobRunningStatusEnum isRunning = null;

            if (jobExecutionStatusOld == null) {
                isRunning = JobRunningStatusEnum.NOT_RUNNING;

                // No change is status when job was requested to stop
            } else if (jobExecutionStatusOld.isRequestedToStop()) {
                isRunning = JobRunningStatusEnum.REQUEST_TO_STOP;
                return jobExecutionStatusOld;

            } else if (!jobExecutionStatusOld.isRunning()) {

                if (jobExecutionStatusOld.getLockForNode() == null) {
                    isRunning = JobRunningStatusEnum.NOT_RUNNING;
                } else if (jobExecutionStatusOld.getLockForNode().equals(currentNode)) {
                    isRunning = JobRunningStatusEnum.LOCKED_THIS;
                } else {
                    isRunning = JobRunningStatusEnum.LOCKED_OTHER;
                }

                // If already running, don't modify nodes
            } else if (jobExecutionStatusOld.isRunning(currentNode)) {
                isRunning = JobRunningStatusEnum.RUNNING_THIS;

            } else {
                isRunning = JobRunningStatusEnum.RUNNING_OTHER;
            }

            // If limited to run on a single node but is running or locked on another server, don't change
            if (limitToSingleNode && (isRunning == JobRunningStatusEnum.RUNNING_OTHER || isRunning == JobRunningStatusEnum.LOCKED_OTHER)) {
                return jobExecutionStatusOld;
            }

            JobExecutionStatus jobExecutionStatus = null;
            if (jobExecutionStatusOld == null) {
                jobExecutionStatus = new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());
            } else {
                jobExecutionStatus = jobExecutionStatusOld.clone();
            }

            jobExecutionStatus.markAsRunningOn(currentNode, jobExecutionResultId, nrOfThreads);

            return jobExecutionStatus;
        };

        JobRunningStatusEnum previousStatus = isJobRunning(jobInstance.getId());
        if (previousStatus == JobRunningStatusEnum.REQUEST_TO_STOP) {
            return previousStatus;
        }

        CacheKeyLong cacheKey = new CacheKeyLong(currentProvider, jobInstance.getId());

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        JobExecutionStatus jobStatus = this.computeCacheWithRetry(cacheKey, remappingFunction, delay, times);

        JobRunningStatusEnum currentStatus = getJobRunningStatus(jobStatus);

        if (threads != null) {
            runningJobFutures.put(jobInstance.getId(), threads);
        }

        log.trace("Job {} of provider {} was marked as RUNNING in job cache for node {}. Job is current status is {}. Previous job running status is {}. Current cache value is {}", jobInstance.getId(), currentProvider,
            currentNode, currentStatus, previousStatus, jobStatus);

        if (currentStatus == JobRunningStatusEnum.LOCKED_THIS || currentStatus == JobRunningStatusEnum.LOCKED_OTHER || currentStatus == JobRunningStatusEnum.RUNNING_OTHER) {
            return currentStatus;

        } else {
            return previousStatus;
        }
    }

    /**
     * Update the cache , and in case of CacheException , retry based on times and delay params.
     *
     * @param cacheKey Cache key
     * @param remappingFunction Remapping function
     * @param delay Delay between tries in ms
     * @param times Number of tries to update the cache
     * @return New/Updated job execution status object
     */
    private JobExecutionStatus computeCacheWithRetry(CacheKeyLong cacheKey, SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction, long delay, final long times) {

        try {
            return runningJobsCache.compute(cacheKey, remappingFunction);

        } catch (CacheException e) {
            log.error(" computeCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);

            if (times > 0) {
                log.debug(" computeCacheWithRetry : Retry with delay = {} and times = {} ", delay, times);
                // waiting for the delay :
                ThreadUtils.sleepSafe(TimeUnit.SECONDS, delay);
                // then retry :
                return this.computeCacheWithRetry(cacheKey, remappingFunction, delay, times - 1);
            } else {
                throw e; // If all retries are failing, then throw the CacheException
            }
        }
    }

    /**
     * Put item in the cache , and in case of CacheException, retry based on times and delay params
     *
     * @param cacheKey Cache key
     * @param delay Delay between tries in ms
     * @param times Number of tries to update the cache
     */
    private void putInCacheWithRetry(CacheKeyLong cacheKey, long delay, final long times) {

        try {
            // Use flags to not return previous value
            runningJobsCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, new JobExecutionStatus());
        } catch (CacheException e) {
            log.error(" putInCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);

            if (times > 0) {
                log.debug(" putInCacheWithRetry : Retry with delay = {} and times = {} ", delay, times);
                // waiting for the delay :
                ThreadUtils.sleepSafe(TimeUnit.SECONDS, delay);
                // then retry :
                this.putInCacheWithRetry(cacheKey, delay, times - 1);
            } else {
                throw e; // If all reties are failing, then throw the CacheException
            }
        }
    }

    /**
     * Mark job, identified by a given job instance, as currently NOT running on CURRENT cluster node.
     * 
     * @param jobInstance Job instance
     */
    // @Lock(LockType.READ)
    public void markJobAsFinished(JobInstance jobInstance) {

        String currentNode = EjbUtils.getCurrentClusterNode();
        boolean isClusterMode = EjbUtils.isRunningInClusterMode();
        String currentProvider = currentUser.getProviderCode();

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            if (jobExecutionStatusOld == null || !jobExecutionStatusOld.isRunning()) {
                return new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());

            } else if (!isClusterMode) {
                return new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());

                // In multi cluster environment remove "requested to stop" flag only when ALL clusters are finished running a job
            } else {
                JobExecutionStatus jobExecutionStatus = jobExecutionStatusOld.clone();
                jobExecutionStatus.markAsFinished(currentNode);

                return jobExecutionStatus;
            }
        };

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        JobExecutionStatus jobStatus = this.computeCacheWithRetry(new CacheKeyLong(currentProvider, jobInstance.getId()), remappingFunction, delay, times);
        if (jobStatus.getNumberThreads(EjbUtils.getCurrentClusterNode()) > 0) {
            runningJobFutures.remove(jobInstance.getId());
        }

        log.trace("Job {} of provider {} was marked as FINISHED in job cache for a node {}. Current cache value is {}.", jobInstance.getId(), currentProvider, currentNode, jobStatus);
    }

    /**
     * Reset job running status - mark job, identified by a given job instance id, as currently NOT running on ALL cluster nodes
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void resetJobRunningStatus(JobInstance jobInstance) {

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {
            return new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());
        };

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        String currentProvider = currentUser.getProviderCode();

        JobExecutionStatus jobStatus = this.computeCacheWithRetry(new CacheKeyLong(currentProvider, jobInstance.getId()), remappingFunction, delay, times);

        if (jobStatus.getNumberThreads(EjbUtils.getCurrentClusterNode()) > 0) {
            runningJobFutures.remove(jobInstance.getId());
        }

        log.trace("Job {} of Provider {} was reset as not running in job cache", jobInstance.getId(), currentProvider);
    }

    /**
     * Get job execution status
     * 
     * @param jobInstanceId Job instance identifier
     * @return Job execution status
     */
    public JobExecutionStatus getJobStatus(Long jobInstanceId) {
        String currentProvider = currentUser.getProviderCode();
        return runningJobsCache.get(new CacheKeyLong(currentProvider, jobInstanceId));
    }

    /**
     * Initialize cache record for a given job instance. According to Infinispan documentation in clustered mode one node is treated as primary node to manage a particular key
     * 
     * @param jobInstance Job instance
     */
    public void addUpdateJobInstance(JobInstance jobInstance) {
        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            if (jobExecutionStatusOld != null) {
                return jobExecutionStatusOld;
            } else {
                return new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());
            }
        };

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        this.computeCacheWithRetry(new CacheKeyLong(currentUser.getProviderCode(), jobInstance.getId()), remappingFunction, delay, times);
    }

    /**
     * Remove job instance running status tracking from cace
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void removeJobInstance(Long jobInstanceId) {
        String currentProvider = currentUser.getProviderCode();

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        this.removeFromCacheWithRetry(new CacheKeyLong(currentProvider, jobInstanceId), delay, times);
    }

    /**
     * Remove item from the cache, and in case of CacheException, retry based on times and delay params.
     * 
     * @param cacheKey
     * @param delay
     * @param times
     */
    private void removeFromCacheWithRetry(CacheKeyLong cacheKey, long delay, final long times) {

        try { // adding Flag.IGNORE_RETURN_VALUES to enhence the update perfs since we dont need a return value
            runningJobsCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
        } catch (CacheException e) {
            log.error(" removeFromCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);

            if (times > 0) {
                log.debug(" removeFromCacheWithRetry : Retry with delay = {} and times = {} ", delay, times);
                // waiting for the delay :
                ThreadUtils.sleepSafe(TimeUnit.SECONDS, delay);
                // then retry :
                this.removeFromCacheWithRetry(cacheKey, delay, times - 1);
            } else {
                throw e; // If all retries are failing, then throw the CacheException
            }
        }
    }

    /**
     * Initialize cache for all job instances
     */
    private void populateJobCache() {
        log.debug("Start to pre-populate Job cache of provider {}.", currentUser.getProviderCode());

        List<JobInstance> jobInsances = jobInstanceService.list();
        for (JobInstance jobInstance : jobInsances) {
            addUpdateJobInstance(jobInstance);
        }

        log.debug("End populating Job cache of Provider {} with {} jobs.", currentUser.getProviderCode(), jobInsances.size());
    }

    /**
     * Clear the current provider data from cache
     */
    private void clear() {
        String currentProvider = currentUser.getProviderCode();
        Iterator<Entry<CacheKeyLong, JobExecutionStatus>> iter = runningJobsCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyLong> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyLong, JobExecutionStatus> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        for (CacheKeyLong elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            this.removeFromCacheWithRetry(elem, delay, times);
        }
    }

    /**
     * Get a list of threads/futures that job is being executed by in current cluster node
     * 
     * @param jobInstanceId
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<Future> getJobExecutionThreads(Long jobInstanceId) {
        JobExecutionStatus jobExecutionStatus = runningJobsCache.get(new CacheKeyLong(currentUser.getProviderCode(), jobInstanceId));
        if (jobExecutionStatus != null) {
            if (jobExecutionStatus.getNumberThreads(EjbUtils.getCurrentClusterNode()) > 0 && runningJobFutures.containsKey(jobInstanceId)) {
                return runningJobFutures.get(jobInstanceId);
            } else {
                runningJobFutures.remove(jobInstanceId);
            }
        }
        return new ArrayList<Future>();
    }

    /**
     * Mark job, identified by a job instance as "requested to stop"
     * 
     * @param jobInstance Job instance to stop
     */
    public void markJobToStop(JobInstance jobInstance) {

        String currentProvider = currentUser.getProviderCode();

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {
            JobExecutionStatus jobExecutionStatus = null;
            if (jobExecutionStatusOld != null) {
                jobExecutionStatus = jobExecutionStatusOld.clone();
            } else {
                jobExecutionStatus = new JobExecutionStatus(jobInstance.getId(), jobInstance.getCode());
            }
            jobExecutionStatus.setRequestedToStop(true);

            return jobExecutionStatus;
        };

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_DELAY, 5);
        long times = ParamBean.getInstance().getPropertyAsInteger(CACHE_RETRY_TIMES, 3);

        this.computeCacheWithRetry(new CacheKeyLong(currentProvider, jobInstance.getId()), remappingFunction, delay, times);

        log.trace("Job {} of Provider {} marked as requested to stop in job cache", jobInstance.getId(), currentProvider);
    }
}