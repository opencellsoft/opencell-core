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
import java.util.concurrent.Future;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobExecutionStatus;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.BaseService;

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

    /**
     * Execute a job and return job execution result ID to be able to query execution results later. Job execution result is persisted right away, while job is executed asynchronously.
     * 
     * @param jobInstance Job instance to execute.
     * @param params Parameters (currently not used)
     * @param jobLauncher How job was launched
     * @return Job execution result ID
     * @throws BusinessException Any exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long executeJob(JobInstance jobInstance, Map<String, Object> params, JobLauncherEnum jobLauncher, boolean triggerExecutionOnOtherNodes) throws BusinessException {

        // Preserve runTimeValues field, that gets set when executing from API
        Map<String, Object> runTimeValues = jobInstance.getRunTimeValues();

        jobInstance = jobInstanceService.findById(jobInstance.getId());

        jobInstance.setRunTimeValues(runTimeValues);

        log.info("Execute a job {} of type {} with parameters {} from {}", jobInstance, jobInstance.getJobTemplate(), params, jobLauncher);

        Long jobExecutionResultId = null;

        if (jobInstance.isRunnableOnNode(EjbUtils.getCurrentClusterNode())) {

            JobRunningStatusEnum isRunning = lockForRunning(jobInstance, jobInstance.isLimitToSingleNode());

            if ((jobInstance.isLimitToSingleNode() && isRunning == JobRunningStatusEnum.NOT_RUNNING)
                    || (!jobInstance.isLimitToSingleNode() && (isRunning == JobRunningStatusEnum.NOT_RUNNING || isRunning == JobRunningStatusEnum.LOCKED_OTHER || isRunning == JobRunningStatusEnum.RUNNING_OTHER))) {

                JobExecutionResultImpl jobExecutionResult = new JobExecutionResultImpl(jobInstance, jobLauncher);
                jobExecutionResultService.persistResult(jobExecutionResult);

                jobExecutionService.executeJobAsync(jobInstance, params, jobExecutionResult, currentUser.unProxy());

                jobExecutionResultId = jobExecutionResult.getId();

            } else if (isRunning == JobRunningStatusEnum.REQUEST_TO_STOP) {
                throw new ValidationException("Job is in the process of stopping. Please try again shortly.");

            } else if (isRunning == JobRunningStatusEnum.RUNNING_THIS || isRunning == JobRunningStatusEnum.LOCKED_THIS) {
                throw new ValidationException("Job is already running on this cluster node");

            } else {
                throw new ValidationException("Job is currently running on another cluster node and is limited to run one at a time");
            }
        }
        // Execute a job on other nodes if was launched from GUI or API and is not limited to run on current node only
        if (triggerExecutionOnOtherNodes && (jobLauncher == JobLauncherEnum.GUI || jobLauncher == JobLauncherEnum.API)
                && (!jobInstance.isLimitToSingleNode() || (jobInstance.isLimitToSingleNode() && !jobInstance.isRunnableOnNode(EjbUtils.getCurrentClusterNode())))) {

            Map<String, Object> jobParameters = new HashMap<String, Object>();
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
     * @param lastCurrentUser Currently authenticated user
     * @throws BusinessException Any exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeJobAsync(JobInstance jobInstance, Map<String, Object> params, JobExecutionResultImpl jobExecutionResult, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        Job job = jobInstanceService.getJobByName(jobInstance.getJobTemplate());
        JobExecutionResultStatusEnum jobResultStatus = job.execute(jobInstance, jobExecutionResult, null);

        int i = 0;
        while (jobResultStatus == JobExecutionResultStatusEnum.COMPLETED_MORE && i < MAX_TIMES_TO_RUN_INCOMPLETE_JOB) {
            jobResultStatus = job.execute(jobInstance, null, JobLauncherEnum.INCOMPLETE);
            i++;
        }

        if (jobResultStatus == JobExecutionResultStatusEnum.COMPLETED && jobInstance.getFollowingJob() != null) {
            JobInstance nextJob = jobInstanceService.refreshOrRetrieve(jobInstance.getFollowingJob());
            nextJob = PersistenceUtils.initializeAndUnproxy(nextJob);
            log.info("Executing next job {} for {}", nextJob.getCode(), jobInstance.getCode());
            executeJob(nextJob, null, JobLauncherEnum.TRIGGER, false);
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

        log.info("Requested to stop job {} of type {}  ", jobInstance, jobInstance.getJobTemplate());

        jobCacheContainerProvider.markJobToStop(jobInstance);
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

        if (triggerStopOnOtherNodes) {
            jobCacheContainerProvider.markJobToStop(jobInstance);
        }

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
            clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.stop);
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
     * @return Previous job execution status - was Job locked or running before and if on this or another node
     */
    private JobRunningStatusEnum lockForRunning(JobInstance jobInstance, boolean limitToSingleNode) {
        return jobCacheContainerProvider.lockForRunning(jobInstance, limitToSingleNode);
    }

    /**
     * Mark job, identified by a given job instance id, as currently running on current cluster node.
     * 
     * @param jobInstanceId Job instance identifier
     * @param limitToSingleNode true if this job can be run on only one node.
     * @param jobExecutionResultId Job execution result/progress identifier
     * @param threads Threads/futures that job is running on (optional)
     * @return Previous job execution status - was Job locked or running before and if on this or another node
     */
    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public JobRunningStatusEnum markJobAsRunning(JobInstance jobInstance, boolean limitToSingleNode, Long jobExecutionResultId, List<Future> threads) {
        return jobCacheContainerProvider.markJobAsRunning(jobInstance, limitToSingleNode, jobExecutionResultId, threads);
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
}