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

import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Invoice service interface.
 * 
 */
@Local
public interface InvoiceServiceLocal extends IPersistenceService<Invoice> {

	public Invoice getInvoiceByNumber(String invoiceNumber, String providerCode)
			throws BusinessException;

	public Invoice getInvoiceByNumber(String invoiceNumber)
			throws BusinessException;

	public List<Invoice> getInvoices(BillingRun billingRun)
			throws BusinessException;

	public List<Invoice> getInvoices(BillingAccount billingAccount, String invoiceType)
			throws BusinessException;

}
