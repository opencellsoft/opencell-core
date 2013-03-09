/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.service.billing.local;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.service.base.local.AccountServiceLocal;

@Local
public interface BillingAccountServiceLocal extends AccountServiceLocal<BillingAccount> {

    public void createBillingAccount(BillingAccount billingAccount, User creator);

    public void updateBillingAccount(BillingAccount billingAccount, User updater);

    public void updateElectronicBilling(String code, Boolean electronicBilling, User updater) throws BusinessException;

    public void updateBillingAccountDiscount(String code, BigDecimal ratedDiscount, User updater)
            throws BusinessException;

    public BillingAccount billingAccountDetails(String code) throws BusinessException;

    public void billingAccountTermination(String code, Date terminationDate, User updater) throws BusinessException;

    public void billingAccountCancellation(String code, Date terminationDate, User updater) throws BusinessException;

    public void billingAccountReactivation(String code, Date activationDate, User updater) throws BusinessException;

    public void closeBillingAccount(String code, User updater) throws UnknownAccountException, ElementNotResiliatedOrCanceledException;

    public List<Invoice> invoiceList(String code) throws BusinessException;

    public Invoice InvoiceDetail(String invoiceReference);

    public InvoiceSubCategory invoiceSubCategoryDetail(String invoiceReference, String invoiceSubCategoryCode);
    public boolean isDuplicationExist(BillingAccount billingAccount);
}