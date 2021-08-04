package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ChargingEdrOnRemoteInstanceErrorException;
import org.meveo.admin.exception.NoChargeException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.SubscriptionNotFoundException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRRejectReasonEnum;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.rating.RatedEDRResult;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.script.catalog.TriggeredEdrScriptService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * UsageRatingService
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UsageRatingService implements Serializable {

    private static final long serialVersionUID = 1411446109227299227L;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

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
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private ReservationService reservationService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private TriggeredEdrScriptService triggeredEdrScriptService;

    @EJB
    private UsageRatingService usageRatingServiceNewTX;

    @Inject
    @Rejected
    private Event<Serializable> rejectedEdrProducer;

    @Inject
    private TaxService taxService;

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
     * Rate EDR using a given charge - instantiates a WalletOperation. DOES not persist wallet operation to DB.
     * 
     * @param edr EDR to process
     * @param quantityToCharge Quantity to charge
     * @param usageChargeInstance Cached charge instance
     * @param isReservation Is this a reservation type EDR - will return WalletReservation instead of WalletOperation
     * @param isVirtual Is this a virtual operation - charges are looked up from subscription
     * @return Wallet operation or reservation resulting of EDR processing
     * @throws BusinessException Business exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private WalletOperation rateEDRwithMatchingCharge(EDR edr, BigDecimal quantityToCharge, UsageChargeInstance usageChargeInstance, boolean isReservation, boolean isVirtual)
            throws BusinessException, RatingException {

        UsageChargeInstance chargeInstance = usageChargeInstance;

        Subscription subscription = subscriptionService.retrieveIfNotManaged(edr.getSubscription());

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

        Tax tax = null;

        BillingAccount billingAccount = chargeInstance.getUserAccount().getBillingAccount();
        if (billingAccountService.isExonerated(billingAccount)) {
            tax = taxService.getZeroTax();
        } else {
            tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, edr.getEventDate());
        }

        WalletOperation walletOperation = null;

        if (isReservation) {
            walletOperation = new WalletReservation(chargeInstance, quantityToCharge, null, edr.getEventDate(), chargeInstance.getOrderNumber(), edr.getParameter1(),
                edr.getParameter2(), edr.getParameter3(), edr.getParameter4(), tax, null, null);
        } else {
            // we set here the wallet to the principal wallet but it will later be overridden by charging algorithm
            walletOperation = new WalletOperation(chargeInstance, quantityToCharge, null, edr.getEventDate(), chargeInstance.getOrderNumber(), edr.getParameter1(),
                edr.getParameter2(), edr.getParameter3(), edr.getParameter4(), tax, null, null);
        }

        walletOperation.setEdr(edr);

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();// em.find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplateId());

        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();

        String translationKey = "CT_" + chargeTemplate.getCode() + languageCode;
        String descTranslated = descriptionMap.get(translationKey);
        if (descTranslated == null) {
            descTranslated = (chargeInstance.getDescription() == null) ? chargeTemplate.getDescriptionOrCode() : chargeInstance.getDescription();
            if (chargeTemplate.getDescriptionI18n() != null && chargeTemplate.getDescriptionI18n().get(languageCode) != null) {
                descTranslated = chargeTemplate.getDescriptionI18n().get(languageCode);
            }
            descriptionMap.put(translationKey, descTranslated);
        }

        walletOperation.setDescription(descTranslated);

        ratingService.rateBareWalletOperation(walletOperation, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), chargeInstance.getCountry().getId(),
            chargeInstance.getCurrency());

        return walletOperation;
    }

    /**
     * This method first look if there is a counter and a counter period for an event date.
     * 
     * @param edr EDR to process
     * @param usageChargeInstance Usage charge instance definition
     * @param reservation Is charge event part of reservation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return if EDR quantity fits partially in the counter, returns the remaining quantity. NOTE: counter and EDR units might differ - translation is performed.
     * @throws BusinessException Business exception
     */
    private BigDecimal deduceCounter(EDR edr, UsageChargeInstance usageChargeInstance, Reservation reservation, boolean isVirtual) throws BusinessException {

        CounterPeriod counterPeriod = null;
        BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;

        // In case of virtual operation only instantiate a counter period, don't create it
        if (isVirtual) {
            counterPeriod = counterInstanceService.instantiateCounterPeriod(usageChargeInstance.getCounter().getCounterTemplate(), edr.getEventDate(),
                usageChargeInstance.getServiceInstance().getSubscriptionDate(), usageChargeInstance, usageChargeInstance.getServiceInstance());

        } else {
            counterPeriod = counterInstanceService.getOrCreateCounterPeriod(usageChargeInstance.getCounter(), edr.getEventDate(),
                usageChargeInstance.getServiceInstance().getSubscriptionDate(), usageChargeInstance, usageChargeInstance.getServiceInstance());
        }
        // CachedCounterPeriod cachedCounterPeriod = ratingCacheContainerProvider.getCounterPeriod(usageChargeInstance.getCounter().getId(), edr.getEventDate());

        if (counterPeriod == null) {
            return BigDecimal.ZERO;
        }

        CounterValueChangeInfo counterValueChangeInfo = null;

        UsageChargeTemplate chargeTemplate = null;
        if (usageChargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = getEntityManager().find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplate().getId());
        }

        BigDecimal quantityToDeduce = chargeTemplate.getInChargeUnit(edr.getQuantityLeftToRate());
        log.trace("Deduce counter instance {} current value {} by  {} * {} = {} ",
            isVirtual ? usageChargeInstance.getCounter().getCode() : usageChargeInstance.getCounter().getId(), counterPeriod.getValue(), edr.getQuantityLeftToRate(),
            chargeTemplate.getUnitMultiplicator(), quantityToDeduce);

        // synchronized (this) {// cachedCounterPeriod) { TODO how to ensure one at a time update?
        counterValueChangeInfo = counterInstanceService.deduceCounterValue(counterPeriod, quantityToDeduce, isVirtual);
        // }
        // Quantity is not tracked in counter (no initial value)
        if (counterValueChangeInfo == null) {
            deducedQuantityInEDRUnit = edr.getQuantityLeftToRate();

        } else if (counterValueChangeInfo.getDeltaValue().compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal deducedQuantity = counterValueChangeInfo.getDeltaValue();

            // Not everything was deduced
            if (deducedQuantity.compareTo(quantityToDeduce) < 0) {
                deducedQuantityInEDRUnit = chargeTemplate.getInEDRUnit(deducedQuantity);
                // Everything was deduced
            } else {
                deducedQuantityInEDRUnit = edr.getQuantityLeftToRate();
            }
            if (reservation != null) {
                reservation.getCounterPeriodValues().put(counterPeriod.getId(), deducedQuantity);
            }
        }

        log.trace("In original EDR units EDR {} deduced by {}", edr.getId(), deducedQuantityInEDRUnit);

        // Fire notifications if counter value matches trigger value and counter value is tracked
        if (counterValueChangeInfo != null && counterPeriod.getNotificationLevels() != null) {
            // Need to refresh counterPeriod as it is stale object if it was updated in counterInstanceService.deduceCounterValue()
            counterPeriod = getEntityManager().find(CounterPeriod.class, counterPeriod.getId());
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
     * @param edr EDR to rate
     * @param usageChargeInstance Charge instance to apply
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     *
     * @return returns an RatedEDRResult object, the RatedEDRResult.eDRfullyRated is true if the charge has been fully rated (either because it has no counter or because the
     *         counter can be fully decremented with the EDR content)
     * @throws BusinessException Business exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private RatedEDRResult rateEDRonChargeAndCounters(EDR edr, UsageChargeInstance usageChargeInstance, boolean isVirtual) throws BusinessException, RatingException {
        // boolean stopEDRRating_fullyRated = false;
        RatedEDRResult ratedEDRResult = new RatedEDRResult();
        BigDecimal deducedQuantity = null;

        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedQuantity = deduceCounter(edr, usageChargeInstance, null, isVirtual);
            if (edr.getQuantityLeftToRate().compareTo(deducedQuantity) == 0) {
                ratedEDRResult.setEDRfullyRated(true);
            }

            if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
                // we continue the rating to have a WO that its needed in pricePlan.script
                return ratedEDRResult;
            }
        } else {
            ratedEDRResult.setEDRfullyRated(true);
        }

        BigDecimal quantityToCharge = null;
        if (deducedQuantity == null) {
            quantityToCharge = edr.getQuantityLeftToRate();

        } else {
            edr.deduceQuantityLeftToRate(deducedQuantity);
            quantityToCharge = deducedQuantity;
        }

        WalletOperation walletOperation = rateEDRwithMatchingCharge(edr, quantityToCharge, usageChargeInstance, false, false);
        ratedEDRResult.setWalletOperation(walletOperation);
        if (!isVirtual) {
            walletOperationService.chargeWalletOperation(walletOperation);
        }

        UsageChargeTemplate chargeTemplate = null;
        if (usageChargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();

        } else {
            chargeTemplate = getEntityManager().find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplate().getId());
        }

        List<EDR> triggeredEdrs = triggerEDRs(chargeTemplate, walletOperation, edr, isVirtual);
        ratedEDRResult.setTriggeredEDRs(triggeredEdrs);
        return ratedEDRResult;
    }

    /**
     * Create a new EDR if charge has triggerEDRTemplate.
     * 
     * @param chargeTemplate template charge
     * @param walletOperation the wallet operation
     * @param edr the event record
     * @param isVirtual do not persist EDR if isVirtual = true
     * @return an EDR
     * @throws BusinessException business exception
     * @throws ChargingEdrOnRemoteInstanceErrorException Failure to communicate with a remote Opencell instance
     */
    private List<EDR> triggerEDRs(ChargeTemplate chargeTemplate, WalletOperation walletOperation, EDR edr, boolean isVirtual)
            throws BusinessException, ChargingEdrOnRemoteInstanceErrorException {
        List<EDR> triggredEDRs = new ArrayList<>();

        EntityManager em = getEntityManager();

        for (TriggeredEDRTemplate triggeredEDRTemplate : chargeTemplate.getEdrTemplates()) {
            if (triggeredEDRTemplate.getConditionEl() == null || "".equals(triggeredEDRTemplate.getConditionEl())
                    || evaluateBooleanExpression(triggeredEDRTemplate.getConditionEl(), edr, walletOperation)) {

                MeveoInstance meveoInstance = null;

                if (triggeredEDRTemplate.getMeveoInstance() != null) {
                    meveoInstance = triggeredEDRTemplate.getMeveoInstance();
                }
                if (!StringUtils.isBlank(triggeredEDRTemplate.getOpencellInstanceEL())) {
                    String opencellInstanceCode = evaluateStringExpression(triggeredEDRTemplate.getOpencellInstanceEL(), edr, walletOperation);
                    meveoInstance = meveoInstanceService.findByCode(opencellInstanceCode);
                }

                log.debug("Will trigger EDR {} for EDR {} / WO {}", triggeredEDRTemplate.getCode(), edr.getId(), walletOperation.getId());

                if (meveoInstance == null) {
                    EDR newEdr = new EDR();
                    newEdr.setCreated(new Date());
                    newEdr.setEventDate(edr.getEventDate());
                    newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
                    newEdr.setOriginRecord("" + walletOperation.getId());
                    newEdr.setParameter1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), edr, walletOperation));
                    newEdr.setParameter2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), edr, walletOperation));
                    newEdr.setParameter3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), edr, walletOperation));
                    newEdr.setParameter4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), edr, walletOperation));
                    newEdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), edr, walletOperation)));

                    Subscription sub = null;

                    if (!StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), edr, walletOperation);
                        sub = subscriptionService.findByCode(subCode);
                        if (sub == null) {
                            throw new SubscriptionNotFoundException("could not find subscription for code =" + subCode + " (EL=" + triggeredEDRTemplate.getSubscriptionEl()
                                    + ") in triggered EDR with code " + triggeredEDRTemplate.getCode());
                        }
                    } else {
                        sub = em.getReference(Subscription.class, edr.getSubscription().getId());
                    }
                    newEdr.setSubscription(sub);

                    if (triggeredEDRTemplate.getTriggeredEdrScript() != null) {
                        newEdr = triggeredEdrScriptService.updateEdr(triggeredEDRTemplate.getTriggeredEdrScript().getCode(), newEdr, walletOperation);
                    }
                    if (!isVirtual) {
                        edrService.create(newEdr);
                    }
                    triggredEDRs.add(newEdr);
                } else {
                    if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
                        throw new BusinessException("TriggeredEDRTemplate.subscriptionEl must not be null and must point to an existing Access.");
                    }

                    CDR cdr = new CDR();
                    String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), edr, walletOperation);
                    cdr.setAccess_id(subCode);
                    cdr.setTimestamp(edr.getEventDate());
                    cdr.setParam1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), edr, walletOperation));
                    cdr.setParam2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), edr, walletOperation));
                    cdr.setParam3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), edr, walletOperation));
                    cdr.setParam4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), edr, walletOperation));
                    cdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(), edr, walletOperation)));

                    String url = "api/rest/billing/mediation/chargeCdr";
                    Response response = meveoInstanceService.callTextServiceMeveoInstance(url, meveoInstance, cdr.toCsv());
                    ActionStatus actionStatus = response.readEntity(ActionStatus.class);
                    log.trace("Triggered remote EDR response {}", actionStatus);

                    if (actionStatus != null && ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new ChargingEdrOnRemoteInstanceErrorException(
                            "Error charging EDR. Error code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());

                    } else if (actionStatus == null) {
                        throw new ChargingEdrOnRemoteInstanceErrorException("Error charging Edr. No response code from API.");
                    }
                }
            }
        }
        return triggredEDRs;
    }

    /**
     * Rate EDR and create wallet operation for reservation. If counter is used, and the quantity left in counter if less then quantity in EDR, EDR is updated with a left over
     * quantity and the remaining quantity will be covered by a next charge or EDR will be marked as rejected.
     * 
     * @param reservation Reservation
     * @param edr EDR to reserve
     * @param usageChargeInstance Associated charge
     * @return True EDR was rated fully - either no counter used, or quantity remaining in a counter was greater or equal to the quantity to rate
     * @throws BusinessException Business exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private boolean reserveEDRonChargeAndCounters(Reservation reservation, EDR edr, UsageChargeInstance usageChargeInstance) throws BusinessException, RatingException {
        boolean stopEDRRating = false;
        BigDecimal deducedQuantity = null;

        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedQuantity = deduceCounter(edr, usageChargeInstance, reservation, false);
            if (edr.getQuantityLeftToRate().compareTo(deducedQuantity) == 0) {
                stopEDRRating = true;
            }
        } else {
            stopEDRRating = true;
        }

        if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return stopEDRRating;
        }

        //
        BigDecimal quantityToCharge = null;
        if (deducedQuantity == null) {
            quantityToCharge = edr.getQuantityLeftToRate();
        } else {
            edr.deduceQuantityLeftToRate(deducedQuantity);
            quantityToCharge = deducedQuantity;
        }

        WalletReservation walletOperation = (WalletReservation) rateEDRwithMatchingCharge(edr, quantityToCharge, usageChargeInstance, true, false);

        walletOperation.setReservation(reservation);
        reservation.setAmountWithoutTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithoutTax()));
        reservation.setAmountWithTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithTax()));

        walletOperationService.chargeWalletOperation(walletOperation);

        return stopEDRRating;
    }

    /**
     * Rate an EDR using counters if they apply. EDR status will be updated to Rejected even if exception is thrown
     * 
     * @param edr EDR to rate
     * @throws BusinessException business exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void ratePostpaidUsage(Long edrId) throws BusinessException, RatingException {

        try {
            usageRatingServiceNewTX.rateUsageInNewTransaction(edrId, false, 0, 0);

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edrId, e.getRejectionReason());
            usageRatingServiceNewTX.rejectEDR(edrId, e);
            throw e;

        } catch (BusinessException e) {
            log.error("Failed to rate EDR {}: {}", edrId, e.getMessage(), e);
            usageRatingServiceNewTX.rejectEDR(edrId, e);
            throw e;
        }
    }

    /**
     * Rate a virtual EDR. Data will not be persisted
     * 
     * @param edr EDR to rate
     * @return A list of wallet operations
     * @throws BusinessException A general exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> rateVirtualEDR(EDR edr) throws BusinessException, RatingException {
        return rateUsageWithinTransaction(edr, true, false, 0, 0);
    }

    /**
     * Rate EDR in a new transaction
     * 
     * @param edrId EDR id
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @return A list of wallet operations corresponding to rated EDR
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<WalletOperation> rateUsageInNewTransaction(Long edrId, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth) throws BusinessException, RatingException {

        EDR edr = getEntityManager().find(EDR.class, edrId);
        return rateUsageWithinTransaction(edr, false, rateTriggeredEdr, maxDeep, currentRatingDepth);
    }

    /**
     * Rate EDR
     * 
     * @param edr EDR
     * @param isVirtual Is this a virtual operation and no real counters or wallets should be affected (applies to quotes)
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @return A list of wallet operations corresponding to rated EDR
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<WalletOperation> rateUsageWithinTransaction(EDR edr, boolean isVirtual, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth)
            throws BusinessException, RatingException {

        log.debug("Rating EDR={}", edr);
        List<WalletOperation> walletOperations = new ArrayList<>();

        try {
            if (edr.getQuantity() == null) {
                throw new RatingException(EDRRejectReasonEnum.QUANTITY_IS_NULL);
            }

            if (edr.getSubscription() == null) {
                throw new RatingException(EDRRejectReasonEnum.SUBSCRIPTION_IS_NULL);
            }

            // edr.setLastUpdate(new Date());

            RatedEDRResult ratedEDRResult = new RatedEDRResult();

            List<UsageChargeInstance> usageChargeInstances = null;

            // Charges should be already ordered by priority and id (why id??)
            usageChargeInstances = usageChargeInstanceService.getUsageChargeInstancesValidForDateBySubscriptionId(edr.getSubscription().getId(), edr.getEventDate());
            if (usageChargeInstances == null || usageChargeInstances.isEmpty()) {
                throw new NoChargeException("No active usage charges are associated with subscription " + edr.getSubscription().getId());
            }

            boolean foundPricePlan = true;

            // Find the first matching charge and rate it
            for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {

                log.trace("Try to rate EDR {} with charge {}", edr.getId(), usageChargeInstance.getCode());
                try {

                    if (!isChargeMatch(usageChargeInstance, edr, true)) {
                        continue;
                    }

                } catch (NoPricePlanException e) {
                    log.debug("Charge {} was matched for EDR {} but does not contain a priceplan", usageChargeInstance.getCode(), edr.getId());
                    foundPricePlan = false;
                    continue;
                }

                log.debug("Will apply matching charge instance id={} for EDR {}", usageChargeInstance.getId(), edr.getId());

                ratedEDRResult = rateEDRonChargeAndCounters(edr, usageChargeInstance, isVirtual);
                if (ratedEDRResult.getWalletOperation() != null) {
                    walletOperations.add(ratedEDRResult.getWalletOperation());
                }

                if (rateTriggeredEdr && !ratedEDRResult.getTriggeredEDRs().isEmpty()) {
                    walletOperations.addAll(rateTriggeredEDRs(isVirtual, rateTriggeredEdr, maxDeep, currentRatingDepth, ratedEDRResult.getTriggeredEDRs()));
                }

                if (ratedEDRResult.isEDRfullyRated()) {
                    boolean triggerNextCharge = false;
                    UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService.findById(usageChargeInstance.getChargeTemplate().getId());

                    if (usageChargeTemplate.getTriggerNextCharge() != null) {
                        triggerNextCharge = usageChargeTemplate.getTriggerNextCharge();
                    }
                    if (!StringUtils.isBlank(usageChargeTemplate.getTriggerNextChargeEL())) {
                        triggerNextCharge = evaluateBooleanExpression(usageChargeTemplate.getTriggerNextChargeEL(), edr, ratedEDRResult.getWalletOperation());
                    }
                    if (!triggerNextCharge) {
                        break;
                    }
                }
            }

            if (ratedEDRResult.isEDRfullyRated()) {
                edr.changeStatus(EDRStatusEnum.RATED);

            } else if (!foundPricePlan) {
                throw new NoPricePlanException("At least one charge was matched but did not contain an applicable price plan for EDR " + edr.getId());

            } else {
                throw new NoChargeException(EDRRejectReasonEnum.NO_MATCHING_CHARGE, "No charge matched for EDR " + edr.getId());
            }

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
            throw e;

        } catch (BusinessException e) {
            log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
            throw e;
        }
        return walletOperations;
    }

    /**
     * Rate Triggered EDR.
     *
     * @param isVirtual rate EDR virtually (no persisting in DB)
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @param triggeredEDRs EDRs that were triggered by a charge processing
     * @return a list of WalletOperation
     * @throws BusinessException General business exception
     */
    private List<WalletOperation> rateTriggeredEDRs(boolean isVirtual, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth, List<EDR> triggeredEDRs)
            throws BusinessException {
        List<WalletOperation> triggeredWOs = new ArrayList<>();
        if (rateTriggeredEdr && currentRatingDepth < maxDeep) {
            for (EDR edr : triggeredEDRs) {
                // Ignore errors, as triggered EDR was saved already, and will be rated again later ?? AKK? Is this valid assumption?
                try {
                    triggeredWOs.addAll(rateUsageWithinTransaction(edr, isVirtual, true, maxDeep, currentRatingDepth + 1));
                } catch (RatingException e) {
                    log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());

                } catch (BusinessException e) {
                    log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
                }
            }
        }
        return triggeredWOs;
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
    private boolean isChargeMatch(UsageChargeInstance chargeInstance, EDR edr, boolean requirePP) throws BusinessException, NoPricePlanException {

        UsageChargeTemplate chargeTemplate = null;
        if (chargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) chargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = getEntityManager().find(UsageChargeTemplate.class, chargeInstance.getChargeTemplate().getId());
        }

        String filter1 = chargeTemplate.getFilterParam1();

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
                                List<PricePlanMatrix> chargePricePlans = pricePlanMatrixService.getActivePricePlansByChargeCode(chargeCode);
                                if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                                    throw new NoPricePlanException("Charge " + chargeCode + " has no price plan defined");
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
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Reservation reserveUsageWithinTransaction(EDR edr) throws BusinessException, RatingException {

        Reservation reservation = null;

        long time = System.currentTimeMillis();
        log.debug("Reserving EDR={}, we override the event date with the current date", edr);
        edr.setEventDate(new Date(time));

        if (edr.getSubscription() == null) {
            edr.setRatingRejectionReason(EDRRejectReasonEnum.SUBSCRIPTION_IS_NULL.getCode());
            return null;
        }

        boolean edrIsRated = false;

        // Charges are ordered by priority and id
        List<UsageChargeInstance> charges = usageChargeInstanceService.getActiveUsageChargeInstancesBySubscriptionId(edr.getSubscription().getId());
        if (charges == null || charges.isEmpty()) {
            edr.setRatingRejectionReason(EDRRejectReasonEnum.SUBSCRIPTION_HAS_NO_CHARGE.getCode());
        }
        reservation = new Reservation();
        reservation.setReservationDate(edr.getEventDate());
        reservation.setExpiryDate(new Date(time + appProvider.getPrepaidReservationExpirationDelayinMillisec()));
        reservation.setStatus(ReservationStatus.OPEN);
        reservation.updateAudit(currentUser);
        reservation.setOriginEdr(edr);
        reservation.setQuantity(edr.getQuantity());

        reservationService.create(reservation);

        UsageChargeTemplate chargeTemplate = null;
        for (UsageChargeInstance usageChargeInstance : charges) {

            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
            log.trace("Try  templateCache {}", chargeTemplate.getCode());
            try {
                if (isChargeMatch(usageChargeInstance, edr, true)) {

                    log.debug("found matching charge inst : id {}", usageChargeInstance.getId());
                    edrIsRated = reserveEDRonChargeAndCounters(reservation, edr, usageChargeInstance);
                    if (edrIsRated) {
                        edr.changeStatus(EDRStatusEnum.RATED);
                        break;
                    }
                }
            } catch (NoPricePlanException e) {
                continue;
            }
        }

        if (!edrIsRated) {
            edr.setRatingRejectionReason(EDRRejectReasonEnum.NO_MATCHING_CHARGE.getCode());
        }

        return reservation;
    }

    private boolean matchExpression(UsageChargeInstance ci, String expression, EDR edr) throws BusinessException {
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("ci", ci);
        return ValueExpressionWrapper.evaluateToBoolean(expression, userMap);
    }

    private boolean evaluateBooleanExpression(String expression, EDR edr, WalletOperation walletOperation) throws BusinessException {
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
            userMap.put("serviceInstance", walletOperation.getServiceInstance());
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
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            userMap.put("serviceInstance", walletOperation.getServiceInstance());
        }

        String result = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
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

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            userMap.put("serviceInstance", walletOperation.getServiceInstance());
        }

        Double result = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        return result;
    }

    /**
     * @return Get entity manager
     */
    private EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void rejectEDR(Long edrId, Exception e) {
        String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);

        EDR edr = edrService.findById(edrId);
        edr.changeStatus(EDRStatusEnum.REJECTED);
        edr.setRejectReason(rejectReason);

        rejectedEdrProducer.fire(edr);
    }
}