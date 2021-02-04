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
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.event.qualifier.Processed;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface that must implement all jobs that are managed in meveo application by the JobService bean. The implementation must be a session EJB and must statically register itself
 * (through its jndi name) to the JobService.
 * 
 * @author seb
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
public abstract class Job {

    public static final String CFT_PREFIX = "JobInstance";

    /**
     * Custom field for a Number of simultaneous threads that job executes
     */
    public static final String CF_NB_RUNS = "nbRuns";

    /**
     * Custom field for a Milliseconds to wait before launching another async processing of data batch in a job
     */
    public static final String CF_WAITING_MILLIS = "waitingMillis";

    @Resource
    protected TimerService timerService;

    @Inject
    protected JobExecutionService jobExecutionService;

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
    protected ResourceBundle resourceMessages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    @Inject
    private AuditOrigin auditOrigin;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Execute job instance with results published to a given job execution result entity.
     * 
     * @param jobInstance Job instance to execute
     * @param executionResult Job execution results
     * @throws BusinessException business exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobInstance jobInstance, JobExecutionResultImpl executionResult) throws BusinessException {

        auditOrigin.setAuditOrigin(ChangeOriginEnum.JOB);
        auditOrigin.setAuditOriginName(jobInstance.getJobTemplate() + "/" + jobInstance.getCode());
        // Init counters
        jobExecutionService.iniJobCounters(jobInstance);
        // Add counter metrics
        Metadata metadata = new MetadataBuilder().withName("is_running_" + jobInstance.getJobTemplate() + "_" + jobInstance.getCode()).build();
        // Counter that return 1 when job is running
        Tag tgName = new Tag("name", jobInstance.getCode());
        Counter counter = registry.counter(metadata, tgName);
        counter.inc();

        if (executionResult == null) {
            executionResult = new JobExecutionResultImpl();
            executionResult.setJobInstance(jobInstance);
            jobExecutionService.create(executionResult);
        }

        JobRunningStatusEnum isRunning = jobCacheContainerProvider.markJobAsRunning(jobInstance.getId(), jobInstance.isLimitToSingleNode());
        if (isRunning == JobRunningStatusEnum.NOT_RUNNING || (isRunning == JobRunningStatusEnum.RUNNING_OTHER && !jobInstance.isLimitToSingleNode())) {
            log.info("Starting Job {} of type {}  with currentUser {}. Processors available {}, paralel procesors requested {}. Job parameters {}", jobInstance.getCode(),
                jobInstance.getJobTemplate(), currentUser.toString(), Runtime.getRuntime().availableProcessors(),
                customFieldInstanceService.getCFValue(jobInstance, CF_NB_RUNS, false), jobInstance.getParametres());

            try {
                execute(executionResult, jobInstance);
                executionResult.close();

                log.trace("Job {} of type {} executed. Persisting job execution results", jobInstance.getCode(), jobInstance.getJobTemplate());

                Boolean jobCompleted = jobExecutionService.persistResult(this, executionResult, jobInstance);
                log.info("Job {} of type {} execution finished. Job completed {}", jobInstance.getCode(), jobInstance.getJobTemplate(), jobCompleted);
                eventJobProcessed.fire(executionResult);

                if (jobCompleted != null && jobExecutionService.isJobRunningOnThis(jobInstance)) {
                    jobCacheContainerProvider.markJobAsNotRunning(jobInstance.getId());
                    try {
                        if (!jobCompleted) {
                            execute(jobInstance, null);
                        } else if (jobInstance.getFollowingJob() != null) {
                            MeveoUser lastCurrentUser = currentUser.unProxy();
                            jobExecutionService.executeNextJob(this, jobInstance, lastCurrentUser);
                        }
                    } catch (Exception e) {
                        if (!jobInstance.isStopOnError()) {
                            MeveoUser lastCurrentUser = currentUser.unProxy();
                            jobExecutionService.executeNextJob(this, jobInstance, lastCurrentUser);
                        }
                        throw new BusinessException(e);
                    }
                }

            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getJobTemplate(), jobInstance.getJobTemplate(), e);
                throw new BusinessException(e);
            } finally {
                counter.inc(-1);
                jobCacheContainerProvider.markJobAsNotRunning(jobInstance.getId());
            }

        } else {
            try {
                log.info("Job {} of type {} execution will be skipped. Reason: isRunning={}", jobInstance.getCode(), jobInstance.getJobTemplate(), isRunning);

                // Mark job a finished. Applies in cases where execution result was already saved to db - like when executing job from API
                if (!executionResult.isTransient()) {
                    executionResult.close();
                    jobExecutionService.persistResult(this, executionResult, jobInstance);
                }
            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getJobTemplate(), jobInstance.getJobTemplate(), e);
                throw new BusinessException(e);
            } finally {
                // revert counter to return 0 at the end of the job
                counter.inc(-1);
            }
        }
    }

    /**
     * Execute job instance with results published to a given job execution result entity. Executed in Asynchronous mode.
     * 
     * @param jobInstance Job instance to execute
     * @param result Job execution results
     * @throws BusinessException business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeInNewTrans(JobInstance jobInstance, JobExecutionResultImpl result) throws BusinessException {

        execute(jobInstance, result);
    }

    /**
     * The actual job execution logic implementation.
     * 
     * @param result Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    protected abstract void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException;

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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Timer createTimer(ScheduleExpression scheduleExpression, JobInstance jobInstance) {

        jobInstance.setProviderCode(currentUser.getProviderCode());

        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(jobInstance);
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
