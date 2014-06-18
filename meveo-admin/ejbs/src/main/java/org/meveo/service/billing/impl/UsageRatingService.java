package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.cache.CounterInstanceCache;
import org.meveo.model.cache.CounterPeriodCache;
import org.meveo.model.cache.UsageChargeInstanceCache;
import org.meveo.model.cache.UsageChargeTemplateCache;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class UsageRatingService {

	@Inject
	@MeveoJpa
	protected EntityManager em;

	@Inject
	protected Logger log;

	// @Resource(lookup="java:jboss/infinispan/container/meveo")
	// private CacheContainer meveoContainer;

	// private org.infinispan.Cache<Long, List<UsageChargeInstanceCache>>
	// chargeCache;
	// private org.infinispan.Cache<Long, CounterInstanceCache> counterCache;
	private static HashMap<String, UsageChargeTemplateCache> chargeTemplateCache;
	private static HashMap<Long, List<UsageChargeInstanceCache>> chargeCache;
	private static HashMap<Long, CounterInstanceCache> counterCache;

	private static boolean cacheLoaded = false;

	@EJB
	private UsageChargeInstanceService usageChargeInstanceService;

	@EJB
	private CounterInstanceService counterInstanceService;

	@EJB
	private RatingService ratingService;

	@EJB
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@EJB
	private WalletOperationService walletOperationService;

	@PostConstruct
	public synchronized void updateCacheFromDB() {
		if (!cacheLoaded) {
			chargeTemplateCache = new HashMap<String, UsageChargeTemplateCache>();
			// this.chargeCache = this.meveoContainer.getCache("usageCharge");
			chargeCache = new HashMap<Long, List<UsageChargeInstanceCache>>();
			// this.counterCache = this.meveoContainer.getCache("counter");
			counterCache = new HashMap<Long, CounterInstanceCache>();
			log.info("loading usage charge cache");
			@SuppressWarnings("unchecked")
			List<UsageChargeInstance> usageChargeInstances = em.createQuery(
					"From UsageChargeInstance u").getResultList();
			if (usageChargeInstances != null) {
				for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {
					updateCache(usageChargeInstance);
				}
			}
			cacheLoaded = true;
		}
	}

	public UsageChargeTemplateCache updateTemplateCache(
			UsageChargeTemplate usageChargeTemplate) {
		UsageChargeTemplateCache cachedValue = null;
		if (usageChargeTemplate != null) {
			log.info("updateTemplateCache " + usageChargeTemplate.getCode());
			if (chargeTemplateCache.containsKey(usageChargeTemplate.getCode())) {
				log.info("cache already contains the code");
				cachedValue = chargeTemplateCache.get(usageChargeTemplate
						.getCode());
			} else {
				log.info("cache does not contain the code");
				cachedValue = new UsageChargeTemplateCache();
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
		List<UsageChargeInstanceCache> charges = chargeCache.get(id);
		Collections.sort(charges);
		log.info("sorted " + charges.size() + " charges");
	}

	public void updateCache(UsageChargeInstance usageChargeInstance) {
		if (usageChargeInstance != null) {

			UsageChargeInstanceCache cachedValue = new UsageChargeInstanceCache();
			ChargeTemplate chargeTemplate = usageChargeInstance
					.getChargeTemplate();
			System.out.println(chargeTemplate.getId());
			// UsageChargeTemplate usageChargeTemplate=(UsageChargeTemplate)
			// usageChargeInstance.getChargeTemplate();
			UsageChargeTemplate usageChargeTemplate = em.find(
					UsageChargeTemplate.class, chargeTemplate.getId());
			Long key = usageChargeInstance.getServiceInstance()
					.getSubscription().getId();
			log.info("update cache key (subs Id)=" + key + "'for charge"
					+ usageChargeInstance.getId());
			boolean cacheContainsKey = chargeCache.containsKey(key);
			boolean cacheContainsCharge = false;

			List<UsageChargeInstanceCache> charges = null;
			if (cacheContainsKey) {
				log.info("the cache contains the key");
				charges = chargeCache.get(key);
				for (UsageChargeInstanceCache charge : charges) {
					if (charge.getChargeInstanceId() == usageChargeInstance
							.getId()) {
						if (usageChargeInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
							log.info("the cache contains the charge but its status in db is not active so we remove it");
							charges.remove(charge);
							if (charges.size() == 0) {
								chargeCache.remove(key);
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

			cachedValue.setChargeDate(usageChargeInstance.getChargeDate());
			cachedValue.setChargeInstanceId(usageChargeInstance.getId());
			cachedValue.setProvider(usageChargeInstance.getProvider());
			cachedValue
					.setCurrencyId(usageChargeInstance.getCurrency().getId());
			if (usageChargeInstance.getCounter() != null) {
				CounterInstanceCache counterCacheValue = null;
				Long counterKey = CounterInstanceCache
						.getKey(usageChargeInstance.getCounter());

				log.info("counter key:" + counterKey);
				if (counterCache.containsKey(counterKey)) {
					log.info("the counter cache contains the key");
					counterCacheValue = counterCache.get(counterKey);
				} else {
					log.info("the counter cache doesnt contain the key, we add it");
					counterCacheValue = CounterInstanceCache
							.getInstance(usageChargeInstance.getCounter());
					counterCache.put(counterKey, counterCacheValue);
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
				log.info("charge added");
				charges.add(cachedValue);
			}
			if (!cacheContainsKey) {
				log.info("key added to charge cache");
				chargeCache.put(key, charges);
			}
			reorderChargeCache(key);
		}
	}

	// @PreDestroy
	// accessing Entity manager in predestroy is bugged in jboss7.1.3
	void saveCounters() {
		for (Long key : counterCache.keySet()) {
			CounterInstanceCache counterInstanceCache = counterCache.get(key);
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
						System.out.println("save counter"
								+ itemPeriodCache.getCounterPeriodId()
								+ " new value=" + itemPeriodCache.getValue());
						// calling ejb in this predestroy method just fail...
						// counterInstanceService.updatePeriodValue(itemPeriodCache.getCounterPeriodId(),itemPeriodCache.getValue());
					}
				}
			}
		}
	}

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
			UsageChargeInstanceCache chargeCache,
			UsageChargeInstance chargeInstance, Provider provider)
			throws BusinessException {
		WalletOperation walletOperation = new WalletOperation();
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
				.findInvoiceSubCategoryCountry(invoiceSubCat.getId(), countryId);
		if(invoiceSubcategoryCountry==null){
			throw new BusinessException("nos tax defined for countryId="+countryId+" in invoice Sub-Category="+invoiceSubCat.getCode());
		}
		TradingCurrency currency = edr.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getTradingCurrency();
		Tax tax = invoiceSubcategoryCountry.getTax();
		walletOperation.setChargeInstance(chargeInstance);
		walletOperation.setSeller(edr.getSubscription().getUserAccount()
				.getBillingAccount().getCustomerAccount().getCustomer()
				.getSeller());
		// FIXME: get the wallet from the ServiceUsageChargeTemplate
		walletOperation.setWallet(edr.getSubscription().getUserAccount()
				.getWallet());
		walletOperation.setCode(chargeInstance.getCode());
		walletOperation.setQuantity(edr.getQuantity());
		walletOperation.setTaxPercent(tax.getPercent());
		walletOperation.setStartDate(null);
		walletOperation.setEndDate(null);
		walletOperation.setCurrency(currency.getCurrency());
		if (chargeInstance.getCounter() != null) {
			walletOperation.setCounter(chargeInstance.getCounter());
		}
		walletOperation.setStatus(WalletOperationStatusEnum.OPEN);
		log.info("provider code:" + provider.getCode());
		ratingService.rateBareWalletOperation(walletOperation, null, null,
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
	 */
	BigDecimal deduceCounter(EDR edr, UsageChargeInstanceCache charge) {
		log.info("deduce counter for key " + charge.getCounter().getKey());
		BigDecimal deducedQuantity = BigDecimal.ZERO;
		CounterInstanceCache counterInstanceCache = counterCache.get(charge
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
					log.info("found counter period in cache:" + periodCache);
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
					counterInstance, edr.getEventDate());
			periodCache = CounterPeriodCache.getInstance(counterPeriod,
					counterInstance.getCounterTemplate());
			counterInstanceCache.getCounterPeriods().add(periodCache);
			log.info("created counter period in cache:" + periodCache);
		}
		synchronized (periodCache) {
			BigDecimal countedValue = edr.getQuantity().multiply(
					charge.getUnityMultiplicator());
			log.info("value to deduce " + edr.getQuantity() + "*"
					+ charge.getUnityMultiplicator() + "=" + countedValue);
			if (charge.getUnityNbDecimal() > 0) {
				int rounding = (charge.getUnityNbDecimal()>BaseEntity.NB_DECIMALS)?BaseEntity.NB_DECIMALS:charge.getUnityNbDecimal();
				countedValue = countedValue.setScale(
						rounding, RoundingMode.HALF_UP);
			}
			if (periodCache.getValue().compareTo(BigDecimal.ZERO) > 0) {
				if (periodCache.getValue().compareTo(countedValue) < 0) {
					deducedQuantity = periodCache.getValue();
					periodCache.setValue(BigDecimal.ZERO);
				} else {
					deducedQuantity = countedValue;
					periodCache.setValue(periodCache.getValue().subtract(
							countedValue));
				}
				// set the cache element to dirty so it is saved to DB when
				// shutdown the server
				// periodCache.setDbDirty(true);
				counterInstanceService.updatePeriodValue(
						periodCache.getCounterPeriodId(),
						periodCache.getValue());
			}
			// put back the deduced quantity in charge unit
			deducedQuantity = deducedQuantity.divide(charge
					.getUnityMultiplicator());
		}
		return deducedQuantity;
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
			UsageChargeInstanceCache charge) throws BusinessException {
		boolean stopEDRRating = false;
		BigDecimal deducedQuantity = null;
		if (charge.getCounter() != null) {

			// if the charge is associated to a counter and we can decrement it
			// then
			// we rate the charge if not we simply try the next charge
			// if the counter has been decremented by the full quantity we stop
			// the rating
			deducedQuantity = deduceCounter(edr, charge);
			if (edr.getQuantity().equals(deducedQuantity)) {
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
					charge, chargeInstance, provider);
			if (deducedQuantity != null) {
				edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
				walletOperation.setQuantity(deducedQuantity);
			}
			walletOperationService.create(walletOperation, null, provider);
		}
		return stopEDRRating;
	}

	/**
	 * Rate an EDR using counters if they apply
	 * 
	 * @param edr
	 */
	// TODO: this is only for postpaid wallets, for prepaid we dont need to
	// check counters
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ratePostpaidUsage(EDR edr) {
		BigDecimal originalQuantity = edr.getQuantity();
		if (edr.getSubscription() == null) {
			edr.setStatus(EDRStatusEnum.REJECTED);
			edr.setRejectReason("subscription null");
		} else {
			boolean edrIsRated = false;
			try {
				if (chargeCache.containsKey(edr.getSubscription().getId())) {
					// TODO:order charges by priority and id
					List<UsageChargeInstanceCache> charges = chargeCache
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
										if (templateCache.getFilterExpression() != null) {
											// TODO: implement EL expression
											// javax.el.ELContext elContext =
											// javax.faces.context.FacesContext.getCurrentInstance().getELContext();
											// javax.el.ExpressionFactory
											// expressionFactory =
										}
										// we found matching charge, if we rate
										// it we exit the look
										edrIsRated = rateEDRonChargeAndCounters(
												edr, charge);
										if (edrIsRated) {
											edr.setStatus(EDRStatusEnum.RATED);
											break;
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
				e.printStackTrace();
			}
		}
		// put back the original quantity in edr (could have been decrease by
		// counters)
		edr.setQuantity(originalQuantity);
		edr.setLastUpdate(new Date());
	}
}
