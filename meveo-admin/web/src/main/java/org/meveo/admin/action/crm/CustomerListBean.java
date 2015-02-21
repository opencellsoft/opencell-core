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
package org.meveo.admin.action.crm;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.crm.Customer;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomerService;

/**
 * Standard backing bean for {@link Customer} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ConversationScoped
public class CustomerListBean extends BaseBean<Customer> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link Customer} service. Extends {@link PersistenceService}. */
	@Inject
	private CustomerService customerService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CustomerListBean() {
		super(Customer.class);
	}

	// TODO
	// /**
	// * Factory method, that is invoked if data model is empty. In this window
	// we
	// * don't need to display data table when page is loaded. Only when Search
	// * button is pressed we should show the list. In this method we check if
	// * list isn't called for sorting and pagination.
	// *
	// * @see org.meveo.admin.action.BaseBean#list()
	// */
	// @Produces
	// @Named("customers")
	// public void noList() {
	// final FacesContext context = FacesContext.getCurrentInstance();
	// final String sortField =
	// context.getExternalContext().getRequestParameterMap()
	// .get("sortField");
	// final String resultsForm =
	// context.getExternalContext().getRequestParameterMap()
	// .get("results_form");
	//
	// if ((sortField != null) || (resultsForm != null)) {
	// this.list();
	// }
	// }

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Customer> getPersistenceService() {
		return customerService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
}
