package org.meveo.admin.job;

import static java.math.BigDecimal.ZERO;
import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.meveo.admin.job.AggregationConfiguration.AggregationOption.NO_AGGREGATION;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.DatePeriod;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.*;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderLotService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InvoiceLinesFactory {

    private BillingAccountService billingAccountService =
            (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());
    private BillingRunService billingRunService =
            (BillingRunService) getServiceInterface(BillingRunService.class.getSimpleName());
    private AccountingArticleService accountingArticleService =
            (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private OfferTemplateService offerTemplateService =
            (OfferTemplateService) getServiceInterface(OfferTemplateService.class.getSimpleName());
    private ServiceInstanceService instanceService =
            (ServiceInstanceService) getServiceInterface(ServiceInstanceService.class.getSimpleName());
    private SubscriptionService subscriptionService =
            (SubscriptionService) getServiceInterface(SubscriptionService.class.getSimpleName());
    private CommercialOrderService commercialOrderService = 
    		(CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());
    private ProductVersionService productVersionService = 
    		(ProductVersionService) getServiceInterface(ProductVersionService.class.getSimpleName());
    private OrderLotService orderLotService = 
    		(OrderLotService) getServiceInterface(OrderLotService.class.getSimpleName());
    private ChargeInstanceService chargeInstanceService =
            (ChargeInstanceService) getServiceInterface(ChargeInstanceService.class.getSimpleName());
    private RatedTransactionService ratedTransactionService =
            (RatedTransactionService) getServiceInterface(RatedTransactionService.class.getSimpleName());

    /**
     *
     * @param record map of ratedTransaction
     * @param configuration aggregation configuration
     * @param result JobExecutionResultImpl
     * @return new InvoiceLine
     */
    public InvoiceLine create(Map<String, Object> record,
                              AggregationConfiguration configuration, JobExecutionResultImpl result) throws BusinessException {
        InvoiceLine invoiceLine = initInvoiceLine(record, result);
        if(configuration.getAggregationOption() == NO_AGGREGATION) {
            withNoAggregationOption(invoiceLine, record, configuration.isEnterprise());
        } else {
            withAggregationOption(invoiceLine, record, configuration.isEnterprise());
        }
        return invoiceLine;
    }

    private InvoiceLine initInvoiceLine(Map<String, Object> record, JobExecutionResultImpl report) {
        InvoiceLine invoiceLine = new InvoiceLine();
        BigInteger rtID = (BigInteger) record.get("id");
        ofNullable(record.get("billing_account__id"))
                .ifPresent(id -> invoiceLine.setBillingAccount(billingAccountService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("id"))
                .ifPresent(id ->
                        invoiceLine.setRatedTransactions(of(ratedTransactionService.findById(((BigInteger) id).longValue()))));
        ofNullable(record.get("billing_run_id"))
                .ifPresent(id -> invoiceLine.setBillingRun(billingRunService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("service_instance_id"))
                .ifPresent(id -> invoiceLine.setServiceInstance(instanceService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("service_instance_id"))
                .ifPresent(id -> invoiceLine.setServiceInstance(instanceService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("offer_id"))
                .ifPresent(id -> invoiceLine.setOfferTemplate(offerTemplateService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("order_id"))
        .ifPresent(id -> invoiceLine.setCommercialOrder(commercialOrderService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("product_version_id"))
        .ifPresent(id -> invoiceLine.setProductVersion(productVersionService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("order_lot_id"))
        .ifPresent(id -> invoiceLine.setOrderLot(orderLotService.findById(((BigInteger) id).longValue())));

        invoiceLine.setValueDate((Date) record.get("usage_date"));
        if(invoiceLine.getValueDate()==null) {
        	invoiceLine.setValueDate(new Date());
        }
        invoiceLine.setOrderNumber((String) record.get("order_number"));
        invoiceLine.setQuantity((BigDecimal) record.get("quantity"));
        invoiceLine.setDiscountAmount(ZERO);
        invoiceLine.setDiscountRate(ZERO);
        BigDecimal taxPercent = (BigDecimal) record.get("tax_percent");
        invoiceLine.setTaxRate(taxPercent);
        BigDecimal amountWithTax = ofNullable((BigDecimal) record.get("sum_with_tax"))
                .orElse(ZERO);
        invoiceLine.setAmountWithTax(amountWithTax);
        BigDecimal amountWithoutTax = ofNullable((BigDecimal) record.get("sum_without_Tax"))
                .orElse(ZERO);
        if(BigDecimal.ZERO.compareTo(amountWithoutTax)==0) {
        	BigDecimal coef=(new BigDecimal(100).add((taxPercent))).divide(new BigDecimal(100));
        	amountWithoutTax=amountWithTax.divide(coef, 2, RoundingMode.HALF_UP);
        }
        invoiceLine.setAmountWithoutTax(amountWithoutTax);
        invoiceLine.setAmountTax(taxPercent.divide(new BigDecimal(100)).multiply(amountWithoutTax));

        ChargeInstance chargeInstance = (ChargeInstance) ofNullable(record.get("charge_instance_id"))
                .map(id -> chargeInstanceService.findById(((BigInteger) id).longValue()))
                .orElse(null);
        if (chargeInstance != null) {
                ServiceInstance serviceInstance = invoiceLine.getServiceInstance();
                Product product = serviceInstance != null ? serviceInstance.getProductVersion() != null ?
                        invoiceLine.getServiceInstance().getProductVersion().getProduct() : null : null;
                List<AttributeValue> attributeValues = fromAttributeInstances(serviceInstance);
                Map<String, Object> attributes = fromAttributeValue(attributeValues);
                if(invoiceLine.getAccountingArticle()==null) {
                    AccountingArticle accountingArticle = accountingArticleService.getAccountingArticle(product, chargeInstance.getChargeTemplate(), attributes)
                            .orElseThrow(() -> new BusinessException("No accountingArticle found"));
                    invoiceLine.setAccountingArticle(accountingArticle);
                }
        }
        return invoiceLine;
    }

    private void withNoAggregationOption(InvoiceLine invoiceLine, Map<String, Object> record, boolean isEnterprise) {
        invoiceLine.setLabel((String) record.get("label"));
        invoiceLine.setUnitPrice(isEnterprise ? (BigDecimal) record.get("unit_amount_without_tax")
                : (BigDecimal) record.get("unit_amount_with_tax"));
        invoiceLine.setRawAmount(isEnterprise ? (BigDecimal) record.get("unit_amount_without_tax")
                : (BigDecimal) record.get("unit_amount_without_tax"));
        DatePeriod validity = new DatePeriod();
        validity.setFrom(ofNullable((Date) record.get("start_date")).orElse((Date) record.get("usage_date")));
        validity.setTo(ofNullable((Date) record.get("end_date")).orElse(null));
        Subscription subscription = subscriptionService.findById(((BigInteger) record.get("subscription_id")).longValue());
        invoiceLine.setValidity(validity);
        ofNullable(subscription)
                .ifPresent(id -> invoiceLine.setSubscription(subscription));
        ofNullable(subscription).ifPresent(sub -> ofNullable(sub.getOrder()).ifPresent(order -> invoiceLine.setCommercialOrder(order)));
    }

    private void withAggregationOption(InvoiceLine invoiceLine, Map<String, Object> record, boolean isEnterprise) {
        invoiceLine.setLabel((invoiceLine.getAccountingArticle() != null)
                ? invoiceLine.getAccountingArticle().getDescription() : (String) record.get("label"));
        invoiceLine.setUnitPrice(isEnterprise ? (BigDecimal) record.get("sum_amount_without_tax") :
                (BigDecimal) record.get("unit_price"));
        invoiceLine.setRawAmount(isEnterprise ? (BigDecimal) record.get("amount_without_tax")
                : (BigDecimal) record.get("amount_with_tax"));
        DatePeriod validity = new DatePeriod();
        validity.setFrom((Date) record.get("start_date"));
        validity.setTo((Date) record.get("end_date"));
        invoiceLine.setValidity(validity);
    }

    private List<AttributeValue> fromAttributeInstances(ServiceInstance serviceInstance) {
    	if(serviceInstance == null) return Collections.emptyList();
        return serviceInstance.getAttributeInstances()
                    .stream()
                    .map(attributeInstance -> (AttributeValue) attributeInstance)
                    .collect(toList());
    }

    private Map<String, Object> fromAttributeValue(List<AttributeValue> attributeValues) {
        return attributeValues
                .stream()
                .filter(attributeValue -> attributeValue.getAttribute().getAttributeType().getValue(attributeValue) != null)
                .collect(toMap(key -> key.getAttribute().getCode(),
                        value -> value.getAttribute().getAttributeType().getValue(value)));
    }

}