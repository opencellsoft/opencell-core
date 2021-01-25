package org.meveo.api.billing;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
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
import org.meveo.service.cpq.order.QuotePriceService;
import org.meveo.service.script.module.ModuleScript;

@SuppressWarnings("serial")
class QuoteValidationTemp extends ModuleScript {
	
	@Inject private QuoteVersionService quoteVersionService;
    @Inject private InvoiceTypeService invoiceTypeService;
    @Inject private CommercialOrderService commercialOrderService;
    @Inject private OrderOfferService orderOfferService;
    @Inject private OrderProductService orderProductService;
    @Inject private OrderAttributeService orderAttributeService;
    @Inject private OrderLotService orderCustomerServiceService;
    @Inject private OrderArticleLineService orderArticleLineService;
    @Inject private OrderPriceService orderPriceService;
    @Inject private QuotePriceService quotePriceService;
	
	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		final CpqQuote cpqQuote = (CpqQuote) methodContext.get("cpqQuote");
		if(cpqQuote == null)
			throw new BusinessException("No Quote found");
		var quotesVersions = quoteVersionService.findByQuoteIdAndStatusActive(cpqQuote.getId());
		if(quotesVersions.size() > 1)
			throw new BusinessException("More than one quote version is published !!");
		var quoteVersion = quotesVersions.get(0);
		
		
		
		quoteVersion.getQuoteOffers().forEach(quoteOffer -> {
			var quoteOfferBillableCode = quoteOffer.getBillableAccount().getCode();
			quoteOffer.getQuoteProduct().forEach(quoteProduct -> {
				if(quoteProduct.getBillableAccount() != null && quoteOffer.getBillableAccount() != null) {
					var quoteProductBillableCode = quoteProduct.getBillableAccount().getCode();
					if(!quoteOfferBillableCode.contentEquals(quoteProductBillableCode)) {
						createNewOrder(cpqQuote, quoteVersion, quoteOffer, quoteProduct);
					}
				}
				
			});
		});
		
		
	}
	
	private void createNewOrder(CpqQuote cpqQuote, QuoteVersion quoteVersion, QuoteOffer quoteOffer, QuoteProduct quoteProduct) {
		CommercialOrder order = processCommercialOrder(cpqQuote, quoteVersion);
		OrderLot orderLot = processOrderCustomerService(quoteOffer.getQuoteLot(), order);
		OrderOffer orderOffer = processOrderOffer(quoteOffer, order);
		processOrderProduct(quoteProduct, order, orderLot, orderOffer);
	}
	
	private CommercialOrder processCommercialOrder(CpqQuote cpqQuote, QuoteVersion quoteVersion) {
		CommercialOrder order = new CommercialOrder();
		order.setSeller(cpqQuote.getSeller());
		order.setBillingAccount(cpqQuote.getBillableAccount());
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
		order.setOrderType(new OrderType()); //TODO: how to map order type
		order.setOrderProgress(1);
		order.setOrderInvoiceType(invoiceTypeService.getDefaultCommercialOrder());
		commercialOrderService.create(order);
		return order;
	}
	
	private OrderOffer processOrderOffer(QuoteOffer quoteOffer, CommercialOrder order) {
		OrderOffer offer = new OrderOffer();
		offer.setOrder(order);
		offer.setOfferTemplate(quoteOffer.getOfferTemplate());
		offer.setCode("ORD_OFF_" + order.getId());
		orderOfferService.create(offer);
		return offer;
	}
	
	private OrderProduct processOrderProduct(QuoteProduct product, CommercialOrder commercialOrder, OrderLot orderLot, OrderOffer orderOffer) {
		OrderProduct orderProduct = new OrderProduct();
		orderProduct.setOrder(commercialOrder);
		orderProduct.setOrderServiceCommercial(orderLot);
		orderProduct.setProductVersion(product.getProductVersion());
		orderProduct.setQuantity(product.getQuantity());
		
		orderProductService.create(orderProduct);
		
		product.getQuoteAttributes().forEach(quoteAttribute -> {
			processOrderAttribute(quoteAttribute, commercialOrder, orderLot, orderProduct);
		});
		
		product.getQuoteArticleLines().forEach(quoteArticleLine -> {
			OrderArticleLine orderArticleLine = processOrderArticleLine(quoteArticleLine, commercialOrder, orderLot);
			processOrderPrice(orderArticleLine, commercialOrder, product.getQuoteVersion());
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
		orderAttribute.setCode("OR_ATTR_" + commercialOrder.getId());
		orderAttributeService.create(orderAttribute);
	}
	
	private OrderLot processOrderCustomerService(QuoteLot quoteLot, CommercialOrder commercialOrder) {
		OrderLot orderCustomer = new OrderLot();
		orderCustomer.setCode("ORD_CUST_SR_" + commercialOrder.getId());
		orderCustomer.setOrder(commercialOrder);
		orderCustomerServiceService.create(orderCustomer);
		return orderCustomer;
	}
	
	private OrderArticleLine processOrderArticleLine(QuoteArticleLine quoteArticleLine, CommercialOrder commercialOrder, OrderLot orderCustomerService) {
		OrderArticleLine articleLine = new OrderArticleLine();
		articleLine.setCode("ORD_ART_LINE_" + commercialOrder.getId());
		articleLine.setOrder(commercialOrder);
		articleLine.setOrderCustomerService(orderCustomerService);
		articleLine.setQuantity(quoteArticleLine.getQuantity());
		articleLine.setQuantityService(quoteArticleLine.getServiceQuantity());
		
		orderArticleLineService.create(articleLine);
		return articleLine;
	}
	
	private void processOrderPrice(OrderArticleLine orderArticleLine, CommercialOrder commercialOrder, QuoteVersion quoteVersion) {
		var quotePrices = quotePriceService.findByQuoteArticleLineIdandQuoteVersionId(orderArticleLine.getId(), quoteVersion.getId());
		quotePrices.forEach( price -> {
			OrderPrice orderPrice = new OrderPrice();
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
			orderPriceService.create(orderPrice);
		});
	}
	
}
