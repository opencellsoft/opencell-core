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

import static org.meveo.model.billing.BillingRunStatusEnum.NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.POSTVALIDATED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREINVOICED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREVALIDATED;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * TODO add javadoc
 */
@Stateless
public class BillingRunJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    protected ResourceBundle resourceMessages;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    @SuppressWarnings("unchecked")
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        List<EntityReferenceWrapper> billingCyclesCf = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "BillingRunJob_billingCycle");
        Date lastTransactionDateFromCF = (Date) this.getParamOrCFValue(jobInstance, "BillingRunJob_lastTransactionDate");
        Date invoiceDateFromCF = (Date) this.getParamOrCFValue(jobInstance, "BillingRunJob_invoiceDate");
        String billingCycleTypeId = (String) this.getParamOrCFValue(jobInstance, "BillingRunJob_billingRun_Process");

        List<String> billingCyclesCode = Collections.emptyList();
        if (billingCyclesCf != null) {
            billingCyclesCode = billingCyclesCf.stream().map(EntityReferenceWrapper::getCode).collect(Collectors.toList());
        }
        log.debug("Creating Billing Runs for billingCycles ={} with invoiceDate = {} and lastTransactionDate={}", billingCyclesCode, invoiceDateFromCF, lastTransactionDateFromCF);

        try {
            int nbItemsToProcess = 0;
            int nbItemsProcessedWithError = 0;
            BillingProcessTypesEnum billingCycleType = BillingProcessTypesEnum.FULL_AUTOMATIC;
            if (billingCycleTypeId != null) {
                billingCycleType = BillingProcessTypesEnum.getValue(Integer.valueOf(billingCycleTypeId));
            }
            ParamBean param = paramBeanFactory.getInstance();
            boolean isAllowed = param.getBooleanValue("billingRun.allowManyInvoicing", true);
            log.info("launchInvoicing allowManyInvoicing={}", isAllowed);
            for (String billingCycleCode : billingCyclesCode) {
                List<BillingRun> billruns = billingRunService.getBillingRuns(billingCycleCode, POSTVALIDATED, NEW, PREVALIDATED, PREINVOICED);
                boolean alreadyLaunched = billruns != null && billruns.size() > 0;
                if (alreadyLaunched && !isAllowed) {
                    log.warn("Not allowed to launch many invoicing for the billingCycle = {}", billingCycleCode);
                    jobExecutionService.registerError(result, resourceMessages.getString("error.invoicing.alreadyLunched"));
                    result.setNbItemsProcessedWithError(++nbItemsProcessedWithError);
                    continue;
                }

                BillingCycle billingCycle = billingCycleService.findByCode(billingCycleCode);

                if (billingCycle == null) {
                    jobExecutionService.registerError(result, "Cannot create a biling run with billing cycle '" + billingCycleCode);
                    result.setNbItemsProcessedWithError(++nbItemsProcessedWithError);
                    continue;
                }

                BillingRun billingRun = new BillingRun();
                billingRun.setBillingCycle(billingCycle);
                billingRun.setProcessDate(new Date());
                billingRun.setProcessType(billingCycleType);
                billingRun.setStatus(BillingRunStatusEnum.NEW);

                if (invoiceDateFromCF != null) {
                    billingRun.setInvoiceDate(invoiceDateFromCF);
                } else if (billingCycle.getInvoiceDateProductionDelayEL() != null) {
                    billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), InvoiceService.resolveInvoiceDateDelay(billingCycle.getInvoiceDateProductionDelayEL(), billingRun)));
                } else {
                    billingRun.setInvoiceDate(billingRun.getProcessDate());
                }

                if (lastTransactionDateFromCF != null) {
                    billingRun.setLastTransactionDate(lastTransactionDateFromCF);
                } else if (billingCycle.getLastTransactionDateEL() != null) {
                    billingRun.setLastTransactionDate(BillingRunService.resolveLastTransactionDate(billingCycle.getLastTransactionDateEL(), billingRun));
                } else if (billingCycle.getLastTransactionDateDelayEL() != null) {
                    billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), BillingRunService.resolveLastTransactionDateDelay(billingCycle.getLastTransactionDateDelayEL(), billingRun)));
                } else {
                    billingRun.setLastTransactionDate(billingRun.getProcessDate());
                }
                billingRunService.create(billingRun);
                // result.setNbItemsCorrectlyProcessed(++nbItemsToProcess);
                jobExecutionService.registerSucces(result);

            }
            result.setNbItemsToProcess(nbItemsToProcess + nbItemsProcessedWithError);
        } catch (Exception e) {
            jobExecutionService.registerError(result, e.getMessage());
            log.error("Failed to run billing ", e);
        }
    }
}