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
import org.meveo.model.admin.Seller;
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
        if(type.equals(Seller.ACCOUNT_TYPE)) {
        	return "/pages/admin/sellers/sellerDetail.xhml";
        }
        if (type.equals(UserAccount.ACCOUNT_TYPE)) {
            return "/pages/billing/userAccounts/userAccountDetail.xhtml";
        } else {
            return "/pages/crm/customers/customerDetail.xhtml";
        }
    }

}