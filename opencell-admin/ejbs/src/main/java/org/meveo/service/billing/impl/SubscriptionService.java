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
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.InvalidEntityStatusException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.TerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderHistoryService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.primefaces.model.SortOrder;

/**
 * A service class to manage CRUD operations on Subscription entity
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
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

    /**
     * Instantiate subscription validity and renewal dates before creating Subscription and execute a subscription script if offer is tied to BusinessOfferModel
     */
    @MeveoAudit
    @Override
    public void create(Subscription subscription) throws BusinessException {

        subscription.updateSubscribedTillAndRenewalNotifyDates();

        super.create(subscription);

        // execute subscription script
        if (subscription.getOffer().getBusinessOfferModel() != null && subscription.getOffer().getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.subscribe(subscription, subscription.getOffer().getBusinessOfferModel().getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", subscription.getOffer().getBusinessOfferModel().getScript().getCode(), e);
            }
        }
    }

    /**
     * Udpate subscription validity and renewal dates before updating Subscription
     */
    @MeveoAudit
    @Override
    public Subscription update(Subscription subscription) throws BusinessException {

        subscription.updateSubscribedTillAndRenewalNotifyDates();

        return super.update(subscription);
    }

    /**
     * Cancel active, created or suspended Subscription. Status will be changed to Canceled.
     * 
     * @param subscription Subscription
     * @param cancelationDate Cancellation date
     * @return Updated Subscription entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public Subscription cancelSubscription(Subscription subscription, Date cancelationDate)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        if (subscription.getStatus() != SubscriptionStatusEnum.ACTIVE && subscription.getStatus() != SubscriptionStatusEnum.CREATED
                && subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
            return subscription;
        }

        log.debug("Will cancel Subscription " + subscription.getCode());

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

        log.info("Subscription" + subscription.getCode() + " was canceled");

        return subscription;
    }

    /**
     * Suspend active or created Subscription. Status will be changed to Suspended. Action will also suspend the services and access points.
     * 
     * @param subscription Subscription
     * @param cancelationDate Cancellation date
     * @return Updated Subscription entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public Subscription suspendSubscription(Subscription subscription, Date suspensionDate)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        if (subscription.getStatus() != SubscriptionStatusEnum.ACTIVE && subscription.getStatus() != SubscriptionStatusEnum.CREATED) {
            return subscription;
        }

        log.debug("Will suspend Subscription " + subscription.getCode());

        if (suspensionDate == null) {
            suspensionDate = new Date();
        }

        if (subscription.getOffer().getBusinessOfferModel() != null && subscription.getOffer().getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.suspendSubscription(subscription, subscription.getOffer().getBusinessOfferModel().getScript().getCode(), suspensionDate);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", subscription.getOffer().getBusinessOfferModel().getScript().getCode(), e);
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

        log.info("Subscription" + subscription.getCode() + " was suspended");

        return subscription;
    }

    /**
     * Activate previously canceled, terminated or suspended Subscription. Status will be changed to Active. Action will also reactivate previously suspended Services.
     * 
     * @param subscription Subscription
     * @param activationDate Activation date
     * @return Updated Subscription entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public Subscription reactivateSubscription(Subscription subscription, Date reactivationDate)
            throws IncorrectSusbcriptionException, InvalidEntityStatusException, IncorrectServiceInstanceException, BusinessException {

        if (subscription.getStatus() != SubscriptionStatusEnum.RESILIATED && subscription.getStatus() != SubscriptionStatusEnum.CANCELED
                && subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
            throw new InvalidEntityStatusException(Subscription.class, subscription.getCode(), "reactivate", subscription.getStatus(), SubscriptionStatusEnum.RESILIATED,
                SubscriptionStatusEnum.CANCELED, SubscriptionStatusEnum.SUSPENDED);
        }

        log.debug("Will reactivate Subscription " + subscription.getCode());

        if (reactivationDate == null) {
            reactivationDate = new Date();
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

        if (subscription.getOffer().getBusinessOfferModel() != null && subscription.getOffer().getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.reactivateSubscription(subscription, subscription.getOffer().getBusinessOfferModel().getScript().getCode(), reactivationDate);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", subscription.getOffer().getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        log.info("Subscription" + subscription.getCode() + " was reactivated");

        return subscription;
    }

    /**
     * Terminate active, created or suspended Subscription. Status will be changed to Terminated. Action will also terminate the services and access points.
     * 
     * @param subscription Subscription
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param orderNumber Order number that initiated termination
     * @return Updated Subscription entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public Subscription terminateSubscription(Subscription subscription, Date terminationDate, TerminationReason terminationReason, String orderNumber)
            throws BusinessException {
        return terminateSubscription(subscription, terminationDate, terminationReason, orderNumber, null, null);
    }

    /**
     * Terminate active, created or suspended Subscription. Status will be changed to Terminated. Action will also terminate the services and access points.
     * 
     * @param subscription Subscription
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param orderNumber Order number that initiated termination
     * @param orderItemId Order item Id that initiated termination
     * @param orderItemAction Order item action that initiated termination
     * @return Updated Subscription entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public Subscription terminateSubscription(Subscription subscription, Date terminationDate, TerminationReason terminationReason, String orderNumber,
            Long orderItemId, OrderItemActionEnum orderItemAction) throws BusinessException {

        if (terminationReason == null) {
            throw new BusinessException("terminationReason is null");
        }

        return terminateSubscription(subscription, terminationDate, terminationReason, terminationReason.isApplyAgreement(), terminationReason.isApplyReimbursment(),
            terminationReason.isApplyTerminationCharges(), orderNumber, orderItemId, orderItemAction);
    }

    /**
     * Terminate active, created or suspended Subscription. Status will be changed to Terminated. Action will also terminate the services and access points
     * 
     * @param subscription Subscription
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param applyAgreement
     * @param applyReimbursment
     * @param applyTerminationCharges
     * @param orderNumber Order number that initiated termination
     * @param orderItemId Order item Id that initiated termination
     * @param orderItemAction Order item action that initiated termination
     * @return Updated Subscription entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    private Subscription terminateSubscription(Subscription subscription, Date terminationDate, TerminationReason terminationReason, boolean applyAgreement,
            boolean applyReimbursment, boolean applyTerminationCharges, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction) throws BusinessException {

        if (subscription.getStatus() != SubscriptionStatusEnum.ACTIVE && subscription.getStatus() != SubscriptionStatusEnum.CREATED
                && subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
            return subscription;
        }

        log.debug("Will terminate Subscription " + subscription.getCode());

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
        if (subscription.getOffer().getBusinessOfferModel() != null && subscription.getOffer().getBusinessOfferModel().getScript() != null) {
            offerModelScriptService.terminateSubscription(subscription, subscription.getOffer().getBusinessOfferModel().getScript().getCode(), terminationDate, terminationReason);
        }



        log.info("Subscription" + subscription.getCode() + " was terminated");

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
     * 
     * @return A list of subscription ids
     */
    public List<Long> getSubscriptionsToRenewOrNotify() {

        List<Long> ids = getEntityManager().createNamedQuery("Subscription.getExpired", Long.class).setParameter("date", new Date())
            .setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED)).getResultList();
        ids.addAll(getEntityManager().createNamedQuery("Subscription.getToNotifyExpiration", Long.class).setParameter("date", new Date())
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

}