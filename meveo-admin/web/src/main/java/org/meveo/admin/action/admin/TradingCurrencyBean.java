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
package org.meveo.admin.action.admin;

import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link TradingCurrency} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class TradingCurrencyBean extends BaseBean<TradingCurrency> {

	private static final long serialVersionUID = 1L;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	ProviderService providerService;

	public TradingCurrencyBean() {
		super(TradingCurrency.class);
	}

	public List<TradingCurrency> listAll() {
		getFilters();
		if (filters.containsKey("currencyCode")) {
			filters.put("currency.currencyCode", filters.get("currencyCode"));
			filters.remove("currencyCode");
		} else if (filters.containsKey("currency.currencyCode")) {
			filters.remove("currency.currencyCode");
		}
		return super.listAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) {
		String back = null;
		try {
			Provider currentProvider = providerService
					.findById(getCurrentProvider().getId());
			for (TradingCurrency tr : currentProvider.getTradingCurrencies()) {
				if (tr.getCurrency()
						.getCurrencyCode()
						.equalsIgnoreCase(
								entity.getCurrency().getCurrencyCode())
						&& !tr.getId().equals(entity.getId())) {
					throw new BusinessEntityException();

				}
			}
			back = super.saveOrUpdate(killConversation);

		} catch (BusinessEntityException e) {
			messages.error(new BundleKey("messages",
					"tradingCurrency.uniqueField"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(new BundleKey("messages",
					"tradingCurrency.uniqueField"));
		}
		return back;

	}

	public void populateCurrencies(Currency currency) {
		log.info("populatCurrencies currency",
				currency != null ? currency.getCurrencyCode() : null);
		if (currency != null) {
			entity.setCurrency(currency);
			entity.setPrDescription(currency.getDescriptionEn());
		}
	}

	public void onRowSelect(SelectEvent event) {
		if (event.getObject() instanceof Currency) {
			Currency currency = (Currency) event.getObject();
			log.info("populatCurrencies currency",
					currency != null ? currency.getCurrencyCode() : null);
			if (currency != null) {
				entity.setCurrency(currency);
				entity.setPrDescription(currency.getDescriptionEn());
			}
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

	@Override
	protected String getListViewName() {
		return "tradingCurrencies";
	}

	@Override
	public String getNewViewName() {
		return "tradingCurrencyDetail";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "currency");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("currency");
	}

	@Override
	protected String getDefaultSort() {
		return "currency.currencyCode";
	}

}