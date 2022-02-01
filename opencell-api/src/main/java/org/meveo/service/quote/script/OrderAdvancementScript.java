package org.meveo.service.quote.script;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.cpq.CommercialOrderApi;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.InvoicingPlanItem;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderPriceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class
OrderAdvancementScript extends ModuleScript {

    private OrderPriceService orderPriceService = (OrderPriceService) getServiceInterface(OrderPriceService.class.getSimpleName());
    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());
    private CommercialOrderApi commercialOrderApi = (CommercialOrderApi) getServiceInterface(CommercialOrderApi.class.getSimpleName());
    private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private InvoiceLineService invoiceLinesService = (InvoiceLineService) getServiceInterface(InvoiceLineService.class.getSimpleName());
    private InvoiceService invoiceService = (InvoiceService) getServiceInterface(InvoiceService.class.getSimpleName());
    private InvoiceTypeService invoiceTypeService = (InvoiceTypeService) getServiceInterface(InvoiceTypeService.class.getSimpleName());
    private CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        CommercialOrder commercialOrder = (CommercialOrder) methodContext.get("commercialOrder");
        if(commercialOrder == null) {
            throw new BusinessException("No Commercial order is found");
        }
        Integer orderProgress = commercialOrder.getOrderProgress() != null ? commercialOrder.getOrderProgress() : 0;

        if (commercialOrder.getInvoicingPlan() != null) {

            Date nextDay = java.sql.Date.valueOf(LocalDate.now().plusDays(1));
            Date firstTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
            Date invoiceDate = new Date();
            List<OrderPrice> pricesToBill = orderPriceService.findByOrder(commercialOrder).stream()
                    .filter(this::isPriceRelatedToOneShotChargeTemplateOfTypeOther)
                    .collect(Collectors.toList());

            if (pricesToBill.isEmpty()) {
                log.info("No order prices to bill related to a one shot charge were found for commercial order: " + commercialOrder.getId());
                if(orderProgress == 100) {
                	commercialOrderApi.validateOrder(commercialOrder, true);
                }
                return;
            }

            AccountingArticle defaultAccountingArticle = getDefaultAccountingArticle();

            BigDecimal totalAmountWithoutTax = pricesToBill.stream()
                    .map(OrderPrice::getAmountWithoutTax)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal totalAmountWithTax = pricesToBill.stream()
                    .map(OrderPrice::getAmountWithTax)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal totalTax = pricesToBill.stream()
                    .map(OrderPrice::getTaxAmount)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal totalTaxRate = pricesToBill.stream()
                    .map(OrderPrice::getTaxRate)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            OrderProduct orderProduct = pricesToBill.get(0).getOrderArticleLine().getOrderProduct();



            if(orderProgress == 100) {
                if(commercialOrder.getRateInvoiced() < 100) {
                    if(isOneShot100Payment(commercialOrder.getInvoicingPlan().getInvoicingPlanItems())){
                        createAccountInvoice(commercialOrder, nextDay, firstTransactionDate, invoiceDate, defaultAccountingArticle, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate, orderProduct, true);
                    }
                    generateGlobalInvoice(commercialOrder, nextDay, firstTransactionDate, invoiceDate, defaultAccountingArticle, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate, orderProduct);
                    commercialOrder.setOrderProgressTmp(orderProgress);
                    commercialOrder.setRateInvoiced(100);
                }
                commercialOrderApi.validateOrder(commercialOrder, true);
            } else {

                List<InvoicingPlanItem> itemsToBill = commercialOrder.getInvoicingPlan().getInvoicingPlanItems().stream()
                        .filter(item -> orderProgress.equals(item.getAdvancement()))
                        .collect(Collectors.toList());
                if (itemsToBill.isEmpty()) {
                    log.info("No invoicing plan item found for the order progress: " + orderProgress + " commercial order id: " + commercialOrder.getId());
                    return;
                } else if (itemsToBill.size() > 1)
                    throw new BusinessException("Many invoicing plan items are set for the advancement: " + orderProgress + " using the invoicing plan: " + commercialOrder.getInvoicingPlan().getCode());

                InvoicingPlanItem invoicingPlanItem = itemsToBill.get(0);

                BigDecimal newRateInvoiced = invoicingPlanItem.getRateToBill().add(BigDecimal.valueOf(commercialOrder.getRateInvoiced()));
                if (newRateInvoiced.compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new BusinessException("the invoicing plan rate is grater than remaining rate to invoice");
                }
                if(newRateInvoiced.compareTo(BigDecimal.valueOf(100)) == 0){
                    generateGlobalInvoice(commercialOrder, nextDay, firstTransactionDate, invoiceDate, defaultAccountingArticle, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate, orderProduct);
                }else {
                    BigDecimal amountWithoutTaxToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithoutTax);
                    BigDecimal amountWithTaxToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithTax);
                    BigDecimal taxAmountToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalTax);

                    createAccountInvoice(commercialOrder, nextDay, firstTransactionDate, invoiceDate, defaultAccountingArticle, amountWithoutTaxToBeInvoiced, amountWithTaxToBeInvoiced, taxAmountToBeInvoiced, totalTaxRate, orderProduct, true);
                }
                commercialOrder.setRateInvoiced(newRateInvoiced.intValue());
                commercialOrder.setOrderProgressTmp(orderProgress);
                commercialOrderService.update(commercialOrder);
            }
        }


    }

    private void createAccountInvoice(CommercialOrder commercialOrder, Date nextDay, Date firstTransactionDate, Date invoiceDate, AccountingArticle defaultAccountingArticle, BigDecimal totalAmountWithoutTax, BigDecimal totalAmountWithTax, BigDecimal totalTax, BigDecimal totalTaxRate, OrderProduct orderProduct, boolean isDepositInvoice) {
        createInvoiceLine(commercialOrder, defaultAccountingArticle, orderProduct, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate);
        List<Invoice> invoices = invoiceService.createAggregatesAndInvoiceWithIL(commercialOrder, null, null, invoiceDate, firstTransactionDate, nextDay, null, false, false, isDepositInvoice);
        invoices.stream()
                .forEach(
                        invoice -> {
                            customFieldInstanceService.instantiateCFWithDefaultValue(invoice);
                            invoiceService.update(invoice);
                        }
                );
    }

    private boolean isOneShot100Payment(List<InvoicingPlanItem> invoicingPlanItems) {
        return invoicingPlanItems.size() == 1 && invoicingPlanItems.get(0).getRateToBill().doubleValue() == BigDecimal.valueOf(100).doubleValue();
    }

    private void generateGlobalInvoice(CommercialOrder commercialOrder, Date nextDay, Date firstTransactionDate, Date invoiceDate, AccountingArticle defaultAccountingArticle, BigDecimal totalAmountWithoutTax, BigDecimal totalAmountWithTax, BigDecimal totalTax, BigDecimal totalTaxRate, OrderProduct orderProduct) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        List<AttributeValue> orderAttributes = orderProduct.getOrderAttributes().stream().map(oa -> (AttributeValue)oa).collect(Collectors.toList());
        for (AttributeValue orderAttribute : orderAttributes) {
            Attribute attribute = orderAttribute.getAttribute();
            Object value = attribute.getAttributeType().getValue(orderAttribute);
            if (value != null) {
                attributes.put(orderAttribute.getAttribute().getCode(), value);
            }
        }
        Product product = orderProduct.getProductVersion().getProduct();
        Optional<AccountingArticle> accountingArticle = accountingArticleService.getAccountingArticle(product, attributes);
        if (!accountingArticle.isPresent())
            throw new BusinessException("No accounting article found for product code: " + product.getCode() + " and attributes: " + attributes.toString());

        List<InvoiceLine> accounts = invoiceLinesService.findByCommercialOrder(commercialOrder);
        for(InvoiceLine account : accounts){
            createInvoiceLine(commercialOrder, defaultAccountingArticle, orderProduct, account.getAmountWithoutTax().negate(), account.getAmountWithTax().negate(), account.getAmountTax().negate(), account.getTaxRate());
        }

        createAccountInvoice(commercialOrder, nextDay, firstTransactionDate, invoiceDate, accountingArticle.get(), totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate, orderProduct, false);


    }

    private AccountingArticle getDefaultAccountingArticle() {
        String articleCode = ParamBean.getInstance().getProperty("accountingArticle.advancePayment.defautl.code", "ADV-STD");

        AccountingArticle accountingArticle = accountingArticleService.findByCode(articleCode);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);
        return accountingArticle;
    }

    private void createInvoiceLine(CommercialOrder commercialOrder, AccountingArticle accountingArticle, OrderProduct orderProduct, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        invoiceLinesService.createInvoiceLine(commercialOrder, accountingArticle, orderProduct.getProductVersion(),orderProduct.getOrderServiceCommercial(), orderProduct.getOrderOffer().getOfferTemplate(),orderProduct.getOrderOffer(), amountWithoutTaxToBeInvoiced, amountWithTaxToBeInvoiced, taxAmountToBeInvoiced, totalTaxRate);
    }

    private boolean isPriceRelatedToOneShotChargeTemplateOfTypeOther(OrderPrice price) {
        return price.getChargeTemplate().getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.ONESHOT
                && ((OneShotChargeTemplate) price.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER;
    }


}
