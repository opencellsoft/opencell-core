package org.meveo.service.quote.script;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.commercial.OrderArticleLine;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderPrice;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.cpq.CpqQuoteService;
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
import org.meveo.service.crm.impl.CurrentUserProducer;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class QuoteValidationScript extends ModuleScript {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteVersionService.class);

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
    private MeveoUser currentUser = ((CurrentUserProducer) getServiceInterface(CurrentUserProducer.class.getSimpleName())).getCurrentUser();
    private CustomFieldInstanceService customFieldInstanceService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) getServiceInterface(CustomFieldTemplateService.class.getSimpleName());
	private CpqQuoteService cpqQuoteService = (CpqQuoteService) getServiceInterface(CpqQuoteService.class.getSimpleName());
	
	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		CpqQuote cpqQuoteTmp = (CpqQuote) methodContext.get("cpqQuote");
		if(cpqQuoteTmp == null)
			throw new BusinessException("No Quote found");
		final CpqQuote cpqQuote = cpqQuoteService.findByCode(cpqQuoteTmp.getCode());
		LOGGER.info("start creation order from quote code {}", cpqQuote.getCode());
		var quotesVersions = quoteVersionService.findByQuoteIdAndStatusActive(cpqQuote.getId());
		if(quotesVersions.size() > 1)
			throw new BusinessException("More than one quote version is published !!");
		var quoteVersion = quotesVersions.get(0);
		LOGGER.info("current quote {} with version : {}", cpqQuote.getCode(), quoteVersion.getQuoteVersion());
		var orderByBillingAccount = new Hashtable<String, List<QuoteOffer>>();
		var billingAccount = new Hashtable<String, BillingAccount>();
		LOGGER.info("current quote version contain {} quote offer.", quoteVersion.getQuoteOffers().size());
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
		LOGGER.info("Number of order by billing account is {}", orderByBillingAccount.size() );
		orderByBillingAccount.keySet().forEach(ba -> {
			List<QuoteOffer> offers = orderByBillingAccount.get(ba);
			BillingAccount billableAccount = billingAccount.get(ba);
			CommercialOrder order = processCommercialOrder(cpqQuote, quoteVersion, billableAccount);
			List<OrderOffer> orderOffers = offers.stream()
					.map(offer -> {
						OrderLot orderLot = processOrderCustomerService(offer.getQuoteLot(), order);
						OrderOffer orderOffer = processOrderOffer(offer, order);
						offer.getQuoteProduct().forEach(quoteProduct -> {
							processOrderProduct(quoteProduct, order, orderLot, orderOffer);
						});
						return orderOffer;
					}).collect(Collectors.toList());
			order.setOffers(orderOffers);
		    orderOffers.stream().findFirst().get().getOfferTemplate().getAllowedDiscountPlans().stream().findFirst().ifPresent(matchedDP -> order.setDiscountPlan(matchedDP));
			commercialOrderService.update(order);
			List<QuotePrice> quotePrices=quoteVersion.getQuotePrices().stream()
					.filter(qp -> qp.getPriceLevelEnum()==PriceLevelEnum.QUOTE).collect(Collectors.toList());
			processOrderPrice(quotePrices, null, order, quoteVersion, null, null);
		
		});
		LOGGER.info("End creation order from quote code {}, number of order created is {}", cpqQuote.getCode(), orderByBillingAccount.size());
		
		
	}
	private CommercialOrder processCommercialOrder(CpqQuote cpqQuote, QuoteVersion quoteVersion, BillingAccount account) {
		CommercialOrder order = new CommercialOrder();
		order.setSeller(cpqQuote.getSeller()!=null?cpqQuote.getSeller():account.getCustomerAccount().getCustomer().getSeller());
		order.setBillingAccount(account);
		order.setQuote(cpqQuote);
		order.setContract(quoteVersion.getContract());
		order.setCustomerServiceBegin(quoteVersion.getStartDate());
		order.setStatus(CommercialOrderEnum.DRAFT.toString());
		Date now = Calendar.getInstance().getTime();
		order.setStatusDate(now);
		order.setOrderDate(now);
		order.setCustomerServiceDuration(cpqQuote.getQuoteLotDuration());
		order.setExternalReference(null);
		order.setInvoicingPlan(quoteVersion.getInvoicingPlan());
		order.setOrderType(createOrderTypeTemp()); //TODO: how to map order type
		order.setOrderProgress(0);
		order.setOrderInvoiceType(invoiceTypeService.getDefaultCommercialOrder());
		order.setProgressDate(Calendar.getInstance().getTime());
		order.setUserAccount(account.getUsersAccounts().size() > 0 ? account.getUsersAccounts().get(0) : null);
		order.setQuoteVersion(quoteVersion);
		order.setDiscountPlan(quoteVersion.getDiscountPlan());
		order.setDeliveryDate(cpqQuote.getDeliveryDate());
		order.setUserAccount(cpqQuote.getUserAccount());
		
		// Set Sales Person Name when it's not null in CpqQuote
		if(StringUtils.isNotEmpty(cpqQuote.getSalesPersonName())) {
			order.setSalesPersonName(cpqQuote.getSalesPersonName());
		}
		
		var customFieldsFromQuoteVersion = quoteVersion.getCfValues();
		var customFieldOrder = customFieldTemplateService.findByAppliesTo(order);
		if(customFieldsFromQuoteVersion != null && customFieldsFromQuoteVersion.getValues() != null && !customFieldsFromQuoteVersion.getValues().isEmpty()) {
			customFieldsFromQuoteVersion.getValues().forEach( (key,value) -> {
				CustomFieldTemplate template = customFieldOrder.get(key);
				if(template != null && template.isUseInheritedAsDefaultValue()) {
					LOGGER.info("found inherent custom field code : " + template.getCode() + " , type : " + template.getFieldType());
					CustomFieldTemplate templateQuoteVersion = customFieldOrder.get(key);
					if(templateQuoteVersion == null)
						throw new BusinessException("No Custom field ("+key+") found for : Quote version");
					if(template.getFieldType() != templateQuoteVersion.getFieldType())
						throw new BusinessException("No Custom field ("+key+") for Quote version has different type for Order");
					customFieldInstanceService.setCFValue(order, key, value);
				}
			});
		}
		
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
	
	private OrderOffer processOrderOffer(QuoteOffer quoteOffer, CommercialOrder order) {
		OrderOffer offer = new OrderOffer();
		offer.setOrder(order);
		offer.setOfferTemplate(quoteOffer.getOfferTemplate()); 
		offer.setDiscountPlan(quoteOffer.getDiscountPlan());
		offer.setQuoteOffer(quoteOffer);
		offer.setDeliveryDate(quoteOffer.getDeliveryDate());
		offer.setUserAccount(quoteOffer.getUserAccount());
		offer.setOrderLineType(OfferLineTypeEnum.CREATE);
		offer.setSubscription(quoteOffer.getSubscription());
		offer.setTerminationDate(quoteOffer.getQuoteVersion().getEndDate());
		orderOfferService.create(offer);
		LOGGER.info("quoteOffer.getQuoteAttributes() size{}",quoteOffer.getQuoteAttributes().size());
		quoteOffer.getQuoteAttributes().forEach(quoteAttribute -> {
			processOrderAttribute(quoteAttribute, order, offer,null);
		});
		return offer;
	}
	
	private OrderProduct processOrderProduct(QuoteProduct product, CommercialOrder commercialOrder, OrderLot orderLot, OrderOffer orderOffer) {
		OrderProduct orderProduct = new OrderProduct();
		orderProduct.setOrder(commercialOrder);
		orderProduct.setOrderServiceCommercial(orderLot);
		orderProduct.setProductVersion(product.getProductVersion());
		orderProduct.setQuantity(product.getQuantity());
		orderProduct.setDiscountPlan(product.getDiscountPlan());
		orderProduct.setOrderOffer(orderOffer);  
		orderProduct.setQuoteProduct(product); 
		orderProduct.setDeliveryDate(product.getDeliveryDate());
		orderProduct.setProductActionType(product.getProductActionType());
		orderProduct.setTerminationDate(product.getTerminationDate());
		orderProduct.setTerminationReason(product.getTerminationReason());
		
		orderProductService.create(orderProduct);
		
		product.getQuoteAttributes().forEach(quoteAttribute -> {
			processOrderAttribute(quoteAttribute, commercialOrder, orderOffer, orderProduct);
		});

		final Map<Long, OrderPrice> quoteToOrder = new HashMap<Long, OrderPrice>();
		product.getQuoteArticleLines().forEach(quoteArticleLine -> {
			OrderArticleLine orderArticleLine = processOrderArticleLine(quoteArticleLine, commercialOrder, orderLot, orderProduct);
			var quotePrices = quotePriceService.findByQuoteArticleLineIdandQuoteVersionId(quoteArticleLine.getId(), product.getQuoteOffer().getQuoteVersion().getId());
			processOrderPrice(quotePrices, orderArticleLine, commercialOrder, product.getQuoteOffer().getQuoteVersion(),orderOffer, quoteToOrder);
		
		});
		
		//set disocuntedOrderPrice
        Iterator<Map.Entry<Long, OrderPrice>> itr = quoteToOrder.entrySet().iterator();
         
        while(itr.hasNext())
        {
             Map.Entry<Long, OrderPrice> entry = itr.next();
             QuotePrice quotePrice=quotePriceService.findById(entry.getKey());
             if(quotePrice!=null && quotePrice.getDiscountedQuotePrice()!=null) {
            	 OrderPrice discountedOrderPrice=quoteToOrder.get(quotePrice.getDiscountedQuotePrice().getId());
            	 OrderPrice orderPrice= entry.getValue();
            	 orderPrice.setDiscountedOrderPrice(discountedOrderPrice);
            	 orderPriceService.update(orderPrice);
             }
            	 
             }
		
		
		return orderProduct;
	}
	
	private void processOrderAttribute(QuoteAttribute quoteAttribute, CommercialOrder commercialOrder, OrderOffer orderOffer, OrderProduct orderProduct) {
		OrderAttribute orderAttribute = new OrderAttribute(quoteAttribute, currentUser);
		orderAttribute.setCommercialOrder(commercialOrder);
		orderAttribute.setOrderOffer(orderOffer);
		orderAttribute.setOrderProduct(orderProduct);
		orderAttribute.setAccessPoint(null);
		LOGGER.info("processordeer attribute code{}",quoteAttribute.getAttribute().getCode());
		orderAttributeService.create(orderAttribute);
	}
	
	private OrderLot processOrderCustomerService(QuoteLot quoteLot, CommercialOrder commercialOrder) {
		OrderLot orderCustomer = new OrderLot();
		orderCustomer.setCode(UUID.randomUUID().toString());
		orderCustomer.setOrder(commercialOrder);
		orderCustomerServiceService.create(orderCustomer);
		return orderCustomer;
	}
	
	private OrderArticleLine processOrderArticleLine(QuoteArticleLine quoteArticleLine, CommercialOrder commercialOrder, OrderLot orderCustomerService, OrderProduct orderProduct) {
		if(quoteArticleLine==null || commercialOrder==null) {
			return null;
		}
		OrderArticleLine articleLine = new OrderArticleLine();
		articleLine.setCode(UUID.randomUUID().toString());
		articleLine.setOrder(commercialOrder);
		articleLine.setOrderCustomerService(orderCustomerService);
		articleLine.setQuantity(quoteArticleLine.getQuantity());
		articleLine.setQuantityService(quoteArticleLine.getServiceQuantity());
		articleLine.setAccountingArticle(quoteArticleLine.getAccountingArticle());
		articleLine.setOrderProduct(orderProduct);
		orderArticleLineService.create(articleLine);
		return articleLine;
	}
	
	private void processOrderPrice(List<QuotePrice> quotePrices, OrderArticleLine orderArticleLine, CommercialOrder commercialOrder, QuoteVersion quoteVersion,OrderOffer orderOffer, Map<Long, OrderPrice> quoteToOrder) {
	    quotePrices.forEach( price -> {
			OrderPrice orderPrice = new OrderPrice();
			orderPrice.setCode(UUID.randomUUID().toString());
			if(orderArticleLine==null) {
				orderPrice.setOrderArticleLine(processOrderArticleLine(price.getQuoteArticleLine(), commercialOrder, null, null));
			}else {
				orderPrice.setOrderArticleLine(orderArticleLine);
			}
			orderPrice.setOrder(commercialOrder);
			orderPrice.setPriceLevelEnum(PriceLevelEnum.QUOTE.equals(price.getPriceLevelEnum())?PriceLevelEnum.ORDER:price.getPriceLevelEnum());
			orderPrice.setAmountWithTax(price.getAmountWithTax());
			orderPrice.setUnitPriceWithoutTax(price.getUnitPriceWithoutTax());
			orderPrice.setAmountWithoutTax(price.getAmountWithoutTax());
			orderPrice.setAmountWithoutTaxWithDiscount(price.getAmountWithoutTaxWithoutDiscount());
			orderPrice.setTaxAmount(price.getTaxAmount());
			orderPrice.setTaxRate(price.getTaxRate());
			orderPrice.setPriceOverCharged(price.getPriceOverCharged());
			orderPrice.setCurrencyCode(price.getCurrencyCode());
			orderPrice.setRecurrenceDuration(price.getRecurrenceDuration());
			orderPrice.setRecurrencePeriodicity(price.getRecurrencePeriodicity());
			orderPrice.setChargeTemplate(price.getChargeTemplate());
			orderPrice.setOrderOffer(orderOffer);
			orderPrice.setPriceTypeEnum(price.getPriceTypeEnum());
			orderPrice.setQuantity(price.getQuantity());
			orderPrice.setDiscountPlan(price.getDiscountPlan());
			orderPrice.setDiscountPlanItem(price.getDiscountPlanItem());
			orderPrice.setDiscountPlanType(price.getDiscountPlanType());
			orderPrice.setDiscountValue(price.getDiscountValue());
			orderPrice.setApplyDiscountsOnOverridenPrice(price.getApplyDiscountsOnOverridenPrice());
			orderPrice.setSequence(price.getSequence());
			orderPrice.setDiscountedAmount(price.getDiscountedAmount());
			
			if( quoteToOrder!=null  &&  price.getDiscountedQuotePrice() != null && price.getDiscountPlan() != null) {
				orderPrice.setDiscountedOrderPrice(quoteToOrder.get(price.getDiscountedQuotePrice().getId()));
			}
			orderPriceService.create(orderPrice);
			if(quoteToOrder!=null) {
				quoteToOrder.put(price.getId(), orderPrice);
			}
		});
	}
	
}
