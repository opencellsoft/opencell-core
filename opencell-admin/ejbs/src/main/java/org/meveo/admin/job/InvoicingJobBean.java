package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author HORRI Khalid
 * @lastModifiedVersion 5.4
 */
@Stateless
public class InvoicingJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {
            List<BillingRun> billingRuns = getBillingRuns(this.getParamOrCFValue(jobInstance, "billingRuns"));

            log.info("BillingRuns to process={}", billingRuns.size());
            result.setNbItemsToProcess(billingRuns.size());

            for (BillingRun billingRun : billingRuns) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                try {
                    billingRunService.detach(billingRun);
                    billingRunService.validate(billingRun, nbRuns.longValue(), waitingMillis.longValue(), result.getJobInstance().getId(), result);
                    result.registerSucces();
                } catch (Exception e) {
                    log.error("Failed to run invoicing", e);
                    result.registerError(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to run invoicing", e);
        }

        log.info("end Execute");
    }

    /**
     * Get Billing runs to process
     *
     * @param billingRunsCF the billing runs getting from the custom field
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<BillingRun> getBillingRuns(Object billingRunsCF) {
        List<EntityReferenceWrapper> brList = (List<EntityReferenceWrapper>) billingRunsCF;
        List<BillingRun> billingRuns = new ArrayList<>();
        if (brList != null && !brList.isEmpty()) {
            List<Long> ids = brList.stream().map(br -> {
                String compositeCode = br.getCode();
                if (compositeCode == null) {
                    return null;
                }
                return Long.valueOf(compositeCode.split("/")[0]);
            }).collect(Collectors.toList());
            Map<String, Object> filters = new HashedMap();
            filters.put("inList id", ids);
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
            billingRuns = billingRunService.list(paginationConfiguration);
        } else {
            if (billingRuns == null || billingRuns.isEmpty()) {
                billingRuns = billingRunService.listByNamedQuery("BillingRun.getForInvoicing");
            }
        }
        return billingRuns;
    }
}