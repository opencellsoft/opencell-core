/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.AccountService;

@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

	@Inject
	private UserAccountService userAccountService;

	@Inject
	RatedTransactionService ratedTransactionService;

	public void createBillingAccount(BillingAccount billingAccount, User creator) {
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		if (billingAccount.getSubscriptionDate() == null) {
			billingAccount.setSubscriptionDate(new Date());
		}
		if (billingAccount.getNextInvoiceDate() == null) {
			billingAccount.setNextInvoiceDate(new Date());
		}
		if (billingAccount.getCustomerAccount() != null) {
			billingAccount.setProvider(billingAccount.getCustomerAccount()
					.getProvider());
		}
		create(billingAccount, creator);
	}

	public void createBillingAccount(BillingAccount billingAccount,
			User creator, Provider provider) {
		createBillingAccount(getEntityManager(), billingAccount, creator,
				provider);
	}
	public void createBillingAccount(EntityManager em,
			BillingAccount billingAccount, User creator, Provider provider) {
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		if (billingAccount.getSubscriptionDate() == null) {
			billingAccount.setSubscriptionDate(new Date());
		}
		if (billingAccount.getNextInvoiceDate() == null) {
			billingAccount.setNextInvoiceDate(new Date());
		}
		if (billingAccount.getCustomerAccount() != null) {
			billingAccount.setProvider(billingAccount.getCustomerAccount()
					.getProvider());
		}
		create(em, billingAccount, creator, provider);
	}

	public void updateBillingAccount(BillingAccount billingAccount, User updater) {
		update(billingAccount, updater);
	}

	public void updateBillingAccount(EntityManager em,
			BillingAccount billingAccount, User updater) {
		update(em, billingAccount, updater);
	}

	public void updateElectronicBilling(BillingAccount billingAccount,
			Boolean electronicBilling, User updater, Provider provider)
			throws BusinessException {
		billingAccount.setElectronicBilling(electronicBilling);
		update(billingAccount, updater);
	}

	public void updateBillingAccountDiscount(BillingAccount billingAccount,
			BigDecimal ratedDiscount, User updater) throws BusinessException {
		billingAccount.setDiscountRate(ratedDiscount);
		update(billingAccount, updater);
	}

	public void billingAccountTermination(BillingAccount billingAccount,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason, User updater)
			throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		for (UserAccount userAccount : userAccounts) {
			userAccountService.userAccountTermination(userAccount,
					terminationDate, terminationReason, updater);
		}
		billingAccount.setTerminationReason(terminationReason);
		billingAccount.setTerminationDate(terminationDate);
		billingAccount.setStatus(AccountStatusEnum.TERMINATED);
		update(billingAccount, updater);
	}

	public void billingAccountCancellation(BillingAccount billingAccount,
			Date terminationDate, User updater) throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		for (UserAccount userAccount : userAccounts) {
			userAccountService.userAccountCancellation(userAccount,
					terminationDate, updater);
		}
		billingAccount.setTerminationDate(terminationDate);
		billingAccount.setStatus(AccountStatusEnum.CANCELED);
		update(billingAccount, updater);
	}

	public void billingAccountReactivation(BillingAccount billingAccount,
			Date activationDate, User updater) throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException(
					"billing account", billingAccount.getCode());
		}

		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setStatusDate(activationDate);
		update(billingAccount, updater);
	}

	public void closeBillingAccount(BillingAccount billingAccount, User updater)
			throws UnknownAccountException,
			ElementNotResiliatedOrCanceledException {

		/**
		 * *
		 * 
		 * @Todo : ajouter la condition : l'encours de facturation est vide :
		 */
		if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException(
					"billing account", billingAccount.getCode());
		}
		billingAccount.setStatus(AccountStatusEnum.CLOSED);
		update(billingAccount, updater);
	}

	public List<Invoice> invoiceList(BillingAccount billingAccount)
			throws BusinessException {
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
			log.debug("invoice search returns no result for reference={}.",
					invoiceReference);
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

		List<BillingAccount> billingAccounts = billingAccount
				.getCustomerAccount().getBillingAccounts();
		for (BillingAccount ba : billingAccounts) {
			if (ba.getDefaultLevel() != null
					&& ba.getDefaultLevel()
					&& (billingAccount.getId() == null || (billingAccount
							.getId() != null && !billingAccount.getId().equals(
							ba.getId())))) {
				return true;
			}
		}

		return false;

	}

	@SuppressWarnings("unchecked")
	public List<BillingAccount> findBillingAccounts(BillingCycle billingCycle,
			Date startdate, Date endDate) {
		try {
			QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b");
			qb.addCriterionEntity("b.billingCycle", billingCycle);
			if (startdate != null) {
				qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate",
						startdate);
			}
			if (endDate != null) {

				qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate",
						endDate);
			}
			return (List<BillingAccount>) qb.getQuery(getEntityManager())
					.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Asynchronous
	public Future<Boolean> updateBillingAccountTotalAmounts(long billingAccountId,
			BillingRun billingRun, boolean entreprise) {
		
		log.info("updateBillingAccountTotalAmounts  billingAccountId:" + billingAccountId);
		BillingAccount billingAccount = findById(getEntityManager(),billingAccountId);
		ratedTransactionService.billingAccountTotalAmounts(billingAccount,
				entreprise);
		billingAccount.setBillingRun(billingRun);
		update(billingAccount);
		return new AsyncResult<Boolean>(true);
	}

}
