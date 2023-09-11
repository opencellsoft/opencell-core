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
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.cpq.commercial.InvoiceLine;
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
import java.math.BigInteger;
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
                    List<RatedTransaction> ratedTransactions = billingRunService.loadRTsByBillingRuns(billingRuns, true);
                    List<Long> ratedTransactionIds = ratedTransactions.stream()
                            .map(RatedTransaction::getId)
                            .collect(toList());
                    if (!ratedTransactionIds.isEmpty()) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("ids", ratedTransactionIds);
                        List<Map<String, Object>> groupedRTs;
                        if (aggregationConfiguration.getAggregationOption() == AggregationOption.NO_AGGREGATION) {
                            groupedRTs = getGroupedRTs(params);
                        } else {
                            groupedRTs = getGroupedRTsWithAggregation(params);
                        }
                        Map<Long, Long> iLIdsRtIdsCorrespondence = createInvoiceLines(groupedRTs, aggregationConfiguration, result);
                        makeAsProcessed(ratedTransactionIds);
                        linkRTWithInvoiceLine(iLIdsRtIdsCorrespondence);
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
        QueryBuilder queryBuilder = fromFilters(billingRun.getFilters());
        billingRun.setExceptionalRTIds(queryBuilder.getIdQuery(ratedTransactionService.getEntityManager()).getResultList());
    }

    private QueryBuilder fromFilters(Map<String, String> filters) {
        QueryBuilder queryBuilder;
        if(filters.containsKey("SQL")) {
            queryBuilder = new QueryBuilder(filters.get("SQL"));
        } else {
            PaginationConfiguration configuration = new PaginationConfiguration(new HashMap<>(filters));
            queryBuilder = ratedTransactionService.getQuery(configuration);
        }
        return queryBuilder;
    }

    private long validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream()
                .filter(br -> br.getStatus() != NEW)
                .collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored", br.getId())));
        billingRuns.removeAll(excludedBRs);
        return excludedBRs.size();
    }

    private List<Map<String, Object>> getGroupedRTs(Map<String, Object> params) {
        String query = "SELECT rt.id, rt.billing_account__id, \n" +
                "                 rt.accounting_code_id, rt.description as label, SUM(rt.quantity) AS quantity, \n" +
                "                 rt.unit_amount_without_tax, rt.unit_amount_with_tax,\n" +
                "                 SUM(rt.amount_without_tax) as sum_without_Tax, SUM(rt.amount_with_tax) as sum_with_tax, \n" +
                "                 rt.offer_id, rt.service_instance_id,\n" +
                "                 rt.usage_date, rt.start_date, rt.end_date,\n" +
                "                 rt.order_number, rt.subscription_id, rt.tax_percent, " + 
                "				  rt.order_id, rt.product_version_id, rt.order_lot_id, charge_instance_id, rt.article_id ,discounted_ratedtransaction_id \n" +
                " FROM billing_rated_transaction rt WHERE id in (:ids) \n" +
                " GROUP BY rt.billing_account__id, rt.accounting_code_id, rt.description, \n" +
                "         rt.unit_amount_without_tax, rt.unit_amount_with_tax,\n" +
                "         rt.offer_id, rt.service_instance_id, rt.usage_date, rt.start_date,\n" +
                "         rt.end_date, rt.order_number, rt.subscription_id, rt.tax_percent," + 
                "		  rt.order_id, rt.product_version_id, rt.order_lot_id, charge_instance_id, rt.id, rt.article_id ,discounted_ratedtransaction_id order by unit_amount_without_tax desc \n";
        return ratedTransactionService.executeNativeSelectQuery(query, params);
    }

    private List<Map<String, Object>> getGroupedRTsWithAggregation(Map<String, Object> params) {
        String query = "SELECT rt.id, rt.billing_account__id,  \n" +
                "              rt.accounting_code_id, rt.description as label, SUM(rt.quantity) AS quantity,  \n" +
                "              sum(rt.amount_without_tax) as sum_amount_without_tax, \n" +
                "              sum(rt.amount_with_tax) / sum(rt.quantity) as unit_price, \n" +
                "              rt.amount_without_tax, rt.amount_with_tax, rt.offer_id, rt.service_instance_id, \n" +
                "              EXTRACT(MONTH FROM rt.usage_date) valueDate, min(rt.start_date) as start_date, \n" +
                "              max(rt.end_date) as end_date, rt.order_number, rt.tax_percent, " + 
                "			   rt.order_id, rt.product_version_id, rt.order_lot_id, charge_instance_id , rt.article_id ,discounted_ratedtransaction_id \n" +
                "    FROM billing_rated_transaction rt WHERE id in (:ids) \n" +
                "    GROUP BY rt.billing_account__id, rt.accounting_code_id, rt.description,  \n" +
                "             rt.amount_without_tax, rt.amount_with_tax, \n" +
                "             rt.offer_id, rt.service_instance_id, EXTRACT(MONTH FROM rt.usage_date), rt.start_date, \n" +
                "             rt.end_date, rt.order_number, rt.tax_percent, " + 
                "			  rt.order_id, rt.product_version_id, rt.order_lot_id, charge_instance_id, rt.id, rt.article_id ,discounted_ratedtransaction_id order by unit_amount_without_tax desc\n";
        return ratedTransactionService.executeNativeSelectQuery(query, params);
    }

    private Map<Long, Long> createInvoiceLines(List<Map<String, Object>> groupedRTs, AggregationConfiguration aggregationConfiguration,
                                    JobExecutionResultImpl result) throws BusinessException {
        InvoiceLinesFactory linesFactory = new InvoiceLinesFactory();
        Map<Long, Long> iLIdsRtIdsCorrespondence = new HashMap<>();
        for (Map<String, Object> record : groupedRTs) {
            try {
                InvoiceLine invoiceLine = linesFactory.create(record, iLIdsRtIdsCorrespondence,aggregationConfiguration, result);
                invoiceLinesService.create(invoiceLine);
                invoiceLine = invoiceLinesService.retrieveIfNotManaged(invoiceLine);
                iLIdsRtIdsCorrespondence.put(((BigInteger) record.get("id")).longValue(),invoiceLine.getId() );
            } catch (BusinessException exception) {
                result.addNbItemsProcessedWithError(1);
                throw new BusinessException(exception);
            }
        }
        return iLIdsRtIdsCorrespondence;
    }

    private int makeAsProcessed(List<Long> ratedTransactionIds) {
        return ratedTransactionService.getEntityManager()
                    .createNamedQuery("RatedTransaction.markAsProcessed")
                    .setParameter("listOfIds", ratedTransactionIds)
                    .executeUpdate();
    }

    private void linkRTWithInvoiceLine(Map<Long, Long> iLIdsRtIdsCorrespondence) {
        for (Map.Entry<Long, Long> entry : iLIdsRtIdsCorrespondence.entrySet()) {
            ratedTransactionService.getEntityManager()
                    .createNamedQuery("RatedTransaction.linkRTWithInvoiceLine")
                    .setParameter("id", entry.getKey())
                    .setParameter("il", invoiceLinesService.findById(entry.getValue()))
                    .executeUpdate();
        }
    }
}
