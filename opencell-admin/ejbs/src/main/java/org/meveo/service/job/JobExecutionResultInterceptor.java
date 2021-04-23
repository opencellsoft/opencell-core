package org.meveo.service.job;

import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * Interceptor to update the Jobs informations in realtime
 *
 * @author Mohamed Ali Hammal
 * @since 11.X
 */
public class JobExecutionResultInterceptor {

    /**
     * class logger
     */
    private static final Logger log = LoggerFactory.getLogger(JobExecutionResultInterceptor.class);

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    /**
     * Update metrics for Prometheus
     * a method on an entity
     *
     * @param context the method invocation context
     * @return the method result if update is OK
     * @throws Exception if the update failed
     */
    @AroundInvoke
    public Object aroundInvoke(InvocationContext context) throws Exception {
        Object[] entity = context.getParameters();
        JobExecutionResultImpl Result = (JobExecutionResultImpl) entity[0];

        // Update the counters
        long NBofOKs = Result.getNbItemsCorrectlyProcessed();
        long NBofKOs = Result.getNbItemsProcessedWithError();
        long NBofRemainingValues = (Result.getNbItemsToProcess() - NBofOKs - NBofKOs);



        counterInc(Result, "number_of_OKs",NBofOKs);
        counterInc(Result, "number_of_KOs",NBofKOs);
        counterInc(Result, "number_of_Remaining_Items",NBofRemainingValues);
        Long nbThreads = -1L;

        if(Result.getJobInstance().getCfValue("nbRuns") != null ) {
            nbThreads = (Long) Result.getJobInstance().getCfValue("nbRuns");
        }
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        counterInc(Result, "number_of_Threads", nbThreads);


        try{
            return context.proceed();
        } catch(Exception e) {
            log.warn(" update of metrics failed because of : {}", e);
            return null;
        }
    }

    /**
     * Increment counter metric for JobExecutionResultImpl
     *
     * @param value
     * @param name the name of metric
     */
    private void counterInc(JobExecutionResultImpl jobExecutionResultImpl, String name, Long value) {
        JobInstance jobInstance = jobExecutionResultImpl.getJobInstance();
        Metadata metadata = new MetadataBuilder().withName(name + "_" + jobInstance.getJobTemplate()).reusable().build();
        Tag tgName = new Tag("name", jobInstance.getCode());
        Counter counter = registry.counter(metadata, tgName);
        counter.inc(counter.getCount() * -1);
        if (value != null) {
            counter.inc(value);
        } else {
            counter.inc();
        }
    }

}
