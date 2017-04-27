package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.slf4j.Logger;

@Startup
@Singleton
public class JobLoader implements Serializable {

    private static final long serialVersionUID = -622043106026256817L;

    @Inject
    private JobInstanceService jobInstanceService;

    @Resource
    protected TimerService timerService;

    @Inject
    private Logger log;

    @PostConstruct
    public void init() {

        cleanAllTimers();

        Set<Class<?>> classes = ReflectionUtils.getSubclasses(Job.class);
        for (Class<?> jobClass : classes) {

            Job job = (Job) EjbUtils.getServiceInterface(jobClass.getSimpleName());

            jobInstanceService.registerJob(job);
        }
    }

    private void cleanAllTimers() {
        // timerService.getAllTimers() work on singleton bean only, so disabled for now

        // Collection<Timer> alltimers = timerService.getAllTimers();
        // log.info("Canceling job timers");
        //
        // for (Timer timer : alltimers) {
        // try {
        // if (timer.getInfo() instanceof JobInstance) {
        // timer.cancel();
        // }
        // } catch (Exception e) {
        // log.error("Failed to cancel timer {} ", timer.getHandle(), e);
        // }
        // }
    }
}
