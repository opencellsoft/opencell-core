package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.DiscountPlanInstance;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@Stateless
public class DiscountPlanInstanceService extends PersistenceService<DiscountPlanInstance> {

	public DiscountPlanInstance findByBillingAccountAndCode(BillingAccount ba, String code) {
		QueryBuilder qb = new QueryBuilder(DiscountPlanInstance.class, "dpi");
		qb.addCriterionEntity("billingAccount", ba);
		qb.addCriterion("discountPlan.code", "=", code, true);

		return (DiscountPlanInstance) qb.getQuery(getEntityManager()).getSingleResult();
	}
	
}
