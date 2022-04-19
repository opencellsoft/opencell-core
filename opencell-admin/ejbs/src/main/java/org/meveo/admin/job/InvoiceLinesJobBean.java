package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.ScrollMode;
import org.hibernate.Session;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration.DateAggregationOption;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BasicStatistics;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.job.Job;
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
    BillingRunExtensionService billingRunExtensionService;

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
                billingRuns.stream().filter(billingRun -> billingRun.isExceptionalBR()).forEach(this::addExceptionalBillingRunData);
                long excludedBRCount = validateBRList(billingRuns, result);
                result.setNbItemsProcessedWithError(excludedBRCount);
                if (excludedBRCount == billingRuns.size()) {
                    result.registerError("No valid billing run with status = NEW found");
                } else {
                    AggregationConfiguration aggregationConfiguration = new AggregationConfiguration(appProvider.isEntreprise(), aggregationPerUnitPrice, dateAggregationOptions);
                    for(BillingRun billingRun : billingRuns) {
                        List<? extends IBillableEntity> billableEntities = billingRunService.getEntitiesToInvoice(billingRun);
                        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
						if (nbRuns == -1) {
							nbRuns = (long) Runtime.getRuntime().availableProcessors();
						}
                        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
                        BasicStatistics basicStatistics = new BasicStatistics();
                        Long maxInvoiceLinesPerTransaction = (Long) this.getParamOrCFValue(jobInstance, "maxInvoiceLinesPerTransaction", null);
                        assignAccountingArticleIfMissingInRTs(result, billableEntities, maxInvoiceLinesPerTransaction, waitingMillis, jobInstance, nbRuns);
                        
                        BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity, jobResult) -> createInvoiceLines(result, aggregationConfiguration, billingRun, billableEntity, basicStatistics,maxInvoiceLinesPerTransaction);
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

    /**
     * This is a correctif step for customers migrating from old versions, as in new opencell process, all RTs must already contain accountingArticle
     * 
	 * @param billingRun
     * @param waitingMillis 
	 * @param waitingMillis
     * @param jobInstance 
	 * @param nbRuns
     * @param jobExecutionResult 
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
	 * @param billingRun
	 * @param billableEntity
	 * @param maxInvoiceLinesPerTransaction
	 * @return
	 */
	private void updateRTAccountingArticle(JobExecutionResultImpl result, IBillableEntity billableEntity, Long maxInvoiceLinesPerTransaction) {
		if(maxInvoiceLinesPerTransaction==null || maxInvoiceLinesPerTransaction < 1) {
			ratedTransactionService.calculateAccountingArticle(result, billableEntity, null, null);
		} else {
			int count = maxInvoiceLinesPerTransaction.intValue();
			while(count >= maxInvoiceLinesPerTransaction){
				count = ratedTransactionService.calculateAccountingArticle(result, billableEntity, maxInvoiceLinesPerTransaction.intValue(), 0);
			}
		}
	}

	/**
	 * @param result
	 * @param aggregationConfiguration
	 * @param billingRun
	 * @param billableEntity
	 * @param basicStatistics
	 * @param maxInvoiceLinesPerTransaction
	 * @param i
	 * @return
	 */
	private void createInvoiceLines(JobExecutionResultImpl result, AggregationConfiguration aggregationConfiguration,
			BillingRun billingRun, IBillableEntity billableEntity, BasicStatistics basicStatistics,
			Long maxInvoiceLinesPerTransaction) {
		if(maxInvoiceLinesPerTransaction==null || maxInvoiceLinesPerTransaction < 1) {
			invoiceLinesService.createInvoiceLines(result, aggregationConfiguration, billingRun, billableEntity, basicStatistics, null, null);
		} else {
			int count = maxInvoiceLinesPerTransaction.intValue();
			while(count >= maxInvoiceLinesPerTransaction){
				count = invoiceLinesService.createInvoiceLines(result, aggregationConfiguration, billingRun, billableEntity, basicStatistics, maxInvoiceLinesPerTransaction.intValue(), 0);
			}
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