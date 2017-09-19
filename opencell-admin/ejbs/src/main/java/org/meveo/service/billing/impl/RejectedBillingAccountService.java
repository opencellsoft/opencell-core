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
package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.service.base.PersistenceService;

@Stateless
public class RejectedBillingAccountService extends PersistenceService<RejectedBillingAccount> {

    /**
     * Register that billing account invoicing has failed
     * 
     * @param billingAccount Billing account
     * @param billingRun Billing run
     * @param reason Reason why it failed
     * @throws BusinessException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(BillingAccount billingAccount, BillingRun billingRun, String reason) throws BusinessException {
        RejectedBillingAccount rejectedBA = new RejectedBillingAccount(billingAccount, billingRun, reason);
        super.create(rejectedBA);
    }
}