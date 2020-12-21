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
package org.meveo.admin.async;

import org.jfree.util.Log;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.UnitSepaRejectedTransactionsJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.service.job.JobExecutionService;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.Future;

/**
 * The Class SepaRejectedTransactionsAsync
 *
 * @author mboukayoua
 */

@Stateless
public class SepaRejectedTransactionsAsync {

	/** The job execution service. */
	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private UnitSepaRejectedTransactionsJobBean unitSepaRejectedTransactionsJobBean;
	
	/**
	 * Create payments for all items from the ddRequestLot. One Item at a time in a
	 * separate transaction.
	 *
	 * @param ddRequestItems the dd request items
	 * @param result         Job execution result
	 * @return Future String
	 * @throws BusinessException BusinessException
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<String> launchAndForgetPaymentsRejectionsWithCauseRJCT(String fileName, List<DDRequestItem> ddRequestItems, JobExecutionResultImpl result) throws BusinessException {
		for (DDRequestItem ddRequestItem : ddRequestItems) {

			if (result != null && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
				break;
			}
			try {
				unitSepaRejectedTransactionsJobBean.rejectPaymentWithCauseRJCT(result, fileName, ddRequestItem);
			} catch (Exception e) {
				Log.warn("Error on launchAndForgetPaymentRejection", e);
				if(result != null) {
					result.registerError(e.getMessage());
				}
			}
		}
		return new AsyncResult<>("OK");
	}

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<Set<Long>> launchAndForgetPaymentsRejectionsWithSpecificCause(String fileName, List<Map.Entry<Long, String>> ddReqItemEntries, JobExecutionResultImpl result) throws BusinessException {
		Set<Long> ddRequestLotIds = new HashSet<>();
		for (Map.Entry<Long, String> ddReqItemEntry : ddReqItemEntries) {
			if (result != null && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
				break;
			}
			try {
				Long ddRequestLotId = unitSepaRejectedTransactionsJobBean.rejectPaymentWithSpecificCause(result, fileName, ddReqItemEntry);
				ddRequestLotIds.add(ddRequestLotId);
			} catch (Exception e) {
				Log.warn("Error on launchAndForgetPaymentRejection", e);
				if(result != null) {
					result.registerError(e.getMessage());
				}
			}
		}
		return new AsyncResult<>(ddRequestLotIds);
	}
}
