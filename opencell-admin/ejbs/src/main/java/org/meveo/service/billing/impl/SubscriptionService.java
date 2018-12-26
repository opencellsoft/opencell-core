/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.InitialTermTypeEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderHistoryService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 * @author khalid HORRI
 * @author Mounir BAHIJE
 * @lastModifiedVersion 5.3
 */
@Stateless
public class SubscriptionService extends BusinessService<Subscription> {

    @Inject
    private OfferModelScriptService offerModelScriptService;

    @EJB
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private AccessService accessService;

    @Inject
    private OrderHistoryService orderHistoryService;

    @Inject
    private OfferTemplateService offerTemplateService;
    
    @MeveoAudit
    @Override
    public void create(Subscription subscription) throws BusinessException {

        subscription.updateSubscribedTillAndRenewalNotifyDates();

        subscription.createAutoRenewDate();

        super.create(subscription);

        // execute subscription script
        OfferTemplate offerTemplate = offerTemplateService.retrieveIfNotManaged(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.subscribe(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }
    }

    @MeveoAudit
    @Override
    public Subscription update(Subscription subscription) throws BusinessException {

        subscription.updateSubscribedTillAndRenewalNotifyDates();

        Subscription subscriptionOld = this.findByCode(subscription.getCode());
        subscription.updateAutoRenewDate(subscriptionOld);

        return super.update(subscription);
    }

    @MeveoAudit
    public Subscription subscriptionCancellation(Subscription subscription, Date cancelationDate)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (cancelationDate == null) {
            cancelationDate = new Date();
        }
        /*
         * List<ServiceInstance> serviceInstances = subscription .getServiceInstances(); for (ServiceInstance serviceInstance : serviceInstances) { if
         * (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) { serviceInstanceService.serviceCancellation(serviceInstance, terminationDate); } }
         */
        subscription.setTerminationDate(cancelationDate);
        subscription.setStatus(SubscriptionStatusEnum.CANCELED);
        subscription = update(subscription);

        return subscription;
    }

    @MeveoAudit
    public Subscription subscriptionSuspension(Subscription subscription, Date suspensionDate)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (suspensionDate == null) {
            suspensionDate = new Date();
        }

        OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.suspendSubscription(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode(), suspensionDate);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
                serviceInstanceService.serviceSuspension(serviceInstance, suspensionDate);
            }
        }

        subscription.setTerminationDate(suspensionDate);
        subscription.setStatus(SubscriptionStatusEnum.SUSPENDED);
        subscription = update(subscription);
        for (Access access : subscription.getAccessPoints()) {
            accessService.disable(access);
        }

        return subscription;
    }

    @MeveoAudit
    public Subscription subscriptionReactivation(Subscription subscription, Date reactivationDate)
            throws IncorrectSusbcriptionException, ElementNotResiliatedOrCanceledException, IncorrectServiceInstanceException, BusinessException {

        if (reactivationDate == null) {
            reactivationDate = new Date();
        }

        if (subscription.getStatus() != SubscriptionStatusEnum.RESILIATED && subscription.getStatus() != SubscriptionStatusEnum.CANCELED
                && subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
            throw new ElementNotResiliatedOrCanceledException("subscription", subscription.getCode());
        }

        subscription.setTerminationDate(null);
        subscription.setSubscriptionTerminationReason(null);
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (InstanceStatusEnum.SUSPENDED.equals(serviceInstance.getStatus())) {
                serviceInstanceService.serviceReactivation(serviceInstance, reactivationDate);
            }
        }

        subscription = update(subscription);

        for (Access access : subscription.getAccessPoints()) {
            accessService.enable(access);
        }

        OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.reactivateSubscription(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode(), reactivationDate);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        return subscription;
    }

    @MeveoAudit
    public Subscription terminateSubscription(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber)
            throws BusinessException {
        return terminateSubscription(subscription, terminationDate, terminationReason, orderNumber, null, null);
    }

    @MeveoAudit
    public Subscription terminateSubscription(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber,
            Long orderItemId, OrderItemActionEnum orderItemAction) throws BusinessException {

        if (terminationReason == null) {
            throw new BusinessException("terminationReason is null");
        }
        
		// checks if termination date is > today
		Date endOfDayToday = DateUtils.setDateToEndOfDay(new Date());
		if (terminationDate.before(endOfDayToday)) {
			return terminateSubscription(subscription, terminationDate, terminationReason, terminationReason.isApplyAgreement(), terminationReason.isApplyReimbursment(),
					terminationReason.isApplyTerminationCharges(), orderNumber, orderItemId, orderItemAction);
		} else {
			// if future date set subscription termination
			return terminateSubscriptionWithFutureDate(subscription, terminationDate, terminationReason);
		}
    }

    private Subscription terminateSubscriptionWithFutureDate(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason) throws BusinessException {
    	subscription.setSubscribedTillDate(terminationDate);
		subscription.getSubscriptionRenewal().setTerminationReason(terminationReason);
		subscription.getSubscriptionRenewal().setInitialTermType(InitialTermTypeEnum.FIXED);
		subscription.getSubscriptionRenewal().setAutoRenew(false);		
		subscription.getSubscriptionRenewal().setEndOfTermAction(EndOfTermActionEnum.TERMINATE);
		
		subscription = update(subscription);
		
		return subscription;
	}

	@MeveoAudit
    private Subscription terminateSubscription(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason, boolean applyAgreement,
            boolean applyReimbursment, boolean applyTerminationCharges, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction) throws BusinessException {
        if (terminationDate == null) {
            terminationDate = new Date();
        }

        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus()) || InstanceStatusEnum.SUSPENDED.equals(serviceInstance.getStatus())) {
                if (terminationReason != null) {
                    serviceInstanceService.terminateService(serviceInstance, terminationDate, terminationReason, orderNumber);
                } else {
                    serviceInstanceService.terminateService(serviceInstance, terminationDate, applyAgreement, applyReimbursment, applyTerminationCharges, orderNumber, null);
                }

                orderHistoryService.create(orderNumber, orderItemId, serviceInstance, orderItemAction);
            }
        }

        if (terminationReason != null) {
            subscription.setSubscriptionTerminationReason(terminationReason);
        }
        subscription.setTerminationDate(terminationDate);
        subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
        subscription = update(subscription);

        for (Access access : subscription.getAccessPoints()) {
            access.setEndDate(terminationDate);
            accessService.update(access);
        }

        // execute termination script
        OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            offerModelScriptService.terminateSubscription(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode(), terminationDate, terminationReason);
        }

        return subscription;
    }

    public boolean hasSubscriptions(OfferTemplate offerTemplate) {
        try {
            QueryBuilder qb = new QueryBuilder(Subscription.class, "s");
            qb.addCriterionEntity("offer", offerTemplate);

            return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult()).longValue() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    public List<Subscription> listByUserAccount(UserAccount userAccount) {
        return listByUserAccount(userAccount, "code", SortOrder.ASCENDING);
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> listByUserAccount(UserAccount userAccount, String sortBy, SortOrder sortOrder) {
        QueryBuilder qb = new QueryBuilder(Subscription.class, "c");
        qb.addCriterionEntity("userAccount", userAccount);
        boolean ascending = true;
        if (sortOrder != null) {
            ascending = sortOrder.equals(SortOrder.ASCENDING);
        }
        qb.addOrderCriterion(sortBy, ascending);

        try {
            return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list subscription by user account", e);
            return null;
        }
    }

    /**
     * Get a list of subscription ids that are about to expire or have expired already
     * @return A list of subscription ids
     */
    public List<Long> getSubscriptionsToRenewOrNotify() {

        return getSubscriptionsToRenewOrNotify(new Date());
    }

    /**
     * Get a list of subscription ids that are about to expire or have expired already
     * @param untillDate the subscription till date
     * @return A list of subscription ids
     */
    public List<Long> getSubscriptionsToRenewOrNotify(Date untillDate) {

        List<Long> ids = getEntityManager().createNamedQuery("Subscription.getExpired", Long.class).setParameter("date", untillDate)
            .setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED)).getResultList();
        ids.addAll(getEntityManager().createNamedQuery("Subscription.getToNotifyExpiration", Long.class).setParameter("date", untillDate)
            .setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED)).getResultList());

        return ids;
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInstance> listBySubscription(Subscription subscription) {
        QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
        qb.addCriterionEntity("subscription", subscription);

        try {
            return (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting user account list by billing account", e);
            return null;
        }
    }
    
	public void activateInstantiatedService(Subscription sub) throws BusinessException {
		for (ServiceInstance si : sub.getServiceInstances()) {
			if (si.getStatus().equals(InstanceStatusEnum.INACTIVE)) {
				serviceInstanceService.serviceActivation(si, null, null);
			}
		}
	}
 
    /**
     * Return all subscriptions with status not equal to CREATED or ACTIVE and now - initialAgreement date &gt; n years.
     * @param nYear age of the subscription
     * @return Filtered list of subscriptions
     */
    @SuppressWarnings("unchecked")
	public List<Subscription> listInactiveSubscriptions(int nYear) {
    	QueryBuilder qb = new QueryBuilder(Subscription.class, "e");
    	Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);
    	
    	qb.addCriterionDateRangeToTruncatedToDay("subscriptionDate", higherBound);
    	qb.addCriterionEnum("status", SubscriptionStatusEnum.CREATED, "<>");
    	qb.addCriterionEnum("status", SubscriptionStatusEnum.ACTIVE, "<>");
    	
    	return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
    }

	public void bulkDelete(List<Subscription> inactiveSubscriptions) throws BusinessException {
		for (Subscription e : inactiveSubscriptions) {
			remove(e);
		}
	}

	public void cancelSubscriptionRenewal(Subscription entity) throws BusinessException {
		entity.setSubscribedTillDate(null);
		entity.setSubscriptionTerminationReason(null);
		entity.getSubscriptionRenewal().setInitialyActiveFor(null);
		entity.setSubscriptionRenewal(new SubscriptionRenewal());
	}

}