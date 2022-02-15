package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PAID;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionModeEnum.AUTOMATIC;
import static org.meveo.model.payments.ActionTypeEnum.RETRY_PAYMENT;
import static org.meveo.model.payments.ActionTypeEnum.SCRIPT;
import static org.meveo.model.payments.ActionTypeEnum.SEND_NOTIFICATION;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.FAILED;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.SUCCESS;
import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningActionInstanceService;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelInstanceService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class AutoRefundSecurityDepositJobBean extends BaseJobBean {

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private DunningCollectionPlanService collectionPlanService;

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

	@Inject
	private DunningActionInstanceService actionInstanceService;

	@EJB
	private AutoRefundSecurityDepositJobBean jobBean;

	private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		List<Long> collectionPlanToProcess = collectionPlanService.getActiveCollectionPlansIds();
		jobExecutionResult.setNbItemsToProcess(collectionPlanToProcess.size());
		for (Long collectionPlanId : collectionPlanToProcess) {
			try {
				jobBean.process(collectionPlanId, jobExecutionResult);
			} catch (Exception exception) {
				jobExecutionResult.addErrorReport(exception.getMessage());
			}
		}
		jobExecutionResult.setNbItemsCorrectlyProcessed(
				collectionPlanToProcess.size() - jobExecutionResult.getNbItemsProcessedWithError());
	}

	private void doRefund(PaymentMethod preferredPaymentMethod, CustomerAccount customerAccount, long amountToPay,
			List<Long> accountOperationsToPayIds, PaymentGateway paymentGateway) {
		if (preferredPaymentMethod.getPaymentType().equals(DIRECTDEBIT)
				|| preferredPaymentMethod.getPaymentType().equals(CARD)) {
			try {
				if (accountOperationsToPayIds != null && !accountOperationsToPayIds.isEmpty()) {
					if (preferredPaymentMethod.getPaymentType().equals(CARD)) {
						if (preferredPaymentMethod instanceof HibernateProxy) {
							preferredPaymentMethod = (PaymentMethod) ((HibernateProxy) preferredPaymentMethod)
									.getHibernateLazyInitializer().getImplementation();
						}
						CardPaymentMethod paymentMethod = (CardPaymentMethod) preferredPaymentMethod;
						paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds, true, true,
								paymentGateway, paymentMethod.getCardNumber(), paymentMethod.getCardNumber(),
								paymentMethod.getHiddenCardNumber(), paymentMethod.getExpirationMonthAndYear(),
								paymentMethod.getCardType(), true, preferredPaymentMethod.getPaymentType());
					} else {
						paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds, true, true,
								paymentGateway, null, null, null, null, null, true,
								preferredPaymentMethod.getPaymentType());
					}
				}
			} catch (Exception exception) {
				throw new BusinessException(
						"Error occurred during payment process for customer " + customerAccount.getCode(), exception);
			}
		}
	}

	/**
	 * Process collection plans
	 *
	 * @param collectionPlanId   Collection plan id to process
	 * @param jobExecutionResult Job execution result
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void process(Long collectionPlanId, JobExecutionResultImpl jobExecutionResult) {
		DunningCollectionPlan collectionPlan = collectionPlanService.findById(collectionPlanId);
		Date dateToCompare;
		Date today = new Date();
		int index = 0;
		int nextLevel = 0;
		String lastAction = "";
		String nextAction = "";
		boolean updateCollectionPlan = false;
		if (collectionPlan.getDunningLevelInstances() == null || collectionPlan.getDunningLevelInstances().isEmpty()) {
			throw new BusinessException(
					"Collection plan ID : " + collectionPlan.getId() + " has no levelInstances associated");
		}
		for (DunningLevelInstance levelInstance : collectionPlan.getDunningLevelInstances()) {
			dateToCompare = DateUtils.addDaysToDate(collectionPlan.getStartDate(),
					ofNullable(collectionPlan.getPauseDuration()).orElse(0) + levelInstance.getDaysOverdue());
			if (levelInstance.getLevelStatus() != DunningLevelInstanceStatusEnum.DONE
					&& !collectionPlan.getRelatedInvoice().getPaymentStatus().equals(PAID)
					&& today.after(dateToCompare)) {
				nextLevel = index + 1;
				for (int i = 0; i < levelInstance.getActions().size(); i++) {
					DunningActionInstance actionInstance = levelInstance.getActions().get(i);
					if (actionInstance.getActionMode().equals(AUTOMATIC)
							&& actionInstance.getActionStatus().equals(TO_BE_DONE)) {
						if (actionInstance.getActionType().equals(SCRIPT)) {
							if (actionInstance.getDunningAction() != null) {
								scriptInstanceService.execute(
										actionInstance.getDunningAction().getScriptInstance().getCode(),
										new HashMap<>());
							}
						}
						if (actionInstance.getActionType().equals(RETRY_PAYMENT)) {
							BillingAccount billingAccount = collectionPlan.getBillingAccount();
							if (billingAccount != null && billingAccount.getCustomerAccount() != null
									&& billingAccount.getCustomerAccount().getPaymentMethods() != null) {
								PaymentMethod preferredPaymentMethod = billingAccount.getCustomerAccount()
										.getPaymentMethods().stream().filter(PaymentMethod::isPreferred).findFirst()
										.orElseThrow(() -> new BusinessException(
												"No preferred payment method found for customer account"
														+ billingAccount.getCustomerAccount().getCode()));
								CustomerAccount customerAccount = billingAccount.getCustomerAccount();
								// PaymentService.doPayment consider amount to pay in cent so amount should be *
								// 100
								long amountToPay = collectionPlan.getRelatedInvoice().getNetToPay().longValue() * 100;
								Invoice invoice = collectionPlan.getRelatedInvoice();
								if (invoice.getRecordedInvoice() == null) {
									throw new BusinessException("No getRecordedInvoice for the invoice "
											+ invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber()
													: invoice.getTemporaryInvoiceNumber());
								}
								PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount,
										preferredPaymentMethod, null);
								doRefund(preferredPaymentMethod, customerAccount, amountToPay,
										asList(invoice.getRecordedInvoice().getId()), paymentGateway);
							}
						}
						actionInstance.setActionStatus(DunningActionInstanceStatusEnum.DONE);
						if (levelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.TO_BE_DONE) {
							levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
							levelInstanceService.update(levelInstance);
						}
						lastAction = actionInstance.getCode();
						if (i + 1 < levelInstance.getActions().size()) {
							nextAction = levelInstance.getActions().get(i + 1).getCode();
						}
					}
					actionInstanceService.update(actionInstance);
				}
				collectionPlan.setLastActionDate(new Date());
				collectionPlan.setLastAction(lastAction);
				collectionPlan.setNextAction(nextAction);
				updateCollectionPlan = true;
				levelInstance = levelInstanceService.refreshOrRetrieve(levelInstance);
				if (nextLevel < collectionPlan.getDunningLevelInstances().size()) {
					collectionPlan.setCurrentDunningLevelSequence(
							collectionPlan.getDunningLevelInstances().get(nextLevel).getSequence());
				}
				if (levelInstance.getDunningLevel() != null && levelInstance.getDunningLevel().isEndOfDunningLevel()
						&& collectionPlan.getRelatedInvoice().getPaymentStatus()
								.equals(InvoicePaymentStatusEnum.UNPAID)) {
					collectionPlan.setStatus(collectionPlanStatusService.findByStatus(FAILED));
				}
				if (collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)) {
					collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
				}
				long countActions = levelInstance.getActions().stream()
						.filter(action -> action.getActionStatus() == DONE).count();
				if (countActions > 0 && countActions < levelInstance.getActions().size()) {
					levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
				}
				if (countActions == levelInstance.getActions().size()) {
					levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
				}
			}
			if (levelInstance.getDunningLevel() == null) {
				throw new BusinessException(
						"No dunning level associated to level instance id " + levelInstance.getId());
			} else {
				levelInstanceService.update(levelInstance);
			}
			index++;
		}
		if (updateCollectionPlan) {
			collectionPlanService.update(collectionPlan);
		}
	}

}