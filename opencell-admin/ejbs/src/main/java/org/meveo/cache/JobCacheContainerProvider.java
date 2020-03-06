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
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ThreadUtils;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobInstanceService;
import org.slf4j.Logger;
import org.infinispan.util.function.SerializableBiFunction;

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
    private Cache<CacheKeyLong, List<String>> runningJobsCache;

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
        String currentProvider = currentUser.getProviderCode();
        if (jobInstanceId == null) {
            return JobRunningStatusEnum.NOT_RUNNING;
        }
        List<String> runningInNodes = runningJobsCache.get(new CacheKeyLong(currentProvider, jobInstanceId));
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
     * Mark job, identified by a given job instance id, as currently running on current cluster node.
     * 
     * @param jobInstanceId Job instance identifier
     * @param limitToSingleNode true if this job can be run on only one node.
     * @return Was Job running before and if on this or another node
     */
    // @Lock(LockType.WRITE)
    public JobRunningStatusEnum markJobAsRunning(Long jobInstanceId, boolean limitToSingleNode) {
        JobRunningStatusEnum[] isRunning = new JobRunningStatusEnum[1];
        String currentNode = EjbUtils.getCurrentClusterNode();
        String currentProvider = currentUser.getProviderCode();

        SerializableBiFunction<? super CacheKeyLong, ? super List<String>, ? extends List<String>> remappingFunction = (jobInstIdFullKey, nodesOld) -> {

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

        CacheKeyLong cacheKey = new CacheKeyLong(currentProvider, jobInstanceId);

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_DELAY, "5"), 5);
        long times = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_TIMES, "3"), 3);

        List<String> nodes = this.computeCacheWithRetry(cacheKey ,remappingFunction, delay, times);

        log.trace("Job {} of provider {} marked as running in job cache. Job is currently running on {} nodes. Previous job running status is {}", jobInstanceId, currentProvider,
            nodes, isRunning[0]);
        return isRunning[0];

    }

    /**
     * Update the cache , and in case of CacheException , retry based on times and delay params.
     *
     * @param cacheKey
     * @param remappingFunction
     * @param delay
     * @param times
     * @return list of nodes
     */
    private List<String> computeCacheWithRetry(CacheKeyLong cacheKey, SerializableBiFunction<? super CacheKeyLong, ? super List<String>, ? extends List<String>> remappingFunction, long delay,
            final long times) {

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
                throw e; // If all reties are failing, then throw the CacheException
            }
        }
    }

    /**
     * Put item in the cache , and in case of CacheException , retry based on times and delay params
     *
     * @param cacheKey
     * @param delay
     * @param times
     */
    private void putInCacheWithRetry(CacheKeyLong cacheKey, long delay,final long times) {

        try {
            // Use flags to not return previous value
            runningJobsCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, new ArrayList<>());
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
     * Mark job, identified by a given job instance id, as currently NOT running on CURRENT cluster node.
     * 
     * @param jobInstanceId Job instance identifier
     */
    // @Lock(LockType.READ)
    public void markJobAsNotRunning(Long jobInstanceId) {

        String currentNode = EjbUtils.getCurrentClusterNode();
        boolean isClusterMode = EjbUtils.isRunningInClusterMode();
        String currentProvider = currentUser.getProviderCode();

        SerializableBiFunction<? super CacheKeyLong, ? super List<String>, ? extends List<String>> remappingFunction = (jobInstIdFullKey, nodesOld) -> {

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

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_DELAY, "5"), 5);
        long times = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_TIMES, "3"), 3);

        List<String> nodes = this.computeCacheWithRetry(new CacheKeyLong(currentProvider, jobInstanceId) ,remappingFunction, delay, times);

        log.trace("Job {}  of Provider {} marked as NOT running in job cache. Job is currently running on {} nodes.", jobInstanceId, currentProvider, nodes);
    }

    /**
     * Reset job running status - mark job, identified by a given job instance id, as currently NOT running on ALL cluster nodes
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void resetJobRunningStatus(Long jobInstanceId) {
        String currentProvider = currentUser.getProviderCode();

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_DELAY, "5"),5);
        long times = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_TIMES, "3"),3);

        this.putInCacheWithRetry(new CacheKeyLong(currentProvider, jobInstanceId), delay, times);
        log.trace("Job {} of Provider {} marked as not running in job cache", jobInstanceId, currentProvider);
    }

    /**
     * Get a list of nodes that job is currently running on
     * 
     * @param jobInstanceId Job instance identifier
     * @return A list of cluster node names that job is currently running on
     */
    public List<String> getNodesJobIsRuningOn(Long jobInstanceId) {
        String currentProvider = currentUser.getProviderCode();
        return runningJobsCache.get(new CacheKeyLong(currentProvider, jobInstanceId));
    }

    /**
     * Initialize cache record for a given job instance. According to Infinispan documentation in clustered mode one node is treated as primary node to manage a particular key
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void addUpdateJobInstance(Long jobInstanceId) {
        SerializableBiFunction<? super CacheKeyLong, ? super List<String>, ? extends List<String>> remappingFunction = (jobInstIdFullKey, nodesOld) -> {

            if (nodesOld != null) {
                return nodesOld;
            } else {
                return new ArrayList<>();
            }

        };

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_DELAY, "5"),5);
        long times = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_TIMES, "3"),3);

        this.computeCacheWithRetry(new CacheKeyLong(currentUser.getProviderCode(), jobInstanceId), remappingFunction,delay ,times);
    }

    /**
     * Remove job instance running status tracking from cace
     * 
     * @param jobInstanceId Job instance identifier
     */
    public void removeJobInstance(Long jobInstanceId) {
        String currentProvider = currentUser.getProviderCode();

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_DELAY, "5"),5);
        long times = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_TIMES, "3"),3);

        this.removeFromCacheWithRetry(new CacheKeyLong(currentProvider, jobInstanceId), delay, times);
    }

    /**
     * * Remove item from the cache , and in case of CacheException , retry based on times and delay params.
     * @param cacheKey
     * @param delay
     * @param times
     */
    private void removeFromCacheWithRetry(CacheKeyLong cacheKey, long delay,final long times) {

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
                throw e; // If all reties are failing, then throw the CacheException
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
            addUpdateJobInstance(jobInstance.getId());
        }

        log.debug("End populating Job cache of Provider {} with {} jobs.", currentUser.getProviderCode(), jobInsances.size());
    }

    /**
     * Clear the current provider data from cache
     */
    private void clear() {
        String currentProvider = currentUser.getProviderCode();
        Iterator<Entry<CacheKeyLong, List<String>>> iter = runningJobsCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyLong> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyLong, List<String>> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        // if the param is not found in properties file then a default value will be set , and if it's not a valid number then also default value will be returned
        long delay = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_DELAY, "5"), 5);
        long times = NumberUtils.parseLongDefault(ParamBean.getInstance().getProperty(CACHE_RETRY_TIMES, "3"), 3);

        for (CacheKeyLong elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            this.removeFromCacheWithRetry(elem, delay, times);
        }
    }

}