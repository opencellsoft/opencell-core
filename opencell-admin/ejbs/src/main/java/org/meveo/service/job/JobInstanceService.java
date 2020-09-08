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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CacheKeyLong;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.util.EntityCustomizationUtils;

@Stateless
public class JobInstanceService extends BusinessService<JobInstance> {

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    private static Map<JobCategoryEnum, List<Class<? extends Job>>> jobClasses = new HashMap<>();
    private static Map<CacheKeyLong, Timer> jobTimers = new HashMap<>();

    private static ParamBean paramBean = ParamBean.getInstance();

    /**
     * Register job classes and schedule active job instances
     */
    public void registerJobs() {

        Set<Class<?>> classes = ReflectionUtils.getSubclasses(Job.class);

        for (Class<?> jobClass : classes) {
            Job job = (Job) EjbUtils.getServiceInterface(jobClass.getSimpleName());
            registerJob(job);
        }
    }

    /**
     * Register job class and schedule active job instances.
     * 
     * @param job job to be registered.
     */
    private void registerJob(Job job) {

        boolean clearTimers = false;
        synchronized (jobTimers) {

            if (!jobClasses.containsKey(job.getJobCategory())) {
                jobClasses.put(job.getJobCategory(), new ArrayList<>());
            }
            if (!jobClasses.get(job.getJobCategory()).contains(job.getClass())) {
                jobClasses.get(job.getJobCategory()).add(job.getClass());
                clearTimers = true;
            }

            Map<String, CustomFieldTemplate> cfts = job.getCustomFields();
            if (cfts != null && !cfts.isEmpty()) {
                try {
                    customFieldTemplateService.createMissingTemplates(EntityCustomizationUtils.getAppliesTo(job.getClass(), null), cfts.values());
                } catch (BusinessException e) {
                    log.error("Failed to registed missing CF templates for job " + job.getClass());
                }
            }

            log.debug("Registered a job {} of category {}", job.getClass(), job.getJobCategory());
        }
        startTimers(job, clearTimers);
    }

    /**
     * Register timers for applicable job instances of a given job class.
     * 
     * @param job Job class to register timers for
     * @param clearTimers Should previous timers be removed - used to remove old timers at application startup
     */
    @SuppressWarnings("unchecked")
    private void startTimers(Job job, boolean clearTimers) {

        if (clearTimers) {
            job.cleanTimers();
        }

        List<JobInstance> jobInstances = getEntityManager().createQuery("from JobInstance ji LEFT JOIN FETCH ji.followingJob where ji.jobTemplate=:jobName")
            .setParameter("jobName", ReflectionUtils.getCleanClassName(job.getClass().getSimpleName())).getResultList();

        int started = 0;

        for (JobInstance jobInstance : jobInstances) {
            if (scheduleJob(jobInstance, job)) {
                started++;
            }
        }
        log.debug("Found {} job instances for {}, started {}", jobInstances.size(), ReflectionUtils.getCleanClassName(job.getClass().getSimpleName()), started);

    }

    public Job getJobByName(String jobName) {
        Job result = null;
        try {
            InitialContext ic = new InitialContext();
            result = (Job) ic.lookup("java:global/" + paramBean.getProperty("opencell.moduleName", "opencell") + "/" + jobName);
        } catch (NamingException e) {
            log.error("Failed to get job by name {}", jobName, e);
        }
        return result;
    }

    public List<Job> getJobs() {
        List<Job> jobs = new ArrayList<>();

        for (List<Class<? extends Job>> jobList : jobClasses.values()) {
            for (Class<? extends Job> jobClass : jobList) {
                Job job = getJobByName(ReflectionUtils.getCleanClassName(jobClass.getSimpleName()));
                jobs.add(job);
            }
        }
        return jobs;
    }

    public List<String> getJobNames() {
        List<String> jobs = new ArrayList<String>();

        for (List<Class<? extends Job>> jobList : jobClasses.values()) {
            for (Class<? extends Job> jobClass : jobList) {
                jobs.add(ReflectionUtils.getCleanClassName(jobClass.getSimpleName()));
            }
        }
        return jobs;
    }

    public List<String> getJobNames(JobCategoryEnum jobCategory) {
        List<String> jobs = new ArrayList<String>();

        if (jobClasses.containsKey(jobCategory)) {
            for (Class<? extends Job> jobClass : jobClasses.get(jobCategory)) {
                jobs.add(ReflectionUtils.getCleanClassName(jobClass.getSimpleName()));
            }
        }
        return jobs;
    }

    @Override
    public void create(JobInstance jobInstance) throws BusinessException {
        super.create(jobInstance);
        jobCacheContainerProvider.addUpdateJobInstance(jobInstance.getId());
        scheduleJob(jobInstance, null);

        clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.create);
    }

    @Override
    public JobInstance update(JobInstance jobInstance) throws BusinessException {
        super.update(jobInstance);
        jobCacheContainerProvider.addUpdateJobInstance(jobInstance.getId());
        scheduleUnscheduleJob(jobInstance);

        clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.update);

        return jobInstance;
    }

    @Override
    public void remove(JobInstance jobInstance) throws BusinessException {

        log.info("remove jobInstance {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId());

        String providerCode = currentUser.getProviderCode();
        if (jobInstance.getId() == null) {
            log.info("removing jobInstance entity with null id, something is wrong");

        } else if (jobTimers.containsKey(new CacheKeyLong(providerCode, jobInstance.getId()))) {
            try {
                Timer timer = jobTimers.get(new CacheKeyLong(providerCode, jobInstance.getId()));
                timer.cancel();
            } catch (Exception ex) {
                log.error("cannot cancel timer " + ex);
            }
            jobTimers.remove(new CacheKeyLong(providerCode, jobInstance.getId()));
        } else {
            log.warn("jobInstance timer not found, cannot remove it");
        }
        super.remove(jobInstance);

        jobCacheContainerProvider.removeJobInstance(jobInstance.getId());

        clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.remove);
    }

    @Override
    public JobInstance enable(JobInstance jobInstance) throws BusinessException {
        jobInstance = super.enable(jobInstance);

        log.info("Enabling jobInstance {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId());
        scheduleUnscheduleJob(jobInstance);

        clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.enable);

        return jobInstance;
    }

    @Override
    public JobInstance disable(JobInstance jobInstance) throws BusinessException {
        jobInstance = super.disable(jobInstance);

        log.info("Disabling jobInstance {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId());
        scheduleUnscheduleJob(jobInstance);

        clusterEventPublisher.publishEvent(jobInstance, CrudActionEnum.disable);

        return jobInstance;
    }

    private void unscheduleJob(Long jobInstanceId) {

        String providerCode = currentUser.getProviderCode();

        if (jobInstanceId != null && jobTimers.containsKey(new CacheKeyLong(providerCode, jobInstanceId))) {
            try {
                Timer timer = jobTimers.get(new CacheKeyLong(providerCode, jobInstanceId));
                timer.cancel();
                jobTimers.remove(new CacheKeyLong(providerCode, jobInstanceId));
                log.info("Cancelled timer id={}", jobInstanceId);

            } catch (Exception ex) {
                log.error("Failed to cancel timer id={}", jobInstanceId, ex);
            }
        }
    }

    /**
     * Schedule a job
     * 
     * @param jobInstance Job instance to schedule
     * @param job Job type. Will be looked up from job instance properties if not provided.
     * @return True if job instance was scheduled
     */
    private boolean scheduleJob(JobInstance jobInstance, Job job) {

        String currentNode = EjbUtils.getCurrentClusterNode();

        if (jobInstance.isActive() && jobInstance.getTimerEntity() != null && jobInstance.isRunnableOnNode(currentNode)) {
            if (job == null) {
                job = getJobByName(jobInstance.getJobTemplate());
            }

            ScheduleExpression scheduleExpression = getScheduleExpression(jobInstance.getTimerEntity());
            log.info("Scheduling job {} of type {} for {}", jobInstance.getCode(), jobInstance.getJobTemplate(), scheduleExpression);

            // detach(jobInstance);
            jobTimers.put(new CacheKeyLong(currentUser.getProviderCode(), jobInstance.getId()), job.createTimer(scheduleExpression, jobInstance));
            return true;

        } else {
            log.info("Job {} of type {} is inactive, has no timer or is not destined to run on node {} and will not be scheduled", jobInstance.getCode(), jobInstance.getJobTemplate(), currentNode);
        }

        return false;
    }

    /**
     * Reschedule a job
     * 
     * @param jobInstance Job instance to reschedule
     */
    private void scheduleUnscheduleJob(JobInstance jobInstance) {

        unscheduleJob(jobInstance.getId());
        scheduleJob(jobInstance, null);
    }

    /**
     * Reschedule a job.
     * 
     * @param jobInstanceId id of job instance need to be scheduled.
     */
    public void scheduleUnscheduleJob(Long jobInstanceId) {

        JobInstance jobInstance = findById(jobInstanceId, Arrays.asList("timerEntity"));
        if (jobInstance == null) {
            unscheduleJob(jobInstanceId);
        } else {
            scheduleUnscheduleJob(jobInstance);
        }
    }

    private ScheduleExpression getScheduleExpression(TimerEntity timerEntity) {
        ScheduleExpression expression = new ScheduleExpression();
        expression.dayOfMonth(timerEntity.getDayOfMonth());
        expression.dayOfWeek(timerEntity.getDayOfWeek());
        expression.end(timerEntity.getEnd());
        expression.hour(timerEntity.getHour());
        expression.minute(timerEntity.getMinute());
        expression.month(timerEntity.getMonth());
        expression.second(timerEntity.getSecond());
        expression.start(timerEntity.getStart());
        expression.year(timerEntity.getYear());
        return expression;
    }

    /**
     * Synchronize definition of custom field templates specified in Job class to those found in DB. Register in DB if was missing.
     * 
     * @param jobInstance Job instance to synchronize custom fields for
     */
    public void createMissingCustomFieldTemplates(JobInstance jobInstance) {

        if (jobInstance.getJobTemplate() == null) {
            return;
        }

        // Get job definition and custom field templates defined in a job
        Job job = getJobByName(jobInstance.getJobTemplate());
        Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();

        // Create missing custom field templates if needed
        Collection<CustomFieldTemplate> jobTemplatesFromJob = null;
        if (jobCustomFields == null) {
            jobTemplatesFromJob = new ArrayList<CustomFieldTemplate>();
        } else {
            jobTemplatesFromJob = jobCustomFields.values();
        }

        try {
            customFieldTemplateService.createMissingTemplates((ICustomFieldEntity) jobInstance, jobTemplatesFromJob);
        } catch (BusinessException e) {
            log.error("Failed to create missing custom field templates", e);
        }
    }

    /**
     * Get a list of job instances of a given job template
     * 
     * @param jobTemplate Job template
     * @return A list of jobInstances
     */
    public List<JobInstance> listByJobType(String jobTemplate) {

        return getEntityManager().createNamedQuery("JobInstance.listByTemplate", JobInstance.class).setParameter("jobTemplate", jobTemplate).getResultList();
    }
}