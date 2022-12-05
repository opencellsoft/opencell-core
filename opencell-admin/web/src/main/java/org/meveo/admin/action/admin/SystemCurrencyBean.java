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
package org.meveo.admin.action.admin;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.model.admin.Currency;
import org.meveo.service.admin.impl.CurrencyService;

/**
 * Seam bean for system currency entity.
 * 
 * @author Ignas
 * @since 2009.09.15
 */
@Named
@SessionScoped
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
	 * s
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
