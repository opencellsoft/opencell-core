package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.meveo.commons.utils.ParamBean.getInstance;
import static org.meveo.model.billing.BillingRunStatusEnum.CREATING_INVOICE_LINES;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BasicStatistics;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;

@Stateless
public class InvoiceLinesJobBean extends IteratorBasedJobBean {

    @Inject
    private BillingRunService billingRunService;
    
    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceLineService invoiceLinesService;
    
    @Inject
    private IteratorBasedJobProcessing iteratorBasedJobProcessing;
    
    public static final String FIELD_PRIORITY_SORT = "billingCycle.priority, auditable.created";
    
    @Inject
    private BillingRunExtensionService billingRunExtensionService;
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
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
                long excludedBRCount = validateBRList(billingRuns, result);
                result.setNbItemsProcessedWithError(excludedBRCount);
                if (excludedBRCount == billingRuns.size()) {
                    result.registerError("No valid billing run with status = NEW found");
                } else {
                    AggregationConfiguration aggregationConfiguration = new AggregationConfiguration(appProvider.isEntreprise(), aggregationPerUnitPrice, dateAggregationOptions);
                    for(BillingRun billingRun : billingRuns) {
                        billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, CREATING_INVOICE_LINES, null);
                        List<Long> billingAccountsIDs = billingRunService.getBillingAccountsIdsForOpenRTs(billingRun);
                        
                        Long nbRunConfig = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
						final Long nbRuns = nbRunConfig == -1 ? (long) Runtime.getRuntime().availableProcessors() : nbRunConfig;
						log.info(" ============ CREATING_INVOICE_LINES, DISPATCHING nbRuns/billableEntities: "+nbRuns+"/"+(billingAccountsIDs!=null?billingAccountsIDs.size():0));
                        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
                        Long maxInvoiceLinesPerTransaction = (Long) this.getParamOrCFValue(jobInstance, "maxInvoiceLinesPerTransaction", 10000L);
                        BasicStatistics basicStatistics = new BasicStatistics();
                        int billableEntitiesSize=0;
                        final int maxValue = getInstance().getPropertyAsInteger("database.number.of.inlist.limit", billingRunService.SHORT_MAX_VALUE);
                        if (billingAccountsIDs.size() > maxValue) {
                            List<List<Long>> invoiceLineIdsSubList = partition(billingAccountsIDs, maxValue);
                            invoiceLineIdsSubList.forEach(subIdsList -> processInvoiceLinesGeneration(result, jobInstance, aggregationConfiguration,
                            		billingRun, nbRuns, waitingMillis, maxInvoiceLinesPerTransaction, basicStatistics, billableEntitiesSize, subIdsList));
                        } else {
                        	processInvoiceLinesGeneration(result, jobInstance, aggregationConfiguration, billingRun, nbRuns, waitingMillis,
									maxInvoiceLinesPerTransaction, basicStatistics, billableEntitiesSize, billingAccountsIDs);
                        }
                        
                        billingRunExtensionService.updateBillingRunStatistics(billingRun, basicStatistics, billableEntitiesSize, INVOICE_LINES_CREATED);
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

	private int processInvoiceLinesGeneration(JobExecutionResultImpl result, JobInstance jobInstance,
			AggregationConfiguration aggregationConfiguration, BillingRun billingRun, Long nbRuns, Long waitingMillis,
			Long maxInvoiceLinesPerTransaction, BasicStatistics basicStatistics, int billableEntitiesSize, List<Long> billingAccountsIDs) {
		List billableEntitiesList = billingAccountService.findByIds(billingAccountsIDs);
		assignAccountingArticleIfMissingInRTs(result, billableEntitiesList, maxInvoiceLinesPerTransaction, waitingMillis, jobInstance, nbRuns);
		BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity, jobResult) -> invoiceLinesService.createInvoiceLines(result, aggregationConfiguration, billingRun, billableEntity, basicStatistics);
		iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator<>(billableEntitiesList), task, null, null, nbRuns, waitingMillis, true, jobInstance.getJobSpeed(),true);
		billableEntitiesSize+=billingAccountsIDs.size();
		return billableEntitiesSize;
	}
    
    
    /**
    	 * @param waitingMillis
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

    private long validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream()
                .filter(br -> br.getStatus() != NEW)
                .collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored", br.getId())));
        billingRuns.removeAll(excludedBRs);
        return excludedBRs.size();
    }
}
