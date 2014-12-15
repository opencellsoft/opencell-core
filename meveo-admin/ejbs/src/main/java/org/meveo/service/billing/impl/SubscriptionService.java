/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.medina.impl.AccessService;

@Stateless
@LocalBean
public class SubscriptionService extends BusinessService<Subscription> {

	@EJB
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private AccessService accessService;

	public void updateSubscription(Subscription subscription, User updater) {
		update(subscription, updater);
	}

	public void terminateSubscription(Subscription subscription,
			Date terminationDate, boolean applyAgreement,
			boolean applyReimbursment, boolean applyTerminationCharges,
			User user) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		terminateSubscription(subscription, terminationDate, null,
				applyAgreement, applyReimbursment, applyTerminationCharges,
				user);
	}

	public void terminateSubscription(Subscription subscription,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		terminateSubscription(getEntityManager(), subscription,
				terminationDate, terminationReason, user);
	}

	public void terminateSubscription(EntityManager em,
			Subscription subscription, Date terminationDate,
			SubscriptionTerminationReason terminationReason, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationReason == null) {
			throw new BusinessException("terminationReason is null");
		}
		terminateSubscription(subscription, terminationDate, terminationReason,
				terminationReason.isApplyAgreement(),
				terminationReason.isApplyReimbursment(),
				terminationReason.isApplyTerminationCharges(), user);

	}

	public void subscriptionCancellation(Subscription subscription,
			Date terminationDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		/*
		 * List<ServiceInstance> serviceInstances = subscription
		 * .getServiceInstances(); for (ServiceInstance serviceInstance :
		 * serviceInstances) { if
		 * (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
		 * serviceInstanceService.serviceCancellation(serviceInstance,
		 * terminationDate, updater); } }
		 */
		subscription.setTerminationDate(terminationDate);
		subscription.setStatus(SubscriptionStatusEnum.CANCELED);
		subscription.setStatusDate(new Date());
		update(subscription, updater);
	}

	public void subscriptionSuspension(Subscription subscription,
			Date suspensionDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (suspensionDate == null) {
			suspensionDate = new Date();
		}
		List<ServiceInstance> serviceInstances = subscription
				.getServiceInstances();
		for (ServiceInstance serviceInstance : serviceInstances) {
			if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
				serviceInstanceService.serviceSuspension(serviceInstance,
						suspensionDate, updater);
			}
		}

		subscription.setTerminationDate(suspensionDate);
		subscription.setStatus(SubscriptionStatusEnum.SUSPENDED);
		subscription.setStatusDate(new Date());
		update(subscription, updater);
	}

	public void subscriptionReactivation(Subscription subscription,
			Date activationDate, User updater)
			throws IncorrectSusbcriptionException,
			ElementNotResiliatedOrCanceledException,
			IncorrectServiceInstanceException, BusinessException {

		if (activationDate == null) {
			activationDate = new Date();
		}

		if (subscription.getStatus() != SubscriptionStatusEnum.RESILIATED
				&& subscription.getStatus() != SubscriptionStatusEnum.CANCELED
				&& subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
			throw new ElementNotResiliatedOrCanceledException("subscription",
					subscription.getCode());
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
			Date subscriptionDate, User updater, Provider provider)
			throws IncorrectSusbcriptionException, BusinessException {
		if (subscriptionDate == null) {
			subscriptionDate = new Date();
		}
		Subscription subscription = findByCode(subscriptionCode, provider);
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
		OfferTemplate offer = offerTemplateService.findByCode(offerCode,
				provider);
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

	private void terminateSubscription(Subscription subscription,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason,
			boolean applyAgreement, boolean applyReimbursment,
			boolean applyTerminationCharges, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		if (terminationDate == null) {
			terminationDate = new Date();
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
		for (Access access : subscription.getAccessPoints()) {
			access.setEndDate(terminationDate);
			accessService.update(access);
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

	@SuppressWarnings("unchecked")
	public List<Subscription> findByOfferTemplate(EntityManager em,
			OfferTemplate offerTemplate, Provider provider) {
		QueryBuilder qb = new QueryBuilder(Subscription.class, "s");

		try {
			qb.addCriterionEntity("provider", provider);
			qb.addCriterionEntity("offer", offerTemplate);

			return (List<Subscription>) qb.getQuery(em).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}