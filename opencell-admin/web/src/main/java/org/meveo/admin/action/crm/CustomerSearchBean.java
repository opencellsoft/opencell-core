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
package org.meveo.admin.action.crm;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.ICustomFieldEntity;
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

public class CustomerSearchBean extends BaseBean<AccountEntity> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AccountEntity} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private AccountEntitySearchService accountEntitySearchService;

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
            public ICustomFieldEntity[] getParentCFEntities() {
                return null;
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
		if (type.equals(CustomerTreeBean.CUSTOMER_KEY)) {
			return "/pages/crm/customers/customerDetail.xhtml";
		} else if (type.equals(CustomerTreeBean.CUSTOMER_ACCOUNT_KEY)) {
			return "/pages/payments/customerAccounts/customerAccountDetail.xhtml";
		}
		if (type.equals(CustomerTreeBean.BILLING_ACCOUNT_KEY)) {
			return "/pages/billing/billingAccounts/billingAccountDetail.xhtml";
		}
		if (type.equals(CustomerTreeBean.USER_ACCOUNT_KEY)) {
			return "/pages/billing/userAccounts/userAccountDetail.xhtml";
		} else {
			return "/pages/crm/customers/customerDetail.xhtml";
			// throw new
			// IllegalStateException("Wrong customer type provided in EL in .xhtml");
		}
	}

	public String getIdParameterName(String type) {
		if (type.equals(CustomerTreeBean.CUSTOMER_KEY)) {
			return "customerId";
		}
		if (type.equals(CustomerTreeBean.CUSTOMER_ACCOUNT_KEY)) {
			return "customerAccountId";
		}
		if (type.equals(CustomerTreeBean.BILLING_ACCOUNT_KEY)) {
			return "billingAccountId";
		}
		if (type.equals(CustomerTreeBean.USER_ACCOUNT_KEY)) {
			return "userAccountId";
		}
		return "customerId";
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

}
