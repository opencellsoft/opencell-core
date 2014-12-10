package org.meveo.service.crm.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
public class AccountImportService {

	@Inject
	private Logger log;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private TitleService titleService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	ParamBean param = ParamBean.getInstance();

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public org.meveo.model.billing.BillingAccount importBillingAccount(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			Provider provider, User userJob) throws BusinessException,
			ImportWarningException {
		log.debug("billingAccount found code:" + billAccount.getCode());
		org.meveo.model.billing.BillingAccount billingAccount = null;
		CustomerAccount customerAccount = null;
		BillingCycle billingCycle = null;
		try {
			billingCycle = billingCycleService.findByBillingCycleCode(
					billAccount.getBillingCycle(), provider);
		} catch (Exception e) {

		}
		if (billingCycle == null) {
			throw new BusinessException("billingCycle not found "
					+ billAccount.getBillingCycle());
		}
		try {
			customerAccount = customerAccountService.findByCode(
					billAccount.getCustomerAccountId(), provider);
		} catch (Exception e) {
		}
		if (customerAccount == null) {
			throw new BusinessException("Cannot find CustomerAccount");
		}
		billingAccountCheckError(billAccount);

		billingAccountCheckWarning(billAccount);

		billingAccount = new BillingAccount();
		billingAccount.setNextInvoiceDate(new Date());
		billingAccount.setBillingCycle(billingCycle);
		billingAccount.setCustomerAccount(customerAccount);
		billingAccount.setCode(billAccount.getCode());
		billingAccount.setSubscriptionDate(DateUtils.parseDateWithPattern(
				billAccount.getSubscriptionDate(),
				param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd")));
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setStatusDate(new Date());
		billingAccount.setDescription(billAccount.getDescription());
		billingAccount.setPaymentMethod(PaymentMethodEnum.valueOf(billAccount
				.getPaymentMethod()));
		if (billAccount.getBankCoordinates() != null
				&& ("DIRECTDEBIT".equalsIgnoreCase(billAccount
						.getPaymentMethod()) || "TIP"
						.equalsIgnoreCase(billAccount.getPaymentMethod()))) {
			BankCoordinates bankCoordinates = new BankCoordinates();
			bankCoordinates.setAccountNumber(billAccount.getBankCoordinates()
					.getAccountNumber() == null ? "" : billAccount
					.getBankCoordinates().getAccountNumber());
			bankCoordinates.setAccountOwner(billAccount.getBankCoordinates()
					.getAccountName() == null ? "" : billAccount
					.getBankCoordinates().getAccountName());
			bankCoordinates.setBankCode(billAccount.getBankCoordinates()
					.getBankCode() == null ? "" : billAccount
					.getBankCoordinates().getBankCode());
			bankCoordinates.setBranchCode(billAccount.getBankCoordinates()
					.getBranchCode() == null ? "" : billAccount
					.getBankCoordinates().getBranchCode());
			bankCoordinates
					.setIban(billAccount.getBankCoordinates().getIBAN() == null ? ""
							: billAccount.getBankCoordinates().getIBAN());
			bankCoordinates
					.setKey(billAccount.getBankCoordinates().getKey() == null ? ""
							: billAccount.getBankCoordinates().getKey());
			billingAccount.setBankCoordinates(bankCoordinates);
		}

		Address address = new Address();
		if (billAccount.getAddress() != null) {
			address.setAddress1(billAccount.getAddress().getAddress1());
			address.setAddress2(billAccount.getAddress().getAddress2());
			address.setAddress3(billAccount.getAddress().getAddress3());
			address.setCity(billAccount.getAddress().getCity());
			address.setCountry(billAccount.getAddress().getCountry());
			address.setZipCode("" + billAccount.getAddress().getZipCode());
			address.setState(billAccount.getAddress().getState());
		}
		billingAccount.setAddress(address);
		billingAccount.setElectronicBilling("1".equalsIgnoreCase(billAccount
				.getElectronicBilling()));
		billingAccount.setEmail(billAccount.getEmail());
		billingAccount.setExternalRef1(billAccount.getExternalRef1());
		billingAccount.setExternalRef2(billAccount.getExternalRef2());
		org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
		if (billAccount.getName() != null) {
			name.setFirstName(billAccount.getName().getFirstname());
			name.setLastName(billAccount.getName().getName());
			name.setTitle(titleService.findByCode(provider, billAccount
					.getName().getTitle().trim()));
			billingAccount.setName(name);
		}
		billingAccount.setTradingCountry(tradingCountryService
				.findByTradingCountryCode(billAccount.getTradingCountryCode(),
						provider));
		billingAccount.setTradingLanguage(tradingLanguageService
				.findByTradingLanguageCode(
						billAccount.getTradingLanguageCode(), provider));

		billingAccount.setProvider(provider);

		billingAccountService.create(billingAccount, userJob);
		return billingAccount;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void importUserAccount(
			org.meveo.model.billing.BillingAccount billingAccount,
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount,
			Provider provider, User userJob) throws BusinessException,
			ImportWarningException {
		userAccountCheckError(billAccount, uAccount);
		userAccountCheckWarning(billAccount, uAccount);
		UserAccount userAccount = new UserAccount();
		userAccount.setBillingAccount(billingAccount);
		Address addressUA = new Address();
		if (uAccount.getAddress() != null) {
			addressUA.setAddress1(uAccount.getAddress().getAddress1());
			addressUA.setAddress2(uAccount.getAddress().getAddress2());
			addressUA.setAddress3(uAccount.getAddress().getAddress3());
			addressUA.setCity(uAccount.getAddress().getCity());
			addressUA.setCountry(uAccount.getAddress().getCountry());
			addressUA.setState(uAccount.getAddress().getState());
			addressUA.setZipCode("" + uAccount.getAddress().getZipCode());
		}
		userAccount.setAddress(addressUA);
		userAccount.setCode(uAccount.getCode());
		userAccount.setDescription(uAccount.getDescription());
		userAccount.setExternalRef1(uAccount.getExternalRef1());
		userAccount.setExternalRef2(uAccount.getExternalRef2());
		org.meveo.model.shared.Name nameUA = new org.meveo.model.shared.Name();
		if (uAccount.getName() != null) {
			nameUA.setFirstName(uAccount.getName().getFirstname());
			nameUA.setLastName(uAccount.getName().getName());
			nameUA.setTitle(titleService.findByCode(provider, uAccount
					.getName().getTitle().trim()));
			userAccount.setName(nameUA);
		}

		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setStatusDate(new Date());
		userAccount.setProvider(provider);
		userAccountService.createUserAccount(billingAccount, userAccount,
				userJob);

	}

	private boolean billingAccountCheckError(
			org.meveo.model.jaxb.account.BillingAccount billAccount)
			throws BusinessException {
		/*
		 * if (StringUtils.isBlank(billAccount.getExternalRef1())) {
		 * createBillingAccountError(billAccount, "ExternalRef1 is null");
		 * return true; } if
		 * (StringUtils.isBlank(billAccount.getBillingCycle())) {
		 * createBillingAccountError(billAccount, "BillingCycle is null");
		 * return true; } if (billAccount.getName() == null) {
		 * createBillingAccountError(billAccount, "Name is null"); return true;
		 * } if (StringUtils.isBlank(billAccount.getName().getTitle())) {
		 * createBillingAccountError(billAccount, "Title is null"); return true;
		 * } if (StringUtils.isBlank(billAccount.getPaymentMethod()) ||
		 * ("DIRECTDEBIT" + "CHECK" + "TIP" +
		 * "WIRETRANSFER").indexOf(billAccount .getPaymentMethod()) == -1) {
		 * createBillingAccountError(billAccount,
		 * "PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}"
		 * ); return true; }
		 */
		if ("DIRECTDEBIT".equals(billAccount.getPaymentMethod())) {
			if (billAccount.getBankCoordinates() == null) {
				throw new BusinessException("BankCoordinates is null");
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates()
					.getAccountName())) {
				throw new BusinessException(
						"BankCoordinates.AccountName is null");
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates()
					.getAccountNumber())) {
				throw new BusinessException(
						"BankCoordinates.AccountNumber is null");
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates()
					.getBankCode())) {
				throw new BusinessException("BankCoordinates.BankCode is null");
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates()
					.getBranchCode())) {
				throw new BusinessException(
						"BankCoordinates.BranchCode is null");
			}
		}
		/*
		 * if (billAccount.getAddress() == null ||
		 * StringUtils.isBlank(billAccount.getAddress().getZipCode())) {
		 * createBillingAccountError(billAccount, "ZipCode is null"); return
		 * true; } if (billAccount.getAddress() == null ||
		 * StringUtils.isBlank(billAccount.getAddress().getCity())) {
		 * createBillingAccountError(billAccount, "City is null"); return true;
		 * } if (billAccount.getAddress() == null ||
		 * StringUtils.isBlank(billAccount.getAddress().getCountry())) {
		 * createBillingAccountError(billAccount, "Country is null"); return
		 * true; }
		 */
		return false;
	}

	private boolean userAccountCheckError(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount) {
		/*
		 * if (StringUtils.isBlank(uAccount.getExternalRef1())) {
		 * createUserAccountError(billAccount, uAccount,
		 * "ExternalRef1 is null"); return true; } if (uAccount.getName() ==
		 * null) { createUserAccountError(billAccount, uAccount,
		 * "Name is null"); return true; } if
		 * (StringUtils.isBlank(uAccount.getName().getTitle())) {
		 * createUserAccountError(billAccount, uAccount, "Title is null");
		 * return true; } if (billAccount.getAddress() == null ||
		 * StringUtils.isBlank(uAccount.getAddress().getZipCode())) {
		 * createUserAccountError(billAccount, uAccount, "ZipCode is null");
		 * return true; } if (billAccount.getAddress() == null ||
		 * StringUtils.isBlank(uAccount.getAddress().getCity())) {
		 * createUserAccountError(billAccount, uAccount, "City is null"); return
		 * true; } if (billAccount.getAddress() == null ||
		 * StringUtils.isBlank(uAccount.getAddress().getCountry())) {
		 * createUserAccountError(billAccount, uAccount, "Country is null");
		 * return true; }
		 */

		return false;
	}

	private void billingAccountCheckWarning(
			org.meveo.model.jaxb.account.BillingAccount billAccount)
			throws ImportWarningException {
		// if ("PRO".equals(customer.getCustomerCategory()) &&
		// StringUtils.isBlank(billAccount.getCompany())) {
		// createBillingAccountWarning(billAccount, "company is null");
		// isWarning = true;
		// }
		// if ("PART".equals(customer.getCustomerCategory()) &&
		// (billAccount.getName() == null ||
		// StringUtils.isBlank(billAccount.getName().getFirstname()))) {
		// createBillingAccountWarning(billAccount, "name is null");
		// isWarning = true;
		// }

		if ("TRUE".equalsIgnoreCase(billAccount.getElectronicBilling())
				&& StringUtils.isBlank(billAccount.getEmail())) {
			throw new ImportWarningException("Email is null");
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() == null) {
			throw new ImportWarningException("BankCoordinates is null");
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates()
						.getBranchCode())) {
			throw new ImportWarningException(
					"BankCoordinates.BranchCode is null");
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates()
						.getAccountNumber())) {
			throw new ImportWarningException(
					"BankCoordinates.AccountNumber is null");
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates()
						.getBankCode())) {
			throw new ImportWarningException("BankCoordinates.BankCode is null");
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates()
						.getKey())) {
			throw new ImportWarningException("BankCoordinates.Key is null");
		}
	}

	private boolean userAccountCheckWarning(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount) {
		boolean isWarning = false;
		// if ("PRO".equals(customer.getCustomerCategory()) &&
		// StringUtils.isBlank(uAccount.getCompany())) {
		// createUserAccountWarning(billAccount, uAccount, "company is null");
		// isWarning = true;
		// }
		// if ("PART".equals(customer.getCustomerCategory()) &&
		// (uAccount.getName() == null ||
		// StringUtils.isBlank(uAccount.getName().getFirstname()))) {
		// createUserAccountWarning(billAccount, uAccount, "name is null");
		// isWarning = true;
		// }

		return isWarning;
	}
}
