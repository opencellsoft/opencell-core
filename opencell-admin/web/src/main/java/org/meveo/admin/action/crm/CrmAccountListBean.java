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
package org.meveo.admin.action.crm;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CrmAccountService;

/**
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */

@Named
@ConversationScoped
public class CrmAccountListBean extends BaseBean<AccountEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * CRM Service injection.
     */
    @Inject
    private CrmAccountService crmAccountService;

    public CrmAccountListBean() {
        super(AccountEntity.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<AccountEntity> getPersistenceService() {
        return crmAccountService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultSort()
     */
    @Override
    protected String getDefaultSort() {
        return "code";
    }

    /**
     * Gets the exact detail view by type param.
     * 
     * @param type
     * @return view uri
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
        }
    }

}