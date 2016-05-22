package org.meveo.api.invoice;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValueException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
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
import org.meveo.model.billing.InvoiceTypeEnum;
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

	public String create(InvoiceDto invoiceDTO, User currentUser) throws MeveoApiException, BusinessException {

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

		if (StringUtils.isBlank(invoiceDTO.getCategoryInvoiceAgregates()) || invoiceDTO.getCategoryInvoiceAgregates().isEmpty()) {
			missingParameters.add("categoryInvoiceAgregates");
		}  


		for(CategoryInvoiceAgregateDto catInvAgrDto: invoiceDTO.getCategoryInvoiceAgregates()){
			if(StringUtils.isBlank(catInvAgrDto.getCategoryInvoiceCode())){
				missingParameters.add("categoryInvoiceAgregateDto.categoryInvoiceCode");	
			}
			if(invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode(), currentUser.getProvider()) == null){
				throw new EntityDoesNotExistsException(InvoiceSubCategory.class, catInvAgrDto.getCategoryInvoiceCode());
			}
			if(catInvAgrDto.getListSubCategoryInvoiceAgregateDto() == null || catInvAgrDto.getListSubCategoryInvoiceAgregateDto().isEmpty()){
				missingParameters.add("categoryInvoiceAgregateDto.listSubCategoryInvoiceAgregateDto");
			}
			for(SubCategoryInvoiceAgregateDto subCatInvAgrDto: catInvAgrDto.getListSubCategoryInvoiceAgregateDto()){                     	
				if(StringUtils.isBlank(subCatInvAgrDto.getInvoiceSubCategoryCode())){
					missingParameters.add("subCategoryInvoiceAgregateDto.invoiceSubCategoryCode");
				}            	
				if(invoiceSubCategoryService.findByCode(subCatInvAgrDto.getInvoiceSubCategoryCode(), currentUser.getProvider()) == null){
					throw new EntityDoesNotExistsException(InvoiceSubCategory.class, subCatInvAgrDto.getInvoiceSubCategoryCode());
				}
				if(StringUtils.isBlank(subCatInvAgrDto.getAmountWithoutTax())){
					missingParameters.add("subCategoryInvoiceAgregateDto.amountWithoutTax");
				}
				if(InvoiceModeEnum.DETAILLED.name().equals(invoiceDTO.getInvoiceMode().name())){
					if(subCatInvAgrDto.getRatedTransactions() == null || subCatInvAgrDto.getRatedTransactions().isEmpty()){
						missingParameters.add("ratedTransactions");	
					}
					for(RatedTransactionDto ratedTransactionDto : subCatInvAgrDto.getRatedTransactions() ){
						if(StringUtils.isBlank(ratedTransactionDto.getCode())){
							missingParameters.add("ratedTransactions.code");	
						}
						if(StringUtils.isBlank(ratedTransactionDto.getUsageDate())){
							missingParameters.add("ratedTransactions.usageDate");	
						}   
						if(StringUtils.isBlank(ratedTransactionDto.getUnitAmountWithoutTax())){
							missingParameters.add("ratedTransactions.unitAmountWithout");	
						}    
						if(StringUtils.isBlank(ratedTransactionDto.getUnitAmountWithoutTax())){
							missingParameters.add("ratedTransactions.unitAmountWithout");	
						}   
						if(StringUtils.isBlank(ratedTransactionDto.getQuantity())){
							missingParameters.add("ratedTransactions.quantity");	
						}              			
					}
				}
			}
		}

		handleMissingParameters();
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
		Invoice invoice = new Invoice();
		invoice.setBillingAccount(billingAccount);


		invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
		invoice.setDueDate(invoiceDTO.getDueDate());
		PaymentMethodEnum paymentMethod = billingAccount.getPaymentMethod();
		if (paymentMethod == null) {
			paymentMethod = billingAccount.getCustomerAccount().getPaymentMethod();
		}
		invoice.setPaymentMethod(paymentMethod);
		invoice.setInvoiceType(invoiceType);

		if (invoiceDTO.getListInvoiceNumbersToLink().isEmpty()) {	    	
			for(String invvoiceNumber : invoiceDTO.getListInvoiceNumbersToLink() ){
				Invoice invoiceTmp = invoiceService.getInvoiceByNumber(invvoiceNumber, provider.getCode());
				if(invoiceTmp == null){
					throw new EntityDoesNotExistsException(Invoice.class, invvoiceNumber);
				}
				if(invoiceType.getAppliesTo().contains(invoiceTmp.getInvoiceType())){
					throw new BusinessApiException("Invoice "+invvoiceNumber+" cant be linked");
				}
				invoice.getInvoiceAdjustments().add(invoiceTmp);
			}	       
		} 

		
		invoiceService.create(invoice, currentUser);

		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		UserAccount userAccount = userAccounts.get(0);
		List<Tax> taxes = new ArrayList<Tax>();

		for(CategoryInvoiceAgregateDto catInvAgrDto: invoiceDTO.getCategoryInvoiceAgregates()){
			CategoryInvoiceAgregate invoiceAgregateCat = new CategoryInvoiceAgregate();
			invoiceAgregateCat.setInvoice(invoice);
			invoiceAgregateCat.setBillingRun(null);						
			invoiceAgregateCat.setDescription(catInvAgrDto.getDescription());
			invoiceAgregateCat.setItemNumber(catInvAgrDto.getListSubCategoryInvoiceAgregateDto().size());
			invoiceAgregateCat.setUserAccount(userAccount);
			invoiceAgregateCat.setBillingAccount(billingAccount);
			invoiceAgregateCat.setInvoiceCategory(invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode(), currentUser.getProvider()));
			invoiceAgregateCat.setAuditable(billingAccount.getAuditable());		
			invoiceAgregateService.create(invoiceAgregateCat, currentUser);
			invoiceAgregateService.commit();			
			
			for(SubCategoryInvoiceAgregateDto subCatInvAgrDTO : catInvAgrDto.getListSubCategoryInvoiceAgregateDto()){
				InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(subCatInvAgrDTO.getInvoiceSubCategoryCode(), provider);				
				for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {					
					if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode()) 							
							&& invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(),billingAccount, invoice)) {
						if( ! taxes.contains(invoicesubcatCountry.getTax())){
							taxes.add(invoicesubcatCountry.getTax());
						}
					}
				}
				SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();	
				invoiceAgregateSubcat.setCategoryInvoiceAgregate(invoiceAgregateCat);
				invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCategory);
				invoiceAgregateSubcat.setInvoice(invoice);
				invoiceAgregateSubcat.setDescription(catInvAgrDto.getDescription());
				invoiceAgregateSubcat.setBillingRun(null);
				invoiceAgregateSubcat.setWallet(userAccount.getWallet());
				invoiceAgregateSubcat.setAccountingCode(invoiceSubCategory.getAccountingCode());
				invoiceAgregateSubcat.setItemNumber(subCatInvAgrDTO.getRatedTransactions().size());
				/////
				invoiceAgregateSubcat.setQuantity(BigDecimal.ONE);
				invoiceAgregateSubcat.setAmountWithoutTax(subCatInvAgrDTO.getAmountWithoutTax());
				invoiceAgregateSubcat.setAmountTax(subCatInvAgrDTO.getAmountTax());
				invoiceAgregateSubcat.setAmountWithTax(subCatInvAgrDTO.getAmountWithTax());

				invoiceAgregateSubcat.setAuditable(billingAccount.getAuditable());
				invoiceAgregateService.create(invoiceAgregateSubcat, currentUser);
				for (Tax tax:taxes) {
					TaxInvoiceAgregate invoiceAgregateTax = null;
					Long taxId = tax.getId();

					if (taxInvoiceAgregateMap.containsKey(taxId)) {
						invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
					} else {
						invoiceAgregateTax = new TaxInvoiceAgregate();
						invoiceAgregateTax.setInvoice(invoice);
						invoiceAgregateTax.setBillingRun(billingAccount.getBillingRun());
						invoiceAgregateTax.setTax(tax);
						invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
						invoiceAgregateTax.setTaxPercent(tax.getPercent());

						taxInvoiceAgregateMap.put(taxId, invoiceAgregateTax);
						invoiceAgregateTax.setAuditable(billingAccount.getAuditable());						
						invoiceAgregateService.create(invoiceAgregateTax, currentUser);						
					}

					invoiceAgregateSubcat.addSubCategoryTax(tax);
				}
			      
	            for (RatedTransactionDto ratedTransaction : subCatInvAgrDTO.getRatedTransactions()) {
	                RatedTransaction meveoRatedTransaction = new RatedTransaction(null, ratedTransaction.getUsageDate(), ratedTransaction.getUnitAmountWithoutTax(),
	                    ratedTransaction.getUnitAmountWithTax(), ratedTransaction.getUnitAmountTax(), ratedTransaction.getQuantity(), ratedTransaction.getAmountWithoutTax(),
	                    ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax(), RatedTransactionStatusEnum.MANUAL, provider, null, billingAccount, invoiceSubCategory,
	                    null, null, null, null, null, null, null);
	                meveoRatedTransaction.setCode(ratedTransaction.getCode());
	                meveoRatedTransaction.setDescription(ratedTransaction.getDescription());
	                meveoRatedTransaction.setUnityDescription(ratedTransaction.getUnityDescription());
	                meveoRatedTransaction.setInvoice(invoice);
	                meveoRatedTransaction.setWallet(userAccount.getWallet());	               
	                ratedTransactionService.create(meveoRatedTransaction, currentUser);	                
	            }
			}
		}

		//includ open RT
	
	try {
		populateCustomFields(invoiceDTO.getCustomFields(), invoice, true, currentUser, true);

	} catch (IllegalArgumentException | IllegalAccessException e) {
		log.error("Failed to associate custom field instance to an entity", e);
		throw new MeveoApiException("Failed to associate custom field instance to an entity");
	}
	
	return "responseObjectWithHeader";
}

public List<InvoiceDto> list(String customerAccountCode, Provider provider) throws MeveoApiException {
	return null;
}

public BillingRun launchExceptionalInvoicing(GenerateInvoiceRequestDto generateInvoiceRequestDto, User currentUser, List<Long> BAids) throws MissingParameterException,
EntityDoesNotExistsException, BusinessException, BusinessApiException, Exception {
	return billingRunService.launchExceptionalInvoicing(BAids, generateInvoiceRequestDto.getInvoicingDate(), generateInvoiceRequestDto.getLastTransactionDate(),
			BillingProcessTypesEnum.AUTOMATIC, currentUser);
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
 * Launch all the invoicing process for a given billingAccount, that's mean :
 * <lu> Create rated transactions
 * <li> Create an exeptionnal billingRun with given dates
 * <li> Validate the preinvoicing resport
 * <li> Validate the postinvoicing report
 * <li> Vaidate the BillingRun
 * </lu>
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
public GenerateInvoiceResultDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto, User currentUser) throws MissingParameterException,
EntityDoesNotExistsException, BusinessException, BusinessApiException, Exception {

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

	if (billingAccount.getBillingRun() != null
			&& (billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.NEW)
					|| billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.PREVALIDATED) || billingAccount.getBillingRun().getStatus()
					.equals(BillingRunStatusEnum.POSTVALIDATED))) {

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
		throw new BusinessApiException("Cant found invoice");
	}

	GenerateInvoiceResultDto generateInvoiceResultDto = new GenerateInvoiceResultDto();
	generateInvoiceResultDto.setInvoiceNumber(invoices.get(0).getInvoiceNumber());
	return generateInvoiceResultDto;
}

public String getXMLInvoice(String invoiceNumber, String invoiceType, User currentUser) throws FileNotFoundException, MissingParameterException, EntityDoesNotExistsException,
BusinessException, InvalidEnumValueException {
	log.debug("getXMLInvoice  invoiceNumber:{}", invoiceNumber);
	if (StringUtils.isBlank(invoiceNumber)) {
		missingParameters.add("invoiceNumber");
		handleMissingParameters();
	}

	InvoiceTypeEnum invoiceTypeEnum = InvoiceTypeEnum.COMMERCIAL;
	try {
		invoiceTypeEnum = InvoiceTypeEnum.valueOf(invoiceType);
	} catch (IllegalArgumentException e) {
		throw new InvalidEnumValueException(InvoiceTypeEnum.class.getName(), invoiceType);
	}

	Invoice invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceTypeEnum, currentUser.getProvider());
	if (invoice == null) {
		throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber);
	}
	ParamBean param = ParamBean.getInstance();
	String invoicesDir = param.getProperty("providers.rootDir", "/tmp/meveo");
	String sep = File.separator;
	String invoicePath = invoicesDir
			+ sep
			+ currentUser.getProvider().getCode()
			+ sep
			+ "invoices"
			+ sep
			+ "xml"
			+ sep
			+ (invoice.getBillingRun() == null ? DateUtils.formatDateWithPattern(invoice.getAuditable().getCreated(),
					paramBean.getProperty("meveo.dateTimeFormat.string", "ddMMyyyy_HHmmss")) : invoice.getBillingRun().getId());
	File billingRundir = new File(invoicePath);
	xmlInvoiceCreator.createXMLInvoice(invoice.getId(), billingRundir);
	String xmlCanonicalPath = invoicePath + sep + invoiceNumber + ".xml";
	Scanner scanner = new Scanner(new File(xmlCanonicalPath));
	String xmlContent = scanner.useDelimiter("\\Z").next();
	scanner.close();
	log.debug("getXMLInvoice  invoiceNumber:{} done.", invoiceNumber);
	return xmlContent;
}

public byte[] getPdfInvoince(String invoiceNumber, String invoiceType, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, Exception {
	log.debug("getPdfInvoince  invoiceNumber:{}", invoiceNumber);
	if (StringUtils.isBlank(invoiceNumber)) {
		missingParameters.add("invoiceNumber");
		handleMissingParameters();
	}

	InvoiceTypeEnum invoiceTypeEnum = InvoiceTypeEnum.COMMERCIAL;
	try {
		invoiceTypeEnum = InvoiceTypeEnum.valueOf(invoiceType);
	} catch (IllegalArgumentException e) {
		throw new InvalidEnumValueException(InvoiceTypeEnum.class.getName(), invoiceType);
	}

	Invoice invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceTypeEnum, currentUser.getProvider());
	if (invoice == null) {
		throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber);
	}
	if (invoice.getPdf() == null) {
		Map<String, Object> parameters = pDFParametersConstruction.constructParameters(invoice.getId(), currentUser, currentUser.getProvider());
		invoiceService.producePdf(parameters, currentUser);
	}
	invoiceService.findById(invoice.getId(), true);
	log.debug("getXMLInvoice invoiceNumber:{} done.", invoiceNumber);
	return invoice.getPdf();
}


public void validateInvoice(Long invoiceId,User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException{
	if (StringUtils.isBlank(invoiceId)) {
		missingParameters.add("invoiceId");
	}
	handleMissingParameters();

	Invoice invoice = invoiceService.findById(invoiceId);
	if(invoice == null){
		throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
	}
	if (invoice.getInvoiceType().getInvoiceTypeEnum().isAdjustment()) {           
		invoice.setInvoiceNumber(invoiceService.getInvoiceAdjustmentNumber(invoice, currentUser));
	} else {
		invoice.setInvoiceNumber(invoiceService.getInvoiceNumber(invoice));
	}       
	invoiceService.update(invoice, currentUser);
}

public void cancelInvoice(Long invoiceId,User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException{
	if (StringUtils.isBlank(invoiceId)) {
		missingParameters.add("invoiceId");
	}
	handleMissingParameters();

	Invoice invoice = invoiceService.findById(invoiceId);
	if(invoice == null){
		throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
	}

	invoiceService.remove(invoice);
}    
}