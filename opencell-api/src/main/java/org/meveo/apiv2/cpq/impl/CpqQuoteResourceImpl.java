package org.meveo.apiv2.cpq.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.cpq.resource.CpqQuoteResource;
import org.meveo.apiv2.cpq.service.CpqQuoteApiService;
import org.meveo.apiv2.ordering.resource.oo.ImmutableAvailableOpenOrder;

@Interceptors({ WsRestApiInterceptor.class })
public class CpqQuoteResourceImpl implements CpqQuoteResource {
	
	@Inject
	private CpqQuoteApiService cpqQuoteApiService;

	@Transactional
	@Override
	public Response findAvailableOpenOrders(String quoteCode) {
		
		List<ImmutableAvailableOpenOrder> result = cpqQuoteApiService.findAvailableOpenOrders(quoteCode);
		
		Map<String, Object> response = new HashMap<>();
		response.put("availableOpenOrders", result);
		
		return Response.ok(response).build();
	}

}
