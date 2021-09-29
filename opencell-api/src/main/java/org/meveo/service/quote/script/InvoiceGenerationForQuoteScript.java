package org.meveo.service.quote.script;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.order.OrderPriceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class InvoiceGenerationForQuoteScript extends ModuleScript {

    private OrderPriceService orderPriceService = (OrderPriceService) getServiceInterface(OrderPriceService.class.getSimpleName());
    private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private InvoiceLineService invoiceLinesService = (InvoiceLineService) getServiceInterface(InvoiceLineService.class.getSimpleName());
    private InvoiceService invoiceService = (InvoiceService) getServiceInterface(InvoiceService.class.getSimpleName());
    private CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private Logger log = LoggerFactory.getLogger(this.getClass());
 
	@Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
    	  CpqQuote quote = (CpqQuote) methodContext.get("quote");
          if(quote == null) {
              throw new BusinessException("No quote is found");
          } 
        if (quote !=null ) { 
            Date nextDay = java.sql.Date.valueOf(LocalDate.now().plusDays(1));
            Date firstTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
            Date invoiceDate = new Date();
            List<OrderPrice> pricesToBill = orderPriceService.findByQuote(quote).stream()
                    .filter(this::isPriceRelatedToOneShotChargeTemplateOfTypeOther)
                    .collect(Collectors.toList());

            if (pricesToBill.isEmpty()) {
                log.info("No charges with type OTHER to bill for quote : " + quote.getId());
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

            createAccountInvoice(quote, nextDay, firstTransactionDate, invoiceDate, defaultAccountingArticle, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate, orderProduct, true);
 
        }

    }

    private void createAccountInvoice(CpqQuote quote, Date nextDay, Date firstTransactionDate, Date invoiceDate, AccountingArticle defaultAccountingArticle, BigDecimal totalAmountWithoutTax, BigDecimal totalAmountWithTax, BigDecimal totalTax, BigDecimal totalTaxRate, OrderProduct orderProduct, boolean isDepositInvoice) {
        createInvoiceLine(quote, defaultAccountingArticle, orderProduct, totalAmountWithoutTax, totalAmountWithTax, totalTax, totalTaxRate);
        List<Invoice> invoices = invoiceService.createAggregatesAndInvoiceWithIL(quote, null, null, invoiceDate, firstTransactionDate, nextDay, null, false, false, isDepositInvoice);
        invoices.stream()
                .forEach(
                        invoice -> {
                            customFieldInstanceService.instantiateCFWithDefaultValue(invoice);
                            invoiceService.update(invoice);
                        }
                );
    }
  
    private AccountingArticle getDefaultAccountingArticle() {
        String articleCode = ParamBean.getInstance().getProperty("accountingArticle.advancePayment.defautl.code", "ADV-STD");

        AccountingArticle accountingArticle = accountingArticleService.findByCode(articleCode);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);
        return accountingArticle;
    }

    private void createInvoiceLine(CpqQuote quote, AccountingArticle accountingArticle, OrderProduct orderProduct, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        invoiceLinesService.createInvoiceLine(quote, accountingArticle, orderProduct.getProductVersion(),orderProduct.getOrderServiceCommercial(), orderProduct.getOrderOffer().getOfferTemplate(),amountWithoutTaxToBeInvoiced, amountWithTaxToBeInvoiced, taxAmountToBeInvoiced, totalTaxRate);
    }
    
    private boolean isPriceRelatedToOneShotChargeTemplateOfTypeOther(OrderPrice price) {
        return price.getChargeTemplate().getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.ONESHOT
                && ((OneShotChargeTemplate) price.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER;
    }


}


