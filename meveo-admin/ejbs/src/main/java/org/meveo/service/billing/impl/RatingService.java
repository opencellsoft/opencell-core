package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.UnrolledbackBusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.SimpleELResolver;
import org.meveo.service.base.SimpleFunctionMapper;
import org.meveo.service.base.SimpleVariableMapper;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class RatingService {

	@PersistenceContext
	protected EntityManager entityManager;

	static protected Logger log = LoggerFactory.getLogger(RatingService.class);

	@Inject
	protected CatMessagesService catMessagesService;

	@Inject
	private EdrService edrService;

	@EJB
	private SubscriptionService subscriptionService;
	
	@EJB
	private RatedTransactionService ratedTransactionService;

	@Inject
	private RatingCacheContainerProvider ratingCacheContainerProvider;
	
	private static final BigDecimal HUNDRED = new BigDecimal("100");

	/*
	 * public int getSharedQuantity(LevelEnum level, Provider provider, String
	 * chargeCode, Date chargeDate, RecurringChargeInstance recChargeInstance) {
	 * return getSharedQuantity(entityManager, level, provider, chargeCode,
	 * chargeDate, recChargeInstance); }
	 */

	public int getSharedQuantity(EntityManager em, LevelEnum level, Provider provider, String chargeCode,
			Date chargeDate, RecurringChargeInstance recChargeInstance) {
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
				query.setParameter("billingAccount", recChargeInstance.getSubscription().getUserAccount()
						.getBillingAccount());
				break;
			case CUSTOMER:
				query.setParameter("customer", recChargeInstance.getSubscription().getUserAccount().getBillingAccount()
						.getCustomerAccount().getCustomer());
				break;
			case CUSTOMER_ACCOUNT:
				query.setParameter("customerAccount", recChargeInstance.getSubscription().getUserAccount()
						.getBillingAccount().getCustomerAccount());
				break;
			case PROVIDER:
				break;
			case SELLER:
				query.setParameter("seller", recChargeInstance.getSubscription().getUserAccount().getBillingAccount()
						.getCustomerAccount().getCustomer().getSeller());
				break;
			case USER_ACCOUNT:
				query.setParameter("userAccount", recChargeInstance.getSubscription().getUserAccount());
				break;
			default:
				break;

			}
			Number sharedQuantity = (Number) query.getSingleResult();
			result = sharedQuantity.intValue();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}

	/*
	 * public WalletOperation prerateChargeApplication(String code, Date
	 * subscriptionDate, String offerCode, ChargeInstance chargeInstance,
	 * ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal
	 * amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity,
	 * TradingCurrency tCurrency, Long countryId, BigDecimal taxPercent,
	 * BigDecimal discountPercent, Date nextApplicationDate, InvoiceSubCategory
	 * invoiceSubCategory, String criteria1, String criteria2, String criteria3,
	 * Date startdate, Date endDate, ChargeApplicationModeEnum mode) throws
	 * BusinessException { return prerateChargeApplication(entityManager, code,
	 * subscriptionDate, offerCode, chargeInstance, applicationType,
	 * applicationDate, amountWithoutTax, amountWithTax, quantity, tCurrency,
	 * countryId, taxPercent, discountPercent, nextApplicationDate,
	 * invoiceSubCategory, criteria1, criteria2, criteria3, startdate, endDate,
	 * mode); }
	 */

	// used to prerate a oneshot or recurring charge
	public WalletOperation prerateChargeApplication(EntityManager em, String code, Date subscriptionDate,
			String offerCode, ChargeInstance chargeInstance, ApplicationTypeEnum applicationType, Date applicationDate,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, TradingCurrency tCurrency,
			Long countryId, BigDecimal taxPercent, BigDecimal discountPercent, Date nextApplicationDate,
			InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode) throws BusinessException {

		WalletOperation result = new WalletOperation();

		if (chargeInstance instanceof RecurringChargeInstance) {
			result.setSubscriptionDate(subscriptionDate);
		} else if (chargeInstance instanceof UsageChargeInstance) {
			result.setUnityDescription(((UsageChargeInstance) chargeInstance).getUnityDescription());
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
		result.setOfferCode(offerCode);
		result.setStatus(WalletOperationStatusEnum.OPEN);
		result.setSeller(chargeInstance.getSeller());
		// TODO:check that setting the principal wallet at this stage is correct
		result.setWallet(chargeInstance.getSubscription().getUserAccount().getWallet());

		BigDecimal unitPriceWithoutTax = amountWithoutTax;
		BigDecimal unitPriceWithTax = null;

		if (unitPriceWithoutTax != null) {
			unitPriceWithTax = amountWithTax;
		}

		rateBareWalletOperation(em, result, unitPriceWithoutTax, unitPriceWithTax, countryId, tCurrency, provider);

		return result;

	}

	public WalletOperation rateChargeApplication(String code, Subscription subscription, ChargeInstance chargeInstance,
			ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax,
			BigDecimal amountWithTax, BigDecimal quantity, TradingCurrency tCurrency, Long countryId,
			BigDecimal taxPercent, BigDecimal discountPercent, Date nextApplicationDate,
			InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode) throws BusinessException {
		return rateChargeApplication(entityManager, code, subscription, chargeInstance, applicationType,
				applicationDate, amountWithoutTax, amountWithTax, quantity, tCurrency, countryId, taxPercent,
				discountPercent, nextApplicationDate, invoiceSubCategory, criteria1, criteria2, criteria3, startdate,
				endDate, mode);
	}

	// used to rate a oneshot or recurring charge and triggerEDR
	public WalletOperation rateChargeApplication(EntityManager em, String code, Subscription subscription,
			ChargeInstance chargeInstance, ApplicationTypeEnum applicationType, Date applicationDate,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, TradingCurrency tCurrency,
			Long countryId, BigDecimal taxPercent, BigDecimal discountPercent, Date nextApplicationDate,
			InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3,
			Date startdate, Date endDate, ChargeApplicationModeEnum mode) throws BusinessException {
		Date subscriptionDate = null;

		if (chargeInstance instanceof RecurringChargeInstance) {
			subscriptionDate = ((RecurringChargeInstance) chargeInstance).getServiceInstance().getSubscriptionDate();
		}

		WalletOperation result = prerateChargeApplication(em, code, subscriptionDate,
				subscription.getOffer().getCode(), chargeInstance, applicationType, applicationDate, amountWithoutTax,
				amountWithTax, quantity, tCurrency, countryId, taxPercent, discountPercent, nextApplicationDate,
				invoiceSubCategory, criteria1, criteria2, criteria3, startdate, endDate, mode);

		String chargeInstnceLabel = null;
		UserAccount ua = subscription.getUserAccount();
		try {
			String languageCode = ua.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
			chargeInstnceLabel = catMessagesService.getMessageDescription(chargeInstance, languageCode);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		result.setDescription(chargeInstnceLabel != null ? chargeInstnceLabel : chargeInstance.getDescription());

		List<TriggeredEDRTemplate> triggeredEDRTemplates = chargeInstance.getChargeTemplate().getEdrTemplates();
		if (triggeredEDRTemplates.size() > 0) {
			for (TriggeredEDRTemplate triggeredEDRTemplate : triggeredEDRTemplates) {

				boolean conditionCheck = triggeredEDRTemplate.getConditionEl() == null
						|| "".equals(triggeredEDRTemplate.getConditionEl())
						|| matchExpression(triggeredEDRTemplate.getConditionEl(), result, ua);
				log.debug("checking condition for {} : {} -> {}", triggeredEDRTemplate.getCode(),
						triggeredEDRTemplate.getConditionEl(), conditionCheck);
				if (conditionCheck) {
					EDR newEdr = new EDR();
					newEdr.setCreated(new Date());
					newEdr.setEventDate(applicationDate);
					newEdr.setOriginBatch(EDR.EDR_TABLE_ORIGIN);
					newEdr.setOriginRecord("CHRG_" + chargeInstance.getId() + "_" + applicationDate.getTime());
					newEdr.setParameter1(evaluateStringExpression(triggeredEDRTemplate.getParam1El(), result, ua));
					newEdr.setParameter2(evaluateStringExpression(triggeredEDRTemplate.getParam2El(), result, ua));
					newEdr.setParameter3(evaluateStringExpression(triggeredEDRTemplate.getParam3El(), result, ua));
					newEdr.setParameter4(evaluateStringExpression(triggeredEDRTemplate.getParam4El(), result, ua));
					newEdr.setProvider(chargeInstance.getProvider());
					newEdr.setQuantity(new BigDecimal(evaluateDoubleExpression(triggeredEDRTemplate.getQuantityEl(),
							result, ua)));
					newEdr.setStatus(EDRStatusEnum.OPEN);
					Subscription sub = null;
					if (StringUtils.isBlank(triggeredEDRTemplate.getSubscriptionEl())) {
						sub = subscription;
					} else {
						String subCode = evaluateStringExpression(triggeredEDRTemplate.getSubscriptionEl(), result, ua);
						sub = subscriptionService.findByCode(em, subCode, subscription.getProvider());
						if (sub == null) {
							log.info("could not find subscription for code =" + subCode + " (EL="
									+ triggeredEDRTemplate.getSubscriptionEl() + ") in triggered EDR with code "
									+ triggeredEDRTemplate.getCode());
						}
					}
					if (sub != null) {
						newEdr.setSubscription(sub);
						log.info("trigger EDR from code " + triggeredEDRTemplate.getCode());
						if (chargeInstance.getAuditable() == null) {
							log.info("trigger EDR from code " + triggeredEDRTemplate.getCode());
						} else {
							edrService.create(em, newEdr, chargeInstance.getAuditable().getCreator(),
									chargeInstance.getProvider());
						}
					}
				}
			}
		}

		return result;
	}

	public void rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTax,
			BigDecimal unitPriceWithTax, Long countryId, TradingCurrency tcurrency, Provider provider)
			throws BusinessException {
		rateBareWalletOperation(entityManager, bareWalletOperation, unitPriceWithoutTax, unitPriceWithTax, countryId,
				tcurrency, provider);
	}

	// used to rate or rerate a bareWalletOperation
	public void rateBareWalletOperation(EntityManager em, WalletOperation bareWalletOperation,
			BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax, Long countryId, TradingCurrency tcurrency,
			Provider provider) throws BusinessException {

		PricePlanMatrix ratePrice = null;
		String providerCode = provider.getCode();

		if (unitPriceWithoutTax == null) {
            List<PricePlanMatrix> chargePricePlans = ratingCacheContainerProvider.getPricePlansByChargeCode(provider.getId(), bareWalletOperation.getCode());
            if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                throw new RuntimeException("No price plan for provider " + providerCode + " and charge code " + bareWalletOperation.getCode());
            }
			ratePrice = ratePrice(chargePricePlans,bareWalletOperation, countryId, tcurrency,
					bareWalletOperation.getSeller() != null ? bareWalletOperation.getSeller().getId() : null);
			if (ratePrice == null || ratePrice.getAmountWithoutTax() == null) {
				throw new BusinessException("Invalid price plan for provider " + providerCode + " and charge code "
						+ bareWalletOperation.getCode());
			} else {
				log.info("found ratePrice:" + ratePrice.getId() + " priceHT=" + ratePrice.getAmountWithoutTax()
						+ " priceTTC=" + ratePrice.getAmountWithTax());
				unitPriceWithoutTax = ratePrice.getAmountWithoutTax();
				unitPriceWithTax = ratePrice.getAmountWithTax();
			}

		}
		// if the wallet operation correspond to a recurring charge that is
		// shared, we divide the price by the number of
		// shared charges
		if (bareWalletOperation.getChargeInstance() != null
				&& bareWalletOperation.getChargeInstance() instanceof RecurringChargeInstance) {
			RecurringChargeTemplate recChargeTemplate = ((RecurringChargeInstance) bareWalletOperation
					.getChargeInstance()).getRecurringChargeTemplate();
			if (recChargeTemplate.getShareLevel() != null) {
				RecurringChargeInstance recChargeInstance = (RecurringChargeInstance) bareWalletOperation
						.getChargeInstance();
				int sharedQuantity = getSharedQuantity(em, recChargeTemplate.getShareLevel(), provider,
						recChargeInstance.getCode(), bareWalletOperation.getOperationDate(), recChargeInstance);
				if (sharedQuantity > 0) {
					unitPriceWithoutTax = unitPriceWithoutTax.divide(new BigDecimal(sharedQuantity),
							BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
					if (unitPriceWithTax != null) {
						unitPriceWithTax = unitPriceWithTax.divide(new BigDecimal(sharedQuantity),
								BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
					}
					log.info("charge is shared " + sharedQuantity + " times, so unit price is " + unitPriceWithoutTax);
				}
			}
		}

		BigDecimal priceWithoutTax = bareWalletOperation.getQuantity().multiply(unitPriceWithoutTax);
		BigDecimal priceWithTax = null;
		BigDecimal unitPriceAmountTax = null;
		BigDecimal amountTax = BigDecimal.ZERO;

		if (bareWalletOperation.getTaxPercent() != null) {
			unitPriceAmountTax = unitPriceWithoutTax.multiply(bareWalletOperation.getTaxPercent().divide(HUNDRED));
			amountTax = priceWithoutTax.multiply(bareWalletOperation.getTaxPercent().divide(HUNDRED));
		}

		if (unitPriceWithTax == null || unitPriceWithTax.intValue() == 0) {
			if (unitPriceAmountTax != null) {
				unitPriceWithTax = unitPriceWithoutTax.add(unitPriceAmountTax);
				priceWithTax = priceWithoutTax.add(amountTax);
			}
		} else {
			unitPriceAmountTax = unitPriceWithTax.subtract(unitPriceWithoutTax);
			priceWithTax = bareWalletOperation.getQuantity().multiply(unitPriceWithTax);
			amountTax = priceWithTax.subtract(priceWithoutTax);
		}

		if (provider.getRounding() != null && provider.getRounding() > 0) {
			priceWithoutTax = NumberUtils.round(priceWithoutTax, provider.getRounding());
			priceWithTax = NumberUtils.round(priceWithTax, provider.getRounding());
		}

		bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTax);
		bareWalletOperation.setUnitAmountWithTax(unitPriceWithTax);
		bareWalletOperation.setUnitAmountTax(unitPriceAmountTax);
		bareWalletOperation.setTaxPercent(bareWalletOperation.getTaxPercent());
		bareWalletOperation.setAmountWithoutTax(priceWithoutTax);
		bareWalletOperation.setAmountWithTax(priceWithTax);
		bareWalletOperation.setAmountTax(amountTax);
	}

	private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan, WalletOperation bareOperation,
			Long countryId, TradingCurrency tcurrency, Long sellerId) throws BusinessException {
		// FIXME: the price plan properties could be null !

		log.info("rate " + bareOperation);
		for (PricePlanMatrix pricePlan : listPricePlan) {
			boolean sellerAreEqual = pricePlan.getSeller() == null || pricePlan.getSeller().getId().equals(sellerId);
			if (!sellerAreEqual) {
				log.debug("The seller of the customer " + sellerId + " is not the same as pricePlan seller "
						+ pricePlan.getSeller().getId() + " (" + pricePlan.getSeller().getCode() + ")");
				continue;
			}

			boolean countryAreEqual = pricePlan.getTradingCountry() == null
					|| pricePlan.getTradingCountry().getId().equals(countryId);
			if (!countryAreEqual) {
				log.debug(
						"The countryId={} of the billing account is not the same as pricePlan with countryId={} and code={}",
						countryId, pricePlan.getTradingCountry().getId(),
								pricePlan.getTradingCountry().getCountry().getCountryCode() );
				continue;
			}
			boolean currencyAreEqual = pricePlan.getTradingCurrency() == null
					|| (tcurrency != null && tcurrency.getId().equals(pricePlan.getTradingCurrency().getId()));
			if (!currencyAreEqual) {
				log.debug("The currency of the customer account "
						+ (tcurrency != null ? tcurrency.getCurrencyCode() : "null")
						+ " is not the same as pricePlan currency" + pricePlan.getTradingCurrency().getId() + " ("
						+ pricePlan.getTradingCurrency().getCurrencyCode() + ")");
				continue;
			}
			boolean subscriptionDateInPricePlanPeriod = bareOperation.getSubscriptionDate() == null
					|| ((pricePlan.getStartSubscriptionDate() == null
							|| bareOperation.getSubscriptionDate().after(pricePlan.getStartSubscriptionDate()) || bareOperation
							.getSubscriptionDate().equals(pricePlan.getStartSubscriptionDate())) && (pricePlan
							.getEndSubscriptionDate() == null || bareOperation.getSubscriptionDate().before(
							pricePlan.getEndSubscriptionDate())));
			if (!subscriptionDateInPricePlanPeriod) {
				log.debug("The subscription date " + bareOperation.getSubscriptionDate()
						+ "is not in the priceplan subscription range");
				continue;
			}

			int subscriptionAge = 0;
			if (bareOperation.getSubscriptionDate() != null && bareOperation.getOperationDate() != null) {
				// logger.info("subscriptionDate=" +
				// bareOperation.getSubscriptionDate() + "->" +
				// DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(),
				// -1));
				subscriptionAge = DateUtils.monthsBetween(bareOperation.getOperationDate(),
						DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(), -1));
			}
			// log.info("subscriptionAge=" + subscriptionAge);
			boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null
					|| subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
			// log.info("subscriptionMinAgeOK(" +
			// pricePlan.getMinSubscriptionAgeInMonth() + ")=" +
			// subscriptionMinAgeOK);
			if (!subscriptionMinAgeOK) {
				log.debug("The subscription age={} is less than the priceplan subscription age min={}",
						subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
				continue;
			}
			boolean subscriptionMaxAgeOK = pricePlan.getMaxSubscriptionAgeInMonth() == null
					|| pricePlan.getMaxSubscriptionAgeInMonth() == 0
					|| subscriptionAge < pricePlan.getMaxSubscriptionAgeInMonth();
			log.debug("subscriptionMaxAgeOK(" + pricePlan.getMaxSubscriptionAgeInMonth() + ")=" + subscriptionMaxAgeOK);
			if (!subscriptionMaxAgeOK) {
				log.debug("The subscription age " + subscriptionAge
						+ " is greater than the priceplan subscription age max :"
						+ pricePlan.getMaxSubscriptionAgeInMonth());
				continue;
			}

			boolean applicationDateInPricePlanPeriod = (pricePlan.getStartRatingDate() == null
					|| bareOperation.getOperationDate().after(pricePlan.getStartRatingDate()) || bareOperation
                .getOperationDate().equals(pricePlan.getStartRatingDate()))
					&& (pricePlan.getEndRatingDate() == null || bareOperation.getOperationDate().before(
							pricePlan.getEndRatingDate()));
			log.debug("applicationDateInPricePlanPeriod(" + pricePlan.getStartRatingDate() + " - "
					+ pricePlan.getEndRatingDate() + ")=" + applicationDateInPricePlanPeriod);
			if (!applicationDateInPricePlanPeriod) {
				log.debug("The application date " + bareOperation.getOperationDate()
						+ " is not in the priceplan application range");
				continue;
			}
			boolean criteria1SameInPricePlan = pricePlan.getCriteria1Value() == null
					|| pricePlan.getCriteria1Value().equals(bareOperation.getParameter1());
			// log.info("criteria1SameInPricePlan(" +
			// pricePlan.getCriteria1Value() + ")=" + criteria1SameInPricePlan);
			if (!criteria1SameInPricePlan) {
				log.debug("The operation param1 " + bareOperation.getParameter1()
						+ " is not compatible with price plan criteria 1: " + pricePlan.getCriteria1Value());
				continue;
			}
			boolean criteria2SameInPricePlan = pricePlan.getCriteria2Value() == null
					|| pricePlan.getCriteria2Value().equals(bareOperation.getParameter2());
			// log.info("criteria2SameInPricePlan(" +
			// pricePlan.getCriteria2Value() + ")=" + criteria2SameInPricePlan);
			if (!criteria2SameInPricePlan) {
				log.debug("The operation param2 " + bareOperation.getParameter2()
						+ " is not compatible with price plan criteria 2: " + pricePlan.getCriteria2Value());
				continue;
			}
			boolean criteria3SameInPricePlan = pricePlan.getCriteria3Value() == null
					|| pricePlan.getCriteria3Value().equals(bareOperation.getParameter3());
			// log.info("criteria3SameInPricePlan(" +
			// pricePlan.getCriteria3Value() + ")=" + criteria3SameInPricePlan);
			if (!criteria3SameInPricePlan) {
				log.debug("The operation param3 " + bareOperation.getParameter3()
						+ " is not compatible with price plan criteria 3: " + pricePlan.getCriteria3Value());
				continue;
			}
			if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
				UserAccount ua = bareOperation.getWallet().getUserAccount();
				if (!matchExpression(pricePlan.getCriteriaEL(), bareOperation, ua)) {
					log.debug("The operation is not compatible with price plan criteria EL: "
							+ pricePlan.getCriteriaEL());
					continue;
				}
			}

			boolean offerCodeSameInPricePlan = pricePlan.getOfferTemplate() == null
					|| pricePlan.getOfferTemplate().getCode().equals(bareOperation.getOfferCode());
			if (!offerCodeSameInPricePlan) {
				log.debug("The operation offerCode " + bareOperation.getOfferCode()
						+ " is not compatible with price plan offerCode: "
						+ ((pricePlan.getOfferTemplate() == null) ? "null" : pricePlan.getOfferTemplate().getCode()));
				continue;
			}
			log.debug("offerCodeSameInPricePlan");
			boolean quantityMaxOk = pricePlan.getMaxQuantity() == null
					|| pricePlan.getMaxQuantity().compareTo(bareOperation.getQuantity()) > 0;
			if (!quantityMaxOk) {
				log.debug("the quantity " + bareOperation.getQuantity() + " is strictly greater than "
						+ pricePlan.getMaxQuantity());
				continue;
			} else {
				log.debug("quantityMaxOkInPricePlan");
			}
			boolean quantityMinOk = pricePlan.getMinQuantity() == null
					|| pricePlan.getMinQuantity().compareTo(bareOperation.getQuantity()) <= 0;
			if (!quantityMinOk) {
			    log.debug("the quantity " + bareOperation.getQuantity() + " is less than " + pricePlan.getMinQuantity());
	            continue;
			} else {
			    log.debug("quantityMinOkInPricePlan");
			} 

            boolean validityCalendarOK = pricePlan.getValidityCalendar() == null || pricePlan.getValidityCalendar().previousCalendarDate(bareOperation.getOperationDate()) != null;
            if (validityCalendarOK) {
                log.debug("validityCalendarOkInPricePlan calendar " + pricePlan.getValidityCalendar() + " operation date " + bareOperation.getOperationDate());
                bareOperation.setPriceplan(pricePlan);
                return pricePlan;
            } else if (pricePlan.getValidityCalendar() != null ){
                log.debug("the operation date " + bareOperation.getOperationDate() + " does not match pricePlan validity calendar " + pricePlan.getValidityCalendar().getCode()
                        + "period range ");
            }
		}
		return null;
	}

	//rerate
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void reRate(WalletOperation operationToRerate,
			boolean useSamePricePlan) throws BusinessException {
		try {
			ratedTransactionService
					.reratedByWalletOperationId(operationToRerate.getId());
			WalletOperation operation = operationToRerate.getUnratedClone();
			operationToRerate.setReratedWalletOperation(operation);
			operationToRerate.setStatus(WalletOperationStatusEnum.RERATED);
			if (useSamePricePlan) {
				if (operation.getPriceplan() != null) {
					operation.setUnitAmountWithoutTax(operation.getPriceplan()
							.getAmountWithoutTax());
					operation.setUnitAmountWithTax(operation.getPriceplan()
							.getAmountWithTax());
					if (operation.getUnitAmountTax() != null
							&& operation.getUnitAmountWithTax() != null) {
						operation.setUnitAmountTax(operation
								.getUnitAmountWithTax().subtract(
										operation.getUnitAmountWithoutTax()));
					}
				}
				operation.setAmountWithoutTax(operation
						.getUnitAmountWithoutTax().multiply(
								operation.getQuantity()));
				if (operation.getUnitAmountWithTax() != null) {
					operation
							.setAmountWithTax(operation.getUnitAmountWithTax().multiply(
									operation.getQuantity()));
				}
				Integer rounding=operationToRerate.getProvider().getRounding();
				if (rounding != null && rounding > 0) {
					operation.setAmountWithoutTax(NumberUtils.round(operation.getAmountWithoutTax(), rounding));
					operation.setAmountWithTax(NumberUtils.round(operation.getAmountWithTax(), rounding));
				}
				operation.setAmountTax(operation.getAmountWithTax().subtract(
						operation.getAmountWithoutTax()));
			} else {
				if(operation.getPriceplan()==null){
					operation.setUnitAmountWithoutTax(null);
					operation.setUnitAmountWithTax(null);
					operation.setUnitAmountTax(null);
				}
				rateBareWalletOperation(entityManager, operation, null,
							null, operation.getPriceplan().getTradingCountry()==null?null:
								operation.getPriceplan().getTradingCountry().getId(), operation.getPriceplan()
									.getTradingCurrency(),
							operation.getProvider());
			}
			entityManager.persist(operation);
		} catch (UnrolledbackBusinessException e) {
			log.info(e.getMessage());
			operationToRerate.setStatus(WalletOperationStatusEnum.TREATED);
		}
	}
	
	private boolean matchExpression(String expression, WalletOperation bareOperation, UserAccount ua)
			throws BusinessException {
		Boolean result = true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("op", bareOperation);
		if(expression.indexOf("charge") >= 0){
			ChargeTemplate charge=bareOperation.getChargeInstance().getChargeTemplate();
			userMap.put("charge",charge);
			charge.getCustomFields();
		}
		if(expression.indexOf("offer") >= 0){
			OfferTemplate offer=bareOperation.getChargeInstance().getSubscription().getOffer();
			offer.getCustomFields();
			userMap.put("offer",offer);
		}
		if (expression.indexOf("ua.") >= 0) {
			userMap.put("ua", ua);
		}
		if (expression.indexOf("ba.") >= 0) {
			userMap.put("ba", ua.getBillingAccount());
		}
		if (expression.indexOf("ca.") >= 0) {
			userMap.put("ca", ua.getBillingAccount().getCustomerAccount());
		}
		if (expression.indexOf("c.") >= 0) {
			userMap.put("c", ua.getBillingAccount().getCustomerAccount().getCustomer());
		}
		Object res = evaluateExpression(expression, userMap, Boolean.class);
		try {
			result = (Boolean) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
		}
		return result;
	}

	private String evaluateStringExpression(String expression, WalletOperation walletOperation, UserAccount ua)
			throws BusinessException {
		String result = null;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("op", walletOperation);
		if(expression.indexOf("charge") >= 0){
			ChargeTemplate charge=walletOperation.getChargeInstance().getChargeTemplate();
			userMap.put("charge",charge);
			charge.getCustomFields();
		}
		if(expression.indexOf("offer") >= 0){
			OfferTemplate offer=walletOperation.getChargeInstance().getSubscription().getOffer();
			offer.getCustomFields();
			userMap.put("offer",offer);
		}
		if (expression.indexOf("ua.") >= 0) {
			userMap.put("ua", ua);
		}
		if (expression.indexOf("ba.") >= 0) {
			userMap.put("ba", ua.getBillingAccount());
		}
		if (expression.indexOf("ca.") >= 0) {
			userMap.put("ca", ua.getBillingAccount().getCustomerAccount());
		}
		if (expression.indexOf("c.") >= 0) {
			userMap.put("c", ua.getBillingAccount().getCustomerAccount().getCustomer());
		}

		Object res = evaluateExpression(expression, userMap, String.class);
		try {
			result = (String) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
		}
		return result;
	}

	private Double evaluateDoubleExpression(String expression, WalletOperation walletOperation, UserAccount ua)
			throws BusinessException {
		Double result = null;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("op", walletOperation);
		if(expression.indexOf("charge") >= 0){
			ChargeTemplate charge=walletOperation.getChargeInstance().getChargeTemplate();
			userMap.put("charge",charge);
			charge.getCustomFields();
		}
		if(expression.indexOf("offer") >= 0){
			OfferTemplate offer=walletOperation.getChargeInstance().getSubscription().getOffer();
			offer.getCustomFields();
			userMap.put("offer",offer);
		}
		/*if(expression.indexOf("service") >= 0){
			ServiceTemplate service=walletOperation.getServiceInstance();
			offer.getCustomFields();
			userMap.put("offer",offer);
		}*/
		if (expression.indexOf("ua.") >= 0) {
			userMap.put("ua", ua);
		}
		if (expression.indexOf("ba.") >= 0) {
			userMap.put("ba", ua.getBillingAccount());
		}
		if (expression.indexOf("ca.") >= 0) {
			userMap.put("ca", ua.getBillingAccount().getCustomerAccount());
		}
		if (expression.indexOf("c.") >= 0) {
			userMap.put("c", ua.getBillingAccount().getCustomerAccount().getCustomer());
		}

		Object res = evaluateExpression(expression, userMap, Double.class);
		try {
			result = (Double) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
		}
		return result;
	}

	public static Object evaluateExpression(String expression, Map<Object, Object> userMap,
			@SuppressWarnings("rawtypes") Class resultClass) throws BusinessException {
		Object result = null;
		if (StringUtils.isBlank(expression)) {
			return null;
		}
		if (expression.indexOf("#{") < 0) {
			log.debug("the expression '{}' doesnt contain any EL", expression);
			if (resultClass.equals(String.class)) {
				return expression;
			} else if (resultClass.equals(Double.class)) {
				return Double.parseDouble(expression);
			} else if (resultClass.equals(Boolean.class)) {
				if ("true".equalsIgnoreCase(expression)) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			}
		}
		// FIXME: externilize the resolver to instance variable and simply set
		// the bare operation
		// before evaluation
		try {
			ELResolver simpleELResolver = new SimpleELResolver(userMap);
			final VariableMapper variableMapper = new SimpleVariableMapper();
			final FunctionMapper functionMapper = new SimpleFunctionMapper();
			final CompositeELResolver compositeELResolver = new CompositeELResolver();
			compositeELResolver.add(simpleELResolver);
			compositeELResolver.add(new ArrayELResolver());
			compositeELResolver.add(new ListELResolver());
			compositeELResolver.add(new BeanELResolver());
			compositeELResolver.add(new MapELResolver());
			ELContext context = new ELContext() {
				@Override
				public ELResolver getELResolver() {
					return compositeELResolver;
				}

				@Override
				public FunctionMapper getFunctionMapper() {
					return functionMapper;
				}

				@Override
				public VariableMapper getVariableMapper() {
					return variableMapper;
				}
			};
			ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

			ValueExpression ve = expressionFactory.createValueExpression(context, expression, resultClass);
			result = ve.getValue(context);
			log.debug("EL {} => {}", expression, result);

		} catch (Exception e) {
			log.warn("EL {} throw error {}", expression, e.getMessage());
			throw new BusinessException("Error while evaluating expression " + expression + " : " + e.getMessage());
		}
		return result;
	}
}
