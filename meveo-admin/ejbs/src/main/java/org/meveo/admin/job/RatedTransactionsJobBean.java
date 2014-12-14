package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.model.admin.User;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class RatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobExecutionResult execute(JobExecutionResultImpl result,
			User currentUser) {
		Provider provider = currentUser.getProvider();

		try {
			// FIXME: only for postpaid wallets
			List<WalletOperation> walletOperations = walletOperationService
					.findByStatus(em, WalletOperationStatusEnum.OPEN, provider);
			log.info("WalletOperations to convert into rateTransactions={}",
					walletOperations.size());
			for (WalletOperation walletOperation : walletOperations) {
				try {
					RatedTransaction ratedTransaction = new RatedTransaction(
							walletOperation.getId(),
							walletOperation.getOperationDate(),
							walletOperation.getUnitAmountWithoutTax(),
							walletOperation.getUnitAmountWithTax(),
							walletOperation.getUnitAmountTax(),
							walletOperation.getQuantity(),
							walletOperation.getAmountWithoutTax(),
							walletOperation.getAmountWithTax(),
							walletOperation.getAmountTax(),
							RatedTransactionStatusEnum.OPEN,
							walletOperation.getProvider(),
							walletOperation.getWallet(), walletOperation
									.getWallet().getUserAccount()
									.getBillingAccount(), walletOperation
									.getChargeInstance().getChargeTemplate()
									.getInvoiceSubCategory(),
							walletOperation.getParameter1(),
							walletOperation.getParameter2(),
							walletOperation.getParameter3(), walletOperation.getUnityDescription());
					ratedTransactionService.create(em, ratedTransaction,
							currentUser, currentUser.getProvider());

					walletOperation
							.setStatus(WalletOperationStatusEnum.TREATED);
					walletOperation.updateAudit(currentUser);
				} catch (Exception e) {
					log.error(e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		result.close("");

		return result;
	}

}
