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
package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.local.InvoiceServiceLocal;
import org.meveo.service.billing.remote.InvoiceServiceRemote;
import org.meveo.service.crm.local.ProviderServiceLocal;

/**
 * Invoice service implementation.
 * 
 * @author Gediminas
 * @created 2010.05.14
 */
@Stateless
@Name("invoiceService")
@AutoCreate
public class InvoiceService extends PersistenceService<Invoice> implements
		InvoiceServiceLocal, InvoiceServiceRemote {

	@Logger
	private Log log;

	@In
	private ProviderServiceLocal providerService;

	public Invoice getInvoiceByNumber(String invoiceNumber, String providerCode)
			throws BusinessException {
		try {
			Query q = em
					.createQuery("from Invoice where invoiceNumber = :invoiceNumber and provider=:provider");
			q.setParameter("invoiceNumber", invoiceNumber).setParameter(
					"provider", providerService.findByCode(providerCode));
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log
					.info(
							"Invoice with invoice number #0 was not found. Returning null.",
							invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log
					.info(
							"Multiple invoices with invoice number #0 was found. Returning null.",
							invoiceNumber);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Invoice getInvoiceByNumber(String invoiceNumber)
			throws BusinessException {
		try {
			Query q = em
					.createQuery("from Invoice where invoiceNumber = :invoiceNumber");
			q.setParameter("invoiceNumber", invoiceNumber);
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log
					.info(
							"Invoice with invoice number #0 was not found. Returning null.",
							invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log
					.info(
							"Multiple invoices with invoice number #0 was found. Returning null.",
							invoiceNumber);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices(BillingRun billingRun)
			throws BusinessException {
		try {
			Query q = em
					.createQuery("from Invoice where billingRun = :billingRun");
			q.setParameter("billingRun", billingRun);
			List<Invoice> invoices = q.getResultList();
			return invoices;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices(BillingAccount billingAccount, String invoiceType)
			throws BusinessException {
		try {
			Query q = em.createQuery("from Invoice where billingAccount = :billingAccount and invoiceType=:invoiceType");
			q.setParameter("billingAccount", billingAccount);
			q.setParameter("invoiceType", invoiceType);
			List<Invoice> invoices = q.getResultList();
			log.info("getInvoices: founds #0 invoices with BA_code=#1 and type=#2 ",invoices.size(),billingAccount.getCode(),invoiceType);
			return invoices;
		} catch (Exception e) {
			return null;
		}
	}
}
