package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.cache.CounterInstanceCache;
import org.meveo.model.cache.CounterPeriodCache;
import org.meveo.model.cache.TriggeredEDRCache;
import org.meveo.model.cache.UsageChargeInstanceCache;
import org.meveo.model.cache.UsageChargeTemplateCache;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.util.MeveoCacheContainerProvider;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
public class UsageRatingService {

	@PersistenceContext
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
	private CounterPeriodService counterPeriodService;

	@Inject
	private RatingService ratingService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private SubscriptionService subscriptionService;
	

	@PostConstruct
	public synchronized void updateCacheFromDB() {
			log.info("loading usage charge cache");
			@SuppressWarnings("unchecked")
			List<UsageChargeInstance> usageChargeInstances = em.createQuery(
					"From UsageChargeInstance u "
					+ "where u.status=org.meveo.model.billing.InstanceStatusEnum.ACTIVE").getResultList();
			if (usageChargeInstances != null) {
				log.debug("loading cache for {} usage charges",usageChargeInstances.size());
				for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {
					updateCache(usageChargeInstance);
				}
			}
	}


	public void updateTemplateCache(TriggeredEDRTemplate triggeredEDRTemplate) {
		log.debug("updateTemplateCache for tigeredEDR {}",triggeredEDRTemplate.toString());
		@SuppressWarnings("unchecked")
		List<UsageChargeTemplate> charges = em.createNamedQuery("UsageChargeTemplate.getWithTemplateEDR")
				.setParameter("edrTemplate", triggeredEDRTemplate).getResultList();
		for(UsageChargeTemplate charge:charges){
			updateTemplateCache(charge);
		}
	}
	
	public UsageChargeTemplateCache updateTemplateCache(
			UsageChargeTemplate usageChargeTemplate) {
		UsageChargeTemplateCache cachedValue = null;
		if (usageChargeTemplate != null) {
			log.debug("updateTemplateCache for charge {}",usageChargeTemplate.getCode());
			if (MeveoCacheContainerProvider.getUsageChargeTemplateCacheCache().containsKey(usageChargeTemplate.getId())) {
				log.info("cache already contains the code");
				cachedValue = MeveoCacheContainerProvider.getUsageChargeTemplateCacheCache().get(usageChargeTemplate
						.getId());
				cachedValue.setEdrTemplates(new HashSet<TriggeredEDRCache>());
			} else {
				log.info("cache does not contain the code");
				cachedValue = new UsageChargeTemplateCache();
				MeveoCacheContainerProvider.getUsageChargeTemplateCacheCache().put(usageChargeTemplate.getId(), cachedValue);
			}

			if (usageChargeTemplate.getFilterExpression() == null
					|| usageChargeTemplate.getFilterExpression().equals("")) {
				log.info("set filterExpression to null");
				cachedValue.setFilterExpression(null);
			} else {
				log.info("set filterExpression to "
						+ usageChargeTemplate.getFilterExpression());
				cachedValue.setFilterExpression(usageChargeTemplate
						.getFilterExpression());
			}
			if (usageChargeTemplate.getFilterParam1() == null
					|| usageChargeTemplate.getFilterParam1().equals("")) {
				log.info("set filter1 to null");
				cachedValue.setFilter1(null);
			} else {
				log.info("set filter1 to "
						+ usageChargeTemplate.getFilterParam1());
				cachedValue.setFilter1(usageChargeTemplate.getFilterParam1());
			}
			if (usageChargeTemplate.getFilterParam2() == null
					|| usageChargeTemplate.getFilterParam2().equals("")) {
				log.info("set filter2 to null");
				cachedValue.setFilter2(null);
			} else {
				log.info("set filter2 to "
						+ usageChargeTemplate.getFilterParam2());
				cachedValue.setFilter2(usageChargeTemplate.getFilterParam2());
			}
			if (usageChargeTemplate.getFilterParam3() == null
					|| usageChargeTemplate.getFilterParam3().equals("")) {
				log.info("set filter3 to null");
				cachedValue.setFilter3(null);
			} else {
				log.info("set filter3 to "
						+ usageChargeTemplate.getFilterParam3());
				cachedValue.setFilter3(usageChargeTemplate.getFilterParam3());
			}
			if (usageChargeTemplate.getFilterParam4() == null
					|| usageChargeTemplate.getFilterParam4().equals("")) {
				log.info("set filter4 to null");
				cachedValue.setFilter4(null);
			} else {
				log.info("set filter4 to "
						+ usageChargeTemplate.getFilterParam4());
				cachedValue.setFilter4(usageChargeTemplate.getFilterParam4());
			}
			if (usageChargeTemplate.getEdrTemplates() == null
					|| usageChargeTemplate.getEdrTemplates().size() == 0) {
				log.info("do not set erdTemplates");
			} else {
				log.info("set erdTemplates");
				Set<TriggeredEDRCache> edrTemplates = cachedValue
						.getEdrTemplates();
				for (TriggeredEDRTemplate edrTemplate : usageChargeTemplate
						.getEdrTemplates()) {
					TriggeredEDRCache trigerredEDRCache = new TriggeredEDRCache();
					trigerredEDRCache.setConditionEL(edrTemplate
							.getConditionEl());

					trigerredEDRCache.setCode(edrTemplate.getCode());
					trigerredEDRCache.setSubscriptionEL(edrTemplate
							.getSubscriptionEl());

					if (edrTemplate.getQuantityEl() == null
							|| (edrTemplate.getQuantityEl().equals(""))) {
						log.error("edrTemplate QuantityEL must be set for triggeredEDRTemplate="
								+ edrTemplate.getId());
					} else {
						trigerredEDRCache.setQuantityEL(edrTemplate
								.getQuantityEl());
					}
					if (edrTemplate.getParam1El() == null
							|| (edrTemplate.getParam1El().equals(""))) {
						log.error("edrTemplate param1El must be set for triggeredEDRTemplate="
								+ edrTemplate.getId());
					} else {
						trigerredEDRCache
								.setParam1EL(edrTemplate.getParam1El());
					}

					if (edrTemplate.getParam2El() == null
							|| (edrTemplate.getParam2El().equals(""))) {
						log.info("set param2El to null");
						trigerredEDRCache.setParam2EL(null);
					} else {
						log.info("set param2El to " + edrTemplate.getParam2El());
						trigerredEDRCache
								.setParam2EL(edrTemplate.getParam2El());
					}

					if (edrTemplate.getParam3El() == null
							|| (edrTemplate.getParam3El().equals(""))) {
						log.info("set param3El to null");
						trigerredEDRCache.setParam3EL(null);
					} else {
						log.info("set param3El to " + edrTemplate.getParam3El());
						trigerredEDRCache
								.setParam3EL(edrTemplate.getParam3El());
					}

					if (edrTemplate.getParam4El() == null
							|| (edrTemplate.getParam4El().equals(""))) {
						log.info("set param4El to null");
						trigerredEDRCache.setParam4EL(null);
					} else {
						log.info("set param4El to " + edrTemplate.getParam4El());
						trigerredEDRCache
								.setParam4EL(edrTemplate.getParam4El());
					}
					edrTemplates.add(trigerredEDRCache);
				}
			}
			if (cachedValue.getPriority() != usageChargeTemplate.getPriority()) {
				log.info("set priority to " + usageChargeTemplate.getPriority());
				cachedValue.setPriority(usageChargeTemplate.getPriority());
				// TODO reorder all cacheInstance associated to this template
				for (Long subscriptionId : cachedValue.getSubscriptionIds()) {
					log.info("reorder charge cache for subscription "
							+ subscriptionId);
					reorderChargeCache(subscriptionId);
				}
			}
			cachedValue.setFilterExpression(usageChargeTemplate
					.getFilterExpression());

		}
		return cachedValue;
	}

	private void reorderChargeCache(Long id) {
		List<UsageChargeInstanceCache> charges = MeveoCacheContainerProvider.getUsageChargeInstanceCache().get(id);
		Collections.sort(charges);
		log.info("sorted " + charges.size() + " usage charges");
	}

	public void updateCache(UsageChargeInstance usageChargeInstance) {
		if (usageChargeInstance != null) {

			UsageChargeInstanceCache cachedValue = new UsageChargeInstanceCache();
			ChargeTemplate chargeTemplate = usageChargeInstance
					.getChargeTemplate();
			log.debug("chargeTemplateId={}" + chargeTemplate.getId());
			// UsageChargeTemplate usageChargeTemplate=(UsageChargeTemplate)
			// usageChargeInstance.getChargeTemplate();
			UsageChargeTemplate usageChargeTemplate = em.find(
					UsageChargeTemplate.class, chargeTemplate.getId());
			Long key = usageChargeInstance.getServiceInstance()
					.getSubscription().getId();
			log.info("update cache key (subs Id)={} for charge with id={}",
					key, usageChargeInstance.getId());
			boolean cacheContainsKey = MeveoCacheContainerProvider.getUsageChargeInstanceCache().containsKey(key);
			boolean cacheContainsCharge = false;

			List<UsageChargeInstanceCache> charges = null;
			if (cacheContainsKey) {
				log.info("the cache contains the key");
				charges = MeveoCacheContainerProvider.getUsageChargeInstanceCache().get(key);
				for (UsageChargeInstanceCache charge : charges) {
					if (charge.getChargeInstanceId() == usageChargeInstance
							.getId()) {
						if (usageChargeInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
							log.info("the cache contains the charge but its status in db is not active so we remove it");
							charges.remove(charge);
							if (charges.size() == 0) {
								MeveoCacheContainerProvider.getUsageChargeInstanceCache().remove(key);
							}
							return;
						} else {
							cachedValue = charge;
							cacheContainsCharge = true;

						}
					}
				}
			} else {
				log.info("the cache does not contains the key");
				charges = new ArrayList<UsageChargeInstanceCache>();
			}
			if (usageChargeInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
				log.info("the charge is not active, we dont add it to cache");
				return;
			}

			cachedValue.setSubscriptionDate(usageChargeInstance.getServiceInstance().getSubscriptionDate());
			cachedValue.setChargeDate(usageChargeInstance.getChargeDate());
			cachedValue.setChargeInstanceId(usageChargeInstance.getId());
			usageChargeInstance.getProvider().getCode();
			cachedValue.setProvider(usageChargeInstance.getProvider());
			cachedValue
					.setCurrencyId(usageChargeInstance.getCurrency().getId());
			if (usageChargeInstance.getCounter() != null) {
				CounterInstanceCache counterCacheValue = null;
				Long counterKey = CounterInstanceCache
						.getKey(usageChargeInstance.getCounter());

				log.info("counter key:" + counterKey);
				if (MeveoCacheContainerProvider.getCounterCache().containsKey(counterKey)) {
					log.info("the counter cache contains the key");
					counterCacheValue = MeveoCacheContainerProvider.getCounterCache().get(counterKey);
				} else {
					log.info("the counter cache doesnt contain the key, we add it");
					counterCacheValue = CounterInstanceCache
							.getInstance(usageChargeInstance.getCounter());
					MeveoCacheContainerProvider.getCounterCache().put(counterKey, counterCacheValue);
				}
				cachedValue.setCounter(counterCacheValue);
			}
			cachedValue.setTerminationDate(usageChargeInstance
					.getTerminationDate());
			UsageChargeTemplateCache templateCache = updateTemplateCache(usageChargeTemplate);
			cachedValue.setTemplateCache(templateCache);
			templateCache.getSubscriptionIds().add(key);
			cachedValue.setUnityMultiplicator(usageChargeTemplate
					.getUnityMultiplicator());
			cachedValue.setUnityNbDecimal(usageChargeTemplate
					.getUnityNbDecimal());
			cachedValue.setLastUpdate(new Date());

			if (!cacheContainsCharge) {
				charges.add(cachedValue);
				log.info("charge added, we order it");
				Collections.sort(charges);
			}

			if (!cacheContainsKey) {
				log.info("key added to charge cache");
				MeveoCacheContainerProvider.getUsageChargeInstanceCache().put(key, charges);
			}

			reorderChargeCache(key);
		}
	}

	// @PreDestroy
	// accessing Entity manager in predestroy is bugged in jboss7.1.3
	/*void saveCounters() {
		for (Long key : MeveoCacheContainerProvider.getCounterCache().keySet()) {
			CounterInstanceCache counterInstanceCache = MeveoCacheContainerProvider.getCounterCache().get(key);
			if (counterInstanceCache.getCounterPeriods() != null) {
				for (CounterPeriodCache itemPeriodCache : counterInstanceCache
						.getCounterPeriods()) {
					if (itemPeriodCache.isDbDirty()) {
						CounterPeriod counterPeriod = em.find(
								CounterPeriod.class,
								itemPeriodCache.getCounterPeriodId());
						counterPeriod.setValue(itemPeriodCache.getValue());
						counterPeriod.getAuditable().setUpdated(new Date());
						em.merge(counterPeriod);
						log.debug("save counter with id={}, new value={}",
								itemPeriodCache.getCounterPeriodId(),
								itemPeriodCache.getValue());
						// calling ejb in this predestroy method just fail...
						// counterInstanceService.updatePeriodValue(itemPeriodCache.getCounterPeriodId(),itemPeriodCache.getValue());
					}
				}
			}
		}
	}*/

	/**
	 * This method use the price plan to rate an EDR knowing what charge must be
	 * used
	 * 
	 * @param edr
	 * @param chargeInstance
	 * @param provider
	 * @param currencyId
	 * @param taxId
	 * @return
	 * @throws BusinessException
	 */
	public WalletOperation rateEDRwithMatchingCharge(EDR edr,
			BigDecimal deducedQuantity, UsageChargeInstanceCache chargeCache,
			UsageChargeInstance chargeInstance, Provider provider,boolean isReservation)
			throws BusinessException {
		WalletOperation walletOperation =null;
		if(isReservation){
			walletOperation = new WalletReservation();
		} else {
			walletOperation = new WalletOperation();
		}
		walletOperation.setSubscriptionDate(null);
		walletOperation.setOperationDate(edr.getEventDate());
		walletOperation.setParameter1(edr.getParameter1());
		walletOperation.setParameter2(edr.getParameter2());
		walletOperation.setParameter3(edr.getParameter3());

		walletOperation.setProvider(provider);

		// FIXME: copy those info in chargeInstance instead of performing
		// multiple queries
		InvoiceSubCategory invoiceSubCat = chargeInstance.getChargeTemplate()
				.getInvoiceSubCategory();
		TradingCountry country = edr.getSubscription().getUserAccount()
				.getBillingAccount().getTradingCountry();
		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCat.getId(),
						countryId, provider);

		if (invoiceSubcategoryCountry == null) {
			throw new BusinessException("No tax defined for countryId="
					+ countryId + " in invoice Sub-Category="
					+ invoiceSubCat.getCode());
		}

		TradingCurrency currency = edr.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getTradingCurrency();
		Tax tax = invoiceSubcategoryCountry.getTax();

		walletOperation.setChargeInstance(chargeInstance);
		walletOperation.setUnityDescription(chargeInstance
				.getUnityDescription());
		walletOperation.setSeller(edr.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getCustomer()
				.getSeller());
		//we set here the wallet to the pricipal wallet but it will later be overriden by charging algo
		walletOperation.setWallet(edr.getSubscription().getUserAccount()
				.getWallet());
		walletOperation.setCode(chargeInstance.getCode());
		walletOperation.setDescription(chargeInstance.getDescription());

		if (deducedQuantity != null) {
			walletOperation.setQuantity(deducedQuantity);
		} else {
			walletOperation.setQuantity(edr.getQuantity());
		}

		walletOperation.setTaxPercent(tax.getPercent());
		walletOperation.setStartDate(null);
		walletOperation.setEndDate(null);
		walletOperation.setCurrency(currency.getCurrency());

		if (chargeInstance.getCounter() != null) {
			walletOperation.setCounter(chargeInstance.getCounter());
		}

		walletOperation
				.setOfferCode(edr.getSubscription().getOffer().getCode());
		walletOperation.setStatus(WalletOperationStatusEnum.OPEN);

		// log.info("provider code:" + provider.getCode());
		ratingService.rateBareWalletOperation(walletOperation, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(),
				countryId, currency, provider);

		
		return walletOperation;
	}

	/**
	 * This method first look if there is a counter and a
	 * 
	 * @param edr
	 * @param charge
	 * @return if edr quantity fits partially in the counter, returns the
	 *         remaining quantity
	 * @throws BusinessException
	 */
	BigDecimal deduceCounter(EDR edr, UsageChargeInstanceCache charge,Reservation reservation,
			User currentUser) throws BusinessException {
		log.info("Deduce counter for key " + charge.getCounter().getKey());

		BigDecimal deducedQuantity = BigDecimal.ZERO;
		BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;
		CounterInstanceCache counterInstanceCache = MeveoCacheContainerProvider.getCounterCache().get(charge
				.getCounter().getKey());
		CounterPeriodCache periodCache = null;

		if (counterInstanceCache.getCounterPeriods() != null) {
			for (CounterPeriodCache itemPeriodCache : counterInstanceCache
					.getCounterPeriods()) {
				if ((itemPeriodCache.getStartDate().before(edr.getEventDate()) || itemPeriodCache
						.getStartDate().equals(edr.getEventDate()))
						&& itemPeriodCache.getEndDate().after(
								edr.getEventDate())) {
					periodCache = itemPeriodCache;
					log.info("Found counter period in cache:" + periodCache);
					break;
				}
			}
		} else {
			counterInstanceCache
					.setCounterPeriods(new ArrayList<CounterPeriodCache>());
		}

		CounterInstance counterInstance = null;
		if (periodCache == null) {
			counterInstance = counterInstanceService
					.findById(counterInstanceCache.getKey());
			CounterPeriod counterPeriod = counterInstanceService.createPeriod(
					counterInstance, edr.getEventDate(),charge.getSubscriptionDate(), currentUser);
			if(counterPeriod!=null){
				periodCache = CounterPeriodCache.getInstance(counterPeriod,
						counterInstance.getCounterTemplate());
				counterInstanceCache.getCounterPeriods().add(periodCache);
	
				log.debug("created counter period in cache:{}", periodCache);
			}
		}

		if(periodCache!=null){
		synchronized (periodCache) {
			BigDecimal countedValue = edr.getQuantity().multiply(
					charge.getUnityMultiplicator());
			log.debug("value to deduce {} * {} = {} from current value {}",edr.getQuantity()
					,charge.getUnityMultiplicator() , countedValue,periodCache.getValue());
			if (charge.getUnityNbDecimal() > 0) {
				int rounding = (charge.getUnityNbDecimal() > BaseEntity.NB_DECIMALS) ? BaseEntity.NB_DECIMALS
						: charge.getUnityNbDecimal();
				countedValue = countedValue.setScale(rounding,
						RoundingMode.HALF_UP);
				log.debug("after rounding we would deduce {}",countedValue);
			}

			if (periodCache.getLevel() == null) {
				deducedQuantity = countedValue;
			} else if (periodCache.getValue().compareTo(BigDecimal.ZERO) > 0) {
				if (periodCache.getValue().compareTo(countedValue) < 0) {
					deducedQuantity = periodCache.getValue();
					periodCache.setValue(BigDecimal.ZERO);
					int rounding = BaseEntity.NB_DECIMALS;
					if (charge.getUnityNbDecimal() < BaseEntity.NB_DECIMALS) {
						rounding = (int)Math.round(charge.getUnityNbDecimal()+Math.floor(Math.log10(charge.getUnityMultiplicator().doubleValue())));
						if(rounding>BaseEntity.NB_DECIMALS){
							rounding = BaseEntity.NB_DECIMALS;
						}
					}
					deducedQuantityInEDRUnit = deducedQuantity.divide(charge
							.getUnityMultiplicator(),rounding, RoundingMode.HALF_UP);
					
					log.debug("we deduced {} and set the counter period value to 0, rounding used for EDR units :{}",deducedQuantity,rounding);
				} else {
					deducedQuantity = countedValue;
					periodCache.setValue(periodCache.getValue().subtract(
							countedValue));
					log.debug("we deduced {} and set the counter period value to {}",deducedQuantity,periodCache.getValue());
					deducedQuantityInEDRUnit = edr.getQuantity();
				}
				if(reservation!=null && deducedQuantity.compareTo(BigDecimal.ZERO)>0){
					reservation.getCounterPeriodValues().put(periodCache.getCounterPeriodId(), deducedQuantity);
				}
				// set the cache element to dirty so it is saved to DB when
				// shutdown the server
				// periodCache.setDbDirty(true);
				counterInstanceService.updatePeriodValue(
						periodCache.getCounterPeriodId(),
						periodCache.getValue(), currentUser);
			}

			// put back the deduced quantity in charge unit
			
			log.debug("in original EDR units, we deduced {}",deducedQuantityInEDRUnit);
		}
		}
		return deducedQuantityInEDRUnit;
	}
	
	public void restoreCounters(Map<Long,BigDecimal> counterPeriodValues) throws BusinessException{
		for(Long cId:counterPeriodValues.keySet())  {
			CounterPeriod counterPeriod = counterPeriodService.findById(cId);
			BigDecimal value=counterPeriodValues.get(cId);
			Long counterKey = CounterInstanceCache.getKey(counterPeriod.getCounterInstance());
			if (MeveoCacheContainerProvider.getCounterCache().containsKey(counterKey)) {
				CounterInstanceCache counterCacheValue = MeveoCacheContainerProvider.getCounterCache().get(counterKey);
				for(CounterPeriodCache cpc:counterCacheValue.getCounterPeriods()){
					if(cpc.getCounterPeriodId()==cId){
						cpc.getValue().add(value);
						log.debug("added {} to cache (new value {})",value,cpc.getValue());
						break;
					}
				}
			}
			counterPeriod.setValue(counterPeriod.getValue().add(value));
			log.debug("added {}  (new value {}) to counterPeriod {}, counter {}",value,counterPeriod.getValue(),counterPeriod.getId(),counterKey);
		}
	}

	/**
	 * this method evaluate the EDR against the charge and its counter it
	 * returns true if the charge has been rated (either because it has no
	 * counter or because the counter can be decremented with the EDR content)
	 * 
	 * @param edr
	 * @param charge
	 * @return
	 * @throws BusinessException
	 */
	public boolean rateEDRonChargeAndCounters(EDR edr,
			UsageChargeInstanceCache charge, User currentUser)
			throws BusinessException {
		boolean stopEDRRating = false;
		BigDecimal deducedQuantity = null;

		if (charge.getCounter() != null) {
			// if the charge is associated to a counter and we can decrement it
			// then we rate the charge if not we simply try the next charge
			// if the counter has been decremented by the full quantity we stop
			// the rating
			deducedQuantity = deduceCounter(edr, charge,null, currentUser);
			if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
				stopEDRRating = true;
			}
		} else {
			stopEDRRating = true;
		}

		if (deducedQuantity == null
				|| deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {
			Provider provider = charge.getProvider();
			UsageChargeInstance chargeInstance = usageChargeInstanceService
					.findById(charge.getChargeInstanceId());
			WalletOperation walletOperation = rateEDRwithMatchingCharge(edr,
					deducedQuantity, charge, chargeInstance, provider,false);

			if (deducedQuantity != null) {
				edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
				walletOperation.setQuantity(deducedQuantity);
			}

			walletOperationService.chargeWalletOperation(walletOperation, currentUser, provider);
			//walletOperationService.create(walletOperation, currentUser, provider);

			// handle associated edr creation
			if (charge.getTemplateCache().getEdrTemplates().size() > 0) {
				for (TriggeredEDRCache triggeredEDRCache : charge
						.getTemplateCache().getEdrTemplates()) {
					if (triggeredEDRCache.getConditionEL() == null
							|| "".equals(triggeredEDRCache.getConditionEL())
							|| matchExpression(
									triggeredEDRCache.getConditionEL(), edr,
									walletOperation)) {
						EDR newEdr = new EDR();
						newEdr.setCreated(new Date());
						newEdr.setEventDate(edr.getEventDate());
						newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
						newEdr.setOriginRecord("" + walletOperation.getId());
						newEdr.setParameter1(evaluateStringExpression(
								triggeredEDRCache.getParam1EL(), edr,
								walletOperation));
						newEdr.setParameter2(evaluateStringExpression(
								triggeredEDRCache.getParam2EL(), edr,
								walletOperation));
						newEdr.setParameter3(evaluateStringExpression(
								triggeredEDRCache.getParam3EL(), edr,
								walletOperation));
						newEdr.setParameter4(evaluateStringExpression(
								triggeredEDRCache.getParam4EL(), edr,
								walletOperation));
						newEdr.setProvider(edr.getProvider());
						newEdr.setQuantity(new BigDecimal(
								evaluateDoubleExpression(
										triggeredEDRCache.getQuantityEL(), edr,
										walletOperation)));
						newEdr.setStatus(EDRStatusEnum.OPEN);
						Subscription sub = edr.getSubscription();
						if (!StringUtils.isBlank(triggeredEDRCache.getSubscriptionEL())) {
							String subCode = evaluateStringExpression(triggeredEDRCache.getSubscriptionEL(), edr,
									walletOperation);
							sub = subscriptionService.findByCode(subCode, provider);
							if (sub == null) {
								throw new BusinessException("could not find subscription for code =" + subCode + " (EL="
										+ triggeredEDRCache.getSubscriptionEL() + ") in triggered EDR with code "
										+ triggeredEDRCache.getCode());
							}
						}
						newEdr.setSubscription(sub);
						log.info("trigger EDR from code " + triggeredEDRCache.getCode());
						edrService.create(newEdr, currentUser, provider);
					}
				}
			}
		} else {
			log.warn("deduceQuantity is null");
		}

		return stopEDRRating;
	}
	
	public boolean reserveEDRonChargeAndCounters(Reservation reservation,EDR edr, UsageChargeInstanceCache charge, User currentUser)
			throws BusinessException {
		boolean stopEDRRating = false;
		BigDecimal deducedQuantity = null;
		if (charge.getCounter() != null) {
			deducedQuantity = deduceCounter(edr, charge,reservation, currentUser);
			if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
				stopEDRRating = true;
			}
		} else {
			stopEDRRating = true;
		}

		if (deducedQuantity == null
				|| deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {
			Provider provider = charge.getProvider();
			UsageChargeInstance chargeInstance = usageChargeInstanceService
					.findById(charge.getChargeInstanceId());
			WalletReservation walletOperation = (WalletReservation)rateEDRwithMatchingCharge(edr,
					deducedQuantity, charge, chargeInstance, provider,true);
			walletOperation.setReservation(reservation);
			walletOperation.setStatus(WalletOperationStatusEnum.RESERVED);
			reservation.setAmountWithoutTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithoutTax()));
			reservation.setAmountWithTax(reservation.getAmountWithoutTax().add(walletOperation.getAmountWithTax()));
			if (deducedQuantity != null) {
				edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
				walletOperation.setQuantity(deducedQuantity);
			}

			walletOperationService.chargeWalletOperation(walletOperation, currentUser, provider);
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
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ratePostpaidUsage(EDR edr, User currentUser) throws BusinessException {
		rateUsageWithinTransaction(edr,currentUser);
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void rateUsageWithinTransaction(EDR edr, User currentUser) throws BusinessException {
		BigDecimal originalQuantity = edr.getQuantity();

		log.info("Rating EDR={}", edr);

		if (edr.getSubscription() == null) {
			edr.setStatus(EDRStatusEnum.REJECTED);
			edr.setRejectReason("Subscription is null");
		} else {
			boolean edrIsRated = false;

			try {
				if (MeveoCacheContainerProvider.getUsageChargeInstanceCache().containsKey(edr.getSubscription().getId())) {
					// TODO:order charges by priority and id
					List<UsageChargeInstanceCache> charges = MeveoCacheContainerProvider.getUsageChargeInstanceCache()
							.get(edr.getSubscription().getId());

					for (UsageChargeInstanceCache charge : charges) {
						UsageChargeTemplateCache templateCache = charge
								.getTemplateCache();
						log.info("try templateCache="
								+ templateCache.toString());
						if (templateCache.getFilter1() == null
								|| templateCache.getFilter1().equals(
										edr.getParameter1())) {
							log.info("filter1 ok");
							if (templateCache.getFilter2() == null
									|| templateCache.getFilter2().equals(
											edr.getParameter2())) {
								log.info("filter2 ok");
								if (templateCache.getFilter3() == null
										|| templateCache.getFilter3().equals(
												edr.getParameter3())) {
									log.info("filter3 ok");
									if (templateCache.getFilter4() == null
											|| templateCache
													.getFilter4()
													.equals(edr.getParameter4())) {
										log.info("filter4 ok");
										if (templateCache.getFilterExpression() == null
												|| matchExpression(
														templateCache
																.getFilterExpression(),
														edr)) {
											log.info("filterExpression ok");
											// we found matching charge, if we
											// rate
											// it we exit the look
											log.debug("found matchig charge inst : id="
													+ charge.getChargeInstanceId());
											edrIsRated = rateEDRonChargeAndCounters(
													edr, charge, currentUser);
											if (edrIsRated) {
												edr.setStatus(EDRStatusEnum.RATED);
												break;
											}
										}
									}
								}
							}
						}
					}

					if (!edrIsRated) {
						edr.setStatus(EDRStatusEnum.REJECTED);
						edr.setRejectReason("no matching charge");
					}
				} else {
					edr.setStatus(EDRStatusEnum.REJECTED);
					edr.setRejectReason("subscription has no usage charge");
				}
			} catch (Exception e) {
				edr.setStatus(EDRStatusEnum.REJECTED);
				edr.setRejectReason(e.getMessage());
				//e.printStackTrace();
				throw new BusinessException(e.getMessage());
			}
		}

		// put back the original quantity in edr (could have been decrease by
		// counters)
		edr.setQuantity(originalQuantity);
		edr.setLastUpdate(new Date());
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
			edr.setRejectReason("Subscription is null");
		} else {
			boolean edrIsRated = false;

			try {
				if (MeveoCacheContainerProvider.getUsageChargeInstanceCache().containsKey(edr.getSubscription().getId())) {
					// TODO:order charges by priority and id
					List<UsageChargeInstanceCache> charges = MeveoCacheContainerProvider.getUsageChargeInstanceCache()
							.get(edr.getSubscription().getId());
					reservation=new Reservation();
					reservation.setProvider(currentUser.getProvider());
					reservation.setReservationDate(edr.getEventDate());
					reservation.setExpiryDate(new Date(time+currentUser.getProvider().getPrepaidReservationExpirationDelayinMillisec()));;
					reservation.setStatus(ReservationStatus.OPEN);
					Auditable audit = new Auditable();
					audit.setCreated(new Date());
					audit.setCreator(currentUser);
					reservation.setAuditable(audit);
					reservation.setOriginEdr(edr);
					reservation.setQuantity(edr.getQuantity());
					//it would be nice to have a persistence context bound to the JTA transaction
					em.persist(reservation);

					for (UsageChargeInstanceCache charge : charges) {
						UsageChargeTemplateCache templateCache = charge
								.getTemplateCache();
						log.info("try templateCache="
								+ templateCache.toString());
						if (templateCache.getFilter1() == null
								|| templateCache.getFilter1().equals(
										edr.getParameter1())) {
							log.info("filter1 ok");
							if (templateCache.getFilter2() == null
									|| templateCache.getFilter2().equals(
											edr.getParameter2())) {
								log.info("filter2 ok");
								if (templateCache.getFilter3() == null
										|| templateCache.getFilter3().equals(
												edr.getParameter3())) {
									log.info("filter3 ok");
									if (templateCache.getFilter4() == null
											|| templateCache
													.getFilter4()
													.equals(edr.getParameter4())) {
										log.info("filter4 ok");
										if (templateCache.getFilterExpression() == null
												|| matchExpression(
														templateCache
																.getFilterExpression(),
														edr)) {
											log.info("filterExpression ok");
											// we found matching charge, if we
											// rate
											// it we exit the look
											log.debug("found matchig charge inst : id="
													+ charge.getChargeInstanceId());
											edrIsRated = reserveEDRonChargeAndCounters(
													reservation,edr, charge, currentUser);
											if (edrIsRated) {
												edr.setStatus(EDRStatusEnum.RATED);
												break;
											}
										}
									}
								}
							}
						}
					}

					if (!edrIsRated) {
						edr.setStatus(EDRStatusEnum.REJECTED);
						edr.setRejectReason("no matching charge");
					}
				} else {
					edr.setStatus(EDRStatusEnum.REJECTED);
					edr.setRejectReason("subscription has no usage charge");
				}
			} catch (Exception e) {
				edr.setStatus(EDRStatusEnum.REJECTED);
				edr.setRejectReason(e.getMessage());
				//e.printStackTrace();
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
		return (Boolean) RatingService.evaluateExpression(expression, userMap,
				Boolean.class);
	}

	private boolean matchExpression(String expression, EDR edr,
			WalletOperation walletOperation) throws BusinessException {
		boolean result=true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("edr", edr);
		userMap.put("op", walletOperation);
		if(expression.indexOf("ua")>=0){
			userMap.put("ua", walletOperation.getWallet().getUserAccount());
		}

		Object res = RatingService.evaluateExpression(expression, userMap, Boolean.class);
		try{
			result=(Boolean) res;
		} catch(Exception e){
			throw new BusinessException("Expression "+expression+" do not evaluate to boolean but "+res);
		}
		return result;
	}

	private String evaluateStringExpression(String expression, EDR edr,
			WalletOperation walletOperation) throws BusinessException {
		if(expression==null){
			return null;
		}
		String result=null;
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("edr", edr);
		userMap.put("op", walletOperation);
		if(expression.indexOf("ua")>=0){
			userMap.put("ua", walletOperation.getWallet().getUserAccount());
		}
		Object res = RatingService.evaluateExpression(expression, userMap, String.class);
		try{
			result=(String) res;
		} catch(Exception e){
			throw new BusinessException("Expression "+expression+" do not evaluate to string but "+res);
		}
		return result;
	}

	private Double evaluateDoubleExpression(String expression, EDR edr,
			WalletOperation walletOperation) throws BusinessException {
		Double result=null;
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("edr", edr);
		userMap.put("op", walletOperation);
		if(expression.indexOf("ua")>=0){
			userMap.put("ua", walletOperation.getWallet().getUserAccount());
		}
		Object res = RatingService.evaluateExpression(expression, userMap, Double.class);
		try{
			result=(Double) res;
		} catch(Exception e){
			throw new BusinessException("Expression "+expression+" do not evaluate to double but "+res);
		}
		
		return result;
	}


}
