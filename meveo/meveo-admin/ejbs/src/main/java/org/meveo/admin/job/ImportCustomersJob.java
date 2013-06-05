package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.RandomStringUtils;
import org.jboss.solder.logging.Logger;
import org.meveo.commons.utils.ExceptionUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.CustomerImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.ErrorCustomer;
import org.meveo.model.jaxb.customer.ErrorCustomerAccount;
import org.meveo.model.jaxb.customer.ErrorSeller;
import org.meveo.model.jaxb.customer.Errors;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jaxb.customer.WarningCustomerAccount;
import org.meveo.model.jaxb.customer.WarningSeller;
import org.meveo.model.jaxb.customer.Warnings;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CustomerImportHistoService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class ImportCustomersJob implements Job {
	@Resource
	TimerService timerService;
	
	@Inject
	JobExecutionService jobExecutionService;

    @Inject
    private Logger log;
    
	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private CustomerService customerService;
	
	@Inject
	private SellerService sellerService;

	@Inject
	UserService userService;

	@Inject
	CustomerBrandService customerBrandService;

	@Inject
	CustomerCategoryService customerCategoryService;

	@Inject
	TradingCountryService tradingCountryService;
	
	@Inject
	TradingCurrencyService tradingCurrencyService;
	
	@Inject
	TradingLanguageService tradingLanguageService;
	
	@Inject
	private TitleService titleService;

	@Inject
	private CustomerImportHistoService customerImportHistoService;

	@Inject
	private ProviderService providerService;

	Sellers sellersWarning;
	Sellers sellersError;

	ParamBean param = ParamBean.getInstance("meveo-admin.properties");

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
	
	int nbCustomerAccounts;
	int nbCustomerAccountsError;
	int nbCustomerAccountsWarning;
	int nbCustomerAccountsIgnored;
	int nbCustomerAccountsCreated;
	CustomerImportHisto customerImportHisto;

    @PostConstruct
    public void init(){
        TimerEntityService.registerJob(this);
    }

    @Override
    public JobExecutionResult execute(String parameter,Provider provider) {
        log.info("execute ImportAccountsJob.");
        
        String dirIN=param.getProperty("connectorCRM.importCustomers.inputDir","/tmp/meveo/crm/input")+File.separator+provider.getCode();
      	String dirOK=param.getProperty("connectorCRM.importCustomers.outputDir","/tmp/meveo/crm/output")+File.separator+provider.getCode();
      	String dirKO=param.getProperty("connectorCRM.importCustomers.rejectDir","/tmp/meveo/crm/output")+File.separator+provider.getCode();
      	String prefix=param.getProperty("connectorCRM.importCustomers.prefix","CUSTOMER_");
      	String ext=param.getProperty("connectorCRM.importCustomers.extension","xml");
   	
      	JobExecutionResultImpl result = new JobExecutionResultImpl();
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
				importFile(currentFile, file.getName(), provider);
				FileUtils.moveFile(dirOK, currentFile, file.getName());
				log.info("InputFiles job " + file.getName() + " done");
				result.registerSucces();
			} catch (Exception e) {
				result.registerError(e.getMessage());
				log.info("InputFiles job " + file.getName() + " failed");
				FileUtils.moveFile(dirKO, currentFile, file.getName());
				e.printStackTrace();
			}
		}
       	 
 
        return result;
    }
    
	private List<File> getFilesToProcess(File dir,String prefix, String ext){
		List<File> files = new ArrayList<File>();
		ImportFileFiltre filtre = new ImportFileFiltre(prefix, ext);
		File[] listFile = dir.listFiles(filtre);
		if(listFile == null){
			return files;			
		}
		for(File file : listFile){
			if(file.isFile()){
				files.add(file);
			}
		}
		return files;
	}
    
	public void importFile(File file, String fileName, Provider provider) throws JAXBException, Exception {

			log.info("start import file :" + fileName);
			sellersWarning = new Sellers();
			sellersError = new Sellers();


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

			CustomerImportHisto customerImportHisto = new CustomerImportHisto();
			customerImportHisto.setExecutionDate(new Date());
			customerImportHisto.setFileName(fileName);
			User userJob = userService.findById(new Long(param.getProperty("connectorCRM.userId")));
			if (file.length() < 83) {
				createSellerWarning(null, "File empty");
				generateReport(fileName);
				createHistory(provider, userJob);
				return;
			}
			Sellers sellers = (Sellers) JAXBUtils.unmarshaller(Sellers.class, file);
			log.debug("parsing file ok");
			int i = -1;

			nbSellers=sellers.getSeller().size();
			if (nbSellers == 0) {
				createSellerWarning(null, "File empty");
			}

			for (org.meveo.model.jaxb.customer.Seller sell : sellers.getSeller()) {
				i++;
				org.meveo.model.admin.Seller seller = null;
				try {
					log.debug("seller found  code:" + sell.getCode());
					try {
						seller = sellerService.findByCode(sell.getCode(),provider);
					} catch (Exception e) {
					}
					if (seller != null) {
						nbSellersIgnored++;
						log.info("file:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode() + ", status:Ignored");
					}
					if (sellerCheckError(sell)) {
						nbSellersError++;
						log.info("file:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
								+ ", status:Error");
						continue;
					}
					for(org.meveo.model.jaxb.customer.Customer cust:sell.getCustomers().getCustomer()){
						createCustomer(fileName, provider, userJob, seller, sell, cust, i);
					}

				} catch (Exception e) {
					createSellerError(sell, ExceptionUtils.getRootCause(e).getMessage());
					nbSellersError++;
					log.info("file:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
							+ ", status:Error");
					e.printStackTrace();
				}

			}
			generateReport(fileName);
			createHistory(provider, userJob);
			log.info("end import file ");

	
	}
	private void createCustomer(String fileName,Provider provider,User userJob,org.meveo.model.admin.Seller seller,org.meveo.model.jaxb.customer.Seller sell,org.meveo.model.jaxb.customer.Customer cust,int i){
		nbSellers++;
		int j=0;
		Customer customer = null;
		try {
			log.debug("customer found  code:" + cust.getCode());
			try {
				customer = customerService.findByCode(cust.getCode(),provider);
			} catch (Exception e) {
			}
			if (customer != null) {
				nbCustomersIgnored++;
				log.info("file:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Ignored");
			}
			if (customerCheckError(sell,cust)) {
				nbCustomersError++;
				log.info("file:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
						+ ", status:Error");
				return;
			}

			if(seller == null){
				seller = new org.meveo.model.admin.Seller();
				seller.setCode(sell.getCode());
				seller.setTradingCountry(tradingCountryService.findByTradingCountryCode(sell.getTradingCountryCode(),provider));
				seller.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(sell.getTradingCurrencyCode(), provider));
				seller.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(sell.getTradingLanguageCode(), provider));
				seller.setProvider(provider);
				sellerService.create(seller, userJob);
				nbSellersCreated++;
				log.info("file:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode()
						+ ", status:Created");
			}
			
			for(org.meveo.model.jaxb.customer.CustomerAccount custAcc : cust.getCustomerAccounts().getCustomerAccount()){
				j++;
				createCustomerAccount(fileName, provider, userJob, customer,seller, custAcc, cust,sell,  i, j);
			}

		} catch (Exception e) {
			createCustomerError(sell,cust, ExceptionUtils.getRootCause(e).getMessage());
			nbCustomersError++;
			log.info("file:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode()
					+ ", status:Error");
			e.printStackTrace();
		}

		
	}
	
	private void createCustomerAccount(String fileName,Provider provider,User userJob,Customer customer,org.meveo.model.admin.Seller seller,org.meveo.model.jaxb.customer.CustomerAccount custAcc,org.meveo.model.jaxb.customer.Customer cust,org.meveo.model.jaxb.customer.Seller sell,int i,int j){
		nbCustomerAccounts++;
		CustomerAccount customerAccountTmp = null;
		try {
			//customerAccountTmp = customerAccountService.findByExternalRef1(custAcc.getExternalRef1());
			customerAccountTmp = customerAccountService.findByCode(custAcc.getCode(),provider);
		} catch (Exception e) {
		}
		if (customerAccountTmp != null) {
			nbCustomerAccountsIgnored++;
			nbCustomersIgnored++;
			log.info("file:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:"
					+ j + " code:" + custAcc.getCode() + ", status:Ignored");
			return;
		}
		log.debug("customerAccount founded  code:" + custAcc.getCode());

		if (customerAccountCheckError(cust,sell, custAcc)) {
			nbCustomerAccountsError++;
			log.info("file:" + fileName + ", typeEntity:CustomerAccount, indexCustomer:" + i + ", index:"
					+ j + " Code:" + custAcc.getCode() + ", status:Error");
			return;
		}

		if (customerAccountCheckWarning(cust,sell, custAcc)) {
			nbCustomerAccountsWarning++;
			log.info("file:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:"
					+ j + " Code:" + custAcc.getCode() + ", status:Warning");
		}
		if (customer == null) {
			customer = new Customer();
			customer.setCode(cust.getCode());
			customer.setDescription(cust.getDesCustomer());
			customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand()));
			customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory()));
			customer.setSeller(seller);
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
		customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode(),provider));
		customerAccount.setProvider(provider);
		customerAccount.setCustomer(customer);
		customerAccountService.create(customerAccount, userJob);
		nbCustomerAccountsCreated++;
		log.info("file:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j
				+ " ExternalRef1:" + custAcc.getExternalRef1() + ", status:Created");
		
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
		customerImportHisto.setNbSellers(nbSellers);
		customerImportHisto.setNbSellersCreated(nbSellersCreated);
		customerImportHisto.setNbSellersError(nbSellersError);
		customerImportHisto.setNbSellersIgnored(nbSellersIgnored);
		customerImportHisto.setNbSellersWarning(nbSellersWarning);
		customerImportHisto.setProvider(provider);
		customerImportHistoService.create(customerImportHisto, userJob);

	}

	private void generateReport(String fileName) throws Exception {
		if (sellersWarning.getWarnings() != null) {
			File dir = new File(param.getProperty("connectorCRM.importCustomers.ouputDir.alert"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(sellersWarning, new File(param
					.getProperty("connectorCRM.importCustomers.ouputDir.alert")
					+ File.separator + param.getProperty("connectorCRM.importCustomers.alert.prefix") + fileName));
		}
		if (sellersError.getErrors() != null) {
			File dir = new File(param.getProperty("connectorCRM.importCustomers.ouputDir.error"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(sellersError, new File(param
					.getProperty("connectorCRM.importCustomers.ouputDir.error")
					+ File.separator + fileName));
		}

	}
	private void createSellerError(org.meveo.model.jaxb.customer.Seller sell, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
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
	private void createCustomerError(org.meveo.model.jaxb.customer.Seller sell,org.meveo.model.jaxb.customer.Customer cust, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
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
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		WarningSeller warningSeller = new WarningSeller();
		warningSeller.setCause(cause);
		warningSeller.setCode(sell == null ? "" : sell.getCode());
		if (!sellersWarning.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject) && sell != null) {
			sellersWarning.getSeller().add(sell);
		}
		if (sellersWarning.getWarnings() == null) {
			sellersWarning.setWarnings(new Warnings());
		}
		sellersWarning.getWarnings().getWarningSeller().add(warningSeller);
	}


	private void createCustomerAccountError(org.meveo.model.jaxb.customer.Seller sell,org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.CustomerAccount custAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
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

	
	private void createCustomerAccountWarning(org.meveo.model.jaxb.customer.Seller sell,org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.CustomerAccount custAccount, String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
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
			createSellerError(sell, "Code is null");
			return true;
		}
		if (StringUtils.isBlank(sell.getTradingCountryCode())) {
			createSellerError(sell, "TradingCountryCode is null");
			return true;
		}
		if (StringUtils.isBlank(sell.getTradingCurrencyCode())) {
			createSellerError(sell, "TradingCurrencyCode is null");
			return true;
		}
		if (StringUtils.isBlank(sell.getTradingLanguageCode())) {
			createSellerError(sell, "TradingLanguageCode is null");
			return true;
		}
		if (sell.getCustomers().getCustomer() == null
				|| sell.getCustomers().getCustomer().isEmpty()) {
			createSellerError(sell, "No customer");
			return true;
		}
		return false;
	}

	private boolean customerCheckError(org.meveo.model.jaxb.customer.Seller sell,org.meveo.model.jaxb.customer.Customer cust) {

		if (StringUtils.isBlank(cust.getDesCustomer())) {
			createCustomerError(sell,cust, "Description is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getCustomerBrand())) {
			createCustomerError(sell,cust, "CustomerBrand is null");
			return true;
		}
		if (StringUtils.isBlank(cust.getCustomerCategory())) {
			createCustomerError(sell,cust, "CustomerCategory is null");
			return true;
		}
		if (cust.getCustomerAccounts().getCustomerAccount() == null
				|| cust.getCustomerAccounts().getCustomerAccount().isEmpty()) {
			createCustomerError(sell,cust, "No customer account");
			return true;
		}
		return false;
	}

	private boolean customerAccountCheckError(org.meveo.model.jaxb.customer.Customer cust,org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
		if (StringUtils.isBlank(custAcc.getPaymentMethod())
				|| ("DIRECTDEBIT" + "CHECK" + "TIP" + "WIRETRANSFER").indexOf(custAcc.getPaymentMethod()) == -1) {
			createCustomerAccountError(sell,cust, custAcc,
					"PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}");
			return true;
		}
		if (custAcc.getAddress() == null || StringUtils.isBlank(custAcc.getAddress().getZipCode())) {

			createCustomerAccountError(sell,cust, custAcc, "ZipCode is null");
			return true;
		}
		if (custAcc.getAddress() == null || StringUtils.isBlank(custAcc.getAddress().getCity())) {
			createCustomerAccountError(sell,cust, custAcc, "City is null");
			return true;
		}
		if (custAcc.getAddress() == null || StringUtils.isBlank(custAcc.getAddress().getCountry())) {
			createCustomerAccountError(sell,cust, custAcc, "Country is null");
			return true;
		}
		if (StringUtils.isBlank(custAcc.getExternalRef1())) {
			createCustomerAccountError(sell,cust, custAcc, "ExternalRef1 is null");
			return true;
		}
		return false;
	}

	private boolean customerAccountCheckWarning(org.meveo.model.jaxb.customer.Customer cust,org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
		boolean isWarning = false;
		if ("PRO".equals(cust.getCustomerCategory()) && StringUtils.isBlank(custAcc.getCompany())) {
			createCustomerAccountWarning(sell,cust, custAcc, "company is null");
			isWarning = true;
		}
		if ((cust.getCustomerCategory().startsWith("PART_"))
				&& (custAcc.getName() == null || StringUtils.isBlank(custAcc.getName().getFirstname()))) {
			createCustomerAccountWarning(sell,cust, custAcc, "name is null");
			isWarning = true;
		}

		return isWarning;
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression,timerConfig);
		return timer.getHandle();
	}

	boolean running=false;
    @Timeout
    public void trigger(Timer timer){
        TimerInfo info = (TimerInfo) timer.getInfo();
        if(!running && info.isActive()){
            try{
                running=true;
                Provider provider=providerService.findById(info.getProviderId());
                JobExecutionResult result=execute(info.getParametres(),provider);
                jobExecutionService.persistResult(this, result,info.getParametres(),provider);
            } catch(Exception e){
                e.printStackTrace();
            } finally{
                running = false;
            }
        }
    }
	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}
	

}
