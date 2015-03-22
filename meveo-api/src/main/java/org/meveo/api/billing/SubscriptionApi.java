package org.meveo.api.billing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.account.ActivateServicesDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceDto;
import org.meveo.api.dto.billing.ServiceToActivateDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.api.dto.billing.TerminateSubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;

@Stateless
public class SubscriptionApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private OneShotChargeInstanceService oneShotChargeInstanceService;

	@Inject
	private TerminationReasonService terminationReasonService;

	@Inject
	private WalletTemplateService walletTemplateService;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	public void create(SubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getOfferTemplate())
				&& !StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getSubscriptionDate())) {
			Provider provider = currentUser.getProvider();

			if (subscriptionService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(Subscription.class, postData.getCode());
			}

			UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount(), provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
			}

			Subscription subscription = new Subscription();
			subscription.setCode(postData.getCode());
			subscription.setDescription(postData.getDescription());
			subscription.setUserAccount(userAccount);
			subscription.setOffer(offerTemplate);

			// populate customFields
			if (postData.getCustomFields() != null) {
				for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) {
					// check if custom field exists has a template
					List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(
							AccountLevelEnum.SUB, provider);
					if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
						for (CustomFieldTemplate cft : customFieldTemplates) {
							if (cf.getCode().equals(cft.getCode())) {
								// create
								CustomFieldInstance cfiNew = new CustomFieldInstance();
								cfiNew.setSubscription(subscription);
								cfiNew.setActive(true);
								cfiNew.setCode(cf.getCode());
								cfiNew.setDateValue(cf.getDateValue());
								cfiNew.setDescription(cf.getDescription());
								cfiNew.setDoubleValue(cf.getDoubleValue());
								cfiNew.setLongValue(cf.getLongValue());
								cfiNew.setProvider(currentUser.getProvider());
								cfiNew.setStringValue(cf.getStringValue());
								cfiNew.updateAudit(currentUser);
								subscription.getCustomFields().put(cfiNew.getCode(), cfiNew);
							}
						}
					} else {
						log.warn("No custom field template defined.");
					}
				}
			}

			subscription.setSubscriptionDate(postData.getSubscriptionDate());
			subscription.setTerminationDate(postData.getTerminationDate());

			subscriptionService.create(subscription, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getUserAccount())) {
				missingParameters.add("userAccount");
			}
			if (StringUtils.isBlank(postData.getOfferTemplate())) {
				missingParameters.add("offerTemplate");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getSubscriptionDate())) {
				missingParameters.add("subscriptionDate");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(SubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getOfferTemplate())
				&& !StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getSubscriptionDate())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getCode(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getCode());
			}

			if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
				throw new MeveoApiException("Subscription is already RESILIATED.");
			}

			UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount(), provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
			}

			subscription.setUserAccount(userAccount);
			subscription.setOffer(offerTemplate);
			subscription.setDescription(postData.getDescription());
			subscription.setSubscriptionDate(postData.getSubscriptionDate());
			subscription.setTerminationDate(postData.getTerminationDate());

			// populate customFields
			if (postData.getCustomFields() != null) {
				for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) {
					// check if custom field exists has a template
					List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(
							AccountLevelEnum.SUB, provider);
					boolean found = false;
					if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
						for (CustomFieldTemplate cft : customFieldTemplates) {
							if (cf.getCode().equals(cft.getCode())) {
								found = true;
								CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(),
										subscription);
								if (cfi != null) {
									// update
									cfi.setActive(true);
									cfi.setDateValue(cf.getDateValue());
									cfi.setDescription(cf.getDescription());
									cfi.setDoubleValue(cf.getDoubleValue());
									cfi.setLongValue(cf.getLongValue());
									cfi.setStringValue(cf.getStringValue());
									cfi.updateAudit(currentUser);
								} else {
									// create
									CustomFieldInstance cfiNew = new CustomFieldInstance();
									cfiNew.setSubscription(subscription);
									cfiNew.setActive(true);
									cfiNew.setCode(cf.getCode());
									cfiNew.setDateValue(cf.getDateValue());
									cfiNew.setDescription(cf.getDescription());
									cfiNew.setDoubleValue(cf.getDoubleValue());
									cfiNew.setLongValue(cf.getLongValue());
									cfiNew.setProvider(currentUser.getProvider());
									cfiNew.setStringValue(cf.getStringValue());
									cfiNew.updateAudit(currentUser);
									subscription.getCustomFields().put(cfiNew.getCode(), cfiNew);
								}
							}
						}
					} else {
						log.warn("No custom field template defined.");
					}

					if (!found) {
						log.warn("No custom field template with code={}", cf.getCode());
					}
				}
			}
			subscriptionService.update(subscription, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getUserAccount())) {
				missingParameters.add("userAccount");
			}
			if (StringUtils.isBlank(postData.getOfferTemplate())) {
				missingParameters.add("offerTemplate");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getSubscriptionDate())) {
				missingParameters.add("subscriptionDate");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void activateServices(ActivateServicesDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getSubscription())
				&& postData.getServicesToActivateDto().getServices() != null
				&& postData.getServicesToActivateDto().getServices().size() > 0) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
					|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
				throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
			}

			// check if exists
			List<ServiceToActivateDto> serviceToActivateDtos = new ArrayList<>();
			for (ServiceToActivateDto serviceToActivateDto : postData.getServicesToActivateDto().getServices()) {
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToActivateDto.getCode(),
						provider);
				if (serviceTemplate == null) {
					throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToActivateDto.getCode());
				}

				serviceToActivateDto.setServiceTemplate(serviceTemplate);
				serviceToActivateDtos.add(serviceToActivateDto);
			}

			// instantiate
			List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
			for (ServiceToActivateDto serviceToActivateDto : serviceToActivateDtos) {
				ServiceTemplate serviceTemplate = serviceToActivateDto.getServiceTemplate();
				log.debug("instanciateService id={} checked, quantity={}", serviceTemplate.getId(), 1);

				ServiceInstance serviceInstance = new ServiceInstance();
				serviceInstance.setProvider(serviceTemplate.getProvider());
				serviceInstance.setCode(serviceTemplate.getCode());
				serviceInstance.setDescription(serviceTemplate.getDescription());
				serviceInstance.setServiceTemplate(serviceTemplate);
				serviceInstance.setSubscription(subscription);

				if (serviceToActivateDto.getSubscriptionDate() == null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					serviceInstance.setSubscriptionDate(calendar.getTime());
				} else {
					serviceInstance.setSubscriptionDate(serviceToActivateDto.getSubscriptionDate());
				}
				serviceInstance.setQuantity(serviceToActivateDto.getQuantity());
				try {
					serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);
					serviceInstances.add(serviceInstance);
				} catch (BusinessException e) {
					throw new MeveoApiException(e.getMessage());
				}
			}

			// activate services
			for (ServiceInstance serviceInstance : serviceInstances) {
				if (serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
					throw new MeveoApiException(
							new BundleKey("messages", "error.activation.terminatedService").getBundle());
				}

				if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
					throw new MeveoApiException(new BundleKey("messages", "error.activation.activeService").getBundle());
				}

				try {
					serviceInstanceService.serviceActivation(serviceInstance, null, null, currentUser);
				} catch (BusinessException e) {
					throw new MeveoApiException(e.getMessage());
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getSubscription())) {
				missingParameters.add("subscription");
			}
			if (postData.getServicesToActivateDto().getServices() == null
					|| postData.getServicesToActivateDto().getServices().size() == 0) {
				missingParameters.add("services");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void applyOneShotChargeInstance(ApplyOneShotChargeInstanceDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getOneShotCharge()) && !StringUtils.isBlank(postData.getSubscription())
				&& postData.getOperationDate() != null) {
			Provider provider = currentUser.getProvider();

			OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(
					postData.getOneShotCharge(), provider);
			if (oneShotChargeTemplate == null) {
				throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getOneShotCharge());
			}

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
					|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
				throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
			}
			if (postData.getWallet() != null) {
				WalletTemplate walletTemplate = walletTemplateService.findByCode(postData.getWallet(), provider);
				if (walletTemplate == null) {
					throw new EntityDoesNotExistsException(WalletTemplate.class, postData.getWallet());
				}

				if ((!postData.getWallet().equals("PRINCIPAL"))
						&& !subscription.getUserAccount().getPrepaidWallets().containsKey(postData.getWallet())) {
					if (postData.getCreateWallet() != null && postData.getCreateWallet()) {
						subscription.getUserAccount().getWalletInstance(postData.getWallet());
					} else {
						throw new MeveoApiException("Subscription is already RESILIATED.");
					}
				}
			}

			try {
				oneShotChargeInstanceService.oneShotChargeApplication(subscription,
						(OneShotChargeTemplate) oneShotChargeTemplate, postData.getWallet(),
						postData.getOperationDate(), postData.getAmountWithoutTax(), postData.getAmountWithTax(),
						postData.getQuantity(), postData.getCriteria1(), postData.getCriteria2(),
						postData.getCriteria3(), currentUser, true);
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

		} else {
			if (StringUtils.isBlank(postData.getOneShotCharge())) {
				missingParameters.add("oneShotCharge");
			}
			if (StringUtils.isBlank(StringUtils.isBlank(postData.getSubscription()))) {
				missingParameters.add("subscription");
			}
			if (postData.getOperationDate() == null) {
				missingParameters.add("operationDate");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void terminateSubscription(TerminateSubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getSubscriptionCode())
				&& !StringUtils.isBlank(postData.getTerminationReason()) && postData.getTerminationDate() != null) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscriptionCode(),
					currentUser.getProvider());
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode());
			}

			if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
				throw new MeveoApiException("Subscription is already RESILIATED.");
			}

			SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(
					postData.getTerminationReason(), provider);
			if (subscriptionTerminationReason == null) {
				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class,
						postData.getTerminationReason());
			}

			try {
				subscriptionService.terminateSubscription(subscription, postData.getTerminationDate(),
						subscriptionTerminationReason, currentUser);
			} catch (BusinessException e) {
				log.error("subscription termination={}", e.getMessage());
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			if (StringUtils.isBlank(postData.getSubscriptionCode())) {
				missingParameters.add("subscriptionCode");
			}
			if (StringUtils.isBlank(postData.getTerminationReason())) {
				missingParameters.add("terminationReason");
			}
			if (postData.getTerminationDate() == null) {
				missingParameters.add("terminationDate");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void terminateServices(TerminateSubscriptionServicesDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getSubscriptionCode())
				&& (postData.getServices() != null || postData.getServices().size() != 0)
				&& !StringUtils.isBlank(postData.getTerminationReason()) && postData.getTerminationDate() != null) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscriptionCode(),
					currentUser.getProvider());
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode());
			}

			SubscriptionTerminationReason serviceTerminationReason = terminationReasonService.findByCode(
					postData.getTerminationReason(), provider);
			if (serviceTerminationReason == null) {
				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class,
						postData.getTerminationReason());
			}

			for (String serviceInstanceCode : postData.getServices()) {
				ServiceInstance serviceInstance = serviceInstanceService.findByCodeAndSubscription(serviceInstanceCode,
						subscription);
				if (serviceInstance == null) {
					throw new EntityDoesNotExistsException(ServiceInstance.class, serviceInstanceCode);
				}

				if (serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
					log.info("serviceInstance with code={} is already TERMINATED", serviceInstance.getCode());
					continue;
				}

				try {
					serviceInstanceService.terminateService(serviceInstance, postData.getTerminationDate(),
							serviceTerminationReason, currentUser);
				} catch (BusinessException e) {
					log.error("service termination={}", e.getMessage());
					throw new MeveoApiException(e.getMessage());
				}
			}
		} else {
			if (StringUtils.isBlank(postData.getSubscriptionCode())) {
				missingParameters.add("subscriptionCode");
			}
			if (postData.getServices() == null || postData.getServices().size() == 0) {
				missingParameters.add("subscriptionCode");
			}
			if (StringUtils.isBlank(postData.getTerminationReason())) {
				missingParameters.add("terminationReason");
			}
			if (postData.getTerminationDate() == null) {
				missingParameters.add("terminationDate");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public SubscriptionsDto listByUserAccount(String userAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(userAccountCode)) {
			UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
			}

			SubscriptionsDto result = new SubscriptionsDto();
			List<Subscription> subscriptions = subscriptionService.listByUserAccount(userAccount);
			if (subscriptions != null) {
				for (Subscription s : subscriptions) {
					result.getSubscription().add(new SubscriptionDto(s));
				}
			}

			return result;
		} else {
			missingParameters.add("userAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
