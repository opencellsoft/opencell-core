package org.meveo.service.billing.impl;

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
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.admin.util.NumberUtil;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
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
import org.meveo.model.cache.CachedCounterInstance;
import org.meveo.model.cache.CachedCounterPeriod;
import org.meveo.model.cache.CachedTriggeredEDR;
import org.meveo.model.cache.CachedUsageChargeInstance;
import org.meveo.model.cache.CachedUsageChargeTemplate;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
public class UsageRatingService {

    @PersistenceContext(unitName = "MeveoAdmin")
    @MeveoJpa
    protected EntityManager em;

    @Inject
    protected Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private RatingService ratingService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    @Inject
    private CounterPeriodService counterPeriodService;

    @Inject
    private Event<CounterPeriodEvent> counterPeriodEvent;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	

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
     * @param cachedChargeInstance Cached charge instance
     * @param userAccount User account in case of virtual operation
     * @param counterInstance Counter instance in case of virtual operation
     * @param offerCode Offer code in case of virtual operation
     * @param currentUser Current user
     * @throws BusinessException
     */
    private void rateEDRwithMatchingCharge(WalletOperation walletOperation, EDR edr, BigDecimal quantityToCharge, CachedUsageChargeInstance cachedChargeInstance,
            boolean isVirtual, User currentUser) throws BusinessException {

        UsageChargeInstance chargeInstance = null;
        
        // Not a virtual operation
        if (!isVirtual) {
            chargeInstance = usageChargeInstanceService.findById(cachedChargeInstance.getId());            
        
        // For virtual operation, lookup charge in the subscription
        } else {
            for (ServiceInstance serviceInstance : edr.getSubscription().getServiceInstances()) {
                for(UsageChargeInstance usageChargeInstance: serviceInstance.getUsageChargeInstances()){
                    if (usageChargeInstance.getCode().equals(cachedChargeInstance.getChargeTemplate().getCode())){
                        chargeInstance = usageChargeInstance;
                        break;
                    }
                }
            }            
        }
        walletOperation.setChargeInstance(chargeInstance);
        UserAccount userAccount = chargeInstance.getUserAccount();
        CounterInstance counterInstance = chargeInstance.getCounter();
        String offerCode = edr.getSubscription().getOffer().getCode();
        walletOperation.setSubscriptionDate(edr.getSubscription().getSubscriptionDate());

        walletOperation.setOperationDate(edr.getEventDate());
        walletOperation.setParameter1(edr.getParameter1());
        walletOperation.setParameter2(edr.getParameter2());
        walletOperation.setParameter3(edr.getParameter3());
        walletOperation.setInputQuantity(edr.getQuantity());
        walletOperation.setEdr(edr);
        walletOperation.setProvider(edr.getProvider());

        // FIXME: copy those info in chargeInstance instead of performing multiple queries
        TradingCountry country = userAccount.getBillingAccount().getTradingCountry();
        Long countryId = country.getId();
        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(cachedChargeInstance.getChargeTemplate()
            .getInvoiceSubCategoryCode(), countryId, currentUser.getProvider());

        if (invoiceSubcategoryCountry == null) {
            throw new BusinessException("No tax defined for countryId=" + countryId + " in invoice Sub-Category="
                    + cachedChargeInstance.getChargeTemplate().getInvoiceSubCategoryCode());
        }

        boolean isExonerated = billingAccountService.isExonerated(userAccount.getBillingAccount());

        TradingCurrency currency = userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency();
        Tax tax = invoiceSubcategoryCountry.getTax();
		if(tax==null){
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(), chargeInstance.getUserAccount().getBillingAccount(), null);
		}

        walletOperation.setInvoiceSubCategory(invoiceSubcategoryCountry.getInvoiceSubCategory());
        walletOperation.setRatingUnitDescription(cachedChargeInstance.getRatingUnitDescription());
        walletOperation.setInputUnitDescription(cachedChargeInstance.getChargeTemplate().getInputUnitDescription());
        walletOperation.setSeller(userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller());

        // we set here the wallet to the principal wallet but it will later be
        // overridden by charging algorithm
        walletOperation.setWallet(userAccount.getWallet());
        walletOperation.setCode(cachedChargeInstance.getChargeTemplate().getCode());
        walletOperation.setDescription(cachedChargeInstance.getDescription());
        walletOperation.setQuantity(quantityToCharge);

        walletOperation.setQuantity(NumberUtil.getInChargeUnit(walletOperation.getQuantity(), cachedChargeInstance.getChargeTemplate().getUnitMultiplicator(), cachedChargeInstance
            .getChargeTemplate().getUnitNbDecimal(), cachedChargeInstance.getChargeTemplate().getRoundingMode()));
        walletOperation.setTaxPercent(isExonerated ? BigDecimal.ZERO : tax.getPercent());
        walletOperation.setStartDate(null);
        walletOperation.setEndDate(null);
        walletOperation.setCurrency(currency.getCurrency());
        walletOperation.setStatus(WalletOperationStatusEnum.OPEN);

        walletOperation.setCounter(counterInstance);
        walletOperation.setOfferCode(offerCode);

        ratingService.rateBareWalletOperation(walletOperation, cachedChargeInstance.getAmountWithoutTax(), cachedChargeInstance.getAmountWithTax(), countryId, currency,
            currentUser);
    }

    /**
     * This method first look if there is a counter and a counter period for an event date.
     * 
     * @param edr EDR to process
     * @param cachedCharge Cached charge definition
     * @param reservation Is charge event part of reservation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return if edr quantity fits partially in the counter, returns the remaining quantity
     * @throws BusinessException
     */
    private BigDecimal deduceCounter(EDR edr, CachedUsageChargeInstance cachedCharge, Reservation reservation, boolean isVirtual, User currentUser) throws BusinessException {
        log.info("Deduce counter for key " + cachedCharge.getCounter().getCounterInstanceId());

        BigDecimal deducedQuantity = BigDecimal.ZERO;
        BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;
        CachedCounterInstance cachedCounterInstance = ratingCacheContainerProvider.getCounterInstance(cachedCharge.getCounter().getCounterInstanceId());
        CachedCounterPeriod cachedCounterPeriod = cachedCounterInstance.getCounterPeriod(edr.getEventDate());

        if (cachedCounterPeriod != null) {
            log.info("Found counter period in cache:" + cachedCounterPeriod);
        } else {

            CounterPeriod counterPeriod = null;
            if (isVirtual) {
                CounterTemplate counterTemplate = counterTemplateService.findByCode(cachedCounterInstance.getCode(), edr.getProvider());
                counterPeriod = counterInstanceService.instantiateCounterPeriod(counterTemplate, edr.getEventDate(), cachedCharge.getSubscriptionDate(), null);

            } else {
                CounterInstance counterInstance = counterInstanceService.findById(cachedCounterInstance.getCounterInstanceId());
                UsageChargeInstance usageChargeInstance = usageChargeInstanceService.findById(cachedCharge.getId());
                counterPeriod = counterInstanceService.createPeriod(counterInstance, edr.getEventDate(), cachedCharge.getSubscriptionDate(), usageChargeInstance, currentUser);
            }
            if (counterPeriod != null) {
                cachedCounterPeriod = new CachedCounterPeriod(counterPeriod, cachedCounterInstance);
                cachedCounterInstance.getCounterPeriods().add(cachedCounterPeriod);

                log.debug("created counter period in cache:{}", cachedCounterPeriod);
            }
        }

        if (cachedCounterPeriod != null) {
            BigDecimal initialValue = cachedCounterPeriod.getValue();
            
            synchronized (cachedCounterPeriod) {
                BigDecimal countedValue = cachedCharge.getInChargeUnit(edr.getQuantity());
                log.debug("value to deduce {} * {} = {} from current value {}", new Object[] { cachedCharge.getInChargeUnit(edr.getQuantity()),
                        cachedCharge.getChargeTemplate().getUnitMultiplicator(), countedValue, cachedCounterPeriod.getValue() });

                if (cachedCounterPeriod.getLevel() == null) {
                    deducedQuantity = countedValue;
                } else if (cachedCounterPeriod.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    if (cachedCounterPeriod.getValue().compareTo(countedValue) < 0) {
                        deducedQuantity = cachedCounterPeriod.getValue();
                        cachedCounterPeriod.setValue(BigDecimal.ZERO);
                        deducedQuantityInEDRUnit = cachedCharge.getInEDRUnit(deducedQuantity);
                        log.debug("we deduced {} and set the counter period value to 0", deducedQuantity);
                    } else {
                        deducedQuantity = countedValue;
                        cachedCounterPeriod.setValue(cachedCounterPeriod.getValue().subtract(countedValue));
                        log.debug("we deduced {} and set the counter period value to {}", deducedQuantity, cachedCounterPeriod.getValue());
                        deducedQuantityInEDRUnit = edr.getQuantity();
                    }
                    if (reservation != null && deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        reservation.getCounterPeriodValues().put(cachedCounterPeriod.getCounterPeriodId(), deducedQuantity);
                    }

                    // Save period value changes to DB unless it is a virtual operation
                    if (!isVirtual) {
                        // set the cache element to dirty so it is saved to DB when
                        // shutdown the server
                        // periodCache.setDbDirty(true);
                        counterInstanceService.updatePeriodValue(cachedCounterPeriod.getCounterPeriodId(), cachedCounterPeriod.getValue(), currentUser);
                    }
                }

                // put back the deduced quantity in charge unit

                log.debug("in original EDR units, we deduced {}", deducedQuantityInEDRUnit);
            }
            
            List<Entry<String, BigDecimal>> counterPeriodEventLevels = cachedCounterPeriod.getMatchedNotificationLevels(initialValue, cachedCounterPeriod.getValue());

            if (counterPeriodEventLevels != null && !counterPeriodEventLevels.isEmpty()) {
                CounterPeriod counterPeriod = counterPeriodService.findById(cachedCounterPeriod.getCounterPeriodId());
                triggerCounterPeriodEvent(counterPeriod, counterPeriodEventLevels);
            }
        }
        return deducedQuantityInEDRUnit;
    }

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
     * @param cachedCharge Charge instance to apply
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param currentUser Current user
     * @return returns true if the charge has been fully rated (either because it has no counter or because the counter can be fully decremented with the EDR content)
     * @throws BusinessException
     */
    private boolean rateEDRonChargeAndCounters(WalletOperation walletOperation, EDR edr, CachedUsageChargeInstance cachedCharge, boolean isVirtual,
            User currentUser) throws BusinessException {

        boolean stopEDRRating = false;
        BigDecimal deducedQuantity = null;

        if (cachedCharge.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedQuantity = deduceCounter(edr, cachedCharge, null, isVirtual, currentUser);
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

        Provider provider = cachedCharge.getProvider();

        BigDecimal quantityToCharge = null;
        if (deducedQuantity == null) {
            quantityToCharge = edr.getQuantity();
        } else {
            edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
            quantityToCharge = deducedQuantity;
        }
        rateEDRwithMatchingCharge(walletOperation, edr, quantityToCharge, cachedCharge, isVirtual, currentUser);

        if (!isVirtual) {
            walletOperationService.chargeWalletOperation(walletOperation, currentUser);
        }

        // handle associated edr creation unless it is a Virtual operation
        if (isVirtual) {
            return stopEDRRating;
        }

        for (CachedTriggeredEDR triggeredEDRCache : cachedCharge.getChargeTemplate().getEdrTemplates()) {
            if (triggeredEDRCache.getConditionEL() == null || "".equals(triggeredEDRCache.getConditionEL())
                    || matchExpression(triggeredEDRCache.getConditionEL(), edr, walletOperation)) {
                if (triggeredEDRCache.getMeveoInstanceCode() == null) {
                    EDR newEdr = new EDR();
                    newEdr.setCreated(new Date());
                    newEdr.setEventDate(edr.getEventDate());
                    newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
                    newEdr.setOriginRecord("" + walletOperation.getId());
                    newEdr.setParameter1(evaluateStringExpression(triggeredEDRCache.getParam1EL(), edr, walletOperation));
                    newEdr.setParameter2(evaluateStringExpression(triggeredEDRCache.getParam2EL(), edr, walletOperation));
                    newEdr.setParameter3(evaluateStringExpression(triggeredEDRCache.getParam3EL(), edr, walletOperation));
                    newEdr.setParameter4(evaluateStringExpression(triggeredEDRCache.getParam4EL(), edr, walletOperation));
                    newEdr.setProvider(edr.getProvider());
                    newEdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRCache.getQuantityEL(), edr, walletOperation)));
                    newEdr.setStatus(EDRStatusEnum.OPEN);
                    Subscription sub = edr.getSubscription();
                    if (!StringUtils.isBlank(triggeredEDRCache.getSubscriptionEL())) {
                        String subCode = evaluateStringExpression(triggeredEDRCache.getSubscriptionEL(), edr, walletOperation);
                        sub = subscriptionService.findByCode(subCode, provider);
                        if (sub == null) {
                            throw new BusinessException("could not find subscription for code =" + subCode + " (EL=" + triggeredEDRCache.getSubscriptionEL()
                                    + ") in triggered EDR with code " + triggeredEDRCache.getCode());
                        }
                    }
                    newEdr.setSubscription(sub);
                    log.info("trigger EDR from code " + triggeredEDRCache.getCode());
                    edrService.create(newEdr, currentUser);

                } else {
                    CDR cdr = new CDR();
                    String subCode = evaluateStringExpression(triggeredEDRCache.getSubscriptionEL(), edr, walletOperation);
                    cdr.setAccess_id(subCode);
                    cdr.setTimestamp(edr.getEventDate());
                    cdr.setParam1(evaluateStringExpression(triggeredEDRCache.getParam1EL(), edr, walletOperation));
                    cdr.setParam2(evaluateStringExpression(triggeredEDRCache.getParam2EL(), edr, walletOperation));
                    cdr.setParam3(evaluateStringExpression(triggeredEDRCache.getParam3EL(), edr, walletOperation));
                    cdr.setParam4(evaluateStringExpression(triggeredEDRCache.getParam4EL(), edr, walletOperation));
                    cdr.setProvider(edr.getProvider());
                    cdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRCache.getQuantityEL(), edr, walletOperation)));

                    String url = "api/rest/billing/mediation/chargeCdr";
                    Response response = meveoInstanceService.callTextServiceMeveoInstance(url, triggeredEDRCache.getMeveoInstanceCode(), cdr.toCsv());
                    ActionStatus actionStatus = response.readEntity(ActionStatus.class);

                    log.debug("response {}", actionStatus);

                    if (actionStatus == null || ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                        throw new BusinessException("Error charging Edr on remote instance Code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());
                    }
                }
            }
        }

        return stopEDRRating;
    }

    private boolean reserveEDRonChargeAndCounters(Reservation reservation, EDR edr, CachedUsageChargeInstance charge, User currentUser) throws BusinessException {
        boolean stopEDRRating = false;
        BigDecimal deducedQuantity = null;
        if (charge.getCounter() != null) {
            deducedQuantity = deduceCounter(edr, charge, reservation, false, currentUser);
            if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
                stopEDRRating = true;
            }
        } else {
            stopEDRRating = true;
        }

        if (deducedQuantity == null || deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {

            WalletReservation walletOperation = new WalletReservation();
            rateEDRwithMatchingCharge(walletOperation, edr, deducedQuantity, charge, false, currentUser);
            walletOperation.setReservation(reservation);
            walletOperation.setStatus(WalletOperationStatusEnum.RESERVED);
            reservation.setAmountWithoutTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithoutTax()));
            reservation.setAmountWithTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithTax()));
            if (deducedQuantity != null) {
                edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
                walletOperation.setQuantity(deducedQuantity);
            }

            walletOperationService.chargeWalletOperation(walletOperation, currentUser);
        } else {
            log.warn("deduceQuantity is null");
        }
        return stopEDRRating;
    }

    /**
     * Rate an EDR using counters if they apply
     * 
     * @param edr
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void ratePostpaidUsage(EDR edr, User currentUser) throws BusinessException {
        rateUsageWithinTransaction(edr, false, currentUser);
    }

    public List<WalletOperation> rateUsageDontChangeTransaction(EDR edr, boolean isVirtual, User currentUser) throws BusinessException {
        return rateUsageWithinTransaction(edr, isVirtual, currentUser);
    }
    
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<WalletOperation> rateUsageWithinTransaction(EDR edr, boolean isVirtual, User currentUser) throws BusinessException {
        BigDecimal originalQuantity = edr.getQuantity();

        log.info("Rating EDR={}", edr);

        List<WalletOperation> walletOperations = new ArrayList<>();

        edr.setLastUpdate(new Date());

        if (edr.getSubscription() == null) {
            edr.setStatus(EDRStatusEnum.REJECTED);
            edr.setRejectReason("NULL_SUBSCRIPTION");
            return null;
        }

        boolean edrIsRated = false;

        try {
            List<CachedUsageChargeInstance> charges = null;

            if (!isVirtual) {
                if (!ratingCacheContainerProvider.isUsageChargeInstancesCached(edr.getSubscription().getId())) {
                    edr.setStatus(EDRStatusEnum.REJECTED);
                    edr.setRejectReason("SUBSCRIPTION_HAS_NO_CHARGE");
                    return null;
                }

                // TODO:order charges by priority and id
                charges = ratingCacheContainerProvider.getUsageChargeInstances(edr.getSubscription().getId());

            } else {
                // TODO still need to obtain charges as subscription is virtual
            }

            boolean foundPricePlan = true;
            // Find the first matching charge and rate it
            for (CachedUsageChargeInstance charge : charges) {
                CachedUsageChargeTemplate templateCache = charge.getChargeTemplate();
                log.trace("try templateCache=" + templateCache.toString());

                try {
                    if (!isChargeMatch(edr, templateCache.getCode(), templateCache.getFilterExpression(), templateCache.getFilter1(), templateCache.getFilter2(),
                        templateCache.getFilter3(), templateCache.getFilter4())) {
                        continue;
                    }
                } catch (ChargeWitoutPricePlanException e) {
                    log.debug("Charge {} was matched but does not contain a priceplan", templateCache.getCode());
                    foundPricePlan = false;
                    continue;
                }

                log.debug("Found matching charge inst : id=" + charge.getId());
                WalletOperation walletOperation = new WalletOperation();
                edrIsRated = rateEDRonChargeAndCounters(walletOperation, edr, charge, isVirtual, currentUser);
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
     * @param edr EDR to check
     * @param chargeCode Charge code. If provided a check that a priceplan is associated will be performed.
     * @param filterExpression Charge filter expression
     * @param filter1 Charge filter 1 value
     * @param filter2 Charge filter 2 value
     * @param filter3 Charge filter 3 value
     * @param filter4 Charge filter 4 value
     * @return
     * @throws BusinessException
     * @throws ChargeWitoutPricePlanException If charge has no price plan associated
     */
    private boolean isChargeMatch(EDR edr, String chargeCode, String filterExpression, String filter1, String filter2, String filter3, String filter4) throws BusinessException,
            ChargeWitoutPricePlanException {

        if (filter1 == null || filter1.equals(edr.getParameter1())) {
            log.trace("filter1 ok");
            if (filter2 == null || filter2.equals(edr.getParameter2())) {
                log.trace("filter2 ok");
                if (filter3 == null || filter3.equals(edr.getParameter3())) {
                    log.trace("filter3 ok");
                    if (filter4 == null || filter4.equals(edr.getParameter4())) {
                        log.trace("filter4 ok");
                        if (filterExpression == null || matchExpression(filterExpression, edr)) {

                            if (chargeCode != null) {
                                List<PricePlanMatrix> chargePricePlans = ratingCacheContainerProvider.getPricePlansByChargeCode(edr.getProvider().getId(), chargeCode);
                                if (chargePricePlans == null || chargePricePlans.size() == 0) {
                                    throw new ChargeWitoutPricePlanException(chargeCode);
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Reservation reserveUsageWithinTransaction(EDR edr, User currentUser) throws BusinessException {

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
                if (ratingCacheContainerProvider.isUsageChargeInstancesCached(edr.getSubscription().getId())) {
                    // TODO:order charges by priority and id
                    List<CachedUsageChargeInstance> charges = ratingCacheContainerProvider.getUsageChargeInstances(edr.getSubscription().getId());
                    reservation = new Reservation();
                    reservation.setProvider(currentUser.getProvider());
                    reservation.setReservationDate(edr.getEventDate());
                    reservation.setExpiryDate(new Date(time + currentUser.getProvider().getPrepaidReservationExpirationDelayinMillisec()));
                    reservation.setStatus(ReservationStatus.OPEN);
                    Auditable audit = new Auditable();
                    audit.setCreated(new Date());
                    audit.setCreator(currentUser);
                    reservation.setAuditable(audit);
                    reservation.setOriginEdr(edr);
                    reservation.setQuantity(edr.getQuantity());
                    // it would be nice to have a persistence context bound to
                    // the JTA transaction
                    em.persist(reservation);

                    for (CachedUsageChargeInstance charge : charges) {
                        CachedUsageChargeTemplate templateCache = charge.getChargeTemplate();
                        log.info("try templateCache=" + templateCache.toString());

                        if (isChargeMatch(edr, templateCache.getCode(), templateCache.getFilterExpression(), templateCache.getFilter1(), templateCache.getFilter2(),
                            templateCache.getFilter3(), templateCache.getFilter4())) {

                            log.debug("found matchig charge inst : id=" + charge.getId());
                            edrIsRated = reserveEDRonChargeAndCounters(reservation, edr, charge, currentUser);
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

    private boolean matchExpression(String expression, EDR edr) throws BusinessException {
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        return (Boolean) ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
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
