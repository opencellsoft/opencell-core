package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.CommercialOrderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialOrderDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialOrderDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.CommercialOrderRs;
import org.meveo.api.rest.impl.BaseRs;

public class CommercialOrderRsImpl extends BaseRs implements CommercialOrderRs {

	@Inject private CommercialOrderApi commercialOrderApi;
	
	@Override
	public Response create(CommercialOrderDto orderDto) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.create(orderDto));
			return Response.ok(result).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response updateUserAccount(Long commercialOrderId, String userAccountCode) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.updateUserAccount(commercialOrderId, userAccountCode));
			return Response.ok(result).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response updateOrderInvoicingPlan(Long commercialOrderId, String invoicingPlanCode) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.updateOrderInvoicingPlan(commercialOrderId, invoicingPlanCode));
			return Response.ok(result).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response update(CommercialOrderDto orderDto) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.update(orderDto));
			return Response.ok(result).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response delete(Long orderId) {
		ActionStatus status = new ActionStatus();
		try {
			commercialOrderApi.delete(orderId);;
			return Response.ok(status).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, status);
		}
	}

	@Override
	public Response updateStatus(Long orderId, String statusTarget) {
		ActionStatus status = new ActionStatus();
		try {
			commercialOrderApi.updateStatus(orderId, statusTarget);
			return Response.ok(status).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, status);
		}
	}

	@Override
	public Response duplicate(Long orderId) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.duplicate(orderId));
			return Response.ok(result).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response validate(Long orderId) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.validate(orderId));
			return Response.ok(result).build();
		}catch(MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response listCommercialOrder(PagingAndFiltering pagingAndFiltering) {
		GetListCommercialOrderDtoResponse result = new GetListCommercialOrderDtoResponse();

	        try {  
	    			result = commercialOrderApi.listCommercialOrder(pagingAndFiltering);
	    			
	    			return Response.ok(result).build(); 
	        	
	        } catch (MeveoApiException e) { 
	            return errorResponse(e, result.getActionStatus());
	        } 
	}

	@Override
	public Response findByOrderNumber(String orderNumber) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
	    try {
	    	result.setCommercialOrderDto(commercialOrderApi.findByOrderNumber(orderNumber));
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}

	@Override
	public Response orderValidationProcess(Long orderId) {
		GetCommercialOrderDtoResponse result = new GetCommercialOrderDtoResponse();
		try {
			result.setCommercialOrderDto(commercialOrderApi.orderValidationProcess(orderId));
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
			return errorResponse(e, result.getActionStatus());
		}
	}

}
