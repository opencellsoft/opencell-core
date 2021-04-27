package org.meveo.service.job;

import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.meveo.model.jobs.JobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Interceptor to update the Jobs number of threads in realtime
 *
 * @author Mohamed Ali Hammal
 * @since 11.X
 */
public class JobExecutionInterceptor {

    /**
     * class logger
     */
    private static final Logger log = LoggerFactory.getLogger(JobExecutionInterceptor.class);

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    /**
     * Update metrics for Prometheus a method on an entity
     *
     * @param context the method invocation context
     * @return the method result if update is OK
     * @throws Exception if the update failed
     */
    @AroundInvoke
    public Object aroundInvoke(InvocationContext context) throws Exception {
        Object[] params = context.getParameters();

        long numberOfThreads = 0;

        if (params.length == 4) {
            List<Future> futures = (List<Future>) params[3];
            if (futures != null && !futures.isEmpty()) {
                numberOfThreads = futures.size();
            }
        }
        counterInc((JobInstance) params[0], "number_of_Threads", numberOfThreads);

        try {
            return context.proceed();
        } catch (Exception e) {
            log.warn(" update of metrics failed because of : {}", e);
            return null;
        }
    }

    /**
     * Increment counter metric for JobExecution
     *
     * @param value
     * @param name the name of metric
     */
    private void counterInc(JobInstance jobInstance, String name, Long value) {
        Metadata metadata = new MetadataBuilder().withName(name + "_" + jobInstance.getJobTemplate() + "_" + jobInstance.getCode()).reusable().build();
        Tag tgName = new Tag("name", jobInstance.getCode());
        Counter counter = registry.counter(metadata, tgName);
        if (value != null) {
            counter.inc(value - counter.getCount());
        } else {
            counter.inc();
        }
    }

}
