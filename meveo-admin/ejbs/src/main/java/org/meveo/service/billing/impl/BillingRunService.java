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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class BillingRunService extends PersistenceService<BillingRun> {

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private BillingAccountService billingAccountService;

	@EJB
	private InvoiceService invoiceService;

	public PreInvoicingReportsDTO generatePreInvoicingReports(BillingRun billingRun) throws BusinessException {
		log.debug("start generatePreInvoicingReports.......");

		PreInvoicingReportsDTO preInvoicingReportsDTO = new PreInvoicingReportsDTO();

		preInvoicingReportsDTO.setBillingCycleCode(billingRun.getBillingCycle() != null ? billingRun.getBillingCycle()
				.getCode() : null);
		preInvoicingReportsDTO.setBillingAccountNumber(billingRun.getBillingAccountNumber());
		preInvoicingReportsDTO.setBillableBillingAccountNumber(billingRun.getBillableBillingAcountNumber());
		preInvoicingReportsDTO.setAmoutWitountTax(billingRun.getPrAmountWithoutTax());

		BillingCycle billingCycle = billingRun.getBillingCycle();

		Date startDate = billingRun.getStartDate();
		Date endDate = billingRun.getEndDate();
		endDate = endDate != null ? endDate : new Date();
		List<BillingAccount> billingAccounts = new ArrayList<BillingAccount>();

		if (billingCycle != null) {
			billingAccounts = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
		} else {
			String[] baIds = billingRun.getSelectedBillingAccounts().split(",");
			for (String id : Arrays.asList(baIds)) {
				Long baId = Long.valueOf(id);
				billingAccounts.add(billingAccountService.findById(baId));
			}
		}

		log.debug("BA in PreInvoicingReport: {}", billingAccounts.size());
		Integer checkBANumber = 0;
		Integer directDebitBANumber = 0;
		Integer tipBANumber = 0;
		Integer wiretransferBANumber = 0;

		Integer checkBillableBANumber = 0;
		Integer directDebitBillableBANumber = 0;
		Integer tipBillableBANumber = 0;
		Integer wiretransferBillableBANumber = 0;

		BigDecimal checkBillableBAAmountHT = BigDecimal.ZERO;
		BigDecimal directDebitBillableBAAmountHT = BigDecimal.ZERO;
		BigDecimal tipBillableBAAmountHT = BigDecimal.ZERO;
		BigDecimal wiretransferBillableBAAmountHT = BigDecimal.ZERO;

		for (BillingAccount billingAccount : billingAccounts) {
		    PaymentMethodEnum paymentMethod= billingAccount.getPaymentMethod();
		    if(paymentMethod==null){
		        paymentMethod=billingAccount.getCustomerAccount().getPaymentMethod();
		    }
			switch (paymentMethod) {
			case CHECK:
				checkBANumber++;
				break;
			case DIRECTDEBIT:
				directDebitBANumber++;
				break;
			case TIP:
				tipBANumber++;
				break;
			case WIRETRANSFER:
				wiretransferBANumber++;
				break;

			default:
				break;
			}

		}

		for (BillingAccount billingAccount : billingRun.getBillableBillingAccounts()) {
            PaymentMethodEnum paymentMethod= billingAccount.getPaymentMethod();
            if(paymentMethod==null){
                paymentMethod=billingAccount.getCustomerAccount().getPaymentMethod();
            }
            switch (paymentMethod) {
			case CHECK:
				checkBillableBANumber++;
				checkBillableBAAmountHT = checkBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
				break;
			case DIRECTDEBIT:
				directDebitBillableBANumber++;
				directDebitBillableBAAmountHT = directDebitBillableBAAmountHT.add(billingAccount
						.getBrAmountWithoutTax());
				break;
			case TIP:
				tipBillableBANumber++;
				tipBillableBAAmountHT = tipBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
				break;
			case WIRETRANSFER:
				wiretransferBillableBANumber++;
				wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT.add(billingAccount
						.getBrAmountWithoutTax());
				break;

			default:
				break;
			}
		}

		preInvoicingReportsDTO.setCheckBANumber(checkBANumber);
		preInvoicingReportsDTO.setCheckBillableBAAmountHT(round(checkBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setCheckBillableBANumber(checkBillableBANumber);
		preInvoicingReportsDTO.setDirectDebitBANumber(directDebitBANumber);
		preInvoicingReportsDTO.setDirectDebitBillableBAAmountHT(round(directDebitBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setDirectDebitBillableBANumber(directDebitBillableBANumber);
		preInvoicingReportsDTO.setTipBANumber(tipBANumber);
		preInvoicingReportsDTO.setTipBillableBAAmountHT(round(tipBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setTipBillableBANumber(tipBillableBANumber);
		preInvoicingReportsDTO.setWiretransferBANumber(wiretransferBANumber);
		preInvoicingReportsDTO.setWiretransferBillableBAAmountHT(round(wiretransferBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setWiretransferBillableBANumber(wiretransferBillableBANumber);

		return preInvoicingReportsDTO;
	}

	public PostInvoicingReportsDTO generatePostInvoicingReports(BillingRun billingRun) throws BusinessException {
		log.info("generatePostInvoicingReports billingRun=" + billingRun.getId());
		PostInvoicingReportsDTO postInvoicingReportsDTO = new PostInvoicingReportsDTO();

		BigDecimal globalAmountHT = BigDecimal.ZERO;
		BigDecimal globalAmountTTC = BigDecimal.ZERO;

		Integer positiveInvoicesNumber = 0;
		BigDecimal positiveInvoicesAmountHT = BigDecimal.ZERO;
		BigDecimal positiveInvoicesAmount = BigDecimal.ZERO;
		BigDecimal positiveInvoicesTaxAmount = BigDecimal.ZERO;

		Integer negativeInvoicesNumber = 0;
		BigDecimal negativeInvoicesAmountHT = BigDecimal.ZERO;
		BigDecimal negativeInvoicesTaxAmount = BigDecimal.ZERO;
		BigDecimal negativeInvoicesAmount = BigDecimal.ZERO;

		Integer emptyInvoicesNumber = 0;
		Integer electronicInvoicesNumber = 0;

		Integer checkInvoicesNumber = 0;
		Integer directDebitInvoicesNumber = 0;
		Integer tipInvoicesNumber = 0;
		Integer wiretransferInvoicesNumber = 0;

		BigDecimal checkAmuontHT = BigDecimal.ZERO;
		BigDecimal directDebitAmuontHT = BigDecimal.ZERO;
		BigDecimal tipAmuontHT = BigDecimal.ZERO;
		BigDecimal wiretransferAmuontHT = BigDecimal.ZERO;

		BigDecimal checkAmuont = BigDecimal.ZERO;
		BigDecimal directDebitAmuont = BigDecimal.ZERO;
		BigDecimal tipAmuont = BigDecimal.ZERO;
		BigDecimal wiretransferAmuont = BigDecimal.ZERO;

		for (Invoice invoice : billingRun.getInvoices()) {

			if (invoice.getAmountWithoutTax() != null && invoice.getAmountWithTax() != null) {
				switch (invoice.getPaymentMethod()) {
				case CHECK:
					checkInvoicesNumber++;
					checkAmuontHT = checkAmuontHT.add(invoice.getAmountWithoutTax());
					checkAmuont = checkAmuont.add(invoice.getAmountWithTax());
					break;
				case DIRECTDEBIT:
					directDebitInvoicesNumber++;
					directDebitAmuontHT = directDebitAmuontHT.add(invoice.getAmountWithoutTax());
					directDebitAmuont = directDebitAmuont.add(invoice.getAmountWithTax());
					break;
				case TIP:
					tipInvoicesNumber++;
					tipAmuontHT = tipAmuontHT.add(invoice.getAmountWithoutTax());
					tipAmuont = tipAmuont.add(invoice.getAmountWithTax());
					break;
				case WIRETRANSFER:
					wiretransferInvoicesNumber++;
					wiretransferAmuontHT = wiretransferAmuontHT.add(invoice.getAmountWithoutTax());
					wiretransferAmuont = wiretransferAmuont.add(invoice.getAmountWithTax());
					break;

				default:
					break;
				}
			}

			if (invoice.getAmountWithoutTax() != null && invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0) {
				positiveInvoicesNumber++;
				positiveInvoicesAmountHT = positiveInvoicesAmountHT.add(invoice.getAmountWithoutTax());
				positiveInvoicesTaxAmount = positiveInvoicesTaxAmount.add(invoice.getAmountTax());
				positiveInvoicesAmount = positiveInvoicesAmount.add(invoice.getAmountWithTax());
			} else if (invoice.getAmountWithoutTax() == null
					|| invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) == 0) {
				emptyInvoicesNumber++;
			} else {
				negativeInvoicesNumber++;
				negativeInvoicesAmountHT = negativeInvoicesAmountHT.add(invoice.getAmountWithoutTax());
				negativeInvoicesTaxAmount = negativeInvoicesTaxAmount.add(invoice.getAmountTax());
				negativeInvoicesAmount = negativeInvoicesAmount.add(invoice.getAmountWithTax());
			}

			if (invoice.getBillingAccount().getElectronicBilling()) {
				electronicInvoicesNumber++;
			}

			if (invoice.getAmountWithoutTax() != null && invoice.getAmountWithTax() != null) {
				globalAmountHT = globalAmountHT.add(invoice.getAmountWithoutTax());
				globalAmountTTC = globalAmountTTC.add(invoice.getAmountWithTax());
			}

		}

		postInvoicingReportsDTO.setInvoicesNumber(billingRun.getInvoiceNumber());
		postInvoicingReportsDTO.setCheckAmuont(checkAmuont);
		postInvoicingReportsDTO.setCheckAmuontHT(checkAmuontHT);
		postInvoicingReportsDTO.setCheckInvoicesNumber(checkInvoicesNumber);
		postInvoicingReportsDTO.setDirectDebitAmuont(directDebitAmuont);
		postInvoicingReportsDTO.setDirectDebitAmuontHT(directDebitAmuontHT);
		postInvoicingReportsDTO.setDirectDebitInvoicesNumber(directDebitInvoicesNumber);
		postInvoicingReportsDTO.setElectronicInvoicesNumber(electronicInvoicesNumber);
		postInvoicingReportsDTO.setEmptyInvoicesNumber(emptyInvoicesNumber);

		postInvoicingReportsDTO.setPositiveInvoicesAmountHT(positiveInvoicesAmountHT);
		postInvoicingReportsDTO.setPositiveInvoicesAmount(positiveInvoicesAmount);
		postInvoicingReportsDTO.setPositiveInvoicesTaxAmount(positiveInvoicesTaxAmount);
		postInvoicingReportsDTO.setPositiveInvoicesNumber(positiveInvoicesNumber);

		postInvoicingReportsDTO.setNegativeInvoicesAmountHT(negativeInvoicesAmountHT);
		postInvoicingReportsDTO.setNegativeInvoicesAmount(negativeInvoicesAmount);
		postInvoicingReportsDTO.setNegativeInvoicesTaxAmount(negativeInvoicesTaxAmount);
		postInvoicingReportsDTO.setNegativeInvoicesNumber(negativeInvoicesNumber);

		postInvoicingReportsDTO.setTipAmuont(tipAmuont);
		postInvoicingReportsDTO.setTipAmuontHT(tipAmuontHT);
		postInvoicingReportsDTO.setTipInvoicesNumber(tipInvoicesNumber);
		postInvoicingReportsDTO.setWiretransferAmuont(wiretransferAmuont);
		postInvoicingReportsDTO.setWiretransferAmuontHT(wiretransferAmuontHT);
		postInvoicingReportsDTO.setWiretransferInvoicesNumber(wiretransferInvoicesNumber);
		postInvoicingReportsDTO.setGlobalAmount(globalAmountHT);

		return postInvoicingReportsDTO;
	}

	public static BigDecimal round(BigDecimal amount, int decimal) {
		if (amount == null) {
			return null;
		}
		amount = amount.setScale(decimal, RoundingMode.HALF_UP);

		return amount;
	}

	public void cleanBillingRun(BillingRun billingRun) {
		Query queryTrans = getEntityManager()
				.createQuery(
						"update "
								+ RatedTransaction.class.getName()
								+ " set invoice=null,invoiceAgregateF=null,invoiceAgregateR=null,invoiceAgregateT=null,status=:status where billingRun=:billingRun");
		queryTrans.setParameter("billingRun", billingRun);
		queryTrans.setParameter("status", RatedTransactionStatusEnum.OPEN);
		queryTrans.executeUpdate();

		Query queryAgregate = getEntityManager().createQuery(
				"delete from " + InvoiceAgregate.class.getName() + " where billingRun=:billingRun");
		queryAgregate.setParameter("billingRun", billingRun);
		queryAgregate.executeUpdate();

		Query queryInvoices = getEntityManager().createQuery(
				"delete from " + Invoice.class.getName() + " where billingRun=:billingRun");
		queryInvoices.setParameter("billingRun", billingRun);
		queryInvoices.executeUpdate();

		Query queryBA = getEntityManager().createQuery(
				"update " + BillingAccount.class.getName() + " set billingRun=null where billingRun=:billingRun");
		queryBA.setParameter("billingRun", billingRun);
		queryBA.executeUpdate();
	}

	public void deleteInvoice(Invoice invoice) {
		Query queryTrans = getEntityManager()
				.createQuery(
						"update "
								+ RatedTransaction.class.getName()
								+ " set invoice=null,invoiceAgregateF=null,invoiceAgregateR=null,invoiceAgregateT=null where invoice=:invoice");
		queryTrans.setParameter("invoice", invoice);
		queryTrans.executeUpdate();

		Query queryAgregate = getEntityManager().createQuery(
				"delete from " + InvoiceAgregate.class.getName() + " where invoice=:invoice");
		queryAgregate.setParameter("invoice", invoice);
		queryAgregate.executeUpdate();

		Query queryInvoices = getEntityManager().createQuery(
				"delete from " + Invoice.class.getName() + " where id=:invoiceId");
		queryInvoices.setParameter("invoiceId", invoice.getId());
		queryInvoices.executeUpdate();
		getEntityManager().flush();
	}

	@SuppressWarnings("unchecked")
	public boolean isActiveBillingRunsExist(Provider provider) {
		QueryBuilder qb = new QueryBuilder(BillingRun.class, "c");
		qb.startOrClause();
		qb.addCriterionEnum("c.status", BillingRunStatusEnum.NEW);
		qb.addCriterionEnum("c.status", BillingRunStatusEnum.ON_GOING);
		qb.addCriterionEnum("c.status", BillingRunStatusEnum.TERMINATED);
		qb.addCriterionEnum("c.status", BillingRunStatusEnum.WAITING);
		qb.endOrClause();
		qb.addCriterionEntity("c.provider", provider);
		List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

		return billingRuns != null && billingRuns.size() > 0 ? true : false;
	}

	public void retateBillingRunTransactions(BillingRun billingRun) {
		for (RatedTransaction ratedTransaction : billingRun.getRatedTransactions()) {
			WalletOperation walletOperation = walletOperationService.findById(ratedTransaction.getWalletOperationId());
			walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE);
			walletOperationService.update(walletOperation);
		}
	}

	public List<BillingRun> getbillingRuns(Provider provider, String code) {
		return getbillingRuns(getEntityManager(), provider, code);
	}

	@SuppressWarnings("unchecked")
	public List<BillingRun> getbillingRuns(EntityManager em, Provider provider, String code) {
		QueryBuilder qb = new QueryBuilder(BillingRun.class, "c", null, provider);

		qb.startOrClause();
		if (code != null) {
			qb.addCriterion("c.billingCycle.code", "=", code, false);
		}
		qb.endOrClause();

		List<BillingRun> billingRuns = qb.getQuery(em).getResultList();

		return billingRuns;

	}

	@SuppressWarnings("unchecked")
	public List<BillingRun> getbillingRuns(Provider provider, BillingRunStatusEnum... status) {
		BillingRunStatusEnum bRStatus;
		log.debug("getbillingRuns for provider " + provider == null ? "null" : provider.getCode());
		QueryBuilder qb = new QueryBuilder(BillingRun.class, "c", null, provider);

		qb.startOrClause();
		if (status != null) {
			for (int i = 0; i < status.length; i++) {
				bRStatus = status[i];
				qb.addCriterionEnum("c.status", bRStatus);
			}
		}
		qb.endOrClause();

		List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

		for (BillingRun br : billingRuns) {
			getEntityManager().refresh(br);
		}

		return billingRuns;
	}

	public List<BillingRun> getValidatedBillingRuns() {
		return getValidatedBillingRuns(getCurrentProvider());
	}

	public List<BillingRun> getValidatedBillingRuns(Provider provider) {
		return getValidatedBillingRuns(getEntityManager(), provider);
	}

	@SuppressWarnings("unchecked")
	public List<BillingRun> getValidatedBillingRuns(EntityManager em, Provider provider) {
		QueryBuilder qb = new QueryBuilder(BillingRun.class, "c", null, provider);
		qb.addCriterionEnum("c.status", BillingRunStatusEnum.VALIDATED);
		qb.addBooleanCriterion("c.xmlInvoiceGenerated", false);
		List<BillingRun> billingRuns = qb.getQuery(em).getResultList();

		return billingRuns;

	}

	public BillingRun getBillingRunById(long id, Provider provider) {
		return getBillingRunById(getEntityManager(), id, provider);
	}

	public BillingRun getBillingRunById(EntityManager em, long id, Provider provider) {
		QueryBuilder qb = new QueryBuilder(BillingRun.class, "b");
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("id", "=", id, true);

		try {
			return (BillingRun) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<BillingAccount> getBillingAccounts(BillingRun billingRun) {
		List<BillingAccount> result = null;
		BillingCycle billingCycle = billingRun.getBillingCycle();

		log.debug("getBillingAccounts for billingRun {}", billingRun.getId());

		Object[] ratedTransactionsAmounts = null;
		if (billingCycle != null) {
			Date startDate = billingRun.getStartDate();
			Date endDate = billingRun.getEndDate();

			if (startDate != null && endDate == null) {
				endDate = new Date();
			}

			if (startDate != null) {
				ratedTransactionsAmounts = (Object[]) getEntityManager()
						.createNamedQuery("RatedTransaction.sumbillingRunByCycle")
						.setParameter("status", RatedTransactionStatusEnum.OPEN)
						.setParameter("billingCycle", billingCycle).setParameter("startDate", startDate)
						.setParameter("endDate", endDate).getSingleResult();
			} else {
				ratedTransactionsAmounts = (Object[]) getEntityManager()
						.createNamedQuery("RatedTransaction.sumbillingRunByCycleNoDate")
						.setParameter("status", RatedTransactionStatusEnum.OPEN)
						.setParameter("billingCycle", billingCycle).getSingleResult();
			}

			result = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
		} else {
			result = new ArrayList<BillingAccount>();
			String[] baIds = billingRun.getSelectedBillingAccounts().split(",");

			for (String id : Arrays.asList(baIds)) {
				Long baId = Long.valueOf(id);
				result.add(billingAccountService.findById(baId));
			}

			ratedTransactionsAmounts = (Object[]) getEntityManager()
					.createNamedQuery("RatedTransaction.sumbillingRunByList")
					.setParameter("status", RatedTransactionStatusEnum.OPEN).setParameter("billingAccountList", result)
					.getSingleResult();
		}

		if (ratedTransactionsAmounts != null) {
			billingRun.setPrAmountWithoutTax((BigDecimal) ratedTransactionsAmounts[0]);
			billingRun.setPrAmountWithTax((BigDecimal) ratedTransactionsAmounts[1]);
			billingRun.setPrAmountTax((BigDecimal) ratedTransactionsAmounts[2]);
		}

		updateNoCheck(billingRun);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void createAgregatesAndInvoice(Long billingRunId, User currentUser) throws BusinessException, Exception {
		List<BillingAccount> billingAccounts = getEntityManager()
				.createNamedQuery("BillingAccount.listByBillingRunId", BillingAccount.class)
				.setParameter("billingRunId", billingRunId).getResultList();

		for (BillingAccount billingAccount : billingAccounts) {
			try {
				invoiceService.createAgregatesAndInvoice(billingAccount, billingRunId, currentUser);
			} catch (Exception e) {
				log.error("Error for BA=" + billingAccount.getCode() + " : " + e.getMessage());
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void validate(BillingRun billingRun, User user) {
		billingRun = findById(billingRun.getId(), true);
		user = getEntityManager().find(User.class, user.getId());
		for (Invoice invoice : billingRun.getInvoices()) {
			invoiceService.setInvoiceNumber(invoice, user);
			BillingAccount billingAccount = invoice.getBillingAccount();
			Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate();
			billingAccount.setNextInvoiceDate(nextCalendarDate);
			billingAccount.updateAudit(user);
		}

		billingRun.setStatus(BillingRunStatusEnum.VALIDATED);
		billingRun.updateAudit(user);
	}

}
