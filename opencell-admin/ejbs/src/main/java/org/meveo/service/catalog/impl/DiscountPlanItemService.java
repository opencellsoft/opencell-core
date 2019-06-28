package org.meveo.service.catalog.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanItemService extends PersistenceService<DiscountPlanItem> {

	@EJB
	private DiscountPlanService discountPlanService;
	
    public DiscountPlanItem findByCode(String code) {
        QueryBuilder qb = new QueryBuilder(DiscountPlanItem.class, "d");
        qb.addCriterion("d.code", "=", code, true);
        try {
            return (DiscountPlanItem) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

	@Override
	public void create(DiscountPlanItem dpi) throws BusinessException {
		dpi.setDiscountPlan(discountPlanService.findById(dpi.getDiscountPlan().getId()));
		super.create(dpi);
		// Needed to refresh DiscountPlan as DiscountPlan.discountPlanItems field as it
		// is cached
		// refresh(dpi.getDiscountPlan());
	}

	@Override
	public DiscountPlanItem update(DiscountPlanItem dpi) throws BusinessException {
		dpi.setDiscountPlan(discountPlanService.findById(dpi.getDiscountPlan().getId()));
		dpi = super.update(dpi);
		// Needed to refresh DiscountPlan as DiscountPlan.discountPlanItems field as it
		// is cached
		// refresh(dpi.getDiscountPlan());
		return dpi;
	}

    @Override
    public void remove(DiscountPlanItem dpi) throws BusinessException {
        super.remove(dpi);
        // Needed to remove from DiscountPlan.discountPlanItems field as it is cached
        dpi.getDiscountPlan().getDiscountPlanItems().remove(dpi);
    }
}