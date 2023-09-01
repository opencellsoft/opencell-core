package org.meveo.apiv2.catalog.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.catalog.service.PricePlanApiService;

@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class PricePlanResourceImpl implements PricePlanResource {

    @Inject
    private PricePlanApiService pricePlanApiService;

	@Override
	public Response getDiscountPlanItem(String pricePlanMatrixCode, int pricePlanMatrixVersion) {
		return Response.ok().entity(pricePlanApiService.checkIfUsed(pricePlanMatrixCode, pricePlanMatrixVersion)).build();
	}

}
