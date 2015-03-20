package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
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
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
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
	private RatingService ratingService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private SubscriptionService subscriptionService;
	
    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

	// @PreDestroy
	// accessing Entity manager in predestroy is bugged in jboss7.1.3
	/*
	 * void saveCounters() { for (Long key :
	 * MeveoCacheContainerProvider.getCounterCache().keySet()) {
	 * CounterInstanceCache counterInstanceCache =
	 * MeveoCacheContainerProvider.getCounterCache().get(key); if
	 * (counterInstanceCache.getCounterPeriods() != null) { for
	 * (CounterPeriodCache itemPeriodCache : counterInstanceCache
	 * .getCounterPeriods()) { if (itemPeriodCache.isDbDirty()) { CounterPeriod
	 * counterPeriod = em.find( CounterPeriod.class,
	 * itemPeriodCache.getCounterPeriodId());
	 * counterPeriod.setValue(itemPeriodCache.getValue());
	 * counterPeriod.getAuditable().setUpdated(new Date());
	 * em.merge(counterPeriod);
	 * log.debug("save counter with id={}, new value={}",
	 * itemPeriodCache.getCounterPeriodId(), itemPeriodCache.getValue()); //
	 * calling ejb in this predestroy method just fail... //
	 * counterInstanceService
	 * .updatePeriodValue(itemPeriodCache.getCounterPeriodId
	 * (),itemPeriodCache.getValue()); } } } } }
	 */

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
	public WalletOperation rateEDRwithMatchingCharge(EDR edr, BigDecimal deducedQuantity,
			UsageChargeInstanceCache chargeCache, UsageChargeInstance chargeInstance, Provider provider,
			boolean isReservation) throws BusinessException {
		WalletOperation walletOperation = null;
		if (isReservation) {
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
		InvoiceSubCategory invoiceSubCat = chargeInstance.getChargeTemplate().getInvoiceSubCategory();
		TradingCountry country = chargeInstance.getSubscription().getUserAccount().getBillingAccount().getTradingCountry();
		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCat.getId(), countryId, provider);

		if (invoiceSubcategoryCountry == null) {
			throw new BusinessException("No tax defined for countryId=" + countryId + " in invoice Sub-Category="
					+ invoiceSubCat.getCode());
		}

		TradingCurrency currency = chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
				.getTradingCurrency();
		Tax tax = invoiceSubcategoryCountry.getTax();

		walletOperation.setChargeInstance(chargeInstance);
		walletOperation.setUnityDescription(chargeInstance.getUnityDescription());
		walletOperation.setSeller(chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
				.getCustomer().getSeller());
		// we set here the wallet to the pricipal wallet but it will later be
		// overriden by charging algo
		walletOperation.setWallet(chargeInstance.getSubscription().getUserAccount().getWallet());
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

		walletOperation.setOfferCode(chargeInstance.getSubscription().getOffer().getCode());
		walletOperation.setStatus(WalletOperationStatusEnum.OPEN);

		// log.info("provider code:" + provider.getCode());
		ratingService.rateBareWalletOperation(walletOperation, chargeInstance.getAmountWithoutTax(),
				chargeInstance.getAmountWithTax(), countryId, currency, provider);

		return walletOperation;
	}

	/**
	 * This method first look if there is a counter and a
	 * 
	 * @param edr
	 * @param cachedCharge
	 * @return if edr quantity fits partially in the counter, returns the
	 *         remaining quantity
	 * @throws BusinessException
	 */
	BigDecimal deduceCounter(EDR edr, UsageChargeInstanceCache cachedCharge, Reservation reservation, User currentUser)
			throws BusinessException {
		log.info("Deduce counter for key " + cachedCharge.getCounter().getCounterInstanceId());

		BigDecimal deducedQuantity = BigDecimal.ZERO;
		BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;
		CounterInstanceCache counterInstanceCache = ratingCacheContainerProvider.getCounterInstance(
				cachedCharge.getCounter().getCounterInstanceId());
		CounterPeriodCache periodCache = null;

		if (counterInstanceCache.getCounterPeriods() != null) {
			for (CounterPeriodCache itemPeriodCache : counterInstanceCache.getCounterPeriods()) {
				if ((itemPeriodCache.getStartDate().before(edr.getEventDate()) || itemPeriodCache.getStartDate()
						.equals(edr.getEventDate())) && itemPeriodCache.getEndDate().after(edr.getEventDate())) {
					periodCache = itemPeriodCache;
					log.info("Found counter period in cache:" + periodCache);
					break;
				}
			}
		} else {
			counterInstanceCache.setCounterPeriods(new ArrayList<CounterPeriodCache>());
		}

		CounterInstance counterInstance = null;
		if (periodCache == null) {
			counterInstance = counterInstanceService.findById(counterInstanceCache.getCounterInstanceId());
			CounterPeriod counterPeriod = counterInstanceService.createPeriod(counterInstance, edr.getEventDate(),
					cachedCharge.getSubscriptionDate(), currentUser);
			if (counterPeriod != null) {
				periodCache = CounterPeriodCache.getInstance(counterPeriod, counterInstance.getCounterTemplate());
				counterInstanceCache.getCounterPeriods().add(periodCache);

				log.debug("created counter period in cache:{}", periodCache);
			}
		}

		if (periodCache != null) {
			synchronized (periodCache) {
				BigDecimal countedValue = cachedCharge.getInChargeUnit(edr.getQuantity());
				log.debug("value to deduce {} * {} = {} from current value {}", edr.getQuantity(),
						cachedCharge.getUnityMultiplicator(), countedValue, periodCache.getValue());

				if (periodCache.getLevel() == null) {
					deducedQuantity = countedValue;
				} else if (periodCache.getValue().compareTo(BigDecimal.ZERO) > 0) {
					if (periodCache.getValue().compareTo(countedValue) < 0) {
						deducedQuantity = periodCache.getValue();
						periodCache.setValue(BigDecimal.ZERO);
						deducedQuantityInEDRUnit = cachedCharge.getInEDRUnit(deducedQuantity);
						log.debug("we deduced {} and set the counter period value to 0", deducedQuantity);
					} else {
						deducedQuantity = countedValue;
						periodCache.setValue(periodCache.getValue().subtract(countedValue));
						log.debug("we deduced {} and set the counter period value to {}", deducedQuantity,
								periodCache.getValue());
						deducedQuantityInEDRUnit = edr.getQuantity();
					}
					if (reservation != null && deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {
						reservation.getCounterPeriodValues().put(periodCache.getCounterPeriodId(), deducedQuantity);
					}
					// set the cache element to dirty so it is saved to DB when
					// shutdown the server
					// periodCache.setDbDirty(true);
					counterInstanceService.updatePeriodValue(periodCache.getCounterPeriodId(), periodCache.getValue(),
							currentUser);
				}

				// put back the deduced quantity in charge unit

				log.debug("in original EDR units, we deduced {}", deducedQuantityInEDRUnit);
			}
		}
		return deducedQuantityInEDRUnit;
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
	public boolean rateEDRonChargeAndCounters(EDR edr, UsageChargeInstanceCache charge, User currentUser)
			throws BusinessException {
		boolean stopEDRRating = false;
		BigDecimal deducedQuantity = null;

		if (charge.getCounter() != null) {
			// if the charge is associated to a counter and we can decrement it
			// then we rate the charge if not we simply try the next charge
			// if the counter has been decremented by the full quantity we stop
			// the rating
			deducedQuantity = deduceCounter(edr, charge, null, currentUser);
			if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
				stopEDRRating = true;
			}
		} else {
			stopEDRRating = true;
		}

		if (deducedQuantity == null || deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {
			Provider provider = charge.getProvider();
			UsageChargeInstance chargeInstance = usageChargeInstanceService.findById(charge.getChargeInstanceId());
			WalletOperation walletOperation = null;
			if (deducedQuantity == null) {
				walletOperation = rateEDRwithMatchingCharge(edr, charge.getInChargeUnit(edr.getQuantity()), charge,
						chargeInstance, provider, false);

			} else {
				edr.setQuantity(edr.getQuantity().subtract(deducedQuantity));
				walletOperation = rateEDRwithMatchingCharge(edr, charge.getInChargeUnit(deducedQuantity), charge,
						chargeInstance, provider, false);
			}
			walletOperationService.chargeWalletOperation(walletOperation, currentUser, provider);

			// handle associated edr creation
			if (charge.getTemplateCache().getEdrTemplates().size() > 0) {
				for (TriggeredEDRCache triggeredEDRCache : charge.getTemplateCache().getEdrTemplates()) {
					if (triggeredEDRCache.getConditionEL() == null || "".equals(triggeredEDRCache.getConditionEL())
							|| matchExpression(triggeredEDRCache.getConditionEL(), edr, walletOperation)) {
						EDR newEdr = new EDR();
						newEdr.setCreated(new Date());
						newEdr.setEventDate(edr.getEventDate());
						newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
						newEdr.setOriginRecord("" + walletOperation.getId());
						newEdr.setParameter1(evaluateStringExpression(triggeredEDRCache.getParam1EL(), edr,
								walletOperation));
						newEdr.setParameter2(evaluateStringExpression(triggeredEDRCache.getParam2EL(), edr,
								walletOperation));
						newEdr.setParameter3(evaluateStringExpression(triggeredEDRCache.getParam3EL(), edr,
								walletOperation));
						newEdr.setParameter4(evaluateStringExpression(triggeredEDRCache.getParam4EL(), edr,
								walletOperation));
						newEdr.setProvider(edr.getProvider());
						newEdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRCache.getQuantityEL(),
								edr, walletOperation)));
						newEdr.setStatus(EDRStatusEnum.OPEN);
						Subscription sub = edr.getSubscription();
						if (!StringUtils.isBlank(triggeredEDRCache.getSubscriptionEL())) {
							String subCode = evaluateStringExpression(triggeredEDRCache.getSubscriptionEL(), edr,
									walletOperation);
							sub = subscriptionService.findByCode(subCode, provider);
							if (sub == null) {
								throw new BusinessException("could not find subscription for code =" + subCode
										+ " (EL=" + triggeredEDRCache.getSubscriptionEL()
										+ ") in triggered EDR with code " + triggeredEDRCache.getCode());
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

	public boolean reserveEDRonChargeAndCounters(Reservation reservation, EDR edr, UsageChargeInstanceCache charge,
			User currentUser) throws BusinessException {
		boolean stopEDRRating = false;
		BigDecimal deducedQuantity = null;
		if (charge.getCounter() != null) {
			deducedQuantity = deduceCounter(edr, charge, reservation, currentUser);
			if (edr.getQuantity().compareTo(deducedQuantity) == 0) {
				stopEDRRating = true;
			}
		} else {
			stopEDRRating = true;
		}

		if (deducedQuantity == null || deducedQuantity.compareTo(BigDecimal.ZERO) > 0) {
			Provider provider = charge.getProvider();
			UsageChargeInstance chargeInstance = usageChargeInstanceService.findById(charge.getChargeInstanceId());
			WalletReservation walletOperation = (WalletReservation) rateEDRwithMatchingCharge(edr, deducedQuantity,
					charge, chargeInstance, provider, true);
			walletOperation.setReservation(reservation);
			walletOperation.setStatus(WalletOperationStatusEnum.RESERVED);
			reservation.setAmountWithoutTax(reservation.getAmountWithoutTax()
					.add(walletOperation.getAmountWithoutTax()));
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
		rateUsageWithinTransaction(edr, currentUser);
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
				if (ratingCacheContainerProvider.isUsageChargeInstancesCached(edr.getSubscription().getId())) {
					// TODO:order charges by priority and id
					List<UsageChargeInstanceCache> charges = ratingCacheContainerProvider.getUsageChargeInstances(edr.getSubscription().getId());

					for (UsageChargeInstanceCache charge : charges) {
						UsageChargeTemplateCache templateCache = charge.getTemplateCache();
						log.info("try templateCache=" + templateCache.toString());
						if (templateCache.getFilter1() == null
								|| templateCache.getFilter1().equals(edr.getParameter1())) {
							log.info("filter1 ok");
							if (templateCache.getFilter2() == null
									|| templateCache.getFilter2().equals(edr.getParameter2())) {
								log.info("filter2 ok");
								if (templateCache.getFilter3() == null
										|| templateCache.getFilter3().equals(edr.getParameter3())) {
									log.info("filter3 ok");
									if (templateCache.getFilter4() == null
											|| templateCache.getFilter4().equals(edr.getParameter4())) {
										log.info("filter4 ok");
										if (templateCache.getFilterExpression() == null
												|| matchExpression(templateCache.getFilterExpression(), edr)) {
											log.info("filterExpression ok");
											// we found matching charge, if we
											// rate
											// it we exit the look
											log.debug("found matchig charge inst : id=" + charge.getChargeInstanceId());
											edrIsRated = rateEDRonChargeAndCounters(edr, charge, currentUser);
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
				log.error(e.getMessage());
				edr.setStatus(EDRStatusEnum.REJECTED);
				edr.setRejectReason(e.getMessage());
				throw new BusinessException(e);
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
				if (ratingCacheContainerProvider.isUsageChargeInstancesCached(edr.getSubscription().getId())) {
					// TODO:order charges by priority and id
					List<UsageChargeInstanceCache> charges = ratingCacheContainerProvider.getUsageChargeInstances(edr.getSubscription().getId());
					reservation = new Reservation();
					reservation.setProvider(currentUser.getProvider());
					reservation.setReservationDate(edr.getEventDate());
					reservation.setExpiryDate(new Date(time
							+ currentUser.getProvider().getPrepaidReservationExpirationDelayinMillisec()));
					;
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

					for (UsageChargeInstanceCache charge : charges) {
						UsageChargeTemplateCache templateCache = charge.getTemplateCache();
						log.info("try templateCache=" + templateCache.toString());
						if (templateCache.getFilter1() == null
								|| templateCache.getFilter1().equals(edr.getParameter1())) {
							log.info("filter1 ok");
							if (templateCache.getFilter2() == null
									|| templateCache.getFilter2().equals(edr.getParameter2())) {
								log.info("filter2 ok");
								if (templateCache.getFilter3() == null
										|| templateCache.getFilter3().equals(edr.getParameter3())) {
									log.info("filter3 ok");
									if (templateCache.getFilter4() == null
											|| templateCache.getFilter4().equals(edr.getParameter4())) {
										log.info("filter4 ok");
										if (templateCache.getFilterExpression() == null
												|| matchExpression(templateCache.getFilterExpression(), edr)) {
											log.info("filterExpression ok");
											// we found matching charge, if we
											// rate
											// it we exit the look
											log.debug("found matchig charge inst : id=" + charge.getChargeInstanceId());
											edrIsRated = reserveEDRonChargeAndCounters(reservation, edr, charge,
													currentUser);
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
				// e.printStackTrace();
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
		return (Boolean) RatingService.evaluateExpression(expression, userMap, Boolean.class);
	}

	private boolean matchExpression(String expression, EDR edr, WalletOperation walletOperation)
			throws BusinessException {
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

		Object res = RatingService.evaluateExpression(expression, userMap, Boolean.class);
		try {
			result = (Boolean) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
		}
		return result;
	}

	private String evaluateStringExpression(String expression, EDR edr, WalletOperation walletOperation)
			throws BusinessException {
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
		Object res = RatingService.evaluateExpression(expression, userMap, String.class);
		try {
			result = (String) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
		}
		return result;
	}

	private Double evaluateDoubleExpression(String expression, EDR edr, WalletOperation walletOperation)
			throws BusinessException {
		Double result = null;
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("edr", edr);
		userMap.put("op", walletOperation);
		if (expression.indexOf("ua") >= 0) {
			userMap.put("ua", walletOperation.getWallet().getUserAccount());
		}
		Object res = RatingService.evaluateExpression(expression, userMap, Double.class);
		try {
			result = (Double) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
		}

		return result;
	}

}
