package org.meveo.api.account;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentTermEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingAccountApi extends AccountApi {

	@Inject
	private Logger log;

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

	public void create(BillingAccountDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomerAccount())
				&& !StringUtils.isBlank(postData.getBillingCycle()) && !StringUtils.isBlank(postData.getCountry())
				&& !StringUtils.isBlank(postData.getLanguage()) && !StringUtils.isBlank(postData.getPaymentMethod())) {
			Provider provider = currentUser.getProvider();

			if (billingAccountService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(BillingAccount.class, postData.getCode());
			}

			CustomerAccount customerAccount = customerAccountService
					.findByCode(postData.getCustomerAccount(), provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
			}

			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(postData.getBillingCycle(), provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
			}

			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry(),
					provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
			}

			TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage(),
					provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
			}

			PaymentMethodEnum paymentMethod = null;
			try {
				paymentMethod = PaymentMethodEnum.valueOf(postData.getPaymentMethod());
			} catch (IllegalArgumentException e) {
				log.error(e.getMessage());
			}

			BillingAccount billingAccount = new BillingAccount();
			populate(postData, billingAccount, currentUser, AccountLevelEnum.BA);

			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setBillingCycle(billingCycle);
			billingAccount.setTradingCountry(tradingCountry);
			billingAccount.setTradingLanguage(tradingLanguage);
			billingAccount.setPaymentMethod(paymentMethod);
			try {
				billingAccount.setPaymentTerm(PaymentTermEnum.valueOf(postData.getPaymentTerms()));
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
			}
			billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
			billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
			billingAccount.setTerminationDate(postData.getTerminationDate());
			billingAccount.setElectronicBilling(postData.getElectronicBilling());
			billingAccount.setEmail(postData.getEmail());

			billingAccountService.createBillingAccount(billingAccount, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
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
			if (StringUtils.isBlank(postData.getPaymentMethod())) {
				missingParameters.add("paymentMethod");
			}
		}
	}

	public void update(BillingAccountDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomerAccount())
				&& !StringUtils.isBlank(postData.getBillingCycle()) && !StringUtils.isBlank(postData.getCountry())
				&& !StringUtils.isBlank(postData.getLanguage()) && !StringUtils.isBlank(postData.getPaymentMethod())) {
			Provider provider = currentUser.getProvider();

			BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode(), provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
			}

			CustomerAccount customerAccount = customerAccountService
					.findByCode(postData.getCustomerAccount(), provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
			}

			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(postData.getBillingCycle(), provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
			}

			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry(),
					provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
			}

			TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage(),
					provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
			}

			PaymentMethodEnum paymentMethod = null;
			try {
				paymentMethod = PaymentMethodEnum.valueOf(postData.getPaymentMethod());
			} catch (IllegalArgumentException e) {
				log.error(e.getMessage());
			}

			updateAccount(billingAccount, postData, currentUser, AccountLevelEnum.BA);

			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setBillingCycle(billingCycle);
			billingAccount.setTradingCountry(tradingCountry);
			billingAccount.setTradingLanguage(tradingLanguage);
			billingAccount.setPaymentMethod(paymentMethod);
			try {
				billingAccount.setPaymentTerm(PaymentTermEnum.valueOf(postData.getPaymentTerms()));
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
			}
			billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
			billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
			billingAccount.setTerminationDate(postData.getTerminationDate());
			billingAccount.setElectronicBilling(postData.getElectronicBilling());
			billingAccount.setEmail(postData.getEmail());

			billingAccountService.update(billingAccount, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
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
			if (StringUtils.isBlank(postData.getPaymentMethod())) {
				missingParameters.add("paymentMethod");
			}
		}
	}

	public BillingAccountDto find(String billingAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(billingAccountCode)) {
			BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
			}

			return new BillingAccountDto(billingAccount);
		} else {
			missingParameters.add("billingAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String billingAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(billingAccountCode)) {
			BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
			}

			billingAccountService.remove(billingAccount);
		} else {
			missingParameters.add("billingAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public BillingAccountsDto listByCustomerAccount(String customerAccountCode, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(customerAccountCode)) {
			CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
			}

			BillingAccountsDto result = new BillingAccountsDto();
			List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount);
			if (billingAccounts != null) {
				for (BillingAccount ba : billingAccounts) {
					result.getBillingAccount().add(new BillingAccountDto(ba));
				}
			}

			return result;
		} else {
			missingParameters.add("customerAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
