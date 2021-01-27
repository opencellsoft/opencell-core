package org.meveo.admin.job;

import org.meveo.model.DatePeriod;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.article.AccountingArticleService1;
import org.meveo.service.catalog.impl.OfferTemplateService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.meveo.admin.job.AggregationConfiguration.AggregationOption.NO_AGGREGATION;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

public class InvoiceLinesFactory {

    private BillingAccountService billingAccountService =
            (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());
    private BillingRunService billingRunService =
            (BillingRunService) getServiceInterface(BillingRunService.class.getSimpleName());
    private AccountingArticleService1 accountingArticleService =
            (AccountingArticleService1) getServiceInterface(AccountingArticleService1.class.getSimpleName());
    private OfferTemplateService offerTemplateService =
            (OfferTemplateService) getServiceInterface(OfferTemplateService.class.getSimpleName());
    private ServiceInstanceService instanceService =
            (ServiceInstanceService) getServiceInterface(ServiceInstanceService.class.getSimpleName());
    private SubscriptionService subscriptionService =
            (SubscriptionService) getServiceInterface(SubscriptionService.class.getSimpleName());

    public InvoiceLine create(Map<String, Object> record, AggregationConfiguration configuration) {
        InvoiceLine invoiceLine = initInvoiceLine(record);
        if(configuration.getAggregationOption() == NO_AGGREGATION) {
            withNoAggregationOption(invoiceLine, record, configuration.isEnterprise());
        } else {
            withAggregationOption(invoiceLine, record, configuration.isEnterprise());
        }
        return invoiceLine;
    }

    private InvoiceLine initInvoiceLine(Map<String, Object> record) {
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setCode("INV_LINE_" + randomUUID());
        ofNullable(record.get("billing_account__id"))
                .ifPresent(id -> invoiceLine.setBillingAccount(billingAccountService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("billing_run_id"))
                .ifPresent(id -> invoiceLine.setBillingRun(billingRunService.findById(((BigInteger) id).longValue())));
         ofNullable(record.get("article_id"))
                .ifPresent(id -> invoiceLine.setAccountingArticle(accountingArticleService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("service_instance_id"))
                .ifPresent(id -> invoiceLine.setServiceInstance(instanceService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("service_instance_id"))
                .ifPresent(id -> invoiceLine.setServiceInstance(instanceService.findById(((BigInteger) id).longValue())));
        ofNullable(record.get("offer_id"))
                .ifPresent(id -> invoiceLine.setOfferTemplate(offerTemplateService.findById(((BigInteger) id).longValue())));
        invoiceLine.setValueDate((Date) record.get("valueDate"));
        invoiceLine.setOrderNumber((String) record.get("order_number"));
        invoiceLine.setQuantity((BigDecimal) record.get("quantity"));
        invoiceLine.setDiscountAmount(BigDecimal.ZERO);
        invoiceLine.setDiscountRate(BigDecimal.ZERO);
        BigDecimal taxPercent = (BigDecimal) record.get("tax_percent");
        invoiceLine.setTaxRate(taxPercent);
        BigDecimal amountWithTax = (BigDecimal) record.get("amount_with_tax");
        invoiceLine.setAmountWithTax(amountWithTax);
        invoiceLine.setAmountWithoutTax((BigDecimal) record.get("amount_without_tax"));
        invoiceLine.setAmountTax(taxPercent.divide(new BigDecimal(100)).multiply(amountWithTax));
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
        ofNullable(subscription.getOrder()).ifPresent(order -> invoiceLine.setCommercialOrder(order));
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
}