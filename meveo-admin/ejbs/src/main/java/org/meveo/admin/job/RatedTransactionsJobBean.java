package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
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
import org.slf4j.Logger;

@Stateless
public class RatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobExecutionResult execute(JobExecutionResultImpl result, User currentUser) {
		Provider provider = currentUser.getProvider();

		try {
			// FIXME: only for postpaid wallets
			List<WalletOperation> walletOperations = walletOperationService.findByStatus(
					WalletOperationStatusEnum.OPEN, provider);

			log.info("WalletOperations to convert into rateTransactions={}", walletOperations.size());

			for (WalletOperation walletOperation : walletOperations) {
				try {
					BigDecimal amountWithTAx = walletOperation.getAmountWithTax();
					BigDecimal amountTax = walletOperation.getAmountTax();
					BigDecimal unitAmountWithTax = walletOperation.getUnitAmountWithTax();
					BigDecimal unitAmountTax = walletOperation.getUnitAmountTax();

					if (walletOperation.getChargeInstance().getSubscription().getUserAccount().getBillingAccount()
							.getCustomerAccount().getCustomer().getCustomerCategory().getExoneratedFromTaxes()) {
						amountWithTAx = walletOperation.getAmountWithoutTax();
						amountTax = BigDecimal.ZERO;
						unitAmountWithTax = walletOperation.getUnitAmountWithoutTax();
						unitAmountTax = BigDecimal.ZERO;
					}
					RatedTransaction ratedTransaction = new RatedTransaction(walletOperation.getId(),
							walletOperation.getOperationDate(), walletOperation.getUnitAmountWithoutTax(),
							unitAmountWithTax, unitAmountTax, walletOperation.getQuantity(),
							walletOperation.getAmountWithoutTax(), amountWithTAx, amountTax,
							RatedTransactionStatusEnum.OPEN, walletOperation.getProvider(),
							walletOperation.getWallet(), walletOperation.getWallet().getUserAccount()
									.getBillingAccount(), walletOperation.getChargeInstance().getChargeTemplate()
									.getInvoiceSubCategory(), walletOperation.getParameter1(),
							walletOperation.getParameter2(), walletOperation.getParameter3(),
							walletOperation.getUnityDescription(), walletOperation.getPriceplan(),
							walletOperation.getOfferCode());
					ratedTransactionService.create(ratedTransaction, currentUser, currentUser.getProvider());

					walletOperation.setStatus(WalletOperationStatusEnum.TREATED);
					walletOperation.updateAudit(currentUser);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		result.close("");

		return result;
	}

}
