package org.meveo.admin.job;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICES_GENERRATED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREVALIDATED;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;

@Stateless
public class InvoicingJobV2Bean extends BaseJobBean {

    @Inject
    private Logger log;
    @Inject
    private BillingRunService billingRunService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running InvoiceSplitJob with parameter={}", jobInstance.getParametres());
        try {
            List<EntityReferenceWrapper> billingRunWrappers =
                    (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_billingRun");
            List<Long> billingRunIds = billingRunWrappers != null ? extractBRIds(billingRunWrappers) : emptyList();
            Map<String, Object> filters = new HashedMap();
            if (billingRunIds.isEmpty()) {
                filters.put("status", PREVALIDATED);
            } else {
                filters.put("inList id", billingRunIds);
            }
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
            List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);
            if (billingRuns.isEmpty()) {
                List<String> errors = List.of("No valid billing run with status=PREVALIDATED found");
                result.setErrors(errors);
            } else {
                validateBRList(billingRuns, result);
                for (BillingRun billingRun : billingRuns) {
                    billingRunService.createAggregatesAndInvoiceWithIl(billingRun, 1, 0, jobInstance.getId());
                    billingRunService.validateBillingRun(billingRun, INVOICES_GENERRATED);
                }
                result.setNbItemsCorrectlyProcessed(billingRuns.size());
            }
        } catch (Exception exception) {
            result.registerError(exception.getMessage());
            log.error(format("Failed to run invoice lines job: %s", exception));
        }
    }

    private List<Long> extractBRIds(List<EntityReferenceWrapper> billingRunWrappers) {
        return billingRunWrappers.stream()
                    .map(br -> Long.valueOf(br.getCode().split("/")[0]))
                    .collect(toList());
    }

    private void validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream()
                                            .filter(br -> br.getStatus() != PREVALIDATED)
                                            .collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored. " +
                                        "Only Billing runs with status=PREVALIDATED can be processed", br.getId())));
        result.setNbItemsProcessedWithWarning(excludedBRs.size());
        billingRuns.removeAll(excludedBRs);
    }

}