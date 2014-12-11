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
package org.meveo.service.catalog.impl;

import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.RatingService;

@Stateless
public class PricePlanMatrixService extends PersistenceService<PricePlanMatrix> {

	public void create(PricePlanMatrix e) throws BusinessException {
		super.create(e);
		RatingService.setPricePlanDirty();
	}

	public void update(PricePlanMatrix e) {
		super.update(e);
		RatingService.setPricePlanDirty();
	}

	public void remove(Long id) {
		super.remove(id);
		RatingService.setPricePlanDirty();
	}

	public void disable(Long id) {
		super.disable(id);
		RatingService.setPricePlanDirty();
	}

	public void remove(PricePlanMatrix e) {
		super.remove(e);
		RatingService.setPricePlanDirty();
	}

	public void remove(Set<Long> ids) {
		super.remove(ids);
		RatingService.setPricePlanDirty();
	}

	public void update(PricePlanMatrix e, User updater) {
		super.update(e, updater);
		RatingService.setPricePlanDirty();
	}

	public void create(PricePlanMatrix e, User creator) {
		super.create(e, creator);
		RatingService.setPricePlanDirty();
	}

	public void create(PricePlanMatrix e, User creator, Provider provider) {
		super.create(e, creator, provider);
		RatingService.setPricePlanDirty();
	}

	public void removeByPrefix(EntityManager em, String prefix,
			Provider provider) {
		Query query = em
				.createQuery("DELETE PricePlanMatrix m WHERE m.eventCode LIKE '"
						+ prefix + "%' AND m.provider=:provider");
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	public void removeByCode(EntityManager em, String code, Provider provider) {
		Query query = em
				.createQuery("DELETE PricePlanMatrix m WHERE m.eventCode=:code AND m.provider=:provider");
		query.setParameter("code", code);
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	public PricePlanMatrix findByEventCodeAndCurrency(EntityManager em,
			String code, TradingCurrency tradingCurrency) {
		QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "p");
		try {
			qb.addCriterion("eventCode", "=", code, false);
			qb.addCriterionEntity("tradingCurrency", tradingCurrency);
			return (PricePlanMatrix) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn("no result");
			return null;
		}
	}

}
