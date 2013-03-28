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
package org.meveo.admin.action.admin;

import java.sql.BatchUpdateException;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.local.IPersistenceService;

/**
 * @author Marouane ALAMI
 */
@Named
@ConversationScoped
public class TradingCurrencyBean extends BaseBean<TradingCurrency> {

	private static final long serialVersionUID = 1L;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	public TradingCurrencyBean() {
		super(TradingCurrency.class);
	}

	@Produces
	@Named("tradingCurrency")
	public TradingCurrency init() {
		return initEntity();
	}


	/**
	 * Override default list view name. (By default its class name starting
	 * lower case + 's').
	 * 
	 * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
	 */
	protected String getDefaultViewName() {
		return "tradingCurrencies";
	}

	public void populateCurrencies(Currency currency) {
		log.info("populatCurrencies currency", currency != null ? currency.getCurrencyCode() : null);
		if (currency != null) {
			entity.setCurrency(currency);
			entity.setPrDescription(currency.getDescriotionEn());
		}
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<TradingCurrency> getPersistenceService() {
		return tradingCurrencyService;
	}

	public void test() throws BatchUpdateException {
		throw new BatchUpdateException();
	}

}