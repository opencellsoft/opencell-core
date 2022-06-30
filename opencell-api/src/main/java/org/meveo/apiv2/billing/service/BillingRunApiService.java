package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.meveo.model.billing.BillingRunStatusEnum.*;
import static org.meveo.model.jobs.JobLauncherEnum.API;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

public class BillingRunApiService implements ApiService<BillingRun> {

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobInstanceService jobInstanceService;
    
    @Inject
    private RatedTransactionService ratedTransactionService;
    
    @Inject
    private InvoiceService invoiceService;
    
    @Inject
    private InvoiceAgregateService invoiceAgregateService;
    
    @Inject
    private InvoiceLineService invoiceLineService;

    private static final String INVOICING_JOB_CODE = "Invoicing_Job_V2";
    private static final String INVOICE_LINES_JOB_CODE = "Invoice_Lines_Job_V2";

    private static final String INVOICING_JOB_PARAMETERS = "InvoicingJobV2_billingRun";
    private static final String INVOICE_LIENS_JOB_PARAMETERS = "InvoiceLinesJob_billingRun";

    @Override
    public BillingRun create(BillingRun billingRun) {
        billingRunService.create(billingRun);
        return billingRun;
    }

    @Override
    public List<BillingRun> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<BillingRun> findById(Long id) {
        return empty();
    }

    @Override
    public Optional<BillingRun> update(Long id, BillingRun baseEntity) {
        return empty();
    }

    @Override
    public Optional<BillingRun> patch(Long id, BillingRun baseEntity) {
        return empty();
    }

    @Override
    public Optional<BillingRun> delete(Long id) {
        return empty();
    }

    @Override
    public Optional<BillingRun> findByCode(String code) {
        return empty();
    }

    public Optional<BillingRun> advancedStatus(Long billingRunId, boolean executeInvoicingJob) {
        BillingRun billingRun = billingRunService.findById(billingRunId);

        if (billingRun == null) {
            return empty();
        }

        if (billingRun.getStatus() != NEW && billingRun.getStatus() != INVOICE_LINES_CREATED
                && billingRun.getStatus() != DRAFT_INVOICES && billingRun.getStatus() != REJECTED) {
            throw new BadRequestException("Billing run status must be either {NEW, INVOICE_LINES_CREATED, DRAFT_INVOICES, REJECTED}");
        }

        if (billingRun.getStatus() == NEW || billingRun.getStatus() == INVOICE_LINES_CREATED
                || billingRun.getStatus() == DRAFT_INVOICES || billingRun.getStatus() == REJECTED) {
            
            if (billingRun.getStatus() == NEW && executeInvoicingJob) {

                Map<String, Object> invoiceLineJobParams = new HashMap<>();
                invoiceLineJobParams.put(INVOICE_LIENS_JOB_PARAMETERS,
                        asList(new EntityReferenceWrapper(BillingRun.class.getName(),
                                null, billingRun.getReferenceCode())));
                JobInstance invoiceLineJob = jobInstanceService.findByCode(INVOICE_LINES_JOB_CODE);
                JobInstance invoicingJob = jobInstanceService.findByCode(INVOICING_JOB_CODE);
                invoiceLineJob.setFollowingJob(jobInstanceService.findByCode(INVOICING_JOB_CODE));
                if(invoicingJob.getCfValues() != null) {
                    if(invoicingJob.getCfValues().getValue("InvoicingJobV2_billingRun") != null
                            && !((List) invoicingJob.getCfValues().getValue("InvoicingJobV2_billingRun")).isEmpty()) {
                        ((List) invoicingJob.getCfValues().getValue("InvoicingJobV2_billingRun")).clear();
                    }
                    invoicingJob.getCfValues().setValue("InvoicingJobV2_billingRun",
                            asList(new EntityReferenceWrapper(BillingRun.class.getName(),
                                    null, billingRun.getReferenceCode())));
                }
                jobInstanceService.update(invoiceLineJob);
                jobInstanceService.update(invoicingJob);
                executeJob(invoiceLineJob, invoiceLineJobParams);
            } else {

                BillingRunStatusEnum initialStatus = billingRun.getStatus();
                if (billingRun.getStatus() == INVOICE_LINES_CREATED) {
                    billingRun.setStatus(PREVALIDATED);
                }
                if (billingRun.getStatus() == DRAFT_INVOICES) {
                    billingRun.setStatus(POSTVALIDATED);
                }
                if (billingRun.getStatus() == REJECTED) {
                    if (billingRunService.isBillingRunValid(billingRun)) {
                        billingRun.setStatus(POSTVALIDATED);
                    }
                }
                if (initialStatus != billingRun.getStatus()) {
                    billingRun.setXmlJobExecutionResultId(null);
                    billingRun.setPdfJobExecutionResultId(null);
                    billingRun = billingRunService.update(billingRun);
                }
                
                if (executeInvoicingJob) {
                    Map<String, Object> jobParams = new HashMap<>();
                    jobParams.put(INVOICING_JOB_PARAMETERS,
                            asList(new EntityReferenceWrapper(BillingRun.class.getName(), null, billingRun.getReferenceCode())));
                    executeJob(jobInstanceService.findByCode(INVOICING_JOB_CODE), jobParams);
                }
            }
        }
        return of(billingRun);
    }

    private long executeJob(JobInstance jobInstance, Map<String, Object> jobParams) {
        try {
            if (jobInstance == null) {
                throw new BusinessException("Cannot execute job");
            }
            jobInstance.setRunTimeValues(jobParams);
            return jobExecutionService.executeJob(jobInstance, jobParams, API);
        } catch (Exception exception) {
            throw new BusinessException("Exception occurred during job execution : "
                    + exception.getMessage(), exception.getCause());
        }
    }

	public Optional<BillingRun> cancelBillingRun(Long billingRunId) {
        BillingRun billingRun = billingRunService.findById(billingRunId);
        if (billingRun == null) {
            return empty();
        }
        if (billingRun.getStatus() == POSTVALIDATED || billingRun.getStatus() == VALIDATED
                || billingRun.getStatus() == CANCELLING || billingRun.getStatus() == CANCELED
                || billingRun.getStatus() == CREATING_INVOICE_LINES) {
            throw new BadRequestException("The billing run with status " + billingRun.getStatus() + " cannot be cancelled");
        }
        try {
            ratedTransactionService.deleteSupplementalRTs(billingRun);
            ratedTransactionService.uninvoiceRTs(billingRun);
            invoiceLineService.deleteInvoiceLines(billingRun);
            invoiceLineService.deleteByAssociatedInvoice(invoiceService.getInvoicesByBR(billingRun.getId()));
            invoiceService.deleteInvoices(billingRun);
            invoiceAgregateService.deleteInvoiceAgregates(billingRun);
            billingRun.setStatus(CANCELED);
            billingRunService.update(billingRun);
            return of(billingRun);
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
	}
}