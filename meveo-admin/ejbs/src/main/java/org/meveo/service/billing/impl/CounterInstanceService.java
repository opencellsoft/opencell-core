/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
@LocalBean
public class CounterInstanceService extends PersistenceService<CounterInstance> {

	@EJB
	UserAccountService userAccountService;

	@EJB
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
			userAccountService.update(em, userAccount);
		} else {
			result = userAccount.getCounters().get(counterTemplate.getCode());
		}

		return result;
	}

	public CounterPeriod createPeriod(CounterInstance counterInstance,
			Date chargeDate) {
		CounterPeriod counterPeriod = new CounterPeriod();
		counterPeriod.setCounterInstance(counterInstance);
		Date startDate = counterInstance.getCounterTemplate().getCalendar()
				.previousCalendarDate(chargeDate);
		Date endDate = counterInstance.getCounterTemplate().getCalendar()
				.nextCalendarDate(startDate);
		log.info("create counter period from " + startDate + " to " + endDate);
		counterPeriod.setPeriodStartDate(startDate);
		counterPeriod.setPeriodEndDate(endDate);
		counterPeriod.setValue(counterInstance.getCounterTemplate().getLevel());
		counterPeriod.setCode(counterInstance.getCode());
		counterPeriod.setDescription(counterInstance.getDescription());
		counterPeriod.setLevel(counterInstance.getCounterTemplate().getLevel());
		counterPeriod.setCounterType(counterInstance.getCounterTemplate()
				.getCounterType());
		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		counterPeriod.setAuditable(auditable);
		counterPeriodService.create(counterPeriod);
		counterInstance.getCounterPeriods().add(counterPeriod);
		update(counterInstance);
		return counterPeriod;
	}

	public void updatePeriodValue(Long counterPeriodId, BigDecimal value) {
		CounterPeriod counterPeriod = counterPeriodService
				.findById(counterPeriodId);
		counterPeriod.setValue(value);
		counterPeriod.getAuditable().setUpdated(new Date());
		counterPeriodService.update(counterPeriod);
	}
}
