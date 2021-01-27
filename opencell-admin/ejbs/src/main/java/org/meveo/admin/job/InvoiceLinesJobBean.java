package org.meveo.admin.job;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.job.AggregationConfiguration.AggregationOption;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLinesService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.*;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;

@Stateless
public class InvoiceLinesJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceLinesService invoiceLinesService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());
        try {
            List<EntityReferenceWrapper> billingRunWrappers =
                    (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_billingRun");
            String aggregationOption =   ofNullable((String) this.getParamOrCFValue(jobInstance, "InvoiceLinesJob_aggregationOption"))
                    .orElse("NO_AGGREGATION");
            if(billingRunWrappers != null && !billingRunWrappers.isEmpty()) {
                List<Long> billingRunIds = billingRunWrappers.stream()
                        .map(br -> valueOf(br.getCode().split("/")[0]))
                        .collect(toList());
                List<BillingRun> billingRuns;
                Map<String, Object> filters = new HashedMap();
                filters.put("inList id", billingRunIds);
                billingRuns = billingRunService.list(new PaginationConfiguration(filters));
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
                        Map<String, Object> params = new HashMap<>();
                        params.put("ids", ratedTransactionIds);
                        List<Map<String, Object>> groupedRTs;
                        if (aggregationConfiguration.getAggregationOption() == AggregationOption.NO_AGGREGATION) {
                            groupedRTs = getGroupedRTs(params);
                        } else {
                            groupedRTs = getGroupedRTsWithAggregation(params);
                        }
                        createInvoiceLines(groupedRTs, aggregationConfiguration);
                        result.setNbItemsCorrectlyProcessed(groupedRTs.size());
                    }
                }
            }
        } catch(Exception exception) {
            result.registerError(exception.getMessage());
            log.error(format("Failed to run invoice lines job: %s", exception));
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

    private List<Map<String, Object>> getGroupedRTs(Map<String, Object> params) {
        String query = "SELECT rt.billing_account__id, \n" +
                "                 rt.article_id, rt.description as label, SUM(rt.quantity) AS quantity, \n" +
                "                 rt.unit_amount_without_tax, rt.unit_amount_with_tax,\n" +
                "                 rt.amount_without_tax, rt.amount_with_tax, \n" +
                "                 rt.offer_id, rt.service_instance_id,\n" +
                "                 rt.usage_date, rt.start_date, rt.end_date,\n" +
                "                 rt.order_number, rt.subscription_id, rt.tax_percent \n" +
                " FROM billing_rated_transaction rt WHERE id in (:ids) \n" +
                " GROUP BY rt.billing_account__id, rt.article_id, rt.description, \n" +
                "         rt.unit_amount_without_tax, rt.unit_amount_with_tax,\n" +
                "         rt.amount_without_tax, rt.amount_with_tax, rt.offer_id,\n" +
                "         rt.service_instance_id, rt.usage_date, rt.start_date,\n" +
                "         rt.end_date, rt.order_number, rt.subscription_id, rt.tax_percent";
        return ratedTransactionService.executeNativeSelectQuery(query, params);
    }

    private List<Map<String, Object>> getGroupedRTsWithAggregation(Map<String, Object> params) {
        String query = "SELECT rt.billing_account__id,  \n" +
                "              rt.article_id, rt.description as label, SUM(rt.quantity) AS quantity,  \n" +
                "              sum(rt.amount_without_tax) as sum_amount_without_tax, \n" +
                "              sum(rt.amount_with_tax) / sum(rt.quantity) as unit_price, \n" +
                "              rt.amount_without_tax, rt.amount_with_tax, rt.offer_id, rt.service_instance_id, \n" +
                "              EXTRACT(MONTH FROM rt.usage_date) valueDate, min(rt.start_date) as start_date, \n" +
                "              max(rt.end_date) as end_date, rt.order_number, rt.tax_percent " +
                "    FROM billing_rated_transaction rt WHERE id in (:ids) \n" +
                "    GROUP BY rt.billing_account__id, rt.article_id, rt.description,  \n" +
                "             rt.amount_without_tax, rt.amount_with_tax, \n" +
                "             rt.offer_id, rt.service_instance_id, EXTRACT(MONTH FROM rt.usage_date), rt.start_date, \n" +
                "             rt.end_date, rt.order_number, rt.tax_percent";
        return ratedTransactionService.executeNativeSelectQuery(query, params);
    }

    private void createInvoiceLines(List<Map<String, Object>> groupedRTs, AggregationConfiguration aggregationConfiguration) {
        InvoiceLinesFactory linesFactory = new InvoiceLinesFactory();
        for (Map<String, Object> record : groupedRTs) {
            try {
                InvoiceLine invoiceLine = linesFactory.create(record, aggregationConfiguration);
                invoiceLinesService.create(invoiceLine);
            } catch (Exception exception) {
                log.info(exception.getMessage());
            }
        }
    }
}