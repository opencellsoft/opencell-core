package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.MeveoJpaForMultiTenancyForJobs;
import org.slf4j.Logger;

@Stateless
public class UsageRatingService implements Serializable {

    private static final long serialVersionUID = 1411446109227299227L;

    @Inject
    @MeveoJpaForMultiTenancyForJobs
    private EntityManager em;

    @Inject
    protected Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private RatingService ratingService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    @Inject
    private Event<CounterPeriodEvent> counterPeriodEvent;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private ReservationService reservationService;

    private Map<String, String> descriptionMap = new HashMap<>();

    // @PreDestroy
    // accessing Entity manager in predestroy is bugged in jboss7.1.3
    /*
     * void saveCounters() { for (Long key : MeveoCacheContainerProvider.getCounterCache().keySet()) { CounterInstanceCache counterInstanceCache =
     * MeveoCacheContainerProvider.getCounterCache().get(key); if (counterInstanceCache.getCounterPeriods() != null) { for (CounterPeriodCache itemPeriodCache :
     * counterInstanceCache .getCounterPeriods()) { if (itemPeriodCache.isDbDirty()) { CounterPeriod counterPeriod = em.find( CounterPeriod.class,
     * itemPeriodCache.getCounterPeriodId()); counterPeriod.setValue(itemPeriodCache.getValue()); counterPeriod.getAuditable().setUpdated(new Date()); em.merge(counterPeriod);
     * log.debug("save counter with id={}, new value={}", itemPeriodCache.getCounterPeriodId(), itemPeriodCache.getValue()); // calling ejb in this predestroy method just fail...
     * // counterInstanceService .updatePeriodValue(itemPeriodCache.getCounterPeriodId (),itemPeriodCache.getValue()); } } } } }
     */

    /**
     * This method use the price plan to rate an EDR knowing what charge must be used
     * 
     * @param walletOperation Wallet operation resulting of EDR processing
     * @param edr EDR to process
     * @param quantityToCharge Quantity to charge
     * @param usageChargeInstance Cached charge instance
     * @param userAccount User account in case of virtual operation
     * @param counterInstance Counter instance in case of virtual operation
     * @param offerCode Offer code in case of virtual operation
     * 
     * @throws BusinessException
     */
    private void rateEDRwithMatchingCharge(WalletOperation walletOperation, EDR edr, BigDecimal quantityToCharge, UsageChargeInstance usageChargeInstance, boolean isVirtual)
            throws BusinessException {

        UsageChargeInstance chargeInstance = usageChargeInstance;

        // Not a virtual operation
        Subscription subscription = edr.getSubscription();

        // For virtual operation, lookup charge in the subscription
        if (isVirtual) {
            List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
            for (ServiceInstance serviceInstance : serviceInstances) {
                List<UsageChargeInstance> usageChargeInstancesFromService = serviceInstance.getUsageChargeInstances();
                for (UsageChargeInstance usageChargeInstanceFromService : usageChargeInstancesFromService) {
                    if (usageChargeInstanceFromService.getCode().equals(usageChargeInstance.getCode())) {
                        chargeInstance = usageChargeInstanceFromService;
                        break;
                    }
                }
            }
        }

        walletOperation.setChargeInstance(chargeInstance);

        walletOperation.setSubscriptionDate(subscription.getSubscriptionDate());
        walletOperation.setOperationDate(edr.getEventDate());
        walletOperation.setParameter1(edr.getParameter1());
        walletOperation.setParameter2(edr.getParameter2());
        walletOperation.setParameter3(edr.getParameter3());
        walletOperation.setOrderNumber(chargeInstance.getOrderNumber());
        walletOperation.setEdr(edr);

        // log.debug("AKK URS line 193");
        UserAccount userAccount = chargeInstance.getUserAccount();
        BillingAccount billingAccount = userAccount.getBillingAccount();
        // log.debug("AKK URS line 196");

        TradingCountry tradingCountry = chargeInstance.getCountry();

        ChargeTemplate chargeTemplate = usageChargeInstance.getChargeTemplate();// em.find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplateId());

        // log.debug("AKK URS line 202");
        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(chargeTemplate.getInvoiceSubCategory(),
            tradingCountry, edr.getEventDate());

        if (invoiceSubcategoryCountry == null) {
            throw new BusinessException(
                "No tax defined for country=" + tradingCountry.getCountryCode() + " in invoice Sub-Category=" + chargeTemplate.getInvoiceSubCategory().getCode());
        }

        // log.debug("AKK URS line 211");
        boolean isExonerated = billingAccountService.isExonerated(billingAccount);
        // log.debug("AKK URS line 213");

        walletOperation.setSeller(chargeInstance.getSeller());

        TradingCurrency currency = chargeInstance.getCurrency();
        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), userAccount, billingAccount, null);
        }

        walletOperation.setInvoiceSubCategory(chargeTemplate.getInvoiceSubCategory());
        walletOperation.setRatingUnitDescription(usageChargeInstance.getRatingUnitDescription());
        walletOperation.setInputUnitDescription(chargeTemplate.getInputUnitDescription());

        // we set here the wallet to the principal wallet but it will later be overridden by charging algorithm
        walletOperation.setWallet(userAccount.getWallet());
        walletOperation.setBillingAccount(billingAccount);
        walletOperation.setCode(chargeTemplate.getCode());

        // log.debug("AKK URS line 232 descriptionMap is empty {}", descriptionMap.isEmpty());
        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();

        String translationKey = "CT_" + chargeTemplate.getCode() + languageCode;
        String descTranslated = descriptionMap.get(translationKey);
        if (descTranslated == null) {
            descTranslated = (usageChargeInstance.getDescription() == null) ? chargeTemplate.getDescriptionOrCode() : usageChargeInstance.getDescription();
            if (chargeTemplate.getDescriptionI18n() != null && chargeTemplate.getDescriptionI18n().get(languageCode) != null) {
                descTranslated = chargeTemplate.getDescriptionI18n().get(languageCode);
            }
            descriptionMap.put(translationKey, descTranslated);
        }

        // log.debug("AKK URS line 245");
        walletOperation.setDescription(descTranslated);

        walletOperation.setInputQuantity(quantityToCharge);
        walletOperation
            .setQuantity(NumberUtils.getInChargeUnit(quantityToCharge, chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(), chargeTemplate.getRoundingMode()));
        walletOperation.setTaxPercent(isExonerated ? BigDecimal.ZERO : tax.getPercent());
        walletOperation.setStartDate(null);
        walletOperation.setEndDate(null);
        walletOperation.setCurrency(currency.getCurrency());
        walletOperation.setStatus(WalletOperationStatusEnum.OPEN);

        walletOperation.setCounter(chargeInstance.getCounter());
        // walletOperation.setOfferCode(subscription.getOffer().getCode()); Offer code is set in walletOperation.setOfferTemplate()
        walletOperation.setOfferTemplate(subscription.getOffer());

        // log.debug("AKK URS line 261 offer id is {}", subscription.getOffer().getId());
        ratingService.rateBareWalletOperation(walletOperation, usageChargeInstance.getAmountWithoutTax(), usageChargeInstance.getAmountWithTax(), tradingCountry.getId(), currency);
        // log.debug("AKK URS line 263");
    }

    /**
     * This method first look if there is a counter and a counter period for an event date.
     * 
     * @param edr EDR to process
     * @param usageChargeInstance Usage charge instance definition
     * @param reservation Is charge event part of reservation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return if EDR quantity fits partially in the counter, returns the remaining quantity. NOTE: counter and EDR units might differ - translation is performed.
     * @throws BusinessException
     */
    private BigDecimal deduceCounter(EDR edr, UsageChargeInstance usageChargeInstance, Reservation reservation, boolean isVirtual) throws BusinessException {

        log.info("Deduce counter for counter instance {} ", isVirtual ? usageChargeInstance.getCounter().getCode() : usageChargeInstance.getCounter().getId());

        CounterPeriod counterPeriod = null;
        BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;

        // In case of virtual operation only instantiate a counter period, don't create it
        if (isVirtual) {
            counterPeriod = counterInstanceService.instantiateCounterPeriod(usageChargeInstance.getCounter().getCounterTemplate(), edr.getEventDate(),
                usageChargeInstance.getServiceInstance().getSubscriptionDate(), usageChargeInstance);

        } else {
            counterPeriod = counterInstanceService.getOrCreateCounterPeriod(usageChargeInstance.getCounter(), edr.getEventDate(),
                usageChargeInstance.getServiceInstance().getSubscriptionDate(), usageChargeInstance);
        }
        // CachedCounterPeriod cachedCounterPeriod = ratingCacheContainerProvider.getCounterPeriod(usageChargeInstance.getCounter().getId(), edr.getEventDate());

        CounterValueChangeInfo counterValueChangeInfo = null;

        UsageChargeTemplate chargeTemplate = null;
        if (usageChargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = em.find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplate().getId());
        }

        synchronized (this) {// cachedCounterPeriod) { TODO how to ensure one at a time update?
            BigDecimal deduceByQuantity = chargeTemplate.getInChargeUnit(edr.getQuantity());
            log.debug("value to deduce {} * {} = {} from current value {}", edr.getQuantity(), chargeTemplate.getUnitMultiplicator(), deduceByQuantity, counterPeriod.getValue());

            counterValueChangeInfo = counterInstanceService.deduceCounterValue(counterPeriod, deduceByQuantity, isVirtual);

            // Quantity is not tracked in counter (no initial value)
            if (counterValueChangeInfo == null) {
                deducedQuantityInEDRUnit = edr.getQuantity();

            } else if (counterValueChangeInfo.getDeltaValue().compareTo(BigDecimal.ZERO) != 0) {

                BigDecimal deducedQuantity = counterValueChangeInfo.getDeltaValue();

                // Not everything was deduced
                if (deducedQuantity.compareTo(deduceByQuantity) < 0) {
                    deducedQuantityInEDRUnit = chargeTemplate.getInEDRUnit(deducedQuantity);
                    // Everything was deduced
                } else {
                    deducedQuantityInEDRUnit = edr.getQuantity();
                }
                if (reservation != null) {
                    reservation.getCounterPeriodValues().put(counterPeriod.getId(), deducedQuantity);
                }
            }

            log.debug("in original EDR units, we deduced {}", deducedQuantityInEDRUnit);
        }

        // Fire notifications if counter value matches trigger value and counter value is tracked
        if (counterValueChangeInfo != null && counterPeriod.getNotificationLevels() != null) {
            // Need to refresh counterPeriod as it is stale object if it was updated in counterInstanceService.deduceCounterValue()
            counterPeriod = em.find(CounterPeriod.class, counterPeriod.getId());
            List<Entry<String, BigDecimal>> counterPeriodEventLevels = counterPeriod.getMatchedNotificationLevels(counterValueChangeInfo.getPreviousValue(),
                counterValueChangeInfo.getNewValue());

            if (counterPeriodEventLevels != null && !counterPeriodEventLevels.isEmpty()) {
                triggerCounterPeriodEvent(counterPeriod, counterPeriodEventLevels);
            }
        }
        return deducedQuantityInEDRUnit;
    }

    /**
     * @param counterPeriod
     * @param counterPeriodEventLevels
     */
    private void triggerCounterPeriodEvent(CounterPeriod counterPeriod, List<Entry<String, BigDecimal>> counterPeriodEventLevels) {
        for (Entry<String, BigDecimal> counterValue : counterPeriodEventLevels) {
            try {
                CounterPeriodEvent event = new CounterPeriodEvent(counterPeriod, counterValue.getValue(), counterValue.getKey());
                event.setCounterPeriod(counterPeriod);
                counterPeriodEvent.fire(event);
            } catch (Exception e) {
                log.error("Failed to executing trigger counterPeriodEvent", e);
            }
        }
    }

    /**
     * This method evaluate the EDR against the charge and its counter. If counter is present, it will be decremented by EDR content
     * 
     * @param walletOperation Wallet operation to charge against
     * @param edr EDR to rate
     * @param usageChargeInstance Charge instance to apply
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * 
     * @return returns true if the charge has been fully rated (either because it has no counter or because the counter can be fully decremented with the EDR content)
     * @throws BusinessException
     */
    private boolean rateEDRonChargeAndCounters(WalletOperation walletOperation, EDR edr, UsageChargeInstance usageChargeInstance, boolean isVirtual) throws BusinessException {
        boolean stopEDRRating = false;
        boolean stopAfterApplication = false;
        BigDecimal deducedQuantity = null;

        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedQuantity = deduceCounter(edr, usageChargeInstance, null, isVirtual);
            if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
                stopEDRRating = true;
                stopAfterApplication = true;

            } else {
                if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
                    stopEDRRating = true;
                }
            }

        } else {
            stopEDRRating = true;
        }

        if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            // we continue the rating to have a WO that its needed in pricePlan.script
            log.warn("deduceQuantity is BigDecimal.ZERO, will continue rating");
            return stopEDRRating;
        }

        BigDecimal quantityToCharge = null;
        if (deducedQuantity == null) {
            quantityToCharge = edr.getQuantity();

        } else {
            edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
            quantityToCharge = deducedQuantity;
        }

        rateEDRwithMatchingCharge(walletOperation, edr, quantityToCharge, usageChargeInstance, isVirtual);

        if (!isVirtual) {
            walletOperationService.chargeWalletOperation(walletOperation);
        }

        // handle associated edr creation unless it is a Virtual operation
        if (isVirtual) {
            return stopEDRRating;
        }

        UsageChargeTemplate chargeTemplate = null;
        if (usageChargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();

        } else {
            chargeTemplate = em.find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplate().getId());
        }

        for (TriggeredEDRTemplate triggeredEDR : chargeTemplate.getEdrTemplates()) {
            if (triggeredEDR.getConditionEl() == null || "".equals(triggeredEDR.getConditionEl()) || matchExpression(triggeredEDR.getConditionEl(), edr, walletOperation)) {
                if (triggeredEDR.getMeveoInstance() == null) {
                    EDR newEdr = new EDR();
                    newEdr.setCreated(new Date());
                    newEdr.setEventDate(edr.getEventDate());
                    newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
                    newEdr.setOriginRecord("" + walletOperation.getId());
                    newEdr.setParameter1(evaluateStringExpression(triggeredEDR.getParam1El(), edr, walletOperation));
                    newEdr.setParameter2(evaluateStringExpression(triggeredEDR.getParam2El(), edr, walletOperation));
                    newEdr.setParameter3(evaluateStringExpression(triggeredEDR.getParam3El(), edr, walletOperation));
                    newEdr.setParameter4(evaluateStringExpression(triggeredEDR.getParam4El(), edr, walletOperation));
                    newEdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDR.getQuantityEl(), edr, walletOperation)));
                    newEdr.setStatus(EDRStatusEnum.OPEN);
                    Subscription sub = edr.getSubscription();
                    if (!StringUtils.isBlank(triggeredEDR.getSubscriptionEl())) {
                        String subCode = evaluateStringExpression(triggeredEDR.getSubscriptionEl(), edr, walletOperation);
                        sub = subscriptionService.findByCode(subCode);
                        if (sub == null) {
                            throw new BusinessException("could not find subscription for code =" + subCode + " (EL=" + triggeredEDR.getSubscriptionEl()
                                    + ") in triggered EDR with code " + triggeredEDR.getCode());
                        }
                    }
                    newEdr.setSubscription(sub);
                    log.info("trigger EDR from code " + triggeredEDR.getCode());
                    edrService.create(newEdr);
                } else {
                    CDR cdr = new CDR();
                    String subCode = evaluateStringExpression(triggeredEDR.getSubscriptionEl(), edr, walletOperation);
                    cdr.setAccess_id(subCode);
                    cdr.setTimestamp(edr.getEventDate());
                    cdr.setParam1(evaluateStringExpression(triggeredEDR.getParam1El(), edr, walletOperation));
                    cdr.setParam2(evaluateStringExpression(triggeredEDR.getParam2El(), edr, walletOperation));
                    cdr.setParam3(evaluateStringExpression(triggeredEDR.getParam3El(), edr, walletOperation));
                    cdr.setParam4(evaluateStringExpression(triggeredEDR.getParam4El(), edr, walletOperation));
                    cdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDR.getQuantityEl(), edr, walletOperation)));

                    String url = "api/rest/billing/mediation/chargeCdr";
                    Response response = meveoInstanceService.callTextServiceMeveoInstance(url, triggeredEDR.getMeveoInstance(), cdr.toCsv());
                    ActionStatus actionStatus = response.readEntity(ActionStatus.class);
                    log.debug("response {}", actionStatus);

                    if (actionStatus == null || ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new BusinessException("Error charging Edr on remote instance Code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());
                    }
                }
            }
        }

        // will only happen if edr.quantity = 0
        if (stopAfterApplication) {
            stopEDRRating = true;
        }

        return stopEDRRating;
    }

    /**
     * Rate EDR and create wallet operation for reservation. If counter is used, and the quantity left in counter if less then quantity in EDR, EDR is updated with a left over
     * quantity and the remaining quantity will be covered by a next charge or EDR will be marked as rejected.
     * 
     * @param reservation Reservation
     * @param edr EDR to reserve
     * @param usageChargeInstance Associated charge
     * @return True EDR was rated fully - either no counter used, or quantity remaining in a counter was greater or equal to the quantity to rate
     * @throws BusinessException
     */
    private boolean reserveEDRonChargeAndCounters(Reservation reservation, EDR edr, UsageChargeInstance usageChargeInstance) throws BusinessException {
        boolean stopEDRRating = false;
        BigDecimal deducedQuantity = null;

        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedQuantity = deduceCounter(edr, usageChargeInstance, reservation, false);
            if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
                stopEDRRating = true;
            }
        } else {
            stopEDRRating = true;
        }

        if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("deduceQuantity is null");
            return stopEDRRating;
        }

        //
        BigDecimal quantityToCharge = null;
        if (deducedQuantity == null) {
            quantityToCharge = edr.getQuantity();
        } else {
            edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
            quantityToCharge = deducedQuantity;
        }

        WalletReservation walletOperation = new WalletReservation();

        rateEDRwithMatchingCharge(walletOperation, edr, quantityToCharge, usageChargeInstance, false);

        walletOperation.setReservation(reservation);
        walletOperation.setStatus(WalletOperationStatusEnum.RESERVED);
        reservation.setAmountWithoutTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithoutTax()));
        reservation.setAmountWithTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithTax()));

        walletOperationService.chargeWalletOperation(walletOperation);

        return stopEDRRating;
    }

    /**
     * Rate an EDR using counters if they apply
     * 
     * @param edr edr to be rated.
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void ratePostpaidUsage(EDR edr) throws BusinessException {
        rateUsageWithinTransaction(edr, false);
    }

    public List<WalletOperation> rateUsageDontChangeTransaction(EDR edr, boolean isVirtual) throws BusinessException {
        return rateUsageWithinTransaction(edr, isVirtual);
    }

    /**
     * Rate EDR
     * 
     * @param edr EDR
     * @param isVirtual Is this a virtual operation and no real counters or wallets should be affected (applies to quotes)
     * @return A list of wallet operations corresponding to rated EDR
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<WalletOperation> rateUsageWithinTransaction(EDR edr, boolean isVirtual) throws BusinessException {

        log.info("Rating EDR={}", edr);

        if (edr.getQuantity() == null) {
            edr.setStatus(EDRStatusEnum.REJECTED);
            edr.setRejectReason("NULL_QUANTITY");
            return null;
        }

        if (edr.getSubscription() == null) {
            edr.setStatus(EDRStatusEnum.REJECTED);
            edr.setRejectReason("NULL_SUBSCRIPTION");
            return null;
        }

        edr.setLastUpdate(new Date());

        boolean edrIsRated = false;

        BigDecimal originalQuantity = edr.getQuantity();
        List<WalletOperation> walletOperations = new ArrayList<>();

        try {
            List<UsageChargeInstance> usageChargeInstances = null;

            if (!isVirtual) {
                // Charges should be already ordered by priority and id (why id??)
                usageChargeInstances = usageChargeInstanceService.getActiveUsageChargeInstancesBySubscriptionId(edr.getSubscription().getId());
                if (usageChargeInstances == null || usageChargeInstances.isEmpty()) {
                    edr.setStatus(EDRStatusEnum.REJECTED);
                    edr.setRejectReason("SUBSCRIPTION_HAS_NO_CHARGE");
                    return null;
                }

            } else {
                // TODO still need to obtain charges as subscription is virtual
            }

            boolean foundPricePlan = true;

            // Find the first matching charge and rate it
            for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {

                log.debug("try to rate with charge {}", usageChargeInstance.getCode());
                try {

                    if (!isChargeMatch(usageChargeInstance, edr, true)) {
                        continue;
                    }

                } catch (ChargeWitoutPricePlanException e) {
                    log.debug("Charge {} was matched but does not contain a priceplan", usageChargeInstance.getCode());
                    foundPricePlan = false;
                    continue;
                }

                log.debug("Found matching charge instance: id=" + usageChargeInstance.getId());

                WalletOperation walletOperation = new WalletOperation();
                edrIsRated = rateEDRonChargeAndCounters(walletOperation, edr, usageChargeInstance, isVirtual);
                walletOperations.add(walletOperation);
                if (edrIsRated) {
                    edr.setStatus(EDRStatusEnum.RATED);
                    break;
                }
            }

            if (!edrIsRated) {
                edr.setStatus(EDRStatusEnum.REJECTED);
                edr.setRejectReason(!foundPricePlan ? "NO_PRICEPLAN" : "NO_MATCHING_CHARGE");
                return null;
            }

        } catch (BusinessException e) {
            if (e instanceof InsufficientBalanceException) {
                log.error("failed to rate usage Within Transaction: {}", e.getMessage());
            } else {
                log.error("failed to rate usage Within Transaction", e);
            }
            edr.setStatus(EDRStatusEnum.REJECTED);
            edr.setRejectReason((e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            throw e;

        } finally {
            // put back the original quantity in edr (could have been decrease by counters)
            edr.setQuantity(originalQuantity);
        }
        return walletOperations;
    }

    /**
     * Check if charge filter parameters match those of EDR. Optionally checks if charge has a priceplan associated
     * 
     * @param chargeInstance Charge instance to match against EDR
     * @param edr EDR to check
     * @param requirePP Require that charge has a priceplan associated
     * @return true if charge is matched
     * @throws BusinessException business exception.
     * @throws ChargeWitoutPricePlanException If charge has no price plan associated
     */
    private boolean isChargeMatch(UsageChargeInstance chargeInstance, EDR edr, boolean requirePP) throws BusinessException, ChargeWitoutPricePlanException {

        long startDate = System.currentTimeMillis();
        UsageChargeTemplate chargeTemplate = null;
        if (chargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) chargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = em.find(UsageChargeTemplate.class, chargeInstance.getChargeTemplate().getId());
        }

        String filter1 = chargeTemplate.getFilterParam1();

        log.debug("Retrieved ChargeTemplate in: {}", (System.currentTimeMillis() - startDate));

        if (filter1 == null || filter1.equals(edr.getParameter1())) {
            String filter2 = chargeTemplate.getFilterParam2();
            if (filter2 == null || filter2.equals(edr.getParameter2())) {
                String filter3 = chargeTemplate.getFilterParam3();
                if (filter3 == null || filter3.equals(edr.getParameter3())) {
                    String filter4 = chargeTemplate.getFilterParam4();
                    if (filter4 == null || filter4.equals(edr.getParameter4())) {
                        String filterExpression = chargeTemplate.getFilterExpression();
                        if (filterExpression == null || matchExpression(chargeInstance, filterExpression, edr)) {

                            if (requirePP) {
                                String chargeCode = chargeTemplate.getCode();
                                startDate = System.currentTimeMillis();
                                List<PricePlanMatrix> chargePricePlans = pricePlanMatrixService.getActivePricePlansByChargeCode(chargeCode);
                                log.debug("Retrieved PPs in: {}", (System.currentTimeMillis() - startDate));
                                if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                                    throw new ChargeWitoutPricePlanException(chargeCode);
                                }
                            }
                            return true;
                        }
                    } else {
                        log.trace("filter 4 not matched");
                    }
                } else {
                    log.trace("filter 3 not matched");
                }
            } else {
                log.trace("filter 2 not matched");
            }
        } else {
            log.trace("filter 1 not matched");
        }
        return false;
    }

    /**
     * Rate EDR as reservation.
     * 
     * @param edr EDR to rate
     * @return Reservation
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Reservation reserveUsageWithinTransaction(EDR edr) throws BusinessException {

        Reservation reservation = null;
        BigDecimal originalQuantity = edr.getQuantity();

        long time = System.currentTimeMillis();
        log.debug("Reserving EDR={}, we override the event date with the current date", edr);
        edr.setEventDate(new Date(time));

        if (edr.getSubscription() == null) {
            edr.setStatus(EDRStatusEnum.REJECTED);
            edr.setRejectReason("SUBSCRIPTION_IS_NULL");
        } else {
            boolean edrIsRated = false;

            try {
                // Charges are ordered by priority and id
                List<UsageChargeInstance> charges = usageChargeInstanceService.getActiveUsageChargeInstancesBySubscriptionId(edr.getSubscription().getId());
                if (charges != null && !charges.isEmpty()) {

                    reservation = new Reservation();
                    reservation.setReservationDate(edr.getEventDate());
                    reservation.setExpiryDate(new Date(time + appProvider.getPrepaidReservationExpirationDelayinMillisec()));
                    reservation.setStatus(ReservationStatus.OPEN);
                    reservation.updateAudit(currentUser);
                    reservation.setOriginEdr(edr);
                    reservation.setQuantity(edr.getQuantity());
                    // it would be nice to have a persistence context bound to
                    // // the JTA transaction
                    // em.persist(reservation);
                    reservationService.create(reservation);

                    UsageChargeTemplate chargeTemplate = null;
                    for (UsageChargeInstance usageChargeInstance : charges) {

                        chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
                        log.trace("Try  templateCache {}", chargeTemplate.getCode());
                        if (isChargeMatch(usageChargeInstance, edr, true)) {

                            log.debug("found matching charge inst : id {}", usageChargeInstance.getId());
                            edrIsRated = reserveEDRonChargeAndCounters(reservation, edr, usageChargeInstance);
                            if (edrIsRated) {
                                edr.setStatus(EDRStatusEnum.RATED);
                                break;
                            }
                        }
                    }

                    if (!edrIsRated) {
                        edr.setStatus(EDRStatusEnum.REJECTED);
                        edr.setRejectReason("NO_MATCHING_CHARGE");
                    }
                } else {
                    edr.setStatus(EDRStatusEnum.REJECTED);
                    edr.setRejectReason("SUBSCRIPTION_HAS_NO_CHARGE");
                }
            } catch (Exception e) {
                edr.setStatus(EDRStatusEnum.REJECTED);
                edr.setRejectReason(e.getMessage());
                throw new BusinessException(e.getMessage());
            }
        }

        // put back the original quantity in edr (could have been decrease by
        // counters)
        edr.setQuantity(originalQuantity);
        edr.setLastUpdate(new Date());
        return reservation;
    }

    private boolean matchExpression(UsageChargeInstance ci, String expression, EDR edr) throws BusinessException {
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("ci", ci);
        return ValueExpressionWrapper.evaluateToBoolean(expression, userMap);
    }

    private boolean matchExpression(String expression, EDR edr, WalletOperation walletOperation) throws BusinessException {
        boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            ServiceInstance service = null;
            if (walletOperation.getChargeInstance() instanceof UsageChargeInstance) {
                service = ((UsageChargeInstance) walletOperation.getChargeInstance()).getServiceInstance();
                userMap.put("serviceInstance", service);
            }
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param edr element description record.
     * @param walletOperation wallet operation
     * @return evaluated value
     * @throws BusinessException business exception.
     */
    private String evaluateStringExpression(String expression, EDR edr, WalletOperation walletOperation) throws BusinessException {
        if (expression == null) {
            return null;
        }
        String result = null;
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            ServiceInstance service = null;
            if (walletOperation.getChargeInstance() instanceof UsageChargeInstance) {
                service = ((UsageChargeInstance) walletOperation.getChargeInstance()).getServiceInstance();
                userMap.put("serviceInstance", service);
            }
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param edr instance of EDR
     * @param walletOperation wallet operation
     * @return evaluated value
     * @throws BusinessException business exception
     */
    private Double evaluateDoubleExpression(String expression, EDR edr, WalletOperation walletOperation) throws BusinessException {
        Double result = null;
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            ServiceInstance service = null;
            if (walletOperation.getChargeInstance() instanceof UsageChargeInstance) {
                service = ((UsageChargeInstance) walletOperation.getChargeInstance()).getServiceInstance();
                userMap.put("serviceInstance", service);
            }
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        try {
            result = (Double) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
        }

        return result;
    }
}