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
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceTypeEnum;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.OCCTemplateService;
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
	private OCCTemplateService oCCTemplateService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private RecordedInvoiceService recordedInvoiceService;

	@Inject
	private ProviderService providerService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser, Long id) {

		try {
			Invoice invoice = invoiceService.findById(id);
			CustomerAccount customerAccount = null;
			OCCTemplate invoiceTemplate = null;
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
				throw new ImportInvoiceException("Cannot found customerAccount");
			}

			try {
				String occCode = "accountOperationsGenerationJob.occCode";
				String occCodeDefaultValue = "FA_FACT";
				
				if(InvoiceTypeEnum.CREDIT_NOTE_ADJUST      ==  invoice.getInvoiceTypeEnum() ||
				   InvoiceTypeEnum.DEBIT_NODE_ADJUST       ==  invoice.getInvoiceTypeEnum() ||
				   InvoiceTypeEnum.SELF_BILLED_CREDIT_NOTE ==  invoice.getInvoiceTypeEnum() ){
					
					occCode = "accountOperationsGenerationJob.occCodeAdjustement";
					occCodeDefaultValue = "FA_ADJ";
				}
				CustomFieldInstance cfInstance = (CustomFieldInstance) providerService.getCustomFieldOrProperty(occCode,occCodeDefaultValue , customerAccount.getProvider(), true,
								AccountLevelEnum.PROVIDER, currentUser);

				if (cfInstance == null) {
					log.error("Custom Field Instance with code=" + occCode+ " does not exist");
					throw new EntityDoesNotExistsException("Custom Field Instance with code=" + occCode+ " does not exist");
				}

				invoiceTemplate = oCCTemplateService.findByCode(cfInstance.getValueAsString(), customerAccount.getProvider().getCode());

				if (invoiceTemplate == null) {
					log.error("Invoice template with code="+ cfInstance.getValueAsString() + " does not exist");
					throw new EntityDoesNotExistsException("Invoice template with code="+ cfInstance.getValueAsString() + " does not exist");
				}
			} catch (Exception e) {
				log.error("error while getting occ template ", e);
				throw new ImportInvoiceException("Cannot found OCC Template for invoice");
			}

			recordedInvoice.setReference(invoice.getInvoiceNumber());
			recordedInvoice.setAccountCode(invoiceTemplate.getAccountCode());
			recordedInvoice.setOccCode(invoiceTemplate.getCode());
			recordedInvoice.setOccDescription(invoiceTemplate.getDescription());
			recordedInvoice.setTransactionCategory(invoiceTemplate.getOccCategory());
			recordedInvoice.setAccountCodeClientSide(invoiceTemplate.getAccountCodeClientSide());

			try {
				recordedInvoice.setAmount(invoice.getAmountWithTax());
				recordedInvoice.setUnMatchingAmount(invoice.getAmountWithTax());
				recordedInvoice.setMatchingAmount(BigDecimal.ZERO);
			} catch (Exception e) {
				log.error("error with amount with tax", e);
				throw new ImportInvoiceException("Error on amountWithTax");
			}

			try {
				recordedInvoice.setAmountWithoutTax(invoice.getAmountWithoutTax());
			} catch (Exception e) {
				log.error("error with amount without tax", e);
				throw new ImportInvoiceException("Error on amountWithoutTax");
			}

			try {
				recordedInvoice.setNetToPay(invoice.getNetToPay());
			} catch (Exception e) {
				log.error("error with netToPay", e);
				throw new ImportInvoiceException("Error on netToPay");
			}

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

			try {
				recordedInvoice.setPaymentMethod(billingAccount.getPaymentMethod());
			} catch (Exception e) {
				log.error("erro with payment method", e);
				throw new ImportInvoiceException("Error on paymentMethod");
			}

			try {
				recordedInvoice.setTaxAmount(invoice.getAmountTax());
			} catch (Exception e) {
				log.error("error with total tax", e);
				throw new ImportInvoiceException("Error on total tax");
			}

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
			recordedInvoiceService.create(recordedInvoice, currentUser, currentUser.getProvider());
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