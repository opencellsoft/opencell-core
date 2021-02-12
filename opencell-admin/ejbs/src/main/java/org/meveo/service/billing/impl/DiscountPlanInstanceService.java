/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.billing.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.IDiscountable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
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

	public DiscountPlanInstance findBySubscriptionAndCode(Subscription subscription, String code) {
		QueryBuilder qb = new QueryBuilder(DiscountPlanInstance.class, "dpi");
		qb.addCriterionEntity("subscription", subscription);
		qb.addCriterion("discountPlan.code", "=", code, true);
		return (DiscountPlanInstance) qb.getQuery(getEntityManager()).getSingleResult();
	}

	/**
	 * Computes the Discount's effective end date. Exclusive of the endDate. If
	 * startDate is not null and endDate is null, endDate is computed from the given
	 * duration.
	 * 
	 * @param startDate starting date of this discount's effectivity
	 * @param endDate ending date of this discount's effectivity
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


	public IDiscountable instantiateDiscountPlan(IDiscountable entity, DiscountPlan dp, List<DiscountPlanInstance> toAdd) throws BusinessException {
		if (!dp.getStatus().equals(DiscountPlanStatusEnum.IN_USE) || !dp.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
			throw new BusinessException("only ACTIVE and IN_USE discount plans can be used instantiated");
		}
		if (entity.getAllDiscountPlanInstances() == null || entity.getAllDiscountPlanInstances().isEmpty()) {
			// add
			DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
			discountPlanInstance.assignEntityToDiscountPlanInstances(entity);
			discountPlanInstance.setDiscountPlan(dp);
			discountPlanInstance.copyEffectivityDates(dp);
			discountPlanInstance.setCfValues(dp.getCfValues());
			this.create(discountPlanInstance, dp);
			entity.addDiscountPlanInstances(discountPlanInstance);

		} else {
			boolean found = false;
			DiscountPlanInstance dpiMatched = null;
			for (DiscountPlanInstance dpi : entity.getAllDiscountPlanInstances()) {
				dpi.setCfValues(dp.getCfValues());
				if (dp.equals(dpi.getDiscountPlan())) {
					found = true;
					dpiMatched = dpi;
					break;
				}
			}

			if (found && dpiMatched != null) {
				// update effectivity dates
				dpiMatched.copyEffectivityDates(dp);
				this.update(dpiMatched, dp);

			} else {
				// add
				DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
				discountPlanInstance.assignEntityToDiscountPlanInstances(entity);
				discountPlanInstance.setDiscountPlan(dp);
				discountPlanInstance.copyEffectivityDates(dp);
				this.create(discountPlanInstance, dp);
				if (toAdd != null) {
					toAdd.add(discountPlanInstance);
				} else {
					entity.getAllDiscountPlanInstances().add(discountPlanInstance);
				}
			}
		}

		return entity;
	}

	public void terminateDiscountPlan(IDiscountable entity, DiscountPlanInstance dpi) throws BusinessException {
		this.remove(dpi);
		entity.getAllDiscountPlanInstances().remove(dpi);
	}


}
