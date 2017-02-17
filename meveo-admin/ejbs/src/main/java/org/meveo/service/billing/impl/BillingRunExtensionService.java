package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingRunExtensionService extends PersistenceService<BillingRun> {

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private InvoiceService invoiceService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BillingRun updateBRAmounts(BillingRun billingRun) {

		log.debug("updateBRAmounts for billingRun {}", billingRun.getId());

		billingRun = findById(billingRun.getId(), true);

		BillingCycle billingCycle = billingRun.getBillingCycle();

		Object[] ratedTransactionsAmounts = null;
		if (billingCycle != null) {
			Date startDate = billingRun.getStartDate();
			Date endDate = billingRun.getEndDate();

			if (startDate != null && endDate == null) {
				endDate = new Date();
			}

			if (startDate != null) {
				ratedTransactionsAmounts = (Object[]) getEntityManager().createNamedQuery("RatedTransaction.sumbillingRunByCycle")
						.setParameter("status", RatedTransactionStatusEnum.OPEN).setParameter("billingCycle", billingCycle).setParameter("startDate", startDate)
						.setParameter("endDate", endDate).setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
						.setParameter("provider", billingRun.getProvider()).getSingleResult();
			} else {
				ratedTransactionsAmounts = (Object[]) getEntityManager().createNamedQuery("RatedTransaction.sumbillingRunByCycleNoDate")
						.setParameter("status", RatedTransactionStatusEnum.OPEN).setParameter("billingCycle", billingCycle)
						.setParameter("lastTransactionDate", billingRun.getLastTransactionDate()).setParameter("provider", billingRun.getProvider()).getSingleResult();
			}

		} else {

			List<BillingAccount> bas = new ArrayList<BillingAccount>();
			String[] baIds = billingRun.getSelectedBillingAccounts().split(",");

			for (String id : Arrays.asList(baIds)) {
				bas.add(billingAccountService.findById(Long.valueOf(id)));
			}

			ratedTransactionsAmounts = (Object[]) getEntityManager().createNamedQuery("RatedTransaction.sumbillingRunByList")
					.setParameter("status", RatedTransactionStatusEnum.OPEN).setParameter("billingAccountList", bas)
					.setParameter("lastTransactionDate", billingRun.getLastTransactionDate()).getSingleResult();
		}

		if (ratedTransactionsAmounts != null) {
			billingRun.setPrAmountWithoutTax((BigDecimal) ratedTransactionsAmounts[0]);
			billingRun.setPrAmountWithTax((BigDecimal) ratedTransactionsAmounts[1]);
			billingRun.setPrAmountTax((BigDecimal) ratedTransactionsAmounts[2]);
		} else {
			billingRun.setPrAmountWithoutTax(BigDecimal.ZERO);
			billingRun.setPrAmountWithTax(BigDecimal.ZERO);
			billingRun.setPrAmountTax(BigDecimal.ZERO);
		}

		billingRun = updateNoCheck(billingRun);

		return billingRun;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BillingRun updateBillingRun(Long billingRunId, User currentUser, Integer sizeBA, Integer billableBA, BillingRunStatusEnum status, Date dateStatus)
			throws BusinessException {

		BillingRun billingRun = findById(billingRunId, true);

		if (billingRun == null) {
			throw new BusinessException("Cant find BillingRun with id:" + billingRunId);
		}
		if (sizeBA != null) {
			billingRun.setBillingAccountNumber(sizeBA);
		}
		if (billableBA != null) {
			billingRun.setBillableBillingAcountNumber(billableBA);
		}
		if (dateStatus != null) {
			billingRun.setProcessDate(dateStatus);
		}
		billingRun.setStatus(status);
		billingRun.updateAudit(currentUser);
		billingRun = updateNoCheck(billingRun);

		return billingRun;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void incrementInvoiceDatesAndValidate(BillingRun billingRun, User currentUser) throws BusinessException {
		log.debug("incrementInvoiceDatesAndValidate");
		for (Invoice invoice : billingRun.getInvoices()) {
			invoice.setInvoiceNumber(invoiceService.getInvoiceNumber(invoice, currentUser));
			invoice.setPdf(null);
			BillingAccount billingAccount = invoice.getBillingAccount();
			Date initCalendarDate = billingAccount.getSubscriptionDate();
			if (initCalendarDate == null) {
				initCalendarDate = billingAccount.getAuditable().getCreated();
			}
			Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate(initCalendarDate);
			billingAccount.setNextInvoiceDate(nextCalendarDate);
			billingAccount.updateAudit(currentUser);
			invoiceService.update(invoice, currentUser);
		}
		billingRun.setStatus(BillingRunStatusEnum.VALIDATED);
		update(billingRun, currentUser);
	}

}
