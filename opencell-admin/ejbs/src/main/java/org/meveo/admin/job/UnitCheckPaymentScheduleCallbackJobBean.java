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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.slf4j.Logger;

/**
 * @author anasseh
 **/
@Stateless
public class UnitCheckPaymentScheduleCallbackJobBean {

    @Inject
    private Logger log;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;
    
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    @Inject
    protected JobExecutionService jobExecutionService;
    
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long id) {

        try {
            RecordedInvoice recordedInvoice = recordedInvoiceService.findById(id);
            paymentScheduleInstanceItemService.checkPaymentRecordInvoice(recordedInvoice);
           

            jobExecutionService.registerSucces(result);

        } catch (Exception e) {
            log.error("Failed to generate acount operations", e);
            jobExecutionService.registerError(result, e.getMessage());
        }
    }
}