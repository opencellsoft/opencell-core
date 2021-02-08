package org.meveo.api.billing;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.InvoicingPlanItem;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.billing.impl.InvoiceLinesService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderPriceService;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class OrderAdvancementScript extends ModuleScript {

    private OrderPriceService orderPriceService = (OrderPriceService) getServiceInterface(OrderPriceService.class.getSimpleName());
    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());
    private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private InvoiceLinesService invoiceLinesService = (InvoiceLinesService) getServiceInterface(InvoiceLinesService.class.getSimpleName());
    private InvoiceService invoiceService = (InvoiceService) getServiceInterface(InvoiceService.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        CommercialOrder commercialOrder = (CommercialOrder) methodContext.get("commercialOrder");
        if(commercialOrder == null) {
            throw new BusinessException("No Commercial order is found");
        }
        Integer orderProgress = commercialOrder.getOrderProgress();

        if (commercialOrder.getInvoicingPlan() != null) {


            List<OrderPrice> pricesToBill = orderPriceService.findByOrder(commercialOrder).stream()
                    .filter(this::isPriceRelatedToOneShotChargeTemplateOfTypeOther)
                    .collect(Collectors.toList());

            if (pricesToBill.isEmpty()) {
                log.info("No order prices to bill related to a one shot charge were found for commercial order: " + commercialOrder.getId());
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

            if(orderProgress == 100){
                Date nextDay = java.sql.Date.valueOf(LocalDate.now().plusDays(1));
                Date firstTransactionDate = new Date();
                Date invoiceDate = firstTransactionDate;
                Map<String, Object> attributes = new HashMap<String, Object>();
                List<OrderAttribute> orderAttributes = orderProduct.getOrderAttributes();
                for (OrderAttribute attributeInstance : orderAttributes) {
                    Attribute attribute = attributeInstance.getAttribute();
                    Object value = attribute.getAttributeType().getValue(attributeInstance);
                    if (value != null) {
                        attributes.put(attributeInstance.getAttribute().getCode(), value);
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

                createInvoiceLine(commercialOrder, accountingArticle.get(), orderProduct, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate);
                commercialOrder.setOrderProgressTmp(orderProgress);
                commercialOrder.setRateInvoiced(100);
                commercialOrderService.orderValidationProcess(commercialOrder);
                invoiceService.createAggregatesAndInvoiceWithILInNewTransaction(commercialOrder.getBillingAccount(), null, null, invoiceDate, firstTransactionDate, nextDay, null, false, false);
            }else {
                List<InvoicingPlanItem> itemsToBill = commercialOrder.getInvoicingPlan().getInvoicingPlanItems().stream()
                        .filter(item -> item.getAdvancement() == orderProgress)
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
                BigDecimal amountWithoutTaxToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithoutTax);
                BigDecimal amountWithTaxToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithTax);
                BigDecimal taxAmountToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalTax);

                createInvoiceLine(commercialOrder, defaultAccountingArticle, orderProduct, amountWithoutTaxToBeInvoiced, amountWithTaxToBeInvoiced, taxAmountToBeInvoiced, totalTaxRate);
                commercialOrder.setRateInvoiced(newRateInvoiced.intValue());
            }

            commercialOrder.setOrderProgressTmp(orderProgress);
            commercialOrderService.update(commercialOrder);

        }


    }

    private AccountingArticle getDefaultAccountingArticle() {
        String articleCode = ParamBean.getInstance().getProperty("advancePayment.accountingArticleCode", "ACT-STD");

        AccountingArticle accountingArticle = accountingArticleService.findByCode(articleCode);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);
        return accountingArticle;
    }

    private void createInvoiceLine(CommercialOrder commercialOrder, AccountingArticle accountingArticle, OrderProduct orderProduct, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setCode("COMMERCIAL-GEN");
        invoiceLine.setCode(invoiceLinesService.findDuplicateCode(invoiceLine));
        invoiceLine.setAccountingArticle(accountingArticle);
        invoiceLine.setLabel(accountingArticle.getDescription());
        invoiceLine.setProduct(orderProduct.getProductVersion().getProduct());
        invoiceLine.setProductVersion(orderProduct.getProductVersion());
        invoiceLine.setCommercialOrder(commercialOrder);
        invoiceLine.setOrderLot(orderProduct.getOrderServiceCommercial());
        invoiceLine.setQuantity(BigDecimal.valueOf(1));
        invoiceLine.setUnitPrice(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithoutTax(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithTax(amountWithTaxToBeInvoiced);
        invoiceLine.setAmountTax(taxAmountToBeInvoiced);
        invoiceLine.setTaxRate(totalTaxRate);
        invoiceLine.setOrderNumber(commercialOrder.getOrderNumber());
        invoiceLine.setBillingAccount(commercialOrder.getBillingAccount());
        invoiceLine.setValueDate(new Date());
        invoiceLine.setSubscription(commercialOrder.getS);
        invoiceLinesService.create(invoiceLine);
    }

    private boolean isPriceRelatedToOneShotChargeTemplateOfTypeOther(OrderPrice price) {
        return price.getChargeTemplate().getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.ONESHOT
                && ((OneShotChargeTemplate) price.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER;
    }


}
