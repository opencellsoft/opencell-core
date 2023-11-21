package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.ELUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixForRating;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValueForRating;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeCategoryEnum;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.enums.PriceVersionDateSettingEnum;
import org.meveo.model.settings.AdvancedSettings;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.settings.impl.AdvancedSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class PricePlanSelectionService implements Serializable {

    private static final long serialVersionUID = 6228282058872986933L;

    Logger log = LoggerFactory.getLogger(PricePlanSelectionService.class);

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private AttributeInstanceService attributeInstanceService;
    
    @Inject
    private AdvancedSettingsService advancedSettingsService;

    @Inject
    private ELUtils elUtils;

    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    /**
     * Determine a price plan that match a wallet operation and buyer country and currency
     * 
     * @param bareWo Wallet operation to determine price for
     * @param buyerCountryId Buyer country id
     * @param buyerCurrency Buyer currency
     * @return Price plan matched
     * @throws NoPricePlanException No price plan line was matched
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public PricePlanMatrix determineDefaultPricePlan(WalletOperation bareWo, Long buyerCountryId, TradingCurrency buyerCurrency) throws NoPricePlanException, InvalidELException {

        long subscriptionAge = 0;
        Date subscriptionDate = DateUtils.truncateTime(bareWo.getSubscriptionDate());
        Date operationDate = DateUtils.truncateTime(bareWo.getOperationDate());
        if (subscriptionDate != null && operationDate != null) {
            subscriptionAge = DateUtils.monthsBetween(operationDate, DateUtils.addDaysToDate(subscriptionDate, -1));
        }

        Date startDate = operationDate;
        Date endDate = operationDate;
        RecurringChargeTemplate recurringChargeTemplate = getRecurringChargeTemplateFromChargeInstance(bareWo.getChargeInstance());

        if ((recurringChargeTemplate != null && recurringChargeTemplate.isProrataOnPriceChange() && bareWo.getEndDate().after(bareWo.getStartDate()))) {
            startDate = DateUtils.truncateTime(bareWo.getStartDate());
            endDate = DateUtils.truncateTime(bareWo.getEndDate());
        }

        Object[] params = new Object[] { "chargeCode", bareWo.getCode(), "sellerId", bareWo.getSeller().getId(), "tradingCountryId", buyerCountryId, "tradingCurrencyId",
                buyerCurrency != null ? buyerCurrency.getId() : null, "subscriptionDate", subscriptionDate, "subscriptionAge", subscriptionAge, "operationDate", operationDate, "param1", bareWo.getParameter1(), "param2",
                bareWo.getParameter2(), "param3", bareWo.getParameter3(), "offerId", bareWo.getOfferTemplate() != null ? bareWo.getOfferTemplate().getId() : null, "quantity", bareWo.getQuantity(), "startDate", startDate,
                "endDate", endDate };

        Map<String, Boolean> advancedSettingsMapByGroup = advancedSettingsService.getAdvancedSettingsMapByGroup("pricePlanFilters", Boolean.class);
        // When matching in DB only, no PricePlanMatrix.criteriaEl and validityCalendar fields will be consulted and the highest priority will be chosen
        boolean matchDbOnly = ParamBean.getInstance().getPropertyAsBoolean("pricePlan.default.matchDBOnly", false) || (!advancedSettingsMapByGroup.get("pricePlanFilters.enablePricePlanFilters")
						|| (!advancedSettingsMapByGroup.get("pricePlanFilters.enableValidityCalendar") && !advancedSettingsMapByGroup.get("pricePlanFilters.enableCriteriaEL")));

        EntityManager em = getEntityManager();

        if (matchDbOnly) {
        	return getActivePricePlansByChargeCodeForRatingMatchDB( bareWo, buyerCountryId, advancedSettingsMapByGroup, buyerCurrency, startDate, endDate, subscriptionAge, subscriptionDate, operationDate);

        } else {
            TypedQuery<PricePlanMatrixForRating> query = em.createNamedQuery("PricePlanMatrix.getActivePricePlansByChargeCodeForRating", PricePlanMatrixForRating.class);

            for (int i = 0; i < 28; i = i + 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }

            List<PricePlanMatrixForRating> chargePricePlans = query.getResultList();
            PricePlanMatrixForRating pricePlan = matchPricePlan(chargePricePlans, bareWo, buyerCountryId, buyerCurrency);
            if (pricePlan == null) {
                throw new NoPricePlanException("No active price plan matched for parameters: " + StringUtils.concatenate(params));
            }
            return em.getReference(PricePlanMatrix.class, pricePlan.getId());
        }
    }
    
    public PricePlanMatrix getActivePricePlansByChargeCodeForRatingMatchDB(WalletOperation bareWo, Long buyerCountryId, Map<String, Boolean> advancedSettingsMapByGroup, TradingCurrency buyerCurrency, Date startDate,
            Date endDate, long subscriptionAge, Date subscriptionDate, Date operationDate) {
		StringBuilder queryBuilder = new StringBuilder("SELECT ppm FROM PricePlanMatrix ppm WHERE ppm.disabled = false AND ppm.eventCode = :chargeCode");

		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("chargeCode", bareWo.getCode());
		
		if(advancedSettingsMapByGroup.get("pricePlanFilters.enablePricePlanFilters")){
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableSeller")) {
				queryBuilder.append(" AND (seller.id = :sellerId OR seller.id IS NULL)");
				queryParams.put("sellerId", bareWo.getSeller().getId());
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableOfferTemplate")) {
				queryBuilder.append(" AND (offerTemplate.id = :offerId OR offerTemplate.id IS NULL)");
				queryParams.put("offerId", bareWo.getOfferTemplate() != null ? bareWo.getOfferTemplate().getId() : null);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableTradingCountry")) {
				queryBuilder.append(" AND (tradingCountry.id = :tradingCountryId OR tradingCountry.id IS NULL)");
				queryParams.put("tradingCountryId", buyerCountryId);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableTradingCurrency")) {
				queryBuilder.append(" AND (tradingCurrency.id = :tradingCurrencyId OR tradingCurrency.id IS NULL)");
				queryParams.put("tradingCurrencyId", buyerCurrency != null ? buyerCurrency.getId() : null);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableCriteria1Value")) {
				queryBuilder.append(" AND (criteria1Value = :param1 OR criteria1Value IS NULL)");
				queryParams.put("param1", bareWo.getParameter1());
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableCriteria2Value")) {
				queryBuilder.append(" AND (criteria2Value = :param2 OR criteria2Value IS NULL)");
				queryParams.put("param2", bareWo.getParameter2());
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableCriteria3Value")) {
				queryBuilder.append(" AND (criteria3Value = :param3 OR criteria3Value IS NULL)");
				queryParams.put("param3", bareWo.getParameter3());
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableStartSubscriptionDate")) {
				queryBuilder.append(" AND (startSubscriptionDate IS NULL OR startSubscriptionDate <= :subscriptionDate)");
				queryParams.put("subscriptionDate", subscriptionDate);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableEndSubscriptionDate")) {
				queryBuilder.append(" AND (endSubscriptionDate IS NULL OR endSubscriptionDate > :subscriptionDate)");
				queryParams.put("subscriptionDate", subscriptionDate);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableMinSubscriptionAgeInMonth")) {
				queryBuilder.append(" AND (minSubscriptionAgeInMonth IS NULL OR minSubscriptionAgeInMonth <= :subscriptionAge)");
				queryParams.put("subscriptionAge", subscriptionAge);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableMaxSubscriptionAgeInMonth")) {
				queryBuilder.append(" AND (maxSubscriptionAgeInMonth IS NULL OR maxSubscriptionAgeInMonth > :subscriptionAge)");
				queryParams.put("subscriptionAge", subscriptionAge);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableStartRatingDate")) {
				queryBuilder.append(" AND (startRatingDate IS NULL OR startRatingDate <= :operationDate)");
				queryParams.put("operationDate", operationDate);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableEndRatingDate")) {
				queryBuilder.append(" AND (endRatingDate IS NULL OR endRatingDate > :operationDate)");
				queryParams.put("operationDate", operationDate);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableValidityFrom")) {
				queryBuilder.append(" AND (validityFrom IS NULL OR validityFrom < :startDate)");
				queryParams.put("startDate", startDate);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableValidityDate")) {
				queryBuilder.append(" AND (validityDate IS NULL OR validityDate >= :startDate OR validityDate >= :endDate)");
				queryParams.put("startDate", startDate);
				queryParams.put("endDate", endDate);
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableMaxQuantity")) {
				queryBuilder.append(" AND (maxQuantity IS NULL OR maxQuantity > :quantity)");
				queryParams.put("quantity", bareWo.getQuantity());
			}
	
			if (advancedSettingsMapByGroup.get("pricePlanFilters.enableMinQuantity")) {
				queryBuilder.append(" AND (minQuantity IS NULL OR minQuantity <= :quantity)");
				queryParams.put("quantity", bareWo.getQuantity());
			}
		}

		queryBuilder.append(" ORDER BY ppm.priority ASC, ppm.id");

        TypedQuery<PricePlanMatrix> query = getEntityManager().createQuery(queryBuilder.toString(), PricePlanMatrix.class);
		query.setMaxResults(1);
		for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		try {
			query.setHint("org.hibernate.cacheable", true);
			PricePlanMatrix pricePlan = query.getSingleResult();
			return pricePlan;
		} catch (NoResultException e) {
            throw new NoPricePlanException("No active price plan matched for parameters: " + queryParams.keySet().stream().map(key -> key + "=" + queryParams.get(key)).collect(Collectors.joining(", ", "{", "}")));
		}

	}

	/**
     * Find a matching price plan for a given wallet operation - used to resolve Price plan criteriaEL and validityCalendar fields that can not be done in DB
     *
     * @param listPricePlan List of price plans to consider
     * @param bareOperation Wallet operation to lookup price plan for
     * @param buyerCountryId Buyer's county id
     * @param buyerCurrency Buyer's trading currency
     * @return Matched price plan
     * @throws InvalidELException Failed to evaluate EL expression
     */
    private PricePlanMatrixForRating matchPricePlan(List<PricePlanMatrixForRating> listPricePlan, WalletOperation bareOperation, Long buyerCountryId, TradingCurrency buyerCurrency) throws InvalidELException {

        for (PricePlanMatrixForRating pricePlan : listPricePlan) {

            log.trace("Try to verify price plan {} for WO {}", pricePlan.getId(), bareOperation.getCode());

            if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
                UserAccount ua = bareOperation.getWallet().getUserAccount();
                if (!elUtils.evaluateBooleanExpression(pricePlan.getCriteriaEL(), bareOperation, ua, null, pricePlan, null)) {
                    // log.trace("The operation is not compatible with price plan criteria EL: {}", pricePlan.getCriteriaEL());
                    continue;
                }
            }

            if (pricePlan.getValidityCalendar() != null) {
                org.meveo.model.catalog.Calendar validityCalendar = getEntityManager().find(org.meveo.model.catalog.Calendar.class, pricePlan.getValidityCalendar());
                boolean validityCalendarOK = validityCalendar.previousCalendarDate(bareOperation.getOperationDate()) != null;
                if (!validityCalendarOK) {
                    // log.trace("The operation date " + operationDate + " does not match pricePlan validity calendar " + validityCalendar.getCode() + "period range ");
                    continue;
                }
            }

            return pricePlan;
        }
        return null;

    }

    /**
     * get pricePlanVersion Valid for the given operationDate
     * 
     * @param pricePlanId Price plan ID
     * @param serviceInstance Service instance
     * @param operationDate Operation date
     * @return PricePlanMatrixVersion Matched Price plan version or NULL if nothing found
     * @throws RatingException More than one Price plan version was found for a given date
     */
    public PricePlanMatrixVersion getPublishedVersionValidForDate(Long pricePlanId, ServiceInstance serviceInstance, Date operationDate) throws RatingException {
        Date operationDateParam = new Date();
        if (serviceInstance == null || PriceVersionDateSettingEnum.EVENT.equals(serviceInstance.getPriceVersionDateSetting())) {
            operationDateParam = operationDate;
        } else if (PriceVersionDateSettingEnum.DELIVERY.equals(serviceInstance.getPriceVersionDateSetting()) || PriceVersionDateSettingEnum.RENEWAL.equals(serviceInstance.getPriceVersionDateSetting())
                || PriceVersionDateSettingEnum.QUOTE.equals(serviceInstance.getPriceVersionDateSetting())) {
            operationDateParam = serviceInstance.getPriceVersionDate();
        }

        if (operationDateParam != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(operationDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            operationDateParam = calendar.getTime();
        }

        List<PricePlanMatrixVersion> result = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.getPublishedVersionValideForDate", PricePlanMatrixVersion.class)
            .setParameter("pricePlanMatrixId", pricePlanId).setParameter("operationDate", operationDateParam).getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        if (result.size() > 1) {
            throw new RatingException("More than one pricePlanVersion for pricePlan '" + pricePlanId + "' matching date: " + operationDate);
        }
        return result.get(0);
    }

    /**
     * Get a valid Price plan version for the given operationDate.
     * 
     * @param ppmCode Price plan code
     * @param serviceInstance Service instance
     * @param operationDate Operation date
     * @return PricePlanMatrixVersion Matched Price plan version or NULL if nothing found
     * @throws RatingException More than one Price plan version was found for a given date
     */
    public PricePlanMatrixVersion getPublishedVersionValideForDate(String ppmCode, ServiceInstance serviceInstance, Date operationDate) throws RatingException {
        Date operationDateParam = new Date();
        if (serviceInstance == null || PriceVersionDateSettingEnum.EVENT.equals(serviceInstance.getPriceVersionDateSetting())) {
            operationDateParam = operationDate;
        } else if (PriceVersionDateSettingEnum.DELIVERY.equals(serviceInstance.getPriceVersionDateSetting()) || PriceVersionDateSettingEnum.RENEWAL.equals(serviceInstance.getPriceVersionDateSetting())
                || PriceVersionDateSettingEnum.QUOTE.equals(serviceInstance.getPriceVersionDateSetting())) {
            operationDateParam = serviceInstance.getPriceVersionDate();
        }

        if (operationDateParam != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(operationDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            operationDateParam = calendar.getTime();
        }

        List<PricePlanMatrixVersion> result = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.getPublishedVersionValideForDateByPpmCode", PricePlanMatrixVersion.class)
            .setParameter("pricePlanMatrixCode", ppmCode).setParameter("operationDate", operationDateParam).getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        if (result.size() > 1) {
            throw new RatingException("More than one pricePlaneVersion for pricePlan '" + ppmCode + "' matching date: " + operationDate);
        }
        return result.get(0);
    }

    /**
     * Determine a price plan matrix line matching wallet operation parameters. Business type attributes defined in a price plan version will be resolved via Wallet operation properties.
     * 
     * @param pricePlan Applicable price plan
     * @param bareWalletOperation Wallet operation to match
     * @return A matched Price plan matrix line
     * @throws NoPricePlanException No price plan line was matched
     */
    public PricePlanMatrixLine determinePricePlanLine(PricePlanMatrix pricePlan, WalletOperation bareWalletOperation) throws NoPricePlanException {
        return determinePricePlanLine(pricePlan.getId(), bareWalletOperation);
    }

    /**
     * Determine a price plan matrix line matching wallet operation parameters. Business type attributes defined in a price plan version will be resolved via Wallet operation properties.
     * 
     * @param pricePlanId Applicable price plan ID
     * @param bareWalletOperation Wallet operation to match
     * @return A matched Price plan matrix line
     * @throws NoPricePlanException No price plan line was matched
     */
    public PricePlanMatrixLine determinePricePlanLine(Long pricePlanId, WalletOperation bareWalletOperation) throws NoPricePlanException {
        PricePlanMatrixVersion ppmVersion = getPublishedVersionValidForDate(pricePlanId, bareWalletOperation.getServiceInstance(), bareWalletOperation.getOperationDate());
        if (ppmVersion != null) {
            PricePlanMatrixLine ppLine = determinePricePlanLine(ppmVersion, bareWalletOperation);
            if (ppLine != null) {
                return ppLine;
            }
        }
        return null;
    }

    /**
     * Determine a price plan matrix line matching wallet operation parameters. Business type attributes defined in a price plan version will be resolved via Wallet operation properties.
     * 
     * @param pricePlanMatrixVersion Price plan version
     * @param walletOperation Wallet operation to match
     * @return A matched Price plan matrix line
     * @throws NoPricePlanException No price plan line was matched
     */
    @SuppressWarnings("rawtypes")
    public PricePlanMatrixLine determinePricePlanLine(PricePlanMatrixVersion pricePlanMatrixVersion, WalletOperation walletOperation) throws NoPricePlanException {
        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        if (chargeInstance.getServiceInstance() == null) {
            return null;
        }
        Set<AttributeValue> attributeValues = chargeInstance.getServiceInstance().getAttributeInstances().stream().map(attributeInstance -> attributeInstanceService.getAttributeValue(attributeInstance, walletOperation))
            .collect(Collectors.toSet());

        addBusinessAttributeValues(pricePlanMatrixVersion.getColumns().stream().filter(column -> AttributeCategoryEnum.BUSINESS.equals(column.getAttribute().getAttributeCategory())).map(column -> column.getAttribute())
            .collect(Collectors.toList()), attributeValues, walletOperation);

        return determinePricePlanLine(pricePlanMatrixVersion, attributeValues);
    }

    /**
     * Determine a price matrix line matching the attribute values passed or throw and exception if matching was unsucessfull
     * 
     * @param pricePlanMatrixVersion Price plan version
     * @param attributeValues Attributes to match
     * @return A matched price matrix line
     * @throws NoPricePlanException No matching price line was found
     */
    @SuppressWarnings("rawtypes")
    public PricePlanMatrixLine determinePricePlanLine(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues) throws NoPricePlanException {
        PricePlanMatrixLine ppLine = determinePricePlanLineOptional(pricePlanMatrixVersion, attributeValues);
        if (ppLine == null) {
            throw new NoPricePlanException("No price match with price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + " id: "
                    + pricePlanMatrixVersion.getId() + ") using attributes : " + attributeValues);
        }
        return ppLine;
    }

    /**
     * Determine a price matrix line matching the attribute values passed
     * 
     * @param pricePlanMatrixVersion Price plan version
     * @param attributeValues Attributes to match
     * @return A matched price matrix line or NULL if no line was matched
     */
    @SuppressWarnings("rawtypes")
    public PricePlanMatrixLine determinePricePlanLineOptional(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues) {

        EntityManager em = getEntityManager();

        if (attributeValues == null || attributeValues.isEmpty()) {

            try {
                return em.createNamedQuery("PricePlanMatrixLine.findDefaultByPricePlanMatrixVersion", PricePlanMatrixLine.class).setParameter("pricePlanMatrixVersionId", pricePlanMatrixVersion.getId()).setMaxResults(1)
                    .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }

        } else {

            List<PricePlanMatrixValueForRating> ppValues = em.createNamedQuery("PricePlanMatrixValue.findByPPVersionForRating", PricePlanMatrixValueForRating.class)
                .setParameter("pricePlanMatrixVersionId", pricePlanMatrixVersion.getId()).getResultList();

            long lastPLId = -100;// A value indicating its initial value. Real values are expected to be above 0.
            boolean allMatch = true;
            Long matchedPlId = null;
            for (PricePlanMatrixValueForRating ppValue : ppValues) {
                // A new price plan Line
                if (lastPLId != ppValue.getPricePlanMatrixLineId()) {
                    // All values were matched in the Last Price plan line
                    if (lastPLId > 0 && allMatch) {
                        matchedPlId = lastPLId;
                        break;
                    }
                    lastPLId = ppValue.getPricePlanMatrixLineId();

                } else if (!allMatch) {
                    continue;
                }

                allMatch = ppValue.isMatch(attributeValues);

            }
            // Case when was matched the last item in the ppValue list (e.g. default value)
            if (matchedPlId == null && (lastPLId > 0 && allMatch)) {
                matchedPlId = lastPLId;
            }
            if (matchedPlId != null) {
                return em.find(PricePlanMatrixLine.class, matchedPlId);
            }
        }

        return null;
    }

    /**
     * Business type attributes are resolved via Wallet operation properties
     * 
     * @param businessAttributes Business type attributes
     * @param attributeValues Attribute values to supplement with new attributes
     * @param walletOperation Wallet operation to use for EL expression resolution
     */
    private void addBusinessAttributeValues(List<Attribute> businessAttributes, @SuppressWarnings("rawtypes") Set<AttributeValue> attributeValues, WalletOperation walletOperation) {
        businessAttributes.stream().forEach(attribute -> attributeValues.add(getBusinessAttributeValue(attribute, walletOperation)));
    }

    /**
     * Resolve attribute value from EL expression
     * 
     * @param attribute Attribute to resolve
     * @param op Wallet operation to use for EL expression resolution
     * @return Resolved Attribute value
     */
    @SuppressWarnings("rawtypes")
    private AttributeValue getBusinessAttributeValue(Attribute attribute, WalletOperation op) {
        Object value = ValueExpressionWrapper.evaluateExpression(attribute.getElValue(), Object.class, op);
        AttributeValue<AttributeValue> attributeValue = new AttributeValue<AttributeValue>(attribute, value);
        return attributeValue;
    }

    private RecurringChargeTemplate getRecurringChargeTemplateFromChargeInstance(ChargeInstance chargeInstance) {
        RecurringChargeTemplate recurringChargeTemplate = null;
        if (chargeInstance != null && chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING) {
            recurringChargeTemplate = ((RecurringChargeInstance) PersistenceUtils.initializeAndUnproxy(chargeInstance)).getRecurringChargeTemplate();
        }
        return recurringChargeTemplate;
    }
}