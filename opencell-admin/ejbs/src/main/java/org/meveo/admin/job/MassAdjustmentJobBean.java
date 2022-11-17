package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.AdjustmentStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;

@Stateless
public class MassAdjustmentJobBean extends BaseJobBean {

    private static final long serialVersionUID = 1L;

	@Inject
	private InvoiceLineService invoiceLineService;

    @Inject
    private InvoiceService invoiceService;
    
    @Inject
	protected ResourceBundle resourceMessages;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public JobExecutionResultImpl execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
    	log.debug("Running MassAdjustmentJob with parameter={}", jobInstance.getParametres());
    	
    	List<InvoiceLine> invoiceLinesToAdjust = invoiceLineService.findInvoiceLinesToAdjust(); 
    	
        if (invoiceLinesToAdjust == null || invoiceLinesToAdjust.isEmpty()) {
            log.info("{}/{} will skip as nothing to process or should not continue", jobInstance.getJobTemplate(), jobInstance.getCode());
            return jobExecutionResult;
        }
        
        log.info("Invoice lines having adjustmentStatus= TO_ADJUST to process = {}", invoiceLinesToAdjust.size());
        
        List<Long> invoiceLinesToAdjustIds = invoiceLinesToAdjust.stream().map(InvoiceLine::getId).collect(Collectors.toList());
        
        invoiceLinesToAdjust.stream().map(InvoiceLine::getInvoice).distinct().forEach(invoice -> {
        	Invoice adjustment = invoiceService.createAdjustment(invoice, invoiceLinesToAdjustIds);
        	invoiceService.validateInvoice(adjustment);
        });
        
        BigDecimal totalAmountWoT = BigDecimal.ZERO;
        BigDecimal totalAmountWT = BigDecimal.ZERO;
        
		for (InvoiceLine invoiceLine : invoiceLinesToAdjust) {
			invoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.ADJUSTED);
			totalAmountWoT = totalAmountWoT.add(invoiceLine.getAmountWithoutTax());
			totalAmountWT = totalAmountWT.add(invoiceLine.getAmountWithTax());
			invoiceLineService.update(invoiceLine);
		}
        
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report", invoiceLinesToAdjust.size(), totalAmountWoT, totalAmountWT));

        return jobExecutionResult;
    }

}