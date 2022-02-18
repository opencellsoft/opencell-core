package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PAID;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.payments.ActionModeEnum.AUTOMATIC;
import static org.meveo.model.payments.ActionTypeEnum.RETRY_PAYMENT;
import static org.meveo.model.payments.ActionTypeEnum.SCRIPT;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.FAILED;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.SUCCESS;
import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelInstanceService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;

@Stateless
public class AutoRefundSecurityDepositJobBean extends BaseJobBean {

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private SecurityDepositService securityDepositService;

	@Inject
	private DunningCollectionPlanStatusService collectionPlanStatusService;

	@Inject
	private DunningLevelInstanceService levelInstanceService;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	private PaymentService paymentService;

	@Inject
	private PaymentGatewayService paymentGatewayService;

	@Inject
	private InvoiceService invoiceService;

	@EJB
	private AutoRefundSecurityDepositJobBean jobBean;

	private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		List<Long> securityDepositsToRefund = securityDepositService.getSecurityDepositsToRefundIds();
		jobExecutionResult.setNbItemsToProcess(securityDepositsToRefund.size());
		for (Long securityDepositId : securityDepositsToRefund) {
			SecurityDeposit securityDeposit = securityDepositService.findById(securityDepositId);
			if(securityDeposit == null) {
	            throw new EntityDoesNotExistsException("security deposit with id " + securityDepositId + " does not exist.");
	        }        
	        if(!SecurityDepositStatusEnum.LOCKED.equals(securityDeposit.getStatus()) 
	                && !SecurityDepositStatusEnum.UNLOCKED.equals(securityDeposit.getStatus())
	                && !SecurityDepositStatusEnum.HOLD.equals(securityDeposit.getStatus())){
	            throw new EntityDoesNotExistsException("The refund is possible ONLY if the status of the security deposit is at 'Locked' or 'Unlocked' or 'HOLD'");
	        } 
			try {
				jobBean.process(securityDepositId, jobExecutionResult);
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
	public void process(Long securityDepositId, JobExecutionResultImpl jobExecutionResult) {
		SecurityDeposit securityDeposit = securityDepositService.findById(securityDepositId);
		securityDepositService.refund(securityDeposit, "AUTO-REFUND", SecurityDepositOperationEnum.AUTO_REFUND_SECURITY_DEPOSIT, SecurityDepositStatusEnum.AUTO_REFUND);
	}

}