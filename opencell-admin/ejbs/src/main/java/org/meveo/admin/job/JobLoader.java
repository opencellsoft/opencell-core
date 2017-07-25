package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;

@Startup
@Singleton
public class JobLoader implements Serializable {

    private static final long serialVersionUID = -622043106026256817L;

    @Inject
    private JobInstanceService jobInstanceService;

//    @Resource
//    protected TimerService timerService;


    @PostConstruct
    public void init() {

//        cleanAllTimers();

        Set<Class<?>> classes = ReflectionUtils.getSubclasses(Job.class);
        for (Class<?> jobClass : classes) {

            Job job = (Job) EjbUtils.getServiceInterface(jobClass.getSimpleName());

            jobInstanceService.registerJob(job);
        }
    }

//    /*
//     * Clear timers work on a bean that scheduler timer, and JobLoader does not schedule anything.
//     */
//    private void cleanAllTimers() {
//        // timerService.getAllTimers() work on singleton bean only and job classes are stateless beans, so disabled for now
//
//        Collection<Timer> alltimers = timerService.getTimers();
//        log.info("Canceling job timers");
//
//        for (Timer timer : alltimers) {
//            try {
//                if (timer.getInfo() instanceof JobInstance) {
//                    timer.cancel();
//                }
//            } catch (Exception e) {
//                log.error("Failed to cancel timer {} ", timer.getHandle(), e);
//            }
//        }
//    }
}
