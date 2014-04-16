package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class RatingService {

	@Inject
	@MeveoJpa
	protected EntityManager entityManager;

	@Inject
	protected Logger log;

	@Inject
	protected CatMessagesService catMessagesService;

	private static boolean isPricePlanDirty;
	private static HashMap<String, HashMap<String, List<PricePlanMatrix>>> allPricePlan;

	private static final BigDecimal HUNDRED = new BigDecimal("100");

	public static void setPricePlanDirty() {
		isPricePlanDirty = true;
	}

	public int getSharedQuantity(LevelEnum level, Provider provider,
			String chargeCode, Date chargeDate,
			RecurringChargeInstance recChargeInstance) {
		return getSharedQuantity(entityManager, level, provider, chargeCode,
				chargeDate, recChargeInstance);
	}

	public int getSharedQuantity(EntityManager em, LevelEnum level,
			Provider provider, String chargeCode, Date chargeDate,
			RecurringChargeInstance recChargeInstance) {
		int result = 0;
		try {
			String strQuery = "select SUM(r.serviceInstance.quantity) from "
					+ RecurringChargeInstance.class.getSimpleName()
					+ " r "
					+ "WHERE r.code=:chargeCode "
					+ "AND r.subscriptionDate<=:chargeDate "
					+ "AND (r.serviceInstance.terminationDate is NULL OR r.serviceInstance.terminationDate>:chargeDate) "
					+ "AND r.provider=:provider ";
			switch (level) {
			case BILLING_ACCOUNT:
				strQuery += "AND r.subscription.userAccount.billingAccount=:billingAccount ";
				break;
			case CUSTOMER:
				strQuery += "AND r.subscription.userAccount.billingAccount.customerAccount.customer=:customer ";
				break;
			case CUSTOMER_ACCOUNT:
				strQuery += "AND r.subscription.userAccount.billingAccount.customerAccount=:customerAccount ";
				break;
			case PROVIDER:
				break;
			case SELLER:
				strQuery += "AND r.subscription.userAccount.billingAccount.customerAccount.customer.seller=:seller ";
				break;
			case USER_ACCOUNT:
				strQuery += "AND r.subscription.userAccount=:userAccount ";
				break;
			default:
				break;

			}
			Query query = em.createQuery(strQuery);
			query.setParameter("chargeCode", chargeCode);
			query.setParameter("chargeDate", chargeDate);
			query.setParameter("provider", provider);
			switch (level) {
			case BILLING_ACCOUNT:
				query.setParameter("billingAccount", recChargeInstance
						.getSubscription().getUserAccount().getBillingAccount());
				break;
			case CUSTOMER:
				query.setParameter("customer", recChargeInstance
						.getSubscription().getUserAccount().getBillingAccount()
						.getCustomerAccount().getCustomer());
				break;
			case CUSTOMER_ACCOUNT:
				query.setParameter("customerAccount", recChargeInstance
						.getSubscription().getUserAccount().getBillingAccount()
						.getCustomerAccount());
				break;
			case PROVIDER:
				break;
			case SELLER:
				query.setParameter("seller", recChargeInstance
						.getSubscription().getUserAccount().getBillingAccount()
						.getCustomerAccount().getCustomer().getSeller());
				break;
			case USER_ACCOUNT:
				query.setParameter("userAccount", recChargeInstance
						.getSubscription().getUserAccount());
				break;
			default:
				break;

			}
			Number sharedQuantity = (Number) query.getSingleResult();
			result = sharedQuantity.intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public WalletOperation prerateChargeApplication(String code,
			Date subscriptionDate, ChargeInstance chargeInstance,
			ApplicationTypeEnum applicationType, Date applicationDate,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax,
			BigDecimal quantity, TradingCurrency tCurrency, Long countryId,
			BigDecimal taxPercent, BigDecimal discountPercent,
			Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory,
			String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode)
			throws BusinessException {
		return prerateChargeApplication(entityManager, code, subscriptionDate,
				chargeInstance, applicationType, applicationDate,
				amountWithoutTax, amountWithTax, quantity, tCurrency,
				countryId, taxPercent, discountPercent, nextApplicationDate,
				invoiceSubCategory, criteria1, criteria2, criteria3, startdate,
				endDate, mode);
	}

	// used to prerate a oneshot or recurring charge
	public WalletOperation prerateChargeApplication(EntityManager em,
			String code, Date subscriptionDate, ChargeInstance chargeInstance,
			ApplicationTypeEnum applicationType, Date applicationDate,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax,
			BigDecimal quantity, TradingCurrency tCurrency, Long countryId,
			BigDecimal taxPercent, BigDecimal discountPercent,
			Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory,
			String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode)
			throws BusinessException {

		WalletOperation result = new WalletOperation();

		if (chargeInstance instanceof RecurringChargeInstance) {
			result.setSubscriptionDate(subscriptionDate);
		}

		Provider provider = chargeInstance.getProvider();

		result.setOperationDate(applicationDate);
		result.setParameter1(criteria1);
		result.setParameter2(criteria2);
		result.setParameter3(criteria3);
		result.setProvider(provider);
		result.setChargeInstance(chargeInstance);
		result.setCode(code);
		result.setQuantity(quantity);
		result.setTaxPercent(taxPercent);
		result.setCurrency(tCurrency.getCurrency());
		result.setStartDate(startdate);
		result.setEndDate(endDate);
		result.setStatus(WalletOperationStatusEnum.OPEN);
		result.setSeller(chargeInstance.getSeller());

		BigDecimal unitPriceWithoutTax = amountWithoutTax;
		BigDecimal unitPriceWithTax = null;

		if (unitPriceWithoutTax != null) {
			unitPriceWithTax = amountWithTax;
		}

		rateBareWalletOperation(em, result, unitPriceWithoutTax,
				unitPriceWithTax, countryId, tCurrency, provider);

		return result;

	}

	public WalletOperation rateChargeApplication(String code,
			Subscription subscription, ChargeInstance chargeInstance,
			ApplicationTypeEnum applicationType, Date applicationDate,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax,
			BigDecimal quantity, TradingCurrency tCurrency, Long countryId,
			BigDecimal taxPercent, BigDecimal discountPercent,
			Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory,
			String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode)
			throws BusinessException {
		return rateChargeApplication(entityManager, code, subscription,
				chargeInstance, applicationType, applicationDate,
				amountWithoutTax, amountWithTax, quantity, tCurrency,
				countryId, taxPercent, discountPercent, nextApplicationDate,
				invoiceSubCategory, criteria1, criteria2, criteria3, startdate,
				endDate, mode);
	}

	// used to rate a oneshot or recurring charge
	public WalletOperation rateChargeApplication(EntityManager em, String code,
			Subscription subscription, ChargeInstance chargeInstance,
			ApplicationTypeEnum applicationType, Date applicationDate,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax,
			BigDecimal quantity, TradingCurrency tCurrency, Long countryId,
			BigDecimal taxPercent, BigDecimal discountPercent,
			Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory,
			String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode)
			throws BusinessException {
		Date subscriptionDate = null;

		if (chargeInstance instanceof RecurringChargeInstance) {
			subscriptionDate = ((RecurringChargeInstance) chargeInstance)
					.getServiceInstance().getSubscriptionDate();
		}

		WalletOperation result = prerateChargeApplication(em, code,
				subscriptionDate, chargeInstance, applicationType,
				applicationDate, amountWithoutTax, amountWithTax, quantity,
				tCurrency, countryId, taxPercent, discountPercent,
				nextApplicationDate, invoiceSubCategory, criteria1, criteria2,
				criteria3, startdate, endDate, mode);

		result.setWallet(subscription.getUserAccount().getWallet());
		String chargeInstnceLabel = null;
		try {
			String languageCode = subscription.getUserAccount()
					.getBillingAccount().getTradingLanguage().getLanguage()
					.getLanguageCode();
			CatMessages catMessage = catMessagesService.getCatMessages(
					chargeInstance.getClass().getSimpleName() + "_"
							+ chargeInstance.getId(), languageCode);
			chargeInstnceLabel = catMessage != null ? catMessage
					.getDescription() : null;
		} catch (Exception e) {
		}
		result.setDescription(chargeInstnceLabel != null ? chargeInstnceLabel
				: chargeInstance.getDescription());

		return result;
	}

	public void rateBareWalletOperation(WalletOperation bareWalletOperation,
			BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax,
			Long countryId, TradingCurrency tcurrency, Provider provider)
			throws BusinessException {
		rateBareWalletOperation(entityManager, bareWalletOperation,
				unitPriceWithoutTax, unitPriceWithTax, countryId, tcurrency,
				provider);
	}

	// used to rate or rerate a bareWalletOperation
	public void rateBareWalletOperation(EntityManager em,
			WalletOperation bareWalletOperation,
			BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax,
			Long countryId, TradingCurrency tcurrency, Provider provider)
			throws BusinessException {

		PricePlanMatrix ratePrice = null;
		String providerCode = provider.getCode();

		if (unitPriceWithoutTax == null) {
			if (allPricePlan == null) {
				loadPricePlan(em);
			} else if (isPricePlanDirty) {
				reloadPricePlan();
			}
			if (!allPricePlan.containsKey(providerCode)) {
				throw new RuntimeException("no price plan for provider "
						+ providerCode);
			}
			if (!allPricePlan.get(providerCode).containsKey(
					bareWalletOperation.getCode())) {
				throw new RuntimeException("no price plan for provider "
						+ providerCode + " and charge code "
						+ bareWalletOperation.getCode());
			}
			ratePrice = ratePrice(
					allPricePlan.get(providerCode).get(
							bareWalletOperation.getCode()),
					bareWalletOperation,
					countryId,
					tcurrency,
					bareWalletOperation.getSeller() != null ? bareWalletOperation
							.getSeller().getId() : null);
			if (ratePrice == null || ratePrice.getAmountWithoutTax() == null) {
				throw new BusinessException("Invalid price plan for provider "
						+ providerCode + " and charge code "
						+ bareWalletOperation.getCode());
			} else {
				log.info("found ratePrice:" + ratePrice.getId() + " priceHT="
						+ ratePrice.getAmountWithoutTax() + " priceTTC="
						+ ratePrice.getAmountWithTax());
				unitPriceWithoutTax = ratePrice.getAmountWithoutTax();
				unitPriceWithTax = ratePrice.getAmountWithTax();
			}
		}
		// if the wallet operation correspond to a recurring charge that is
		// shared, we divide the price by the number of
		// shared charges
		if (bareWalletOperation.getChargeInstance() instanceof RecurringChargeInstance) {
			RecurringChargeTemplate recChargeTemplate = ((RecurringChargeInstance) bareWalletOperation
					.getChargeInstance()).getRecurringChargeTemplate();
			if (recChargeTemplate.getShareLevel() != null) {
				RecurringChargeInstance recChargeInstance = (RecurringChargeInstance) bareWalletOperation
						.getChargeInstance();
				int sharedQuantity = getSharedQuantity(
						recChargeTemplate.getShareLevel(), provider,
						recChargeInstance.getCode(),
						bareWalletOperation.getOperationDate(),
						recChargeInstance);
				if (sharedQuantity > 0) {
					unitPriceWithoutTax = unitPriceWithoutTax.divide(
							new BigDecimal(sharedQuantity),
							BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
					if (unitPriceWithTax != null) {
						unitPriceWithTax = unitPriceWithTax.divide(
								new BigDecimal(sharedQuantity),
								BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
					}
					log.info("charge is shared " + sharedQuantity
							+ " times, so unit price is " + unitPriceWithoutTax);
				}
			}
		}

		BigDecimal priceWithoutTax = bareWalletOperation.getQuantity()
				.multiply(unitPriceWithoutTax);
		BigDecimal priceWithTax = null;
		BigDecimal amountTax = BigDecimal.ZERO;
		if (bareWalletOperation.getTaxPercent() != null) {
			amountTax = priceWithoutTax.multiply(bareWalletOperation
					.getTaxPercent().divide(HUNDRED));
		}
		if (unitPriceWithTax == null || unitPriceWithTax.intValue() == 0) {
			priceWithTax = priceWithoutTax.add(amountTax);
		} else {
			priceWithTax = bareWalletOperation.getQuantity().multiply(
					unitPriceWithTax);
		}

		if (provider.getRounding() != null && provider.getRounding() > 0) {
			priceWithoutTax = NumberUtils.round(priceWithoutTax,
					provider.getRounding());
			priceWithTax = NumberUtils.round(priceWithTax,
					provider.getRounding());
		}

		bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTax);
		bareWalletOperation.setUnitAmountWithTax(unitPriceWithTax);
		bareWalletOperation.setTaxPercent(bareWalletOperation.getTaxPercent());
		bareWalletOperation.setAmountWithoutTax(priceWithoutTax);
		bareWalletOperation.setAmountWithTax(priceWithTax);
		bareWalletOperation.setAmountTax(amountTax);

	}

	private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan,
			WalletOperation bareOperation, Long countryId,
			TradingCurrency tcurrency, Long sellerId) {
		// FIXME: the price plan properties could be null !

		log.info("rate " + bareOperation);
		for (PricePlanMatrix pricePlan : listPricePlan) {
			boolean sellerAreEqual = pricePlan.getSeller() == null
					|| pricePlan.getSeller().getId().equals(sellerId);
			if (!sellerAreEqual) {
				log.debug("The seller of the customer " + sellerId
						+ " is not the same as pricePlan seller "
						+ pricePlan.getSeller().getId() + " ("
						+ pricePlan.getSeller().getCode() + ")");
				continue;
			}

			boolean countryAreEqual = pricePlan.getTradingCountry() == null
					|| pricePlan.getTradingCountry().getId().equals(countryId);
			if (!countryAreEqual) {
				log.debug("The country of the billing account "
						+ countryId
						+ " is not the same as pricePlan country"
						+ pricePlan.getTradingCountry().getId()
						+ " ("
						+ pricePlan.getTradingCountry().getCountry()
								.getCountryCode() + ")");
				continue;
			}
			boolean currencyAreEqual = pricePlan.getTradingCurrency() == null
					|| (tcurrency != null && tcurrency.getId().equals(
							pricePlan.getTradingCurrency().getId()));
			if (!currencyAreEqual) {
				log.debug("The currency of the customer account "
						+ (tcurrency != null ? tcurrency.getCurrencyCode()
								: "null")
						+ " is not the same as pricePlan currency"
						+ pricePlan.getTradingCurrency().getId() + " ("
						+ pricePlan.getTradingCurrency().getCurrencyCode()
						+ ")");
				continue;
			}
			boolean subscriptionDateInPricePlanPeriod = bareOperation
					.getSubscriptionDate() == null
					|| ((pricePlan.getStartSubscriptionDate() == null
							|| bareOperation.getSubscriptionDate().after(
									pricePlan.getStartSubscriptionDate()) || bareOperation
							.getSubscriptionDate().equals(
									pricePlan.getStartSubscriptionDate())) && (pricePlan
							.getEndSubscriptionDate() == null || bareOperation
							.getSubscriptionDate().before(
									pricePlan.getEndSubscriptionDate())));
			if (!subscriptionDateInPricePlanPeriod) {
				log.debug("The subscription date "
						+ bareOperation.getSubscriptionDate()
						+ "is not in the priceplan subscription range");
				continue;
			}

			int subscriptionAge = 0;
			if (bareOperation.getSubscriptionDate() != null
					&& bareOperation.getOperationDate() != null) {
				// logger.info("subscriptionDate=" +
				// bareOperation.getSubscriptionDate() + "->" +
				// DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(),
				// -1));
				subscriptionAge = DateUtils.monthsBetween(
						bareOperation.getOperationDate(),
						DateUtils.addDaysToDate(
								bareOperation.getSubscriptionDate(), -1));
			}
			// log.info("subscriptionAge=" + subscriptionAge);
			boolean subscriptionMinAgeOK = pricePlan
					.getMinSubscriptionAgeInMonth() == null
					|| subscriptionAge >= pricePlan
							.getMinSubscriptionAgeInMonth();
			// log.info("subscriptionMinAgeOK(" +
			// pricePlan.getMinSubscriptionAgeInMonth() + ")=" +
			// subscriptionMinAgeOK);
			if (!subscriptionMinAgeOK) {
				log.debug("The subscription age " + subscriptionAge
						+ "is less than the priceplan subscription age min :"
						+ pricePlan.getMinSubscriptionAgeInMonth());
				continue;
			}
			boolean subscriptionMaxAgeOK = pricePlan
					.getMaxSubscriptionAgeInMonth() == null
					|| pricePlan.getMaxSubscriptionAgeInMonth() == 0
					|| subscriptionAge < pricePlan
							.getMaxSubscriptionAgeInMonth();
			log.debug("subscriptionMaxAgeOK("
					+ pricePlan.getMaxSubscriptionAgeInMonth() + ")="
					+ subscriptionMaxAgeOK);
			if (!subscriptionMaxAgeOK) {
				log.debug("The subscription age "
						+ subscriptionAge
						+ " is greater than the priceplan subscription age max :"
						+ pricePlan.getMaxSubscriptionAgeInMonth());
				continue;
			}

			boolean applicationDateInPricePlanPeriod = (pricePlan
					.getStartRatingDate() == null
					|| bareOperation.getOperationDate().after(
							pricePlan.getStartRatingDate()) || bareOperation
					.getOperationDate().equals(pricePlan.getStartRatingDate()))
					&& (pricePlan.getEndRatingDate() == null || bareOperation
							.getOperationDate().before(
									pricePlan.getEndRatingDate()));
			log.debug("applicationDateInPricePlanPeriod("
					+ pricePlan.getStartRatingDate() + " - "
					+ pricePlan.getEndRatingDate() + ")="
					+ applicationDateInPricePlanPeriod);
			if (!applicationDateInPricePlanPeriod) {
				log.debug("The application date "
						+ bareOperation.getOperationDate()
						+ " is not in the priceplan application range");
				continue;
			}
			boolean criteria1SameInPricePlan = pricePlan.getCriteria1Value() == null
					|| pricePlan.getCriteria1Value().equals(
							bareOperation.getParameter1());
			// log.info("criteria1SameInPricePlan(" +
			// pricePlan.getCriteria1Value() + ")=" + criteria1SameInPricePlan);
			if (!criteria1SameInPricePlan) {
				log.debug("The operation param1 "
						+ bareOperation.getParameter1()
						+ " is not compatible with price plan criteria 1: "
						+ pricePlan.getCriteria1Value());
				continue;
			}
			boolean criteria2SameInPricePlan = pricePlan.getCriteria2Value() == null
					|| pricePlan.getCriteria2Value().equals(
							bareOperation.getParameter2());
			// log.info("criteria2SameInPricePlan(" +
			// pricePlan.getCriteria2Value() + ")=" + criteria2SameInPricePlan);
			if (!criteria2SameInPricePlan) {
				log.debug("The operation param2 "
						+ bareOperation.getParameter2()
						+ " is not compatible with price plan criteria 2: "
						+ pricePlan.getCriteria2Value());
				continue;
			}
			boolean criteria3SameInPricePlan = pricePlan.getCriteria3Value() == null
					|| pricePlan.getCriteria3Value().equals(
							bareOperation.getParameter3());
			// log.info("criteria3SameInPricePlan(" +
			// pricePlan.getCriteria3Value() + ")=" + criteria3SameInPricePlan);
			if (criteria3SameInPricePlan) {
				log.debug("criteria3SameInPricePlan");
				return pricePlan;
			}
			log.debug("The operation param3 " + bareOperation.getParameter3()
					+ " is not compatible with price plan criteria 3: "
					+ pricePlan.getCriteria3Value());
		}
		return null;
	}

	// synchronized to avoid different threads to reload the priceplan
	// concurrently
	protected synchronized void reloadPricePlan() {
		if (isPricePlanDirty) {
			log.info("Reload priceplan");
			loadPricePlan();
			isPricePlanDirty = false;
		}
	}

	protected void loadPricePlan() {
		loadPricePlan(entityManager);
	}

	// FIXME : call this method when priceplan is edited (or more precisely add
	// a button to reload the priceplan)
	@SuppressWarnings("unchecked")
	protected void loadPricePlan(EntityManager em) {
		HashMap<String, HashMap<String, List<PricePlanMatrix>>> result = new HashMap<String, HashMap<String, List<PricePlanMatrix>>>();
		List<PricePlanMatrix> allPricePlans = (List<PricePlanMatrix>) em
				.createQuery(
						"from PricePlanMatrix where disabled=false order by priority ASC")
				.getResultList();
		if (allPricePlans != null & allPricePlans.size() > 0) {
			for (PricePlanMatrix pricePlan : allPricePlans) {
				if (!result.containsKey(pricePlan.getProvider().getCode())) {
					result.put(pricePlan.getProvider().getCode(),
							new HashMap<String, List<PricePlanMatrix>>());
				}
				HashMap<String, List<PricePlanMatrix>> providerPricePlans = result
						.get(pricePlan.getProvider().getCode());
				if (!providerPricePlans.containsKey(pricePlan.getEventCode())) {
					providerPricePlans.put(pricePlan.getEventCode(),
							new ArrayList<PricePlanMatrix>());
				}
				if (pricePlan.getCriteria1Value() != null
						&& pricePlan.getCriteria1Value().length() == 0) {
					pricePlan.setCriteria1Value(null);
				}
				if (pricePlan.getCriteria2Value() != null
						&& pricePlan.getCriteria2Value().length() == 0) {
					pricePlan.setCriteria2Value(null);
				}
				if (pricePlan.getCriteria3Value() != null
						&& pricePlan.getCriteria3Value().length() == 0) {
					pricePlan.setCriteria3Value(null);
				}
				log.info("Add pricePlan for provider="
						+ pricePlan.getProvider().getCode() + "; chargeCode="
						+ pricePlan.getEventCode() + "; priceplan=" + pricePlan
						+ "; criteria1=" + pricePlan.getCriteria1Value()
						+ "; criteria2=" + pricePlan.getCriteria2Value()
						+ "; criteria3=" + pricePlan.getCriteria3Value());
				providerPricePlans.get(pricePlan.getEventCode()).add(pricePlan);
			}
		}
		allPricePlan = result;
	}
}
