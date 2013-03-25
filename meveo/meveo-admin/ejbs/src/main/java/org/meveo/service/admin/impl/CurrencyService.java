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
package org.meveo.service.admin.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Currency;
import org.meveo.service.base.PersistenceService;

/**
 * Currency service implementation.
 * 
 * @author Ignas
 * @created 2009.09.16
 */
@Stateless @LocalBean
public class CurrencyService extends PersistenceService<Currency> {

	private static final String SYSTEM_CURRENCY_QUERY = "select c from Currency c where c.systemCurrency = true";

	public Currency getSystemCurrency() {
		return (Currency) em.createQuery(SYSTEM_CURRENCY_QUERY)
				.getSingleResult();
	}

	public void setNewSystemCurrency(Currency currency) {
		Currency oldSystemCurrency = getSystemCurrency();
		oldSystemCurrency.setSystemCurrency(false);
		em.merge(oldSystemCurrency);
		// set new system currency
		currency.setSystemCurrency(true);
		em.merge(currency);
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
}
