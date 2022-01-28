package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.*;
import static org.meveo.model.billing.BillingRunStatusEnum.*;
import static org.meveo.model.billing.BillingRunStatusEnum.REJECTED;
import static org.meveo.model.jobs.JobLauncherEnum.*;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.*;

public class BillingRunApiService implements ApiService<BillingRun> {

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobInstanceService jobInstanceService;

    private static final String INVOICING_JOB_CODE = "Invoicing_Job_V2";

    private static final String INVOICING_JOB_PARAMETERS = "InvoicingJobV2_billingRun";

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
        if(billingRun.getStatus() == INVOICE_LINES_CREATED || billingRun.getStatus() == DRAFT_INVOICES) {
            if (billingRun.getStatus() == INVOICE_LINES_CREATED) {
                billingRun.setStatus(PREVALIDATED);
            }
            if (billingRun.getStatus() == DRAFT_INVOICES) {
                billingRun.setStatus(POSTVALIDATED);
            }
            billingRunService.update(billingRun);
        }
        if (executeInvoicingJob) {
            executeJob(INVOICING_JOB_CODE, billingRun.getReferenceCode());
        }
        return of(billingRun);
    }

    private long executeJob(String jobCode, String billingRungCode) {
        try {
            JobInstance jobInstance = jobInstanceService.findByCode(jobCode);
            if (jobInstance == null) {
                throw new BusinessException("Invoicing job not found");
            }
            Map<String, Object> jobParams = new HashMap();
            jobParams.put(INVOICING_JOB_PARAMETERS,
                    asList(new EntityReferenceWrapper(BillingRun.class.getName(),null, billingRungCode)));
            jobInstance.setRunTimeValues(jobParams);
            return jobExecutionService.executeJob(jobInstance, jobParams, API);
        } catch (Exception exception) {
            throw new BusinessException("Exception occurred during job execution : "
                    + exception.getMessage(), exception.getCause());
        }
    }
}