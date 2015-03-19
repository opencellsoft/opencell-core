package org.meveo.admin.job.importexport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ExceptionUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.CustomerImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.ErrorCustomer;
import org.meveo.model.jaxb.customer.ErrorCustomerAccount;
import org.meveo.model.jaxb.customer.ErrorSeller;
import org.meveo.model.jaxb.customer.Errors;
import org.meveo.model.jaxb.customer.Seller;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jaxb.customer.WarningCustomerAccount;
import org.meveo.model.jaxb.customer.WarningSeller;
import org.meveo.model.jaxb.customer.Warnings;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.CustomerImportHistoService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.CustomerImportService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
public class ImportCustomersJobBean {

	@Inject
	private Logger log;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	@Inject
	private CustomerImportHistoService customerImportHistoService;

	@EJB
	private CustomerImportService customerImportService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private CustomerService customerService;

	@Inject
	private SellerService sellerService;

	Sellers sellersWarning;
	Sellers sellersError;

	ParamBean param = ParamBean.getInstance();

	int nbCustomers;
	int nbCustomersError;
	int nbCustomersWarning;
	int nbCustomersIgnored;
	int nbCustomersCreated;

	int nbSellers;
	int nbSellersError;
	int nbSellersWarning;
	int nbSellersIgnored;
	int nbSellersCreated;

	int nbSellersUpdated;
	int nbCustomersUpdated;
	int nbCustomerAccountsUpdated;

	int nbCustomerAccounts;
	int nbCustomerAccountsError;
	int nbCustomerAccountsWarning;
	int nbCustomerAccountsIgnored;
	int nbCustomerAccountsCreated;
	CustomerImportHisto customerImportHisto;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, User currentUser) {
		Provider provider = currentUser.getProvider();

		String importDir = param.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode()
				+ File.separator + "imports" + File.separator + "customers" + File.separator;
		String dirIN = importDir + "input";
		log.info("dirIN=" + dirIN);
		String dirOK = importDir + "output";
		String dirKO = importDir + "reject";
		String prefix = param.getProperty("connectorCRM.importCustomers.prefix", "CUSTOMER_");
		String ext = param.getProperty("connectorCRM.importCustomers.extension", "xml");

		File dir = new File(dirIN);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		List<File> files = getFilesToProcess(dir, prefix, ext);
		int numberOfFiles = files.size();
		log.info("InputFiles job " + numberOfFiles + " to import");
		result.setNbItemsToProcess(numberOfFiles);

		for (File file : files) {
			File currentFile = null;
			try {
				log.info("InputFiles job " + file.getName() + " in progres");
				currentFile = FileUtils.addExtension(file, ".processing");

				importFile(currentFile, file.getName(), currentUser);

				FileUtils.moveFile(dirOK, currentFile, file.getName());
				log.info("InputFiles job " + file.getName() + " done");
				result.registerSucces();
			} catch (Exception e) {
				result.registerError(e.getMessage());
				log.info("InputFiles job " + file.getName() + " failed");
				FileUtils.moveFile(dirKO, currentFile, file.getName());
				log.error(e.getMessage());
			} finally {
				if (currentFile != null)
					currentFile.delete();
			}
		}
	}

	private List<File> getFilesToProcess(File dir, String prefix, String ext) {
		List<File> files = new ArrayList<File>();
		ImportFileFiltre filtre = new ImportFileFiltre(prefix, ext);
		File[] listFile = dir.listFiles(filtre);

		if (listFile == null) {
			return files;
		}

		for (File file : listFile) {
			if (file.isFile()) {
				files.add(file);
				// we just process one file
				return files;
			}
		}

		return files;
	}

	public void importFile(File file, String fileName, User currentUser) throws JAXBException, Exception {

		log.info("start import file :" + fileName);

		Provider provider = currentUser.getProvider();
		sellersWarning = new Sellers();
		sellersError = new Sellers();

		nbSellersUpdated = 0;
		nbCustomersUpdated = 0;
		nbCustomerAccountsUpdated = 0;

		nbSellers = 0;
		nbSellersError = 0;
		nbSellersWarning = 0;
		nbSellersIgnored = 0;
		nbSellersCreated = 0;

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

		customerImportHisto.setExecutionDate(new Date());
		customerImportHisto.setFileName(fileName);

		if (file.length() < 83) {
			createSellerWarning(null, "File empty");
			generateReport(fileName, provider);
			createHistory(currentUser);
			return;
		}

		Sellers sellers = (Sellers) JAXBUtils.unmarshaller(Sellers.class, file);
		log.debug("parsing file ok");
		int i = -1;

		nbSellers = sellers.getSeller().size();
		if (nbSellers == 0) {
			createSellerWarning(null, "File empty");
		}

		for (org.meveo.model.jaxb.customer.Seller sell : sellers.getSeller()) {
			i++;
			org.meveo.model.admin.Seller seller = null;
			try {
				log.debug("seller found  code:" + sell.getCode());

				if (sellerCheckError(sell)) {
					nbSellersError++;
					log.error("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
							+ ", status:Error");
					continue;
				}

				seller = createSeller(sell, fileName, i, currentUser, provider);

				for (org.meveo.model.jaxb.customer.Customer cust : sell.getCustomers().getCustomer()) {
					if (customerCheckError(sell, cust)) {
						nbCustomersError++;
						log.error("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
								+ ", status:Error");
						return;
					}

					createCustomer(fileName, currentUser, seller, sell, cust, i);
				}
			} catch (Exception e) {
				createSellerError(sell, ExceptionUtils.getRootCause(e).getMessage());
				nbSellersError++;
				log.error("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
						+ ", status:Error");
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		generateReport(fileName, provider);
		createHistory(currentUser);
		log.info("end import file ");
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private org.meveo.model.admin.Seller createSeller(Seller sell, String fileName, int i, User currentUser,
			Provider provider) {
		org.meveo.model.admin.Seller seller = null;
		try {
			seller = sellerService.findByCode(sell.getCode(), provider);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}

		if (seller != null) {
			nbSellersUpdated++;
			seller.setDescription(sell.getDescription());
			seller.setTradingCountry(tradingCountryService.findByTradingCountryCode(sell.getTradingCountryCode(),
					provider));
			seller.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(sell.getTradingCurrencyCode(),
					provider));
			seller.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(sell.getTradingLanguageCode(),
					provider));
			seller.updateAudit(currentUser);
			customerImportService.updateSeller(seller);
			log.info("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
					+ ", status:Updated");
		} else {
			nbSellersCreated++;
			log.info("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
					+ ", status:Created");

			seller = new org.meveo.model.admin.Seller();
			seller.setCode(sell.getCode());
			seller.setDescription(sell.getDescription());
			seller.setTradingCountry(tradingCountryService.findByTradingCountryCode(sell.getTradingCountryCode(),
					provider));
			seller.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(sell.getTradingCurrencyCode(),
					provider));
			seller.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(sell.getTradingLanguageCode(),
					provider));
			seller.setProvider(provider);
			customerImportService.createSeller(seller, currentUser, provider);
		}

		return seller;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void createCustomer(String fileName, User currentUser, org.meveo.model.admin.Seller seller,
			org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust, int i) {
		Provider provider = currentUser.getProvider();
		nbSellers++;
		int j = 0;
		Customer customer = null;

		try {
			log.debug("customer found code={}", cust.getCode());

			try {
				customer = customerService.findByCode(cust.getCode(), provider);
			} catch (Exception e) {
				log.warn(e.getMessage());
			}

			if (customer != null) {
				if (!customer.getSeller().getCode().equals(sell.getCode())) {
					createCustomerError(sell, cust,
							"The customer already exists but is attached to a different seller.");
					nbCustomersError++;
					log.error("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
							+ ", status:Error");
					return;
				}

				nbCustomersUpdated++;
				customer = customerImportService.updateCustomer(customer, currentUser, seller, sell, cust);
				log.info("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
						+ ", status:Updated");
			} else {
				nbCustomersCreated++;
				customer = customerImportService.createCustomer(currentUser, seller, sell, cust);
				log.info("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
						+ ", status:Created");
			}

			for (org.meveo.model.jaxb.customer.CustomerAccount custAcc : cust.getCustomerAccounts()
					.getCustomerAccount()) {
				j++;

				if (customerAccountCheckError(cust, sell, custAcc)) {
					nbCustomerAccountsError++;
					log.error("File:" + fileName + ", typeEntity:CustomerAccount, indexCustomer:" + i + ", index:" + j
							+ " Code:" + custAcc.getCode() + ", status:Error");
					continue;
				}

				if (customerAccountCheckWarning(cust, sell, custAcc)) {
					nbCustomerAccountsWarning++;
					log.info("File:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j
							+ " Code:" + custAcc.getCode() + ", status:Warning");
				}

				createCustomerAccount(fileName, currentUser, customer, seller, custAcc, cust, sell, i, j);
			}
		} catch (Exception e) {
			createCustomerError(sell, cust, ExceptionUtils.getRootCause(e).getMessage());
			nbCustomersError++;
			log.error("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
					+ ", status:Error");
			log.error(e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void createCustomerAccount(String fileName, User currentUser, Customer customer,
			org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
			org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell, int i, int j) {
		nbCustomerAccounts++;
		CustomerAccount customerAccountTmp = null;

		try {
			customerAccountTmp = customerAccountService.findByCode(custAcc.getCode(), currentUser.getProvider());
		} catch (Exception e) {
			log.warn(e.getMessage());
		}

		if (customerAccountTmp != null) {
			if (!customerAccountTmp.getCustomer().getCode().equals(cust.getCode())) {
				nbCustomerAccountsError++;
				createCustomerAccountError(sell, cust, custAcc,
						"A customer account with same code exists for another customer");
				return;
			}

			customerImportService.updateCustomerAccount(customerAccountTmp, currentUser, customer, seller, custAcc,
					cust, sell);
			nbCustomerAccountsUpdated++;
			log.info("File:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j
					+ " code:" + custAcc.getCode() + ", status:Updated");
		} else {
			customerImportService.createCustomerAccount(currentUser, customer, seller, custAcc, cust, sell);
			nbCustomerAccountsCreated++;
			log.info("File:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j
					+ " code:" + custAcc.getCode() + ", status:Created");
		}
	}

	private void createHistory(User currentUser) throws Exception {
		Provider provider = currentUser.getProvider();
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
		customerImportHisto.setNbSellers(nbSellers);
		customerImportHisto.setNbSellersCreated(nbSellersCreated);
		customerImportHisto.setNbSellersError(nbSellersError);
		customerImportHisto.setNbSellersIgnored(nbSellersIgnored);
		customerImportHisto.setNbSellersWarning(nbSellersWarning);
		customerImportHisto.setProvider(provider);
		customerImportHistoService.create(customerImportHisto, currentUser, provider);
	}

	private void generateReport(String fileName, Provider provider) throws Exception {
		String importDir = param.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode()
				+ File.separator + "imports" + File.separator + "customers" + File.separator;

		if (sellersWarning.getWarnings() != null) {
			String warningDir = importDir + "output" + File.separator + "warnings";
			File dir = new File(warningDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(sellersWarning, new File(warningDir + File.separator + "WARN_" + fileName));
		}

		if (sellersError.getErrors() != null) {
			String errorDir = importDir + "output" + File.separator + "errors";

			File dir = new File(errorDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(sellersError, new File(errorDir + File.separator + "ERR_" + fileName));
		}
	}

	private void createSellerError(org.meveo.model.jaxb.customer.Seller sell, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
		ErrorSeller errorSeller = new ErrorSeller();
		errorSeller.setCause(cause);
		errorSeller.setCode(sell.getCode());

		if (!sellersError.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			sellersError.getSeller().add(sell);
		}

		if (sellersError.getErrors() == null) {
			sellersError.setErrors(new Errors());
		}

		sellersError.getErrors().getErrorSeller().add(errorSeller);
	}

	private void createCustomerError(org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.Customer cust, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
		ErrorCustomer errorCustomer = new ErrorCustomer();
		errorCustomer.setCause(cause);
		errorCustomer.setCode(cust.getCode());

		if (!sellersError.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			sellersError.getSeller().add(sell);
		}

		if (sellersError.getErrors() == null) {
			sellersError.setErrors(new Errors());
		}

		sellersError.getErrors().getErrorCustomer().add(errorCustomer);
	}

	private void createSellerWarning(org.meveo.model.jaxb.customer.Seller sell, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
		WarningSeller warningSeller = new WarningSeller();
		warningSeller.setCause(cause);
		warningSeller.setCode(sell == null ? "" : sell.getCode());

		if (!sellersWarning.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)
				&& sell != null) {
			sellersWarning.getSeller().add(sell);
		}

		if (sellersWarning.getWarnings() == null) {
			sellersWarning.setWarnings(new Warnings());
		}

		sellersWarning.getWarnings().getWarningSeller().add(warningSeller);
	}

	private void createCustomerAccountError(org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.CustomerAccount custAccount,
			String cause) {
		log.error("Seller={}, customer={}, customerAccount={}, cause={}",
				new Object[] { sell, cust, custAccount, cause });
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
		ErrorCustomerAccount errorCustomerAccount = new ErrorCustomerAccount();
		errorCustomerAccount.setCause(cause);
		errorCustomerAccount.setCode(custAccount.getCode());
		errorCustomerAccount.setCustomerCode(cust.getCode());

		if (sellersError.getErrors() == null) {
			sellersError.setErrors(new Errors());
		}

		if (!sellersError.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			sellersError.getSeller().add(sell);
		}

		sellersError.getErrors().getErrorCustomerAccount().add(errorCustomerAccount);
	}

	private void createCustomerAccountWarning(org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.CustomerAccount custAccount,
			String cause) {
		log.warn("Seller={}, customer={}, customerAccount={}, cause={}",
				new Object[] { sell, cust, custAccount, cause });
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
		WarningCustomerAccount warningCustomerAccount = new WarningCustomerAccount();
		warningCustomerAccount.setCause(cause);
		warningCustomerAccount.setCode(custAccount.getCode());
		warningCustomerAccount.setCustomerCode(cust.getCode());

		if (!sellersWarning.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
			sellersWarning.getSeller().add(sell);
		}

		if (sellersWarning.getWarnings() == null) {
			sellersWarning.setWarnings(new Warnings());
		}

		sellersWarning.getWarnings().getWarningCustomerAccount().add(warningCustomerAccount);
	}

	private boolean sellerCheckError(org.meveo.model.jaxb.customer.Seller sell) {
		if (StringUtils.isBlank(sell.getCode())) {
			createSellerError(sell, "Code is null.");
			return true;
		}

		if (StringUtils.isBlank(sell.getTradingCountryCode())) {
			createSellerError(sell, "TradingCountryCode is null.");
			return true;
		}

		if (StringUtils.isBlank(sell.getTradingCurrencyCode())) {
			createSellerError(sell, "TradingCurrencyCode is null.");
			return true;
		}

		if (StringUtils.isBlank(sell.getTradingLanguageCode())) {
			createSellerError(sell, "TradingLanguageCode is null.");
			return true;
		}

		if (sell.getCustomers().getCustomer() == null || sell.getCustomers().getCustomer().isEmpty()) {
			createSellerError(sell, "No customer.");
			return true;
		}

		return false;
	}

	private boolean customerCheckError(org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.Customer cust) {

		if (StringUtils.isBlank(cust.getCode())) {
			createCustomerError(sell, cust, "Code is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getDesCustomer())) {
			createCustomerError(sell, cust, "Description is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getCustomerCategory())) {
			createCustomerError(sell, cust, "CustomerCategory is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getCustomerBrand())) {
			createCustomerError(sell, cust, "CustomerBrand is null");
			return true;
		}

		if (cust.getCustomerAccounts().getCustomerAccount() == null
				|| cust.getCustomerAccounts().getCustomerAccount().isEmpty()) {
			createCustomerError(sell, cust, "No customer account");
			return true;
		}

		return false;
	}

	private boolean customerAccountCheckError(org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
		if (StringUtils.isBlank(custAcc.getCode())) {
			createCustomerAccountError(sell, cust, custAcc, "Code is null");
			return true;
		}
		if (StringUtils.isBlank(custAcc.getDescription())) {
			createCustomerAccountError(sell, cust, custAcc, "Description is null");
			return true;
		}
		if (StringUtils.isBlank(custAcc.getTradingCurrencyCode())) {
			createCustomerAccountError(sell, cust, custAcc, "Currency is null");
			return true;
		}
		if (StringUtils.isBlank(custAcc.getCreditCategory())) {
			createCustomerAccountError(sell, cust, custAcc, "Credit Category is null");
			return true;
		}
		if (custAcc.getName() == null || StringUtils.isBlank(custAcc.getName().getName())) {
			createCustomerAccountError(sell, cust, custAcc, "Lastname is null");
			return true;
		}
		/*
		 * if (StringUtils.isBlank(custAcc.getPaymentMethod()) || ("DIRECTDEBIT"
		 * + "CHECK" + "TIP" +
		 * "WIRETRANSFER").indexOf(custAcc.getPaymentMethod()) == -1) {
		 * createCustomerAccountError(sell,cust, custAcc,
		 * "PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}"
		 * ); return true; } if (custAcc.getAddress() == null ||
		 * StringUtils.isBlank(custAcc.getAddress().getZipCode())) {
		 * 
		 * createCustomerAccountError(sell,cust, custAcc, "ZipCode is null");
		 * return true; } if (custAcc.getAddress() == null ||
		 * StringUtils.isBlank(custAcc.getAddress().getCity())) {
		 * createCustomerAccountError(sell,cust, custAcc, "City is null");
		 * return true; } if (custAcc.getAddress() == null ||
		 * StringUtils.isBlank(custAcc.getAddress().getCountry())) {
		 * createCustomerAccountError(sell,cust, custAcc, "Country is null");
		 * return true; } if (StringUtils.isBlank(custAcc.getExternalRef1())) {
		 * createCustomerAccountError(sell,cust, custAcc,
		 * "ExternalRef1 is null"); return true; }
		 */
		return false;
	}

	private boolean customerAccountCheckWarning(org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
		boolean isWarning = false;

		if ("PRO".equals(cust.getCustomerCategory()) && StringUtils.isBlank(custAcc.getCompany())) {
			createCustomerAccountWarning(sell, cust, custAcc, "Company is null");
			isWarning = true;
		}

		if ((cust.getCustomerCategory().startsWith("PART_"))
				&& (custAcc.getName() == null || StringUtils.isBlank(custAcc.getName().getFirstname()))) {
			createCustomerAccountWarning(sell, cust, custAcc, "Name is null");
			isWarning = true;
		}

		return isWarning;
	}

}
