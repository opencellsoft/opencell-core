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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementAlreadyExistsException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.remote.SubscriptionServiceRemote;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless @LocalBean
public class SubscriptionService extends BusinessService<Subscription>
		implements SubscriptionServiceRemote {

	@EJB
	private UserAccountService userAccountService;

	@EJB
	private ServiceInstanceService serviceInstanceService;

	@EJB
	private OfferTemplateService offerTemplateService;

	public void createSubscription(String userAccountCode,
			Subscription subscription, User creator)
			throws ElementAlreadyExistsException, UnknownAccountException {
		UserAccount userAccount = userAccountService
				.findByCode(userAccountCode);
		if (userAccount == null) {
			throw new UnknownAccountException(userAccountCode);
		}
		Subscription existingSubscription = findByCode(subscription.getCode());
		if (existingSubscription != null) {
			throw new ElementAlreadyExistsException(subscription.getCode(),
					"subscription");
		}
		subscription.setUserAccount(userAccount);
		create(subscription, creator, userAccount.getProvider());
	}

	public void updateSubscription(Subscription subscription, User updater) {
		update(subscription, updater);
	}

	public Subscription subscriptionDetails(String subscriptionCode) {
		Subscription subscription = findByCode(subscriptionCode);
		return subscription;
	}

	public List<OneShotChargeInstance> SubscriptionChargeList(
			String subscriptionCode) throws IncorrectSusbcriptionException {
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		return subscription.getOneShotChargeInstances();
	}

	public ServiceInstance SubscriptionServiceDetail(String subscriptionCode,
			String serviceCode) throws IncorrectSusbcriptionException {
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		ServiceInstance serviceInstance = serviceInstanceService
				.findByCodeAndSubscription(serviceCode, subscription.getCode());
		return serviceInstance;
	}

	public void subscriptionTermination(String subscriptionCode,
			Date terminationDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		List<ServiceInstance> serviceInstances = subscription
				.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
				serviceInstanceService.serviceTermination(serviceInstance,
						terminationDate, updater);
			}
		}
		subscription.setTerminationDate(terminationDate);
		subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
		subscription.setStatusDate(new Date());
		update(subscription, updater);
	}

	public void terminateSubscription(String subscriptionCode,
			Date terminationDate, boolean applyAgreement,
			boolean applyReimbursment, boolean applyTerminationCharges,
			User user) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		terminateSubscription(subscriptionCode, terminationDate, null,
				applyAgreement, applyReimbursment, applyTerminationCharges,
				user);
	}

	public void terminateSubscription(String subscriptionCode,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationReason == null) {
			throw new BusinessException("terminationReason is null");
		}
		terminateSubscription(subscriptionCode, terminationDate,
				terminationReason, terminationReason.isApplyAgreement(),
				terminationReason.isApplyReimbursment(),
				terminationReason.isApplyTerminationCharges(), user);

	}

	public void subscriptionCancellation(String subscriptionCode,
			Date terminationDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		/*List<ServiceInstance> serviceInstances = subscription
				.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
				serviceInstanceService.serviceCancellation(serviceInstance,
						terminationDate, updater);
			}
		}*/
		subscription.setTerminationDate(terminationDate);
		subscription.setStatus(SubscriptionStatusEnum.CANCELED);
		subscription.setStatusDate(new Date());
		update(subscription, updater);
	}

	public void subscriptionSuspension(String subscriptionCode,
			Date suspensionDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (suspensionDate == null) {
			suspensionDate = new Date();
		}
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		List<ServiceInstance> serviceInstances = subscription
				.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
				serviceInstanceService.serviceSusupension(serviceInstance,
						suspensionDate, updater);
			}
		}

		subscription.setTerminationDate(suspensionDate);
		subscription.setStatus(SubscriptionStatusEnum.SUSPENDED);
		subscription.setStatusDate(new Date());
		update(subscription, updater);
	}

	public void subscriptionReactivation(String subscriptionCode,
			Date activationDate, User updater)
			throws IncorrectSusbcriptionException,
			ElementNotResiliatedOrCanceledException,
			IncorrectServiceInstanceException, BusinessException {

		if (activationDate == null) {
			activationDate = new Date();
		}

		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		if (subscription.getStatus() != SubscriptionStatusEnum.RESILIATED
				&& subscription.getStatus() != SubscriptionStatusEnum.CANCELED
				&& subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
			throw new ElementNotResiliatedOrCanceledException("subscription",
					subscriptionCode);
		}

		subscription.setTerminationDate(null);
		subscription.setSubscriptionTerminationReason(null);
		subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
		subscription.setStatusDate(activationDate);

		List<ServiceInstance> serviceInstances = subscription
				.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.CANCELED.equals(serviceInstance.getStatus())
					|| InstanceStatusEnum.TERMINATED.equals(serviceInstance
							.getStatus())
					|| InstanceStatusEnum.SUSPENDED.equals(serviceInstance
							.getStatus())) {
				serviceInstanceService.serviceReactivation(serviceInstance,
						updater);
			}
		}

		update(subscription, updater);
	}

	public void subscriptionOffer(String subscriptionCode, String offerCode,
			Date subscriptionDate, User updater)
			throws IncorrectSusbcriptionException, BusinessException {
		if (subscriptionDate == null) {
			subscriptionDate = new Date();
		}
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		if (subscription.getStatus() != SubscriptionStatusEnum.CREATED) {
			throw new BusinessException(
					"subscription has not a created status. code="
							+ subscriptionCode);
		}
		if (subscription.getOffer() != null) {
			throw new BusinessException(
					"subscription has already an offer. code="
							+ subscriptionCode);
		}
		OfferTemplate offer = offerTemplateService.findByCode(offerCode);
		if (offer == null) {
			throw new BusinessException("offer does not exist. code="
					+ offerCode);
		}
		subscription.setOffer(offer);
		subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
		subscription.setStatusDate(new Date());
		subscription.setSubscriptionDate(subscriptionDate);
		update(subscription, updater);
	}

	private void terminateSubscription(String subscriptionCode,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason,
			boolean applyAgreement, boolean applyReimbursment,
			boolean applyTerminationCharges, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		Subscription subscription = findByCode(subscriptionCode);
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + subscriptionCode);
		}
		List<ServiceInstance> serviceInstances = subscription
				.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
				if (terminationReason != null) {
					serviceInstanceService.terminateService(serviceInstance,
							terminationDate, terminationReason, user);
				} else {
					serviceInstanceService.terminateService(serviceInstance,
							terminationDate, applyAgreement, applyReimbursment,
							applyTerminationCharges, user);
				}
			}
		}
		if (terminationReason != null) {
			subscription.setSubscriptionTerminationReason(terminationReason);
		}
		subscription.setTerminationDate(terminationDate);
		subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
		subscription.setStatusDate(new Date());
		update(subscription, user);
	}

	public boolean isDuplicationExist(Subscription subscription) {
		if (subscription == null) {
			return false;
		}
		UserAccount ua = subscription.getUserAccount();
		List<Subscription> subscriptions = ua.getSubscriptions();
		for (Subscription sub : subscriptions) {
			if (sub.getDefaultLevel() != null
					&& sub.getDefaultLevel()
					&& (subscription.getId() == null || (subscription.getId() != null && !subscription
							.getId().equals(sub.getId())))) {
				return true;
			}
		}

		return false;

	}

}