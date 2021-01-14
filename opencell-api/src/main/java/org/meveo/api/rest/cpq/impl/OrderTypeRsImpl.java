package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.OrderTypeApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.order.OrderTypeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetListOrderTypeResponseDto;
import org.meveo.api.dto.response.cpq.GetOrderTypeDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.OrderTypeRs;
import org.meveo.api.rest.impl.BaseRs;

public class OrderTypeRsImpl extends BaseRs implements OrderTypeRs {

	@Inject private OrderTypeApi orderTypeApi;
	
	@Override
	public Response create(OrderTypeDto orderTypeDto) {
		 GetOrderTypeDtoResponse result = new GetOrderTypeDtoResponse();
		 try {
			 result.setOrderTypeDto(orderTypeApi.create(orderTypeDto));
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response update(OrderTypeDto orderTypeDto) {
		 GetOrderTypeDtoResponse result = new GetOrderTypeDtoResponse();
		 try {
			 result.setOrderTypeDto(orderTypeApi.update(orderTypeDto));
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response delete(String orderTypeCode) {
		 ActionStatus result = new ActionStatus();
		 try {
			 orderTypeApi.delete(orderTypeCode);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response list(PagingAndFiltering pagingAndFiltering) {
		GetListOrderTypeResponseDto result = new GetListOrderTypeResponseDto();
		 try {
			 result = orderTypeApi.list(pagingAndFiltering);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response find(String orderTypeCode) {
		 GetOrderTypeDtoResponse result = new GetOrderTypeDtoResponse();
		 try {
			 result.setOrderTypeDto(orderTypeApi.findByCode(orderTypeCode));
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

}
