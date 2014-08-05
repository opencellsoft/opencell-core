package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomerInvoiceDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CustomerInvoiceApi extends BaseApi {

	@Inject
	RecordedInvoiceService recordedInvoiceService;

	@Inject
	ProviderService providerService;

	@Inject
	BillingAccountService billingAccountService;

	@Inject
	private CustomerAccountService customerAccountService;

	public List<CustomerInvoiceDto> getInvoiceList(String customerAccountCode,
			String providerCode) throws Exception {

		List<CustomerInvoiceDto> customerInvoiceDtos = new ArrayList<CustomerInvoiceDto>();

		if (!StringUtils.isBlank(customerAccountCode)
				&& !StringUtils.isBlank(providerCode)) {
			Provider provider = providerService.findByCode(em, providerCode);
			CustomerAccount customerAccount = customerAccountService
					.findByCode(em, customerAccountCode, provider);
			if (customerAccount == null) {
				throw new BusinessException(
						"Cannot find customer account with code="
								+ customerAccountCode);
			}

			for (BillingAccount billingAccount : customerAccount
					.getBillingAccounts()) {
				List<Invoice> invoiceList = billingAccount.getInvoices();

				for (Invoice invoices : invoiceList) {
					CustomerInvoiceDto customerInvoiceDto = new CustomerInvoiceDto();
					customerInvoiceDto.setBillingAccount(billingAccount
							.getCode());
					customerInvoiceDto
							.setInvoiceDate(invoices.getInvoiceDate());
					customerInvoiceDto.setDueDate(invoices.getDueDate());
					customerInvoiceDto.setAmount(invoices.getAmount());
					customerInvoiceDto.setAmount(invoices.getAmount());
					customerInvoiceDto.setAmountWithoutTax(invoices
							.getAmountWithoutTax());
					customerInvoiceDto.setInvoiceNumber(invoices
							.getInvoiceNumber());
					customerInvoiceDto
							.setProductDate(invoices.getProductDate());
					customerInvoiceDto.setNetToPay(invoices.getNetToPay());
					customerInvoiceDto.setPaymentMethod(invoices
							.getPaymentMethod().toString() != null ? invoices
							.getPaymentMethod().toString() : null);
					customerInvoiceDto.setIban(invoices.getIban());
					customerInvoiceDto.setAlias(invoices.getAlias());
					/*customerInvoiceDto.setPdf(invoices.getPdf());*/
					customerInvoiceDto
							.setInvoiceType(invoices.getInvoiceType());
					SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();

					for (InvoiceAgregate invoiceAgregate : invoices
							.getInvoiceAgregates()) {
						subCategoryInvoiceAgregateDto
								.setItemNumber(invoiceAgregate.getItemNumber());
						subCategoryInvoiceAgregateDto
								.setAccountingCode(invoiceAgregate
										.getAccountingCode());
						subCategoryInvoiceAgregateDto
								.setDescription(invoiceAgregate
										.getDescription());
						subCategoryInvoiceAgregateDto
								.setQuantity(invoiceAgregate.getQuantity());
						subCategoryInvoiceAgregateDto
								.setDiscount(invoiceAgregate.getDiscount());
						subCategoryInvoiceAgregateDto
								.setAmountWithoutTax(invoiceAgregate
										.getAmountWithoutTax());
						subCategoryInvoiceAgregateDto
								.setAmountTax(invoiceAgregate.getAmountTax());
						subCategoryInvoiceAgregateDto
								.setAmountWithTax(invoiceAgregate
										.getAmountWithTax());
						customerInvoiceDto
								.addSubCategoryInvoiceAgregates(subCategoryInvoiceAgregateDto);
					}

					customerInvoiceDtos.add(customerInvoiceDto);
				}
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(customerAccountCode)) {
				missingFields.add("CustomerAccountCode");
			}

			if (StringUtils.isBlank(providerCode)) {
				missingFields.add("providerCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());

		}

		return customerInvoiceDtos;
	}

}