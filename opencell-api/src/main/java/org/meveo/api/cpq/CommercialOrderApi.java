package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
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
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.order.Order;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.InvoicingPlanService;
import org.meveo.service.cpq.order.OrderLotService;
import org.meveo.service.cpq.order.OrderTypeService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderService;
import org.primefaces.model.SortOrder;


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
    @Inject private OrderLotService orderLotService;
	
	public CommercialOrderDto create(CommercialOrderDto orderDto) {
		checkParam(orderDto);
		final CommercialOrder order = new CommercialOrder();
		final Seller seller = sellerService.findByCode(orderDto.getSellerCode());
		if(seller == null)
			throw new EntityDoesNotExistsException(Seller.class, orderDto.getSellerCode());
		order.setSeller(seller);
		final BillingAccount billingAccount = billingAccountService.findByCode(orderDto.getBillingAccountCode());
		if(billingAccount == null)
			throw new EntityDoesNotExistsException(BillingAccount.class, orderDto.getBillingAccountCode());
		order.setBillingAccount(billingAccount);
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
		order.setStatus(CommercialOrderEnum.DRAFT.toString());
		order.setStatusDate(Calendar.getInstance().getTime());
		order.setOrderProgress(orderDto.getOrderProgress());
		order.setProgressDate(orderDto.getProgressDate());
		order.setOrderDate(orderDto.getOrderDate());
		order.setRealisationDate(orderDto.getRealisationDate());
		order.setCustomerServiceBegin(orderDto.getCustomerServiceBegin());
		order.setCustomerServiceDuration(orderDto.getCustomerServiceDuration());
		order.setExternalReference(orderDto.getExternalReference());
		if(!Strings.isEmpty(orderDto.getOrderParentCode())) {
			final Order orderParent = orderService.findByCode(orderDto.getOrderParentCode());
			if(orderParent == null)
				throw new EntityDoesNotExistsException(Order.class, orderDto.getOrderParentCode());
			order.setOrderParent(orderParent);
		}
		if(!Strings.isEmpty(orderDto.getOrderLotCode())) {
			OrderLot orderLot = loadEntityByCode(orderLotService, orderDto.getOrderLotCode(), OrderLot.class);
			order.setOrderLot(orderLot);
		}
		order.setOrderInvoiceType(invoiceTypeService.getDefaultCommercialOrder());
		commercialOrderService.create(order);
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

		if(!Strings.isEmpty(orderDto.getOrderLotCode())) {
			OrderLot orderLot = loadEntityByCode(orderLotService, orderDto.getOrderLotCode(), OrderLot.class);
			order.setOrderLot(orderLot);
		}
		commercialOrderService.update(order);
		return new CommercialOrderDto(order);
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
		final CommercialOrder order = commercialOrderService.findById(commercialOrderId);
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
			
		}
		List<String> status = allStatus();

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
	
	public CommercialOrderDto validate(Long commercialOrderId) {
		if(commercialOrderId == null) {
			missingParameters.add("commercialOrderId");
		}
		handleMissingParameters();
		final CommercialOrder order = commercialOrderService.findById(commercialOrderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
		if(!order.getStatus().equalsIgnoreCase(CommercialOrderEnum.COMPLETED.toString()))
			throw new MeveoApiException("the status of order must be COMPLETED.");
		return new CommercialOrderDto(commercialOrderService.validateOrder(order));
	}
	
	
	private List<String> allStatus(){
		
		final List<String> allStatus = new ArrayList<String>();
		for(CommercialOrderEnum status:CommercialOrderEnum.values()) {
			allStatus.add(status.toString().toLowerCase());
		}
		String statusProperties = ParamBean.getInstance().getProperty("commercialOrder.status", "");
		
		if(!Strings.isEmpty(statusProperties)) {
			for (String currentStatus : statusProperties.split(",")) {
				allStatus.add(currentStatus.toLowerCase());
			}
		}
		return allStatus;
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
					 .replace("orderTypeCode", "orderType.code")
					 .replace("orderLotCode", "orderLot.code");
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
		return new CommercialOrderDto(commercialOrder);
	}
	
	private void checkParam(CommercialOrderDto order) {
		if(Strings.isEmpty(order.getSellerCode()))
			missingParameters.add("sellerCode");
		if(Strings.isEmpty(order.getBillingAccountCode()))
			missingParameters.add("billingAccountCode");
		if(Strings.isEmpty(order.getOrderTypeCode()))
			missingParameters.add("orderTypeCode");
		if(order.getOrderProgress() == null)
			missingParameters.add("orderProgress");
		if(order.getProgressDate() == null)
			missingParameters.add("progressDate");
		if(order.getOrderDate() == null)
			missingParameters.add("orderDate");
		handleMissingParameters();
	}

	public CommercialOrderDto orderValidationProcess(Long orderId){
		CommercialOrder commercialOrder = commercialOrderService.orderValidationProcess(orderId);
		return new CommercialOrderDto(commercialOrder);
	}
}
