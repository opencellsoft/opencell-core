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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.AccountService;

@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

	@Inject
	private UserAccountService userAccountService;

	@EJB
	private BillingRunService billingRunService;

	public void initBillingAccount(BillingAccount billingAccount) {
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
	}

	public void createBillingAccount(BillingAccount billingAccount, User creator, Provider provider) {
		createBillingAccount(getEntityManager(), billingAccount, creator, provider);
	}

	public void createBillingAccount(EntityManager em, BillingAccount billingAccount, User creator, Provider provider) {
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

		create(billingAccount, creator, provider);
	}

	public BillingAccount updateElectronicBilling(BillingAccount billingAccount, Boolean electronicBilling, User updater,
			Provider provider) throws BusinessException {
		billingAccount.setElectronicBilling(electronicBilling);
		return update(billingAccount, updater);
	}

	public BillingAccount updateBillingAccountDiscount(BillingAccount billingAccount, BigDecimal ratedDiscount, User updater)
			throws BusinessException {
		billingAccount.setDiscountRate(ratedDiscount);
		return update(billingAccount, updater);
	}

	public BillingAccount billingAccountTermination(BillingAccount billingAccount, Date terminationDate,
			SubscriptionTerminationReason terminationReason, User updater) throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		for (UserAccount userAccount : userAccounts) {
			userAccountService.userAccountTermination(userAccount, terminationDate, terminationReason, updater);
		}
		billingAccount.setTerminationReason(terminationReason);
		billingAccount.setTerminationDate(terminationDate);
		billingAccount.setStatus(AccountStatusEnum.TERMINATED);
		return update(billingAccount, updater);
	}

	public BillingAccount billingAccountCancellation(BillingAccount billingAccount, Date terminationDate, User updater)
			throws BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		for (UserAccount userAccount : userAccounts) {
			userAccountService.userAccountCancellation(userAccount, terminationDate, updater);
		}
		billingAccount.setTerminationDate(terminationDate);
		billingAccount.setStatus(AccountStatusEnum.CANCELED);
		return update(billingAccount, updater);
	}

	public BillingAccount billingAccountReactivation(BillingAccount billingAccount, Date activationDate, User updater)
			throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("billing account", billingAccount.getCode());
		}

		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setStatusDate(activationDate);
		return update(billingAccount, updater);
	}

	public BillingAccount closeBillingAccount(BillingAccount billingAccount, User updater) throws UnknownAccountException,
			ElementNotResiliatedOrCanceledException {

		/**
		 * *
		 * 
		 * @Todo : ajouter la condition : l'encours de facturation est vide :
		 */
		if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("billing account", billingAccount.getCode());
		}
		billingAccount.setStatus(AccountStatusEnum.CLOSED);
		return update(billingAccount, updater);
	}

	public List<Invoice> invoiceList(BillingAccount billingAccount) throws BusinessException {
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

	public InvoiceSubCategory invoiceSubCategoryDetail(String invoiceReference, String invoiceSubCategoryCode) {
		// TODO : need to be more clarified
		return null;
	}

	public boolean isDuplicationExist(BillingAccount billingAccount) {
		if (billingAccount == null || !billingAccount.getDefaultLevel()) {
			return false;
		}

		List<BillingAccount> billingAccounts = listByCustomerAccount(billingAccount.getCustomerAccount());
		for (BillingAccount ba : billingAccounts) {
			if (ba.getDefaultLevel() != null
					&& ba.getDefaultLevel()
					&& (billingAccount.getId() == null || (billingAccount.getId() != null && !billingAccount.getId()
							.equals(ba.getId())))) {
				return true;
			}
		}

		return false;

	}

	@SuppressWarnings("unchecked")
    public List<BillingAccount> findBillingAccounts(BillingCycle billingCycle, Date startdate, Date endDate,Provider currentProvider) {
		try {
			QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b",null,currentProvider);
			qb.addCriterionEntity("b.billingCycle", billingCycle);

			if (startdate != null) {
				qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
			}

			if (endDate != null) {
				qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate);
			}

			return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
		} catch (Exception ex) {
			log.error("failed to find billing accounts",ex);
		}

		return null;
	}
	
	

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean updateBillingAccountTotalAmounts(BillingAccount billingAccount, BillingRun billingRun,
			User currentUser) {

		log.debug("updateBillingAccountTotalAmounts  billingAccount:" + billingAccount.getCode());

		billingAccount = findById(billingAccount.getId(), true);
		
		//FIXE ME : api caller
		log.debug("billingRun.id:"+((billingRun==null)?"null":billingRun.getId()));
		billingRun = billingRunService.findById(billingRun.getId(),true);
		log.debug(" refresh billingRun.id:"+((billingRun==null)?"null":billingRun.getId()));

			Query q = null;
			if (billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
				q = getEntityManager().createNamedQuery("RatedTransaction.sumBillingAccountDisplayFree")
				        .setParameter(
						"billingAccount", billingAccount);
			} else {
				q = getEntityManager().createNamedQuery("RatedTransaction.sumBillingAccount").setParameter(
						"billingAccount", billingAccount);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> queryResults = q.setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
			        .getResultList();
			Object[] queryResult = queryResults.size() > 0 ? queryResults.get(0) : null;

			if (queryResult != null) {
			    if(queryResult[0]==null){
			        return false;
			    }
			    if(billingRun.getBillingCycle() != null){
			    	if(billingRun.getBillingCycle().getInvoicingThreshold() != null  && 
			    			billingRun.getBillingCycle().getInvoicingThreshold().compareTo((BigDecimal) queryResult[0]) < 0 	){
			    		log.debug("updateBillingAccountTotalAmounts  baCode:{}, mountWithoutTax:{} ,invoicingThreshold:{}",billingAccount.getCode(),billingRun.getBillingCycle().getInvoicingThreshold(),(BigDecimal) queryResult[0]);
			    		return false;
			    	}
			    }
				billingAccount.setBrAmountWithoutTax((BigDecimal) queryResult[0]);
				billingAccount.setBrAmountWithTax((BigDecimal) queryResult[1]);
				log.debug("set brAmount {} in BA {}", queryResult[0], billingAccount.getId());
			}

			billingAccount.setBillingRun(billingRun);
			billingAccount.updateAudit(currentUser);
			updateNoCheck(billingAccount);
			getEntityManager().flush();
		

		return true;
	}

	@SuppressWarnings("unchecked")
	public List<BillingAccount> listByCustomerAccount(CustomerAccount customerAccount) {
		QueryBuilder qb = new QueryBuilder(BillingAccount.class, "c");
		qb.addCriterionEntity("customerAccount", customerAccount);

		try {
			return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("error while getting list by customer account",e);
			return null;
		}
	}

}
