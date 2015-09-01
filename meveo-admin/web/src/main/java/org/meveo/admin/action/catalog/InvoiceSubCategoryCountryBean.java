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
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class InvoiceSubCategoryCountryBean extends
		BaseBean<InvoiceSubcategoryCountry> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PricePlanMatrix} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceSubCategoryCountryBean() {
		super(InvoiceSubcategoryCountry.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<InvoiceSubcategoryCountry> getPersistenceService() {
		return invoiceSubCategoryCountryService;
	}

	@Override
	protected String getListViewName() {
		return "invoiceSubCategoryCountries";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("invoiceSubCategory", "tradingCountry", "tax");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("invoiceSubCategory", "tradingCountry", "tax");
	}

}