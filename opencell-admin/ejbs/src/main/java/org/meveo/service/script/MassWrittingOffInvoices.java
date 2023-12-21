package org.meveo.service.script;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.WriteOff;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MassWrittingOffInvoices extends Script {
	
	private static final String RECORD_VARIABLE_NAME = "record";
	private static final String INVOICE_NUMBER = "INVOICE_NUMBER";
	private static final String INVOICE_AMOUNT = "INVOICE_AMOUNT";
	private static final String UNCOLLECTIBILITY_CERTIFICATE_NUMBER = "UNCOLLECTIBILITY_CERTIFICATE_NUMBER";
	final String EXP_WRT = "EXP_WRT";
	
	private InvoiceService invoiceService = getServiceInterface("InvoiceService");
	private AccountOperationService accountOperationService = getServiceInterface("AccountOperationService");
	private AccountingCodeService accountingCodeService = getServiceInterface(AccountingCodeService.class.getSimpleName());
	private MatchingCodeService matchingCodeService = getServiceInterface(MatchingCodeService.class.getSimpleName());
	private OCCTemplateService occTemplateService = getServiceInterface(OCCTemplateService.class.getSimpleName());
	
	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
		Date currentDate = new Date();
		if(MapUtils.isEmpty(recordMap)) {
			log.warn("The list of invoice is empty!");
			return;
		}
		String invoiceNumber = (String) recordMap.get(INVOICE_NUMBER);
		Invoice invoice = getInvoice(invoiceNumber);
		if(invoice == null) {
			log.warn("the invoice number " + invoiceNumber + " doesn't exist");
			return;
		}
		boolean isValid = isEligibleToWriteOff(invoice);
		if(!isValid) {
			log.warn("the invoice number : " + invoiceNumber + " is with incorrect payment status ");
			return;
		}
		AccountOperation accountOperation = createAccountOperationFromInvoice(invoice, new BigDecimal((String) recordMap.get(INVOICE_AMOUNT)), currentDate);
		List<Long> operations = Optional.ofNullable(accountOperationService.listByInvoice(invoice).stream().map(AccountOperation::getId).collect(Collectors.toList())).orElse(Collections.emptyList());
		matchOperation(accountOperation, operations);
		
		//invoice.setPaymentStatus(InvoicePaymentStatusEnum.ABANDONED);
		invoice.setPaymentStatusDate(currentDate);
		invoice.setCertificateUncollectibilityNumber((String) recordMap.get(UNCOLLECTIBILITY_CERTIFICATE_NUMBER));
		
		invoiceService.updateNoCheck(invoice);
	}
	
	private Invoice getInvoice(String invoiceNumber) {
		if(StringUtils.isEmpty(invoiceNumber)){
			throw new BusinessException("the invoice number is required!!");
		}
		return invoiceService.findByInvoiceNumber(invoiceNumber);
	}
	
	private boolean isEligibleToWriteOff(Invoice invoice) {
		if(invoice.getStatus() != InvoiceStatusEnum.VALIDATED) {
			log.warn("The invoice number : " + invoice.getInvoiceNumber() + "  is not valid!");
			return false;
		}
		List<String> paymentStatus = List.of(InvoicePaymentStatusEnum.PENDING.name(),
											InvoicePaymentStatusEnum.UNPAID.name(),
											InvoicePaymentStatusEnum.PPAID.name(),
											InvoicePaymentStatusEnum.DISPUTED.name());
		return paymentStatus.contains(invoice.getPaymentStatus().name());
	}
	
	private AccountOperation createAccountOperationFromInvoice(Invoice invoice, BigDecimal amount, Date currentDate) {
		AccountOperation writeOff = new WriteOff();
		CustomerAccount customerAccount = invoice.getBillingAccount() != null ? invoice.getBillingAccount().getCustomerAccount() : null;
		writeOff.setCustomerAccount(customerAccount);
		writeOff.setAmount(amount);
		writeOff.setDueDate(invoice.getDueDate());
		// TODO : call matching and set matchingAmount & matchingStatus & unmatching Amount
		
		
		OCCTemplate occTemplate = occTemplateService.findByCode(EXP_WRT);
		if(occTemplate == null) {
			throw new EntityDoesNotExistsException(AccountingCode.class, EXP_WRT);
		}
		writeOff.setAccountingCode(occTemplate.getAccountingCode());
		writeOff.setCode(EXP_WRT);
		writeOff.setDescription(occTemplate.getAccountingCode() != null ? occTemplate.getAccountingCode().getDescription(): null);
		writeOff.setReference(invoice.getAlias());
		writeOff.setTransactionCategory(occTemplate.getOccCategory());
		writeOff.setTransactionDate(currentDate);
		writeOff.setPaymentMethod(invoice.getPaymentMethodType());
		writeOff.setAmountWithoutTax(invoice.getAmountWithoutTax());
		writeOff.setTaxAmount(invoice.getAmountTax());
		writeOff.setUuid(UUID.randomUUID().toString());
		writeOff.setSeller(invoice.getSeller());
		writeOff.setSubscription(invoice.getSubscription());
		writeOff.setInvoices(List.of(invoice));
		writeOff.setStatus(AccountOperationStatus.POSTED);
		writeOff.setAccountingDate(currentDate);
		writeOff.setTransactionalAmount(amount);
		//writeOff.setTransactionalTaxAmount(invoice.getTransactionalAmountWithTax());
		writeOff.setTransactionalCurrency(invoice.getTradingCurrency());
		writeOff.setAppliedRate(invoice.getAppliedRate());
		writeOff.setAppliedRateDate(invoice.getLastAppliedRateDate());
		writeOff.setMatchingStatus(MatchingStatusEnum.O);
		writeOff.setTransactionalMatchingAmount(amount);
		writeOff.setTransactionalUnMatchingAmount(invoice.getAmountWithTax());
		
		accountOperationService.create(writeOff);
		return writeOff;
	}
	
	private void matchOperation(AccountOperation accountOperation, List<Long> accountOperations) {
		try {
			accountOperations.add(accountOperation.getId());
			matchingCodeService.matchOperations(accountOperation.getCustomerAccount().getId(), null, accountOperations, null);
		} catch (Exception e) {
			log.error("Error while matching writeOff:", e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
}
