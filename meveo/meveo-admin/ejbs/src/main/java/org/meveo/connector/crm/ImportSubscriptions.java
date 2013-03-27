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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.meveo.commons.utils.DateUtils;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.connector.InputFiles;
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
import org.meveo.service.admin.impl.SubscriptionImportHistoService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @created 22.12.2010
 * 
 */
@Named
public class ImportSubscriptions extends InputFiles {

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

	protected Logger log;

	Subscriptions subscriptionsError;
	Subscriptions subscriptionsWarning;

	ParamBean param = ParamBean.getInstance("meveo-admin.properties");
	int nbSubscriptions;
	int nbSubscriptionsError;
	int nbSubscriptionsTerminated;
	int nbSubscriptionsIgnored;
	int nbSubscriptionsCreated;
	SubscriptionImportHisto subscriptionImportHisto;

	@Asynchronous
	public void importFile(File file, String fileName, CountDownLatch latch) throws JAXBException,
			Exception {

		try {

			log.info("start import file :" + fileName);
			subscriptionsError = new Subscriptions();
			subscriptionsWarning = new Subscriptions();
			nbSubscriptions = 0;
			nbSubscriptionsError = 0;
			nbSubscriptionsTerminated = 0;
			nbSubscriptionsIgnored = 0;
			nbSubscriptionsCreated = 0;

			String providerCode = getProvider(fileName);
			if (providerCode == null) {
				throw new Exception("invalid fileName");
			}
			Provider provider = providerService.findByCode(providerCode);
			if (provider == null) {
				throw new Exception("Cannot found provider : " + providerCode);
			}

			subscriptionImportHisto = new SubscriptionImportHisto();
			subscriptionImportHisto.setExecutionDate(new Date());
			subscriptionImportHisto.setFileName(fileName);
			User userJob = userService.findById(new Long(param.getProperty("connectorCRM.userId")));
			if (file.length() < 100) {
				createSubscriptionWarning(null, "Fichier vide");
				generateReport(fileName);
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
					CheckSubscription checkSubscription = subscriptionCheckError(subscrip);
					if (checkSubscription == null) {
						nbSubscriptionsError++;
						log.info("file:" + fileName + ", typeEntity:Subscription, index:" + i
								+ ", code:" + subscrip.getCode() + ", status:Error");
						continue;
					}
					Subscription subscription = checkSubscription.subscription;
					if (subscription != null) {
						if (!"ACTIVE".equals(subscrip.getStatus().getValue())) {
							if (!provider.getCode().equals(subscription.getProvider().getCode())) {
								createSubscriptionError(subscrip,
										"Conflict subscription.provider and file.provider");
								nbSubscriptionsError++;
								log.info("file:" + fileName + ", typeEntity:Subscription, index:"
										+ i + ", code:" + subscrip.getCode() + ", status:Error");
								continue;
							}
							SubscriptionTerminationReason subscriptionTerminationType = null;
							try {
								subscriptionTerminationType = subscriptionTerminationReasonService
										.findByCodeReason(subscrip.getStatus().getReason(),
												provider.getCode());
							} catch (Exception e) {
							}
							if (subscriptionTerminationType == null) {
								createSubscriptionError(subscrip,
										"subscriptionTerminationType not found for codeReason:"
												+ subscrip.getStatus().getReason());
								nbSubscriptionsError++;
								log.info("file:" + fileName + ", typeEntity:Subscription, index:"
										+ i + ", code:" + subscrip.getCode() + ", status:Error");
								continue;
							}
							try {
								subscriptionService.terminateSubscription(subscription.getCode(),
										DateUtils.parseDateWithPattern(subscrip.getStatus()
												.getDate(), param
												.getProperty("connectorCRM.dateFormat")),
										subscriptionTerminationType, userJob);
								log.info("file:" + fileName + ", typeEntity:Subscription, index:"
										+ i + ", code:" + subscrip.getCode()
										+ ", status:Terminated");

								continue;
							} catch (Exception e) {
								createSubscriptionError(subscrip, e.getMessage());
								nbSubscriptionsError++;
								log.info("file:" + fileName + ", typeEntity:Subscription, index:"
										+ i + ", code:" + subscrip.getCode() + ", status:Error");
								continue;
							}
						} else {
							log.info("file:" + fileName + ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode() + ", status:Ignored");
							nbSubscriptionsIgnored++;
							continue;
						}
					}

					subscription = new Subscription();

					subscription.setOffer(checkSubscription.offerTemplate);
					subscription.setCode(subscrip.getCode());
					subscription.setDescription(subscrip.getDescription());
					subscription.setSubscriptionDate(DateUtils.parseDateWithPattern(
							subscrip.getSubscriptionDate(),
							param.getProperty("connectorCRM.dateFormat")));
					subscription.setEndAgrementDate(DateUtils.parseDateWithPattern(
							subscrip.getEndAgreementDate(),
							param.getProperty("connectorCRM.dateFormat")));
					subscription.setStatusDate(DateUtils.parseDateWithPattern(subscrip.getStatus()
							.getDate(), param.getProperty("connectorCRM.dateFormat")));
					subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
					subscription.setUserAccount(checkSubscription.userAccount);
					subscriptionService.create(subscription, userJob, provider);
					nbSubscriptionsCreated++;
					log.info("file:" + fileName + ", typeEntity:Subscription, index:" + i
							+ ", code:" + subscrip.getCode() + ", status:Created");
					for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : checkSubscription.serviceInsts) {
						try {
							ServiceTemplate serviceTemplate = null;
							ServiceInstance serviceInstance = new ServiceInstance();
							serviceTemplate = serviceTemplateService.findByCode(serviceInst
									.getCode().toUpperCase());
							serviceInstance.setCode(serviceTemplate.getCode());
							serviceInstance.setDescription(serviceTemplate.getDescription());
							serviceInstance.setServiceTemplate(serviceTemplate);
							serviceInstance.setSubscription(subscription);
							serviceInstance.setSubscriptionDate(DateUtils.parseDateWithPattern(
									serviceInst.getSubscriptionDate(),
									param.getProperty("connectorCRM.dateFormat")));
							int quantity = 1;
							if (serviceInst.getQuantity() != null
									&& serviceInst.getQuantity().trim().length() != 0) {
								quantity = Integer.parseInt(serviceInst.getQuantity().trim());
							}
							log.debug("file:" + fileName + ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode() + ", quantity:" + quantity);
							serviceInstance.setQuantity(quantity);
							serviceInstance.setProvider(provider);
							serviceInstanceService.serviceInstanciation(serviceInstance, userJob);
							subscription.getServiceInstances().add(serviceInstance);
							if (serviceInst.getRecurringCharges() != null) {
								if (serviceInstance.getRecurringChargeInstances() != null) {
									for (RecurringChargeInstance recurringChargeInstance : serviceInstance
											.getRecurringChargeInstances()) {
										log.debug("file:" + fileName
												+ ", typeEntity:Subscription, index:" + i
												+ ", code:" + subscrip.getCode()
												+ ", recurringChargeInstance:"
												+ recurringChargeInstance.getCode());
										if (serviceInst.getRecurringCharges().getAmountWithoutTax() != null) {
											recurringChargeInstance
													.setAmountWithoutTax(new BigDecimal(serviceInst
															.getRecurringCharges()
															.getAmountWithoutTax()
															.replace(',', '.')));
											log.debug("file:"
													+ fileName
													+ ", typeEntity:Subscription, index:"
													+ i
													+ ", code:"
													+ subscrip.getCode()
													+ ", recurringChargeInstance.setAmountWithoutTax:"
													+ serviceInst.getRecurringCharges()
															.getAmountWithoutTax());
										}
										if (serviceInst.getRecurringCharges().getAmountWithoutTax() != null) {

											recurringChargeInstance
													.setPrAmountWithoutTax(new BigDecimal(
															serviceInst.getRecurringCharges()
																	.getAmountWithTax()
																	.replace(',', '.')));
											log.debug("file:"
													+ fileName
													+ ", typeEntity:Subscription, index:"
													+ i
													+ ", code:"
													+ subscrip.getCode()
													+ ", recurringChargeInstance.setAmount2:"
													+ serviceInst.getRecurringCharges()
															.getAmountWithTax());
										}
										recurringChargeInstance.setCriteria1(serviceInst
												.getRecurringCharges().getC1());
										recurringChargeInstance.setCriteria2(serviceInst
												.getRecurringCharges().getC2());
										recurringChargeInstance.setCriteria3(serviceInst
												.getRecurringCharges().getC3());
									}
								}
							}

							if (serviceInst.getOneshotCharges() != null) {
								if (serviceInstance.getSubscriptionChargeInstances() != null) {
									for (ChargeInstance subscriptionChargeInstance : serviceInstance
											.getSubscriptionChargeInstances()) {
										if (serviceInst.getOneshotCharges().getAmountWithoutTax() != null) {
											subscriptionChargeInstance
													.setAmountWithoutTax(new BigDecimal(serviceInst
															.getOneshotCharges()
															.getAmountWithoutTax()
															.replace(',', '.')));
											log.debug("file:"
													+ fileName
													+ ", typeEntity:Subscription, index:"
													+ i
													+ ", code:"
													+ subscrip.getCode()
													+ ", subscriptionChargeInstance.setAmountWithoutTax:"
													+ serviceInst.getOneshotCharges()
															.getAmountWithoutTax());
										}
										if (serviceInst.getOneshotCharges().getAmountWithoutTax() != null) {

											subscriptionChargeInstance
													.setPrAmountWithoutTax(new BigDecimal(
															serviceInst.getOneshotCharges()
																	.getAmountWithTax()
																	.replace(',', '.')));
											log.debug("file:"
													+ fileName
													+ ", typeEntity:Subscription, index:"
													+ i
													+ ", code:"
													+ subscrip.getCode()

													+ ", subscriptionChargeInstance.setAmount2:"
													+ serviceInst.getOneshotCharges()
															.getAmountWithTax());
										}
										subscriptionChargeInstance.setCriteria1(serviceInst
												.getOneshotCharges().getC1());
										subscriptionChargeInstance.setCriteria2(serviceInst
												.getOneshotCharges().getC2());
										subscriptionChargeInstance.setCriteria3(serviceInst
												.getOneshotCharges().getC3());
									}
								}
							}

							subscriptionService.update(subscription, userJob);
							serviceInstanceService.serviceActivation(serviceInstance, null, null,
									userJob);
						} catch (Exception e) {
							createServiceInstanceError(subscrip, serviceInst, e.getMessage());
							nbSubscriptionsError++;
							log.info("file:" + fileName + ", typeEntity:Subscription, index:" + i
									+ ", code:" + subscrip.getCode() + ", status:Error");
							e.printStackTrace();
							continue SubscripFOR;
						}

						log.info("file:" + fileName + ", typeEntity:ServiceInstance, index:" + i
								+ ", code:" + serviceInst.getCode() + ", status:Actived");
					}
				} catch (Exception e) {
					// createSubscriptionError(subscrip,
					// ExceptionUtils.getRootCause(e).getMessage());
					createSubscriptionError(subscrip, e.getMessage());
					nbSubscriptionsError++;
					log.info("file:" + fileName + ", typeEntity:Subscription, index:" + i
							+ ", code:" + subscrip.getCode() + ", status:Error");
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
		subscriptionImportHisto.setLinesRead(nbSubscriptions);
		subscriptionImportHisto.setLinesInserted(nbSubscriptionsCreated);
		subscriptionImportHisto.setLinesRejected(nbSubscriptionsError);
		subscriptionImportHisto.setNbSubscriptionsIgnored(nbSubscriptionsIgnored);
		subscriptionImportHisto.setNbSubscriptionsTerminated(nbSubscriptionsTerminated);
		subscriptionImportHisto.setProvider(provider);
		subscriptionImportHistoService.create(subscriptionImportHisto, userJob);

	}

	private void generateReport(String fileName) throws Exception {
		if (subscriptionsWarning.getWarnings() != null) {
			File dir = new File(
					param.getProperty("connectorCRM.importSubscriptions.ouputDir.alert"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(
					subscriptionsWarning,
					new File(param.getProperty("connectorCRM.importSubscriptions.ouputDir.alert")
							+ File.separator
							+ param.getProperty("connectorCRM.importSubscriptions.alert.prefix")
							+ fileName));
		}

		if (subscriptionsError.getErrors() != null) {
			File dir = new File(
					param.getProperty("connectorCRM.importSubscriptions.ouputDir.error"));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			JAXBUtils.marshaller(subscriptionsError,
					new File(param.getProperty("connectorCRM.importSubscriptions.ouputDir.error")
							+ File.separator + fileName));
		}

	}

	private void createSubscriptionError(org.meveo.model.jaxb.subscription.Subscription subscrip,
			String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
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
		subscriptionsError.getErrors().getErrorSubscription().add(errorSubscription);
	}

	private void createSubscriptionWarning(org.meveo.model.jaxb.subscription.Subscription subscrip,
			String cause) {
		String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject");
		WarningSubscription warningSubscription = new WarningSubscription();
		warningSubscription.setCause(cause);
		warningSubscription.setCode(subscrip == null ? "" : subscrip.getCode());
		if (!subscriptionsWarning.getSubscription().contains(subscrip)
				&& "true".equalsIgnoreCase(generateFullCrmReject) && subscrip != null) {
			subscriptionsWarning.getSubscription().add(subscrip);
		}
		if (subscriptionsWarning.getWarnings() == null) {
			subscriptionsWarning.setWarnings(new Warnings());
		}
		subscriptionsWarning.getWarnings().getWarningSubscription().add(warningSubscription);
	}

	private CheckSubscription subscriptionCheckError(
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
				|| ("ACTIVE" + "TERMINATED" + "CANCELED" + "SUSPENDED").indexOf(subscrip
						.getStatus().getValue()) == -1) {
			createSubscriptionError(subscrip,
					"Status is null,or not in {ACTIVE,TERMINATED,CANCELED,SUSPENDED}");
			return null;
		}
		OfferTemplate offerTemplate = null;
		try {
			offerTemplate = offerTemplateService.findByCode(subscrip.getOfferCode().toUpperCase());
		} catch (Exception e) {
		}
		if (offerTemplate == null) {
			createSubscriptionError(subscrip, "cannot found OfferTemplate entity");
			return null;
		}
		checkSubscription.offerTemplate = offerTemplate;
		UserAccount userAccount = null;
		try {
			userAccount = userAccountService.findByExternalRef1(subscrip.getUserAccountId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (userAccount == null) {
			createSubscriptionError(subscrip,
					"cannot found UserAccount entity:" + subscrip.getUserAccountId());
			return null;
		}
		checkSubscription.userAccount = userAccount;

		try {
			checkSubscription.subscription = subscriptionService.findByCode(subscrip.getCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!"ACTIVE".equals(subscrip.getStatus().getValue())
				&& checkSubscription.subscription == null) {
			createSubscriptionError(subscrip,
					"cannot found souscription code:" + subscrip.getCode());
			return null;
		}
		if ("ACTIVE".equals(subscrip.getStatus().getValue())) {
			if (subscrip.getServices() == null
					|| subscrip.getServices().getServiceInstance() == null
					|| subscrip.getServices().getServiceInstance().isEmpty()) {
				createSubscriptionError(subscrip, "cannot create souscription without services");
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
			org.meveo.model.jaxb.subscription.ServiceInstance serviceInst, String cause) {
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
		subscriptionsError.getErrors().getErrorServiceInstance().add(errorServiceInstance);
	}
}

class CheckSubscription {
	OfferTemplate offerTemplate;
	UserAccount userAccount;
	Subscription subscription;
	List<org.meveo.model.jaxb.subscription.ServiceInstance> serviceInsts = new ArrayList<org.meveo.model.jaxb.subscription.ServiceInstance>();
}
