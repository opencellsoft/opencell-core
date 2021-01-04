package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.CommercialOrderApi;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.response.cpq.GetCommercialOrderDtoResponse;
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

}
