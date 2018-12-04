package org.meveo.service.billing.impl;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;
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

	/**
	 * Computes the Discount's effective end date. Exclusive of the endDate. If
	 * startDate is not null and endDate is null, endDate is computed from the given
	 * duration.
	 * 
	 * @param date
	 *            the given date
	 * @return returns true if this DiscountItem is to be applied
	 */
	public Date computeEndDate(Date startDate, Date endDate, Integer defaultDuration,
			DurationPeriodUnitEnum durationUnit) {
		Date computedEndDate = endDate;

		if (startDate != null && endDate == null && defaultDuration != null && durationUnit != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(durationUnit.getCalendarField(), defaultDuration);
			computedEndDate = cal.getTime();
		}

		return computedEndDate;
	}

	/**
	 * Create an instance of this entity. Computing the endDate first.
	 * 
	 * @param dp
	 *            DiscountPlan
	 */
	public void create(DiscountPlanInstance entity, DiscountPlan dp) throws BusinessException {
		entity.setEndDate(computeEndDate(entity.getStartDate(), entity.getEndDate(), dp.getDefaultDuration(),
				dp.getDurationUnit()));
		super.create(entity);
	}

	/**
	 * Update this entity. Computing the endDate first.
	 * 
	 * @param dp
	 *            DiscountPlan
	 */
	public DiscountPlanInstance update(DiscountPlanInstance entity, DiscountPlan dp) throws BusinessException {
		entity.setEndDate(computeEndDate(entity.getStartDate(), entity.getEndDate(), dp.getDefaultDuration(),
				dp.getDurationUnit()));
		return super.update(entity);
	}

}
