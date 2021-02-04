/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.EndOfTerm;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author HORRI Khalid
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class SubscriptionStatusJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	@EndOfTerm
	protected Event<Subscription> endOfTermEventProducer;

	@Inject
	@EndOfTerm
	protected Event<ServiceInstance> serviceEndOfTermEventProducer;
	
	@Inject
    protected JobExecutionService jobExecutionService;

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateSubscriptionStatus(JobExecutionResultImpl result, Long subscriptionId) throws BusinessException {
		updateSubscriptionStatus(result, subscriptionId, null);
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateSubscriptionStatus(JobExecutionResultImpl result, Long subscriptionId, Date untillDate) throws BusinessException {
		try {
			Subscription subscription = subscriptionService.findById(subscriptionId);
			// Handle subscription renewal or termination
			if (subscription.isSubscriptionExpired() && (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE
					|| subscription.getStatus() == SubscriptionStatusEnum.CREATED)) {

				if (subscription.getSubscriptionRenewal().isAutoRenew()) {
					Date subscribedTillDate = subscription.getSubscribedTillDate();
					while (subscribedTillDate.before(untillDate)) {
						Date calendarDate = new Date();
						Calendar calendar = new GregorianCalendar();
                        if (subscription.getSubscriptionRenewal().getRenewalTermType() == SubscriptionRenewal.RenewalTermTypeEnum.CALENDAR) {
                            org.meveo.model.catalog.Calendar calendarRenew = CalendarService.initializeCalendar(subscription.getSubscriptionRenewal().getCalendarRenewFor(), subscription.getSubscribedTillDate(),
                                subscription);
                            calendarDate = calendarRenew.nextCalendarDate(subscription.getSubscribedTillDate());
						} else {
							calendar.setTime(subscription.getSubscribedTillDate());
							calendar.add(subscription.getSubscriptionRenewal().getRenewForUnit().getCalendarField(), subscription.getSubscriptionRenewal().getRenewFor());
							calendarDate = calendar.getTime();
						}
						subscription.setSubscribedTillDate(calendarDate);
						subscription.setRenewed(true);
						subscription.setRenewalNotifiedDate(null);

						if (subscription.getSubscriptionRenewal().isExtendAgreementPeriodToSubscribedTillDate()) {
							subscription.setEndAgreementDate(subscription.getSubscribedTillDate());
						}

						subscription = subscriptionService.update(subscription);

						log.debug("Subscription {} has beed renewed to date {} with end agreement date of {}", subscription.getCode(), subscription.getSubscribedTillDate(),
								subscription.getEndAgreementDate());
						subscribedTillDate = subscription.getSubscribedTillDate();
					}

				} else if (subscription.getSubscriptionRenewal().getEndOfTermAction() == EndOfTermActionEnum.SUSPEND) {
					subscriptionService.subscriptionSuspension(subscription,
							subscription.getSubscribedTillDate());

				} else if (subscription.getSubscriptionRenewal()
						.getEndOfTermAction() == EndOfTermActionEnum.TERMINATE) {
					subscriptionService.terminateSubscription(subscription,
							subscription.getSubscribedTillDate(),
							subscription.getSubscriptionRenewal().getTerminationReason(), null);
				}

				// Fire "soon to renew" notification
			} else if (subscription.isFireRenewalNotice() && (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE
					|| subscription.getStatus() == SubscriptionStatusEnum.CREATED)) {
				subscription.setRenewalNotifiedDate(new Date());
				subscription = subscriptionService.update(subscription);
				endOfTermEventProducer.fire(subscription);
			}

			jobExecutionService.registerSucces(result);
		} catch (Exception e) {
			log.error("Failed to process status of subscription {} ", subscriptionId, e);
			jobExecutionService.registerError(result, "Failed to process status of subscription " + subscriptionId + ":" + e.getMessage());
		}
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateServiceInstanceStatus(JobExecutionResultImpl result, Long serviceId, Date untillDate) {
		try {
			ServiceInstance serviceInstance = serviceInstanceService.findById(serviceId);
			// Handle subscription renewal or termination
			if (serviceInstance.isSubscriptionExpired() && serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {

				if (serviceInstance.getServiceRenewal().isAutoRenew()) {

					while (serviceInstance.getSubscribedTillDate() != null && serviceInstance.getSubscribedTillDate().before(untillDate)) {
						Date calendarDate = new Date();
						Calendar calendar = new GregorianCalendar();
                        if (serviceInstance.getServiceRenewal().getRenewalTermType() == SubscriptionRenewal.RenewalTermTypeEnum.CALENDAR) {
                            org.meveo.model.catalog.Calendar calendarRenew = CalendarService.initializeCalendar(serviceInstance.getServiceRenewal().getCalendarRenewFor(), serviceInstance.getSubscribedTillDate(),
                                serviceInstance);
                            calendarDate = calendarRenew.nextCalendarDate(serviceInstance.getSubscribedTillDate());
						} else {
							calendar.setTime(serviceInstance.getSubscribedTillDate());
							calendar.add(serviceInstance.getServiceRenewal().getRenewForUnit().getCalendarField(),
									serviceInstance.getServiceRenewal().getRenewFor());
							calendarDate = calendar.getTime();
						}

						serviceInstance.setSubscribedTillDate(calendarDate);
					}

					serviceInstance.setRenewed(true);
					serviceInstance.setRenewalNotifiedDate(null);
					if (serviceInstance.getServiceRenewal().isExtendAgreementPeriodToSubscribedTillDate()) {
						serviceInstance.setEndAgreementDate(serviceInstance.getSubscribedTillDate());
					}
					serviceInstance = serviceInstanceService.update(serviceInstance);

					log.debug("ServiceInstance {} has beed renewed to date {} with end agreement date of {}",
							serviceInstance.getCode(), serviceInstance.getSubscribedTillDate(),
							serviceInstance.getEndAgreementDate());

				} else if (serviceInstance.getServiceRenewal()
						.getEndOfTermAction() == EndOfTermActionEnum.SUSPEND) {
					serviceInstanceService.serviceSuspension(serviceInstance, serviceInstance.getSubscribedTillDate());

				} else if (serviceInstance.getServiceRenewal().getEndOfTermAction() == EndOfTermActionEnum.TERMINATE) {
					serviceInstanceService.terminateService(serviceInstance, serviceInstance.getSubscribedTillDate(),
							serviceInstance.getServiceRenewal().getTerminationReason(), null);
				}

				// Fire "soon to renew" notification
			} else if (serviceInstance.isFireRenewalNotice() && serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
				serviceInstance.setRenewalNotifiedDate(new Date());
				serviceInstance = serviceInstanceService.update(serviceInstance);
				serviceEndOfTermEventProducer.fire(serviceInstance);
			}

			jobExecutionService.registerSucces(result);
		} catch (Exception e) {
			log.error("Failed to process status of serviceInstance with id={}. {}", serviceId, e.getMessage());
			jobExecutionService.registerError(result, "Failed to process status of serviceInstance with id=" + serviceId + ". " + e.getMessage());
		}
	}
}