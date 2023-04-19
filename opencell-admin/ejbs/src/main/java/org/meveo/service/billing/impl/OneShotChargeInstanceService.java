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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.TerminationChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ServiceCharge;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

@Stateless
public class OneShotChargeInstanceService extends BusinessService<OneShotChargeInstance> {

    @Inject
    private WalletService walletService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private OneShotRatingService oneShotRatingService;

    public OneShotChargeInstance findByCodeAndSubsription(String code, Long subscriptionId) {
        OneShotChargeInstance oneShotChargeInstance = null;
        try {
            log.debug("start of find {} by code (code={}, subscriptionId={}) ..", new Object[] { "OneShotChargeInstance", code, subscriptionId });
            QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
            oneShotChargeInstance = (OneShotChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();
            log.debug("end of find {} by code (code={}, subscriptionId={}). Result found={}.", new Object[] { "OneShotChargeInstance", code, subscriptionId, oneShotChargeInstance != null });
        } catch (NoResultException nre) {
            log.debug("findByCodeAndSubsription : aucune charge ponctuelle n'a ete trouvee");
        } catch (Exception e) {
            log.error("failed to find oneShotChargeInstance by Code and subsription", e);
        }
        return oneShotChargeInstance;
    }

    @Override
    public OneShotChargeInstance findById(Long oneShotChargeId) {
        OneShotChargeInstance oneShotChargeInstance = null;
        try {
            log.debug("start of find {} by id (id={}) ..", new Object[] { "OneShotChargeInstance", oneShotChargeId });
            QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
            qb.addCriterion("c.id", "=", oneShotChargeId, true);
            oneShotChargeInstance = (OneShotChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();
            log.debug("end of find {} by id (id={}, Result found={}.", new Object[] { "OneShotChargeInstance", oneShotChargeId, oneShotChargeInstance != null });
        } catch (NoResultException nre) {
            log.debug("findById : aucune charge ponctuelle n'a ete trouvee");
        } catch (Exception e) {
            log.error("failed to find oneShotChargeInstance by Id", e);
        }
        return oneShotChargeInstance;
    }

    /**
     * Instantiate subscription or termination charge
     *
     * @param serviceInstance Service instance
     * @param serviceCharge
     * @param serviceChargeTemplate Service Charge template
     * @param amoutWithoutTax Amount without tax
     * @param amoutWithTax Amount with tax
     * @param isSubscriptionCharge True if this is a subscription charge
     * @param isVirtual Is it a virtual charge - should not be persisted
     * @return Subscription or termination charge instance
     * @throws BusinessException General exception
     */
    @SuppressWarnings("rawtypes")
    public OneShotChargeInstance oneShotChargeInstanciation(ServiceInstance serviceInstance, ServiceCharge serviceCharge, ServiceChargeTemplate serviceChargeTemplate, BigDecimal amoutWithoutTax, BigDecimal amoutWithTax,
            boolean isSubscriptionCharge, boolean isVirtual) throws BusinessException {

        OneShotChargeTemplate chargeTemplate = (OneShotChargeTemplate) serviceChargeTemplate.getChargeTemplate();

        log.debug("Instanciate a oneshot charge for code {} on subscription {}", chargeTemplate.getCode(), serviceInstance.getSubscription().getCode());

        OneShotChargeInstance oneShotChargeInstance = null;

        if (isSubscriptionCharge) {
            oneShotChargeInstance = new SubscriptionChargeInstance(amoutWithoutTax, amoutWithTax, chargeTemplate, serviceInstance, InstanceStatusEnum.INACTIVE);
        } else {
            oneShotChargeInstance = new TerminationChargeInstance(amoutWithoutTax, amoutWithTax, chargeTemplate, serviceInstance, InstanceStatusEnum.INACTIVE);
        }

        List<WalletTemplate> walletTemplates = null;

        // FIXME : this code should not be here
        if (isSubscriptionCharge) {
            ServiceChargeTemplateSubscription recChTmplServ = serviceCharge.getServiceChargeTemplateSubscriptionByChargeCode(chargeTemplate.getCode());
            walletTemplates = recChTmplServ.getWalletTemplates();

        } else {
            ServiceChargeTemplateTermination recChTmplServ = serviceCharge.getServiceChargeTemplateTerminationByChargeCode(chargeTemplate.getCode());
            walletTemplates = recChTmplServ.getWalletTemplates();
        }
        // By default we set the charge instance as being postpaid
        oneShotChargeInstance.setPrepaid(false);
        if (walletTemplates != null && walletTemplates.size() > 0) {
            for (WalletTemplate walletTemplate : walletTemplates) {
                if (walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                    // This wallet is prepaid, we set the charge instance itself as being prepaid
                    oneShotChargeInstance.setPrepaid(true);

                }
                WalletInstance walletInstance = walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(), walletTemplate, isVirtual);
                log.debug("Added the wallet instance {} to the chargeInstance {}", walletInstance.getId(), oneShotChargeInstance.getId());
                oneShotChargeInstance.getWalletInstances().add(walletInstance);
            }
        } else {
            // As the charge is postpaid, we add the principal wallet
            oneShotChargeInstance.getWalletInstances().add(serviceInstance.getSubscription().getUserAccount().getWallet());
        }

        if (!isVirtual) {
            create(oneShotChargeInstance);
        }

        if (serviceChargeTemplate.getAccumulatorCounterTemplates() != null && !serviceChargeTemplate.getAccumulatorCounterTemplates().isEmpty()) {
            for (Object counterTemplate : serviceChargeTemplate.getAccumulatorCounterTemplates()) {
                CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, (CounterTemplate) counterTemplate,oneShotChargeInstance, isVirtual);
                log.debug("Counter instance {} will be add to charge instance {}", counterInstance, oneShotChargeInstance);
                oneShotChargeInstance.addAccumulatorCounterInstance(counterInstance);
            }

            if (!isVirtual) {
                update(oneShotChargeInstance);
            }
        }

        return oneShotChargeInstance;
    }

    /**
     * Instantiate and apply/rate a one shot charge
     * 
     * @param subscription Subscription, to instantiate a charge against
     * @param serviceInstance Service instance to instantiate a charge against. Optional.
     * @param chargeTemplate Charge to instantiate
     * @param walletCode Wallet code for charge against a specific wallet. Optional. A primary User account wallet will be used if not provided.
     * @param chargeDate Charge date
     * @param amoutWithoutTax Amount without tax to override
     * @param amoutWithTax Amount with tax to override
     * @param quantity Quantity to charge
     * @param criteria1 Criteria parameter value
     * @param criteria2 Criteria parameter value
     * @param criteria3 Criteria parameter value
     * @param description Description to override. Optional
     * @param orderNumberOverride Order number to override. If not provided, a value from serviceInstance or subscription will be used. A value of ChargeInstance.NO_ORDER_NUMBER will set a value to null.
     * @param cfValues Custom field values
     * @param applyCharge True if wallet operation should be created
     * @param chargeMode Charge mode. Optional. Defaults to SUBSCRIPTION.
     * @return One shot charge instance
     * @throws BusinessException General business exception
     * @throws RatingException Rating related exception
     */
    public OneShotChargeInstance instantiateAndApplyOneShotCharge(Subscription subscription, ServiceInstance serviceInstance, OneShotChargeTemplate chargeTemplate, String walletCode, Date chargeDate,
            BigDecimal amoutWithoutTax, BigDecimal amoutWithTax, BigDecimal quantity, String criteria1, String criteria2, String criteria3, String description, String orderNumberOverride, CustomFieldValues cfValues,
            boolean applyCharge, ChargeApplicationModeEnum chargeMode) throws BusinessException, RatingException {

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            final Date terminationDate = subscription.getTerminationDate();
            if (terminationDate != null && terminationDate.compareTo(chargeDate) <= 0) {
                throw new ValidationException("Subscription " + subscription.getCode() + " is already RESILIATED or CANCELLED.");
            }
        }

        if (chargeDate.before(subscription.getSubscriptionDate())) {
            throw new ValidationException("Operation date is before subscription date for subscription: " + subscription.getCode());
        }

        if (quantity == null) {
            quantity = BigDecimal.ONE;
        }

        if (chargeTemplate.getAmountEditable() != null && !chargeTemplate.getAmountEditable()) {
            amoutWithoutTax = null;
            amoutWithTax = null;
        }

        OneShotChargeInstance oneShotChargeInstance = null;

        if (serviceInstance != null) {
            if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.SUBSCRIPTION) {
                oneShotChargeInstance = new SubscriptionChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumberOverride, serviceInstance, chargeTemplate);
            } else if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
                oneShotChargeInstance = new TerminationChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumberOverride, serviceInstance, chargeTemplate);
            } else {
                oneShotChargeInstance = new OneShotChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumberOverride, serviceInstance, chargeTemplate);
            }
        } else {
            if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.SUBSCRIPTION) {
                oneShotChargeInstance = new SubscriptionChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumberOverride, subscription, chargeTemplate);
            } else if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
                oneShotChargeInstance = new TerminationChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumberOverride, subscription, chargeTemplate);
            } else {
                oneShotChargeInstance = new OneShotChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumberOverride, subscription, chargeTemplate);
            }
        }

        oneShotChargeInstance.setCriteria1(criteria1);
        oneShotChargeInstance.setCriteria2(criteria2);
        oneShotChargeInstance.setCriteria3(criteria3);

        if (cfValues != null) {
            oneShotChargeInstance.setCfValues(cfValues);
        }

        if (walletCode == null) {
            oneShotChargeInstance.setPrepaid(false);
            oneShotChargeInstance.getWalletInstances().add(subscription.getUserAccount().getWallet());
        } else {
            WalletInstance wallet = subscription.getUserAccount().getWalletInstance(walletCode);
            oneShotChargeInstance.getWalletInstances().add(wallet);
            if (wallet.getWalletTemplate() == null) {
                oneShotChargeInstance.setPrepaid(false);
            } else {
                if (wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
                    oneShotChargeInstance.setPrepaid(true);
                }
            }
        }

        create(oneShotChargeInstance);

        if (chargeMode == null) {
            chargeMode = ChargeApplicationModeEnum.SUBSCRIPTION;
        }

        if (applyCharge) {
            oneShotRatingService.rateOneShotCharge(oneShotChargeInstance, quantity, null, chargeDate, orderNumberOverride, chargeMode, false, false);
        }
        return oneShotChargeInstance;
    }

    /**
     * Apply/rate one shot charge to a user account.
     * 
     * @param subscription subscription
     * @param oneShotChargeInstance Recurring charge instance
     * @param quantity Quantity as calculated
     * @param effectiveDate Recurring charge application start
     * @param orderNumberOverride Order number to overwrite with
     * @param chargeMode Charge mode. Optional. Defaults to SUBSCRIPTION.
     * @return Rating status summary
     * @throws BusinessException business exception.
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult applyOneShotCharge(OneShotChargeInstance oneShotChargeInstance, Date effectiveDate, BigDecimal quantity, String orderNumberOverride, ChargeApplicationModeEnum chargeMode)
            throws BusinessException, RatingException {

        log.debug("Apply one shot charge. User account {}, offer {}, charge {}, quantity {}", oneShotChargeInstance.getUserAccount().getCode(), oneShotChargeInstance.getSubscription().getOffer().getCode(),
            oneShotChargeInstance.getChargeTemplate().getCode(), quantity);

        RatingResult ratingResult = oneShotRatingService.rateOneShotCharge(oneShotChargeInstance, quantity, null, effectiveDate, orderNumberOverride, chargeMode, false, false);

        return ratingResult;
    }

    /**
     * Apply one shot charge to a user account for a Virtual operation. Does not create/update/persist any entity.
     * 
     * @param subscription subscription
     * @param oneShotChargeInstance Recurring charge instance
     * @param quantity Quantity as calculated
     * @param effectiveDate Recurring charge application start
     * @return Rating status summary
     * @throws BusinessException business exception.
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult applyOneShotChargeVirtual(OneShotChargeInstance oneShotChargeInstance, Date effectiveDate, BigDecimal quantity) throws BusinessException, RatingException {

        log.debug("Apply one shot charge on Virtual operation. User account {}, offer {}, charge {}, quantity {}", oneShotChargeInstance.getUserAccount().getCode(),
            oneShotChargeInstance.getSubscription().getOffer().getCode(), oneShotChargeInstance.getChargeTemplate().getCode(), quantity);

        RatingResult ratingResult = oneShotRatingService.rateOneShotCharge(oneShotChargeInstance, quantity, null, effectiveDate, oneShotChargeInstance.getSubscription().getOrderNumber(),
            ChargeApplicationModeEnum.SUBSCRIPTION, true, false);

        return ratingResult;
    }

    @SuppressWarnings("unchecked")
    public List<OneShotChargeInstance> findOneShotChargeInstancesBySubscriptionId(Long subscriptionId) {
        QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c", Arrays.asList("chargeTemplate"));
        qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
        return qb.getQuery(getEntityManager()).getResultList();
    }

    public void terminateOneShotChargeInstance(OneShotChargeInstance oneShotChargeInstance) throws BusinessException {

        getEntityManager().createNamedQuery("WalletOperation.setStatusOfNotTreatedToCanceledByCharge").setParameter("chargeInstance", oneShotChargeInstance).setParameter("now", new Date());
        oneShotChargeInstance.setStatus(InstanceStatusEnum.CANCELED);

        update(oneShotChargeInstance);
    }

    public void matchPrepaidWallet(WalletInstance wallet, String matchingChargeCode) throws BusinessException, RatingException {

        OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(matchingChargeCode);
        if (oneShotChargeTemplate == null) {
            throw new BusinessException("Charge template " + matchingChargeCode + " not found");
        }

        List<WalletOperation> wos = getEntityManager().createNamedQuery("WalletOperation.getOpenByWallet", WalletOperation.class).setParameter("wallet", wallet).setHint("org.hibernate.readOnly", true).getResultList();

        if (wos.isEmpty()) {
            return;
        }

        log.info("Prepaid matching - setting to TREATED {} wallet operations on wallet {} and creating matching and compensating charges and wallet operations", wos.size(), wallet.getId());

        BigDecimal balanceNoTax = BigDecimal.ZERO;
        BigDecimal balanceWithTax = BigDecimal.ZERO;

        Subscription firstActiveSubscription = null;
        for (WalletOperation wo : wos) {
            if (firstActiveSubscription == null && wo.getSubscription() != null && wo.getSubscription().isActive()) {
                firstActiveSubscription = wo.getSubscription();
            }

            // Here the amounts must be summed up negated
            balanceNoTax = balanceNoTax.subtract(wo.getAmountWithoutTax());
            balanceWithTax = balanceWithTax.subtract(wo.getAmountWithTax());

            wo.changeStatus(WalletOperationStatusEnum.TREATED);

        }
        if (firstActiveSubscription == null) {
            for (Subscription sub : wallet.getUserAccount().getSubscriptions()) {
                if (sub.isActive()) {
                    firstActiveSubscription = sub;
                    break;
                }
            }
            if (firstActiveSubscription == null) {
                throw new BusinessException("NO_ACTIVE_SUBSCRIPTION");
            }
        }

        log.debug("Create matching and compensating charge {} instances with amountWithoutTax {}, amountWithTax {}", matchingChargeCode, balanceNoTax, balanceWithTax);
        OneShotChargeInstance matchingCharge = instantiateAndApplyOneShotCharge(firstActiveSubscription, null, (OneShotChargeTemplate) oneShotChargeTemplate, wallet.getCode(), new Date(), balanceNoTax, balanceWithTax,
            BigDecimal.ONE, null, null, null, null, null, null, true, null);

        if (matchingCharge == null) {
            throw new BusinessException("Cannot find or create matching charge instance for code " + matchingChargeCode);
        }
        OneShotChargeInstance compensationCharge = instantiateAndApplyOneShotCharge(firstActiveSubscription, null, (OneShotChargeTemplate) oneShotChargeTemplate, wallet.getCode(), new Date(), balanceNoTax.negate(),
            balanceWithTax.negate(), BigDecimal.ONE, null, null, null, null, null, null, true, null);
        if (compensationCharge == null) {
            throw new BusinessException("Cannot find or create compensating charge instance for code " + matchingChargeCode);
        }
        BigDecimal inputQuantity = BigDecimal.ONE;

        RatingResult ratingResult = oneShotRatingService.rateOneShotCharge(matchingCharge, inputQuantity, null, new Date(), firstActiveSubscription.getOrderNumber(), ChargeApplicationModeEnum.SUBSCRIPTION, false, false);
        for (WalletOperation walletOperation : ratingResult.getWalletOperations()) {
            walletOperation.changeStatus(WalletOperationStatusEnum.TREATED);
        }

        ratingResult = oneShotRatingService.rateOneShotCharge(compensationCharge, inputQuantity, null, new Date(), null, ChargeApplicationModeEnum.SUBSCRIPTION, false, false);
        for (WalletOperation walletOperation : ratingResult.getWalletOperations()) {
            walletOperation.changeStatus(WalletOperationStatusEnum.TREATED);
        }

        // we check that balance is unchanged
        BigDecimal cacheBalance = walletService.getWalletBalance(wallet.getId());
        if (cacheBalance.compareTo(balanceWithTax) != 0) {
            log.error("Balances after prepaid matching process do not match. Balance in cache={}, balance expected={}", cacheBalance, balanceWithTax);
            throw new BusinessException("MATCHING_ERROR");
        }
    }
}