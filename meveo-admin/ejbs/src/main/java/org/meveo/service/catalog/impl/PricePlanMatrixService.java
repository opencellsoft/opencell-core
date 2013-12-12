/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.catalog.impl;

import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.RatingService;

@Stateless
@LocalBean
public class PricePlanMatrixService extends PersistenceService<PricePlanMatrix> {

	public void create(PricePlanMatrix e) {
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
