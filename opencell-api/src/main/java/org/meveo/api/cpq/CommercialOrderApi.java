package org.meveo.api.cpq;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.BillingPlan;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.order.BillingPlanService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderTypeService;
import org.meveo.service.order.OrderService;


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
	@Inject private BillingPlanService billingPlanService;
	@Inject private OrderService orderService;
	
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
			final BillingPlan billingPlan = billingPlanService.findByCode(orderDto.getInvoicingPlanCode());
			if(billingPlan == null)
				throw new EntityDoesNotExistsException(BillingPlan.class, orderDto.getInvoicingPlanCode());
			order.setBillingAccount(billingAccount);
		}
		order.setStatus(OrderStatusEnum.IN_CREATION);
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
		commercialOrderService.create(order);
		return new CommercialOrderDto(order);
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
}
