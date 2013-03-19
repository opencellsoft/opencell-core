/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.connector.crm;

import java.io.File;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.meveo.commons.utils.DateUtils;
import org.meveo.commons.utils.ExceptionUtils;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.connector.InputFiles;
import org.meveo.model.admin.AccountImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.ErrorBillingAccount;
import org.meveo.model.jaxb.account.ErrorUserAccount;
import org.meveo.model.jaxb.account.Errors;
import org.meveo.model.jaxb.account.WarningBillingAccount;
import org.meveo.model.jaxb.account.WarningUserAccount;
import org.meveo.model.jaxb.account.Warnings;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.service.admin.impl.AccountImportHistoService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @created 22.12.2010
 * 
 */
@Named
public class ImportAccounts extends InputFiles {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	UserService userService;

	@Inject
	BillingCycleService billingCycleService;

	@Inject
	private AccountImportHistoService accountImportHistoService;

	protected Logger log;

	@Inject
	private ProviderService providerService;

	@Inject
	private TitleService titleService;

	BillingAccounts billingAccountsWarning;
	BillingAccounts billingAccountsError;
	ParamBean param = ParamBean.getInstance("meveo-admin.properties");
	int nbBillingAccounts;
	int nbBillingAccountsError;
	int nbBillingAccountsWarning;
	int nbBillingAccountsIgnored;
	int nbBillingAccountsCreated;

	int nbUserAccounts;
	int nbUserAccountsError;
	int nbUserAccountsWarning;
	int nbUserAccountsIgnored;
	int nbUserAccountsCreated;

	AccountImportHisto accountImportHisto;

	@Asynchronous
	public void importFile(File file, String fileName, CountDownLatch latch) throws JAXBException,
			Exception {

		try {
			log.info("start import file :" + fileName);
			billingAccountsWarning = new BillingAccounts();
			billingAccountsError = new BillingAccounts();
			nbBillingAccounts = 0;
			nbBillingAccountsError = 0;
			nbBillingAccountsWarning = 0;
			nbBillingAccountsIgnored = 0;
			nbBillingAccountsCreated = 0;

			nbUserAccounts = 0;
			nbUserAccountsError = 0;
			nbUserAccountsWarning = 0;
			nbUserAccountsIgnored = 0;
			nbUserAccountsCreated = 0;
			accountImportHisto = new AccountImportHisto();

			String providerCode = getProvider(fileName);
			if (providerCode == null) {
				throw new Exception("invalid fileName");
			}
			Provider provider = providerService.findByCode(providerCode);
			if (provider == null) {
				throw new Exception("Cannot found provider : " + providerCode);
			}
			accountImportHisto.setExecutionDate(new Date());
			accountImportHisto.setFileName(fileName);
			User userJob = userService.findById(new Long(param.getProperty("connectorCRM.userId")));
			if (file.length() < 83) {
				createBillingAccountWarning(null, "Fichier vide");
				generateReport(fileName);
				createHistory(provider, userJob);
				return;
			}
			BillingAccounts billingAccounts = (BillingAccounts) JAXBUtils.unmarshaller(
					BillingAccounts.class, file);
			log.debug("parsing file ok");

			int i = -1;

			nbBillingAccounts = billingAccounts.getBillingAccount().size();
			if (nbBillingAccounts == 0) {
				createBillingAccountWarning(null, "Fichier vide");
			}
			for (org.meveo.model.jaxb.account.BillingAccount billAccount : billingAccounts
					.getBillingAccount()) {
				nbUserAccounts += billAccount.getUserAccounts().getUserAccount().size();
			}

			for (org.meveo.model.jaxb.account.BillingAccount billAccount : billingAccounts
					.getBillingAccount()) {
				i++;
				int j = -1;
				org.meveo.model.billing.BillingAccount billingAccount = null;
				org.meveo.model.payments.CustomerAccount customerAccount = null;
				boolean existBillingAccount = false;
				try {
					log.debug("billingAccount founded ExternalRef1:"
							+ billAccount.getExternalRef1());
					try {
						billingAccount = billingAccountService.findByExternalRef1(billAccount
								.getExternalRef1());
					} catch (Exception e) {
					}
					if (billingAccount != null) {
						log.info("file1:" + fileName + ", typeEntity:BillingAccount, index:" + i
								+ ", ExternalRef1:" + billAccount.getExternalRef1()
								+ ", status:Ignored");
						nbBillingAccountsIgnored++;
						existBillingAccount = true;
					}
					if (!existBillingAccount) {
						BillingCycle billingCycle = null;
						try {
							billingCycle = billingCycleService.findByBillingCycleCode(
									billAccount.getBillingCycle(), provider);
						} catch (Exception e) {

						}
						if (billingCycle == null) {
							nbBillingAccountsError++;
							log.info("file2:" + fileName + ", typeEntity:BillingAccount, index:"
									+ i + ", ExternalRef1:" + billAccount.getExternalRef1()
									+ ", status:Error");
							continue;
						}
						try {
							customerAccount = customerAccountService.findByExternalRef1(billAccount
									.getExternalRef1());
						} catch (Exception e) {
						}
						if (customerAccount == null) {
							createBillingAccountError(billAccount, "Cannot found CustomerAccount");
							nbBillingAccountsError++;
							log.info("file3:" + fileName + ", typeEntity:BillingAccount, index:"
									+ i + ", ExternalRef1:" + billAccount.getExternalRef1()
									+ ", status:Error");
							continue;
						}
						if (billingAccountCheckError(billAccount)) {
							nbBillingAccountsError++;
							log.info("file4:" + fileName + ", typeEntity:BillingAccount, index:"
									+ i + ", ExternalRef1:" + billAccount.getExternalRef1()
									+ ", status:Error");
							continue;
						}

						if (billingAccountCheckWarning(billAccount)) {
							nbBillingAccountsWarning++;
							log.info("file5:" + fileName + ", typeEntity:BillingAccount,  index:"
									+ i + " ExternalRef1:" + billAccount.getExternalRef1()
									+ ", status:Warning");
						}
						billingAccount = new BillingAccount();
						billingAccount.setNextInvoiceDate(new Date());
						billingAccount.setBillingCycle(billingCycle);
						billingAccount.setCustomerAccount(customerAccount);
						billingAccount.setCode(billAccount.getCode());
						billingAccount.setSubscriptionDate(DateUtils.parseDateWithPattern(
								billAccount.getSubscriptionDate(),
								param.getProperty("connectorCRM.dateFormat")));
						billingAccount.setStatus(AccountStatusEnum.ACTIVE);
						billingAccount.setStatusDate(new Date());
						billingAccount.setDescription(billAccount.getDescription());
						billingAccount.setPaymentMethod(PaymentMethodEnum.valueOf(billAccount
								.getPaymentMethod()));
						if (billAccount.getBankCoordinates() != null
								&& ("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()) || "TIP"
										.equalsIgnoreCase(billAccount.getPaymentMethod()))) {
							BankCoordinates bankCoordinates = new BankCoordinates();
							bankCoordinates.setAccountNumber(billAccount.getBankCoordinates()
									.getAccountNumber() == null ? "" : billAccount
									.getBankCoordinates().getAccountNumber());
							bankCoordinates.setAccountOwner(billAccount.getBankCoordinates()
									.getAccountName() == null ? "" : billAccount
									.getBankCoordinates().getAccountName());
							bankCoordinates.setBankCode(billAccount.getBankCoordinates()
									.getBankCode() == null ? "" : billAccount.getBankCoordinates()
									.getBankCode());
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
							name.setTitle(titleService.findByCode(provider, billAccount.getName()
									.getTitle().trim()));
							billingAccount.setName(name);
						}
						billingAccount.setProvider(provider);

						billingAccountService.create(billingAccount, userJob);
						log.info("file6:" + fileName + ", typeEntity:BillingAccount, index:" + i
								+ ", ExternalRef1:" + billAccount.getExternalRef1()
								+ ", status:Created");
						nbBillingAccountsCreated++;
					}
				} catch (Exception e) {
					createBillingAccountError(billAccount, ExceptionUtils.getRootCause(e)
							.getMessage());
					nbBillingAccountsError++;
					log.info("file7:" + fileName + ", typeEntity:BillingAccount, index:" + i
							+ ", ExternalRef1:" + billAccount.getExternalRef1() + ", status:Error");
					e.printStackTrace();
				}

				for (org.meveo.model.jaxb.account.UserAccount uAccount : billAccount
						.getUserAccounts().getUserAccount()) {
					j++;
					try {
						UserAccount userAccount = null;
						log.debug("userAccount founded ExternalRef1:" + uAccount.getExternalRef1());
						try {
							userAccount = userAccountService.findByExternalRef1(uAccount
									.getExternalRef1());
						} catch (Exception e) {
						}
						if (userAccount != null) {
							nbUserAccountsIgnored++;
							log.info("file:" + fileName
									+ ", typeEntity:UserAccount,  indexBillingAccount:" + i
									+ ", index:" + j + " ExternalRef1:"
									+ uAccount.getExternalRef1() + ", status:Ignored");
							continue;
						}
						if (userAccountCheckError(billAccount, uAccount)) {
							nbUserAccountsError++;
							log.info("file:" + fileName
									+ ", typeEntity:UserAccount,  indexBillingAccount:" + i
									+ ", index:" + j + " ExternalRef1:"
									+ uAccount.getExternalRef1() + ", status:Error");
							continue;
						}
						if (userAccountCheckWarning(billAccount, uAccount)) {
							nbUserAccountsWarning++;
							log.info("file:" + fileName
									+ ", typeEntity:UserAccount,  indexBillingAccount:" + i
									+ ", index:" + j + " ExternalRef1:"
									+ uAccount.getExternalRef1() + ", status:Warning");
						}
						userAccount = new UserAccount();
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
							nameUA.setTitle(titleService.findByCode(provider, uAccount.getName()
									.getTitle().trim()));
							userAccount.setName(nameUA);
						}

						userAccount.setStatus(AccountStatusEnum.ACTIVE);
						userAccount.setStatusDate(new Date());
						userAccount.setProvider(provider);
						userAccountService.createUserAccount(billingAccount.getCode(), userAccount,
								userJob);
						log.info("file:" + fileName
								+ ", typeEntity:UserAccount,  indexBillingAccount:" + i
								+ ", index:" + j + " ExternalRef1:" + uAccount.getExternalRef1()
								+ ", status:Created");
						nbUserAccountsCreated++;
					} catch (Exception e) {
						createUserAccountError(billAccount, uAccount, ExceptionUtils
								.getRootCause(e).getMessage());
						nbUserAccountsError++;
						log.info("file:" + fileName
								+ ", typeEntity:UserAccount,  indexBillingAccount:" + i
								+ ", index:" + j + " ExternalRef1:" + uAccount.getExternalRef1()
								+ ", status:Error");
						e.printStackTrace();
					}
				}
			}
			generateReport(fileName);
			createHistory(provider, userJob);
			log.info("end import file ");
		} finally {
			latch.countDown();
		}
	}

	private void createBillingAccountError(org.meveo.model.jaxb.account.BillingAccount billAccount,
			String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		ErrorBillingAccount errorBillingAccount = new ErrorBillingAccount();
		errorBillingAccount.setCause(cause);
		errorBillingAccount.setCode(billAccount.getCode());
		if (!billingAccountsError.getBillingAccount().contains(billAccount)
				&& "true".equalsIgnoreCase(generateFullCrmReject)) {
			billingAccountsError.getBillingAccount().add(billAccount);
		}
		if (billingAccountsError.getErrors() == null) {
			billingAccountsError.setErrors(new Errors());
		}
		billingAccountsError.getErrors().getErrorBillingAccount().add(errorBillingAccount);
	}

	private void createUserAccountError(org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		ErrorUserAccount errorUserAccount = new ErrorUserAccount();
		errorUserAccount.setCause(cause);
		errorUserAccount.setCode(uAccount.getCode());
		errorUserAccount.setBillingAccountCode(billAccount.getCode());
		if (billingAccountsError.getErrors() == null) {
			billingAccountsError.setErrors(new Errors());
		}
		if (!billingAccountsError.getBillingAccount().contains(billAccount)
				&& "true".equalsIgnoreCase(generateFullCrmReject)) {
			billingAccountsError.getBillingAccount().add(billAccount);
		}

		billingAccountsError.getErrors().getErrorUserAccount().add(errorUserAccount);
	}

	private void createBillingAccountWarning(
			org.meveo.model.jaxb.account.BillingAccount billAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		WarningBillingAccount warningBillingAccount = new WarningBillingAccount();
		warningBillingAccount.setCause(cause);
		warningBillingAccount.setCode(billAccount == null ? "" : billAccount.getCode());
		if (!billingAccountsWarning.getBillingAccount().contains(billAccount)
				&& "true".equalsIgnoreCase(generateFullCrmReject) && billAccount != null) {
			billingAccountsWarning.getBillingAccount().add(billAccount);
		}
		if (billingAccountsWarning.getWarnings() == null) {
			billingAccountsWarning.setWarnings(new Warnings());
		}
		billingAccountsWarning.getWarnings().getWarningBillingAccount().add(warningBillingAccount);
	}

	@SuppressWarnings("unused")
	private void createUserAccountWarning(org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		WarningUserAccount warningUserAccount = new WarningUserAccount();
		warningUserAccount.setCause(cause);
		warningUserAccount.setCode(uAccount.getCode());
		warningUserAccount.setBillingAccountCode(billAccount.getCode());
		if (!billingAccountsWarning.getBillingAccount().contains(billAccount)
				&& "true".equalsIgnoreCase(generateFullCrmReject)) {
			billingAccountsWarning.getBillingAccount().add(billAccount);
		}
		if (billingAccountsWarning.getWarnings() == null) {
			billingAccountsWarning.setWarnings(new Warnings());
		}
		billingAccountsWarning.getWarnings().getWarningUserAccount().add(warningUserAccount);
	}

	private boolean billingAccountCheckError(org.meveo.model.jaxb.account.BillingAccount billAccount) {
		if (StringUtils.isBlank(billAccount.getExternalRef1())) {
			createBillingAccountError(billAccount, "ExternalRef1 is null");
			return true;
		}
		if (StringUtils.isBlank(billAccount.getBillingCycle())) {
			createBillingAccountError(billAccount, "BillingCycle is null");
			return true;
		}
		if (billAccount.getName() == null) {
			createBillingAccountError(billAccount, "Name is null");
			return true;
		}
		if (StringUtils.isBlank(billAccount.getName().getTitle())) {
			createBillingAccountError(billAccount, "Title is null");
			return true;
		}
		if (StringUtils.isBlank(billAccount.getPaymentMethod())
				|| ("DIRECTDEBIT" + "CHECK" + "TIP" + "WIRETRANSFER").indexOf(billAccount
						.getPaymentMethod()) == -1) {
			createBillingAccountError(billAccount,
					"PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}");
			return true;
		}
		if ("DIRECTDEBIT".equals(billAccount.getPaymentMethod())) {
			if (billAccount.getBankCoordinates() == null) {
				createBillingAccountError(billAccount, "BankCoordinates is null");
				return true;
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates().getAccountName())) {
				createBillingAccountError(billAccount, "BankCoordinates.AccountName is null");
				return true;
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates().getAccountNumber())) {
				createBillingAccountError(billAccount, "BankCoordinates.AccountNumber is null");
				return true;
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates().getBankCode())) {
				createBillingAccountError(billAccount, "BankCoordinates.BankCode is null");
				return true;
			}
			if (StringUtils.isBlank(billAccount.getBankCoordinates().getBranchCode())) {
				createBillingAccountError(billAccount, "BankCoordinates.BranchCode is null");
				return true;
			}
		}
		if (billAccount.getAddress() == null
				|| StringUtils.isBlank(billAccount.getAddress().getZipCode())) {
			createBillingAccountError(billAccount, "ZipCode is null");
			return true;
		}
		if (billAccount.getAddress() == null
				|| StringUtils.isBlank(billAccount.getAddress().getCity())) {
			createBillingAccountError(billAccount, "City is null");
			return true;
		}
		if (billAccount.getAddress() == null
				|| StringUtils.isBlank(billAccount.getAddress().getCountry())) {
			createBillingAccountError(billAccount, "Country is null");
			return true;
		}
		return false;
	}

	private boolean userAccountCheckError(org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount) {
		if (StringUtils.isBlank(uAccount.getExternalRef1())) {
			createUserAccountError(billAccount, uAccount, "ExternalRef1 is null");
			return true;
		}
		if (uAccount.getName() == null) {
			createUserAccountError(billAccount, uAccount, "Name is null");
			return true;
		}
		if (StringUtils.isBlank(uAccount.getName().getTitle())) {
			createUserAccountError(billAccount, uAccount, "Title is null");
			return true;
		}
		if (billAccount.getAddress() == null
				|| StringUtils.isBlank(uAccount.getAddress().getZipCode())) {
			createUserAccountError(billAccount, uAccount, "ZipCode is null");
			return true;
		}
		if (billAccount.getAddress() == null
				|| StringUtils.isBlank(uAccount.getAddress().getCity())) {
			createUserAccountError(billAccount, uAccount, "City is null");
			return true;
		}
		if (billAccount.getAddress() == null
				|| StringUtils.isBlank(uAccount.getAddress().getCountry())) {
			createUserAccountError(billAccount, uAccount, "Country is null");
			return true;
		}

		return false;
	}

	private boolean billingAccountCheckWarning(
			org.meveo.model.jaxb.account.BillingAccount billAccount) {
		boolean isWarning = false;
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
			createBillingAccountWarning(billAccount, "Email is null");
			isWarning = true;
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() == null) {
			createBillingAccountWarning(billAccount, "BankCoordinates is null");
			isWarning = true;
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates().getBranchCode())) {
			createBillingAccountWarning(billAccount, "BankCoordinates.BranchCode is null");
			isWarning = true;
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates().getAccountNumber())) {
			createBillingAccountWarning(billAccount, "BankCoordinates.AccountNumber is null");
			isWarning = true;
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates().getBankCode())) {
			createBillingAccountWarning(billAccount, "BankCoordinates.BankCode is null");
			isWarning = true;
		}
		if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod()))
				&& billAccount.getBankCoordinates() != null
				&& StringUtils.isBlank(billAccount.getBankCoordinates().getKey())) {
			createBillingAccountWarning(billAccount, "BankCoordinates.Key is null");
			isWarning = true;
		}
		return isWarning;
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

	private void generateReport(String fileName) throws Exception {
		if (billingAccountsWarning.getWarnings() != null) {
			File dir = new File(param.getProperty("connectorCRM.importAccounts.ouputDir.alert"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(
					billingAccountsWarning,
					new File(param.getProperty("connectorCRM.importAccounts.ouputDir.alert")
							+ File.separator
							+ param.getProperty("connectorCRM.importAccounts.alert.prefix")
							+ fileName));
		}
		if (billingAccountsError.getErrors() != null) {
			File dir = new File(param.getProperty("connectorCRM.importAccounts.ouputDir.error"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(billingAccountsError,
					new File(param.getProperty("connectorCRM.importAccounts.ouputDir.error")
							+ File.separator + fileName));
		}

	}

	private void createHistory(Provider provider, User userJob) throws Exception {
		accountImportHisto.setNbBillingAccounts(nbBillingAccounts);
		accountImportHisto.setNbBillingAccountsCreated(nbBillingAccountsCreated);
		accountImportHisto.setNbBillingAccountsError(nbBillingAccountsError);
		accountImportHisto.setNbBillingAccountsIgnored(nbBillingAccountsIgnored);
		accountImportHisto.setNbBillingAccountsWarning(nbBillingAccountsWarning);
		accountImportHisto.setNbUserAccounts(nbUserAccounts);
		accountImportHisto.setNbUserAccountsCreated(nbUserAccountsCreated);
		accountImportHisto.setNbUserAccountsError(nbUserAccountsError);
		accountImportHisto.setNbUserAccountsIgnored(nbUserAccountsIgnored);
		accountImportHisto.setNbUserAccountsWarning(nbUserAccountsWarning);
		accountImportHisto.setProvider(provider);
		accountImportHistoService.create(accountImportHisto, userJob);
	}
}
