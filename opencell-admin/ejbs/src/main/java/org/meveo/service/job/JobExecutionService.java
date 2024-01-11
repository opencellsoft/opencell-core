/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.MapUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.JobExecutionException;
import org.meveo.admin.job.IteratorBasedJobBean;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobExecutionStatus;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobClusterBehaviorEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JobExecutionService.
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class JobExecutionService extends BaseService {

    /**
     * Number of times to repeat a job when it did not finish in a first/subsequent runs
     */
    private static final int MAX_TIMES_TO_RUN_INCOMPLETE_JOB = 50;

    /**
     * Tracks if server entered a shutdown mode
     */
    public static AtomicBoolean serverIsInShutdownMode = new AtomicBoolean(false);

    /**
     * job instance service.
     */
    @Inject
    private JobInstanceService jobInstanceService;

    /** The job cache container provider. */
    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @EJB
    private JobExecutionService jobExecutionService;

    @Inject
    private JobExecutionResultService jobExecutionResultService;

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    /**
     * Execute a job and return job execution result ID to be able to query execution results later. Job execution result is persisted right away, while job is executed asynchronously.
     * 
     * @param jobInstance Job instance to execute.
     * @param params Parameters (currently not used)
     * @param jobLauncher How job was launched
     * @return Job execution result ID
     * @throws BusinessException Any exception
     */
    public Long executeJob(JobInstance jobInstance, Map<String, Object> params, JobLauncherEnum jobLauncher) throws BusinessException {
        return executeJob(jobInstance, params, jobLauncher, true);
    }

    /**
     * Execute a job and return job execution result ID to be able to query execution results later. Job execution result is persisted right away, while job is executed asynchronously.
     * 
     * @param jobInstance Job instance to execute.
     * @param params Parameters (currently not used)
     * @param jobLauncher How job was launched
     * @param triggerExecutionOnOtherNodes When job is initiated from GUI or API, shall job execution be triggered on other nodes as well
     * @return Job execution result ID
     * @throws BusinessException Any exception
     */
    public Long executeJob(JobInstance jobInstance, Map<String, Object> params, JobLauncherEnum jobLauncher, boolean triggerExecutionOnOtherNodes) throws BusinessException {

        jobInstance = jobInstanceService.findById(jobInstance.getId());

        log.info("Execute a job {} of type {} with parameters {} from {}", jobInstance, jobInstance.getJobTemplate(), params, jobLauncher);

        Long jobExecutionResultId = null;

        boolean isRunningAsJobManager = jobLauncher != JobLauncherEnum.WORKER;
        // In Spread data processing over cluster nodes only one node can act as a job manager
        boolean limitRunToASingleNode = jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.LIMIT_TO_SINGLE_NODE
                || (jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.SPREAD_OVER_CLUSTER_NODES && isRunningAsJobManager);

        if (JobExecutionService.isRunnableOnNode(jobInstance.getRunOnNodesResolved())) {

            JobRunningStatusEnum lockStatus = lockForRunning(jobInstance, limitRunToASingleNode);

            if (lockStatus == JobRunningStatusEnum.LOCKED_THIS) {

                JobExecutionResultImpl jobExecutionResult = new JobExecutionResultImpl(jobInstance, jobLauncher, EjbUtils.getCurrentClusterNode());
                // set parent history id
                if (params != null && params.containsKey(Job.JOB_PARAM_HISTORY_PARENT_ID)) {
                    jobExecutionResult.setParentJobExecutionResult((Long) params.get(Job.JOB_PARAM_HISTORY_PARENT_ID));
                }

                jobExecutionResultService.persistResult(jobExecutionResult);

                jobExecutionService.executeJobAsync(jobInstance, params, jobExecutionResult, jobLauncher, currentUser.unProxy());

                jobExecutionResultId = jobExecutionResult.getId();

            } else if (lockStatus == JobRunningStatusEnum.REQUEST_TO_STOP) {
                throw new JobExecutionException("Job is in the process of stopping. Please try again shortly.");

            } else {
                throw new JobExecutionException("Job is currently running on this or another cluster node and is limited to run one node at a time, or one job manager at a time");
            }
        }
        // Execute a job on other nodes if was launched from GUI or API and is not limited to run on current node only or was launched from a node that is not allowed to run on.
        if (triggerExecutionOnOtherNodes && (jobLauncher == JobLauncherEnum.GUI || jobLauncher == JobLauncherEnum.API || jobLauncher == JobLauncherEnum.TRIGGER)
                && (jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.RUN_IN_PARALLEL
                        || ((jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.LIMIT_TO_SINGLE_NODE || jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.SPREAD_OVER_CLUSTER_NODES)
                                && !JobExecutionService.isRunnableOnNode(jobInstance.getRunOnNodesResolved())))) {

            Map<String, Object> jobParameters = new HashMap<String, Object>();
            if (params != null) {
                jobParameters.putAll(params);
            }
            jobParameters.put(Job.JOB_PARAM_LAUNCHER, jobLauncher);
            clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.execute, jobParameters);
        }
        return jobExecutionResultId;
    }

    /**
     * Execute job asynchronously in a new (no transaction) transaction demarcation
     * 
     * @param jobInstance Job instance to execute.
     * @param params Parameters (currently not used)
     * @param jobExecutionResult Job execution history/results. Optional. If not provided. One will be created automatically.
     * @param jobLauncher How job was launched
     * @param lastCurrentUser Currently authenticated user
     * @throws BusinessException Any exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeJobAsync(JobInstance jobInstance, Map<String, Object> params, JobExecutionResultImpl jobExecutionResult, JobLauncherEnum jobLauncher, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        Job job = jobInstanceService.getJobByName(jobInstance.getJobTemplate());

        jobInstance.setRunTimeValues(params);
        JobExecutionResultStatusEnum jobResultStatus = job.execute(jobInstance, jobExecutionResult, jobLauncher);

        if (jobLauncher != JobLauncherEnum.WORKER) {
            int i = 0;

            final long checkEveryMilis = ((Integer) ParamBean.getInstance().getPropertyAsInteger("jobs.completeMore.checkEveryMilis", 5000)).longValue();
            final int checkTimes = ParamBean.getInstance().getPropertyAsInteger("jobs.completeMore.checkTimes", 15);

            while (jobResultStatus == JobExecutionResultStatusEnum.COMPLETED_MORE && i < MAX_TIMES_TO_RUN_INCOMPLETE_JOB) {

                if (!waitForAllNodesToFinishRunning(jobInstance.getId(), checkEveryMilis, checkTimes)) {
                    jobExecutionResult.setStatus(JobExecutionResultStatusEnum.FAILED);
                    jobExecutionResult.addReportToBeginning("Job completed successfully with more data to process, but failed to complete on other nodes. Will stop further processing.");
                    jobExecutionResultService.persistResult(jobExecutionResult);
                    return;
                }

                jobExecutionResult = new JobExecutionResultImpl(jobInstance, jobLauncher, EjbUtils.getCurrentClusterNode());
                jobExecutionResultService.persistResult(jobExecutionResult);

                jobResultStatus = job.execute(jobInstance, jobExecutionResult, JobLauncherEnum.INCOMPLETE);
                i++;
            }

            if (jobResultStatus == JobExecutionResultStatusEnum.COMPLETED && jobInstance.getFollowingJob() != null) {
                JobInstance nextJob = jobInstanceService.refreshOrRetrieve(jobInstance.getFollowingJob());
                nextJob = PersistenceUtils.initializeAndUnproxy(nextJob);
                log.info("Executing next job {} for {}", nextJob.getCode(), jobInstance.getCode());
                executeJob(nextJob, null, JobLauncherEnum.TRIGGER, true);
            }
        }
    }

    /**
     * Gets the job instance service.
     *
     * @return job instance service
     */
    public JobInstanceService getJobInstanceService() {
        return jobInstanceService;
    }

    /**
     * Stop a running job.
     *
     * @param jobInstance Job instance to stop
     */
    public void stopJob(JobInstance jobInstance) {

        stopJob(jobInstance, true);
    }

    /**
     * Stop a running job.
     *
     * @param jobInstance Job instance to stop
     * @param triggerStopOnOtherNodes When job is being stopped from GUI or API, shall job stopping be triggered on other nodes as well
     */
    public void stopJob(JobInstance jobInstance, boolean triggerStopOnOtherNodes) {

        log.info("Requested to stop job {} of type {}  ", jobInstance, jobInstance.getJobTemplate());

        IteratorBasedJobBean.markJobToStop(jobInstance.getId());
        jobCacheContainerProvider.markJobToStop(jobInstance);
        IteratorBasedJobBean.releaseJobDataProcessingThreads(jobInstance.getId());

        // Publish to other cluster nodes to cancel job execution
        if (triggerStopOnOtherNodes) {
            clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.stop);
        }
    }

    /**
     * Stop a running job by force - cancel futures/kill threads
     *
     * @param jobInstance Job instance to stop
     */
    public void stopJobByForce(JobInstance jobInstance) {
        stopJobByForce(jobInstance, true);
    }

    /**
     * Stop a running job by force - cancel futures/kill threads
     *
     * @param jobInstance Job instance to stop
     * @param triggerStopOnOtherNodes When job is being stopped from GUI or API, shall job stopping be triggered on other nodes as well
     */
    @SuppressWarnings("rawtypes")
    public void stopJobByForce(JobInstance jobInstance, boolean triggerStopOnOtherNodes) {

        log.info("Requested to stop BY FORCE job {}  of type {}", jobInstance, jobInstance.getJobTemplate());

        IteratorBasedJobBean.markJobToStop(jobInstance.getId());
        if (triggerStopOnOtherNodes) {
            jobCacheContainerProvider.markJobToStop(jobInstance);
        }
        IteratorBasedJobBean.releaseJobDataProcessingThreads(jobInstance.getId());

        List<Future> futures = jobCacheContainerProvider.getJobExecutionThreads(jobInstance.getId());
        if (futures.isEmpty()) {
            jobCacheContainerProvider.markJobAsFinished(jobInstance);

        } else {
            int i = 1;
            for (Future future : futures) {
                if (!future.isDone()) {
                    boolean canceled = future.cancel(true);
                    if (canceled) {
                        log.info("Job {} thread #{} was canceled by force", jobInstance, i);
                    } else {
                        log.error("Failed to cancel a job {} thread #{}", jobInstance, i);
                    }
                }
                i++;
            }
        }

        // Publish to other cluster nodes to cancel job execution
        if (triggerStopOnOtherNodes) {
            clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.stopByForce);
        }
    }

    /**
     * Check if the job are running on this node.
     * 
     * @param jobInstance job instance to check
     * @return return true if job are running
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isJobRunningOnThis(JobInstance jobInstance) {
        return isJobRunningOnThis(jobInstance.getId());
    }

    /**
     * Check if the job are running on this node.
     * 
     * @param jobInstanceId job instance id to check
     * @return return true if job are running
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isJobRunningOnThis(Long jobInstanceId) {
        return JobRunningStatusEnum.RUNNING_THIS == jobCacheContainerProvider.isJobRunning(jobInstanceId);
    }

    /**
     * Determine if job, identified by a given job instance id, should be running on a current cluster node
     * 
     * @param jobInstanceId Job instance identifier
     * @return Is Job currently running on this cluster node and was not requested to be stopped
     */
    // @Lock(LockType.READ)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isShouldJobContinue(Long jobInstanceId) {
        return jobCacheContainerProvider.isShouldJobContinue(jobInstanceId);
    }

    /**
     * Mark job, identified by a given job instance, as locked to be running on current cluster node.
     * 
     * @param jobInstance Job instance
     * @param limitToSingleNode true if this job can be run on only one node.
     * @return Job execution status - was Job lock successful, failed to lock or job was requested stopped
     */
    // @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private JobRunningStatusEnum lockForRunning(JobInstance jobInstance, boolean limitToSingleNode) {
        return jobCacheContainerProvider.lockForRunning(jobInstance, limitToSingleNode);
    }

    /**
     * Mark job, identified by a given job instance id, as currently running on current cluster node.
     * 
     * @param jobInstanceId Job instance identifier
     * @param jobExecutionResultId Job execution result/progress identifier
     * @param threads Threads/futures that job is running on (optional)
     * @return Job execution status
     */
    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public JobRunningStatusEnum markJobAsRunning(JobInstance jobInstance, Long jobExecutionResultId, List<Future> threads) {
        return jobCacheContainerProvider.markJobAsRunning(jobInstance, jobExecutionResultId, threads);
    }

    /**
     * Mark job, identified by a given job instance id, as currently NOT running on CURRENT cluster node.
     * 
     * @param jobInstanceId Job instance identifier
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void markJobAsFinished(JobInstance jobInstance) {
        jobCacheContainerProvider.markJobAsFinished(jobInstance);
    }

    /**
     * Mark job, identified by a given job instance id, as requested to stop on CURRENT cluster node.
     * 
     * @param jobInstanceId Job instance identifier
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void markJobToStop(JobInstance jobInstance) {
        jobCacheContainerProvider.markJobToStop(jobInstance);
    }

    /**
     * Check if job execution was canceled
     * 
     * @param id Job instance identifier
     * @return True if job was execution was canceled by a user
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isJobCancelled(Long jobInstanceId) {

        JobExecutionStatus jobStatus = jobCacheContainerProvider.getJobStatus(jobInstanceId);
        if (jobStatus != null) {
            return jobStatus.isRequestedToStop();
        }
        return false;
    }

    /**
     * Restart any unfinished jobs that were not completed
     */
    @SuppressWarnings("unchecked")
    public void restartUnfinishedJobsUpponNodeRestart() {

        String nodeName = EjbUtils.getCurrentClusterNode();

        List<JobExecutionResultImpl> unfinishedJobResults = jobExecutionResultService.listUnfinishedJobsAndMarkThemCanceled(nodeName);

        for (JobExecutionResultImpl jobExecutionResult : unfinishedJobResults) {

            JobInstance jobInstance = jobExecutionResult.getJobInstance();

            if (!JobExecutionService.isRunnableOnNode(jobInstance.getRunOnNodesResolved())) {
                continue;
            }

            // A job that was running on a cluster and still has data to be processed in a queue, will be launched as a worker node
            if (jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.SPREAD_OVER_CLUSTER_NODES
                    && !IteratorBasedJobBean.areAllMessagesDelivered(jobInstance.getCode(), JobExecutionService.getJobQueueName(jobInstance.getCode()))) {

                Map<String, Object> jobParams = MapUtils.putAll(new HashMap<String, Object>(), new Object[] { Job.JOB_PARAM_HISTORY_PARENT_ID, jobExecutionResult.getId() });
                jobExecutionService.executeJob(jobInstance, jobParams, JobLauncherEnum.WORKER, false);

                // For jobs that run in parallel - launch it as regular job
            } else if (jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.RUN_IN_PARALLEL) {
                jobExecutionService.executeJob(jobInstance, null, JobLauncherEnum.INCOMPLETE, false);

                // For jobs that run one at a time - check if job that was running on the same node and launch it as a regular job
            } else if (jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.LIMIT_TO_SINGLE_NODE && nodeName.equals(jobExecutionResult.getNodeName())) {
                jobExecutionService.executeJob(jobInstance, null, JobLauncherEnum.INCOMPLETE, false);
            }
        }
    }

    /**
     * Wait for all nodes to finish running a job
     * 
     * @param jobInstanceId Job instance identifier
     * @param checkEveryMillis Wait time between status checks
     * @param checkTimes How many times to repeat status check
     * @return True if all nodes have finished running the job. False, if even after waiting, job is still marked as running
     */
    private boolean waitForAllNodesToFinishRunning(long jobInstanceId, long checkEveryMillis, int checkTimes) {

        JobRunningStatusEnum status = null;
        for (int i = 0; i < checkTimes; i++) {
            status = jobCacheContainerProvider.isJobRunning(jobInstanceId);
            if (status == JobRunningStatusEnum.NOT_RUNNING) {
                return true;
            }
            try {
                Thread.sleep(checkEveryMillis);
            } catch (InterruptedException e) {
            }
        }

        log.error("Timedout while waiting for all nodes to finish executing a job {}. Last status received was {}.", jobInstanceId, status);

        return false;
    }

    /**
     * Create JVM non-gracefull shutdown hooks to stop by force all running job threads, so job execution progress is logged
     */
    public static void addShutdownHooks() {

        @SuppressWarnings("rawtypes")
        Thread shutdownHook = new Thread(() -> {

            JobExecutionService.markServerIsInShutdownMode();

            Logger log = LoggerFactory.getLogger(JobExecutionService.class);

            log.error("Stopping jobs because of server shutdown");

            Map<Long, List<Future>> futureInfos = JobCacheContainerProvider.getJobExecutionThreads();

            for (Entry<Long, List<Future>> futureInfo : futureInfos.entrySet()) {

                IteratorBasedJobBean.markJobToStop(futureInfo.getKey());
                IteratorBasedJobBean.releaseJobDataProcessingThreads(futureInfo.getKey());

                int i = 1;
                for (Future future : futureInfo.getValue()) {
                    if (!future.isDone()) {
                        boolean canceled = future.cancel(true);
                        if (canceled) {
                            log.info("Job {} thread #{} was canceled by force as server was shutting down", futureInfo.getKey(), i);
                        } else {
                            log.error("Failed to cancel a job {} thread #{} as server was shutting down", futureInfo.getKey(), i);
                        }
                    }
                    i++;
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * Mark that server is in shutdown mode
     */
    public static void markServerIsInShutdownMode() {
        serverIsInShutdownMode.set(true);
    }

    /**
     * 
     * @return Is server operating in shutdown mode
     */
    public static boolean isServerIsInShutdownMode() {
        return serverIsInShutdownMode.get();
    }

    /**
     * Check if job instance is runnable on a current cluster node.
     *
     * @param currentNode Current cluster node
     * @return True if either current cluster node is unknown (non-clustered mode), runOnNodes is not specified or current cluster node matches any node in a list of nodes
     */
    public static boolean isRunnableOnNode(String runOnNodes) {

        String currentNode = EjbUtils.getCurrentClusterNode();

        if (currentNode == null || runOnNodes == null) {
            return true;
        }

        // Resolve EL expression
        if (runOnNodes.startsWith("#")) {
            return ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(runOnNodes, ValueExpressionWrapper.VAR_CURRENT_NODE, currentNode);

            // Evaluate from a list of node names
        } else {
            String[] nodes = runOnNodes.split(",");
            for (String node : nodes) {
                if (node.trim().equals(currentNode)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get a MQ Queue name for a given job code
     * 
     * @param jobCode Job code
     * @return MQ Queue name
     */
    public static String getJobQueueName(String jobCode) {
        return "JOB_" + jobCode.replace(' ', '_');
    }
}