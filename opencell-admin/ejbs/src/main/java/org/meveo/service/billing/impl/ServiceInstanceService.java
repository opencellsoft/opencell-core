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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.RatingResult;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditableFieldNameEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Renewal;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.TerminationChargeInstance;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.ChargeTemplateStatusEnum;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ServiceCharge;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.PriceVersionDateSettingEnum;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.order.OrderHistoryService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;
import org.meveo.service.script.service.ServiceModelScriptService;

/**
 * ServiceInstanceService.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @author akadid abdelmounaim
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ServiceInstanceService extends BusinessService<ServiceInstance> {
    /**
     * ServiceModelScriptService
     */
    @Inject
    private ServiceModelScriptService serviceModelScriptService;

    /**
     * RecurringChargeInstanceService
     */
    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    /**
     * OneShotChargeInstanceService
     */
    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;
    
    @Inject
    private OneShotRatingService oneShotRatingService;

    /**
     * UsageChargeInstanceService
     */
    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    /**
     * ServiceTemplateService
     */
    @Inject
    ServiceTemplateService serviceTemplateService;

    /**
     * PaymentScheduleInstanceService
     */
    @Inject
    private PaymentScheduleInstanceService paymentScheduleInstanceService;

    /**
     * PaymentScheduleTemplateService
     */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    ParamBean paramBean = ParamBean.getInstance();

    @Inject
    private OrderHistoryService orderHistoryService;

    @Inject
    private AuditableFieldService auditableFieldService;
    
    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private DiscountPlanService discountPlanService;
    /**
     * Find a service instance list by subscription entity, service template code and service instance status list.
     * 
     * @param code the service template code
     * @param subscription the subscription entity
     * @param statuses service instance statuses
     * @return the ServiceInstance list found
     */
    @SuppressWarnings("unchecked")
    public List<ServiceInstance> findByCodeSubscriptionAndStatus(String code, Subscription subscription, InstanceStatusEnum... statuses) {
        List<ServiceInstance> serviceInstances = null;
        try {
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

            serviceInstances = (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by code and subscription/status (code={}). Result found={}.", "ServiceInstance", code, serviceInstances != null && !serviceInstances.isEmpty());

        } catch (Exception e) {
            log.error("Failed to find ServiceInstance by code and subscription/status (code={}) ..", code);
        }

        return serviceInstances;
    }

    /**
     * Find a service instance list by subscription entity, service template code and service instance status list.
     * 
     * @param code the service template code
     * @param subscriptionCode the subscription entity
     * @param statuses service instance statuses
     * @return the ServiceInstance list found
     */
    @SuppressWarnings("unchecked")
    public List<ServiceInstance> findByCodeSubscriptionAndStatus(String code, String subscriptionCode, InstanceStatusEnum... statuses) {
        List<ServiceInstance> serviceInstances = null;
        try {
            QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.subscription.code", "=", subscriptionCode, true);
            qb.startOrClause();
            if (statuses != null && statuses.length > 0) {
                for (InstanceStatusEnum status : statuses) {
                    qb.addCriterionEnum("c.status", status);
                }
            }
            qb.endOrClause();

            serviceInstances = (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by code and subscription/status (code={}). Result found={}.", "ServiceInstance", code, serviceInstances != null && !serviceInstances.isEmpty());

        } catch (Exception e) {
            log.error("Failed find ServiceInstance by code and subscription/status (code={}) ..", code);
        }

        return serviceInstances;
    }

    /**
     * Instantiate a service
     * 
     * @param serviceInstance service instance to instantiate
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     */
    public void serviceInstanciation(ServiceInstance serviceInstance) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        serviceInstanciation(serviceInstance, null, null, false);
    }

    /**
     * 
     * @param serviceInstance service instance to instantiate
     * @param descriptionOverride overridden description
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     */
    public void serviceInstanciation(ServiceInstance serviceInstance, String descriptionOverride) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (serviceInstance.getServiceTemplate() != null)
            serviceInstanciation(serviceInstance, descriptionOverride, null, null, false);
        else
            cpqServiceInstanciation(serviceInstance, serviceInstance.getProductVersion().getProduct(), null, null, false);
    }

    // validate service is in offer service list
    private boolean checkServiceAssociatedWithOffer(ServiceInstance serviceInstance) throws BusinessException {
        OfferTemplate offer = serviceInstance.getSubscription().getOffer();
        if (offer != null && !offer.containsServiceTemplate(serviceInstance.getServiceTemplate()) && !offer.haveProduct(serviceInstance.getCode())) {
            throw new ValidationException("Service " + serviceInstance.getCode() + " is not associated with Offer");
        }

        if (offer != null && serviceInstance != null) {
            log.debug("check service {} is associated with offer {}", serviceInstance.getCode(), offer.getCode());

        }
        return true;
    }

    private boolean checkProductAssociatedWithOffer(ServiceInstance serviceInstance) {

        OfferTemplate offer = serviceInstance.getSubscription().getOffer();
        if (!offer.haveProduct(serviceInstance.getCode())) {
            throw new ValidationException("Service " + serviceInstance.getCode() + " is not associated with Offer");
        }

        return true;
    }

    /**
     * @param serviceInstance service instance
     * @param subscriptionAmount subscription amount
     * @param terminationAmount termination amount
     * @param isVirtual true/false
     * @throws BusinessException business exception
     */
    public void serviceInstanciation(ServiceInstance serviceInstance, BigDecimal subscriptionAmount, BigDecimal terminationAmount, boolean isVirtual) throws BusinessException {
        serviceInstanciation(serviceInstance, null, subscriptionAmount, terminationAmount, isVirtual);
    }

    public void cpqServiceInstanciation(ServiceInstance serviceInstance, Product product, BigDecimal subscriptionAmount, BigDecimal terminationAmount, boolean isVirtual) throws BusinessException {
        productServiceInstanciation(serviceInstance, product, subscriptionAmount, terminationAmount, isVirtual);
    }

    /**
     * v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service
     * 
     * @param serviceInstance service instance
     * @param descriptionOverride overridden description
     * @param subscriptionAmount subscription amount
     * @param terminationAmount termination amount
     * @param isVirtual true/false
     * @throws BusinessException business exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public void serviceInstanciation(ServiceInstance serviceInstance, String descriptionOverride, BigDecimal subscriptionAmount, BigDecimal terminationAmount, boolean isVirtual) throws BusinessException {

        log.debug("Will instantiate service {} for subscription {} quantity {}", serviceInstance.getCode(), serviceInstance.getSubscription().getCode(), serviceInstance.getQuantity());

        Subscription subscription = serviceInstance.getSubscription();
        ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new IncorrectSusbcriptionException("Subscription is not active");
        }
        if (!isVirtual) {
            if (paramBean.isServiceMultiInstantiation()) {
                List<ServiceInstance> serviceInstances = findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription, InstanceStatusEnum.INACTIVE);
                if (serviceInstances != null && !serviceInstances.isEmpty()) {
                    throw new IncorrectServiceInstanceException("Service instance with code=" + serviceInstance.getCode() + ", subscription code=" + subscription.getCode() + " is already instantiated.");
                }
            } else {
                List<ServiceInstance> serviceInstances = findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription, InstanceStatusEnum.INACTIVE, InstanceStatusEnum.ACTIVE);
                if (serviceInstances != null && !serviceInstances.isEmpty()) {
                    throw new IncorrectServiceInstanceException("Service instance with code=" + serviceInstance.getCode() + " and subscription code=" + subscription.getCode() + " is already instantiated or activated.");
                }
            }
        }
        checkServiceAssociatedWithOffer(serviceInstance);

        if (serviceInstance.getSubscriptionDate() == null) {
            serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate() != null ? subscription.getSubscriptionDate() : new Date());
        }
        serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
        if (serviceInstance.getCode() == null) {
            serviceInstance.setCode(serviceTemplate.getCode());
        }
        if (!StringUtils.isBlank(descriptionOverride)) {
            serviceInstance.setDescription(descriptionOverride);
        } else {
            serviceInstance.setDescription(serviceTemplate.getDescription());
        }
        serviceInstance.setInvoicingCalendar(serviceTemplate.getInvoicingCalendar());

        if (serviceInstance.getServiceRenewal() == null) {
            SubscriptionRenewal serviceRenewal = serviceTemplate.getServiceRenewal();
            serviceInstance.setServiceRenewal(serviceRenewal);
        }
        // serviceInstance.setMinimumAmountEl(serviceTemplate.getMinimumAmountEl());
        // serviceInstance.setMinimumLabelEl(serviceTemplate.getMinimumLabelEl());
        // serviceInstance.setMinimumInvoiceSubCategory(serviceTemplate.getMinimumInvoiceSubCategory());

        if (!isVirtual) {
            create(serviceInstance);
        } else {
            serviceInstance.updateSubscribedTillAndRenewalNotifyDates();
        }

        subscription.getServiceInstances().add(serviceInstance);

        instanciateCharges(serviceInstance, serviceTemplate, subscriptionAmount, terminationAmount, isVirtual);

        if (!isVirtual) {
            // execute instantiation script
            if (serviceTemplate.getBusinessServiceModel() != null && serviceTemplate.getBusinessServiceModel().getScript() != null) {
                serviceModelScriptService.instantiateServiceInstance(serviceInstance, serviceTemplate.getBusinessServiceModel().getScript().getCode());
            }
        }

        if (serviceInstance.getOrderItemId() != null && serviceInstance.getOrderItemAction() != null) {
            orderHistoryService.create(serviceInstance.getOrderNumber(), serviceInstance.getOrderItemId(), serviceInstance, serviceInstance.getOrderItemAction());
        }
    }

    public void productServiceInstanciation(ServiceInstance serviceInstance, Product product, BigDecimal subscriptionAmount, BigDecimal terminationAmount, boolean isVirtual) throws BusinessException {

        log.debug("Will instantiate service {} for subscription {} quantity {}", serviceInstance.getCode(), serviceInstance.getSubscription().getCode(), serviceInstance.getQuantity());

        Subscription subscription = serviceInstance.getSubscription();

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new IncorrectSusbcriptionException("Subscription is not active");
        }
        if (!isVirtual) {
        	// Fix INTRD-8842 : Allow multi instance of the same product for a subscription
            if (!paramBean.isServiceMultiInstantiation()) {
                List<ServiceInstance> serviceInstances = findByCodeSubscriptionAndStatus(product.getCode(), subscription, InstanceStatusEnum.INACTIVE, InstanceStatusEnum.ACTIVE);
                if (serviceInstances != null && !serviceInstances.isEmpty()) {
                    throw new IncorrectServiceInstanceException("Mutli-instantiation is not active on your environment. Please contact your system administrator.");
                }
            }
        }
        checkProductAssociatedWithOffer(serviceInstance);

        if (serviceInstance.getSubscriptionDate() == null) {
            serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate() != null ? subscription.getSubscriptionDate() : new Date());
        }
        serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
        if (serviceInstance.getCode() == null) {
            serviceInstance.setCode(product.getCode());
        }

        serviceInstance.setDescription(product.getDescription());
        serviceInstance.setPriceVersionDateSetting(product.getPriceVersionDateSetting());
        
		if(PriceVersionDateSettingEnum.DELIVERY.equals(serviceInstance.getPriceVersionDateSetting())) {
			serviceInstance.setPriceVersionDate(serviceInstance.getSubscriptionDate()); 
		}else if(PriceVersionDateSettingEnum.RENEWAL.equals(serviceInstance.getPriceVersionDateSetting())) {
			serviceInstance.setPriceVersionDate((serviceInstance.getRenewalNotifiedDate() != null && serviceInstance.isRenewed())?serviceInstance.getRenewalNotifiedDate():serviceInstance.getSubscriptionDate()); 
		}else if(PriceVersionDateSettingEnum.QUOTE.equals(serviceInstance.getPriceVersionDateSetting())) {
			if(serviceInstance.getPriceVersionDate() == null) {
				serviceInstance.setPriceVersionDate(serviceInstance.getSubscriptionDate()); 
			}
		}else if(PriceVersionDateSettingEnum.EVENT.equals(serviceInstance.getPriceVersionDateSetting())) {
			serviceInstance.setPriceVersionDate(null); 
		}

        if (!isVirtual) {
            create(serviceInstance);
            getEntityManager().flush();
        } else {
            serviceInstance.updateSubscribedTillAndRenewalNotifyDates();
        }

        subscription.getServiceInstances().add(serviceInstance);

        instanciateCharges(serviceInstance, product, subscriptionAmount, terminationAmount, isVirtual);

    }

    private void instanciateCharges(ServiceInstance serviceInstance, ServiceCharge serviceCharge, BigDecimal subscriptionAmount, BigDecimal terminationAmount, boolean isVirtual) {
        final List<ServiceChargeTemplateRecurring> recurringServices = CollectionUtils.isEmpty(serviceCharge.getServiceRecurringCharges())? new ArrayList<>() : serviceCharge.getServiceRecurringCharges().stream().filter(service->ChargeTemplateStatusEnum.ACTIVE.equals(service.getChargeTemplate().getStatus())).collect(Collectors.toList());
		for (ServiceChargeTemplateRecurring serviceChargeTemplateRecurring : recurringServices) { //
            RecurringChargeInstance chargeInstance = recurringChargeInstanceService.recurringChargeInstanciation(serviceInstance, serviceCharge, serviceChargeTemplateRecurring, isVirtual);
            serviceInstance.getRecurringChargeInstances().add(chargeInstance);
        }
		
        final List<ServiceChargeTemplateSubscription> subscriptionServices = CollectionUtils.isEmpty(serviceCharge.getServiceSubscriptionCharges())? new ArrayList<>() : serviceCharge.getServiceSubscriptionCharges().stream().filter(service->ChargeTemplateStatusEnum.ACTIVE.equals(service.getChargeTemplate().getStatus())).collect(Collectors.toList());
        for (ServiceChargeTemplateSubscription serviceChargeTemplate : subscriptionServices) {
            SubscriptionChargeInstance chargeInstance = (SubscriptionChargeInstance) oneShotChargeInstanceService.oneShotChargeInstanciation(serviceInstance, serviceCharge, serviceChargeTemplate, subscriptionAmount,
                null, true, isVirtual || (serviceChargeTemplate.getChargeTemplate().getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER));
            serviceInstance.getSubscriptionChargeInstances().add(chargeInstance);
        }
        
        final List<ServiceChargeTemplateTermination> services = CollectionUtils.isEmpty(serviceCharge.getServiceTerminationCharges())? new ArrayList<>() : serviceCharge.getServiceTerminationCharges().stream().filter(service->ChargeTemplateStatusEnum.ACTIVE.equals(service.getChargeTemplate().getStatus())).collect(Collectors.toList());
        for (ServiceChargeTemplateTermination serviceChargeTemplate : services) {
            TerminationChargeInstance chargeInstance = (TerminationChargeInstance) oneShotChargeInstanceService.oneShotChargeInstanciation(serviceInstance, serviceCharge, serviceChargeTemplate, terminationAmount, null,
                false, isVirtual);
            serviceInstance.getTerminationChargeInstances().add(chargeInstance);
        }

        final List<ServiceChargeTemplateUsage> usageServices = CollectionUtils.isEmpty(serviceCharge.getServiceUsageCharges())? new ArrayList<>() : serviceCharge.getServiceUsageCharges().stream().filter(service->ChargeTemplateStatusEnum.ACTIVE.equals(service.getChargeTemplate().getStatus())).collect(Collectors.toList());
        for (ServiceChargeTemplateUsage serviceUsageChargeTemplate : usageServices) {
            UsageChargeInstance chargeInstance = usageChargeInstanceService.usageChargeInstanciation(serviceInstance, serviceUsageChargeTemplate, isVirtual);
            serviceInstance.getUsageChargeInstances().add(chargeInstance);
        }

        if (serviceInstance.getOrderItemId() != null && serviceInstance.getOrderItemAction() != null) {
            orderHistoryService.create(serviceInstance.getOrderNumber(), serviceInstance.getOrderItemId(), serviceInstance, serviceInstance.getOrderItemAction());
        }
    }

    /**
     * Activate a service, the subscription and recurring charges are applied.
     *
     * @param serviceInstance service instance
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     */
    public RatingResult serviceActivation(ServiceInstance serviceInstance) throws BusinessException {
        return serviceActivation(serviceInstance, true, true);
    }

    /**
     * Activate a service with an option of applying/rating subscription charges and/or recurring charges. v5.0 admin parameter to authorize/bare the multiactivation of an instantiated service v5.0 add control over WO
     * creation if service suspended and reactivated
     * 
     * @param serviceInstance service instance
     * @param applySubscriptionCharges Shall subscription charges be applied/rated
     * @param applyRecurringCharges Shall recurring charges be applied/rated
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public RatingResult serviceActivation(ServiceInstance serviceInstance, boolean applySubscriptionCharges, boolean applyRecurringCharges)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        Subscription subscription = serviceInstance.getSubscription();
        List<DiscountPlanItem> eligibleFixedDiscountItems = new ArrayList<DiscountPlanItem>();

        log.debug("Will activate service {} for subscription {} quantity {}", serviceInstance.getCode(), serviceInstance.getSubscription().getCode(), serviceInstance.getQuantity());

        // String serviceCode = serviceInstance.getCode();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("Subscription does not exist. code=" + serviceInstance.getSubscription().getCode());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new IncorrectServiceInstanceException("Subscription is " + subscription.getStatus());
        }

        if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("Can not activate a ServiceInstance that is " + serviceInstance.getStatus());
        }

        if (!paramBean.isServiceMultiInstantiation()) {
            List<ServiceInstance> serviceInstances = findByCodeSubscriptionAndStatus(serviceInstance.getCode(), subscription, InstanceStatusEnum.ACTIVE);
            if (serviceInstances != null && !serviceInstances.isEmpty()) {
                throw new IncorrectServiceInstanceException("Service instance with code=" + serviceInstance.getCode() + ", subscription code=" + subscription.getCode() + " is already activated.");
            }
        }

        checkServiceAssociatedWithOffer(serviceInstance);
        
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        if (serviceInstance.getSubscriptionDate() == null) {
            serviceInstance.setSubscriptionDate(new Date());
        }

        if (serviceInstance.getEndAgreementDate() == null) {
            serviceInstance.setEndAgreementDate(subscription.getEndAgreementDate());
        }

    	RatingResult ratingResult = new RatingResult();
        // apply subscription charges
        if (!serviceInstance.getStatus().equals(InstanceStatusEnum.SUSPENDED)) {
            for (SubscriptionChargeInstance oneShotChargeInstance : serviceInstance.getSubscriptionChargeInstances()) {
                oneShotChargeInstance.setQuantity(serviceInstance.getQuantity());
                oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());

                if (applySubscriptionCharges) {

                    ratingResult = oneShotRatingService.rateOneShotCharge(oneShotChargeInstance, oneShotChargeInstance.getQuantity(), null, serviceInstance.getSubscriptionDate(),
                        serviceInstance.getOrderNumber(), ChargeApplicationModeEnum.SUBSCRIPTION, false, false);
                    if(ratingResult != null) {
                    	eligibleFixedDiscountItems.addAll(ratingResult.getEligibleFixedDiscountItems());
                    }

                    // Uncomment if want to rate with failSilently=true and there is a job that that will rate failed to rate one shot charges afterwards
//                    // If failed to rate, charge instance status is left as active
//                    if (ratingResult.getRatingException() != null) {
//                        oneShotChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
//                    }

                } else {
                    log.debug("ServiceActivation: subscription charges were not applied/rated.");
                }

                oneShotChargeInstanceService.update(oneShotChargeInstance);
                instanciateCounterPeriods(oneShotChargeInstance);
            }
        }

        // activate recurring charges
        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {

            // application of subscription prorata
            recurringChargeInstance.setSubscriptionDate(serviceInstance.getSubscriptionDate());
            recurringChargeInstance.setQuantity(serviceInstance.getQuantity());
            recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            recurringChargeInstance = recurringChargeInstanceService.update(recurringChargeInstance);

            try {
                if (applyRecurringCharges) {
                	RatingResult ratingResultRec = recurringChargeInstanceService.applyRecurringCharge(recurringChargeInstance, serviceInstance.getRateUntilDate() == null ? new Date() : serviceInstance.getRateUntilDate(),
                            serviceInstance.getRateUntilDate() == null, false, null);
                    if(ratingResultRec != null) {
                    	ratingResult.add(ratingResultRec);
                    	eligibleFixedDiscountItems.addAll(ratingResultRec.getEligibleFixedDiscountItems());
                    }
                }

            } catch (RatingException e) {
                log.trace("Failed to apply recurring charge {}: {}", recurringChargeInstance, e.getRejectionReason());
                throw e; // e.getBusinessException();

            } catch (BusinessException e) {
                log.error("Failed to apply recurring charge {}: {}", recurringChargeInstance, e.getMessage(), e);
                throw e;
            }
            instanciateCounterPeriods(recurringChargeInstance);
        }

        for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
            usageChargeInstanceService.activateUsageChargeInstance(usageChargeInstance);
            instanciateCounterPeriods(usageChargeInstance);
        }
        

        serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
        serviceInstance = update(serviceInstance);

        // execute subscription script
        if (serviceInstance.getServiceTemplate() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript() != null) {
            serviceModelScriptService.activateServiceInstance(serviceInstance, serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript().getCode());
        }

        if (serviceInstance.getOrderItemId() != null && serviceInstance.getOrderItemAction() != null) {
            orderHistoryService.create(serviceInstance.getOrderNumber(), serviceInstance.getOrderItemId(), serviceInstance, serviceInstance.getOrderItemAction());
        }
        Date endAgreementDate=serviceInstance.getEndAgreementDate() == null ? serviceInstance.getSubscription().getEndAgreementDate() : serviceInstance.getEndAgreementDate();
        if(endAgreementDate!=null) {
        PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.findByServiceTemplate(serviceInstance.getServiceTemplate());
        if (paymentScheduleTemplate != null && paymentScheduleTemplateService.matchExpression(paymentScheduleTemplate.getFilterEl(), serviceInstance)) {
            paymentScheduleInstanceService.instanciateFromService(paymentScheduleTemplate, serviceInstance);
        }
        }

        if(!eligibleFixedDiscountItems.isEmpty()) {
        	//TODO : v12 & dev change new Date() with delivered != null
        	String description = StringUtils.isBlank(serviceInstance.getDescription()) ? serviceInstance.getCode() : serviceInstance.getDescription();
            discountPlanService.calculateDiscountplanItems(eligibleFixedDiscountItems, subscription.getSeller(), subscription.getUserAccount().getBillingAccount(), new Date(), serviceInstance.getQuantity(), null , 
            												serviceInstance.getCode(), subscription.getUserAccount().getWallet(), subscription.getOffer(), null, subscription, description, false, null, null, DiscountPlanTypeEnum.PRODUCT);
        }
        
        return ratingResult;
    }

    /**
     * Terminate service with a date in the past
     * 
     * @param subscription Subscription
     * @param serviceInstance Service instance
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param orderNumber Order number associated with termination
     * @return Updated service instance
     * @throws BusinessException
     */
    private ServiceInstance terminateServiceWithPastDate(Subscription subscription, ServiceInstance serviceInstance, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber)
            throws BusinessException {

        // Execute termination script
        if (serviceInstance.getServiceTemplate() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript() != null) {
            serviceModelScriptService.terminateServiceInstance(serviceInstance, serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript().getCode(), terminationDate, terminationReason);
        }

        boolean applyReimbursment = terminationReason.isApplyReimbursment();
        boolean applyTerminationCharges = terminationReason.isApplyTerminationCharges();

        serviceInstance.setTerminationDate(terminationDate);
        serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
        if (terminationReason != null) {
            serviceInstance.setSubscriptionTerminationReason(terminationReason);
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {

			if (recurringChargeInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
				Date lastChargedDate = recurringChargeInstance.getChargedToDate() != null ? recurringChargeInstance.getChargedToDate() : recurringChargeInstance.getChargeDate();
				recurringChargeInstance.setChargeToDateOnTermination(lastChargedDate);
			}

            recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
            recurringChargeInstance.setTerminationDate(terminationDate);

            Date chargeToDateOnTermination = getChargeToDateOnTermination(recurringChargeInstance);
            recurringChargeInstance.setChargeToDateOnTermination(chargeToDateOnTermination);

            Date chargedToDate = recurringChargeInstance.calculateChargedToDate();
            // if the charge was not rated yet ==> we use subscription date instead of the date to which charge was applied to
            if (chargedToDate == null) {
                chargedToDate = recurringChargeInstance.getSubscriptionDate();
            }

            log.info("Terminating recurring charge {}/{} with chargedToDate {},  terminationDate {}, endAggrementDate {}, efectiveTerminationDate {}, terminationReason {}", recurringChargeInstance.getId(),
                recurringChargeInstance.getCode(), recurringChargeInstance.getChargedToDate(), terminationDate, serviceInstance.getEndAgreementDate(), chargeToDateOnTermination, terminationReason.getCode());

            // Effective termination date was moved to the future - to the end of agreement
            // chargedToDate is null means that charge was never charged
            if (chargedToDate == null || chargeToDateOnTermination.after(chargedToDate)) {
                try {
                    recurringChargeInstanceService.applyRecuringChargeToEndAgreementDate(recurringChargeInstance, chargeToDateOnTermination,
                            terminationReason.isInvoiceAgreementImmediately(), terminationDate);

                } catch (RatingException e) {
                    log.trace("Failed to apply recurring charge {}: {}", recurringChargeInstance, e.getRejectionReason());
                    throw e;

                } catch (BusinessException e) {
                    log.error("Failed to apply recurring charge {}: {}", recurringChargeInstance, e.getMessage(), e);
                    throw e;
                }

            } else if (applyReimbursment && chargeToDateOnTermination.before(chargedToDate)) {

                try {
                    recurringChargeInstanceService.reimburseRecuringCharges(recurringChargeInstance, orderNumber);

                } catch (RatingException e) {
                    log.trace("Failed to apply reimbursement recurring charge {}: {}", recurringChargeInstance, e.getRejectionReason());
                    throw e;

                } catch (BusinessException e) {
                    log.error("Failed to apply reimbursement recurring charge {}: {}", recurringChargeInstance, e.getMessage(), e);
                    throw e;
                }
            }
            recurringChargeInstance = recurringChargeInstanceService.update(recurringChargeInstance);
        }

        if (applyTerminationCharges) {
            for (TerminationChargeInstance oneShotChargeInstance : serviceInstance.getTerminationChargeInstances()) {
                if (oneShotChargeInstance.getStatus() == InstanceStatusEnum.INACTIVE) {
                    log.info("Applying the termination charge {}", oneShotChargeInstance.getId());

                    // #3174 Setting termination informations which will be also reachable from within the "rating scripts"
                    oneShotChargeInstance.setChargeDate(terminationDate);
                    if (orderNumber != null) {
                        oneShotChargeInstance.setOrderNumber(orderNumber);
                    }

                    RatingResult ratingResult = oneShotRatingService.rateOneShotCharge(oneShotChargeInstance, oneShotChargeInstance.getQuantity(), null, terminationDate, orderNumber,
                        ChargeApplicationModeEnum.SUBSCRIPTION, false, false);

                    // Uncomment if want to rate with failSilently=true and there is a job that that will rate failed to rate one shot charges afterwards
//                    // If failed to rate, charge instance status is left as active
//                    if (ratingResult.getRatingException() != null) {
//                        oneShotChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
//                    }

                    oneShotChargeInstanceService.update(oneShotChargeInstance);

                } else {
                    log.debug("we do not apply the termination charge because of its status {}", oneShotChargeInstance.getId(), oneShotChargeInstance.getStatus());
                }
            }
        }

        for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
            usageChargeInstanceService.terminateUsageChargeInstance(usageChargeInstance, terminationDate);
        }

        // Apply one-shot refunds
        if (terminationReason.isReimburseOneshots()) {
            for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getSubscriptionChargeInstances()) {
                if (oneShotChargeInstance.getStatus() == InstanceStatusEnum.CLOSED) {
                    log.info("Reimbursing the subscription charge {}", oneShotChargeInstance.getId());

                    // Left as failSilently=false, as currently there is no way of applying a one shot charge in another way that mode=SUBSCRIPTION
                    oneShotRatingService.rateOneShotCharge(oneShotChargeInstance,  oneShotChargeInstance.getQuantity().negate(), null, terminationDate, orderNumber, ChargeApplicationModeEnum.REIMBURSMENT, false, false);
                    oneShotChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
                    oneShotChargeInstanceService.update(oneShotChargeInstance);
                }
            }
        }

        if (serviceInstance.getServiceTemplate() != null) {
            PaymentScheduleTemplate paymentScheduleTemplate = paymentScheduleTemplateService.findByServiceTemplate(serviceInstance.getServiceTemplate());
            if (paymentScheduleTemplate != null && serviceInstance.getPsInstances() != null && !serviceInstance.getPsInstances().isEmpty()) {
                paymentScheduleInstanceService.terminate(serviceInstance, terminationDate);
            }
        }
        serviceInstance = update(serviceInstance);

        return serviceInstance;
    }

    private ServiceInstance terminateServiceWithFutureDate(ServiceInstance serviceInstance, Date terminationDate, SubscriptionTerminationReason terminationReason) throws BusinessException {

        SubscriptionRenewal serviceRenewal = serviceInstance.getServiceRenewal();
        serviceRenewal.setTerminationReason(PersistenceUtils.initializeAndUnproxy(serviceRenewal.getTerminationReason()));

        Renewal renewal = new Renewal(serviceRenewal, serviceInstance.getSubscribedTillDate());
        serviceInstance.setInitialServiceRenewal(JacksonUtil.toString(renewal));

        serviceInstance.setSubscribedTillDate(terminationDate);
        serviceRenewal.setTerminationReason(terminationReason);
        serviceRenewal.setInitialTermType(SubscriptionRenewal.InitialTermTypeEnum.FIXED);
        serviceRenewal.setAutoRenew(false);
        serviceRenewal.setEndOfTermAction(SubscriptionRenewal.EndOfTermActionEnum.TERMINATE);

        serviceInstance = update(serviceInstance);

        return serviceInstance;
    }

    /**
     * Terminate a service. If terminationDate if before the service's subscriptionDate, a service's subscriptionDate will be used as termination date.
     * 
     * @param serviceInstance Service instance to terminate
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param orderNumber Order number requesting service termination
     * @return Updated service instance
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service exception
     * @throws BusinessException business exception
     */
    public ServiceInstance terminateService(ServiceInstance serviceInstance, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        Subscription subscription = serviceInstance.getSubscription();

        if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE && serviceInstance.getStatus() != InstanceStatusEnum.SUSPENDED) {
            throw new IncorrectServiceInstanceException("Can not terminate a service that is not active or suspended. Service Code=" + serviceInstance.getCode() + ",subscription Code" + subscription.getCode());
        }

        if (terminationDate == null) {
            terminationDate = new Date();
        }

        if (terminationReason == null) {
            throw new ValidationException("Termination reason not provided", "subscription.error.noTerminationReason");
        } else if (DateUtils.setDateToEndOfDay(terminationDate).before(serviceInstance.getSubscription().getSubscriptionDate())) {
            throw new ValidationException("Termination date can not be before the subscription date", "subscription.error.terminationDateBeforeSubscriptionDate");
        }

        if (serviceInstance.getId() != null) {
            log.info("Terminating service {} for {} with reason {}", serviceInstance.getId(), terminationDate, terminationReason);
        }

        // checks if termination date is > now (do not ignore time, as service time is time sensative)
        Date now = new Date();
        if (terminationDate.compareTo(now) <= 0) {
            return terminateServiceWithPastDate(subscription, serviceInstance, terminationDate, terminationReason, orderNumber);
        } else {
            // if future date/time set service termination
            return terminateServiceWithFutureDate(serviceInstance, terminationDate, terminationReason);
        }

    }

    /**
     * @param serviceInstance service instance
     * @param suspensionDate suspension date
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     */
    public void serviceSuspension(ServiceInstance serviceInstance, Date suspensionDate) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        String serviceCode = serviceInstance.getCode();

        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode=" + serviceCode);
        }

        if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is not active. service Code=" + serviceCode + ",subscription Code" + subscription.getCode());
        }

        if (serviceInstance.getServiceTemplate() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript() != null) {
            serviceModelScriptService.suspendServiceInstance(serviceInstance, serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript().getCode(), suspensionDate);
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                recurringChargeInstanceService.recurringChargeSuspension(recurringChargeInstance.getId(), suspensionDate);
            }

        }

        for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
            usageChargeInstanceService.suspendUsageChargeInstance(usageChargeInstance, suspensionDate);
        }

        serviceInstance.setStatus(InstanceStatusEnum.SUSPENDED);
        serviceInstance.setTerminationDate(suspensionDate);
        update(serviceInstance);
    }

    /**
     * @param serviceInstance service instance
     * @param reactivationDate reactivation date
     * @param reactivateSuspendedCharges
     * @param reactivateTerminatedCharges
     * @throws IncorrectSusbcriptionException incorrect subscription exception
     * @throws IncorrectServiceInstanceException incorrect service instance exception
     * @throws BusinessException business exception
     */
    public void serviceReactivation(ServiceInstance serviceInstance, Date reactivationDate, boolean reactivateSuspendedCharges, boolean reactivateTerminatedCharges)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        String serviceCode = serviceInstance.getCode();
        if (reactivationDate == null) {
            reactivationDate = new Date();
        }

        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode=" + serviceInstance.getCode());
        }
        String description = serviceInstance.getServiceTemplate() != null ? serviceInstance.getServiceTemplate().getDescription() : serviceInstance.getProductVersion().getProduct().getDescription();
        if (serviceInstance.getStatus() != InstanceStatusEnum.SUSPENDED && !reactivateTerminatedCharges) {
            throw new IncorrectServiceInstanceException("service instance is not suspended. service Code=" + serviceCode + ",subscription Code" + subscription.getCode());
        }
        checkServiceAssociatedWithOffer(serviceInstance);

        serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
        serviceInstance.setReactivationDate(reactivationDate);
        serviceInstance.setDescription(description);
        serviceInstance.setTerminationDate(null);

        if (reactivateSuspendedCharges) {
            reactivateRecurringChargeWithStatus(serviceInstance, reactivationDate, subscription, InstanceStatusEnum.SUSPENDED);
            reactivateUsageChargeWithStatus(serviceInstance, reactivationDate, InstanceStatusEnum.SUSPENDED);
        }
        if (reactivateTerminatedCharges) {
            reactivateRecurringChargeWithStatus(serviceInstance, reactivationDate, subscription, InstanceStatusEnum.TERMINATED);
            reactivateUsageChargeWithStatus(serviceInstance, reactivationDate, InstanceStatusEnum.TERMINATED);
        }

        update(serviceInstance);

        if (serviceInstance.getServiceTemplate() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel() != null && serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript() != null) {
            serviceModelScriptService.reactivateServiceInstance(serviceInstance, serviceInstance.getServiceTemplate().getBusinessServiceModel().getScript().getCode(), reactivationDate);
        }
    }

    private void reactivateUsageChargeWithStatus(ServiceInstance serviceInstance, Date reactivationDate, InstanceStatusEnum status) {
        for (UsageChargeInstance usageChargeInstance : serviceInstance.getUsageChargeInstances()) {
            if (usageChargeInstance.getStatus() == status) {
                usageChargeInstanceService.reactivateUsageChargeInstance(usageChargeInstance, reactivationDate);
            }
        }
    }

    private void reactivateRecurringChargeWithStatus(ServiceInstance serviceInstance, Date reactivationDate, Subscription subscription, InstanceStatusEnum status) {
        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            if (recurringChargeInstance.getStatus() == status) {
                recurringChargeInstanceService.recurringChargeReactivation(serviceInstance, subscription, reactivationDate);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInstance> findByServiceTemplate(ServiceTemplate serviceTemplate) {

        QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "i");

        try {
            qb.addCriterionEntity("serviceTemplate", serviceTemplate);

            return (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void create(ServiceInstance entity) throws BusinessException {
        entity.updateSubscribedTillAndRenewalNotifyDates();
        super.create(entity);
        // Status audit (to trace the passage from before creation "" to creation "CREATED") need for lifecycle
        auditableFieldService.createFieldHistory(entity, AuditableFieldNameEnum.STATUS.getFieldName(), AuditChangeTypeEnum.STATUS, "", String.valueOf(entity.getStatus()));
    }

    @Override
    public ServiceInstance update(ServiceInstance entity) throws BusinessException {

        boolean quantityChanged = entity.isQuantityChanged();

        entity.updateSubscribedTillAndRenewalNotifyDates();

        entity = super.update(entity);
        // update quantity in charges

        if (entity.getStatus() == InstanceStatusEnum.INACTIVE || quantityChanged) {
            if (entity.getRecurringChargeInstances() != null) {
                for (RecurringChargeInstance chargeInstance : entity.getRecurringChargeInstances()) {
                    if (!chargeInstance.getStatus().isFinalStatus() && (entity.getQuantity() == null || chargeInstance.getQuantity() == null || entity.getQuantity().compareTo(chargeInstance.getQuantity()) != 0)) {
                        chargeInstance.setQuantity(entity.getQuantity());
                    }
                }
            }
            if (entity.getSubscriptionChargeInstances() != null) {
                for (SubscriptionChargeInstance chargeInstance : entity.getSubscriptionChargeInstances()) {
                    if (!chargeInstance.getStatus().isFinalStatus() && (entity.getQuantity() == null || chargeInstance.getQuantity() == null || entity.getQuantity().compareTo(chargeInstance.getQuantity()) != 0)) {
                        chargeInstance.setQuantity(entity.getQuantity());
                    }
                }
            }

            if (entity.getTerminationChargeInstances() != null) {
                for (TerminationChargeInstance chargeInstance : entity.getTerminationChargeInstances()) {
                    if (!chargeInstance.getStatus().isFinalStatus() && (entity.getQuantity() == null || chargeInstance.getQuantity() == null || entity.getQuantity().compareTo(chargeInstance.getQuantity()) != 0)) {
                        chargeInstance.setQuantity(entity.getQuantity());
                    }
                }
            }
        }

        return entity;

    }

    /**
     * Check of service template has any instances
     * 
     * @param serviceTemplate Service template
     * @param status Instance status. Opptional
     * @return True if any instances are found
     */
    public boolean hasInstances(ServiceTemplate serviceTemplate, InstanceStatusEnum status) {
        QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "i");

        try {
            qb.addCriterionEntity("serviceTemplate", serviceTemplate);

            if (status != null) {
                qb.addCriterionEnum("status", status);
            }

            return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult()).longValue() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInstance> listServiceInstance(Long subscriptionId, String serviceInstanceCode) {
        List<ServiceInstance> serviceInstances = null;
        try {
            QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
            qb.addCriterion("c.code", "=", serviceInstanceCode, true);
            qb.addValueIsEqualToField("c.subscription.id", subscriptionId, false, false);
            serviceInstances = (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by code (code={}). Result found={}.", new Object[] { "ServiceInstance", serviceInstanceCode, serviceInstances != null });

        } catch (NoResultException nre) {
            log.debug("listServiceInstance : no service has been found");

        } catch (Exception e) {
            log.error("listServiceInstance error={} ", e.getMessage());
        }

        return serviceInstances;
    }

    /**
     * Get a list of service ids that are about to expire or have expired already
     *
     * @param untillDate the subscription till date
     * @return A list of service ids
     */
    public List<Long> getSubscriptionsToRenewOrNotify(Date untillDate) {
        List<Long> ids = getEntityManager().createNamedQuery("ServiceInstance.getExpired", Long.class) //
            .setParameter("date", untillDate) //
            .setParameter("subscriptionStatuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.SUSPENDED)) //
            .setParameter("statuses", Arrays.asList(InstanceStatusEnum.ACTIVE, InstanceStatusEnum.SUSPENDED)) //
            .getResultList();
        
        ids.addAll(getEntityManager().createNamedQuery("ServiceInstance.getPendingToActivate", Long.class) //
                .setParameter("date", new Date()) //
                .setParameter("subscriptionStatuses", Arrays.asList(SubscriptionStatusEnum.PENDING)) //
                .setParameter("statuses", Arrays.asList(InstanceStatusEnum.PENDING)) //
                .getResultList());

        ids.addAll(getEntityManager().createNamedQuery("ServiceInstance.getToNotifyExpiration", Long.class) //
            .setParameter("date", untillDate) //
            .setParameter("subscriptionStatuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.SUSPENDED)) //
            .setParameter("statuses", Arrays.asList(InstanceStatusEnum.ACTIVE, InstanceStatusEnum.SUSPENDED)) //
            .getResultList());

        return ids;
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInstance> findBySubscription(Subscription s) {
        QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "si");
        qb.addCriterionEntity("subscription", s);

        return qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Find a list of service instance by subscription entity, service template codes and service instance status list.
     * 
     * @param codes the service template codes
     * @param subscriptionCode the code of subscription entity
     * @param statuses service instance statuses
     * @return the ServiceInstance list found
     */
    @SuppressWarnings("unchecked")
    public List<ServiceInstance> findByCodeSubscriptionAndStatus(List<String> codes, String subscriptionCode, InstanceStatusEnum... statuses) {
        List<ServiceInstance> serviceInstances = null;
        try {

            QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");

            qb.startOrClause();
            if (codes != null && !codes.isEmpty()) {
                for (String code : codes) {
                    qb.addCriterion("c.code", "=", code, true);
                }
            }
            qb.endOrClause();

            qb.addCriterion("c.subscription.code", "=", subscriptionCode, true);
            qb.startOrClause();
            if (statuses != null && statuses.length > 0) {
                for (InstanceStatusEnum status : statuses) {
                    qb.addCriterionEnum("c.status", status);
                }
            }
            qb.endOrClause();

            serviceInstances = (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by code and subscription/status (code={}). Result found={}.", "ServiceInstance", codes, serviceInstances != null && !serviceInstances.isEmpty());

        } catch (Exception e) {
            log.error("Failed to find ServiceInstance by code and subscription/status (code={}) ..", codes, e);
        }

        return serviceInstances;
    }

    /**
     * check if the service will be terminated in future
     *
     * @param serviceInstance the service instance
     * @return true is the service will be terminated in future.
     */
    public boolean willBeTerminatedInFuture(ServiceInstance serviceInstance) {
        Subscription subscription = serviceInstance != null ? serviceInstance.getSubscription() : null;
        SubscriptionRenewal serviceRenewal = serviceInstance != null ? serviceInstance.getServiceRenewal() : null;
        return (serviceInstance != null && subscription != null && subscription.getStatus() == SubscriptionStatusEnum.ACTIVE
                && (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE || serviceInstance.getStatus() == InstanceStatusEnum.SUSPENDED) && serviceInstance.getSubscribedTillDate() != null
                && serviceInstance.getSubscribedTillDate().compareTo(new Date()) > 0 && serviceRenewal != null && !serviceRenewal.isAutoRenew() && serviceRenewal.getTerminationReason() != null
                && serviceRenewal.getEndOfTermAction() == SubscriptionRenewal.EndOfTermActionEnum.TERMINATE);
    }

    /**
     * cancel service termination
     *
     * @param serviceInstance the service instance
     * @throws BusinessException business exception
     */
    public void cancelServiceTermination(ServiceInstance serviceInstance) throws BusinessException {
        if (!InstanceStatusEnum.TERMINATED.equals(serviceInstance.getStatus())) {
            throw new BusinessException("Cannot cancelServiceTermination for a service with status " + serviceInstance.getStatus());
        }
        SubscriptionRenewal serviceRenewal = null;
        Date subscribedTillDate = null;

        String initialRenewal = serviceInstance.getInitialServiceRenewal();
        if (!StringUtils.isBlank(initialRenewal)) {

            Renewal renewal = JacksonUtil.fromString(initialRenewal, Renewal.class);
            serviceRenewal = renewal.getValue();
            serviceRenewal.setTerminationReason(serviceRenewal.getTerminationReason() != null && serviceRenewal.getTerminationReason().getId() != null ? serviceRenewal.getTerminationReason() : null);
            subscribedTillDate = renewal.getSubscribedTillDate();
        }
        serviceInstance.setServiceRenewal(serviceRenewal);
        serviceInstance.setSubscribedTillDate(subscribedTillDate);
        update(serviceInstance);
    }

    /**
     * Get an effective date that charge should be applied to. In case termination reason implies to apply a endAgreement, effective date will be endAgreement date (if it was provided on service), otherwise a termination
     * date. This rule can be overridden by an EL expression on Recurring charge template level.
     * 
     * 
     * @return Termination date or end agreement date depending on termination reason
     */
    private Date getChargeToDateOnTermination(RecurringChargeInstance chargeInstance) {

        if (chargeInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
            return null;
        }

        if (chargeInstance.getChargeToDateOnTermination() != null) {
            return chargeInstance.getChargeToDateOnTermination();
        }

        Date terminationDate = chargeInstance.getTerminationDate();
        Date endAgreementDate = chargeInstance.getServiceInstance().getEndAgreementDate();

        Date chargeToDateOnTermination = terminationDate;

        SubscriptionTerminationReason terminationReason = chargeInstance.getServiceInstance().getSubscriptionTerminationReason();
        if (terminationReason.isApplyAgreement() && endAgreementDate != null) {
            chargeToDateOnTermination = terminationDate.after(endAgreementDate) ? terminationDate : endAgreementDate;
        }

        if (chargeInstance.getRecurringChargeTemplate().getApplyTerminatedChargeToDateEL() != null) {
            Date evalDate = ValueExpressionWrapper.evaluateExpression(chargeInstance.getRecurringChargeTemplate().getApplyTerminatedChargeToDateEL(), Date.class, chargeInstance, chargeToDateOnTermination);
            if (evalDate != null) {
                chargeToDateOnTermination = evalDate;
            }
        }
        return chargeToDateOnTermination;
    }

    /**
     * Find a service instance list by subscription entity, service template code and service instance status list.
     * 
     * @param code the service template code
     * @param subscriptionCode the subscription code
     * @return the ServiceInstance list found
     */
    @SuppressWarnings("unchecked")
    public List<ServiceInstance> findByCodeAndCodeSubscription(String code, String subscriptionCode) {
        return getEntityManager().createNamedQuery("ServiceInstance.findByServiceCodeAndSubscriptionCode") //
            .setParameter("code", code).setParameter("subscriptionCode", subscriptionCode).getResultList();

    }

    /**
     * Find a service instance by subscription entity id, service instance code
     *
     * @param code service instance code
     * @param subscription subscription
     * @return the ServiceInstance found
     */
    public ServiceInstance findByCodeAndCodeSubscriptionId(String code, Subscription subscription) {
        try {
            return (ServiceInstance) getEntityManager().createNamedQuery("ServiceInstance.findByServiceCodeAndSubscriptionId")
                    .setParameter("code", code)
                    .setParameter("subscriptionId", subscription.getId())
                    .getSingleResult();
        } catch (Exception exception) {
            throw new BusinessException("No service instance with code " + code + " associated to subscription code : " + subscription.getCode());
        }
    }

    private void instanciateCounterPeriods(ChargeInstance chargeInstance) {

        // Accumulator counters
        for (CounterInstance counterInstance : chargeInstance.getAccumulatorCounterInstances()) {
            if (counterInstance != null) {
                counterInstanceService.createCounterPeriodIfMissingInSameTX(counterInstance, chargeInstance.getChargeDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance, null, null);
            }
        }
        // Standard counter
        if (chargeInstance.getCounter() != null) {
            counterInstanceService.createCounterPeriodIfMissingInSameTX(chargeInstance.getCounter(), chargeInstance.getChargeDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance, null, null);
        }
    } 
    
    /**
     * Find a service instance matching id or code for a given subscription and optional statuses. I
     *
     * @param serviceId Service instance id
     * @param serviceCode Service instance code
     * @param subscription Subscription containing service instance
     * @param statuses Statuses to match (optional)
     * @return Service instance matched
     * @throws MissingParameterException Either serviceId or serviceCode value must be provided
     * @throws EntityDoesNotExistsException Service instance was not matched
     * @throws InvalidParameterException More than one matching service instance found or does not correspond to given subscription and/or statuses
     */
    public ServiceInstance getSingleServiceInstance(Long serviceId, String serviceCode, Subscription subscription, InstanceStatusEnum... statuses)
            throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException {

        ServiceInstance serviceInstance = null;
        if (serviceId != null) {
            serviceInstance = findById(serviceId);

            if (serviceInstance == null) {
                throw new EntityDoesNotExistsException(ServiceInstance.class, serviceId);

            } else if (!serviceInstance.getSubscription().equals(subscription) || (statuses != null && statuses.length > 0 && !ArrayUtils.contains(statuses, serviceInstance.getStatus()))) {
                throw new InvalidParameterException("Service instance id " + serviceId + " does not correspond to subscription " + subscription.getCode() + " or is not of status ["
                        + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "]");
            }

        } else if (!StringUtils.isBlank(serviceCode)) {
            List<ServiceInstance> services = findByCodeSubscriptionAndStatus(serviceCode, subscription, statuses);
            if (services.size() == 1) {
                serviceInstance = services.get(0);
            } else if (services.size() > 1) {
                throw new InvalidParameterException(
                        "More than one service instance with status [" + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "] was found. Please use ID to refer to service instance.");
            } else {
                throw new EntityDoesNotExistsException("Service instance with code " + serviceCode + " was not found or is not of status [" + (statuses != null ? StringUtils.concatenate((Object[]) statuses) : "") + "]");
            }

        } else {
            throw new MissingParameterException("service id or code");
        }
        return serviceInstance;
    }
}