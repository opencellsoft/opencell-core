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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.TerminationChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
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
    private WalletOperationService walletOperationService;

    @Inject
    private CounterInstanceService counterInstanceService;

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
     * @param serviceChargeTemplate Service Charge template
     * @param amoutWithoutTax Amount without tax
     * @param amoutWithTax Amount with tax
     * @param isSubscriptionCharge True if this is a subscription charge
     * @param isVirtual Is it a virtual charge - should not be persisted
     * @return Subscription or termination charge instance
     * @throws BusinessException General exception
     */
    @SuppressWarnings("rawtypes")
    public OneShotChargeInstance oneShotChargeInstanciation(ServiceInstance serviceInstance, ServiceChargeTemplate serviceChargeTemplate, BigDecimal amoutWithoutTax, BigDecimal amoutWithTax, boolean isSubscriptionCharge,
            boolean isVirtual) throws BusinessException {

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
            ServiceChargeTemplateSubscription recChTmplServ = serviceInstance.getServiceTemplate().getServiceChargeTemplateSubscriptionByChargeCode(chargeTemplate.getCode());
            walletTemplates = recChTmplServ.getWalletTemplates();

        } else {
            ServiceChargeTemplateTermination recChTmplServ = serviceInstance.getServiceTemplate().getServiceChargeTemplateTerminationByChargeCode(chargeTemplate.getCode());
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
                CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, (CounterTemplate) counterTemplate, isVirtual);
                log.debug("Counter instance {} will be add to charge instance {}", counterInstance, oneShotChargeInstance);
                oneShotChargeInstance.addCounterInstance(counterInstance);
            }

            if (!isVirtual) {
                update(oneShotChargeInstance);
            }
        }

        return oneShotChargeInstance;
    }

    /**
     * Instantiate and apply a one shot charge. Charge will be applied with a charge mode "Subscription".
     * 
     * @param subscription Subscription, to instantiate a charge against
     * @param chargetemplate Charge to instantiate
     * @param walletCode Wallet code for charge against a specific wallet. Optional. A primary User account wallet will be used if not provided.
     * @param chargeDate Charge date
     * @param amoutWithoutTax Amount without tax to override
     * @param amoutWithTax Amount with tax to override
     * @param quantity Quantity to charge
     * @param orderNumber Order number
     * @param applyCharge True if wallet operation should be created
     * @return One shot charge instance
     * @throws BusinessException General business exception
     * @throws RatingException Rating related exception
     */
    public OneShotChargeInstance oneShotChargeApplication(Subscription subscription, OneShotChargeTemplate chargetemplate, String walletCode, Date chargeDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithTax,
            BigDecimal quantity, String orderNumber, boolean applyCharge) throws BusinessException, RatingException {

        return oneShotChargeApplication(subscription, null, chargetemplate, walletCode, chargeDate, amoutWithoutTax, amoutWithTax, quantity, null, null, null, null, orderNumber, null, applyCharge,
            ChargeApplicationModeEnum.SUBSCRIPTION);
    }

    /**
     * Instantiate and apply a one shot charge
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
     * @param orderNumber Order number
     * @param cfValues Custom field values
     * @param applyCharge True if wallet operation should be created
     * @param chargeMode Charge mode
     * @return One shot charge instance
     * @throws BusinessException General business exception
     * @throws RatingException Rating related exception
     */
    public OneShotChargeInstance oneShotChargeApplication(Subscription subscription, ServiceInstance serviceInstance, OneShotChargeTemplate chargeTemplate, String walletCode, Date chargeDate, BigDecimal amoutWithoutTax,
            BigDecimal amoutWithTax, BigDecimal quantity, String criteria1, String criteria2, String criteria3, String description, String orderNumber, CustomFieldValues cfValues, boolean applyCharge,
            ChargeApplicationModeEnum chargeMode) throws BusinessException, RatingException {

        if (quantity == null) {
            quantity = BigDecimal.ONE;
        }

        if (!chargeTemplate.getAmountEditable()) {
            amoutWithoutTax = null;
            amoutWithTax = null;
        }

        OneShotChargeInstance oneShotChargeInstance = null;

        if (serviceInstance != null) {
            if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.SUBSCRIPTION) {
                oneShotChargeInstance = new SubscriptionChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumber, serviceInstance, chargeTemplate);
            } else if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
                oneShotChargeInstance = new TerminationChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumber, serviceInstance, chargeTemplate);
            } else {
                oneShotChargeInstance = new OneShotChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumber, serviceInstance, chargeTemplate);
            }
        } else {
            if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.SUBSCRIPTION) {
                oneShotChargeInstance = new SubscriptionChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumber, subscription, chargeTemplate);
            } else if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
                oneShotChargeInstance = new TerminationChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumber, subscription, chargeTemplate);
            } else {
                oneShotChargeInstance = new OneShotChargeInstance(description, chargeDate, amoutWithoutTax, amoutWithTax, quantity, orderNumber, subscription, chargeTemplate);
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

        if (!walletOperationService.isChargeMatch(oneShotChargeInstance, chargeTemplate.getFilterExpression())) {
            log.debug("not rating chargeInstance with code={}, filter expression not evaluated to true", oneShotChargeInstance.getCode());
            return oneShotChargeInstance;
        }

        if (applyCharge) {
            walletOperationService.applyOneShotWalletOperation(subscription, oneShotChargeInstance, quantity, null, chargeDate, false, subscription.getOrderNumber(), chargeMode);
            oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
        }
        return oneShotChargeInstance;
    }

    /**
     * Apply a charge that WAS INSTANTIATED before. Charge will be applied with a charge mode "Subscription".
     * 
     * @param oneShotChargeInstance A charge to apply
     * @param chargeDate Charge date
     * @param quantity Quantity to apply
     * @param orderNumberOverride Order number to override
     * @throws BusinessException General business exception
     * @throws RatingException Rating related exception
     */
    public void oneShotChargeApplication(OneShotChargeInstance oneShotChargeInstance, Date chargeDate, BigDecimal quantity, String orderNumberOverride) throws BusinessException, RatingException {
        oneShotChargeApplication(oneShotChargeInstance, chargeDate, quantity, orderNumberOverride, ChargeApplicationModeEnum.SUBSCRIPTION);
    }

    /**
     * Apply a charge that WAS INSTANTIATED before
     * 
     * @param oneShotChargeInstance A charge to apply
     * @param chargeDate Charge date
     * @param quantity Quantity to apply
     * @param orderNumberOverride Order number to override
     * @param chargeMode Charge mode
     * @throws BusinessException General business exception
     * @throws RatingException Rating related exception
     */
    public void oneShotChargeApplication(OneShotChargeInstance oneShotChargeInstance, Date chargeDate, BigDecimal quantity, String orderNumberOverride, ChargeApplicationModeEnum chargeMode)
            throws BusinessException, RatingException {

        if (!walletOperationService.isChargeMatch(oneShotChargeInstance, oneShotChargeInstance.getChargeTemplate().getFilterExpression())) {
            log.debug("not rating chargeInstance with code={}, filter expression not evaluated to true", oneShotChargeInstance.getCode());
            return;
        }

        walletOperationService.applyOneShotWalletOperation(oneShotChargeInstance.getSubscription(), oneShotChargeInstance, quantity, null, chargeDate, false, orderNumberOverride, chargeMode);

        oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
    }

    /**
     * Apply one shot charge to a user account for a Virtual operation. Does not create/update/persist any entity.
     * 
     * @param subscription subscription
     * @param oneShotChargeInstance Recurring charge instance
     * @param quantity Quantity as calculated
     * @param effectiveDate Recurring charge application start
     * @return Wallet operations
     * @throws BusinessException business exception.
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public WalletOperation oneShotChargeApplicationVirtual(Subscription subscription, OneShotChargeInstance oneShotChargeInstance, Date effectiveDate, BigDecimal quantity) throws BusinessException, RatingException {

        log.debug("Apply one shot charge on Virtual operation. User account {}, offer {}, charge {}, quantity {}", oneShotChargeInstance.getUserAccount().getCode(), subscription.getOffer().getCode(),
            oneShotChargeInstance.getChargeTemplate().getCode(), quantity);

        if (!walletOperationService.isChargeMatch(oneShotChargeInstance, oneShotChargeInstance.getChargeTemplate().getFilterExpression())) {
            log.debug("not rating chargeInstance with code={}, filter expression not evaluated to true", oneShotChargeInstance.getCode());
            return null;
        }

        WalletOperation wo = walletOperationService.applyOneShotWalletOperation(subscription, oneShotChargeInstance, quantity, null, effectiveDate, true, subscription.getOrderNumber(),
            ChargeApplicationModeEnum.SUBSCRIPTION);

        oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
        return wo;
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

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
        OneShotChargeInstance matchingCharge = oneShotChargeApplication(firstActiveSubscription, (OneShotChargeTemplate) oneShotChargeTemplate, wallet.getCode(), new Date(), balanceNoTax, balanceWithTax, BigDecimal.ONE,
            null, true);
        if (matchingCharge == null) {
            throw new BusinessException("Cannot find or create matching charge instance for code " + matchingChargeCode);
        }
        OneShotChargeInstance compensationCharge = oneShotChargeApplication(firstActiveSubscription, (OneShotChargeTemplate) oneShotChargeTemplate, wallet.getCode(), new Date(), balanceNoTax.negate(),
            balanceWithTax.negate(), BigDecimal.ONE, null, true);
        if (compensationCharge == null) {
            throw new BusinessException("Cannot find or create compensating charge instance for code " + matchingChargeCode);
        }
        BigDecimal inputQuantity = BigDecimal.ONE;

        WalletOperation op = walletOperationService.applyOneShotWalletOperation(firstActiveSubscription, matchingCharge, inputQuantity, null, new Date(), false, firstActiveSubscription.getOrderNumber(),
            ChargeApplicationModeEnum.SUBSCRIPTION);
        op.changeStatus(WalletOperationStatusEnum.TREATED);

        walletOperationService.applyOneShotWalletOperation(firstActiveSubscription, compensationCharge, inputQuantity, null, new Date(), false, null, ChargeApplicationModeEnum.SUBSCRIPTION);

        // we check that balance is unchanged
        BigDecimal cacheBalance = walletService.getWalletBalance(wallet.getId());
        if (cacheBalance.compareTo(balanceWithTax) != 0) {
            log.error("Balances after prepaid matching process do not match. Balance in cache={}, balance expected={}", cacheBalance, balanceWithTax);
            throw new BusinessException("MATCHING_ERROR");
        }
    }
}
