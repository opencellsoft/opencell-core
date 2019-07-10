/**
 * 
 */
package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * The Class InvoicingAsync.
 *
 * @author anasseh
 */

@Stateless
public class InvoicingAsync {

    /** The billing account service. */
    @Inject
    private RatedTransactionService ratedTransactionService;

    /** The invoice service. */
    @Inject
    private InvoiceService invoiceService;

    /** The log. */
    @Inject
    protected Logger log;

    /** The JobExecution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @EJB
    private InvoicingAsync invoicingNewTransaction;

    /**
     * Update billing account total amounts async. One billing account at a time in a separate transaction.
     *
     * @param entities Entities
     * @param billingRun The billing run
     * @param jobInstanceId Job instance id
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     * @throws BusinessException the business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<List<IBillableEntity>> updateBillingAccountTotalAmountsAsync(List<IBillableEntity> entities, BillingRun billingRun, Long jobInstanceId, MeveoUser lastCurrentUser)
            throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        List<IBillableEntity> billableEntities = new ArrayList<IBillableEntity>();
        int i = 0;
        for (IBillableEntity entity : entities) {
            i++;
            if (jobInstanceId != null && i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            IBillableEntity billableEntity = ratedTransactionService.updateEntityTotalAmounts(entity, billingRun);
            if (billableEntity != null) {
                billableEntities.add(billableEntity);
            }
        }
        return new AsyncResult<List<IBillableEntity>>(billableEntities);
    }

    /**
     * Creates the agregates and invoice async. One entity at a time in a separate transaction.
     *
     * @param entities the entity objects
     * @param billingRun the billing run
     * @param jobInstanceId the job instance id
     * @param instantiateMinRts Should rated transactions to reach minimum invoicing amount be checked and instantiated
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> createAgregatesAndInvoiceAsync(List<? extends IBillableEntity> entities, BillingRun billingRun, Long jobInstanceId, boolean instantiateMinRts,
            MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        // This is a version of doing invoicing with one entity at a time
        for (IBillableEntity entityToInvoice : entities) {
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            try {
                invoiceService.createAgregatesAndInvoiceInNewTransaction(entityToInvoice, billingRun, null, null, null, null, instantiateMinRts, false, false);
            } catch (Exception e1) {
                log.error("Error for entity {}/{}", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId(), e1);
            }
        }

        // This is a version of doing invoicing with 50 entities at a time
        /*
         * int invoiceCreationBatchSize = ParamBean.getInstance().getPropertyAsInteger("invoicing.invoiceCreationBatchSize", 50);
         * 
         * SubListCreator<? extends IBillableEntity> listIterator = new SubListCreator<>(invoiceCreationBatchSize, entities); while (listIterator.isHasNext()) { if (jobInstanceId
         * != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) { break; } List<? extends IBillableEntity> entitiesInBatch = listIterator.getNextWorkSet();
         * 
         * try { invoicingNewTransaction.createAgregatesAndInvoiceInBatch(entitiesInBatch, billingRun, jobInstanceId, instantiateMinRts); } catch (Exception e) {
         * 
         * log.error("Failed to create invoices in batch. Will switch to invoicing to one by one", e);
         * 
         * for (IBillableEntity entityToInvoice : entitiesInBatch) { try { invoiceService.createAgregatesAndInvoiceInNewTransaction(entityToInvoice, billingRun, null, null, null,
         * null, instantiateMinRts, false, false); } catch (Exception e1) { log.error("Error for entity {}/{}", entityToInvoice.getClass().getSimpleName(), entityToInvoice.getId(),
         * e1); } } } }
         */

        return new AsyncResult<String>("OK");
    }

    /**
     * Create invoices for a given list of entities. Invoices are created in a batch and in a single transaction
     * 
     * @param entities Entities to invoice
     * @param billingRun Billing run
     * @param jobInstanceId Job identifier
     * @param instantiateMinRts Should minimum Rated transactions be instantiated first
     * @throws BusinessException General business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createAgregatesAndInvoiceInBatch(List<? extends IBillableEntity> entities, BillingRun billingRun, Long jobInstanceId, boolean instantiateMinRts)
            throws BusinessException {

        List<RatedTransaction> rtsUpdateInBatch = new ArrayList<>();
//        int i = 0;
        for (IBillableEntity entity : entities) {
//            i++;
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) { // && i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0
                break;
            }
            invoiceService.createAgregatesAndInvoice(entity, billingRun, null, null, null, null, instantiateMinRts, false, false, rtsUpdateInBatch);
        }

        long start = System.currentTimeMillis();
        invoiceService.commit();
        log.error("AKK invoice commit took {}", System.currentTimeMillis() - start);

        ratedTransactionService.updateViaDeleteAndInsert(rtsUpdateInBatch);
    }

    /**
     * Assign invoice number and increment BA invoice dates async. One invoice at a time in a separate transaction.
     *
     * @param billingRun the billing run to process
     * @param invoiceIds the invoice ids
     * @param invoicesToNumberInfo the invoices to number info
     * @param jobInstanceId the job instance id
     * @param result the Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> assignInvoiceNumberAndIncrementBAInvoiceDatesAsync(BillingRun billingRun, List<Long> invoiceIds, InvoicesToNumberInfo invoicesToNumberInfo,
            Long jobInstanceId, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int i = 0;
        for (Long invoiceId : invoiceIds) {
            i++;
            if (jobInstanceId != null && i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            try {
                invoiceService.assignInvoiceNumberAndIncrementBAInvoiceDate(invoiceId, invoicesToNumberInfo);

            } catch (Exception e) {
                log.error("Failed to increment invoice date for invoice {}", invoiceId, e);

                if (result != null) {
                    result.registerWarning("Failed when assign invoice number to invoice " + invoiceId + " : " + e.getMessage());
                }
            }
        }
        return new AsyncResult<String>("OK");
    }

    /**
     * Generate pdf async for a list of given invoice ids. One invoice at a time in a separate transaction.
     *
     * @param invoiceIds the invoice ids
     * @param result the result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> generatePdfAsync(List<Long> invoiceIds, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int i = 0;
        for (Long invoiceId : invoiceIds) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            try {
                invoiceService.produceInvoicePdfInNewTransaction(invoiceId);
                result.registerSucces();
            } catch (Exception e) {
                result.registerError(invoiceId, e.getMessage());
                log.error("Failed to create PDF invoice for invoice {}", invoiceId, e);
            }
        }

        return new AsyncResult<String>("OK");
    }

    /**
     * Generate xml async for a list of given invoice ids. One invoice at a time in a separate transaction.
     *
     * @param invoiceIds the invoice ids
     * @param result the result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<Boolean> generateXmlAsync(List<Long> invoiceIds, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        boolean allOk = true;

        int i = 0;
        for (Long invoiceId : invoiceIds) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            try {
                invoiceService.produceInvoiceXmlInNewTransaction(invoiceId);
                result.registerSucces();
            } catch (Exception e) {
                result.registerError(invoiceId, e.getMessage());
                allOk = false;
                log.error("Failed to create XML invoice for invoice {}", invoiceId, e);
            }
        }

        return new AsyncResult<Boolean>(allOk);
    }
}