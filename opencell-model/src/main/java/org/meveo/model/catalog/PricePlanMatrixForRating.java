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
package org.meveo.model.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Price plan DTO style entity
 * 
 * @author Andrius Karpavicius
 */
public class PricePlanMatrixForRating implements Comparable<PricePlanMatrixForRating>, Serializable {

    private static final long serialVersionUID = -3464323449652262449L;

    private Long id;

    private String code;

    /**
     * Filtering criteria - Offer template
     */
    private Long offerTemplate;

    /**
     * Filtering criteria - subscription date range - start date
     */
    private Date startSubscriptionDate;

    /**
     * Filtering criteria - subscription date range - end date
     */
    private Date endSubscriptionDate;

    /**
     * Filtering criteria - operation date range - start date
     */
    private Date startRatingDate;

    /**
     * Filtering criteria - operation date range - end date
     */
    private Date endRatingDate;

    /**
     * Filtering criteria - quantity range - min value
     */
    private BigDecimal minQuantity;

    /**
     * Filtering criteria - quantity range - max value
     */
    private BigDecimal maxQuantity;

    /**
     * Filtering criteria - subscription age range in month - min value
     */
    private Long minSubscriptionAgeInMonth;

    /**
     * Filtering criteria - subscription age range in month - max value
     */
    private Long maxSubscriptionAgeInMonth;

    /**
     * Filtering criteria - criteria value
     */
    private String criteria1Value;

    /**
     * Filtering criteria - criteria value
     */
    private String criteria2Value;

    /**
     * Filtering criteria - criteria value
     */
    private String criteria3Value;

    /**
     * Filtering criteria - expression to calculate criteria value
     */
    private String criteriaEL;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax;

    /**
     * Amount with tax
     */
    private BigDecimal amountWithTax;

    /**
     * Expression to calculate amount without tax
     */
    private String amountWithoutTaxEL;

    /**
     * Expression to calculate amount with tax
     */
    private String amountWithTaxEL;

    /**
     * Filtering criteria - currency
     */
    private Long tradingCurrency;

    /**
     * Filtering criteria - country
     */
    private Long tradingCountry;

    /**
     * The lower number, the higher the priority is
     */
    private int priority = 1;

    /**
     * Filtering criteria - seller
     */
    private Long seller;

    /**
     * Validity calendar
     */
    private Long validityCalendar;

    /**
     * Ordering sequence
     */
    private Long sequence;

    /**
     * Script to run to determine the amounts
     */
    private Long scriptInstance;

//    private String woDescriptionEL;

    /**
     * Expression to calculate price with/without tax. It overrides quantity x unitPrice when set.
     */
    private String totalAmountEL;

    /**
     * Minimum allowed amount for a walletOperation. If this amount is less than the walletOperation this amount is save and the old value is save in rawAmount.
     */
    private String minimumAmountEL;

    private String invoiceSubCategoryEL;

    private Date validityFrom;

    private Date validityDate;

//    /**
//     * An El expression used to override wallet operation's parameter1El.
//     */
//    private String parameter1El;
//
//    /**
//     * An El expression used to override wallet operation's parameter2El.
//     */
//    private String parameter2El;
//
//    /**
//     * An El expression used to override wallet operation's parameter3El.
//     */
//    private String parameter3El;

//    /**
//     * Custom field values in JSON format
//     */
//    protected String cfValues;

    @Override
    public int hashCode() {
        return id.intValue();
    }

    public PricePlanMatrixForRating() {
    }

    public PricePlanMatrixForRating(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    public PricePlanMatrixForRating(Long id, String code, Long offerTemplate, Date startSubscriptionDate, Date endSubscriptionDate, Date startRatingDate, Date endRatingDate, BigDecimal minQuantity,
            BigDecimal maxQuantity, Long minSubscriptionAgeInMonth, Long maxSubscriptionAgeInMonth, String criteria1Value, String criteria2Value, String criteria3Value, String criteriaEL, BigDecimal amountWithoutTax,
            BigDecimal amountWithTax, String amountWithoutTaxEL, String amountWithTaxEL, Long tradingCurrency, Long tradingCountry, int priority, Long seller, Long validityCalendar, Long sequence, Long scriptInstance,
            String totalAmountEL, String minimumAmountEL, String invoiceSubCategoryEL, Date validityFrom, Date validityDate) {
        this.id = id;
        this.code = code;
        this.offerTemplate = offerTemplate;
        this.startSubscriptionDate = startSubscriptionDate;
        this.endSubscriptionDate = endSubscriptionDate;
        this.startRatingDate = startRatingDate;
        this.endRatingDate = endRatingDate;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.minSubscriptionAgeInMonth = minSubscriptionAgeInMonth;
        this.maxSubscriptionAgeInMonth = maxSubscriptionAgeInMonth;
        this.criteria1Value = criteria1Value;
        this.criteria2Value = criteria2Value;
        this.criteria3Value = criteria3Value;
        this.criteriaEL = criteriaEL;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.amountWithoutTaxEL = amountWithoutTaxEL;
        this.amountWithTaxEL = amountWithTaxEL;
        this.tradingCurrency = tradingCurrency;
        this.tradingCountry = tradingCountry;
        this.priority = priority;
        this.seller = seller;
        this.validityCalendar = validityCalendar;
        this.sequence = sequence;
        this.scriptInstance = scriptInstance;
        this.totalAmountEL = totalAmountEL;
        this.minimumAmountEL = minimumAmountEL;
        this.invoiceSubCategoryEL = invoiceSubCategoryEL;
        this.validityFrom = validityFrom;
        this.validityDate = validityDate;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof PricePlanMatrixForRating)) {
            return false;
        }

        PricePlanMatrixForRating other = (PricePlanMatrixForRating) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(PricePlanMatrixForRating o) {
        return this.getPriority() - o.getPriority();
    }
//
//    /**
//     * @return Expression to determine Wallet operation description
//     */
//    public String getWoDescriptionEL() {
//        return woDescriptionEL;
//    }
//
//    /**
//     * @param woDescriptionEL Expression to determine Wallet operation description
//     */
//    public void setWoDescriptionEL(String woDescriptionEL) {
//        this.woDescriptionEL = woDescriptionEL;
//    }

    public String getInvoiceSubCategoryEL() {
        return invoiceSubCategoryEL;
    }

    public void setInvoiceSubCategoryEL(String invoiceSubCategoryEL) {
        this.invoiceSubCategoryEL = invoiceSubCategoryEL;
    }

    /**
     * Expression to get the total amount. Previously called ratingEL.
     * 
     * @return total amount expression
     */
    public String getTotalAmountEL() {
        return totalAmountEL;
    }

    /**
     * Expression to get the total amount. Previously called ratingEL.
     * 
     * @param totalAmountEL EL expression
     */
    public void setTotalAmountEL(String totalAmountEL) {
        this.totalAmountEL = totalAmountEL;
    }

    /**
     * Expression to set the minimum allowed amount.
     * 
     * @return EL expression
     */
    public String getMinimumAmountEL() {
        return minimumAmountEL;
    }

    /**
     * @param minimumAmountEL Expression to set the minimum allowed amount.
     */
    public void setMinimumAmountEL(String minimumAmountEL) {
        this.minimumAmountEL = minimumAmountEL;
    }

    public Date getValidityFrom() {
        return validityFrom;
    }

    public void setValidityFrom(Date validityFrom) {
        this.validityFrom = validityFrom;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

//    /**
//     * Gets the parameter1El EL expression.
//     *
//     * @return an El expression
//     */
//    public String getParameter1El() {
//        return parameter1El;
//    }
//
//    /**
//     * Sets the parameter1El EL expression.
//     *
//     * @param parameter1El an El expression
//     */
//    public void setParameter1El(String parameter1El) {
//        this.parameter1El = parameter1El;
//    }
//
//    /**
//     * Gets the parameter2El EL expression.
//     *
//     * @return an El expression
//     */
//    public String getParameter2El() {
//        return parameter2El;
//    }
//
//    /**
//     * Sets the parameter2El EL expression.
//     *
//     * @param parameter2El an El expression
//     */
//    public void setParameter2El(String parameter2El) {
//        this.parameter2El = parameter2El;
//    }
//
//    /**
//     * Gets the parameter3El EL expression.
//     *
//     * @return an El expression
//     */
//    public String getParameter3El() {
//        return parameter3El;
//    }
//
//    /**
//     * Sets the parameter3El EL expression.
//     *
//     * @param parameter3El an El expression
//     */
//    public void setParameter3El(String parameter3El) {
//        this.parameter3El = parameter3El;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(Long offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public Date getStartSubscriptionDate() {
        return startSubscriptionDate;
    }

    public void setStartSubscriptionDate(Date startSubscriptionDate) {
        this.startSubscriptionDate = startSubscriptionDate;
    }

    public Date getEndSubscriptionDate() {
        return endSubscriptionDate;
    }

    public void setEndSubscriptionDate(Date endSubscriptionDate) {
        this.endSubscriptionDate = endSubscriptionDate;
    }

    public Date getStartRatingDate() {
        return startRatingDate;
    }

    public void setStartRatingDate(Date startRatingDate) {
        this.startRatingDate = startRatingDate;
    }

    public Date getEndRatingDate() {
        return endRatingDate;
    }

    public void setEndRatingDate(Date endRatingDate) {
        this.endRatingDate = endRatingDate;
    }

    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }

    public BigDecimal getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Long getMinSubscriptionAgeInMonth() {
        return minSubscriptionAgeInMonth;
    }

    public void setMinSubscriptionAgeInMonth(Long minSubscriptionAgeInMonth) {
        this.minSubscriptionAgeInMonth = minSubscriptionAgeInMonth;
    }

    public Long getMaxSubscriptionAgeInMonth() {
        return maxSubscriptionAgeInMonth;
    }

    public void setMaxSubscriptionAgeInMonth(Long maxSubscriptionAgeInMonth) {
        this.maxSubscriptionAgeInMonth = maxSubscriptionAgeInMonth;
    }

    public String getCriteria1Value() {
        return criteria1Value;
    }

    public void setCriteria1Value(String criteria1Value) {
        this.criteria1Value = criteria1Value;
    }

    public String getCriteria2Value() {
        return criteria2Value;
    }

    public void setCriteria2Value(String criteria2Value) {
        this.criteria2Value = criteria2Value;
    }

    public String getCriteria3Value() {
        return criteria3Value;
    }

    public void setCriteria3Value(String criteria3Value) {
        this.criteria3Value = criteria3Value;
    }

    public String getCriteriaEL() {
        return criteriaEL;
    }

    public void setCriteriaEL(String criteriaEL) {
        this.criteriaEL = criteriaEL;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }

    public Long getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(Long tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public Long getTradingCountry() {
        return tradingCountry;
    }

    public void setTradingCountry(Long tradingCountry) {
        this.tradingCountry = tradingCountry;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Long getSeller() {
        return seller;
    }

    public void setSeller(Long seller) {
        this.seller = seller;
    }

    public Long getValidityCalendar() {
        return validityCalendar;
    }

    public void setValidityCalendar(Long validityCalendar) {
        this.validityCalendar = validityCalendar;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Long getScriptInstance() {
        return scriptInstance;
    }

    public void setScriptInstance(Long scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

//
//    public String getCfValues() {
//        return cfValues;
//    }
//
//    public void setCfValues(String cfValues) {
//        this.cfValues = cfValues;
//    }
}