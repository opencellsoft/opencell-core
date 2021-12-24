package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration.AggregationOption;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.*;

@Stateless
public class InvoiceLinesJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceLineService invoiceLinesService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;
    
    @Inject
    BillingRunExtensionService billingRunExtensionService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());
        try {
            List<EntityReferenceWrapper> billingRunWrappers =
                    (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_billingRun");
            String aggregationOption = ofNullable((String) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_aggregationOption"))
                    .orElse("NO_AGGREGATION");
            List<Long> billingRunIds = billingRunWrappers != null ? billingRunWrappers.stream()
                    .map(br -> valueOf(br.getCode().split("/")[0]))
                    .collect(toList()) : emptyList();
            Map<String, Object> filters = new HashedMap();
            if (billingRunIds.isEmpty()) {
                filters.put("status", NEW);
            } else {
                filters.put("inList id", billingRunIds);
            }
            List<BillingRun> billingRuns = billingRunService.list(new PaginationConfiguration(filters));
            if(billingRuns != null && !billingRuns.isEmpty()) {
                billingRuns.stream()
                        .filter(billingRun -> billingRun.isExceptionalBR())
                        .forEach(this::addExceptionalBillingRunData);
                long excludedBRCount = validateBRList(billingRuns, result);
                result.setNbItemsProcessedWithError(excludedBRCount);
                if (excludedBRCount == billingRuns.size()) {
                    result.registerError("No valid billing run with status = NEW found");
                } else {
                    AggregationConfiguration aggregationConfiguration = new AggregationConfiguration(appProvider.isEntreprise(),
                            AggregationOption.fromValue(aggregationOption));
                    List<RatedTransaction> ratedTransactions = billingRunService.loadRTsByBillingRuns(billingRuns);
                    List<Long> ratedTransactionIds = ratedTransactions.stream()
                            .map(RatedTransaction::getId)
                            .collect(toList());
                    if (!ratedTransactionIds.isEmpty()) {
                        List<Map<String, Object>> groupedRTs;
                        if (aggregationConfiguration.getAggregationOption() == AggregationOption.NO_AGGREGATION) {
                            groupedRTs = ratedTransactionService.getGroupedRTs(ratedTransactionIds);
                        } else {
                            groupedRTs = ratedTransactionService.getGroupedRTsWithAggregation(ratedTransactionIds);
                        }
                        Map<Long, List<Long>> iLIdsRtIdsCorrespondence
                                = invoiceLinesService.createInvoiceLines(groupedRTs, aggregationConfiguration, result);
                        ratedTransactionService.makeAsProcessed(ratedTransactionIds);
                        ratedTransactionService.linkRTWithInvoiceLine(iLIdsRtIdsCorrespondence);
                        result.setNbItemsCorrectlyProcessed(groupedRTs.size());
                    }
                }
                for(BillingRun billingRun : billingRuns) {
                	  billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null,
                              INVOICE_LINES_CREATED, new Date());
                }
            }
        } catch(BusinessException exception) {
            result.registerError(exception.getMessage());
            log.error(format("Failed to run invoice lines job: %s", exception));
        }
    }

    private void addExceptionalBillingRunData(BillingRun billingRun) {
        QueryBuilder queryBuilder = invoiceLinesService.fromFilters(billingRun.getFilters());
        billingRun.setExceptionalRTIds(queryBuilder.getIdQuery(ratedTransactionService.getEntityManager()).getResultList());
    }

    private long validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream()
                .filter(br -> br.getStatus() != NEW)
                .collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored", br.getId())));
        billingRuns.removeAll(excludedBRs);
        return excludedBRs.size();
    }
}