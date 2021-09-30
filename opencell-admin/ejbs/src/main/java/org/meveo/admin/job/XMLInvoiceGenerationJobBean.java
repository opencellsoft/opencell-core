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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.slf4j.Logger;

@Stateless
public class XMLInvoiceGenerationJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoicingAsync invoicingAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String parameter, JobInstance jobInstance) {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {

            InvoicesToProcessEnum invoicesToProcessEnum = InvoicesToProcessEnum.valueOf((String) this.getParamOrCFValue(jobInstance, "invoicesToProcess", "FinalOnly"));
            List<Long> invoiceIds = new ArrayList<>();

            List<Long> billingRunsIds = getBillingRunsIds(this.getParamOrCFValue(jobInstance, "billingRuns"));
            for (Long billingRunId : billingRunsIds) {
                invoiceIds.addAll(this.fetchInvoiceIdsToProcess(invoicesToProcessEnum, billingRunId));
            }

            log.info("invoices to process={}", invoiceIds.size());
            List<Future<Boolean>> futures = new ArrayList<>();
            SubListCreator subListCreator = new SubListCreator(invoiceIds, nbRuns.intValue());
            result.setNbItemsToProcess(subListCreator.getListSize());

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(invoicingAsync.generateXmlAsync((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<Boolean> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

        } catch (Exception e) {
            log.error("Failed to generate XML invoices", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }

    private List<Long> fetchInvoiceIdsToProcess(InvoicesToProcessEnum invoicesToProcessEnum, Long billingRunId) {

        log.debug(" fetchInvoiceIdsToProcess for invoicesToProcessEnum = {} and billingRunId = {} ", invoicesToProcessEnum, billingRunId);
        List<Long> invoiceIds = null;

        switch (invoicesToProcessEnum) {
        case FinalOnly:
            invoiceIds = invoiceService.getInvoiceIdsByBRWithNoXml(billingRunId);
            break;

        case DraftOnly:
            invoiceIds = invoiceService.getDraftInvoiceIdsByBRWithNoXml(billingRunId);
            break;

        case All:
            invoiceIds = invoiceService.getInvoiceIdsIncludeDraftByBRWithNoXml(billingRunId);
            break;
        }
        return invoiceIds;
    }

    /**
     * Get Billing runs Ids to process
     *
     * @param billingRunsCF the billing runs getting from the custom field
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Long> getBillingRunsIds(Object billingRunsCF) {
        List<EntityReferenceWrapper> brList = (List<EntityReferenceWrapper>) billingRunsCF;
        if (brList != null && !brList.isEmpty()) {
            return brList.stream().map(br -> {
                String compositeCode = br.getCode();
                if (compositeCode == null) {
                    return null;
                }
                return Long.valueOf(compositeCode.split("/")[0]);
            }).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}