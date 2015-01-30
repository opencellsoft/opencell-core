package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.ServiceUsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ServiceTemplateApi extends BaseApi {

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	@Inject
	private WalletTemplateService walletTemplateService;

	@Inject
	private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;

	@Inject
	private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;

	@Inject
	private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;

	@Inject
	private ServiceChargeTemplateUsageService serviceUsageChargeTemplateService;

	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	private void createServiceChargeTemplateRecurring(
			ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		ServiceChargeTemplateRecurring serviceChargeTemplateRecurring = null;
		Provider provider = currentUser.getProvider();
		RecurringChargeTemplate chargeTempRecurring = null;
		if (postData.getServiceChargeTemplateRecurrings() != null) {

			List<WalletTemplate> wallets = null;

			for (Map<String, List<String>> servChargRecs : postData
					.getServiceChargeTemplateRecurrings()) {
				for (String chargeCode : servChargRecs.keySet()) {
					wallets = new ArrayList<WalletTemplate>();
					serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();
					chargeTempRecurring = recurringChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTempRecurring == null) {
						throw new EntityDoesNotExistsException(
								RecurringChargeTemplate.class, chargeCode);
					}
					List<String> wallettemplateCodes = servChargRecs
							.get(chargeCode);

					for (String walletCode : wallettemplateCodes) {
						WalletTemplate walletTemplate = walletTemplateService
								.findByCode(walletCode);
						if (walletTemplate == null) {
							throw new EntityDoesNotExistsException(
									WalletTemplate.class, walletCode);
						}
						wallets.add(walletTemplate);
					}
					serviceChargeTemplateRecurring
							.setChargeTemplate(chargeTempRecurring);
					serviceChargeTemplateRecurring.setWalletTemplates(wallets);
					serviceChargeTemplateRecurring
							.setServiceTemplate(serviceTemplate);
					serviceChargeTemplateRecurring.setProvider(provider);
					serviceChargeTemplateRecurringService.create(
							serviceChargeTemplateRecurring, currentUser,
							provider);
				}
			}
		}
	}

	private void createServiceChargeTemplateSubscription(
			ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		ServiceChargeTemplateSubscription serviceChargeTemplateSubscription = null;
		OneShotChargeTemplate chargeTemplateSub = null;
		if (postData.getServiceChargeTemplateSubscriptions() != null) {
			List<WalletTemplate> wallets = null;

			for (Map<String, List<String>> servChargSubs : postData
					.getServiceChargeTemplateSubscriptions()) {
				for (String chargeCode : servChargSubs.keySet()) {
					wallets = new ArrayList<WalletTemplate>();
					serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();
					chargeTemplateSub = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplateSub == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}
					List<String> wallettemplateCodes = servChargSubs
							.get(chargeCode);

					for (String walletCode : wallettemplateCodes) {
						WalletTemplate walletTemplate = walletTemplateService
								.findByCode(walletCode);
						if (walletTemplate == null) {
							throw new EntityDoesNotExistsException(
									WalletTemplate.class, walletCode);
						}
						wallets.add(walletTemplate);
					}

					serviceChargeTemplateSubscription
							.setChargeTemplate(chargeTemplateSub);
					serviceChargeTemplateSubscription
							.setWalletTemplates(wallets);
					serviceChargeTemplateSubscription
							.setServiceTemplate(serviceTemplate);
					serviceChargeTemplateSubscription.setProvider(provider);
					serviceChargeTemplateSubscriptionService.create(
							serviceChargeTemplateSubscription, currentUser,
							provider);

				}

			}
		}
	}

	private void createServiceChargeTemplateTermination(
			ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		ServiceChargeTemplateTermination serviceChargeTemplateTermination = null;
		OneShotChargeTemplate chargeTemplateTerm = null;
		if (postData.getServiceChargeTemplateTerminations() != null
				&& postData.getServiceChargeTemplateTerminations().size() > 0) {

			List<WalletTemplate> wallets = null;

			for (Map<String, List<String>> servChargSubs : postData
					.getServiceChargeTemplateTerminations()) {
				for (String chargeCode : servChargSubs.keySet()) {
					wallets = new ArrayList<WalletTemplate>();
					serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();
					chargeTemplateTerm = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTemplateTerm == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}

					List<String> wallettemplateCodes = servChargSubs
							.get(chargeCode);

					for (String walletCode : wallettemplateCodes) {
						WalletTemplate walletTemplate = walletTemplateService
								.findByCode(walletCode);
						if (walletTemplate == null) {
							throw new EntityDoesNotExistsException(
									WalletTemplate.class, walletCode);
						}
						wallets.add(walletTemplate);
					}
					serviceChargeTemplateTermination
							.setChargeTemplate(chargeTemplateTerm);
					serviceChargeTemplateTermination
							.setWalletTemplates(wallets);
					serviceChargeTemplateTermination
							.setServiceTemplate(serviceTemplate);
					serviceChargeTemplateTermination.setProvider(provider);
					serviceChargeTemplateTerminationService.create(
							serviceChargeTemplateTermination, currentUser,
							provider);
				}
			}
		}
	}

	private void createServiceChargeTemplateUsage(ServiceTemplateDto postData,
			User currentUser, ServiceTemplate serviceTemplate)
			throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		if (postData.getServiceChargeTemplateUsages() != null
				&& postData.getServiceChargeTemplateUsages().size() > 0) {
			List<WalletTemplate> wallets = null;
			for (ServiceUsageChargeTemplateDto serviceUsageChargeTemplateDto : postData
					.getServiceChargeTemplateUsages()) {
				// search for usageCharge
				UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService
						.findByCode(serviceUsageChargeTemplateDto
								.getUsageChargeTemplate(), provider);
				if (usageChargeTemplate == null) {
					throw new EntityDoesNotExistsException(
							UsageChargeTemplate.class,
							serviceUsageChargeTemplateDto
									.getUsageChargeTemplate());
				}

				// search for counter
				CounterTemplate counterTemplate = (CounterTemplate) counterTemplateService
						.findByCode(serviceUsageChargeTemplateDto
								.getCounterTemplate(), provider);
				if (counterTemplate == null) {
					throw new EntityDoesNotExistsException(
							CounterTemplate.class,
							serviceUsageChargeTemplateDto.getCounterTemplate());
				}
				wallets = new ArrayList<WalletTemplate>();
				for (String walletCode : serviceUsageChargeTemplateDto
						.getWalletTemplates()) {
					WalletTemplate walletTemplate = walletTemplateService
							.findByCode(walletCode);
					if (walletTemplate == null) {
						throw new EntityDoesNotExistsException(
								WalletTemplate.class, walletCode);
					}
					wallets.add(walletTemplate);
				}
				ServiceChargeTemplateUsage serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();
				serviceChargeTemplateUsage
						.setChargeTemplate(usageChargeTemplate);
				serviceChargeTemplateUsage.setCounterTemplate(counterTemplate);
				serviceChargeTemplateUsage.setServiceTemplate(serviceTemplate);
				serviceChargeTemplateUsage.setWalletTemplates(wallets);
				serviceChargeTemplateUsage.setProvider(provider);
				serviceUsageChargeTemplateService.create(
						serviceChargeTemplateUsage, currentUser, provider);
			}
		}

	}

	public void create(ServiceTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			// check if code already exists
			if (serviceTemplateService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(
						ServiceTemplateService.class, postData.getCode());
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setCode(postData.getCode());
			serviceTemplate.setDescription(postData.getDescription());
			serviceTemplate.setProvider(provider);
			serviceTemplateService.create(serviceTemplate, currentUser,
					provider);

			// check for recurring charges
			createServiceChargeTemplateRecurring(postData, currentUser,
					serviceTemplate);
			// check for subscription charges
			createServiceChargeTemplateSubscription(postData, currentUser,
					serviceTemplate);
			// check for termination charges
			createServiceChargeTemplateTermination(postData, currentUser,
					serviceTemplate);
			// check for usage charges
			createServiceChargeTemplateUsage(postData, currentUser,
					serviceTemplate);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	private void updateServiceChargeTemplateRecurring(
			ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		ServiceChargeTemplateRecurring serviceChargeTemplateRecurring = null;
		RecurringChargeTemplate chargeTempRecurring = null;
		if (postData.getServiceChargeTemplateRecurrings() != null) {
			List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();
			for (Map<String, List<String>> servChargRecs : postData
					.getServiceChargeTemplateRecurrings()) {
				for (String chargeCode : servChargRecs.keySet()) {
					serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurring();
					chargeTempRecurring = recurringChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTempRecurring == null) {
						throw new EntityDoesNotExistsException(
								RecurringChargeTemplate.class, chargeCode);
					}
					List<String> wallettemplateCodes = servChargRecs
							.get(chargeCode);
					for (String walletCode : wallettemplateCodes) {
						WalletTemplate walletTemplate = walletTemplateService
								.findByCode(walletCode);
						if (walletTemplate == null) {
							throw new EntityDoesNotExistsException(
									WalletTemplate.class, walletCode);
						}
						wallets.add(walletTemplate);
					}
					serviceChargeTemplateRecurring
							.setChargeTemplate(chargeTempRecurring);
					serviceChargeTemplateRecurring.setWalletTemplates(wallets);
					serviceChargeTemplateRecurring
							.setServiceTemplate(serviceTemplate);
					serviceChargeTemplateRecurring.setProvider(provider);
					serviceChargeTemplateRecurringService.create(
							serviceChargeTemplateRecurring, currentUser,
							provider);
				}
			}
		}
	}

	private void updateServiceChargeTemplateSubscription(
			ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		ServiceChargeTemplateSubscription serviceChargeTemplateSubscription = null;
		OneShotChargeTemplate chargeTempSubscription = null;
		if (postData.getServiceChargeTemplateSubscriptions() != null) {
			List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();
			for (Map<String, List<String>> servChargSubs : postData
					.getServiceChargeTemplateSubscriptions()) {
				for (String chargeCode : servChargSubs.keySet()) {
					serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscription();
					chargeTempSubscription = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTempSubscription == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}
					List<String> wallettemplateCodes = servChargSubs
							.get(chargeCode);

					for (String walletCode : wallettemplateCodes) {
						WalletTemplate walletTemplate = walletTemplateService
								.findByCode(walletCode);
						if (walletTemplate == null) {
							throw new EntityDoesNotExistsException(
									WalletTemplate.class, walletCode);
						}
						wallets.add(walletTemplate);
					}
					serviceChargeTemplateSubscription
							.setChargeTemplate(chargeTempSubscription);
					serviceChargeTemplateSubscription
							.setWalletTemplates(wallets);
					serviceChargeTemplateSubscription
							.setServiceTemplate(serviceTemplate);
					serviceChargeTemplateSubscription.setProvider(provider);
					serviceChargeTemplateSubscriptionService.create(
							serviceChargeTemplateSubscription, currentUser,
							provider);
				}
			}
		}
	}

	private void updateServiceChargeTemplateTermination(
			ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		ServiceChargeTemplateTermination serviceChargeTemplateTermination = null;
		OneShotChargeTemplate chargeTempTermination = null;
		if (postData.getServiceChargeTemplateTerminations() != null) {
			List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();
			for (Map<String, List<String>> servChargTerms : postData
					.getServiceChargeTemplateTerminations()) {
				for (String chargeCode : servChargTerms.keySet()) {
					serviceChargeTemplateTermination = new ServiceChargeTemplateTermination();
					chargeTempTermination = oneShotChargeTemplateService
							.findByCode(chargeCode, provider);
					if (chargeTempTermination == null) {
						throw new EntityDoesNotExistsException(
								OneShotChargeTemplate.class, chargeCode);
					}
					List<String> wallettemplateCodes = servChargTerms
							.get(chargeCode);

					for (String walletCode : wallettemplateCodes) {
						WalletTemplate walletTemplate = walletTemplateService
								.findByCode(walletCode);
						if (walletTemplate == null) {
							throw new EntityDoesNotExistsException(
									WalletTemplate.class, walletCode);
						}
						wallets.add(walletTemplate);
					}
					serviceChargeTemplateTermination
							.setChargeTemplate(chargeTempTermination);
					serviceChargeTemplateTermination
							.setWalletTemplates(wallets);
					serviceChargeTemplateTermination
							.setServiceTemplate(serviceTemplate);
					serviceChargeTemplateTermination.setProvider(provider);
					serviceChargeTemplateTerminationService.create(
							serviceChargeTemplateTermination, currentUser,
							provider);
				}
			}
		}
	}

	private void updateServiceChargeTemplateUsage(ServiceTemplateDto postData,
			User currentUser, ServiceTemplate serviceTemplate)
			throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		if (postData.getServiceChargeTemplateUsages() != null
				&& postData.getServiceChargeTemplateUsages().size() > 0) {
			List<WalletTemplate> wallets = null;
			for (ServiceUsageChargeTemplateDto serviceUsageChargeTemplateDto : postData
					.getServiceChargeTemplateUsages()) {
				// search for usageCharge
				UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService
						.findByCode(serviceUsageChargeTemplateDto
								.getUsageChargeTemplate(), provider);
				if (usageChargeTemplate == null) {
					throw new EntityDoesNotExistsException(
							UsageChargeTemplate.class,
							serviceUsageChargeTemplateDto
									.getUsageChargeTemplate());
				}
				// search for counter
				CounterTemplate counterTemplate = (CounterTemplate) counterTemplateService
						.findByCode(serviceUsageChargeTemplateDto
								.getCounterTemplate(), provider);
				if (counterTemplate == null) {
					throw new EntityDoesNotExistsException(
							CounterTemplate.class,
							serviceUsageChargeTemplateDto.getCounterTemplate());
				}
				wallets = new ArrayList<WalletTemplate>();
				for (String walletCode : serviceUsageChargeTemplateDto
						.getWalletTemplates()) {
					WalletTemplate walletTemplate = walletTemplateService
							.findByCode(walletCode);
					if (walletTemplate == null) {
						throw new EntityDoesNotExistsException(
								WalletTemplate.class, walletCode);
					}
					wallets.add(walletTemplate);
				}
				ServiceChargeTemplateUsage serviceChargeTemplateUsage = new ServiceChargeTemplateUsage();
				serviceChargeTemplateUsage
						.setChargeTemplate(usageChargeTemplate);
				serviceChargeTemplateUsage.setWalletTemplates(wallets);
				serviceChargeTemplateUsage.setCounterTemplate(counterTemplate);
				serviceChargeTemplateUsage.setServiceTemplate(serviceTemplate);
				serviceChargeTemplateUsage.setProvider(provider);
				serviceUsageChargeTemplateService.create(
						serviceChargeTemplateUsage, currentUser, provider);
			}
		}
	}

	public void update(ServiceTemplateDto postData, User currentUser)
			throws MeveoApiException {

		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();
			// check if code already exists
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(postData.getCode(), provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(
						ServiceTemplateService.class, postData.getCode());
			}
			serviceTemplate.setDescription(postData.getDescription());

			// TODO set walletTemplates to null
			setAllwaletTemplatsToNull(serviceTemplate);

			serviceTemplateService.update(serviceTemplate, currentUser);
			serviceChargeTemplateRecurringService.removeByServiceTemplate(
					serviceTemplate, provider);
			serviceChargeTemplateSubscriptionService.removeByServiceTemplate(
					serviceTemplate, provider);
			serviceChargeTemplateTerminationService.removeByServiceTemplate(
					serviceTemplate, provider);
			serviceUsageChargeTemplateService.removeByServiceTemplate(
					serviceTemplate, provider);

			// search for recurring charges
			updateServiceChargeTemplateRecurring(postData, currentUser,
					serviceTemplate);

			// search for subscription charges
			updateServiceChargeTemplateSubscription(postData, currentUser,
					serviceTemplate);

			// search for termination charges
			updateServiceChargeTemplateTermination(postData, currentUser,
					serviceTemplate);
			// search for usageCharge
			updateServiceChargeTemplateUsage(postData, currentUser,
					serviceTemplate);

		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public ServiceTemplateDto find(String serviceTemplateCode, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(serviceTemplateCode)) {
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplate.class,
						serviceTemplateCode);
			}
			ServiceTemplateDto result = new ServiceTemplateDto(serviceTemplate);
			return result;
		} else {
			missingParameters.add("serviceTemplateCode");
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	private void setAllwaletTemplatsToNull(ServiceTemplate serviceTemplate) {
		// TODO set walletTemplates to null

		List<ServiceChargeTemplateRecurring> listRec = new ArrayList<ServiceChargeTemplateRecurring>();
		for (ServiceChargeTemplateRecurring recurring : serviceTemplate
				.getServiceRecurringCharges()) {
			recurring.setWalletTemplates(null);
			listRec.add(recurring);
		}
		serviceTemplate.setServiceRecurringCharges(listRec);

		List<ServiceChargeTemplateSubscription> listSubs = new ArrayList<ServiceChargeTemplateSubscription>();
		for (ServiceChargeTemplateSubscription subscription : serviceTemplate
				.getServiceSubscriptionCharges()) {
			subscription.setWalletTemplates(null);
			listSubs.add(subscription);
		}
		serviceTemplate.setServiceSubscriptionCharges(listSubs);

		List<ServiceChargeTemplateTermination> listTerms = new ArrayList<ServiceChargeTemplateTermination>();
		for (ServiceChargeTemplateTermination termination : serviceTemplate
				.getServiceTerminationCharges()) {
			termination.setWalletTemplates(null);
			listTerms.add(termination);
		}
		serviceTemplate.setServiceTerminationCharges(listTerms);

		List<ServiceChargeTemplateUsage> listUsages = new ArrayList<ServiceChargeTemplateUsage>();
		for (ServiceChargeTemplateUsage usage : serviceTemplate
				.getServiceUsageCharges()) {
			usage.setWalletTemplates(null);
			listUsages.add(usage);
		}
		serviceTemplate.setServiceUsageCharges(listUsages);
	}

	public void remove(String serviceTemplateCode, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(serviceTemplateCode)) {
			ServiceTemplate serviceTemplate = serviceTemplateService
					.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplate.class,
						serviceTemplateCode);
			}
			// TODO set walletTemplates to null
			setAllwaletTemplatsToNull(serviceTemplate);
			// remove serviceChargeTemplateRecurring
			serviceChargeTemplateRecurringService.removeByServiceTemplate(
					serviceTemplate, provider);

			// remove serviceChargeTemplateSubscription
			serviceChargeTemplateSubscriptionService.removeByServiceTemplate(
					serviceTemplate, provider);

			// remove serviceChargeTemplateTermination
			serviceChargeTemplateTerminationService.removeByServiceTemplate(
					serviceTemplate, provider);

			// remove serviceUsageChargeTemplate
			serviceUsageChargeTemplateService.removeByServiceTemplate(
					serviceTemplate, provider);

			serviceTemplateService.remove(serviceTemplate);
		} else {
			missingParameters.add("serviceTemplateCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
