package org.meveo.service.quote.script;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.cpq.CommercialOrderApi;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoicingPlanItem;
import org.meveo.model.cpq.commercial.OrderArticleLine;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderArticleLineService;
import org.meveo.service.cpq.order.OrderPriceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
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
    private CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private OrderArticleLineService orderArticleLineService = (OrderArticleLineService) getServiceInterface(OrderArticleLineService.class.getSimpleName());
    private ServiceSingleton serviceSingleton  = (ServiceSingleton) getServiceInterface(ServiceSingleton.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        CommercialOrder commercialOrder = (CommercialOrder) methodContext.get("commercialOrder");
        if(commercialOrder == null) {
            throw new BusinessException("No Commercial order is found");
        }
        // Force fetch nested entities to avoid LazyInitializationException
        // All those nested entities are not fetched : quote, orderType, invoices, orderLots, orderPrices
        log.info("Process CommericalOrder [quote='{}', orderType='{}']",
                commercialOrder.getQuote() != null ? commercialOrder.getQuote().getCode() : "not specified",
                commercialOrder.getOrderType() != null ? commercialOrder.getOrderType().getCode() : "not specified");

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

            List<Object[]>  groupedPricesToBill = orderPriceService.getGroupedOrderPrices(commercialOrder.getId(), PriceTypeEnum.ONE_SHOT_INVOICING_PLAN);

            if(orderProgress == 100) {
                if(commercialOrder.getRateInvoiced() < 100) {
                    if(isOneShot100Payment(commercialOrder.getInvoicingPlan().getInvoicingPlanItems())){
                    	createIlsAndInvoice(commercialOrder,groupedPricesToBill,null, nextDay, firstTransactionDate, invoiceDate,true, true);
                    }
                    generateGlobalInvoice(commercialOrder,groupedPricesToBill, nextDay, firstTransactionDate, invoiceDate, true);
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
                	generateGlobalInvoice(commercialOrder, groupedPricesToBill,nextDay, firstTransactionDate, invoiceDate, true);
                }else {
                    createIlsAndInvoice(commercialOrder,groupedPricesToBill,invoicingPlanItem.getRateToBill(), nextDay, firstTransactionDate, invoiceDate, true, false);
                }
                commercialOrder.setRateInvoiced(newRateInvoiced.intValue());
                commercialOrder.setOrderProgressTmp(orderProgress);
                commercialOrderService.update(commercialOrder);
            }
        }


    }

//    private void createAccountInvoice(CommercialOrder commercialOrder, Date nextDay, Date firstTransactionDate, Date invoiceDate, AccountingArticle defaultAccountingArticle, BigDecimal totalAmountWithoutTax, BigDecimal totalAmountWithTax, BigDecimal totalTax, BigDecimal totalTaxRate, OrderProduct orderProduct, boolean isDepositInvoice) {
//        createInvoiceLine(commercialOrder, defaultAccountingArticle, orderProduct, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate);
//        List<Invoice> invoices = invoiceService.createAggregatesAndInvoiceWithIL(commercialOrder, null, null, invoiceDate, firstTransactionDate, nextDay, null, false, false, isDepositInvoice);
//        invoices.stream()
//                .forEach(
//                        invoice -> {
//                            customFieldInstanceService.instantiateCFWithDefaultValue(invoice);
//                            invoiceService.update(invoice);
//                        }
//                );
//    }

    private void createIlsAndInvoice(CommercialOrder commercialOrder, List<Object[]> groupedPricesToBill,BigDecimal rateToBill,Date nextDay, Date firstTransactionDate, Date invoiceDate, boolean isDepositInvoice, boolean isBillOver) {
    	BigDecimal taxRate=null;
    	BigDecimal totalAmountWithoutTax=null;
    	BigDecimal totalAmountWithTax=null;
    	BigDecimal totalAmountTax =null;
    	BigDecimal totalQuantity=null;
    	Map<Long,InvoiceLine> orderToInvoiceLine=new HashMap<Long, InvoiceLine>();
    	InvoiceLine discountedInvoiceLine=null;
    	 AccountingArticle defaultAccountingArticle = null;
    	 if(!isBillOver) {
    	     defaultAccountingArticle = getDefaultAccountingArticle();
    	 }
    	 
    	for (Object[] groupedOrderPrice : groupedPricesToBill) {
			Long orderArticleLineId = (Long) groupedOrderPrice[0];
			Long discountedOrderPriceId = (Long) groupedOrderPrice[1];
			if(!isBillOver) {
				taxRate = BigDecimal.ZERO;
				totalAmountWithoutTax = (BigDecimal) groupedOrderPrice[4];
				totalAmountWithTax = (BigDecimal) groupedOrderPrice[4];
				totalAmountTax = BigDecimal.ZERO;
			}else {
				taxRate = (BigDecimal) groupedOrderPrice[2];
				totalAmountWithoutTax = (BigDecimal) groupedOrderPrice[3];
				totalAmountWithTax = (BigDecimal) groupedOrderPrice[4];
				totalAmountTax = (BigDecimal) groupedOrderPrice[5];
			}
			
			totalQuantity = (BigDecimal) groupedOrderPrice[6];
			OrderArticleLine orderArticleLine = null;
            if(orderArticleLineId!=null) {
            	orderArticleLine=orderArticleLineService.findById(orderArticleLineId);
            }
            if(rateToBill!=null && orderArticleLine!=null) {
          	  totalAmountWithoutTax = rateToBill.divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithoutTax);
                totalAmountWithTax= rateToBill.divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountWithTax);
                totalAmountTax = rateToBill.divide(BigDecimal.valueOf(100).setScale(8, RoundingMode.HALF_UP)).multiply(totalAmountTax);
                if(discountedOrderPriceId!=null) {
                	log.warn("discountedOrderPrice discountedOrderPriceId={}",discountedOrderPriceId);
                	 OrderPrice discountedOrderPrice=orderPriceService.findById(discountedOrderPriceId);
                	 if(discountedOrderPrice!=null) {
                		  discountedInvoiceLine=orderToInvoiceLine.get(discountedOrderPrice.getOrderArticleLine().getId());
                	 }else {
                		 log.warn("discountedOrderPrice does not exist id={}",discountedOrderPriceId);
                	 }
                	 
                }
                
                InvoiceLine invoiceLine=createInvoiceLine(commercialOrder, defaultAccountingArticle != null ? defaultAccountingArticle : orderArticleLine.getAccountingArticle(),orderArticleLine.getOrderProduct().getProductVersion(),orderArticleLine.getOrderProduct().getOrderOffer(), totalAmountWithoutTax, totalAmountWithTax, totalAmountTax, taxRate,discountedInvoiceLine!=null?discountedInvoiceLine.getId():null);
                
               orderToInvoiceLine.put(orderArticleLineId, invoiceLine);
           
           }
        }  
        List<Invoice> invoices = invoiceService.createAggregatesAndInvoiceWithIL(commercialOrder, null, null, invoiceDate, firstTransactionDate, nextDay, null, false, false, isDepositInvoice);
        invoices.stream()
                .forEach(
                        invoice -> {
                            invoice = invoiceService.refreshOrRetrieve(invoice);
                            customFieldInstanceService.instantiateCFWithDefaultValue(invoice);
                            if(isDepositInvoice) {
                            	invoice.setStatus(InvoiceStatusEnum.VALIDATED);
                                serviceSingleton.assignInvoiceNumber(invoice, true);
                            }
                            if(!isBillOver && invoice.getInvoiceType() != null && invoice.getInvoiceType().getCode().equals("ADV")) {
                                BigDecimal amountWithTax = invoice.getInvoiceLines().stream().map(InvoiceLine::getAmountWithTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                                invoice.setInvoiceBalance(amountWithTax);
                            }
                            invoiceService.update(invoice);
                        }
                );

    }

    private boolean isOneShot100Payment(List<InvoicingPlanItem> invoicingPlanItems) {
        return invoicingPlanItems.size() == 1 && invoicingPlanItems.get(0).getRateToBill().doubleValue() == BigDecimal.valueOf(100).doubleValue();
    }

    private void generateGlobalInvoice(CommercialOrder commercialOrder,List<Object[]>  groupedPricesToBill, Date nextDay, Date firstTransactionDate, Date invoiceDate, boolean isBillOver) {
        createIlsAndInvoice(commercialOrder,groupedPricesToBill, BigDecimal.valueOf(100 - commercialOrder.getRateInvoiced()), nextDay, firstTransactionDate, invoiceDate, false, isBillOver);
    }
    
    private AccountingArticle getDefaultAccountingArticle() {
        String articleCode = ParamBean.getInstance().getProperty("accountingArticle.advancePayment.defautl.code", "ADV-STD");

        AccountingArticle accountingArticle = accountingArticleService.findByCode(articleCode);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);
        return accountingArticle;
    }


    private InvoiceLine createInvoiceLine(CommercialOrder commercialOrder, AccountingArticle accountingArticle,ProductVersion productVersion, OrderOffer orderOffer, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate,Long discountedInvoiceLineId) {
        return invoiceLinesService.createInvoiceLine(commercialOrder, accountingArticle, productVersion,null,null,orderOffer, amountWithoutTaxToBeInvoiced, amountWithTaxToBeInvoiced, taxAmountToBeInvoiced, totalTaxRate,discountedInvoiceLineId);
    }

    private boolean isPriceRelatedToOneShotChargeTemplateOfTypeOther(OrderPrice price) {
        return price.getChargeTemplate().getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.ONESHOT
                && ((OneShotChargeTemplate) price.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.INVOICING_PLAN;
    }


}
