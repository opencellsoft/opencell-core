package org.meveo.service.billing.impl;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.jobs.JobLauncherEnum.API;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.billing.AccountingArticleAmount;
import org.meveo.model.billing.BillingAccountAmount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunReport;
import org.meveo.model.billing.BillingRunReportTypeEnum;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.OfferAmount;
import org.meveo.model.billing.ProductAmount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.SubscriptionAmount;
import org.meveo.model.cpq.Product;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class BillingRunReportService extends PersistenceService<BillingRunReport> {
	
	@Inject
	private ResourceBundle resourceMessages;
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

    @Inject
    private SubscriptionAmountService subscriptionAmountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ProductAmountService productAmountService;

    @Inject
    private ProductService productService;

    @Inject
    private AccountingArticleAmountService accountingArticleAmountService;

    @Inject
    private AccountingArticleService accountingArticleService;

    private static final String BILLING_RUN_REPORT_JOB_CODE = "BILLING_RUN_REPORT_JOB";

    private static final int DEFAULT_BLOCK_SIZE = 10;


    /**
     * Create billing run reports from billing run
     *
     * @param billingRun billing run
     * @param filters billing run filters
     * @param reportType report type
     * @return Billing run report
     */
    public BillingRunReport createBillingRunReport(BillingRun billingRun,
                                                   Map<String, Object> filters, BillingRunReportTypeEnum reportType) {
		if(billingRun.getPreInvoicingReport() != null && billingRun.getPreInvoicingReport().isReportFinal()){
			throw new BusinessException(resourceMessages.getString("billingRunReport.billingRun.preInvoicingReport.final.error.message"));
		}
        List<RatedTransaction> ratedTransactions = ratedTransactionService.getReportRatedTransactions(billingRun, filters);
        BillingRunReport billingRunReport = new BillingRunReport();
        if (!ratedTransactions.isEmpty()) {
            List<Long> rtIds = ratedTransactions.stream().map(RatedTransaction::getId).collect(toList());
            List<Object[]> reportDetails = ratedTransactionService.getReportStatisticsDetails(billingRun, rtIds, filters);
            List<Object[]> initialDetails = ratedTransactionService.getReportInitialDetails(billingRun, rtIds, filters);
            billingRunReport = prepareBillingRunReport(billingRun, reportDetails, reportType, initialDetails);
            if(billingRunReport.getId() != null) {
                clearOldAmounts(billingRunReport.getId());
            }
            createBillingAccountAmounts(billingRun, rtIds, billingRunReport, filters);
            createOfferAmounts(billingRun, rtIds, billingRunReport, filters);
            createSubscriptionAmounts(billingRun, rtIds, billingRunReport, filters);
            createProductAmounts(billingRun, rtIds, billingRunReport, filters);
            createArticleAmounts(billingRun, rtIds, billingRunReport, filters);
        }
        billingRunReport.setBillingRun(billingRun);
		if(!Arrays.asList(BillingRunStatusEnum.OPEN, BillingRunStatusEnum.NEW, BillingRunStatusEnum.CREATING_INVOICE_LINES).contains(billingRun.getStatus())){
			billingRunReport.setReportFinal(true);
		}
        createOrUpdate(billingRunReport);
        return billingRunReport;
    }

    private BillingRunReport prepareBillingRunReport(BillingRun billingRun, List<Object[]> reportDetails,
                                                     BillingRunReportTypeEnum type, List<Object[]> initialData) {
        BillingRunReport billingRunReport = findBillingReportBy(billingRun).orElse(new BillingRunReport());
        if(billingRunReport.getId() == null) {
            billingRunReport.setCreationDate(new Date());
        }
        billingRunReport.setType(type);
        BigDecimal totalAmountWithoutTax = ZERO;
        billingRunReport.setOneShotTransactionsCount(ZERO);
        billingRunReport.setOneShotTotalAmountWithoutTax(ZERO);
        for (Object[] line : reportDetails) {
            final String chargeType = (String) line[5];
            if ("R".equalsIgnoreCase(chargeType)) {
                billingRunReport.setRecurringTransactionsCount(valueOf((Long) line[3]));
                billingRunReport.setRecurringTotalAmountWithoutTax((BigDecimal) line[4]);
            }
            if ("O".equalsIgnoreCase(chargeType)
                    || "S".equalsIgnoreCase(chargeType) || "T".equalsIgnoreCase(chargeType)) {
                billingRunReport.addOneShotTransactionsCount(valueOf((Long) line[3]));
                billingRunReport.addOneShotTotalAmountWithoutTax((BigDecimal) line[4]);
            }
            if ("U".equalsIgnoreCase(chargeType)) {
                billingRunReport.setUsageTransactionsCount(valueOf((Long) line[3]));
                billingRunReport.setUsageTotalAmountWithoutTax((BigDecimal) line[4]);
            }
            totalAmountWithoutTax = totalAmountWithoutTax.add((BigDecimal) line[4]);
        }
        billingRunReport.setTotalAmountWithoutTax(totalAmountWithoutTax);
        if (initialData != null) {
            Object[] result = initialData.get(0);
            billingRunReport.setRatedTransactionsCount(BigDecimal.valueOf((Long) result[0]));
            billingRunReport.setBillingAccountsCount(BigDecimal.valueOf((Long) result[1]));
            billingRunReport.setSubscriptionsCount(BigDecimal.valueOf((Long) result[2]));
        }
        return billingRunReport;
    }

    private void clearOldAmounts(Long billingRunReportId) {
        getEntityManager().createNamedQuery("AccountingArticleAmount.deleteByBillingReport")
                .setParameter("billingRunReportId", billingRunReportId).executeUpdate();
        getEntityManager().createNamedQuery("SubscriptionAmount.deleteByBillingReport")
                .setParameter("billingRunReportId", billingRunReportId).executeUpdate();
        getEntityManager().createNamedQuery("OfferAmount.deleteByBillingReport")
                .setParameter("billingRunReportId", billingRunReportId).executeUpdate();
        getEntityManager().createNamedQuery("BillingAccountAmount.deleteByBillingReport")
                .setParameter("billingRunReportId", billingRunReportId).executeUpdate();
        getEntityManager().createNamedQuery("ProductAmount.deleteByBillingReport")
                .setParameter("billingRunReportId", billingRunReportId).executeUpdate();
    }

    private List<BillingAccountAmount> createBillingAccountAmounts(BillingRun billingRun, List<Long> rtIds,
                                                                   BillingRunReport billingRunReport, Map<String, Object> filters) {
        if(billingRun.getBillingCycle() == null
                || (billingRun.getBillingCycle() != null && billingRun.getBillingCycle().getReportConfigDisplayBillingAccounts())) {
            List<BillingAccountAmount> billingAccountAmounts = new ArrayList<>();
            final int blockSize = ofNullable(billingRun.getBillingCycle())
                    .map(BillingCycle::getReportConfigBlockSizeBillingAccounts)
                    .orElse(DEFAULT_BLOCK_SIZE);
            List<Object[]> amountsPerBA =
                    ratedTransactionService.getBillingAccountStatisticsDetails(billingRun, rtIds, filters, blockSize);
            for (Object[] amount : amountsPerBA) {
                if (amount[0] != null) {
                    BillingAccountAmount billingAccountAmount = new BillingAccountAmount();
                    billingAccountAmount.setBillingAccount(billingAccountService.findById((Long) amount[0]));
                    billingAccountAmount.setAmount((BigDecimal) amount[1]);
                    billingAccountAmount.setRatedTransactionCount(BigDecimal.valueOf((Long) amount[2]));
                    billingAccountAmountService.create(billingAccountAmount);
                    billingAccountAmount.setBillingRunReport(billingRunReport);
                    billingAccountAmounts.add(billingAccountAmount);
                }
            }
            return billingAccountAmounts;
        }
        return emptyList();
    }

    private List<OfferAmount> createOfferAmounts(BillingRun billingRun, List<Long> rtIds,
                                                 BillingRunReport billingRunReport, Map<String, Object> filters) {
        if(billingRun.getBillingCycle() == null
                || (billingRun.getBillingCycle() != null && billingRun.getBillingCycle().getReportConfigDisplayOffers())) {
            List<OfferAmount> offerAmounts = new ArrayList<>();
            final int OfferBlockSize = ofNullable(billingRun.getBillingCycle())
                    .map(BillingCycle::getReportConfigBlockSizeOffers)
                    .orElse(DEFAULT_BLOCK_SIZE);
            List<Object[]> amountsPerOffer =
                    ratedTransactionService.getOfferStatisticsDetails(billingRun, rtIds, filters, OfferBlockSize);
            for (Object[] amount : amountsPerOffer) {
                if (amount[0] != null) {
                    OfferAmount offerAmount = new OfferAmount();
                    offerAmount.setOffer(offerTemplateService.findById((Long) amount[0]));
                    offerAmount.setAmount((BigDecimal) amount[1]);
                    offerAmount.setRatedTransactionCount(BigDecimal.valueOf((Long) amount[2]));
                    offerAmount.setBillingRunReport(billingRunReport);
                    offerAmountService.create(offerAmount);
                    offerAmounts.add(offerAmount);
                }
            }
            return offerAmounts;
        }
        return emptyList();
    }


    private List<SubscriptionAmount> createSubscriptionAmounts(BillingRun billingRun, List<Long> rtIds,
                                                               BillingRunReport billingRunReport, Map<String, Object> filters) {
        if(billingRun.getBillingCycle() == null
                || (billingRun.getBillingCycle() != null && billingRun.getBillingCycle().getReportConfigDisplaySubscriptions())) {
            List<SubscriptionAmount> subscriptionAmounts = new ArrayList<>();
            final int subscriptionBlockSize = ofNullable(billingRun.getBillingCycle())
                    .map(BillingCycle::getReportConfigBlockSizeSubscriptions)
                    .orElse(DEFAULT_BLOCK_SIZE);
            List<Object[]> amountsPerSubscription =
                    ratedTransactionService.getSubscriptionStatisticsDetails(billingRun, rtIds, filters, subscriptionBlockSize);
            for (Object[] amount : amountsPerSubscription) {
                if (amount[0] != null) {
                    SubscriptionAmount subscriptionAmount = new SubscriptionAmount();
                    subscriptionAmount.setSubscription(subscriptionService.findById((Long) amount[0]));
                    subscriptionAmount.setAmount((BigDecimal) amount[1]);
                    subscriptionAmount.setRatedTransactionCount(BigDecimal.valueOf((Long) amount[2]));
                    subscriptionAmount.setBillingRunReport(billingRunReport);
                    subscriptionAmountService.create(subscriptionAmount);
                    subscriptionAmounts.add(subscriptionAmount);
                }
            }
            return subscriptionAmounts;
        }
        return emptyList();
    }

    private List<ProductAmount> createProductAmounts(BillingRun billingRun, List<Long> rtIds,
                                                               BillingRunReport billingRunReport, Map<String, Object> filters) {
        if(billingRun.getBillingCycle() == null
                || (billingRun.getBillingCycle() != null && billingRun.getBillingCycle().getReportConfigDisplayProducts())) {
            List<ProductAmount> productAmounts = new ArrayList<>();
            final int productsBlockSize = ofNullable(billingRun.getBillingCycle())
                    .map(BillingCycle::getReportConfigBlockSizeProducts)
                    .orElse(DEFAULT_BLOCK_SIZE);
            List<Object[]> amountsPerProduct =
                    ratedTransactionService.getProductStatisticsDetails(billingRun, rtIds, filters, productsBlockSize);
            for (Object[] amount : amountsPerProduct) {
                if (amount[0] != null) {
                    ProductAmount productAmount = new ProductAmount();
                    Product product = productService.findById((Long) amount[0]);
                    if(product != null) {
                        productAmount.setProduct(product);
                        productAmount.setAmount((BigDecimal) amount[1]);
                        productAmount.setRatedTransactionCount(BigDecimal.valueOf((Long) amount[2]));
                        productAmount.setBillingRunReport(billingRunReport);
                        productAmountService.create(productAmount);
                        productAmounts.add(productAmount);
                    }
                }
            }
            return productAmounts;
        }
        return emptyList();
    }

    private List<AccountingArticleAmount> createArticleAmounts(BillingRun billingRun, List<Long> rtIds,
                                                     BillingRunReport billingRunReport, Map<String, Object> filters) {
        if(billingRun.getBillingCycle() == null
                || (billingRun.getBillingCycle() != null && billingRun.getBillingCycle().getReportConfigDisplayArticles())) {
            List<AccountingArticleAmount> accountingArticleAmounts = new ArrayList<>();
            final int articleBlockSize = ofNullable(billingRun.getBillingCycle())
                    .map(BillingCycle::getReportConfigBlockSizeArticles)
                    .orElse(DEFAULT_BLOCK_SIZE);
            List<Object[]> amountsPerArticle =
                    ratedTransactionService.getArticleStatisticsDetails(billingRun, rtIds, filters, articleBlockSize);
            for (Object[] amount : amountsPerArticle) {
                if (amount[0] != null) {
                    AccountingArticleAmount accountingArticleAmount = new AccountingArticleAmount();
                    accountingArticleAmount.setAccountingArticle(accountingArticleService.findById((Long) amount[0]));
                    accountingArticleAmount.setAmount((BigDecimal) amount[1]);
                    accountingArticleAmount.setRatedTransactionCount(BigDecimal.valueOf((Long) amount[2]));
                    accountingArticleAmount.setBillingRunReport(billingRunReport);
                    accountingArticleAmountService.create(accountingArticleAmount);
                    accountingArticleAmounts.add(accountingArticleAmount);
                }
            }
            return accountingArticleAmounts;
        }
        return emptyList();
    }

    /**
     * generate billing run report
     * @param billingRun
     */
    public void generateBillingRunReport(BillingRun billingRun) {
        if (billingRun.isPreReportAutoOnCreate() && !billingRun.hasPreInvoicingReport()) {
            try {
                Map<String, Object> jobParams = new HashMap<>();
                jobParams.put("billingRuns",  Arrays.asList(new EntityReferenceWrapper("org.meveo.model.billing.BillingRun", "BillingRun", billingRun.getId().toString())));
                JobInstance jobInstance = jobInstanceService.findByCode(BILLING_RUN_REPORT_JOB_CODE);
                jobInstance.setRunTimeValues(jobParams);
                jobExecutionService.executeJob(jobInstance, jobParams, API);
            } catch (Exception exception) {
                throw new BusinessException("Exception occurred during during report generation : "
                        + exception.getMessage(), exception.getCause());
            }
        }
    }

    public Optional<BillingRunReport> findBillingReportBy(BillingRun billingRun) {
        List<BillingRunReport> billingRunReports = getEntityManager()
                .createNamedQuery("BillingRunReport.findAssociatedReportToBillingRun")
                .setParameter("billingRunId", billingRun.getId())
                .setMaxResults(1)
                .getResultList();
        if(billingRunReports != null && !billingRunReports.isEmpty()) {
            return of(billingRunReports.get(0));
        } else {
            return empty();
        }
    }

    public BillingRunReport createOrUpdate(BillingRunReport billingRunReport) {
        if (billingRunReport.getId() == null) {
            create(billingRunReport);
        } else {
            update(billingRunReport);
        }
        return billingRunReport;
    }
}
