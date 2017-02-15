package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanItemService extends PersistenceService<DiscountPlanItem> {

	public DiscountPlanItem findByCode(String code){
		QueryBuilder qb=new QueryBuilder(DiscountPlanItem.class,"d");
		qb.addCriterion("d.code", "=", code, true);
		try {
			return (DiscountPlanItem) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
