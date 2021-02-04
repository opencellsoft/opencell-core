/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.PaymentAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.meveo.service.script.payment.DateRangeScript;
import org.slf4j.Logger;

/**
 * The Class PaymentJobBean, PaymentJob implementation.
 * 
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
@Stateless
public class PaymentJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private AccountOperationService accountOperationService;

	@Inject
	private PaymentAsync paymentAsync;

	@Inject
	private PaymentGatewayService paymentGatewayService;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;
	
	@Inject
    protected JobExecutionService jobExecutionService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running with parameter={}", jobInstance.getParametres());

		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}
		jobExecutionService.counterRunningThreads(result, nbRuns);
		Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

		try {
			boolean createAO = true;
			boolean matchingAO = true;
			Date fromDueDate = null;
			Date toDueDate = null;

			OperationCategoryEnum operationCategory = OperationCategoryEnum.CREDIT;
			PaymentMethodEnum paymentMethodType = PaymentMethodEnum.CARD;
			PaymentOrRefundEnum paymentOrRefundEnum = PaymentOrRefundEnum.PAYMENT;

			PaymentGateway paymentGateway = null;
			if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway") != null) {
				paymentGateway = paymentGatewayService.findByCode(((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway")).getCode());
			}
			try {
				operationCategory = OperationCategoryEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "PaymentJob_creditOrDebit")).toUpperCase());
				paymentMethodType = PaymentMethodEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "PaymentJob_cardOrDD")).toUpperCase());

				createAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_createAO"));
				matchingAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_matchingAO"));

				if (operationCategory == OperationCategoryEnum.DEBIT) {
					paymentOrRefundEnum = PaymentOrRefundEnum.REFUND;
				}

				DateRangeScript dateRangeScript = this.getDueDateRangeScript(jobInstance);
				if (dateRangeScript != null) {
					DateRange dueDateRange = dateRangeScript.computeDateRange(new HashMap<>()); // no addtional params are needed right now for computeDateRange, may be in the
																								// future.
					fromDueDate = dueDateRange.getFrom();
					toDueDate = dueDateRange.getTo();
				} else {
					fromDueDate = (Date) this.getParamOrCFValue(jobInstance, "PaymentJob_fromDueDate");
					toDueDate = (Date) this.getParamOrCFValue(jobInstance, "PaymentJob_toDueDate");
				}

			} catch (Exception e) {
				log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
			}

			if (fromDueDate == null) {
				fromDueDate = new Date(1);
			}
			if (toDueDate == null) {
				toDueDate = DateUtils.addYearsToDate(fromDueDate, 1000);
			}

			List<AccountOperation> aos = new ArrayList<AccountOperation>();

			AccountOperationFilterScript aoFilterScript = getAOScriptInstance(jobInstance);

			if (aoFilterScript == null) {
				log.info("native query used");
				aos = accountOperationService.getAOsToPayOrRefund(paymentMethodType, fromDueDate, toDueDate, paymentOrRefundEnum.getOperationCategoryToProcess(), null);
			} else {
				log.info("custom query used");
				Map<String, Object> methodContext = new HashMap<>();
				methodContext.put(AccountOperationFilterScript.FROM_DUE_DATE, fromDueDate);
				methodContext.put(AccountOperationFilterScript.TO_DUE_DATE, toDueDate);
				methodContext.put(AccountOperationFilterScript.PAYMENT_METHOD, paymentMethodType);
				methodContext.put(AccountOperationFilterScript.CAT_TO_PROCESS, paymentOrRefundEnum.getOperationCategoryToProcess());				
				
				aos = aoFilterScript.filterAoToPay(methodContext);				
			}

			log.debug("nb aos for payment/refund:" + aos.size());

			result.setNbItemsToProcess(aos.size());
			jobExecutionService.initCounterElementsRemaining(result, aos.size());

			List<Future<String>> futures = new ArrayList<Future<String>>();
			SubListCreator subListCreator = new SubListCreator(aos, nbRuns.intValue());
			log.debug("block to run:" + subListCreator.getBlocToRun());
			log.debug("nbThreads:" + nbRuns);
			MeveoUser lastCurrentUser = currentUser.unProxy();

			while (subListCreator.isHasNext()) {
				futures.add(paymentAsync.launchAndForget((List<AccountOperation>) subListCreator.getNextWorkSet(), result, createAO, matchingAO, paymentGateway, operationCategory,
						paymentMethodType, lastCurrentUser, fromDueDate, toDueDate, aoFilterScript));
				if (subListCreator.isHasNext()) {
					try {
						Thread.sleep(waitingMillis.longValue());
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
			}
			// Wait for all async methods to finish
			for (Future<String> future : futures) {
				try {
					future.get();

				} catch (InterruptedException e) {
					// It was cancelled from outside - no interest

				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					jobExecutionService.registerError(result, cause.getMessage());
					result.addReport(cause.getMessage());
					log.error("Failed to execute async method", cause);
				}
			}
		} catch (Exception e) {
			log.error("Failed to run usage rating job", e);
			jobExecutionService.registerError(result, e.getMessage());
			result.addReport(e.getMessage());
		}
	}

	private AccountOperationFilterScript getAOScriptInstance(JobInstance jobInstance) {
		return (AccountOperationFilterScript) this.getJobScriptByCfCode(jobInstance, "PaymentJob_aoFilterScript", AccountOperationFilterScript.class);
	}

	private DateRangeScript getDueDateRangeScript(JobInstance jobInstance) {
		return (DateRangeScript) this.getJobScriptByCfCode(jobInstance, "PaymentJob_dueDateRangeScript", DateRangeScript.class);
	}

	@SuppressWarnings("rawtypes")
	private ScriptInterface getJobScriptByCfCode(JobInstance jobInstance, String scriptCfCode, Class clazz) {
		try {
			EntityReferenceWrapper entityReferenceWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, scriptCfCode);
			if (entityReferenceWrapper != null) {
				final String scriptCode = entityReferenceWrapper.getCode();
				if (scriptCode != null) {
					log.debug(" looking for ScriptInstance with code :  [{}] ", scriptCode);
					ScriptInterface si = scriptInstanceService.getScriptInstance(scriptCode);
					if (si != null && clazz.isInstance(si)) {
						return si;
					}
				}
			}
		} catch (Exception e) {
			log.error(" Error on getJobScriptByCfCode : [{}]", e.getMessage());
		}
		return null;
	}

}