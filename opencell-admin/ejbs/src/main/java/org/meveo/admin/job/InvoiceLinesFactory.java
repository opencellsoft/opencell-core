package org.meveo.admin.job;

import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderLotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceLinesFactory {

    private BillingAccountService billingAccountService = (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());
    private BillingRunService billingRunService = (BillingRunService) getServiceInterface(BillingRunService.class.getSimpleName());
    private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private OfferTemplateService offerTemplateService = (OfferTemplateService) getServiceInterface(OfferTemplateService.class.getSimpleName());
    private ServiceInstanceService instanceService = (ServiceInstanceService) getServiceInterface(ServiceInstanceService.class.getSimpleName());
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface(SubscriptionService.class.getSimpleName());
    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());
    private ProductVersionService productVersionService = (ProductVersionService) getServiceInterface(ProductVersionService.class.getSimpleName());
    private OrderLotService orderLotService = (OrderLotService) getServiceInterface(OrderLotService.class.getSimpleName());
    private RatedTransactionService ratedTransactionService = (RatedTransactionService) getServiceInterface(RatedTransactionService.class.getSimpleName());
    private UserAccountService userAccountService = (UserAccountService) getServiceInterface(UserAccountService.class.getSimpleName());
    private TaxService taxService = (TaxService) getServiceInterface(TaxService.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private InvoiceLineService invoiceLineService =
            (InvoiceLineService) getServiceInterface(InvoiceLineService.class.getSimpleName());
    

    /**
     * @param data        map of ratedTransaction
     * @param configuration aggregation configuration
     * @param result        JobExecutionResultImpl
     * @param billingRun
     * @param openOrderNumber
     * @return new InvoiceLine
     */
    public InvoiceLine create(Map<String, Object> data, Map<Long, Long> iLIdsRtIdsCorrespondence,
                              AggregationConfiguration configuration, JobExecutionResultImpl result,
                              Provider appProvider, BillingRun billingRun, String openOrderNumber) throws BusinessException {
        return initInvoiceLine(data, iLIdsRtIdsCorrespondence,
                appProvider, billingRun, configuration, openOrderNumber);
    }

    private InvoiceLine initInvoiceLine(Map<String, Object> data, Map<Long, Long> iLIdsRtIdsCorrespondence,
                                        Provider appProvider, BillingRun billingRun,
                                        AggregationConfiguration configuration, String openOrderNumber) {
        InvoiceLine invoiceLine = new InvoiceLine();
        String rtID = (String) data.get("rated_transaction_ids");
        ofNullable(data.get("billing_account__id")).ifPresent(id -> invoiceLine.setBillingAccount(billingAccountService.getEntityManager().getReference(BillingAccount.class, ((Number)id).longValue())));
        ofNullable(data.get("billing_run_id")).ifPresent(id -> invoiceLine.setBillingRun(billingRunService.getEntityManager().getReference(BillingRun.class, ((Number)id).longValue())));
        ofNullable(data.get("service_instance_id")).ifPresent(id -> invoiceLine.setServiceInstance(instanceService.getEntityManager().getReference(ServiceInstance.class, ((Number)id).longValue())));
        ofNullable(data.get("user_account_id")).ifPresent(id -> invoiceLine.setUserAccount(userAccountService.getEntityManager().getReference(UserAccount.class, id)));
        ofNullable(data.get("offer_id")).ifPresent(id -> invoiceLine.setOfferTemplate(offerTemplateService.getEntityManager().getReference(OfferTemplate.class, ((Number)id).longValue())));
        ofNullable(data.get("order_id")).ifPresent(id -> invoiceLine.setCommercialOrder(commercialOrderService.getEntityManager().getReference(CommercialOrder.class, ((Number)id).longValue())));
        ofNullable(data.get("product_version_id")).ifPresent(id -> invoiceLine.setProductVersion(productVersionService.getEntityManager().getReference(ProductVersion.class, ((Number)id).longValue())));
        ofNullable(data.get("order_lot_id")).ifPresent(id -> invoiceLine.setOrderLot(orderLotService.getEntityManager().getReference(OrderLot.class, ((Number)id).longValue())));
        ofNullable(data.get("tax_id")).ifPresent(id -> invoiceLine.setTax(taxService.getEntityManager().getReference(Tax.class, ((Number)id).longValue())));
        ofNullable(data.get("article_id")).ifPresent(id -> invoiceLine.setAccountingArticle(accountingArticleService.getEntityManager().getReference(AccountingArticle.class, (Number)id)));
        ofNullable(data.get("discount_plan_type")).ifPresent(id -> invoiceLine.setDiscountPlanType((DiscountPlanItemTypeEnum) data.get("discount_plan_type")));
        ofNullable(data.get("discount_value")).ifPresent(id -> invoiceLine.setDiscountValue((BigDecimal) data.get("discount_value")));
        log.debug("discounted_Ratedtransaction_id={},{}",data.get("discounted_ratedtransaction_id"),iLIdsRtIdsCorrespondence.size());
        if(data.get("discounted_ratedtransaction_id")!=null) {
        	Long discountedILId = iLIdsRtIdsCorrespondence.get(((Number)data.get("discounted_ratedtransaction_id")).longValue());
        		log.debug("discountedRatedTransaction discountedILId={}",discountedILId);
        		if(discountedILId!=null) {
        			InvoiceLine discountedIL = invoiceLineService.findById(discountedILId);
            		invoiceLine.setDiscountedInvoiceLine(discountedIL);
            		String[] splitrtId = rtID.split(",");
            		for (String id : splitrtId) {
            		    RatedTransaction discountRatedTransaction = ratedTransactionService.findById(Long.valueOf(id));
                        if(discountRatedTransaction!=null) {
                            invoiceLine.setDiscountPlan(discountRatedTransaction.getDiscountPlan());
                            invoiceLine.setDiscountPlanItem(discountRatedTransaction.getDiscountPlanItem());
                            invoiceLine.setSequence(discountRatedTransaction.getSequence());
                            invoiceLine.setDiscountAmount(invoiceLine.getDiscountAmount()
                                    .add(discountRatedTransaction.getDiscountValue()));
                            break;
                        }
                    }
            		
        		}
        		
        }

        Date usageDate = getUsageDate(data.get("usage_date"), configuration.getDateAggregationOption());
        invoiceLine.setValueDate(usageDate);
        if (invoiceLine.getValueDate() == null) {
            invoiceLine.setValueDate(new Date());
        }
        invoiceLine.setOrderNumber((String) data.get("order_number"));
        invoiceLine.setQuantity((BigDecimal) data.get("quantity"));
        invoiceLine.setDiscountRate(ZERO);
        invoiceLine.setBillingRun(billingRun);
        BigDecimal taxPercent = invoiceLine.getTax() != null ? invoiceLine.getTax().getPercent() : (BigDecimal) data.get("tax_percent");
        invoiceLine.setTaxRate(taxPercent);
        BigDecimal amountWithoutTax = ofNullable((BigDecimal) data.get("sum_without_tax")).orElse(ZERO);
        BigDecimal amountWithTax = ofNullable((BigDecimal) data.get("sum_with_tax")).orElse(ZERO);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amountWithoutTax, amountWithTax, taxPercent, appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());
        invoiceLine.setAmountWithoutTax(amounts[0]);
        invoiceLine.setAmountWithTax(amounts[1]);
        invoiceLine.setAmountTax(amounts[2]);

        boolean isEnterprise = configuration.isEnterprise();
        if(billingRun != null
                && billingRun.getBillingCycle() != null
                && !billingRun.getBillingCycle().isDisableAggregation()
                && billingRun.getBillingCycle().isAggregateUnitAmounts()) {
            BigDecimal unitAmount = (BigDecimal) data.getOrDefault("sum_without_tax", ZERO);
            BigDecimal quantity = (BigDecimal) data.getOrDefault("quantity", ZERO);
            MathContext mc = new MathContext(appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
            BigDecimal unitPrice = quantity.compareTo(ZERO) == 0 ? unitAmount : unitAmount.divide(quantity, mc);
            invoiceLine.setUnitPrice(unitPrice);
        } else {
            invoiceLine.setUnitPrice(isEnterprise ? (BigDecimal) data.getOrDefault("unit_amount_without_tax", ZERO)
                    : (BigDecimal) data.getOrDefault("unit_amount_with_tax", ZERO));
        }
        invoiceLine.setRawAmount(isEnterprise ? amountWithoutTax : amountWithTax);
        DatePeriod validity = new DatePeriod();
        validity.setFrom(ofNullable((Date) data.get("start_date")).orElse(usageDate));
        validity.setTo(ofNullable((Date) data.get("end_date")).orElse(null));
        if(data.get("subscription_id") != null) {
            Subscription subscription = subscriptionService.getEntityManager().getReference(Subscription.class, ((Number) data.get("subscription_id")).longValue());
            invoiceLine.setSubscription(subscription);
            if(data.get("commercial_order_id") != null) {
                invoiceLine.setCommercialOrder(commercialOrderService.getEntityManager().getReference(CommercialOrder.class, ((Number) data.get("commercial_order_id")).longValue()));
            }
        }
        invoiceLine.setValidity(validity);
        if(billingRun != null && billingRun.getBillingCycle() != null
                && billingRun.getBillingCycle().isUseAccountingArticleLabel()
                && invoiceLine.getAccountingArticle() != null) {
            String languageCode = getLanguageCode(invoiceLine.getBillingAccount(), appProvider);
            Map<String, String> descriptionsI18N = invoiceLine.getAccountingArticle().getDescriptionI18nNotNull();
            invoiceLine.setLabel(ofNullable(descriptionsI18N.get(languageCode))
                    .orElse(invoiceLine.getAccountingArticle().getDescription()));
        } else {
            String label = StringUtils.EMPTY;
            if (data.get("label") != null) {
                label = (String) data.get("label");
            } else if (invoiceLine.getAccountingArticle() != null && invoiceLine.getAccountingArticle().getDescription() != null) {
                label = invoiceLine.getAccountingArticle().getDescription();
            }
            invoiceLine.setLabel(label);
        }
        ofNullable(openOrderNumber).ifPresent(invoiceLine::setOpenOrderNumber);
        return invoiceLine;
    }

    /**
     * @param invoiceLineId id of invoice line to be updated
     * @param deltaAmounts difference of amount of tax, difference of amount with tax, difference of amount without tax
     *                     to be updated
     * @param deltaQuantity difference of quantity to be updated
     * @param beginDate beginDate
     * @param endDate endDate
     */
    public void update(Long invoiceLineId, BigDecimal[] deltaAmounts, BigDecimal deltaQuantity, Date beginDate,
                       Date endDate, BigDecimal unitPrice) throws BusinessException {
        invoiceLineService.getEntityManager()
                .createNamedQuery("InvoiceLine.updateByIncrementalMode")
                .setParameter("id", invoiceLineId).setParameter("deltaAmountWithoutTax", deltaAmounts[0])
                .setParameter("deltaAmountWithTax", deltaAmounts[1]).setParameter("deltaAmountTax", deltaAmounts[2])
                .setParameter("deltaQuantity", deltaQuantity).setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate).setParameter("now", new Date())
                .setParameter("unitPrice", unitPrice).executeUpdate();
    }

    /**
     * get usage date from string.
     *
     * @param usageDate             a date string
     * @param dateAggregationOption a date aggregation option.
     * @return a date
     */
    private Date getUsageDate(Object usageDate, DateAggregationOption dateAggregationOption) {
    	if(usageDate instanceof Date) {
    		return (Date)usageDate;
    	}
        try {
        	String usageDateString = (String) usageDate;
            if (usageDateString != null) {
                if (usageDateString.length() == 7) {
                    if (DateAggregationOption.MONTH_OF_USAGE_DATE.equals(dateAggregationOption)) {
                        usageDateString = usageDateString.concat("-01");
                    } else if (DateAggregationOption.WEEK_OF_USAGE_DATE.equals(dateAggregationOption)) {
                        int year = Integer.parseInt(usageDateString.split("-")[0]);
                        int week = Integer.parseInt(usageDateString.split("-")[1]);
                        return DateUtils.getFirstDayFromYearAndWeek(year, week);
                    }
                }
                return DateUtils.parseDate(usageDateString);
            }
        } catch (Exception e) {
            log.error("cannot parse '{}' as date", usageDate);
        }
        return null;
    }

    private String getLanguageCode(BillingAccount billingAccount, Provider provider) {
        String languageCode = billingAccount.getBillingAccountTradingLanguageCode();
        if(languageCode == null) {
            languageCode = provider.getLanguage() != null ? provider.getLanguage().getLanguageCode() : "ENG";
        }
        return languageCode;
    }

}