package org.meveo.apiv2.catalog.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.catalog.service.PricePlanApiService;

@Stateless
public class PricePlanResourceImpl implements PricePlanResource {

    @Inject
    private PricePlanApiService pricePlanApiService;

	@Override
	public Response getDiscountPlanItem(String pricePlanMatrixCode, int pricePlanMatrixVersion) {
		return Response.ok().entity(pricePlanApiService.checkIfUsed(pricePlanMatrixCode, pricePlanMatrixVersion)).build();
	}

}
