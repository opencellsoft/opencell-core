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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * Job implementation to check if payment was rejected or not
 * 
 * @author anasseh
 * @author Andrius Karpavicius
 **/
@Stateless
public class CheckPaymentScheduleCallbackJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 3912010469445989038L;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::checkPaymentStatus, null, null, JobSpeedEnum.NORMAL);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of invoice Ids to check payment status for
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        List<Long> ids = recordedInvoiceService.queryInvoiceIdsForPS();

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Check payment status
     * 
     * @param invoiceId Invoice id
     * @param jobExecutionResult Job execution result
     */
    private void checkPaymentStatus(Long invoiceId, JobExecutionResultImpl jobExecutionResult) {

        RecordedInvoice recordedInvoice = recordedInvoiceService.findById(invoiceId);
        paymentScheduleInstanceItemService.checkPaymentRecordInvoice(recordedInvoice);
    }
}