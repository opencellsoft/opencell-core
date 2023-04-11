package org.meveo.service.catalog.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.service.base.PersistenceService;


public class PricePlanMatrixValueService extends PersistenceService<PricePlanMatrixValue> {

	@SuppressWarnings("unchecked")
	public Set<PricePlanMatrixValue> findByPricePlanMatrixColumn(Long matrixColumnId) {
		QueryBuilder builder = new QueryBuilder(PricePlanMatrixValue.class, "p", null);
		builder.addCriterion("p.pricePlanMatrixColumn.id", "=", matrixColumnId, false);
		return new HashSet<>(builder.getQuery(getEntityManager()).getResultList());
	}
}
