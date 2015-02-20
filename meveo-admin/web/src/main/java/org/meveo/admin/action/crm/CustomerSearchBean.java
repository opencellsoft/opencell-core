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

import java.io.IOException;

import javax.enterprise.context.ConversationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.AccountEntitySearchService;

/**
 * Standard backing bean for {@link AccountEntity} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable. For this window create, edit, view, delete operations are not
 * used, because it just searches of all subtypes of AccountEntity. Crud
 * operations is dedicated to concrete entity window (e.g.
 * {@link CustomerAccount} window). Concrete windows also show more of the
 * fields and filters specific for that entity. This bean works with Manaty
 * custom JSF components.
 */
@Named
@ConversationScoped
public class CustomerSearchBean extends BaseBean<AccountEntity> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AccountEntity} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private AccountEntitySearchService accountEntitySearchService;

	private String customerPage;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CustomerSearchBean() {
		super(AccountEntity.class);
	}

	/**
	 * Override get instance method because AccountEntity is abstract class and
	 * can not be instantiated in {@link BaseBean}.
	 */
	@Override
	public AccountEntity getInstance() throws InstantiationException,
			IllegalAccessException {
		return new AccountEntity() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getAccountType() {
				return "";
			}
		};
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<AccountEntity> getPersistenceService() {
		return accountEntitySearchService;
	}

	/**
	 * Because in customer search any type of customer can appear, this method
	 * is used in UI to get link to concrete customer edit page.
	 * 
	 * @param type
	 *            Account type of Customer
	 * 
	 * @return Edit page url.
	 */
	public String getView(String type) {
		if (type.equals(Customer.ACCOUNT_TYPE)) {
			return "/pages/crm/customers/customerDetail.xhtml";
		} else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
			return "/pages/payments/customerAccounts/customerAccountDetail.xhtml";
		}
		if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
			return "/pages/billing/billingAccounts/billingAccountDetail.xhtml";
		}
		if (type.equals(UserAccount.ACCOUNT_TYPE)) {
			return "/pages/billing/userAccounts/userAccountDetail.xhtml";
		} else {
			return "/pages/crm/customers/customerDetail.xhtml";
			// throw new
			// IllegalStateException("Wrong customer type provided in EL in .xhtml");
		}
	}

	/**
	 * Redirect to view that is selected as type in search field. (for example
	 * when user selects customer account type in search, he is redirected to
	 * custmer accounts search page).
	 */
	public String changeCustomerPage(ValueChangeEvent event) throws IOException {
		String page = (String) event.getNewValue();
		/*
		 * TODO: Redirect.instance().setViewId(page);
		 * Redirect.instance().execute();
		 */
		return page;
	}

	public String getCustomerPage() {
		return customerPage;
	}

	public void setCustomerPage(String customerPage) {
		this.customerPage = customerPage;
	}

	public String getIdParameterName(String type) {
		if (type.equals(Customer.ACCOUNT_TYPE)) {
			return "customerId";
		}
		if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
			return "customerAccountId";
		}
		if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
			return "billingAccountId";
		}
		if (type.equals(UserAccount.ACCOUNT_TYPE)) {
			return "userAccountId";
		}
		return "customerId";
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

}
