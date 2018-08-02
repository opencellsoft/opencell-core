/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
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
    private BillingAccountService billingAccountService;

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

    /**
     * Update billing account total amounts async. One billing account at a time in a separate transaction.
     *
     * @param billingAccountIds Billing account ids
     * @param billingRun The billing run
     * @param jobInstanceId Job instance id
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     * @throws BusinessException the business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<Integer> updateBillingAccountTotalAmountsAsync(List<Long> billingAccountIds, BillingRun billingRun, Long jobInstanceId, MeveoUser lastCurrentUser)
            throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int count = 0;
        for (Long billingAccountId : billingAccountIds) {
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            if (billingAccountService.updateBillingAccountTotalAmounts(billingAccountId, billingRun)) {
                count++;
            }
        }
        log.info("WorkSet billableBA:" + count);
        return new AsyncResult<Integer>(new Integer(count));
    }

    /**
     * Creates the aggregates and invoice async. One billing account at a time in a separate transaction.
     *
     * @param billingAccounts the billing accounts
     * @param billingRunId The billing run id
     * @param jobInstanceId the job instance id
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> createAgregatesAndInvoiceAsync(List<BillingAccount> billingAccounts, Long billingRunId, Long jobInstanceId, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (BillingAccount billingAccount : billingAccounts) {
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            try {
                invoiceService.createAgregatesAndInvoice(billingAccount.getId(), billingRunId, null, null, null, null, null, billingAccount.getMinRatedTransactions());
            } catch (Exception e) {
                log.error("Error for BA=" + billingAccount.getId() + " : " + e);
            }
        }
        return new AsyncResult<String>("OK");
    }

    /**
     * Assign invoice number and increment BA invoice dates async. One invoice at a time in a separate transaction.
     *
     * @param invoiceIds the invoice ids
     * @param invoicesToNumberInfo the invoices to number info
     * @param jobInstanceId the job instance id
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> assignInvoiceNumberAndIncrementBAInvoiceDatesAsync(List<Long> invoiceIds, InvoicesToNumberInfo invoicesToNumberInfo, Long jobInstanceId,
            MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (Long invoiceId : invoiceIds) {
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            try {
                invoiceService.assignInvoiceNumberAndIncrementBAInvoiceDate(invoiceId, invoicesToNumberInfo);
            } catch (Exception e) {
                log.error("Failed to increment invoice date for invoice {}", invoiceId, e);
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

        for (Long invoiceId : invoiceIds) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
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

        for (Long invoiceId : invoiceIds) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            long startDate = System.currentTimeMillis();
            try {
                invoiceService.produceInvoiceXmlInNewTransaction(invoiceId);
                result.registerSucces();
            } catch (Exception e) {
                result.registerError(invoiceId, e.getMessage());
                allOk = false;
                log.error("Failed to create XML invoice for invoice {}", invoiceId, e);
            }
            log.info("Invoice creation delay :" + (System.currentTimeMillis() - startDate));
        }

        return new AsyncResult<Boolean>(allOk);
    }
}