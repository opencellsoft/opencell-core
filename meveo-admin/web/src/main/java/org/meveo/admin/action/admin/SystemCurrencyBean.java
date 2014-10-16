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
package org.meveo.admin.action.admin;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.model.admin.Currency;
import org.meveo.service.admin.impl.CurrencyService;

/**
 * Seam bean for system currency entity.
 * 
 * @author Ignas
 * @created 2009.09.15
 */
@Named
@SessionScoped
// TODO: @Restrict("#{s:hasRole('meveo.admin')}")
public class SystemCurrencyBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Injected @{link Currency} service. */
	@Inject
	private CurrencyService currencyService;

	/**
	 * Current system currency.
	 */
	private Currency systemCurrency;

	/**
	 * Selected new currency for becoming new "system currency".
	 */
	private Currency selectedCurrency;

	/**
	 * Load current system currency.
	 */
	@PostConstruct
	public void initSystemCurrency() {
		if (systemCurrency == null) {
			systemCurrency = currencyService.getSystemCurrency();
		}
	}

	/**
	 * Sets new system currency.
	 * 
	 * @param newCurrency
	 *            New system currency.
	 */
	public void saveNewSystemCurrency() {
		if (!systemCurrency.equals(selectedCurrency)) {
			systemCurrency = selectedCurrency;
			currencyService.setNewSystemCurrency(selectedCurrency);
			selectedCurrency = systemCurrency;
		}
	}

	/**
	 * Gets list of all currencies for a drop down select box.
	 * 
	 * @return List of currencies.
	 */
	public List<Currency> getCurrencies() {
		return (List<Currency>) currencyService.list();
	}

	public Currency getSelectedCurrency() {
		if (selectedCurrency == null) {
			selectedCurrency = systemCurrency;
		}
		return selectedCurrency;
	}

	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	public Currency getSystemCurrency() {
		return systemCurrency;
	}

}
