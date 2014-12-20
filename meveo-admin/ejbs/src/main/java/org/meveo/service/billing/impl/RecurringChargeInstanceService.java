/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.RecurringChargeTemplate;

@Stateless
public class RecurringChargeInstanceService extends
		ChargeInstanceService<RecurringChargeInstance> {

	@Inject
	private WalletOperationService chargeApplicationService;

	// @Inject
	// private RecurringChargeTemplateServiceLocal
	// recurringChargeTemplateService;

	@SuppressWarnings("unchecked")
	public List<RecurringChargeInstance> findByStatus(
			InstanceStatusEnum status, Date maxChargeDate) {
		List<RecurringChargeInstance> recurringChargeInstances = new ArrayList<RecurringChargeInstance>();
		try {
			log.debug("start of find #0 by status (status={})) ..",
					"RecurringChargeInstance", status);
			QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class,
					"c");
			qb.addCriterion("c.status", "=", status, true);
			qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate",
					maxChargeDate);
			recurringChargeInstances = qb.getQuery(getEntityManager())
					.getResultList();
			log.debug(
					"end of find {} by status (status={}). Result size found={}.",
					new Object[] {
							"RecurringChargeInstance",
							status,
							recurringChargeInstances != null ? recurringChargeInstances
									.size() : 0 });

		} catch (Exception e) {
			log.error("findByStatus error={} ", e.getMessage());
		}
		return recurringChargeInstances;
	}

	public Long recurringChargeApplication(Subscription subscription,
			RecurringChargeTemplate chargetemplate, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			Integer quantity, String criteria1, String criteria2,
			String criteria3, User creator) throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}
		RecurringChargeInstance recurringChargeInstance = new RecurringChargeInstance(
				chargetemplate.getCode(), chargetemplate.getDescription(),
				effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargetemplate, null);
		recurringChargeInstance.setCriteria1(criteria1);
		recurringChargeInstance.setCriteria2(criteria2);
		recurringChargeInstance.setCriteria3(criteria3);
		recurringChargeInstance.setCountry(subscription.getUserAccount()
				.getBillingAccount().getTradingCountry());
		recurringChargeInstance.setCurrency(subscription.getUserAccount()
				.getBillingAccount().getCustomerAccount().getTradingCurrency());

		create(recurringChargeInstance, creator, chargetemplate.getProvider());

		chargeApplicationService.recurringWalletOperation(subscription,
				recurringChargeInstance, quantity, effetDate, creator);
		return recurringChargeInstance.getId();
	}

	public void recurringChargeApplication(
			RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		recurringChargeApplication(getEntityManager(), chargeInstance, creator);
	}

	public void recurringChargeApplication(EntityManager em,
			RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		chargeApplicationService
				.chargeSubscription(em, chargeInstance, creator);
	}

	@SuppressWarnings("unchecked")
	public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(
			Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

}
