package org.meveo.apiv2.cpq.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.cpq.resource.CpqQuoteResource;
import org.meveo.apiv2.cpq.service.CpqQuoteApiService;
import org.meveo.apiv2.ordering.resource.oo.ImmutableAvailableOpenOrder;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.OpenOrder;

public class CpqQuoteResourceImpl implements CpqQuoteResource {
	
	@Inject
	private CpqQuoteApiService cpqQuoteApiService;

	@Transactional
	@Override
	public Response findAvailableOpenOrders(String quoteCode) {
		
		Set<OpenOrder> openOrders = cpqQuoteApiService.findAvailableOpenOrders(quoteCode);
		
		List<ImmutableAvailableOpenOrder> result = openOrders.stream()
					.map(oo -> ImmutableAvailableOpenOrder.builder()
													.openOrderNumber(oo.getOpenOrderNumber())
													.startDate(oo.getActivationDate())
													.externalReference(oo.getExternalReference())
													.addAllProducts(oo.getProducts().stream().map(Product::getId).collect(Collectors.toList()))
													.addAllArticles(oo.getArticles().stream().map(AccountingArticle::getId).collect(Collectors.toList()))
													.build())
					.collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();
		response.put("availableOpenOrders", result);
		
		return Response.ok(response).build();
	}

}
