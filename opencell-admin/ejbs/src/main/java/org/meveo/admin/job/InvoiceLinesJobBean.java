package org.meveo.admin.job;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedIteratorGrouped;
import org.meveo.admin.async.SynchronizedMultiItemIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.BillinRunApplicationElFilterUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.DiscountAggregationModeEnum;
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
import org.meveo.service.billing.impl.InvoiceLineAggregationService.RTtoILAggregationQuery;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceLineService.InvoiceLineCreationStatistics;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;

@Stateless
public class InvoiceLinesJobBean extends IteratorBasedJobBean<List<Map<String, Object>>> {

    private static final long serialVersionUID = -1318477921039388503L;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private InvoiceLineService invoiceLinesService;

    @Inject
    private InvoiceLineAggregationService invoiceLineAggregationService;

    public static final String FIELD_PRIORITY_SORT = "billingCycle.priority, auditable.created";

    private static final String BR_PROCESSED = "brProcessed";

    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private JobInstanceService jobInstanceService;

    private AggregationConfiguration aggregationConfiguration;
    private boolean incrementalInvoiceLines;

    private boolean hasMore = false;
    private StatelessSession statelessSession;
    private ScrollableResults scrollableResults;

    private Long minId = null;
    private Long maxId = null;
    private Long nrOfAccounts = null;

    /**
     * Job process one billing run at a time. A current billing run being processed
     */
    private BillingRun currentBillingRun;

    private static final String BILLING_RUN_REPORT_JOB_CODE = "BILLING_RUN_REPORT_JOB";


    /**
     * Execution statistics
     */
    private BasicStatistics aggregatedStats = new BasicStatistics();

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::createInvoiceLines, null, this::hasMore, this::closeResultset, this::closeBillingRun);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    @SuppressWarnings({ "unchecked" })
    private Optional<Iterator<List<Map<String, Object>>>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        // Get a list of Billing runs to process - either from job parameters or Billing runs with NEW and OPEN status
        List<BillingRun> billingRuns = getBillingRunsToProcess(jobInstance, jobExecutionResult);
        if (billingRuns == null || billingRuns.isEmpty()) {
            return Optional.empty();
        }

        // Number of Rated transactions to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("invoiceLinesJob.processNrInJobRun", 4000000);

        Long batchSize = 10L;// Just for fetching purpose
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        EntityManager em = emWrapper.getEntityManager();
        statelessSession = em.unwrap(Session.class).getSessionFactory().openStatelessSession();

        // Advance to the first unprocessed billing run. Loop over Billing runs to process and process the first one that has data. Others will be run in a next job run
        boolean isLastBRToProcess = false;

        if (jobInstance.getParamValue(BR_PROCESSED) == null) {
            jobInstance.setParamValue(BR_PROCESSED, new ArrayList<Long>());
        }

        RTtoILAggregationQuery aggregationQueryInfo = null;

        for (int i = 0; i < billingRuns.size(); i++) {
            isLastBRToProcess = i == billingRuns.size() - 1;
            BillingRun billingRun = billingRuns.get(i);

            // Skip billing runs that were already processed by a job
            if (((List<Long>) jobInstance.getParamValue(BR_PROCESSED)).contains(billingRun.getId())) {
                continue;
            }

            currentBillingRun = billingRun;

            // The first run of billing run (status is 'NEW' at that moment) should be in a normal run to create new invoice line
            // and to avoid doing unnecessary joins.
            // The next runs of BR (status has already changed to 'OPEN' at that moment) will apply the appending mode on existing invoice lines
            incrementalInvoiceLines = currentBillingRun.getIncrementalInvoiceLines() && currentBillingRun.getStatus() == BillingRunStatusEnum.OPEN;

            // set status of billing run as CREATING_INVOICE_LINES, i.e. it indicates that the invoice line job is running
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.CREATING_INVOICE_LINES, null);

            // Determine aggregation options from Billing run
            aggregationConfiguration = new AggregationConfiguration(currentBillingRun);
            aggregationConfiguration.setEnterprise(appProvider.isEntreprise());

            aggregationQueryInfo = invoiceLineAggregationService.getAggregationSummaryAndILDetailsQuery(currentBillingRun, aggregationConfiguration, statelessSession, incrementalInvoiceLines);
            nrOfAccounts = aggregationQueryInfo.getNumberOfBA();

            jobExecutionResult.addReport("Billing run #" + billingRun.getId() + ": will process " + nrOfAccounts + " accounts" + (incrementalInvoiceLines ? " in append mode." : "."));

            // If no records found for a BR to process, continue to another BR
            if (nrOfAccounts.intValue() == 0) {
                dropView();
                if (isLastBRToProcess) {
                    closeBillingRun(jobExecutionResult);
                    return Optional.empty();
                } else {
                    closeBillingRun(jobExecutionResult);
                    continue;
                }
            }

            break;
        }

        Object[] convertSummary = getProcessingSummary();
        minId = (Long) convertSummary[0];
        maxId = (Long) convertSummary[1];

        scrollableResults = aggregationQueryInfo.getQuery().setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

        Long nrOfRecords = aggregationQueryInfo.getNumberOfIL();
        List<String> aggregationFields = aggregationQueryInfo.getFieldNames();

        em.detach(currentBillingRun);

        boolean brHasMore = nrOfRecords >= processNrInJobRun;

        // Mark BR as processed if it has no more records
        if (!brHasMore) {
            ((List<Long>) jobInstance.getParamValue(BR_PROCESSED)).add(currentBillingRun.getId());
        }

        // Continue with additional job run if there are still BR related records to process or there are more BR to process
        hasMore = brHasMore || (!brHasMore && !isLastBRToProcess);

        // Grouping/processing settings
        boolean groupByBA = (boolean) getParamOrCFValue(jobInstance, InvoiceLinesJob.CF_INVOICE_LINES_GROUP_BY_BA, false);
        final long nrRtsPerTx = (Long) this.getParamOrCFValue(jobInstance, InvoiceLinesJob.CF_INVOICE_LINES_NR_RTS_PER_TX, 1000000L);
        final long nrILsPerTx = (Long) this.getParamOrCFValue(jobInstance, InvoiceLinesJob.CF_INVOICE_LINES_NR_ILS_PER_TX, 10000L);

        if (groupByBA) {
            return Optional.of(new SynchronizedIteratorGrouped<Map<String, Object>>(scrollableResults, nrOfRecords.intValue(), true, aggregationFields) {

                @Override
                public Object getGroupByValue(Map<String, Object> item) {
                    return item.get("billing_account__id");
                }
            });
        } else {
            return Optional.of(new SynchronizedMultiItemIterator<Map<String, Object>>(scrollableResults, nrOfRecords.intValue(), true, aggregationFields) {

                long totalRtCount = 0L;
                long totalIlCount = 0L;

                @Override
                public void initializeDecisionMaking(Map<String, Object> item) {
                    totalRtCount = item.get("rt_count") != null ? ((Number) item.get("rt_count")).longValue() : 1L;
                    totalIlCount = 1;
                }

                @Override
                public boolean isIncludeItem(Map<String, Object> item) {

                    long rtCount = item.get("rt_count") != null ? ((Number) item.get("rt_count")).longValue() : 1L;

                    if (totalIlCount + 1 > nrILsPerTx || totalRtCount + rtCount > nrRtsPerTx) {
                        return false;
                    }

                    totalRtCount = totalRtCount + rtCount;
                    totalIlCount++;
                    return true;
                }

            });
        }
    }

    private void createInvoiceLines(List<Map<String, Object>> aggregationInfo, JobExecutionResultImpl jobExecutionResult) {

        Long brId = ((Integer) aggregationInfo.get(0).get("billing_run_id")).longValue();

        BillingRun br = currentBillingRun != null ? currentBillingRun : billingRunService.findById(brId);

        InvoiceLineCreationStatistics ilBasicStatistics = invoiceLinesService.createInvoiceLines(aggregationInfo, aggregationConfiguration, jobExecutionResult, br, null);

        aggregatedStats.append(ilBasicStatistics);
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
        if (scrollableResults != null) {
            scrollableResults.close();
        }
        if (statelessSession != null) {
            statelessSession.close();
        }

        dropView();
    }

    private void dropView() {

    	if(currentBillingRun!=null) {
	        EntityManager em = emWrapper.getEntityManager();
	        Session hibernateSession = em.unwrap(Session.class);
	
	        String viewName = InvoiceLineAggregationService.getMaterializedAggregationViewName(currentBillingRun.getId());
	        hibernateSession.doWork(new org.hibernate.jdbc.Work() {
	
	            @Override
	            public void execute(Connection connection) throws SQLException {
	
	                try (Statement statement = connection.createStatement()) {
	                    log.info("Dropping materialized view {}", viewName);
	                    statement.execute("drop materialized view if exists " + viewName);
	
	                } catch (Exception e) {
	                    log.error("Failed to drop/create the materialized view " + viewName, e.getMessage());
	                    throw new BusinessException(e);
	                }
	            }
	        });
    	}
    }

    /**
     * Change Billing run status
     * 
     * @param jobExecutionResult Job execution result
     */
    private void closeBillingRun(JobExecutionResultImpl jobExecutionResult) {

        if (jobExecutionResult.getJobLauncherEnum() != JobLauncherEnum.WORKER) {

            if (minId != null && aggregationConfiguration.getDiscountAggregation() == DiscountAggregationModeEnum.NO_AGGREGATION) {
                invoiceLinesService.bridgeDiscountILs(currentBillingRun.getId(), minId, maxId);
            }

            // In case of incrementalInvoiceLines, update status of billing run as
            if (currentBillingRun.getIncrementalInvoiceLines()) {

                if (incrementalInvoiceLines) {
                    nrOfAccounts = (Long) emWrapper.getEntityManager().createNamedQuery("InvoiceLine.countDistinctBAByBR").setParameter("brId", currentBillingRun.getId()).getSingleResult();
                }

                billingRunExtensionService.updateBillingRunStatistics(currentBillingRun.getId(), aggregatedStats, nrOfAccounts.intValue(), BillingRunStatusEnum.OPEN);
            }
            // Otherwise, update directly status of billing run as INVOICE_LINES_CREATED
            else {
                billingRunExtensionService.updateBillingRunStatistics(currentBillingRun.getId(), aggregatedStats, nrOfAccounts.intValue(),
                    jobExecutionResult.getStatus() != JobExecutionResultStatusEnum.CANCELLED ? BillingRunStatusEnum.INVOICE_LINES_CREATED : BillingRunStatusEnum.NEW);
            }

            billingRunService.updateBillingRunJobExecution(currentBillingRun.getId(), jobExecutionResult);
            runBillingRunReports(asList(currentBillingRun.getId()));
        }
    }

    /**
     * Get a min and max ID from RT table
     * 
     * @return An array containing [ min RT id, max RT id]
     */
    private Long[] getProcessingSummary() {

        EntityManager em = emWrapper.getEntityManager();
        Object[] minMax = (Object[]) em.createQuery("select min(id), max(id) from RatedTransaction where status='OPEN'").getSingleResult();

        if (minMax[0] != null) {
            Long minId = ((Number) minMax[0]).longValue();
            Long maxId = ((Number) minMax[1]).longValue();

            return new Long[] { minId, maxId };

        } else {
            return new Long[] { null, null };
        }
    }

    /**
     * Get a list of Billing runs to process - either from job parameters or Billing runs with NEW and OPEN status
     * 
     * @param jobInstance Job instance
     * @param jobExecutionResult Job execution result
     * @return A list of Billing runs to process
     */
    @SuppressWarnings("unchecked")
    private List<BillingRun> getBillingRunsToProcess(JobInstance jobInstance, JobExecutionResultImpl jobExecutionResult) {

        List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_billingRun");
        List<Long> billingRunIds = billingRunWrappers != null ? billingRunWrappers.stream().map(br -> valueOf(br.getCode().split("/")[0])).collect(toList()) : emptyList();
        List<BillingRunStatusEnum> billingRunStatus = asList(BillingRunStatusEnum.NEW, BillingRunStatusEnum.OPEN);
        Map<String, Object> filters = new HashMap<>();
        if (billingRunIds.isEmpty()) {
            filters.put("inList status", billingRunStatus);
        } else {
            filters.put("inList id", billingRunIds);
        }
        PaginationConfiguration pagination = new PaginationConfiguration(null, null, filters, null, asList("billingCycle"), FIELD_PRIORITY_SORT, SortOrder.ASCENDING);

        List<BillingRun> billingRuns = BillinRunApplicationElFilterUtils.filterByApplicationEL(billingRunService.list(pagination), jobInstance);

        // Extra validation of BR status when billing run list is provided as parameters
        if (!billingRunIds.isEmpty() && !billingRuns.isEmpty()) {
            List<BillingRun> excludedBRs = billingRuns.stream().filter(br -> br.getStatus() != BillingRunStatusEnum.NEW && (br.getStatus() != BillingRunStatusEnum.OPEN || !br.getIncrementalInvoiceLines()))
                .collect(toList());
            excludedBRs.forEach(br -> jobExecutionResult.registerWarning(format("BillingRun[id={%d}] has been ignored as it neither NEW nor OPEN status", br.getId())));
            billingRuns.removeAll(excludedBRs);
            if (billingRuns.isEmpty()) {
                jobExecutionResult.registerError("No valid billing run with status = NEW or OPEN found");
            }
        }

        return billingRuns;
    }

//    private org.hibernate.query.Query getQueryBaBrIds(List<BillingRun> billingRuns, StatelessSession statelessSession, Long maxId) {
//
//        String unionSql = null;
//        Map<String, Object> params = new HashMap<String, Object>();
//
//        EntityManager em = emWrapper.getEntityManager();
//        int i = 0;
//        for (BillingRun billingRun : billingRuns) {
//
//            Map<String, Object> filters = billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getFilters() : billingRun.getFilters();
//            if (filters == null && billingRun.getBillingCycle() != null) {
//                filters = new HashMap<>();
//                filters.put("billingAccount.billingCycle.id", billingRun.getBillingCycle().getId());
//            }
//            if (filters == null) {
//                throw new BusinessException("No filter found for billingRun " + billingRun.getId());
//            }
//            filters.put("status", RatedTransactionStatusEnum.OPEN.toString());
//            filters.put("toRangeInclusive id", maxId);
//
//            QueryBuilder queryBuilder = ratedTransactionService.getQueryFromFilters(filters, "distinct a.billingAccount.id, " + billingRun.getId(), null, false);
//
//            String jpaSql = queryBuilder.getSqlString();
//            String sql = PersistenceService.getNativeQueryFromJPA(em.createQuery(jpaSql));
//
//            for (Entry<String, Object> paramInfo : queryBuilder.getParams().entrySet()) {
//                sql = sql.replaceFirst("(:" + paramInfo.getKey() + ")[ |)]?", ":" + paramInfo.getKey() + "_" + i);
//                params.put(paramInfo.getKey() + "_" + i, paramInfo.getValue());
//            }
//
//            if (i == 0) {
//                unionSql = sql;
//            } else {
//                unionSql = unionSql + " UNION " + sql;
//            }
//
//            i++;
//        }
//
//        org.hibernate.query.Query query = statelessSession.createNativeQuery(unionSql);
//        for (Entry<String, Object> paramInfo : params.entrySet()) {
//            if (paramInfo.getValue() != null && paramInfo.getValue().getClass().isEnum()) {
//                query.setParameter(paramInfo.getKey(), paramInfo.getValue().toString());
//            } else {
//                query.setParameter(paramInfo.getKey(), paramInfo.getValue());
//            }
//        }
//        return query;
//
//        // @NamedQuery(name="RatedTransaction.getDistinctBaAndBr",query="SELECT k.baId, :brId from (SELECT rt.billingAccount.id as baId from RatedTransaction rt where rt.status='OPEN' and
//        // rt.billingAccount.billingCycle.id in :bcIds and rt.id<=:maxId) k")
//    }

    private void runBillingRunReports(List<Long> billingRuns) {
        Map<String, Object> jobParams = new HashMap<>();
        jobParams.put("billingRun", billingRuns.stream()
                .map(String::valueOf)
                .collect(joining("/")));
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", RatedTransactionStatusEnum.BILLED.toString());
        jobParams.put("filters", filters);
        try {
            JobInstance jobInstance = jobInstanceService.findByCode(BILLING_RUN_REPORT_JOB_CODE);
            jobInstance.setRunTimeValues(jobParams);
            jobExecutionService.executeJob(jobInstance, jobParams, JobLauncherEnum.TRIGGER);
        } catch (Exception exception) {
            throw new BusinessException("Exception occurred during billing run report job execution : "
                    + exception.getMessage(), exception.getCause());
        }
    }

}