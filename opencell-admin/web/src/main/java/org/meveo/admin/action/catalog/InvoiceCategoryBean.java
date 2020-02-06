/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;

@Named
@ViewScoped
public class InvoiceCategoryBean extends CustomFieldBean<InvoiceCategory> {

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
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("invoiceSubCategories");
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("invoiceSubCategories");
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
}
