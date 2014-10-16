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
package org.meveo.service.admin.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Currency;
import org.meveo.service.base.PersistenceService;

/**
 * Currency service implementation.
 */
@Stateless
@LocalBean
public class CurrencyService extends PersistenceService<Currency> {

	private static final String SYSTEM_CURRENCY_QUERY = "select c from Currency c where c.systemCurrency = true";

	public Currency getSystemCurrency() {
		return (Currency) getEntityManager().createQuery(SYSTEM_CURRENCY_QUERY)
				.getSingleResult();
	}

	public void setNewSystemCurrency(Currency currency) {
		Currency oldSystemCurrency = getSystemCurrency();
		oldSystemCurrency.setSystemCurrency(false);
		getEntityManager().merge(oldSystemCurrency);
		// set new system currency
		currency.setSystemCurrency(true);
		getEntityManager().merge(currency);
	}

	/**
	 * Don't let to delete a currency which is system currency.
	 */
	// TODO use it
	public void validateBeforeRemove(Currency currency)
			throws BusinessException {
		if (currency.getSystemCurrency())
			throw new BusinessException("System currency can not be deleted.");
	}

	public Currency findByCode(String currencyCode) {
		if (currencyCode == null) {
			return null;
		}
		QueryBuilder qb = new QueryBuilder(Currency.class, "c");
		qb.addCriterion("currencyCode", "=", currencyCode, false);

		try {
			return (Currency) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Currency findByCode(EntityManager em, String currencyCode) {
		if (currencyCode == null) {
			return null;
		}
		QueryBuilder qb = new QueryBuilder(Currency.class, "c");
		qb.addCriterion("currencyCode", "=", currencyCode, false);

		try {
			return (Currency) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
