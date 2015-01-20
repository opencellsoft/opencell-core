package org.meveo.api.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.AccountOperationDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.MatchingAmountDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
public class CustomerAccountApi extends AccountApi {

	@Inject
	private Logger log;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private CustomerService customerService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	public void create(CustomerAccountDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomer()) && !StringUtils.isBlank(postData.getCurrency())
				&& !StringUtils.isBlank(postData.getName()) && !StringUtils.isBlank(postData.getName().getLastName())) {
			Provider provider = currentUser.getProvider();
			// check if already exists
			if (customerAccountService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(CustomerAccount.class, postData.getCode());
			}

			Customer customer = customerService.findByCode(postData.getCustomer(), provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency(),
					provider);
			if (tradingCurrency == null) {
				throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
			}

			ContactInformation contactInformation = new ContactInformation();
			contactInformation.setEmail(postData.getEmail());
			contactInformation.setPhone(postData.getPhone());
			contactInformation.setMobile(postData.getMobile());
			contactInformation.setFax(postData.getFax());

			CustomerAccount customerAccount = new CustomerAccount();
			populate(postData, customerAccount, currentUser);

			customerAccount.setCustomer(customer);
			customerAccount.setTradingCurrency(tradingCurrency);
			try {
				customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(postData.getPaymentMethod()));
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
			}
			try {
				customerAccount.setCreditCategory(CreditCategoryEnum.valueOf(postData.getCreditCategory()));
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
			}
			customerAccount.setContactInformation(contactInformation);
			customerAccount.setMandateDate(postData.getMandateDate());
			customerAccount.setMandateIdentification(postData.getMandateIdentification());

			customerAccountService.create(customerAccount, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCustomer())) {
				missingParameters.add("customer");
			}
			if (StringUtils.isBlank(postData.getCurrency())) {
				missingParameters.add("currency");
			}
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getName().getLastName())) {
				missingParameters.add("name.lastName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

	}

	public void update(CustomerAccountDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomer()) && !StringUtils.isBlank(postData.getCurrency())
				&& !StringUtils.isBlank(postData.getName()) && !StringUtils.isBlank(postData.getName().getLastName())) {
			Provider provider = currentUser.getProvider();
			// check if already exists
			CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode(),
					currentUser.getProvider());
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCode());
			}

			Customer customer = customerService.findByCode(postData.getCustomer(), provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency(),
					provider);
			if (tradingCurrency == null) {
				throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
			}

			if (customerAccount.getContactInformation() != null) {
				customerAccount.getContactInformation().setEmail(postData.getEmail());
				customerAccount.getContactInformation().setPhone(postData.getPhone());
				customerAccount.getContactInformation().setMobile(postData.getMobile());
				customerAccount.getContactInformation().setFax(postData.getFax());
			} else {
				ContactInformation contactInformation = new ContactInformation();
				contactInformation.setEmail(postData.getEmail());
				contactInformation.setPhone(postData.getPhone());
				contactInformation.setMobile(postData.getMobile());
				contactInformation.setFax(postData.getFax());
				customerAccount.setContactInformation(contactInformation);
			}

			updateAccount(customerAccount, postData, currentUser);

			customerAccount.setCustomer(customer);
			customerAccount.setTradingCurrency(tradingCurrency);
			try {
				customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(postData.getPaymentMethod()));
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
			}
			try {
				customerAccount.setCreditCategory(CreditCategoryEnum.valueOf(postData.getCreditCategory()));
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
			}
			customerAccount.setMandateDate(postData.getMandateDate());
			customerAccount.setMandateIdentification(postData.getMandateIdentification());

			customerAccountService.update(customerAccount, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCustomer())) {
				missingParameters.add("customer");
			}
			if (StringUtils.isBlank(postData.getCurrency())) {
				missingParameters.add("currency");
			}
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getName().getLastName())) {
				missingParameters.add("name.lastName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public CustomerAccountDto find(String customerAccountCode, User currentUser) throws Exception {
		CustomerAccountDto customerAccountDto = new CustomerAccountDto();

		if (!StringUtils.isBlank(customerAccountCode)) {
			Provider provider = currentUser.getProvider();
			CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
			if (customerAccount == null) {
				throw new BusinessException("Cannot find customer account with code=" + customerAccountCode);
			}

			customerAccountDto.setStatus(customerAccount.getStatus().toString() != null ? customerAccount.getStatus()
					.toString() : null);
			if (customerAccount.getPaymentMethod() != null) {
				customerAccountDto
						.setPaymentMethod(customerAccount.getPaymentMethod().toString() != null ? customerAccount
								.getPaymentMethod().toString() : null);
			}

			if (customerAccount.getCreditCategory() != null) {
				customerAccountDto
						.setCreditCategory(customerAccount.getCreditCategory().toString() != null ? customerAccount
								.getCreditCategory().toString() : null);
			}

			customerAccountDto.setDateStatus(customerAccount.getDateStatus());
			customerAccountDto.setDateDunningLevel(customerAccount.getDateDunningLevel());

			if (customerAccount.getContactInformation() != null) {
				customerAccountDto
						.setEmail(customerAccount.getContactInformation().getEmail() != null ? customerAccount
								.getContactInformation().getEmail() : null);
				customerAccountDto
						.setPhone(customerAccount.getContactInformation().getPhone() != null ? customerAccount
								.getContactInformation().getPhone() : null);
				customerAccountDto
						.setMobile(customerAccount.getContactInformation().getMobile() != null ? customerAccount
								.getContactInformation().getMobile() : null);
				customerAccountDto.setFax(customerAccount.getContactInformation().getFax() != null ? customerAccount
						.getContactInformation().getFax() : null);
			}

			if (customerAccount.getCustomer() != null) {
				customerAccountDto.setCustomer(customerAccount.getCustomer().getCode());
			}

			if (customerAccount.getCustomFields() != null && customerAccount.getCustomFields().size() > 0) {
				for (Map.Entry<String, CustomFieldInstance> entry : customerAccount.getCustomFields().entrySet()) {
					CustomFieldDto cfDto = new CustomFieldDto();
					cfDto.setCode(entry.getValue().getCode());
					cfDto.setDateValue(entry.getValue().getDateValue());
					cfDto.setDescription(entry.getValue().getDescription());
					cfDto.setDoubleValue(entry.getValue().getDoubleValue());
					cfDto.setLongValue(entry.getValue().getLongValue());
					cfDto.setStringValue(entry.getValue().getStringValue());
					customerAccountDto.getCustomFields().add(cfDto);
				}
			}

			customerAccountDto.setDunningLevel(customerAccount.getDunningLevel().toString() != null ? customerAccount
					.getDunningLevel().toString() : null);
			customerAccountDto.setMandateIdentification(customerAccount.getMandateIdentification());
			customerAccountDto.setMandateDate(customerAccount.getMandateDate());

			List<AccountOperation> accountOperations = customerAccount.getAccountOperations();
			AccountOperationDto accountOperationDto = new AccountOperationDto();

			for (AccountOperation accountOp : accountOperations) {
				accountOperationDto.setDueDate(accountOp.getDueDate());
				accountOperationDto.setType(accountOp.getType());
				accountOperationDto.setTransactionDate(accountOp.getTransactionDate());
				accountOperationDto
						.setTransactionCategory(accountOp.getTransactionCategory().toString() != null ? accountOp
								.getTransactionCategory().toString() : null);
				accountOperationDto.setReference(accountOp.getReference());
				accountOperationDto.setAccountCode(accountOp.getAccountCode());
				accountOperationDto.setAccountCodeClientSide(accountOp.getAccountCodeClientSide());
				accountOperationDto.setAmount(accountOp.getAmount());
				accountOperationDto.setMatchingAmount(accountOp.getMatchingAmount());
				accountOperationDto.setUnMatchingAmount(accountOp.getUnMatchingAmount());
				accountOperationDto.setMatchingStatus(accountOp.getMatchingStatus().toString() != null ? accountOp
						.getMatchingStatus().toString() : null);
				accountOperationDto.setOccCode(accountOp.getOccCode());
				accountOperationDto.setOccDescription(accountOp.getOccDescription());

				List<MatchingAmount> matchingAmounts = accountOp.getMatchingAmounts();
				MatchingAmountDto matchingAmountDto = new MatchingAmountDto();
				for (MatchingAmount matchingAmount : matchingAmounts) {
					matchingAmountDto.setMatchingCode(matchingAmount.getMatchingCode().getCode());
					matchingAmountDto.setMatchingAmount(matchingAmount.getMatchingAmount());
					accountOperationDto.addMatchingAmounts(matchingAmountDto);
				}

				customerAccountDto.addAccountOperations(accountOperationDto);
			}
			BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, customerAccount.getCode(),
					new Date());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}

			customerAccountDto.setBalance(balance);
		} else {
			if (StringUtils.isBlank(customerAccountCode)) {
				missingParameters.add("customerAccountCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return customerAccountDto;
	}

	public void remove(String customerAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerAccountCode)) {
			CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(Customer.class, customerAccountCode);
			}

			customerAccountService.remove(customerAccount);
		} else {
			missingParameters.add("customerAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
