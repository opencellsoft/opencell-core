package org.meveo.admin.job;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UnitAccountOperationsGenerationJobBean {

	@Inject
	private Logger log;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private RecordedInvoiceService recordedInvoiceService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser, Long id) {

		try {
			Invoice invoice = invoiceService.findById(id);
			CustomerAccount customerAccount = null;
			RecordedInvoice recordedInvoice = new RecordedInvoice();
			BillingAccount billingAccount = invoice.getBillingAccount();

			if (recordedInvoiceService.isRecordedInvoiceExist(invoice.getInvoiceNumber(), invoice.getProvider())) {
				throw new InvoiceExistException("Invoice id" + invoice.getId() + " already exist");
			}

			try {
				customerAccount = invoice.getBillingAccount().getCustomerAccount();
				recordedInvoice.setCustomerAccount(customerAccount);
				recordedInvoice.setProvider(customerAccount.getProvider());
			} catch (Exception e) {
				log.error("error while getting customer account ", e);
				throw new ImportInvoiceException("Cant find customerAccount");
			}
			if (invoice.getNetToPay() == null) {
				throw new ImportInvoiceException("Net to pay is null");
			}
			if (invoice.getInvoiceType() == null) {
				throw new ImportInvoiceException("Invoice type is null");
			}
			
			OCCTemplate invoiceTemplate = invoice.getInvoiceType().getOccTemplate();
			BigDecimal amountWithoutTax = invoice.getAmountWithoutTax();
			BigDecimal amountTax = invoice.getAmountTax();
			BigDecimal amountWithTax = invoice.getAmountWithTax();
			BigDecimal netToPay = invoice.getNetToPay();

			if (netToPay.compareTo(BigDecimal.ZERO) < 0) {
				netToPay = netToPay.abs();
				invoiceTemplate = invoice.getInvoiceType().getOccTemplateNegative();
			}
			if (amountWithoutTax != null && amountWithoutTax.compareTo(BigDecimal.ZERO) < 0) {
				amountWithoutTax = amountWithoutTax.abs();
			}
			if (amountTax != null && amountTax.compareTo(BigDecimal.ZERO) < 0) {
				amountTax = amountTax.abs();
			}
			if (amountWithTax != null && amountWithTax.compareTo(BigDecimal.ZERO) < 0) {
				amountWithTax = amountWithTax.abs();
			}

			if (invoiceTemplate == null) {
				throw new ImportInvoiceException("Cant find OccTemplate");
			}

			recordedInvoice.setReference(invoice.getInvoiceNumber());
			recordedInvoice.setAccountCode(invoiceTemplate.getAccountCode());
			recordedInvoice.setOccCode(invoiceTemplate.getCode());
			recordedInvoice.setOccDescription(invoiceTemplate.getDescription());
			recordedInvoice.setTransactionCategory(invoiceTemplate.getOccCategory());
			recordedInvoice.setAccountCodeClientSide(invoiceTemplate.getAccountCodeClientSide());

			recordedInvoice.setAmount(amountWithTax);
			recordedInvoice.setUnMatchingAmount(amountWithTax);
			recordedInvoice.setMatchingAmount(BigDecimal.ZERO);

			recordedInvoice.setAmountWithoutTax(amountWithoutTax);
			recordedInvoice.setTaxAmount(amountTax);
			recordedInvoice.setNetToPay(invoice.getNetToPay());

			try {
				recordedInvoice.setDueDate(DateUtils.setTimeToZero(invoice.getDueDate()));
			} catch (Exception e) {
				log.error("error with due date ", e);
				throw new ImportInvoiceException("Error on DueDate");
			}

			try {
				recordedInvoice.setInvoiceDate(DateUtils.setTimeToZero(invoice.getInvoiceDate()));
				recordedInvoice.setTransactionDate(DateUtils.setTimeToZero(invoice.getInvoiceDate()));
			} catch (Exception e) {
				log.error("error with invoice date", e);
				throw new ImportInvoiceException("Error on invoiceDate");
			}

			recordedInvoice.setPaymentMethod(billingAccount.getPaymentMethod());

			if (billingAccount.getBankCoordinates() != null) {
				recordedInvoice.setPaymentInfo(billingAccount.getBankCoordinates().getIban());
				recordedInvoice.setPaymentInfo1(billingAccount.getBankCoordinates().getBankCode());
				recordedInvoice.setPaymentInfo2(billingAccount.getBankCoordinates().getBranchCode());
				recordedInvoice.setPaymentInfo3(billingAccount.getBankCoordinates().getAccountNumber());
				recordedInvoice.setPaymentInfo4(billingAccount.getBankCoordinates().getKey());
				recordedInvoice.setPaymentInfo5(billingAccount.getBankCoordinates().getBankName());
				recordedInvoice.setPaymentInfo6(billingAccount.getBankCoordinates().getBic());
				recordedInvoice.setBillingAccountName(billingAccount.getBankCoordinates().getAccountOwner());
			}

			recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
			recordedInvoiceService.create(recordedInvoice, currentUser);
			invoice.setRecordedInvoice(recordedInvoice);
			invoice.updateAudit(currentUser);
			invoiceService.updateNoCheck(invoice);
			result.registerSucces();
		} catch (Exception e) {
			log.error("Failed to generate acount operations", e);
			result.registerError(e.getMessage());
		}
	}
}