package org.meveo.api.account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.ActivateServicesDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
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

	public void create(SubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getOfferTemplate())
				&& !StringUtils.isBlank(postData.getCode())) {
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

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(SubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getOfferTemplate())
				&& !StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getCode(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getCode());
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

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void activateServices(ActivateServicesDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getSubscription()) && postData.getServices() != null
				&& postData.getServices().size() > 0) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			// check if exists
			Map<ServiceTemplate, Integer> serviceTemplates = new HashMap<ServiceTemplate, Integer>();
			for (String serviceTemplateCode : postData.getServices().keySet()) {
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode, provider);
				if (serviceTemplate == null) {
					throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
				}

				serviceTemplates.put(serviceTemplate, postData.getServices().get(serviceTemplateCode));
			}

			// instantiate
			List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
			for (ServiceTemplate serviceTemplate : serviceTemplates.keySet()) {
				log.debug("instanciateService id={} checked, quantity={}", serviceTemplate.getId(), 1);

				ServiceInstance serviceInstance = new ServiceInstance();
				serviceInstance.setProvider(serviceTemplate.getProvider());
				serviceInstance.setCode(serviceTemplate.getCode());
				serviceInstance.setDescription(serviceTemplate.getDescription());
				serviceInstance.setServiceTemplate(serviceTemplate);
				serviceInstance.setSubscription(subscription);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

				serviceInstance.setSubscriptionDate(calendar.getTime());
				Integer quantity = serviceTemplates.get(serviceTemplate);
				serviceInstance.setQuantity(quantity == null ? 0 : quantity);
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
			if (postData.getServices() == null || postData.getServices().size() == 0) {
				missingParameters.add("services");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void applyOneShotChargeInstance(ApplyOneShotChargeInstanceDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getOneShotChargeInstance())
				&& !StringUtils.isBlank(postData.getSubscription()) && postData.getOperationDate() != null) {
			Provider provider = currentUser.getProvider();

			OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(
					postData.getOneShotChargeInstance(), provider);
			if (oneShotChargeTemplate == null) {
				throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getOneShotChargeInstance());
			}

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			OneShotChargeInstance oneShotChargeInstance = oneShotChargeInstanceService.findByCode(
					postData.getOneShotChargeInstance(), provider);
			if (oneShotChargeInstance == null) {
				oneShotChargeInstance = new OneShotChargeInstance();
				oneShotChargeInstance.setChargeTemplate(oneShotChargeTemplate);
				Long id;
				try {
					id = oneShotChargeInstanceService.oneShotChargeApplication(subscription,
							(OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate(),
							oneShotChargeInstance.getChargeDate(), oneShotChargeInstance.getAmountWithoutTax(),
							oneShotChargeInstance.getAmountWithTax(), 1, oneShotChargeInstance.getCriteria1(),
							oneShotChargeInstance.getCriteria2(), oneShotChargeInstance.getCriteria3(),
							oneShotChargeInstance.getSeller(), currentUser);
				} catch (BusinessException e) {
					throw new MeveoApiException(e.getMessage());
				}

				oneShotChargeInstance.setId(id);
				oneShotChargeInstance.setChargeDate(postData.getOperationDate());
				oneShotChargeInstance.setSubscription(subscription);
				oneShotChargeInstance.setSeller(subscription.getUserAccount().getBillingAccount().getCustomerAccount()
						.getCustomer().getSeller());
				oneShotChargeInstance.setCurrency(subscription.getUserAccount().getBillingAccount()
						.getCustomerAccount().getTradingCurrency());
				oneShotChargeInstance.setCountry(subscription.getUserAccount().getBillingAccount().getTradingCountry());
				oneShotChargeInstance.setProvider(oneShotChargeInstance.getChargeTemplate().getProvider());

				oneShotChargeInstance.setDescription(postData.getDescription());
				oneShotChargeInstance.setAmountWithoutTax(postData.getAmountWithoutTax());
				oneShotChargeInstance.setAmountWithTax(postData.getAmountWithTax());
				oneShotChargeInstance.setCriteria1(postData.getCriteria1());
				oneShotChargeInstance.setCriteria2(postData.getCriteria2());
				oneShotChargeInstance.setCriteria3(postData.getCriteria3());
			} else {
				oneShotChargeInstance.setChargeDate(postData.getOperationDate());
			}

			oneShotChargeInstance.setDescription(postData.getDescription());
			oneShotChargeInstance.setAmountWithoutTax(postData.getAmountWithoutTax());
			oneShotChargeInstance.setAmountWithTax(postData.getAmountWithTax());
			oneShotChargeInstance.setCriteria1(postData.getCriteria1());
			oneShotChargeInstance.setCriteria2(postData.getCriteria2());
			oneShotChargeInstance.setCriteria3(postData.getCriteria3());

			oneShotChargeInstanceService.update(oneShotChargeInstance, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getOneShotChargeInstance())) {
				missingParameters.add("oneShotChargeInstance");
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
}
