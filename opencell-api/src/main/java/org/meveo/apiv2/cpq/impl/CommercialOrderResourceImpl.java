package org.meveo.apiv2.cpq.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.cpq.resource.CommercialOrderResource;
import org.meveo.apiv2.cpq.service.CommercialOrderApiService;
import org.meveo.apiv2.ordering.resource.oo.ImmutableAvailableOpenOrder;

@Interceptors({ WsRestApiInterceptor.class })
public class CommercialOrderResourceImpl implements CommercialOrderResource {
	
	@Inject
	private CommercialOrderApiService commercialOrderApiService;

	@Transactional
	@Override
	public Response findAvailableOpenOrders(String quoteCode) {

		List<ImmutableAvailableOpenOrder> result = commercialOrderApiService.findAvailableOpenOrders(quoteCode);

		Map<String, Object> response = new HashMap<>();
		response.put("availableOpenOrders", result);

		return Response.ok(response).build();
	}

}
