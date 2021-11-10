package org.meveo.apiv2.catalog.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.api.catalog.PricePlanMatrixLineApi;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.apiv2.catalog.service.PricePlanMatrixApiService;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

@Stateless
public class PricePlanMatrixResourceImpl implements PricePlanMatrixResource {
	
	@Inject
	PricePlanMatrixColumnService pricePlanMatrixColumnService;
	
	@Inject
	PricePlanMatrixVersionService pricePlanMatrixVersionService;
	
	@Inject
    private PricePlanMatrixApiService pricePlanMatrixApiService;
	
	@Inject
    private PricePlanMatrixLineApi pricePlanMatrixLineApi;
	
	

	@Override
	public Response importPricePlanMatrixLines(PricePlanMLinesDTO pricePlanMLinesDTO) {
		String data = new String(pricePlanMLinesDTO.getData());
		PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMLinesDTO.getPricePlanMatrixCode(), pricePlanMLinesDTO.getPricePlanMatrixVersion());
		if (pricePlanMatrixVersion == null) {
			throw new NotFoundException("pricePlanMatrixVersion not found with code"+pricePlanMLinesDTO.getPricePlanMatrixCode()+"and version"+pricePlanMLinesDTO.getPricePlanMatrixVersion());
		}
		PricePlanMatrixLinesDto pricePlanMatrixLinesDto = pricePlanMatrixApiService.updatePricePlanMatrixLines(pricePlanMLinesDTO, data, pricePlanMatrixVersion);
		pricePlanMatrixLineApi.updatePricePlanMatrixLines(pricePlanMLinesDTO.getPricePlanMatrixCode(), pricePlanMLinesDTO.getPricePlanMatrixVersion(), pricePlanMatrixLinesDto);
		
		return Response.ok().build();
	}


}