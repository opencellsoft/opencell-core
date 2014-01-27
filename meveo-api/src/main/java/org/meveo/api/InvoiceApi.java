package org.meveo.api;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InvoiceApi extends BaseApi {

	@Inject
	RecordedInvoiceService recordedInvoiceService;
	
	@Inject
	CustomerAccountService customerAccountService;
	

	@Inject
	OCCTemplateService  oCCTemplateService;
	
	ParamBean paramBean=ParamBean.getInstance();
	
	public void registerInvoice(String customerAccountCode,String providerCode,String invoiceNo, 
			double amount,double amountWithoutTax,double taxAmount,double netToPay, Date date,
			Date dueDate,String bankAccountName,PaymentMethodEnum paymentMethod,String BIC,String IBAN) throws BusinessException {
		Provider provider = providerService.findByCode(providerCode);
		if(provider==null){
			throw new BusinessException("provider code invalid");
		}
		CustomerAccount customerAccount = customerAccountService.findByCode(em, customerAccountCode, provider);
		if(customerAccount==null){
			throw new BusinessException("accountCode code invalid");
		}
		RecordedInvoice invoice = new RecordedInvoice();
		OCCTemplate invoiceTemplate=null;
		try {
			invoiceTemplate = oCCTemplateService.findByCode(paramBean.getProperty("accountOperationsGenerationJob.occCode"),customerAccount.getProvider().getCode());
		} catch (Exception e) {
			throw new BusinessException("Cannot find OCC Template for invoice");
		}
		invoice.setAccountCode(invoiceTemplate.getAccountCode());
		invoice.setOccCode(invoiceTemplate.getCode());
		invoice.setOccDescription(invoiceTemplate.getDescription());
		try {
			invoice.setAmount(new BigDecimal(amount));
		} catch(Exception e){
			throw new BusinessException("Incorrect amount:"+amount);
		}
		try {
			invoice.setAmountWithoutTax(new BigDecimal(amountWithoutTax));
		} catch(Exception e){
			throw new BusinessException("Incorrect amountWithoutTax:"+amountWithoutTax);
		}
		try {
			invoice.setUnMatchingAmount(new BigDecimal(amount));
		} catch(Exception e){
			throw new BusinessException("Incorrect unMatchingAmount:"+amount);
		}
		try {
			invoice.setNetToPay(new BigDecimal(netToPay));
		} catch(Exception e){
			throw new BusinessException("Incorrect netToPay:"+netToPay);
		}
		invoice.setMatchingAmount(BigDecimal.ZERO);
		invoice.setBillingAccountName(bankAccountName);
		invoice.setCustomerAccount(customerAccount);
		invoice.setDueDate(DateUtils.parseDateWithPattern(dueDate, paramBean.getProperty("accountOperationsGenerationJob.dateFormat","dd/MM/yyyy")));
		invoice.setInvoiceDate(date);
		invoice.setPaymentMethod(paymentMethod);
		if(paymentMethod==PaymentMethodEnum.DIRECTDEBIT){
			invoice.setPaymentInfo(IBAN);
			invoice.setPaymentInfo1(BIC);
		}
		invoice.setProvider(provider);
		invoice.setTaxAmount(new BigDecimal(taxAmount));
		invoice.setReference(invoiceNo);
		recordedInvoiceService.create(invoice);
	}

}
