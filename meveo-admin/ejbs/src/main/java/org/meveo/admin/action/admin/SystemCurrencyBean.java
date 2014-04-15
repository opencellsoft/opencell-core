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
