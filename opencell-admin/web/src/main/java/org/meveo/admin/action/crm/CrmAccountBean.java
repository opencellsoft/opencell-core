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

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CrmAccountService;

/**
 * Standard backing bean for {@link AccountEntity} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@Named
@ViewScoped
public class CrmAccountBean extends BaseBean<AccountEntity> {

    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link AccountEntity} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CrmAccountService crmAccountService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CrmAccountBean() {
        super(AccountEntity.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return Account entity
     */
    @Produces
    @Named("accountEntity")
    public AccountEntity init() {
        return initEntity();
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
        if (type.equals(Seller.ACCOUNT_TYPE)) {
            return "objectId";
        }
        return "customerId";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<AccountEntity> getPersistenceService() {
        return crmAccountService;
    }

    /*
     * @see org.meveo.admin.action.BaseBean#getDefaultSort()
     */
    @Override
    protected String getDefaultSort() {
        return "code";
    }

}
