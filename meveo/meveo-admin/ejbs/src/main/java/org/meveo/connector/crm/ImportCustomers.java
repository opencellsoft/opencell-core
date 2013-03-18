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

import org.apache.commons.lang.RandomStringUtils;
import org.meveo.commons.utils.ExceptionUtils;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.connector.InputFiles;
import org.meveo.model.admin.CustomerImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.Customers;
import org.meveo.model.jaxb.customer.ErrorCustomer;
import org.meveo.model.jaxb.customer.ErrorCustomerAccount;
import org.meveo.model.jaxb.customer.Errors;
import org.meveo.model.jaxb.customer.WarningCustomer;
import org.meveo.model.jaxb.customer.WarningCustomerAccount;
import org.meveo.model.jaxb.customer.Warnings;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.local.CustomerImportHistoServiceLocal;
import org.meveo.service.admin.local.UserServiceLocal;
import org.meveo.service.catalog.local.TitleServiceLocal;
import org.meveo.service.crm.local.CustomerBrandServiceLocal;
import org.meveo.service.crm.local.CustomerCategoryServiceLocal;
import org.meveo.service.crm.local.CustomerServiceLocal;
import org.meveo.service.crm.local.ProviderServiceLocal;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @created 22.12.2010
 * 
 */
@Named
public class ImportCustomers extends InputFiles {

	@Inject
	private CustomerAccountServiceLocal customerAccountService;

	@Inject
	private CustomerServiceLocal customerService;

	@Inject
	UserServiceLocal userService;

	@Inject
	CustomerBrandServiceLocal customerBrandService;

	@Inject
	CustomerCategoryServiceLocal customerCategoryService;

	@Inject
	private TitleServiceLocal titleService;

	@Inject
	private CustomerImportHistoServiceLocal customerImportHistoService;

	@Inject
	private ProviderServiceLocal providerService;

	protected Logger log;

	Customers customersWarning;
	Customers customersError;

	ParamBean param = ParamBean.getInstance("meveo-admin.properties");

	int nbCustomers;
	int nbCustomersError;
	int nbCustomersWarning;
	int nbCustomersIgnored;
	int nbCustomersCreated;

	int nbCustomerAccounts;
	int nbCustomerAccountsError;
	int nbCustomerAccountsWarning;
	int nbCustomerAccountsIgnored;
	int nbCustomerAccountsCreated;
	CustomerImportHisto customerImportHisto;

	@Asynchronous
	public void importFile(File file, String fileName, CountDownLatch latch) throws JAXBException, Exception {

		try {

			log.info("start import file :" + fileName);
			customersWarning = new Customers();
			customersError = new Customers();

			nbCustomers = 0;
			nbCustomersError = 0;
			nbCustomersWarning = 0;
			nbCustomersIgnored = 0;
			nbCustomersCreated = 0;

			nbCustomerAccounts = 0;
			nbCustomerAccountsError = 0;
			nbCustomerAccountsWarning = 0;
			nbCustomerAccountsIgnored = 0;
			nbCustomerAccountsCreated = 0;
			customerImportHisto = new CustomerImportHisto();

			String providerCode = getProvider(fileName);
			if (providerCode == null) {
				throw new Exception("invalid fileName");
			}
			Provider provider = providerService.findByCode(providerCode);
			if (provider == null) {
				throw new Exception("Cannot found provider : " + providerCode);
			}

			CustomerImportHisto customerImportHisto = new CustomerImportHisto();
			customerImportHisto.setExecutionDate(new Date());
			customerImportHisto.setFileName(fileName);
			User userJob = userService.findById(new Long(param.getProperty("connectorCRM.userId")));
			if (file.length() < 83) {
				createCustomerWarning(null, "Fichier vide");
				generateReport(fileName);
				createHistory(provider, userJob);
				return;
			}
			Customers customers = (Customers) JAXBUtils.unmarshaller(Customers.class, file);
			log.debug("parsing file ok");
			int i = -1;

			nbCustomers = customers.getCustomer().size();
			if (nbCustomers == 0) {
				createCustomerWarning(null, "Fichier vide");
			}
			for (org.meveo.model.jaxb.customer.Customer cust : customers.getCustomer()) {
				nbCustomerAccounts += cust.getCustomerAccounts().getCustomerAccount().size();
			}

			for (org.meveo.model.jaxb.customer.Customer cust : customers.getCustomer()) {
				i++;
				int j = 0;// un seul customerAccount par customer
				Customer customer = null;
				try {
					log.debug("customer founded  code:" + cust.getCode());
					try {
						customer = customerService.findByCode(cust.getCode());
					} catch (Exception e) {
					}
					if (customer != null) {
						nbCustomersIgnored++;
						log.info("file:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Ignored");
					}
					if (customerCheckError(cust)) {
						nbCustomersError++;
						log.info("file:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
								+ ", status:Error");
						continue;
					}
					org.meveo.model.jaxb.customer.CustomerAccount custAcc = cust.getCustomerAccounts()
							.getCustomerAccount().get(0);
					CustomerAccount customerAccountTmp = null;
					try {
						customerAccountTmp = customerAccountService.findByExternalRef1(custAcc.getExternalRef1());
					} catch (Exception e) {
					}
					if (customerAccountTmp != null) {
						nbCustomerAccountsIgnored++;
						nbCustomersIgnored++;
						log.info("file:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:"
								+ j + " ExternalRef1:" + custAcc.getExternalRef1() + ", status:Ignored");
						continue;
					}
					log.debug("customerAccount founded  code:" + custAcc.getCode());

					if (customerAccountCheckError(cust, custAcc)) {
						nbCustomerAccountsError++;
						log.info("file:" + fileName + ", typeEntity:CustomerAccount, indexCustomer:" + i + ", index:"
								+ j + " ExternalRef1:" + custAcc.getExternalRef1() + ", status:Error");
						continue;
					}

					if (customerAccountCheckWarning(cust, custAcc)) {
						nbCustomerAccountsWarning++;
						log.info("file:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:"
								+ j + " ExternalRef1:" + custAcc.getExternalRef1() + ", status:Warning");
					}
					if (customer == null) {
						customer = new Customer();
						customer.setCode(cust.getCode());
						customer.setDescription(cust.getDesCustomer());
						customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand()));
						customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory()));
						customer.setProvider(provider);
						customerService.create(customer, userJob);
						nbCustomersCreated++;
						log.info("file:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
								+ ", status:Created");
					}

					CustomerAccount customerAccount = new CustomerAccount();
					customerAccount.setCode(custAcc.getCode());
					customerAccount.setDescription(custAcc.getDescription());
					customerAccount.setDateDunningLevel(new Date());
					customerAccount.setDunningLevel(DunningLevelEnum.R0);
					customerAccount.setPassword(RandomStringUtils.randomAlphabetic(8));
					customerAccount.setDateStatus(new Date());
					customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
					Address address = new Address();
					address.setAddress1(custAcc.getAddress().getAddress1());
					address.setAddress2(custAcc.getAddress().getAddress2());
					address.setAddress3(custAcc.getAddress().getAddress3());
					address.setCity(custAcc.getAddress().getCity());
					address.setCountry(custAcc.getAddress().getCountry());
					address.setZipCode("" + custAcc.getAddress().getZipCode());
					address.setState(custAcc.getAddress().getState());
					customerAccount.setAddress(address);
					ContactInformation contactInformation = new ContactInformation();
					contactInformation.setEmail(custAcc.getEmail());
					contactInformation.setPhone(custAcc.getTel1());
					contactInformation.setMobile(custAcc.getTel2());
					customerAccount.setContactInformation(contactInformation);
					customerAccount.setCreditCategory(CreditCategoryEnum.valueOf(custAcc.getCreditCategory()));
					customerAccount.setExternalRef1(custAcc.getExternalRef1());
					customerAccount.setExternalRef2(custAcc.getExternalRef2());
					customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(custAcc.getPaymentMethod()));
					org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
					if (custAcc.getName() != null) {
						name.setFirstName(custAcc.getName().getFirstname());
						name.setLastName(custAcc.getName().getName());
						Title title = titleService.findByCode(provider, custAcc.getName().getTitle().trim());
						name.setTitle(title);
						customerAccount.setName(name);
					}
					customerAccount.setProvider(provider);
					customerAccount.setCustomer(customer);
					customerAccountService.create(customerAccount, userJob);
					nbCustomerAccountsCreated++;
					log.info("file:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j
							+ " ExternalRef1:" + custAcc.getExternalRef1() + ", status:Created");
				} catch (Exception e) {
					createCustomerError(cust, ExceptionUtils.getRootCause(e).getMessage());
					nbCustomersError++;
					nbCustomerAccountsError++;
					log.info("file:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
							+ ", status:Error");
					e.printStackTrace();
				}

			}
			generateReport(fileName);
			createHistory(provider, userJob);
			log.info("end import file ");

		} finally {
			latch.countDown();
		}
	}

	private void createHistory(Provider provider, User userJob) throws Exception {
		customerImportHisto.setNbCustomerAccounts(nbCustomerAccounts);
		customerImportHisto.setNbCustomerAccountsCreated(nbCustomerAccountsCreated);
		customerImportHisto.setNbCustomerAccountsError(nbCustomerAccountsError);
		customerImportHisto.setNbCustomerAccountsIgnored(nbCustomerAccountsIgnored);
		customerImportHisto.setNbCustomerAccountsWarning(nbCustomerAccountsWarning);
		customerImportHisto.setNbCustomers(nbCustomers);
		customerImportHisto.setNbCustomersCreated(nbCustomersCreated);
		customerImportHisto.setNbCustomersError(nbCustomersError);
		customerImportHisto.setNbCustomersIgnored(nbCustomersIgnored);
		customerImportHisto.setNbCustomersWarning(nbCustomersWarning);
		customerImportHisto.setProvider(provider);
		customerImportHistoService.create(customerImportHisto, userJob);

	}

	private void generateReport(String fileName) throws Exception {
		if (customersWarning.getWarnings() != null) {
			File dir = new File(param.getProperty("connectorCRM.importCustomers.ouputDir.alert"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(customersWarning, new File(param
					.getProperty("connectorCRM.importCustomers.ouputDir.alert")
					+ File.separator + param.getProperty("connectorCRM.importCustomers.alert.prefix") + fileName));
		}
		if (customersError.getErrors() != null) {
			File dir = new File(param.getProperty("connectorCRM.importCustomers.ouputDir.error"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(customersError, new File(param
					.getProperty("connectorCRM.importCustomers.ouputDir.error")
					+ File.separator + fileName));
		}

	}

	private void createCustomerError(org.meveo.model.jaxb.customer.Customer cust, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		ErrorCustomer errorCustomer = new ErrorCustomer();
		errorCustomer.setCause(cause);
		errorCustomer.setCode(cust.getCode());
		if (!customersError.getCustomer().contains(cust) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			customersError.getCustomer().add(cust);
		}
		if (customersError.getErrors() == null) {
			customersError.setErrors(new Errors());
		}
		customersError.getErrors().getErrorCustomer().add(errorCustomer);
	}

	private void createCustomerWarning(org.meveo.model.jaxb.customer.Customer cust, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		WarningCustomer warningCustomer = new WarningCustomer();
		warningCustomer.setCause(cause);
		warningCustomer.setCode(cust == null ? "" : cust.getCode());
		if (!customersWarning.getCustomer().contains(cust) && "true".equalsIgnoreCase(generateFullCrmReject) && cust != null) {
			customersWarning.getCustomer().add(cust);
		}
		if (customersWarning.getWarnings() == null) {
			customersWarning.setWarnings(new Warnings());
		}
		customersWarning.getWarnings().getWarningCustomer().add(warningCustomer);
	}

	private void createCustomerAccountError(org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.CustomerAccount custAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		ErrorCustomerAccount errorCustomerAccount = new ErrorCustomerAccount();
		errorCustomerAccount.setCause(cause);
		errorCustomerAccount.setCode(custAccount.getCode());
		errorCustomerAccount.setCustomerCode(cust.getCode());
		if (customersError.getErrors() == null) {
			customersError.setErrors(new Errors());
		}
		if (!customersError.getCustomer().contains(cust) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			customersError.getCustomer().add(cust);
		}

		customersError.getErrors().getErrorCustomerAccount().add(errorCustomerAccount);
	}

	private void createCustomerAccountWarning(org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.CustomerAccount custAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		WarningCustomerAccount warningCustomerAccount = new WarningCustomerAccount();
		warningCustomerAccount.setCause(cause);
		warningCustomerAccount.setCode(custAccount.getCode());
		warningCustomerAccount.setCustomerCode(cust.getCode());
		if (!customersWarning.getCustomer().contains(cust) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			customersWarning.getCustomer().add(cust);
		}
		if (customersWarning.getWarnings() == null) {
			customersWarning.setWarnings(new Warnings());
		}
		customersWarning.getWarnings().getWarningCustomerAccount().add(warningCustomerAccount);
	}

	private boolean customerCheckError(org.meveo.model.jaxb.customer.Customer cust) {

		if (StringUtils.isBlank(cust.getDesCustomer())) {
			createCustomerError(cust, "Description is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getCustomerBrand())) {
			createCustomerError(cust, "CustomerBrand is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getCustomerCategory())) {
			createCustomerError(cust, "CustomerCategory is null");
			return true;
		}
		if (cust.getCustomerAccounts().getCustomerAccount() == null
				|| cust.getCustomerAccounts().getCustomerAccount().isEmpty()) {
			createCustomerError(cust, "No customerAcount");
			return true;
		}
		return false;
	}

	private boolean customerAccountCheckError(org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
		if (StringUtils.isBlank(custAcc.getPaymentMethod())
				|| ("DIRECTDEBIT" + "CHECK" + "TIP" + "WIRETRANSFER").indexOf(custAcc.getPaymentMethod()) == -1) {
			createCustomerAccountError(cust, custAcc,
					"PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}");
			return true;
		}
		if (custAcc.getAddress() == null || StringUtils.isBlank(custAcc.getAddress().getZipCode())) {

			createCustomerAccountError(cust, custAcc, "ZipCode is null");
			return true;
		}
		if (custAcc.getAddress() == null || StringUtils.isBlank(custAcc.getAddress().getCity())) {
			createCustomerAccountError(cust, custAcc, "City is null");
			return true;
		}
		if (custAcc.getAddress() == null || StringUtils.isBlank(custAcc.getAddress().getCountry())) {
			createCustomerAccountError(cust, custAcc, "Country is null");
			return true;
		}
		if (StringUtils.isBlank(custAcc.getExternalRef1())) {
			createCustomerAccountError(cust, custAcc, "ExternalRef1 is null");
			return true;
		}
		return false;
	}

	private boolean customerAccountCheckWarning(org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
		boolean isWarning = false;
		if ("PRO".equals(cust.getCustomerCategory()) && StringUtils.isBlank(custAcc.getCompany())) {
			createCustomerAccountWarning(cust, custAcc, "company is null");
			isWarning = true;
		}
		if ((cust.getCustomerCategory().startsWith("PART_"))
				&& (custAcc.getName() == null || StringUtils.isBlank(custAcc.getName().getFirstname()))) {
			createCustomerAccountWarning(cust, custAcc, "name is null");
			isWarning = true;
		}

		return isWarning;
	}
}
