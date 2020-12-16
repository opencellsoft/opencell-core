package org.meveo.admin.job;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.FiltringJobAsync;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.LanguageIsoDto;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private BillingRunService billingRunService;

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

        List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>)this.getParamOrCFValue(jobInstance, "billingRuns");
        if(billingRunWrappers != null){
            List<Long> billingRunIds = billingRunWrappers.stream()
                    .map(br -> Long.valueOf(br.getCode().split("/")[0]))
                    .collect(Collectors.toList());
            Map<String, Object> filters = new HashedMap();
            filters.put("inList id", billingRunIds);
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
            List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);
        }

    }
}
