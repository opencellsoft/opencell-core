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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.*;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.payments.impl.*;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * The Class UnitSepaRejectedTransactionsJobBean.
 *
 * @author mboukayoua
 */

@Stateless
public class UnitSepaRejectedTransactionsJobBean {


	/** The log. */
	@Inject
	private Logger log;

	/** The dd request item service. */
	@Inject
	private DDRequestItemService ddRequestItemService;

	@Inject
	private DDRequestLOTService ddRequestLotService;

	/**
	 * Reject payment for a DD request item with "RJCT" as reject reason.
	 *
	 * @param fileName Original SEPA reject file name
	 * @param ddRequestItem list of dd request items to reject payments
	 * @param result  Job execution result
	 * @throws BusinessException BusinessException
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void rejectPaymentWithCauseRJCT(JobExecutionResultImpl result, String fileName, DDRequestItem ddRequestItem) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		ddRequestItem = ddRequestItemService.refreshOrRetrieve(ddRequestItem);
		if (!ddRequestItem.hasError()) {
			ddRequestLotService.rejectPayment(ddRequestItem, "RJCT", fileName);
			result.registerSucces();
		}
	}

	/**
	 * Reject payment for a DD request item. a reject reason is specified as a Map entry value
	 *
	 * @param fileName Original SEPA reject file name
	 * @param ddReqItemEntry A Map entry that contain item ID as key and reject reason code as value
	 * @param result Job execution result
	 * @return Future Set<Long> future with set of item's DD req lot ID
	 * @throws BusinessException BusinessException
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long rejectPaymentWithSpecificCause(JobExecutionResultImpl result, String fileName, Map.Entry<Long, String> ddReqItemEntry) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		Long ddReqItemID = ddReqItemEntry.getKey();
		String rejectionReason = ddReqItemEntry.getValue();

		DDRequestItem ddRequestItem = ddRequestItemService.findById(ddReqItemID, Arrays.asList("ddRequestLOT"));
		if (ddRequestItem == null) {
			throw new BusinessException("Cant find item by id:" + ddReqItemID);
		}
		ddRequestLotService.rejectPayment(ddRequestItem, rejectionReason, fileName);
		result.registerSucces();
		return ddRequestItem.getDdRequestLOT().getId();
	}

	/**
	 * Update DD requests lots status and file name based on DD Reject File infos
	 *
	 * @param ddRequestLotIds IDs of DD requests lots to update
	 * @param ddRejectFileInfos DD Reject File infos
	 * @throws BusinessException Business Exception
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateDDRequestLotsStatus(Set<Long> ddRequestLotIds, DDRejectFileInfos ddRejectFileInfos)
			throws BusinessException {

		for (Long ddRequestLotId : ddRequestLotIds) {
			DDRequestLOT ddRequestLOT = ddRequestLotService.findById(ddRequestLotId);
			ddRequestLOT.setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
			ddRequestLOT.setReturnFileName(ddRejectFileInfos.getFileName());

			ddRequestLotService.update(ddRequestLOT);
		}
	}

}