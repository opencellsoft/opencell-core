package org.meveo.api.invoice;

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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
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
import org.meveo.api.filter.FilteredListApi;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
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
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
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
	private InvoiceTypeService invoiceTypeService;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private FilteredListApi filteredListApi;
	
	@Inject
	@MeveoParamBean
	private ParamBean paramBean;
	
	@Inject
	private ResourceBundle resourceMessages;
	
	/**
	 * Create an invoice based on the DTO object data and current user
     * 
	 * @param invoiceDTO invoice DTO
	 * @param currentUser current logged user
	 * @return CreateInvoiceResponseDto
	 * @throws MeveoApiException Meveo Api exception
	 * @throws BusinessException Business exception
	 * @throws Exception exception
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CreateInvoiceResponseDto create(InvoiceDto invoiceDTO, User currentUser) throws MeveoApiException, BusinessException, Exception {
        log.debug("InvoiceDto:" + JsonUtils.toJson(invoiceDTO, true));
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
		invoiceService.create(invoice, currentUser);
		if (invoiceDTO.getListInvoiceIdToLink() != null) {
			for (Long invoiceId : invoiceDTO.getListInvoiceIdToLink()) {
				Invoice invoiceTmp = invoiceService.findById(invoiceId);
				if (invoiceTmp == null) {
					throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
				}
				if (!invoiceType.getAppliesTo().contains(invoiceTmp.getInvoiceType())) {				
					throw new BusinessApiException("InvoiceId " + invoiceId + " cant be linked");
				}
				invoice.getLinkedInvoices().add(invoiceTmp);
				invoiceTmp.getLinkedInvoices().add(invoice);
				invoiceService.update(invoiceTmp, currentUser);
			}
		}
		invoiceService.update(invoice, currentUser);
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
                    if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode())
                            && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, invoice)) {
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
                        ratedTransaction.getUnitAmountWithTax(), ratedTransaction.getUnitAmountTax(), ratedTransaction.getQuantity(), amountWithoutTax, amountWithTax, amountTax,
                        RatedTransactionStatusEnum.BILLED, provider, userAccount.getWallet(), billingAccount, invoiceSubCategory, null, null, null, null, null, null, null);
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
				if(invoiceDTO.getInvoiceType().equals(invoiceTypeService.getCommercialCode())){
					List<RatedTransaction> openedRT = ratedTransactionService.openRTbySubCat(userAccount.getWallet(), invoiceSubCategory);
					for(RatedTransaction ratedTransaction : openedRT){
						subCatAmountWithoutTax = subCatAmountWithoutTax.add(ratedTransaction.getAmountWithoutTax());
						subCatAmountTax = subCatAmountTax.add(ratedTransaction.getAmountTax());
						subCatAmountWithTax = subCatAmountWithTax.add(ratedTransaction.getAmountWithTax());
						ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
						ratedTransaction.setInvoice(invoice);
						ratedTransactionService.update(ratedTransaction, currentUser);
					}
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
					//we add subCatAmountWithoutTax, in the case if there any opened RT to includ
					invoiceAgregateSubcat.setAmountWithoutTax(subCatAmountWithoutTax.add(subCatInvAgrDTO.getAmountWithoutTax()));					
					invoiceAgregateSubcat.setAmountWithTax(subCatAmountWithTax.add(getAmountWithTax(currentTax, subCatInvAgrDTO.getAmountWithoutTax())));
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
            BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount().getCustomerAccount().getCode(), invoice.getDueDate(),
                invoice.getProvider());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = invoice.getAmountWithTax().add(balance);
		}
		invoice.setNetToPay(netToPay);
		invoiceService.update(invoice, currentUser);
	
		try {
			populateCustomFields(invoiceDTO.getCustomFields(), invoice, true, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

		CreateInvoiceResponseDto response = new CreateInvoiceResponseDto();
		response.setInvoiceId(invoice.getId());
		response.setAmountWithoutTax(invoice.getAmountWithoutTax());
		response.setAmountTax(invoice.getAmountTax());
		response.setAmountWithTax(invoice.getAmountWithTax());
		response.setDueDate(invoice.getDueDate());
		response.setInvoiceDate(invoice.getInvoiceDate());
		response.setNetToPay(invoice.getNetToPay());
		
		//pdf and xml are added to response in the ws impl
		if (invoiceDTO.isAutoValidation()) {
			response.setInvoiceNumber(validateInvoice(invoice.getId(), currentUser));
		}
		return response;
	}

	
	
	/**
	 * list invoices based on a customer account and a provider
     * 
	 * @param customerAccountCode customer account code
	 * @param provider provider
	 * @return list of invoice DTOs
	 * @throws MeveoApiException Meveo Api exception
	 */
	public List<InvoiceDto> list(String customerAccountCode, Provider provider) throws MeveoApiException {
		return listByPresentInAR(customerAccountCode, provider, false)	;
	}
	
	/**
	 * list invoices based on a customer account and a provider, and presentInAR
	 * @param customerAccountCode customer account code
	 * @param provider provider
	 * @return list of invoice DTOs
	 * @throws MeveoApiException Meveo Api exception
	 */
	public List<InvoiceDto> listPresentInAR(String customerAccountCode, Provider provider) throws MeveoApiException {
		return listByPresentInAR(customerAccountCode, provider, true)	;
	}

	public List<InvoiceDto> listByPresentInAR(String customerAccountCode, Provider provider,boolean isPresentInAR) throws MeveoApiException {
		if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        List<InvoiceDto> customerInvoiceDtos = new ArrayList<InvoiceDto>();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        for (BillingAccount billingAccount : customerAccount.getBillingAccounts()) {
            List<Invoice> invoiceList = new ArrayList<Invoice>();
            if(isPresentInAR){
            	invoiceList = invoiceService.getInvoicesWithAccountOperation(billingAccount, provider);
            }else{
            	invoiceList =  billingAccount.getInvoices();
            }          
            for (Invoice invoice : invoiceList) {
            	InvoiceDto customerInvoiceDto = new InvoiceDto(invoice);
                customerInvoiceDtos.add(customerInvoiceDto);
            }
        }

        return customerInvoiceDtos;
	}


	/**
	 * Update the billing run
     * 
	 * @param billingRun billing run
	 * @param status status of billing run
	 * @param billingAccountNumber billing account number
	 * @param billableBillingAcountNumber billable Billing account number
	 * @param currentUser current logged user
	 * @return the billing run
	 * @throws BusinessException Business exception
	 */
    public BillingRun updateBR(BillingRun billingRun, BillingRunStatusEnum status, Integer billingAccountNumber, Integer billableBillingAcountNumber, User currentUser)
            throws BusinessException {
		billingRun.setStatus(status);
		if (billingAccountNumber != null) {
			billingRun.setBillingAccountNumber(billingAccountNumber);
		}
		if (billableBillingAcountNumber != null) {
			billingRun.setBillableBillingAcountNumber(billableBillingAcountNumber);
		}
		return billingRunService.update(billingRun, currentUser);
	}
	
	/**
	 * Validate the Billing run
     * 
	 * @param billingRun billing run to validate
	 * @param user current logged user
	 * @throws BusinessException business exception
	 */
	public void validateBR(BillingRun billingRun, User user) throws BusinessException {
		billingRunService.forceValidate(billingRun.getId(), user);
	}


	/**
     * Launch all the invoicing process for a given billingAccount, that's mean : <lu> Create rated transactions <li>Create an exeptionnal billingRun with given dates <li>Validate
     * the preinvoicing resport <li>Validate the postinvoicing report <li>Vaidate the BillingRun </lu>
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
		if (generateInvoiceRequestDto.getLastTransactionDate() == null && 
				generateInvoiceRequestDto.getFilter()==null) {
			missingParameters.add("lastTransactionDate or filter");
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
		
		ratedTransactionService.createRatedTransaction(billingAccount.getId(), currentUser, generateInvoiceRequestDto.getInvoicingDate());				
		log.debug("createRatedTransaction ok");

		Filter ratedTransactionFilter =null;
		if(generateInvoiceRequestDto.getFilter()!=null){
			ratedTransactionFilter=filteredListApi.getFilterFromDto(generateInvoiceRequestDto.getFilter(), currentUser);
		}else{
			if( ! ratedTransactionService.isBillingAccountBillable(billingAccount, (generateInvoiceRequestDto.getLastTransactionDate()))){
				throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));		
			}
		}
		
		Invoice invoice = invoiceService.createAgregatesAndInvoice(billingAccount,null,ratedTransactionFilter
				,generateInvoiceRequestDto.getInvoicingDate(),generateInvoiceRequestDto.getLastTransactionDate(),currentUser);
		log.debug("createAgregatesAndInvoice ok ");

		invoice.setInvoiceNumber(invoiceService.getInvoiceNumber(invoice, currentUser));
		invoice.setPdf(null);							
		invoiceService.update(invoice, currentUser);						
		
		return new GenerateInvoiceResultDto(invoice);
	}

	public String getXMLInvoice(String invoiceNumber, User currentUser) throws FileNotFoundException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		return getXMLInvoice(invoiceNumber, invoiceTypeService.getDefaultCommertial(currentUser).getCode(), currentUser);
	}

    public String getXMLInvoice(String invoiceNumber, String invoiceTypeCode, User currentUser) throws FileNotFoundException, MissingParameterException,
            EntityDoesNotExistsException, BusinessException {
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
		
		return invoiceService.getXMLInvoice(invoice, invoiceNumber, currentUser, true);
	}
	
    /**
     * 
     * @param invoiceNumber
     * @param currentUser
     * @return
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     * @throws Exception
     */
	public byte[] getPdfInvoince(String invoiceNumber, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, Exception {
		return getPdfInvoince(invoiceNumber, invoiceTypeService.getDefaultCommertial(currentUser).getCode(), currentUser);
	}

	/**
	 * 
	 * @param invoiceNumber
	 * @param invoiceTypeCode
	 * @param currentUser
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws Exception
	 */
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
		
		return invoiceService.generatePdfInvoice(invoice, invoiceNumber, currentUser);
	}

	/**
	 * 
	 * @param invoiceId
	 * @param currentUser
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws BusinessException
	 */
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

	/**
	 * 
	 * @param invoiceId
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void cancelInvoice(Long invoiceId, User currentUser) throws MeveoApiException, BusinessException {
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
				ratedTransactionService.remove(rt, currentUser);
			}
		}
				
		invoiceService.remove(invoice, currentUser);
	}

	/**
	 * 
	 * @param invoiceDTO
	 * @param currentUser
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
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
	
	/**
	 * 
	 * @param id
	 * @param invoiceNumber
	 * @param invoiceTypeCode
	 * @param provider
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
    public InvoiceDto find(Long id, String invoiceNumber, String invoiceTypeCode, Provider provider) throws MissingParameterException, EntityDoesNotExistsException,
            MeveoApiException, BusinessException {
		boolean searchById = true;
		
        if (StringUtils.isBlank(id)) {
        	searchById = false;
        	if(StringUtils.isBlank(invoiceNumber) && StringUtils.isBlank(invoiceTypeCode)) {
        		missingParameters.add("id");
        		missingParameters.add("invoiceNumber");
        		missingParameters.add("invoiceTypeCode");
        	}
            handleMissingParameters();
        }

        InvoiceDto result = new InvoiceDto();
        Invoice invoice = null;
        
        if(searchById) {
        	invoice = invoiceService.findById(id, provider);
        } else {
        	InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode, provider);
        	if (invoiceType == null) {
    			throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
    		}
        	invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType, provider);
        }

        if (invoice == null) {
        	if(searchById)
        		throw new EntityDoesNotExistsException(Invoice.class, id);
        	else
        		throw new EntityDoesNotExistsException(Invoice.class, "invoiceNumber", invoiceNumber, "invoiceType", invoiceTypeCode);
        }

        result = new InvoiceDto(invoice);

        return result;
    }
	
	/**
	 * 
	 * @param tax
	 * @param amountWithoutTax
	 * @return
	 */
	private BigDecimal getAmountWithTax(Tax tax,BigDecimal amountWithoutTax ){
		Integer rounding =  tax.getProvider().getRounding()==null?2:tax.getProvider().getRounding();
		BigDecimal ttc = amountWithoutTax.add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100),rounding,RoundingMode.HALF_UP));
		return ttc;	
	}
	
	/**
	 * 
	 * @param amountWithTax
	 * @param amountWithoutTax
	 * @return
	 */
	private BigDecimal getAmountTax(BigDecimal amountWithTax, BigDecimal amountWithoutTax){		
		return amountWithTax.subtract(amountWithoutTax);
	}
}
