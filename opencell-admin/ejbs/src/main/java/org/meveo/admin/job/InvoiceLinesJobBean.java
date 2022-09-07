package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingRunStatusEnum.CREATING_INVOICE_LINES;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration.DateAggregationOption;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BasicStatistics;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

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
    private IteratorBasedJobProcessing iteratorBasedJobProcessing;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;
    
    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());
        try {
            List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_billingRun");
            boolean aggregationPerUnitPrice = (Boolean) getParamOrCFValue(jobInstance, InvoiceLinesJob.INVOICE_LINES_AGGREGATION_PER_UNIT_PRICE, false);
            DateAggregationOption dateAggregationOptions = (DateAggregationOption) DateAggregationOption.valueOf((String)getParamOrCFValue(jobInstance, InvoiceLinesJob.INVOICE_LINES_IL_DATE_AGGREGATION_OPTIONS, "MONTH_OF_USAGE_DATE"));

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
                billingRuns.stream().filter(billingRun -> billingRun.isExceptionalBR())
                        .forEach(this::addExceptionalBillingRunData);
                long excludedBRCount = validateBRList(billingRuns, result);
                result.setNbItemsProcessedWithError(excludedBRCount);
                if (excludedBRCount == billingRuns.size()) {
                    result.registerError("No valid billing run with status = NEW found");
                } else {
                    AggregationConfiguration aggregationConfiguration = new AggregationConfiguration(appProvider.isEntreprise(), aggregationPerUnitPrice, dateAggregationOptions);
                    for(BillingRun billingRun : billingRuns) {
                        billingRunExtensionService.updateBillingRun(billingRun.getId(),
                                null, null, CREATING_INVOICE_LINES, null);
                        List<? extends IBillableEntity> billableEntities = billingRunService.getEntitiesToInvoice(billingRun);
                        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
						if (nbRuns == -1) {
							nbRuns = (long) Runtime.getRuntime().availableProcessors();
						}
                        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
                        BasicStatistics basicStatistics = new BasicStatistics();
                        BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity, jobResult) -> invoiceLinesService.createInvoiceLines(result, aggregationConfiguration, billingRun, billableEntity, basicStatistics);
                        iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator<>((Collection<IBillableEntity>) billableEntities), task, null, null, nbRuns, waitingMillis, true, jobInstance.getJobSpeed(),true);
                        billingRunExtensionService.updateBillingRunStatistics(billingRun, basicStatistics, billableEntities.size(), INVOICE_LINES_CREATED);
            		    result.setNbItemsCorrectlyProcessed(basicStatistics.getCount());
                    }
                }
            }
        } catch(BusinessException exception) {
            result.registerError(exception.getMessage());
            log.error(format("Failed to run invoice lines job: %s", exception));
        }
    }

    private void addExceptionalBillingRunData(BillingRun billingRun) {
        QueryBuilder queryBuilder = invoiceLinesService.fromFilters(billingRun.getFilters());
        List<RatedTransaction> ratedTransactions = queryBuilder.getQuery(ratedTransactionService.getEntityManager()).getResultList();
        billingRun.setExceptionalRTIds(ratedTransactions
                .stream().filter(rt -> (rt.getStatus() == RatedTransactionStatusEnum.OPEN && rt.getBillingRun() == null))
                .map(rt -> rt.getId()).collect(toList()));
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