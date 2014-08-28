package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceDto;
import org.meveo.api.dto.RatedTransactionDTO;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * @author R.AITYAAZZA
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InvoiceApi extends BaseApi {

	@Inject
	RecordedInvoiceService recordedInvoiceService;

	@Inject
	ProviderService providerService;

	@Inject
	CustomerAccountService customerAccountService;

	@Inject
	BillingAccountService billingAccountService;
	@Inject
	BillingRunService billingRunService;

	@Inject
	InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	RatedTransactionService ratedTransactionService;

	@Inject
	OCCTemplateService oCCTemplateService;

	@Inject
	private InvoiceAgregateService invoiceAgregateService;

	@Inject
	InvoiceService invoiceService;

	@Inject
	TaxService taxService;

	ParamBean paramBean = ParamBean.getInstance();

	public void createInvoice(InvoiceDto invoiceDTO) throws BusinessException {
		User currentUser = invoiceDTO.getCurrentUser();
		Provider provider = currentUser.getProvider();
		if (invoiceDTO.getSubCategoryInvoiceAgregates().size() > 0
				&& !StringUtils.isBlank(invoiceDTO.getBillingAccountCode())
				&& !StringUtils.isBlank(invoiceDTO.getDueDate())
				&& !StringUtils.isBlank(invoiceDTO.getAmountTax())
				&& !StringUtils.isBlank(invoiceDTO.getAmountWithoutTax())
				&& !StringUtils.isBlank(invoiceDTO.getAmountWithTax())) {
			BillingAccount billingAccount = billingAccountService.findByCode(
					em, invoiceDTO.getBillingAccountCode(), provider);

			// FIXME : store that in SubCategoryInvoiceAgregateDto
			String invoiceSubCategoryCode = paramBean.getProperty(
					"invoiceSubCategory.code.default", "");

			// FIXME : store that in SubCategoryInvoiceAgregateDto
			String taxCode = paramBean.getProperty("tax.code.default", "");

			Tax tax = taxService.findByCode(em, taxCode);
			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(em, invoiceSubCategoryCode);
			BillingRun br = new BillingRun();
			br.setStartDate(new Date());
			br.setProvider(provider);
			br.setStatus(BillingRunStatusEnum.VALIDATED);
			billingRunService.create(em, br, currentUser, provider);

			Invoice invoice = new Invoice();
			invoice.setBillingAccount(billingAccount);
			invoice.setBillingRun(br);
			invoice.setAuditable(br.getAuditable());
			invoice.setProvider(provider);
			Date invoiceDate = new Date();
			invoice.setInvoiceDate(invoiceDate);
			invoice.setDueDate(invoiceDTO.getDueDate());
			invoice.setPaymentMethod(billingAccount.getPaymentMethod());
			invoice.setAmount(invoiceDTO.getAmount());
			invoice.setAmountTax(invoiceDTO.getAmountTax());
			invoice.setAmountWithoutTax(invoiceDTO.getAmountWithoutTax());
			invoice.setAmountWithTax(invoiceDTO.getAmountWithTax());
			invoice.setDiscount(invoiceDTO.getDiscount());
			invoice.setComment(invoiceDTO.getComment());

			invoiceService.create(em, invoice, currentUser, provider);
			UserAccount userAccount = billingAccount.getDefaultUserAccount();

			for (SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDTO : invoiceDTO
					.getSubCategoryInvoiceAgregates()) {
				if (subCategoryInvoiceAgregateDTO.getRatedTransactions().size() > 0
						&& !StringUtils.isBlank(subCategoryInvoiceAgregateDTO
								.getItemNumber())
						&& !StringUtils.isBlank(subCategoryInvoiceAgregateDTO
								.getAmountTax())
						&& !StringUtils.isBlank(subCategoryInvoiceAgregateDTO
								.getAmountWithoutTax())
						&& !StringUtils.isBlank(subCategoryInvoiceAgregateDTO
								.getAmountWithTax())) {

					SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
					subCategoryInvoiceAgregate
							.setAmountWithoutTax(subCategoryInvoiceAgregateDTO
									.getAmountWithoutTax());
					subCategoryInvoiceAgregate
							.setAmountWithTax(subCategoryInvoiceAgregateDTO
									.getAmountWithTax());
					subCategoryInvoiceAgregate
							.setAmountTax(subCategoryInvoiceAgregateDTO
									.getAmountTax());
					subCategoryInvoiceAgregate
							.setAccountingCode(subCategoryInvoiceAgregateDTO
									.getAccountingCode());
					subCategoryInvoiceAgregate
							.setBillingAccount(billingAccount);
					subCategoryInvoiceAgregate.setUserAccount(userAccount);
					subCategoryInvoiceAgregate.setBillingRun(br);
					subCategoryInvoiceAgregate.setInvoice(invoice);
					subCategoryInvoiceAgregate.setSubCategoryTax(tax);
					subCategoryInvoiceAgregate
							.setItemNumber(subCategoryInvoiceAgregateDTO
									.getItemNumber());
					subCategoryInvoiceAgregate
							.setInvoiceSubCategory(invoiceSubCategory);
					subCategoryInvoiceAgregate.setWallet(userAccount
							.getWallet());

					CategoryInvoiceAgregate categoryInvoiceAgregate = new CategoryInvoiceAgregate();
					categoryInvoiceAgregate
							.setAmountWithTax(subCategoryInvoiceAgregateDTO
									.getAmountWithTax());
					categoryInvoiceAgregate
							.setAmountWithoutTax(subCategoryInvoiceAgregateDTO
									.getAmountWithoutTax());
					categoryInvoiceAgregate
							.setAmountTax(subCategoryInvoiceAgregateDTO
									.getAmountTax());
					categoryInvoiceAgregate.setBillingAccount(billingAccount);
					categoryInvoiceAgregate.setBillingRun(br);
					categoryInvoiceAgregate.setInvoice(invoice);
					categoryInvoiceAgregate
							.setItemNumber(subCategoryInvoiceAgregateDTO
									.getItemNumber());
					categoryInvoiceAgregate.setUserAccount(billingAccount
							.getDefaultUserAccount());
					categoryInvoiceAgregate
							.setInvoiceCategory(invoiceSubCategory
									.getInvoiceCategory());
					invoiceAgregateService.create(em, categoryInvoiceAgregate,
							currentUser, provider);

					TaxInvoiceAgregate taxInvoiceAgregate = new TaxInvoiceAgregate();
					taxInvoiceAgregate
							.setAmountWithoutTax(subCategoryInvoiceAgregateDTO
									.getAmountWithoutTax());
					taxInvoiceAgregate
							.setAmountTax(subCategoryInvoiceAgregateDTO
									.getAmountTax());
					taxInvoiceAgregate
							.setAmountWithTax(subCategoryInvoiceAgregateDTO
									.getAmountWithTax());
					taxInvoiceAgregate
							.setTaxPercent(subCategoryInvoiceAgregateDTO
									.getTaxPercent());
					taxInvoiceAgregate.setBillingAccount(billingAccount);
					taxInvoiceAgregate.setBillingRun(br);
					taxInvoiceAgregate.setInvoice(invoice);
					taxInvoiceAgregate.setUserAccount(billingAccount
							.getDefaultUserAccount());
					taxInvoiceAgregate
							.setItemNumber(subCategoryInvoiceAgregateDTO
									.getItemNumber());
					taxInvoiceAgregate.setTax(tax);
					invoiceAgregateService.create(em, taxInvoiceAgregate,
							currentUser, provider);

					subCategoryInvoiceAgregate
							.setCategoryInvoiceAgregate(categoryInvoiceAgregate);
					subCategoryInvoiceAgregate
							.setTaxInvoiceAgregate(taxInvoiceAgregate);
					invoiceAgregateService.create(em,
							subCategoryInvoiceAgregate, currentUser, provider);

					for (RatedTransactionDTO ratedTransaction : subCategoryInvoiceAgregateDTO
							.getRatedTransactions()) {
						RatedTransaction meveoRatedTransaction = new RatedTransaction(
								null, ratedTransaction.getUsageDate(),
								ratedTransaction.getUnitAmountWithoutTax(),
								ratedTransaction.getUnitAmountWithTax(),
								ratedTransaction.getUnitAmountTax(),
								ratedTransaction.getQuantity(),
								ratedTransaction.getAmountWithoutTax(),
								ratedTransaction.getAmountWithTax(),
								ratedTransaction.getAmountTax(),
								RatedTransactionStatusEnum.BILLED, provider,
								null, billingAccount, invoiceSubCategory, null,
								null, null);
						meveoRatedTransaction.setCode(ratedTransaction
								.getCode());
						meveoRatedTransaction.setDescription(ratedTransaction
								.getDescription());
						meveoRatedTransaction.setBillingRun(br);
						meveoRatedTransaction.setInvoice(invoice);
						meveoRatedTransaction
								.setWallet(userAccount.getWallet());
						ratedTransactionService.create(em,
								meveoRatedTransaction, currentUser, provider);

					}
				} else {
					StringBuilder sb = new StringBuilder(
							"Missing value for the following parameters ");
					List<String> missingFields = new ArrayList<String>();

					if (subCategoryInvoiceAgregateDTO.getRatedTransactions()
							.size() <= 0) {
						missingFields.add("Rated Transactions");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getItemNumber())) {
						missingFields.add("Item Number");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getAmountTax())) {
						missingFields.add("Tax Amount");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getAmountWithoutTax())) {
						missingFields.add("Amount Without Tax");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getAmountWithTax())) {
						missingFields.add("Amount With Tax");
					}

					if (missingFields.size() > 1) {
						sb.append(org.apache.commons.lang.StringUtils.join(
								missingFields.toArray(), ", "));
					}
					sb.append(".");

					throw new BusinessException(sb.toString());
				}

			}
		} else {

			StringBuilder sb = new StringBuilder(
					"Missing value for the following parameters ");
			List<String> missingFields = new ArrayList<String>();

			if (invoiceDTO.getSubCategoryInvoiceAgregates().size() > 0) {
				missingFields.add("Subcategory Invoice Agregates");
			}
			if (StringUtils.isBlank(invoiceDTO.getBillingAccountCode())) {
				missingFields.add("Billing Account Code");
			}
			if (StringUtils.isBlank(invoiceDTO.getDueDate())) {
				missingFields.add("Due Date");
			}
			if (StringUtils.isBlank(invoiceDTO.getAmountTax())) {
				missingFields.add("Amount Tax");
			}
			if (StringUtils.isBlank(invoiceDTO.getAmountWithoutTax())) {
				missingFields.add("Amount Without Tax");
			}
			if (StringUtils.isBlank(invoiceDTO.getAmountWithTax())) {
				missingFields.add("Amount With Tax");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			}
			sb.append(".");

			throw new BusinessException(sb.toString());
		}

	}

	public List<InvoiceDto> getInvoiceList(String customerAccountCode,
			User currentUser) throws Exception {
		List<InvoiceDto> customerInvoiceDtos = new ArrayList<InvoiceDto>();

		if (!StringUtils.isBlank(customerAccountCode)) {
			Provider provider = currentUser.getProvider();
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
					InvoiceDto customerInvoiceDto = new InvoiceDto();
					customerInvoiceDto.setBillingAccountCode(billingAccount
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
					customerInvoiceDto.setPDFpresent(invoices.getPdf()!=null);
					SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = null;

					for (InvoiceAgregate invoiceAgregate : invoices
							.getInvoiceAgregates()) {
						
						subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();
						
						if(invoiceAgregate instanceof CategoryInvoiceAgregate){
							subCategoryInvoiceAgregateDto.setType("R");
						}else if(invoiceAgregate instanceof SubCategoryInvoiceAgregate){
							subCategoryInvoiceAgregateDto.setType("F");
						}else if(invoiceAgregate instanceof TaxInvoiceAgregate){
							subCategoryInvoiceAgregateDto.setType("T");
						}
						
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
						customerInvoiceDto.getSubCategoryInvoiceAgregates()
								.add(subCategoryInvoiceAgregateDto);
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
