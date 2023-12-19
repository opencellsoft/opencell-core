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

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.event.qualifier.Processed;
import org.meveo.event.qualifier.Started;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobClusterBehaviorEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface that must implement all jobs that are managed in meveo application by the JobService bean. The implementation must be a session EJB and must statically register itself (through its jndi name) to the
 * JobService.
 * 
 * @author seb
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
public abstract class Job {

    public static final String CFT_PREFIX = "JobInstance";

    /**
     * Custom field for a Number of simultaneous data processing threads that job executes
     */
    public static final String CF_NB_RUNS = "nbRuns";

    /**
     * Custom field for a Milliseconds to wait before launching another job thread
     */
    public static final String CF_WAITING_MILLIS = "waitingMillis";

    /**
     * Custom field for a number of items to process simultaneously in one transaction as a batch. If batch fails, items will be processed one by one.
     */
    public static final String CF_BATCH_SIZE = "batchSize";

    /**
     * Custom field for a number of threads to publish data for cluster wide processing
     */
    public static final String CF_NB_PUBLISHERS = "nbPublishers";
    
    /**
     * Custom field for a applyBilingRules.
     */
    public static final String CF_APPLY_BILLING_RULES = "applyBillingRules";

    /**
     * Custom field for a sorting option
     */
    public static final String CF_SORTING_OPTION = "sortingOption";

    /**
     * What initiated/launched Job
     */
    public static final String JOB_PARAM_LAUNCHER = "jobLauncher";

    /**
     * Parent job result identifier
     */
    public static final String JOB_PARAM_HISTORY_PARENT_ID = "parentJobResultId";

    @Resource
    protected TimerService timerService;

    @Inject
    protected JobExecutionService jobExecutionService;

    @Inject
    protected JobExecutionResultService jobExecutionResultService;

    @Inject
    private JobExecutionInJaasService jobExecutionInJaasService;

    @EJB
    protected JobInstanceService jobInstanceService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    protected UserService userService;

    @Inject
    @Processed
    private Event<JobExecutionResultImpl> eventJobProcessed;

    @Inject
    @Started
    private Event<JobExecutionResultImpl> eventJobStarted;

    @Inject
    protected ResourceBundle resourceMessages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/default")
    ManagedExecutorService executor;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Execute job instance with results published to a given job execution result entity.
     * 
     * @param jobInstance Job instance to execute
     * @param executionResult Job execution results
     * @param jobLauncher How job was launched. A value to use when job is executing with no job execution result provided and new job execution result must be created
     * @return True if job executed completely and no more data is left to process
     * @throws BusinessException business exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public JobExecutionResultStatusEnum execute(JobInstance jobInstance, JobExecutionResultImpl executionResult, JobLauncherEnum jobLauncher) throws BusinessException {

        AuditOrigin.setAuditOriginAndName(ChangeOriginEnum.JOB, jobInstance.getJobTemplate() + "/" + jobInstance.getCode());

        JobRunningStatusEnum jobRunningStatus = jobExecutionService.markJobAsRunning(jobInstance, jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.LIMIT_TO_SINGLE_NODE,
            executionResult != null ? executionResult.getId() : null, null);

        if (jobRunningStatus == JobRunningStatusEnum.NOT_RUNNING || jobRunningStatus == JobRunningStatusEnum.LOCKED_THIS
                || (jobInstance.getClusterBehavior() != JobClusterBehaviorEnum.LIMIT_TO_SINGLE_NODE && (jobRunningStatus == JobRunningStatusEnum.RUNNING_OTHER || jobRunningStatus == JobRunningStatusEnum.LOCKED_OTHER))) {

            log.info("Starting Job {} of type {}  with currentUser {}. Processors available {}, paralel procesors requested {}. Job parameters {}", jobInstance.getCode(), jobInstance.getJobTemplate(), currentUser,
                Runtime.getRuntime().availableProcessors(), customFieldInstanceService.getCFValue(jobInstance, "nbRuns", false), jobInstance.getParametres());

            if (executionResult == null) {
                executionResult = new JobExecutionResultImpl(jobInstance, jobLauncher != null ? jobLauncher : JobLauncherEnum.TRIGGER, EjbUtils.getCurrentClusterNode());
                jobExecutionResultService.persistResult(executionResult);
            }

            try {
                eventJobStarted.fire(executionResult);
                executionResult = execute(executionResult, jobInstance);

                boolean moreToProcess = executionResult.isMoreToProcess();

                boolean jobCanceled = closeExecutionResult(jobInstance, executionResult, moreToProcess);

                log.info("Job {} of type {} execution finished. Job {}", jobInstance.getCode(), jobInstance.getJobTemplate(),
                    jobCanceled ? "was canceled." : moreToProcess ? "completed, with more data to process." : "completed.");

                if (!moreToProcess) {
                    eventJobProcessed.fire(executionResult);
                }

                return executionResult.getStatus();

            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate(), e);
                executionResult.setStatus(JobExecutionResultStatusEnum.FAILED);
                executionResult.addReport(e.getMessage());
                executionResult.close();
                jobExecutionResultService.persistResult(executionResult);

                throw new BusinessException(e);

            } finally {
                jobExecutionService.markJobAsFinished(jobInstance);
            }

        } else {
            log.info("Job {} of type {} execution will be skipped. Reason: jobStatus={}", jobInstance.getCode(), jobInstance.getJobTemplate(), jobRunningStatus);

            // Mark job a finished and remove execution result from history
            executionResult.close();
            jobExecutionResultService.remove(executionResult);

            return JobExecutionResultStatusEnum.CANCELLED;
        }
    }

    protected boolean closeExecutionResult(JobInstance jobInstance, JobExecutionResultImpl executionResult, boolean moreToProcess) {
        boolean serverShutdown = JobExecutionService.isServerIsInShutdownMode();
        boolean jobCanceled = serverShutdown ? true : jobExecutionService.isJobCancelled(jobInstance.getId());
        executionResult.setStatus(serverShutdown ? JobExecutionResultStatusEnum.SHUTDOWN
                : jobCanceled ? JobExecutionResultStatusEnum.CANCELLED : moreToProcess ? JobExecutionResultStatusEnum.COMPLETED_MORE : JobExecutionResultStatusEnum.COMPLETED);
        if (serverShutdown) {
            executionResult.addReportToBeginning("Job cancelled due to the server was shutdown in the middle of job execution");
        }
        executionResult.close();
        jobExecutionResultService.persistResult(executionResult);
        return jobCanceled;
    }

    /**
     * The actual job execution logic implementation.
     * 
     * @param result Job execution results
     * @param jobInstance Job instance to execute
     * @return In case job consist of various stages and each stage tracks its own progress, the Job execution result of the last stage will be returned. In case of a single stage, a Job execution results that was passed
     *         as argument to the method and method return value will be the same.
     * @throws BusinessException Any exception
     */
    protected abstract JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException;

    /**
     * Canceling timers associated to this job implmenentation- solves and issue when server is restarted and wildlfy data directory contains previously active timers.
     */
    public void cleanTimers() {

        Collection<Timer> alltimers = timerService.getTimers();
        log.info("Canceling job timers for job {}", this.getClass().getName());

        for (Timer timer : alltimers) {
            try {
                if (timer.getInfo() instanceof JobInstance) {
                    timer.cancel();
                }
            } catch (Exception e) {
                log.error("Failed to cancel timer {} for job{}", timer.getHandle(), this.getClass().getName(), e);
            }
        }
    }

    /**
     * Register/schedule a timer for a job instance.
     * 
     * @param scheduleExpression Schedule expression
     * @param jobInstance Job instance to execute
     * @return Instantiated timer object
     */
    // @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Timer createTimer(ScheduleExpression scheduleExpression, JobInstance jobInstance) {

        TimerConfig timerConfig = new TimerConfig();

        JobInstance jobInstanceSimple = new JobInstance();
        jobInstanceSimple.setId(jobInstance.getId());
        jobInstanceSimple.setCode(jobInstance.getCode());
        jobInstanceSimple.setJobTemplate(jobInstance.getJobTemplate());
        jobInstanceSimple.setProviderCode(currentUser.getProviderCode());
        jobInstanceSimple.setAuditable(jobInstance.getAuditable());

        timerConfig.setInfo(jobInstanceSimple);
        // timerConfig.setPersistent(false);
        // log.error("AKK creating a timer for {}", jobInstance.getCode());
        return timerService.createCalendarTimer(scheduleExpression, timerConfig);
    }

    /**
     * Trigger job execution uppon scheduler timer expiration.
     * 
     * @param timer Timer configuration with jobInstance entity as Info attribute
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void trigger(Timer timer) {

        JobInstance jobInstance = (JobInstance) timer.getInfo();
        if (jobInstance == null) {
            return;
        }

        try {
            jobExecutionInJaasService.executeInJaas(jobInstance, this);
        } catch (Exception e) {
            log.error("Failed to execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate(), e);
        }
    }

    /**
     * @return job category enum
     */
    @SuppressWarnings("rawtypes")
    public abstract JobCategoryEnum getJobCategory();

    /**
     * @return map of custom fields
     */
    public Map<String, CustomFieldTemplate> getCustomFields() {

        return null;
    }

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @param defaultValue Default value if no value found
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode, Object defaultValue) {
        Object value = getParamOrCFValue(jobInstance, cfCode);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the parameter CF value if found , otherwise return CF value from customFieldInstanceService
     *
     * @param jobInstance the job instance
     * @param cfCode the cf code
     * @return the param or CF value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }

    /**
     * What is a job's target entity class - an entity that job iterates through and error logs can be traced to that entity
     * 
     * @param jobInstance Job instance definition
     * @return Entity class
     */
    @SuppressWarnings("rawtypes")
    public Class getTargetEntityClass(JobInstance jobInstance) {
        return null;
    }
    
    /*
     * those methods will be used later for asynchronous jobs
     * 
     * public JobExecutionResult pause();
     * 
     * public JobExecutionResult resume();
     * 
     * public JobExecutionResult stop();
     * 
     * public JobExecutionResult getResult();
     */
}
