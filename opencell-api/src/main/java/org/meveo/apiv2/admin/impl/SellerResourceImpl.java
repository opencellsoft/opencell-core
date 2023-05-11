package org.meveo.apiv2.admin.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.apiv2.admin.Seller;
import org.meveo.apiv2.admin.SellerApiService;
import org.meveo.apiv2.admin.resource.SellerResource;
import org.meveo.service.admin.impl.SellerService;

public class SellerResourceImpl implements SellerResource {

	@Inject
	private SellerApiService sellerApiService;
	
	@Override
	public Response create(Seller postData) {
		
		return null;
	}

	@Override
	public ActionStatus update(Seller postData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus createOrUpdate(Seller postData) {
		// TODO Auto-generated method stub
		return null;
	}

}
