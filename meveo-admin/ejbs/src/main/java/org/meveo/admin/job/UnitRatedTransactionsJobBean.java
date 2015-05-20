package org.meveo.admin.job;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class UnitRatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser,Long walletOperationId ) {
		try {
			WalletOperation walletOperation = walletOperationService.findById(walletOperationId) ;

			BigDecimal amountWithTAx = walletOperation.getAmountWithTax();
			BigDecimal amountTax = walletOperation.getAmountTax();
			BigDecimal unitAmountWithTax = walletOperation.getUnitAmountWithTax();
			BigDecimal unitAmountTax = walletOperation.getUnitAmountTax();

			/*if (walletOperation.getChargeInstance().getSubscription().getUserAccount().getBillingAccount()
							.getCustomerAccount().getCustomer().getCustomerCategory().getExoneratedFromTaxes()) {
						amountWithTAx = walletOperation.getAmountWithoutTax();
						amountTax = BigDecimal.ZERO;
						unitAmountWithTax = walletOperation.getUnitAmountWithoutTax();
						unitAmountTax = BigDecimal.ZERO;
					}*/
			RatedTransaction ratedTransaction = new RatedTransaction(walletOperation.getId(),
					walletOperation.getOperationDate(), walletOperation.getUnitAmountWithoutTax(),
					unitAmountWithTax, unitAmountTax, walletOperation.getQuantity(),
					walletOperation.getAmountWithoutTax(), amountWithTAx, amountTax,
					RatedTransactionStatusEnum.OPEN, walletOperation.getProvider(),
					walletOperation.getWallet(), walletOperation.getWallet().getUserAccount()
					.getBillingAccount(), walletOperation.getChargeInstance().getChargeTemplate()
					.getInvoiceSubCategory(), walletOperation.getParameter1(),
					walletOperation.getParameter2(), walletOperation.getParameter3(),
					walletOperation.getRatingUnitDescription(), walletOperation.getPriceplan(),
					walletOperation.getOfferCode());
			ratedTransactionService.create(ratedTransaction, currentUser, currentUser.getProvider());

			walletOperation.setStatus(WalletOperationStatusEnum.TREATED);
			walletOperation.updateAudit(currentUser);
			result.registerSucces();
		} catch (Exception e) {
			log.error("Failed to rate transaction for wallet operation {}", walletOperationId, e);
			result.registerError(e.getMessage());
		}
	}
}
