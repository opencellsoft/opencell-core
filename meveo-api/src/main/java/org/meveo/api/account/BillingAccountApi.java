package org.meveo.api.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.invoice.Invoice4_2Dto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.UserParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Edward P. Legaspi
 **/
@SuppressWarnings("deprecation")
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class BillingAccountApi extends AccountApi {

	@Inject
	private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	@Inject
	private CustomerAccountService customerAccountService;

	@EJB
	private AccountHierarchyApi accountHierarchyApi;

	@Inject
	private InvoiceTypeService invoiceTypeService;

	public void create(BillingAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {
		create(postData, currentUser, true);
	}

	public BillingAccount create(BillingAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, BusinessException {
		return create(postData, currentUser, true, null);
	}

	public BillingAccount create(BillingAccountDto postData, User currentUser, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getCustomerAccount())) {
			missingParameters.add("customerAccount");
		}
		if (StringUtils.isBlank(postData.getBillingCycle())) {
			missingParameters.add("billingCycle");
		}
		if (StringUtils.isBlank(postData.getCountry())) {
			missingParameters.add("country");
		}
		if (StringUtils.isBlank(postData.getLanguage())) {
			missingParameters.add("language");
		}
		if (postData.getPaymentMethod() == null) {
			missingParameters.add("paymentMethod");
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		if (billingAccountService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(BillingAccount.class, postData.getCode());
		}

		CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount(), provider);
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
		}

		BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(postData.getBillingCycle(), provider);
		if (billingCycle == null) {
			throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
		}

		TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry(), provider);
		if (tradingCountry == null) {
			throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
		}

		TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage(), provider);
		if (tradingLanguage == null) {
			throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
		}

		BillingAccount billingAccount = new BillingAccount();
		populate(postData, billingAccount, currentUser);

		billingAccount.setCustomerAccount(customerAccount);
		billingAccount.setBillingCycle(billingCycle);
		billingAccount.setTradingCountry(tradingCountry);
		billingAccount.setTradingLanguage(tradingLanguage);
		billingAccount.setPaymentMethod(postData.getPaymentMethod());
		billingAccount.setPaymentTerm(postData.getPaymentTerms());
		billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
		billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
		billingAccount.setTerminationDate(postData.getTerminationDate());
		if (postData.getElectronicBilling() == null) {
			billingAccount.setElectronicBilling(false);
		} else {
			billingAccount.setElectronicBilling(postData.getElectronicBilling());
		}
		billingAccount.setEmail(postData.getEmail());
		billingAccount.setExternalRef1(postData.getExternalRef1());
		billingAccount.setExternalRef2(postData.getExternalRef2());

		if (postData.getBankCoordinates() != null) {
			billingAccount.getBankCoordinates().setBankCode(postData.getBankCoordinates().getBankCode());
			billingAccount.getBankCoordinates().setBranchCode(postData.getBankCoordinates().getBranchCode());
			billingAccount.getBankCoordinates().setAccountNumber(postData.getBankCoordinates().getAccountNumber());
			billingAccount.getBankCoordinates().setKey(postData.getBankCoordinates().getKey());
			billingAccount.getBankCoordinates().setIban(postData.getBankCoordinates().getIban());
			billingAccount.getBankCoordinates().setBic(postData.getBankCoordinates().getBic());
			billingAccount.getBankCoordinates().setAccountOwner(postData.getBankCoordinates().getAccountOwner());
			billingAccount.getBankCoordinates().setBankName(postData.getBankCoordinates().getBankName());
			billingAccount.getBankCoordinates().setBankId(postData.getBankCoordinates().getBankId());
			billingAccount.getBankCoordinates().setIssuerNumber(postData.getBankCoordinates().getIssuerNumber());
			billingAccount.getBankCoordinates().setIssuerName(postData.getBankCoordinates().getIssuerName());
			billingAccount.getBankCoordinates().setIcs(postData.getBankCoordinates().getIcs());
		}

		if(businessAccountModel != null) {
			billingAccount.setBusinessAccountModel(businessAccountModel);
		}

		billingAccountService.createBillingAccount(billingAccount, currentUser);

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), billingAccount, true, currentUser, checkCustomFields);
		} catch (Exception e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw e;
		}

		return billingAccount;
	}

	public void update(BillingAccountDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
		update(postData, currentUser, true);
	}

	public BillingAccount update(BillingAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {
		return update(postData, currentUser, true, null);
	}

	public BillingAccount update(BillingAccountDto postData, User currentUser, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, DuplicateDefaultAccountException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getCustomerAccount())) {
			missingParameters.add("customerAccount");
		}
		if (StringUtils.isBlank(postData.getBillingCycle())) {
			missingParameters.add("billingCycle");
		}
		if (StringUtils.isBlank(postData.getCountry())) {
			missingParameters.add("country");
		}
		if (StringUtils.isBlank(postData.getLanguage())) {
			missingParameters.add("language");
		}
		if (postData.getPaymentMethod() == null) {
			missingParameters.add("paymentMethod");
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode(), provider);
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
		}

		if (!StringUtils.isBlank(postData.getCustomerAccount())) {
			CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount(), provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
			}
			billingAccount.setCustomerAccount(customerAccount);
		}

		if (!StringUtils.isBlank(postData.getBillingCycle())) {
			BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(postData.getBillingCycle(), provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
			}
			billingAccount.setBillingCycle(billingCycle);
		}

		if (!StringUtils.isBlank(postData.getCountry())) {
			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry(), provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
			}
			billingAccount.setTradingCountry(tradingCountry);
		}

		if (!StringUtils.isBlank(postData.getLanguage())) {
			TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage(), provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
			}
			billingAccount.setTradingLanguage(tradingLanguage);
		}

		if (postData.getPaymentMethod() != null) {
			billingAccount.setPaymentMethod(postData.getPaymentMethod());
		}

		if (!StringUtils.isBlank(postData.getPaymentTerms())) {
			billingAccount.setPaymentTerm(postData.getPaymentTerms());
		}

		if (!StringUtils.isBlank(postData.getExternalRef1())) {
			billingAccount.setExternalRef1(postData.getExternalRef1());
		}
		if (!StringUtils.isBlank(postData.getExternalRef2())) {
			billingAccount.setExternalRef2(postData.getExternalRef2());
		}

		updateAccount(billingAccount, postData, currentUser, checkCustomFields);

		if (!StringUtils.isBlank(postData.getNextInvoiceDate())) {
			billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
		}
		if (!StringUtils.isBlank(postData.getSubscriptionDate())) {
			billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
		}
		if (!StringUtils.isBlank(postData.getTerminationDate())) {
			billingAccount.setTerminationDate(postData.getTerminationDate());
		}
		if (!StringUtils.isBlank(postData.getElectronicBilling())) {
			billingAccount.setElectronicBilling(postData.getElectronicBilling());
		}
		if (!StringUtils.isBlank(postData.getEmail())) {
			billingAccount.setEmail(postData.getEmail());
		}
		if (postData.getBankCoordinates() != null) {
			BankCoordinates bankCoordinates = new BankCoordinates();
			if (!StringUtils.isBlank(postData.getBankCoordinates().getBankCode())) {
				bankCoordinates.setBankCode(postData.getBankCoordinates().getBankCode());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getBranchCode())) {
				bankCoordinates.setBranchCode(postData.getBankCoordinates().getBranchCode());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getAccountNumber())) {
				bankCoordinates.setAccountNumber(postData.getBankCoordinates().getAccountNumber());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getKey())) {
				bankCoordinates.setKey(postData.getBankCoordinates().getKey());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getIban())) {
				bankCoordinates.setIban(postData.getBankCoordinates().getIban());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getBic())) {
				bankCoordinates.setBic(postData.getBankCoordinates().getBic());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getAccountOwner())) {
				bankCoordinates.setAccountOwner(postData.getBankCoordinates().getAccountOwner());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getBankName())) {
				bankCoordinates.setBankName(postData.getBankCoordinates().getBankName());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getBankId())) {
				bankCoordinates.setBankId(postData.getBankCoordinates().getBankId());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getIssuerNumber())) {
				bankCoordinates.setIssuerNumber(postData.getBankCoordinates().getIssuerNumber());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getIssuerName())) {
				bankCoordinates.setIssuerName(postData.getBankCoordinates().getIssuerName());
			}
			if (!StringUtils.isBlank(postData.getBankCoordinates().getIcs())) {
				bankCoordinates.setIcs(postData.getBankCoordinates().getIcs());
			}
			billingAccount.setBankCoordinates(bankCoordinates);
		}

		if(businessAccountModel != null){
			billingAccount.setBusinessAccountModel(businessAccountModel);
		}

		try {
			billingAccount = billingAccountService.update(billingAccount, currentUser);
		} catch (BusinessException e1) {
			throw new MeveoApiException(e1.getMessage());
		}

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), billingAccount, false, currentUser, checkCustomFields);
		} catch (Exception e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw e;
		}

		return billingAccount;
	}

	@SecuredBusinessEntityMethod(
			validate = @SecureMethodParameter(entity = BillingAccount.class), 
			user = @SecureMethodParameter(index = 1, parser = UserParser.class))
	public BillingAccountDto find(String billingAccountCode, User user) throws MeveoApiException {
		if (StringUtils.isBlank(billingAccountCode)) {
			missingParameters.add("billingAccountCode");
			handleMissingParameters();
		}
		BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, user.getProvider());
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
		}

		return accountHierarchyApi.billingAccountToDto(billingAccount);
	}

	public void remove(String billingAccountCode, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(billingAccountCode)) {
			missingParameters.add("billingAccountCode");
			handleMissingParameters();
		}
		BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, currentUser.getProvider());
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
		}
		try {
			billingAccountService.remove(billingAccount, currentUser);
			billingAccountService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(BillingAccount.class, billingAccountCode);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	public BillingAccountsDto listByCustomerAccount(String customerAccountCode, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(customerAccountCode)) {
			missingParameters.add("customerAccountCode");
			handleMissingParameters();
		}

		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
		}

		BillingAccountsDto result = new BillingAccountsDto();
		List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount);
		if (billingAccounts != null) {
			for (BillingAccount ba : billingAccounts) {
				BillingAccountDto billingAccountDto = accountHierarchyApi.billingAccountToDto(ba);

				List<Invoice> invoices = ba.getInvoices();
				if (invoices != null && invoices.size() > 0) {
					List<Invoice4_2Dto> invoicesDto = new ArrayList<Invoice4_2Dto>();
					String billingAccountCode = ba.getCode();
					if (invoices != null && invoices.size() > 0) {
						for (Invoice i : invoices) {
							if (invoiceTypeService.getAdjustementCode().equals(i.getInvoiceType().getCode())) {
								Invoice4_2Dto invoiceDto = new Invoice4_2Dto(i, billingAccountCode);
								invoicesDto.add(invoiceDto);
							}
						}
						billingAccountDto.setInvoices(invoicesDto);
					}
				}

				result.getBillingAccount().add(billingAccountDto);
			}
		}

		return result;
	}

	/**
	 * Create or update Billing Account based on Billing Account Code
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void createOrUpdate(BillingAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (billingAccountService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}

	public BillingAccount terminate(BillingAccountDto postData, User currentUser) throws MeveoApiException {
		SubscriptionTerminationReason terminationReason = null;
		try {
			terminationReason = subscriptionTerminationReasonService.findByCodeReason(postData.getTerminationReason(), currentUser.getProvider());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (terminationReason == null) {
			throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
		}

		BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode(), currentUser.getProvider());
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
		}

		try {
			billingAccountService.billingAccountTermination(billingAccount, postData.getTerminationDate(), terminationReason, currentUser);
		} catch (BusinessException e) {
			log.error("Failed terminating a billingAccount with code={}. {}", postData.getCode(), e.getMessage());
			throw new MeveoApiException("Failed terminating billingAccount with code=" + postData.getCode());
		}

		return billingAccount;
	}

	public List<CounterInstance> filterCountersByPeriod(String billingAccountCode, Date date, Provider provider) throws MeveoApiException, BusinessException {

		BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, provider);

		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
		}

		if (StringUtils.isBlank(date)) {
			throw new MeveoApiException("date is null");
		}

		return new ArrayList<>(billingAccountService.filterCountersByPeriod(billingAccount.getCounters(), date).values());
	}

	public void createOrUpdatePartial(BillingAccountDto billingAccountDto, User currentUser) throws MeveoApiException, BusinessException {
		BillingAccountDto existedBillingAccountDto = null;
		try {
			existedBillingAccountDto = find(billingAccountDto.getCode(), currentUser);
		} catch (Exception e) {
			existedBillingAccountDto = null;
		}
		log.debug("createOrUpdate billingAccount {}", billingAccountDto);
		if (existedBillingAccountDto == null) {// create
			create(billingAccountDto, currentUser);
		} else {// update
			if (billingAccountDto.getTerminationDate() != null) {
				if (StringUtils.isBlank(billingAccountDto.getTerminationReason())) {
					missingParameters.add("billingAccount.terminationReason");
					handleMissingParameters();
				}
				terminate(billingAccountDto, currentUser);
			} else {

				if (!StringUtils.isBlank(billingAccountDto.getBillingCycle())) {
					existedBillingAccountDto.setBillingCycle(billingAccountDto.getBillingCycle());
				}
				if (!StringUtils.isBlank(billingAccountDto.getCountry())) {
					existedBillingAccountDto.setCountry(billingAccountDto.getCountry());
				}
				if (!StringUtils.isBlank(billingAccountDto.getLanguage())) {
					existedBillingAccountDto.setLanguage(billingAccountDto.getLanguage());
				}

				if (billingAccountDto.getPaymentMethod() != null) {
					existedBillingAccountDto.setPaymentMethod(billingAccountDto.getPaymentMethod());
				}
				if (billingAccountDto.getPaymentTerms() != null) {
					existedBillingAccountDto.setPaymentTerms(billingAccountDto.getPaymentTerms());
				}
				//
				if (!StringUtils.isBlank(billingAccountDto.getNextInvoiceDate())) {
					existedBillingAccountDto.setNextInvoiceDate(billingAccountDto.getNextInvoiceDate());
				}
				if (!StringUtils.isBlank(billingAccountDto.getSubscriptionDate())) {
					existedBillingAccountDto.setSubscriptionDate(billingAccountDto.getSubscriptionDate());
				}
				if (!StringUtils.isBlank(billingAccountDto.getTerminationDate())) {
					existedBillingAccountDto.setTerminationDate(billingAccountDto.getTerminationDate());
				}
				if (!StringUtils.isBlank(billingAccountDto.getElectronicBilling())) {
					existedBillingAccountDto.setElectronicBilling(billingAccountDto.getElectronicBilling());
				}
				if (!StringUtils.isBlank(billingAccountDto.getEmail())) {
					existedBillingAccountDto.setEmail(billingAccountDto.getEmail());
				}
				//
				accountHierarchyApi.populateNameAddress(existedBillingAccountDto, billingAccountDto, currentUser);
				if (!StringUtils.isBlank(billingAccountDto.getCustomFields())) {
					existedBillingAccountDto.setCustomFields(billingAccountDto.getCustomFields());
				}
				update(existedBillingAccountDto, currentUser);
			}
		}
	}
}