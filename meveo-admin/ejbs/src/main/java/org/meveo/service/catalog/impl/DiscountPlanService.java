package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanService extends PersistenceService<DiscountPlan> {
	public DiscountPlan findByCode(String code, Provider provider) {
		return findByCode(getEntityManager(), code, provider);
	}
	
	public DiscountPlan findByCode(EntityManager em, String code, Provider provider) {
		QueryBuilder qb = new QueryBuilder(DiscountPlan.class, "t");
		qb.addCriterion("t.code", "=", code, false);
		qb.addCriterionEntity("t.provider", provider);
		
		try {
			return (DiscountPlan) qb.getQuery(em).getSingleResult();
		} catch (NoResultException ne ) {
			return null;
		}catch (NonUniqueResultException nre){
			return null;
		}
	}
}
