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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.PersistenceService;

@Stateless
public class JobInstanceService extends PersistenceService<JobInstance> {

    private static List<Class<? extends Job>> jobClasses = new ArrayList<>();
    private static Map<Long, Timer> jobTimers = new HashMap<Long, Timer>();
    protected static List<Long> runningJobs = new ArrayList<Long>();

    private static ParamBean paramBean = ParamBean.getInstance();


    /**
     * Used by job instance classes to register themselves to the timer service
     * 
     * @param name unique name in the application, used by the admin to manage timers
     * @param description describe the task realized by the job
     * @param JNDIName used to instanciate the implementation to execute the job (instantiacion class must be a session EJB)
     */
    public void registerJob(Job job) {
        jobClasses.add(job.getClass());
        startTimers(job);
    }

    public void startTimers(Job job) {
        // job.cleanAllTimers();
        @SuppressWarnings("unchecked")
        List<JobInstance> jobInstances = getEntityManager().createQuery("from JobInstance ji JOIN FETCH ji.followingJob where ji.jobTemplate=:jobName")
            .setParameter("jobName", job.getClass().getSimpleName()).getResultList();

        if (jobInstances != null) {
            int started = 0;
            for (JobInstance jobInstance : jobInstances) {
                if (jobInstance.isActive() && jobInstance.getTimerEntity() != null) {
                    ScheduleExpression scheduleExpression = getScheduleExpression(jobInstance.getTimerEntity());
                    detach(jobInstance);
                    jobTimers.put(jobInstance.getId(), job.createTimer(scheduleExpression, jobInstance));
                    started++;
                }
            }
            log.debug("Found {} job instances for {}, started {}", jobInstances.size(), job.getClass().getSimpleName(), started);
        }
    }

    public Job getJobByName(String jobName) {
        Job result = null;
        try {
            InitialContext ic = new InitialContext();
            result = (Job) ic.lookup("java:global/" + paramBean.getProperty("meveo.moduleName", "meveo") + "/" + jobName);
        } catch (NamingException e) {
            log.error("Failed to get job by name {}", jobName, e);
        }
        return result;
    }

    public List<Job> getJobs() {
        List<Job> jobs = new ArrayList<>();

        for (Class<? extends Job> jobClass : jobClasses) {
            Job job = getJobByName(jobClass.getSimpleName());
            jobs.add(job);
        }
        return jobs;
    }

    public List<String> getJobNames() {
        List<String> jobs = new ArrayList<String>();

        for (Class<? extends Job> jobClass : jobClasses) {
            jobs.add(jobClass.getSimpleName());
        }
        return jobs;
    }

    @Override
    public void create(JobInstance jobInstance) throws BusinessException {

        super.create(jobInstance);
        scheduleUnscheduleJob(jobInstance);
    }

    @Override
    public JobInstance update(JobInstance jobInstance) throws BusinessException {

        super.update(jobInstance);
        scheduleUnscheduleJob(jobInstance);

        return jobInstance;
    }

    @Override
    public void remove(JobInstance entity) throws BusinessException {
        log.info("remove jobInstance {}, id={}", entity.getJobTemplate(), entity.getId());
        if (entity.getId() == null) {
            log.info("removing jobInstance entity with null id, something is wrong");
        } else if (jobTimers.containsKey(entity.getId())) {
            try {
                Timer timer = jobTimers.get(entity.getId());
                timer.cancel();
            } catch (Exception ex) {
                log.info("cannot cancel timer " + ex);
            }
            jobTimers.remove(entity.getId());
        } else {
            log.info("jobInstance timer not found, cannot remove it");
        }
        super.remove(entity);
    }

    @Override
    public JobInstance enable(JobInstance jobInstance) throws BusinessException {
        jobInstance = super.enable(jobInstance);

        log.info("Enabling jobInstance {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId());
        scheduleUnscheduleJob(jobInstance);

        return jobInstance;
    }

    @Override
    public JobInstance disable(JobInstance jobInstance) throws BusinessException {
        jobInstance = super.disable(jobInstance);

        log.info("Disabling jobInstance {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId());
        scheduleUnscheduleJob(jobInstance);

        return jobInstance;
    }

    private void scheduleUnscheduleJob(JobInstance jobInstance) {

        if (jobTimers.containsKey(jobInstance.getId())) {
            try {
                Timer timer = jobTimers.get(jobInstance.getId());
                timer.cancel();
                jobTimers.remove(jobInstance.getId());
                log.info("Cancelled timer {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId());
            } catch (Exception ex) {
                log.error("Failed to cancel timer {}, id={}", jobInstance.getJobTemplate(), jobInstance.getId(), ex);
            }
        }

        if (jobInstance.isActive() && jobInstance.getTimerEntity() != null) {
            Job job = getJobByName(jobInstance.getJobTemplate());
            log.info("Scheduling job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate());

            ScheduleExpression scheduleExpression = getScheduleExpression(jobInstance.getTimerEntity());
            detach(jobInstance);
            jobTimers.put(jobInstance.getId(), job.createTimer(scheduleExpression, jobInstance));

        } else {
            log.info("Job {} of type {} is inactive or has no timer and will not be scheduled", jobInstance.getCode(), jobInstance.getJobTemplate());
        }
    }

    private QueryBuilder getFindQuery(PaginationConfiguration configuration) {
        String sql = "select distinct t from JobInstance t";
        QueryBuilder qb = new QueryBuilder(sql);// FIXME: .cacheable(); there is
        // no cacheable in MEVEO
        // QueryBuilder
        qb.addPaginationConfiguration(configuration);
        return qb;
    }

    @SuppressWarnings("unchecked")
    public List<JobInstance> find(PaginationConfiguration configuration) {
        return getFindQuery(configuration).find(getEntityManager());
    }

    public long count(PaginationConfiguration configuration) {
        return getFindQuery(configuration).count(getEntityManager());
    }

    /**
     * Check if a timer, identifier by a timer id, is running
     * 
     * @param timerEntityId Timer entity id
     * @return True if running
     */
    public boolean isJobRunning(Long timerEntityId) {
        return runningJobs.contains(timerEntityId);
    }

    public JobInstance findByCode(String code) {
        // QueryBuilder qb = new QueryBuilder(JobInstance.class, "t");
        QueryBuilder qb = new QueryBuilder(JobInstance.class, "t", Arrays.asList("timerEntity"));
        qb.addCriterion("t.code", "=", code, true);
        try {
            return (JobInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
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
}