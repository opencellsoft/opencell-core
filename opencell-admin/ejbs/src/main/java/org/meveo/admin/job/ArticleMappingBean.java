package org.meveo.admin.job;

import org.meveo.admin.async.FiltringJobAsync;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionErrorService;
import org.meveo.service.script.ScriptInstanceService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Stateless
public class ArticleMappingBean extends BaseJobBean {
    @Inject
    private Logger log;

    @Inject
    private FilterService filterService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private FiltringJobAsync filtringJobAsync;

    @Inject
    private BeanManager manager;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private JobExecutionErrorService jobExecutionErrorService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        ratedTransactionService.get
    }
}
