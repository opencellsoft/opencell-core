package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanService extends PersistenceService<DiscountPlan> {
    
    public DiscountPlan findByCode(String code) {

        QueryBuilder qb = new QueryBuilder(DiscountPlan.class, "t");
        qb.addCriterion("t.code", "=", code, false);

        try {
            return (DiscountPlan) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        } catch (NonUniqueResultException nre) {
            return null;
        }
    }
}
