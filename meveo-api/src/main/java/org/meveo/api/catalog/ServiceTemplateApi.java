package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ServiceChargeTemplateRecurringDto;
import org.meveo.api.dto.catalog.ServiceChargeTemplateSubscriptionDto;
import org.meveo.api.dto.catalog.ServiceChargeTemplateTerminationDto;
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

	private void createServiceChargeTemplateRecurring(ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();

		if (postData.getServiceChargeTemplateRecurrings() != null) {
			for (ServiceChargeTemplateRecurringDto serviceChargeTemplateDto : postData
					.getServiceChargeTemplateRecurrings().getServiceChargeTemplateRecurring()) {
				List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

				RecurringChargeTemplate chargeTemplate = recurringChargeTemplateService.findByCode(
						serviceChargeTemplateDto.getCode(), provider);
				if (chargeTemplate == null) {
					throw new EntityDoesNotExistsException(RecurringChargeTemplate.class,
							serviceChargeTemplateDto.getCode());
				}

				for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
					WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode, provider);
					if (walletTemplate == null) {
						throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
					}

					wallets.add(walletTemplate);
				}

				ServiceChargeTemplateRecurring serviceChargeTemplate = new ServiceChargeTemplateRecurring();
				serviceChargeTemplate.setChargeTemplate(chargeTemplate);
				serviceChargeTemplate.setWalletTemplates(wallets);
				serviceChargeTemplate.setServiceTemplate(serviceTemplate);
				serviceChargeTemplate.setProvider(provider);
				serviceChargeTemplateRecurringService.create(serviceChargeTemplate, currentUser, provider);
			}
		}
	}

	private void createServiceChargeTemplateSubscription(ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();

		if (postData.getServiceChargeTemplateSubscriptions() != null) {
			for (ServiceChargeTemplateSubscriptionDto serviceChargeTemplateDto : postData
					.getServiceChargeTemplateSubscriptions().getServiceChargeTemplateSubscription()) {
				List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

				OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(
						serviceChargeTemplateDto.getCode(), provider);
				if (chargeTemplate == null) {
					throw new EntityDoesNotExistsException(OneShotChargeTemplate.class,
							serviceChargeTemplateDto.getCode());
				}

				for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
					WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode, provider);
					if (walletTemplate == null) {
						throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
					}

					wallets.add(walletTemplate);
				}

				ServiceChargeTemplateSubscription serviceChargeTemplate = new ServiceChargeTemplateSubscription();
				serviceChargeTemplate.setChargeTemplate(chargeTemplate);
				serviceChargeTemplate.setWalletTemplates(wallets);
				serviceChargeTemplate.setServiceTemplate(serviceTemplate);
				serviceChargeTemplate.setProvider(provider);
				serviceChargeTemplateSubscriptionService.create(serviceChargeTemplate, currentUser, provider);
			}
		}
	}

	private void createServiceChargeTemplateTermination(ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();

		if (postData.getServiceChargeTemplateTerminations() != null) {
			for (ServiceChargeTemplateTerminationDto serviceChargeTemplateDto : postData
					.getServiceChargeTemplateTerminations().getServiceChargeTemplateTermination()) {
				List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

				OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(
						serviceChargeTemplateDto.getCode(), provider);
				if (chargeTemplate == null) {
					throw new EntityDoesNotExistsException(OneShotChargeTemplate.class,
							serviceChargeTemplateDto.getCode());
				}

				for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
					WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode, provider);
					if (walletTemplate == null) {
						throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
					}

					wallets.add(walletTemplate);
				}

				ServiceChargeTemplateTermination serviceChargeTemplate = new ServiceChargeTemplateTermination();
				serviceChargeTemplate.setChargeTemplate(chargeTemplate);
				serviceChargeTemplate.setWalletTemplates(wallets);
				serviceChargeTemplate.setServiceTemplate(serviceTemplate);
				serviceChargeTemplate.setProvider(provider);
				serviceChargeTemplateTerminationService.create(serviceChargeTemplate, currentUser, provider);
			}
		}
	}

	private void createServiceChargeTemplateUsage(ServiceTemplateDto postData, User currentUser,
			ServiceTemplate serviceTemplate) throws MeveoApiException {
		Provider provider = currentUser.getProvider();

		if (postData.getServiceChargeTemplateUsages() != null) {
			for (ServiceUsageChargeTemplateDto serviceChargeTemplateDto : postData.getServiceChargeTemplateUsages()
					.getServiceChargeTemplateUsage()) {
				List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

				UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(
						serviceChargeTemplateDto.getCode(), provider);
				if (chargeTemplate == null) {
					throw new EntityDoesNotExistsException(UsageChargeTemplate.class,
							serviceChargeTemplateDto.getCode());
				}

				for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
					WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode, provider);
					if (walletTemplate == null) {
						throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
					}

					wallets.add(walletTemplate);
				}

				ServiceChargeTemplateUsage serviceChargeTemplate = new ServiceChargeTemplateUsage();

				// search for counter
				if (!StringUtils.isBlank(serviceChargeTemplateDto.getCounterTemplate())) {
					CounterTemplate counterTemplate = (CounterTemplate) counterTemplateService.findByCode(
							serviceChargeTemplateDto.getCounterTemplate(), provider);
					if (counterTemplate == null) {
						throw new EntityDoesNotExistsException(CounterTemplate.class,
								serviceChargeTemplateDto.getCounterTemplate());
					}

					serviceChargeTemplate.setCounterTemplate(counterTemplate);
				}

				serviceChargeTemplate.setChargeTemplate(chargeTemplate);
				serviceChargeTemplate.setWalletTemplates(wallets);
				serviceChargeTemplate.setServiceTemplate(serviceTemplate);
				serviceChargeTemplate.setProvider(provider);
				serviceUsageChargeTemplateService.create(serviceChargeTemplate, currentUser, provider);
			}
		}
	}

	public void create(ServiceTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();

			// check if code already exists
			if (serviceTemplateService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(ServiceTemplateService.class, postData.getCode());
			}

			ServiceTemplate serviceTemplate = new ServiceTemplate();
			serviceTemplate.setCode(postData.getCode());
			serviceTemplate.setDescription(postData.getDescription());
			serviceTemplate.setProvider(provider);
			serviceTemplateService.create(serviceTemplate, currentUser, provider);

			// check for recurring charges
			createServiceChargeTemplateRecurring(postData, currentUser, serviceTemplate);

			// check for subscription charges
			createServiceChargeTemplateSubscription(postData, currentUser, serviceTemplate);

			// check for termination charges
			createServiceChargeTemplateTermination(postData, currentUser, serviceTemplate);

			// check for usage charges
			createServiceChargeTemplateUsage(postData, currentUser, serviceTemplate);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(ServiceTemplateDto postData, User currentUser) throws MeveoApiException {

		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			Provider provider = currentUser.getProvider();
			// check if code already exists
			ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(postData.getCode(), provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplateService.class, postData.getCode());
			}
			serviceTemplate.setDescription(postData.getDescription());

			setAllWalletTemplatesToNull(serviceTemplate);

			serviceTemplateService.update(serviceTemplate, currentUser);
			serviceChargeTemplateRecurringService.removeByServiceTemplate(serviceTemplate, provider);
			serviceChargeTemplateSubscriptionService.removeByServiceTemplate(serviceTemplate, provider);
			serviceChargeTemplateTerminationService.removeByServiceTemplate(serviceTemplate, provider);
			serviceUsageChargeTemplateService.removeByServiceTemplate(serviceTemplate, provider);

			// check for recurring charges
			createServiceChargeTemplateRecurring(postData, currentUser, serviceTemplate);

			// check for subscription charges
			createServiceChargeTemplateSubscription(postData, currentUser, serviceTemplate);

			// check for termination charges
			createServiceChargeTemplateTermination(postData, currentUser, serviceTemplate);

			// check for usage charges
			createServiceChargeTemplateUsage(postData, currentUser, serviceTemplate);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public ServiceTemplateDto find(String serviceTemplateCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(serviceTemplateCode)) {
			ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
			}
			ServiceTemplateDto result = new ServiceTemplateDto(serviceTemplate);
			return result;
		} else {
			missingParameters.add("serviceTemplateCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	private void setAllWalletTemplatesToNull(ServiceTemplate serviceTemplate) {
		List<ServiceChargeTemplateRecurring> listRec = new ArrayList<ServiceChargeTemplateRecurring>();
		for (ServiceChargeTemplateRecurring recurring : serviceTemplate.getServiceRecurringCharges()) {
			recurring.setWalletTemplates(null);
			listRec.add(recurring);
		}
		serviceTemplate.setServiceRecurringCharges(listRec);

		List<ServiceChargeTemplateSubscription> listSubs = new ArrayList<ServiceChargeTemplateSubscription>();
		for (ServiceChargeTemplateSubscription subscription : serviceTemplate.getServiceSubscriptionCharges()) {
			subscription.setWalletTemplates(null);
			listSubs.add(subscription);
		}
		serviceTemplate.setServiceSubscriptionCharges(listSubs);

		List<ServiceChargeTemplateTermination> listTerms = new ArrayList<ServiceChargeTemplateTermination>();
		for (ServiceChargeTemplateTermination termination : serviceTemplate.getServiceTerminationCharges()) {
			termination.setWalletTemplates(null);
			listTerms.add(termination);
		}
		serviceTemplate.setServiceTerminationCharges(listTerms);

		List<ServiceChargeTemplateUsage> listUsages = new ArrayList<ServiceChargeTemplateUsage>();
		for (ServiceChargeTemplateUsage usage : serviceTemplate.getServiceUsageCharges()) {
			usage.setWalletTemplates(null);
			listUsages.add(usage);
		}
		serviceTemplate.setServiceUsageCharges(listUsages);
	}

	public void remove(String serviceTemplateCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(serviceTemplateCode)) {
			ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode, provider);
			if (serviceTemplate == null) {
				throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
			}

			setAllWalletTemplatesToNull(serviceTemplate);

			// remove serviceChargeTemplateRecurring
			serviceChargeTemplateRecurringService.removeByServiceTemplate(serviceTemplate, provider);

			// remove serviceChargeTemplateSubscription
			serviceChargeTemplateSubscriptionService.removeByServiceTemplate(serviceTemplate, provider);

			// remove serviceChargeTemplateTermination
			serviceChargeTemplateTerminationService.removeByServiceTemplate(serviceTemplate, provider);

			// remove serviceUsageChargeTemplate
			serviceUsageChargeTemplateService.removeByServiceTemplate(serviceTemplate, provider);

			serviceTemplateService.remove(serviceTemplate);
		} else {
			missingParameters.add("serviceTemplateCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
