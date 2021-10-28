/**
 *
 */
package org.meveo.apiv2.catalog.service;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

@Stateless
public class PricePlanApiService {

	@Inject
	private PricePlanMatrixVersionService pricePlanMatrixVersionService;
	
	public Map<String, List<Long>> checkIfUsed(String pricePlanMatrixCode, int pricePlanMatrixVersion) {
		return pricePlanMatrixVersionService.getUsedEntities(pricePlanMatrixCode, pricePlanMatrixVersion);
	}
}