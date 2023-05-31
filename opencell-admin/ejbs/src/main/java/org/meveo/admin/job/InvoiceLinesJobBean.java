package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.service.billing.impl.BasicStatistics;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineAggregationService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.job.Job;

@Stateless
public class InvoiceLinesJobBean extends IteratorBasedJobBean<Object[]> {

    private static final long serialVersionUID = -1318477921039388503L;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceLineService invoiceLinesService;

    @Inject
    private InvoiceLineAggregationService invoiceLineAggregationService;

    public static final String FIELD_PRIORITY_SORT = "billingCycle.priority, auditable.created";

    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private Map<Long, BillingRun> billingRunsById = new HashMap<Long, BillingRun>();
    private AggregationConfiguration aggregationConfiguration;

    private boolean hasMore = false;
    private StatelessSession statelessSession;
    private ScrollableResults scrollableResults;

    private Long maxId = null;
    private Long nrOfRecords = null;

    /**
     * Aggregation query mapped by Billing run ID
     */
    private Map<Long, String> aggregationQueryByBr = new HashMap<Long, String>();

    /**
     * Statistics mapped by Billing run ID
     */
    private Map<Long, BasicStatistics> statsByBr = new HashMap<Long, BasicStatistics>();

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::createInvoiceLines, null, this::hasMore, this::closeResultset, this::closeBillingRuns);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<Object[]>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_billingRun");
        boolean aggregationPerUnitPrice = (Boolean) getParamOrCFValue(jobInstance, InvoiceLinesJob.INVOICE_LINES_AGGREGATION_PER_UNIT_PRICE, false);
        DateAggregationOption dateAggregationOptions = (DateAggregationOption) DateAggregationOption
            .valueOf((String) getParamOrCFValue(jobInstance, InvoiceLinesJob.INVOICE_LINES_IL_DATE_AGGREGATION_OPTIONS, "MONTH_OF_USAGE_DATE"));

        List<Long> billingRunIds = billingRunWrappers != null ? billingRunWrappers.stream().map(br -> valueOf(br.getCode().split("/")[0])).collect(toList()) : emptyList();
        // look for billing runs with status NEW or OPEN (case of incrementalInvoiceLines)
        List<BillingRunStatusEnum> billingRunStatus = Arrays.asList(BillingRunStatusEnum.NEW, BillingRunStatusEnum.OPEN);
        Map<String, Object> filters = new HashedMap();
        if (billingRunIds.isEmpty()) {
            filters.put("inList status", billingRunStatus);
        } else {
            filters.put("inList id", billingRunIds);
        }
        PaginationConfiguration pagination = new PaginationConfiguration(null, null, filters, null, Arrays.asList("billingCycle"), FIELD_PRIORITY_SORT, SortOrder.ASCENDING);

        List<BillingRun> billingRuns = billingRunService.list(pagination);
        if (billingRuns == null || billingRuns.isEmpty()) {
            return Optional.empty();
        }

        long excludedBRCount = validateBRList(billingRuns, jobExecutionResult);
        if (excludedBRCount == billingRuns.size()) {
            jobExecutionResult.registerError("No valid billing run with status = NEW found");
            return Optional.empty();
        }

        EntityManager em = emWrapper.getEntityManager();

        aggregationConfiguration = new AggregationConfiguration(appProvider.isEntreprise(), aggregationPerUnitPrice, dateAggregationOptions);
        for (BillingRun billingRun : billingRuns) {
            String query = invoiceLineAggregationService.getAggregationQuery(aggregationConfiguration, billingRun, BillingAccount.class);
            aggregationQueryByBr.put(billingRun.getId(), query);
            statsByBr.put(billingRun.getId(), new BasicStatistics());
            em.detach(billingRun);
            billingRunsById.put(billingRun.getId(), billingRun);

            // set status of billing run as CREATING_INVOICE_LINES, i.e. it indicates that the invoice line job is running
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.CREATING_INVOICE_LINES, null);

        }

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 10000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        // Number of Rated transactions to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("invoiceLinesJob.processNrInJobRun", 4000000);

        Object[] convertSummary = getConvertToILsSummary(billingRuns, jobExecutionResult);

        nrOfRecords = (Long) convertSummary[0];
        maxId = (Long) convertSummary[1];

        if (nrOfRecords.intValue() == 0) {
            return Optional.empty();
        }

        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();
        scrollableResults = getQueryBaBrIds(billingRuns, statelessSession, maxId).setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

        hasMore = nrOfRecords >= processNrInJobRun;

        return Optional.of(new SynchronizedIterator<Object[]>(scrollableResults, nrOfRecords.intValue(), 2));
    }

    private void createInvoiceLines(Object[] baIdBrId, JobExecutionResultImpl jobExecutionResult) {

        Long baId = (Long) baIdBrId[0];
        Long brId = ((Integer) baIdBrId[1]).longValue();

        BillingRun br = billingRunsById.get(brId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baId", baId);
        params.put("firstTransactionDate", new Date(0));
        if (br.getLastTransactionDate() != null) {
            params.put("lastTransactionDate", br.getLastTransactionDate());
        }
        params.put("invoiceUpToDate", br.getInvoiceDate());

        List<Map<String, Object>> aggregatedInfo = ratedTransactionService.getSelectQueryAsMap(aggregationQueryByBr.get(brId), params);

        BasicStatistics ilBasicStatistics = invoiceLinesService.createInvoiceLines(aggregatedInfo, aggregationConfiguration, jobExecutionResult, br);
        ilBasicStatistics.setCount(1);

        BasicStatistics aggregatedStats = statsByBr.get(brId);
        aggregatedStats.append(ilBasicStatistics);
    }

//    /**
//     * @param waitingMillis
//     * @param jobInstance
//     * @param nbRuns
//     * @param jobInstance
//     * @param nbRuns
//     */
//    private void assignAccountingArticleIfMissingInRTs(JobExecutionResultImpl result, List<? extends IBillableEntity> billableEntities, Long maxInvoiceLinesPerTransaction, Long waitingMillis, JobInstance jobInstance,
//            Long nbRuns) {
//        BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity, jobResult) -> updateRTAccountingArticle(result, billableEntity, maxInvoiceLinesPerTransaction);
//        iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator<>((Collection<IBillableEntity>) billableEntities), task, null, null, nbRuns, waitingMillis, true, jobInstance.getJobSpeed(), true);
//    }
//
//    /**
//     * @param result
//     * @param billableEntity
//     * @param maxInvoiceLinesPerTransaction
//     * @return
//     */
//    private void updateRTAccountingArticle(JobExecutionResultImpl result, IBillableEntity billableEntity, Long maxInvoiceLinesPerTransaction) {
//        if (maxInvoiceLinesPerTransaction == null || maxInvoiceLinesPerTransaction < 1) {
//            ratedTransactionService.calculateAccountingArticle(result, billableEntity, null, null);
//        } else {
//            int index = 0;
//            int count = maxInvoiceLinesPerTransaction.intValue();
//            while (count >= maxInvoiceLinesPerTransaction) {
//                count = ratedTransactionService.calculateAccountingArticle(result, billableEntity, maxInvoiceLinesPerTransaction.intValue(), index++);
//            }
//        }
//    }

    private long validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream().filter(br -> br.getStatus() != BillingRunStatusEnum.NEW && (br.getStatus() != BillingRunStatusEnum.OPEN || !br.getIncrementalInvoiceLines())).collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored", br.getId())));
        billingRuns.removeAll(excludedBRs);
        return excludedBRs.size();
    }

    private boolean hasMore(JobInstance jobInstance) {
        return hasMore;
    }

    /**
     * Close data resultset
     * 
     * @param jobExecutionResult Job execution result
     */
    private void closeResultset(JobExecutionResultImpl jobExecutionResult) {
        scrollableResults.close();
        statelessSession.close();
    }

    /**
     * Change Billing run status
     * 
     * @param jobExecutionResult Job execution result
     */
    private void closeBillingRuns(JobExecutionResultImpl jobExecutionResult) {

        if (jobExecutionResult.getJobLauncherEnum() != JobLauncherEnum.WORKER) {

            for (BillingRun billingRun : billingRunsById.values()) {

                BasicStatistics stats = statsByBr.get(billingRun.getId());

                // in case of incrementalInvoiceLines, update status of billing run as OPEN, and ready to update
                // existing invoice lines with new upcoming RTs
                if (billingRun.getIncrementalInvoiceLines()) {
                    billingRunExtensionService.updateBillingRunStatistics(billingRun.getId(), stats, stats.getCount(), BillingRunStatusEnum.OPEN);
                }
                // otherwise, update directly status of billing run as INVOICE_LINES_CREATED
                else {
                    billingRunExtensionService.updateBillingRunStatistics(billingRun.getId(), stats, stats.getCount(),
                        jobExecutionResult.getStatus() != JobExecutionResultStatusEnum.CANCELLED ? BillingRunStatusEnum.INVOICE_LINES_CREATED : BillingRunStatusEnum.NEW);
                }

                billingRunService.updateBillingRunJobExecution(billingRun.getId(), jobExecutionResult);
            }
        }
    }

    private Long[] getConvertToILsSummary(List<BillingRun> billingRuns, JobExecutionResultImpl jobExecutionResult) {

        EntityManager em = emWrapper.getEntityManager();
        maxId = em.createQuery("select max(id) from RatedTransaction where status='OPEN'", Long.class).getSingleResult();
        Long accountCountTotal = 0L;

        String unionSql = null;
        Map<String, Object> params = new HashMap<String, Object>();

        int i = 0;
        for (BillingRun billingRun : billingRuns) {

            Map<String, Object> filters = billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getFilters() : billingRun.getFilters();
            if (filters == null && billingRun.getBillingCycle() != null) {
                filters = new HashMap<>();
                filters.put("billingAccount.billingCycle.id", billingRun.getBillingCycle().getId());
            }
            if (filters == null) {
                throw new BusinessException("No filter found for billingRun " + billingRun.getId());
            }
            filters.put("status", RatedTransactionStatusEnum.OPEN.toString());

            QueryBuilder queryBuilder = ratedTransactionService.getQueryFromFilters(filters, "count(distinct a.billingAccount.id)", null, false);

            Long accountCountByBr = (Long) queryBuilder.getQuery(em).getSingleResult();
            accountCountTotal = accountCountTotal + accountCountByBr;

            jobExecutionResult.addReport("Billing run #" + billingRun.getId() + ": will process " + accountCountByBr + " accounts");

        }

        return new Long[] { accountCountTotal, maxId };
    }

    private org.hibernate.query.Query getQueryBaBrIds(List<BillingRun> billingRuns, StatelessSession statelessSession, Long maxId) {

        String unionSql = null;
        Map<String, Object> params = new HashMap<String, Object>();

        int i = 0;
        for (BillingRun billingRun : billingRuns) {

            Map<String, Object> filters = billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getFilters() : billingRun.getFilters();
            if (filters == null && billingRun.getBillingCycle() != null) {
                filters = new HashMap<>();
                filters.put("billingAccount.billingCycle.id", billingRun.getBillingCycle().getId());
            }
            if (filters == null) {
                throw new BusinessException("No filter found for billingRun " + billingRun.getId());
            }
            filters.put("status", RatedTransactionStatusEnum.OPEN.toString());
            filters.put("toRangeInclusive id", maxId);

            QueryBuilder queryBuilder = ratedTransactionService.getQueryFromFilters(filters, "distinct a.billingAccount.id, " + billingRun.getId(), null, false);

            String sql = queryBuilder.getSqlString();
            for (Entry<String, Object> paramInfo : queryBuilder.getParams().entrySet()) {
                sql = sql.replaceAll(":" + paramInfo.getKey(), ":" + paramInfo.getKey() + "_" + i);
                params.put(paramInfo.getKey() + "_" + i, paramInfo.getValue());
            }

            if (i == 0) {
                unionSql = sql;
            } else {
                unionSql = unionSql + " UNION " + sql;
            }

            i++;
        }

        org.hibernate.query.Query query = statelessSession.createQuery(unionSql);
        for (Entry<String, Object> paramInfo : params.entrySet()) {
            query.setParameter(paramInfo.getKey(), paramInfo.getValue());
        }
        return query;

        // @NamedQuery(name="RatedTransaction.getDistinctBaAndBr",query="SELECT k.baId, :brId from (SELECT rt.billingAccount.id as baId from RatedTransaction rt where rt.status='OPEN' and
        // rt.billingAccount.billingCycle.id in :bcIds and rt.id<=:maxId) k")
    }
}