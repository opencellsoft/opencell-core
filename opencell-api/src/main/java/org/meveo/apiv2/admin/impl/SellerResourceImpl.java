package org.meveo.apiv2.admin.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
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
public class SellerResourceImpl implements SellerResource {

	@Inject
	private SellerApiService sellerApiService;
	
	private final SellerMapper sellerMapper = new SellerMapper();
	
	@Override
	public Response create(Seller postData) {
		var seller = sellerMapper.toEntity(postData);
		sellerApiService.create(seller);
		Link link = new org.meveo.apiv2.generic.common.LinkGenerator.SelfLinkGenerator(SellerResource.class).withPutAction().withPostAction().withId(seller.getId()).build();
		var response = ImmutableSeller.copyOf(sellerMapper.toResource(seller));
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningSettingResource.class, seller.getId()).build())
				.entity(response)
				.build();
	}

	@Override
	public Response update(Seller postData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response createOrUpdate(Seller postData) {
		// TODO Auto-generated method stub
		return null;
	}
}
