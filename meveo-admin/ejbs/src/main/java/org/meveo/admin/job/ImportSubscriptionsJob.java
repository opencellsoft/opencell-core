package org.meveo.admin.job;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.SubscriptionImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.subscription.ErrorServiceInstance;
import org.meveo.model.jaxb.subscription.ErrorSubscription;
import org.meveo.model.jaxb.subscription.Errors;
import org.meveo.model.jaxb.subscription.Subscriptions;
import org.meveo.model.jaxb.subscription.WarningSubscription;
import org.meveo.model.jaxb.subscription.Warnings;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SubscriptionImportHistoService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class ImportSubscriptionsJob implements Job {
	@Resource
	TimerService timerService;

	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	private Logger log;

	@Inject
	UserService userService;

	@Inject
	SubscriptionService subscriptionService;

	@Inject
	OfferTemplateService offerTemplateService;

	@Inject
	UserAccountService userAccountService;

	@Inject
	SubscriptionTerminationReasonService subscriptionTerminationReasonService;

	@Inject
	ServiceTemplateService serviceTemplateService;

	@Inject
	ServiceInstanceService serviceInstanceService;

	@Inject
	SubscriptionImportHistoService subscriptionImportHistoService;

	@Inject
	private ProviderService providerService;

	ParamBean param = ParamBean.getInstance("meveo-admin.properties");
	String importDir = param.getProperty("connectorCRM.importDir",
			"/tmp/meveo/crm");

	Subscriptions subscriptionsError;
	Subscriptions subscriptionsWarning;

	int nbSubscriptions;
	int nbSubscriptionsError;
	int nbSubscriptionsTerminated;
	int nbSubscriptionsIgnored;
	int nbSubscriptionsCreated;
	SubscriptionImportHisto subscriptionImportHisto;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute ImportSubscriptionsJob.");

		String dirIN = importDir + File.separator + provider.getCode()
				+ File.separator + "subscriptions" + File.separator + "input";
		log.info("dirIN=" + dirIN);
		String dirOK = importDir + File.separator + provider.getCode()
				+ File.separator + "subscriptions" + File.separator + "output";
		String dirKO = importDir + File.separator + provider.getCode()
				+ File.separator + "subscriptions" + File.separator + "reject";
		String prefix = param.getProperty(
				"connectorCRM.importSubscriptions.prefix", "SUB_");
		String ext = param.getProperty(
				"connectorCRM.importSubscriptions.extension", "xml");

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

	public void importFile(File file, String fileName, Provider provider)
			throws JAXBException, Exception {
		log.info("start import file :" + fileName);
		subscriptionsError = new Subscriptions();
		subscriptionsWarning = new Subscriptions();
		nbSubscriptions = 0;
		nbSubscriptionsError = 0;
		nbSubscriptionsTerminated = 0;
		nbSubscriptionsIgnored = 0;
		nbSubscriptionsCreated = 0;

		subscriptionImportHisto = new SubscriptionImportHisto();
		subscriptionImportHisto.setExecutionDate(new Date());
		subscriptionImportHisto.setFileName(fileName);
		User userJob = userService.findById(new Long(param
				.getProperty("connectorCRM.userId")));
		if (file.length() < 100) {
			createSubscriptionWarning(null, "Fichier vide");
			generateReport(fileName, provider);
			createHistory(provider, userJob);
			return;
		}
		Subscriptions subscriptions = (Subscriptions) JAXBUtils.unmarshaller(
				Subscriptions.class, file);
		log.debug("parsing file ok");
		int i = -1;
		nbSubscriptions = subscriptions.getSubscription().size();
		if (nbSubscriptions == 0) {
			createSubscriptionWarning(null, "Fichier vide");
		}
		SubscripFOR: for (org.meveo.model.jaxb.subscription.Subscription subscrip : subscriptions
				.getSubscription()) {
			try {
				i++;
				CheckSubscription checkSubscription = subscriptionCheckError(
						provider, subscrip);
				if (checkSubscription == null) {
					nbSubscriptionsError++;
					log.info("file:" + fileName
							+ ", typeEntity:Subscription, index:" + i
							+ ", code:" + subscrip.getCode() + ", status:Error");
					continue;
				}
				Subscription subscription = checkSubscription.subscription;
				if (subscription != null) {
					if (!"ACTIVE".equals(subscrip.getStatus().getValue())) {
						if (!provider.getCode().equals(
								subscription.getProvider().getCode())) {
							createSubscriptionError(subscrip,
									"Conflict subscription.provider and file.provider");
							nbSubscriptionsError++;
							log.info("file:" + fileName
									+ ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode()
									+ ", status:Error");
							continue;
						}
						SubscriptionTerminationReason subscriptionTerminationType = null;
						try {
							subscriptionTerminationType = subscriptionTerminationReasonService
									.findByCodeReason(subscrip.getStatus()
											.getReason(), provider.getCode());
						} catch (Exception e) {
						}
						if (subscriptionTerminationType == null) {
							createSubscriptionError(subscrip,
									"subscriptionTerminationType not found for codeReason:"
											+ subscrip.getStatus().getReason());
							nbSubscriptionsError++;
							log.info("file:" + fileName
									+ ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode()
									+ ", status:Error");
							continue;
						}
						try {
							subscriptionService
									.terminateSubscription(
											subscription,
											DateUtils.parseDateWithPattern(
													subscrip.getStatus()
															.getDate(),
													param.getProperty("connectorCRM.dateFormat")),
											subscriptionTerminationType,
											userJob);
							log.info("file:" + fileName
									+ ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode()
									+ ", status:Terminated");

							continue;
						} catch (Exception e) {
							createSubscriptionError(subscrip, e.getMessage());
							nbSubscriptionsError++;
							log.info("file:" + fileName
									+ ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode()
									+ ", status:Error");
							continue;
						}
					} else {
						log.info("file:" + fileName
								+ ", typeEntity:Subscription, index:" + i
								+ ", code:" + subscrip.getCode()
								+ ", status:Ignored");
						nbSubscriptionsIgnored++;
						continue;
					}
				}

				subscription = new Subscription();

				subscription.setOffer(checkSubscription.offerTemplate);
				subscription.setCode(subscrip.getCode());
				subscription.setDescription(subscrip.getDescription());
				subscription.setSubscriptionDate(DateUtils
						.parseDateWithPattern(subscrip.getSubscriptionDate(),
								param.getProperty("connectorCRM.dateFormat")));
				subscription.setEndAgrementDate(DateUtils.parseDateWithPattern(
						subscrip.getEndAgreementDate(),
						param.getProperty("connectorCRM.dateFormat")));
				subscription.setStatusDate(DateUtils.parseDateWithPattern(
						subscrip.getStatus().getDate(),
						param.getProperty("connectorCRM.dateFormat")));
				subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
				subscription.setUserAccount(checkSubscription.userAccount);
				subscriptionService.create(subscription, userJob, provider);
				nbSubscriptionsCreated++;
				log.info("file:" + fileName
						+ ", typeEntity:Subscription, index:" + i + ", code:"
						+ subscrip.getCode() + ", status:Created");
				for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : checkSubscription.serviceInsts) {
					try {
						ServiceTemplate serviceTemplate = null;
						ServiceInstance serviceInstance = new ServiceInstance();
						serviceTemplate = serviceTemplateService.findByCode(
								serviceInst.getCode().toUpperCase(), provider);
						serviceInstance.setCode(serviceTemplate.getCode());
						serviceInstance.setDescription(serviceTemplate
								.getDescription());
						serviceInstance.setServiceTemplate(serviceTemplate);
						serviceInstance.setSubscription(subscription);
						serviceInstance
								.setSubscriptionDate(DateUtils.parseDateWithPattern(
										serviceInst.getSubscriptionDate(),
										param.getProperty("connectorCRM.dateFormat")));
						int quantity = 1;
						if (serviceInst.getQuantity() != null
								&& serviceInst.getQuantity().trim().length() != 0) {
							quantity = Integer.parseInt(serviceInst
									.getQuantity().trim());
						}
						log.debug("file:" + fileName
								+ ", typeEntity:Subscription, index:" + i
								+ ", code:" + subscrip.getCode()
								+ ", quantity:" + quantity);
						serviceInstance.setQuantity(quantity);
						serviceInstance.setProvider(provider);
						serviceInstanceService.serviceInstanciation(
								serviceInstance, userJob);
						subscription.getServiceInstances().add(serviceInstance);
						if (serviceInst.getRecurringCharges() != null) {
							if (serviceInstance.getRecurringChargeInstances() != null) {
								for (RecurringChargeInstance recurringChargeInstance : serviceInstance
										.getRecurringChargeInstances()) {
									log.debug("file:"
											+ fileName
											+ ", typeEntity:Subscription, index:"
											+ i + ", code:"
											+ subscrip.getCode()
											+ ", recurringChargeInstance:"
											+ recurringChargeInstance.getCode());
									if (serviceInst.getRecurringCharges()
											.getAmountWithoutTax() != null) {
										recurringChargeInstance
												.setAmountWithoutTax(new BigDecimal(
														serviceInst
																.getRecurringCharges()
																.getAmountWithoutTax()
																.replace(',',
																		'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
										log.debug("file:"
												+ fileName
												+ ", typeEntity:Subscription, index:"
												+ i
												+ ", code:"
												+ subscrip.getCode()
												+ ", recurringChargeInstance.setAmountWithoutTax:"
												+ serviceInst
														.getRecurringCharges()
														.getAmountWithoutTax());
									}
									if (serviceInst.getRecurringCharges()
											.getAmountWithoutTax() != null) {
										recurringChargeInstance
												.setAmountWithTax(new BigDecimal(
														serviceInst
																.getRecurringCharges()
																.getAmountWithTax()
																.replace(',',
																		'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
										log.debug("file:"
												+ fileName
												+ ", typeEntity:Subscription, index:"
												+ i
												+ ", code:"
												+ subscrip.getCode()
												+ ", recurringChargeInstance.setAmount2:"
												+ serviceInst
														.getRecurringCharges()
														.getAmountWithTax());
									}
									recurringChargeInstance
											.setCriteria1(serviceInst
													.getRecurringCharges()
													.getC1());
									recurringChargeInstance
											.setCriteria2(serviceInst
													.getRecurringCharges()
													.getC2());
									recurringChargeInstance
											.setCriteria3(serviceInst
													.getRecurringCharges()
													.getC3());
								}
							}
						}

						if (serviceInst.getOneshotCharges() != null) {
							if (serviceInstance
									.getSubscriptionChargeInstances() != null) {
								for (ChargeInstance subscriptionChargeInstance : serviceInstance
										.getSubscriptionChargeInstances()) {
									if (serviceInst.getOneshotCharges()
											.getAmountWithoutTax() != null) {
										subscriptionChargeInstance
												.setAmountWithoutTax(new BigDecimal(
														serviceInst
																.getOneshotCharges()
																.getAmountWithoutTax()
																.replace(',',
																		'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
										log.debug("file:"
												+ fileName
												+ ", typeEntity:Subscription, index:"
												+ i
												+ ", code:"
												+ subscrip.getCode()
												+ ", subscriptionChargeInstance.setAmountWithoutTax:"
												+ serviceInst
														.getOneshotCharges()
														.getAmountWithoutTax());
									}
									if (serviceInst.getOneshotCharges()
											.getAmountWithoutTax() != null) {
										subscriptionChargeInstance
												.setAmountWithTax(new BigDecimal(
														serviceInst
																.getOneshotCharges()
																.getAmountWithTax()
																.replace(',',
																		'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
										log.debug("file:"
												+ fileName
												+ ", typeEntity:Subscription, index:"
												+ i
												+ ", code:"
												+ subscrip.getCode()
												+ ", subscriptionChargeInstance.setAmount2:"
												+ serviceInst
														.getOneshotCharges()
														.getAmountWithTax());
									}
									subscriptionChargeInstance
											.setCriteria1(serviceInst
													.getOneshotCharges()
													.getC1());
									subscriptionChargeInstance
											.setCriteria2(serviceInst
													.getOneshotCharges()
													.getC2());
									subscriptionChargeInstance
											.setCriteria3(serviceInst
													.getOneshotCharges()
													.getC3());
								}
							}
						}

						subscriptionService.update(subscription, userJob);
						serviceInstanceService.serviceActivation(
								serviceInstance, null, null, userJob);
					} catch (Exception e) {
						createServiceInstanceError(subscrip, serviceInst,
								e.getMessage());
						nbSubscriptionsError++;
						log.info("file:" + fileName
								+ ", typeEntity:Subscription, index:" + i
								+ ", code:" + subscrip.getCode()
								+ ", status:Error");
						e.printStackTrace();
						continue SubscripFOR;
					}

					log.info("file:" + fileName
							+ ", typeEntity:ServiceInstance, index:" + i
							+ ", code:" + serviceInst.getCode()
							+ ", status:Actived");
				}
			} catch (Exception e) {
				// createSubscriptionError(subscrip,
				// ExceptionUtils.getRootCause(e).getMessage());
				createSubscriptionError(subscrip, e.getMessage());
				nbSubscriptionsError++;
				log.info("file:" + fileName
						+ ", typeEntity:Subscription, index:" + i + ", code:"
						+ subscrip.getCode() + ", status:Error");
				e.printStackTrace();
			}
		}
		generateReport(fileName, provider);
		createHistory(provider, userJob);
		log.info("end import file ");

	}

	private void createHistory(Provider provider, User userJob)
			throws Exception {
		subscriptionImportHisto.setLinesRead(nbSubscriptions);
		subscriptionImportHisto.setLinesInserted(nbSubscriptionsCreated);
		subscriptionImportHisto.setLinesRejected(nbSubscriptionsError);
		subscriptionImportHisto
				.setNbSubscriptionsIgnored(nbSubscriptionsIgnored);
		subscriptionImportHisto
				.setNbSubscriptionsTerminated(nbSubscriptionsTerminated);
		subscriptionImportHisto.setProvider(provider);
		subscriptionImportHistoService.create(subscriptionImportHisto, userJob);

	}

	private void generateReport(String fileName, Provider provider)
			throws Exception {
		if (subscriptionsWarning.getWarnings() != null) {
			String warningDir = importDir + File.separator + provider.getCode()
					+ File.separator + "subscriptions" + File.separator
					+ "output" + File.separator + "warnings";
			File dir = new File(warningDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(subscriptionsWarning, new File(warningDir
					+ File.separator + "WARN_" + fileName));
		}

		if (subscriptionsError.getErrors() != null) {
			String errorDir = importDir + File.separator + provider.getCode()
					+ File.separator + "subscriptions" + File.separator
					+ "output" + File.separator + "errors";
			File dir = new File(errorDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(subscriptionsError, new File(errorDir
					+ File.separator + "ERR_" + fileName));
		}

	}

	private void createSubscriptionError(
			org.meveo.model.jaxb.subscription.Subscription subscrip,
			String cause) {
		String generateFullCrmReject = param
				.getProperty("connectorCRM.generateFullCrmReject");
		ErrorSubscription errorSubscription = new ErrorSubscription();
		errorSubscription.setCause(cause);
		errorSubscription.setCode(subscrip.getCode());
		if (!subscriptionsError.getSubscription().contains(subscrip)
				&& "true".equalsIgnoreCase(generateFullCrmReject)) {
			subscriptionsError.getSubscription().add(subscrip);
		}
		if (subscriptionsError.getErrors() == null) {
			subscriptionsError.setErrors(new Errors());
		}
		subscriptionsError.getErrors().getErrorSubscription()
				.add(errorSubscription);
	}

	private void createSubscriptionWarning(
			org.meveo.model.jaxb.subscription.Subscription subscrip,
			String cause) {
		String generateFullCrmReject = param
				.getProperty("connectorCRM.generateFullCrmReject");
		WarningSubscription warningSubscription = new WarningSubscription();
		warningSubscription.setCause(cause);
		warningSubscription.setCode(subscrip == null ? "" : subscrip.getCode());
		if (!subscriptionsWarning.getSubscription().contains(subscrip)
				&& "true".equalsIgnoreCase(generateFullCrmReject)
				&& subscrip != null) {
			subscriptionsWarning.getSubscription().add(subscrip);
		}
		if (subscriptionsWarning.getWarnings() == null) {
			subscriptionsWarning.setWarnings(new Warnings());
		}
		subscriptionsWarning.getWarnings().getWarningSubscription()
				.add(warningSubscription);
	}

	private CheckSubscription subscriptionCheckError(Provider provider,
			org.meveo.model.jaxb.subscription.Subscription subscrip) {
		CheckSubscription checkSubscription = new CheckSubscription();
		if (StringUtils.isBlank(subscrip.getCode())) {
			createSubscriptionError(subscrip, "code is null");
			return null;
		}
		if (StringUtils.isBlank(subscrip.getUserAccountId())) {
			createSubscriptionError(subscrip, "UserAccountId is null");
			return null;
		}
		if (StringUtils.isBlank(subscrip.getOfferCode())) {
			createSubscriptionError(subscrip, "OfferCode is null");
			return null;
		}
		if (StringUtils.isBlank(subscrip.getSubscriptionDate())) {
			createSubscriptionError(subscrip, "SubscriptionDate is null");
			return null;
		}
		if (subscrip.getStatus() == null
				|| StringUtils.isBlank(subscrip.getStatus().getValue())
				|| ("ACTIVE" + "TERMINATED" + "CANCELED" + "SUSPENDED")
						.indexOf(subscrip.getStatus().getValue()) == -1) {
			createSubscriptionError(subscrip,
					"Status is null,or not in {ACTIVE,TERMINATED,CANCELED,SUSPENDED}");
			return null;
		}
		OfferTemplate offerTemplate = null;
		try {
			offerTemplate = offerTemplateService.findByCode(subscrip
					.getOfferCode().toUpperCase(), provider);
		} catch (Exception e) {
		}
		if (offerTemplate == null) {
			createSubscriptionError(subscrip,
					"cannot find OfferTemplate entity");
			return null;
		}
		checkSubscription.offerTemplate = offerTemplate;
		UserAccount userAccount = null;
		try {
			userAccount = userAccountService.findByCode(
					subscrip.getUserAccountId(), provider);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (userAccount == null) {
			createSubscriptionError(subscrip, "cannot find UserAccount entity:"
					+ subscrip.getUserAccountId());
			return null;
		}
		checkSubscription.userAccount = userAccount;

		try {
			checkSubscription.subscription = subscriptionService.findByCode(
					subscrip.getCode(), provider);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!"ACTIVE".equals(subscrip.getStatus().getValue())
				&& checkSubscription.subscription == null) {
			createSubscriptionError(subscrip, "cannot find souscription code:"
					+ subscrip.getCode());
			return null;
		}
		if ("ACTIVE".equals(subscrip.getStatus().getValue())) {
			if (subscrip.getServices() == null
					|| subscrip.getServices().getServiceInstance() == null
					|| subscrip.getServices().getServiceInstance().isEmpty()) {
				createSubscriptionError(subscrip,
						"cannot create souscription without services");
				return null;
			}
			for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : subscrip
					.getServices().getServiceInstance()) {
				if (serviceInstanceCheckError(subscrip, serviceInst)) {
					return null;
				}
				checkSubscription.serviceInsts.add(serviceInst);
			}
		}
		return checkSubscription;
	}

	private boolean serviceInstanceCheckError(
			org.meveo.model.jaxb.subscription.Subscription subscrip,
			org.meveo.model.jaxb.subscription.ServiceInstance serviceInst) {

		if (StringUtils.isBlank(serviceInst.getCode())) {
			createServiceInstanceError(subscrip, serviceInst, "code is null");
			return true;
		}
		if (StringUtils.isBlank(serviceInst.getSubscriptionDate())) {
			createSubscriptionError(subscrip, "SubscriptionDate is null");
			return true;
		}
		return false;
	}

	private void createServiceInstanceError(
			org.meveo.model.jaxb.subscription.Subscription subscrip,
			org.meveo.model.jaxb.subscription.ServiceInstance serviceInst,
			String cause) {
		ErrorServiceInstance errorServiceInstance = new ErrorServiceInstance();
		errorServiceInstance.setCause(cause);
		errorServiceInstance.setCode(serviceInst.getCode());
		errorServiceInstance.setSubscriptionCode(subscrip.getCode());
		if (!subscriptionsError.getSubscription().contains(subscrip)) {
			subscriptionsError.getSubscription().add(subscrip);
		}
		if (subscriptionsError.getErrors() == null) {
			subscriptionsError.setErrors(new Errors());
		}
		subscriptionsError.getErrors().getErrorServiceInstance()
				.add(errorServiceInstance);
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

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
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
}

class CheckSubscription {
	OfferTemplate offerTemplate;
	UserAccount userAccount;
	Subscription subscription;
	List<org.meveo.model.jaxb.subscription.ServiceInstance> serviceInsts = new ArrayList<org.meveo.model.jaxb.subscription.ServiceInstance>();
}
