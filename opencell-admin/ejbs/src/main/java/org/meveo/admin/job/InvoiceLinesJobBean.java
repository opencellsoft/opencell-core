package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingRunStatusEnum.CREATING_INVOICE_LINES;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BasicStatistics;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;

@Stateless
public class InvoiceLinesJobBean extends BaseJobBean {

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceLineService invoiceLinesService;
    
    @Inject
    private IteratorBasedJobProcessing iteratorBasedJobProcessing;
    
    public static final String FIELD_PRIORITY_SORT = "billingCycle.priority, auditable.created";
    
    @Inject
    private BillingRunExtensionService billingRunExtensionService;
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug(" Running for with parameter={}", jobInstance.getParametres());
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
            PaginationConfiguration pagination = new PaginationConfiguration(null, null, filters, null, Arrays.asList("billingCycle"), FIELD_PRIORITY_SORT, SortOrder.ASCENDING);
            List<BillingRun> billingRuns = billingRunService.list(pagination);
            if(billingRuns != null && !billingRuns.isEmpty()) {
                billingRuns.stream().filter(BillingRun::isExceptionalBR)
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
                        List<? extends IBillableEntity> billableEntities = billingRunService.getEntitiesToInvoice(billingRun, true);
                        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
						if (nbRuns == -1) {
							nbRuns = (long) Runtime.getRuntime().availableProcessors();
						}
						log.info(" ============ CREATING_INVOICE_LINES, DISPATCHING nbRuns/billableEntities: "+nbRuns+"/"+(billableEntities!=null?billableEntities.size():0));
                        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
                        Long maxInvoiceLinesPerTransaction = (Long) this.getParamOrCFValue(jobInstance, "maxInvoiceLinesPerTransaction", 10000L);
                        BasicStatistics basicStatistics = new BasicStatistics();
                        assignAccountingArticleIfMissingInRTs(result, billableEntities, maxInvoiceLinesPerTransaction, waitingMillis, jobInstance, nbRuns);
                        BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity, jobResult) -> invoiceLinesService.createInvoiceLines(result, aggregationConfiguration, billingRun, billableEntity, basicStatistics);
                        iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator<>((Collection<IBillableEntity>) billableEntities), task, null, null, nbRuns, waitingMillis, true, jobInstance.getJobSpeed(),true);
                        billingRunService.update(billingRun);
                        billingRunExtensionService.updateBillingRunStatistics(billingRun, basicStatistics, billableEntities.size(), INVOICE_LINES_CREATED);
            		    result.setNbItemsCorrectlyProcessed(basicStatistics.getCount());
                        billingRunService.updateBillingRunJobExecution(billingRun, result);

                    }
                }
            }
        } catch(BusinessException exception) {
            result.registerError(exception.getMessage());
            log.error(format("Failed to run invoice lines job: %s", exception));
        }
    }
    
    
    /**
         * @param waitingMillis
    	 * @param waitingMillis
         * @param jobInstance 
    	 * @param nbRuns
         * @param jobInstance
         * @param nbRuns 
    	 */
    	private void assignAccountingArticleIfMissingInRTs(JobExecutionResultImpl result, List<? extends IBillableEntity> billableEntities,
    			Long maxInvoiceLinesPerTransaction, Long waitingMillis, JobInstance jobInstance, Long nbRuns) {
    		BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity, jobResult) -> updateRTAccountingArticle(result, billableEntity, maxInvoiceLinesPerTransaction);
    		iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator<>((Collection<IBillableEntity>) billableEntities), task, null, null, nbRuns, waitingMillis, true, jobInstance.getJobSpeed(), true);
    	}
    
    	/**
    	 * @param result
    	 * @param billableEntity
    	 * @param maxInvoiceLinesPerTransaction
    	 * @return
    	 */
    	private void updateRTAccountingArticle(JobExecutionResultImpl result, IBillableEntity billableEntity, Long maxInvoiceLinesPerTransaction) {
    		if(maxInvoiceLinesPerTransaction==null || maxInvoiceLinesPerTransaction < 1) {
    			ratedTransactionService.calculateAccountingArticle(result, billableEntity, null, null);
    		} else {
    			int index=0;
    			int count = maxInvoiceLinesPerTransaction.intValue();
    			while(count >= maxInvoiceLinesPerTransaction){
    				count = ratedTransactionService.calculateAccountingArticle(result, billableEntity, maxInvoiceLinesPerTransaction.intValue(), index++);
   			}
    		}
    	}

    private void addExceptionalBillingRunData(BillingRun billingRun) {
        Map<String, Object> filters = billingRun.getFilters();
        filters.put("status", RatedTransactionStatusEnum.OPEN.toString());
		QueryBuilder queryBuilder = invoiceLinesService.fromFilters(filters, true);
        List<Object[]> resultList = queryBuilder.getQuery(ratedTransactionService.getEntityManager()).getResultList();
		billingRun.setExceptionalRTIds(resultList.stream().map(x->((Long)x[0])).collect(Collectors.toList()));
		billingRun.setExceptionalBAIds(resultList.stream().map(x->((Long)x[1])).distinct().collect(Collectors.toList()));
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
