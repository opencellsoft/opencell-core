/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.catalog.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.BusinessService;
import org.meveo.service.cpq.QuoteAttributeService;
import org.meveo.service.cpq.QuoteProductService;

/**
 * @author Wassim Drira
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
public class PricePlanMatrixService extends BusinessService<PricePlanMatrix> {

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    @Inject
    private QuoteAttributeService quoteAttributeService;

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    // private ParamBean param = ParamBean.getInstance();

    // private SimpleDateFormat sdf = new SimpleDateFormat(param.getProperty("excelImport.dateFormat", "dd/MM/yyyy"));

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    public void createPP(PricePlanMatrix pp) throws BusinessException {

        pp.setCriteria1Value(StringUtils.stripToNull(pp.getCriteria1Value()));
        pp.setCriteria2Value(StringUtils.stripToNull(pp.getCriteria2Value()));
        pp.setCriteria3Value(StringUtils.stripToNull(pp.getCriteria3Value()));
        pp.setCriteriaEL(StringUtils.stripToNull(pp.getCriteriaEL()));
        pp.setAmountWithoutTaxEL(StringUtils.stripToNull(pp.getAmountWithoutTaxEL()));
        pp.setAmountWithTaxEL(StringUtils.stripToNull(pp.getAmountWithTaxEL()));
        validatePricePlan(pp);
        create(pp);
    }

    private void validatePricePlan(PricePlanMatrix pp) {
        List<PricePlanMatrix> pricePlanMatrices = listByChargeCode(pp.getEventCode());
        for (PricePlanMatrix pricePlanMatrix : pricePlanMatrices){
            if(!pricePlanMatrix.getId().equals(pp.getId()) &&
                    areValidityPeriodsOverlap(pp.getValidityFrom(), pp.getValidityDate(), pricePlanMatrix.getValidityFrom(), pricePlanMatrix.getValidityDate())){
                throw new BusinessException("price plan validity date overlaps with other charge price plans { "+pricePlanMatrix.getCode()+" } ");
            }
        }
    }

    private boolean areValidityPeriodsOverlap(Date start1, Date end1, Date start2, Date end2){
        return  (start1 != null && isDateBetween(start1, start2, end2)) || (end1 != null && isDateBetween(end1, start2, end2));
    }

    private boolean isDateBetween(Date date, Date from, Date to) {
        return date.equals(from) || (date.after(from) && (to != null && (!date.equals(to) && date.before(to))));
    }

    @Override
    public PricePlanMatrix update(PricePlanMatrix pp) throws BusinessException {

        pp.setCriteria1Value(StringUtils.stripToNull(pp.getCriteria1Value()));
        pp.setCriteria2Value(StringUtils.stripToNull(pp.getCriteria2Value()));
        pp.setCriteria3Value(StringUtils.stripToNull(pp.getCriteria3Value()));
        pp.setCriteriaEL(StringUtils.stripToNull(pp.getCriteriaEL()));
        pp.setAmountWithoutTaxEL(StringUtils.stripToNull(pp.getAmountWithoutTaxEL()));
        pp.setAmountWithTaxEL(StringUtils.stripToNull(pp.getAmountWithTaxEL()));
        validatePricePlan(pp);
        return super.update(pp);
    }

    private String getCellAsString(Cell cell) {
        switch (cell.getCellType()) {
        case BOOLEAN:
            return cell.getBooleanCellValue() + "";
        case ERROR:
        case BLANK:
        case FORMULA:
            return null;
        case NUMERIC:
            return "" + cell.getNumericCellValue();
        default:
            return cell.getStringCellValue();
        }
    }

    private Date getCellAsDate(Cell cell) {        
        switch (cell.getCellType()) {
        case ERROR:
        case BLANK:
        case FORMULA:
            return null;
        case NUMERIC:
            return DateUtil.getJavaDate(cell.getNumericCellValue());
        default:
            try {
                return cell.getDateCellValue();
            } catch (Exception e) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(paramBeanFactory.getInstance().getProperty("excelImport.dateFormat", "dd/MM/yyyy"));
                    return sdf.parse(cell.getStringCellValue());
                } catch (ParseException e1) {
                    return null;
                }
            }
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void importExcelLine(Row row) throws BusinessException {
        EntityManager em = getEntityManager();
        Object[] cellsObj = IteratorUtils.toArray(row.cellIterator());
        int rowIndex = row.getRowNum();
        int i = 0;
        String pricePlanCode = getCellAsString((Cell) cellsObj[i++]);

        PricePlanMatrix pricePlan = null;
        QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "p");
        qb.addCriterion("code", "=", pricePlanCode, false);

        @SuppressWarnings("unchecked")
        List<PricePlanMatrix> pricePlans = qb.getQuery(em).getResultList();

        if (pricePlans == null || pricePlans.size() == 0) {
            pricePlan = new PricePlanMatrix();
        } else if (pricePlans.size() == 1) {
            pricePlan = pricePlans.get(0);
        } else {
            throw new BusinessException("More than one priceplan in line=" + rowIndex + "with code=" + pricePlanCode);
        }

        String pricePlanDescription = getCellAsString((Cell) cellsObj[i++]);
        String eventCode = getCellAsString((Cell) cellsObj[i++]);
        String sellerCode = getCellAsString((Cell) cellsObj[i++]);
        String countryCode = getCellAsString((Cell) cellsObj[i++]);
        String currencyCode = getCellAsString((Cell) cellsObj[i++]);
        try {
            pricePlan.setStartSubscriptionDate(getCellAsDate((Cell) cellsObj[i++]));
        } catch (Exception e) {
            throw new BusinessException("Invalid startAppli in line=" + rowIndex + " expected format:"
                    + paramBeanFactory.getInstance().getProperty("excelImport.dateFormat", "dd/MM/yyyy") + ", you may change the property excelImport.dateFormat.");
        }
        try {
            pricePlan.setEndSubscriptionDate(getCellAsDate((Cell) cellsObj[i++]));
        } catch (Exception e) {
            throw new BusinessException("Invalid endAppli in line=" + rowIndex + " expected format:"
                    + paramBeanFactory.getInstance().getProperty("excelImport.dateFormat", "dd/MM/yyyy") + ", you may change the property excelImport.dateFormat.");
        }
        String offerCode = getCellAsString((Cell) cellsObj[i++]);
        String priority = getCellAsString((Cell) cellsObj[i++]);
        String amountWOTax = getCellAsString((Cell) cellsObj[i++]);
        String amountWithTax = getCellAsString((Cell) cellsObj[i++]);
        String amountWOTaxEL = getCellAsString((Cell) cellsObj[i++]);
        String amountWithTaxEL = getCellAsString((Cell) cellsObj[i++]);
        String minQuantity = getCellAsString((Cell) cellsObj[i++]);
        String maxQuantity = getCellAsString((Cell) cellsObj[i++]);
        String criteria1 = getCellAsString((Cell) cellsObj[i++]);
        String criteria2 = getCellAsString((Cell) cellsObj[i++]);
        String criteria3 = getCellAsString((Cell) cellsObj[i++]);
        String criteriaEL = getCellAsString((Cell) cellsObj[i++]);
        try {
            pricePlan.setStartRatingDate(getCellAsDate((Cell) cellsObj[i++]));
        } catch (Exception e) {
            throw new BusinessException("Invalid startRating in line=" + rowIndex + " expected format:"
                    + paramBeanFactory.getInstance().getProperty("excelImport.dateFormat", "dd/MM/yyyy") + ", you may change the property excelImport.dateFormat.");
        }
        try {
            pricePlan.setEndRatingDate(getCellAsDate((Cell) cellsObj[i++]));
        } catch (Exception e) {
            throw new BusinessException("Invalid endRating in line=" + rowIndex + " expected format:"
                    + paramBeanFactory.getInstance().getProperty("excelImport.dateFormat", "dd/MM/yyyy") + ", you may change the property excelImport.dateFormat.");
        }
        String minSubAge = getCellAsString((Cell) cellsObj[i++]);
        String maxSubAge = getCellAsString((Cell) cellsObj[i++]);
        String validityCalendarCode = getCellAsString((Cell) cellsObj[i++]);
        log.debug(
            "priceplanCode={}, priceplanDescription= {}, chargeCode={} sellerCode={}, countryCode={}, currencyCode={},"
                    + " startSub={}, endSub={}, offerCode={}, priority={}, amountWOTax={}, amountWithTax={},amountWOTaxEL={}, amountWithTaxEL={},"
                    + " minQuantity={}, maxQuantity={}, criteria1={}, criteria2={}, criteria3={}, criteriaEL={},"
                    + " startRating={}, endRating={}, minSubAge={}, maxSubAge={}, validityCalendarCode={}",
            new Object[] { pricePlanCode, pricePlanDescription, eventCode, sellerCode, countryCode, currencyCode, pricePlan.getStartSubscriptionDate(),
                    pricePlan.getEndSubscriptionDate(), offerCode, priority, amountWOTax, amountWithTax, amountWOTaxEL, amountWithTaxEL, minQuantity, maxQuantity, criteria1,
                    criteria2, criteria3, criteriaEL, pricePlan.getStartRatingDate(), pricePlan.getEndRatingDate(), minSubAge, maxSubAge, validityCalendarCode });

        if (!StringUtils.isBlank(eventCode)) {
            qb = new QueryBuilder(ChargeTemplate.class, "p");
            qb.addCriterion("code", "=", eventCode, false);

            @SuppressWarnings("unchecked")
            List<Seller> charges = qb.getQuery(em).getResultList();
            if (charges.size() == 0) {
                throw new BusinessException("cannot find charge in line=" + rowIndex + " with code=" + eventCode);
            } else if (charges.size() > 1) {
                throw new BusinessException("more than one charge in line=" + rowIndex + " with code=" + eventCode);
            }
            pricePlan.setEventCode(eventCode);
        } else {
            throw new BusinessException("Empty chargeCode in line=" + rowIndex + ", code=" + eventCode);
        }

        // Seller
        if (!StringUtils.isBlank(sellerCode)) {
            qb = new QueryBuilder(Seller.class, "p");
            qb.addCriterion("code", "=", sellerCode, false);

            @SuppressWarnings("unchecked")
            List<Seller> sellers = qb.getQuery(em).getResultList();
            Seller seller = null;

            if (sellers == null || sellers.size() == 0) {
                throw new BusinessException("Invalid seller in line=" + rowIndex + ", code=" + sellerCode);
            }

            seller = sellers.get(0);
            pricePlan.setSeller(seller);
        } else {
            pricePlan.setSeller(null);
        }

        // Country
        if (!StringUtils.isBlank(countryCode)) {
            qb = new QueryBuilder(TradingCountry.class, "p");
            qb.addCriterion("p.country.countryCode", "=", countryCode, false);

            @SuppressWarnings("unchecked")
            List<TradingCountry> countries = qb.getQuery(em).getResultList();
            TradingCountry tradingCountry = null;

            if (countries == null || countries.size() == 0) {
                throw new BusinessException("Invalid country in line=" + rowIndex + ", code=" + countryCode);
            }

            tradingCountry = countries.get(0);
            pricePlan.setTradingCountry(tradingCountry);
        } else {
            pricePlan.setTradingCountry(null);
        }

        // Currency
        if (!StringUtils.isBlank(currencyCode)) {
            qb = new QueryBuilder(TradingCurrency.class, "p");
            qb.addCriterion("p.currency.currencyCode", "=", currencyCode, false);

            @SuppressWarnings("unchecked")
            List<TradingCurrency> currencies = qb.getQuery(em).getResultList();
            TradingCurrency tradingCurrency = null;

            if (currencies == null || currencies.size() == 0) {
                throw new BusinessException("Invalid currency in line=" + rowIndex + ", code=" + countryCode);
            }

            tradingCurrency = currencies.get(0);
            pricePlan.setTradingCurrency(tradingCurrency);
        } else {
            pricePlan.setTradingCurrency(null);
        }

        if (!StringUtils.isBlank(pricePlanCode)) {
            pricePlan.setCode(pricePlanCode);
        } else {
            throw new BusinessException("Invalid priceplan code in line=" + rowIndex + ", code=" + offerCode);
        }

        if (!StringUtils.isBlank(pricePlanDescription)) {
            pricePlan.setDescription(pricePlanDescription);
        } else {
            pricePlan.setDescription(pricePlanCode);
        }

        // OfferCode
        if (!StringUtils.isBlank(offerCode)) {
            qb = new QueryBuilder(OfferTemplate.class, "p");
            qb.addCriterion("code", "=", offerCode, false);

            @SuppressWarnings("unchecked")
            List<OfferTemplate> offers = qb.getQuery(em).getResultList();
            OfferTemplate offer = null;

            if (offers == null || offers.size() == 0) {
                throw new BusinessException("Invalid offer code in line=" + rowIndex + ", code=" + offerCode);
            }

            offer = offers.get(0);
            pricePlan.setOfferTemplate(offer);
        } else {
            pricePlan.setOfferTemplate(null);
        }

        if (!StringUtils.isBlank(validityCalendarCode)) {
            qb = new QueryBuilder(Calendar.class, "p");
            qb.addCriterion("code", "=", validityCalendarCode, false);

            @SuppressWarnings("unchecked")
            List<Calendar> calendars = qb.getQuery(em).getResultList();
            Calendar calendar = null;

            if (calendars == null || calendars.size() == 0) {
                throw new BusinessException("Invalid calendars code in line=" + rowIndex + ", code=" + validityCalendarCode);
            }

            calendar = calendars.get(0);
            pricePlan.setValidityCalendar(calendar);
        } else {
            pricePlan.setValidityCalendar(null);
        }

        // Priority
        if (!StringUtils.isBlank(priority)) {
            try {
                pricePlan.setPriority(Integer.parseInt(priority));
            } catch (Exception e) {
                throw new BusinessException("Invalid priority in line=" + rowIndex + ", priority=" + priority);
            }
        } else {
            pricePlan.setPriority(1);
        }

        // AmountWOTax
        if (!StringUtils.isBlank(amountWOTax)) {
            try {
                pricePlan.setAmountWithoutTax(new BigDecimal(amountWOTax));
            } catch (Exception e) {
                throw new BusinessException("Invalid amount wo tax in line=" + rowIndex + ", amountWOTax=" + amountWOTax);
            }
        } else {
            throw new BusinessException("Amount wo tax in line=" + rowIndex + " should not be empty");
        }

        // AmountWithTax
        if (!StringUtils.isBlank(amountWithTax)) {
            try {
                pricePlan.setAmountWithTax(new BigDecimal(amountWithTax));
            } catch (Exception e) {
                throw new BusinessException("Invalid amount wo tax in line=" + rowIndex + ", amountWithTax=" + amountWithTax);
            }
        } else {
            pricePlan.setAmountWithTax(null);
        }

        if (!StringUtils.isBlank(amountWOTaxEL)) {
            pricePlan.setAmountWithoutTaxEL(amountWOTaxEL);
        } else {
            pricePlan.setAmountWithoutTaxEL(null);
        }

        if (!StringUtils.isBlank(amountWithTaxEL)) {
            pricePlan.setAmountWithTaxEL(amountWithTaxEL);
        } else {
            pricePlan.setAmountWithTaxEL(null);
        }
        // minQuantity
        if (!StringUtils.isBlank(minQuantity)) {
            try {
                pricePlan.setMinQuantity(new BigDecimal(minQuantity));
            } catch (Exception e) {
                throw new BusinessException("Invalid minQuantity in line=" + rowIndex + ", minQuantity=" + minQuantity);
            }
        } else {
            pricePlan.setMinQuantity(null);
        }

        // maxQuantity
        if (!StringUtils.isBlank(maxQuantity)) {
            try {
                pricePlan.setMaxQuantity(new BigDecimal(maxSubAge));
            } catch (Exception e) {
                throw new BusinessException("Invalid maxQuantity in line=" + rowIndex + ", maxQuantity=" + maxQuantity);
            }
        } else {
            pricePlan.setMaxQuantity(null);
        }

        // Criteria1
        if (!StringUtils.isBlank(criteria1)) {
            try {
                pricePlan.setCriteria1Value(criteria1);
            } catch (Exception e) {
                throw new BusinessException("Invalid criteria1 in line=" + rowIndex + ", criteria1=" + criteria1);
            }
        } else {
            pricePlan.setCriteria1Value(null);
        }

        // Criteria2
        if (!StringUtils.isBlank(criteria2)) {
            try {
                pricePlan.setCriteria2Value(criteria2);
            } catch (Exception e) {
                throw new BusinessException("Invalid criteria2 in line=" + rowIndex + ", criteria2=" + criteria2);
            }
        } else {
            pricePlan.setCriteria2Value(null);
        }

        // Criteria3
        if (!StringUtils.isBlank(criteria3)) {
            try {
                pricePlan.setCriteria3Value(criteria3);
            } catch (Exception e) {
                throw new BusinessException("Invalid criteria3 in line=" + rowIndex + ", criteria3=" + criteria3);
            }
        } else {
            pricePlan.setCriteria3Value(null);
        }

        // CriteriaEL
        if (!StringUtils.isBlank(criteriaEL)) {
            try {
                pricePlan.setCriteriaEL(criteriaEL);
                ;
            } catch (Exception e) {
                throw new BusinessException("Invalid criteriaEL in line=" + rowIndex + ", criteriaEL=" + criteriaEL);
            }
        } else {
            pricePlan.setCriteriaEL(null);
        }

        // minSubAge
        if (!StringUtils.isBlank(minSubAge)) {
            try {
                pricePlan.setMinSubscriptionAgeInMonth(Long.parseLong(minSubAge));
            } catch (Exception e) {
                throw new BusinessException("Invalid minSubAge in line=" + rowIndex + ", minSubAge=" + minSubAge);
            }
        } else {
            pricePlan.setMinSubscriptionAgeInMonth(0L);
        }

        // maxSubAge
        if (!StringUtils.isBlank(maxSubAge)) {
            try {
                pricePlan.setMaxSubscriptionAgeInMonth(Long.parseLong(maxSubAge));
            } catch (Exception e) {
                throw new BusinessException("Invalid maxSubAge in line=" + rowIndex + ", maxSubAge=" + maxSubAge);
            }
        } else {
            pricePlan.setMaxSubscriptionAgeInMonth(9999L);
        }

        if (pricePlan.getId() == null) {
            createPP(pricePlan);
        } else {
            updateNoCheck(pricePlan);
        }
    }

    @SuppressWarnings("unchecked")
    public List<PricePlanMatrix> findByOfferTemplate(OfferTemplate offerTemplate) {
        QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "p");
        qb.addCriterionEntity("offerTemplate", offerTemplate);

        try {
            return (List<PricePlanMatrix>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to find pricePlanMatrix By offerTemplate", e);
            return null;
        }
    }

    public List<PricePlanMatrix> getActivePricePlansByOfferAndChargeCode(String offerTemplateCode, String chargeCode) {

        List<PricePlanMatrix> priceplansByOffer = new ArrayList<>();

        List<PricePlanMatrix> priceplans = getActivePricePlansByChargeCode(chargeCode);
        if (priceplans == null) {
            return priceplansByOffer;
        }
        for (PricePlanMatrix pricePlan : priceplans) {
            if (offerTemplateCode == null) {
                if (pricePlan.getOfferTemplate() == null) {
                    priceplansByOffer.add(pricePlan);
                }

            } else if (pricePlan.getOfferTemplate() != null && pricePlan.getOfferTemplate().getCode().equals(offerTemplateCode)) {
                priceplansByOffer.add(pricePlan);
            }
        }

        return priceplansByOffer;
    }

    /**
     * Get all price plans for a given charge code
     * 
     * @param chargeCode Charge code
     * @return A list of price plans matching a charge code
     */
    @SuppressWarnings("unchecked")
    public List<PricePlanMatrix> listByChargeCode(String chargeCode) {
        QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "m", null);
        qb.addCriterion("eventCode", "=", chargeCode, true);
        qb.addOrderCriterionAsIs("priority", true);

        try {
            return (List<PricePlanMatrix>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Get active price plans for a given charge code. Only these are applicable for rating.
     * 
     * @param chargeCode Charge code
     * @return A list of applicable price plans matching a charge code and ordered by priority
     */
    public List<PricePlanMatrix> getActivePricePlansByChargeCode(String chargeCode) {
        return getEntityManager().createNamedQuery("PricePlanMatrix.getActivePricePlansByChargeCode", PricePlanMatrix.class).setParameter("chargeCode", chargeCode).getResultList();
    }

    public Long getLastPricePlanSequenceByChargeCode(String chargeCode) {
        QueryBuilder qb = new QueryBuilder("select max(sequence) from PricePlanMatrix m");
        qb.addCriterion("m.eventCode", "=", chargeCode, true);
        try {
            Long result = (Long) qb.getQuery(getEntityManager()).getSingleResult();
            return result == null ? 0L : result;
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean updateCellEdit(PricePlanMatrix entity) throws BusinessException {
        boolean result = false;
        PricePlanMatrix pricePlanMatrix = findById(entity.getId());
        if (pricePlanMatrix != null) {
            if (!equal(entity.getCode(), pricePlanMatrix.getCode())) {
                PricePlanMatrix existed = findByCode(entity.getCode());
                if (existed != null) {
                    throw new BusinessException("Price plan " + entity.getCode() + " is existed!");
                } else {
                    pricePlanMatrix.setCode(entity.getCode());
                    result = true;
                }
            }
            if (!equal(entity.getDescription(), pricePlanMatrix.getDescription())) {
                pricePlanMatrix.setDescription(entity.getDescription());
                result = true;
            }
            if (!equal(entity.getEventCode(), pricePlanMatrix.getEventCode())) {
                pricePlanMatrix.setEventCode(entity.getEventCode());
                result = true;
            }
            if (!(equal(entity.getOfferTemplate(), pricePlanMatrix.getOfferTemplate()))) {
                pricePlanMatrix.setOfferTemplate(entity.getOfferTemplate());
                result = true;
            }
            if (!equal(entity.getSeller(), pricePlanMatrix.getSeller())) {
                pricePlanMatrix.setSeller(entity.getSeller());
                result = true;
            }
            if (!equal(entity.getAmountWithTax(), pricePlanMatrix.getAmountWithTax())) {
                pricePlanMatrix.setAmountWithTax(entity.getAmountWithTax());
                result = true;
            }
            if (!equal(entity.getAmountWithoutTax(), pricePlanMatrix.getAmountWithoutTax())) {
                pricePlanMatrix.setAmountWithoutTax(entity.getAmountWithoutTax());
                result = true;
            }
            if (!equal(entity.getAmountWithoutTaxEL(), pricePlanMatrix.getAmountWithoutTaxEL())) {
                pricePlanMatrix.setAmountWithoutTaxEL(entity.getAmountWithoutTaxEL());
                result = true;
            }
            if (!equal(entity.getAmountWithTaxEL(), pricePlanMatrix.getAmountWithTaxEL())) {
                pricePlanMatrix.setAmountWithTaxEL(entity.getAmountWithTaxEL());
                result = true;
            }
            if (!equal(entity.getStartRatingDate(), pricePlanMatrix.getStartRatingDate())) {
                pricePlanMatrix.setStartRatingDate(entity.getStartRatingDate());
                result = true;
            }
            if (!equal(entity.getEndRatingDate(), pricePlanMatrix.getEndRatingDate())) {
                pricePlanMatrix.setEndRatingDate(entity.getEndRatingDate());
                result = true;
            }
            if (!equal(entity.getCriteriaEL(), pricePlanMatrix.getCriteriaEL())) {
                pricePlanMatrix.setCriteriaEL(entity.getCriteriaEL());
                result = true;
            }
            if (!equal(entity.getTradingCountry(), pricePlanMatrix.getTradingCountry())) {
                pricePlanMatrix.setTradingCountry(entity.getTradingCountry());
                result = true;
            }
            if (!equal(entity.getTradingCurrency(), pricePlanMatrix.getTradingCurrency())) {
                pricePlanMatrix.setTradingCurrency(entity.getTradingCurrency());
                result = true;
            }
            if (!equal(entity.getCriteria1Value(), pricePlanMatrix.getCriteria1Value())) {
                pricePlanMatrix.setCriteria1Value(entity.getCriteria1Value());
                result = true;
            }
            if (!equal(entity.getCriteria2Value(), pricePlanMatrix.getCriteria2Value())) {
                pricePlanMatrix.setCriteria2Value(entity.getCriteria2Value());
                result = true;
            }
            if (!equal(entity.getCriteria3Value(), pricePlanMatrix.getCriteria3Value())) {
                pricePlanMatrix.setCriteria3Value(entity.getCriteria3Value());
                result = true;
            }
            if (!equal(entity.getPriority(), pricePlanMatrix.getPriority())) {
                pricePlanMatrix.setPriority(entity.getPriority());
                result = true;
            }
            if (!equal(entity.getMinQuantity(), pricePlanMatrix.getMinQuantity())) {
                pricePlanMatrix.setMinQuantity(entity.getMinQuantity());
                result = true;
            }
            if (!equal(entity.getMaxQuantity(), pricePlanMatrix.getMaxQuantity())) {
                pricePlanMatrix.setMaxQuantity(entity.getMaxQuantity());
                result = true;
            }
            if (!equal(entity.getStartSubscriptionDate(), pricePlanMatrix.getStartSubscriptionDate())) {
                pricePlanMatrix.setStartSubscriptionDate(entity.getStartSubscriptionDate());
                result = true;
            }
            if (!equal(entity.getEndSubscriptionDate(), pricePlanMatrix.getEndSubscriptionDate())) {
                pricePlanMatrix.setEndSubscriptionDate(entity.getEndSubscriptionDate());
                result = true;
            }
            if (!equal(entity.getMaxSubscriptionAgeInMonth(), pricePlanMatrix.getMaxSubscriptionAgeInMonth())) {
                pricePlanMatrix.setMaxSubscriptionAgeInMonth(entity.getMaxSubscriptionAgeInMonth());
                result = true;
            }
            if (!equal(entity.getMinSubscriptionAgeInMonth(), pricePlanMatrix.getMinSubscriptionAgeInMonth())) {
                pricePlanMatrix.setMinSubscriptionAgeInMonth(entity.getMinSubscriptionAgeInMonth());
                result = true;
            }
            if (!equal(entity.getValidityCalendar(), pricePlanMatrix.getValidityCalendar())) {
                pricePlanMatrix.setValidityCalendar(entity.getValidityCalendar());
                result = true;
            }
            if (result) {
                update(pricePlanMatrix);
            }
        }
        return result;
    }

    public boolean equal(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        return obj1 != null ? obj1.equals(obj2) : (obj2 != null ? false : true);
    }

    public synchronized void duplicate(PricePlanMatrix pricePlan) throws BusinessException {
        pricePlan = refreshOrRetrieve(pricePlan);
        String code = findDuplicateCode(pricePlan);
        detach(pricePlan);
        pricePlan.setId(null);
        pricePlan.clearUuid();
        pricePlan.setCode(code);
        createPP(pricePlan);
    }

    public PricePlanMatrixDto findPricePlanMatrix(String pricePlanCode) {
        PricePlanMatrix pricePlanMatrix = findByCode(pricePlanCode);
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanCode);
        }

        return new PricePlanMatrixDto(pricePlanMatrix, entityToDtoConverter.getCustomFieldsDTO(pricePlanMatrix, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }

    public List<PricePlanMatrixLineDto> loadPrices(PricePlanMatrixVersion pricePlanMatrixVersion, QuoteProduct quoteProduct) {

        List<PricePlanMatrixColumn> pricePlanMatrixColumns = pricePlanMatrixColumnService.findByProduct(quoteProduct.getProductVersion().getProduct());

        List<QuoteAttribute> quoteAttributes = pricePlanMatrixColumns.stream()
                .map(column -> quoteAttributeService.findByAttributeAndQuoteProduct(column.getAttribute().getId(), quoteProduct.getId()))
                .collect(Collectors.toList());

        return pricePlanMatrixLineService.loadMatchedLines(pricePlanMatrixVersion, quoteAttributes)
                .stream()
                .map(PricePlanMatrixLineDto::new)
                .collect(Collectors.toList());

    }
}