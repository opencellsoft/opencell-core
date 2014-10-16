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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * BillingCycle service implementation.
 */
@Stateless
@LocalBean
public class BillingCycleService extends PersistenceService<BillingCycle> {
	/**
	 * Find BillingCycle by its billing cycle code.
	 * 
	 * @param billingCycleCode
	 *            Billing Cycle Code
	 * @return Billing cycle found or null.
	 * @throws ElementNotFoundException
	 */
	public BillingCycle findByBillingCycleCode(String billingCycleCode,
			Provider provider) {
		try {
			log.info("findByBillingCycleCode billingCycleCode={},provider={}",
					billingCycleCode, provider != null ? provider.getCode()
							: null);
			Query query = getEntityManager()
					.createQuery(
							"select b from BillingCycle b where b.code = :billingCycleCode and b.provider=:provider");
			query.setParameter("billingCycleCode", billingCycleCode);
			query.setParameter("provider", provider);
			return (BillingCycle) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByBillingCycleCode billing cycle not found : billingCycleCode={},provider={}",
					billingCycleCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}

	public BillingCycle findByBillingCycleCode(EntityManager em,
			String billingCycleCode, Provider provider) {
		try {
			log.info("findByBillingCycleCode billingCycleCode={},provider={}",
					billingCycleCode, provider != null ? provider.getCode()
							: null);
			Query query = em
					.createQuery("select b from BillingCycle b where b.code = :billingCycleCode and b.provider=:provider");
			query.setParameter("billingCycleCode", billingCycleCode);
			query.setParameter("provider", provider);
			return (BillingCycle) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByBillingCycleCode billing cycle not found : billingCycleCode={},provider={}",
					billingCycleCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}

	public BillingCycle findByBillingCycleCode(EntityManager em,
			String billingCycleCode, User currentUser, Provider provider) {
		try {
			log.info("findByBillingCycleCode billingCycleCode={},provider={}",
					billingCycleCode, provider != null ? provider.getCode()
							: null);
			Query query = em
					.createQuery("select b from BillingCycle b where b.code = :billingCycleCode and b.provider=:provider");
			query.setParameter("billingCycleCode", billingCycleCode);
			query.setParameter("provider", provider);
			return (BillingCycle) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn(
					"findByBillingCycleCode billing cycle not found : billingCycleCode={},provider={}",
					billingCycleCode, provider != null ? provider.getCode()
							: null);
			return null;
		}
	}
}