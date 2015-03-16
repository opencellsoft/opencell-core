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
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.PersistenceService;

@Stateless
public class CounterInstanceService extends PersistenceService<CounterInstance> {

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private CounterPeriodService counterPeriodService;

	public CounterInstance counterInstanciation(UserAccount userAccount, CounterTemplate counterTemplate, User creator)
			throws BusinessException {
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
		if (counterTemplate.getCounterLevel() == CounterTemplateLevel.BA) {
			BillingAccount billingAccount = userAccount.getBillingAccount();
			if (!billingAccount.getCounters().containsKey(counterTemplate.getCode())) {
				result = new CounterInstance();
				result.setCounterTemplate(counterTemplate);
				result.setBillingAccount(billingAccount);
				create(result, creator, billingAccount.getProvider());

				billingAccount.getCounters().put(counterTemplate.getCode(), result);
				billingAccountService.setProvider(creator.getProvider());
				billingAccountService.update(billingAccount, creator);
			} else {
				result = userAccount.getBillingAccount().getCounters().get(counterTemplate.getCode());
			}
		} else {
			if (!userAccount.getCounters().containsKey(counterTemplate.getCode())) {
				result = new CounterInstance();
				result.setCounterTemplate(counterTemplate);
				result.setUserAccount(userAccount);
				create(result, creator, userAccount.getProvider());

				userAccount.getCounters().put(counterTemplate.getCode(), result);
				userAccountService.setProvider(creator.getProvider());
				userAccountService.update(userAccount, creator);
			} else {
				result = userAccount.getCounters().get(counterTemplate.getCode());
			}
		}

		return result;
	}

	public CounterInstance counterInstanciation(Notification notification, CounterTemplate counterTemplate, User creator)
			throws BusinessException {
		return counterInstanciation(getEntityManager(), notification, counterTemplate, creator);
	}

	public CounterInstance counterInstanciation(EntityManager em, Notification notification,
			CounterTemplate counterTemplate, User creator) throws BusinessException {
		CounterInstance counterInstance = null;

		if (notification == null) {
			throw new BusinessException("notification is null");
		}

		if (counterTemplate == null) {
			throw new BusinessException("counterTemplate is null");
		}

		if (creator == null) {
			throw new BusinessException("creator is null");
		}

		// Remove current counter instance if it does not match the counter
		// template to be instantiated
		if (notification.getCounterInstance() != null
				&& !counterTemplate.getId().equals(notification.getCounterInstance().getCounterTemplate().getId())) {
			CounterInstance ci = notification.getCounterInstance();
			notification.setCounterInstance(null);
			remove(ci);
		}

		// Instantiate counter instance if there is not one yet
		if (notification.getCounterInstance() == null) {
			counterInstance = new CounterInstance();
			counterInstance.setCounterTemplate(counterTemplate);
			create(counterInstance, creator, notification.getProvider());

			notification.setCounterTemplate(counterTemplate);
			notification.setCounterInstance(counterInstance);
		} else {
			counterInstance = notification.getCounterInstance();
		}

		return counterInstance;
	}

	public CounterPeriod createPeriod(CounterInstance counterInstance, Date chargeDate, Date initDate, User currentUser) {
		refresh(counterInstance);
		counterInstance = (CounterInstance) attach(counterInstance);
		CounterPeriod counterPeriod = new CounterPeriod();
		counterPeriod.setCounterInstance(counterInstance);
		Calendar cal = counterInstance.getCounterTemplate().getCalendar();
		cal.setInitDate(initDate);
		Date startDate = cal.previousCalendarDate(chargeDate);
		if (startDate == null) {
			log.info("cannot create counter for the date {} (not in calendar)", chargeDate);
			return null;
		}
		Date endDate = cal.nextCalendarDate(startDate);
		log.info("create counter period from {} to {}", startDate, endDate);

		counterPeriod.setPeriodStartDate(startDate);
		counterPeriod.setPeriodEndDate(endDate);
		counterPeriod.setProvider(counterInstance.getProvider());
		counterPeriod.setValue(counterInstance.getCounterTemplate().getLevel());
		counterPeriod.setCode(counterInstance.getCode());
		counterPeriod.setDescription(counterInstance.getDescription());
		counterPeriod.setLevel(counterInstance.getCounterTemplate().getLevel());
		counterPeriod.setCounterType(counterInstance.getCounterTemplate().getCounterType());
		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(counterInstance.getAuditable().getCreator());
		counterPeriod.setAuditable(auditable);
		counterPeriodService.create(counterPeriod, counterInstance.getAuditable().getCreator(),
				counterInstance.getProvider());

		counterInstance.getCounterPeriods().add(counterPeriod);
		counterInstance.updateAudit(currentUser);

		return counterPeriod;
	}

	/**
	 * Find or create a counter period for a given date
	 * 
	 * @param counterInstance
	 *            Counter instance
	 * @param date
	 *            Date to match
	 * @param currentUser
	 *            User performing operation
	 * @return Found or created counter period
	 */
	public CounterPeriod getCounterPeriod(CounterInstance counterInstance, Date date, Date initDate, User currentUser) {
		Query query = getEntityManager().createNamedQuery("CounterPeriod.findByPeriodDate");
		query.setParameter("counterInstance", counterInstance);
		query.setParameter("date", date, TemporalType.TIMESTAMP);

		try {
			return (CounterPeriod) query.getSingleResult();
		} catch (NoResultException e) {
			return createPeriod(counterInstance, date, initDate, currentUser);
		}
	}

	/**
	 * Update counter period value
	 * 
	 * @param counterPeriodId
	 *            Counter period identifier
	 * @param value
	 *            Value to set to
	 * @param currentUser
	 *            User performing an action
	 * @throws BusinessException
	 */
	public void updatePeriodValue(Long counterPeriodId, BigDecimal value, User currentUser) throws BusinessException {
		CounterPeriod counterPeriod = counterPeriodService.findById(counterPeriodId);

		if (counterPeriod == null) {
			throw new BusinessException("CounterPeriod with id=" + counterPeriodId + " does not exists.");
		}

		counterPeriod.setValue(value);
		counterPeriod.updateAudit(currentUser);
	}

	/**
	 * Deduce a given value from a counter
	 * 
	 * @param counterInstance
	 *            Counter instance
	 * @param date
	 *            Date of event
	 * @param value
	 *            Value to deduce
	 * @param currentUser
	 *            User performing an action
	 * @return
	 * @throws CounterValueInsufficientException
	 */
	public BigDecimal deduceCounterValue(CounterInstance counterInstance, Date date, Date initDate, BigDecimal value,
			User currentUser) throws CounterValueInsufficientException {

		CounterPeriod counterPeriod = getCounterPeriod(counterInstance, date, initDate, currentUser);

		if (counterPeriod == null || counterPeriod.getValue().compareTo(value) < 0) {
			throw new CounterValueInsufficientException();

		} else {
			counterPeriod.setValue(counterPeriod.getValue().subtract(value));
			counterPeriod.updateAudit(currentUser);
			return counterPeriod.getValue();
		}
	}
}
