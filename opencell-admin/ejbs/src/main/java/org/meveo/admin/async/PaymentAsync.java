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

/**
 * 
 */
package org.meveo.admin.async;

import static org.meveo.service.script.payment.AccountOperationFilterScript.LIST_AO_TO_PAY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.job.UnitPaymentJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PaymentAsync.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 9.3.1
 */

@Stateless
public class PaymentAsync {

	/** The unit payment job bean. */
	@Inject
	private UnitPaymentJobBean unitPaymentJobBean;

	/** The JobExecution service. */
	@Inject
	private JobExecutionService jobExecutionService;

	/** The current user provider. */
	@Inject
	private CurrentUserProvider currentUserProvider;

	/** The account operation service. */
	@Inject
	private AccountOperationService accountOperationService;

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Process card payments for a list of given account operation ids. One account
	 * operation at a time in a separate transaction.
	 *
	 * @param caIds             List of customerAccount ids
	 * @param result            Job execution result
	 * @param createAO          True/ false to create account operation
	 * @param matchingAO        Matching account operation
	 * @param paymentGateway    PaymentGateway to use
	 * @param operationCategory Operation category.
	 * @param paymentMethodType Payment method type to use
	 * @param lastCurrentUser   Current user. In case of multitenancy, when user
	 *                          authentication is forced as result of a fired
	 *                          trigger (scheduled jobs, other timed event
	 *                          expirations), current user might be lost, thus there
	 *                          is a need to reestablish.
	 * @param paymentPerAOorCA  make payment for each AO or all AO for each CA
	 * @param fromDueDate       the from due date
	 * @param toDueDate         the to due date
	 * @param aoFilterScript    custom script to use in order to filter AOs to pay
	 *                          or refund
	 * @return future result
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<String> launchAndForget(List<Long> caIds, JobExecutionResultImpl result, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
			OperationCategoryEnum operationCategory, PaymentMethodEnum paymentMethodType, MeveoUser lastCurrentUser, String paymentPerAOorCA, Date fromDueDate, Date toDueDate,
			AccountOperationFilterScript aoFilterScript) { // TODO : nbr of method arguments is disturbing , refactor it by using a
															// dedicated bean/dto

		currentUserProvider.reestablishAuthentication(lastCurrentUser);
		BigDecimal oneHundred = new BigDecimal("100");
		int i = 0;
		for (Long caID : caIds) {
			i++;
			if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
				break;
			}

			List<AccountOperation> listAoToPayOrRefund = null;
			if (operationCategory == OperationCategoryEnum.CREDIT) {
				List<AccountOperation> listAoToPay = accountOperationService.getAOsToPayOrRefund(paymentMethodType, fromDueDate, toDueDate, OperationCategoryEnum.DEBIT, caID);
				log.info("listAoToPay size before filter :" + (listAoToPay == null ? "null" : listAoToPay.size()));
				listAoToPayOrRefund = this.filterAoToPayOrRefund(aoFilterScript, listAoToPay, paymentMethodType, OperationCategoryEnum.DEBIT);
				log.info("listAoToPay size after filter :" + (listAoToPayOrRefund == null ? "null" : listAoToPayOrRefund.size()));
			} else {
				List<AccountOperation> listAoToRefund = accountOperationService.getAOsToPayOrRefund(paymentMethodType, fromDueDate, toDueDate, OperationCategoryEnum.CREDIT, caID);
				log.info("listAoToRefund size before filter :" + (listAoToRefund == null ? "null" : listAoToRefund.size()));
				listAoToPayOrRefund = this.filterAoToPayOrRefund(aoFilterScript, listAoToRefund, paymentMethodType, OperationCategoryEnum.CREDIT);
				log.info("listAoToRefund size after filter :" + (listAoToPayOrRefund == null ? "null" : listAoToPayOrRefund.size()));
			}
			if ("CA".equals(paymentPerAOorCA)) {
				List<Long> aoIds = new ArrayList<Long>();
				BigDecimal amountToPay = BigDecimal.ZERO;
				for (AccountOperation ao : listAoToPayOrRefund) {
					aoIds.add(ao.getId());
					amountToPay = amountToPay.add(ao.getUnMatchingAmount());
				}
				if (amountToPay.compareTo(BigDecimal.ZERO) != 0) {
					unitPaymentJobBean.execute(result, caID, aoIds, amountToPay.multiply(oneHundred).longValue(), createAO, matchingAO, operationCategory, paymentGateway,
							paymentMethodType, aoFilterScript);
				}
			} else {
				for (AccountOperation ao : listAoToPayOrRefund) {
					if (ao.getUnMatchingAmount().compareTo(BigDecimal.ZERO) != 0) {
						List<Long> aoIds = new ArrayList<Long>();
						aoIds.add(ao.getId());
						unitPaymentJobBean.execute(result, caID, aoIds, ao.getUnMatchingAmount().multiply(oneHundred).longValue(), createAO, matchingAO, operationCategory,
								paymentGateway, paymentMethodType, aoFilterScript);
					}
				}
			}
		}
		return new AsyncResult<String>("OK");
	}

	private List<AccountOperation> filterAoToPayOrRefund(AccountOperationFilterScript aoFilterScript, List<AccountOperation> listAoToPay, PaymentMethodEnum paymentMethodType,
			OperationCategoryEnum aoCategory) {
		if (aoFilterScript != null) {
			Map<String, Object> methodContext = new HashMap<>();
			methodContext.put(LIST_AO_TO_PAY, listAoToPay);
			List<AccountOperation> filteredAOs = aoFilterScript.filterAoToPay(methodContext);
			if (CollectionUtils.isNotEmpty(filteredAOs)) {
				return filteredAOs.stream().filter((ao) -> (ao.getPaymentMethod() == paymentMethodType && ao.getTransactionCategory() == aoCategory
						&& (ao.getMatchingStatus() == MatchingStatusEnum.O || ao.getMatchingStatus() == MatchingStatusEnum.P))).collect(Collectors.toList());
			} else {
				return new ArrayList<AccountOperation>();
			}
		}
		return listAoToPay;
	}
}
