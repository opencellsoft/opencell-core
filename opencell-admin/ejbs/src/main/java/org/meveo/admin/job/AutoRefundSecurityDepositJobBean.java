package org.meveo.admin.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelInstanceService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;

@Stateless
public class AutoRefundSecurityDepositJobBean extends BaseJobBean {


	@Inject
	private SecurityDepositService securityDepositService;

	@Inject
	private FinanceSettingsService financeSettingsService;

	@EJB
	private AutoRefundSecurityDepositJobBean jobBean;


	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		
		FinanceSettings financeSettings = financeSettingsService.findLastOne();
        if (financeSettings != null && !financeSettings.isAutoRefund()) {
            throw new BadRequestException("Auto refund is not allowed in general settings");
        }
		List<Long> securityDeposits = securityDepositService.getSecurityDepositsToRefundIds();
		List<SecurityDeposit> securityDepositsToRefund = securityDepositService.checkPeriod(securityDeposits);
		jobExecutionResult.setNbItemsToProcess(securityDepositsToRefund.size());
		for (SecurityDeposit securityDeposit : securityDepositsToRefund) {
			try {
				jobBean.process(securityDeposit, jobExecutionResult);
			} catch (Exception exception) {
				jobExecutionResult.addErrorReport(exception.getMessage());
			}
		}
		jobExecutionResult.setNbItemsCorrectlyProcessed(
				securityDepositsToRefund.size() - jobExecutionResult.getNbItemsProcessedWithError());
	}


	/**
	 * Process collection plans
	 *
	 * @param collectionPlanId   Collection plan id to process
	 * @param jobExecutionResult Job execution result
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void process(SecurityDeposit securityDeposit, JobExecutionResultImpl jobExecutionResult) {
		securityDepositService.refund(securityDeposit, "AUTO-REFUND", SecurityDepositOperationEnum.AUTO_REFUND_SECURITY_DEPOSIT, SecurityDepositStatusEnum.AUTO_REFUND, "AUTO-REFUND");
	}

}