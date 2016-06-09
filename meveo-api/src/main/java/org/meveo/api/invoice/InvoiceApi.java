package org.meveo.api.invoice;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
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
	XMLInvoiceCreator xmlInvoiceCreator;

	@Inject
	private PDFParametersConstruction pDFParametersConstruction;

	@Inject
	private InvoiceTypeService invoiceTypeService;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CreateInvoiceResponseDto create(InvoiceDto invoiceDTO, User currentUser) throws MeveoApiException, BusinessException, Exception {
		log.debug("InvoiceDto:"+JsonUtils.toJson(invoiceDTO));
		validateInvoiceDto(invoiceDTO, currentUser);						 
		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(currentUser);
		Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();
		Provider provider = currentUser.getProvider();
		BillingAccount billingAccount = billingAccountService.findByCode(invoiceDTO.getBillingAccountCode(), provider);
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, invoiceDTO.getBillingAccountCode());
		}
		InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceDTO.getInvoiceType(), provider);
		if (invoiceType == null) {
			throw new EntityDoesNotExistsException(InvoiceType.class, invoiceDTO.getInvoiceType());
		}

		BigDecimal invoiceAmountWithoutTax = BigDecimal.ZERO;
		BigDecimal invoiceAmountTax = BigDecimal.ZERO;
		BigDecimal invoiceAmountWithTax = BigDecimal.ZERO;
		Invoice invoice = new Invoice();
		invoice.setBillingAccount(billingAccount);
		invoice.setAuditable(auditable);

		invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
		invoice.setDueDate(invoiceDTO.getDueDate());
		PaymentMethodEnum paymentMethod = billingAccount.getPaymentMethod();
		if (paymentMethod == null) {
			paymentMethod = billingAccount.getCustomerAccount().getPaymentMethod();
		}
		invoice.setPaymentMethod(paymentMethod);
		invoice.setInvoiceType(invoiceType);

		if (invoiceDTO.getListInvoiceNumbersToLink().isEmpty()) {
			for (String invvoiceNumber : invoiceDTO.getListInvoiceNumbersToLink()) {
				Invoice invoiceTmp = invoiceService.getInvoiceByNumber(invvoiceNumber, provider.getCode());
				if (invoiceTmp == null) {
					throw new EntityDoesNotExistsException(Invoice.class, invvoiceNumber);
				}
				if (invoiceType.getAppliesTo().contains(invoiceTmp.getInvoiceType())) {
					throw new BusinessApiException("Invoice " + invvoiceNumber + " cant be linked");
				}
				invoice.getInvoiceAdjustments().add(invoiceTmp);
			}
		}
		invoiceService.create(invoice, currentUser);
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			throw new BusinessApiException("BillingAccount " + invoiceDTO.getBillingAccountCode() + " has no userAccount");
		}
		// TODO : userAccount on dto ?
		UserAccount userAccount = userAccounts.get(0);
		for (CategoryInvoiceAgregateDto catInvAgrDto : invoiceDTO.getCategoryInvoiceAgregates()) {
			BigDecimal catAmountWithoutTax = BigDecimal.ZERO;
			BigDecimal catAmountTax = BigDecimal.ZERO;
			BigDecimal catAmountWithTax = BigDecimal.ZERO;
			CategoryInvoiceAgregate invoiceAgregateCat = new CategoryInvoiceAgregate();
			invoiceAgregateCat.setAuditable(auditable);
			invoiceAgregateCat.setInvoice(invoice);
			invoiceAgregateCat.setBillingRun(null);
			invoiceAgregateCat.setDescription(catInvAgrDto.getDescription());
			invoiceAgregateCat.setItemNumber(catInvAgrDto.getListSubCategoryInvoiceAgregateDto().size());
			invoiceAgregateCat.setUserAccount(userAccount);
			invoiceAgregateCat.setBillingAccount(billingAccount);
			invoiceAgregateCat.setInvoiceCategory(invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode(), currentUser.getProvider()));
			invoiceAgregateCat.setUserAccount(userAccount);
			invoiceAgregateService.create(invoiceAgregateCat, currentUser);
			
			for (SubCategoryInvoiceAgregateDto subCatInvAgrDTO : catInvAgrDto.getListSubCategoryInvoiceAgregateDto()) {
				BigDecimal subCatAmountWithoutTax = BigDecimal.ZERO;
				BigDecimal subCatAmountTax = BigDecimal.ZERO;
				BigDecimal subCatAmountWithTax = BigDecimal.ZERO;
				Tax currentTax = null;
				List<Tax> taxes = new ArrayList<Tax>();
				InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(subCatInvAgrDTO.getInvoiceSubCategoryCode(), provider);
				for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
					if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode()) && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, invoice)) {
						if (!taxes.contains(invoicesubcatCountry.getTax())) {
							taxes.add(invoicesubcatCountry.getTax());
						}
						if(currentTax == null){
							currentTax = invoicesubcatCountry.getTax();
						}
					}
				}
				if(currentTax == null){
					throw new BusinessApiException("Cant find tax for InvoiceSubCategory:"+subCatInvAgrDTO.getInvoiceSubCategoryCode());
				}
				for (RatedTransactionDto ratedTransaction : subCatInvAgrDTO.getRatedTransactions()) {
					
					BigDecimal amountWithoutTax = ratedTransaction.getUnitAmountWithoutTax().multiply(ratedTransaction.getQuantity());
					BigDecimal amountWithTax = getAmountWithTax(currentTax, amountWithoutTax);
					BigDecimal amountTax = getAmountTax(amountWithTax, amountWithoutTax);
					
					RatedTransaction meveoRatedTransaction = new RatedTransaction(null, ratedTransaction.getUsageDate(), ratedTransaction.getUnitAmountWithoutTax(),
							ratedTransaction.getUnitAmountWithTax(), ratedTransaction.getUnitAmountTax(), ratedTransaction.getQuantity(), amountWithoutTax,
							amountWithTax, amountTax, RatedTransactionStatusEnum.BILLED, provider, userAccount.getWallet(), billingAccount,
							invoiceSubCategory, null, null, null, null, null, null, null);
					meveoRatedTransaction.setCode(ratedTransaction.getCode());
					meveoRatedTransaction.setDescription(ratedTransaction.getDescription());
					meveoRatedTransaction.setUnityDescription(ratedTransaction.getUnityDescription());
					meveoRatedTransaction.setInvoice(invoice);
					meveoRatedTransaction.setWallet(userAccount.getWallet());
					ratedTransactionService.create(meveoRatedTransaction, currentUser);

					subCatAmountWithoutTax = subCatAmountWithoutTax.add(amountWithoutTax);
					subCatAmountTax = subCatAmountTax.add(amountTax);
					subCatAmountWithTax = subCatAmountWithTax.add(amountWithTax);
				}
				List<RatedTransaction> openedRT = ratedTransactionService.openRTbySubCat(userAccount.getWallet(), invoiceSubCategory);
				for(RatedTransaction ratedTransaction : openedRT){
					subCatAmountWithoutTax = subCatAmountWithoutTax.add(ratedTransaction.getAmountWithoutTax());
					subCatAmountTax = subCatAmountTax.add(ratedTransaction.getAmountTax());
					subCatAmountWithTax = subCatAmountWithTax.add(ratedTransaction.getAmountWithTax());
					ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
					ratedTransaction.setInvoice(invoice);
					ratedTransactionService.update(ratedTransaction, currentUser);
				}
		

				SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
				invoiceAgregateSubcat.setCategoryInvoiceAgregate(invoiceAgregateCat);
				invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCategory);
				invoiceAgregateSubcat.setInvoice(invoice);
				invoiceAgregateSubcat.setDescription(subCatInvAgrDTO.getDescription());
				invoiceAgregateSubcat.setBillingRun(null);
				invoiceAgregateSubcat.setWallet(userAccount.getWallet());
				invoiceAgregateSubcat.setUserAccount(userAccount);
				invoiceAgregateSubcat.setAccountingCode(invoiceSubCategory.getAccountingCode());
				invoiceAgregateSubcat.setAuditable(auditable);
				invoiceAgregateSubcat.setQuantity(BigDecimal.ONE);
				invoiceAgregateSubcat.setTaxPercent(currentTax.getPercent());
				invoiceAgregateSubcat.setSubCategoryTaxes(new HashSet<Tax>( Arrays.asList(currentTax)));
				if (InvoiceModeEnum.DETAILLED.name().equals(invoiceDTO.getInvoiceMode().name())) {
					invoiceAgregateSubcat.setItemNumber(subCatInvAgrDTO.getRatedTransactions().size());
					invoiceAgregateSubcat.setAmountWithoutTax(subCatAmountWithoutTax);
					invoiceAgregateSubcat.setAmountTax(subCatAmountTax);
					invoiceAgregateSubcat.setAmountWithTax(subCatAmountWithTax);
				} else {
					invoiceAgregateSubcat.setAmountWithoutTax(subCatInvAgrDTO.getAmountWithoutTax());					
					invoiceAgregateSubcat.setAmountWithTax(getAmountWithTax(currentTax, subCatInvAgrDTO.getAmountWithoutTax()));
					invoiceAgregateSubcat.setAmountTax(getAmountTax(invoiceAgregateSubcat.getAmountWithTax(), invoiceAgregateSubcat.getAmountWithoutTax()));
				}

				invoiceAgregateService.create(invoiceAgregateSubcat, currentUser);
				for (Tax tax : taxes) {
					TaxInvoiceAgregate invoiceAgregateTax = null;
					Long taxId = tax.getId();

					if (taxInvoiceAgregateMap.containsKey(taxId)) {
						invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
					} else {
						invoiceAgregateTax = new TaxInvoiceAgregate();
						invoiceAgregateTax.setInvoice(invoice);
						invoiceAgregateTax.setBillingRun(null);
						invoiceAgregateTax.setTax(tax);
						invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
						invoiceAgregateTax.setTaxPercent(tax.getPercent());
						invoiceAgregateTax.setUserAccount(userAccount);
						invoiceAgregateTax.setAmountWithoutTax(BigDecimal.ZERO);
						invoiceAgregateTax.setAmountWithTax(BigDecimal.ZERO);
						invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
						invoiceAgregateTax.setBillingAccount(billingAccount);
						invoiceAgregateTax.setUserAccount(userAccount);
						invoiceAgregateTax.setAuditable(auditable);
					}
					invoiceAgregateTax.setAmountWithoutTax(invoiceAgregateTax.getAmountWithoutTax().add(invoiceAgregateSubcat.getAmountWithoutTax()));
					invoiceAgregateTax.setAmountTax(invoiceAgregateTax.getAmountTax().add(invoiceAgregateSubcat.getAmountTax()));
					invoiceAgregateTax.setAmountWithTax(invoiceAgregateTax.getAmountWithTax().add(invoiceAgregateSubcat.getAmountWithTax()));

					taxInvoiceAgregateMap.put(taxId, invoiceAgregateTax);
				}
				catAmountWithoutTax = catAmountWithoutTax.add(invoiceAgregateSubcat.getAmountWithoutTax());
				catAmountTax = catAmountTax.add(invoiceAgregateSubcat.getAmountTax());
				catAmountWithTax = catAmountWithTax.add(invoiceAgregateSubcat.getAmountWithTax());
			}
			
			invoiceAgregateCat.setAmountWithoutTax(catAmountWithoutTax);
			invoiceAgregateCat.setAmountTax(catAmountTax);
			invoiceAgregateCat.setAmountWithTax(catAmountWithTax);

			invoiceAmountWithoutTax = invoiceAmountWithoutTax.add(invoiceAgregateCat.getAmountWithoutTax());
			invoiceAmountTax = invoiceAmountTax.add(invoiceAgregateCat.getAmountTax());
			invoiceAmountWithTax = invoiceAmountWithTax.add(invoiceAgregateCat.getAmountWithTax());
		}

		for (Entry<Long, TaxInvoiceAgregate> entry : taxInvoiceAgregateMap.entrySet()) {
			invoiceAgregateService.create(entry.getValue(), currentUser);
		}

		invoice.setAmountWithoutTax(invoiceAmountWithoutTax);
		invoice.setAmountTax(invoiceAmountTax);
		invoice.setAmountWithTax(invoiceAmountWithTax);
		
		BigDecimal netToPay = invoice.getAmountWithTax();
		if (!provider.isEntreprise() && invoiceDTO.isIncludeBalance()) {
			BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount()
					.getCustomerAccount().getCode(), invoice.getDueDate(), invoice.getProvider());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = invoice.getAmountWithTax().add(balance);
		}
		invoice.setNetToPay(netToPay);
		invoiceService.update(invoice, currentUser);
		// include open RT

		try {
			populateCustomFields(invoiceDTO.getCustomFields(), invoice, true, currentUser, true);

		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw new MeveoApiException("Failed to associate custom field instance to an entity");
		}

		CreateInvoiceResponseDto response = new CreateInvoiceResponseDto();
		response.setInvoiceId(invoice.getId());
		response.setAmountWithoutTax(invoice.getAmountWithoutTax());
		response.setAmountTax(invoice.getAmountTax());
		response.setAmountWithTax(invoice.getAmountWithTax());
		response.setDueDate(invoice.getDueDate());
		response.setInvoiceDate(invoice.getInvoiceDate());
		response.setNetToPay(invoice.getNetToPay());
		
		if (invoiceDTO.isAutoValidation()) {
			response.setInvoiceNumber(validateInvoice(invoice.getId(), currentUser));
		}
		return response;
	}

	public List<InvoiceDto> list(String customerAccountCode, Provider provider) throws MeveoApiException {
		return null;
	}

	public BillingRun launchExceptionalInvoicing(GenerateInvoiceRequestDto generateInvoiceRequestDto, User currentUser, List<Long> BAids) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, BusinessApiException, Exception {
		return billingRunService.launchExceptionalInvoicing(BAids, generateInvoiceRequestDto.getInvoicingDate(), generateInvoiceRequestDto.getLastTransactionDate(), BillingProcessTypesEnum.AUTOMATIC, currentUser);
	}

	public void updateBAtotalAmount(BillingAccount billingAccount, BillingRun billingRun, User currentUser) {
		billingAccountService.updateBillingAccountTotalAmounts(billingAccount, billingRun, currentUser);
		log.debug("updateBillingAccountTotalAmounts ok");
	}

	public void createRatedTransaction(Long billingAccountId, User currentUser, Date invoicingDate) throws Exception {
		ratedTransactionService.createRatedTransaction(billingAccountId, currentUser, invoicingDate);
	}

	public BillingRun updateBR(BillingRun billingRun, BillingRunStatusEnum status, Integer billingAccountNumber, Integer billableBillingAcountNumber, User currentUser) throws BusinessException {
		billingRun.setStatus(status);
		if (billingAccountNumber != null) {
			billingRun.setBillingAccountNumber(billingAccountNumber);
		}
		if (billableBillingAcountNumber != null) {
			billingRun.setBillableBillingAcountNumber(billableBillingAcountNumber);
		}
		return billingRunService.update(billingRun, currentUser);
	}

	public void validateBR(BillingRun billingRun, User user) throws BusinessException {
		billingRunService.forceValidate(billingRun.getId(), user);
	}

	public void createAgregatesAndInvoice(Long billingRunId, Date lastTransactionDate, User currentUser) throws BusinessException, Exception {
		billingRunService.createAgregatesAndInvoice(billingRunId, lastTransactionDate, currentUser, 1, 0);
	}

	/**
	 * Launch all the invoicing process for a given billingAccount, that's mean
	 * : <lu> Create rated transactions <li>Create an exeptionnal billingRun
	 * with given dates <li>Validate the preinvoicing resport <li>Validate the
	 * postinvoicing report <li>Vaidate the BillingRun </lu>
	 * 
	 * @param generateInvoiceRequestDto
	 * @param currentUser
	 * @return The invoiceNumber
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws BusinessException
	 * @throws BusinessApiException
	 * @throws Exception
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public GenerateInvoiceResultDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, BusinessApiException, Exception {

		if (generateInvoiceRequestDto == null) {
			missingParameters.add("generateInvoiceRequest");
			handleMissingParameters();
		}
		if (StringUtils.isBlank(generateInvoiceRequestDto.getBillingAccountCode())) {
			missingParameters.add("billingAccountCode");
		}

		if (generateInvoiceRequestDto.getInvoicingDate() == null) {
			missingParameters.add("invoicingDate");
		}
		if (generateInvoiceRequestDto.getLastTransactionDate() == null) {
			missingParameters.add("lastTransactionDate");
		}

		handleMissingParameters();

		BillingAccount billingAccount = billingAccountService.findByCode(generateInvoiceRequestDto.getBillingAccountCode(), currentUser.getProvider(), Arrays.asList("billingRun"));
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, generateInvoiceRequestDto.getBillingAccountCode());
		}

		if (billingAccount.getBillingRun() != null && (billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.NEW) || billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.PREVALIDATED) || billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.POSTVALIDATED))) {

			throw new BusinessApiException("The billingAccount is already in an billing run with status " + billingAccount.getBillingRun().getStatus());
		}

		List<Long> baIds = new ArrayList<Long>();
		baIds.add(billingAccount.getId());

		createRatedTransaction(billingAccount.getId(), currentUser, generateInvoiceRequestDto.getInvoicingDate());
		log.info("createRatedTransaction ok");

		BillingRun billingRun = launchExceptionalInvoicing(generateInvoiceRequestDto, currentUser, baIds);
		Long billingRunId = billingRun.getId();
		log.info("launchExceptionalInvoicing ok , billingRun.id:" + billingRunId);

		updateBAtotalAmount(billingAccount, billingRun, currentUser);
		log.info("updateBillingAccountTotalAmounts ok");

		billingRun = updateBR(billingRun, BillingRunStatusEnum.PREVALIDATED, 1, 1, currentUser);
		log.info("update billingRun ON_GOING");

		createAgregatesAndInvoice(billingRun.getId(), billingRun.getLastTransactionDate(), currentUser);
		log.info("createAgregatesAndInvoice ok");

		billingRun = updateBR(billingRun, BillingRunStatusEnum.POSTINVOICED, null, null, currentUser);
		log.info("update billingRun POSTINVOICED");

		validateBR(billingRun, currentUser);
		log.info("billingRunService.validate ok");

		List<Invoice> invoices = invoiceService.getInvoices(billingRun);
		log.info((invoices == null) ? "getInvoice is null" : "size=" + invoices.size());
		if (invoices == null || invoices.isEmpty()) {
			throw new BusinessApiException("Can't find invoice");
		}

		GenerateInvoiceResultDto generateInvoiceResultDto = new GenerateInvoiceResultDto();
		generateInvoiceResultDto.setInvoiceNumber(invoices.get(0).getInvoiceNumber());
		return generateInvoiceResultDto;
	}

	public String getXMLInvoice(String invoiceNumber, User currentUser) throws FileNotFoundException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		return getXMLInvoice(invoiceNumber, invoiceTypeService.getDefaultCommertial(currentUser).getCode(), currentUser);
	}

	public String getXMLInvoice(String invoiceNumber, String invoiceTypeCode, User currentUser) throws FileNotFoundException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		log.debug("getXMLInvoice  invoiceNumber:{}", invoiceNumber);
		if (StringUtils.isBlank(invoiceNumber)) {
			missingParameters.add("invoiceNumber");
		}
		if (StringUtils.isBlank(invoiceTypeCode)) {
			missingParameters.add("invoiceTypeCode");
		}
		handleMissingParameters();

		InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode, currentUser.getProvider());
		if (invoiceType == null) {
			throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
		}

		Invoice invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType, currentUser.getProvider());
		if (invoice == null) {
			throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber, "invoiceNumber", invoiceTypeCode, "invoiceTypeCode");
		}
		ParamBean param = ParamBean.getInstance();
		String invoicesDir = param.getProperty("providers.rootDir", "/tmp/meveo");
		String sep = File.separator;
		String invoicePath = invoicesDir + sep + currentUser.getProvider().getCode() + sep + "invoices" + sep + "xml" + sep + (invoice.getBillingRun() == null ? DateUtils.formatDateWithPattern(invoice.getAuditable().getCreated(), paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss")) : invoice.getBillingRun().getId());
		File billingRundir = new File(invoicePath);
		xmlInvoiceCreator.createXMLInvoice(invoice.getId(), billingRundir);
		String xmlCanonicalPath = invoicePath + sep + invoiceNumber + ".xml";
		Scanner scanner = new Scanner(new File(xmlCanonicalPath));
		String xmlContent = scanner.useDelimiter("\\Z").next();
		scanner.close();
		log.debug("getXMLInvoice  invoiceNumber:{} done.", invoiceNumber);
		return xmlContent;
	}

	public byte[] getPdfInvoince(String invoiceNumber, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, Exception {
		return getPdfInvoince(invoiceNumber, invoiceTypeService.getDefaultCommertial(currentUser).getCode(), currentUser);
	}

	public byte[] getPdfInvoince(String invoiceNumber, String invoiceTypeCode, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, Exception {
		log.debug("getPdfInvoince  invoiceNumber:{}", invoiceNumber);
		if (StringUtils.isBlank(invoiceNumber)) {
			missingParameters.add("invoiceNumber");
		}
		if (StringUtils.isBlank(invoiceTypeCode)) {
			missingParameters.add("invoiceTypeCode");
		}
		handleMissingParameters();

		InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode, currentUser.getProvider());
		if (invoiceType == null) {
			throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
		}

		Invoice invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType, currentUser.getProvider());
		if (invoice == null) {
			throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber, "invoiceNumber", invoiceTypeCode, "invoiceTypeCode");
		}
		if (invoice.getPdf() == null) {
			Map<String, Object> parameters = pDFParametersConstruction.constructParameters(invoice.getId(), currentUser, currentUser.getProvider());
			invoiceService.producePdf(parameters, currentUser);
		}
		invoiceService.findById(invoice.getId(), true);
		log.debug("getXMLInvoice invoiceNumber:{} done.", invoiceNumber);
		return invoice.getPdf();
	}

	public String validateInvoice(Long invoiceId, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (StringUtils.isBlank(invoiceId)) {
			missingParameters.add("invoiceId");
		}
		handleMissingParameters();

		Invoice invoice = invoiceService.findById(invoiceId);
		if (invoice == null) {
			throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
		}
		invoice.setInvoiceNumber(invoiceService.getInvoiceNumber(invoice));		
		invoiceService.update(invoice, currentUser);
		return invoice.getInvoiceNumber();
	}

	public void cancelInvoice(Long invoiceId, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, MeveoApiException, BusinessException {
		if (StringUtils.isBlank(invoiceId)) {
			missingParameters.add("invoiceId");
		}
		handleMissingParameters();
		Invoice invoice = invoiceService.findById(invoiceId);
		if (invoice == null) {
			throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
		}
		if (!StringUtils.isBlank(invoice.getInvoiceNumber())) {
			throw new MeveoApiException("Invoice already validated");
		}
		for(RatedTransaction rt : ratedTransactionService.listByInvoice(invoice)) {
			if(rt.getWalletOperationId() != null){
				rt.setStatus(RatedTransactionStatusEnum.OPEN);
				rt.setInvoice(null);
				ratedTransactionService.update(rt,currentUser);
			}else{
				ratedTransactionService.remove(rt);
			}
		}
				
		invoiceService.remove(invoice);
	}

	private void validateInvoiceDto(InvoiceDto invoiceDTO, User currentUser) throws MissingParameterException, EntityDoesNotExistsException {
		if (StringUtils.isBlank(invoiceDTO.getInvoiceMode())) {
			missingParameters.add("invoiceMode");
		}
		if (StringUtils.isBlank(invoiceDTO.getBillingAccountCode())) {
			missingParameters.add("billingAccountCode");
		}
		if (StringUtils.isBlank(invoiceDTO.getDueDate())) {
			missingParameters.add("dueDate");
		}
		if (StringUtils.isBlank(invoiceDTO.getInvoiceDate())) {
			missingParameters.add("invoiceDate");
		}
		if (StringUtils.isBlank(invoiceDTO.getInvoiceType())) {
			missingParameters.add("invoiceType");
		}
		if (StringUtils.isBlank(invoiceDTO.getInvoiceMode())) {
			missingParameters.add("invoiceMode");
		}

		if (StringUtils.isBlank(invoiceDTO.getCategoryInvoiceAgregates()) || invoiceDTO.getCategoryInvoiceAgregates().isEmpty()) {
			missingParameters.add("categoryInvoiceAgregates");
		}

		handleMissingParameters();

		for (CategoryInvoiceAgregateDto catInvAgrDto : invoiceDTO.getCategoryInvoiceAgregates()) {
			if (StringUtils.isBlank(catInvAgrDto.getCategoryInvoiceCode())) {
				missingParameters.add("categoryInvoiceAgregateDto.categoryInvoiceCode");
			}
			if (invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode(), currentUser.getProvider()) == null) {
				throw new EntityDoesNotExistsException(InvoiceSubCategory.class, catInvAgrDto.getCategoryInvoiceCode());
			}
			if (catInvAgrDto.getListSubCategoryInvoiceAgregateDto() == null || catInvAgrDto.getListSubCategoryInvoiceAgregateDto().isEmpty()) {
				missingParameters.add("categoryInvoiceAgregateDto.listSubCategoryInvoiceAgregateDto");
			}
			handleMissingParameters();
			for (SubCategoryInvoiceAgregateDto subCatInvAgrDto : catInvAgrDto.getListSubCategoryInvoiceAgregateDto()) {
				if (StringUtils.isBlank(subCatInvAgrDto.getInvoiceSubCategoryCode())) {
					missingParameters.add("subCategoryInvoiceAgregateDto.invoiceSubCategoryCode");
				}
				if (invoiceSubCategoryService.findByCode(subCatInvAgrDto.getInvoiceSubCategoryCode(), currentUser.getProvider()) == null) {
					throw new EntityDoesNotExistsException(InvoiceSubCategory.class, subCatInvAgrDto.getInvoiceSubCategoryCode());
				}

				if (InvoiceModeEnum.DETAILLED.name().equals(invoiceDTO.getInvoiceMode().name())) {
					if (subCatInvAgrDto.getRatedTransactions() == null || subCatInvAgrDto.getRatedTransactions().isEmpty()) {
						missingParameters.add("ratedTransactions");
					}
					handleMissingParameters();
					for (RatedTransactionDto ratedTransactionDto : subCatInvAgrDto.getRatedTransactions()) {
						if (StringUtils.isBlank(ratedTransactionDto.getCode())) {
							missingParameters.add("ratedTransactions.code");
						}
						if (StringUtils.isBlank(ratedTransactionDto.getUsageDate())) {
							missingParameters.add("ratedTransactions.usageDate");
						}
						if (StringUtils.isBlank(ratedTransactionDto.getUnitAmountWithoutTax())) {
							missingParameters.add("ratedTransactions.unitAmountWithout");
						}
						if (StringUtils.isBlank(ratedTransactionDto.getQuantity())) {
							missingParameters.add("ratedTransactions.quantity");
						}
					}
				} else {
					if (StringUtils.isBlank(subCatInvAgrDto.getAmountWithoutTax())) {
						missingParameters.add("subCategoryInvoiceAgregateDto.amountWithoutTax");
					}
				}
			}
		}

		handleMissingParameters();

	}
	private BigDecimal getAmountWithTax(Tax tax,BigDecimal amountWithoutTax ){
		Integer rounding =  tax.getProvider().getRounding()==null?2:tax.getProvider().getRounding();
		BigDecimal ttc = amountWithoutTax.add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100),rounding,RoundingMode.HALF_UP));
		return ttc;	
	}
	private BigDecimal getAmountTax(BigDecimal amountWithTax, BigDecimal amountWithoutTax){		
		return amountWithTax.subtract(amountWithoutTax);
	}
}