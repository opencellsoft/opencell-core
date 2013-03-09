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

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.local.IPersistenceService;

/**
 * @author R.AITYAAZZA
 * @created 29 d�c. 10
 */
@Local
public interface BillingRunServiceLocal extends IPersistenceService<BillingRun> {
    public PreInvoicingReportsDTO generatePreInvoicingReports(BillingRun billingRun) throws BusinessException;

    public PostInvoicingReportsDTO generatePostInvoicingReports(BillingRun billingRun) throws BusinessException;

    public void cleanBillingRun(BillingRun billingRun);

    public boolean isActiveBillingRunsExist(Provider provider);

    public void retateBillingRunTransactions(BillingRun billingRun);

    public void deleteInvoice(Invoice invoice);
}
