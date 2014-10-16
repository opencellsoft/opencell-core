/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
@LocalBean
public class ServiceInstanceService extends BusinessService<ServiceInstance> {

	@EJB
	private SubscriptionService subscriptionService;

	@EJB
	private ServiceTemplateService serviceTemplateService;

	@EJB
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@EJB
	private ChargeInstanceService<ChargeInstance> chargeInstanceService;

	@EJB
	private OneShotChargeInstanceService oneShotChargeInstanceService;

	@EJB
	private UsageChargeInstanceService usageChargeInstanceService;

	@EJB
	private WalletOperationService chargeApplicationService;

	@EJB
	private CustomerAccountService customerAccountService;

	@EJB
	private RatedTransactionService ratedTransactionService;

	public ServiceInstance findByCodeAndSubscription(String code,
			Subscription subscription) {
		return findByCodeAndSubscription(getEntityManager(), code, subscription);
	}

	public ServiceInstance findByCodeAndSubscription(EntityManager em,
			String code, Subscription subscription) {
		ServiceInstance chargeInstance = null;
		try {
			log.debug("start of find {} by code (code={}) ..",
					"ServiceInstance", code);
			QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription", "=", subscription, true);
			chargeInstance = (ServiceInstance) qb.getQuery(em)
					.getSingleResult();
			log.debug("end of find {} by code (code={}). Result found={}.",
					new Object[] { "ServiceInstance", code,
							chargeInstance != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubscription : aucun service n'a ete trouve");
		} catch (Exception e) {
			log.error("findByCodeAndSubscription error={} ", e.getMessage());
		}

		return chargeInstance;
	}

	public void serviceInstanciation(ServiceInstance serviceInstance,
			User creator) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		serviceInstanciation(serviceInstance, creator, null, null);
	}

	public void serviceInstanciation(EntityManager em,
			ServiceInstance serviceInstance, User creator)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		serviceInstanciation(em, serviceInstance, creator, null, null);
	}

	public void serviceInstanciation(ServiceInstance serviceInstance,
			User creator, BigDecimal subscriptionAmount,
			BigDecimal terminationAmount)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		serviceInstanciation(getEntityManager(), serviceInstance, creator);
	}

	public void serviceInstanciation(EntityManager em,
			ServiceInstance serviceInstance, User creator,
			BigDecimal subscriptionAmount, BigDecimal terminationAmount)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		log.debug("serviceInstanciation serviceID={}, code={}",
				serviceInstance.getId(), serviceInstance.getCode());

		String serviceCode = serviceInstance.getServiceTemplate().getCode();
		Subscription subscription = serviceInstance.getSubscription();

		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
				|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
			throw new IncorrectSusbcriptionException(
					"subscription is not active");
		}

		ServiceInstance serviceInst = findByCodeAndSubscription(em,
				serviceCode, subscription);
		if (serviceInst != null) {
			throw new IncorrectServiceInstanceException(
					"service instance already created. service Code="
							+ serviceInstance.getCode() + ",subscription Code"
							+ subscription.getCode());
		}

		Seller seller = subscription.getUserAccount().getBillingAccount()
				.getCustomerAccount().getCustomer().getSeller();

		if (serviceInstance.getSubscriptionDate() == null) {
			serviceInstance.setSubscriptionDate(new Date());
		}

		serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
		serviceInstance.setStatusDate(new Date());
		serviceInstance.setCode(serviceCode);
		create(em, serviceInstance, creator, subscription.getProvider());

		ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();

		for (RecurringChargeTemplate recurringChargeTemplate : serviceTemplate
				.getRecurringCharges()) {
			chargeInstanceService.recurringChargeInstanciation(em,
					serviceInstance, recurringChargeTemplate.getCode(),
					serviceInstance.getSubscriptionDate(), seller, creator);
		}

		for (OneShotChargeTemplate subscriptionChargeTemplate : serviceTemplate
				.getSubscriptionCharges()) {
			oneShotChargeInstanceService.oneShotChargeInstanciation(em,
					serviceInstance.getSubscription(), serviceInstance,
					subscriptionChargeTemplate,
					serviceInstance.getSubscriptionDate(), subscriptionAmount,
					null, 1, seller, creator);
		}

		for (OneShotChargeTemplate terminationChargeTemplate : serviceTemplate
				.getTerminationCharges()) {
			oneShotChargeInstanceService.oneShotChargeInstanciation(em,
					serviceInstance.getSubscription(), serviceInstance,
					terminationChargeTemplate,
					serviceInstance.getSubscriptionDate(), terminationAmount,
					null, 1, seller, creator);
		}

		for (ServiceUsageChargeTemplate serviceUsageChargeTemplate : serviceTemplate
				.getServiceUsageCharges()) {
			usageChargeInstanceService.usageChargeInstanciation(em,
					serviceInstance.getSubscription(), serviceInstance,
					serviceUsageChargeTemplate,
					serviceInstance.getSubscriptionDate(), seller, creator);
		}
	}

	public void serviceActivation(ServiceInstance serviceInstance,
			BigDecimal amountWithoutTax, BigDecimal amountWithoutTax2,
			User creator) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		serviceActivation(getEntityManager(), serviceInstance,
				amountWithoutTax, amountWithoutTax2, creator);
	}
	/**
	 * Activate a service, the subscription charges are applied
	 */
	public void serviceActivation(EntityManager em,
			ServiceInstance serviceInstance, BigDecimal amountWithoutTax,
			BigDecimal amountWithoutTax2, User creator)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		serviceActivation(em,serviceInstance, true, amountWithoutTax,
				 amountWithoutTax2,  creator);
	}
	
	/**
	 * Activate a service, the subscription charges can be applied or not
	 */
	public void serviceActivation(EntityManager em,
			ServiceInstance serviceInstance, boolean applySubscriptionCharges,BigDecimal amountWithoutTax,
			BigDecimal amountWithoutTax2, User creator)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		Subscription subscription = serviceInstance.getSubscription();

		// String serviceCode = serviceInstance.getCode();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code="
							+ serviceInstance.getSubscription().getCode());
		}

		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
				|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
			throw new IncorrectServiceInstanceException("subscription is "
					+ subscription.getStatus());
		}

		if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE
				|| serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
			throw new IncorrectServiceInstanceException("serviceInstance is "
					+ subscription.getStatus());
		}

		if (serviceInstance.getSubscriptionDate() == null) {
			serviceInstance.setSubscriptionDate(new Date());
		}

		int agreementMonthTerm = 0;
		// activate recurring charges
		log.debug(
				"serviceActivation:serviceInstance.getRecurrringChargeInstances.size={}",
				serviceInstance.getRecurringChargeInstances().size());
		for (RecurringChargeInstance recurringChargeInstance : serviceInstance
				.getRecurringChargeInstances()) {

			// application of subscription prorata
			recurringChargeInstance.setSubscriptionDate(serviceInstance
					.getSubscriptionDate());
			recurringChargeInstance.setChargeDate(serviceInstance
					.getSubscriptionDate());
			recurringChargeInstance.setSeller(subscription.getUserAccount()
					.getBillingAccount().getCustomerAccount().getCustomer()
					.getSeller());
			recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
			recurringChargeInstance.setStatusDate(new Date());
			recurringChargeInstanceService.update(em, recurringChargeInstance);
			recurringChargeInstanceService.recurringChargeApplication(em,
					recurringChargeInstance, creator);

			if (recurringChargeInstance.getRecurringChargeTemplate()
					.getDurationTermInMonth() != null) {
				if (recurringChargeInstance.getRecurringChargeTemplate()
						.getDurationTermInMonth() > agreementMonthTerm) {
					agreementMonthTerm = recurringChargeInstance
							.getRecurringChargeTemplate()
							.getDurationTermInMonth();
				}
			}

		}

		// set end Agreement Date
		Date serviceEngAgreementDate = null;
		if (agreementMonthTerm > 0) {
			serviceEngAgreementDate = DateUtils.addMonthsToDate(
					subscription.getSubscriptionDate(), agreementMonthTerm);
		}

		if ((serviceEngAgreementDate == null)) {
			serviceInstance.setEndAgrementDate(subscription
					.getEndAgrementDate());
		} else {
			serviceInstance.setEndAgrementDate(serviceEngAgreementDate);
		}

		// apply subscription charges
		if(applySubscriptionCharges){
			log.debug(
					"serviceActivation:serviceInstance.getSubscriptionChargeInstances.size={}",
					serviceInstance.getSubscriptionChargeInstances().size());
			for (OneShotChargeInstance oneShotChargeInstance : serviceInstance
					.getSubscriptionChargeInstances()) {
				 oneShotChargeInstanceService.oneShotChargeApplication(em,
				 subscription, oneShotChargeInstance,
				 serviceInstance.getSubscriptionDate(),
				 serviceInstance.getQuantity(), creator);
				oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
				oneShotChargeInstance.setStatusDate(new Date());
				oneShotChargeInstanceService.update(em, oneShotChargeInstance);
			}
		} else {
			log.debug("serviceActivation: subscription charges are not applied");
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance
				.getUsageChargeInstances()) {
			 usageChargeInstanceService.activateUsageChargeInstance(em,
			 usageChargeInstance);
		}

		serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
		serviceInstance.setStatusDate(new Date());
		update(em, serviceInstance, creator);

	}

	public void terminateService(ServiceInstance serviceInstance,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		terminateService(serviceInstance, terminationDate,
				terminationReason.isApplyAgreement(),
				terminationReason.isApplyReimbursment(),
				terminationReason.isApplyTerminationCharges(), user);
		serviceInstance.setSubscriptionTerminationReason(terminationReason);
		update(serviceInstance, user);
	}

	public void terminateService(ServiceInstance serviceInstance,
			Date terminationDate, boolean applyAgreement,
			boolean applyReimbursment, boolean applyTerminationCharges,
			User user) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		terminateService(getEntityManager(), serviceInstance, terminationDate,
				applyAgreement, applyReimbursment, applyTerminationCharges,
				user);
	}

	public void terminateService(EntityManager em,
			ServiceInstance serviceInstance, Date terminationDate,
			boolean applyAgreement, boolean applyReimbursment,
			boolean applyTerminationCharges, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		if (serviceInstance.getId() != null) {
			log.info(
					"terminateService terminationDate={}, serviceInstanceId={}",
					terminationDate, serviceInstance.getId());
		}
		if (terminationDate == null) {
			terminationDate = new Date();
		}

		String serviceCode = serviceInstance.getCode();
		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"service Instance does not have subscrption . serviceCode="
							+ serviceInstance.getCode());
		}
		if (serviceInstance.getStatus() == InstanceStatusEnum.INACTIVE) {
			throw new IncorrectServiceInstanceException(
					"service instance is inactive. service Code=" + serviceCode
							+ ",subscription Code" + subscription.getCode());
		}

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance
				.getRecurringChargeInstances()) {
			Date chargeDate = recurringChargeInstance.getChargeDate();
			Date nextChargeDate = recurringChargeInstance.getNextChargeDate();
			Date storedNextChargeDate = recurringChargeInstance
					.getNextChargeDate();

			if (recurringChargeInstance.getRecurringChargeTemplate()
					.getApplyInAdvance()) {
				nextChargeDate = recurringChargeInstance.getChargeDate();
			}

			if (applyAgreement) {
				Date endAgrementDate = serviceInstance.getEndAgrementDate();
				if (endAgrementDate != null
						&& terminationDate.before(endAgrementDate)) {
					if (endAgrementDate.after(nextChargeDate)) {
						chargeApplicationService.applyChargeAgreement(em,
								recurringChargeInstance,
								recurringChargeInstance
										.getRecurringChargeTemplate(), user);
					}

				}
			}

			if (applyReimbursment) {
				Date endAgrementDate = recurringChargeInstance
						.getServiceInstance().getEndAgrementDate();
				if (applyAgreement && endAgrementDate != null
						&& terminationDate.before(endAgrementDate)) {
					if (endAgrementDate.before(nextChargeDate)) {
						recurringChargeInstance
								.setTerminationDate(endAgrementDate);
						chargeApplicationService.applyReimbursment(em,
								recurringChargeInstance, user);
					}

				} else if (terminationDate.before(storedNextChargeDate)) {
					recurringChargeInstance.setTerminationDate(terminationDate);
					chargeApplicationService.applyReimbursment(em,
							recurringChargeInstance, user);
				}

			}

			recurringChargeInstance.setChargeDate(chargeDate);
			recurringChargeInstance.setNextChargeDate(storedNextChargeDate);
			recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
			recurringChargeInstance.setStatusDate(new Date());
			chargeInstanceService.update(em, recurringChargeInstance);
		}

		if (applyTerminationCharges) {
			for (OneShotChargeInstance oneShotChargeInstance : serviceInstance
					.getTerminationChargeInstances()) {
				oneShotChargeInstanceService.oneShotChargeApplication(em,
						subscription, oneShotChargeInstance, terminationDate,
						serviceInstance.getQuantity(), user);
			}
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance
				.getUsageChargeInstances()) {
			usageChargeInstanceService.terminateUsageChargeInstance(em,
					usageChargeInstance, terminationDate);
		}

		serviceInstance.setTerminationDate(terminationDate);
		serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
		serviceInstance.setStatusDate(new Date());
		update(em, serviceInstance, user);

		boolean termineSubscription = true;
		for (ServiceInstance srv : subscription.getServiceInstances()) {
			if (srv.getStatus() != InstanceStatusEnum.TERMINATED) {
				termineSubscription = false;
			}
		}
		if (termineSubscription) {
			subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
			subscription.setStatusDate(new Date());
			subscription.setTerminationDate(new Date());
			subscriptionService.update(em, subscription);
		}

		CustomerAccount customerAccount = serviceInstance.getSubscription()
				.getUserAccount().getBillingAccount().getCustomerAccount();
		if (customerAccountService
				.isAllServiceInstancesTerminated(customerAccount)) {
			for (BillingAccount ba : customerAccount.getBillingAccounts()) {
				for (UserAccount ua : ba.getUsersAccounts()) {
					WalletInstance wallet = ua.getWallet();
					for (RatedTransaction rt : wallet.getRatedTransactions()) {
						rt.setDoNotTriggerInvoicing(false);
						ratedTransactionService.update(em, rt);
					}
				}
			}
		}

	}

	public void updateTerminationMode(ServiceInstance serviceInstance,
			Date terminationDate, User user)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {
		log.info(
				"updateTerminationMode terminationDate={},serviceInstanceId={}",
				terminationDate, serviceInstance.getId());

		SubscriptionTerminationReason newReason = serviceInstance
				.getSubscriptionTerminationReason();

		log.info(
				"updateTerminationMode terminationDate={},serviceInstanceId={},newApplyReimbursment=#2,newApplyAgreement=#3,newApplyTerminationCharges=#4",
				terminationDate, serviceInstance.getId(),
				newReason.isApplyReimbursment(), newReason.isApplyAgreement(),
				newReason.isApplyTerminationCharges());

		String serviceCode = serviceInstance.getCode();
		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"service Instance does not have subscrption . serviceCode="
							+ serviceInstance.getCode());
		}

		if (serviceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
			throw new IncorrectServiceInstanceException(
					"service instance is not terminated. service Code="
							+ serviceCode + ",subscription Code"
							+ subscription.getCode());
		}

		/*
		 * for (RecurringChargeInstance recurringChargeInstance :
		 * serviceInstance.getRecurringChargeInstances()) {
		 * 
		 * chargeApplicationService.cancelChargeApplications(recurringChargeInstance
		 * .getId(), ChargeApplicationModeEnum.AGREEMENT, user);
		 * 
		 * chargeApplicationService.cancelChargeApplications(recurringChargeInstance
		 * .getId(), ChargeApplicationModeEnum.REIMBURSMENT, user);
		 * 
		 * } for (OneShotChargeInstance oneShotChargeInstance :
		 * serviceInstance.getTerminationChargeInstances()) {
		 * chargeApplicationService
		 * .cancelOneShotChargeApplications(oneShotChargeInstance,
		 * OneShotChargeTemplateTypeEnum.TERMINATION, user); }
		 */

		terminateService(serviceInstance, terminationDate,
				newReason.isApplyAgreement(), newReason.isApplyReimbursment(),
				newReason.isApplyTerminationCharges(), user);

	}

	public void serviceSuspension(ServiceInstance serviceInstance,
			Date suspensionDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		String serviceCode = serviceInstance.getCode();

		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"service Instance does not have subscrption . serviceCode="
							+ serviceCode);
		}

		if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
			throw new IncorrectServiceInstanceException(
					"service instance is not active. service Code="
							+ serviceCode + ",subscription Code"
							+ subscription.getCode());
		}

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance
				.getRecurringChargeInstances()) {
			if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
				chargeInstanceService.recurringChargeDeactivation(
						recurringChargeInstance.getId(), suspensionDate,
						updater);
			}

		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance
				.getUsageChargeInstances()) {
			usageChargeInstanceService.suspendUsageChargeInstance(
					usageChargeInstance, suspensionDate);
		}

		serviceInstance.setStatus(InstanceStatusEnum.SUSPENDED);
		serviceInstance.setStatusDate(new Date());
		serviceInstance.setTerminationDate(suspensionDate);
		update(serviceInstance, updater);
	}

	public void serviceReactivation(ServiceInstance serviceInstance,
			User updater) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		String serviceCode = serviceInstance.getCode();
		Date subscriptionDate = new Date();

		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"service Instance does not have subscrption . serviceCode="
							+ serviceInstance.getCode());
		}
		ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();
		if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
			throw new IncorrectServiceInstanceException(
					"service instance is already active. service Code="
							+ serviceCode + ",subscription Code"
							+ subscription.getCode());
		}

		serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
		serviceInstance.setStatusDate(new Date());
		serviceInstance.setSubscriptionDate(subscriptionDate);
		serviceInstance.setDescription(serviceTemplate.getDescription());
		serviceInstance.setTerminationDate(null);

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance
				.getRecurringChargeInstances()) {
			if (recurringChargeInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
				chargeInstanceService.recurringChargeReactivation(
						serviceInstance, subscription, subscriptionDate,
						updater);
			}
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance
				.getUsageChargeInstances()) {
			usageChargeInstanceService.reactivateUsageChargeInstance(
					usageChargeInstance, subscriptionDate);
		}
		update(serviceInstance, updater);
	}

	/*
	 * public void cancelService(ServiceInstance serviceInstance, User updater)
	 * throws IncorrectServiceInstanceException, BusinessException {
	 * 
	 * String serviceCode = serviceInstance.getCode(); String subscriptionCode =
	 * serviceInstance.getSubscription().getCode();
	 * 
	 * if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) { throw new
	 * IncorrectServiceInstanceException
	 * ("service instance is not active. service Code=" + serviceCode +
	 * ",subscription Code" + subscriptionCode); } List<ChargeInstance>
	 * chargeInstances = new ArrayList<ChargeInstance>();
	 * chargeInstances.addAll(serviceInstance.getRecurringChargeInstances());
	 * chargeInstances.addAll(serviceInstance.getSubscriptionChargeInstances());
	 * chargeInstances.addAll(serviceInstance.getTerminationChargeInstances());
	 * for (ChargeInstance chargeInstance : chargeInstances) {
	 * chargeInstanceService.chargeInstanceCancellation(chargeInstance.getId(),
	 * updater);
	 * 
	 * for (ChargeApplication chargeApplication :
	 * chargeInstance.getChargeApplications()) { if
	 * (chargeApplication.getStatus() != ApplicationChgStatusEnum.TREATED) {
	 * chargeApplication.setStatus(ApplicationChgStatusEnum.CANCELED);
	 * chargeApplication.setStatusDate(new Date());
	 * 
	 * } for (RatedTransaction ratedTransaction :
	 * chargeApplication.getRatedTransactions()) { if
	 * (ratedTransaction.getBillingRun() == null ||
	 * (ratedTransaction.getBillingRun() != null &&
	 * ratedTransaction.getBillingRun() .getStatus() ==
	 * BillingRunStatusEnum.CANCELED)) {
	 * ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
	 * chargeApplication.setStatus(ApplicationChgStatusEnum.CANCELED);
	 * chargeApplication.setStatusDate(new Date()); } } } }
	 * serviceInstance.setStatus(InstanceStatusEnum.CANCELED);
	 * serviceInstance.setStatusDate(new Date()); update(serviceInstance,
	 * updater); }
	 */

	@SuppressWarnings("deprecation")
	public void serviceTermination(ServiceInstance serviceInstance,
			Date terminationDate, User updater)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		String serviceCode = serviceInstance.getCode();
		Subscription subscription = serviceInstance.getSubscription();
		if (subscription == null) {
			throw new IncorrectSusbcriptionException(
					"service Instance does not have subscrption . serviceCode="
							+ serviceInstance.getCode());
		}
		if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
			throw new IncorrectServiceInstanceException(
					"service instance is not active. service Code="
							+ serviceCode + ",subscription Code"
							+ subscription.getCode());
		}

		for (RecurringChargeInstance recurringChargeInstance : serviceInstance
				.getRecurringChargeInstances()) {
			if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
				chargeInstanceService.recurringChargeDeactivation(
						recurringChargeInstance.getId(), terminationDate,
						updater);
			}
			recurringChargeInstance.setTerminationDate(terminationDate);
			//FIXME : 
			//chargeApplicationService.chargeTermination(recurringChargeInstance,
				//	updater);

		}

		for (OneShotChargeInstance oneShotChargeInstance : serviceInstance
				.getTerminationChargeInstances()) {
			oneShotChargeInstanceService.oneShotChargeApplication(subscription,
					oneShotChargeInstance, terminationDate,
					serviceInstance.getQuantity(), updater);
		}

		for (UsageChargeInstance usageChargeInstance : serviceInstance
				.getUsageChargeInstances()) {
			usageChargeInstanceService.terminateUsageChargeInstance(
					usageChargeInstance, terminationDate);
		}

		serviceInstance.setTerminationDate(terminationDate);
		serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
		serviceInstance.setStatusDate(new Date());
		update(serviceInstance, updater);
	}

	/*
	 * public void serviceCancellation(ServiceInstance serviceInstance, Date
	 * terminationDate, User updater) throws IncorrectSusbcriptionException,
	 * IncorrectServiceInstanceException, BusinessException {
	 * 
	 * String serviceCode = serviceInstance.getCode(); String subscriptionCode =
	 * serviceInstance.getSubscription().getCode(); Subscription subscription =
	 * serviceInstance.getSubscription(); if (subscription == null) { throw new
	 * IncorrectSusbcriptionException
	 * ("service Instance does not have subscrption . serviceCode=" +
	 * serviceInstance.getCode()); } if (serviceInstance.getStatus() !=
	 * InstanceStatusEnum.ACTIVE) { throw new IncorrectServiceInstanceException(
	 * "service instance is not active. service Code=" + serviceCode +
	 * ",subscription Code" + subscriptionCode); } for (RecurringChargeInstance
	 * recurringChargeInstance : serviceInstance.getRecurringChargeInstances())
	 * { if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
	 * chargeInstanceService
	 * .recurringChargeDeactivation(recurringChargeInstance.getId(),
	 * terminationDate, updater); }
	 * recurringChargeInstance.setTerminationDate(terminationDate);
	 * chargeApplicationService.chargeTermination(recurringChargeInstance,
	 * updater); } serviceInstance.setTerminationDate(terminationDate);
	 * serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
	 * serviceInstance.setStatusDate(new Date()); update(serviceInstance,
	 * updater); }
	 */

	@SuppressWarnings("unchecked")
	public List<ServiceInstance> findByServiceTemplate(EntityManager em,
			ServiceTemplate serviceTemplate, Provider provider,
			InstanceStatusEnum status) {
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
	public List<ServiceInstance> findByServiceTemplate(EntityManager em,
			ServiceTemplate serviceTemplate, Provider provider) {
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
