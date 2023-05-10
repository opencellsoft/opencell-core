package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.ELUtils;
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
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PricePlanSelectionService implements Serializable {

    private static final long serialVersionUID = 6228282058872986933L;

    Logger log = LoggerFactory.getLogger(PricePlanSelectionService.class);

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private AttributeInstanceService attributeInstanceService;

    @Inject
    private ELUtils elUtils;

    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    /**
     * Determine a price plan that match a wallet operation and buyer country and currency
     * 
     * @param bareWalletOperation Wallet operation to determine price for
     * @param buyerCountryId Buyer country id
     * @param buyerCurrency Buyer currency
     * @return Price plan matched
     * @throws NoPricePlanException No price plan line was matched
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public PricePlanMatrix determineDefaultPricePlan(WalletOperation bareWalletOperation, Long buyerCountryId, TradingCurrency buyerCurrency) throws NoPricePlanException, InvalidELException {

        EntityManager em = getEntityManager();
        List<PricePlanMatrixForRating> chargePricePlans = em.createNamedQuery("PricePlanMatrix.getActivePricePlansByChargeCodeForRating", PricePlanMatrixForRating.class)
            .setParameter("chargeCode", bareWalletOperation.getCode()).getResultList();

        if (chargePricePlans == null || chargePricePlans.isEmpty()) {
            throw new NoPricePlanException("No active price plan found for charge code " + bareWalletOperation.getCode());
        }
        PricePlanMatrixForRating pricePlan = matchPricePlan(chargePricePlans, bareWalletOperation, buyerCountryId, buyerCurrency);
        if (pricePlan == null) {
            throw new NoPricePlanException("No price plan matched to rate WO for charge code " + bareWalletOperation.getCode());
        }
        return em.getReference(PricePlanMatrix.class, pricePlan.getId());
    }

    /**
     * Find a matching price plan for a given wallet operation
     *
     * @param listPricePlan List of price plans to consider
     * @param bareOperation Wallet operation to lookup price plan for
     * @param buyerCountryId Buyer's county id
     * @param buyerCurrency Buyer's trading currency
     * @return Matched price plan
     * @throws InvalidELException Failed to evaluate EL expression
     */
    private PricePlanMatrixForRating matchPricePlan(List<PricePlanMatrixForRating> listPricePlan, WalletOperation bareOperation, Long buyerCountryId, TradingCurrency buyerCurrency) throws InvalidELException {
        // FIXME: the price plan properties could be null !
        Date startDate = bareOperation.getStartDate();
        Date endDate = bareOperation.getEndDate();

        RecurringChargeTemplate recurringChargeTemplate = getRecurringChargeTemplateFromChargeInstance(bareOperation.getChargeInstance());

        for (PricePlanMatrixForRating pricePlan : listPricePlan) {

            log.trace("Try to verify price plan {} for WO {}", pricePlan.getId(), bareOperation.getCode());

            Long sellerId = pricePlan.getSeller();
            boolean sellerAreEqual = sellerId == null || sellerId.equals(bareOperation.getSeller().getId());
            if (!sellerAreEqual) {
                continue;
            }

            Long tradingCountryId = pricePlan.getTradingCountry();
            boolean countryAreEqual = tradingCountryId == null || tradingCountryId.equals(buyerCountryId);
            if (!countryAreEqual) {
                continue;
            }

            Long tradingCurrencyId = pricePlan.getTradingCurrency();
            boolean currencyAreEqual = tradingCurrencyId == null || (buyerCurrency != null && buyerCurrency.getId().equals(tradingCurrencyId));
            if (!currencyAreEqual) {
                continue;
            }
            Date subscriptionDate = bareOperation.getSubscriptionDate();
            Date startSubscriptionDate = pricePlan.getStartSubscriptionDate();
            Date endSubscriptionDate = pricePlan.getEndSubscriptionDate();
            boolean subscriptionDateInPricePlanPeriod = subscriptionDate == null || ((startSubscriptionDate == null || subscriptionDate.after(startSubscriptionDate) || subscriptionDate.equals(startSubscriptionDate))
                    && (endSubscriptionDate == null || subscriptionDate.before(endSubscriptionDate)));
            if (!subscriptionDateInPricePlanPeriod) {
//                log.trace("The subscription date {} is not in the priceplan subscription range {} - {}", subscriptionDate, startSubscriptionDate, endSubscriptionDate);
                continue;
            }

            int subscriptionAge = 0;
            Date operationDate = bareOperation.getOperationDate();
            if (subscriptionDate != null && operationDate != null) {
                subscriptionAge = DateUtils.monthsBetween(operationDate, DateUtils.addDaysToDate(subscriptionDate, -1));
            }

            boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
            if (!subscriptionMinAgeOK) {
//                log.trace("The subscription age={} is less than the priceplan subscription age min={}", subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
                continue;
            }
            Long maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
            boolean subscriptionMaxAgeOK = maxSubscriptionAgeInMonth == null || maxSubscriptionAgeInMonth == 0 || subscriptionAge < maxSubscriptionAgeInMonth;
            if (!subscriptionMaxAgeOK) {
//                log.trace("The subscription age {} is greater than the priceplan subscription age max {}", subscriptionAge, maxSubscriptionAgeInMonth);
                continue;
            }

            Date startRatingDate = pricePlan.getStartRatingDate();
            Date endRatingDate = pricePlan.getEndRatingDate();
            boolean applicationDateInPricePlanPeriod = (startRatingDate == null || operationDate.after(startRatingDate) || operationDate.equals(startRatingDate))
                    && (endRatingDate == null || operationDate.before(endRatingDate));
            if (!applicationDateInPricePlanPeriod) {
//                log.trace("The application date {} is not in the priceplan application range {} - {}", operationDate, startRatingDate, endRatingDate);
                continue;
            }

            String criteria1Value = pricePlan.getCriteria1Value();
            boolean criteria1SameInPricePlan = criteria1Value == null || criteria1Value.equals(bareOperation.getParameter1());
            if (!criteria1SameInPricePlan) {
//                log.trace("The operation param1 {} is not compatible with price plan criteria 1: {}", bareOperation.getParameter1(), criteria1Value);
                continue;
            }
            String criteria2Value = pricePlan.getCriteria2Value();
            String parameter2 = bareOperation.getParameter2();
            boolean criteria2SameInPricePlan = criteria2Value == null || criteria2Value.equals(parameter2);
            if (!criteria2SameInPricePlan) {
//                log.trace("The operation param2 {} is not compatible with price plan criteria 2: {}", parameter2, criteria2Value);
                continue;
            }
            String criteria3Value = pricePlan.getCriteria3Value();
            boolean criteria3SameInPricePlan = criteria3Value == null || criteria3Value.equals(bareOperation.getParameter3());
            if (!criteria3SameInPricePlan) {
//                log.trace("The operation param3 {} is not compatible with price plan criteria 3: {}", bareOperation.getParameter3(), criteria3Value);
                continue;
            }
            if (!StringUtils.isBlank(pricePlan.getCriteriaEL())) {
                UserAccount ua = bareOperation.getWallet().getUserAccount();
                if (!elUtils.evaluateBooleanExpression(pricePlan.getCriteriaEL(), bareOperation, ua, null, pricePlan, null)) {
//                    log.trace("The operation is not compatible with price plan criteria EL: {}", pricePlan.getCriteriaEL());
                    continue;
                }
            }

            Long ppOfferTemplateId = pricePlan.getOfferTemplate();
            if (ppOfferTemplateId != null) {
                boolean offerCodeSameInPricePlan = true;

                if (bareOperation.getOfferTemplate() != null) {
                    offerCodeSameInPricePlan = bareOperation.getOfferTemplate().getId().equals(ppOfferTemplateId);
//                } else if (bareOperation.getOfferCode() != null) {
//                    offerCodeSameInPricePlan = ppOfferTemplateId.getCode().equals(bareOperation.getOfferCode());
                }

                if (!offerCodeSameInPricePlan) {
//                    log.trace("The operation offerCode {} is not compatible with price plan offerCode: {}", bareOperation.getOfferTemplate() != null ? bareOperation.getOfferTemplate() : bareOperation.getOfferCode(),
//                        ppOfferTemplateId);
                    continue;
                }
            }

            BigDecimal maxQuantity = pricePlan.getMaxQuantity();
            BigDecimal quantity = bareOperation.getQuantity();
            boolean quantityMaxOk = maxQuantity == null || maxQuantity.compareTo(quantity) > 0;
            if (!quantityMaxOk) {
//                log.trace("The quantity " + quantity + " is strictly greater than " + maxQuantity);
                continue;
            }

            BigDecimal minQuantity = pricePlan.getMinQuantity();
            boolean quantityMinOk = minQuantity == null || minQuantity.compareTo(quantity) <= 0;
            if (!quantityMinOk) {
//                log.trace("The quantity " + quantity + " is less than " + minQuantity);
                continue;
            }
            if ((recurringChargeTemplate != null && recurringChargeTemplate.isProrataOnPriceChange())
                    && (!isStartDateBetween(startDate, pricePlan.getValidityFrom(), pricePlan.getValidityDate()) || !isEndDateBetween(endDate, startDate, pricePlan.getValidityDate()))) {
                continue;
            }

            if (pricePlan.getValidityCalendar() != null) {
                org.meveo.model.catalog.Calendar validityCalendar = getEntityManager().find(org.meveo.model.catalog.Calendar.class, pricePlan.getValidityCalendar());
                boolean validityCalendarOK = validityCalendar.previousCalendarDate(operationDate) != null;
                if (!validityCalendarOK) {
//                    log.trace("The operation date " + operationDate + " does not match pricePlan validity calendar " + validityCalendar.getCode() + "period range ");
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
     * @param ppmId Price plan ID
     * @param serviceInstance Service instance
     * @param operationDate Operation date
     * @return PricePlanMatrixVersion Matched Price plan version or NULL if nothing found
     * @throws RatingException More than one Price plan version was found for a given date
     */
    public PricePlanMatrixVersion getPublishedVersionValidForDate(Long ppmId, ServiceInstance serviceInstance, Date operationDate) throws RatingException {
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

        List<PricePlanMatrixVersion> result = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.getPublishedVersionValideForDate", PricePlanMatrixVersion.class).setParameter("pricePlanMatrixId", ppmId)
            .setParameter("operationDate", operationDateParam).getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        if (result.size() > 1) {
            throw new RatingException("More than one pricePlanVersion for pricePlan '" + ppmId + "' matching date: " + operationDate);
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
        PricePlanMatrixVersion ppmVersion = getPublishedVersionValidForDate(pricePlan.getId(), bareWalletOperation.getServiceInstance(), bareWalletOperation.getOperationDate());
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
                    } else {
                        allMatch = true;
                    }
                    lastPLId = ppValue.getPricePlanMatrixLineId();

                } else if (!allMatch) {
                    continue;
                }

                allMatch = ppValue.isMatch(attributeValues);

            }

            if (matchedPlId != null) {
                return em.find(PricePlanMatrixLine.class, matchedPlId);
            }
        }

        return null;
    }

    /**
     * @param businessAttributes
     * @param attributeValues
     * @param walletOperation
     */
    private void addBusinessAttributeValues(List<Attribute> businessAttributes, Set<AttributeValue> attributeValues, WalletOperation walletOperation) {
        businessAttributes.stream().forEach(attribute -> attributeValues.add(getBusinessAttributeValue(attribute, walletOperation)));
    }

    /**
     * @param attribute
     * @return
     */
    @SuppressWarnings("rawtypes")
    private AttributeValue getBusinessAttributeValue(Attribute attribute, WalletOperation op) {
        Object value = ValueExpressionWrapper.evaluateExpression(attribute.getElValue(), Object.class, op);
        AttributeValue<AttributeValue> attributeValue = new AttributeValue<AttributeValue>(attribute, value);
        return attributeValue;
    }

    private boolean isStartDateBetween(Date date, Date from, Date to) {
        return (from != null && (date.equals(from) || (date.after(from))));
    }

    private boolean isEndDateBetween(Date date, Date from, Date to) {
        return date.after(from) && (to == null || (date.before(to) || date.equals(to)));
    }

    private RecurringChargeTemplate getRecurringChargeTemplateFromChargeInstance(ChargeInstance chargeInstance) {
        RecurringChargeTemplate recurringChargeTemplate = null;
        if (chargeInstance != null && chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING) {
            recurringChargeTemplate = ((RecurringChargeInstance) PersistenceUtils.initializeAndUnproxy(chargeInstance)).getRecurringChargeTemplate();
        }
        return recurringChargeTemplate;
    }
}