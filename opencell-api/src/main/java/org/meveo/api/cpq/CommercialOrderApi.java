package org.meveo.api.cpq;

import static java.lang.String.format;
import static org.meveo.model.cpq.enums.ProductStatusEnum.CLOSED;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.billing.ContractHierarchyHelper;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.cpq.OrderAttributeDto;
import org.meveo.api.dto.cpq.OrderProductDto;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.cpq.order.OrderOfferDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.cpq.GetListCommercialOrderDtoResponse;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.AdvancementRateIncreased;
import org.meveo.event.qualifier.StatusUpdated;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.commercial.*;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.order.Order;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.*;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PriceListService;
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
	@Inject private TerminationReasonService terminationReasonService;
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

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	@StatusUpdated
	private Event<CommercialOrder> commercialOrderStatusUpdatedEvent;

	@Inject
	@AdvancementRateIncreased
	protected Event<CommercialOrder> entityAdvancementRateIncreasedEventProducer;
	
    @Inject
    private ResourceBundle resourceMessages;

	@Inject
	private ServiceInstanceService serviceInstanceService;
	
	@Inject
	private ContractHierarchyHelper contractHierarchyHelper;

	@Inject
	private PriceListService priceListService;

	private static final String ADMINISTRATION_VISUALIZATION = "administrationVisualization";
    private static final String ADMINISTRATION_MANAGEMENT = "administrationManagement";
	
	public CommercialOrderDto create(CommercialOrderDto orderDto) {
		checkParam(orderDto);
		final CommercialOrder order = new CommercialOrder();
		final BillingAccount billingAccount = loadEntityByCode(billingAccountService, orderDto.getBillingAccountCode(), BillingAccount.class);
		order.setBillingAccount(billingAccount);
		Seller seller = null;

		if(!Strings.isEmpty(orderDto.getSellerCode())) {
			seller = sellerService.findByCode(orderDto.getSellerCode());
			if(seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, orderDto.getSellerCode());
			}
		} else {
			seller = billingAccount.getCustomerAccount().getCustomer().getSeller();
			if(seller == null) {
				throw new EntityDoesNotExistsException("No seller found. a seller must be defined either on quote or at customer level");
			}
		}
		order.setSeller(seller);
		
		if(!Strings.isEmpty(orderDto.getOrderTypeCode())) {
			final OrderType orderType = orderTypeService.findByCode(orderDto.getOrderTypeCode());
			order.setOrderType(orderType);
		}
		if(!Strings.isEmpty(orderDto.getDiscountPlanCode())) {
			order.setDiscountPlan(loadEntityByCode(discountPlanService, orderDto.getDiscountPlanCode(), DiscountPlan.class));
        }
		order.setLabel(orderDto.getLabel());
		if(!Strings.isEmpty(orderDto.getCode())){
			order.setCode(orderDto.getCode());
		} else {
			order.setCode(customGenericEntityCodeService.getGenericEntityCode(order));
		}
		if(!Strings.isEmpty(orderDto.getDescription())){
			order.setCode(orderDto.getDescription());
		}
		if(!Strings.isEmpty(orderDto.getQuoteCode())) {
			order.setQuote(loadEntityByCode(cpqQuoteService, orderDto.getQuoteCode(), CpqQuote.class));
		}
		if(!Strings.isEmpty(orderDto.getContractCode())) {
			order.setContract(contractHierarchyHelper.checkContractHierarchy(billingAccount, orderDto.getContractCode()));
		}
		if(!Strings.isEmpty(orderDto.getInvoicingPlanCode())) {
			order.setInvoicingPlan(loadEntityByCode(invoicingPlanService, orderDto.getInvoicingPlanCode(), InvoicingPlan.class));
		}

		if(!Strings.isEmpty(orderDto.getUserAccountCode())) {
			UserAccount userAccount = loadEntityByCode(userAccountService, orderDto.getUserAccountCode(), UserAccount.class);
			if(!userAccount.getIsConsumer()) {
	            throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Order for this user account is not allowed.");
			}
			order.setUserAccount(userAccount);
		}
		
		if(!Strings.isEmpty(orderDto.getOrderNumber())) {
			order.setOrderNumber(orderDto.getOrderNumber());
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

		if(StringUtils.isNotBlank(orderDto.getPriceListCode())) {
			PriceList priceList = priceListService.findByCode(orderDto.getPriceListCode());
			if(priceList == null) {
				throw new EntityDoesNotExistsException(PriceList.class, orderDto.getPriceListCode());
			} else if(!PriceListStatusEnum.ACTIVE.equals(priceList.getStatus())) {
				throw new BusinessApiException("Only Active PriceList can be attached to an order");
			}
			order.setPriceList(priceList);
		}

		order.setStatus(CommercialOrderEnum.DRAFT.toString());
		order.setStatusDate(Calendar.getInstance().getTime());
		order.setOrderProgress(orderDto.getOrderProgress()!=null?orderDto.getOrderProgress():0);
		order.setProgressDate(new Date());
		order.setOrderDate(orderDto.getOrderDate()!=null?orderDto.getOrderDate():new Date());

		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		if (orderDto.getDeliveryDate() != null && orderDto.getDeliveryDate().before(today) && !formatter.format(orderDto.getDeliveryDate()).equals(formatter.format(today))) {
			throw new MeveoApiException("Delivery date can't be in the past");
		}
		order.setDeliveryDate(orderDto.getDeliveryDate());
        
		order.setCustomerServiceBegin(orderDto.getCustomerServiceBegin());
		order.setCustomerServiceDuration(orderDto.getCustomerServiceDuration());
		order.setExternalReference(orderDto.getExternalReference());
		populateCustomFields(orderDto.getCustomFields(), order, true);
		if(!Strings.isEmpty(orderDto.getOrderParentCode())) {
			order.setOrderParent(loadEntityByCode(orderService, orderDto.getOrderParentCode(), Order.class));
		}
		order.setOrderInvoiceType(invoiceTypeService.getDefaultCommercialOrder());
		processOrderLot(orderDto, order);
		if(StringUtils.isNotBlank(orderDto.getBillingCycleCode())) {
			BillingCycle bc=billingCycleService.findByCode(orderDto.getBillingCycleCode());
			order.setBillingCycle(bc);
		}

		//Set the sales person name
		order.setSalesPersonName(orderDto.getSalesPersonName());
		commercialOrderService.create(order);
		CommercialOrderDto dto = new CommercialOrderDto(order);
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(order,CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
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
		if(!userAccount.getIsConsumer()) {
            throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Order for this user account is not allowed.");
		}
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
		
		if(order == null) {
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderDto.getId());
		}
		
		//Get the administration roles
        boolean isAdmin = currentUser.hasRoles(ADMINISTRATION_VISUALIZATION, ADMINISTRATION_MANAGEMENT);
			
        //Check if the status completed or validate, if yes check if the user is admin to update the salesPerosnName
		if(CommercialOrderEnum.COMPLETED.toString().equals(order.getStatus()) || CommercialOrderEnum.VALIDATED.toString().equals(order.getStatus())) {
	        if(!isAdmin) {
	        	if(!orderDto.getSalesPersonName().equalsIgnoreCase(order.getSalesPersonName())) {
	        		throw new MeveoApiException("The SalesPersonName can not be updated if the status of the order is " + order.getStatus() + " and the user is not admin");
	        	}
	        } else {
	            order.setSalesPersonName(orderDto.getSalesPersonName());
	            commercialOrderService.update(order);
	    		CommercialOrderDto dto = new CommercialOrderDto(order);
	    		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(order,CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
	    		return dto;       	
	        }
		}
		
		if(!order.getStatus().equals(CommercialOrderEnum.DRAFT.toString()) && !order.getStatus().equals(CommercialOrderEnum.FINALIZED.toString())) {
			throw new MeveoApiException("The Order can not be edited, the status must not be : " + order.getStatus());
		}
		
		//Check if the status is finalized and the user is admin and salesPersonName is updated
		if(CommercialOrderEnum.FINALIZED.toString().equals(order.getStatus()) && !isAdmin && !orderDto.getSalesPersonName().equalsIgnoreCase(order.getSalesPersonName())) {
			throw new MeveoApiException("The SalesPersonName can not be updated if the status of the order is " + order.getStatus() + " and the user is not admin");
		}
		
		order.setSalesPersonName(orderDto.getSalesPersonName());
		        
		if(!Strings.isEmpty(orderDto.getCode())){
			order.setCode(orderDto.getCode());
		}
		if(!Strings.isEmpty(orderDto.getDescription())){
			order.setCode(orderDto.getDescription());
		}
		if(order.getOrderProgress() != null)
			order.setOrderProgressTmp(Integer.valueOf(order.getOrderProgress().intValue()));
		
		if(!Strings.isEmpty(orderDto.getDiscountPlanCode())) {
			order.setDiscountPlan(loadEntityByCode(discountPlanService, orderDto.getDiscountPlanCode(), DiscountPlan.class));
        }

		if(!Strings.isEmpty(orderDto.getBillingAccountCode())) {
			final BillingAccount billingAccount = billingAccountService.findByCode(orderDto.getBillingAccountCode());
			if(billingAccount == null)
				throw new EntityDoesNotExistsException(BillingAccount.class, orderDto.getBillingAccountCode());
			order.setBillingAccount(billingAccount);
		}

		Seller seller = null;

		if(!Strings.isEmpty(orderDto.getSellerCode())) {
			seller = sellerService.findByCode(orderDto.getSellerCode());
			if(seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, orderDto.getSellerCode());
			}
		} else {
			seller = order.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
			if(seller == null) {
				throw new EntityDoesNotExistsException("No seller found. a seller must be defined either on quote or at customer level");
			}
		}
		order.setSeller(seller);
		
		if(!Strings.isEmpty(orderDto.getOrderTypeCode())) {
			final OrderType orderType = orderTypeService.findByCode(orderDto.getOrderTypeCode());
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
			order.setContract(contractHierarchyHelper.checkContractHierarchy(order.getBillingAccount(), orderDto.getContractCode()));
		}
		if (!Strings.isBlank(orderDto.getInvoicingPlanCode())) {
			final InvoicingPlan billingPlan = invoicingPlanService.findByCode(orderDto.getInvoicingPlanCode());
			if(billingPlan == null)
				throw new EntityDoesNotExistsException(InvoicingPlan.class, orderDto.getInvoicingPlanCode());
			order.setInvoicingPlan(billingPlan);
		} else if ("".equals(orderDto.getInvoicingPlanCode())) {
			order.setInvoicingPlan(null);
		}

		if(!Strings.isEmpty(orderDto.getUserAccountCode())) {
			final UserAccount userAccount = userAccountService.findByCode(orderDto.getUserAccountCode());
			if(userAccount == null)
				throw new EntityDoesNotExistsException(UserAccount.class, orderDto.getUserAccountCode());
			if(!userAccount.getIsConsumer()) {
	            throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Order for this user account is not allowed.");
			}
			order.setUserAccount(userAccount);
		} else
			order.setUserAccount(null);
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

    	if(orderDto.getDeliveryDate() != null && orderDto.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
    	order.setDeliveryDate(orderDto.getDeliveryDate());
        
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
		if(StringUtils.isNotBlank(orderDto.getBillingCycleCode())) {
			final BillingCycle bc=billingCycleService.findByCode(orderDto.getBillingCycleCode());
			if(bc == null)
				throw new EntityDoesNotExistsException(BillingCycle.class, orderDto.getBillingCycleCode());
			order.setBillingCycle(bc);
		}
		if(orderDto.getPriceListCode() != null) {
			if(!orderDto.getPriceListCode().isEmpty()) {
				PriceList priceList = priceListService.findByCode(orderDto.getPriceListCode());
				if(priceList == null) {
					throw new EntityDoesNotExistsException(PriceList.class, orderDto.getPriceListCode());
				} else if(!PriceListStatusEnum.ACTIVE.equals(priceList.getStatus())) {
					throw new BusinessApiException("Only Active PriceList can be attached to an order");
				}
				order.setPriceList(priceList);
			} else {
				order.setPriceList(null);
			}
		}
		commercialOrderService.update(order);
		CommercialOrderDto dto = new CommercialOrderDto(order);
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(order,CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
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
		validateProducts(order.getOffers());
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
		if(order.getStatus().equalsIgnoreCase(statusTarget))
			return;
		if(order.getStatus().equalsIgnoreCase(CommercialOrderEnum.CANCELED.toString())) {
			throw new MeveoApiException("can not change order status, because the current status is Canceled");
		}
		boolean shouldFireAdvancementRateIncreasedEvent = false;
		if(statusTarget.equalsIgnoreCase(CommercialOrderEnum.COMPLETED.toString())) {
			if(!order.getStatus().equalsIgnoreCase(CommercialOrderEnum.FINALIZED.toString()))
				throw new MeveoApiException("The Order is not yet finalize");
		}else if (statusTarget.equalsIgnoreCase(CommercialOrderEnum.VALIDATED.toString())) {
			if(!order.getStatus().equalsIgnoreCase(CommercialOrderEnum.COMPLETED.toString()))
				throw new MeveoApiException("The Order is not yet complete");
			
		}else if(statusTarget.equalsIgnoreCase(CommercialOrderEnum.FINALIZED.toString())){
            order = serviceSingleton.assignCommercialOrderNumber(order);
			if(order.getInvoicingPlan() != null &&
				order.getInvoicingPlan().getInvoicingPlanItems().stream()
						.filter(invoicingPlanItem -> invoicingPlanItem.getAdvancement() == null ||  invoicingPlanItem.getAdvancement()== 0)
						.findFirst().isPresent()){
				shouldFireAdvancementRateIncreasedEvent = true;
			}
        }
		List<String> status = allStatus(CommercialOrderEnum.class, "commercialOrder.status", "");

		if(!status.contains(statusTarget.toLowerCase())) {
			throw new MeveoApiException("Status is invalid, here is the list of available status : " + status);
		}
		order.setStatus(statusTarget);
		order.setStatusDate(Calendar.getInstance().getTime());

		commercialOrderService.update(order);
		commercialOrderStatusUpdatedEvent.fire(order);
		if(shouldFireAdvancementRateIncreasedEvent){
			entityAdvancementRateIncreasedEventProducer.fire(order);
		}
	}

	private void validateProducts(List<OrderOffer> orderOffers) {
		for (OrderOffer orderOffer : orderOffers) {
			if (orderOffer.getProducts() != null) {
				orderOffer.getProducts()
						.stream()
						.filter(Objects::nonNull)
						.map(OrderProduct::getProductVersion)
						.filter(Objects::nonNull)
						.map(ProductVersion::getProduct)
						.filter(product -> CLOSED.equals(product.getStatus()))
						.findAny()
						.ifPresent(product -> {
							throw new BusinessApiException(
									format("Can not perform action product status is CLOSED, product code : %s",
											product.getCode()));
						});
			}
		}
	}
	
	public CommercialOrderDto duplicate(Long commercialOrderId) {
		if(commercialOrderId == null) {
			missingParameters.add("commercialOrderId");
		}
		handleMissingParameters();
		final CommercialOrder order = commercialOrderService.findById(commercialOrderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);

		CommercialOrderDto duplicatedCommercialOrderDto = duplicateFrom(order);
		CommercialOrderDto commercialOrderDto = create(duplicatedCommercialOrderDto);
		CommercialOrder duplicatedOrder = commercialOrderService.findById(commercialOrderDto.getId());

		order.getOrderLots().stream()
				.map(orderLot -> {
					OrderLot duplicateOrderLot = new OrderLot();
					duplicateOrderLot.setCode(UUID.randomUUID().toString());
					duplicateOrderLot.setOrder(duplicatedOrder);
					duplicateOrderLot.setName(orderLot.getName());
					duplicateOrderLot.setQuoteLot(orderLot.getQuoteLot());
					orderLotService.create(duplicateOrderLot);
					return duplicateOrderLot;
				})
				.collect(Collectors.toSet());

		duplicateOrderOffers(order, duplicatedOrder);
		return commercialOrderDto;
	}

	private void duplicateOrderOffers(CommercialOrder order, CommercialOrder duplicatedOrder) {
		duplicatedOrder.setOffers(new ArrayList<>());
		order.getOffers().stream()
				.forEach( orderOffer -> {
					OrderOffer offer = new OrderOffer();
					offer.setOrder(duplicatedOrder);
					offer.setOfferTemplate(orderOffer.getOfferTemplate());
					offer.setSubscription(orderOffer.getSubscription());
					offer.setDiscountPlan(orderOffer.getDiscountPlan());
					offer.setDeliveryDate(orderOffer.getDeliveryDate());
					offer.setUserAccount(orderOffer.getUserAccount());
					offer.setOrderLineType(orderOffer.getOrderLineType());
					orderOfferService.create(offer);
					offer.setProducts(orderOffer.getProducts().stream()
							.map(orderProduct -> duplicateProduct(orderProduct, offer))
							.collect(Collectors.toList()));
					duplicatedOrder.getOffers().add(offer);
				});
	}

	private CommercialOrderDto duplicateFrom(CommercialOrder order) {
		final CommercialOrderDto duplicatedCommercialOrderDto = new CommercialOrderDto();
		duplicatedCommercialOrderDto.setStatus("DRAFT");
		duplicatedCommercialOrderDto.setCode(order.getCode()+"-copy");
		if(order.getBillingAccount() != null){
			duplicatedCommercialOrderDto.setBillingAccountCode(order.getBillingAccount().getCode());
		}
		if(order.getOrderType() != null){
			duplicatedCommercialOrderDto.setOrderTypeCode(order.getOrderType().getCode());
		}
		if(order.getSeller() != null){
			duplicatedCommercialOrderDto.setSellerCode(order.getSeller().getCode());
		}
		if(order.getDiscountPlan() != null){
			duplicatedCommercialOrderDto.setDiscountPlanCode(order.getDiscountPlan().getCode());
		}
		duplicatedCommercialOrderDto.setLabel(order.getLabel());
		duplicatedCommercialOrderDto.setDescription(order.getDescription());
		duplicatedCommercialOrderDto.setStatus(order.getStatus());
		if(order.getUserAccount() != null){
			duplicatedCommercialOrderDto.setUserAccountCode(order.getUserAccount().getCode());
		}
		duplicatedCommercialOrderDto.setOrderDate(order.getOrderDate());
		if(order.getContract() != null){
			duplicatedCommercialOrderDto.setContractCode(order.getContract().getCode());
		}
		if(order.getInvoicingPlan() != null){
			duplicatedCommercialOrderDto.setInvoicingPlanCode(order.getInvoicingPlan().getCode());
		}
		duplicatedCommercialOrderDto.setProgressDate(new Date());
		duplicatedCommercialOrderDto.setOrderDate(new Date());
		duplicatedCommercialOrderDto.setDeliveryDate(order.getDeliveryDate());
		duplicatedCommercialOrderDto.setOrderProgress(0);
		if(order.getAccess() != null){
			AccessDto accessDto = new AccessDto();
			accessDto.setCode(order.getAccess().getAccessUserId());
			accessDto.setSubscription(order.getAccess().getSubscription().getCode());
			duplicatedCommercialOrderDto.setAccessDto(accessDto);
		}
		duplicatedCommercialOrderDto.setCustomerServiceBegin(order.getCustomerServiceBegin());
		duplicatedCommercialOrderDto.setCustomerServiceDuration(order.getCustomerServiceDuration());
		duplicatedCommercialOrderDto.setExternalReference(order.getExternalReference());
		if(order.getOrderParent() != null){
			duplicatedCommercialOrderDto.setOrderParentCode(order.getOrderParent().getCode());
		}

		// build custom field
		duplicatedCommercialOrderDto.setCustomFields(
				entityToDtoConverter.getCustomFieldsDTO(order, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

		return duplicatedCommercialOrderDto;
	}

	private OrderProduct duplicateProduct(OrderProduct orderProduct, OrderOffer offer) {
		OrderProduct newProduct = new OrderProduct();
		newProduct.setOrder(orderProduct.getOrder());
		newProduct.setOrderServiceCommercial(orderProduct.getOrderServiceCommercial());
		newProduct.setProductVersion(orderProduct.getProductVersion());
		newProduct.setQuantity(orderProduct.getQuantity());
		newProduct.setDiscountPlan(orderProduct.getDiscountPlan());
		newProduct.setOrderOffer(offer);
		newProduct.setQuoteProduct(orderProduct.getQuoteProduct());
		newProduct.setDeliveryDate(orderProduct.getDeliveryDate());
		newProduct.setOrderAttributes(orderProduct.getOrderAttributes().stream()
				.map(orderAttribute -> {
					OrderAttribute attributeCopy = new OrderAttribute();
					attributeCopy.setAuditable(new Auditable(currentUser));
					attributeCopy.setCommercialOrder(orderProduct.getOrder());
					attributeCopy.setOrderProduct(newProduct);
					attributeCopy.setOrderOffer(offer);
					attributeCopy.setAttribute(orderAttribute.getAttribute());
					attributeCopy.setBooleanValue(orderAttribute.getBooleanValue());
					attributeCopy.setStringValue(orderAttribute.getStringValue());
					attributeCopy.setDoubleValue(orderAttribute.getDoubleValue());
					attributeCopy.setDateValue(orderAttribute.getDateValue());
					orderAttributeService.create(attributeCopy);
					return attributeCopy;
				})
				.collect(Collectors.toList()));
		orderProductService.create(newProduct);
		return newProduct;
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
		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(commercialOrder,CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
		return dto;
	}
	
	private void checkParam(CommercialOrderDto order) {
		if(Strings.isEmpty(order.getBillingAccountCode()))
			missingParameters.add("billingAccountCode");
		
		handleMissingParameters();
	}

	public CommercialOrderDto validateOrder(Long orderId){
		CommercialOrder order = commercialOrderService.findById(orderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderId);

		if(order.getInvoicingPlan() != null)
			throw new MeveoApiException("Order id: " + order.getId() + ", please go through the validation plan in order to validate it");

		return validateOrder(order, false);
	}

	public CommercialOrderDto validateOrder(CommercialOrder order, boolean orderCompleted) {
		BillingAccount orderBillingAccount = order.getBillingAccount();
		if(order.getUserAccount() == null && orderBillingAccount.getUsersAccounts().size() == 1){
			order.setUserAccount(orderBillingAccount.getUsersAccounts().get(0));
		}
		Optional<OrderOffer> optionalOrderOfferWithoutUA = order.getOffers().stream()
				.filter(orderOffer -> orderOffer.getUserAccount() == null)
				.findFirst();
		if(order.getUserAccount() == null && optionalOrderOfferWithoutUA.isPresent()){
			throw new MissingParameterException("Customer has no consumer. You must create a consumer for this customer in order to validate the order");
		}
		ParamBean paramBean = ParamBean.getInstance();
		String sellerCode = getSelectedSeller(order).getCode();
		String orderScriptCode = paramBean.getProperty("seller." + sellerCode + ".orderValidationScript", "");
		if (!StringUtils.isBlank(orderScriptCode)) {
			ScriptInstance scriptInstance = scriptInstanceService.findByCode(orderScriptCode);
			if (scriptInstance != null) {
				String orderValidationProcess = scriptInstance.getCode();
				ScriptInterface script = scriptInstanceService.getScriptInstance(orderValidationProcess);
				Map<String, Object> methodContext = new HashMap<>();
				methodContext.put("commercialOrder", order);
				methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
				methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
				if (script != null) {
					script.execute(methodContext);
					return new CommercialOrderDto((CommercialOrder) methodContext.get(Script.RESULT_VALUE));
				} else
					throw new MeveoApiException("No script interface found with code: " + orderValidationProcess);
			} else
				throw new EntityDoesNotExistsException(ScriptInstance.class, orderScriptCode);
		}
		try {
			CommercialOrder commercialOrder = commercialOrderService.validateOrder(order, orderCompleted);
			return new CommercialOrderDto(commercialOrder);
		}catch(BusinessException e) {
			throw new BusinessApiException(e.getMessage());
		}
	}
	
	private Seller getSelectedSeller(CommercialOrder order) {
    	Seller seller = null;
        if(order.getSeller()!=null) {
        	seller = order.getSeller();
        }
        else if(order.getQuote()!=null) {
        	if( order.getQuote().getSeller()!=null)
        		seller = order.getQuote().getSeller();
        }else {
        	seller = order.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }
        return seller;
    }
	
	private void processOrderLot(CommercialOrderDto postData, CommercialOrder commercialOrder) {
		Set<String> orderLots = postData.getOrderLotCodes(); 
		List<OrderLot> orderLotList=new ArrayList<>();
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
		OrderOffer orderOffer = new OrderOffer();
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
		
		if(!CommercialOrderEnum.DRAFT.toString().equals(commercialOrder.getStatus())) {
            throw new MeveoApiException("Cannot add offers to order with status : " + commercialOrder.getStatus());
        }

		OfferTemplate offerTemplate = offerTemplateService.findByCode(orderOfferDto.getOfferTemplateCode());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, orderOfferDto.getOfferTemplateCode());
		}
		UserAccount userAccount=null;
		if(!StringUtils.isBlank(orderOfferDto.getUserAccountCode())) {
			userAccount = userAccountService.findByCode(orderOfferDto.getUserAccountCode());
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, orderOfferDto.getUserAccountCode());
			}
	        if(!userAccount.getIsConsumer()) {
	            throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Order for this user account is not allowed.");
	        }
		} else {
			userAccount = commercialOrder.getUserAccount();
		}
		
		if (!StringUtils.isBlank(orderOfferDto.getContractCode())) {
			orderOffer.setContract(contractHierarchyHelper.checkContractHierarchy(userAccount.getBillingAccount(), orderOfferDto.getContractCode()));
		}

		orderOffer.setUserAccount(userAccount);
		DiscountPlan discountPlan=null;
		if(!StringUtils.isBlank(orderOfferDto.getDiscountPlanCode())) {
		 discountPlan = discountPlanService.findByCode(orderOfferDto.getDiscountPlanCode());	
		if (discountPlan == null)
			throw new EntityDoesNotExistsException(DiscountPlan.class, orderOfferDto.getDiscountPlanCode());	
		}
		
		
		orderOffer.setOrder(commercialOrder);
		orderOffer.setOfferTemplate(offerTemplate);
		orderOffer.setDiscountPlan(discountPlan);
		
    	if(orderOfferDto.getDeliveryDate()!=null && orderOfferDto.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
        orderOffer.setDeliveryDate(orderOfferDto.getDeliveryDate());
        if(orderOfferDto.getOrderLineType() == OfferLineTypeEnum.AMEND) {
        	if (orderOfferDto.getSubscriptionCode() == null) {
				throw new BusinessApiException("Subscription is missing");
			}
        	List<OrderOffer> orderOffers = orderOfferService.findBySubscriptionAndStatus(orderOfferDto.getSubscriptionCode(), OfferLineTypeEnum.AMEND);
        	if(!orderOffers.isEmpty()) {
        		throw new BusinessApiException(format("Amendment order line already exists on subscription %s",orderOfferDto.getSubscriptionCode()));
        	}
        	orderOffer.setOrderLineType(OfferLineTypeEnum.AMEND);
        	
        	Subscription subscription = subscriptionService.findByCode(orderOfferDto.getSubscriptionCode());
        	if(subscription == null) {
        		throw new EntityDoesNotExistsException("Subscription with code "+orderOfferDto.getSubscriptionCode()+" does not exist");
        	}
        	orderOffer.setSubscription(subscription);
        }else if(orderOfferDto.getOrderLineType() == OfferLineTypeEnum.TERMINATE) {
        	if (orderOfferDto.getSubscriptionCode() == null) {
				throw new BusinessApiException("Subscription is missing");
			}
        	orderOffer.setOrderLineType(OfferLineTypeEnum.TERMINATE);
        	
        	Subscription subscription = subscriptionService.findByCode(orderOfferDto.getSubscriptionCode());
        	if(subscription == null) {
        		throw new EntityDoesNotExistsException("Subscription with code "+orderOfferDto.getSubscriptionCode()+" does not exist");
        	}
        	Date terminationDateTime = DateUtils.setDateToEndOfDay(orderOfferDto.getTerminationDate());
        	if(orderOfferDto.getTerminationDate()!=null && terminationDateTime.before(subscription.getSubscriptionDate())) {
        		throw new MeveoApiException("The termination date must not be before the subscription date");	
        	}
        	if(orderOfferDto.getTerminationDate()!=null && terminationDateTime.compareTo(new Date()) < 0) {
        		throw new MeveoApiException("The termination date must not be in the past");	
        	}
        	orderOffer.setSubscription(subscription);
        	SubscriptionTerminationReason terminationReason = null;
        	if(!StringUtils.isBlank(orderOfferDto.getTerminationReasonCode())) {
    			terminationReason = terminationReasonService.findByCode(orderOfferDto.getTerminationReasonCode());
    			if (terminationReason == null)
    				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, orderOfferDto.getTerminationReasonCode());	
    		}
        	orderOffer.setTerminationReason(terminationReason);
    		orderOffer.setTerminationDate(orderOfferDto.getTerminationDate());
        }else if(orderOfferDto.getOrderLineType() == OfferLineTypeEnum.APPLY_ONE_SHOT) {
        	for (OrderProductDto orderProductDto : orderOfferDto.getOrderProducts()) { 
        		if(!StringUtils.isBlank(orderProductDto.getProductCode()) && !StringUtils.isBlank(orderProductDto.getProductVersion())) {
        			ProductVersion productVersion = productVersionService.findByProductAndVersion(orderProductDto.getProductCode(), orderProductDto.getProductVersion());
        			if(productVersion == null) {
        				throw new EntityDoesNotExistsException(ProductVersion.class, orderProductDto.getProductCode() +","+ orderProductDto.getProductVersion());
        			}
    				boolean haveOneShotChargeOther = false;
        			for(ProductChargeTemplateMapping charge : productVersion.getProduct().getProductCharges()) {
        				if(charge.getChargeTemplate() != null) {
        					ChargeTemplate templateCharge = (ChargeTemplate) PersistenceUtils.initializeAndUnproxy(charge.getChargeTemplate());
        					if(templateCharge instanceof OneShotChargeTemplate) {
        						OneShotChargeTemplate oneShotCharge = (OneShotChargeTemplate) templateCharge;
        						if(oneShotCharge.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER) {
        							haveOneShotChargeOther = true;
        							break;
        						}
        					}
        				}
        			}
        			if (!haveOneShotChargeOther) {
            			throw new MeveoApiException(resourceMessages.getString("order.line.type.one.shot.other.error", orderProductDto.getProductCode()));
        			}
        			
        		}

			}
        	orderOffer.setOrderLineType(OfferLineTypeEnum.APPLY_ONE_SHOT);
        	Subscription subscription = subscriptionService.findByCode(orderOfferDto.getSubscriptionCode());
        	if(subscription == null) {
        		throw new EntityDoesNotExistsException("Subscription with code "+orderOfferDto.getSubscriptionCode()+" does not exist");
        	}
        	orderOffer.setSubscription(subscription);
        }else {
        	orderOffer.setOrderLineType(OfferLineTypeEnum.CREATE);
        }
        if (orderOfferDto.getCustomFields() != null) {
        	populateCustomFields(orderOfferDto.getCustomFields(), orderOffer, true);
        }
		orderOfferService.create(orderOffer);
		orderOfferDto.setOrderOfferId(orderOffer.getId());
		createOrderProduct(orderOfferDto.getOrderProducts(),orderOffer);
		Optional.ofNullable(orderOffer.getProducts()).orElse(Collections.emptyList())
				.forEach(orderProduct -> attributeService.validateAttributes(
						orderOffer.getProducts().get(0).getProductVersion().getAttributes(),
						orderProduct.getOrderAttributes()));
		createOrderAttribute(orderOfferDto.getOrderAttributes(),null,orderOffer);
		return orderOfferDto;
	}
	
	public OrderOfferDto updateOrderOffer(OrderOfferDto orderOfferDto) throws MeveoApiException, BusinessException { 
    	if (orderOfferDto.getOrderOfferId()==null) {
    		missingParameters.add("orderOfferId");
    	}
    	handleMissingParameters();
    	OrderOffer orderOffer = orderOfferService.findById(orderOfferDto.getOrderOfferId());
    	if (orderOffer == null) {
    		throw new EntityDoesNotExistsException(OrderOffer.class, orderOfferDto.getOrderOfferId());
    	}
    	
    	if(orderOffer.getOrder() != null && CommercialOrderEnum.VALIDATED.toString().equalsIgnoreCase(orderOffer.getOrder().getStatus())) {
    		throw new BusinessApiException("A validated order cannot be update");
    	}
    	
		if (orderOfferDto.getCommercialOrderId() != null) {
			CommercialOrder commercialOrder=null;
			commercialOrder = commercialOrderService.findById(orderOfferDto.getCommercialOrderId());
			if (commercialOrder == null) {
				throw new EntityDoesNotExistsException(CommercialOrder.class, orderOfferDto.getCommercialOrderId());
			}
			orderOffer.setOrder(commercialOrder);
		}
    	
    	if(!StringUtils.isBlank(orderOfferDto.getOfferTemplateCode())) {
    		OfferTemplate offerTemplate=null;
    		 offerTemplate = offerTemplateService.findByCode(orderOfferDto.getOfferTemplateCode());
        	if (offerTemplate == null) {
        		throw new EntityDoesNotExistsException(OfferTemplate.class, orderOfferDto.getOfferTemplateCode());
        	}	
        	orderOffer.setOfferTemplate(offerTemplate);
    	}

		DiscountPlan discountPlan=null;
		if (!StringUtils.isBlank(orderOfferDto.getDiscountPlanCode())) {
			discountPlan = discountPlanService.findByCode(orderOfferDto.getDiscountPlanCode());
			if (discountPlan == null) {
				throw new EntityDoesNotExistsException(DiscountPlan.class, orderOfferDto.getDiscountPlanCode());
			}
		}
		orderOffer.setDiscountPlan(discountPlan);

		if(!StringUtils.isBlank(orderOfferDto.getUserAccountCode())) {
			UserAccount userAccount = userAccountService.findByCode(orderOfferDto.getUserAccountCode());
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, orderOfferDto.getUserAccountCode());
			}
	        if(!userAccount.getIsConsumer()) {
	            throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Order for this user account is not allowed.");
	        }
	        orderOffer.setUserAccount(userAccount);
		} 
		
		if (!StringUtils.isBlank(orderOfferDto.getContractCode())) {
			orderOffer.setContract(contractHierarchyHelper.checkContractHierarchy(orderOffer.getUserAccount().getBillingAccount(), orderOfferDto.getContractCode()));
		}
    	
    	if(orderOfferDto.getDeliveryDate()!=null && orderOfferDto.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
    	orderOffer.setDeliveryDate(orderOfferDto.getDeliveryDate());
        orderOffer.setOrderLineType(orderOfferDto.getOrderLineType());
        
        if(orderOfferDto.getOrderLineType() == OfferLineTypeEnum.AMEND) {
        	if (orderOfferDto.getSubscriptionCode() == null) {
				throw new BusinessApiException("Subscription is missing");
			}
        	
        	Subscription subscription = subscriptionService.findByCode(orderOfferDto.getSubscriptionCode());
        	if(subscription == null) {
        		throw new EntityDoesNotExistsException("Subscription with code "+orderOfferDto.getSubscriptionCode()+" does not exist");
        	}
        	orderOffer.setSubscription(subscription);
        }
        
        if(orderOfferDto.getOrderLineType() == OfferLineTypeEnum.TERMINATE) {
        	if (orderOfferDto.getSubscriptionCode() == null) {
				throw new BusinessApiException("Subscription is missing");
			}
        	orderOffer.setOrderLineType(OfferLineTypeEnum.TERMINATE);
        	
        	Subscription subscription = subscriptionService.findByCode(orderOfferDto.getSubscriptionCode());
        	if(subscription == null) {
        		throw new EntityDoesNotExistsException("Subscription with code "+orderOfferDto.getSubscriptionCode()+" does not exist");
        	}
        	Date terminationDateTime = DateUtils.setDateToEndOfDay(orderOfferDto.getTerminationDate());
        	if(orderOfferDto.getTerminationDate()!=null && terminationDateTime.before(subscription.getSubscriptionDate())) {
        		throw new MeveoApiException("The termination date must not be before the subscription date");	
        	}
        	if(orderOfferDto.getTerminationDate()!=null && terminationDateTime.compareTo(new Date()) < 0) {
        		throw new MeveoApiException("The termination date must not be in the past");	
        	}
        	orderOffer.setSubscription(subscription);
        	SubscriptionTerminationReason terminationReason = null;
        	if(!StringUtils.isBlank(orderOfferDto.getTerminationReasonCode())) {
    			terminationReason = terminationReasonService.findByCode(orderOfferDto.getTerminationReasonCode());
    			if (terminationReason == null)
    				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, orderOfferDto.getTerminationReasonCode());	
    		}
        	orderOffer.setTerminationReason(terminationReason);
    		orderOffer.setTerminationDate(orderOfferDto.getTerminationDate());
        }
        
    	processOrderProductFromOffer(orderOfferDto, orderOffer);
		Optional.ofNullable(orderOffer.getProducts()).orElse(Collections.emptyList())
				.forEach(orderProduct -> attributeService.validateAttributes(
						orderOffer.getProducts().get(0).getProductVersion().getAttributes(),
						orderProduct.getOrderAttributes()));
        processOrderAttribute(orderOfferDto,  orderOffer);
        if (orderOfferDto.getCustomFields() != null) {
        	populateCustomFields(orderOfferDto.getCustomFields(), orderOffer, false);
        }
    	orderOfferService.update(orderOffer);
    	return orderOfferDto;
    }
	
	public void updateOrderProgress(Long commercialOrderId,Integer progressValue) throws MeveoApiException { 
		
		if (commercialOrderId==null) {
    		missingParameters.add("commercialOrderId");
    	}
		if (progressValue==null) {
    		missingParameters.add("progressValue");
    	}
		handleMissingParameters();
    	CommercialOrder commercialOrder=null;
    	if(commercialOrderId!=null) {
    	 commercialOrder = commercialOrderService.findById(commercialOrderId);
		 validateProducts(commercialOrder.getOffers());
    	if ( commercialOrder== null)
    		throw new EntityDoesNotExistsException(CommercialOrder.class, commercialOrderId);
    	} 
    	if(!CommercialOrderEnum.FINALIZED.toString().equals(commercialOrder.getStatus())) {
    		throw new MeveoApiException("Commercial order status should be FINALIZED");
    	}
    	if(commercialOrder.getOrderProgress()!=null && commercialOrder.getOrderProgress()>progressValue) {
    		throw new MeveoApiException("new progress value should be greater than orderProgress");
    	} 
    	commercialOrder.setOrderProgressTmp(commercialOrder.getOrderProgress());
    	commercialOrder.setOrderProgress(progressValue);
    	commercialOrderService.update(commercialOrder);
    	
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
			 orderAttribute = populateOrderAttribute(orderAttributeDTO, orderProduct, null, orderOffer);
			 orderAttributeService.create(orderAttribute);
		 }
		 if(orderProduct != null && orderAttribute.getOrderProduct() != null && orderProduct.getId() != orderAttribute.getOrderProduct().getId()) {
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

		OrderProduct orderProduct = orderProductDTO.getOrderProductId()!= null  ?
					orderProductService.findById(orderProductDTO.getOrderProductId()) : null;
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
		if(orderProduct==null) {
			orderProduct=new OrderProduct();
		}
		if(!StringUtils.isBlank(orderProductDto.getProductCode()) && !StringUtils.isBlank(orderProductDto.getProductVersion())) {
			ProductVersion productVersion =null;
			productVersion = productVersionService.findByProductAndVersion(orderProductDto.getProductCode(), orderProductDto.getProductVersion());
			if(productVersion == null) {
				throw new EntityDoesNotExistsException(ProductVersion.class, orderProductDto.getProductCode() +","+ orderProductDto.getProductVersion());
			}
			orderProduct.setProductVersion(productVersion);
		}

		DiscountPlan discountPlan=null;
		if(!StringUtils.isBlank(orderProductDto.getDiscountPlanCode())) {
		 discountPlan = discountPlanService.findByCode(orderProductDto.getDiscountPlanCode());
		if (discountPlan == null)
			throw new EntityDoesNotExistsException(DiscountPlan.class, orderProductDto.getDiscountPlanCode());
		}

		orderProduct.setOrder(commercialOrder);
		orderProduct.setOrderServiceCommercial(orderLot);
		orderProduct.setDiscountPlan(discountPlan);
		orderProduct.setOrderOffer(orderOffer); 
		orderProduct.setStatus(orderProductDto.getInstanceStatus());
		if(orderProductDto.getQuantity() == null) {
			throw new MeveoApiException("The quantity is required");
		}
		orderProduct.setQuantity(orderProductDto.getQuantity());
		
		if(orderProductDto.getActionType() != null) {
			orderProduct.setProductActionType(orderProductDto.getActionType());
		}
		
		SubscriptionTerminationReason terminationReason = null;
		if(!StringUtils.isBlank(orderProductDto.getTerminationReasonCode())) {
			terminationReason = terminationReasonService.findByCode(orderProductDto.getTerminationReasonCode());
			if (terminationReason == null)
				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, orderProductDto.getTerminationReasonCode());	
		}
		orderProduct.setTerminationReason(terminationReason);
		orderProduct.setTerminationDate(orderProductDto.getTerminationDate());
		
    	if(orderProductDto.getDeliveryDate()!=null && orderProductDto.getDeliveryDate().before(new Date()) &&
		ProductActionTypeEnum.CREATE.equals(orderProductDto.getActionType())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
    	orderProduct.setDeliveryDate(orderProductDto.getDeliveryDate());

		if (orderProductDto.getServiceInstanceId() != null) {
			ServiceInstance serviceInstance = serviceInstanceService.findById(orderProductDto.getServiceInstanceId());
			if (serviceInstance == null) {
				throw new EntityDoesNotExistsException(ServiceInstance.class, orderProductDto.getServiceInstanceId());
			}
			orderProduct.setServiceInstance(serviceInstance);
		}

		orderProduct.updateAudit(currentUser);
		return orderProduct;
    }
    
	private void createOrderProduct(List<OrderProductDto> orderProductDtos, OrderOffer orderOffer) {
	    if(CollectionUtils.isEmpty(orderProductDtos)) { 
	        missingParameters.add("orderProducts");
	        handleMissingParameters();
	    }
		for (OrderProductDto orderProductDto : orderProductDtos) {  
		    if(orderProductDto.getQuantity() == null || orderProductDto.getQuantity().equals(BigDecimal.ZERO) )
		        throw new BusinessApiException("The quantity for product code " + orderProductDto.getProductCode() + " must be great than 0" );
			OrderProduct orderProduct=populateOrderProduct(orderProductDto,orderOffer,null);  
			orderProductService.create(orderProduct);
			//create order attributes linked to orderProduct
			createOrderAttribute(orderProductDto.getOrderAttributes(), orderProduct,null);
			orderOffer.getProducts().add(orderProduct); 
		}
	}
	
	
	private void createOrderAttribute(List<OrderAttributeDto> orderAttributeDtos, OrderProduct orderProduct,OrderOffer orderOffer) {
        if (orderAttributeDtos != null && !orderAttributeDtos.isEmpty()) {
        	if(orderProduct!=null) {
        		orderProduct.getOrderAttributes().clear(); 
        		}
        	
            orderAttributeDtos.stream()
            		.map(orderAttributeDTO -> populateOrderAttribute(orderAttributeDTO, orderProduct, orderProduct!=null? orderProduct.getProductVersion().getAttributes():null,orderOffer))
                    .collect(Collectors.toList())
                    .forEach(orderAttribute -> orderAttributeService.create(orderAttribute));
        }
    }
	
	private OrderAttribute 	populateOrderAttribute(OrderAttributeDto orderAttributeDTO, OrderProduct orderProduct, Set<ProductVersionAttribute> productVersionAttributes, OrderOffer orderOffer) {
        if (Strings.isEmpty( orderAttributeDTO.getOrderAttributeCode())) {
            missingParameters.add("orderAttributeCode");
            handleMissingParameters();
        }
        Attribute attribute = null;
        if(!StringUtils.isBlank(orderAttributeDTO.getOrderAttributeCode())) {
	        attribute = attributeService.findByCode(orderAttributeDTO.getOrderAttributeCode());
	        if (attribute == null) {
	            throw new EntityDoesNotExistsException(Attribute.class, orderAttributeDTO.getOrderAttributeCode());
	        }
        }
        if (productVersionAttributes != null) {
            List<Attribute> productAttributes = productVersionAttributes.stream().map(ProductVersionAttribute::getAttribute).collect(Collectors.toList());
            if(productAttributes != null && !productAttributes.contains(attribute) && orderProduct!=null){
                throw new BusinessApiException(format("Product version (code: %s, version: %d), doesn't contain attribute code: %s", orderProduct.getProductVersion().getProduct().getCode() , orderProduct.getProductVersion().getCurrentVersion(), attribute.getCode()));
            }
        }
        
        CommercialOrder commercialOrder = null;
    	if(!StringUtils.isBlank(orderAttributeDTO.getCommercialOrderId())) {
            commercialOrder = commercialOrderService.findById(orderAttributeDTO.getCommercialOrderId());
    		if (commercialOrder == null) {
    			throw new EntityDoesNotExistsException(CommercialOrder.class, orderAttributeDTO.getCommercialOrderId());
    		}
    	}
		OrderLot orderLot = null;
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
		orderAttributeDTO.setAttributeType(attribute.getAttributeType());
        orderAttribute.updateAudit(currentUser);
        if(orderProduct != null) {
            orderProduct.getOrderAttributes().add(orderAttribute);
            orderAttribute.setOrderProduct(orderProduct);
        }
        if(orderOffer != null) {
            orderAttribute.setOrderOffer(orderOffer);
        }
        if(!orderAttributeDTO.getLinkedOrderAttribute().isEmpty()) {
            List<OrderAttribute> linkedOrderAttributes = orderAttributeDTO.getLinkedOrderAttribute()
                    .stream()
                    .map(dto -> {
                    	OrderAttribute linkedAttribute = populateOrderAttribute(dto, orderProduct, productVersionAttributes,orderOffer);
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
    	if (orderOffer.getOrder() != null && orderOffer.getOrder().getId() != null) {
            CommercialOrder commercialOrder = commercialOrderService.findById(orderOffer.getOrder().getId());
            if (commercialOrder != null && !CommercialOrderEnum.DRAFT.toString().equals(commercialOrder.getStatus())) {
                throw new MeveoApiException("Cannot delete offers associated to an order in status : " + commercialOrder.getStatus());
            } 
        }
		if(orderOffer.getProducts() != null) {
			orderOffer.getProducts()
					.stream()
					.filter(orderProduct -> orderProduct.getOrderArticleLines() != null)
					.forEach(orderProduct -> {
						orderProduct.getOrderArticleLines().forEach(a -> a.setOrderProduct(null));
						orderProduct.getOrderArticleLines().clear();
					});
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
