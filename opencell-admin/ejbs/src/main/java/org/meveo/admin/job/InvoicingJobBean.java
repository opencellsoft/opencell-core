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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.AmountsToInvoice;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RejectedBillingAccountService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

/**
 * @author HORRI Khalid
 * @lastModifiedVersion 5.4
 */
@Stateless
public class InvoicingJobBean extends BaseJobBean {

    private static final long serialVersionUID = 7770286274770556518L;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private IteratorBasedJobProcessing iteratorBasedJobProcessing;

    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private RejectedBillingAccountService rejectedBillingAccountService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public JobExecutionResultImpl execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {

        List<BillingRun> billingRuns = getBillingRuns(this.getParamOrCFValue(jobInstance, "billingRuns"), jobInstance);

        log.info("BillingRuns to process={}", billingRuns.size());

        if (billingRuns.isEmpty() || !jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
            log.info("{}/{} will skip as nothing to process or should not continue", jobInstance.getJobTemplate(), jobInstance.getCode());
            return jobExecutionResult;
        }

        BillingRun billingRun = billingRuns.get(0);
        billingRunService.detach(billingRun);
        jobExecutionResult = executeBillingRun(billingRun, jobExecutionResult);

        jobExecutionResult.setMoreToProcess(billingRuns.size() > 1);

        return jobExecutionResult;
    }

    /**
     * Get Billing runs to process
     *
     * @param billingRunsCF the billing runs setting from the custom field
     * @param jobInstance the job instance
     * @return A list if billing runs to process
     */
    @SuppressWarnings("unchecked")
    private List<BillingRun> getBillingRuns(Object billingRunsCF, JobInstance jobInstance) {
        List<EntityReferenceWrapper> brList = (List<EntityReferenceWrapper>) billingRunsCF;

        if (brList != null && !brList.isEmpty()) {
            List<Long> ids = brList.stream().map(br -> {
                String compositeCode = br.getCode();
                if (compositeCode == null) {
                    return null;
                }
                return Long.valueOf(compositeCode.split("/")[0]);
            }).collect(Collectors.toList());

            return billingRunService.listByNamedQuery("BillingRun.getForInvoicingLimitToIds", "ids", ids);

        } else {
            Integer jobItemsLimit = jobInstanceService.getJobItemsLimit(jobInstance);
            return billingRunService.getForInvoicing(jobItemsLimit != null ? jobItemsLimit : 0);
        }
    }

    /**
     * Execute a billing run calculating amounts to invoice, creating aggregates and invoices and/or assigning numbers to invoices depending on billing run status
     * 
     * @param billingRun Billing run
     * @param jobExecutionResult Job execution result
     * @return Job execution results corresponding to the last stage of current job run. In case of manual processing, Job execution result passed as argument to the method and teh method result, will be the same object.
     */
    @SuppressWarnings({ "unchecked" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public JobExecutionResultImpl executeBillingRun(BillingRun billingRun, JobExecutionResultImpl jobExecutionResult) throws BusinessException {
        log.info("Processing billingRun id={} status={}", billingRun.getId(), billingRun.getStatus());

        List<IBillableEntity> billableEntities = new ArrayList<>();

        BillingCycle billingCycle = billingRun.getBillingCycle();
        BillingEntityTypeEnum type = BillingEntityTypeEnum.BILLINGACCOUNT;
        if (billingCycle != null) {
            type = billingCycle.getType();
        }

        if(billingRun.isExceptionalBR()) {
            QueryBuilder queryBuilder = fromFilters(billingRun.getFilters());
            billingRun.setExceptionalRTIds(queryBuilder.getIdQuery(ratedTransactionService.getEntityManager()).getResultList());
            if(billingRun.getExceptionalRTIds().size() == 0) {
                jobExecutionResult.setReport("Exceptional Billing filters returning no rated transaction to process");
            }
        }

        MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts();
        if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus()) || BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus())) {
            minAmountForAccounts = ratedTransactionService.isMinAmountForAccountsActivated();
        }
        boolean includesFirstRun = false;
        boolean ranAnotherStage = false;

        if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {
            jobExecutionResult.addReport("Billing run #" + billingRun.getId() + ", stage: Pre-invoicing report.");

            billableEntities = calculateAndUpdateBillableAmounts(billingCycle, billingRun, type, minAmountForAccounts, jobExecutionResult);

            BillingRunStatusEnum nextStatus = BillingRunStatusEnum.PREINVOICED;
            if (billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC || billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC || appProvider.isAutomaticInvoicing()) {
                nextStatus = BillingRunStatusEnum.PREVALIDATED;
            }

            billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), Long.valueOf(jobExecutionResult.getNbItemsToProcess()).intValue(), billableEntities == null ? 0 : billableEntities.size(),
                nextStatus, new Date());
            includesFirstRun = true;

            ranAnotherStage = true;
        }

        if (BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus())) {

            // Need to close current and create a new job execution result
            if (includesFirstRun) {

                jobExecutionResult.close();

                if (jobExecutionResult.getStatus() == JobExecutionResultStatusEnum.RUNNING) {
                    jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                }
                jobExecutionResultService.persistResult(jobExecutionResult);

                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum(), EjbUtils.getCurrentClusterNode());

            } else {
                billableEntities = (List<IBillableEntity>) billingRunService.getEntitiesByBillingRun(billingRun);
            }

            jobExecutionResult.addReport("Billing run #" + billingRun.getId() + ", stage: Invoice generation.");
            jobExecutionResult.setNbItemsToProcess(billableEntities == null ? 0 : billableEntities.size());
            jobExecutionResultService.persistResult(jobExecutionResult);

            createAgregatesAndInvoice(billingRun, type, jobExecutionResult, billableEntities, includesFirstRun, minAmountForAccounts);
            billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.INVOICES_GENERATED, null);

            ranAnotherStage = true;
        }

        if (BillingRunStatusEnum.INVOICES_GENERATED.equals(billingRun.getStatus())) {
            billingRunService.applyThreshold(billingRun.getId());
            rejectBAWithoutBillableTransactions(billingRun, jobExecutionResult);

            BillingRunStatusEnum nextStatus = BillingRunStatusEnum.POSTINVOICED;            
            if (!billingRunService.isBillingRunValid(billingRun)) {
                nextStatus = BillingRunStatusEnum.REJECTED;
            }
            billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, nextStatus, null);

            ranAnotherStage = true;
        }

        if (billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC && (BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus()) || BillingRunStatusEnum.REJECTED.equals(billingRun.getStatus()))) {
            billingRunService.applyAutomaticValidationActions(billingRun);
            billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.POSTVALIDATED, null);
        }

        if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())) {

            if (ranAnotherStage) {
                jobExecutionResult.close();
                if (jobExecutionResult.getStatus() == JobExecutionResultStatusEnum.RUNNING) {
                    jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                }
                jobExecutionResultService.persistResult(jobExecutionResult);

                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum(), EjbUtils.getCurrentClusterNode());
            }

            jobExecutionResult.addReport("Billing run #" + billingRun.getId() + ", stage: Assign invoice numbers.");

            assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, jobExecutionResult);
            billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);
        }

        return jobExecutionResult;
    }

    private QueryBuilder fromFilters(Map<String, Object> filters) {
        QueryBuilder queryBuilder;
        String filterValue = QueryBuilder.getFilterByKey(filters, "SQL");
        if (!StringUtils.isBlank(filterValue)) {
            queryBuilder = new QueryBuilder(filterValue);
        } else {
            FilterConverter converter = new FilterConverter(RatedTransaction.class);
            PaginationConfiguration configuration = new PaginationConfiguration(converter.convertFilters(filters));
            queryBuilder = ratedTransactionService.getQuery(configuration);
        }
        return queryBuilder;
    }

    /**
     * Calculate amounts to invoice, link with Billing run and update Billing account with amount to invoice (if it is a billable entity). One billable entity at a time in a separate transaction.
     * 
     * @param billingCycle Billing cycle
     * @param billingRun Billing run
     * @param type Billable entity type
     * @param minAmountForAccounts Should rated transactions to reach minimum invoicing amount be checked and instantiated on service, subscription or billing account level
     * @param jobExecutionResult Job execution result
     * @return A list of entities to bill
     */
    @SuppressWarnings("unchecked")
    private List<IBillableEntity> calculateAndUpdateBillableAmounts(BillingCycle billingCycle, BillingRun billingRun, BillingEntityTypeEnum type, MinAmountForAccounts minAmountForAccounts,
            JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        int totalEntityCount = 0;

        List<IBillableEntity> entitiesToBill = null;

        // Use billable amount calculation one billable entity at a time when minimum billable amount rule is used or billable entities are provided as parameter of billing run
        // (billingCycle=null)
        // NOTE: invoice by order is also included here as there is no FK between Order and RT
        if ((billingCycle == null || billingCycle.getType() == BillingEntityTypeEnum.ORDER
                || minAmountForAccounts.isMinAmountCalculationActivated()) && !billingRun.isExceptionalBR()) {
            // TODO check how we can pass v11process
            List<IBillableEntity> entities = (List<IBillableEntity>) billingRunService.getEntitiesToInvoice(billingRun, true);

            totalEntityCount = entities != null ? entities.size() : 0;

            jobExecutionResult.setNbItemsToProcess(totalEntityCount);
            log.info("Will create min RTs and update billable amount totals for Billing run {} for {} entities of type {}. Minimum invoicing amount is used for accounts hierarchy {}", billingRun.getId(),
                totalEntityCount, type, minAmountForAccounts);

            if (totalEntityCount > 0) {

                Function<IBillableEntity, IBillableEntity> task = (billableEntity) -> ratedTransactionService.updateEntityTotalAmountsAndLinkToBR(billableEntity, billingRun, minAmountForAccounts);

                entitiesToBill = iteratorBasedJobProcessing.processItemsAndAgregateResults(jobExecutionResult, new SynchronizedIterator<>(entities), task, nbRuns, waitingMillis, false,
                    true);
            }

            // A simplified form of calculating of total amounts when no need to worry about minimum amounts
        } else {
            List<AmountsToInvoice> billableAmountSummary = billingRunService.getAmountsToInvoice(billingRun);

            totalEntityCount = billableAmountSummary != null ? billableAmountSummary.size() : 0;

            jobExecutionResult.setNbItemsToProcess(totalEntityCount);
            log.info("Will create min RTs and update billable amount totals for Billing run {} for {} entities of type {}. Minimum invoicing amount is skipped.", billingRun.getId(), totalEntityCount, type);

            if (totalEntityCount > 0) {

                Function<AmountsToInvoice, IBillableEntity> task = (amountsToInvoice) -> ratedTransactionService.updateEntityTotalAmountsAndLinkToBR(amountsToInvoice.getEntityToInvoiceId(), billingRun,
                    amountsToInvoice.getAmountsToInvoice());

                entitiesToBill = iteratorBasedJobProcessing.processItemsAndAgregateResults(jobExecutionResult, new SynchronizedIterator<>(billableAmountSummary), task, nbRuns, waitingMillis, false,
                    true);

                log.info("Will update BR amount totals for Billing run {}. Will invoice {} out of {} entities of type {}", billingRun.getId(), (entitiesToBill != null ? entitiesToBill.size() : 0), totalEntityCount,
                    type);
                if(!billingRun.isExceptionalBR()) {
                    billingRunExtensionService.updateBRAmounts(billingRun.getId(), entitiesToBill);
                }

            }
        }

        return entitiesToBill;
    }

    /**
     * Creates the agregates and invoice.
     * 
     * @param billingRun Billing run
     * @param type Billable entity type
     * @param jobExecutionResult Job execution results
     * @param billableEntities List of entities to invoice
     * @param alreadyInstantiatedMinRTs Were minimum amount RTs were instantiated during the first run (TRUE only when first and second invoicing steps are run together)
     * @param minAmountForAccounts Should rated transactions to reach minimum invoicing amount be checked and instantiated on service, subscription or billing account level
     * @throws BusinessException business exception.
     */
    @SuppressWarnings({ "unchecked" })
    private void createAgregatesAndInvoice(BillingRun billingRun, BillingEntityTypeEnum type, JobExecutionResultImpl jobExecutionResult,
                                           List<? extends IBillableEntity> billableEntities, boolean alreadyInstantiatedMinRTs,
                                           MinAmountForAccounts minAmountForAccounts) {

        if (billableEntities == null || billableEntities.isEmpty()) {
            return;
        }
        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        boolean instantiateMinRts = !alreadyInstantiatedMinRTs && minAmountForAccounts.isMinAmountCalculationActivated();

        log.info("Will create invoices for Billing run {} for {} entities of type {}. Min RTs were {} instantiated before. {}",
                billingRun.getId(), (billableEntities != null ? billableEntities.size() : 0), type, alreadyInstantiatedMinRTs ? "" : "NOT",
                !instantiateMinRts ? "" : "Minimum invoicing amount is used for serviceInstance " + minAmountForAccounts.isServiceHasMinAmount() +
                    ", subscription " + minAmountForAccounts.isSubscriptionHasMinAmount() + ", billingAccount " + minAmountForAccounts.isBaHasMinAmount());

        final MinAmountForAccounts minAmountForAccountsAdjusted = minAmountForAccounts.adjustForFirstRun(alreadyInstantiatedMinRTs);

        BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (entityToInvoice, jobResult) -> invoiceService.createAgregatesAndInvoiceInNewTransaction(entityToInvoice, billingRun,
            billingRun.isExceptionalBR() ? billingRunService.createFilter(billingRun, false) : null, null, null, null, minAmountForAccountsAdjusted, false, !billingRun.isSkipValidationScript());

        iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>((Collection<IBillableEntity>) billableEntities), task, null, null, nbRuns, waitingMillis, false,
            true);
    }

    /**
     * Reject Billing accounts from invoicing that have no billable transactions (that means they were not included in this BR) and increment their next invoice date. This applies only when processing a Billing run with
     * a billing cycle set
     *
     * @param billingRun The billing run
     * @param jobExecutionResult the Job execution result
     * @throws BusinessException the business exception
     */
    private void rejectBAWithoutBillableTransactions(BillingRun billingRun, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

        // Does not apply when processing a Billing run without a billing cycle set
        if (billingRun.getBillingCycle() == null) {
            return;
        }

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        List<Long> billingAccountIds = billingAccountService.findNotProcessedBillingAccounts(billingRun);

        BiConsumer<Long, JobExecutionResultImpl> task = (baId, jobResult) -> {

            // TODO this way of finding reason is not very good, as nextInvoiceDate will be null only of the first run.
            BillingAccount ba = billingAccountService.findById(baId);
            String reason = ba.getNextInvoiceDate() == null ? "Next Invoicing Date is null" : "No billable transaction";
            rejectedBillingAccountService.create(ba, billingRun, reason);

            invoiceService.incrementBAInvoiceDateInNewTx(billingRun, baId);
        };

        iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<Long>((Collection<Long>) billingAccountIds), task, null, null, nbRuns, waitingMillis, false, false);
    }

    /**
     * Assign invoice number and increment BA invoice dates.
     *
     * @param billingRun The billing run
     * @param jobExecutionResult the Job execution result
     * @throws BusinessException the business exception
     */
    private void assignInvoiceNumberAndIncrementBAInvoiceDates(BillingRun billingRun, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

        log.info("Will assign invoice numbers to invoices of Billing run {}", billingRun.getId());

        List<InvoicesToNumberInfo> invoiceSummary = invoiceService.getInvoicesToNumberSummary(billingRun.getId());

        // A quick loop to update job progress with a number of items to process
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
            jobExecutionResult.addNbItemsToProcess(invoicesToNumberInfo.getNrOfInvoices());
        }
        jobExecutionResultService.persistResult(jobExecutionResult);

        invoiceService.nullifyInvoiceFileNames(billingRun); // #3600

        // Reserve invoice number for each invoice type/seller/invoice date combination
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
            InvoiceSequence sequence = serviceSingleton.reserveInvoiceNumbers(invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate(),
                invoicesToNumberInfo.getNrOfInvoices());
            invoicesToNumberInfo.setNumberingSequence(sequence);
        }

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        // Find and process invoices
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {

            List<Long> invoiceIds = invoiceService.getInvoiceIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate());
            // Validate that what was retrieved as summary matches the details
            if (invoiceIds.size() != invoicesToNumberInfo.getNrOfInvoices().intValue()) {
                throw new BusinessException(String.format("Number of invoices retrieved %s dont match the expected number %s for %s/%s/%s/%s", invoiceIds.size(), invoicesToNumberInfo.getNrOfInvoices(),
                    billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate()));
            }

            // Assign invoice numbers
            BiConsumer<Long, JobExecutionResultImpl> task = (invoiceId, jobResult) -> {
                invoiceService.recalculateDates(invoiceId);
                invoiceService.assignInvoiceNumber(invoiceId, invoicesToNumberInfo);
                invoiceService.updateStatus(invoiceId, InvoiceStatusEnum.VALIDATED);

            };
            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(invoiceIds), task, null, null, nbRuns, waitingMillis, false, true);

            List<Long> baIds = invoiceService.getBillingAccountIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate());

            // Increment next invoice date of a billing account
            task = (baId, jobResult) -> {
                invoiceService.incrementBAInvoiceDate(billingRun, baId);
            };

            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(baIds), task, null, null, nbRuns, waitingMillis, false, false);
        }
    }

}
