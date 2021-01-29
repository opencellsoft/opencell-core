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
package org.meveo.service.job;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
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
     * Check if job is still running (or is stopped) every 25 records being processed (per thread). Value to be used in jobs that run slow.
     */
    public static final int CHECK_IS_JOB_RUNNING_EVERY_NR_SLOW = 25;

    /**
     * Check if job is still running (or is stopped) every 50 records being processed (per thread). Value to be in jobs that run slower.
     */
    public static final int CHECK_IS_JOB_RUNNING_EVERY_NR = 50;

    /**
     * Check if job is still running (or is stopped) every 100 records being processed (per thread). Value to be used in jobs that run faster.
     */
    public static final int CHECK_IS_JOB_RUNNING_EVERY_NR_FAST = 100;

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

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

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
                log.info(job.getClass().getName() + " " + resultToPersist.toString());
                isPersistResult = true;
            } else {
                log.info("{}/{}: No items were found to process", job.getClass().getName(), jobInstance.getCode());
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
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeNextJob(Job job, JobInstance jobInstance, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        try {
            if (jobInstance.getFollowingJob() != null) {
                JobInstance nextJob = jobInstanceService.retrieveIfNotManaged(jobInstance.getFollowingJob());
                log.info("Executing next job {} for {}", nextJob.getCode(), jobInstance.getCode());
                executeJobWithParameters(nextJob, null);
            }
        } catch (BusinessException e) {
            log.error("Failed to execute next job", e);
        }
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
     * @param jobName job name (optional)
     * @param date Date to check
     * @return A number of job execution history records which is older then a given date
     */
    public long countJobExecutionHistoryToDelete(String jobName, Date date) {
        long result = 0;

        if (jobName == null) {
            result = getEntityManager().createNamedQuery("JobExecutionResult.countHistoryToPurgeByDate", Long.class).setParameter("date", date).getSingleResult();
        } else {
            JobInstance jobInstance = jobInstanceService.findByCode(jobName);
            if (jobInstance == null) {
                log.error("No Job instance by code {} was found. No Job execution history will be removed.", jobName);
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
     * @param jobName Job name to match (optional)
     * @param date Date to check
     * @return A number of records that were removed
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteJobExecutionHistory(String jobName, Date date) {
        log.debug("Removing Job execution history of job {} which date is older then a {} date", jobName == null ? "ALL" : jobName, date);

        long itemsDeleted = 0;

        if (jobName == null) {
            itemsDeleted = getEntityManager().createNamedQuery("JobExecutionResult.purgeHistoryByDate").setParameter("date", date).executeUpdate();

        } else {
            JobInstance jobInstance = jobInstanceService.findByCode(jobName);
            if (jobInstance == null) {
                log.error("No Job instance by code {} was found. No Job execution history will be removed.", jobName);
                return 0;
            }
            itemsDeleted = getEntityManager().createNamedQuery("JobExecutionResult.purgeHistoryByDateAndJobInstance").setParameter("date", date)
                .setParameter("jobInstance", jobInstance).executeUpdate();
        }

        log.info("Removed {} Job execution history of job {} which date is older then a {} date", itemsDeleted, jobName == null ? "ALL" : jobName, date);

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

        List resultList = qb.getQuery(getEntityManager()).setMaxResults(1).getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (JobExecutionResultImpl) resultList.get(0);
        }
        return null;
    }

    /**
     * Init job counters for multiple executions
     * 
     * @param jobInstance
     */
    public void iniJobCounters(JobInstance jobInstance) {
        String[] counterNames = new String[] { "is_running", "running_threads", "elements_remaining", "number_of_ok", "number_of_war", "number_of_ko" };

        Tag tgName = new Tag("name", jobInstance.getCode());

        for (String name : counterNames) {

            String fullName = name + "_" + jobInstance.getJobTemplate() + "_" + jobInstance.getCode();
            Optional<Entry<MetricID, Counter>> result = registry.getCounters().entrySet().stream().filter(m -> m.getKey().compareTo(new MetricID(fullName, tgName)) == 0).findAny();

            if (result.isPresent()) {
                Counter counter = result.get().getValue();
                counter.inc(counter.getCount() * -1);
            }
        }
    }

    /**
     * Create counter metric for JobExecutionResultImpl
     * 
     * @param jobExecutionResultImpl
     * @param value
     * @param name the name of metric
     */
    public void initCounterElementsRemaining(JobExecutionResultImpl jobExecutionResultImpl, Number value) {
        counterInc(jobExecutionResultImpl, "elements_remaining", value.longValue());
    }

    /**
     * Decreased elements_remaining counter for JobExecutionResultImpl
     * 
     * @param jobExecutionResultImpl
     * @param name the name of metric
     */
    public void decCounterElementsRemaining(JobExecutionResultImpl jobExecutionResultImpl) {
        counterInc(jobExecutionResultImpl, "elements_remaining", -1L);
    }

    /**
     * Create counter metric for JobExecutionResultImpl
     * 
     * @param jobExecutionResultImpl
     * @param value
     * @param name the name of metric
     */
    public void counterInc(JobExecutionResultImpl jobExecutionResultImpl, String name, Long value) {
        JobInstance jobInstance = jobExecutionResultImpl.getJobInstance();
        Metadata metadata = new MetadataBuilder().withName(name + "_" + jobInstance.getJobTemplate() + "_" + jobInstance.getCode()).reusable().build();
        Tag tgName = new Tag("name", jobInstance.getCode());
        Counter counter = registry.counter(metadata, tgName);

        if (value != null) {
            counter.inc(value);
        } else {
            counter.inc();
        }
    }

    /**
     * Create counter metric for JobExecutionResultImpl
     * 
     * @param jobExecutionResultImpl
     * @param name the name of metric
     */
    public void counterInc(JobExecutionResultImpl jobExecutionResultImpl, String name) {
        counterInc(jobExecutionResultImpl, name, null);
    }

    /**
     * Metric of the number of running threads of jobs
     * 
     * @param jobExecutionResultImpl
     * @param value
     * @param name the name of metric
     */
    public void counterRunningThreads(JobExecutionResultImpl jobExecutionResultImpl, Long value) {
        counterInc(jobExecutionResultImpl, "running_threads", value);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param incrementBy
     */
    public void addNbItemsCorrectlyProcessed(JobExecutionResultImpl jobExecutionResultImpl, long incrementBy) {
        jobExecutionResultImpl.addNbItemsCorrectlyProcessed(incrementBy);
        counterNbItemsProcessed(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param incrementBy
     */
    public void addNbItemsProcessedWithWarning(JobExecutionResultImpl jobExecutionResultImpl, long incrementBy) {
        jobExecutionResultImpl.addNbItemsProcessedWithWarning(incrementBy);
        counterNbItemsProcessedWithWarning(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param incrementBy
     */
    public void addNbItemsProcessedWithError(JobExecutionResultImpl jobExecutionResultImpl, long incrementBy) {
        jobExecutionResultImpl.addNbItemsProcessedWithError(incrementBy);
        counterNbItemsProcessedWithError(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     */
    public void registerSucces(JobExecutionResultImpl jobExecutionResultImpl) {
        jobExecutionResultImpl.registerSucces();
        counterNbItemsProcessed(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param warning
     */
    public void registerWarning(JobExecutionResultImpl jobExecutionResultImpl, String warning) {
        jobExecutionResultImpl.registerWarning(warning);
        counterNbItemsProcessedWithWarning(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param identifier
     * @param warning
     */
    public void registerWarning(JobExecutionResultImpl jobExecutionResultImpl, Serializable identifier, String warning) {
        jobExecutionResultImpl.registerWarning(identifier, warning);
        counterNbItemsProcessedWithWarning(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     */
    public void registerError(JobExecutionResultImpl jobExecutionResultImpl) {
        jobExecutionResultImpl.registerError();
        counterNbItemsProcessedWithError(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param error
     */
    public void registerError(JobExecutionResultImpl jobExecutionResultImpl, String error) {
        jobExecutionResultImpl.registerError(error);
        counterNbItemsProcessedWithError(jobExecutionResultImpl);
    }

    /**
     * Use counter metric
     * 
     * @param jobExecutionResultImpl
     * @param identifier
     * @param error
     */
    public void registerError(JobExecutionResultImpl jobExecutionResultImpl, Serializable identifier, String error) {
        jobExecutionResultImpl.registerError(identifier, error);
        counterNbItemsProcessedWithError(jobExecutionResultImpl);
    }

    private void counterNbItemsProcessed(JobExecutionResultImpl jobExecutionResultImpl) {
        counterInc(jobExecutionResultImpl, "number_of_ok");
    }

    private void counterNbItemsProcessedWithWarning(JobExecutionResultImpl jobExecutionResultImpl) {
        counterInc(jobExecutionResultImpl, "number_of_warn");
    }

    private void counterNbItemsProcessedWithError(JobExecutionResultImpl jobExecutionResultImpl) {
        counterInc(jobExecutionResultImpl, "number_of_ko");
    }
}