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

import org.meveo.event.qualifier.Updated;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Date;

/**
 * @author Mounir BOUKAYOUA
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UnitUpdateUnpaidInvoiceStatusJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    @Updated
    private Event<BaseEntity> entityUpdatedEventProducer;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long unpaidInvoiceId) {
        log.debug("update Invoice[id={}] status to unpaid", unpaidInvoiceId);
        try {
            Invoice invoice = invoiceService.findById(unpaidInvoiceId);
            invoice.setPaymentStatus(InvoicePaymentStatusEnum.UNPAID);
            invoice.setPaymentStatusDate(new Date());
            invoice = invoiceService.updateNoCheck(invoice);
            entityUpdatedEventProducer.fire(invoice);

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to update Invoice[id={}] status to unpaid", unpaidInvoiceId, e);
            result.registerError(e.getMessage());
        }
    }
}
