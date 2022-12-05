package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

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
        
        List<Invoice> impactedInvoices = invoiceLinesToAdjust.stream().map(InvoiceLine::getInvoice).distinct().collect(Collectors.toList());
        
        BigDecimal totalAWoTProcessed = BigDecimal.ZERO;
        BigDecimal totalAWTProcessed = BigDecimal.ZERO;
        int totalLinesProcessed = 0;
        int totalImpactedBA = impactedInvoices.stream().map(Invoice::getBillingAccount).mapToInt(i -> 1).sum();
        
        for (Invoice invoice : impactedInvoices) {
        	Invoice adjustment = invoiceService.createAdjustment(invoice, invoiceLinesToAdjustIds);
        	invoiceService.validateInvoice(adjustment);
        	totalLinesProcessed += adjustment.getInvoiceLines().size();
        	totalAWoTProcessed = totalAWoTProcessed.add(adjustment.getInvoiceLines().stream().map(InvoiceLine::getAmountWithoutTax).reduce(BigDecimal.ZERO, BigDecimal::add));
        	totalAWTProcessed = totalAWTProcessed.add(adjustment.getInvoiceLines().stream().map(InvoiceLine::getAmountWithTax).reduce(BigDecimal.ZERO, BigDecimal::add));
        };
        
        BigDecimal totalAmountWoT = BigDecimal.ZERO;
        BigDecimal totalAmountWT = BigDecimal.ZERO;
        
		for (InvoiceLine invoiceLine : invoiceLinesToAdjust) {
			invoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.ADJUSTED);
			totalAmountWoT = totalAmountWoT.add(invoiceLine.getAmountWithoutTax());
			totalAmountWT = totalAmountWT.add(invoiceLine.getAmountWithTax());
			invoiceLineService.update(invoiceLine);
		}
        
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.initial.lines", invoiceLinesToAdjust.size()));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.processed.lines", totalLinesProcessed));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.inital.awot", totalAmountWoT));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.initial.awt", totalAmountWT));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.processed.awot", totalAWoTProcessed));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.processed.awt", totalAWTProcessed));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.impacted.invoices", impactedInvoices.size()));
        jobExecutionResult.addReport(resourceMessages.getString("jobExecution.mass.adjustment.report.impacted.ba", totalImpactedBA));
        
        return jobExecutionResult;
    }

}