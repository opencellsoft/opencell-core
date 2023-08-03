package org.meveo.apiv2.catalog.resource.pricelist;

import static javax.ws.rs.core.Response.ok;

import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.catalog.service.PriceListApiService;
import org.meveo.apiv2.dunning.resource.DunningLevelResource;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListStatusEnum;

@Interceptors({ WsRestApiInterceptor.class })
public class PriceListResourceImpl implements PriceListResource {

    @Inject
    private PriceListApiService priceListApiService;
    private PriceListMapper mapper = new PriceListMapper();
    
    @Override
	public Response create(org.meveo.apiv2.catalog.PriceList priceList) {
    	PriceList entity = mapper.toEntity(priceList);
    	PriceList savedPriceList = priceListApiService.create(entity, priceList.getCustomFields());
		return Response.created(LinkGenerator.getUriBuilderFromResource(PriceListResource.class, savedPriceList.getId()).build())
				.entity(mapper.toResource(savedPriceList))
				.build();
	}
    
    @Override
	public Response update(org.meveo.apiv2.catalog.PriceList priceList, String priceListCode) {
    	PriceList entity = mapper.toEntity(priceList);
    	PriceList updated = priceListApiService.update(entity, priceListCode, priceList.getCustomFields()).get();
		return ok(LinkGenerator.getUriBuilderFromResource(DunningLevelResource.class, updated.getId()).build())
				.entity(mapper.toResource(updated))
				.build();
	}
    
    @Override
	public Response delete(String priceListCode) {
		ActionStatus actionStatus = new ActionStatus();
		actionStatus.setStatus(ActionStatusEnum.SUCCESS);
		priceListApiService.delete(priceListCode);
		return Response.ok(actionStatus).build();
	}

    @Override
    public Response updateStatus(String priceListCode, PriceListStatusEnum newStatus) {
        priceListApiService.updateStatus(priceListCode, newStatus);
        Map<String, String> lReturn = Map.of("status", "SUCCESS");
        return Response.ok(lReturn).build();
    }

	@Override
	public Response duplicate(String priceListCode) {
		PriceList savedPriceList = priceListApiService.duplicate(priceListCode);
		return ok(LinkGenerator.getUriBuilderFromResource(PriceListResource.class, savedPriceList.getId()).build())
				.entity(mapper.toResource(savedPriceList))
				.build();
	}
}