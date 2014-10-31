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

import org.jboss.solder.logging.Logger;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ExceptionUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.AccountImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.ErrorBillingAccount;
import org.meveo.model.jaxb.account.ErrorUserAccount;
import org.meveo.model.jaxb.account.Errors;
import org.meveo.model.jaxb.account.WarningBillingAccount;
import org.meveo.model.jaxb.account.WarningUserAccount;
import org.meveo.model.jaxb.account.Warnings;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.AccountImportHistoService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.AccountImportService;
import org.meveo.service.crm.impl.ImportWarningException;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class ImportAccountsJob implements Job {
	@Resource
	TimerService timerService;

	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	private Logger log;


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

	@Inject
	private ProviderService providerService;


	@Inject
	TradingCountryService tradingCountryService;

	@Inject
	TradingLanguageService tradingLanguageService;

	@Inject
	AccountImportService accountImportService;
	
	BillingAccounts billingAccountsWarning;
	BillingAccounts billingAccountsError;
	ParamBean param = ParamBean.getInstance();

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


	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute ImportAccountsJob.");
		String importDir = param.getProperty("providers.rootDir", "/tmp/meveo/")+ File.separator + provider.getCode()
				+ File.separator+"imports"+ File.separator+"accounts" + File.separator ;
		String dirIN = importDir + "input";
		log.info("dirIN=" + dirIN);
		String dirOK = importDir + "output";
		String dirKO = importDir + "reject";
		String prefix = param.getProperty("connectorCRM.importAccounts.prefix",
				"ACCOUNT_");
		String ext = param.getProperty("connectorCRM.importAccounts.extension",
				"xml");

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
			} finally {
				if (currentFile != null)
					currentFile.delete();
			}
		}

		return result;
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
			}
		}
		return files;
	}

	public void importFile(File file, String fileName, Provider provider)
			throws JAXBException, Exception {
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

		accountImportHisto.setExecutionDate(new Date());
		accountImportHisto.setFileName(fileName);
		User userJob = userService.findById(new Long(param
				.getProperty("connectorCRM.userId","1")));
		if (file.length() < 83) {
			createBillingAccountWarning(null, "Fichier vide");
			generateReport(fileName, provider);
			createHistory(provider, userJob);
			return;
		}
		BillingAccounts billingAccounts = (BillingAccounts) JAXBUtils
				.unmarshaller(BillingAccounts.class, file);
		log.debug("parsing file ok");

		int i = -1;

		nbBillingAccounts = billingAccounts.getBillingAccount().size();
		if (nbBillingAccounts == 0) {
			createBillingAccountWarning(null, "Fichier vide");
		}
		for (org.meveo.model.jaxb.account.BillingAccount billAccount : billingAccounts
				.getBillingAccount()) {
			nbUserAccounts += billAccount.getUserAccounts().getUserAccount()
					.size();
		}

		for (org.meveo.model.jaxb.account.BillingAccount billAccount : billingAccounts
				.getBillingAccount()) {
			i++;
			int j = -1;
			org.meveo.model.billing.BillingAccount billingAccount = null;
			boolean existBillingAccount = false;
			try {
				try {
					billingAccount = billingAccountService.findByCode(
							billAccount.getCode(), provider);
					billingAccount=accountImportService.importBillingAccount(billAccount, provider, userJob);
					log.info("file6:" + fileName
							+ ", typeEntity:BillingAccount, index:" + i
							+ ", code:" + billAccount.getCode()
							+ ", status:Created");
					nbBillingAccountsCreated++;
				} catch(ImportWarningException w){
					createBillingAccountWarning(billAccount, w.getMessage());
					nbBillingAccountsWarning++;
					log.info("file5:" + fileName
							+ ", typeEntity:BillingAccount,  index:" + i
							+ " code:" + billAccount.getCode()
							+ ", status:Warning");
				
				}
				catch (BusinessException e) {
					createBillingAccountError(billAccount,e.getMessage());
					nbBillingAccountsError++;
					log.info("file2:" + fileName
							+ ", typeEntity:BillingAccount, index:" + i
							+ ", code:" + billAccount.getCode()
							+ ", status:Error");
				}
				if (billingAccount != null) {
					log.info("file1:" + fileName
							+ ", typeEntity:BillingAccount, index:" + i
							+ ", code:" + billAccount.getCode()
							+ ", status:Ignored");
					nbBillingAccountsIgnored++;
					existBillingAccount = true;
				}
				if (!existBillingAccount) {
					//FIXME
				}
			} catch (Exception e) {
				createBillingAccountError(billAccount, ExceptionUtils
						.getRootCause(e).getMessage());
				nbBillingAccountsError++;
				log.info("file7:" + fileName
						+ ", typeEntity:BillingAccount, index:" + i + ", code:"
						+ billAccount.getCode() + ", status:Error");
				e.printStackTrace();
			}

			for (org.meveo.model.jaxb.account.UserAccount uAccount : billAccount
					.getUserAccounts().getUserAccount()) {
				j++;
				UserAccount userAccount = null;
				log.debug("userAccount found code:" + uAccount.getCode());
				try {
						userAccount = userAccountService.findByCode(
								uAccount.getCode(), provider);
				} catch (Exception e) {
				}
				if (userAccount != null) {
						nbUserAccountsIgnored++;
						log.info("file:"
								+ fileName
								+ ", typeEntity:UserAccount,  indexBillingAccount:"
								+ i + ", index:" + j + " code:"
								+ uAccount.getCode() + ", status:Ignored");
				} else {
					try{
						accountImportService.importUserAccount(billingAccount,billAccount,
								uAccount, provider, userJob);
						log.info("file:" + fileName
								+ ", typeEntity:UserAccount,  indexBillingAccount:"
								+ i + ", index:" + j + " code:"
								+ uAccount.getCode() + ", status:Created");
						nbUserAccountsCreated++;
					} catch(ImportWarningException w){
						createUserAccountWarning(billAccount, uAccount, w.getMessage());
						nbUserAccountsWarning++;
						log.info("file:"
								+ fileName
								+ ", typeEntity:UserAccount,  indexBillingAccount:"
								+ i + ", index:" + j + " code:"
								+ uAccount.getCode() + ", status:Warning");
					
					}
					catch (BusinessException e) {
						createUserAccountError(billAccount, uAccount,e.getMessage());
						nbUserAccountsError++;
						log.info("file:"
								+ fileName
								+ ", typeEntity:UserAccount,  indexBillingAccount:"
								+ i + ", index:" + j + " code:"
								+ uAccount.getCode() + ", status:Error");
					}
				}
			}
		}
		generateReport(fileName, provider);
		createHistory(provider, userJob);
		log.info("end import file ");
	}

	private void createBillingAccountError(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			String cause) {
		String generateFullCrmReject = param
				.getProperty("connectorCRM.generateFullCrmReject","true");
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
		billingAccountsError.getErrors().getErrorBillingAccount()
				.add(errorBillingAccount);
	}

	private void createUserAccountError(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount, String cause) {
		String generateFullCrmReject = param
				.getProperty("connectorCRM.generateFullCrmReject","true");
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

		billingAccountsError.getErrors().getErrorUserAccount()
				.add(errorUserAccount);
	}

	private void createBillingAccountWarning(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			String cause) {
		String generateFullCrmReject = param
				.getProperty("connectorCRM.generateFullCrmReject","true");
		WarningBillingAccount warningBillingAccount = new WarningBillingAccount();
		warningBillingAccount.setCause(cause);
		warningBillingAccount.setCode(billAccount == null ? "" : billAccount
				.getCode());
		if (!billingAccountsWarning.getBillingAccount().contains(billAccount)
				&& "true".equalsIgnoreCase(generateFullCrmReject)
				&& billAccount != null) {
			billingAccountsWarning.getBillingAccount().add(billAccount);
		}
		if (billingAccountsWarning.getWarnings() == null) {
			billingAccountsWarning.setWarnings(new Warnings());
		}
		billingAccountsWarning.getWarnings().getWarningBillingAccount()
				.add(warningBillingAccount);
	}

	
	private void createUserAccountWarning(
			org.meveo.model.jaxb.account.BillingAccount billAccount,
			org.meveo.model.jaxb.account.UserAccount uAccount, String cause) {
		String generateFullCrmReject = param
				.getProperty("connectorCRM.generateFullCrmReject","true");
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
		billingAccountsWarning.getWarnings().getWarningUserAccount()
				.add(warningUserAccount);
	}



	private void generateReport(String fileName, Provider provider)
			throws Exception {
		String importDir = param.getProperty("providers.rootDir", "/tmp/meveo/")+ File.separator + provider.getCode()
				+ File.separator+"imports"+ File.separator+"accounts" + File.separator ;
		if (billingAccountsWarning.getWarnings() != null) {
			String warningDir = importDir + "output"
					+ File.separator + "warnings";
			File dir = new File(warningDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(billingAccountsWarning, new File(warningDir
					+ File.separator + "WARN_" + fileName));
		}
		if (billingAccountsError.getErrors() != null) {
			String errorDir = importDir  + "output"
					+ File.separator + "errors";

			File dir = new File(errorDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(billingAccountsError, new File(errorDir
					+ File.separator + "ERR_" + fileName));
		}

	}

	private void createHistory(Provider provider, User userJob)
			throws Exception {
		accountImportHisto.setNbBillingAccounts(nbBillingAccounts);
		accountImportHisto
				.setNbBillingAccountsCreated(nbBillingAccountsCreated);
		accountImportHisto.setNbBillingAccountsError(nbBillingAccountsError);
		accountImportHisto
				.setNbBillingAccountsIgnored(nbBillingAccountsIgnored);
		accountImportHisto
				.setNbBillingAccountsWarning(nbBillingAccountsWarning);
		accountImportHisto.setNbUserAccounts(nbUserAccounts);
		accountImportHisto.setNbUserAccountsCreated(nbUserAccountsCreated);
		accountImportHisto.setNbUserAccountsError(nbUserAccountsError);
		accountImportHisto.setNbUserAccountsIgnored(nbUserAccountsIgnored);
		accountImportHisto.setNbUserAccountsWarning(nbUserAccountsWarning);
		accountImportHisto.setProvider(provider);
		accountImportHistoService.create(accountImportHisto, userJob);
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		//timerConfig.setPersistent(false);
		Timer timer = timerService.createCalendarTimer(scheduleExpression,
				timerConfig);
		return timer.getHandle();
	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;
				Provider provider = providerService.findById(info
						.getProviderId());
				JobExecutionResult result = execute(info.getParametres(),
						provider);
				jobExecutionService.persistResult(this, result, info, provider);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				running = false;
			}
		}
	}

	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}
	
	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}
}
