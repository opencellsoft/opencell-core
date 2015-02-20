package org.meveo.api.invoice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
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
import org.meveo.util.MeveoParamBean;

@Stateless
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

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	public String create(InvoiceDto invoiceDTO, User currentUser)
			throws MeveoApiException {
		Provider provider = currentUser.getProvider();

		if (invoiceDTO.getSubCategoryInvoiceAgregates().size() > 0
				&& !StringUtils.isBlank(invoiceDTO.getBillingAccountCode())
				&& !StringUtils.isBlank(invoiceDTO.getDueDate())
				&& !StringUtils.isBlank(invoiceDTO.getAmountTax())
				&& !StringUtils.isBlank(invoiceDTO.getAmountWithoutTax())
				&& !StringUtils.isBlank(invoiceDTO.getAmountWithTax())) {
			BillingAccount billingAccount = billingAccountService.findByCode(
					invoiceDTO.getBillingAccountCode(), provider);

			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class,
						invoiceDTO.getBillingAccountCode());
			}

			// FIXME : store that in SubCategoryInvoiceAgregateDto
			String invoiceSubCategoryCode = paramBean.getProperty(
					"api.default.invoiceSubCategory.code", "SUB_DATA");

			// FIXME : store that in SubCategoryInvoiceAgregateDto
			String taxCode = paramBean.getProperty("api.default.tax.code", "");

			if (StringUtils.isBlank(taxCode)) {
				throw new MeveoApiException(
						"Property api.default.tax.code must be set.");
			}

			Tax tax = taxService.findByCode(taxCode, provider);
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class, taxCode);
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(invoiceSubCategoryCode);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class, invoiceSubCategoryCode);
			}

			Invoice invoice = new Invoice();
			invoice.setBillingAccount(billingAccount);
			invoice.setProvider(provider);
			Date invoiceDate = new Date();
			invoice.setInvoiceDate(invoiceDate);
			invoice.setDueDate(invoiceDTO.getDueDate());
			invoice.setPaymentMethod(billingAccount.getPaymentMethod());
			invoice.setAmountTax(invoiceDTO.getAmountTax());
			invoice.setAmountWithoutTax(invoiceDTO.getAmountWithoutTax());
			invoice.setAmountWithTax(invoiceDTO.getAmountWithTax());
			invoice.setDiscount(invoiceDTO.getDiscount());
			invoice.setInvoiceNumber(invoiceService.getInvoiceNumber(invoice));
			invoiceService.create(invoice, currentUser, provider);
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
					categoryInvoiceAgregate.setInvoice(invoice);
					categoryInvoiceAgregate
							.setItemNumber(subCategoryInvoiceAgregateDTO
									.getItemNumber());
					categoryInvoiceAgregate.setUserAccount(billingAccount
							.getDefaultUserAccount());
					categoryInvoiceAgregate
							.setInvoiceCategory(invoiceSubCategory
									.getInvoiceCategory());
					invoiceAgregateService.create(categoryInvoiceAgregate,
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
					taxInvoiceAgregate.setInvoice(invoice);
					taxInvoiceAgregate.setUserAccount(billingAccount
							.getDefaultUserAccount());
					taxInvoiceAgregate
							.setItemNumber(subCategoryInvoiceAgregateDTO
									.getItemNumber());
					taxInvoiceAgregate.setTax(tax);
					invoiceAgregateService.create(taxInvoiceAgregate,
							currentUser, provider);

					subCategoryInvoiceAgregate
							.setCategoryInvoiceAgregate(categoryInvoiceAgregate);
					subCategoryInvoiceAgregate
							.setTaxInvoiceAgregate(taxInvoiceAgregate);
					invoiceAgregateService.create(subCategoryInvoiceAgregate,
							currentUser, provider);

					for (RatedTransactionDto ratedTransaction : subCategoryInvoiceAgregateDTO
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
								null, null, null,null,null);
						meveoRatedTransaction.setCode(ratedTransaction
								.getCode());
						meveoRatedTransaction.setDescription(ratedTransaction
								.getDescription());
						meveoRatedTransaction
								.setUnityDescription(ratedTransaction
										.getUnityDescription());
						meveoRatedTransaction.setInvoice(invoice);
						meveoRatedTransaction
								.setWallet(userAccount.getWallet());
						ratedTransactionService.create(meveoRatedTransaction,
								currentUser, provider);

					}
				} else {
					if (subCategoryInvoiceAgregateDTO.getRatedTransactions()
							.size() <= 0) {
						missingParameters.add("ratedTransactions");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getItemNumber())) {
						missingParameters.add("itemNumber");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getAmountTax())) {
						missingParameters.add("amountTax");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getAmountWithoutTax())) {
						missingParameters.add("amountWithoutTax");
					}
					if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO
							.getAmountWithTax())) {
						missingParameters.add("amountWithTax");
					}

					throw new MissingParameterException(
							getMissingParametersExceptionMessage());
				}
			}
			return invoice.getInvoiceNumber();
		} else {
			if (invoiceDTO.getSubCategoryInvoiceAgregates().size() <= 0) {
				missingParameters.add("subCategoryInvoiceAgregates");
			}
			if (StringUtils.isBlank(invoiceDTO.getBillingAccountCode())) {
				missingParameters.add("billingAccountCode");
			}
			if (StringUtils.isBlank(invoiceDTO.getDueDate())) {
				missingParameters.add("dueDate");
			}
			if (StringUtils.isBlank(invoiceDTO.getAmountTax())) {
				missingParameters.add("amountTax");
			}
			if (StringUtils.isBlank(invoiceDTO.getAmountWithoutTax())) {
				missingParameters.add("amountWithoutTax");
			}
			if (StringUtils.isBlank(invoiceDTO.getAmountWithTax())) {
				missingParameters.add("amountWithTax");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public List<InvoiceDto> list(String customerAccountCode, Provider provider)
			throws MeveoApiException {
		List<InvoiceDto> customerInvoiceDtos = new ArrayList<InvoiceDto>();

		if (!StringUtils.isBlank(customerAccountCode)) {
			CustomerAccount customerAccount = customerAccountService
					.findByCode(customerAccountCode, provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class,
						customerAccountCode);
			}

			for (BillingAccount billingAccount : customerAccount
					.getBillingAccounts()) {
				List<Invoice> invoiceList = billingAccount.getInvoices();

				for (Invoice invoice : invoiceList) {
					InvoiceDto customerInvoiceDto = new InvoiceDto();
					customerInvoiceDto.setBillingAccountCode(billingAccount
							.getCode());
					customerInvoiceDto.setInvoiceDate(invoice.getInvoiceDate());
					customerInvoiceDto.setDueDate(invoice.getDueDate());

					customerInvoiceDto.setAmountWithoutTax(invoice
							.getAmountWithoutTax());
					customerInvoiceDto.setAmountTax(invoice.getAmountTax());
					customerInvoiceDto.setAmountWithTax(invoice
							.getAmountWithTax());
					customerInvoiceDto.setInvoiceNumber(invoice
							.getInvoiceNumber());
					customerInvoiceDto.setPaymentMathod(invoice
							.getPaymentMethod().toString());
					customerInvoiceDto.setPDFpresent(invoice.getPdf() != null);
					SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = null;

					for (InvoiceAgregate invoiceAgregate : invoice
							.getInvoiceAgregates()) {

						subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();

						if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
							subCategoryInvoiceAgregateDto.setType("R");
						} else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
							subCategoryInvoiceAgregateDto.setType("F");
						} else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
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
			if (StringUtils.isBlank(customerAccountCode)) {
				missingParameters.add("CustomerAccountCode");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return customerInvoiceDtos;
	}
}
