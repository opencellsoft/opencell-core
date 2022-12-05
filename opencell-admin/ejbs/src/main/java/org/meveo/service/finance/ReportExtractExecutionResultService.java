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

package org.meveo.service.finance;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.Created;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.service.base.PersistenceService;

/**
 * Service for managing ReportExtractExecutionResult.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 24 Apr 2018
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class ReportExtractExecutionResultService extends PersistenceService<ReportExtractExecutionResult> {

	@Inject
	@Created
	protected Event<ReportExtractExecutionResult> reportExtractExecutionResultEventProducer;

	/**
	 * Creates and commits a new ReportExtractExecutionResult in a new transaction.
	 * 
	 * @param reportExtractExecutionResult Result of ReportExtract execution.
	 * @throws BusinessException Business exception.
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createInNewTransaction(ReportExtractExecutionResult reportExtractExecutionResult) throws BusinessException {
		create(reportExtractExecutionResult);

		// fire a notification
		reportExtractExecutionResultEventProducer.fire(reportExtractExecutionResult);
	}

}
