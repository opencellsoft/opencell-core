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
package org.meveo.service.admin.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.service.base.PersistenceService;

/**
 * Currency service implementation.
 */
@Stateless
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
	 * @param currency curencey to check.
	 * @throws BusinessException business exception.
	 */
	// TODO use it
	public void validateBeforeRemove(Currency currency)
			throws BusinessException {
		if (currency.getSystemCurrency()){
			throw new BusinessException("System currency can not be deleted.");
		}
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

	public Currency tryToFindByCodeOrId(Currency currency) {
		if (currency == null) {
			return null;
		}
		Currency result = null;
		if (StringUtils.isNotBlank(currency.getCurrencyCode())) {
			result = findByCode(currency.getCurrencyCode());
		} else if (currency.getId() != null) {
			result = findById(currency.getId());
		}
		if (result == null) {
			throw new BadRequestException("No Currency found with Id: "+currency.getId()+" or Code :"+currency.getCurrencyCode());
		}
		return result;
	}
}
