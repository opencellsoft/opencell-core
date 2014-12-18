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
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.service.base.PersistenceService;

@Stateless
public class CounterInstanceService extends PersistenceService<CounterInstance> {

	@Inject
	UserAccountService userAccountService;

	@Inject
	CounterPeriodService counterPeriodService;

	public CounterInstance counterInstanciation(UserAccount userAccount,
			CounterTemplate counterTemplate, User creator)
			throws BusinessException {
		return counterInstanciation(getEntityManager(), userAccount,
				counterTemplate, creator);
	}

	public CounterInstance counterInstanciation(EntityManager em,
			UserAccount userAccount, CounterTemplate counterTemplate,
			User creator) throws BusinessException {
		CounterInstance result = null;

		if (userAccount == null) {
			throw new BusinessException("userAccount is null");
		}

		if (counterTemplate == null) {
			throw new BusinessException("counterTemplate is null");
		}

		if (creator == null) {
			throw new BusinessException("creator is null");
		}

		// we instanciate the counter only if there is no existing instance for
		// the same template
		if (!userAccount.getCounters().containsKey(counterTemplate.getCode())) {
			result = new CounterInstance();
			result.setCounterTemplate(counterTemplate);
			result.setUserAccount(userAccount);
			create(em, result, creator, userAccount.getProvider());

			userAccount.getCounters().put(counterTemplate.getCode(), result);
			userAccountService.setProvider(creator.getProvider());
			userAccountService.update(em, userAccount, creator);
		} else {
			result = userAccount.getCounters().get(counterTemplate.getCode());
		}

		return result;
	}

	public CounterPeriod createPeriod(CounterInstance counterInstance,
			Date chargeDate, EntityManager em, User currentUser) {
		CounterPeriod counterPeriod = new CounterPeriod();
		counterPeriod.setCounterInstance(counterInstance);
		Date startDate = counterInstance.getCounterTemplate().getCalendar()
				.previousCalendarDate(chargeDate);
		Date endDate = counterInstance.getCounterTemplate().getCalendar()
				.nextCalendarDate(startDate);
		log.info("create counter period from " + startDate + " to " + endDate);

		counterPeriod.setPeriodStartDate(startDate);
		counterPeriod.setPeriodEndDate(endDate);
		counterPeriod.setProvider(counterInstance.getProvider());
		counterPeriod.setValue(counterInstance.getCounterTemplate().getLevel());
		counterPeriod.setCode(counterInstance.getCode());
		counterPeriod.setDescription(counterInstance.getDescription());
		counterPeriod.setLevel(counterInstance.getCounterTemplate().getLevel());
		counterPeriod.setCounterType(counterInstance.getCounterTemplate()
				.getCounterType());
		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(counterInstance.getAuditable().getCreator());
		counterPeriod.setAuditable(auditable);
		counterPeriodService.create(em, counterPeriod, counterInstance
				.getAuditable().getCreator(), counterInstance.getProvider());

		counterInstance.getCounterPeriods().add(counterPeriod);
		counterInstance.updateAudit(currentUser);

		return counterPeriod;
	}

	public void updatePeriodValue(Long counterPeriodId, BigDecimal value,
			EntityManager em, User currentUser) throws BusinessException {
		CounterPeriod counterPeriod = counterPeriodService.findById(em,
				counterPeriodId);

		if (counterPeriod == null)
			throw new BusinessException("CounterPeriod with id="
					+ counterPeriodId + " does not exists.");

		counterPeriod.setValue(value);
		counterPeriod.updateAudit(currentUser);
	}

}
