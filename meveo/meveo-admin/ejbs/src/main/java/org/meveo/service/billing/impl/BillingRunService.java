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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunList;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless
@LocalBean
public class BillingRunService extends PersistenceService<BillingRun> {


	@EJB
	private WalletOperationService walletOperationService;

	@EJB
	private ProviderService providerService;
	
	@EJB
	private BillingAccountService  billingAccountService;

	@EJB
	InvoiceService invoiceService;

	public PreInvoicingReportsDTO generatePreInvoicingReports(BillingRun billingRun)
			throws BusinessException {
		log.debug("start generatePreInvoicingReports.......");
		PreInvoicingReportsDTO preInvoicingReportsDTO = new PreInvoicingReportsDTO();

		preInvoicingReportsDTO
				.setBillingCycleCode(billingRun.getBillingCycle() != null ? billingRun
						.getBillingCycle().getCode() : null);
		preInvoicingReportsDTO.setBillingAccountNumber(billingRun.getBillingAccountNumber());
		preInvoicingReportsDTO.setBillableBillingAccountNumber(billingRun
				.getBillableBillingAcountNumber());
		preInvoicingReportsDTO.setAmoutWitountTax(billingRun.getPrAmountWithoutTax());

		BillingCycle billingCycle = billingRun.getBillingCycle();

        boolean entreprise = billingRun.getProvider().isEntreprise();

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();
        endDate = endDate != null ? endDate : new Date();
        List<BillingAccount> billingAccounts = null;
        if (billingCycle != null) {
            billingAccounts = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
        } else {
        	billingAccounts = billingRun.getBillableBillingAccounts();
        }

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

			switch (billingAccount.getPaymentMethod()) {
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
			switch (billingAccount.getPaymentMethod()) {
			case CHECK:
				checkBillableBANumber++;
				checkBillableBAAmountHT = checkBillableBAAmountHT.add(billingAccount
						.getBrAmountWithoutTax());
				break;
			case DIRECTDEBIT:
				directDebitBillableBANumber++;
				directDebitBillableBAAmountHT = directDebitBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
				break;
			case TIP:
				tipBillableBANumber++;
				tipBillableBAAmountHT = tipBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
				break;
			case WIRETRANSFER:
				wiretransferBillableBANumber++;
				wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
				break;

			default:
				break;
			}

		}

		preInvoicingReportsDTO.setCheckBANumber(checkBANumber);
		preInvoicingReportsDTO.setCheckBillableBAAmountHT(round(checkBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setCheckBillableBANumber(checkBillableBANumber);
		preInvoicingReportsDTO.setDirectDebitBANumber(directDebitBANumber);
		preInvoicingReportsDTO.setDirectDebitBillableBAAmountHT(round(
				directDebitBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setDirectDebitBillableBANumber(directDebitBillableBANumber);
		preInvoicingReportsDTO.setTipBANumber(tipBANumber);
		preInvoicingReportsDTO.setTipBillableBAAmountHT(round(tipBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setTipBillableBANumber(tipBillableBANumber);
		preInvoicingReportsDTO.setWiretransferBANumber(wiretransferBANumber);
		preInvoicingReportsDTO.setWiretransferBillableBAAmountHT(round(
				wiretransferBillableBAAmountHT, 2));
		preInvoicingReportsDTO.setWiretransferBillableBANumber(wiretransferBillableBANumber);

		return preInvoicingReportsDTO;

	}

	public PostInvoicingReportsDTO generatePostInvoicingReports(BillingRun billingRun)
			throws BusinessException {
		log.info("generatePostInvoicingReports billingRun="+billingRun.getId());
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

			if (invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0) {
				positiveInvoicesNumber++;
				positiveInvoicesAmountHT = positiveInvoicesAmountHT.add(invoice
						.getAmountWithoutTax());
				positiveInvoicesTaxAmount = positiveInvoicesTaxAmount.add(invoice.getAmountTax());
				positiveInvoicesAmount = positiveInvoicesAmount.add(invoice.getAmountWithTax());
			} else if (invoice.getAmountWithoutTax().equals(BigDecimal.ZERO)) {
				emptyInvoicesNumber++;
			} else {
				negativeInvoicesNumber++;
				negativeInvoicesAmountHT = negativeInvoicesAmountHT.add(invoice
						.getAmountWithoutTax());
				negativeInvoicesTaxAmount = negativeInvoicesTaxAmount.add(invoice.getAmountTax());
				negativeInvoicesAmount = negativeInvoicesAmount.add(invoice.getAmountWithTax());
			}

			if (invoice.getBillingAccount().getElectronicBilling()) {
				electronicInvoicesNumber++;
			}

			globalAmountHT = globalAmountHT.add(invoice.getAmountWithoutTax());
			globalAmountTTC = globalAmountTTC.add(invoice.getAmountWithTax());

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
								+ " set invoice=null,invoiceAgregateF=null,invoiceAgregateR=null,invoiceAgregateT=null where billingRun=:billingRun");
		queryTrans.setParameter("billingRun", billingRun);
		queryTrans.executeUpdate();

		Query queryAgregate = getEntityManager().createQuery(
				"delete from " + InvoiceAgregate.class.getName() + " where billingRun=:billingRun");
		queryAgregate.setParameter("billingRun", billingRun);
		queryAgregate.executeUpdate();

		Query queryInvoices = getEntityManager().createQuery(
				"delete from " + Invoice.class.getName() + " where billingRun=:billingRun");
		queryInvoices.setParameter("billingRun", billingRun);
		queryInvoices.executeUpdate();
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
			WalletOperation walletOperation =walletOperationService.findById(ratedTransaction.getWalletOperationId());
			walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE);
			walletOperationService.update(walletOperation);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<BillingRun> getbillingRuns(BillingRunStatusEnum ... status){
		BillingRunStatusEnum bRStatus;
		QueryBuilder qb = new QueryBuilder(BillingRun.class, "c");
		qb.startOrClause();
		for (int i = 0; i < status.length; i++){
			bRStatus=status[i];
			qb.addCriterionEnum("c.status", bRStatus);
		}
		qb.endOrClause();
		List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();
		return billingRuns;
         
	}
	
	
}
