package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.OrderAttributeDto;
import org.meveo.api.dto.cpq.OrderProductDto;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.cpq.order.OrderOfferDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetListCommercialOrderDtoResponse;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.order.Order;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.InvoicingPlanService;
import org.meveo.service.cpq.order.OrderAttributeService;
import org.meveo.service.cpq.order.OrderLotService;
import org.meveo.service.cpq.order.OrderOfferService;
import org.meveo.service.cpq.order.OrderProductService;
import org.meveo.service.cpq.order.OrderTypeService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.primefaces.model.SortOrder;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;


/**
 * @author Tarik FA.
 * @version 11.0
 * @dateCreation 31-12-2020
 *
 */
@Stateless
public class CommercialOrderApi extends BaseApi {

	@Inject private CommercialOrderService commercialOrderService;
	@Inject private SellerService sellerService;
	@Inject private BillingAccountService billingAccountService;
	@Inject private OrderTypeService orderTypeService;
	@Inject private CpqQuoteService cpqQuoteService;
	@Inject private ContractService contractService;
	@Inject private InvoicingPlanService invoicingPlanService;
	@Inject private OrderService orderService;
    @Inject private InvoiceTypeService invoiceTypeService;
    @Inject private UserAccountService userAccountService;
    @Inject private AccessService accessService;
    @Inject private SubscriptionService subscriptionService;
    @Inject private ServiceSingleton serviceSingleton;
	@Inject private ScriptInstanceService scriptInstanceService;
	@Inject private OrderLotService orderLotService;
	@Inject 
	private OrderOfferService orderOfferService; 
	@Inject 
	private OfferTemplateService offerTemplateService;
	
	@Inject 
	private ProductVersionService productVersionService;
	
	@Inject 
	private OrderProductService orderProductService;
	
	@Inject 
	private OrderAttributeService orderAttributeService;
	
	@Inject 
	private AttributeService attributeService;
	
	@Inject
	private DiscountPlanService discountPlanService;
	
	public CommercialOrderDto create(CommercialOrderDto orderDto) {
		checkParam(orderDto);
		final CommercialOrder order = new CommercialOrder();
		
		final BillingAccount billingAccount = billingAccountService.findByCode(orderDto.getBillingAccountCode());
		if(billingAccount == null)
			throw new EntityDoesNotExistsException(BillingAccount.class, orderDto.getBillingAccountCode());
		order.setBillingAccount(billingAccount);
		final Seller seller = billingAccount.getCustomerAccount().getCustomer().getSeller();
		if(seller == null)
			throw new EntityDoesNotExistsException(Seller.class, orderDto.getSellerCode());
		order.setSeller(seller);
		final OrderType orderType = orderTypeService.findByCode(orderDto.getOrderTypeCode());
		if(orderType == null)
			throw new EntityDoesNotExistsException(OrderType.class, orderDto.getOrderTypeCode());
		order.setOrderType(orderType);
		
		order.setLabel(orderDto.getLabel());
		if(!Strings.isEmpty(orderDto.getQuoteCode())) {
			final CpqQuote quote = cpqQuoteService.findByCode(orderDto.getQuoteCode());
			if(quote == null)
				throw new EntityDoesNotExistsException(CpqQuote.class, orderDto.getQuoteCode());
			order.setQuote(quote);
		}
		if(!Strings.isEmpty(orderDto.getContractCode())) {
			final Contract contract = contractService.findByCode(orderDto.getContractCode());
			if(contract == null)
				throw new EntityDoesNotExistsException(Contract.class, orderDto.getContractCode());
			order.setContract(contract);
				
		}
		if(!Strings.isEmpty(orderDto.getInvoicingPlanCode())) {
			final InvoicingPlan billingPlan = invoicingPlanService.findByCode(orderDto.getInvoicingPlanCode());
			if(billingPlan == null)
				throw new EntityDoesNotExistsException(InvoicingPlan.class, orderDto.getInvoicingPlanCode());
			order.setInvoicingPlan(billingPlan);
		}
		if(!Strings.isEmpty(orderDto.getUserAccountCode())) {
			final UserAccount userAccount = userAccountService.findByCode(orderDto.getUserAccountCode());
			if(userAccount == null)
				throw new EntityDoesNotExistsException(UserAccount.class, orderDto.getUserAccountCode());
			order.setUserAccount(userAccount);
		}
		if(orderDto.getAccessDto() != null) {
			var accessDto = orderDto.getAccessDto();
			if(Strings.isEmpty(accessDto.getCode()))
				missingParameters.add("accessDto.code");
			if(Strings.isEmpty(accessDto.getSubscription()))
				missingParameters.add("accessDto.subscription");
			handleMissingParameters();
			var subscription = subscriptionService.findByCode(accessDto.getSubscription());
			if(subscription == null) 
				throw new EntityDoesNotExistsException("No Access found for subscription : " + accessDto.getSubscription());
			var access = accessService.findByUserIdAndSubscription(accessDto.getCode(), subscription, accessDto.getStartDate(), accessDto.getEndDate());
			if(access == null)
				throw new EntityDoesNotExistsException("No Access found for code : " + accessDto.getCode() + " and subscription : " + accessDto.getSubscription());
			order.setAccess(access);
		}
		if(Strings.isEmpty(orderDto.getQuoteCode())) {
			
		}
		order.setStatus(CommercialOrderEnum.DRAFT.toString());
		order.setStatusDate(Calendar.getInstance().getTime());
		order.setOrderProgress(orderDto.getOrderProgress()!=null?orderDto.getOrderProgress():0);
		order.setProgressDate(new Date());
		order.setOrderDate(orderDto.getOrderDate()!=null?orderDto.getOrderDate():new Date());
		order.setRealisationDate(orderDto.getRealisationDate());
		order.setCustomerServiceBegin(orderDto.getCustomerServiceBegin());
		order.setCustomerServiceDuration(orderDto.getCustomerServiceDuration());
		order.setExternalReference(orderDto.getExternalReference());
		populateCustomFields(orderDto.getCustomFields(), order, true);
		if(!Strings.isEmpty(orderDto.getOrderParentCode())) {
			final Order orderParent = orderService.findByCode(orderDto.getOrderParentCode());
			if(orderParent == null)
				throw new EntityDoesNotExistsException(Order.class, orderDto.getOrderParentCode());
			order.setOrderParent(orderParent);
		}
		order.setOrderInvoiceType(invoiceTypeService.getDefaultCommercialOrder());
		processOrderLot(orderDto, order);
		commercialOrderService.create(order);
		CommercialOrderDto dto = new CommercialOrderDto(order);
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(order));
		return dto;
	}

	public CommercialOrderDto updateUserAccount(Long commercialOrderId, String userAccountCode){
		if(commercialOrderId == null)
			missingParameters.add("commercialOrderId");
		if(userAccountCode == null)
			missingParameters.add("userAccountCode");
		handleMissingParameters();
		CommercialOrder order = commercialOrderService.findById(commercialOrderId);
		if (order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
		UserAccount userAccount = loadEntityByCode(userAccountService, userAccountCode, UserAccount.class);
		order.setUserAccount(userAccount);
		order = commercialOrderService.update(order);
		return new CommercialOrderDto(order);
	}

	public CommercialOrderDto updateOrderInvoicingPlan(Long commercialOrderId, String invoicingPlanCode){
		if(commercialOrderId == null)
			missingParameters.add("commercialOrderId");
		if(invoicingPlanCode == null)
			missingParameters.add("userAccountCode");
		handleMissingParameters();
		CommercialOrder order = commercialOrderService.findById(commercialOrderId);
		if (order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
		InvoicingPlan invoicingPlan = loadEntityByCode(invoicingPlanService, invoicingPlanCode, InvoicingPlan.class);
		order.setInvoicingPlan(invoicingPlan);
		order = commercialOrderService.update(order);
		return new CommercialOrderDto(order);
	}
	
	public CommercialOrderDto update(CommercialOrderDto orderDto) {
		if(orderDto.getId() == null)
			missingParameters.add("id");
		handleMissingParameters();
		final CommercialOrder order = commercialOrderService.findById(orderDto.getId());
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderDto.getId());
		if(!order.getStatus().equals(CommercialOrderEnum.DRAFT.toString())) {
			throw new BusinessApiException("The Order can not be edited, the status must not be : " + order.getStatus());
		}
		if(order.getOrderProgress() != null)
			order.setOrderProgressTmp(Integer.valueOf(order.getOrderProgress().intValue()));
		
		if(!Strings.isEmpty(orderDto.getSellerCode())) {
			final Seller seller = sellerService.findByCode(orderDto.getSellerCode());
			if(seller == null)
				throw new EntityDoesNotExistsException(Seller.class, orderDto.getSellerCode());
			order.setSeller(seller);
		}
		if(!Strings.isEmpty(orderDto.getBillingAccountCode())) {
			final BillingAccount billingAccount = billingAccountService.findByCode(orderDto.getBillingAccountCode());
			if(billingAccount == null)
				throw new EntityDoesNotExistsException(BillingAccount.class, orderDto.getBillingAccountCode());
			order.setBillingAccount(billingAccount);
		}
		
		if(!Strings.isEmpty(orderDto.getOrderTypeCode())) {
			final OrderType orderType = orderTypeService.findByCode(orderDto.getOrderTypeCode());
			if(orderType == null)
				throw new EntityDoesNotExistsException(OrderType.class, orderDto.getOrderTypeCode());
			order.setOrderType(orderType);
		}
		order.setLabel(orderDto.getLabel());
		if(!Strings.isEmpty(orderDto.getQuoteCode())) {
			final CpqQuote quote = cpqQuoteService.findByCode(orderDto.getQuoteCode());
			if(quote == null)
				throw new EntityDoesNotExistsException(CpqQuote.class, orderDto.getQuoteCode());
			order.setQuote(quote);
		}
		if(!Strings.isEmpty(orderDto.getContractCode())) {
			final Contract contract = contractService.findByCode(orderDto.getContractCode());
			if(contract == null)
				throw new EntityDoesNotExistsException(Contract.class, orderDto.getContractCode());
			order.setContract(contract);
				
		}
		if(!Strings.isEmpty(orderDto.getInvoicingPlanCode())) {
			final InvoicingPlan billingPlan = invoicingPlanService.findByCode(orderDto.getInvoicingPlanCode());
			if(billingPlan == null)
				throw new EntityDoesNotExistsException(InvoicingPlan.class, orderDto.getInvoicingPlanCode());
			order.setInvoicingPlan(billingPlan);
		}

		if(!Strings.isEmpty(orderDto.getUserAccountCode())) {
			final UserAccount userAccount = userAccountService.findByCode(orderDto.getUserAccountCode());
			if(userAccount == null)
				throw new EntityDoesNotExistsException(UserAccount.class, orderDto.getUserAccountCode());
			order.setUserAccount(userAccount);
		}
		if(orderDto.getAccessDto() != null) {
			var accessDto = orderDto.getAccessDto();
			if(Strings.isEmpty(accessDto.getCode()))
				missingParameters.add("accessDto.code");
			if(Strings.isEmpty(accessDto.getSubscription()))
				missingParameters.add("accessDto.subscription");
			handleMissingParameters();
			var subscription = subscriptionService.findByCode(accessDto.getSubscription());
			if(subscription == null) 
				throw new EntityDoesNotExistsException("No Access found for subscription : " + accessDto.getSubscription());
			var access = accessService.findByUserIdAndSubscription(accessDto.getCode(), subscription, accessDto.getStartDate(), accessDto.getEndDate());
			if(access == null)
				throw new EntityDoesNotExistsException("No Access found for code : " + accessDto.getCode() + " and subscription : " + accessDto.getSubscription());
			order.setAccess(access);
		}
		if(orderDto.getOrderProgress() != null)
			order.setOrderProgress(orderDto.getOrderProgress());
		if(orderDto.getProgressDate() != null)
			order.setProgressDate(orderDto.getProgressDate());
		if(orderDto.getOrderDate() != null)
			order.setOrderDate(orderDto.getOrderDate());
		if(orderDto.getRealisationDate() != null)
			order.setRealisationDate(orderDto.getRealisationDate());
		if(orderDto.getCustomerServiceBegin() != null)
			order.setCustomerServiceBegin(orderDto.getCustomerServiceBegin());
		
		order.setCustomerServiceDuration(orderDto.getCustomerServiceDuration());
		if(!Strings.isEmpty(orderDto.getExternalReference()))
			order.setExternalReference(orderDto.getExternalReference());
		if(!Strings.isEmpty(orderDto.getOrderParentCode())) {
			final Order orderParent = orderService.findByCode(orderDto.getOrderParentCode());
			if(orderParent == null)
				throw new EntityDoesNotExistsException(Order.class, orderDto.getOrderParentCode());
			order.setOrderParent(orderParent);
		}
		populateCustomFields(orderDto.getCustomFields(), order, false);
		processOrderLot(orderDto, order);
		commercialOrderService.update(order);
		CommercialOrderDto dto = new CommercialOrderDto(order);
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(order));
		return dto;
	}
	
	public void delete(Long orderId) {
		if(orderId == null)
			missingParameters.add("orderId");
		handleMissingParameters();
		
		final CommercialOrder order = commercialOrderService.findById(orderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderId);
		if(order.getStatus().equalsIgnoreCase(CommercialOrderEnum.CANCELED.toString()))
			commercialOrderService.remove(order);
		else
			throw new MeveoApiException("Can not be deleted, only status In_Creation or Canceled can be delete, current status : " + order.getStatus() );
	}
	
	public void updateStatus(Long commercialOrderId, String statusTarget) {
		if(Strings.isEmpty(statusTarget)) {
			missingParameters.add("status");
		}
		handleMissingParameters();
		CommercialOrder order = commercialOrderService.findById(commercialOrderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
		if(order.getStatus().equalsIgnoreCase(CommercialOrderEnum.CANCELED.toString())) {
			throw new MeveoApiException("can not change order status, because the current status is Canceled");
		}
		
		if(statusTarget.equalsIgnoreCase(CommercialOrderEnum.COMPLETED.toString())) {
			if(!order.getStatus().equalsIgnoreCase(CommercialOrderEnum.FINALIZED.toString()))
				throw new MeveoApiException("The Order is not yet finalize");
		}else if (statusTarget.equalsIgnoreCase(CommercialOrderEnum.VALIDATED.toString())) {
			if(!order.getStatus().equalsIgnoreCase(CommercialOrderEnum.COMPLETED.toString()))
				throw new MeveoApiException("The Order is not yet complete");
			
		}else if(statusTarget.equalsIgnoreCase(CommercialOrderEnum.FINALIZED.toString())){
            order = serviceSingleton.assignCommercialOrderNumber(order);
        }
		List<String> status = allStatus(CommercialOrderEnum.class, "commercialOrder.status", "");

		if(!status.contains(statusTarget.toLowerCase())) {
			throw new MeveoApiException("Status is invalid, here is the list of available status : " + status);
		}
		order.setStatus(statusTarget);
		order.setStatusDate(Calendar.getInstance().getTime());
		
		commercialOrderService.update(order);
	}
	
	public CommercialOrderDto duplicate(Long commercialOrderId) {
		if(commercialOrderId == null) {
			missingParameters.add("commercialOrderId");
		}
		handleMissingParameters();
		final CommercialOrder order = commercialOrderService.findById(commercialOrderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
		return new CommercialOrderDto(commercialOrderService.duplicate(order));
	}
	

	private static final String DEFAULT_SORT_ORDER_ID = "id";
	public GetListCommercialOrderDtoResponse listCommercialOrder(PagingAndFiltering pagingAndFiltering) {
		 if (pagingAndFiltering == null) {
			 pagingAndFiltering = new PagingAndFiltering();
		 }
		 String sortBy = DEFAULT_SORT_ORDER_ID;
		 if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
			 sortBy = pagingAndFiltering.getSortBy();
		 }
		 var filters = new HashedMap<String, Object>();
		 pagingAndFiltering.getFilters().forEach( (key, value) -> {
			 String newKey = key.replace("sellerCode", "seller.code")
					 .replace("billingAccountCode", "billingAccount.code")
					 .replace("quoteCode", "quote.code")
					 .replace("contractCode", "contract.code")
					 .replace("orderTypeCode", "orderType.code");
			 filters.put(key.replace(key, newKey), value);
		 });
		 pagingAndFiltering.getFilters().clear();
		 pagingAndFiltering.getFilters().putAll(filters);
		 List<String> fields = Arrays.asList("seller", "billingAccount", "quote", "contract", "orderType");
		 PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, fields, pagingAndFiltering, CommercialOrder.class);
		 Long totalCount = commercialOrderService.count(paginationConfiguration);
		 GetListCommercialOrderDtoResponse result = new GetListCommercialOrderDtoResponse();
		 result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		 result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
		 if(totalCount > 0) {
			 commercialOrderService.list(paginationConfiguration).stream().forEach(co -> {
			 	/*if(co.getId() == 1){
					OrderAdvancementScript temp = new OrderAdvancementScript();
					Map<String, Object> methodContext = new HashMap<String, Object>();
					co.setOrderProgress(100);
					methodContext.put("commercialOrder", co);
					temp.execute(methodContext );
				}*/
				 result.getCommercialOrderDtos().add(new CommercialOrderDto(co));
			 });
		 }
		return result;
	}

	public CommercialOrderDto findByOrderNumber(String orderNumber) {
		if(Strings.isEmpty(orderNumber))
			missingParameters.add("orderNumber");
		handleMissingParameters();
		
		CommercialOrder commercialOrder =  commercialOrderService.findByOrderNumer(orderNumber);
		if(commercialOrder == null)
			throw new EntityDoesNotExistsException("No Commercial order found for order number = " + orderNumber);
		CommercialOrderDto dto = new CommercialOrderDto(commercialOrder);
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(commercialOrder));
		return dto;
	}
	
	private void checkParam(CommercialOrderDto order) {
		if(Strings.isEmpty(order.getBillingAccountCode()))
			missingParameters.add("billingAccountCode");
		if(Strings.isEmpty(order.getOrderTypeCode()))
			missingParameters.add("orderTypeCode");
		
		handleMissingParameters();
	}

	public CommercialOrderDto validateOrder(Long orderId){
		CommercialOrder order = commercialOrderService.findById(orderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderId);

		if(order.getInvoicingPlan() != null)
			throw new BusinessException("Order id: " + order.getId() + ", please go throw the validation plan in order to validate it");

		return validateOrder(order, false);
	}

	public CommercialOrderDto validateOrder(CommercialOrder order, boolean orderCompleted) {
		ParamBean paramBean = ParamBean.getInstance();
		String sellerCode = order.getBillingAccount().getCustomerAccount().getCustomer().getSeller().getCode();
		String orderScriptCode = paramBean.getProperty("seller." + sellerCode + ".orderValidationScript", "");
		if (!StringUtils.isBlank(orderScriptCode)) {
			ScriptInstance scriptInstance = scriptInstanceService.findByCode(orderScriptCode);
			if (scriptInstance != null) {
				String orderValidationProcess = scriptInstance.getCode();
				ScriptInterface script = scriptInstanceService.getScriptInstance(orderValidationProcess);
				Map<String, Object> methodContext = new HashMap<String, Object>();
				methodContext.put("commercialOrder", order);
				methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
				methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
				if (script != null) {
					script.execute(methodContext);
					return new CommercialOrderDto((CommercialOrder) methodContext.get(Script.RESULT_VALUE));
				} else
					throw new BusinessException("No script interface found with code: " + orderValidationProcess);
			} else
				throw new EntityDoesNotExistsException(ScriptInstance.class, orderScriptCode);
		}
		CommercialOrder commercialOrder = commercialOrderService.validateOrder(order, orderCompleted);
		return new CommercialOrderDto(commercialOrder);
	}
	
	private void processOrderLot(CommercialOrderDto postData, CommercialOrder commercialOrder) {
		Set<String> orderLots = postData.getOrderLotCodes(); 
		List<OrderLot> orderLotList=new ArrayList<OrderLot>();
		if(orderLots != null && !orderLots.isEmpty()){
			for(String code:orderLots) {
				OrderLot orderLot=orderLotService.findByCode(code);
				if(orderLot == null) { 
					throw new EntityDoesNotExistsException(OrderLot.class,code);
				}
				orderLotList.add(orderLot);
			}
		}
		commercialOrder.setOrderLots(orderLotList);
	} 
	
	
	public OrderOfferDto createOrderOffer(OrderOfferDto orderOfferDto) throws MeveoApiException, BusinessException {

		if (orderOfferDto.getCommercialOrderId()==null) {
			missingParameters.add("commercialOrderId");
		}
		if (StringUtils.isBlank(orderOfferDto.getOfferTemplateCode())) {
			missingParameters.add("offerTemplateCode");
		}
		handleMissingParametersAndValidate(orderOfferDto);

		CommercialOrder commercialOrder = commercialOrderService.findById(orderOfferDto.getCommercialOrderId());

		if ( commercialOrder== null) {
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderOfferDto.getCommercialOrderId());
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(orderOfferDto.getOfferTemplateCode());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, orderOfferDto.getOfferTemplateCode());
		}
		DiscountPlan discountPlan=null;
		if(!StringUtils.isBlank(orderOfferDto.getDiscountPlanCode())) {
		 discountPlan = discountPlanService.findByCode(orderOfferDto.getDiscountPlanCode());	
		if (discountPlan == null)
			throw new EntityDoesNotExistsException(DiscountPlan.class, orderOfferDto.getDiscountPlanCode());	
		}
		
		OrderOffer orderOffer = new OrderOffer();
		orderOffer.setOrder(commercialOrder);
		orderOffer.setOfferTemplate(offerTemplate);
		orderOffer.setDiscountPlan(discountPlan);
		orderOfferService.create(orderOffer);
		orderOfferDto.setOrderOfferId(orderOffer.getId());
		createOrderProduct(orderOfferDto.getOrderProducts(),orderOffer);
		createOrderAttribute(orderOfferDto.getOrderAttributes(),null,orderOffer);
		return orderOfferDto;
	}
	
	public OrderOfferDto updateOrderOffer(OrderOfferDto orderOfferDto) throws MeveoApiException, BusinessException { 
    	if (orderOfferDto.getOrderOfferId()==null) {
    		missingParameters.add("orderOfferId");
    	}
    	OrderOffer orderOffer = orderOfferService.findById(orderOfferDto.getOrderOfferId());
    	if (orderOffer == null) {
    		throw new EntityDoesNotExistsException(OrderOffer.class, orderOfferDto.getOrderOfferId());
    	}
    	CommercialOrder commercialOrder=null;
    	if(orderOfferDto.getCommercialOrderId()!=null) {
    	 commercialOrder = commercialOrderService.findById(orderOfferDto.getCommercialOrderId());

    	if ( commercialOrder== null) {
    		throw new EntityDoesNotExistsException(CommercialOrder.class, orderOfferDto.getCommercialOrderId());
    	}
    	}
    	OfferTemplate offerTemplate=null;
    	if(!StringUtils.isBlank(orderOfferDto.getOfferTemplateCode())) {
    		 offerTemplate = offerTemplateService.findByCode(orderOfferDto.getOfferTemplateCode());
        	if (offerTemplate == null) {
        		throw new EntityDoesNotExistsException(OfferTemplate.class, orderOfferDto.getOfferTemplateCode());
        	}	
    	}
    	DiscountPlan discountPlan=null;
		if(!StringUtils.isBlank(orderOfferDto.getDiscountPlanCode())) {
		 discountPlan = discountPlanService.findByCode(orderOfferDto.getDiscountPlanCode());	
		if (discountPlan == null)
			throw new EntityDoesNotExistsException(DiscountPlan.class, orderOfferDto.getDiscountPlanCode());	
		}
    	 
    	orderOffer.setOrder(commercialOrder);
    	orderOffer.setOfferTemplate(offerTemplate);
    	orderOffer.setDiscountPlan(discountPlan);
    	processOrderProductFromOffer(orderOfferDto, orderOffer); 
        processOrderAttribute(orderOfferDto,  orderOffer);
    	orderOfferService.update(orderOffer); 
    	return orderOfferDto;
    }
	
	private void processOrderProductFromOffer(OrderOfferDto orderOfferDTO, OrderOffer orderOffer) {
        List<OrderProductDto> orderProductDtos = orderOfferDTO.getOrderProducts(); 
        var existencOrderProducts = orderOffer.getProducts();
        var hasExistingOrders = existencOrderProducts != null && !existencOrderProducts.isEmpty();
        if (orderProductDtos != null && !orderProductDtos.isEmpty()) {
            var newOrderProducts = new ArrayList<OrderProduct>();
            OrderProduct orderProduct = null; 
            for (OrderProductDto orderProductDto : orderProductDtos) {
                orderProduct = getOrderProductFromDto(orderProductDto, orderOffer);
                newOrderProducts.add(orderProduct); 
            }
            if (!hasExistingOrders) {
                orderOffer.getProducts().addAll(newOrderProducts);
            } else {
                existencOrderProducts.retainAll(newOrderProducts);
                for (OrderProduct qpNew : newOrderProducts) {
                    int index = existencOrderProducts.indexOf(qpNew);
                    if (index >= 0) {
                        OrderProduct old = existencOrderProducts.get(index);
                        old.update(qpNew);
                    } else {
                        existencOrderProducts.add(qpNew);
                    }
                }
            }
        } else if (hasExistingOrders) {
            orderOffer.getProducts().removeAll(existencOrderProducts);
        }
    }
	 private void processOrderProduct(OrderProductDto orderProductDTO, OrderProduct q) {
	        var orderAttributeDtos = orderProductDTO.getOrderAttributes();
	        var hasOrderProductDtos = orderAttributeDtos != null && !orderAttributeDtos.isEmpty();

	        var existencOrderProducts = q.getOrderAttributes();
	        var hasExistingOrders = existencOrderProducts != null && !existencOrderProducts.isEmpty();

	        if(hasOrderProductDtos) {
	            var newOrderProducts = new ArrayList<OrderAttribute>();
	            OrderAttribute orderAttribute = null;
	            for (OrderAttributeDto orderAttributeDTO : orderAttributeDtos) {
	                orderAttribute = getOrderAttributeFromDto(orderAttributeDTO, q,null);
	                newOrderProducts.add(orderAttribute);
	            }
	            if(!hasExistingOrders) {
	                q.getOrderAttributes().addAll(newOrderProducts);
	            }else {
	                existencOrderProducts.retainAll(newOrderProducts);
	                for (OrderAttribute qpNew : newOrderProducts) {
	                    int index = existencOrderProducts.indexOf(qpNew);
	                    if(index >= 0) {
	                    	OrderAttribute old = existencOrderProducts.get(index);
	                        old.update(qpNew);
	                    }else {
	                        existencOrderProducts.add(qpNew);
	                    }
	                }
	            }
	        }else if(hasExistingOrders){
	            q.getOrderAttributes().removeAll(existencOrderProducts);
	        }
	    }
	 
	 private OrderAttribute getOrderAttributeFromDto(OrderAttributeDto orderAttributeDTO, OrderProduct orderProduct,OrderOffer orderOffer) {
  
		 OrderAttribute orderAttribute = null;
		 if(orderAttributeDTO.getOrderAttributeId() != null) {
			 orderAttribute = orderAttributeService.findById(orderAttributeDTO.getOrderAttributeId());
		 } 
		 if(orderAttribute == null) { 
			 orderAttribute= populateOrderAttribute(orderAttributeDTO, orderProduct, null, orderOffer);
			 orderAttributeService.create(orderAttribute);
		 }
		 if(orderProduct.getId() != orderAttribute.getOrderProduct().getId()) {
		  throw new MeveoApiException("order Attribute is already attached to : " + orderAttribute.getOrderProduct().getId());  
		 }
		 return orderAttribute;
	 }
    
    private void processOrderAttribute(OrderOfferDto orderOfferDTO, OrderOffer orderOffer) { 
        var orderAttributeDtos = orderOfferDTO.getOrderAttributes();   
        var existencOrderAttributes = orderOffer.getOrderAttributes();
        var hasExistingOrders = existencOrderAttributes != null && !existencOrderAttributes.isEmpty();

        if (orderAttributeDtos != null && !orderAttributeDtos.isEmpty()) {
            var newOrderAttributes = new ArrayList<OrderAttribute>();
            OrderAttribute orderAttribute = null; 
            for (OrderAttributeDto orderAttributeDto : orderAttributeDtos) {
                orderAttribute = getOrderAttributeFromDto(orderAttributeDto, null,orderOffer);
                newOrderAttributes.add(orderAttribute); 
            }
            if (!hasExistingOrders) {
                orderOffer.getOrderAttributes().addAll(newOrderAttributes);
            } else {
                existencOrderAttributes.retainAll(newOrderAttributes);
                for (OrderAttribute qpNew : newOrderAttributes) {
                    int index = existencOrderAttributes.indexOf(qpNew);
                    if (index >= 0) {
                        OrderAttribute old = existencOrderAttributes.get(index);
                        old.update(qpNew);
                    } else {
                        existencOrderAttributes.add(qpNew);
                    }
                }
            }
        } else if (hasExistingOrders) {
            orderOffer.getOrderAttributes().removeAll(existencOrderAttributes);
        }
    }
  
	
    private OrderProduct getOrderProductFromDto(OrderProductDto orderProductDTO, OrderOffer orderOffer) { 
    	 if (orderProductDTO.getOrderProductId()==null) {
             missingParameters.add("orderProductId");
         handleMissingParameters();
         }
            OrderProduct orderProduct = orderProductService.findById(orderProductDTO.getOrderProductId());  
        if (orderProduct == null) {  
        	orderProduct= populateOrderProduct(orderProductDTO, orderOffer,orderProduct);
        	orderProductService.create(orderProduct);
        }else {
        	orderProduct= populateOrderProduct(orderProductDTO, orderOffer,orderProduct);
        }
        	processOrderProduct( orderProductDTO, orderProduct); 
        
        return orderProduct;
        
    }
     
    public OrderProduct populateOrderProduct(OrderProductDto orderProductDto,OrderOffer orderOffer,OrderProduct orderProduct) {
    	CommercialOrder commercialOrder =null;
    	if(!StringUtils.isBlank(orderProductDto.getCommercialOrderId())) {
         commercialOrder = commercialOrderService.findById(orderProductDto.getCommercialOrderId());
		if (commercialOrder == null) {
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderProductDto.getCommercialOrderId());
		}
    	}
		OrderLot orderLot=null;
		if(!StringUtils.isBlank(orderProductDto.getOrderLotCode())) {
		 orderLot=orderLotService.findByCode(orderProductDto.getOrderLotCode());
		if(orderLot == null) { 
			throw new EntityDoesNotExistsException(OrderLot.class,orderProductDto.getOrderLotCode());
		}
		}
		ProductVersion productVersion =null;
		if(!StringUtils.isBlank(orderProductDto.getProductCode())&&!StringUtils.isBlank(orderProductDto.getProductVersion()) ) {
		 productVersion = productVersionService.findByProductAndVersion(orderProductDto.getProductCode(), orderProductDto.getProductVersion());
		if(productVersion == null) {
			throw new EntityDoesNotExistsException(ProductVersion.class, orderProductDto.getProductCode() +","+ orderProductDto.getProductVersion());
		}
		} 
		
		DiscountPlan discountPlan=null;
		if(!StringUtils.isBlank(orderProductDto.getDiscountPlanCode())) {
		 discountPlan = discountPlanService.findByCode(orderProductDto.getDiscountPlanCode());	
		if (discountPlan == null)
			throw new EntityDoesNotExistsException(DiscountPlan.class, orderProductDto.getDiscountPlanCode());	
		}
		
		if(orderProduct==null) {
			orderProduct=new OrderProduct();
		}
		orderProduct.setOrder(commercialOrder);
		orderProduct.setOrderServiceCommercial(orderLot);
		orderProduct.setProductVersion(productVersion);
		orderProduct.setDiscountPlan(discountPlan);
		orderProduct.setOrderOffer(orderOffer); 
		orderProduct.setQuantity(orderProductDto.getQuantity()); 
		orderProduct.updateAudit(currentUser); 
		return orderProduct;
    }
    
	private void createOrderProduct(List<OrderProductDto> orderProductDtos, OrderOffer orderOffer) {
		if(orderProductDtos != null && !orderProductDtos.isEmpty()) { 
			for (OrderProductDto orderProductDto : orderProductDtos) {  
				OrderProduct orderProduct=populateOrderProduct(orderProductDto,orderOffer,null);  
				orderProductService.create(orderProduct);
				//create order attributes linked to orderProduct
				createOrderAttribute(orderProductDto.getOrderAttributes(), orderProduct,orderOffer);
				orderOffer.getProducts().add(orderProduct); 
			}
		}
	}
	
	
	private void createOrderAttribute(List<OrderAttributeDto> orderAttributeDtos, OrderProduct orderProduct,OrderOffer orderOffer) {
        if (orderAttributeDtos != null && !orderAttributeDtos.isEmpty()) {
        	if(orderProduct!=null) {
        		orderProduct.getOrderAttributes().clear(); 
        		}
            orderAttributeDtos.stream()
                    .map(orderAttributeDTO -> populateOrderAttribute(orderAttributeDTO, orderProduct, orderProduct!=null?orderProduct.getProductVersion().getAttributes():null,orderOffer))
                    .collect(Collectors.toList())
                    .forEach(orderAttribute -> orderAttributeService.create(orderAttribute));
        }
    }
	
    private OrderAttribute populateOrderAttribute(OrderAttributeDto orderAttributeDTO, OrderProduct  orderProduct, List<Attribute> productAttributes,OrderOffer orderOffer) {
        if (Strings.isEmpty( orderAttributeDTO.getOrderAttributeCode())) {
            missingParameters.add("orderAttributeCode");
        handleMissingParameters();
        }
        Attribute attribute=null;
        if(!StringUtils.isBlank(orderAttributeDTO.getOrderAttributeCode())) {
         attribute = attributeService.findByCode(orderAttributeDTO.getOrderAttributeCode());
        if (attribute == null) {
            throw new EntityDoesNotExistsException(Attribute.class, orderAttributeDTO.getOrderAttributeCode());
        }
        }
        if(productAttributes != null && !productAttributes.contains(attribute) && orderProduct!=null){
            throw new BusinessApiException(String.format("Product version (code: %s, version: %d), doesn't contain attribute code: %s", orderProduct.getProductVersion().getProduct().getCode() , orderProduct.getProductVersion().getCurrentVersion(), attribute.getCode()));
        }
        CommercialOrder commercialOrder =null;
    	if(!StringUtils.isBlank(orderAttributeDTO.getCommercialOrderId())) {
         commercialOrder = commercialOrderService.findById(orderAttributeDTO.getCommercialOrderId());
		if (commercialOrder == null) {
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderAttributeDTO.getCommercialOrderId());
		}
    	}
		OrderLot orderLot=null;
		if(!StringUtils.isBlank(orderAttributeDTO.getOrderLotCode())) {
		 orderLot=orderLotService.findByCode(orderAttributeDTO.getOrderLotCode());
		if(orderLot == null) { 
			throw new EntityDoesNotExistsException(OrderLot.class,orderAttributeDTO.getOrderLotCode());
		}
		} 
        OrderAttribute orderAttribute = new OrderAttribute();
        orderAttribute.setAttribute(attribute);
        orderAttribute.setCommercialOrder(commercialOrder);
		orderAttribute.setOrderLot(orderLot);
        orderAttribute.setStringValue(orderAttributeDTO.getStringValue());
        orderAttribute.setDoubleValue(orderAttributeDTO.getDoubleValue());
        orderAttribute.setDateValue(orderAttributeDTO.getDateValue());
        orderAttribute.updateAudit(currentUser);
        if(orderProduct!=null ) {
            orderProduct.getOrderAttributes().add(orderAttribute);
            orderAttribute.setOrderProduct(orderProduct);
        }
        if(orderOffer!=null) {
        orderAttribute.setOrderOffer(orderOffer);
        }
        if(!orderAttributeDTO.getLinkedOrderAttribute().isEmpty()){
            List<OrderAttribute> linkedOrderAttributes = orderAttributeDTO.getLinkedOrderAttribute()
                    .stream()
                    .map(dto -> {
                        OrderAttribute linkedAttribute = populateOrderAttribute(dto, orderProduct, productAttributes,orderOffer);
                        linkedAttribute.setParentAttributeValue(orderAttribute);
                        return linkedAttribute;
                    })
                    .collect(Collectors.toList());
            orderAttribute.setAssignedAttributeValue(linkedOrderAttributes);
        }
        return orderAttribute;
    }
   
    public void removeOrderOffer(Long id) throws MeveoApiException, BusinessException { 
    	OrderOffer orderOffer = orderOfferService.findById(id);
    	if (orderOffer == null) {
    		throw new EntityDoesNotExistsException(ProductOrder.class, id);
    	} 
    	orderOfferService.remove(orderOffer);

    }
    
	public OrderOfferDto findOrderOffer(Long id) {
		if(id==null)
			missingParameters.add("id");
	    OrderOffer orderOffer = orderOfferService.findById(id);
		if(orderOffer == null)
			throw new EntityDoesNotExistsException(OrderOffer.class, id);
		return new OrderOfferDto(orderOffer, true,true,true);
	}
}
