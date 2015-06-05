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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;

@Stateless
public class ServiceInstanceService extends BusinessService<ServiceInstance> {

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private OneShotChargeInstanceService oneShotChargeInstanceService;

	@Inject
	private UsageChargeInstanceService usageChargeInstanceService;

	@Inject
	private WalletOperationService chargeApplicationService;

	public ServiceInstance findByCodeAndSubscription(String code, Subscription subscription) {
		return findByCodeAndSubscription(getEntityManager(), code, subscription);
	}

	public ServiceInstance findByCodeAndSubscription(EntityManager em, String code, Subscription subscription) {
		ServiceInstance chargeInstance = null;
		try {
			log.debug("start of find {} by code (code={}) ..", "ServiceInstance", code);
			QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription", "=", subscription, true);
			chargeInstance = (ServiceInstance) qb.getQuery(em).getSingleResult();
			log.debug("end of find {} by code (code={}). Result found={}.", new Object[] { "ServiceInstance", code,
					chargeInstance != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubscription : no service has been found");
		} catch (Exception e) {
			log.error("findByCodeAndSubscription error={} ", e);
		}

		return chargeInstance;
	}

	@SuppressWarnings("unchecked")
	public List<ServiceInstance> findByCodeSubscriptionAndStatus(String code, Subscription subscription,
			InstanceStatusEnum... statuses) {
		List<ServiceInstance> chargeInstances = null;
		try {
			log.debug("start of find {} by code (code={}) ..", "ServiceInstance", code);
			QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription", "=", subscription, true);
			qb.startOrClause();
			if (statuses != null && statuses.length > 0) {
				for (InstanceStatusEnum status : statuses) {
					qb.addCriterionEnum("c.status", status);
				}
			}
			qb.endOrClause();

			chargeInstances = (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
			log.debug("end of find {} by code (code={}). Result found={}.", new Object[] { "ServiceInstance", code,
					chargeInstances != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubscription : no service has been found");
		} catch (Exception e) {
			log.error("findByCodeAndSubscription error={} ", e);
		}

		return chargeInstances;
	}

	public void serviceInstanciation(ServiceInstance serviceInstance, User creator)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		serviceInstanciation(serviceInstance, creator, null, null);
	}

	public void serviceInstanciation(ServiceInstance serviceInstance, User creator, BigDecimal subscriptionAmount,
			BigDecimal terminationAmount) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
			BusinessException {
		log.debug("serviceInstanciation serviceID={}, code={}", serviceInstance.getId(), serviceInstance.getCode());

		String serviceCode = serviceInstance.getServiceTemplate().getCode();
		Subscription subscription = serviceInstance.getSubscription();

		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
				|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
			throw new IncorrectSusbcriptionException("subscription is not active");
		}

		List<ServiceInstance> serviceInstances = findByCodeSubscriptionAndStatus(serviceCode, subscription,
				InstanceStatusEnum.ACTIVE, InstanceStatusEnum.INACTIVE, InstanceStatusEnum.SUSPENDED);
		if (serviceInstances != null && serviceInstances.size() > 0) {
			throw new IncorrectServiceInstanceException("Service instance with code=" + serviceInstance.getCode()
					+ ", subscription code=" + subscription.getCode()
					+ " and status is [ACTIVE or INACTIVE or SUSPENDED] is already created.");
		}

		UserAccount userAccount = subscription.getUserAccount();

		Seller seller = userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller();

		if (serviceInstance.getSubscriptionDate() == null) {
			serviceInstance.setSubscriptionDate(new Date());
		}

		serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
		serviceInstance.setStatusDate(new Date());
		serviceInstance.setCode(serviceCode);
		serviceInstance.setInvoicingCalendar(serviceInstance.getServiceTemplate().getInvoicingCalendar());
		create(serviceInstance, creator, subscription.getProvider());

		ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();

		for (ServiceChargeTemplate<RecurringChargeTemplate> serviceChargeTemplate : serviceTemplate
				.getServiceRecurringCharges()) {
			RecurringChargeInstance chargeInstance = recurringChargeInstanceService.recurringChargeInstanciation(
					serviceInstance, serviceChargeTemplate.getChargeTemplate(), serviceInstance.getSubscriptionDate(),
					seller, creator);
			serviceInstance.getRecurringChargeInstances().add(chargeInstance);
		}

		for (ServiceChargeTemplate<OneShotChargeTemplate> serviceChargeTemplate : serviceTemplate
				.getServiceSubscriptionCharges()) {
			OneShotChargeInstance chargeInstance = oneShotChargeInstanceService.oneShotChargeInstanciation(
					serviceInstance.getSubscription(), serviceInstance, serviceChargeTemplate.getChargeTemplate(),
					serviceInstance.getSubscriptionDate(), subscriptionAmount, null, 1, creator, true);
			serviceInstance.getSubscriptionChargeInstances().add(chargeInstance);
		}

		for (ServiceChargeTemplate<OneShotChargeTemplate> serviceChargeTemplate : serviceTemplate
				.getServiceTerminationCharges()) {
			OneShotChargeInstance chargeInstance = oneShotChargeInstanceService.oneShotChargeInstanciation(
					serviceInstance.getSubscription(), serviceInstance, serviceChargeTemplate.getChargeTemplate(),
					serviceInstance.getSubscriptionDate(), terminationAmount, null, 1, creator, false);
			serviceInstance.getTerminationChargeInstances().add(chargeInstance);
		}

		for (ServiceChargeTemplateUsage serviceUsageChargeTemplate : serviceTemplate.getServiceUsageCharges()) {
			UsageChargeInstance chargeInstance = usageChargeInstanceService.usageChargeInstanciation(
					serviceInstance.getSubscription(), serviceInstance, serviceUsageChargeTemplate,
					serviceInstance.getSubscriptionDate(), seller, creator);
			serviceInstance.getUsageChargeInstances().add(chargeInstance);
		}

	}

	/**
	 * Activate a service, the subscription charges are applied
	 */
	public void serviceActivation(ServiceInstance serviceInstance, BigDecimal amountWithoutTax,
			BigDecimal amountWithoutTax2, User creator) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		serviceActivation(serviceInstance, true, amountWithoutTax, amountWithoutTax2, creator);
	}

	/**
	 * Activate a service, the subscription charges can be applied or not
	 */
	public void serviceActivation(ServiceInstance serviceInstance, boolean applySubscriptionCharges,
			BigDecimal amountWithoutTax, BigDecimal amountWithoutTax2, User creator)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		Subscription subscription = serviceInstance.getSubscription();

		// String serviceCode = serviceInstance.getCode();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException("Subscription does not exist. code="
					+ serviceInstance.getSubscription().getCode());
		}

		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
				|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
			throw new IncorrectServiceInstanceException("Subscription is " + subscription.getStatus());
		}

		if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
			throw new IncorrectServiceInstanceException("ServiceInstance is " + serviceInstance.getStatus());
		}

		if (serviceInstance.getSubscriptionDate() == null) {
			serviceInstance.setSubscriptionDate(new Date());
		}

		int agreementMonthTerm = 0;
		// activate recurring charges
		log.debug("serviceActivation:serviceInstance.getRecurrringChargeInstances.size={}", serviceInstance
				.getRecurringChargeInstances().size());

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {

			// application of subscription prorata
			recurringChargeInstance.setSubscriptionDate(serviceInstance.getSubscriptionDate());
			recurringChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
			recurringChargeInstance.setSeller(subscription.getUserAccount().getBillingAccount().getCustomerAccount()
					.getCustomer().getSeller());
			recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
			recurringChargeInstance.setStatusDate(new Date());
			recurringChargeInstanceService.update(recurringChargeInstance, creator);
			recurringChargeInstanceService.recurringChargeApplication(recurringChargeInstance, creator);

			if (recurringChargeInstance.getRecurringChargeTemplate().getDurationTermInMonth() != null) {
				if (recurringChargeInstance.getRecurringChargeTemplate().getDurationTermInMonth() > agreementMonthTerm) {
					agreementMonthTerm = recurringChargeInstance.getRecurringChargeTemplate().getDurationTermInMonth();
				}
			}
		}

		// set end Agreement Date
		Date serviceEngAgreementDate = null;
		if (agreementMonthTerm > 0) {
			serviceEngAgreementDate = DateUtils.addMonthsToDate(subscription.getSubscriptionDate(), agreementMonthTerm);
		}

		if ((serviceEngAgreementDate == null)) {
			serviceInstance.setEndAgrementDate(subscription.getEndAgrementDate());
		} else {
			serviceInstance.setEndAgrementDate(serviceEngAgreementDate);
		}

		// apply subscription charges
		if (applySubscriptionCharges) {
			log.debug("serviceActivation:serviceInstance.getSubscriptionChargeInstances.size={}", serviceInstance
					.getSubscriptionChargeInstances().size());
			for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getSubscriptionChargeInstances()) {
				oneShotChargeInstanceService.oneShotChargeApplication(subscription, oneShotChargeInstance,
						serviceInstance.getSubscriptionDate(), serviceInstance.getQuantity(), creator);
				oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
				oneShotChargeInstance.setStatusDate(new Date());
				oneShotChargeInstanceService.update(oneShotChargeInstance, creator);
			}
		} else {
			log.debug("ServiceActivation: subscription charges are not applied.");
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
			usageChargeInstanceService.activateUsageChargeInstance(usageChargeInstance, creator);
		}

		serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
		serviceInstance.setStatusDate(new Date());
		update(serviceInstance, creator);
	}

	public void terminateService(ServiceInstance serviceInstance, Date terminationDate,
			SubscriptionTerminationReason terminationReason, User user) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		terminateService(serviceInstance, terminationDate, terminationReason.isApplyAgreement(),
				terminationReason.isApplyReimbursment(), terminationReason.isApplyTerminationCharges(), user);
		serviceInstance.setSubscriptionTerminationReason(terminationReason);
		update(serviceInstance, user);
	}

	public void terminateService(ServiceInstance serviceInstance, Date terminationDate,
			boolean applyAgreement, boolean applyReimbursment, boolean applyTerminationCharges, User user)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

		if (serviceInstance.getId() != null) {
			log.info("terminateService terminationDate={}, serviceInstanceId={}", terminationDate,
					serviceInstance.getId());
		}
		if (terminationDate == null) {
			terminationDate = new Date();
		}

		String serviceCode = serviceInstance.getCode();
		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
					+ serviceInstance.getCode());
		}
		if (serviceInstance.getStatus() == InstanceStatusEnum.INACTIVE) {
			throw new IncorrectServiceInstanceException("service instance is inactive. service Code=" + serviceCode
					+ ",subscription Code" + subscription.getCode());
		}

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
			Date chargeDate = recurringChargeInstance.getChargeDate();
			Date nextChargeDate = recurringChargeInstance.getNextChargeDate();
			Date storedNextChargeDate = recurringChargeInstance.getNextChargeDate();

			if (recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvance() != null && recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvance()) {
				nextChargeDate = recurringChargeInstance.getChargeDate();
			}

			if (applyAgreement) {
				Date endAgrementDate = serviceInstance.getEndAgrementDate();
				if (endAgrementDate != null && terminationDate.before(endAgrementDate)) {
					if (endAgrementDate.after(nextChargeDate)) {
						chargeApplicationService.applyChargeAgreement(recurringChargeInstance,
								recurringChargeInstance.getRecurringChargeTemplate(), user);
					}

				}
			}

			if (applyReimbursment) {
				Date endAgrementDate = recurringChargeInstance.getServiceInstance().getEndAgrementDate();
				if (applyAgreement && endAgrementDate != null && terminationDate.before(endAgrementDate)) {
					if (endAgrementDate.before(nextChargeDate)) {
						recurringChargeInstance.setTerminationDate(endAgrementDate);
						chargeApplicationService.applyReimbursment(recurringChargeInstance, user);
					}

				} else if (terminationDate.before(storedNextChargeDate)) {
					recurringChargeInstance.setTerminationDate(terminationDate);
					chargeApplicationService.applyReimbursment(recurringChargeInstance, user);
				}

			}

			recurringChargeInstance.setChargeDate(chargeDate);
			recurringChargeInstance.setNextChargeDate(storedNextChargeDate);
			recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
			recurringChargeInstance.setStatusDate(new Date());
			recurringChargeInstanceService.update(recurringChargeInstance);
		}

		if (applyTerminationCharges) {
			for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getTerminationChargeInstances()) {
				if(oneShotChargeInstance.getStatus()==InstanceStatusEnum.INACTIVE){
					log.debug("applying the termination charge {}",oneShotChargeInstance.getCode());
					oneShotChargeInstanceService.oneShotChargeApplication(subscription, oneShotChargeInstance,
							terminationDate, serviceInstance.getQuantity(), user);
					oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
				} else {
					log.debug("we do not apply the termination charge because of its status {}",oneShotChargeInstance.getCode()
							,oneShotChargeInstance.getStatus());
				}
			}
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
			usageChargeInstanceService.terminateUsageChargeInstance(usageChargeInstance, terminationDate);
		}

		serviceInstance.setTerminationDate(terminationDate);
		serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
		serviceInstance.setStatusDate(new Date());
		update(serviceInstance, user);

	}

	public void updateTerminationMode(ServiceInstance serviceInstance, Date terminationDate, User user)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		log.info("updateTerminationMode terminationDate={},serviceInstanceId={}", terminationDate,
				serviceInstance.getId());

		SubscriptionTerminationReason newReason = serviceInstance.getSubscriptionTerminationReason();

		log.info(
				"updateTerminationMode terminationDate={},serviceInstanceId={},newApplyReimbursment=#2,newApplyAgreement=#3,newApplyTerminationCharges=#4",
				terminationDate, serviceInstance.getId(), newReason.isApplyReimbursment(),
				newReason.isApplyAgreement(), newReason.isApplyTerminationCharges());

		String serviceCode = serviceInstance.getCode();
		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
					+ serviceInstance.getCode());
		}

		if (serviceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
			throw new IncorrectServiceInstanceException("service instance is not terminated. service Code="
					+ serviceCode + ",subscription Code" + subscription.getCode());
		}

		terminateService(serviceInstance, terminationDate, newReason.isApplyAgreement(),
				newReason.isApplyReimbursment(), newReason.isApplyTerminationCharges(), user);

	}

	public void serviceSuspension(ServiceInstance serviceInstance, Date suspensionDate, User updater)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

		String serviceCode = serviceInstance.getCode();

		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
					+ serviceCode);
		}

		if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
			throw new IncorrectServiceInstanceException("service instance is not active. service Code=" + serviceCode
					+ ",subscription Code" + subscription.getCode());
		}

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
			if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
				recurringChargeInstanceService.recurringChargeDeactivation(recurringChargeInstance.getId(),
						suspensionDate, updater);
			}

		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
			usageChargeInstanceService.suspendUsageChargeInstance(usageChargeInstance, suspensionDate);
		}

		serviceInstance.setStatus(InstanceStatusEnum.SUSPENDED);
		serviceInstance.setStatusDate(new Date());
		serviceInstance.setTerminationDate(suspensionDate);
		update(serviceInstance, updater);
	}

	public void serviceReactivation(ServiceInstance serviceInstance, User updater)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

		String serviceCode = serviceInstance.getCode();
		Date subscriptionDate = new Date();

		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
					+ serviceInstance.getCode());
		}
		ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();
		if (serviceInstance.getStatus() != InstanceStatusEnum.SUSPENDED) {
			throw new IncorrectServiceInstanceException("service instance is not suspended. service Code="
					+ serviceCode + ",subscription Code" + subscription.getCode());
		}

		serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
		serviceInstance.setStatusDate(new Date());
		serviceInstance.setSubscriptionDate(subscriptionDate);
		serviceInstance.setDescription(serviceTemplate.getDescription());
		serviceInstance.setTerminationDate(null);

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
			if (recurringChargeInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
				recurringChargeInstanceService.recurringChargeReactivation(serviceInstance, subscription,
						subscriptionDate, updater);
			}
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
			if (usageChargeInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
				usageChargeInstanceService.reactivateUsageChargeInstance(usageChargeInstance, subscriptionDate);
			}
		}
		update(serviceInstance, updater);
	}

	@SuppressWarnings("unchecked")
	public List<ServiceInstance> findByServiceTemplate(EntityManager em, ServiceTemplate serviceTemplate,
			Provider provider, InstanceStatusEnum status) {
		QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "i");

		try {
			qb.addCriterionEntity("serviceTemplate", serviceTemplate);
			qb.addCriterionEntity("provider", provider);
			qb.addCriterionEnum("status", status);

			return (List<ServiceInstance>) qb.getQuery(em).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ServiceInstance> findByServiceTemplate(EntityManager em, ServiceTemplate serviceTemplate,
			Provider provider) {
		QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "i");

		try {
			qb.addCriterionEntity("serviceTemplate", serviceTemplate);
			qb.addCriterionEntity("provider", provider);

			return (List<ServiceInstance>) qb.getQuery(em).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
