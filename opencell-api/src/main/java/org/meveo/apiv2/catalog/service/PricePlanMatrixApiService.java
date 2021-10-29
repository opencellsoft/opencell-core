
package org.meveo.apiv2.catalog.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.apiv2.catalog.resource.PricePlanMLinesDTO;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;

public class PricePlanMatrixApiService  implements ApiService<PricePlanMatrix> {
	
	
	@Inject
	PricePlanMatrixColumnService pricePlanMatrixColumnService;

	@Override
	public List<PricePlanMatrix> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<PricePlanMatrix> findById(Long id) {
		return Optional.empty();
	}

	@Override
	public PricePlanMatrix create(PricePlanMatrix baseEntity) {
		return null;
	}

	@Override
	public Optional<PricePlanMatrix> update(Long id, PricePlanMatrix baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<PricePlanMatrix> patch(Long id, PricePlanMatrix baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<PricePlanMatrix> delete(Long id) {
		return Optional.empty();
	}

	@Override
	public Optional<PricePlanMatrix> findByCode(String code) {
		return Optional.empty();
	}

	public PricePlanMatrixLinesDto updatePricePlanMatrixLines(PricePlanMLinesDTO pricePlanMLinesDTO, String data,
			PricePlanMatrixVersion pricePlanMatrixVersion) {
		return pricePlanMatrixColumnService.populateLinesAndValues(pricePlanMLinesDTO.getPricePlanMatrixCode(), data,
				pricePlanMatrixVersion);
		
	}

	
    

}