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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * Job implementation to generate account operations for all invoices that don't have it yet.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author Andrius Karpavicius
 * @lastModifiedVersion 10.0
 **/
@Stateless
public class AccountOperationsGenerationJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = -1247529117246250636L;

    @Inject
    private InvoiceService invoiceService;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    private ScriptInterface script;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::createAccountOperations, null, null);
        script = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Invoice Ids to create account operations for
     */
    @SuppressWarnings("unchecked")
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Boolean isExcludeInvoicesWithoutAmount = (Boolean) this.getParamOrCFValue(jobInstance, "AccountOperationsGenerationJob_excludeInvoicesWithoutAmount", Boolean.FALSE);
        List<Long> ids = invoiceService.queryInvoiceIdsWithNoAccountOperation(null, isExcludeInvoicesWithoutAmount, Boolean.TRUE);

        try {
            String scriptInstanceCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "AccountOperationsGenerationJob_script")).getCode();
            Map<String, Object> context = new HashMap<String, Object>();
            if (this.getParamOrCFValue(jobInstance, "AccountOperationsGenerationJob_variables") != null) {
                context = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "AccountOperationsGenerationJob_variables");
            }
            if (!StringUtils.isBlank(scriptInstanceCode)) {
                script = scriptInstanceService.getScriptInstance(scriptInstanceCode);
                script.init(context);
            }
        } catch (Exception e) {
            log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
        }

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Create account operations
     * 
     * @param invoiceId Invoice id
     * @param jobExecutionResult Job execution result
     * @throws InvoiceExistException invoice exist exception
     * @throws ImportInvoiceException import invoice exception
     * @throws BusinessException General business exception
     */
    private void createAccountOperations(Long invoiceId, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

        try {
            Invoice invoice = invoiceService.findById(invoiceId);
            recordedInvoiceService.generateRecordedInvoice(invoice);

            invoice = invoiceService.update(invoice);

            if (script != null) {
                Map<String, Object> context = new HashMap<String, Object>();
                context.put(Script.CONTEXT_ENTITY, invoice.getRecordedInvoice());
                context.put(Script.CONTEXT_CURRENT_USER, currentUser);
                context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                script.execute(context);
            }
        } catch (InvoiceExistException | ImportInvoiceException e) {
            throw new BusinessException(e);
        }
    }
}