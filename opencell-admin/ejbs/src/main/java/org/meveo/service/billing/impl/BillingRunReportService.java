package org.meveo.service.billing.impl;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingRunReportTypeEnum.OPEN_RATED_TRANSACTIONS;
import static org.meveo.model.jobs.JobLauncherEnum.API;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccountAmount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunReport;
import org.meveo.model.billing.OfferAmount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class BillingRunReportService extends PersistenceService<BillingRunReport> {

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private BillingAccountAmountService billingAccountAmountService;

    @Inject
    private OfferAmountService offerAmountService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    private static final String BILLING_RUN_REPORT_JOB_CODE = "BILLING_RUN_REPORT_JOB";
    private static final String BILLING_RUN_REPORT_JOB_PARAMETERS = "BillingRunReportJob_billingRun";


    /**
     * Create billing run reports from billing run
     *
     * @param billingRun billing run
     * @return Billing run report
     */
    public BillingRunReport createBillingRunReport(BillingRun billingRun) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", RatedTransactionStatusEnum.OPEN.toString());
        List<RatedTransaction> ratedTransactions = ratedTransactionService.getReportRatedTransactions(billingRun);
        BillingRunReport billingRunReport = new BillingRunReport();
        if (!ratedTransactions.isEmpty()) {
            List<Long> rtIds = ratedTransactions.stream().map(RatedTransaction::getId).collect(toList());
            List<Object[]> reportDetails = ratedTransactionService.getReportStatisticsDetails(billingRun, rtIds);
            billingRunReport = prepareBillingRunReport(reportDetails);
            createBillingAccountAmounts(billingRun, rtIds, billingRunReport);
            createOfferAmounts(billingRun, rtIds, billingRunReport);
        }
        billingRunReport.setBillingRun(billingRun);
        create(billingRunReport);
        billingRun.setPreInvoicingReport(billingRunReport);
        return billingRunReport;
    }

    private BillingRunReport prepareBillingRunReport(List<Object[]> reportDetails) {
        BillingRunReport billingRunReport = new BillingRunReport();
        billingRunReport.setCreationDate(new Date());
        billingRunReport.setType(OPEN_RATED_TRANSACTIONS);
        BigDecimal totalAmountWithoutTax = ZERO;
        BigDecimal subscriptionCount = ZERO;
        BigDecimal billingAccountCount = ZERO;
        BigDecimal ratedTransactionCount = ZERO;
        for (Object[] line : reportDetails) {
            if ("R".equalsIgnoreCase((String) line[5])) {
                billingRunReport.setRecurringTransactionsCount(valueOf((Long) line[3]));
                billingRunReport.setRecurringTotalAmountWithoutTax((BigDecimal) line[4]);
            }
            if ("O".equalsIgnoreCase((String) line[5])) {
                billingRunReport.setOneShotTransactionsCount(valueOf((Long) line[3]));
                billingRunReport.setOneShotTotalAmountWithoutTax((BigDecimal) line[4]);
            }
            if ("U".equalsIgnoreCase((String) line[5])) {
                billingRunReport.setUsageTransactionsCount(valueOf((Long) line[3]));
                billingRunReport.setUsageTotalAmountWithoutTax((BigDecimal) line[4]);
            }
            totalAmountWithoutTax = totalAmountWithoutTax.add((BigDecimal) line[4]);
            ratedTransactionCount = ratedTransactionCount.add(valueOf((Long) line[0]));
            subscriptionCount = subscriptionCount.add(valueOf((Long) line[2]));
            billingAccountCount = billingAccountCount.add(valueOf((Long) line[1]));
        }
        billingRunReport.setTotalAmountWithoutTax(totalAmountWithoutTax);
        billingRunReport.setRatedTransactionsCount(ratedTransactionCount);
        billingRunReport.setBillingAccountsCount(billingAccountCount);
        billingRunReport.setSubscriptionsCount(subscriptionCount);
        return billingRunReport;
    }

    private List<BillingAccountAmount> createBillingAccountAmounts(BillingRun billingRun,
                                                                   List<Long> rtIds, BillingRunReport billingRunReport) {
        List<BillingAccountAmount> billingAccountAmounts = new ArrayList<>();
        List<Object[]> amountsPerBA = ratedTransactionService.getBillingAccountStatisticsDetails(billingRun, rtIds);
        for (Object[] amount : amountsPerBA) {
            BillingAccountAmount billingAccountAmount = new BillingAccountAmount();
            billingAccountAmount.setBillingAccount(billingAccountService.findById((Long) amount[0]));
            billingAccountAmount.setAmount((BigDecimal) amount[1]);
            billingAccountAmountService.create(billingAccountAmount);
            billingAccountAmount.setBillingRunReport(billingRunReport);
            billingAccountAmounts.add(billingAccountAmount);
        }
        return billingAccountAmounts;
    }

    private List<OfferAmount> createOfferAmounts(BillingRun billingRun,
                                                 List<Long> rtIds, BillingRunReport billingRunReport) {
        List<OfferAmount> offerAmounts = new ArrayList<>();
        List<Object[]> amountsPerOffer = ratedTransactionService.getOfferStatisticsDetails(billingRun, rtIds);
        for (Object[] amount : amountsPerOffer) {
            OfferAmount offerAmount = new OfferAmount();
            offerAmount.setOffer(offerTemplateService.findById((Long) amount[0]));
            offerAmount.setAmount((BigDecimal) amount[1]);
            offerAmount.setBillingRunReport(billingRunReport);
            offerAmountService.create(offerAmount);
            offerAmounts.add(offerAmount);
        }
        return offerAmounts;
    }

    /**
     * Launch billing run report job
     * @param billingRun
     */
    public void launchBillingRunReportJob(BillingRun billingRun) {
        if (billingRun.isPreReportAutoOnCreate() && !billingRun.hasPreInvoicingReport()) {
            try {
                Map<String, Object> jobParams = new HashMap<>();
                jobParams.put(BILLING_RUN_REPORT_JOB_PARAMETERS,
                        asList(new EntityReferenceWrapper(BillingRun.class.getName(),
                                null, billingRun.getReferenceCode())));
                JobInstance jobInstance = jobInstanceService.findByCode(BILLING_RUN_REPORT_JOB_CODE);
                jobInstance.setRunTimeValues(jobParams);
                jobExecutionService.executeJob(jobInstance, jobParams, API);
            } catch (Exception exception) {
                throw new BusinessException("Exception occurred during job execution : "
                        + exception.getMessage(), exception.getCause());
            }
        }
    }
}
