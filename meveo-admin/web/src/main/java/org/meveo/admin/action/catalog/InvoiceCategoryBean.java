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
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class InvoiceCategoryBean extends BaseBean<InvoiceCategory> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link InvoiceCategory} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceCategoryBean() {
		super(InvoiceCategory.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<InvoiceCategory> getPersistenceService() {
		return invoiceCategoryService;
	}

	@Override
	protected String getListViewName() {
		return "invoiceCategories";
	}

	@Override
	public String getNewViewName() {
		return "invoiceCategoryDetail";
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("invoiceSubCategories");
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "invoiceSubCategories");
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

}
