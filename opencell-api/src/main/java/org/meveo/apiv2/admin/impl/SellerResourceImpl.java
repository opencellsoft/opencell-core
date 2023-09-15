package org.meveo.apiv2.admin.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.admin.ImmutableSeller;
import org.meveo.apiv2.admin.Seller;
import org.meveo.apiv2.admin.SellerApiService;
import org.meveo.apiv2.admin.resource.SellerResource;
import org.meveo.apiv2.billing.resource.RatedTransactionResource;
import org.meveo.apiv2.catalog.resource.DiscountPlanResource;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.service.admin.impl.SellerService;

import java.net.URI;
import java.util.Collections;
@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class SellerResourceImpl implements SellerResource {

	@Inject
	private SellerApiService sellerApiService;
	
	private final SellerMapper sellerMapper = new SellerMapper();
	
	@Override
	public Response create(Seller postData) {
		var seller = sellerMapper.toEntity(postData);
		sellerApiService.populateCustomFieldsForGenericApi(postData.getCustomFields(), seller, true);
		sellerApiService.create(seller);
		var response = ImmutableSeller.copyOf(sellerMapper.toResource(seller)).withCustomFields(sellerApiService.getDto(seller));
		return Response.created(LinkGenerator.getUriBuilderFromResource(SellerResource.class, seller.getId()).build())
				.entity(response)
				.build();
	}

	@Override
	public Response update(Seller postData) {
		var sellerSeller = sellerMapper.toEntity(postData);
		sellerApiService.populateCustomFieldsForGenericApi(postData.getCustomFields(), sellerSeller, false);
		sellerApiService.update(sellerSeller);
		var response = ImmutableSeller.copyOf(sellerMapper.toResource(sellerSeller)).withCustomFields(sellerApiService.getDto(sellerSeller));
		return Response.ok(LinkGenerator.getUriBuilderFromResource(SellerResource.class, sellerSeller.getId()).build())
				.entity(response)
				.build();
	}

	@Override
	public Response createOrUpdate(Seller postData) {
		var sellerSeller = sellerMapper.toEntity(postData);
		var isNewEntity = postData.getId() == null;
		sellerApiService.populateCustomFieldsForGenericApi(postData.getCustomFields(), sellerSeller, isNewEntity);
		sellerApiService.createOrUpdate(sellerSeller);
		var response = ImmutableSeller.copyOf(sellerMapper.toResource(sellerSeller)).withCustomFields(sellerApiService.getDto(sellerSeller));
		return Response.ok(LinkGenerator.getUriBuilderFromResource(SellerResource.class, sellerSeller.getId()).build())
				.entity(response)
				.build();
	}
}
