package org.meveo.api.billing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OrderArticleLine;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderArticleLineService;
import org.meveo.service.cpq.order.OrderAttributeService;
import org.meveo.service.cpq.order.OrderLotService;
import org.meveo.service.cpq.order.OrderOfferService;
import org.meveo.service.cpq.order.OrderPriceService;
import org.meveo.service.cpq.order.OrderProductService;
import org.meveo.service.cpq.order.OrderTypeService;
import org.meveo.service.cpq.order.QuotePriceService;
import org.meveo.service.script.module.ModuleScript;

@SuppressWarnings("serial")
public class QuoteValidationTemp extends ModuleScript {

	private QuoteVersionService quoteVersionService = (QuoteVersionService) getServiceInterface(QuoteVersionService.class.getSimpleName());
    private InvoiceTypeService invoiceTypeService = (InvoiceTypeService) getServiceInterface(InvoiceTypeService.class.getSimpleName());
    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface(CommercialOrderService.class.getSimpleName());
    private OrderOfferService orderOfferService = (OrderOfferService) getServiceInterface(OrderOfferService.class.getSimpleName());
    private OrderProductService orderProductService = (OrderProductService) getServiceInterface(OrderProductService.class.getSimpleName());
    private OrderAttributeService orderAttributeService = (OrderAttributeService) getServiceInterface(OrderAttributeService.class.getSimpleName());
    private OrderLotService orderCustomerServiceService = (OrderLotService) getServiceInterface(OrderLotService.class.getSimpleName());
    private OrderArticleLineService orderArticleLineService = (OrderArticleLineService) getServiceInterface(OrderArticleLineService.class.getSimpleName());
    private OrderPriceService orderPriceService = (OrderPriceService) getServiceInterface(OrderPriceService.class.getSimpleName());
    private QuotePriceService quotePriceService = (QuotePriceService) getServiceInterface(QuotePriceService.class.getSimpleName());
    private OrderTypeService orderTypeService = (OrderTypeService) getServiceInterface(OrderTypeService.class.getSimpleName());
    
	
	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		final CpqQuote cpqQuote = (CpqQuote) methodContext.get("cpqQuote");
		if(cpqQuote == null)
			throw new BusinessException("No Quote found");
		var quotesVersions = quoteVersionService.findByQuoteIdAndStatusActive(cpqQuote.getId());
		if(quotesVersions.size() > 1)
			throw new BusinessException("More than one quote version is published !!");
		var quoteVersion = quotesVersions.get(0);
		var orderByBillingAccount = new Hashtable<String, List<QuoteOffer>>();
		var billingAccount = new Hashtable<String, BillingAccount>();
		quoteVersion.getQuoteOffers().forEach(quoteOffer -> {
			if(quoteOffer.getBillableAccount() == null) {
				quoteOffer.setBillableAccount(cpqQuote.getBillableAccount());
			}
			List<QuoteOffer> offers = new ArrayList<>();
			if(orderByBillingAccount.get(quoteOffer.getBillableAccount().getCode()) != null) {
				offers = orderByBillingAccount.get(quoteOffer.getBillableAccount().getCode());
			}
			offers.add(quoteOffer);
			orderByBillingAccount.put(quoteOffer.getBillableAccount().getCode(), offers);
			billingAccount.put(quoteOffer.getBillableAccount().getCode(), quoteOffer.getBillableAccount());
			
		});
		orderByBillingAccount.keySet().forEach(ba -> {
			List<QuoteOffer> offers = orderByBillingAccount.get(ba);
			BillingAccount billableAccount = billingAccount.get(ba);
			CommercialOrder order = processCommercialOrder(cpqQuote, quoteVersion, billableAccount);
			offers.forEach(offer -> {
				processOrderOffer(offer, order);
				OrderLot orderLot = processOrderCustomerService(offer.getQuoteLot(), order);
				OrderOffer orderOffer = processOrderOffer(offer, order);
				offer.getQuoteProduct().forEach(quoteProduct -> {
					processOrderProduct(quoteProduct, order, orderLot, orderOffer);
				});
			});
		});
		
		
	}
	private CommercialOrder processCommercialOrder(CpqQuote cpqQuote, QuoteVersion quoteVersion, BillingAccount account) {
		CommercialOrder order = new CommercialOrder();
		order.setSeller(cpqQuote.getSeller());
		order.setBillingAccount(account);
		order.setQuote(cpqQuote);
		order.setContract(cpqQuote.getContract());
		order.setCustomerServiceBegin(quoteVersion.getStartDate());
		order.setStatus(CommercialOrderEnum.DRAFT.toString());
		Date now = Calendar.getInstance().getTime();
		order.setStatusDate(now);
		order.setOrderDate(now);
		order.setCustomerServiceDuration(cpqQuote.getQuoteLotDuration());
		order.setExternalReference(null);
		order.setInvoicingPlan(null); //TODO: how to map invoice plan
		order.setOrderType(createOrderTypeTemp()); //TODO: how to map order type
		order.setOrderProgress(1);
		order.setOrderInvoiceType(invoiceTypeService.getDefaultCommercialOrder());
		order.setProgressDate(Calendar.getInstance().getTime());
		order.setUserAccount(account.getUsersAccounts().get(0));
		commercialOrderService.create(order);
		return order;
	}
	
	private OrderType createOrderTypeTemp() {
		final String TEMP_CODE = "COMMERCIAL"; 
		OrderType orderType = orderTypeService.findByCode(TEMP_CODE);
		if(orderType == null) {
			orderType = new OrderType();
			orderType.setCode(TEMP_CODE);
			orderType.setDescription("generated on quote validation  script");
			orderTypeService.create(orderType);
		}
		return orderType;
	}
	private static final String GENERIC_CODE = "COMMERCIAL_GEN";
	private OrderOffer processOrderOffer(QuoteOffer quoteOffer, CommercialOrder order) {
		OrderOffer offer = new OrderOffer();
		offer.setOrder(order);
		offer.setOfferTemplate(quoteOffer.getOfferTemplate());
		offer.setCode(GENERIC_CODE);
		offer.setCode(orderOfferService.findDuplicateCode(offer));
		orderOfferService.create(offer);
		return offer;
	}
	
	private OrderProduct processOrderProduct(QuoteProduct product, CommercialOrder commercialOrder, OrderLot orderLot, OrderOffer orderOffer) {
		OrderProduct orderProduct = new OrderProduct();
		orderProduct.setOrder(commercialOrder);
		orderProduct.setOrderServiceCommercial(orderLot);
		orderProduct.setProductVersion(product.getProductVersion());
		orderProduct.setQuantity(product.getQuantity());
		orderProduct.setOrderOffer(orderOffer);
		orderProduct.setCode(GENERIC_CODE);
		orderProduct.setCode(orderProductService.findDuplicateCode(orderProduct));
		
		orderProductService.create(orderProduct);
		
		product.getQuoteAttributes().forEach(quoteAttribute -> {
			processOrderAttribute(quoteAttribute, commercialOrder, orderLot, orderProduct);
		});
		
		product.getQuoteArticleLines().forEach(quoteArticleLine -> {
			OrderArticleLine orderArticleLine = processOrderArticleLine(quoteArticleLine, commercialOrder, orderLot, orderProduct);
			processOrderPrice(quoteArticleLine.getId(), orderArticleLine, commercialOrder, product.getQuoteOffre().getQuoteVersion());
		});
		
		
		return orderProduct;
	}
	
	private void processOrderAttribute(QuoteAttribute quoteAttribute, CommercialOrder commercialOrder, OrderLot orderLot, OrderProduct orderProduct) {
		OrderAttribute orderAttribute = new OrderAttribute();
		orderAttribute.setOrderCode(commercialOrder);
		orderAttribute.setOrderLot(orderLot);
		orderAttribute.setOrderProduct(orderProduct);
		orderAttribute.setAccessPoint(null);
		orderAttribute.setAttribute(quoteAttribute.getAttribute());
		orderAttribute.setStringValue(quoteAttribute.getStringValue());
		orderAttribute.setDateValue(quoteAttribute.getDateValue());
		orderAttribute.setDoubleValue(quoteAttribute.getDoubleValue());
		orderAttribute.setCode(GENERIC_CODE);
		orderAttribute.setCode(orderAttributeService.findDuplicateCode(orderAttribute));
		orderAttributeService.create(orderAttribute);
	}
	
	private OrderLot processOrderCustomerService(QuoteLot quoteLot, CommercialOrder commercialOrder) {
		OrderLot orderCustomer = new OrderLot();
		orderCustomer.setCode(GENERIC_CODE);
		orderCustomer.setCode(orderCustomerServiceService.findDuplicateCode(orderCustomer));
		//orderCustomer.setOrder(commercialOrder);
		orderCustomerServiceService.create(orderCustomer);
		return orderCustomer;
	}
	
	private OrderArticleLine processOrderArticleLine(QuoteArticleLine quoteArticleLine, CommercialOrder commercialOrder, OrderLot orderCustomerService, OrderProduct orderProduct) {
		OrderArticleLine articleLine = new OrderArticleLine();
		articleLine.setCode(orderArticleLineService.findDuplicateCode(articleLine));
		articleLine.setOrder(commercialOrder);
		articleLine.setOrderCustomerService(orderCustomerService);
		articleLine.setQuantity(quoteArticleLine.getQuantity());
		articleLine.setQuantityService(quoteArticleLine.getServiceQuantity());
		articleLine.setAccountingArticle(quoteArticleLine.getAccountingArticle());
		articleLine.setOrderProduct(orderProduct);
		orderArticleLineService.create(articleLine);
		return articleLine;
	}
	
	private void processOrderPrice(Long quoteArticleLineId, OrderArticleLine orderArticleLine, CommercialOrder commercialOrder, QuoteVersion quoteVersion) {
		var quotePrices = quotePriceService.findByQuoteArticleLineIdandQuoteVersionId(quoteArticleLineId, quoteVersion.getId());
		quotePrices.forEach( price -> {
			OrderPrice orderPrice = new OrderPrice();
			orderPrice.setCode(GENERIC_CODE);
			orderPrice.setCode(orderPriceService.findDuplicateCode(orderPrice));
			orderPrice.setOrderArticleLine(orderArticleLine);
			orderPrice.setOrder(commercialOrder);
			orderPrice.setPriceLevelEnum(price.getPriceLevelEnum());
			orderPrice.setAmountWithTax(price.getAmountWithTax());
			orderPrice.setUnitPriceWithoutTax(price.getUnitPriceWithoutTax());
			orderPrice.setAmountWithoutTax(price.getAmountWithoutTax());
			orderPrice.setAmountWithoutTaxWithDiscount(price.getAmountWithoutTaxWithDiscount());
			orderPrice.setTaxAmount(price.getTaxAmount());
			orderPrice.setTaxRate(price.getTaxRate());
			orderPrice.setPriceOverCharged(price.getPriceOverCharged());
			orderPrice.setCurrencyCode(price.getCurrencyCode());
			orderPrice.setRecurrenceDuration(price.getRecurrenceDuration());
			orderPrice.setRecurrencePeriodicity(price.getRecurrencePeriodicity());
			orderPrice.setChargeTemplate(price.getChargeTemplate());
			orderPriceService.create(orderPrice);
		});
	}
	
}
