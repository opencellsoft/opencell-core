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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.base.AccountService;

@Stateless
@LocalBean
public class BillingAccountService extends AccountService<BillingAccount> {

	@EJB
	private UserAccountService userAccountService;

	public List<UserAccount> UserAccountList(String code) throws BusinessException {
		BillingAccount billingAccount = findByCode(code);
		return billingAccount.getUsersAccounts();
	}

	public void createBillingAccount(BillingAccount billingAccount, User creator) {
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		if (billingAccount.getSubscriptionDate() == null) {
			billingAccount.setSubscriptionDate(new Date());
		}
		if (billingAccount.getNextInvoiceDate() == null) {
			billingAccount.setNextInvoiceDate(new Date());
		}
		if (billingAccount.getCustomerAccount() != null) {
			billingAccount.setProvider(billingAccount.getCustomerAccount().getProvider());
		}
		create(billingAccount, creator);
	}

	public void updateBillingAccount(BillingAccount billingAccount, User updater) {
		update(billingAccount, updater);
	}

	public void updateElectronicBilling(String code, Boolean electronicBilling, User updater)
			throws BusinessException {
		BillingAccount billingAccount = findByCode(code);
		billingAccount.setElectronicBilling(electronicBilling);
		update(billingAccount, updater);
	}

	public void updateBillingAccountDiscount(String code, BigDecimal ratedDiscount, User updater)
			throws BusinessException {
		BillingAccount billingAccount = findByCode(code);
		billingAccount.setDiscountRate(ratedDiscount);
		update(billingAccount, updater);
	}

	public BillingAccount billingAccountDetails(String code) throws BusinessException {
		BillingAccount billingAccount = findByCode(code);
		return billingAccount;
	}

	public void billingAccountTermination(String code, Date terminationDate,
			SubscriptionTerminationReason terminationReason, User updater) throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		BillingAccount billingAccount = findByCode(code);
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		for (UserAccount userAccount : userAccounts) {
			userAccountService.userAccountTermination(userAccount.getCode(), terminationDate,
					terminationReason, updater);
		}
		billingAccount.setTerminationReason(terminationReason);
		billingAccount.setTerminationDate(terminationDate);
		billingAccount.setStatus(AccountStatusEnum.TERMINATED);
		update(billingAccount, updater);
	}

	public void billingAccountCancellation(String code, Date terminationDate, User updater)
			throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		BillingAccount billingAccount = findByCode(code);
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		for (UserAccount userAccount : userAccounts) {
			userAccountService.userAccountCancellation(userAccount.getCode(), terminationDate,
					updater);
		}
		billingAccount.setTerminationDate(terminationDate);
		billingAccount.setStatus(AccountStatusEnum.CANCELED);
		update(billingAccount, updater);
	}

	public void billingAccountReactivation(String code, Date activationDate, User updater)
			throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		BillingAccount billingAccount = findByCode(code);
		if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("billing account", code);
		}

		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setStatusDate(activationDate);
		update(billingAccount, updater);
	}

	public void closeBillingAccount(String code, User updater) throws UnknownAccountException,
			ElementNotResiliatedOrCanceledException {

		BillingAccount billingAccount = findByCode(code);
		if (billingAccount == null) {
			throw new UnknownAccountException(code);
		}

		/**
		 * *
		 * 
		 * @Todo : ajouter la condition : l'encours de facturation est vide :
		 */
		if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("billing account", code);
		}
		billingAccount.setStatus(AccountStatusEnum.CLOSED);
		update(billingAccount, updater);
	}

	public List<Invoice> invoiceList(String code) throws BusinessException {
		BillingAccount billingAccount = findByCode(code);
		if (billingAccount == null) {
			throw new BusinessException("Cannot found BillingAccount by code:" + code);
		}
		List<Invoice> invoices = billingAccount.getInvoices();
		Collections.sort(invoices, new Comparator<Invoice>() {
			public int compare(Invoice c0, Invoice c1) {

				return c1.getInvoiceDate().compareTo(c0.getInvoiceDate());
			}
		});
		return invoices;
	}

	public Invoice InvoiceDetail(String invoiceReference) {
		try {
			QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
			qb.addCriterion("i.invoiceNumber", "=", invoiceReference, true);
			return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException ex) {
			log.debug("invoice search returns no result for reference={}.", invoiceReference);
		}
		return null;
	}

	public InvoiceSubCategory invoiceSubCategoryDetail(String invoiceReference,
			String invoiceSubCategoryCode) {
		// TODO : need to be more clarified
		return null;
	}

	public boolean isDuplicationExist(BillingAccount billingAccount) {
		if (billingAccount == null || !billingAccount.getDefaultLevel()) {
			return false;
		}

		List<BillingAccount> billingAccounts = billingAccount.getCustomerAccount()
				.getBillingAccounts();
		for (BillingAccount ba : billingAccounts) {
			if (ba.getDefaultLevel() != null
					&& ba.getDefaultLevel()
					&& (billingAccount.getId() == null || (billingAccount.getId() != null && !billingAccount
							.getId().equals(ba.getId())))) {
				return true;
			}
		}

		return false;

	}

}
