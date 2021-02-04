package org.meveo.api.billing;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.InvoicingPlanItem;
import org.meveo.model.cpq.commercial.OrderInvoice;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.billing.impl.InvoiceLinesService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderPriceService;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class OrderAdvancementScript extends ModuleScript {

    private OrderPriceService orderPriceService = (OrderPriceService) getServiceInterface(OrderPriceService.class.getSimpleName());
    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());
    private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private InvoiceLinesService invoiceLinesService = (InvoiceLinesService) getServiceInterface(InvoiceLinesService.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        CommercialOrder commercialOrder = (CommercialOrder) methodContext.get("commercialOrder");
        if(commercialOrder == null) {
            throw new BusinessException("No Commercial order is found");
        }
        Integer orderProgress = commercialOrder.getOrderProgress();

        if (commercialOrder.getInvoicingPlan() != null) {
            List<InvoicingPlanItem> itemsToBill = commercialOrder.getInvoicingPlan().getInvoicingPlanItems().stream()
                    .filter(item -> item.getAdvancement() == orderProgress)
                    .collect(Collectors.toList());

            if (itemsToBill.isEmpty()) {
                log.info("No invoicing plan item found for the order progress: " + orderProgress + " commercial order id: " + commercialOrder.getId());
                return;
            } else if (itemsToBill.size() > 1)
                throw new BusinessException("Many invoicing plan items are set for the advancement: " + orderProgress + " using the invoicing plan: " + commercialOrder.getInvoicingPlan().getCode());

            InvoicingPlanItem invoicingPlanItem = itemsToBill.get(0);

            List<OrderPrice> pricesToBill = orderPriceService.findByOrder(commercialOrder).stream()
                    .filter(this::isPriceRelatedToOneShotChargeTemplateOfTypeOther)
                    .collect(Collectors.toList());

            if (pricesToBill.isEmpty()) {
                log.info("No order prices to bill related to a one shot charge were found for commercial order: " + commercialOrder.getId());
                return;
            }

            if ((invoicingPlanItem.getRateToBill().add(BigDecimal.valueOf(commercialOrder.getRateInvoiced()))).compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BusinessException("the invoicing plan rate is grater than remaining rate to invoice");
            }

            BigDecimal totalAmountWithoutTax = pricesToBill.stream()
                    .map(OrderPrice::getAmountWithoutTax)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal totalAmountWithTax = pricesToBill.stream()
                    .map(OrderPrice::getAmountWithoutTax)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal totalTax = pricesToBill.stream()
                    .map(OrderPrice::getTaxAmount)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal totalTaxRate = pricesToBill.stream()
                    .map(OrderPrice::getTaxRate)
                    .reduce(BigDecimal.valueOf(0), BigDecimal::add);

            BigDecimal amountWithoutTaxToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithoutTax);
            BigDecimal amountWithTaxToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithTax);
            BigDecimal taxAmountToBeInvoiced = invoicingPlanItem.getRateToBill().divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalTax);

            String articleCode = ParamBean.getInstance().getProperty("advancePayment.accountingArticleCode", "ACT-STD");

            AccountingArticle accountingArticle = accountingArticleService.findByCode(articleCode);
            if (accountingArticle == null)
                throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);

            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setCode("COMMERCIAL-GEN");
            invoiceLine.setCode(invoiceLinesService.findDuplicateCode(invoiceLine));
            invoiceLine.setAccountingArticle(accountingArticle);
            invoiceLine.setLabel(accountingArticle.getDescription());
            invoiceLine.setProduct(pricesToBill.get(0).getOrderArticleLine().getOrderProduct().getProductVersion().getProduct());
            invoiceLine.setProductVersion(pricesToBill.get(0).getOrderArticleLine().getOrderProduct().getProductVersion());
            invoiceLine.setCommercialOrder(commercialOrder);
            invoiceLine.setOrderLot(pricesToBill.get(0).getOrderArticleLine().getOrderProduct().getOrderServiceCommercial());
            invoiceLine.setQuantity(BigDecimal.valueOf(1));
            invoiceLine.setUnitPrice(amountWithoutTaxToBeInvoiced);
            invoiceLine.setAmountWithoutTax(amountWithoutTaxToBeInvoiced);
            invoiceLine.setAmountWithTax(amountWithTaxToBeInvoiced);
            invoiceLine.setAmountTax(taxAmountToBeInvoiced);
            invoiceLine.setTaxRate(totalTaxRate);
            invoiceLinesService.create(invoiceLine);

            commercialOrder.setOrderProgressTmp(orderProgress);
            commercialOrder.addInvoicedRate(invoicingPlanItem.getRateToBill());
            commercialOrderService.update(commercialOrder);
        }


    }

    private boolean isPriceRelatedToOneShotChargeTemplateOfTypeOther(OrderPrice price) {
        return price.getChargeTemplate().getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.ONESHOT
                && ((OneShotChargeTemplate) price.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER;
    }


}
