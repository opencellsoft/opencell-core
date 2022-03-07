package org.meveo.admin.job;

import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
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
    private ChargeInstanceService chargeInstanceService = (ChargeInstanceService) getServiceInterface(ChargeInstanceService.class.getSimpleName());

    private TaxService taxService = (TaxService) getServiceInterface(TaxService.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @param record        map of ratedTransaction
     * @param configuration aggregation configuration
     * @param result        JobExecutionResultImpl
     * @param billingRun
     * @return new InvoiceLine
     */
    public InvoiceLine create(Map<String, Object> record, AggregationConfiguration configuration, JobExecutionResultImpl result, Provider appProvider, BillingRun billingRun) throws BusinessException {
        InvoiceLine invoiceLine = initInvoiceLine(record, result, appProvider, billingRun, configuration.isEnterprise());
        return invoiceLine;
    }

    private InvoiceLine initInvoiceLine(Map<String, Object> record, JobExecutionResultImpl report, Provider appProvider, BillingRun billingRun, boolean isEnterprise) {
        InvoiceLine invoiceLine = new InvoiceLine();
        ofNullable(record.get("billing_account__id")).ifPresent(id -> invoiceLine.setBillingAccount(billingAccountService.getEntityManager().getReference(BillingAccount.class, id)));
        ofNullable(record.get("billing_run_id")).ifPresent(id -> invoiceLine.setBillingRun(billingRunService.getEntityManager().getReference(BillingRun.class, id)));
        ofNullable(record.get("service_instance_id")).ifPresent(id -> invoiceLine.setServiceInstance(instanceService.getEntityManager().getReference(ServiceInstance.class, id)));
        ofNullable(record.get("offer_id")).ifPresent(id -> invoiceLine.setOfferTemplate(offerTemplateService.getEntityManager().getReference(OfferTemplate.class, id)));
        ofNullable(record.get("order_id")).ifPresent(id -> invoiceLine.setCommercialOrder(commercialOrderService.getEntityManager().getReference(CommercialOrder.class, id)));
        ofNullable(record.get("product_version_id")).ifPresent(id -> invoiceLine.setProductVersion(productVersionService.getEntityManager().getReference(ProductVersion.class, id)));
        ofNullable(record.get("order_lot_id")).ifPresent(id -> invoiceLine.setOrderLot(orderLotService.getEntityManager().getReference(OrderLot.class, id)));
        ofNullable(record.get("tax_id")).ifPresent(id -> invoiceLine.setTax(taxService.getEntityManager().getReference(Tax.class, id)));

        Date usageDate = getUsageDate((String) record.get("usage_date"));
        invoiceLine.setValueDate(usageDate);
        if (invoiceLine.getValueDate() == null) {
            invoiceLine.setValueDate(new Date());
        }
        invoiceLine.setOrderNumber((String) record.get("order_number"));
        invoiceLine.setQuantity((BigDecimal) record.get("quantity"));
        invoiceLine.setDiscountAmount(ZERO);
        invoiceLine.setDiscountRate(ZERO);
        invoiceLine.setBillingRun(billingRun);
        BigDecimal taxPercent = invoiceLine.getTax() != null ? invoiceLine.getTax().getPercent() : (BigDecimal) record.get("tax_percent");
        invoiceLine.setTaxRate(taxPercent);
        BigDecimal amountWithoutTax = ofNullable((BigDecimal) record.get("sum_without_tax")).orElse(ZERO);
        BigDecimal amountWithTax = ofNullable((BigDecimal) record.get("sum_with_tax")).orElse(ZERO);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amountWithoutTax, amountWithTax, taxPercent, appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());
        invoiceLine.setAmountWithoutTax(amounts[0]);
        invoiceLine.setAmountWithTax(amounts[1]);
        invoiceLine.setAmountTax(amounts[2]);

        invoiceLine.setUnitPrice(isEnterprise ? (BigDecimal) record.getOrDefault("unit_amount_without_tax", ZERO) : (BigDecimal) record.getOrDefault("unit_amount_with_tax", ZERO));
        invoiceLine.setRawAmount(isEnterprise ? amountWithoutTax : amountWithTax);
        DatePeriod validity = new DatePeriod();
        validity.setFrom(ofNullable((Date) record.get("start_date")).orElse(usageDate));
        validity.setTo(ofNullable((Date) record.get("end_date")).orElse(null));
        if(record.get("subscription_id") != null) {
            Subscription subscription = subscriptionService.getEntityManager().getReference(Subscription.class, record.get("subscription_id"));
            invoiceLine.setSubscription(subscription);
            if(record.get("commercial_order_id") != null) {
            	invoiceLine.setCommercialOrder(commercialOrderService.getEntityManager().getReference(CommercialOrder.class, record.get("subscription_id")));
            }
        }
        invoiceLine.setValidity(validity);

        if (record.get("charge_instance_id") != null && invoiceLine.getAccountingArticle() == null) {
        	ChargeInstance chargeInstance = (ChargeInstance) chargeInstanceService.findById((Long) record.get("charge_instance_id"));
            ServiceInstance serviceInstance = invoiceLine.getServiceInstance();
            Product product = serviceInstance != null ? serviceInstance.getProductVersion() != null ? invoiceLine.getServiceInstance().getProductVersion().getProduct() : null : null;
            List<AttributeValue> attributeValues = fromAttributeInstances(serviceInstance);
            Map<String, Object> attributes = fromAttributeValue(attributeValues);
            AccountingArticle accountingArticle = accountingArticleService.getAccountingArticle(product, chargeInstance.getChargeTemplate(), attributes)
                    .orElseThrow(() -> new BusinessException("No accountingArticle found"));
            invoiceLine.setAccountingArticle(accountingArticle);
        }
        invoiceLine.setLabel(invoiceLine.getAccountingArticle()!=null?invoiceLine.getAccountingArticle().getDescription() : (String) record.get("label"));
        return invoiceLine;
    }

    private Date getUsageDate(String usageDateString) {
        try {
            if (usageDateString != null) {
                if (usageDateString.length() == 7) {
                    usageDateString = usageDateString.concat("-01");
                }
                return DateUtils.parseDate(usageDateString);
            }
        } catch (Exception e) {
            log.error("cannot parse this {} to date", usageDateString);
        }
        return null;
    }

    private List<AttributeValue> fromAttributeInstances(ServiceInstance serviceInstance) {
        if (serviceInstance == null) {
            return Collections.emptyList();
        }
        return serviceInstance.getAttributeInstances().stream().map(attributeInstance -> (AttributeValue) attributeInstance).collect(toList());
    }

    private Map<String, Object> fromAttributeValue(List<AttributeValue> attributeValues) {
        return attributeValues
                .stream()
                .filter(attributeValue -> attributeValue.getAttribute().getAttributeType().getValue(attributeValue) != null)
                .collect(toMap(key -> key.getAttribute().getCode(),
                        value -> value.getAttribute().getAttributeType().getValue(value)));
    }

}