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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.PersistenceService;

/**
 * The Class JobExecutionService.
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class JobExecutionService extends PersistenceService<JobExecutionResultImpl> {

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

    /**
     * Persist job execution results.
     * 
     * @param job Job implementation
     * @param result Execution result
     * @param jobInstance Job instance
     * @return True if job is completely done. False if any data are left to process.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Boolean persistResult(Job job, JobExecutionResultImpl result, JobInstance jobInstance) {
        try {
            JobExecutionResultImpl resultToPersist = JobExecutionResultImpl.createFromInterface(jobInstance, result);
            boolean isPersistResult = false;

            if ((resultToPersist.getNbItemsCorrectlyProcessed() + resultToPersist.getNbItemsProcessedWithError() + resultToPersist.getNbItemsProcessedWithWarning()) > 0) {
                log.info(job.getClass().getName() + resultToPersist.toString());
                isPersistResult = true;
            } else {
                log.info(job.getClass().getName() + ": nothing was processed");
                isPersistResult = "true".equals(paramBeanFactory.getInstance().getProperty("meveo.job.persistResult", "true"));
            }
            if (isPersistResult) {
                if (resultToPersist.isTransient()) {
                    create(resultToPersist);
                    result.setId(resultToPersist.getId());
                } else {
                    // search for job execution result
                    JobExecutionResultImpl updateEntity = findById(result.getId());
                    if (updateEntity != null) {
                        JobExecutionResultImpl.updateFromInterface(result, updateEntity);
                        update(updateEntity);
                    }
                }
            }
            return resultToPersist.isDone();

        } catch (Exception e) { // FIXME:BusinessException e) {
            log.error("Failed to persist job execution results", e);
        }

        return null;
    }

    /**
     * Execute next job or continue executing same job if more data is left to process (execution in batches). Executed asynchronously. Current user should be already available
     * from earlier context.
     * 
     * @param job Job implementation
     * @param jobInstance Job instance
     * @param continueSameJob Continue executing same job
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeNextJob(Job job, JobInstance jobInstance, boolean continueSameJob, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        try {
            if (continueSameJob) {
                log.debug("Continue executing job {}", jobInstance);
                executeJobWithParameters(jobInstance, null);

            } else if (jobInstance.getFollowingJob() != null) {
                JobInstance nextJob = jobInstanceService.retrieveIfNotManaged(jobInstance.getFollowingJob());
                log.debug("Executing next job {}", nextJob.getCode());
                executeJobWithParameters(nextJob, null);
            }
        } catch (Exception e) { // FIXME:BusinessException e) {
            log.error("Failed to execute next job or continue same job", e);
        }
        log.info("JobExecutionService executeNextJob End");
    }

    /**
     * Execute a given job instance.
     * 
     * @param jobInstance Job instance to execute
     * @param params Parameters to pass to job execution
     * @throws BusinessException business exception
     */
    private void executeJobWithParameters(JobInstance jobInstance, Map<Object, Object> params) throws BusinessException {
        Job job = jobInstanceService.getJobByName(jobInstance.getJobTemplate());
        job.execute(jobInstance, null);
    }

    /**
     * Execute job from GUI. Execution is done asynchronously. Current user is already set by GUI.
     * 
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void manualExecute(JobInstance jobInstance) throws BusinessException {
        log.info("Manual execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate());
        try {
            executeJobWithParameters(jobInstance, null);
        } catch (Exception e) {
            log.error("Failed to manually execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate(), e);
            throw e;
        }
    }

    /**
     * Execute job and return job execution result ID to be able to query execution results later. Job execution result is persisted right away, while job is executed
     * asynchronously.
     * 
     * @param jobInstance Job instance to execute.
     * @param params Parameters (currently not used)
     * @return Job execution result ID
     * @throws BusinessException Any exception
     */
    public Long executeJobWithResultId(JobInstance jobInstance, Map<String, String> params) throws BusinessException {
        log.info("Execute a job {}  of type {} with parameters {} ", jobInstance, jobInstance.getJobTemplate(), params);
        try {
            JobExecutionResultImpl jobExecutionResult = new JobExecutionResultImpl();
            jobExecutionResult.setJobInstance(jobInstance);
            create(jobExecutionResult);

            Job job = jobInstanceService.getJobByName(jobInstance.getJobTemplate());
            job.executeInNewTrans(jobInstance, jobExecutionResult);

            log.debug("Job execution result ID for job {} of type {} is {}", jobInstance, jobInstance.getJobTemplate(), jobExecutionResult.getId());
            return jobExecutionResult.getId();

        } catch (Exception e) {
            log.error("Failed to execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate(), e);
            throw new BusinessException(e);
        }
    }

    /**
     * Execute job and return job execution result ID to be able to query execution results later. Job is executed asynchronously.
     * 
     * @param jobInstance Job instance to execute.
     * @param params Parameters (currently not used)
     * @throws BusinessException Any exception
     */
    public void executeJob(JobInstance jobInstance, Map<Object, Object> params) throws BusinessException {
        log.info("Execute a job {}  of type {} with parameters {} ", jobInstance, jobInstance.getJobTemplate(), params);
        executeJobWithParameters(jobInstance, params);
    }

    /**
     * Gets the find query.
     *
     * @param jobName job name
     * @param configuration configuration
     * @return querry builder
     */
    private QueryBuilder getFindQuery(String jobName, PaginationConfiguration configuration) {
        QueryBuilder qb = new QueryBuilder("select distinct t from JobExecutionResultImpl t"); // FIXME:.cacheable();

        if (!StringUtils.isEmpty(jobName)) {
            qb.addCriterion("t.jobInstance.code", "=", jobName, false);
        }
        qb.addPaginationConfiguration(configuration);

        return qb;
    }

    /**
     * Count job execution history records which end date is older then a given date and belong to a given job (optional)
     * 
     * @param jobCode Job code (optional)
     * @param date Date to check
     * @return A number of job execution history records which is older then a given date
     */
    public long countJobExecutionHistoryToDelete(String jobCode, Date date) {

        long result = 0;

        if (jobCode == null) {
            result = getEntityManager().createNamedQuery("JobExecutionResult.countHistoryToPurgeByDate", Long.class).setParameter("date", date).getSingleResult();
        } else {
            JobInstance jobInstance = jobInstanceService.findByCode(jobCode);
            if (jobInstance == null) {
                log.error("No Job instance by code {} was found. No Job execution history will be removed.", jobCode);
                return 0;
            }
            result = getEntityManager().createNamedQuery("JobExecutionResult.countHistoryToPurgeByDateAndJobInstance", Long.class).setParameter("date", date)
                .setParameter("jobInstance", jobInstance).getSingleResult();
        }
        return result;
    }

    /**
     * Remove job execution history older than a given date and belong to a given job (optional)
     * 
     * @param jobCode Job code (optional)
     * @param date Date to check
     * @return A number of records that were removed
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteJobExecutionHistory(String jobCode, Date date) {
        log.debug("Removing Job execution history of job {} which date is older then a {} date", jobCode == null ? "ALL" : jobCode, date);

        long itemsDeleted = 0;

        if (jobCode == null) {
            itemsDeleted = getEntityManager().createNamedQuery("JobExecutionResult.purgeHistoryByDate").setParameter("date", date).executeUpdate();

        } else {
            JobInstance jobInstance = jobInstanceService.findByCode(jobCode);
            if (jobInstance == null) {
                log.error("No Job instance by code {} was found. No Job execution history will be removed.", jobCode);
                return 0;
            }
            itemsDeleted = getEntityManager().createNamedQuery("JobExecutionResult.purgeHistoryByDateAndJobInstance").setParameter("date", date)
                .setParameter("jobInstance", jobInstance).executeUpdate();
        }

        log.info("Removed {} Job execution history of job {} which date is older then a {} date", itemsDeleted, jobCode == null ? "ALL" : jobCode, date);

        return itemsDeleted;
    }

    /**
     * Find JobExecutionResultImpl.
     *
     * @param jobName job's name
     * @param configuration pagination configuration
     * @return list of job's result.
     */
    @SuppressWarnings("unchecked")
    public List<JobExecutionResultImpl> find(String jobName, PaginationConfiguration configuration) {
        return getFindQuery(jobName, configuration).find(getEntityManager());
    }

    /**
     * Count.
     *
     * @param jobName job name
     * @param configuration configuration
     * @return number of job
     */
    public long count(String jobName, PaginationConfiguration configuration) {
        return getFindQuery(jobName, configuration).count(getEntityManager());
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
     * Find by code like.
     *
     * @param code the code
     * @return list of job's result
     * @see org.meveo.service.base.PersistenceService#findByCodeLike(java.lang.String)
     */
    @Override
    public List<JobExecutionResultImpl> findByCodeLike(String code) {
        throw new UnsupportedOperationException();
    }

    /**
     * Stop a running job.
     *
     * @param jobInstance job instance to stop
     * @throws BusinessException the business exception
     */
    public void stopJob(JobInstance jobInstance) throws BusinessException {
        log.info("Stop job {}  of type {}  ", jobInstance, jobInstance.getJobTemplate());
        if (!isJobRunningOnThis(jobInstance)) {
            throw new BusinessException("Job " + jobInstance.getCode() + " currently are not running on this node.");
        }
        jobCacheContainerProvider.markJobAsNotRunning(jobInstance.getId());
    }

    /**
     * Check if the job are running on this node.
     * 
     * @param jobInstance job instance to ckeck
     * @return return true if job are running
     */
    public boolean isJobRunningOnThis(JobInstance jobInstance) {
        return isJobRunningOnThis(jobInstance.getId());
    }

    /**
     * Check if the job are running on this node.
     * 
     * @param jobInstanceId job instance id to ckeck
     * @return return true if job are running
     */
    public boolean isJobRunningOnThis(Long jobInstanceId) {
        return JobRunningStatusEnum.RUNNING_THIS == jobCacheContainerProvider.isJobRunning(jobInstanceId);
    }

    /**
     * Finds the last job execution result by a given job instance.
     * 
     * @param jobInstance JobInstance filter
     * @return last job execution result
     */
    public JobExecutionResultImpl findLastExecutionByInstance(JobInstance jobInstance) {
        QueryBuilder qb = new QueryBuilder(JobExecutionResultImpl.class, "j");
        qb.addCriterionEntity("jobInstance", jobInstance);
        qb.addOrderCriterionAsIs("startDate", false);

        return (JobExecutionResultImpl) qb.getQuery(getEntityManager()).setMaxResults(1).getResultList().get(0);
    }
}